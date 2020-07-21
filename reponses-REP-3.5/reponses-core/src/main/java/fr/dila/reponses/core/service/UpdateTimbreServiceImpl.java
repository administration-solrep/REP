package fr.dila.reponses.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingLineDetail;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.DateUtil;

/**
 * Service de mise à jour des timbres
 * 
 */
public class UpdateTimbreServiceImpl implements UpdateTimbreService {

	private static final String	COL_MINISTERE							= FlexibleQueryMaker.COL_ID;
	private static final String	COL_COUNT								= FlexibleQueryMaker.COL_COUNT;
	private static final String	COL_ID									= FlexibleQueryMaker.COL_ID;

	/**
	 * Une question close est une QE dont l'état est 'repondu' et qui n'a plus d'étape en cours ou à venir
	 */
	private static final String	QUERY_COUNT_CLOSED_QUESTIONS			= "SELECT "
																				+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																				+ " as "
																				+ COL_MINISTERE
																				+ ", count(1) as "
																				+ COL_COUNT
																				+ " FROM dossier_reponse d inner join question q on d."
																				+ DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID
																				+ " = q.id WHERE q."
																				+ DossierConstants.DOSSIER_ETAT_QUESTION_VALUE_PROPERTY
																				+ " = 'repondu' AND q."
																				+ DossierConstants.DOSSIER_TYPE_QUESTION
																				+ " = ? "
																				+ " AND not exists(select rt.id from routing_task rt inner join misc lcs on rt.id = lcs.id where rt."
																				+ STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY
																				+ " = d."
																				+ STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY
																				+ " and lcs.lifecyclestate IN ('running', 'ready')) GROUP BY q."
																				+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION;

	/**
	 * Une question close est une QE dont l'état est 'repondu' et qui n'a plus d'étape en cours ou à venir
	 */
	private static final String	QUERY_COUNT_CLOSED_QUESTIONS_FOR_MIN	= "SELECT "
																				+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																				+ " as "
																				+ COL_MINISTERE
																				+ ", count(1) as "
																				+ COL_COUNT
																				+ " FROM dossier_reponse d inner join question q on d."
																				+ DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID
																				+ " = q.id WHERE q."
																				+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																				+ " = ? AND q."
																				+ DossierConstants.DOSSIER_ETAT_QUESTION_VALUE_PROPERTY
																				+ " = 'repondu' AND q."
																				+ DossierConstants.DOSSIER_TYPE_QUESTION
																				+ " = ? "
																				+ " AND not exists(select rt.id from routing_task rt inner join misc lcs on rt.id = lcs.id where rt."
																				+ STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY
																				+ " = d."
																				+ STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY
																				+ " and lcs.lifecyclestate IN ('running', 'ready')) ";

	/**
	 * 
	 */
	private static final String	QUERY_CLOSED_QUESTIONS_FOR_MIN			= "SELECT q.id FROM dossier_reponse d inner join question q on d."
																				+ DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID
																				+ " = q.id WHERE q."
																				+ DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION
																				+ " = '%s' AND q."
																				+ DossierConstants.DOSSIER_ETAT_QUESTION_VALUE_PROPERTY
																				+ " = 'repondu' AND q."
																				+ DossierConstants.DOSSIER_TYPE_QUESTION
																				+ " = '%s' "
																				+ " AND not exists(select rt.id from routing_task rt inner join misc lcs on rt.id = lcs.id where rt."
																				+ STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY
																				+ " = d."
																				+ STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY
																				+ " and lcs.lifecyclestate IN ('running', 'ready')) ";

	/**
	 * une question signée est une question 'QE' avec signature qui a au moins une etape a venir ou en cours dans sa fdr
	 */
	private static final String	QUERY_COUNT_SIGNED_QUESTIONS			= "SELECT "
																				+ DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT
																				+ " as "
																				+ COL_MINISTERE
																				+ ", count(1) as "
																				+ COL_COUNT
																				+ " FROM dossier_reponse d inner join Reponse r on d."
																				+ DossierConstants.DOSSIER_DOCUMENT_REPONSE_ID
																				+ " = r.id inner join question q on q.id = d."
																				+ DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID
																				+ " WHERE r."
																				+ DossierConstants.DOSSIER_SIGNATURE_REPONSE
																				+ " IS NOT NULL AND q."
																				+ DossierConstants.DOSSIER_TYPE_QUESTION
																				+ " = ? "
																				+ " AND exists(select rt.id from routing_task rt inner join misc lcs on rt.id=lcs.id where rt."
																				+ STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY
																				+ " = d."
																				+ STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY
																				+ " and lcs.lifecyclestate IN ('running', 'ready')) GROUP BY d."
																				+ DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT;

