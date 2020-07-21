package fr.dila.reponses.core.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.schema.FacetNames;
import org.nuxeo.ecm.core.versioning.VersioningService;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;

/**
 * @author arolin
 */
@SuppressWarnings("unchecked")
public class TestFondDeDossierService extends ReponsesRepositoryTestCase {

	private static final Log		log	= LogFactory.getLog(TestFondDeDossierService.class);

	private DocumentModel			docModel;

	private Dossier					dossier;

	private FondDeDossierService	fondDeDossierService;

	@Override
	public void setUp() throws Exception {

		super.setUp();
		// import bundle for versioning service of element fdd
		// deployBundle("org.nuxeo.ecm.platform.versioning.api");
		// deployBundle("org.nuxeo.ecm.platform.versioning");

		// import Fdd service
		fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
		assertNotNull(fondDeDossierService);

		openSession();
	
		createDocumentModel(session, "trash-root", "TrashRoot", "/case-management");
		
		createOrGetFeuilleRouteModelFolder(session);

		DocumentModel questionDocumentModel = createDocument(DossierConstants.QUESTION_DOCUMENT_TYPE, "newQuestionTest");
		Question question = questionDocumentModel.getAdapter(Question.class);

		// Crée un dossier avec un fond de dossier et les 3 répertoires de niveau 1
		docModel = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "newDossierTest");
		dossier = docModel.getAdapter(Dossier.class);
		dossier = getDossierDistributionService().createDossier(session, dossier, question, null,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);

		// Démarre la feuille de route associée au dossier
		getDossierDistributionService().startDefaultRoute(session, dossier);

