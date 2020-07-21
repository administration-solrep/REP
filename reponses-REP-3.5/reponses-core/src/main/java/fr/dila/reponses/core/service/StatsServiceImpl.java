package fr.dila.reponses.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunVoid;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.runtime.api.Framework;

import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.core.stats.StatistiqueDirection;
import fr.dila.reponses.core.stats.StatistiqueGroupe;
import fr.dila.reponses.core.stats.StatistiqueMinistere;
import fr.dila.reponses.core.stats.StatistiqueMois;
import fr.dila.reponses.core.stats.StatistiqueQuestionReponse;
import fr.dila.reponses.core.stats.StatistiqueValeur;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.organigramme.ProtocolarOrderComparator;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Service de gestion des statistiques
 * 
 * @author bgd
 * 
 */
public class StatsServiceImpl implements StatsService {

	private static final long					serialVersionUID				= 1L;

	private static final Log					LOG								= LogFactory
																						.getLog(StatsServiceImpl.class);

	private static final STLogger				LOGGER							= STLogFactory
																						.getLog(StatsServiceImpl.class);

	private static volatile PersistenceProvider	persistenceProvider;

	private static final String					NB_QUESTION_QUERY				= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min, question.originequestion as origine"
																						+ " from question "
																						+ " where question.typequestion = 'QE'"
																						+ " and question.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "','"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "')"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ ", question.originequestion";

	private static final String					NB_REPONDU1_QUERY				= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min, question.originequestion as origine"
																						+ " from question, dossier_reponse, reponse"
																						+ " where question.id = dossier_reponse.iddocumentquestion"
																						+ " and dossier_reponse.iddocumentreponse = reponse.id (+)"
																						+ " and abs(MONTHS_BETWEEN(reponse.datepublicationjoreponse,question.datepublicationjo)) <='01'"
																						+ " and question.typequestion = 'QE'"
																						+ " and question.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "','"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "')"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ ", question.originequestion";

	private static final String					NB_REPONDU2_QUERY				= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min, question.originequestion as origine"
																						+ " from question, dossier_reponse, reponse"
																						+ " where question.id = dossier_reponse.iddocumentquestion"
																						+ " and dossier_reponse.iddocumentreponse = reponse.id (+)"
																						+ " and abs(MONTHS_BETWEEN(reponse.datepublicationjoreponse,question.datepublicationjo)) >'01'"
																						+ " and abs(MONTHS_BETWEEN(reponse.datepublicationjoreponse,question.datepublicationjo)) <='02'"
																						+ " and question.typequestion = 'QE'"
																						+ " and question.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "','"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "')"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ ", question.originequestion";

	private static final String					NB_REPONDU_SUP_QUERY			= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min, question.originequestion as origine"
																						+ " from question, dossier_reponse, reponse"
																						+ " where question.id = dossier_reponse.iddocumentquestion"
																						+ " and dossier_reponse.iddocumentreponse = reponse.id (+)"
																						+ " and abs(MONTHS_BETWEEN(reponse.datepublicationjoreponse,question.datepublicationjo)) >'02'"
																						+ " and question.typequestion = 'QE'"
																						+ " and question.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "','"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "')"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ ", question.originequestion";

	private static final String					NB_RETIRE_QUERY					= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min"
																						+ " from question "
																						+ " where question.typequestion = 'QE'"
																						+ " and question.dateretraitquestion is not null"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION;

	private static final String					NB_RENOUVELLE_QUERY				= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min "
																						+ "from question where question.typequestion = 'QE' and question.ETATRENOUVELE = '1' "
																						+ " and question.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "','"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "')"
																						+ "and question.legislaturequestion = ADDLEGISLATURECOURANTE group by question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION;

	private static final String					NB_RENOUVELLE_EC_QUERY			= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min "
																						+ "from question, dossier_reponse, reponse where question.id = dossier_reponse.iddocumentquestion "
																						+ "and dossier_reponse.iddocumentreponse = reponse.id (+) and reponse.datepublicationjoreponse is null "
																						+ "and question.typequestion = 'QE' and question.ETATRENOUVELE = '1' "
																						+ " and question.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "','"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "')"
																						+ "and question.legislaturequestion = ADDLEGISLATURECOURANTE group by question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION;

	private static final String					NB_SANS_REPONSE_QUERY			= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min"
																						+ " from question where "
																						+ " question.typequestion = 'QE'"
																						+ " and question.etatquestion = '"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "'"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION;

	private static final String					NB_SANS_REPONSE_SUP_QUERY		= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min"
																						+ " from question "
																						+ " where abs(MONTHS_BETWEEN(question.datepublicationjo, current_date)) > '02'"
																						+ " and question.typequestion = 'QE'"
																						+ " and question.etatquestion = '"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "'"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION;

	private static final String					NB_QUESTION_MOIS_QUERY			= "select count(*) as n, TO_CHAR(question.datepublicationjo, 'YYYY-MM') as mois, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min"
																						+ " from question, dossier_reponse, reponse"
																						+ " where question.id = dossier_reponse.iddocumentquestion"
																						+ " and dossier_reponse.iddocumentreponse = reponse.id (+)"
																						+ " and question.typequestion = 'QE'"
																						+ " and question.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "','"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "')"
																						+ " and question.datepublicationjo is not null"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by TO_CHAR(question.datepublicationjo, 'YYYY-MM'), question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " order by TO_CHAR(question.datepublicationjo, 'YYYY-MM')";

	private static final String					NB_REPONSE_MOIS_QUERY			= "select count(*) as n, TO_CHAR(reponse.datepublicationjoreponse, 'YYYY-MM') as mois, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min"
																						+ " from question, dossier_reponse, reponse"
																						+ " where question.id = dossier_reponse.iddocumentquestion"
																						+ " and dossier_reponse.iddocumentreponse = reponse.id (+)"
																						+ " and question.typequestion = 'QE'"
																						+ " and question.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "','"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "')"
																						+ " and reponse.datepublicationjoreponse is not null"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by TO_CHAR(reponse.datepublicationjoreponse, 'YYYY-MM'), question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " order by TO_CHAR(reponse.datepublicationjoreponse, 'YYYY-MM')";

	private static final String					NB_QUESTION_BY_POSTE_QUERY		= "select count(routing_task.distributionmailboxid) as nb, substr(routing_task.distributionmailboxid, 7) as posteid, dossier_reponse."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT
																						+ " as ministereid"
																						+ " from dossier_reponses_link"
																						+ " left join routing_task on dossier_reponses_link.routingtaskid = routing_task.id"
																						+ " left join case_link on case_link.id = dossier_reponses_link.id"
																						+ " left join dossier_reponse on case_link.casedocumentid = dossier_reponse.id"
																						+ " where dossier_reponse.numeroQuestion IN (select numeroQuestion from Question where legislaturequestion = ADDLEGISLATURECOURANTE)"
																						+ " group by routing_task.distributionmailboxid, dossier_reponse."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT;

