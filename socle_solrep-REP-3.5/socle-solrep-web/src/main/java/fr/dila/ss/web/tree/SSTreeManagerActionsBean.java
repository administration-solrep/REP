package fr.dila.ss.web.tree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.annotations.In;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.schema.FacetNames;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;
import org.nuxeo.ecm.platform.usermanager.exceptions.GroupAlreadyExistsException;
import org.nuxeo.ecm.webapp.edit.lock.LockActions;
import org.nuxeo.ecm.webapp.filemanager.UploadItemHolder;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSTreeService;
import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.api.tree.SSTreeFolder;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * ActionBean de gestion de l'arborescence.
 * 
 */
public abstract class SSTreeManagerActionsBean implements Serializable {

	/**
     * 
     */
	private static final long				serialVersionUID				= -6536590305366580977L;

	private static final STLogger			LOGGER							= STLogFactory
																					.getLog(SSTreeManagerActionsBean.class);

	@In(create = true, required = true)
	protected transient CoreSession			documentManager;

	@In(create = true, required = false)
	protected FacesMessages					facesMessages;

	@In(required = true, create = true)
	protected transient SSPrincipal			ssPrincipal;

	@In(create = true)
	protected ResourcesAccessor				resourcesAccessor;

	@In(create = true)
	protected transient LockActions			lockActions;

	protected String						selectedNodeId;

	protected String						selectedNodeType;

	protected String						selectedNodeName;

	protected String						selectedNodeTitle;

	protected String						removeFileName;

	protected String						choixSuppression;

	protected List<SelectItem>				selectItemsDelete;

	protected String						numeroVersion;

	protected DocumentModel					currentElement;

	protected Boolean						isErrorOccurred;

	protected String						errorName;

	protected List<String>					listErrorName;
	
	protected String 						fileNameSansExtention = "";

	protected String						extentionFile = "";

	/*
	 * Liste de caractères non autorisés dans le nom de document
	 */
	protected final String[]				forbiddenChars					= { "\'" };

	@In(create = true, required = false)
	protected UploadItemHolder				fileUploadHolder;

	/*
	 * Nombre de fichier téléchargeable par défaut
	 */
	protected int							uploadsAvailable				= Integer.MAX_VALUE;

	protected String						acceptedTypes;

	// used to get fondDeDossier document
	@In(create = true, required = false)
	protected transient NavigationContext	navigationContext;

	// file properties

	public static final String				ERROR_MSG_DOCUMENT_EMPTY		= "feedback.ss.document.tree.document.error.empty.form";

	public static final String				ERROR_MSG_NO_FILE_SELECTED		= "feedback.ss.document.tree.document.error.unselected.file";

	public static final String				ERROR_MSG_NAME_ALREADY_EXIST	= "feedback.ss.document.tree.document.error.existing.name";

	public static final String				ERROR_CREATION					= "feedback.ss.document.tree.document.error.creation";

	protected static final String			ERROR_MSG_NAME_INCORRECT		= "feedback.ss.document.tree.document.error.incorrect.name";

	protected static final String			ERROR_MSG_FILE_TOO_BIG			= "feedback.ss.document.tree.document.error.file.too.big";

	/**
	 * Reset the temp properties
	 * 
	 */
	public void resetProperties() {
		choixSuppression = null;
		currentElement = null;
		numeroVersion = null;
		removeFileName = null;
		selectedNodeId = null;
		selectedNodeName = null;
		selectedNodeTitle = null;
		selectedNodeType = null;
		selectItemsDelete = null;
		setUploadedFiles(null);
	}

	/**
	 * Reset the file fdd properties
	 * 
	 */
	public void resetFileUploadProperties() {
		uploadsAvailable = 10;
	}

	public void resetErrorProperties() {
		setIsErrorOccurred(false);
		setErrorName(null);
		setListErrorName(null);
	}
	
	public void resetAllTmpProperties() {
		resetProperties();
		resetErrorProperties();
	}