	/**
	 * une question signée est une question 'QE' avec signature qui a au moins une etape a venir ou en cours dans sa fdr
	 */
	private static final String	QUERY_SIGNED_QUESTIONS_FOR_MIN			= "SELECT d.id as "
																				+ COL_ID
																				+ ", 1 as "
																				+ COL_COUNT
																				+ " FROM dossier_reponse d inner join Reponse r on d."
																				+ DossierConstants.DOSSIER_DOCUMENT_REPONSE_ID
																				+ " = r.id inner join question q on q.id = d."
																				+ DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID
																				+ " WHERE d."
																				+ DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT
																				+ " = ? AND r."
																				+ DossierConstants.DOSSIER_SIGNATURE_REPONSE
																				+ " IS NOT NULL AND q."
																				+ DossierConstants.DOSSIER_TYPE_QUESTION
																				+ " = ? "
																				+ " AND exists(select rt.id from routing_task rt inner join misc lcs on rt.id=lcs.id where rt."
																				+ STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY
																				+ " = d."
																				+ STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY
																				+ " and lcs.lifecyclestate IN ('running', 'ready'))";

	/**
	 * une question migrable est une question 'QE' sans signature qui a au moins une etape a venir ou en cours dans sa
	 * fdr
	 */
	private static final String	QUERY_COUNT_MIGRABLE_QUESTIONS			= "SELECT "
																				+ DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT
																				+ " as "
																				+ COL_MINISTERE
																				+ ", count(1) as "
																				+ COL_COUNT
																				+ " FROM dossier_reponse d inner join question q on d."
																				+ DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID
																				+ " = q.id left join reponse r on r.id = d."
																				+ DossierConstants.DOSSIER_DOCUMENT_REPONSE_ID
																				+ " WHERE r."
																				+ DossierConstants.DOSSIER_SIGNATURE_REPONSE
																				+ " IS NULL AND q."
																				+ DossierConstants.DOSSIER_TYPE_QUESTION
																				+ " = ? "
																				+ " AND exists(select rt.id from routing_task rt inner join misc lcs on rt.id = lcs.id where rt."
																				+ STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY
																				+ " = d."
																				+ STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY
																				+ " and lcs.lifecyclestate IN ('running', 'ready')) GROUP BY d."
																				+ DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT;

	/**
	 * une question migrable est une question 'QE' sans signature qui a au moins une etape a venir ou en cours dans sa
	 * fdr
	 */
	private static final String	QUERY_MIGRABLE_DOSSIERS_FOR_MIN			= "SELECT d.id as "
																				+ COL_ID
																				+ ", 1 as "
																				+ COL_COUNT
																				+ " FROM dossier_reponse d inner join question q on d."
																				+ DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID
																				+ " = q.id left join reponse r on r.id = d."
																				+ DossierConstants.DOSSIER_DOCUMENT_REPONSE_ID
																				+ " WHERE d."
																				+ DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT
																				+ " = ? AND r."
																				+ DossierConstants.DOSSIER_SIGNATURE_REPONSE
																				+ " IS NULL AND q."
																				+ DossierConstants.DOSSIER_TYPE_QUESTION
																				+ " = ? AND exists(select rt.id from routing_task rt inner join misc lcs on rt.id = lcs.id where rt."
																				+ STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY
																				+ " = d."
																				+ STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY
																				+ " and lcs.lifecyclestate IN ('running', 'ready')) ";

	/**
	 * Requete de calcul du nombre de modèle de feuille de route pour chaque ministere
	 */
	private static final String	QUERY_COUNT_MODELE_FDR					= "SELECT f.ministere as "
																				+ COL_MINISTERE
																				+ ", count(1) as "
																				+ COL_COUNT
																				+ " FROM feuille_route f inner join hierarchy h on f.id = h.id WHERE h.parentId = ? GROUP BY f.ministere";

	@Override
	public Map<String, Long> getCloseCount(final CoreSession session) throws ClientException {
		final String[] params = new String[] { VocabularyConstants.QUESTION_TYPE_QE };
		return buildResultMapFromSQLQueryResult(session, QUERY_COUNT_CLOSED_QUESTIONS, params);
	}

	private Map<String, Long> buildResultMapFromSQLQueryResult(final CoreSession session, final String query,
			final Object[] params) throws ClientException {
		final String[] returnTypes = new String[] { COL_MINISTERE, COL_COUNT };
		return buildResultMapFromSQLQueryResult(session, query, params, returnTypes);
	}

