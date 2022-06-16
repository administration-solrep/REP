package fr.dila.reponses.core.vocabulary;

import fr.dila.reponses.core.ReponseFeature;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test du service de vocabulaire.
 *
 * @author jtremeaux
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestVocabulary {
    private static final String TA_RUBRIQUE = "TA_rubrique";

    private static final String AN_RUBRIQUE = "AN_rubrique";

    private static final Log log = LogFactory.getLog(TestVocabulary.class);

    // initialize by setUp
    @Inject
    private DirectoryService directoryService;

    @Inject
    private SchemaManager schemaManager;

    @Test
    public void testDeclSchemaVocabulary() {
        Schema schema = schemaManager.getSchema("vocabulary");
        Assert.assertNotNull(schema);
    }

    @Test
    public void testVocDilaCreation() throws Exception {
        log.info("begin : test vocabulary conf info");
        Set<String> names = new HashSet<>();
        List<Directory> dirs = directoryService.getDirectories();
        for (Directory d : dirs) {
            names.add(d.getName());
        }
        Assert.assertTrue(names.contains(AN_RUBRIQUE));
        Assert.assertTrue(names.contains(TA_RUBRIQUE));
    }

    @Test
    public void testVocDilaValue() throws Exception {
        log.info("begin : test vocabulary value");

        Directory anRubriqueDir = directoryService.getDirectory(AN_RUBRIQUE);
        Assert.assertNotNull(anRubriqueDir);

        try (Session anRubriqueTestSession = directoryService.open(AN_RUBRIQUE)) {
            Assert.assertTrue(anRubriqueTestSession.hasEntry("Têtes d'analyse : Liste générale"));
        }

        Directory taRubrique = directoryService.getDirectory(TA_RUBRIQUE);
        Assert.assertNotNull(taRubrique);
        try (Session taRubriqueTestSession = directoryService.open(TA_RUBRIQUE)) {
            Assert.assertTrue(taRubriqueTestSession.hasEntry("2"));
            DocumentModel affil = taRubriqueTestSession.getEntry("2");
            Assert.assertEquals("Têtes d'analyse : Liste générale", affil.getProperty("xvocabulary", "parent"));
        }
    }

    @Test
    public void testNewVocSchema() {
        log.info("begin : test new vocabulary schema");

        Set<String> names = new HashSet<>();
        List<Directory> dirs = directoryService.getDirectories();
        for (Directory d : dirs) {
            names.add(d.getName());
        }
        Assert.assertTrue(names.contains(AN_RUBRIQUE));
        Assert.assertTrue(names.contains(TA_RUBRIQUE));
        Assert.assertTrue(names.contains("legislature"));

        Directory legislatureDir = directoryService.getDirectory("legislature");
        Assert.assertNotNull(legislatureDir);

        try (Session legislatureSession = directoryService.open("legislature")) {
            Assert.assertTrue(legislatureSession.hasEntry("13"));
            DocumentModelList listLegisltature = legislatureSession.query(new HashMap<>());
            Assert.assertNotNull(listLegisltature);
            int nbLegisltature = listLegisltature.size();
            Assert.assertTrue(nbLegisltature > 0);
            DocumentModel legislatureCourante = listLegisltature.get(1);
            String nomLegisltature = (String) legislatureCourante.getProperty("vocabularyLegislature", "label");
            Assert.assertNotNull(nomLegisltature);
        }
    }

    /**
     * Test d'un filtre exclusif sur les entrées d'un vocabulaire.
     *
     * @throws Exception
     */
    @Test
    public void testFilter() {
        try (Session session = directoryService.open(AN_RUBRIQUE)) {
            Assert.assertNotNull(session);
            Map<String, Serializable> filter = new HashMap<>();
            DocumentModelList list = session.query(filter);
            boolean found = false;
            for (Iterator<DocumentModel> iter = list.iterator(); iter.hasNext();) {
                DocumentModel doc = iter.next();
                String id = (String) doc.getProperty("vocabulary", "id");
                if ("administration".equals(id)) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
}
