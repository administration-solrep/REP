package fr.dila.ss.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.versioning.VersioningService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSTreeService;
import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.api.tree.SSTreeFolder;
import fr.dila.ss.api.tree.SSTreeNode;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * Implémentation du service d'écriture de fichier dans une arborescence
 * 
 */
public class SSTreeServiceImpl extends DefaultComponent implements SSTreeService {

	protected static final String	ERROR_CREATE_FOLDER	= "Erreur lors de la creation d'un répertoire";
	protected static final String	ERROR_CREATE_FILE	= "Erreur lors de la creation d'un fichier";
	protected static final String	NOT_DELETED			= STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH + " != '"
																+ STLifeCycleConstant.DELETED_STATE + "'";

	/**
	 * Serial version UID
	 */
	private static final long		serialVersionUID	= -7388806415805124494L;

	private static final STLogger	LOGGER				= STLogFactory.getLog(SSTreeServiceImpl.class);

	@Override
	public DocumentModel persistFileInFolder(CoreSession session, DocumentModel bareFileDoc) throws SSException {
		if (bareFileDoc == null) {
			throw new SSException("Pas de documentModel spécifié");
		}
		DocumentModel fileDoc = null;
		try {
			// creation du document en session
			LOGGER.info(session, STLogEnumImpl.CREATE_FILE_TEC, bareFileDoc);
			fileDoc = session.createDocument(bareFileDoc);
			// sauvegarde du document en session pour avoir une version du document
			// incrementation du numero de version
			fileDoc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
			fileDoc = session.saveDocument(fileDoc);
			// commit creation
			session.save();
		} catch (ClientException e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_FILE_TEC);
			throw new SSException(ERROR_CREATE_FILE, e);
		}
		return fileDoc;
	}

	@Override
	public DocumentModel createFileInFolder(CoreSession session, DocumentModel parent, String documentType,
			String fileName, Blob content) throws SSException {
		if (documentType == null) {
			throw new SSException("Pas de type de document spécifié");
		}
		DocumentModel docModel = null;
		// création du DocumentModel
		try {
			docModel = createBareFileInFolder(session, parent, documentType, fileName, content);
			docModel = persistFileInFolder(session, docModel);
		} catch (final ClientException e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_FILE_TEC);
			throw new SSException(ERROR_CREATE_FILE, e);
		}

		return docModel;
	}

	@Override
	public DocumentModel createBareFileInFolder(CoreSession session, DocumentModel parent, String documentType,
			String fileName, Blob content) throws SSException {
		if (documentType == null) {
			throw new SSException("Pas de type de document spécifié");
		}
		final String docName = StringUtil.removeSpacesAndAccent(fileName);
		DocumentModel docModel = null;
		// création du DocumentModel
		try {
			docModel = session.createDocumentModel(parent.getPathAsString(), docName, documentType);
			SSTreeFile fichier = docModel.getAdapter(SSTreeFile.class);
			// set document name
			fichier.setTitle(fileName);
			// set document file properties
			fichier.setFilename(fileName);
			if (content != null) {
				fichier.setContent(content);
			}
		} catch (final ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_FILE_TEC);
			throw new SSException(ERROR_CREATE_FILE, exc);
		}
		return docModel;
	}

	@Override
	public void deleteFile(CoreSession session, DocumentModel document) throws SSException {
		String lifeCycleStateDoc = null;
		try {
			lifeCycleStateDoc = document.getCurrentLifeCycleState();
		} catch (ClientException exc) {
			// NOP
			LOGGER.debug(session, STLogEnumImpl.FAIL_GET_META_DONNEE_TEC, exc);
			// Si la variable lifeCycleStateDoc est null, la suppression physique se fera
		}

		if ("true".equals(Framework.getProperty("tree.file.soft.delete", "true"))
				&& !STLifeCycleConstant.UNDEFINED_STATE.equals(lifeCycleStateDoc)) {
			try {
				LOGGER.info(session, STLogEnumImpl.UPDATE_FILE_TEC, document);
				session.move(document.getRef(), session.getDocument(new PathRef("/case-management/trash-root"))
						.getRef(), null);
				session.followTransition(document.getRef(), STLifeCycleConstant.TO_DELETE_TRANSITION);
				session.save();
			} catch (ClientException exc) {
				LOGGER.debug(session, STLogEnumImpl.FAIL_UPDATE_FILE_TEC, document, exc);
				LOGGER.warn(session, STLogEnumImpl.FAIL_UPDATE_FILE_TEC, document);
				LOGGER.info(session, STLogEnumImpl.DEL_FILE_TEC, document);
				physicalDelete(session, document);
			}
		} else {
			physicalDelete(session, document);
		}
	}

	/**
	 * Effectue la suppression définitive du document (session.remove)
	 * 
	 * @param session
	 * @param document
	 * @throws SSException
	 */
	protected void physicalDelete(CoreSession session, DocumentModel document) throws SSException {
		try {
			LOGGER.info(session, STLogEnumImpl.DEL_FILE_TEC, document);
			session.removeDocument(document.getRef());
			session.save();
		} catch (ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_DEL_FILE_TEC, document);
			throw new SSException("Impossible de supprimer le fichier de l'arborescence", exc);
		}
	}

	@Override
	public DocumentModel createNewFolder(CoreSession session, String folderType, DocumentModel parent, String folderName)
			throws SSException {
		DocumentModel folder = null;
		try {
			// create document model
			folder = session.createDocumentModel(parent.getPathAsString(),
					StringUtil.removeSpacesAndAccent(folderName), folderType);
			// on définit le titre du document
			DublincoreSchemaUtils.setTitle(folder, folderName);
			// create document in session
			LOGGER.info(session, STLogEnumImpl.CREATE_FOLDER_TEC);
			folder = session.createDocument(folder);
			session.saveDocument(folder);
			session.save();
		} catch (final ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_FOLDER_TEC);
			throw new SSException(ERROR_CREATE_FOLDER, exc);
		}
		return folder;
	}

	@Override
	public DocumentModel createNewFolderBefore(CoreSession session, String folderType, DocumentModel currentDoc,
			String folderName) throws SSException {
		DocumentModel folder = null;
		try {
			// on récupère le document parent à l'aide de sa référence
			final DocumentModel parent = session.getDocument(currentDoc.getParentRef());
			// on cree le nouveau repertoire
			LOGGER.info(session, STLogEnumImpl.CREATE_FOLDER_TEC);
			folder = createNewFolder(session, folderType, parent, folderName);
			// on place le document avant le document Courant
			session.orderBefore(parent.getRef(), folder.getName(), currentDoc.getName());
			// commit de la creation
			session.save();
		} catch (ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_FOLDER_TEC);
			throw new SSException(ERROR_CREATE_FOLDER, exc);
		}
		return folder;
	}

	@Override
	public DocumentModel createNewFolderAfter(CoreSession session, String folderType, DocumentModel currentDoc,
			String folderName) throws SSException {
		DocumentModel folder = null;
		try {
			// on récupère le document parent à l'aide de sa référence
			final DocumentModel parent = session.getDocument(currentDoc.getParentRef());
			final DocumentModelList orderedChildren = session.getChildren(currentDoc.getParentRef());
			// on récupère l'index du document courant
			final int selectedDocumentIndex = orderedChildren.indexOf(currentDoc);
			// on récupère l'index suivant s'il en existe un et on récupère son nom
			String nomDocumentSource = null;
			final int nextIndex = selectedDocumentIndex + 1;
			if (nextIndex < orderedChildren.size()) {
				nomDocumentSource = orderedChildren.get(nextIndex).getName();
			}
			// on cree le nouveau repertoire
			final DocumentModel newFolder = createNewFolder(session, folderType, parent, folderName);
			// on place le document avant l'élement suivant du document courant :
			// si le document courant est le dernier document de la liste on place le nouveau document en fin de liste
			session.orderBefore(parent.getRef(), newFolder.getName(), nomDocumentSource);
			// commit de la creation
			session.save();
		} catch (ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_FOLDER_TEC);
			throw new SSException(ERROR_CREATE_FOLDER, exc);
		}
		return folder;
	}

	@Override
	public DocumentModel renameFolder(CoreSession session, DocumentModel folderDoc, String newTitle)
			throws ClientException {
		LOGGER.info(session, STLogEnumImpl.UPDATE_FOLDER_TEC);
		return renameDocument(session, folderDoc, newTitle);
	}
	
	@Override
	public DocumentModel renameDocument(CoreSession session, DocumentModel document, String newTitle) throws ClientException {
		// on modifie le nom du document
		DublincoreSchemaUtils.setTitle(document, newTitle);
		document = session.saveDocument(document);
		// On met à jour son path avec le nouveau nom
		document = session.move(document.getRef(), document.getParentRef(), StringUtil.removeSpacesAndAccent(newTitle));
		session.save();
		return document;		
	}

	@Override
	public void deleteFolder(CoreSession session, DocumentModel folderDoc) throws ClientException {
		LOGGER.info(session, STLogEnumImpl.DEL_FOLDER_TEC);
		session.removeDocument(folderDoc.getRef());
		session.save();
	}

	@Override
	public List<SSTreeNode> getChildrenNode(CoreSession session, SSTreeNode parent) throws ClientException {
		return getChildrenNode(session, parent.getDocument().getRef(), parent.getDepth());
	}

	@Override
	public List<SSTreeNode> getChildrenNode(CoreSession session, DocumentRef parentRef, int parentDepth)
			throws ClientException {
		List<SSTreeNode> childrenNode = new ArrayList<SSTreeNode>();
		DocumentModelList children = session.getChildren(parentRef);
		for (DocumentModel child : children) {
			SSTreeNode childNode = child.getAdapter(SSTreeNode.class);
			childNode.setDepth(parentDepth + 1);
			childrenNode.add(childNode);
		}

		return childrenNode;
	}

	@Override
	public List<? extends SSTreeFolder> getChildrenFolder(CoreSession session, DocumentModel parentDoc)
			throws ClientException {
		return getChildrenFolder(session, parentDoc.getRef());
	}

	@Override
	public List<? extends SSTreeFolder> getChildrenFolder(CoreSession session, DocumentRef parentDocRef)
			throws ClientException {
		// initialisation des variables
		List<SSTreeFolder> subGroupsFolderList = new ArrayList<SSTreeFolder>();
		// on récupère les fils du document à partir de son identifiant
		DocumentModelList childDocModelList = session.getChildren(parentDocRef);
		// on parcourt les fils : on récupère les fils pour récupérer les fils non supprimés
		for (DocumentModel childDoc : childDocModelList) {
			if (!STLifeCycleConstant.DELETED_STATE.equals(childDoc.getCurrentLifeCycleState())
					&& childDoc.hasFacet(SSTreeConstants.FOLDERISH_FACET)) {
				subGroupsFolderList.add(getFolderImplFromDoc(childDoc));
			}
		}
		return subGroupsFolderList;
	}

	@Override
	public List<? extends SSTreeFile> getChildrenFile(CoreSession session, DocumentModel parentDoc)
			throws ClientException {
		return getChildrenFile(session, parentDoc.getRef());
	}

	@Override
	public List<? extends SSTreeFile> getChildrenFile(CoreSession session, DocumentRef parentDocRef)
			throws ClientException {
		// initialisation des variables
		List<SSTreeFile> subGroupsFileList = new ArrayList<SSTreeFile>();
		// on récupère les fils du document à partir de son identifiant
		DocumentModelList childDocModelList = session.getChildren(parentDocRef);
		// on parcourt les fils : on récupère les fils pour récupérer les fils non supprimés
		for (DocumentModel childDoc : childDocModelList) {
			if (!STLifeCycleConstant.DELETED_STATE.equals(childDoc.getCurrentLifeCycleState())
					&& childDoc.hasSchema(SSTreeConstants.FILE_SCHEMA)) {
				subGroupsFileList.add(getFileImplFromDoc(childDoc));
			}
		}
		return subGroupsFileList;
	}

	protected SSTreeFile getFileImplFromDoc(DocumentModel doc) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	protected SSTreeFolder getFolderImplFromDoc(DocumentModel doc) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void restoreToPreviousVersion(CoreSession session, String currentDocId, DocumentModel dossierDoc)
			throws ClientException {
		// get document
		DocumentRef currentDocRef = new IdRef(currentDocId);
		DocumentModel currentFileDoc = session.getDocument(currentDocRef);
		SSTreeFile versionFileN1 = currentFileDoc.getAdapter(SSTreeFile.class);

		// get version number
		Long majorVersionLong = versionFileN1.getMajorVersion();
		// get all version
		List<DocumentModel> versionList = session.getVersions(currentDocRef);
		// get the last version
		DocumentModel lastVersion = versionList.get(majorVersionLong.intValue() - 2);
		DocumentRef lastVersionDocRef = lastVersion.getRef();
		// update current document with last version
		lastVersion = session.restoreToVersion(currentDocRef, lastVersionDocRef);
		session.save();

		// get new version list
		versionList = session.getVersions(lastVersion.getRef());

		// remove the last version document (currentDoc before save)
		// remove the last version document
		session.removeDocument(versionList.get(versionList.size() - 1).getRef());

		// change the document path
		if (DublincoreSchemaUtils.getTitle(lastVersion) != null) {
			String newPathName = DublincoreSchemaUtils.getTitle(lastVersion);
			DocumentRef docParentRef = lastVersion.getParentRef();
			// lastVersion.setPathInfo(lastVersion.getPathAsString(), oldName);
			moveVersionableDocumentToFolder(session, docParentRef, newPathName, lastVersion);
		}
		// save change in session
		session.save();
	}

	/**
	 * 
	 * Move a document and linked versions into another folder.
	 * 
	 */
	protected DocumentModel moveVersionableDocumentToFolder(CoreSession session, DocumentRef newFolderRef,
			String newVersionName, DocumentModel versionableDocument) throws ClientException {
		// move the document and all versions in new repository
		DocumentRef versionableDocumentRef = versionableDocument.getRef();
		try {
			// move document and document verson to new Folder
			session.move(versionableDocumentRef, newFolderRef, newVersionName);
		} catch (ClientException e) {
			throw new ClientException("erreur lors du déplacement du document dans un autre répertoire", e);
		}
		// get new document
		DocumentModel newVersionableDocument = session.getDocument(versionableDocumentRef);
		// return document
		return newVersionableDocument;
	}

	@Override
	public Boolean isFolderEmpty(CoreSession session, SSTreeFolder folder) throws ClientException {
		if (session == null || folder == null) {
			throw new SSException(
					"Session ou folder ne peuvent pas être null pour déterminer si le répertoire est vide");
		} else {
			return !session.hasChildren(new IdRef(folder.getId()));
		}
	}

	@Override
	public void updateFile(CoreSession session, DocumentModel fichier, String fileName, Blob blob,
			NuxeoPrincipal currentUser, DocumentModel dossierDocument) throws ClientException {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Récupère l'entité de l'utilisateur connecté.
	 * 
	 * @param currentUser
	 *            currentUser
	 * 
	 * @return String
	 * @throws ClientException
	 */
	protected String getEntite(final NuxeoPrincipal currentUser) throws ClientException {
		String entite = null;
		// récupération de l'entité de l'utilisateur
		// on récupère l'identifiant du premier ministère d'appartenance de l'utilisateur
		final SSPrincipal ssPrincipal = (SSPrincipal) currentUser;
		final Set<String> ministereSet = ssPrincipal.getMinistereIdSet();
		String identifiantMinistere = null;
		if (ministereSet != null && !ministereSet.isEmpty()) {
			identifiantMinistere = ministereSet.iterator().next();
			// on récupère le label du ministère à partir de son identifiant
			final OrganigrammeNode orgDoc = STServiceLocator.getSTMinisteresService().getEntiteNode(
					identifiantMinistere);
			if (orgDoc != null) {
				entite = ((EntiteNode) orgDoc).getEdition();
			}
		}
		return entite;
	}
}
