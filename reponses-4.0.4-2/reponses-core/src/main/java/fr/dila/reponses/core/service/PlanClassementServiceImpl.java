package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.PlanClassementService;
import fr.dila.st.core.util.ResourceHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.text.Collator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

public class PlanClassementServiceImpl implements PlanClassementService {
    private static final String EGAL_FERMER_PARENTHESE = " = ? )";
    private static final String EGAL_OR = " = ? OR ";

    private static final String NIVEAU1_THEME = "Thème";

    private static final String NIVEAU1_RUBRIQUE = "Rubrique";

    private static final String COL_LABEL = "label";
    private static final String COL_COUNT = "count";

    private static final String QUERY_AN_RUBRIQUE =
        "SELECT q.ixa:" +
        AN_RUBRIQUE.getValue() +
        " AS " +
        COL_LABEL +
        ", count() AS " +
        COL_COUNT +
        " FROM Question AS q" +
        " WHERE q.ixa:" +
        AN_RUBRIQUE.getValue() +
        " IS NOT NULL" +
        " GROUP BY q.ixa:" +
        AN_RUBRIQUE.getValue();

    private static final String QUERY_AN_RUBRIQUE_COMP =
        "SELECT q.ixacomp:" +
        AN_RUBRIQUE.getValue() +
        " AS " +
        COL_LABEL +
        ", count() AS " +
        COL_COUNT +
        " FROM Question AS q" +
        " WHERE q.ixacomp:" +
        AN_RUBRIQUE.getValue() +
        " IS NOT NULL" +
        " GROUP BY q.ixacomp:" +
        AN_RUBRIQUE.getValue();

    private static final String QUERY_AN_TA =
        "SELECT q.ixa:" +
        TA_RUBRIQUE.getValue() +
        " AS " +
        COL_LABEL +
        ", count() AS " +
        COL_COUNT +
        " FROM Question AS q" +
        " WHERE q.ixa:" +
        AN_RUBRIQUE.getValue() +
        " = ? GROUP BY q.ixa:" +
        TA_RUBRIQUE.getValue();

    private static final String QUERY_AN_TA_COMP =
        "SELECT q.ixacomp:" +
        TA_RUBRIQUE.getValue() +
        " AS " +
        COL_LABEL +
        ", count() AS " +
        COL_COUNT +
        " FROM Question AS q" +
        " WHERE q.ixacomp:" +
        AN_RUBRIQUE.getValue() +
        " = ? AND q.ixacomp:" +
        TA_RUBRIQUE.getValue() +
        " IS NOT NULL" +
        " GROUP BY q.ixacomp:" +
        TA_RUBRIQUE.getValue();

    @Override
    public Map<String, Integer> getPlanClassementNiveau1(final CoreSession session, final String treeMode) {
        if (treeMode.equals(DossierConstants.ORIGINE_QUESTION_SENAT)) {
            return getPlanClassementSenatNiveau1(session);
        } else {
            // indexation initiale
            Map<String, Integer> map = buildResultMapFromUFNXQLQueryResult(session, QUERY_AN_RUBRIQUE, null, null);

            // indexation complémentaire
            map = buildResultMapFromUFNXQLQueryResult(session, QUERY_AN_RUBRIQUE_COMP, null, map);

            return map;
        }
    }

    @Override
    public Map<String, Integer> getPlanClassementNiveau2(
        final CoreSession session,
        final String treeMode,
        final String indexation
    ) {
        if (treeMode.equals(DossierConstants.ORIGINE_QUESTION_SENAT)) {
            return getSenatRubriqueThemeAndQuestionCount(session, indexation);
        } else {
            // indexation initiale
            Map<String, Integer> map = buildResultMapFromUFNXQLQueryResult(
                session,
                QUERY_AN_TA,
                new Object[] { indexation },
                null
            );

            // indexation complémentaire
            map = buildResultMapFromUFNXQLQueryResult(session, QUERY_AN_TA_COMP, new Object[] { indexation }, map);

            map.put(
                ResourceHelper.getString("plan.classement.tous.label"),
                getPlanClassementNiveau1(session, treeMode).get(indexation)
            );

            return map;
        }
    }

    private Map<String, Integer> getPlanClassementSenatNiveau1(final CoreSession session) {
        final Map<String, Integer> map = new TreeMap<>();
        map.put(NIVEAU1_RUBRIQUE, getSenatNbRubriqueTheme(session, NIVEAU1_RUBRIQUE));
        map.put(NIVEAU1_THEME, getSenatNbRubriqueTheme(session, NIVEAU1_THEME));
        return map;
    }

    /**
     * Retourne les nombres de rubriques ou ou de themes.
     *
     * @param session
     * @param type
     *            valeur NIVEAU1_RUBRIQUE pour les rubriques et NIVEAU1_THEME pour les themes
     * @return
     */
    private int getSenatNbRubriqueTheme(final CoreSession session, final String type) {
        String column = SE_RUBRIQUE.getValue();
        if (!NIVEAU1_RUBRIQUE.equals(type)) {
            column = SE_THEME.getValue();
        }
        StringBuilder query = new StringBuilder("SELECT DISTINCT q.ixa:")
            .append(column)
            .append(" AS ")
            .append(COL_LABEL)
            .append(" FROM Question AS q")
            .append(" WHERE q.ixa:")
            .append(column)
            .append(" IS NOT NULL");

        final Set<String> labelSet = new HashSet<>();
        IterableQueryResult res = null;
        try {
            res = QueryUtils.doUFNXQLQuery(session, query.toString(), null);

            final Iterator<Map<String, Serializable>> iterator = res.iterator();
            while (iterator.hasNext()) {
                final Map<String, Serializable> row = iterator.next();
                final String label = (String) row.get(COL_LABEL);
                labelSet.add(label);
            }
        } finally {
            if (res != null) {
                res.close();
            }
        }

        query =
            new StringBuilder("SELECT DISTINCT q.ixacomp:")
                .append(column)
                .append(" AS ")
                .append(COL_LABEL)
                .append(" FROM Question AS q")
                .append(" WHERE q.ixacomp:")
                .append(column)
                .append(" IS NOT NULL");

        res = null;
        try {
            res = QueryUtils.doUFNXQLQuery(session, query.toString(), null);

            final Iterator<Map<String, Serializable>> iterator = res.iterator();
            while (iterator.hasNext()) {
                final Map<String, Serializable> row = iterator.next();
                final String label = (String) row.get(COL_LABEL);
                labelSet.add(label);
            }
        } finally {
            if (res != null) {
                res.close();
            }
        }
        return labelSet.size();
    }