	private static final String					NB_QUESTION_GROUPE_QUERY		= "select count(*) as n, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ " as min, question.groupepolitique as groupe, question.originequestion as origine"
																						+ " from question "
																						+ " where question.typequestion = 'QE'"
																						+ " and question.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "','"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "')"
																						+ " and question.legislaturequestion = ADDLEGISLATURECOURANTE"
																						+ " group by question.originequestion, question."
																						+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																						+ ", question.groupepolitique";

	private static final String					ETAPE_QUERY						= "insert into statistique_etape "
																						+ " select q.datepublicationjo datejo, q.numeroquestion numero,  "
																						+ " q.originequestion origine,  q.prenomauteur || ' ' || q.nomauteur auteur,  "
																						+ " d.ministereattributairecourant idministere, null ministere,"
																						+ " r.directionid iddirection, r.directionlabel direction, re.datepublicationjoreponse datejoreponse, "
																						+ " r.type idtype,  v.\"label\" type,  r.postelabel poste, "
																						+ " r.distributionmailboxid idmailbox,  r.datedebutetape datedebut,  "
																						+ " r.datefinetape datefin,  q.hasreponseinitiee repondu,  r.ministereid idministereetape,  "
																						+ " q.IDMINISTERERATTACHEMENT, q.INTITULEMINISTERERATTACHEMENT, r.VALIDATIONSTATUS, q.datesignalementquestion, null edition, null EDITIONMINISTERERATTACHEMENT, q.INTITULEDIRECTIONPILOTE "
																						+ " from question q, "
																						+ " dossier_reponse d "
																						+ " left join reponse re "
																						+ " on re.id = d.iddocumentreponse, "
																						+ " routing_task r  "
																						+ " left join voc_cm_routing_task_type v   "
																						+ " on v.\"id\" = r.type  "
																						+ " where d.iddocumentquestion = q.id  "
																						+ " and r.documentrouteid = d.lastdocumentroute"
																						+ " and q.legislaturequestion = ADDLEGISLATURECOURANTE "
																						+ " and q.etatquestion in ('"
																						+ VocabularyConstants.ETAT_QUESTION_EN_COURS
																						+ "', '"
																						+ VocabularyConstants.ETAT_QUESTION_REPONDU
																						+ "') ";

	private static final String					UPDATE_VALIDATION_SIGNATURE		= "UPDATE statistique_date_parcours sdp SET sdp.DATETRANSMISSIONMINISTERE = null "
																						+ " WHERE EXISTS (SELECT 1 FROM statistique_etape se "
																						+ " WHERE sdp.origine = se.origine AND sdp.numero = se.numero AND sdp.DATETRANSMISSIONMINISTERE = se.datefin "
																						+ " AND se.idtype = '"
																						+ VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE
																						+ "' "
																						+ " AND se.validationstatus <> '"
																						+ STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE
																						+ "' ) ";

	private static final String					UPDATE_VALIDATION_TRANSMISSION	= "UPDATE statistique_date_parcours sdp SET sdp.DATETRANSMISSIONPARLEMENT = null "
																						+ " WHERE EXISTS (SELECT 1 FROM statistique_etape se "
																						+ " WHERE sdp.origine = se.origine AND sdp.numero = se.numero AND sdp.DATETRANSMISSIONPARLEMENT = se.datefin "
																						+ " AND se.idtype = '"
																						+ VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE
																						+ "' "
																						+ " AND se.validationstatus <> '"
																						+ STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE
																						+ "' ) ";

	private static String						legislatureCourante;

	private BatchLoggerModel					batchLogger;

	/**
	 * Default constructor
	 */
	public StatsServiceImpl() {
		// do nothing
	}

	/**
	 * Must be called when the service is no longer needed
	 */
	public static void dispose() {
		deactivatePersistenceProvider();
	}

	public static PersistenceProvider getOrCreatePersistenceProvider() {
		if (persistenceProvider == null) {
			synchronized (StatsServiceImpl.class) {
				if (persistenceProvider == null) {
					activatePersistenceProvider();
				}
			}
		}
		return persistenceProvider;
	}

	private static void activatePersistenceProvider() {
		Thread thread = Thread.currentThread();
		ClassLoader last = thread.getContextClassLoader();
		try {
			thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
			PersistenceProviderFactory persistenceProviderFactory = Framework
					.getLocalService(PersistenceProviderFactory.class);
			persistenceProvider = persistenceProviderFactory.newProvider("sword-provider");
			persistenceProvider.openPersistenceUnit();
		} finally {
			thread.setContextClassLoader(last);
		}
	}

