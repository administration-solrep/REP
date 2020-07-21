package fr.dila.reponses.core.operation.nxshell;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.Exception.AllotissementException;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Une opération pour cloturer des dossiers d'une législature.
 */
@Operation(id = CloturerDossiersLegislatureOperation.ID, category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY, label = "CloturerDossiersLegislature", description = "Cloture les dossiers en cours d'une législature donnée")
public class CloturerDossiersLegislatureOperation {
	/**
	 * Identifiant technique de l'opération.
	 */
	public static final String	ID							= "Reponses.Cloturer.Dossiers.Legislature";

	/**
	 * Logger.
	 */
	private static final Log	LOGGER						= LogFactory
																	.getLog(CloturerDossiersLegislatureOperation.class);
	/**
	 * Mode lecture
	 */
	private static final String	MODE_LECTURE				= "LECTURE";
	/**
	 * Mode exécution
	 */
	private static final String	MODE_EXECUTION				= "EXECUTION";
	/**
	 * Origine AN
	 */
	private static final String	ORIGINE_AN					= "AN";
	/**
	 * Origine SENAT
	 */
	private static final String	ORIGINE_SENAT				= "SENAT";
	/**
	 * Cloture retiree
	 */
	private static final String	CLOTURE_RETIREE				= "RETIREE";
	/**
	 * Cloture caduqe
	 */
	private static final String	CLOTURE_CADUQE				= "CADUQUE";
	/**
	 * Cloture autre
	 */
	private static final String	CLOTURE_AUTRE				= "CLOTURE_AUTRE";
	/**
	 * Requête de base
	 */
	private static final String	QUERY						= "SELECT q.ecm:uuid AS id FROM Question AS q WHERE q.qu:etatQuestion = 'en cours'  ";
	/**
	 * Where Origine question
	 */
	private static final String	QUERY_WHERE_ORIGINE			= " AND q.qu:origineQuestion = ? ";
	/**
	 * Where législature question
	 */
	private static final String	QUERY_WHERE_LEGISLATURE		= " AND q.qu:legislatureQuestion = ? ";
	/**
	 * Where numéro question
	 */
	private static final String	QUERY_WHERE_NUMERO_QUESTION	= " AND q.qu:numeroQuestion <> '";
	/**
	 * Order by id pour rester cohérent quand on prend que les 10 premiers
	 */
	private static final String	ORDER_BY_ID					= " ORDER BY q.ecm:uuid ";

	@Context
	protected CoreSession		session;

	// Paramètres de l'Opération
	// Mode lecture ou exécution
	@Param(name = "mode", required = true)
	protected String			mode;
	// Origine AN ou SENAT
	@Param(name = "origine", required = true)
	protected String			origine;
	// Législature
	@Param(name = "legislature", required = true)
	protected String			legislature;
	// Jour et heure de fin au format JJ-MM-AAAA-HH-MM
	@Param(name = "dateFin", required = true)
	protected String			dateFin;
	// Type de cloture
	@Param(name = "typeCloture", required = true)
	protected String			typeCloture;

