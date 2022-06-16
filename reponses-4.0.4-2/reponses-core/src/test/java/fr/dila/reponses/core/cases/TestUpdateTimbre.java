package fr.dila.reponses.core.cases;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author asatre
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestUpdateTimbre {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    /** Injection des données pour le test. */
    @Before
    public void setUp() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            reponseFeature.createDossier(session);
        }
    }

    @Test
    public void testFieldEcriture() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            final StringBuilder sb = new StringBuilder(" SELECT d.ecm:uuid AS id, d.dos:");
            sb.append(STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY);
            sb.append(" FROM Dossier as d, Question as q ");
            sb.append(" WHERE d.dos:");
            sb.append(DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID);
            sb.append(" = q.ecm:uuid ");
            sb.append(" AND q.qu:");
            sb.append(DossierConstants.DOSSIER_TYPE_QUESTION);
            sb.append(" = ? ");

            sb.append(" AND d.dos:");
            sb.append(DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT);
            sb.append(" = ? ");

            sb.append(" AND ");
            sb.append("	exist(");
            sb.append(" select s.ecm:uuid from RouteStep AS s WHERE s.rtsk:documentRouteId = ");
            sb.append("	d.dos:");
            sb.append(STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY);
            sb.append("	AND s.");
            sb.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH);
            sb.append(" IN ('running', 'ready')");
            sb.append(") = 0 ");

            final Object[] params = new Object[] {
                VocabularyConstants.QUESTION_TYPE_QE,
                ReponseFeature.ID_MINISTERE_INTERROGE
            };

            Long count = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(sb.toString()), params);

            Assert.assertEquals(0, count.longValue());
        }
    }
}
