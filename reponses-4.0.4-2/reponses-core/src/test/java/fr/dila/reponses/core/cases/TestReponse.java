package fr.dila.reponses.core.cases;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.RErratum;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.service.STParametreService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.versioning.VersioningService;
import org.nuxeo.ecm.core.schema.DocumentType;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Ajout de test sur la manipulation des versions de reponses
 * @author spesnel
 *
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestReponse {
    private static final Log LOG = LogFactory.getLog(TestReponse.class);

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private ReponseService reponseService;

    @Inject
    private STParametreService stParametreService;

    @Before
    public void setUp() {
        stParametreService.clearCache();
    }

    @Test
    public void testReponseCreationHandled() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession("Administrator")) {
            // Cree l'objet representant le document
            String path = "mypath";

            Dossier dossier = reponseFeature.createDossier(session);
            DocumentType reponse = session.getDocumentType(DossierConstants.REPONSE_DOCUMENT_TYPE);
            Assert.assertNotNull("Does our type exist?", reponse);

            //create the object that represents the new document
            DocumentModel modelDesired = session.createDocumentModel("/", path, DossierConstants.REPONSE_DOCUMENT_TYPE);
            modelDesired.setProperty("reponse", "identifiant", 12356);
            modelDesired.setProperty("reponse", "dateValidation", new Date());
            modelDesired.setProperty("reponse", "datePublicationJOReponse", new Date());
            modelDesired.setProperty("reponse", "pageJOReponse", 3);
            modelDesired.setProperty("reponse", "verrou", "oui");
            modelDesired.setProperty("note", "note", "test");

            //create the document in the repository
            DocumentModel modelResult = session.createDocument(modelDesired);
            Assert.assertNotNull(modelResult.getAdapter(Reponse.class));
            Assert.assertTrue(modelResult.hasFacet("Versionable"));

            //save new reponse with new data
            modelResult.setProperty("note", "note", "note version 1");

            modelResult = reponseService.saveReponse(session, modelResult, dossier.getDocument());
            DocumentModel initVersion = modelResult;

            //checkVersionNumber
            Assert.assertEquals(reponseService.getReponseVersionDocumentList(session, modelResult).size(), 0);

            //make sure that the path is the parent path (/) plus our new path
            Assert.assertEquals("path is same?", "/" + path, modelResult.getPathAsString());
            Assert.assertEquals("path is same? (sanity)", "/" + path, modelDesired.getPathAsString());
            //document is created ok, let's see if event handler ran
            Assert.assertNotSame("the result object is different than the desired object?", modelDesired, modelResult);

            //test document reponse property
            Assert.assertNotNull(modelResult.getProperty("reponse", "idAuteurReponse"));
            Assert.assertEquals("Administrator", modelResult.getProperty("reponse", "idAuteurReponse"));
            Assert.assertEquals("note version 1", modelResult.getProperty("note", "note"));

            //test document versionning : document must be in major version 1
            Long numeroVersion = 1L;
            Assert.assertNotNull(modelResult.getPropertyValue(VersioningService.MAJOR_VERSION_PROP));
            modelResult = reponseService.incrementReponseVersion(session, modelResult);
            Assert.assertEquals(numeroVersion, modelResult.getPropertyValue(VersioningService.MAJOR_VERSION_PROP));
            //Assert.assertEquals(0, ((Long)modelResult.getPropertyValue(VersioningService.MINOR_VERSION_PROP)).longValue());
            //call service to check n° version
            Assert.assertNotNull(reponseService.getReponseMajorVersionNumber(session, modelResult));
            Assert.assertEquals(
                numeroVersion.intValue(),
                reponseService.getReponseMajorVersionNumber(session, modelResult)
            );

            // CREATE A NEW VERSION (v2.0)
            modelResult.setProperty("note", "note", "note version 2");
            modelResult = reponseService.incrementReponseVersion(session, modelResult);
            Assert.assertEquals(2L, reponseService.getReponseMajorVersionNumber(session, modelResult));

            // CREATE A NEW VERSION (v3.0)
            modelResult.setProperty("note", "note", "note version 3");
            reponseService.incrementReponseVersion(session, modelResult);
            modelResult = session.getDocument(modelResult.getRef());
            Assert.assertEquals(3L, reponseService.getReponseMajorVersionNumber(session, modelResult));

            {
                DocumentRef docref = initVersion.getRef();

                DocumentModel rep = session.getDocument(docref);
                Assert.assertEquals(3L, reponseService.getReponseMajorVersionNumber(session, rep));
                Assert.assertEquals("note version 3", rep.getProperty("note", "note"));

                // verfi recup de version
                DocumentModel repv1 = reponseService.getReponseOldVersionDocument(session, rep, 1);
                Assert.assertEquals(1L, reponseService.getReponseMajorVersionNumber(session, repv1));

                DocumentModel repv2 = reponseService.getReponseOldVersionDocument(session, rep, 2);
                Assert.assertEquals(2L, reponseService.getReponseMajorVersionNumber(session, repv2));

                DocumentModel repv3 = reponseService.getReponseOldVersionDocument(session, rep, 3);
                Assert.assertEquals(3L, reponseService.getReponseMajorVersionNumber(session, repv3));

                LOG.info(rep);
                // parcours des versions
                List<DocumentModel> versions = reponseService.getReponseVersionDocumentList(session, rep);
                Assert.assertEquals(3, versions.size());
                for (DocumentModel dm : versions) {
                    LOG.info(dm);
                    int num = reponseService.getReponseMajorVersionNumber(session, dm);
                    String note = "note version " + num;
                    Assert.assertEquals(note, dm.getProperty("note", "note"));

                    String auteur = dm
                        .getProperty(
                            DossierConstants.REPONSE_DOCUMENT_SCHEMA,
                            DossierConstants.DOSSIER_ID_AUTEUR_REPONSE
                        )
                        .toString();

                    LOG.info(auteur);
                }
            }

            session.cancel();
        }
    }

    /**
     * Test de l'errata de la réponse, cas de la réponse non publiée
     * @throws Exception
     */
    @Test
    public void testErratumReponsesHandled_reponsenonpubliee() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Cree l'objet representant le document
            String path = "mypath";

            DocumentType reponseDoctype = session.getDocumentType(DossierConstants.REPONSE_DOCUMENT_TYPE);
            Assert.assertNotNull("Does our type exist?", reponseDoctype);

            //Sauvegarde d'une réponse non publiée
            DocumentModel modelDesired = session.createDocumentModel("/", path, DossierConstants.REPONSE_DOCUMENT_TYPE);
            Reponse reponse = modelDesired.getAdapter(Reponse.class);
            reponse.setTexteReponse("Ceci est le message initial");
            DocumentModel reponsesResult = session.createDocument(reponse.getDocument());

            DocumentModel savedReponseDoc = reponseService.saveReponseAndErratum(session, reponsesResult, null);
            Reponse savedReponse = savedReponseDoc.getAdapter(Reponse.class);
            Assert.assertNotNull(savedReponse.getErrata());
            Assert.assertEquals(0, savedReponse.getErrata().size());
            reponse.setDateJOreponse(Calendar.getInstance());
            reponse.setPageJOreponse((long) 3);
        }
    }

    /**
     * Test de l'errata de la réponse, cas de la réponse publiée
     * @throws Exception
     */
    @Test
    public void testErratumReponsesHandled_reponsepubliee() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Cree l'objet representant le document
            String path = "mypath";

            DocumentType reponseDoctype = session.getDocumentType(DossierConstants.REPONSE_DOCUMENT_TYPE);
            Assert.assertNotNull("Does our type exist?", reponseDoctype);

            //Sauvegarde d'une réponse non publiée
            DocumentModel modelDesired = session.createDocumentModel("/", path, DossierConstants.REPONSE_DOCUMENT_TYPE);
            Reponse reponse = modelDesired.getAdapter(Reponse.class);
            reponse.setTexteReponse("Ceci est le message final");
            reponse.setCurrentErratum("Correction du message initial en final");
            reponse.setDateJOreponse(Calendar.getInstance());
            reponse.setPageJOreponse((long) 3);
            Assert.assertTrue(reponse.isPublished());
            DocumentModel reponsesResult = session.createDocument(reponse.getDocument());

            DocumentModel savedReponseDoc = reponseService.saveReponseAndErratum(session, reponsesResult, null);
            Reponse savedReponse = savedReponseDoc.getAdapter(Reponse.class);
            Assert.assertNotNull(savedReponse.getErrata());
            Assert.assertEquals(1, savedReponse.getErrata().size());
            RErratum erratum = savedReponse.getErrata().get(0);
            Assert.assertEquals("Correction du message initial en final", erratum.getTexteErratum());
            Assert.assertEquals("Ceci est le message final", erratum.getTexteConsolide());
        }
    }

    @Test
    public void testFondDeDossierCreationHandled() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Cree l'objet representant le document
            String path = "mypath";
            DocumentModel fddModel = session.createDocumentModel(path, "unFDD", "FondDeDossier");
            Assert.assertNotNull(fddModel);

            // Verifie que l'objet est folderish
            session.createDocument(fddModel);
            DocumentModel doc = session.createDocumentModel(fddModel.getPathAsString(), "unFile", "File");
            Assert.assertNotNull(doc);

            // Verifie que l'objet n'est pas folderish pour les objet de type case
            DocumentModel doc2 = session.createDocumentModel(
                fddModel.getPathAsString(),
                "unFile",
                DossierConstants.DOSSIER_DOCUMENT_TYPE
            );
            Assert.assertNotNull(doc2);

            // Verifie les propriétés de l'objet
            DocumentModel modelDesired = session.createDocumentModel("/", path, "FondDeDossier");
            Assert.assertNotNull("fond dossier doesn't exist", modelDesired);
            DocumentModel modelResult = session.createDocument(modelDesired);

            // Controle si le chemin du fichier est correct
            Assert.assertEquals("path is same?", "/" + path, modelResult.getPathAsString());
            Assert.assertEquals("path is same? (sanity)", "/" + path, modelDesired.getPathAsString());

            DocumentType reponseType = session.getDocumentType("Reponse");
            Assert.assertNotNull("Le type fdd n'existe pas ", reponseType);
        }
    }
}