    /**
     * Retourne les rubriques ou themes accompagnés du nombre de questions
     *
     * @param session
     * @param type
     *            valeur NIVEAU1_RUBRIQUE pour les rubriques et NIVEAU1_THEME pour les themes
     * @return
     */
    private Map<String, Integer> getSenatRubriqueThemeAndQuestionCount(final CoreSession session, final String type) {
        String column = SE_RUBRIQUE.getValue();
        if (!NIVEAU1_RUBRIQUE.equals(type)) {
            column = SE_THEME.getValue();
        }
        StringBuilder query = new StringBuilder("SELECT q.ixa:")
            .append(column)
            .append(" AS ")
            .append(COL_LABEL)
            .append(", count() AS ")
            .append(COL_COUNT)
            .append(" FROM Question AS q")
            .append(" WHERE q.ixa:")
            .append(column)
            .append(" IS NOT NULL group by q.ixa:")
            .append(column);

        Map<String, Integer> mapLabelCount = buildResultMapFromUFNXQLQueryResult(session, query.toString(), null, null);

        query =
            new StringBuilder("SELECT q.ixacomp:")
                .append(column)
                .append(" AS ")
                .append(COL_LABEL)
                .append(", count() AS ")
                .append(COL_COUNT)
                .append(" FROM Question AS q")
                .append(" WHERE q.ixacomp:")
                .append(column)
                .append(" IS NOT NULL group by q.ixacomp:")
                .append(column);

        mapLabelCount = buildResultMapFromUFNXQLQueryResult(session, query.toString(), null, mapLabelCount);

        return mapLabelCount;
    }

    /**
     * Rempli une map avec les retour de la requete, les champs attendu sont label et count (qui contient le nombre
     * d'occurence de label) Cree un map ou en complete une. dans ce dernier cas le nombre d'occurence est ajouté au
     * nombre courant se trouvant dans la map "initMap"
     *
     * @param query
     *            UFNQL query
     * @param initMap
     * @return
     */
    private Map<String, Integer> buildResultMapFromUFNXQLQueryResult(
        final CoreSession session,
        final String query,
        final Object[] params,
        final Map<String, Integer> initMap
    ) {
        try (IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params)) {
            final Map<String, Integer> map = initMap == null ? new TreeMap<>(Collator.getInstance()) : initMap;
            final Iterator<Map<String, Serializable>> iterator = res.iterator();
            while (iterator.hasNext()) {
                final Map<String, Serializable> row = iterator.next();
                if (row.get(COL_LABEL) != null) {
                    final String label = (String) row.get(COL_LABEL);
                    final Long count = (Long) row.get(COL_COUNT);

                    final Integer existingCount = map.get(label);
                    map.put(label, count.intValue() + (existingCount == null ? 0 : existingCount));
                }
            }
            return map;
        }
    }

    @Override
    public String getDossierFromIndexationQuery(final String origine, String indexNiv1, String indexNiv2) {
        final StringBuilder sb = new StringBuilder("SELECT ecm:uuid as id FROM ");
        sb.append(DossierConstants.QUESTION_DOCUMENT_TYPE);
        sb.append(" WHERE (");

        if (DossierConstants.ORIGINE_QUESTION_AN.equals(origine)) {
            sb.append(DossierConstants.QUESTION_INDEX_AN_RUBRIQUE_XPATH);
            sb.append(EGAL_OR);
            sb.append(DossierConstants.QUESTION_IXCOMP_AN_RUBRIQUE_XPATH);
            sb.append(EGAL_FERMER_PARENTHESE);
            if (!ResourceHelper.getString("plan.classement.tous.label").equals(indexNiv2)) {
                sb.append(" AND ( ");
                sb.append(DossierConstants.QUESTION_INDEX_AN_TA_XPATH);
                sb.append(EGAL_OR);
                sb.append(DossierConstants.QUESTION_IXCOMP_AN_TA_XPATH);
                sb.append(EGAL_FERMER_PARENTHESE);
            }
        } else if (DossierConstants.ORIGINE_QUESTION_SENAT.equals(origine)) {
            String field = ":" + DossierConstants.DOSSIER_SENAT_RUBRIQUE_QUESTION;
            if (!indexNiv1.equals(NIVEAU1_RUBRIQUE)) {
                field = ":" + DossierConstants.DOSSIER_SENAT_THEMES_QUESTION;
            }

            sb.append(DossierConstants.INDEXATION_DOCUMENT_SCHEMA_PREFIX);
            sb.append(field);
            sb.append(EGAL_OR);
            sb.append(DossierConstants.INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA_PREFIX);
            sb.append(field);
            sb.append(EGAL_FERMER_PARENTHESE);
        }

        return sb.toString();
    }
}
