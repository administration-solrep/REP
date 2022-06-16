package fr.dila.reponses.core.requeteur;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.core.requeteur.KeywordUfnxqlDateResolver;
import fr.dila.st.core.requeteur.RequeteurFunctionSolverHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestKeywordMinistereAttributaireResolver {
    @Inject
    private CoreFeature coreFeature;

    @Test
    public void testMinistereResolver() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.newMockSSPrincipal())) {
            final KeywordMinistereAttributaireResolver keywordMinistereResolver = new KeywordMinistereAttributaireResolver();
            keywordMinistereResolver.setExpr(
                "(d.dos." + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + ")"
            );
            Assert.assertEquals(
                "d.dos." + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " IN ('502','503')",
                keywordMinistereResolver.solve(session)
            );
        }
    }

    @Test
    public void testMatch_pasApas() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.newMockSSPrincipal())) {
            final KeywordMinistereAttributaireResolver solver_test = new KeywordMinistereAttributaireResolver();
            String expr =
                "((q.qu:numeroQuestion = '4' AND ufnxql_ministere:(d.dos:" +
                DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
                ")))";
            final Pattern pattern = solver_test.getPattern();
            final Matcher matcher = pattern.matcher(expr);
            matcher.find();
            String groupStr = "";
            groupStr = matcher.group(1);
            Assert.assertEquals("d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT, groupStr);
            final KeywordMinistereAttributaireResolver solver = new KeywordMinistereAttributaireResolver();
            solver.setExpr(groupStr);
            Assert.assertEquals(
                "d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " IN ('502','503')",
                solver.solve(session)
            );
            expr = expr.replace("ufnxql_ministere:(" + groupStr + ")", solver.solve(session));
            Assert.assertEquals(
                "((q.qu:numeroQuestion = '4' AND d.dos:" +
                DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
                " IN ('502','503')))",
                expr
            );
        }
    }

    @Test
    public void testMatch_Normal() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.newMockSSPrincipal())) {
            final KeywordUfnxqlDateResolver date_solver = new KeywordUfnxqlDateResolver();
            final KeywordMinistereAttributaireResolver min_solver = new KeywordMinistereAttributaireResolver();
            final Map<String, Object> env = new HashMap<String, Object>();
            env.put("NOW", new DateTime(2011, 7, 5, 10, 36, 0, 0));
            String query =
                "ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q,Dossier AS d WHERE ((q.qu:numeroQuestion = '4' AND ufnxql_ministere:(d.dos:" +
                DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
                ") AND  d.dos:idDocumentQuestion = q.ecm:uuid)";

            query = RequeteurFunctionSolverHelper.apply(min_solver, session, query, env);
            query = RequeteurFunctionSolverHelper.apply(date_solver, session, query, env);
            Assert.assertEquals(
                "ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q,Dossier AS d WHERE ((q.qu:numeroQuestion = '4' AND d.dos:" +
                DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
                " IN ('502','503') AND  d.dos:idDocumentQuestion = q.ecm:uuid)",
                query
            );
        }
    }
}
