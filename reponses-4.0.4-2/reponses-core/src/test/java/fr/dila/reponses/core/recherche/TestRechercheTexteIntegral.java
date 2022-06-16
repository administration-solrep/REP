package fr.dila.reponses.core.recherche;

import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STQueryConstant;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 *
 * @author jgomez
 *
 */

@RunWith(FeaturesRunner.class)
@Features(RechercheFeature.class)
public class TestRechercheTexteIntegral {
    @Inject
    private RechercheFeature rechercheFeature;

    @Inject
    private RechercheService rechercheService;

    // Test tous les champs nuls
    @Test
    public void testSubClauseNull() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                DocumentModelList retour = rechercheService.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    // S'assure que la sous-clause est bien calculé -> plutot dans le test requete
    @Test
    public void testComputeSubClause() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.setQueryType(STQueryConstant.NXQL);
                requete.setDansTexteReponse(false);
                requete.setDansTexteQuestion(true);
                requete.setDansTitre(true);
                String critere = "cheval-et-chat'lioe";
                String critere_result = "${cheval-et-chat''lioe}";
                requete.setCritereRechercheIntegral(critere);
                requete.doBeforeQuery();
                String subclause = requete.getSubClause();
                Assert.assertEquals(
                    String.format(
                        "(ecm:fulltext_txtQuestion = \"%s\" OR ecm:fulltext_senatTitre = \"%s\")",
                        critere_result,
                        critere_result
                    ),
                    subclause
                );
            }
        );
    }

    @Test
    public void testSearchChateau() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setDansTexteQuestion(true);
                requete.setCritereRechercheIntegral("avec");
                requete.doBeforeQuery();
                RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
                String query = rechercheService.getFullQuery(session, requete);
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY + " WHERE (((q.ecm:fulltext_txtQuestion = \"${avec}\")))",
                    query
                );
            }
        );
    }

    @Test
    public void testSearchMedecin() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setDansTexteQuestion(true);
                requete.setCritereRechercheIntegral("médecin\r\n");
                requete.doBeforeQuery();
                RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
                String query = rechercheService.getFullQuery(session, requete);
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY + " WHERE (((q.ecm:fulltext_txtQuestion = \"${médecin}\")))",
                    query
                );
            }
        );
    }

    @Test
    public void testSearchChaineNumérique() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setDansTexteQuestion(true);
                requete.setCritereRechercheIntegral("Une voiture à 75 000 euros");
                requete.doBeforeQuery();
                RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
                String query = rechercheService.getFullQuery(session, requete);
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY +
                    " WHERE (((q.ecm:fulltext_txtQuestion = \"${Une} ${voiture} ${75} ${000} ${euros}\")))",
                    query
                );
            }
        );
    }

    @Test
    public void testRechercheExacte() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setDansTexteQuestion(true);
                requete.setCritereRechercheIntegral("un cadeau de roi, c'est vrai");
                requete.setAppliquerRechercheExacte(true);
                requete.doBeforeQuery();
                RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
                String query = rechercheService.getFullQuery(session, requete);
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY +
                    " WHERE (((q.ecm:fulltext_txtQuestion = \"un cadeau de roi, c''est vrai\")))",
                    query
                );
            }
        );
    }

    @Test
    public void testRechercheExacteMinus() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setDansTexteQuestion(true);
                requete.setCritereRechercheIntegral("renforcée par le décret n° 92-1354");
                requete.setAppliquerRechercheExacte(true);
                requete.doBeforeQuery();
                RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
                String query = rechercheService.getFullQuery(session, requete);
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY +
                    " WHERE (((q.ecm:fulltext_txtQuestion = \"renforcée par le décret n° 92{-}1354\")))",
                    query
                );
            }
        );
    }
}