		session.save();
		closeSession();
	}

	public void testDefaultRepositoryCreationInDossier() throws PropertyException, ClientException {
		openSession();
		log.info("begin : test dossier default repository creation ");

		// get fond de dossier
		FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
		assertNotNull(fondDeDossier);
		assertNotNull(fondDeDossier.getDocument());
		assertNotNull(fondDeDossier.getDocument().getId());

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);
		assertNotNull(fondDossierNodeList);
		// assertEquals(fondDossierNodeList.size(), 3);
		assertEquals(fondDossierNodeList.get(0).getName(),
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME);
		assertEquals(fondDossierNodeList.get(1).getName(),
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME);
		assertEquals(fondDossierNodeList.get(2).getName(),
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_NAME);

		assertEquals(fondDeDossier.getRepertoireParlementId(), fondDossierNodeList.get(0).getId());
		assertEquals(fondDeDossier.getRepertoireSggId(), fondDossierNodeList.get(1).getId());
		assertEquals(fondDeDossier.getRepertoireMinistereId(), fondDossierNodeList.get(2).getId());

		closeSession();
	}

	public void testCreateBasicRepertoire() throws PropertyException, ClientException {
		openSession();
		log.info("begin : test dossier creation repertoire ");

		// getFondDeDossier
		FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
		assertNotNull(fondDeDossier);
		assertNotNull(fondDeDossier.getDocument());
		assertNotNull(fondDeDossier.getDocument().getId());

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();
		assertTrue(fondDeDossierDoc.hasFacet(FacetNames.FOLDERISH));
		assertTrue(fondDeDossierDoc.hasSchema("fondDossier"));

		// check creation service
		fondDeDossierService.createFondDeDossierRepertoire(session, fondDeDossier.getDocument(),
				"creation répertoire Fdd test");
		session.save();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);
		assertNotNull(fondDossierNodeList);
		// the dossier has already 3 child so if the creation work, he must have 4 child
		assertEquals(fondDossierNodeList.size(), 4);
		assertEquals(fondDossierNodeList.get(3).getName(), "creation répertoire Fdd test");
		assertEquals(fondDossierNodeList.get(3).getDocument().getType(),
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE);
		closeSession();
	}

	public void testcreateEmptyFondDeDossierFichierDocModel() throws PropertyException, ClientException {
		openSession();
		log.info("begin : test createEmptyFondDeDossierFichierDocModel method ");

		// call method
		DocumentModel emptyFddFileDocModel = fondDeDossierService.createBareFondDeDossierFichier(session);
		assertNotNull(emptyFddFileDocModel);
		// check schema and Facet
		assertTrue(emptyFddFileDocModel.hasSchema(STSchemaConstant.FILE_SCHEMA));
		assertTrue(emptyFddFileDocModel.hasSchema(STSchemaConstant.UID_SCHEMA));
		assertTrue(emptyFddFileDocModel.hasSchema(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA));
		assertTrue(emptyFddFileDocModel.hasSchema(STSchemaConstant.DUBLINCORE_SCHEMA));
		assertTrue(emptyFddFileDocModel.hasFacet("Versionable"));

		closeSession();

	}

	public void testCreatefddFile() throws PropertyException, ClientException {
		openSession();
		log.info("begin : test dossier creation fdd File method ");

		// get fond de dossier
		FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
		assertNotNull(fondDeDossier);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);
		assertNotNull(fondDossierNodeList);
		// assertEquals(fondDossierNodeList.size(), 3);
		assertEquals(fondDossierNodeList.get(0).getName(),
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME);

		// init fichierFdd
		// check service : creation fichier with repertoire parent
		DocumentModel repParent = fondDossierNodeList.get(0).getDocument();
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "nomFichier",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL, null);

		// check new repertoire node properties
		List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, repParent);
		assertNotNull(repertoireNodeList);
		assertEquals(repertoireNodeList.size(), 1);

		closeSession();
	}

	public void testGetDefaultNiveauVisibiliteFromRepository() throws PropertyException, ClientException {
		openSession();
		log.info("begin : test dossier creation fdd File method ");

		// create fond de dossier
		FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);

		fondDeDossier = dossier.getFondDeDossier(session);
		assertNotNull(fondDeDossier);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);
		assertNotNull(fondDossierNodeList);
		// assertEquals(fondDossierNodeList.size(), 3);
		assertEquals(fondDossierNodeList.get(1).getName(),
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME);

		String nivVisibilite = null;
		String idRepertoire = fondDossierNodeList.get(1).getId();
		nivVisibilite = fondDeDossierService.getDefaultNiveauVisibiliteFromRepository(idRepertoire, session);
		assertNotNull(nivVisibilite);
		assertEquals(nivVisibilite, ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL);
		closeSession();
	}

	public void testMultipleChildCreation() throws PropertyException, ClientException {
		openSession();
		// on verifie que les différents objet
		log.info("begin : test dossier creation repertoire + fichier ");

		// create fond de dossier and
		FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
		assertNotNull(fondDeDossier);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);
		assertNotNull(fondDossierNodeList);
		// assertEquals(fondDossierNodeList.size(), 3);
		assertEquals(fondDossierNodeList.get(0).getName(),
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME);

		// check service : creation fichier with repertoire parent
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "creation répertoire child",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// check new repertoire node properties
		List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, fondDossierNodeList.get(0).getDocument());
		assertNotNull(repertoireNodeList);
		assertEquals(1, repertoireNodeList.size());
		assertEquals("creation répertoire child", repertoireNodeList.get(0).getFilename());

		closeSession();
	}

	public void testElementFddCreation() throws PropertyException, ClientException {
		openSession();
		// on verifie que les différents objet
		log.info("begin : test dossier creation repertoire + element fond de dossier ");

		// create fond de dossier and
		FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
		assertNotNull(fondDeDossier);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);

		// check service : creation fichier with repertoire parent
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "creation_element_fils",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// check new element fond de dossier node properties
		List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, fondDossierNodeList.get(0).getDocument());
		assertNotNull(repertoireNodeList);
		assertEquals(1, repertoireNodeList.size());
		assertEquals("creation_element_fils", repertoireNodeList.get(0).getFilename());
		assertNotNull(repertoireNodeList.get(0).getId());
		assertEquals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
				repertoireNodeList.get(0).getNiveauVisibilite());
		assertEquals("1", repertoireNodeList.get(0).getNumeroVersion());

		DocumentModel elementFddDoc = session.getDocument(new IdRef(repertoireNodeList.get(0).getId()));
		assertNotNull(elementFddDoc);
		closeSession();
	}

	public void testDeleteElementFdd() throws PropertyException, ClientException {
		openSession();
		// on verifie que les différents objet
		log.info("begin : test delete an fdd file document ");

		// create fond de dossier and
		FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);
		fondDeDossier = dossier.getFondDeDossier(session);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check service : creation fdd child
		fondDeDossierService.createFondDeDossierRepertoire(session, fondDeDossier.getDocument(),
				"creation répertoire Fdd test");
		session.save();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);

		// check service : creation fichier with repertoire parent
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "creation_element_fils",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// check new element fond de dossier node properties
		List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, fondDossierNodeList.get(0).getDocument());
		assertNotNull(repertoireNodeList);
		assertEquals(1, repertoireNodeList.size());
		assertEquals("creation_element_fils", repertoireNodeList.get(0).getFilename());
		assertNotNull(repertoireNodeList.get(0).getId());

		String idFddDoc = repertoireNodeList.get(0).getId();
		DocumentModel elementFddDoc = session.getDocument(new IdRef(idFddDoc));
		assertNotNull(elementFddDoc);

		// delete element fils
		fondDeDossierService.delete(idFddDoc, session, dossier.getDocument());

		// check element fils removal
		List<FondDeDossierFile> repertoireNodeListDeleted = (List<FondDeDossierFile>) fondDeDossierService
				.getChildrenFile(session, fondDossierNodeList.get(0).getDocument());
		assertNotNull(repertoireNodeListDeleted);
		assertEquals(repertoireNodeListDeleted.size(), 0);

		closeSession();
	}

	public void testDeleteElementFddWithVersion() throws PropertyException, ClientException {
		openSession();

		// on verifie que les différents objet
		log.info("begin : test delete an fdd file document with versions ");

		// create fond de dossier and
		FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);
		fondDeDossier = dossier.getFondDeDossier(session);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);

		// check service : creation fichier with repertoire parent
		DocumentModel fichierFdd = fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc,
				"creation_element_fils", ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL,
				null);

		// check new element fond de dossier node properties
		List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, fondDossierNodeList.get(0).getDocument());
		assertNotNull(repertoireNodeList);
		assertEquals(1, repertoireNodeList.size());
		assertEquals("creation_element_fils", repertoireNodeList.get(0).getFilename());
		assertNotNull(repertoireNodeList.get(0).getId());

		DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
		FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
		newFichier.setFilename("update_element_fils");
		newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// save update
		fondDeDossierService.updateFddFile(session, fichierFdd, newFichierDoc, dossier.getDocument());

		DocumentModel repertoireDoc = fondDossierNodeList.get(0).getDocument();
		String elementFddId = null;
		List<FondDeDossierFile> repertoireNodeListUpdated = (List<FondDeDossierFile>) fondDeDossierService
				.getChildrenFile(session, repertoireDoc);
		assertNotNull(repertoireNodeListUpdated);
		assertEquals(1, repertoireNodeListUpdated.size());
		assertEquals("update_element_fils", repertoireNodeListUpdated.get(0).getFilename());
		assertNotNull(repertoireNodeListUpdated.get(0).getId());
		assertEquals("2", repertoireNodeListUpdated.get(0).getNumeroVersion());
		elementFddId = repertoireNodeListUpdated.get(0).getId();
		DocumentModel elementFddDoc = session.getDocument(new IdRef(elementFddId));
		assertNotNull(elementFddDoc);

		// delete element fils
		fondDeDossierService.delete(elementFddId, session, dossier.getDocument());

		// check element fils removal
		List<FondDeDossierFile> repertoireNodeListDeleted = (List<FondDeDossierFile>) fondDeDossierService
				.getChildrenFile(session, repertoireDoc);
		assertNotNull(repertoireNodeListDeleted);
		assertEquals(0, repertoireNodeListDeleted.size());

		closeSession();
	}

	public void testVersionManipulation() throws ClientException {
		openSession();

		DocumentModel doc = session.createDocumentModel("/", "testfile1",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE);

		doc = session.createDocument(doc);
		DublincoreSchemaUtils.setTitle(doc, "A");

		doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
		// doc.setPropertyValue("uid:major_version", new Long(1));

		doc = session.saveDocument(doc);
		// DocumentRef docRef = session.checkIn(doc.getRef(), VersioningOption.MAJOR, "");
		DocumentRef docRef = doc.getRef();
		// assertTrue(doc.isCheckedOut());
		assertVersion("1.0", doc);

		log.debug("LIFECYCLE : " + doc.getLifeCyclePolicy());
		String lifecyclestate = session.getCurrentLifeCycleState(doc.getRef());
		log.debug("LIFECYCLESTATE : " + lifecyclestate);

		// save with no option, use default
		DublincoreSchemaUtils.setTitle(doc, "B");
		doc = session.saveDocument(doc);
		assertTrue(doc.isCheckedOut());
		assertVersion("1.0", doc);

		// change and save with new minor
		DublincoreSchemaUtils.setTitle(doc, "C");
		doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MINOR);
		doc = session.saveDocument(doc);
		assertFalse(doc.isCheckedOut());
		assertVersion("1.1", doc);

		// DocumentModel v01 = session.getLastDocumentVersion(docRef);
		// assertEquals(v01.getId(), session.getBaseVersion(docRef).reference());

		List<DocumentModel> dml = session.getVersions(docRef);
		for (DocumentModel dm : dml) {
			log.info("doc: " + getMajor(dm) + "." + getMinor(dm) + "-->" + DublincoreSchemaUtils.getTitle(dm));
		}

		closeSession();
	}

	public void testRestoreToLastFddFileVersion() throws PropertyException, ClientException, Exception {
		openSession();
		log.info("begin : test dossier creation repertoire + element fond de dossier ");

		// create fond de dossier and
		FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);

		fondDeDossier = dossier.getFondDeDossier(session);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);

		// check service : creation fichier with repertoire parent
		String initialFilename = "creation_element_fils";
		String initialVersion = "1";
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, initialFilename,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL, null);

		// check new element fond de dossier node properties
		DocumentModel repertoireDoc = fondDossierNodeList.get(0).getDocument();

		List<FondDeDossierFile> fondDossierFileList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, repertoireDoc);
		checkCurrentFileAndLastVersion(fondDossierFileList, initialFilename, initialVersion);

		FondDeDossierFile currentFile = getCurrentFile(fondDossierFileList);

		// update document name
		String filenameUpdate1 = "update_element_fils";
		String versionUpdate1 = "2";

		DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
		FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
		newFichier.setFilename(filenameUpdate1);
		newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// save update
		fondDeDossierService.updateFddFile(session, currentFile.getDocument(), newFichierDoc, dossier.getDocument());

		// Check again with update
		fondDossierFileList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, repertoireDoc);
		checkCurrentFileAndLastVersion(fondDossierFileList, filenameUpdate1, versionUpdate1);

		currentFile = getCurrentFile(fondDossierFileList);

		// update document name again
		String filenameUpdate2 = "update_element_fils_twice";
		String versionUpdate2 = "3";

		newFichier.setFilename(filenameUpdate2);

		// save update
		fondDeDossierService.updateFddFile(session, currentFile.getDocument(), newFichierDoc, dossier.getDocument());
		// Check again with update
		fondDossierFileList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, repertoireDoc);
		checkCurrentFileAndLastVersion(fondDossierFileList, filenameUpdate2, versionUpdate2);

		currentFile = getCurrentFile(fondDossierFileList);

		// On vérifie le nombre de versions
		List<DocumentModel> versionList = session.getVersions(currentFile.getDocument().getRef());
		assertEquals(3, versionList.size());

		// retrieve last version
		log.info(currentFile.getId());
		for (DocumentModel dm : session.getVersions(new IdRef(currentFile.getId()))) {
			log.info(dm.getId() + " " + dm.getPropertyValue(VersioningService.MAJOR_VERSION_PROP) + "-->"
					+ dm.getProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_FILENAME_PROPERTY) + " / "
					+ DublincoreSchemaUtils.getTitle(dm));
		}
		session.save();

		// ************************************ Retour en arrière progressif dans les versions

		// On revient à la version précédente
		fondDeDossierService.restoreToPreviousVersion(session, currentFile.getId(), dossier.getDocument());

		// Check again with update
		fondDossierFileList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, repertoireDoc);
		checkCurrentFileAndLastVersion(fondDossierFileList, filenameUpdate1, versionUpdate1);
		currentFile = getCurrentFile(fondDossierFileList);

		// retrieve last version again
		fondDeDossierService.restoreToPreviousVersion(session, currentFile.getId(), dossier.getDocument());
		// Check again with update
		fondDossierFileList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, repertoireDoc);
		checkCurrentFileAndLastVersion(fondDossierFileList, initialFilename, initialVersion);

		closeSession();
	}

	private void checkCurrentFileAndLastVersion(List<FondDeDossierFile> childrenFile, String expectedFilename,
			String expectedVersion) throws ClientException {
		assertNotNull(childrenFile);
		assertEquals(1, childrenFile.size());

		// On vérifie que le fichier en cours a bien été mis à jour
		FondDeDossierFile currentFile = childrenFile.get(0);
		DocumentModel lastVersionDoc = session.getLastDocumentVersion(new IdRef(currentFile.getId()));

		// On vérifie que le fichier en cours a bien été mis à jour
		assertEquals(expectedFilename, currentFile.getFilename());
		assertEquals(expectedVersion, currentFile.getNumeroVersion());
		assertEquals(new Long(expectedVersion), currentFile.getMajorVersion());
		assertNotNull(currentFile.getId());

		// Check de la récup par id doc
		String elementFddId = currentFile.getId();
		DocumentModel elementFddDoc = session.getDocument(new IdRef(elementFddId));
		assertNotNull(elementFddDoc);
		assertEquals(new Long(expectedVersion), elementFddDoc.getPropertyValue(VersioningService.MAJOR_VERSION_PROP));
		assertEquals(expectedVersion, elementFddDoc.getProperty(
				DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ELEMENT_NUMERO_VERSION));
		assertEquals(expectedFilename,
				elementFddDoc.getProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_FILENAME_PROPERTY));

		// On vérifie que la version a bien les même infos que le fichier en cours
		assertNotNull(lastVersionDoc);
		assertEquals(new Long(expectedVersion), lastVersionDoc.getPropertyValue(VersioningService.MAJOR_VERSION_PROP));
		assertEquals(expectedFilename,
				lastVersionDoc.getProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_FILENAME_PROPERTY));
		assertEquals(expectedVersion, (String) lastVersionDoc.getProperty(
				DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ELEMENT_NUMERO_VERSION));
		assertNotNull(lastVersionDoc.getId());
	}

	private FondDeDossierFile getCurrentFile(List<FondDeDossierFile> childrenFile) throws ClientException {
		DocumentModel elementFddDoc = session.getDocument(new IdRef(childrenFile.get(0).getId()));
		assertNotNull(elementFddDoc);
		return elementFddDoc.getAdapter(FondDeDossierFile.class);
	}

	public void testUpdateFddFile() throws PropertyException, ClientException {
		openSession();
		// on verifie que les différents objet
		log.info("begin : test dossier creation repertoire + element fond de dossier ");

		// create fond de dossier and
		FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);

		fondDeDossier = dossier.getFondDeDossier(session);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);

		// check service : creation fichier with repertoire parent
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "creation_element_fils",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// check new element fond de dossier node properties
		List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, fondDossierNodeList.get(0).getDocument());
		assertNotNull(repertoireNodeList);
		assertEquals(1, repertoireNodeList.size());
		assertEquals("creation_element_fils", repertoireNodeList.get(0).getFilename());
		assertNotNull(repertoireNodeList.get(0).getId());

		DocumentModel elementFddDoc = session.getDocument(new IdRef(repertoireNodeList.get(0).getId()));
		assertNotNull(elementFddDoc);

		DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
		FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
		newFichier.setFilename("update_element_fils");
		newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// update
		fondDeDossierService.updateFddFile(session, elementFddDoc, newFichierDoc, dossier.getDocument());

		String elementFddId = null;
		List<FondDeDossierFile> repertoireNodeListUpdated = (List<FondDeDossierFile>) fondDeDossierService
				.getChildrenFile(session, fondDossierNodeList.get(0).getDocument());
		assertNotNull(repertoireNodeListUpdated);
		assertEquals(1, repertoireNodeListUpdated.size());
		assertEquals("update_element_fils", repertoireNodeListUpdated.get(0).getFilename());
		assertNotNull(repertoireNodeListUpdated.get(0).getId());
		assertEquals("2", repertoireNodeListUpdated.get(0).getNumeroVersion());
		elementFddId = repertoireNodeListUpdated.get(0).getId();
		elementFddDoc = session.getDocument(new IdRef(elementFddId));
		assertNotNull(elementFddDoc);

		closeSession();
	}

	public void testUpdateFddFileMoveFileRepository() throws PropertyException, ClientException {
		openSession();

		// on verifie que la mise à jour et le déplacement dans un autre répertoire d'un ficheri fond de dossier
		// fonctionne
		log.info("begin : test update file fdd + move file fdd in another repository ");

		// get fond de dossier
		FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);
		// assertEquals(fondDossierNodeList.size(), 3);
		DocumentModel repertoireParlementDoc = fondDossierNodeList.get(0).getDocument();
		DocumentModel repertoireMinistereSggDoc = fondDossierNodeList.get(1).getDocument();

		// check service : creation fichier with repertoire parent
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "creation_element_fils",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// check new element fond de dossier node properties
		List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, repertoireParlementDoc);
		assertNotNull(repertoireNodeList);
		assertEquals(1, repertoireNodeList.size());
		assertEquals("creation_element_fils", repertoireNodeList.get(0).getFilename());
		assertNotNull(repertoireNodeList.get(0).getId());
		// get Document
		DocumentModel elementFddDoc = session.getDocument(new IdRef(repertoireNodeList.get(0).getId()));
		assertEquals(elementFddDoc.getParentRef(), repertoireParlementDoc.getRef());
		assertNotNull(elementFddDoc);

		DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
		FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
		newFichier.setFilename("update_element_fils");
		newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL);

		// save update
		fondDeDossierService.updateFddFile(session, elementFddDoc, newFichierDoc, dossier.getDocument());

		String elementFddId = null;
		List<FondDeDossierFile> repertoireNodeListUpdated = (List<FondDeDossierFile>) fondDeDossierService
				.getChildrenFile(session, repertoireMinistereSggDoc);
		assertNotNull(repertoireNodeListUpdated);
		assertEquals(1, repertoireNodeListUpdated.size());
		assertEquals("update_element_fils", repertoireNodeListUpdated.get(0).getFilename());
		assertNotNull(repertoireNodeListUpdated.get(0).getId());
		assertEquals("2", repertoireNodeListUpdated.get(0).getNumeroVersion());
		elementFddId = repertoireNodeListUpdated.get(0).getId();
		elementFddDoc = session.getDocument(new IdRef(elementFddId));
		assertEquals(elementFddDoc.getParentRef(), repertoireMinistereSggDoc.getRef());
		assertNotNull(elementFddDoc);

		closeSession();
	}

	public void testUpdateFddFileMoveFileAndVersionRepository() throws PropertyException, ClientException {
		openSession();
		// on verifie que la mise à jour et le déplacement dans un autre répertoire d'un ficheri fond de dossier
		// fonctionne
		log.info("begin : test update file fdd + move file fdd in another repository ");

		// create fond de dossier
		FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);
		DocumentModel repertoireParlementDoc = fondDossierNodeList.get(0).getDocument();
		DocumentModel repertoireMinistereSggDoc = fondDossierNodeList.get(1).getDocument();

		// check service : creation fichier with repertoire parent
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "creation_element_fils",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// check new element fond de dossier node properties
		List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, repertoireParlementDoc);
		assertNotNull(repertoireNodeList);
		assertEquals(1, repertoireNodeList.size());
		assertEquals("creation_element_fils", repertoireNodeList.get(0).getFilename());
		assertNotNull(repertoireNodeList.get(0).getId());
		// get Document
		DocumentModel elementFddDoc = session.getDocument(new IdRef(repertoireNodeList.get(0).getId()));
		assertEquals(elementFddDoc.getParentRef(), repertoireParlementDoc.getRef());
		assertNotNull(elementFddDoc);

		DocumentModel newFichierDoc = fondDeDossierService.createBareFondDeDossierFichier(session);
		FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
		newFichier.setFilename("update_element_fils");
		newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL);

		// save update
		fondDeDossierService.updateFddFile(session, elementFddDoc, newFichierDoc, dossier.getDocument());

		String elementFddId = null;
		List<FondDeDossierFile> repertoireNodeListUpdated = (List<FondDeDossierFile>) fondDeDossierService
				.getChildrenFile(session, repertoireMinistereSggDoc);
		assertNotNull(repertoireNodeListUpdated);
		assertEquals(1, repertoireNodeListUpdated.size());
		assertEquals("update_element_fils", repertoireNodeListUpdated.get(0).getFilename());
		assertNotNull(repertoireNodeListUpdated.get(0).getId());
		assertEquals("2", repertoireNodeListUpdated.get(0).getNumeroVersion());
		elementFddId = repertoireNodeListUpdated.get(0).getId();
		elementFddDoc = session.getDocument(new IdRef(elementFddId));
		// check repository
		assertEquals(elementFddDoc.getParentRef(), repertoireMinistereSggDoc.getRef());
		assertNotNull(elementFddDoc);

		// update document name
		newFichier.setFilename("update_element_fils_2");
		newFichier.setNiveauVisibilite(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// save update
		fondDeDossierService.updateFddFile(session, elementFddDoc, newFichierDoc, dossier.getDocument());

		repertoireNodeListUpdated = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session,
				repertoireParlementDoc);
		assertNotNull(repertoireNodeListUpdated);
		assertEquals(1, repertoireNodeListUpdated.size());
		assertEquals("update_element_fils_2", repertoireNodeListUpdated.get(0).getFilename());
		assertNotNull(repertoireNodeListUpdated.get(0).getId());
		assertEquals("3", repertoireNodeListUpdated.get(0).getNumeroVersion());
		elementFddId = repertoireNodeListUpdated.get(0).getId();
		elementFddDoc = session.getDocument(new IdRef(elementFddId));
		// check repository
		assertEquals(elementFddDoc.getParentRef(), repertoireParlementDoc.getRef());
		assertNotNull(elementFddDoc);

		closeSession();
	}

	public void testRepertoireChildrenDelete() throws PropertyException, ClientException {
		openSession();

		// on verifie que les différents objet
		log.info("begin : test dossier creation repertoire + element fond de dossier ");

		// create fond de dossier and
		FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);

		fondDeDossier = dossier.getFondDeDossier(session);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check service : creation fdd child
		fondDeDossierService.createFondDeDossierRepertoire(session, fondDeDossierDoc, "creation répertoire Fdd test");
		session.save();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);

		// check service : creation fichier with repertoire parent
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "creation_element_fils",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);
		// check service : creation fichier with repertoire parent
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "creation_element_fils2",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// check new element fond de dossier node properties
		DocumentModel repertoireParent = fondDossierNodeList.get(0).getDocument();
		List<FondDeDossierFile> repertoireNodeList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, repertoireParent);
		assertNotNull(repertoireNodeList);
		assertEquals(repertoireNodeList.size(), 2);

		// delete repertoire children
		fondDeDossierService.delete(fondDossierNodeList.get(0).getId(), session, dossier.getDocument());

		// check repertoire children removal
		List<FondDeDossierFile> repertoireNodeListDeleted = (List<FondDeDossierFile>) fondDeDossierService
				.getChildrenFile(session, repertoireParent);
		assertNotNull(repertoireNodeListDeleted);
		assertEquals(repertoireNodeListDeleted.size(), 0);

		closeSession();

	}

	public void testGetFondDeDossierPublicDocument() throws PropertyException, ClientException {
		openSession();

		log.info("begin : test GetFondDeDossierPublicDocument");

		// Récupère le fond de dossier
		FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();
		assertNotNull(fondDeDossierDoc);

		// Crée le répertoire parlement de niveau 1
		List<FondDeDossierFolder> fondDossierRepertoireParlementNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);
		assertNotNull(fondDossierRepertoireParlementNodeList);
		FondDeDossierFolder repertoireParlement = fondDossierRepertoireParlementNodeList.get(0);
		assertEquals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME,
				repertoireParlement.getName());

		// Crée un document dans le réperoire parlement
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "nomFichier",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// Récupère l'ID du document parlement
		List<FondDeDossierFile> fichierParlementList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(
				session, repertoireParlement.getDocument());
		assertNotNull(fichierParlementList);
		assertEquals(1, fichierParlementList.size());
		FondDeDossierFile documentParlementNode = fichierParlementList.get(0);
		assertNotNull(documentParlementNode.getId());
		String documentParlementId = documentParlementNode.getId();

		// Récupère le répertoire public de niveau 1
		List<FondDeDossierFile> fondDossierRepertoirePublicNodeList = fondDeDossierService
				.getFondDeDossierPublicDocument(session, fondDeDossierDoc);
		assertNotNull(fondDossierRepertoirePublicNodeList);
		assertEquals(1, fondDossierRepertoirePublicNodeList.size());
		FondDeDossierFile fondDeDossierRepertoirePublicFile = fondDossierRepertoirePublicNodeList.get(0);
		assertEquals(fondDeDossierRepertoirePublicFile.getId(), documentParlementId);

		// Ajout d'un fichier au répertoire ministère
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "nomFichier2",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL);

		// Vérifie que le document parlement n'a pas été ajouté au répertoire public
		fondDossierRepertoirePublicNodeList = fondDeDossierService.getFondDeDossierPublicDocument(session,
				fondDeDossierDoc);
		assertNotNull(fondDossierRepertoirePublicNodeList);
		assertEquals(1, fondDossierRepertoirePublicNodeList.size());
		assertEquals(fondDeDossierRepertoirePublicFile.getId(), documentParlementId);

		// Ajoute un document au répertoire parlement
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "nomFichier3",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// Récupère les documents du répertoire parlement
		fichierParlementList = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session,
				repertoireParlement.getDocument());
		assertNotNull(fichierParlementList);
		// ****************************ERREUR
		assertEquals(2, fichierParlementList.size());
		assertNotNull(documentParlementNode.getId());
		documentParlementId = documentParlementNode.getId();

		// call method
		fondDossierRepertoirePublicNodeList = fondDeDossierService.getFondDeDossierPublicDocument(session,
				fondDeDossierDoc);
		// we don't have new file
		assertNotNull(fondDossierRepertoirePublicNodeList);
		assertEquals(2, fondDossierRepertoirePublicNodeList.size());
		assertEquals(fondDeDossierRepertoirePublicFile.getId(), documentParlementId);

		closeSession();
	}

	public void testIsFondDeDossierFileNameUnique() throws PropertyException, ClientException {
		openSession();

		// isFondDeDossierFileNameUnique
		log.info("begin : test isFondDeDossierFileNameUnique method ");

		// get fond de dossier
		FondDeDossier fondDeDossier = dossier.getFondDeDossier(session);

		// check fond Dossier properties
		DocumentModel fondDeDossierDoc = fondDeDossier.getDocument();

		// check new repertoire node properties
		List<FondDeDossierFolder> fondDossierNodeList = (List<FondDeDossierFolder>) fondDeDossierService
				.getChildrenFolder(session, fondDeDossierDoc);
		assertNotNull(fondDossierNodeList);
		// assertEquals(fondDossierNodeList.size(), 3);
		assertEquals(fondDossierNodeList.get(0).getName(),
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME);

		// init fichierFdd with filename nom_1.doc
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "nom_1.doc",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);

		// init fichierFdd with name nom_2.doc
		fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "nom_2.doc",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL);

		// init fichierFdd with name nom_1.doc and parlement visibility
		DocumentModel newFddFileDocModle = fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc,
				"nom_1.doc", ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);
		// check if name alreday exist
		assertFalse(fondDeDossierService.isFondDeDossierFileNameUnique(fondDeDossier, newFddFileDocModle, session));

		// init fichierFdd with name nom_2.doc and parlement visibility
		newFddFileDocModle = fondDeDossierService.createFondDeDossierFile(session, fondDeDossierDoc, "nom_2.doc",
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL);
		// check if name alreday exist
		assertFalse(fondDeDossierService.isFondDeDossierFileNameUnique(fondDeDossier, newFddFileDocModle, session));

		// check if name
		// List<FondDeDossierNode> repertoireNodeList = fondDeDossierService.getChildrenNode(idParent, session);
		// assertNotNull(repertoireNodeList);
		// assertEquals(repertoireNodeList.size(), 1);

		closeSession();
	}

	private void assertVersion(String expected, DocumentModel doc) throws ClientException {
		assertEquals(expected, getMajor(doc) + "." + getMinor(doc));
	}

	private long getMajor(DocumentModel doc) throws ClientException {
		return ((Long) doc.getPropertyValue(VersioningService.MAJOR_VERSION_PROP)).longValue();
	}

	private long getMinor(DocumentModel doc) throws ClientException {
		return ((Long) doc.getPropertyValue(VersioningService.MINOR_VERSION_PROP)).longValue();
	}

}
