package fr.dila.reponses.core.recherche;

import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.constant.STQueryConstant;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class RequeteImplIT {
    @Inject
    private RechercheFeature rechercheFeature;

    //On s'assure que les dates sont bien initialisées à une date de début
    // et de fin arbitraires mais éloignées, pour permettre une recherche avec une seule date.
    @Test
    public void testPostTraitementDate() throws Exception {
        rechercheFeature.runTest(
            "testRequete",
            (session, requete) -> {
                requete.setDateQuestionDebut(Calendar.getInstance());
                Assert.assertNotNull(requete.getDateQuestionDebut());
                Assert.assertNull(requete.getDateQuestionFin());
                requete.doBeforeQuery();
                Assert.assertNotNull(requete.getDateQuestionDebut());
                Assert.assertNotNull(requete.getDateQuestionFin());
            }
        );
    }

    /**
     * Un problème dans le cacul des intervalles de date
     * @throws Exception
     */
    @Test
    public void testBug0029563() throws Exception {
        rechercheFeature.runTest(
            "testRequete",
            (session, requete) -> {
                Calendar dateDebut = new GregorianCalendar();
                Calendar dateFin = new GregorianCalendar();
                dateFin.add(3, Calendar.MONTH);
                requete.setDateRange(dateDebut, dateFin);
                requete.doBeforeQuery();
                // On s'assure que les dates ne bougent pas ...
                Assert.assertEquals(dateDebut.getTime(), requete.getDateQuestionDebut().getTime());
                Assert.assertEquals(dateFin.getTime(), requete.getDateQuestionFin().getTime());
                // On met la date de fin nulle, on s'assure qu'elle est bien recalculée la première fois et intouchée la seconde.
                dateDebut = new GregorianCalendar();
                dateFin = null;
                requete.setDateRange(dateDebut, dateFin);
                requete.doBeforeQuery();
                Assert.assertEquals(dateDebut.getTime(), requete.getDateQuestionDebut().getTime());
            }
        );
    }

    //On s'assure que la sous-clause du texte intégral renvoie bien avec les champs null ou faux
    @Test
    public void testHasTextRechercheSelected() throws Exception {
        rechercheFeature.runTest(
            "testRequete",
            (session, requete) -> {
                requete.setDansTexteQuestion(true);
                requete.setDansTexteReponse(false);
                requete.setDansTitre(true);
                Assert.assertEquals(Boolean.TRUE, requete.hasTextRechercheSelected());
            }
        );
    }

    //On s'assure que la sous-clause du texte intégral est bien calculé
    public void testPostTraitementTexteIntegral() throws Exception {
        rechercheFeature.runTest(
            "testRequete",
            (session, requete) -> {
                requete.setQueryType(STQueryConstant.NXQL);
                requete.setDansTexteQuestion(true);
                requete.setDansTexteReponse(false);
                requete.setDansTitre(true);
                Assert.assertEquals(Boolean.FALSE, requete.getDansTexteReponse());
                Assert.assertEquals(Boolean.TRUE, requete.getDansTexteQuestion());
                Assert.assertEquals(Boolean.TRUE, requete.getDansTitre());
                String critere = "alouette, gentille alouette";
                String computed_critere = "${alouette,} ${gentille} ${alouette}";
                requete.setCritereRechercheIntegral(critere);
                requete.doBeforeQuery();
                Assert.assertNotNull(requete.getSubClause());
                String subclause = requete.getSubClause();
                Assert.assertEquals(
                    String.format(
                        "(ecm:fulltext_txtQuestion = \"%s\" OR ecm:fulltext_senatTitre = \"%s\")",
                        computed_critere,
                        computed_critere
                    ),
                    subclause
                );
            }
        );
    }

    //On s'assure que la sous-clause du texte intégral renvoie bien avec les champs null ou faux
    public void testPostTraitementTexteIntegralEmpty() throws Exception {
        rechercheFeature.runTest(
            "testRequete",
            (session, requete) -> {
                requete.setQueryType(STQueryConstant.NXQL);
                requete.setDansTexteQuestion(null);
                requete.setDansTexteReponse(null);
                requete.setDansTitre(null);
                requete.setCritereRechercheIntegral(null);
                requete.doBeforeQuery();
                Assert.assertEquals(null, requete.getSubClause());

                requete.setDansTexteQuestion(false);
                requete.setDansTexteReponse(false);
                requete.setDansTitre(false);
                requete.setCritereRechercheIntegral(null);
                requete.doBeforeQuery();
                Assert.assertEquals(null, requete.getSubClause());
            }
        );
    }

    //Initialisation de la requete
    @Test
    public void testInit() throws Exception {
        rechercheFeature.runTest(
            "testRequete",
            (session, requete) -> {
                requete.init();
                Assert.assertEquals(Boolean.TRUE, requete.getDansTexteQuestion());
                requete.setCritereRechercheIntegral("hhdleo");
                requete.getDocument().reset();
                requete.init();
                Assert.assertEquals(Boolean.TRUE, requete.getDansTexteQuestion());
            }
        );
    }
}
