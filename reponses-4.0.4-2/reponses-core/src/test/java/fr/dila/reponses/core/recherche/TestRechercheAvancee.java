package fr.dila.reponses.core.recherche;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.RechercheService;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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
public class TestRechercheAvancee {
    @Inject
    private RechercheFeature rechercheFeature;

    @Inject
    private RechercheService rs;

    //    Numéro Question
    @Test
    public void testRequeteAvancee_numeroQuestion_mauvaisintervalle() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                // mauvais intervalle !
                requete.setNumeroRange(7, 200);
                DocumentModelList retour_numero_rien = rs.query(session, requete);
                Assert.assertEquals(0, retour_numero_rien.size());
            }
        );
    }

    @Test
    public void testRequeteAvancee_numeroQuestion_bonintervalle() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                // bon intervalle !
                requete.setNumeroRange(0, 20000);
                DocumentModelList docs = session.query("SELECT * FROM Question");
                Assert.assertNotNull(docs);
                Assert.assertEquals(1, docs.size());
                DocumentModelList retour_numero = rs.query(session, requete);
                Assert.assertEquals(1, retour_numero.size());
            }
        );
    }

    // Date JO Question
    @Test
    public void testRequeteAvancee_dateJOQuestion_mauvaisintervalle() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                Calendar dateDebut = GregorianCalendar.getInstance();
                dateDebut.set(2000, 5, 14);
                Calendar dateFin = GregorianCalendar.getInstance();
                dateFin.set(2005, 5, 14);
                requete.setDateRange(dateDebut, dateFin);
                DocumentModelList retour_date = rs.query(session, requete);
                Assert.assertEquals(0, retour_date.size());
            }
        );
    }

    @Test
    public void testRequeteAvancee_dateJOQuestion_bonintervalle() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                Calendar dateDebut = GregorianCalendar.getInstance();
                dateDebut.set(2000, 5, 14);
                Calendar dateFin = GregorianCalendar.getInstance();
                dateFin.set(2015, 5, 14);
                requete.setDateRange(dateDebut, dateFin);
                DocumentModelList retour_date = rs.query(session, requete);
                Assert.assertEquals(1, retour_date.size());
            }
        );
    }

    ////    //Test date de debut null
    @Test
    public void testRequeteAvancee_dateJOQuestion_debutnull() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                Calendar dateFin = GregorianCalendar.getInstance();
                dateFin.set(2015, 5, 14);
                requete.setDateRange(null, dateFin);
                DocumentModelList retour_date = rs.query(session, requete);
                Assert.assertEquals(1, retour_date.size());
            }
        );
    }

    //
    ////  //Test date de debut null qui echoue
    @Test
    public void testRequeteAvancee_dateJOQuestion_debutnullmauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                Calendar dateFin = GregorianCalendar.getInstance();
                dateFin.set(1990, 5, 14);
                requete.setDateRange(null, dateFin);
                DocumentModelList retour_date = rs.query(session, requete);
                Assert.assertEquals(0, retour_date.size());
            }
        );
    }

    //
    //
    ////  //Test date de fin null
    @Test
    public void testRequeteAvancee_dateJOQuestion_finnull() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                Calendar dateDebut = GregorianCalendar.getInstance();
                dateDebut.set(2000, 5, 14);
                requete.setDateRange(dateDebut, null);
                DocumentModelList retour_date = rs.query(session, requete);
                Assert.assertEquals(1, retour_date.size());
            }
        );
    }

    //
    ////  //Test date de fin null qui echoue
    @Test
    public void testRequeteAvancee_dateJOQuestion_finnullmauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                Calendar dateDebut = GregorianCalendar.getInstance();
                dateDebut.set(2020, 5, 14);
                requete.setDateRange(dateDebut, null);
                DocumentModelList retour_date = rs.query(session, requete);
                Assert.assertEquals(0, retour_date.size());
            }
        );
    }

    //
    //
    //    // Date JO Reponse
    @Test
    public void testRequeteAvancee_dateJOReponse_mauvaisintervalle() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                Calendar dateDebut = GregorianCalendar.getInstance();
                dateDebut.set(1990, 5, 14);
                Calendar dateFin = GregorianCalendar.getInstance();
                dateFin.set(2000, 5, 14);
                requete.setDateRangeReponse(dateDebut, dateFin);
                DocumentModelList retour_date = rs.query(session, requete);
                Assert.assertEquals(0, retour_date.size());
            }
        );
    }

    // Groupe politique
    @Test
    public void testRequeteAvancee_groupePolitique_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.setGroupePolitique("UMP");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    //
    @Test
    public void testRequeteAvancee_groupePolitique_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.setGroupePolitique("PS");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    @Test
    public void testRequeteAvancee_nomAuteur_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                String nomauteur = "Taillerand Jean-Marc";
                requete.setNomAuteur(nomauteur);
                Question question1 = rechercheFeature.getQuestion1(session);
                Assert.assertEquals("Taillerand Jean-Marc", question1.getNomCompletAuteur());
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testRequeteAvancee_nomAuteur_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.setNomAuteur("Taillerant Jean-Marc");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    @Test
    public void testRequeteAvancee_accent() throws Exception {
        String nomauteur = "Hervé André";
        String nomauteurSansAccent = Normalizer.normalize(nomauteur, Normalizer.Form.NFD);
        nomauteurSansAccent = nomauteurSansAccent.replaceAll("[^\\p{ASCII}]", "");
        Assert.assertEquals("Herve Andre", nomauteurSansAccent);
    }

    @Test
    public void testRequeteAvancee_parenthese() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                List<String> origineQuestion = new ArrayList<>();
                requete.init();
                origineQuestion.add("AN");
                origineQuestion.add("SENAT");
                requete.setOrigineQuestion(origineQuestion);
                List<String> typeQuestions = new ArrayList<>();
                typeQuestions.add(VocabularyConstants.QUESTION_TYPE_QE);
                requete.setTypeQuestion(typeQuestions);
                requete.doBeforeQuery();
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY +
                    " WHERE (((q.qu:origineQuestion IN ('AN', 'SENAT')) AND (q.qu:typeQuestion = 'QE')))",
                    rs.getFullQuery(session, requete)
                );
            }
        );
    }

    /**
     * Assure que le traitement du champ question est bien protégé par des parenthèses.
     * @throws Exception
     */
    @Test
    public void testRequeteNumeroQuestion() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setChampRequeteSimple("50;1546");
                List<String> typeQuestions = new ArrayList<>();
                typeQuestions.add(VocabularyConstants.QUESTION_TYPE_QE);
                requete.setTypeQuestion(typeQuestions);
                requete.doBeforeQuery();
                //   Assert.assertEquals("((q.ecm:fulltext_idQuestion LIKE '50') OR (q.qu:numeroQuestion = 1546))",requete.getClauseChampRequeteSimple());
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY +
                    " WHERE ((((q.ecm:fulltext_idQuestion LIKE '50') OR (q.qu:numeroQuestion = 1546)) AND (q.qu:typeQuestion = 'QE')))",
                    rs.getFullQuery(session, requete)
                );
            }
        );
    }
}
