package fr.dila.reponses.core.service;

import com.google.common.collect.ImmutableList;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.st.core.util.SHA512Util;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;

public class QuestionConnexeServiceImpl implements QuestionConnexeService {

    @Override
    public String getHash(Question question, HashTarget hashTarget, CoreSession session) {
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
                break;
        }

        return hashValue;
    }

    private String getSEIndexation(Question question, CoreSession session) {
        List<String> queries = ImmutableList.of(
            "SELECT doc.ixa:SE_theme as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixa:SE_theme",
            "SELECT doc.ixacomp:SE_theme as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixacomp:SE_theme",
            "SELECT doc.ixa:SE_rubrique as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixa:SE_rubrique",
            "SELECT doc.ixacomp:SE_rubrique as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixacomp:SE_rubrique",
            "SELECT doc.ixa:SE_renvoi as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixa:SE_renvoi",
            "SELECT doc.ixacomp:SE_renvoi as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixacomp:SE_renvoi"
        );

        return queries.stream().map(query -> getQueryResults(session, question, query)).collect(Collectors.joining());
    }

    private static String getQueryResults(CoreSession session, Question question, String query) {
        Object[] params = { question.getDocument().getId() };

        try (IterableQueryResult results = QueryUtils.doUFNXQLQuery(session, query, params)) {
            return StreamSupport
                .stream(results.spliterator(), false)
                .filter(res -> res.get("ixaT") != null)
                .map(res -> res.get("ixaT").toString())
                .collect(Collectors.joining(", "));
        }
    }

    private String getANIndexation(Question question, CoreSession session) {
        List<String> queries = ImmutableList.of(
            "SELECT doc.ixa:AN_analyse as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixa:AN_analyse",
            "SELECT doc.ixacomp:AN_analyse as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixacomp:AN_analyse",
            "SELECT doc.ixa:TA_rubrique as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixa:TA_rubrique",
            "SELECT doc.ixacomp:TA_rubrique as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixacomp:TA_rubrique",
            "SELECT doc.ixa:AN_rubrique as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixa:AN_rubrique",
            "SELECT doc.ixacomp:AN_rubrique as ixaT FROM Question as doc WHERE doc.ecm:uuid = ? ORDER BY doc.ixacomp:AN_rubrique"
        );

        return queries.stream().map(query -> getQueryResults(session, question, query)).collect(Collectors.joining());
    }

    private String buildConditionParam(ImmutablePair<String, String> pair) {
        return (
            "q." +
            DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
            ':' +
            pair.getLeft() +
            " = '" +
            pair.getRight() +
            "'"
        );
    }

    private List<String> getIdQuestionsConnexes(Question quest, String minAttribId, CoreSession session) {
        StringBuilder nxQuery = new StringBuilder("SELECT q.ecm:uuid as id FROM Question as q");
        nxQuery.append(" WHERE (testAcl(q.ecm:uuid)=1) AND ");

        String hashCondition = Stream
            .of(
                ImmutablePair.of(DossierConstants.DOSSIER_HASH_TITRE, quest.getHashConnexiteTitre()),
                ImmutablePair.of(DossierConstants.DOSSIER_HASH_TEXTE, quest.getHashConnexiteTexte()),
                ImmutablePair.of(DossierConstants.DOSSIER_HASH_AN, quest.getHashConnexiteAN()),
                ImmutablePair.of(DossierConstants.DOSSIER_HASH_SENAT, quest.getHashConnexiteSE())
            )
            .filter(pair -> StringUtils.isNotBlank(pair.getRight()))
            .map(this::buildConditionParam)
            .collect(Collectors.joining(" OR ", "(", ")"));
        if (StringUtils.isNotBlank(hashCondition)) {
            nxQuery.append(hashCondition);
        }

        if (StringUtils.isNotBlank(hashCondition) && StringUtils.isNotBlank(minAttribId)) {
            nxQuery.append(" AND ");

            addConditionParam(nxQuery, DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_QUESTION, minAttribId);
        }

        if (StringUtils.isBlank(hashCondition)) {
            return new ArrayList<>();
        } else {
            return QueryUtils.doUFNXQLQueryForIdsList(session, nxQuery.toString(), null);
        }
    }

    @Override
    public Map<String, List<String>> getMinisteresMap(Question question, CoreSession session) {
        List<String> idQuestionsConnexe = getListIdQuestionsConnexite(question, session);
        List<String> idQuestionsLot = getListIdQuestionsLot(question, session);

        Set<String> idQuestions = new HashSet<>();
        idQuestions.addAll(idQuestionsConnexe);
        idQuestions.addAll(idQuestionsLot);
        idQuestions.remove(question.getDocument().getId());

        if (idQuestions.isEmpty()) {
            return new HashMap<>();
        } else {
            return getMinistereFromQuestion(idQuestions, session);
        }
    }

    @Override
    public List<String> getMinisteresDossiersConnexe(Question question, String ministereId, CoreSession session) {
        List<String> idQuestionsConnexe = getListIdQuestionsConnexiteForMin(question, ministereId, session);
        List<String> idQuestionsLot = getListIdQuestionsLotForMin(question, session, ministereId);

        Set<String> idQuestions = new HashSet<>();
        idQuestions.addAll(idQuestionsConnexe);
        idQuestions.addAll(idQuestionsLot);
        idQuestions.remove(question.getDocument().getId());

        return new ArrayList<>(idQuestions);
    }

    private Map<String, List<String>> getMinistereFromQuestion(Set<String> idQuestions, CoreSession session) {
        String sb =
            " SELECT d.dos:ministereAttributaireCourant as min , d.dos:idDocumentQuestion as quest FROM Dossier as d " +
            " WHERE d.dos:idDocumentQuestion IN (" +
            StringUtil.join(idQuestions, ",", "'") +
            ")";
        try (IterableQueryResult results = QueryUtils.doUFNXQLQuery(session, sb, null)) {
            return StreamSupport
                .stream(results.spliterator(), false)
                .map(this::createMinistereIdWithQuestionId)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, ListUtils::union));
        }
    }

    private ImmutablePair<String, List<String>> createMinistereIdWithQuestionId(Map<String, Serializable> row) {
        List<String> questionIds = new ArrayList<>();
        questionIds.add((String) row.get("quest"));
        return ImmutablePair.of((String) row.get("min"), questionIds);
    }

    private List<String> getListIdQuestionsConnexite(Question question, CoreSession session) {
        return getIdQuestionsConnexes(question, null, session);
    }

    private List<String> getListIdQuestionsConnexiteForMin(Question question, String minAttribId, CoreSession session) {
        return getIdQuestionsConnexes(question, minAttribId, session);
    }

    private List<String> getListIdQuestionsLot(Question question, CoreSession session) {
        StringBuilder sb = getQuestionsLotsQuery(question, session);

        return QueryUtils.doUFNXQLQueryForIdsList(session, sb.toString(), null);
    }

    private StringBuilder getQuestionsLotsQuery(Question question, CoreSession session) {
        DocumentModel docDossier = session.getDocument(question.getDossierRef());
        Dossier dossier = docDossier.getAdapter(Dossier.class);

        StringBuilder sb = new StringBuilder(
            "SELECT d.dos:idDocumentQuestion as id FROM Allotissement as a, Dossier as d "
        );
        sb.append(" WHERE a.allot:idDossiers = d.ecm:uuid AND (testAcl(d.ecm:uuid)=1) ");
        sb.append(" AND a.allot:nom = '").append(dossier.getDossierLot()).append("' ");
        return sb;
    }

    private List<String> getListIdQuestionsLotForMin(Question question, CoreSession session, String ministereId) {
        StringBuilder sb = getQuestionsLotsQuery(question, session);
        sb.append(" AND d.dos:ministereAttributaireCourant = '").append(ministereId).append("' ");

        return QueryUtils.doUFNXQLQueryForIdsList(session, sb.toString(), null);
    }

    @Override
    public Reponse getReponse(Question question, CoreSession session) {
        UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(
            session,
            DossierConstants.DOSSIER_SCHEMA
        );
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
    private void addConditionParam(StringBuilder nxQuery, String hashProperty, String hashValue) {
        nxQuery
            .append("q.")
            .append(DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX)
            .append(':')
            .append(hashProperty)
            .append(" = '")
            .append(hashValue)
            .append("'");
    }
}