	/**
	 * 
	 * @param session
	 * @param query
	 * @param params
	 * @param returnTypes
	 *            doit contenir deux alias de la requete : le premier chaine de caractère (String), le second numérique
	 *            (Long)
	 * @return
	 * @throws ClientException
	 */
	private Map<String, Long> buildResultMapFromSQLQueryResult(final CoreSession session, final String query,
			final Object[] params, final String[] returnTypes) throws ClientException {
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(session, returnTypes, query, params);

			final Map<String, Long> map = new HashMap<String, Long>();
			final Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				final Map<String, Serializable> row = iterator.next();
				final String label = (String) row.get(returnTypes[0]);
				final Long count = (Long) row.get(returnTypes[1]);
				map.put(label, count);
			}
			return map;
		} finally {
			if (res != null) {
				res.close();
			}
		}
	}

	@Override
	public Map<String, Long> getSigneCount(final CoreSession session) throws ClientException {
		final String[] params = new String[] { VocabularyConstants.QUESTION_TYPE_QE };
		return buildResultMapFromSQLQueryResult(session, QUERY_COUNT_SIGNED_QUESTIONS, params);
	}

	@Override
	public List<DocumentModel> getSignedDossiersForMinistere(final CoreSession session, final String idMinistere)
			throws ClientException {
		final String[] params = new String[] { idMinistere, VocabularyConstants.QUESTION_TYPE_QE };
		Map<String, Long> mapResult = buildResultMapFromSQLQueryResult(session, QUERY_SIGNED_QUESTIONS_FOR_MIN, params,
				new String[] { COL_ID, COL_COUNT });
		return QueryUtils.retrieveDocuments(session, DossierConstants.DOSSIER_DOCUMENT_TYPE, mapResult.keySet());
	}

	@Override
	public Map<String, Long> getMigrableCount(final CoreSession session) throws ClientException {
		final String[] params = new String[] { VocabularyConstants.QUESTION_TYPE_QE };
		return buildResultMapFromSQLQueryResult(session, QUERY_COUNT_MIGRABLE_QUESTIONS, params);
	}

	@Override
	public List<DocumentModel> getMigrableDossiersForMinistere(final CoreSession session, final String idMinistere)
			throws ClientException {
		final String[] params = new String[] { idMinistere, VocabularyConstants.QUESTION_TYPE_QE };
		Map<String, Long> mapResult = buildResultMapFromSQLQueryResult(session, QUERY_MIGRABLE_DOSSIERS_FOR_MIN,
				params, new String[] { COL_ID, COL_COUNT });
		return QueryUtils.retrieveDocuments(session, DossierConstants.DOSSIER_DOCUMENT_TYPE, mapResult.keySet());
	}

	@Override
	public Map<String, Long> getModelFDRCount(final CoreSession session) throws ClientException {
		final FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		final String feuilleRouteModelFolderId = feuilleRouteModelService.getFeuilleRouteModelFolderId(session);
		final String[] params = new String[] { feuilleRouteModelFolderId };
		return buildResultMapFromSQLQueryResult(session, QUERY_COUNT_MODELE_FDR, params);
	}

	@Override
	public Boolean isMigrationEnCours(final CoreSession session) throws ClientException {
		final StringBuilder query = new StringBuilder("SELECT r.ecm:uuid as id FROM ReponsesLogging as r ")
				.append(" WHERE r.replog:").append(ReponsesLoggingConstant.REPONSES_LOGGING_END_DATE)
				.append(" IS NULL ");
		return QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(query.toString())) > 0;
	}

	@Override
	public ReponsesLogging getMigrationEnCours(final CoreSession session) throws ClientException {
		final StringBuilder query = new StringBuilder("SELECT r.ecm:uuid as id FROM ReponsesLogging as r ")
				.append(" WHERE r.replog:").append(ReponsesLoggingConstant.REPONSES_LOGGING_END_DATE)
				.append(" IS NULL ");

		final List<DocumentModel> documentModelList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
				"ReponsesLogging", query.toString(), null);
		if (documentModelList.isEmpty()) {
			return null;
		} else {
			final DocumentModel doc = documentModelList.get(0);
			return doc.getAdapter(ReponsesLogging.class);
		}
	}

	@Override
	public String createLogging(final CoreSession session, final Long previsionnalCount,
			final Long closePrevisionalCount, final Map<String, String> timbre, final String currentGouvernement,
			final String nextGouvernement) throws ClientException {
		DocumentModel modelDesired = session
				.createDocumentModel(ReponsesLoggingConstant.REPONSES_LOGGING_DOCUMENT_TYPE);
		modelDesired.setPathInfo(ReponsesLoggingConstant.REPONSES_LOGGING_PATH,
				String.valueOf(Calendar.getInstance().getTimeInMillis()));

		modelDesired = session.createDocument(modelDesired);

		final ReponsesLogging reponsesLogging = modelDesired.getAdapter(ReponsesLogging.class);
		reponsesLogging.setStartDate(Calendar.getInstance());
		reponsesLogging.setMessage("Changement de gouvernement du "
				+ DateUtil.formatWithHour(Calendar.getInstance().getTime()));
		reponsesLogging.setPrevisionalCount(previsionnalCount);
		reponsesLogging.setClosePrevisionalCount(closePrevisionalCount);
		reponsesLogging.setCloseEndCount(0L);
		reponsesLogging.setEndCount(0L);

		for (Entry<String, String> entry : timbre.entrySet()) {
			reponsesLogging.addtimbre(entry.getKey().toString(), entry.getValue().toString());
		}

		reponsesLogging.setCurrentGouvernement(currentGouvernement);
		reponsesLogging.setNextGouvernement(nextGouvernement);
		session.saveDocument(reponsesLogging.getDocument());
		// sauvegarde de la premiere ligne du log pour l'affichage
		session.save();

		return modelDesired.getId();
	}

	@Override
	public List<ReponsesLogging> getAllReponsesLogging(final CoreSession session) throws ClientException {
		final StringBuilder query = new StringBuilder(
				" SELECT r.ecm:uuid as id FROM ReponsesLogging as r ORDER BY r.replog:startDate DESC ");

		final List<DocumentModel> documentModelList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
				"ReponsesLogging", query.toString(), null);

		final List<ReponsesLogging> result = new ArrayList<ReponsesLogging>();
		for (final DocumentModel documentModel : documentModelList) {
			result.add(documentModel.getAdapter(ReponsesLogging.class));
		}

		return result;
	}

	@Override
	public List<ReponsesLoggingLine> getAllReponsesLoggingLine(final CoreSession session, final String reponsesLoggingId)
			throws ClientException {
		final List<ReponsesLoggingLine> result = new ArrayList<ReponsesLoggingLine>();

		if (StringUtils.isNotEmpty(reponsesLoggingId)) {
			final DocumentModel doc = session.getDocument(new IdRef(reponsesLoggingId));
			final ReponsesLogging reponsesLogging = doc.getAdapter(ReponsesLogging.class);

			for (final String idLine : reponsesLogging.getReponsesLoggingLines()) {
				final DocumentModel docLine = session.getDocument(new IdRef(idLine));
				result.add(docLine.getAdapter(ReponsesLoggingLine.class));
			}
		}

		return result;
	}

	@Override
	public Map<String, String> getReponsesLoggingTimbre(final CoreSession session, final String reponsesLoggingId)
			throws ClientException {
		Map<String, String> result = new HashMap<String, String>();

		if (StringUtils.isNotEmpty(reponsesLoggingId)) {
			final DocumentModel doc = session.getDocument(new IdRef(reponsesLoggingId));
			final ReponsesLogging reponsesLogging = doc.getAdapter(ReponsesLogging.class);
			result = reponsesLogging.geTimbresAsMap();
		}

		return result;
	}

	@Override
	public List<ReponsesLoggingLineDetail> getAllReponsesLoggingLineDetail(final CoreSession session,
			final String reponsesLoggingLineId) throws ClientException {
		final List<ReponsesLoggingLineDetail> result = new ArrayList<ReponsesLoggingLineDetail>();

		if (StringUtils.isNotEmpty(reponsesLoggingLineId)) {
			final DocumentModel doc = session.getDocument(new IdRef(reponsesLoggingLineId));
			final ReponsesLoggingLine reponsesLoggingLine = doc.getAdapter(ReponsesLoggingLine.class);

			for (final String idLineDetail : reponsesLoggingLine.getReponsesLoggingLineDetails()) {
				final DocumentModel docLine = session.getDocument(new IdRef(idLineDetail));
				result.add(docLine.getAdapter(ReponsesLoggingLineDetail.class));
			}
		}
		return result;
	}

	@Override
	public Long getCloseCountForMinistere(CoreSession session, String currentMin) throws ClientException {
		final String[] params = new String[] { currentMin, VocabularyConstants.QUESTION_TYPE_QE };
		Map<String, Long> mapResult = buildResultMapFromSQLQueryResult(session, QUERY_COUNT_CLOSED_QUESTIONS_FOR_MIN,
				params);
		Long result = mapResult.get(currentMin);
		if (result != null) {
			return result;
		}
		// Si on n'a pas trouvé de question à fermer on retourne 0
		return 0L;
	}

	@Override
	public String getQueryClosedQuestionsForMinistere(String currentMin) {
		return String.format(QUERY_CLOSED_QUESTIONS_FOR_MIN, currentMin, "QE");
	}

}