	/**
	 * Suppression d'un fichier dans l'arborescence du document.
	 * 
	 * @return null
	 * @throws ClientException
	 * @author ARN
	 */
	public void deleteFile() throws ClientException {
		LOGGER.info(documentManager, STLogEnumImpl.DEL_FILE_FONC);
		resetErrorProperties();
		final SSTreeService ssTreeService = SSServiceLocator.getSSTreeService();
		if (selectedNodeId == null || choixSuppression == null) {
			setIsErrorOccurred(true);
		} else if (SSTreeConstants.DELETE_ALL_CHOICE.equals(choixSuppression)) {
			// on supprime le document et toutes les versions qui lui sont associées
			ssTreeService.deleteFile(documentManager, documentManager.getDocument(new IdRef(selectedNodeId)));
			resetProperties();
			treeChangeEvent();
		} else if (SSTreeConstants.DELETE_CURRENT_VERSION_CHOICE.equals(choixSuppression)) {
			// Restauration de l'avant dernière version du noeud dans l'arborescence du fond de dossier.
			ssTreeService.restoreToPreviousVersion(documentManager, selectedNodeId,
					navigationContext.getCurrentDocument());
			resetProperties();
			treeChangeEvent();
		} else {
			setIsErrorOccurred(true);
		}
	}

	/**
	 * Supprime le document sélectionné ainsi que tous ses enfants
	 */
	public void deleteDocument() throws ClientException {
		if (selectedNodeId != null) {
			final SSTreeService ssTreeService = SSServiceLocator.getSSTreeService();
			ssTreeService.deleteFolder(documentManager, documentManager.getDocument(new IdRef(selectedNodeId)));
			// on deselectionner le répertoire : remise à zero des variables concernant le répertoire sélectionné
			resetProperties();
			// on recharge l'arbre
			treeChangeEvent();
		}
	}

	/**
	 * Création d'un type de fichier à définir
	 * 
	 * @param fichier
	 * @param filename
	 * @throws ClientException
	 */
	protected abstract void createSpecificFile(SSTreeFile fichier, String filename) throws ClientException;

	/**
	 * Mise à jour d'un type de fichier à définir
	 * 
	 * @param fichier
	 * @param filename
	 * @throws ClientException
	 */
	protected void updateSpecificFile(SSTreeFile fichier, String filename) throws ClientException {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Création d'un fichier d'arborescence non persisté
	 * 
	 * @return
	 * @throws SSException
	 */
	protected DocumentModel createBareFile() throws SSException {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	private DocumentModel createBareFileFromUploadItem(UploadItem item) throws ClientException, FileNotFoundException {
		// get fileName
		String fileName = item.getFileName();
		// get file content
		Blob blob = FileUtils.createSerializableBlob(new FileInputStream(item.getFile()), item.getFileName(), null);
		DocumentModel newElement = createBareFile();
		if (StringUtils.isBlank(fileName)) {
			// log.warn("error : fileName is null !");
		} else {
			fileName = cleanFileName(fileName);
			newElement.setProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_FILENAME_PROPERTY,
					fileName);
		}
		if (blob == null) {
			// log.warn("error : fileContent is null !");
		} else {
			newElement.setProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_CONTENT_PROPERTY, blob);
		}
		return newElement;
	}

