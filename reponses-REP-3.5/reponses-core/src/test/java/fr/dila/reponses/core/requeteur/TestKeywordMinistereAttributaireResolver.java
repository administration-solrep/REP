package fr.dila.reponses.core.requeteur;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.local.LocalSession;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.core.requeteur.KeywordUfnxqlDateResolver;
import fr.dila.st.core.requeteur.RequeteurFunctionSolverHelper;

public class TestKeywordMinistereAttributaireResolver extends ReponsesRepositoryTestCase {

    public void testMinistereResolver() {
        final KeywordMinistereAttributaireResolver keywordMinistereResolver = new KeywordMinistereAttributaireResolver();
        keywordMinistereResolver.setExpr("(d.dos." + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + ")");
        assertEquals("d.dos." + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " IN ('503','502')", keywordMinistereResolver.solve(new MockSession()));
    }

    public void testMatch_pasApas() {
        final KeywordMinistereAttributaireResolver solver_test = new KeywordMinistereAttributaireResolver();
        String expr = "((q.qu:numeroQuestion = '4' AND ufnxql_ministere:(d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + ")))";
        final Pattern pattern = solver_test.getPattern();
        final Matcher matcher = pattern.matcher(expr);
        matcher.find();
        String groupStr = "";
        groupStr = matcher.group(1);
        assertEquals("d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT, groupStr);
        final KeywordMinistereAttributaireResolver solver = new KeywordMinistereAttributaireResolver();
        solver.setExpr(groupStr);
        assertEquals("d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " IN ('503','502')", solver.solve(new MockSession()));
        expr = expr.replace("ufnxql_ministere:(" + groupStr + ")", solver.solve(new MockSession()));
        assertEquals("((q.qu:numeroQuestion = '4' AND d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " IN ('503','502')))",
                expr);
    }

    public void testMatch_Normal() {
        final KeywordUfnxqlDateResolver date_solver = new KeywordUfnxqlDateResolver();
        final KeywordMinistereAttributaireResolver min_solver = new KeywordMinistereAttributaireResolver();
        final Map<String, Object> env = new HashMap<String, Object>();
        env.put("NOW", new DateTime(2011, 7, 5, 10, 36, 0, 0));
        String query = "ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q,Dossier AS d WHERE ((q.qu:numeroQuestion = '4' AND ufnxql_ministere:(d.dos:"
                + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + ") AND  d.dos:idDocumentQuestion = q.ecm:uuid)";
        
        final LocalSession mock_session = new MockSession();
        query = RequeteurFunctionSolverHelper.apply(min_solver, mock_session, query, env);
        query = RequeteurFunctionSolverHelper.apply(date_solver, mock_session, query, env);
        assertEquals("ufnxql:SELECT q.ecm:uuid AS id FROM Question AS q,Dossier AS d WHERE ((q.qu:numeroQuestion = '4' AND d.dos:"
                + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " IN ('503','502') AND  d.dos:idDocumentQuestion = q.ecm:uuid)", query);
    }

}
