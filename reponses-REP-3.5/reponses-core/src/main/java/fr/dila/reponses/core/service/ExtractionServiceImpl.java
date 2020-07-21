package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunVoid;
import org.nuxeo.runtime.api.Framework;

import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.extraction.Question;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ExtractionService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * Implémentation du service d'extraction des questions de l'application
 * 
 */
public class ExtractionServiceImpl implements ExtractionService {

	/**
	 * Serial version UID
	 */
	private static final long					serialVersionUID	= 5242530093209190761L;

	private static final STLogger				LOGGER				= STLogFactory.getLog(ExtractionServiceImpl.class);

	private static final String					EXTRACTION_PROVIDER	= "sword-provider";

	private static volatile PersistenceProvider	persistenceProvider;

	private static final String					LEGISLATURE_PARAM	= "legislatureQuestion";
	private static final String					ORIGINE_PARAM		= "origineQuestion";
	private static final String					TYPE_PARAM			= "typeQuestion";

	private static final String					WHERE_PARAMS		= "q.typeQuestion=:" + TYPE_PARAM
																			+ " AND q.origineQuestion=:"
																			+ ORIGINE_PARAM
																			+ " AND q.legislatureQuestion=:"
																			+ LEGISLATURE_PARAM;

	private static final String					WHERE_ERR_REP		= " q.id = d.idDocumentQuestion AND r.id = d.idDocumentReponse AND r.erratum IS NOT NULL ";
	private static final String					WHERE_ERR_QUEST		= " h.parentId = q.id AND h.name = 'qu:errata' AND h.primaryType = 'erratum#anonymousType' ";

	private static final String					WHERE_EN_COURS		= " q.etatQuestion = 'en cours' AND "
																			+ WHERE_PARAMS;
	private static final String					WHERE_EN_COURS_QE	= "q.etatQuestion IN ('en cours','repondu') AND q.dateTransmissionAssemblees IS NULL AND "
																			+ WHERE_PARAMS;

	private static final String					WHERE_CLOSES_AND_QE	= " ((q.etatQuestion = 'repondu' AND q.dateTransmissionAssemblees IS NOT NULL) "
																			+ " OR (q.etatQuestion IN ('caduque', 'retiree', 'cloture_autre'))) AND "
																			+ WHERE_PARAMS;

	private static final String					WHERE_CLOSES_NOT_QE	= " q.etatQuestion IN ('repondu', 'caduque', 'retiree', 'cloture_autre') AND "
																			+ WHERE_PARAMS;

	private static final String					ORDER_BY			= " ORDER BY q.numeroQuestion asc";

	private List<Question>						result;