	@OperationMethod
	public void run() throws Exception {
		LOGGER.info("Début opération " + ID);

		if (!checkParameters(mode, origine, legislature, dateFin, typeCloture)) {
			LOGGER.warn("Les paramètres reçus ne sont pas valides");
			return;
		}

		// Construction de la requête de base
		String query = QUERY + QUERY_WHERE_ORIGINE + QUERY_WHERE_LEGISLATURE;

		// Vérification si mode lecture ou mode exécution
		if (mode.toUpperCase().equals(MODE_LECTURE)) {
			LOGGER.info("Affichage des dossiers à cloturer pour la législature " + legislature
					+ " et ayant comme origine " + origine);
			// ici on exécute la requête normalement en retournant tous les dossiers qu'on trouve (sans se limiter à un
			// nombre particulier)
			final List<DocumentModel> questionsDoc = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, "Question",
					query + ORDER_BY_ID, new Object[] { origine.toUpperCase(), legislature });
			for (final DocumentModel questionDoc : questionsDoc) {
				final Question question = questionDoc.getAdapter(Question.class);
				LOGGER.info("Législature " + legislature + " " + question.getTypeQuestion() + " "
						+ question.getOrigineQuestion() + " " + question.getNumeroQuestion().toString());
			}
		} else if (mode.toUpperCase().equals(MODE_EXECUTION)) {
			LOGGER.info("Début de cloture des questions de la législature " + legislature + " et ayant comme origine "
					+ origine + " et avec comme type de cloture " + typeCloture
					+ ". Le traitement se terminera automatiquement à la date " + dateFin
					+ " même si toutes les questions n'ont pas été traitées.");
			// On regarde combien de questions on a en tout
			final List<DocumentModel> questionsDocTotal = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
					"Question", query, new Object[] { origine.toUpperCase(), legislature });
			Integer sizeTotale = questionsDocTotal.size();
			LOGGER.info(sizeTotale.toString() + " dossiers à cloturer");
			// traduction de la date au format date
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm");
			Date dateFinDate = formatter.parse(dateFin);
			// On effectue une boucle tous les dix dossiers traités pour vérifier l'heure qu'il est et relancer le
			// traitement
			int compteurQuestionTraitees = 0;
			boolean onContinue = true;
			final STLockService lockService = STServiceLocator.getSTLockService();
			final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
			final DossierDistributionService dossierDistributionService = ReponsesServiceLocator
					.getDossierDistributionService();
			final JournalService journalService = STServiceLocator.getJournalService();
			final STParametreService parametreService = STServiceLocator.getSTParametreService();
			// Si la date est inférieure à la date du jour, on n'exécute rien
			if (dateFinDate.before(new Date())) {
				onContinue = false;
				LOGGER.warn("La date indiquée est antérieure à la date du jour. Aucun traitement n'est effectuée");
			}
			Integer compteurQuestionsCommencees = 0;
			String whereQueryDossiersIncorrects = "";
			
			GregorianCalendar dateNow = new GregorianCalendar();
			String delaiQuestionSignalee = parametreService.getParametreValue(session, STParametreConstant.DELAI_QUESTION_SIGNALEE);
			
