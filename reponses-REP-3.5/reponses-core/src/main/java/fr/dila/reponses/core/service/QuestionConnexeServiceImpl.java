package fr.dila.reponses.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SHA512Util;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;

public class QuestionConnexeServiceImpl implements QuestionConnexeService {

	private static final String	AUTRES_MINISTÈRES	= "Autres ministères";

	@Override
	public String getHash(Question question, HashTarget hashTarget, CoreSession session) throws ClientException {
		String hashValue = null;
		switch (hashTarget) {
			case TITLE:
				if (DossierConstants.ORIGINE_QUESTION_SENAT.equals(question.getOrigineQuestion())) {
					hashValue = SHA512Util.getSHA512Hash(question.getSenatQuestionTitre());
				}
				break;
			case TEXTE:
				hashValue = SHA512Util.getSHA512Hash(question.getTexteQuestion());
				break;
			case INDEXATION_AN:
				hashValue = SHA512Util.getSHA512Hash(getANIndexation(question, session));
				break;
			case INDEXATION_SE:
				if (DossierConstants.ORIGINE_QUESTION_SENAT.equals(question.getOrigineQuestion())) {
					hashValue = SHA512Util.getSHA512Hash(getSEIndexation(question, session));
				}
				break;
			default:
				hashValue = null;
				break;
		}

		return hashValue;
	}