	private static void deactivatePersistenceProvider() {
		if (persistenceProvider != null) {
			synchronized (StatsServiceImpl.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	@Override
	public void computeStats(final CoreSession session, final BatchLoggerModel batchLoggerModel) throws ClientException {
		this.batchLogger = batchLoggerModel;
		getOrCreatePersistenceProvider().run(true, new RunVoid() {
			public void runWith(EntityManager entityManager) throws ClientException {
				legislatureCourante = STServiceLocator.getSTParametreService().getParametreValue(session,
						ReponsesParametreConstant.LEGISLATURE_COURANTE);
				computeStats(session, entityManager);
			}
		});
	}

	private void computeStats(CoreSession session, EntityManager entityManager) throws ClientException {
		LOG.info("Batch de calcul des statistiques");
		final long startTime = Calendar.getInstance().getTimeInMillis();

		try {
			computeMinistere(session, entityManager);
			entityManager.flush();

			computeQuestionReponse(session, entityManager);
			entityManager.flush();

			computeMois(session, entityManager);
			entityManager.flush();

			computeDirection(session, entityManager);
			entityManager.flush();

			computeValeur(session, entityManager);
			entityManager.flush();

			computeGroupe(session, entityManager);
			entityManager.flush();

			computeEtape(session, entityManager);
			entityManager.flush();

			computeView(session, entityManager);
			entityManager.flush();
		} catch (Exception e) {
			throw new ClientException(e);
		} finally {
			try {
				final long endTime = Calendar.getInstance().getTimeInMillis();
				STServiceLocator.getSuiviBatchService().createBatchResultFor(batchLogger,
						"Batch de calcul des statistiques", endTime - startTime);
			} catch (Exception e) {
				LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
			}
			LOG.info("Fin du batch de calcul des statistiques");
		}

	}

	private void computeView(CoreSession session, EntityManager entityManager) {
		final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
		LOG.info("Mise à jour table statistiques");

		// Vide la table statistique date parcours
		long startTime = Calendar.getInstance().getTimeInMillis();
		int rowCount = entityManager.createNativeQuery("delete from statistique_date_parcours").executeUpdate();
		long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger,
					"Nombre de lignes supprimées de la table des dates parcours", rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Suppression des élements de la table des dates parcours : " + rowCount + " lignes");

		StringBuilder insertQuery = new StringBuilder();
		insertQuery
				.append("INSERT INTO statistique_date_parcours ")
				.append(" SELECT t3.IDMINISTERERATTACHEMENT as idministere, t3.INTITULEMINISTERERATTACHEMENT as ministere, t3.origine as origine, t3.numero as numero, ")
				.append(" t3.datejo as datejo, t3.datejoreponse as datejoreponse, t2.datefinMin as DATETRANSMISSIONMINISTERE, ")
				.append(" t1.DateTransmissionParlement as DateTransmissionParlement, t3.datesignalementquestion as DATESIGNALEMENTQUESTION,t3.EDITIONMINISTERERATTACHEMENT ")
				.append(" FROM (select origine,numero, max(case when idtype=11 then datefin else null end) DateTransmissionParlement ")
				.append(" FROM statistique_etape group by origine,numero,datejo,datejoreponse,IDMINISTERERATTACHEMENT,INTITULEMINISTERERATTACHEMENT,EDITIONMINISTERERATTACHEMENT order by numero) t1, ")
				.append(" (select origine, numero, (case when( max(nvl(datefin, date '9999-12-31')))=date '9999-12-31' then null else max(nvl(datefin, date '9999-12-31')) end) as datefinMin ")
				.append(" FROM statistique_etape where idtype = '")
				.append(VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE)
				.append("' group by origine, numero,datejo, datejoreponse,IDMINISTERERATTACHEMENT order by numero) t2,")
				.append(" (select origine,numero,datejo,IDMINISTERERATTACHEMENT,INTITULEMINISTERERATTACHEMENT,datejoreponse,datesignalementquestion,EDITIONMINISTERERATTACHEMENT ")
				.append(" FROM statistique_etape group by origine, numero,datejo, datejoreponse,IDMINISTERERATTACHEMENT,INTITULEMINISTERERATTACHEMENT,datejoreponse,datesignalementquestion,EDITIONMINISTERERATTACHEMENT order by numero) t3 ")
				.append(" where t3.origine = t1.origine (+) and t3.origine=t2.origine (+) and t3.numero = t1.numero (+) and t3.numero=t2.numero (+)");
		startTime = Calendar.getInstance().getTimeInMillis();
		rowCount = entityManager.createNativeQuery(insertQuery.toString()).executeUpdate();
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger,
					"Nombre d'éléments ajoutées à la table des dates parcours", rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Remplissage de la table des dates parcours : " + rowCount + " lignes");

		// Mise à jour final de la date de transmission au ministère : vérification de la validité de la dernière
		// signature
		LOG.info("Vérification du statut des étapes 'Pour signature'");
		startTime = Calendar.getInstance().getTimeInMillis();
		rowCount = entityManager.createNativeQuery(UPDATE_VALIDATION_SIGNATURE).executeUpdate();
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService
					.createBatchResultFor(
							batchLogger,
							"Nombre de corrections de la date de transmission au ministère en fonction de la validité de l'étape",
							rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Correction de la date de transmission au ministère en fonction de la validité de l'étape : "
				+ rowCount + " lignes");

		// Mise à jour final de la date de transmission au parlement : vérification de la validité de la dernière
		// transmission
		LOG.info("Vérification du statut des étapes 'Pour transmission assemblée'");
		startTime = Calendar.getInstance().getTimeInMillis();
		rowCount = entityManager.createNativeQuery(UPDATE_VALIDATION_TRANSMISSION).executeUpdate();
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger,
					"Nombre de corrections de la date de transmission assemblée en fonction de la validité de l'étape",
					rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Correction de la date de transmission assemblée en fonction de la validité de l'étape : " + rowCount
				+ " lignes");

	}

	@SuppressWarnings("unchecked")
	private void computeEtape(CoreSession session, EntityManager entityManager) throws ClientException {
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
		final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
		LOG.info("Mise à jour des étapes");

		// Vide la table des étapes
		long startTime = Calendar.getInstance().getTimeInMillis();
		int rowCount = entityManager.createNativeQuery("delete from statistique_etape").executeUpdate();
		long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Nombre d'éléments supprimés de la table des étapes",
					rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Suppression des élements de la table des étapes : " + rowCount + " lignes");

		String requeteEtape = ETAPE_QUERY;
		requeteEtape = requeteEtape.replace("ADDLEGISLATURECOURANTE", legislatureCourante);

		// Rempli la table des étapes
		startTime = Calendar.getInstance().getTimeInMillis();
		rowCount = entityManager.createNativeQuery(requeteEtape).executeUpdate();
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Remplissage de la table des étapes", rowCount, endTime
					- startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Remplissage de la table des étapes : " + rowCount + " lignes");

		// Dénormalisation des postes et des directions
		LOG.info("Dénormalisation des postes et des directions");
		startTime = Calendar.getInstance().getTimeInMillis();
		Query query = entityManager.createNativeQuery("select idmailbox from statistique_etape group by idmailbox");
		List<String> idsMailbox = query.getResultList();
		List<String> idsPoste = new ArrayList<String>();
		for (String idMailbox : idsMailbox) {
			idsPoste.add(idMailbox.replace("poste-", ""));
		}

		List<PosteNode> postes = STServiceLocator.getSTPostesService().getPostesNodes(idsPoste);
		for (OrganigrammeNode poste : postes) {
			// Dénormalisation du poste
			Query queryPoste = entityManager
					.createNativeQuery("update statistique_etape set poste = :labelPoste where idmailbox = :idMailbox and poste is null");
			queryPoste.setParameter("labelPoste", poste.getLabel());
			queryPoste.setParameter("idMailbox", "poste-" + poste.getId());
			queryPoste.executeUpdate();

			// Dénormalisation de la direction
			List<OrganigrammeNode> directions = usService.getDirectionFromPoste(poste.getId());
			if (directions.isEmpty()) {
				Query queryDirection = entityManager.createNativeQuery("update statistique_etape "
						+ " set iddirection = 0 " + " where idmailbox = :idMailbox and direction is null");
				queryDirection.setParameter("idMailbox", "poste-" + poste.getId());
				queryDirection.executeUpdate();
			} else {
				// Depuis la FEV009, un poste n'est lié qu'à une seule direction
				OrganigrammeNode direction = directions.get(0);
				Query queryDirection = entityManager.createNativeQuery("update statistique_etape "
						+ " set iddirection = :idDirection, direction = :labelDirection "
						+ " where idmailbox = :idMailbox and direction is null");
				queryDirection.setParameter("idDirection", direction.getId());
				queryDirection.setParameter("labelDirection", direction.getLabel());
				queryDirection.setParameter("idMailbox", "poste-" + poste.getId());
				queryDirection.executeUpdate();
			}
		}
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Dénormalisation des postes et des directions", endTime
					- startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}

		// Dénormalisation des ministères
		LOG.info("Dénormalisation des ministères");
		startTime = Calendar.getInstance().getTimeInMillis();

		query = entityManager.createNativeQuery("select idministere from statistique_etape group by idministere");
		List<String> idsMinistere = query.getResultList();
		for (String idMinistere : idsMinistere) {
			OrganigrammeNode ministere = ministeresService.getEntiteNode(idMinistere);
			if (ministere == null) {
				continue;
			}
			Query queryMinistere = entityManager
					.createNativeQuery("update statistique_etape set ministere = :labelMinistere,  edition = :editionValue where idministere = :idMinistere and ministere is null");
			queryMinistere.setParameter("labelMinistere", ministere.getLabel());
			queryMinistere.setParameter("editionValue", ((EntiteNode) ministere).getEdition());
			queryMinistere.setParameter("idMinistere", ministere.getId());
			queryMinistere.executeUpdate();
			Query queryMinistere2 = entityManager
					.createNativeQuery("update statistique_etape set INTITULEMINISTERERATTACHEMENT = :labelMinistere, EDITIONMINISTERERATTACHEMENT = :edition  where IDMINISTERERATTACHEMENT = :idMinistere");
			queryMinistere2.setParameter("labelMinistere", ministere.getLabel());
			queryMinistere2.setParameter("idMinistere", ministere.getId());
			queryMinistere2.setParameter("edition", ((EntiteNode) ministere).getEdition());
			queryMinistere2.executeUpdate();
		}

		Query query2 = entityManager
				.createNativeQuery("select IDMINISTERERATTACHEMENT from statistique_etape group by IDMINISTERERATTACHEMENT");
		List<String> idsMinistereRattachement = query2.getResultList();
		for (String idMinistere : idsMinistereRattachement) {
			if (!idsMinistere.contains(idMinistere)) {
				OrganigrammeNode ministere = ministeresService.getEntiteNode(idMinistere);
				if (ministere == null) {
					continue;
				}
				Query queryMinistereRattachement = entityManager
						.createNativeQuery("update statistique_etape set INTITULEMINISTERERATTACHEMENT = :labelMinistere, EDITIONMINISTERERATTACHEMENT = :edition  where IDMINISTERERATTACHEMENT = :idMinistere");
				queryMinistereRattachement.setParameter("labelMinistere", ministere.getLabel());
				queryMinistereRattachement.setParameter("edition", ((EntiteNode) ministere).getEdition());
				queryMinistereRattachement.setParameter("idMinistere", ministere.getId());
				queryMinistereRattachement.executeUpdate();
			}
		}
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Dénormalisation des ministères", endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}

		// Labels des types d'étapes de feuille de route
		LOG.info("Récupération des libellés des étapes de feuille de route");
		startTime = Calendar.getInstance().getTimeInMillis();
		ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.FRENCH);
		query = entityManager.createNativeQuery("select type from statistique_etape group by type");
		List<String> types = query.getResultList();
		for (String type : types) {
			try {
				String label = bundle.getString(type);
				Query queryType = entityManager
						.createNativeQuery("update statistique_etape set type = :label where type = :type");
				queryType.setParameter("label", label);
				queryType.setParameter("type", type);
				queryType.executeUpdate();
			} catch (MissingResourceException e) {
				continue;
			}
		}
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger,
					"Récupération des libellés des étapes de feuille de route", endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}

	}

	private void computeGroupe(CoreSession session, EntityManager entityManager) throws ClientException {
		final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
		LOG.info("Mise à jour des groupes");

		// Vide la table des groupes
		long startTime = Calendar.getInstance().getTimeInMillis();
		int rowCount = entityManager.createQuery("delete from StatistiqueGroupe").executeUpdate();
		long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Nombre d'éléments supprimés de la table des groupes",
					rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Suppression des élements de la table des groupes : " + rowCount + " lignes");

		startTime = Calendar.getInstance().getTimeInMillis();
		IterableQueryResult res = null;
		try {
			String requeteNBQuestionGroupe = NB_QUESTION_GROUPE_QUERY;
			requeteNBQuestionGroupe = requeteNBQuestionGroupe.replace("ADDLEGISLATURECOURANTE", legislatureCourante);

			res = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_COUNT, "dc:title", "dc:creator",
					"dc:source" }, requeteNBQuestionGroupe, new Object[] {});
			Iterator<Map<String, Serializable>> iterator = res.iterator();
			final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
			while (iterator.hasNext()) {
				Map<String, Serializable> row = iterator.next();
				int count = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
				String min = (String) row.get("dc:title");
				String groupe = (String) row.get("dc:creator");
				String origine = (String) row.get("dc:source");

				EntiteNode entite = null;
				if (min != null) {
					entite = ministeresService.getEntiteNode(min);
				}
				if (entite == null) {
					continue;
				}

				StatistiqueGroupe stat = new StatistiqueGroupe(0, entite.getLabel(), origine, entite.getEdition(),
						groupe, entite.getOrdre().intValue(), entite.isActive(), count);
				entityManager.persist(stat);
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Mise à jour des groupes", endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Fin mise à jour des groupes");
	}

	private void computeDirection(CoreSession session, EntityManager entityManager) throws ClientException {
		final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();

		LOG.info("Mise à jour des directions");

		// Vide la table des directions
		long startTime = Calendar.getInstance().getTimeInMillis();
		int rowCount = entityManager.createQuery("delete from StatistiqueDirection").executeUpdate();
		long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger,
					"Nombre d'éléments supprimés de la table des directions", rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Suppression des élements de la table des directions : " + rowCount + " lignes");

		Map<String, Integer> nbQuestionDirection = new HashMap<String, Integer>();
		Map<String, String> directionMinistere = new HashMap<String, String>();

		// Nombre de questions par poste
		startTime = Calendar.getInstance().getTimeInMillis();
		IterableQueryResult res = null;
		try {
			STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
			String requeteNBQuestionPoste = NB_QUESTION_BY_POSTE_QUERY;
			requeteNBQuestionPoste = requeteNBQuestionPoste.replace("ADDLEGISLATURECOURANTE", legislatureCourante);

			res = QueryUtils.doSqlQuery(session,
					new String[] { FlexibleQueryMaker.COL_COUNT, "dc:title", "dc:creator" }, requeteNBQuestionPoste,
					new Object[] {});
			Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				Map<String, Serializable> row = iterator.next();
				int count = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
				String posteid = (String) row.get("dc:title");
				String ministereId = (String) row.get("dc:creator");
				EntiteNode ministere = STServiceLocator.getSTMinisteresService().getEntiteNode(ministereId);

				List<OrganigrammeNode> directions = usService.getDirectionFromPoste(posteid);

				for (OrganigrammeNode dir : directions) {
					UniteStructurelleNode us = (UniteStructurelleNode) dir;
					if (us.getEntiteParentList().contains(ministere)) {
						String key = ministereId + "#" + dir.getLabel();
						directionMinistere.put(key, ministereId);
						if (!nbQuestionDirection.containsKey(key)) {
							nbQuestionDirection.put(key, count);
						} else {
							nbQuestionDirection.put(key, nbQuestionDirection.get(key) + count);
						}
					}
				}
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Debug stat - debut enregistrement");
		}
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		for (Entry<String, Integer> entry : nbQuestionDirection.entrySet()) {
			String ministereId = directionMinistere.get(entry.getKey());
			EntiteNode ministereNode = null;
			LOG.debug("Debug stat - ministereId : " + ministereId);
			if (ministereId != null) {
				ministereNode = ministeresService.getEntiteNode(ministereId);
			}
			if (ministereNode != null) {
				LOG.debug("Debug stat - ministereNode != null");
				StatistiqueDirection stat = new StatistiqueDirection(0, entry.getKey().split("#", 2)[1],
						ministereNode.getLabel(), ministereNode.getEdition(), ministereNode.getOrdre().intValue(),
						true, entry.getValue());
				LOG.debug("Debug stat - direction : " + entry.getKey());
				entityManager.persist(stat);
			} else {
				LOG.debug("Debug stat - ministereNode = null");

			}
		}
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Mise à jour des directions", endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}

		LOG.info("Fin mise à jour des directions");
	}

	private void computeMinistere(CoreSession session, EntityManager entityManager) throws ClientException {
		final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
		LOG.info("Mise à jour des ministères");
		long startTime = Calendar.getInstance().getTimeInMillis();
		int rowCount = 0;
		// Vide la table des ministères
		try {
			rowCount = entityManager.createQuery("delete from StatistiqueMinistere").executeUpdate();
		} catch (Exception e) {
			try {
				rowCount = entityManager.createQuery("delete from StatistiqueMinistere").executeUpdate();
			} catch (Exception ee) {
				throw new ReponsesException(
						"Impossible d'executer la suppression des éléments de la table StatistiqueMinistere", e);
			}
		}
		long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger,
					"Nombre d'éléments supprimés de la table des ministères", rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Suppression des élements de la table des ministères : " + rowCount + " lignes");

		startTime = Calendar.getInstance().getTimeInMillis();
		String requeteNBRenouvelle = NB_RENOUVELLE_QUERY;
		String requeteNBECRenouvelle = NB_RENOUVELLE_EC_QUERY;
		String requeteNBRetire = NB_RETIRE_QUERY;
		String requeteNBSansReponse = NB_SANS_REPONSE_QUERY;
		String requeteNBSansReponseSup = NB_SANS_REPONSE_SUP_QUERY;
		requeteNBRenouvelle = requeteNBRenouvelle.replace("ADDLEGISLATURECOURANTE", legislatureCourante);
		requeteNBECRenouvelle = requeteNBECRenouvelle.replace("ADDLEGISLATURECOURANTE", legislatureCourante);
		requeteNBRetire = requeteNBRetire.replace("ADDLEGISLATURECOURANTE", legislatureCourante);
		requeteNBSansReponse = requeteNBSansReponse.replace("ADDLEGISLATURECOURANTE", legislatureCourante);
		requeteNBSansReponseSup = requeteNBSansReponseSup.replace("ADDLEGISLATURECOURANTE", legislatureCourante);

		// Execution des requêtes
		Map<String, Integer> renouvelleResult = fetchMinisteresStatsFromQuery(session, requeteNBRenouvelle);
		Map<String, Integer> renouvelleECResult = fetchMinisteresStatsFromQuery(session, requeteNBECRenouvelle);
		Map<String, Integer> retireResult = fetchMinisteresStatsFromQuery(session, requeteNBRetire);
		Map<String, Integer> sansReponseResult = fetchMinisteresStatsFromQuery(session, requeteNBSansReponse);
		Map<String, Integer> sansReponseSupResult = fetchMinisteresStatsFromQuery(session, requeteNBSansReponseSup);

		Map<String, StatistiqueMinistere> dataResult = new HashMap<String, StatistiqueMinistere>();

		// Renouvellées
		for (Entry<String, Integer> entry : renouvelleResult.entrySet()) {
			if (dataResult.containsKey(entry.getKey())) {
				StatistiqueMinistere stat = dataResult.get(entry.getKey());
				stat.setNbRenouvelle(entry.getValue());
			} else {
				dataResult.put(entry.getKey(), new StatistiqueMinistere(0, "", "", true, 0, entry.getValue(), 0, 0, 0,
						0));
			}
		}

		// Renouvellées en cours
		for (Entry<String, Integer> entry : renouvelleECResult.entrySet()) {
			if (dataResult.containsKey(entry.getKey())) {
				StatistiqueMinistere stat = dataResult.get(entry.getKey());
				stat.setNbRenouvelleEC(entry.getValue());
			} else {
				dataResult.put(entry.getKey(), new StatistiqueMinistere(0, "", "", true, 0, 0, entry.getValue(), 0, 0,
						0));
			}
		}

		// Retirées
		for (Entry<String, Integer> entry : retireResult.entrySet()) {
			if (dataResult.containsKey(entry.getKey())) {
				StatistiqueMinistere stat = dataResult.get(entry.getKey());
				stat.setNbRetire(entry.getValue());
			} else {
				dataResult.put(entry.getKey(), new StatistiqueMinistere(0, "", "", true, 0, 0, 0, entry.getValue(), 0,
						0));
			}
		}

		// Sans réponses
		for (Entry<String, Integer> entry : sansReponseResult.entrySet()) {
			if (dataResult.containsKey(entry.getKey())) {
				StatistiqueMinistere stat = dataResult.get(entry.getKey());
				stat.setNbQuestionSsReponse(entry.getValue());
			} else {
				dataResult.put(entry.getKey(), new StatistiqueMinistere(0, "", "", true, 0, 0, 0, 0, entry.getValue(),
						0));
			}
		}

		// Sans réponses supérieur à 2 mois
		for (Entry<String, Integer> entry : sansReponseSupResult.entrySet()) {
			if (dataResult.containsKey(entry.getKey())) {
				StatistiqueMinistere stat = dataResult.get(entry.getKey());
				stat.setNbQuestionSsReponseSup2Mois(entry.getValue());
			} else {
				dataResult.put(entry.getKey(),
						new StatistiqueMinistere(0, "", "", true, 0, 0, 0, 0, 0, entry.getValue()));
			}
		}

		// Enregistrement des résultats
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		for (Entry<String, StatistiqueMinistere> entry : dataResult.entrySet()) {
			String min = entry.getKey();
			StatistiqueMinistere data = entry.getValue();

			EntiteNode entite = null;
			if (min != null) {
				entite = ministeresService.getEntiteNode(min);
			}
			if (entite == null) {
				continue;
			}

			data.setEdition(entite.getEdition());
			data.setOrdreProtocolaire(entite.getOrdre().intValue());
			data.setMinistere(entite.getLabel());
			data.setActif(entite.isActive());

			entityManager.persist(data);
		}
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Mise à jour des ministères", endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}

		LOG.info("Fin mise à jour des ministères");
	}

	private void computeMois(CoreSession session, EntityManager entityManager) throws ClientException {
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
		LOG.info("Mise à jour des mois");

		// Vide la table des mois
		long startTime = Calendar.getInstance().getTimeInMillis();
		int rowCount = entityManager.createQuery("delete from StatistiqueMois").executeUpdate();
		long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger,
					"Nombre d'éléments supprimés de la table des ministères", rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Suppression des élements de la table des mois : " + rowCount + " lignes");

		startTime = Calendar.getInstance().getTimeInMillis();
		Map<String, StatistiqueMois> output = new TreeMap<String, StatistiqueMois>(new Comparator<String>() {
			@Override
			public int compare(String str1, String str2) {
				Integer min1 = Integer.parseInt(str1.substring(7, str1.length()));
				Integer min2 = Integer.parseInt(str2.substring(7, str2.length()));
				String date1 = str1.substring(0, 7);
				String date2 = str2.substring(0, 7);
				if (min1.equals(min2)) {
					return date1.compareTo(date2);
				} else {
					return min1.compareTo(min2);
				}
			}
		});

		Map<String, Integer> nbQuestionTotal = new HashMap<String, Integer>();

		IterableQueryResult res = null;
		try {
			String requeteNBQuestionMois = NB_QUESTION_MOIS_QUERY;
			requeteNBQuestionMois = requeteNBQuestionMois.replace("ADDLEGISLATURECOURANTE", legislatureCourante);

			res = QueryUtils.doSqlQuery(session,
					new String[] { FlexibleQueryMaker.COL_COUNT, "dc:title", "dc:creator" }, requeteNBQuestionMois,
					new Object[] {});
			Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				Map<String, Serializable> row = iterator.next();
				int count = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
				String mois = (String) row.get("dc:title");
				String min = (String) row.get("dc:creator");

				if (!nbQuestionTotal.containsKey(min)) {
					nbQuestionTotal.put(min, count);
				} else {
					nbQuestionTotal.put(min, nbQuestionTotal.get(min) + count);
				}

				EntiteNode entite = null;
				if (min != null) {
					entite = ministeresService.getEntiteNode(min);
				}
				if (entite == null) {
					continue;
				}

				StatistiqueMois stat = new StatistiqueMois(0, entite.getLabel(), entite.getEdition(),
						entite.isActive(), entite.getOrdre().intValue(), mois, nbQuestionTotal.get(min), 0, 0);
				output.put(mois + min, stat);
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		Map<String, Integer> nbReponseTotal = new HashMap<String, Integer>();

		try {
			String requeteNBReponseMois = NB_REPONSE_MOIS_QUERY;
			requeteNBReponseMois = requeteNBReponseMois.replace("ADDLEGISLATURECOURANTE", legislatureCourante);

			res = QueryUtils.doSqlQuery(session,
					new String[] { FlexibleQueryMaker.COL_COUNT, "dc:title", "dc:creator" }, requeteNBReponseMois,
					new Object[] {});
			Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				Map<String, Serializable> row = iterator.next();
				int count = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
				String mois = (String) row.get("dc:title");
				String min = (String) row.get("dc:creator");

				if (!nbReponseTotal.containsKey(min)) {
					nbReponseTotal.put(min, count);
				} else {
					nbReponseTotal.put(min, nbReponseTotal.get(min) + count);
				}

				if (output.containsKey(mois + min)) {
					output.get(mois + min).setNbReponse(count);
					output.get(mois + min).setNbReponseTotal(nbReponseTotal.get(min));
				} else {
					EntiteNode entite = null;
					if (min != null) {
						entite = ministeresService.getEntiteNode(min);
					}
					if (entite == null) {
						continue;
					}

					StatistiqueMois stat = new StatistiqueMois(0, entite.getLabel(), entite.getEdition(),
							entite.isActive(), entite.getOrdre().intValue(), mois, 0, count, nbReponseTotal.get(min));
					output.put(mois + min, stat);
				}
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		// Modification des nombres de réponses et de questions totals à zéro
		String prevMin = "";
		Integer prevNbQuestion = 0;
		Integer prevNbReponseTotal = 0;
		for (Entry<String, StatistiqueMois> entry : output.entrySet()) {
			StatistiqueMois stat = entry.getValue();
			String min = entry.getKey().substring(7, entry.getKey().length());

			if (prevMin.equals(min)) {
				if (stat.getNbQuestion() == 0) {
					stat.setNbQuestion(prevNbQuestion);
				}
				if (stat.getNbReponseTotal() == 0) {
					stat.setNbReponseTotal(prevNbReponseTotal);
				}
			}

			entityManager.persist(stat);

			prevMin = min;
			prevNbQuestion = stat.getNbQuestion();
			prevNbReponseTotal = stat.getNbReponseTotal();
		}
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Mise à jour des mois", endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}

		LOG.info("Fin mise à jour des mois");
	}

	protected void computeValeur(CoreSession session, EntityManager entityManager) {
		LOG.info("Mise à jour des valeurs");

		long startTime = Calendar.getInstance().getTimeInMillis();
		Query query = entityManager.createQuery("from StatistiqueValeur");
		List<?> results = query.getResultList();
		for (Object result : results) {
			StatistiqueValeur statValeur = (StatistiqueValeur) result;
			entityManager.createNativeQuery(
					"update statistique_valeur set valeur=(" + statValeur.getRequete() + ") where id="
							+ statValeur.getId()).executeUpdate();
		}
		long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			STServiceLocator.getSuiviBatchService().createBatchResultFor(batchLogger, "Mise à jour des valeurs",
					endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}

		LOG.info("Fin mise à jour des valeurs");
	}

	protected void computeQuestionReponse(CoreSession session, EntityManager entityManager) throws ClientException {
		final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();

		// Vide la table des statistiques
		long startTime = Calendar.getInstance().getTimeInMillis();
		int rowCount = entityManager.createQuery("delete from StatistiqueQuestionReponse").executeUpdate();
		long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger,
					"Nombre d'éléments supprimés de la table des statistiques", rowCount, endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOG.info("Suppression des élements de la table des statistiques : " + rowCount + " lignes");

		LOG.info("Calcul des statistiques");

		startTime = Calendar.getInstance().getTimeInMillis();
		String requeteNBRepondu1 = NB_REPONDU1_QUERY;
		String requeteNBRepondu2 = NB_REPONDU2_QUERY;
		String requeteNBReponduSup = NB_REPONDU_SUP_QUERY;
		requeteNBRepondu1 = requeteNBRepondu1.replace("ADDLEGISLATURECOURANTE", legislatureCourante);
		requeteNBRepondu2 = requeteNBRepondu2.replace("ADDLEGISLATURECOURANTE", legislatureCourante);
		requeteNBReponduSup = requeteNBReponduSup.replace("ADDLEGISLATURECOURANTE", legislatureCourante);

		Map<String, Integer> repondu1Result = fetchMinisteresOrigineStatsFromQuery(session, requeteNBRepondu1);
		Map<String, Integer> repondu2Result = fetchMinisteresOrigineStatsFromQuery(session, requeteNBRepondu2);
		Map<String, Integer> reponduSupResult = fetchMinisteresOrigineStatsFromQuery(session, requeteNBReponduSup);

		StatistiqueQuestionReponse AnGlobal = new StatistiqueQuestionReponse(0, "GLOBAL", "GLOBAL", false, 0, "AN", 0,
				0, 0, 0);
		StatistiqueQuestionReponse SenatGlobal = new StatistiqueQuestionReponse(0, "GLOBAL", "GLOBAL", false, 0,
				"SENAT", 0, 0, 0, 0);

		IterableQueryResult res = null;
		try {

			String requeteNBQuestion = NB_QUESTION_QUERY;
			requeteNBQuestion = requeteNBQuestion.replace("ADDLEGISLATURECOURANTE", legislatureCourante);

			res = QueryUtils.doSqlQuery(session,
					new String[] { FlexibleQueryMaker.COL_COUNT, "dc:title", "dc:creator" }, requeteNBQuestion,
					new Object[] {});
			Iterator<Map<String, Serializable>> iterator = res.iterator();
			final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
			while (iterator.hasNext()) {
				Map<String, Serializable> row = iterator.next();
				int nbQuestion = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
				String min = (String) row.get("dc:title");
				String origine = (String) row.get("dc:creator");

				int nbRepondu1 = getNbFromResult(repondu1Result, origine, min);
				int nbRepondu2 = getNbFromResult(repondu2Result, origine, min);
				int nbReponduSup = getNbFromResult(reponduSupResult, origine, min);

				StatistiqueQuestionReponse global = AnGlobal;
				if ("SENAT".equals(origine)) {
					global = SenatGlobal;
				}
				global.setNbQuestion(global.getNbQuestion() + nbQuestion);
				global.setNbRepondu1Mois(global.getNbRepondu1Mois() + nbRepondu1);
				global.setNbRepondu2Mois(global.getNbRepondu2Mois() + nbRepondu2);
				global.setNbReponduSuperieur(global.getNbReponduSuperieur() + nbReponduSup);

				EntiteNode entite = null;
				if (min != null) {
					entite = ministeresService.getEntiteNode(min);
				}
				if (entite == null) {
					continue;
				}

				StatistiqueQuestionReponse stat = new StatistiqueQuestionReponse(0, entite.getLabel(),
						entite.getEdition(), entite.isActive(), entite.getOrdre().intValue(), origine, nbQuestion,
						nbRepondu1, nbRepondu2, nbReponduSup);
				entityManager.persist(stat);
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		// Statistiques globales
		entityManager.persist(AnGlobal);
		entityManager.persist(SenatGlobal);
		endTime = Calendar.getInstance().getTimeInMillis();
		try {
			suiviBatchService.createBatchResultFor(batchLogger, "Calcul des statistiques", endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}

		LOG.info("Fin calcul des statistiques");
	}

	private Map<String, Integer> fetchMinisteresOrigineStatsFromQuery(CoreSession session, String query)
			throws ClientException {
		Map<String, Integer> result = new HashMap<String, Integer>();

		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(session,
					new String[] { FlexibleQueryMaker.COL_COUNT, "dc:title", "dc:creator" }, query, new Object[] {});
			Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				Map<String, Serializable> row = iterator.next();
				int count = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
				String min = (String) row.get("dc:title");
				String origine = (String) row.get("dc:creator");

				result.put(origine + min, count);
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		return result;
	}

	private Map<String, Integer> fetchMinisteresStatsFromQuery(CoreSession session, String query)
			throws ClientException {
		Map<String, Integer> result = new HashMap<String, Integer>();

		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_COUNT, "dc:title" }, query,
					new Object[] {});
			Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				Map<String, Serializable> row = iterator.next();
				int count = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
				String min = (String) row.get("dc:title");

				result.put(min, count);
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		return result;
	}

	private int getNbFromResult(Map<String, Integer> result, String origine, String min) {
		if (result.containsKey(origine + min)) {
			return result.get(origine + min);
		}
		return 0;
	}

	@Override
	public void denormaliserDirection(final CoreSession session) throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunVoid() {
			public void runWith(EntityManager entityManager) throws ClientException {

				IterableQueryResult res = null;
				try {
					final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
					res = QueryUtils.doUFNXQLQuery(session, "SELECT DISTINCT r.rtsk:distributionMailboxId AS mailboxId"
							+ " FROM RouteStep AS r" + " WHERE (r.rtsk:directionId IS NULL OR r.rtsk:directionId = '')"
							+ " AND (r." + STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH + " = 'done' OR r."
							+ STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH + " = 'running')", new Object[] {});
					Iterator<Map<String, Serializable>> iterator = res.iterator();
					while (iterator.hasNext()) {
						Map<String, Serializable> m = iterator.next();
						String mailboxId = (String) m.get("mailboxId");
						String posteId = mailboxId.replace("poste-", "");

						LOG.info("Dénormalisation de la direction du poste " + posteId);

						List<OrganigrammeNode> uniteStructurelleList = usService.getDirectionFromPoste(posteId);
						if (uniteStructurelleList != null && !uniteStructurelleList.isEmpty()) {
							String directionId = uniteStructurelleList.get(0).getId();

							LOG.info("Identifiant de la direction pour le poste " + posteId + " : " + directionId);

							int count = entityManager
									.createNativeQuery(
											"update routing_task"
													+ " set directionid = :directionId"
													+ " where distributionmailboxid = :mailboxId and postelabel is not null")
									.setParameter("directionId", directionId).setParameter("mailboxId", mailboxId)
									.executeUpdate();

							LOG.info(count + " étapes de feuille de route mises à jour");
						} else {
							LOG.info("Aucun direction trouvée pour le poste " + posteId);
						}
					}
				} finally {
					if (res != null) {
						res.close();
					}
				}

				entityManager.flush();
			}
		});
	}

	@Override
	public void denormaliserEtapeSuivante(final CoreSession session) throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunVoid() {
			public void runWith(EntityManager entityManager) throws ClientException {
				final FeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
				final STPostesService postesService = STServiceLocator.getSTPostesService();
				final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
				LOG.info("Debut de denormalisation des labels d'etapes suivantes");
				IterableQueryResult dosIterat = null;
				int count = 0;
				try {
					dosIterat = QueryUtils.doUFNXQLQuery(session, "SELECT d.ecm:uuid as dosId" + " FROM Dossier AS d"
							+ " WHERE d." + STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH + " = 'running'"
							+ " AND d.dos:lastDocumentRoute is not null", new Object[] {});
					Iterator<Map<String, Serializable>> iterator = dosIterat.iterator();
					while (iterator.hasNext()) {
						Map<String, Serializable> m = iterator.next();
						String dosId = (String) m.get("dosId");
						LOG.info("Dénormalisation du label d'étape suivante du dossier " + dosId);

						Dossier dossier = session.getDocument(new IdRef(dosId)).getAdapter(Dossier.class);
						List<DocumentModel> runningSteps = feuilleRouteService.getRunningSteps(session,
								dossier.getLastDocumentRoute());
						Set<String> labels = new HashSet<String>();
						List<String> postesId = new ArrayList<String>();
						List<DocumentModel> nextSteps = new ArrayList<DocumentModel>();
						if (runningSteps != null) {
							for (DocumentModel runningStep : runningSteps) {
								nextSteps.addAll(feuilleRouteService.findNextSteps(session,
										dossier.getLastDocumentRoute(), runningStep, null));
							}

							if (nextSteps != null) {
								for (DocumentModel nextStepDoc : nextSteps) {
									if (!nextStepDoc.isFolder()) {
										STRouteStep nextStep = nextStepDoc.getAdapter(STRouteStep.class);
										postesId.add(mailboxPosteService.getPosteIdFromMailboxId(nextStep
												.getDistributionMailboxId()));
									}
								}
							}
						}
						List<PosteNode> listNode = postesService.getPostesNodes(postesId);

						for (OrganigrammeNode node : listNode) {
							labels.add(node.getLabel());
						}

						String label;
						if (labels.isEmpty()) {
							label = "-";
						} else {
							label = StringUtils.join(labels, ", ");
						}

						LOG.info("Libellé de l'étape suivante pour le dossier " + dosId + " : " + label);
						count += entityManager
								.createNativeQuery(
										"update dossier_reponse" + " set LABELETAPESUIVANTE = :etapeSuivanteLabel"
												+ " where id = :dosId").setParameter("etapeSuivanteLabel", label)
								.setParameter("dosId", dosId).executeUpdate();

					}
				} finally {
					if (dosIterat != null) {
						dosIterat.close();
					}
				}
				LOG.info(count + " dossiers mis à jour");

				entityManager.flush();
			}
		});
	}

	@Override
	public void denormaliserMinistere(final CoreSession session) throws ClientException {
		getOrCreatePersistenceProvider().run(true, new RunVoid() {
			public void runWith(EntityManager entityManager) throws ClientException {
				final STPostesService postesService = STServiceLocator.getSTPostesService();
				IterableQueryResult res = null;
				try {
					res = QueryUtils.doUFNXQLQuery(session, "SELECT DISTINCT r.rtsk:distributionMailboxId AS mailboxId"
							+ " FROM RouteStep AS r" + " WHERE (r.rtsk:ministereId IS NULL OR r.rtsk:ministereId = '')"
							+ " AND r.rtsk:ministereLabel is not null AND r.rtsk:dateDebutEtape > DATE '2012-05-23'",
							new Object[] {});
					Iterator<Map<String, Serializable>> iterator = res.iterator();
					final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
					while (iterator.hasNext()) {
						Map<String, Serializable> m = iterator.next();
						String mailboxId = (String) m.get("mailboxId");
						String posteId = mailboxId.replace("poste-", "");
						PosteNode posteDoc = postesService.getPoste(posteId);
						LOGGER.info(session, STLogEnumImpl.DENO_PST_TEC, "Dénormalisation du ministereId du poste "
								+ posteId);

						List<EntiteNode> entiteNodeList = ministeresService.getMinistereParentFromPoste(posteId);
						if (entiteNodeList != null && !entiteNodeList.isEmpty()) {
							// Si le poste n'a qu'un seul ministere parent, on met à jour l'étape
							// S'il en a plusieurs on prend l'ordre protocolaire le plus bas
							if (entiteNodeList.size() > 1) {
								Collections.sort(entiteNodeList, new ProtocolarOrderComparator());
								// On log les postes avec plusieurs ministeres
								LOGGER.warn(session, STLogEnumImpl.ANO_PST_MULTI_MIN_TEC, posteDoc);
							}
							String ministereId = entiteNodeList.get(0).getId();
							LOGGER.info(session, STLogEnumImpl.DENO_PST_TEC,
									"Identifiant de ministereId pour le poste " + posteId + " : " + ministereId);
							int count = entityManager
									.createNativeQuery(
											"update routing_task" + " set ministereid = :ministereId"
													+ " where distributionmailboxid = :mailboxId ")
									.setParameter("ministereId", ministereId).setParameter("mailboxId", mailboxId)
									.executeUpdate();
							LOGGER.info(session, SSLogEnumImpl.UPDATE_STEP_TEC, count
									+ " étapes de feuille de route mises à jour");
						} else {
							LOGGER.warn(session, STLogEnumImpl.ANO_PST_NONE_MIN_TEC, posteDoc);
						}
					}
				} finally {
					if (res != null) {
						res.close();
					}
				}

				entityManager.flush();
			}
		});
	}

}