			while (onContinue) {
				final List<DocumentModel> questionsDoc = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
						"Question", query + whereQueryDossiersIncorrects + ORDER_BY_ID,
						new Object[] { origine.toUpperCase(), legislature }, 10, 0);
				if (questionsDoc.size() == 0) {
					// Si on n'a trouvé aucun dossier c'est qu'on est arrivé au bout donc on s'arrête
					onContinue = false;
				} else if (sizeTotale <= compteurQuestionsCommencees) {
					// On s'arrête quand on a traité au moins une fois toutes les questions
					onContinue = false;
				}
				for (final DocumentModel questionDoc : questionsDoc) {
					compteurQuestionsCommencees = compteurQuestionsCommencees + 1;
					final Question question = questionDoc.getAdapter(Question.class);
					LOGGER.info("Début de cloture de la QE : " + "Législature " + legislature + " "
							+ question.getTypeQuestion() + " " + question.getOrigineQuestion() + " "
							+ question.getNumeroQuestion().toString());

					// là on va donc cloturer la question
					Dossier dossier = question.getDossier(session);
					try {
						DocumentModel dossierDoc = dossier.getDocument();
						// Unlock du dossier et de la route si nécéssaire
						Map<String, String> dossierLockInfo = lockService.getLockDetails(session, dossierDoc);
						if (dossierLockInfo.get(STLockService.LOCKER) != null) {
							lockService.unlockDocUnrestricted(session, dossierDoc);
						}
						String idRoute = dossier.getLastDocumentRoute();
						if (idRoute != null) {
							DocumentModel routeDoc = session.getDocument(new IdRef(idRoute));
							Map<String, String> routeLockInfo = lockService.getLockDetails(session, routeDoc);
							if (routeLockInfo.get(STLockService.LOCKER) != null) {
								lockService.unlockDocUnrestricted(session, routeDoc);
							}
						}
						allotissementService.removeDossierFromLotIfNeeded(session, dossier);

						// on recharge le dossier car il a été modifié
						dossierDoc = session.getDocument(dossier.getDocument().getRef());
						dossier = dossierDoc.getAdapter(Dossier.class);
						dossierDistributionService.retirerFeuilleRoute(session, dossier);

						// Journalise l'action
						String comment = "";
						if (VocabularyConstants.ETAT_QUESTION_RETIREE.equals(typeCloture.toLowerCase())) {
							comment = " : question retirée";
						} else if (VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(typeCloture.toLowerCase())) {
							comment = " : question caduque";
						} else if (VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(typeCloture.toLowerCase())) {
							comment = " : question cloturée";
						}
						journalService.journaliserActionFDR(session, dossierDoc,
								STEventConstant.EVENT_FEUILLE_ROUTE_STEP_UPDATE,
								ReponsesEventConstant.COMMENT_QUESTION_CHANGEMENT + comment);
					} catch (AllotissementException e) {
						LOGGER.error("Une erreur est survenue lors du retrait du dossier de son lot :" + e.getMessage());
						whereQueryDossiersIncorrects = whereQueryDossiersIncorrects + QUERY_WHERE_NUMERO_QUESTION
								+ question.getNumeroQuestion() + "'";
					} catch (Exception e) {
						LOGGER.error("Une erreur est survenue lors de la modification de la feuille de route :"
								+ e.getMessage());
						whereQueryDossiersIncorrects = whereQueryDossiersIncorrects + QUERY_WHERE_NUMERO_QUESTION
								+ question.getNumeroQuestion() + "'";
					}
					try {
						
						// Changement de l'état de la question en mettant la date effective à maintenant
						question.setEtatQuestion(session, typeCloture.toLowerCase(), dateNow, delaiQuestionSignalee);

						session.saveDocument(question.getDocument());
						session.save();

					} catch (ClientException e1) {
						LOGGER.error("Une erreur est survenue lors de la sauvegarde de la question : "
								+ e1.getMessage());
						whereQueryDossiersIncorrects = whereQueryDossiersIncorrects + QUERY_WHERE_NUMERO_QUESTION
								+ question.getNumeroQuestion() + "'";
					}
					compteurQuestionTraitees = compteurQuestionTraitees + 1;
					// On commit et on restart la transaction quand ça marche pour éviter un rollback stupide de Nuxeo
					TransactionHelper.commitOrRollbackTransaction();
					TransactionHelper.startTransaction();
				}
				// Quand on arrive ici c'est qu'on a fini de traiter 10 questions
				// Vérification de la date et de l'heure de fin
				// Quand l'heure est dépassée on arrête le traitement
				if (dateFinDate.before(new Date())) {
					onContinue = false;
				}
			}
			LOGGER.info(Integer.toString(compteurQuestionTraitees) + " questions ont été cloturées.");
		}
		LOGGER.info("Fin de l'opération " + ID);
	}

	private boolean checkParameters(String mode, String origine, String legislature, String dateFin, String typeCloture)
			throws ClientException {
		Boolean paramsOK = true;
		if (!mode.toUpperCase().equals(MODE_EXECUTION) && !mode.toUpperCase().equals(MODE_LECTURE)) {
			LOGGER.warn("Le mode d'exécution renseigné est incorrect. Il ne peut prendre que deux valeurs. "
					+ MODE_EXECUTION + " pour l'exécution de la cloture des dossiers et " + MODE_LECTURE
					+ " pour obtenir la liste des dossiers à cloturer.");
			paramsOK = false;
		}
		if (!origine.toUpperCase().equals(ORIGINE_AN) && !origine.toUpperCase().equals(ORIGINE_SENAT)) {
			LOGGER.warn("L'origine renseignée est incorrecte. Elle ne peut prendre que deux valeurs. " + ORIGINE_AN
					+ " pour l'exécution de la cloture des dossiers provenant de l'Assemblée Nationale et "
					+ ORIGINE_SENAT + " pour la cloture des dossiers provenant du Sénat.");
			paramsOK = false;
		}
		if (legislature.toString().length() > 2) {
			LOGGER.warn("La législature renseignée ne peut pas avoir plus de deux caractères");
			paramsOK = false;
		} else if (legislature.toString().length() < 2) {
			LOGGER.warn("La législature renseignée ne peut pas avoir moins de deux caractères");
			paramsOK = false;
		}
		if (!dateFin.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})-([01][0-9]|2[0-3])-[0-5][0-9]")) {
			LOGGER.warn("La date de fin renseignée doit être au format JJ-MM-AAAA-hh-mm");
			paramsOK = false;
		}
		if (!typeCloture.toUpperCase().equals(CLOTURE_AUTRE) && !typeCloture.toUpperCase().equals(CLOTURE_CADUQE)
				&& !typeCloture.toUpperCase().equals(CLOTURE_RETIREE)) {
			LOGGER.warn("Le type de cloture est incorrect. Ce paramètre ne peut prendre que trois valeurs :"
					+ CLOTURE_AUTRE + ", " + CLOTURE_CADUQE + " ou " + CLOTURE_RETIREE);
			paramsOK = false;
		}
		return paramsOK;
	}
}