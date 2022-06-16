package fr.dila.reponses.core.recherche;

import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ETAT_NON_RETIRE;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ETAT_REATTRIBUE;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ETAT_RETIRE;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.enumeration.StatutReponseEnum;
import fr.dila.reponses.api.service.RechercheService;
import java.util.ArrayList;
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
public class TestRechercheMetadonnees {
    @Inject
    private RechercheFeature rechercheFeature;

    @Inject
    private RechercheService rs;

    // Origine question
    @Test
    public void testOrigineQuestion_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                List<String> origineList = new ArrayList<>();
                origineList.add("SE");
                requete.setOrigineQuestion(origineList);
                requete.init();
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    @Test
    public void testOrigineQuestion_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                List<String> origineList = new ArrayList<>();
                origineList.add("SE");
                origineList.add("AN");
                requete.setOrigineQuestion(origineList);
                requete.init();
                String query = rs.getFullQuery(session, requete);
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY + " WHERE (((q.qu:origineQuestion IN ('SE', 'AN'))))",
                    query
                );
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    // Caractéristique question
    @Test
    public void testCaracteristiqueQuestion_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                List<String> cars = new ArrayList<>();
                cars.add(StatutReponseEnum.EN_ATTENTE.name());
                requete.setCaracteristiqueQuestion(cars);
                requete.doBeforeQuery();
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    /**
     * Les 2 conditions ne s'annulent plus, on recherche les questions en cours ou à l'état répondu.
     * @throws Exception
     */
    @Test
    public void testCaracteristiqueQuestion_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                List<String> cars = new ArrayList<>();
                cars.add(StatutReponseEnum.REPONDU.name());
                cars.add(StatutReponseEnum.EN_ATTENTE.name());
                requete.setCaracteristiqueQuestion(cars);
                requete.doBeforeQuery();
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    // Voir avec le directory service
    @Test
    public void test_type_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                List<String> typeQuestions = new ArrayList<>();
                typeQuestions.add(VocabularyConstants.QUESTION_TYPE_QE);
                requete.setTypeQuestion(typeQuestions);
                requete.init();
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    // Voir avec le directory service
    @Test
    public void test_type_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                List<String> typeQuestions = new ArrayList<>();
                typeQuestions.add(VocabularyConstants.QUESTION_TYPE_QOSD);
                requete.setTypeQuestion(typeQuestions);
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    @Test
    public void test_type_etat_question_vide() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.doBeforeQuery();
                String query = rs.getFullQuery(session, requete);
                Assert.assertEquals(RechercheFeature.BASE_QUERY, query);
            }
        );
    }

    @Test
    public void test_type_etat_autre_etat_cloture() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setEtatClotureAutre(true);
                requete.doBeforeQuery();
                String query = rs.getFullQuery(session, requete);
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY + " WHERE (((q.qu:etatQuestion = 'cloture_autre')))",
                    query
                );
            }
        );
    }

    @Test
    public void test_type_etat_caduque() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setEtatCaduque(true);
                requete.doBeforeQuery();
                String query = rs.getFullQuery(session, requete);
                Assert.assertEquals(RechercheFeature.BASE_QUERY + " WHERE (((q.qu:etatQuestion = 'caduque')))", query);
            }
        );
    }

    @Test
    public void test_type_etat_non_clos_et_clos() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setEtatCaduque(true);
                requete.setEtatClotureAutre(true);
                requete.doBeforeQuery();
                String query = rs.getFullQuery(session, requete);
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY + " WHERE (((q.qu:etatQuestion IN ('caduque', 'cloture_autre'))))",
                    query
                );
            }
        );
    }

    @Test
    public void test_type_etat_question_tous() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setEtat(DOSSIER_ETAT_NON_RETIRE, true);
                requete.setEtat(DOSSIER_ETAT_RETIRE, true);
                requete.setEtat(DOSSIER_ETAT_REATTRIBUE, true);
                requete.doBeforeQuery();
                String query = rs.getFullQuery(session, requete);
                Assert.assertEquals(
                    RechercheFeature.BASE_QUERY +
                    " WHERE (((q.qu:etatQuestion = 'retiree' AND q.qu:etatReattribue = 1)))",
                    query
                );
            }
        );
    }
}
