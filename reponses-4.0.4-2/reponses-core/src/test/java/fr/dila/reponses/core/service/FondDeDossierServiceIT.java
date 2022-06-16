package fr.dila.reponses.core.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.mock.MockFeuilleRouteModelService;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.api.versioning.VersioningService;
import org.nuxeo.ecm.core.schema.FacetNames;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author arolin
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class FondDeDossierServiceIT {
    private static final Log log = LogFactory.getLog(FondDeDossierServiceIT.class);

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private FondDeDossierService fondDeDossierService;

    @Inject
    private DossierDistributionService dossierDistributionService;

    private DocumentRef dossierRef = null;

    @Before
    public void setUp() {
        STServiceLocator.getSTParametreService().clearCache();
        ((MockFeuilleRouteModelService) ReponsesServiceLocator.getFeuilleRouteModelService()).clear();
        reponseFeature.setFeuilleRootModelFolderId(null);
        if (dossierRef == null) {
            try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.newMockSSPrincipal())) {
                reponseFeature.createDocument(session, "trash-root", "TrashRoot", "/case-management");

                reponseFeature.createOrGetFeuilleRouteModelFolder(session);

                DocumentModel questionDocumentModel = ReponseFeature.createDocument(
                    session,
                    DossierConstants.QUESTION_DOCUMENT_TYPE,
                    "newQuestionTest"
                );
                Question question = questionDocumentModel.getAdapter(Question.class);

                // Crée un dossier avec un fond de dossier et les 3 répertoires de niveau 1
                DocumentModel docModel = ReponseFeature.createDocument(
                    session,
                    DossierConstants.DOSSIER_DOCUMENT_TYPE,
                    "newDossierTest"
                );
                dossierRef = docModel.getRef();
                Dossier dossier = docModel.getAdapter(Dossier.class);
                dossier =
                    dossierDistributionService.createDossier(
                        session,
                        dossier,
                        question,
                        null,
                        VocabularyConstants.ETAT_QUESTION_EN_COURS
                    );

                // Démarre la feuille de route associée au dossier
                dossierDistributionService.startDefaultRoute(session, dossier);

                session.save();
            }
        }
    }

    @Test
    public void testDefaultRepositoryCreationInDossier() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            log.info("begin : test dossier default repository creation ");

            // get fond de dossier
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
            Assert.assertNotNull(fondDeDossier);
            Assert.assertNotNull(fondDeDossier.getDocument());
            Assert.assertNotNull(fondDeDossier.getDocument().getId());

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );
            Assert.assertNotNull(fondDossierNodeList);
            Assert.assertEquals(fondDossierNodeList.size(), 3);
            Assert.assertEquals(
                fondDossierNodeList.get(0).getName(),
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME
            );
            Assert.assertEquals(
                fondDossierNodeList.get(1).getName(),
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME
            );
            Assert.assertEquals(
                fondDossierNodeList.get(2).getName(),
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_NAME
            );

            Assert.assertEquals(fondDeDossier.getRepertoireParlementId(), fondDossierNodeList.get(0).getId());
            Assert.assertEquals(fondDeDossier.getRepertoireSggId(), fondDossierNodeList.get(1).getId());
            Assert.assertEquals(fondDeDossier.getRepertoireMinistereId(), fondDossierNodeList.get(2).getId());
        }
    }

    @Test
    public void testCreateBasicRepertoire() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            log.info("begin : test dossier creation repertoire ");

            // getFondDeDossier
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
            Assert.assertNotNull(fondDeDossier);
            Assert.assertNotNull(fondDeDossier.getDocument());
            Assert.assertNotNull(fondDeDossier.getDocument().getId());

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();
            Assert.assertTrue(fondDeDossierDoc.hasFacet(FacetNames.FOLDERISH));
            Assert.assertTrue(fondDeDossierDoc.hasSchema("fondDossier"));

            // check creation service
            fondDeDossierService.createFondDeDossierRepertoire(
                session,
                fondDeDossier.getDocument(),
                "creation répertoire Fdd test"
            );
            session.save();

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );

            Assert.assertNotNull(fondDossierNodeList);
            // the dossier has already 3 child so if the creation work, he must have 4 child
            Assert.assertEquals(fondDossierNodeList.size(), 4);
            Assert.assertEquals(fondDossierNodeList.get(3).getName(), "creation répertoire Fdd test");
            Assert.assertEquals(
                fondDossierNodeList.get(3).getDocument().getType(),
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE
            );
        }
    }

    @Test
    public void testcreateEmptyFondDeDossierFichierDocModel() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            log.info("begin : test createEmptyFondDeDossierFichierDocModel method ");

            // call method
            DocumentModel emptyFddFileDocModel = fondDeDossierService.createBareFondDeDossierFichier(session);
            Assert.assertNotNull(emptyFddFileDocModel);
            // check schema and Facet
            Assert.assertTrue(emptyFddFileDocModel.hasSchema(STSchemaConstant.FILE_SCHEMA));
            Assert.assertTrue(emptyFddFileDocModel.hasSchema(STSchemaConstant.UID_SCHEMA));
            Assert.assertTrue(emptyFddFileDocModel.hasSchema(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA));
            Assert.assertTrue(emptyFddFileDocModel.hasSchema(STSchemaConstant.DUBLINCORE_SCHEMA));
            Assert.assertTrue(emptyFddFileDocModel.hasFacet("Versionable"));
        }
    }

    @Test
    public void testCreatefddFile() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            log.info("begin : test dossier creation fdd File method ");

            // get fond de dossier
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
            Assert.assertNotNull(fondDeDossier);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );
            Assert.assertNotNull(fondDossierNodeList);
            Assert.assertEquals(fondDossierNodeList.size(), 3);
            for (FondDeDossierFolder folder : fondDossierNodeList) {
                log.info("Name :" + folder.getName() + "    Pos :" + folder.getDocument().getPos());
            }

            Assert.assertEquals(
                fondDossierNodeList.get(0).getName(),
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME
            );

            // init fichierFdd
            // check service : creation fichier with repertoire parent
            DocumentModel repParent = fondDossierNodeList.get(0).getDocument();
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "nomFichier",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                null
            );

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repParent
            );
            Assert.assertNotNull(repertoireNodeList);
            Assert.assertEquals(repertoireNodeList.size(), 1);
        }
    }

    @Test
    public void testGetDefaultNiveauVisibiliteFromRepository() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            log.info("begin : test dossier creation fdd File method ");

            // create fond de dossier
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);

            fondDeDossier = dossier.getFondDeDossier(session);
            Assert.assertNotNull(fondDeDossier);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );
            Assert.assertNotNull(fondDossierNodeList);
            Assert.assertEquals(fondDossierNodeList.size(), 3);
            Assert.assertEquals(
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME,
                fondDossierNodeList.get(1).getName()
            );

            String nivVisibilite = null;
            String idRepertoire = fondDossierNodeList.get(1).getId();
            nivVisibilite = fondDeDossierService.getDefaultNiveauVisibiliteFromRepository(idRepertoire, session);
            Assert.assertNotNull(nivVisibilite);
            Assert.assertEquals(
                nivVisibilite,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL
            );
        }
    }

    @Test
    public void testMultipleChildCreation() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("begin : test dossier creation repertoire + fichier ");

            // create fond de dossier and
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
            Assert.assertNotNull(fondDeDossier);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );
            Assert.assertNotNull(fondDossierNodeList);
            Assert.assertEquals(fondDossierNodeList.size(), 3);
            Assert.assertEquals(
                fondDossierNodeList.get(0).getName(),
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME
            );

            // check service : creation fichier with repertoire parent
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "creation répertoire child",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                fondDossierNodeList.get(0).getDocument()
            );
            Assert.assertNotNull(repertoireNodeList);
            Assert.assertEquals(1, repertoireNodeList.size());
        }
    }

    @Test
    public void testElementFddCreation() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("begin : test dossier creation repertoire + element fond de dossier ");

            // create fond de dossier and
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
            Assert.assertNotNull(fondDeDossier);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );

            // check service : creation fichier with repertoire parent
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "creation_element_fils",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // check new element fond de dossier node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                fondDossierNodeList.get(0).getDocument()
            );
            Assert.assertNotNull(repertoireNodeList);
            Assert.assertEquals(1, repertoireNodeList.size());
            Assert.assertEquals("creation_element_fils", repertoireNodeList.get(0).getTitle());
            Assert.assertNotNull(repertoireNodeList.get(0).getId());
            Assert.assertEquals(
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                repertoireNodeList.get(0).getNiveauVisibilite()
            );
            Assert.assertEquals("1", repertoireNodeList.get(0).getNumeroVersion());

            DocumentModel elementFddDoc = session.getDocument(new IdRef(repertoireNodeList.get(0).getId()));
            Assert.assertNotNull(elementFddDoc);
        }
    }

    @Test
    public void testDeleteElementFdd() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("begin : test delete an fdd file document ");

            // create fond de dossier and
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);
            fondDeDossier = dossier.getFondDeDossier(session);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check service : creation fdd child
            fondDeDossierService.createFondDeDossierRepertoire(
                session,
                fondDeDossier.getDocument(),
                "creation répertoire Fdd test"
            );
            session.save();

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );

            // check service : creation fichier with repertoire parent
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "creation_element_fils",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // check new element fond de dossier node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                fondDossierNodeList.get(0).getDocument()
            );
            Assert.assertNotNull(repertoireNodeList);
            Assert.assertEquals(1, repertoireNodeList.size());
            Assert.assertEquals("creation_element_fils", repertoireNodeList.get(0).getTitle());
            Assert.assertNotNull(repertoireNodeList.get(0).getId());

            String idFddDoc = repertoireNodeList.get(0).getId();
            DocumentModel elementFddDoc = session.getDocument(new IdRef(idFddDoc));
            Assert.assertNotNull(elementFddDoc);

            // delete element fils
            fondDeDossierService.delete(idFddDoc, session, dossier.getDocument());

            // check element fils removal
            @SuppressWarnings("unchecked")
            List<FondDeDossierFile> repertoireNodeListDeleted = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                fondDossierNodeList.get(0).getDocument()
            );
            Assert.assertNotNull(repertoireNodeListDeleted);
            Assert.assertEquals(repertoireNodeListDeleted.size(), 0);
        }
    }

    @Test
    public void testDeleteElementFddWithVersion() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("begin : test delete an fdd file document with versions ");

            // create fond de dossier and
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);
            fondDeDossier = dossier.getFondDeDossier(session);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );

            // check service : creation fichier with repertoire parent
            DocumentModel fichierFdd = fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "creation_element_fils",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // check new element fond de dossier node properties
            @SuppressWarnings("unchecked")
            List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                fondDossierNodeList.get(0).getDocument()
            );
            Assert.assertNotNull(repertoireNodeList);
            Assert.assertEquals(1, repertoireNodeList.size());
            Assert.assertEquals("creation_element_fils", repertoireNodeList.get(0).getTitle());
            Assert.assertNotNull(repertoireNodeList.get(0).getId());

            DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
            FondDeDossierFile file = newFichierDoc.getAdapter(FondDeDossierFile.class);
            file.setContent(new StringBlob("update_element_fils"));
            file.setFilename("update_element_fils");

            FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
            newFichier.setTitle("update_element_fils");
            newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

            // save update
            fondDeDossierService.updateFddFile(session, fichierFdd, newFichierDoc, dossier.getDocument());

            DocumentModel repertoireDoc = fondDossierNodeList.get(0).getDocument();
            String elementFddId = null;
            @SuppressWarnings("unchecked")
            List<FondDeDossierFile> repertoireNodeListUpdated = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireDoc
            );
            Assert.assertNotNull(repertoireNodeListUpdated);
            Assert.assertEquals(1, repertoireNodeListUpdated.size());
            Assert.assertEquals("update_element_fils", repertoireNodeListUpdated.get(0).getTitle());
            Assert.assertNotNull(repertoireNodeListUpdated.get(0).getId());
            Assert.assertEquals("2", repertoireNodeListUpdated.get(0).getNumeroVersion());
            elementFddId = repertoireNodeListUpdated.get(0).getId();
            DocumentModel elementFddDoc = session.getDocument(new IdRef(elementFddId));
            Assert.assertNotNull(elementFddDoc);

            // delete element fils
            fondDeDossierService.delete(elementFddId, session, dossier.getDocument());

            // check element fils removal
            @SuppressWarnings("unchecked")
            List<FondDeDossierFile> repertoireNodeListDeleted = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireDoc
            );
            Assert.assertNotNull(repertoireNodeListDeleted);
            Assert.assertEquals(0, repertoireNodeListDeleted.size());
        }
    }

    @Test
    public void testVersionManipulation() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = session.createDocumentModel(
                "/",
                "testfile1",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE
            );

            doc = session.createDocument(doc);
            DublincoreSchemaUtils.setTitle(doc, "A");

            doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
            // doc.setPropertyValue("uid:major_version", new Long(1));

            doc = session.saveDocument(doc);
            // DocumentRef docRef = session.checkIn(doc.getRef(), VersioningOption.MAJOR, "");
            DocumentRef docRef = doc.getRef();
            // Assert.assertTrue(doc.isCheckedOut());
            assertVersion("1.0", doc);

            log.debug("LIFECYCLE : " + doc.getLifeCyclePolicy());
            String lifecyclestate = session.getCurrentLifeCycleState(doc.getRef());
            log.debug("LIFECYCLESTATE : " + lifecyclestate);

            // save with no option, use default
            DublincoreSchemaUtils.setTitle(doc, "B");
            doc = session.saveDocument(doc);
            Assert.assertTrue(doc.isCheckedOut());
            assertVersion("1.0", doc);

            // change and save with new minor
            DublincoreSchemaUtils.setTitle(doc, "C");
            doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MINOR);
            doc = session.saveDocument(doc);
            Assert.assertFalse(doc.isCheckedOut());
            assertVersion("1.1", doc);

            // DocumentModel v01 = session.getLastDocumentVersion(docRef);
            // Assert.assertEquals(v01.getId(), session.getBaseVersion(docRef).reference());

            List<DocumentModel> dml = session.getVersions(docRef);
            for (DocumentModel dm : dml) {
                log.info("doc: " + getMajor(dm) + "." + getMinor(dm) + "-->" + DublincoreSchemaUtils.getTitle(dm));
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRestoreToLastFddFileVersion() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            log.info("begin : test dossier creation repertoire + element fond de dossier ");

            // create fond de dossier and
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);

            fondDeDossier = dossier.getFondDeDossier(session);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );

            // check service : creation fichier with repertoire parent
            String initialFilename = "creation_element_fils";
            String initialVersion = "1";
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                initialFilename,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // check new element fond de dossier node properties
            DocumentModel repertoireDoc = fondDossierNodeList.get(0).getDocument();

            List<FondDeDossierFile> fondDossierFileList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireDoc
            );
            checkCurrentFileAndLastVersion(session, fondDossierFileList, initialFilename, initialVersion);

            FondDeDossierFile currentFile = getCurrentFile(session, fondDossierFileList);

            // update document name
            String filenameUpdate1 = "update_element_fils";
            String versionUpdate1 = "2";

            DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
            FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
            newFichier.setTitle(filenameUpdate1);
            newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);
            newFichier.setContent(new StringBlob("update_element_fils"));
            newFichier.setFilename("update_element_fils");

            // save update
            fondDeDossierService.updateFddFile(
                session,
                currentFile.getDocument(),
                newFichierDoc,
                dossier.getDocument()
            );

            // Check again with update

            fondDossierFileList =
                (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, repertoireDoc);
            checkCurrentFileAndLastVersion(session, fondDossierFileList, filenameUpdate1, versionUpdate1);

            currentFile = getCurrentFile(session, fondDossierFileList);

            // update document name again
            String filenameUpdate2 = "update_element_fils_twice";
            String versionUpdate2 = "3";

            newFichier.setFilename(filenameUpdate2);

            // save update
            fondDeDossierService.updateFddFile(
                session,
                currentFile.getDocument(),
                newFichierDoc,
                dossier.getDocument()
            );
            // Check again with update
            fondDossierFileList =
                (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, repertoireDoc);
            checkCurrentFileAndLastVersion(session, fondDossierFileList, filenameUpdate2, versionUpdate2);

            currentFile = getCurrentFile(session, fondDossierFileList);

            // On vérifie le nombre de versions
            List<DocumentModel> versionList = session.getVersions(currentFile.getDocument().getRef());
            Assert.assertEquals(3, versionList.size());

            // retrieve last version
            log.info(currentFile.getId());
            for (DocumentModel dm : session.getVersions(new IdRef(currentFile.getId()))) {
                log.info(
                    dm.getId() +
                    " " +
                    dm.getPropertyValue(VersioningService.MAJOR_VERSION_PROP) +
                    "-->" +
                    dm.getAdapter(FondDeDossierFile.class).getName() +
                    " / " +
                    DublincoreSchemaUtils.getTitle(dm)
                );
            }
            session.save();

            // ************************************ Retour en arrière progressif dans les versions

            // On revient à la version précédente
            fondDeDossierService.restoreToPreviousVersion(session, currentFile.getId(), dossier.getDocument());

            // Check again with update
            fondDossierFileList =
                (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, repertoireDoc);
            checkCurrentFileAndLastVersion(session, fondDossierFileList, filenameUpdate1, versionUpdate1);
            currentFile = getCurrentFile(session, fondDossierFileList);

            // retrieve last version again
            fondDeDossierService.restoreToPreviousVersion(session, currentFile.getId(), dossier.getDocument());
            // Check again with update
            fondDossierFileList =
                (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, repertoireDoc);
            checkCurrentFileAndLastVersion(session, fondDossierFileList, initialFilename, initialVersion);
        }
    }

    private void checkCurrentFileAndLastVersion(
        CoreSession session,
        List<FondDeDossierFile> childrenFile,
        String expectedFilename,
        String expectedVersion
    ) {
        Assert.assertNotNull(childrenFile);
        Assert.assertEquals(1, childrenFile.size());

        // On vérifie que le fichier en cours a bien été mis à jour
        FondDeDossierFile currentFile = childrenFile.get(0);
        DocumentModel lastVersionDoc = session.getLastDocumentVersion(new IdRef(currentFile.getId()));

        // On vérifie que le fichier en cours a bien été mis à jour
        Assert.assertEquals(expectedFilename, currentFile.getFilename());
        Assert.assertEquals(expectedVersion, currentFile.getNumeroVersion());
        Assert.assertEquals(new Long(expectedVersion), currentFile.getMajorVersion());
        Assert.assertNotNull(currentFile.getId());

        // Check de la récup par id doc
        String elementFddId = currentFile.getId();
        DocumentModel elementFddDoc = session.getDocument(new IdRef(elementFddId));
        Assert.assertNotNull(elementFddDoc);
        Assert.assertEquals(
            new Long(expectedVersion),
            elementFddDoc.getPropertyValue(VersioningService.MAJOR_VERSION_PROP)
        );
        Assert.assertEquals(
            expectedVersion,
            elementFddDoc.getProperty(
                DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ELEMENT_NUMERO_VERSION
            )
        );
        Assert.assertEquals(expectedFilename, elementFddDoc.getAdapter(FondDeDossierFile.class).getName());

        // On vérifie que la version a bien les même infos que le fichier en cours
        Assert.assertNotNull(lastVersionDoc);
        Assert.assertEquals(
            new Long(expectedVersion),
            lastVersionDoc.getPropertyValue(VersioningService.MAJOR_VERSION_PROP)
        );
        Assert.assertEquals(expectedFilename, lastVersionDoc.getAdapter(FondDeDossierFile.class).getName());
        Assert.assertEquals(
            expectedVersion,
            lastVersionDoc.getProperty(
                DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ELEMENT_NUMERO_VERSION
            )
        );
        Assert.assertNotNull(lastVersionDoc.getId());
    }

    private FondDeDossierFile getCurrentFile(CoreSession session, List<FondDeDossierFile> childrenFile) {
        DocumentModel elementFddDoc = session.getDocument(new IdRef(childrenFile.get(0).getId()));
        Assert.assertNotNull(elementFddDoc);
        return elementFddDoc.getAdapter(FondDeDossierFile.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateFddFile() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("begin : test dossier creation repertoire + element fond de dossier ");

            // create fond de dossier and
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);

            fondDeDossier = dossier.getFondDeDossier(session);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );

            // check service : creation fichier with repertoire parent
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "creation_element_fils",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // check new element fond de dossier node properties
            List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                fondDossierNodeList.get(0).getDocument()
            );
            Assert.assertNotNull(repertoireNodeList);
            Assert.assertEquals(1, repertoireNodeList.size());
            Assert.assertEquals("creation_element_fils", repertoireNodeList.get(0).getTitle());
            Assert.assertNotNull(repertoireNodeList.get(0).getId());

            DocumentModel elementFddDoc = session.getDocument(new IdRef(repertoireNodeList.get(0).getId()));
            Assert.assertNotNull(elementFddDoc);

            DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
            FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
            newFichier.setTitle("update_element_fils");
            newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);
            newFichier.setContent(new StringBlob("update_element_fils"));
            newFichier.setFilename("update_element_fils");

            // update
            fondDeDossierService.updateFddFile(session, elementFddDoc, newFichierDoc, dossier.getDocument());

            String elementFddId = null;
            List<FondDeDossierFile> repertoireNodeListUpdated = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                fondDossierNodeList.get(0).getDocument()
            );
            Assert.assertNotNull(repertoireNodeListUpdated);
            Assert.assertEquals(1, repertoireNodeListUpdated.size());
            Assert.assertEquals("update_element_fils", repertoireNodeListUpdated.get(0).getTitle());
            Assert.assertNotNull(repertoireNodeListUpdated.get(0).getId());
            Assert.assertEquals("2", repertoireNodeListUpdated.get(0).getNumeroVersion());
            elementFddId = repertoireNodeListUpdated.get(0).getId();
            elementFddDoc = session.getDocument(new IdRef(elementFddId));
            Assert.assertNotNull(elementFddDoc);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateFddFileMoveFileRepository() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que la mise à jour et le déplacement dans un autre répertoire d'un ficheri fond de dossier
            // fonctionne
            log.info("begin : test update file fdd + move file fdd in another repository ");

            // get fond de dossier
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );
            // Assert.assertEquals(fondDossierNodeList.size(), 3);
            DocumentModel repertoireParlementDoc = fondDossierNodeList.get(0).getDocument();
            DocumentModel repertoireMinistereSggDoc = fondDossierNodeList.get(1).getDocument();

            // check service : creation fichier with repertoire parent
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "creation_element_fils",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // check new element fond de dossier node properties
            List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireParlementDoc
            );
            Assert.assertNotNull(repertoireNodeList);
            Assert.assertEquals(1, repertoireNodeList.size());
            Assert.assertEquals("creation_element_fils", repertoireNodeList.get(0).getFilename());
            Assert.assertNotNull(repertoireNodeList.get(0).getId());
            // get Document
            DocumentModel elementFddDoc = session.getDocument(new IdRef(repertoireNodeList.get(0).getId()));
            Assert.assertEquals(elementFddDoc.getParentRef(), repertoireParlementDoc.getRef());
            Assert.assertNotNull(elementFddDoc);

            DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
            FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
            newFichier.setTitle("update_element_fils");
            newFichier.setNiveauVisibilite(
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL
            );
            newFichier.setContent(new StringBlob("update_element_fils"));
            newFichier.setFilename("update_element_fils");

            // save update
            fondDeDossierService.updateFddFile(session, elementFddDoc, newFichierDoc, dossier.getDocument());

            String elementFddId = null;
            List<FondDeDossierFile> repertoireNodeListUpdated = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireMinistereSggDoc
            );
            Assert.assertNotNull(repertoireNodeListUpdated);
            Assert.assertEquals(1, repertoireNodeListUpdated.size());
            Assert.assertEquals("update_element_fils", repertoireNodeListUpdated.get(0).getTitle());
            Assert.assertNotNull(repertoireNodeListUpdated.get(0).getId());
            Assert.assertEquals("2", repertoireNodeListUpdated.get(0).getNumeroVersion());
            elementFddId = repertoireNodeListUpdated.get(0).getId();
            elementFddDoc = session.getDocument(new IdRef(elementFddId));
            Assert.assertEquals(elementFddDoc.getParentRef(), repertoireMinistereSggDoc.getRef());
            Assert.assertNotNull(elementFddDoc);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateFddFileMoveFileAndVersionRepository() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que la mise à jour et le déplacement dans un autre répertoire d'un ficheri fond de dossier
            // fonctionne
            log.info("begin : test update file fdd + move file fdd in another repository ");

            // create fond de dossier
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check new repertoire node properties
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );
            DocumentModel repertoireParlementDoc = fondDossierNodeList.get(0).getDocument();
            DocumentModel repertoireMinistereSggDoc = fondDossierNodeList.get(1).getDocument();

            // check service : creation fichier with repertoire parent
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "creation_element_fils",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // check new element fond de dossier node properties
            List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireParlementDoc
            );
            Assert.assertNotNull(repertoireNodeList);
            Assert.assertEquals(1, repertoireNodeList.size());
            Assert.assertNotNull(repertoireNodeList.get(0).getId());
            // get Document
            DocumentModel elementFddDoc = session.getDocument(new IdRef(repertoireNodeList.get(0).getId()));
            Assert.assertEquals(elementFddDoc.getParentRef(), repertoireParlementDoc.getRef());
            Assert.assertNotNull(elementFddDoc);

            DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
            FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
            newFichier.setTitle("update_element_fils");
            newFichier.setNiveauVisibilite(
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL
            );
            newFichier.setContent(new StringBlob("update_element_fils"));
            newFichier.setFilename("update_element_fils");

            // save update
            fondDeDossierService.updateFddFile(session, elementFddDoc, newFichierDoc, dossier.getDocument());

            String elementFddId = null;
            List<FondDeDossierFile> repertoireNodeListUpdated = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireMinistereSggDoc
            );
            Assert.assertNotNull(repertoireNodeListUpdated);
            Assert.assertEquals(1, repertoireNodeListUpdated.size());
            Assert.assertEquals("update_element_fils", repertoireNodeListUpdated.get(0).getTitle());
            Assert.assertNotNull(repertoireNodeListUpdated.get(0).getId());
            Assert.assertEquals("2", repertoireNodeListUpdated.get(0).getNumeroVersion());
            elementFddId = repertoireNodeListUpdated.get(0).getId();
            elementFddDoc = session.getDocument(new IdRef(elementFddId));
            // check repository
            Assert.assertEquals(elementFddDoc.getParentRef(), repertoireMinistereSggDoc.getRef());
            Assert.assertNotNull(elementFddDoc);

            // update document name
            newFichier.setFilename("update_element_fils_2");
            newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

            // save update
            fondDeDossierService.updateFddFile(session, elementFddDoc, newFichierDoc, dossier.getDocument());

            repertoireNodeListUpdated =
                (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, repertoireParlementDoc);
            Assert.assertNotNull(repertoireNodeListUpdated);
            Assert.assertEquals(1, repertoireNodeListUpdated.size());
            Assert.assertEquals("update_element_fils_2", repertoireNodeListUpdated.get(0).getTitle());
            Assert.assertNotNull(repertoireNodeListUpdated.get(0).getId());
            Assert.assertEquals("3", repertoireNodeListUpdated.get(0).getNumeroVersion());
            elementFddId = repertoireNodeListUpdated.get(0).getId();
            elementFddDoc = session.getDocument(new IdRef(elementFddId));
            // check repository
            Assert.assertEquals(elementFddDoc.getParentRef(), repertoireParlementDoc.getRef());
            Assert.assertNotNull(elementFddDoc);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRepertoireChildrenDelete() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("begin : test dossier creation repertoire + element fond de dossier ");

            // create fond de dossier and
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);

            fondDeDossier = dossier.getFondDeDossier(session);

            // check fond Dossier properties
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

            // check service : creation fdd child
            fondDeDossierService.createFondDeDossierRepertoire(
                session,
                fondDeDossierDoc,
                "creation répertoire Fdd test"
            );
            session.save();

            // check new repertoire node properties
            List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );

            // check service : creation fichier with repertoire parent
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "creation_element_fils",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );
            // check service : creation fichier with repertoire parent
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "creation_element_fils2",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // check new element fond de dossier node properties
            DocumentModel repertoireParent = fondDossierNodeList.get(0).getDocument();

            List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireParent
            );
            Assert.assertNotNull(repertoireNodeList);
            Assert.assertEquals(repertoireNodeList.size(), 2);

            // delete repertoire children
            fondDeDossierService.delete(fondDossierNodeList.get(0).getId(), session, dossier.getDocument());

            // check repertoire children removal

            List<FondDeDossierFile> repertoireNodeListDeleted = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireParent
            );
            Assert.assertNotNull(repertoireNodeListDeleted);
            Assert.assertEquals(repertoireNodeListDeleted.size(), 0);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetFondDeDossierPublicDocument() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            log.info("begin : test GetFondDeDossierPublicDocument");

            // Récupère le fond de dossier
            Dossier dossier = getDossier(session);
            FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
            DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();
            Assert.assertNotNull(fondDeDossierDoc);

            // Crée le répertoire parlement de niveau 1
            List<FondDeDossierFolder> fondDossierRepertoireParlementNodeList = (List<FondDeDossierFolder>) fondDeDossierService.getChildrenFolder(
                session,
                fondDeDossierDoc
            );
            Assert.assertNotNull(fondDossierRepertoireParlementNodeList);
            FondDeDossierFolder repertoireParlement = fondDossierRepertoireParlementNodeList.get(0);
            Assert.assertEquals(
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME,
                repertoireParlement.getName()
            );

            // Crée un document dans le réperoire parlement
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "nomFichier",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // Récupère l'ID du document parlement
            List<FondDeDossierFile> fichierParlementList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                session,
                repertoireParlement.getDocument()
            );
            Assert.assertNotNull(fichierParlementList);
            Assert.assertEquals(1, fichierParlementList.size());
            FondDeDossierFile documentParlementNode = fichierParlementList.get(0);
            Assert.assertNotNull(documentParlementNode.getId());
            String documentParlementId = documentParlementNode.getId();

            // Récupère le répertoire public de niveau 1
            List<FondDeDossierFile> fondDossierRepertoirePublicNodeList = fondDeDossierService.getFondDeDossierPublicDocument(
                session,
                fondDeDossierDoc
            );
            Assert.assertNotNull(fondDossierRepertoirePublicNodeList);
            Assert.assertEquals(1, fondDossierRepertoirePublicNodeList.size());
            FondDeDossierFile fondDeDossierRepertoirePublicFile = fondDossierRepertoirePublicNodeList.get(0);
            Assert.assertEquals(fondDeDossierRepertoirePublicFile.getId(), documentParlementId);

            // Ajout d'un fichier au répertoire ministère
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "nomFichier2",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL,
                new StringBlob("test")
            );

            // Vérifie que le document parlement n'a pas été ajouté au répertoire public
            fondDossierRepertoirePublicNodeList =
                fondDeDossierService.getFondDeDossierPublicDocument(session, fondDeDossierDoc);
            Assert.assertNotNull(fondDossierRepertoirePublicNodeList);
            Assert.assertEquals(1, fondDossierRepertoirePublicNodeList.size());
            Assert.assertEquals(fondDeDossierRepertoirePublicFile.getId(), documentParlementId);

            // Ajoute un document au répertoire parlement
            fondDeDossierService.createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                "nomFichier3",
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
                new StringBlob("test")
            );

            // Récupère les documents du répertoire parlement
            fichierParlementList =
                (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
                    session,
                    repertoireParlement.getDocument()
                );
            Assert.assertNotNull(fichierParlementList);
            // ****************************ERREUR
            Assert.assertEquals(2, fichierParlementList.size());
            Assert.assertNotNull(documentParlementNode.getId());
            documentParlementId = documentParlementNode.getId();

            // call method
            fondDossierRepertoirePublicNodeList =
                fondDeDossierService.getFondDeDossierPublicDocument(session, fondDeDossierDoc);
            // we don't have new file
            Assert.assertNotNull(fondDossierRepertoirePublicNodeList);
            Assert.assertEquals(2, fondDossierRepertoirePublicNodeList.size());
            Assert.assertEquals(fondDeDossierRepertoirePublicFile.getId(), documentParlementId);
        }
    }

    private void assertVersion(String expected, DocumentModel doc) {
        Assert.assertEquals(expected, getMajor(doc) + "." + getMinor(doc));
    }

    private long getMajor(DocumentModel doc) {
        return ((Long) doc.getPropertyValue(VersioningService.MAJOR_VERSION_PROP)).longValue();
    }

    private long getMinor(DocumentModel doc) {
        return ((Long) doc.getPropertyValue(VersioningService.MINOR_VERSION_PROP)).longValue();
    }

    private Dossier getDossier(CoreSession session) {
        return session.getDocument(dossierRef).getAdapter(Dossier.class);
    }
}