	private String getSEIndexation(Question question, CoreSession session) throws ClientException {

		StringBuilder query = new StringBuilder(" SELECT doc.ixa:SE_theme as ixaT ");
		query.append(" FROM Question as doc ");
		query.append(" WHERE doc.ecm:uuid = ? ");
		query.append(" ORDER BY doc.ixa:SE_theme ");

		Object[] params = new Object[] { question.getDocument().getId() };

		IterableQueryResult res = null;
		try {
			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			StringBuilder sbResult = new StringBuilder();
			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder(" SELECT doc.ixacomp:SE_theme as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixacomp:SE_theme ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder(" SELECT doc.ixa:SE_rubrique as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixa:SE_rubrique ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder(" SELECT doc.ixacomp:SE_rubrique as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixacomp:SE_rubrique ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder(" SELECT doc.ixa:SE_renvoi as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixa:SE_renvoi ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder(" SELECT doc.ixacomp:SE_renvoi as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixacomp:SE_renvoi ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			return sbResult.toString();
		} finally {
			if (res != null) {
				res.close();
			}
		}
	}

	private String getANIndexation(Question question, CoreSession session) throws ClientException {
		StringBuilder query = new StringBuilder("SELECT doc.ixa:AN_analyse as ixaT ");
		query.append(" FROM Question as doc ");
		query.append(" WHERE doc.ecm:uuid = ? ");
		query.append(" ORDER BY doc.ixa:AN_analyse ");

		Object[] params = new Object[] { question.getDocument().getId() };

		IterableQueryResult res = null;
		try {
			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);
			StringBuilder sbResult = new StringBuilder();
			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder("SELECT doc.ixacomp:AN_analyse as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixacomp:AN_analyse ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder("SELECT doc.ixa:TA_rubrique as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixa:TA_rubrique ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder("SELECT doc.ixacomp:TA_rubrique as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixacomp:TA_rubrique ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder("SELECT doc.ixa:AN_rubrique as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixa:AN_rubrique ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			query = new StringBuilder("SELECT doc.ixacomp:AN_rubrique as ixaT ");
			query.append(" FROM Question as doc ");
			query.append(" WHERE doc.ecm:uuid = ? ");
			query.append(" ORDER BY doc.ixacomp:AN_rubrique ");

			params = new Object[] { question.getDocument().getId() };

			res = QueryUtils.doUFNXQLQuery(session, query.toString(), params);

			for (Map<String, Serializable> result : res) {
				sbResult.append((String) (result.get("ixaT") != null ? result.get("ixaT") : ""));
			}
			res.close();

			return sbResult.toString();

		} finally {
			if (res != null) {
				res.close();
			}
		}

	}

	private List<String> getIdQuestionsConnexes(String hashTitre, String hashTexte, String hashAN, String hashSE,
			CoreSession session) throws ClientException {

		StringBuilder nxQuery = new StringBuilder("SELECT q.ecm:uuid as id FROM  Question as q ");
		nxQuery.append(" WHERE  (testAcl(q.ecm:uuid)=1) AND (");

		boolean hasCondition = false;
		if (hashTitre != null) {
			addConditionHash(nxQuery, DossierConstants.DOSSIER_HASH_TITRE, hashTitre);
			hasCondition = true;
		}

		if (hashTexte != null) {
			if (hasCondition) {
				nxQuery.append(" OR ");
			} else {
				hasCondition = true;
			}
			addConditionHash(nxQuery, DossierConstants.DOSSIER_HASH_TEXTE, hashTexte);
		}

		if (hashAN != null) {
			if (hasCondition) {
				nxQuery.append(" OR ");
			} else {
				hasCondition = true;
			}
			addConditionHash(nxQuery, DossierConstants.DOSSIER_HASH_AN, hashAN);
		}

		if (hashSE != null) {
			if (hasCondition) {
				nxQuery.append(" OR ");
			} else {
				hasCondition = true;
			}
			addConditionHash(nxQuery, DossierConstants.DOSSIER_HASH_SENAT, hashSE);
		}
		nxQuery.append(")");

		if (!hasCondition) {
			return new ArrayList<String>();
		} else {
			return QueryUtils.doUFNXQLQueryForIdsList(session, nxQuery.toString(), null);
		}
	}

	@Override
	public Map<String, List<String>> getMinisteresMap(Question question, CoreSession session) throws ClientException {

		List<String> idQuestionsConnexe = getListIdQuestionsConnexite(question, session);
		List<String> idQuestionsLot = getListIdQuestionsLot(question, session);

		Set<String> idQuestions = new HashSet<String>();
		idQuestions.addAll(idQuestionsConnexe);
		idQuestions.addAll(idQuestionsLot);
		idQuestions.remove(question.getDocument().getId());

		if (idQuestions.isEmpty()) {
			return new HashMap<String, List<String>>();
		} else {
			return getMinistereFromQuestion(idQuestions, session);
		}
	}

	private Map<String, List<String>> getMinistereFromQuestion(Set<String> idQuestions, CoreSession session)
			throws ClientException {

		Map<String, List<String>> mapCount = new HashMap<String, List<String>>();

		StringBuilder sb = new StringBuilder(
				" SELECT d.dos:ministereAttributaireCourant as min , d.dos:idDocumentQuestion as quest FROM Dossier as d ");
		sb.append(" WHERE d.dos:idDocumentQuestion IN (");
		sb.append(StringUtil.join(idQuestions, ",", "'"));
		sb.append(")");

		IterableQueryResult res = null;
		try {
			res = QueryUtils.doUFNXQLQuery(session, sb.toString(), null);

			final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
			Iterator<Map<String, Serializable>> it = res.iterator();
			while (it.hasNext()) {
				Map<String, Serializable> row = it.next();
				String label = (String) row.get("min");
				if (StringUtils.isEmpty(label)) {
					label = AUTRES_MINISTÈRES;
				} else {
					OrganigrammeNode node = ministeresService.getEntiteNode(label);
					if (node != null) {
						label = node.getLabel();
					} else {
						label = AUTRES_MINISTÈRES;
					}
				}
				String questionId = (String) row.get("quest");
				if (mapCount.get(label) == null) {
					mapCount.put(label, new ArrayList<String>());
				}

				mapCount.get(label).add(questionId);
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		return mapCount;

	}

	private List<String> getListIdQuestionsConnexite(Question question, CoreSession session) throws ClientException {
		return getIdQuestionsConnexes(question.getHashConnexiteTitre(), question.getHashConnexiteTexte(),
				question.getHashConnexiteAN(), question.getHashConnexiteSE(), session);
	}

	private List<String> getListIdQuestionsLot(Question question, CoreSession session) throws ClientException {

		DocumentModel docDossier = session.getDocument(question.getDossierRef());
		Dossier dossier = docDossier.getAdapter(Dossier.class);

		StringBuilder sb = new StringBuilder(
				"SELECT d.dos:idDocumentQuestion as id FROM Allotissement as a, Dossier as d ");
		sb.append(" WHERE a.allot:idDossiers = d.ecm:uuid AND (testAcl(d.ecm:uuid)=1) ");
		sb.append(" AND a.allot:nom = '").append(dossier.getDossierLot()).append("' ");

		return QueryUtils.doUFNXQLQueryForIdsList(session, sb.toString(), null);
	}

	@Override
	public Reponse getReponse(Question question, CoreSession session) throws ClientException {
		UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);
		DocumentModel docDossier = uGet.getByRef(question.getDossierRef());
		Dossier d = docDossier.getAdapter(Dossier.class);
		return d.getReponse(session);
	}

	/**
	 * Méthode utilitare qui construit un test sur une des propriétés de hash de la question et l'ajoute à une string.
	 * Utilisé dans countDistinctIdQuestion
	 * 
	 * @param nxQuery
	 * @param hashProperty
	 * @param hashValue
	 * 
	 * @see countDistinctIdQuestion
	 */
	private void addConditionHash(StringBuilder nxQuery, String hashProperty, String hashValue) {
		nxQuery.append("q.").append(DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX).append(':').append(hashProperty)
				.append(" = '").append(hashValue).append("'");
	}

}