	public List<Question> extractQuestionsCloses(final CoreSession session, final String typeQuestion,
			final String origineQuestion) {
		final List<Question> result = new ArrayList<Question>();
		try {
			result.addAll(processExtraction(session, typeQuestion, origineQuestion, false));
		} catch (ClientException exc) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_EXTRACT_QUESTION_TEC, exc);
		}
		return result;
	}

	public List<Question> extractQuestionsOuvertes(final CoreSession session, final String typeQuestion,
			final String origineQuestion) {
		final List<Question> result = new ArrayList<Question>();
		try {
			result.addAll(processExtraction(session, typeQuestion, origineQuestion, true));
		} catch (ClientException exc) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_EXTRACT_QUESTION_TEC, exc);
		}
		return result;
	}

	private List<Question> processExtraction(final CoreSession session, final String typeQuestion,
			final String origineQuestion, final boolean openQuestions) throws ClientException {
		final List<Question> result = new ArrayList<Question>();
		// initialisation des requêtes
		StringBuilder queryAllQuestions = new StringBuilder("SELECT q FROM Question q WHERE ");
		StringBuilder queryErrQuestions = new StringBuilder("SELECT new Question(q, h) FROM Question q, Hierarchy h "
				+ " WHERE " + WHERE_ERR_QUEST + " AND ");
		StringBuilder queryRepErrQuestions = new StringBuilder(
				"SELECT new Question(q, r) FROM Question q, DossierReponse d, Reponse r" + " WHERE " + WHERE_ERR_REP
						+ " AND ");

		// Si on récupère les questions ouvertes
		if (openQuestions) {
			// de types QE
			if (VocabularyConstants.QUESTION_TYPE_QE.equals(typeQuestion)) {
				queryAllQuestions.append(WHERE_EN_COURS_QE);
				queryErrQuestions.append(WHERE_EN_COURS_QE);
				queryRepErrQuestions.append(WHERE_EN_COURS_QE);
			} else {
				// ou autre que QE
				queryAllQuestions.append(WHERE_EN_COURS);
				queryErrQuestions.append(WHERE_EN_COURS);
				queryRepErrQuestions.append(WHERE_EN_COURS);
			}
		} else {
			// Ou les question closes
			// de type QE
			if (VocabularyConstants.QUESTION_TYPE_QE.equals(typeQuestion)) {
				queryAllQuestions.append(WHERE_CLOSES_AND_QE);
				queryErrQuestions.append(WHERE_CLOSES_AND_QE);
				queryRepErrQuestions.append(WHERE_CLOSES_AND_QE);
			} else {
				// ou autre que QE
				queryAllQuestions.append(WHERE_CLOSES_NOT_QE);
				queryErrQuestions.append(WHERE_CLOSES_NOT_QE);
				queryRepErrQuestions.append(WHERE_CLOSES_NOT_QE);
			}
		}

		// Ajout de l'order by
		queryAllQuestions.append(ORDER_BY);
		queryErrQuestions.append(ORDER_BY);
		queryRepErrQuestions.append(ORDER_BY);

		Map<String, Question> mapQuestions = new HashMap<String, Question>();
		// récupération des questions
		result.addAll(extractQuestions(session, queryAllQuestions.toString(), typeQuestion, origineQuestion));
		for (Question question : result) {
			mapQuestions.put(question.getId(), question);
		}
		// récupération des questions avec des erratum en question
		List<Question> resultTmp = extractQuestions(session, queryErrQuestions.toString(), typeQuestion,
				origineQuestion);
		for (Question question : resultTmp) {
			// on a un erratum, on met à jour l'info
			Question questionInMap = mapQuestions.get(question.getId());
			// Si la question n'avait pas été récupérée, on l'ajoute à la liste de résultat
			if (questionInMap == null) {
				questionInMap = question;
				result.add(questionInMap);
			}
			questionInMap.setHasErratum(true);
		}
		// récupération des questions avec des erratum en réponse
		resultTmp = extractQuestions(session, queryRepErrQuestions.toString(), typeQuestion, origineQuestion);
		for (Question question : resultTmp) {
			// on a un erratum en réponse, on met à jour l'info
			Question questionInMap = mapQuestions.get(question.getId());
			// Si la question n'avait pas été récupérée on l'ajoute à la liste de résultat
			if (questionInMap == null) {
				questionInMap = question;
				result.add(questionInMap);
			}
			questionInMap.setHasRepErratum(true);
		}
		return result;
	}

	/**
	 * Must be called when the service is no longer needed
	 */
	public static void dispose() {
		deactivatePersistenceProvider();
	}

	public static PersistenceProvider getOrCreatePersistenceProvider(String providerName) {
		if (persistenceProvider == null) {
			synchronized (ExtractionServiceImpl.class) {
				if (persistenceProvider == null) {
					activatePersistenceProvider(providerName);
				}
			}
		}
		return persistenceProvider;
	}

	private static void activatePersistenceProvider(String providerName) {
		Thread thread = Thread.currentThread();
		ClassLoader last = thread.getContextClassLoader();
		try {
			thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
			PersistenceProviderFactory persistenceProviderFactory = Framework
					.getLocalService(PersistenceProviderFactory.class);
			persistenceProvider = persistenceProviderFactory.newProvider(providerName);
			persistenceProvider.openPersistenceUnit();
		} finally {
			thread.setContextClassLoader(last);
		}
	}

	private static void deactivatePersistenceProvider() {
		if (persistenceProvider != null) {
			synchronized (ExtractionServiceImpl.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	/**
	 * Récupère la législature courante TODO => cette méthode existe dans plusieurs services. Refactoring nécessaire
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	private Long getLegislatureCourante(final CoreSession session) throws ClientException {

		final String legislatureValue = STServiceLocator.getSTParametreService().getParametreValue(session,
				ReponsesParametreConstant.LEGISLATURE_COURANTE);

		Long legislature = null;
		try {
			legislature = Long.parseLong(legislatureValue);
		} catch (final Exception e) {
			throw new ClientException("La législature courante n'est pas paramétrée correctement.", e);
		}
		return legislature;
	}

	/**
	 * Extrait les questions à partir de la requête, du type et de l'origine.
	 * 
	 * @param session
	 * @param query
	 * @param typeQuestion
	 * @param origineQuestion
	 * @return
	 * @throws ClientException
	 */
	private List<Question> extractQuestions(final CoreSession session, final String query, final String typeQuestion,
			final String origineQuestion) throws ClientException {
		deactivatePersistenceProvider();
		getOrCreatePersistenceProvider(EXTRACTION_PROVIDER).run(true, new RunVoid() {
			@SuppressWarnings("unchecked")
			public void runWith(EntityManager entityManager) throws ClientException {
				final int legislatureCourante = getLegislatureCourante(session).intValue();
				Query nativeQuery = entityManager.createQuery(query);
				nativeQuery.setParameter(TYPE_PARAM, typeQuestion);
				nativeQuery.setParameter(ORIGINE_PARAM, origineQuestion);
				nativeQuery.setParameter(LEGISLATURE_PARAM, legislatureCourante);
				try {
					result = nativeQuery.getResultList();
				} catch (Exception exc) {
					String[] params = new String[] { typeQuestion, origineQuestion, String.valueOf(legislatureCourante) };
					StringBuilder message = new StringBuilder(query);
					message.append(StringUtil.join(params, ",", "'"));
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_EXTRACT_QUESTION_TEC, message);
					result = new ArrayList<Question>();
				}
			}
		});
		deactivatePersistenceProvider();
		return result;
	}

}
