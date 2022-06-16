package fr.dila.reponses.core.recherche;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_ANALYSE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.MOTSCLEF_MINISTERE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RENVOI;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;

import fr.dila.reponses.api.enumeration.IndexModeEnum;
import fr.dila.reponses.api.service.RechercheService;
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
public class TestRechercheIndex {
    @Inject
    private RechercheFeature rechercheFeature;

    @Inject
    private RechercheService rs;

    /** INDEXATION AN **/
    // Indexation AN rubrique
    @Test
    public void testIndexationAN_rubrique_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(AN_RUBRIQUE.getValue(), "Navet");
                requete.addVocEntry(AN_RUBRIQUE.getValue(), "Jesus");
                requete.addVocEntry(AN_RUBRIQUE.getValue(), "agroalimentaire");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testIndexationAN_rubrique_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(AN_RUBRIQUE.getValue(), "Chat");
                requete.addVocEntry(AN_RUBRIQUE.getValue(), "Biere");
                requete.addVocEntry(AN_RUBRIQUE.getValue(), "Autruche");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    // Indexation AN analyse
    @Test
    public void testIndexationAN_analyse_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(AN_ANALYSE.getValue(), "Vin");
                requete.addVocEntry(AN_ANALYSE.getValue(), "choux");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testIndexationAN_analyse_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(AN_ANALYSE.getValue(), "test1");
                requete.addVocEntry(AN_ANALYSE.getValue(), "test2");
                requete.addVocEntry(AN_ANALYSE.getValue(), "test3");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    // Indexation AN TA
    @Test
    public void testIndexationAN_TA_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(TA_RUBRIQUE.getValue(), "vélo");
                requete.addVocEntry(TA_RUBRIQUE.getValue(), "ta1");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testIndexationAN_TA_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(TA_RUBRIQUE.getValue(), "test1");
                requete.addVocEntry(TA_RUBRIQUE.getValue(), "test2");
                requete.addVocEntry(TA_RUBRIQUE.getValue(), "test3");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    /** INDEXATION SENAT **/
    // SE theme
    @Test
    public void testIndexationSenat_theme_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(SE_THEME.getValue(), "Bidonville");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testIndexationSenat_theme_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(SE_THEME.getValue(), "Liberté");
                requete.addVocEntry(SE_THEME.getValue(), "Egalité");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    //SE rubrique
    @Test
    public void testIndexationSenat_rubrique_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(SE_RUBRIQUE.getValue(), "videos");
                requete.addVocEntry(SE_RUBRIQUE.getValue(), "BD");
                requete.addVocEntry(SE_RUBRIQUE.getValue(), "serub2");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testIndexationSenat_rubrique_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(SE_RUBRIQUE.getValue(), "test1");
                requete.addVocEntry(SE_RUBRIQUE.getValue(), "test2");
                requete.addVocEntry(SE_RUBRIQUE.getValue(), "test3");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    //SE_renvois
    @Test
    public void testIndexationSenat_renvois_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(SE_RENVOI.getValue(), "renvois1");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testIndexationSenat_renvois_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(SE_RENVOI.getValue(), "renvois8");
                requete.addVocEntry(SE_RENVOI.getValue(), "renvois9");
                requete.addVocEntry(SE_RENVOI.getValue(), "renvois10");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    //mots clef ministere
    @Test
    public void testIndexationMinistere_bon() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(MOTSCLEF_MINISTERE.getValue(), "m1");
                requete.addVocEntry(MOTSCLEF_MINISTERE.getValue(), "m2");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testIndexationMinistere_mauvais() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.addVocEntry(MOTSCLEF_MINISTERE.getValue(), "m6");
                requete.addVocEntry(MOTSCLEF_MINISTERE.getValue(), "m22");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(0, retour.size());
            }
        );
    }

    @Test
    public void testIndexation_complementaire() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.setIndexationMode(IndexModeEnum.INDEX_COMPL);
                requete.addVocEntry(SE_THEME.getValue(), "tata");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testIndexation_tous() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.setIndexationMode(IndexModeEnum.TOUS);
                requete.addVocEntry(SE_THEME.getValue(), "toto");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }

    @Test
    public void testIndexation_accent() throws Exception {
        rechercheFeature.runTest(
            (session, requete) -> {
                requete.init();
                requete.setIndexationMode(IndexModeEnum.INDEX_COMPL);
                requete.addVocEntry(AN_RUBRIQUE.getValue(), "lutte contre les inondations");
                DocumentModelList retour = rs.query(session, requete);
                Assert.assertEquals(1, retour.size());
            }
        );
    }
}
