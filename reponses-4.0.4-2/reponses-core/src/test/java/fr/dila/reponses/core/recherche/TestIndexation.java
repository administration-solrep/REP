package fr.dila.reponses.core.recherche;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.MOTSCLEF_MINISTERE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.ReponseFeature;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestIndexation {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private RechercheService rs;

    public ReponsesIndexableDocument createIndexableDocument(CoreSession session) {
        ReponsesIndexableDocument indexableDoc = null;
        DocumentModel requeteDoc = rs.createRequete(session, "testRequete").getDocument();
        indexableDoc = requeteDoc.getAdapter(ReponsesIndexableDocument.class);
        return indexableDoc;
    }

    /**
     * Crée un document indexable avec qq valeurs par défaut
     * @return
     * @throws Exception
     */
    public ReponsesIndexableDocument getDefaultIndexableDocument(CoreSession session) {
        ReponsesIndexableDocument indexableDoc = createIndexableDocument(session);
        indexableDoc.addVocEntry(AN_RUBRIQUE.getValue(), "truc");
        indexableDoc.addVocEntry(AN_RUBRIQUE.getValue(), "machin");
        indexableDoc.addVocEntry(SE_THEME.getValue(), "obj1");
        indexableDoc.addVocEntry(SE_THEME.getValue(), "obj2");
        indexableDoc.addVocEntry(SE_THEME.getValue(), "obj3");
        indexableDoc.addVocEntry(TA_RUBRIQUE.getValue(), "bidouille");
        return indexableDoc;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddAndRemoveVocEntry() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            ReponsesIndexableDocument indexableDoc = createIndexableDocument(session);
            String vocAN = AN_RUBRIQUE.getValue();
            String label1 = "chômage : indemnisation";
            String label2 = "collectivités territoriales";
            indexableDoc.addVocEntry(vocAN, label1);
            indexableDoc.addVocEntry(vocAN, label2);
            List<String> AN_list = (List<String>) indexableDoc
                .getDocument()
                .getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA, vocAN);
            Assert.assertNotNull(AN_list);
            Assert.assertEquals(2, AN_list.size());
            indexableDoc.removeVocEntry(vocAN, label1);
            AN_list =
                (List<String>) indexableDoc
                    .getDocument()
                    .getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA, vocAN);
            Assert.assertNotNull(AN_list);
            Assert.assertEquals(1, AN_list.size());
        }
    }

    @Test
    public void testAddAndRemoveVocEntryException() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Test ajout et retrait sur un vocabulaire ne faisant pas partie du schéma
            ReponsesIndexableDocument indexableDoc = createIndexableDocument(session);
            try {
                indexableDoc.addVocEntry("notavoc", "terre");
                Assert.fail("addVoc aurait du lancer une exception");
            } catch (Exception e) {
                //Ok, c'est une exeption
            }
        }
    }

    @Test
    public void testListIndexByZone() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            ReponsesIndexableDocument indexableDoc = getDefaultIndexableDocument(session);
            Assert.assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN));
            Assert.assertEquals(3, indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN).size());
            Assert.assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_SENAT));
            Assert.assertEquals(3, indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_SENAT).size());
            indexableDoc.removeVocEntry(AN_RUBRIQUE.getValue(), "machin");
            indexableDoc.addVocEntry(MOTSCLEF_MINISTERE.getValue(), "chose");
            Assert.assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN));
            Assert.assertEquals(2, indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN).size());
            Assert.assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_SENAT));
            Assert.assertEquals(3, indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_SENAT).size());
            Assert.assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_MINISTERE));
            Assert.assertEquals(
                1,
                indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_MINISTERE).size()
            );
            Assert.assertEquals(
                "motclef_ministere",
                indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_MINISTERE).get(0)[0]
            );
            Assert.assertEquals(
                "chose",
                indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_MINISTERE).get(0)[1]
            );
        }
    }

    /**
     * Test le retour de tous les mots-clef sous forme de string
     * @throws Exception
     */
    @Test
    public void testMotsClef() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            ReponsesIndexableDocument indexableDoc = getDefaultIndexableDocument(session);
            String motsClef = indexableDoc.getMotsClef();
            Assert.assertNotNull(motsClef);
            Assert.assertEquals("obj1 obj2 obj3 truc machin bidouille ".length(), motsClef.length());
            Assert.assertTrue(motsClef.contains("obj1"));
            Assert.assertTrue(motsClef.contains("obj2"));
            Assert.assertTrue(motsClef.contains("obj3"));
            Assert.assertTrue(motsClef.contains("truc"));
            Assert.assertTrue(motsClef.contains("machin"));
            Assert.assertTrue(motsClef.contains("bidouille"));
        }
    }
}
