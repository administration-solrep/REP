package fr.dila.reponses.web.dossier;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.webapp.edit.lock.LockActions;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.security.CaseManagementSecurityConstants;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.historique.HistoriqueAttribution;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.client.BordereauDTO;
import fr.dila.reponses.web.client.BordereauDTOImpl;
import fr.dila.reponses.web.client.HistoriqueAttributionDTO;
import fr.dila.reponses.web.client.HistoriqueAttributionDTOImpl;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.schema.RoutingTaskSchemaUtils;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.LogDocumentUpdateService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.context.NavigationContextBean;
import fr.dila.st.web.dossier.DossierLockActionsBean;

/**
 * Une classe qui permet de gérer les actions relatives aux dossiers.
 * 
 * @author bgamard
 */
@Name("dossierActions")
@Scope(ScopeType.PAGE)
public class DossierActionsBean implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long							serialVersionUID	= 1L;

	private static STLogger								LOGGER				= STLogFactory
																					.getLog(DossierActionsBean.class);

	@In(create = true, required = true)
	protected transient CoreSession						documentManager;

	@In(create = true, required = false)
	protected transient DossierDistributionActionsBean	dossierDistributionActions;

	@In(create = true, required = true)
	protected DocumentModel								currentIndexation;

	@In(create = true, required = false)
	protected transient DossierLockActionsBean			dossierLockActions;

	@In(create = true, required = false)
	protected transient ReponseActionsBean				reponseActions;

	@In(create = true)
	protected transient LockActions						lockActions;

	@In(create = true, required = false)
	protected transient FacesMessages					facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor				resourcesAccessor;

	@In(create = true)
	protected transient CorbeilleActionsBean			corbeilleActions;

	@In(create = true, required = false)
	protected transient NavigationContextBean			navigationContext;

	@In(required = true, create = true)
	protected SSPrincipal								ssPrincipal;

	public Object getAttribute(DocumentModel dossierDoc, String className, String attributeName) throws Exception {
		// invocation de la méthode de la classe Dossier par reflection
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		Method method = Dossier.class.getMethod(className, CoreSession.class);
		Object arglist[] = new Object[1];
		arglist[0] = documentManager;
		Object classObject = method.invoke(dossier, arglist);

		method = method.getReturnType().getMethod(attributeName);
		Object ret = method.invoke(classObject);

		return ret;
	}

	@Factory(value = "currentIndexation")
	public DocumentModel getCurrentIndexation() throws ClientException {
		DocumentModel questionDoc = dossierDistributionActions.getQuestion();
		if (questionDoc != null) {
			return getIndexationComplementaire(questionDoc);
		}
		return documentManager.createDocumentModel(DossierConstants.QUESTION_DOCUMENT_TYPE);
	}

	private Boolean isNotEmptyCollection(Collection<?> liste) {
		return liste != null && !liste.isEmpty();
	}

	public BordereauDTO getBordereauDTO() throws ClientException {

		DocumentModel questionDoc = dossierDistributionActions.getQuestion();
		Question question = questionDoc.getAdapter(Question.class);
		DocumentModel reponseDoc = reponseActions.getReponse();
		Reponse reponse = reponseDoc.getAdapter(Reponse.class);
		Boolean canUnlockCurrentDoc = lockActions.getCanUnlockCurrentDoc();
		Boolean canEditIndexationComp = getCanEditIndexationComplementaire();
		Boolean canEditMinistereRattachement = dossierDistributionActions.getCanEditMinistereRattachement();
		Boolean canEditDirectionPilote = dossierDistributionActions.getCanEditDirectionPilote();
		DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);

		Boolean partMiseAJour = isNotEmptyCollection(question.getSignalements())
				|| isNotEmptyCollection(question.getRenouvellements()) || question.getDateRetraitQuestion() != null
				|| question.getDateTransmissionAssemblees() != null || question.getDateCaducite() != null;

		Boolean partReponse = (reponse.getPageJOreponse() != null || reponse.getDateJOreponse() != null);

		Boolean partIndexationAN = question.hasIndexationAn();

		Boolean partIndexationSENAT = question.hasIndexationSenat();

		Boolean partEditableIndexationComplementaire = canUnlockCurrentDoc && canEditIndexationComp;

		Boolean partIndexationComplementaireAN = question.hasIndexationComplementaireAn();

		Boolean partIndexationComplementaireSE = question.hasIndexationComplementaireSenat();

		Boolean partIndexationComplementaireMotCle = question.hasIndexationComplementaireMotCleMinistere();

		Boolean partFeuilleRoute = question.isQuestionTypeEcrite() && dossier.hasFeuilleRoute();

		Boolean partEditableMinisetreRatatchement = canUnlockCurrentDoc && canEditMinistereRattachement;

		Boolean partEditableDirectionPilote = canUnlockCurrentDoc && canEditDirectionPilote;

		BordereauDTO bordereauDTO = new BordereauDTOImpl(partMiseAJour, partReponse, partIndexationAN,
				partIndexationSENAT, partEditableIndexationComplementaire, partIndexationComplementaireAN,
				partIndexationComplementaireSE, partIndexationComplementaireMotCle, partFeuilleRoute,
				partEditableMinisetreRatatchement, partEditableDirectionPilote);
		return bordereauDTO;
	}

	public DocumentModel getIndexationComplementaire(DocumentModel questionDoc) {
		DocumentModel indexationComp = new DocumentModelImpl("/", DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
				ReponsesConstant.RECHERCHE_DOCUMENT_TYPE);

		Question question = questionDoc.getAdapter(Question.class);

		ReponsesIndexableDocument indexationObj = indexationComp.getAdapter(ReponsesIndexableDocument.class);
		indexationObj.setSenatQuestionThemes(question.getIndexationComplSenatQuestionThemes());
		indexationObj.setSenatQuestionRubrique(question.getIndexationComplSenatQuestionRubrique());
		indexationObj.setSenatQuestionRenvois(question.getIndexationComplSenatQuestionRenvois());
		indexationObj.setAssNatRubrique(question.getIndexationComplAssNatRubrique());
		indexationObj.setAssNatTeteAnalyse(question.getIndexationComplAssNatTeteAnalyse());
		indexationObj.setAssNatAnalyses(question.getIndexationComplAssNatAnalyses());
		indexationObj.setMotsClefMinistere(question.getIndexationComplMotsClefMinistere());

		currentIndexation = indexationComp;

		return currentIndexation;
	}

	public void saveIndexationComplementaire() throws ClientException {
		DocumentModel questionDoc = dossierDistributionActions.getQuestion();
		Question question = questionDoc.getAdapter(Question.class);
		ReponsesIndexableDocument indexationObj = currentIndexation.getAdapter(ReponsesIndexableDocument.class);

		question.setIndexationComplSenatQuestionThemes(indexationObj.getSenatQuestionThemes());
		question.setIndexationComplSenatQuestionRubrique(indexationObj.getSenatQuestionRubrique());
		question.setIndexationComplSenatQuestionRenvois(indexationObj.getSenatQuestionRenvois());
		question.setIndexationComplAssNatRubrique(indexationObj.getAssNatRubrique());
		question.setIndexationComplAssNatTeteAnalyse(indexationObj.getAssNatTeteAnalyse());
		question.setIndexationComplAssNatAnalyses(indexationObj.getAssNatAnalyses());
		question.setIndexationComplMotsClefMinistere(indexationObj.getMotsClefMinistere());

		// on log les modifications effectuée sur le bordereau.
		final LogDocumentUpdateService dossierDistributionService = ReponsesServiceLocator.getDossierBordereauService();
		dossierDistributionService.logAllDocumentUpdate(documentManager, questionDoc);

		documentManager.saveDocument(questionDoc);
		// FEV 580 - On ne déverrouille pas la question lors de la sauvegarde du bordereau
		//dossierLockActions.unlockCurrentDossier();
	}

	public void cancelIndexationComplementaire() throws ClientException {
		dossierLockActions.unlockCurrentDossier();
	}

	public List<HistoriqueAttributionDTO> getHistoriqueAttributionFeuilleRoute() {
		DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		List<HistoriqueAttributionDTO> result = new ArrayList<HistoriqueAttributionDTO>();
		List<HistoriqueAttribution> historiquesAttribution = dossier.getHistoriqueAttribution(documentManager);
		for (HistoriqueAttribution historique : historiquesAttribution) {
			String minAttribution = historique.getMinAttribution();
			Calendar dateAttribution = historique.getDateAttribution();
			String typeAttribution = historique.getTypeAttribution();
			result.add(new HistoriqueAttributionDTOImpl(minAttribution, dateAttribution, typeAttribution));
		}
		return result;
	}

	/**
	 * Retourne la liste des renouvellements de la question courante triés par ordre antéchronologique.
	 * 
	 * @return
	 * @throws ClientException
	 */
	public List<Renouvellement> getRenouvellements() throws ClientException {
		DocumentModel questionDoc = dossierDistributionActions.getQuestion();
		Question question = questionDoc.getAdapter(Question.class);
		List<Renouvellement> out = question.getRenouvellements();
		Collections.sort(out, new Comparator<Renouvellement>() {
			@Override
			public int compare(Renouvellement o1, Renouvellement o2) {
				if (o1.getDateEffet() == null) {
					return 1;
				}
				if (o2.getDateEffet() == null) {
					return -1;
				}
				return -o1.getDateEffet().compareTo(o2.getDateEffet());
			}
		});
		return out;
	}

	public boolean isPropertyEmpty(DocumentModel doc, String schema, String property) throws PropertyException,
			ClientException {
		Object value = doc.getProperty(schema, property);
		if (value == null) {
			return true;
		}
		if (value instanceof Collection<?>) {
			Collection<?> list = (Collection<?>) value;
			return list.isEmpty();
		}
		return false;
	}

	public boolean hasFeuilleRoute() {
		DocumentModel dossierDoc = dossierDistributionActions.getDossier();
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		return dossier.hasFeuilleRoute();
	}

	/**
	 * Crée un nouveau dossier.
	 */
	public String createDossier() throws ClientException {
		// Crée la question
		final DocumentModel questionDoc = navigationContext.getChangeableDocument();
		final Question question = questionDoc.getAdapter(Question.class);

		final Mailbox currentMailbox = ReponsesServiceLocator.getCorbeilleService().getDossierOwnerPersonalMailbox(
				documentManager);

		final DossierDistributionService dossierDistributionService = ReponsesServiceLocator
				.getDossierDistributionService();
		Dossier dossier = dossierDistributionService.initDossier(documentManager, question.getNumeroQuestion());
		// creation de tous les sous elements du dossier
		// BGD : Reponse reponseAdapt = reponse != null ? reponse.getAdapter(Reponse.class) : null;
		dossier = dossierDistributionService.createDossier(documentManager, dossier, question, null,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);
		// Démarre la feuille de route associée au dossier
		dossierDistributionService.startDefaultRoute(documentManager, dossier);

		documentManager.save();

		// default code nuxeo ( CaseItemDocumentActionsBean : )
		facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("question enregistrée"),
				resourcesAccessor.getMessages().get(dossier.getDocument().getType()));

		Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED,
				documentManager.getDocument(currentMailbox.getDocument().getRef()));

		navigationContext.setCurrentDocument(dossier.getDocument());

		return corbeilleActions.navigateToEspaceTravail();
	}

	public Boolean isDossierContainsMinistere() throws ClientException {
		Boolean result = Boolean.FALSE;
		if (ssPrincipal.isAdministrator()
				|| ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER)) {
			// L'administrateur fonctionnel a le droit de lecture et d'ecriture
			// sur tous les dossiers
			result = Boolean.TRUE;
		}

		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		if (!result && dossierDoc != null && dossierDoc.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {

			// recupere les poste autorise a ecrire sur ce dossier
			final Set<String> posteAutorise = new HashSet<String>();
			final ACL acl = dossierDoc.getACP().getACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
			for (final ACE ace : acl.getACEs()) {
				final String posteId = ace.getUsername().replace(
						CaseManagementSecurityConstants.MAILBOX_PREFIX + SSConstant.MAILBOX_POSTE_ID_PREFIX, "");
				posteAutorise.add(posteId);
			}

			// teste les postes de l'utilisateur
			for (final String posteId : ssPrincipal.getPosteIdSet()) {
				if (posteAutorise.contains(posteId)) {
					result = Boolean.TRUE;
					break;
				}
			}

			// L'administrateur ministériel peut modifier le parapheur des
			// dossiers en cours de son ministère
			// recupere tous les poste des ministeres auquel il est rattaché pour
			if (!result && ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER)) {
				final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
				final STPostesService postesService = STServiceLocator.getSTPostesService();
				final Set<String> idsMinistere = ssPrincipal.getMinistereIdSet();
				for (final String idMinistere : idsMinistere) {
					final OrganigrammeNode node = ministeresService.getEntiteNode(idMinistere);
					if (node != null) {
						final List<String> postes = postesService.getPosteIdInSubNode(node);
						for (final String posteId : postes) {
							if (posteAutorise.contains(posteId)) {
								result = Boolean.TRUE;
								break;
							}
						}
					}
					if (result) {
						break;
					}
				}
			}
		}

		return result;
	}

	public Signalement getLastSignalement() {
		if (navigationContext.getCurrentDocument() != null) {
			return ReponsesServiceLocator.getDossierDistributionService().getLastSignalement(
					navigationContext.getCurrentDocument());
		}
		LOGGER.info(documentManager, ReponsesLogEnumImpl.GET_ATTR_QUEST_FONC, "Signalement : le dossier est vide");
		return null;
	}

	public Boolean hasSignalement() {
		return getLastSignalement() != null;
	}

	/**
	 * Retourne vrai si l'utilisateur courant peut éditer l'indexation complémentaire.
	 * 
	 * @return Condition
	 */
	public Boolean getCanEditIndexationComplementaire() {
		if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.INDEXATION_COMPLEMENTAIRE_ADMIN_UPDATER)) {
			return true;
		}

		if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.INDEXATION_COMPLEMENTAIRE_UPDATER)) {
			return corbeilleActions.isDossierLoadedInCorbeille();
		}

		if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.INDEXATION_COMPLEMENTAIRE_ADMIN_MINISTERIEL_UPDATER)) {
			return isCurrentDossierInUserMinistere();
		}
		return false;
	}

	/**
	 * Retourne vrai si le ministère interpellé dossier en cours fait partie des ministères de l'utilisateur.
	 * 
	 * @return Condition
	 */
	public boolean isCurrentDossierInUserMinistere() {
		final DocumentModel doc = navigationContext.getCurrentDocument();
		if (doc == null) {
			return false;
		}

		final Dossier dossier = doc.getAdapter(Dossier.class);
		return ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant());
	}

	/**
	 * Retourne vrai si l'utilisateur appartient à un poste qui a participé au dossier.
	 * 
	 * @return Condition
	 * @throws ClientException
	 */
	public boolean isUserMailboxInDossier() throws ClientException {
		// L'utilisateur peut voir la reponse si il est destinataire de la distribution
		if (corbeilleActions.getCurrentDossierLink() != null) {
			return true;
		}

		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();

		final ACP acp = dossierDoc.getACP();
		final ACL[] acls = acp.getACLs();

		for (final ACL acl : acls) {
			final ACE[] aces = acl.getACEs();
			for (final ACE ace : aces) {
				if (ace.getUsername().startsWith("mailbox_poste") && ssPrincipal.isMemberOf(ace.getUsername())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * True if the dossier question origine is AN (Assemblee Nationale).
	 * 
	 */
	public Boolean isDossierQuestionOrigineAN() {
		Boolean isDossierQuestionOrigineAN = false;
		final DocumentModel questionDoc = getQuestion();
		// SONAR: getQuestion est nullable; on introduit une erreur explicite
		if (questionDoc == null) {
			throw new IllegalStateException("Question null");
		}
		final Question question = questionDoc.getAdapter(Question.class);
		if (question != null) {
			isDossierQuestionOrigineAN = question.hasOrigineAN();
		}
		return isDossierQuestionOrigineAN;
	}

	/**
	 * Get the ministere attributaire of the current Dossier from Reponse.
	 * 
	 */
	public String getCurrentDossierMinistereAttributaire() {
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		return dossier.getIdMinistereAttributaireCourant();
	}

	/**
	 * Indique si le dossier a été arbitré par le passé
	 * 
	 * @return
	 */
	public boolean isDossierArbitrated() {
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		return dossierDoc.getAdapter(Dossier.class).isArbitrated();
	}

	/**
	 * Retourne la liste des noms des directions du dossier courant.
	 * 
	 * @return Liste des noms des directions du dossier courant
	 * @throws ClientException
	 */
	public Set<String> getListingUnitesStruct() throws ClientException {
		final Set<String> directionNomList = new HashSet<String>();
		final Set<String> mailboxIds = new HashSet<String>();

		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();

		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		final List<DocumentModel> steps = ReponsesServiceLocator.getFeuilleRouteService().getSteps(documentManager,
				dossierDoc);

		for (final DocumentModel step : steps) {
			final String mailboxId = RoutingTaskSchemaUtils.getMailboxId(step);

			if (mailboxId != null && !mailboxIds.contains(mailboxId)) {
				final String direction = mailboxPosteService.getIdDirectionFromMailbox(mailboxId);
				directionNomList.add(direction);
			}
		}

		return directionNomList;
	}

	/**
	 * Renvoie true si la reponse est signée
	 * 
	 * @return
	 * @throws ClientException
	 */
	public boolean isReponseSignee() throws ClientException {
		return ReponsesServiceLocator.getReponseService().isReponseSignee(documentManager,
				navigationContext.getCurrentDocument());
	}

	/**
	 * Vérifie si une demande d'arbitrage peut être fait sur le DossierLink en cours.
	 * 
	 * @return true si le dossier n'est pas bloqué par l'arbitrage
	 * @throws ClientException
	 *             ClientException
	 */
	public Boolean canDemandeArbitrageSGG() {
		return ReponsesServiceLocator.getReponsesArbitrageService().canAddStepArbitrageSGG(
				navigationContext.getCurrentDocument());
	}

	/**
	 * Retourne le label correspondant à l'icone d'arbitrage en fonction de blocage
	 * 
	 * @return label.responses.dossier.toolbar.arbitrageDossier ou
	 *         label.responses.dossier.toolbar.arbitrageDossierIndisponible
	 */
	public String getLabelArbitrageSGG() {
		if (canDemandeArbitrageSGG()) {
			return "label.responses.dossier.toolbar.arbitrageDossier";
		} else {
			return "label.responses.dossier.toolbar.arbitrageDossierIndisponible";
		}
	}

	/**
	 * Retourne le label correspondant à l'icone de réattribution en fonction de blocage
	 * 
	 * @return label.responses.dossier.toolbar.reattributionDossier ou
	 *         label.responses.dossier.toolbar.reattributionDossierIndisponible
	 */
	public String getLabelReattribution() {
		if (canDemandeArbitrageSGG()) {
			return "label.responses.dossier.toolbar.reattributionDossier";
		} else {
			return "label.responses.dossier.toolbar.reattributionDossierIndisponible";
		}
	}

	/**
	 * Retourne le lien pour la réattribution : si l'arbitrage peut encore être demandé, alors on n'a jamais encore
	 * arbitré donc on peut encore reattribuer, donc on retourne le lien pour le faire
	 * 
	 * @return
	 */
	public String getLinkReattribution() {
		if (canDemandeArbitrageSGG()) {
			return "javascript:Richfaces.showModalPanel('unconcerned_task_panel');";
		} else {
			return "";
		}
	}

	private DocumentModel getQuestion() {
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		if (dossierDoc != null && dossierDoc.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
			Dossier dossier = dossierDoc.getAdapter(Dossier.class);
			final DocumentModel question = dossier.getQuestion(documentManager).getDocument();
			return question;
		}
		return null;
	}
}