	public void createFile() {
		LOGGER.info(documentManager, STLogEnumImpl.CREATE_FILE_FONC);
		resetErrorProperties();
		List<UploadItem> uploadItemList = new ArrayList<UploadItem>(getUploadedFiles());
		if (getSelectedNodeId() == null) {
			setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_NO_FILE_SELECTED));
			setIsErrorOccurred(true);
		} else if (uploadItemList == null || uploadItemList.isEmpty()) {
			setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_DOCUMENT_EMPTY));
			setIsErrorOccurred(true);
		} else {
			try {
				// récupération des données du fichier courant
				boolean creationDone = false;
				for (UploadItem item : uploadItemList) {
					DocumentModel newElement = createBareFileFromUploadItem(item);
					try {
						final SSTreeFile fichier = newElement.getAdapter(SSTreeFile.class);
						final String filename = FileUtils.getCleanFileName(item.getFileName());
						if (isFileNameCorrect(filename)) {
							// On vérifie la taille du fichier
							if (verifyFileLength(documentManager, fichier)) {
								createSpecificFile(fichier, filename);
								creationDone = true;
							} else {
								setIsErrorOccurred(true);
								setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_FILE_TOO_BIG));
							}
						} else {
							setIsErrorOccurred(true);
							setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_NAME_INCORRECT));
						}
					} catch (GroupAlreadyExistsException gaee) {
						getListErrorName().add(resourcesAccessor.getMessages().get(ERROR_CREATION));
						setIsErrorOccurred(true);
					}
				}
				if (creationDone) {
					// on réinitialise les variables de l'édition de fichier
					resetAllTmpProperties();
				}
				// on recharge l'arbre
				treeChangeEvent();
			} catch (final Exception exc) {
				LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_FILE_FONC, exc.getMessage());
				LOGGER.debug(documentManager, STLogEnumImpl.FAIL_CREATE_FILE_FONC, exc);
				setIsErrorOccurred(true);
				setErrorName(exc.getMessage());
			}
		}
	}

	public void updateFile() {
		LOGGER.info(documentManager, STLogEnumImpl.UPDATE_FILE_FONC);
		resetErrorProperties();
		if (getSelectedNodeId() == null || getSelectedNodeName() == null) {
			setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_NO_FILE_SELECTED));
			setIsErrorOccurred(true);
		} else if (getUploadedFiles() == null) {
			setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_DOCUMENT_EMPTY));
			setIsErrorOccurred(true);
		} else {
			try {
				// récupération des données du fichier courant
				boolean creationDone = false;
				for (UploadItem item : getUploadedFiles()) {
					DocumentModel newElement = createBareFileFromUploadItem(item);
					try {
						final SSTreeFile fichier = newElement.getAdapter(SSTreeFile.class);
						final String filename = FileUtils.getCleanFileName(item.getFileName());
						if (isFileNameCorrect(filename)) {
							// On vérifie la taille du fichier
							if (verifyFileLength(documentManager, fichier)) {
								updateSpecificFile(fichier, filename);
								creationDone = true;
							} else {
								setIsErrorOccurred(true);
								setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_FILE_TOO_BIG));
							}
						} else {
							setIsErrorOccurred(true);
							setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_NAME_INCORRECT));
						}
					} catch (GroupAlreadyExistsException gaee) {
						getListErrorName().add(resourcesAccessor.getMessages().get(ERROR_CREATION));
						setIsErrorOccurred(true);
					}
				}
				if (creationDone) {
					// on réinitialise les variables de l'édition de fichier
					resetAllTmpProperties();
				}
				// on recharge l'arbre
				treeChangeEvent();
			} catch (final Exception exc) {
				LOGGER.error(documentManager, STLogEnumImpl.FAIL_UPDATE_FILE_FONC, exc);
				setIsErrorOccurred(true);
				setErrorName(exc.getMessage());
			}
		}
	}

	public void createFolder(DocumentModel parentDoc, String documentType, String folderName) {
		final SSTreeService ssTreeService = SSServiceLocator.getSSTreeService();
		resetErrorProperties();
		try {
			LOGGER.info(documentManager, STLogEnumImpl.CREATE_FOLDER_FONC, "Into : " + parentDoc.getId());
			ssTreeService.createNewFolder(documentManager, documentType, parentDoc, folderName);
		} catch (SSException sse) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_FOLDER_FONC, sse);
			facesMessages.add(StatusMessage.Severity.WARN, "Erreur lors de la création du répertoire");
		}
		resetProperties();
		treeChangeEvent();
	}

	public void createFolderBefore(DocumentModel parentDoc, String documentType, String folderName) {
		final SSTreeService ssTreeService = SSServiceLocator.getSSTreeService();
		resetErrorProperties();
		try {
			LOGGER.info(documentManager, STLogEnumImpl.CREATE_FOLDER_FONC, "Before : " + parentDoc.getId());
			ssTreeService.createNewFolderBefore(documentManager, documentType, parentDoc, folderName);
		} catch (SSException sse) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_FOLDER_FONC, sse);
			facesMessages.add(StatusMessage.Severity.WARN, "Erreur lors de la création du répertoire");
		}
		resetProperties();
		treeChangeEvent();
	}

	public void createFolderAfter(DocumentModel parentDoc, String documentType, String folderName) {
		final SSTreeService ssTreeService = SSServiceLocator.getSSTreeService();
		resetErrorProperties();
		try {
			LOGGER.info(documentManager, STLogEnumImpl.CREATE_FOLDER_FONC, "After : " + parentDoc.getId());
			ssTreeService.createNewFolderAfter(documentManager, documentType, parentDoc, folderName);
		} catch (SSException sse) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_FOLDER_FONC, sse);
			facesMessages.add(StatusMessage.Severity.WARN, "Erreur lors de la création du répertoire");
		}
		resetProperties();
		treeChangeEvent();
	}

	public void renameFolder(DocumentModel docToRename, String newName) {
		final SSTreeService ssTreeService = SSServiceLocator.getSSTreeService();
		resetErrorProperties();
		try {
			LOGGER.info(documentManager, STLogEnumImpl.UPDATE_FOLDER_FONC);
			ssTreeService.renameFolder(documentManager, docToRename, newName);
		} catch (ClientException sse) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_UPDATE_FOLDER_FONC, sse);
			facesMessages.add(StatusMessage.Severity.WARN, "Erreur lors du rennomage du répertoire");
		}
		resetProperties();
		treeChangeEvent();
	}

	protected boolean checkFileCreation() throws ClientException {
		resetErrorProperties();
		if (getListErrorName() == null) {
			setListErrorName(new ArrayList<String>());
		}
		if (getCurrentElement() == null) {
			getListErrorName().add(resourcesAccessor.getMessages().get(ERROR_MSG_DOCUMENT_EMPTY));
			setIsErrorOccurred(true);
			return false;
		} else if (getCurrentElement().getProperty(STSchemaConstant.FILE_SCHEMA,
				STSchemaConstant.FILE_FILENAME_PROPERTY) == null) {
			getListErrorName().add(resourcesAccessor.getMessages().get(ERROR_MSG_NO_FILE_SELECTED));
			setIsErrorOccurred(true);
			return false;
		}

		return true;
	}

	public String getSelectedNodeId() {
		return selectedNodeId;
	}

	public void setSelectedNodeId(String selectedNodeId) {
		this.selectedNodeId = selectedNodeId;
	}

	public String getSelectedNodeType() {
		return selectedNodeType;
	}

	public void setSelectedNodeType(String selectedNodeType) {
		this.selectedNodeType = selectedNodeType;
	}

	public String getSelectedNodeName() {
		return selectedNodeName;
	}

	public void setSelectedNodeName(String selectedNodeName) {
		this.selectedNodeName = selectedNodeName;
	}

	public String getNumeroVersion() {
		return numeroVersion;
	}

	public void setNumeroVersion(String numeroVersion) {
		this.numeroVersion = numeroVersion;
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(final int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	/**
	 * Listener calleds when user change suppression choice.
	 * 
	 */
	public void choiceSuppressionChangeListener(ValueChangeEvent event) throws Exception {
		if (event != null && event.getNewValue() != null) {
			String newValue = event.getNewValue().toString();
			this.choixSuppression = newValue;
		}
	}

	/**
	 * Nettoie le nom du fichier : si le nom du fichier correspond au chemin sur le disque, on ne récupère que le nom du
	 * fichier => Utilisé pour IE
	 */
	protected String cleanFileName(String fileNameToClean) {
		String fileName = fileNameToClean;
		int lastSlashChar = fileName.lastIndexOf('/');
		if (lastSlashChar != -1) {
			fileName = fileName.substring(lastSlashChar + 1);
		}
		int lastBackSlashChar = fileName.lastIndexOf('\\');
		if (lastBackSlashChar != -1) {
			fileName = fileName.substring(lastBackSlashChar + 1);
		}
		return fileName;
	}

	/**
	 * vérifie la validité d'une chaine de caractère en fonction des caractères non autorisés
	 * 
	 * @param filename
	 * @return vrai si la string en paramètre ne contient pas l'un des caractères interdits, faux sinon
	 */
	protected boolean isFileNameCorrect(final String filename) {
		for (final String s : forbiddenChars) {
			if (filename.contains(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Vérifie la taille du fichier en fonction des paramètres de l'application
	 * 
	 * @param fichier
	 * @return Vrai si la taille du fichier est inférieur au paramètre ou si la paramètre est null, faux sinon
	 */
	protected boolean verifyFileLength(CoreSession session, SSTreeFile fichier) {
		boolean isFileOK = false;
		final STParametreService paramService = STServiceLocator.getSTParametreService();
		String param = null;
		try {
			param = paramService.getParametreValue(session, STParametreConstant.PARAMETRE_TAILLE_PIECES_JOINTES);
		} catch (ClientException e) {
			LOGGER.warn(documentManager, STLogEnumImpl.CREATE_FILE_FONC, e);
			return true;
		}
		try {
			Long.parseLong(param);
		} catch (Exception e) {
			LOGGER.warn(documentManager, STLogEnumImpl.CREATE_FILE_FONC, e);
			return true;
		}
		if (param == null || (fichier.getContent().getLength() <= (Long.parseLong(param) * 1048576))) {
			isFileOK = true;
		}
		return isFileOK;
	}

	/**
	 * Listener sur l'upload d'un fichier.
	 * 
	 */
	public void fileUploadListener(UploadEvent event) throws Exception {
		LOGGER.debug(documentManager, STLogEnumImpl.CREATE_FILE_FONC);
		resetErrorProperties();

		if (!checkUploadAvailable(event)) {
			return;
		}

		if (errorName == null || errorName.isEmpty()) {
			setIsErrorOccurred(false);
			setListErrorName(null);
			// on transmet le fichier dans le bean dédié et on écrase l'ancienne valeur;
			addUploadedFile(event.getUploadItem());

			--uploadsAvailable;
		}
	}

	protected boolean checkUploadAvailable(UploadEvent event) {
		if (event == null || event.getUploadItem() == null || event.getUploadItem().getFileName() == null) {
			errorName = "le fichier est vide !";
			setIsErrorOccurred(true);
			resetProperties();
			return false;
		}
		if (selectedNodeId == null) {
			errorName = "répertoire ou fichier non sélectionné !";
			setIsErrorOccurred(true);
			resetProperties();
			return false;
		}

		return true;
	}

	/**
	 * Getter/setter pour les fichiers.
	 */

	public Collection<UploadItem> getUploadedFiles() {
		if (fileUploadHolder != null) {
			Collection<UploadItem> files = fileUploadHolder.getUploadedFiles();
			if (files == null) {
				files = new ArrayList<UploadItem>();
			}
			return files;
		} else {
			return null;
		}
	}

	private void setUploadedFiles(final Collection<UploadItem> uploadedFiles) {
		if (fileUploadHolder != null) {			
			fileUploadHolder.setUploadedFiles(uploadedFiles);
		}
	}
	
	private void addUploadedFile(UploadItem uploadedItem) {
		if (fileUploadHolder != null) {
			Collection<UploadItem> alreadyUploadedItems = new ArrayList<UploadItem>(getUploadedFiles());
			for (UploadItem item : alreadyUploadedItems) {
				if (item.getFileName().equals(uploadedItem.getFileName()) && item.getFileSize() == uploadedItem.getFileSize()) {
					return;
				}
			}
			alreadyUploadedItems.add(uploadedItem);
			fileUploadHolder.setUploadedFiles(alreadyUploadedItems);
		}
	}

	/**
	 * listener onClear one or more file
	 * 
	 * clears on file from the actual uploaded (but not yet saved) files. The current selected filename is assigned by
	 * an a4j:actionparam to the property 'fileName'; If filename==null all uploaded files will be removed
	 * 
	 */
	public void clearUploadData(ActionEvent event) {
		// test if a single file was cleared....
		if (removeFileName != null && !"".equals(removeFileName)) {
			LOGGER.info(documentManager, STLogEnumImpl.DEL_FILE_UPLOAD_FONC, "Fichier : " + removeFileName);
			if (getUploadedFiles() != null) {
				for (Iterator<UploadItem> iterator = getUploadedFiles().iterator(); iterator.hasNext();) {
					UploadItem documentItem = iterator.next();
					if (removeFileName.equals(documentItem.getFileName())) {
						iterator.remove();
					}
				}
			}
		} else {
			// otherwise remove all files
			setUploadedFiles(null);
			LOGGER.info(documentManager, STLogEnumImpl.DEL_FILE_UPLOAD_FONC,
					"Suppression de tous les fichiers effectuée");
		}
		resetErrorProperties();
	}

	public String getRemoveFileName() {
		return removeFileName;
	}

	public void setRemoveFileName(String removeFileName) {
		this.removeFileName = removeFileName;
	}

	public Boolean getIsErrorOccurred() {
		if (isErrorOccurred == null) {
			isErrorOccurred = true;
		}
		return isErrorOccurred;
	}

	public void setIsErrorOccurred(Boolean isErrorOccurred) {
		this.isErrorOccurred = isErrorOccurred;
	}

	public String getErrorName() {
		if (getListErrorName() != null && !getListErrorName().isEmpty()) {
			// if only one element
			if (getListErrorName().size() == 1) {
				return getListErrorName().get(0);
			}
			// otherwise it is a list of multiple filenames
			final StringBuilder allErrors = new StringBuilder();
			allErrors.append("Plusieurs erreurs ont été détectées : ");
			allErrors.append('\n');
			for (String st : getListErrorName()) {
				allErrors.append(st);
				allErrors.append('\n');
				allErrors.append(" ");
			}
			return allErrors.toString();
		}
		return errorName;
	}

	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}

	public String getChoixSuppression() {
		return choixSuppression;
	}

	public void setChoixSuppression(String choixSuppression) {
		this.choixSuppression = choixSuppression;
	}

	public List<SelectItem> getSelectItemsDelete() throws ClientException {
		selectItemsDelete = new ArrayList<SelectItem>();
		if (selectedNodeId != null) {
			selectItemsDelete.add(new SelectItem(SSTreeConstants.DELETE_ALL_CHOICE));
			if (numeroVersion != null && Integer.parseInt(numeroVersion) > 1) {
				selectItemsDelete.add(new SelectItem(SSTreeConstants.DELETE_CURRENT_VERSION_CHOICE));
			}
		}
		return selectItemsDelete;
	}

	public void setSelectItemsDelete(List<SelectItem> selectItemsDelete) {
		this.selectItemsDelete = selectItemsDelete;
	}

	public void setCurrentElement(DocumentModel currentElement) {
		this.currentElement = currentElement;
	}

	public DocumentModel getCurrentElement() {
		return currentElement;
	}

	public void setListErrorName(List<String> listErrorName) {
		this.listErrorName = listErrorName;
	}

	public List<String> getListErrorName() {
		return listErrorName;
	}

	public Boolean isFolderEmpty(SSTreeFolder folder) {
		final SSTreeService ssTreeService = SSServiceLocator.getSSTreeService();
		try {
			return ssTreeService.isFolderEmpty(documentManager, folder);
		} catch (ClientException exc) {
			if (folder != null) {
				logExcAndDisplayMessage(STLogEnumImpl.FAIL_GET_INFORMATION_TEC, exc, folder.getDocument());
			} else {
				logExcAndDisplayMessage(STLogEnumImpl.FAIL_GET_INFORMATION_TEC, exc, null);
			}
		}
		return true;
	}

	/**
	 * Signale que l'arborescence de document a changé et va déclencher les événements de rechargement sur ces arbres.
	 * 
	 */
	protected void treeChangeEvent() {
		Events.instance().raiseEvent(SSTreeConstants.TREE_CHANGED_EVENT);
	}

	protected void logExcAndDisplayMessage(STLogEnum log, Throwable exception, DocumentModel doc) {
		if (doc == null) {
			LOGGER.error(documentManager, log, exception);
		} else {
			LOGGER.error(documentManager, log, doc, exception);
		}

		facesMessages.add(StatusMessage.Severity.WARN, log.getText());
	}

	/**
	 * Récupération des versions d'un document.
	 * 
	 * @throws FileNotFoundException
	 * 
	 */
	public List<DocumentModel> getFileVersionList() throws ClientException, FileNotFoundException {
		List<DocumentModel> fileVersionList = null;
		if (getSelectedNodeId() != null) {
			final DocumentModel currentFile = getSelectedDocument(getSelectedNodeId());
			if (currentFile != null && currentFile.hasFacet(FacetNames.VERSIONABLE)) {
				// récupération de toute les versions du documents
				fileVersionList = documentManager.getVersions(currentFile.getRef());
				// on renvoie les élément du plus récent auplus anciens
				Collections.reverse(fileVersionList);
			}
		}
		return fileVersionList;
	}

	/**
	 * récupère le document sélectionné par l'utilisateur à partir d"un identifiant.
	 * 
	 * @return DocumentModel
	 */
	protected DocumentModel getSelectedDocument(final String idDocument) {
		DocumentModel selectedFolder = null;
		// on récupère le document à l'aide de son identifiant
		final String selectedNodeId = idDocument;
		if (selectedNodeId != null) {
			try {
				selectedFolder = documentManager.getDocument(new IdRef(selectedNodeId));
			} catch (final ClientException e) {
				throw new RuntimeException("impossible de récupérer le répertoire sélectionné", e);
			}
		}
		return selectedFolder;
	}

	/**
	 * @return the selectedNodeTitle
	 */
	public String getSelectedNodeTitle() {
		return selectedNodeTitle;
	}

	/**
	 * @param selectedNodeTitle
	 *            the selectedNodeTitle to set
	 */
	public void setSelectedNodeTitle(String selectedNodeTitle) {
		this.selectedNodeTitle = selectedNodeTitle;
	}
	
	public void renameFile() {
		if (selectedNodeId != null && selectedNodeTitle != null) {
			DocumentModel document = getSelectedDocument(selectedNodeId);
			String newName = selectedNodeTitle;
			try {
				if(! newName.equals(document.getTitle())) {
					try {
						SSTreeFile fichier = document.getAdapter(SSTreeFile.class);
						final String filename = FileUtils.getCleanFileName(newName);
						if (isFileNameCorrect(filename)) {
								updateSpecificFile(fichier, filename);
								// on réinitialise les variables de l'édition de fichier
								resetProperties();
						} else {
							setIsErrorOccurred(true);
							setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_NAME_INCORRECT));
						}
					} catch (ClientException sse) {
						setIsErrorOccurred(true);
						getListErrorName().add(resourcesAccessor.getMessages().get(ERROR_CREATION));
						facesMessages.add(StatusMessage.Severity.WARN, "Erreur lors de l'update du document");
					}
					resetProperties();
					treeChangeEvent();
				}
			} catch (ClientException e) {
				LOGGER.error(documentManager,STLogEnumImpl.FAIL_UPDATE_FILE_TEC , e);
				facesMessages.add(StatusMessage.Severity.WARN, "Erreur lors de l'update du document");
			}
		}
	}
	
	public void renameDocument() {
		if (selectedNodeId != null && selectedNodeTitle != null) {
			DocumentModel document = getSelectedDocument(selectedNodeId);
			String newName = selectedNodeTitle;
			final SSTreeService ssTreeService = SSServiceLocator.getSSTreeService();
			resetErrorProperties();
			try {
				ssTreeService.renameDocument(documentManager, document, newName);
			} catch (ClientException sse) {
				facesMessages.add(StatusMessage.Severity.WARN, "Erreur lors de l'update du document");
			}
			resetProperties();
			treeChangeEvent();
		}
	}

	/**
	 * Listener sur le renommage du nom d'un document : on modifie la propriété "nom" associé au document dans le bean
	 * 
	 */
	public void nameChangeListener(final ValueChangeEvent event) throws Exception {
		// log.debug("nameChangeListener ");
		if (event != null && event.getNewValue() != null) {
			final String newValue = event.getNewValue().toString();
			setSelectedNodeTitle(newValue);
		}
	}

	public void concatAndRenameFile() {
		selectedNodeTitle = fileNameSansExtention + extentionFile;
		renameFile();
	}

	public String getFileNameSansExtention() {
		if (selectedNodeTitle != null) {
			if (selectedNodeTitle.contains(".")) {
				fileNameSansExtention = selectedNodeTitle.substring(0, selectedNodeTitle.lastIndexOf('.'));
			} else {
				fileNameSansExtention = selectedNodeTitle;
			}
		}
		return fileNameSansExtention;
	}

	public void setFileNameSansExtention(String fileNameSansExtention) {
		this.fileNameSansExtention = fileNameSansExtention;
	}

	public String getExtentionFile() {
		extentionFile = "";
		if (selectedNodeTitle != null && selectedNodeTitle.contains(".")) {
			extentionFile = selectedNodeTitle.substring(selectedNodeTitle.lastIndexOf('.'));
		}
		return extentionFile;
	}

	public void setExtentionFile(String extentionFile) {
		this.extentionFile = extentionFile;
	}
	
	/**
	 * Listener sur le renommage du nom d'un document hors extention : on modifie la propriété "nom" associé au document dans le bean
	 * 
	 */
	public void nameChangeListenerSansExtention(final ValueChangeEvent event) throws Exception {
		// log.debug("nameChangeListener ");
		if (event != null && event.getNewValue() != null) {
			final String newValue = event.getNewValue().toString();
			setFileNameSansExtention(newValue);
		}
	}

}
