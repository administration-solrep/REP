package fr.dila.ss.web.admin.utilisateur;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.filemanager.UploadItemHolder;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.domain.file.ExportBlob;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.user.STPrincipalImpl;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Web du Bean de gestion des utilisateurs.
 * 
 * @author jtremeaux
 */
@Name("userManagerActions")
@Scope(CONVERSATION)
@Install(precedence = FRAMEWORK + 2)
public class UserManagerActionsBean extends fr.dila.st.web.administration.utilisateur.UserManagerActionsBean {

	private static final long					serialVersionUID				= 1L;

	private static final STLogger				LOGGER							= STLogFactory
																						.getLog(UserManagerActionsBean.class);

	@In(create = true, required = true)
	protected transient CoreSession				documentManager;

	protected String							formObjetMail					= "";

	protected String							formTexteMail					= "";

	@In(create = true)
	protected transient ContentViewActions		contentViewActions;

	@In(create = true)
	protected transient DocumentsListsManager	documentsListsManager;

	@In(create = true, required = false)
	protected transient NavigationContextBean	navigationContext;

	@In(create = true, required = false)
	protected UploadItemHolder					fileUploadHolder;

	protected Boolean							isErrorOccurred;

	protected String							errorName;

	protected List<String>						listErrorName;

	protected String							acceptedTypes					= "";

	protected Blob								currentElement;

	protected List<Blob>						listFiles;

	protected String							removeFileName;

	/*
	 * Nombre de fichier téléchargeable par défaut
	 */
	protected int								uploadsAvailable				= 20;

	protected final String[]					forbiddenChars					= { "\'" };

	public static final String					ERROR_MSG_DOCUMENT_EMPTY		= "feedback.ss.document.tree.document.error.empty.form";

	public static final String					ERROR_MSG_NO_FILE_SELECTED		= "feedback.ss.document.tree.document.error.unselected.file";

	public static final String					ERROR_MSG_NAME_ALREADY_EXIST	= "feedback.ss.document.tree.document.error.existing.name";

	public static final String					ERROR_CREATION					= "feedback.ss.document.tree.document.error.creation";

	protected static final String				ERROR_MSG_NAME_INCORRECT		= "feedback.ss.document.tree.document.error.incorrect.name";

	protected static final String				ERROR_MSG_FILE_TOO_BIG			= "feedback.ss.document.tree.document.error.file.too.big";

	/**
	 * Default constructor
	 */
	public UserManagerActionsBean() {
		super();
	}

	@Override
	protected boolean getCanEditUsers(boolean allowCurrentUser) throws ClientException {
		if (userManager.areUsersReadOnly()) {
			return false;
		}
		if (currentUser instanceof STPrincipalImpl) {
			SSPrincipal pal = (SSPrincipal) currentUser;
			if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_UPDATER)) {
				return true;
			}

			// Test si l'utilisateur connecté peut modifier les utilisateurs
			// s'ils appartiennent au même ministère
			if (pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_MINISTERE_UPDATER)) {
				Set<String> currentUserMinistere = pal.getMinistereIdSet();

				SSPrincipal sel = (SSPrincipal) userManager.getPrincipal(selectedUser.getTitle());

				// si l'utilisateur selectionné est UtilisateurUpdater (admin fonctionnel), non modifiable
				if (sel.isMemberOf(STBaseFunctionConstant.UTILISATEUR_UPDATER)) {
					return false;
				}

				Set<String> selectedUserMinistere = sel.getMinistereIdSet();
				for (String ministereId : selectedUserMinistere) {
					if (currentUserMinistere.contains(ministereId)) {
						return true;
					}
				}
			}
			if (allowCurrentUser && selectedUser != null) {
				if (pal.getName().equals(selectedUser.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean getCanDeleteUsers(boolean allowCurrentUser) throws ClientException {
		if (selectedUser != null) {
			return canDeleteUser(selectedUser.getTitle());
		}
		return false;
	}

	private boolean canDeleteUser(String userToDelete) throws ClientException {
		if (userToDelete == null || userManager.areUsersReadOnly() || userToDelete.equals(currentUser.getName())) {
			return false;
		}
		if (currentUser instanceof STPrincipalImpl) {
			SSPrincipal pal = (SSPrincipal) currentUser;
			if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_DELETER)) {
				return true;
			}
			// Test si l'utilisateur connecté peut effacer les utilisateurs
			// s'ils appartiennent au même ministère
			if (pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_MINISTERE_DELETER)) {
				Set<String> currentUserMinistere = pal.getMinistereIdSet();

				SSPrincipal sel = (SSPrincipal) userManager.getPrincipal(userToDelete);

				// si l'utilisateur selectionné est UtilisateurDeleter (admin fonctionnel), non supprimable
				if (sel.isMemberOf(STBaseFunctionConstant.UTILISATEUR_DELETER)) {
					return false;
				}

				Set<String> selectedUserMinistere = sel.getMinistereIdSet();
				for (String ministereId : selectedUserMinistere) {
					if (currentUserMinistere.contains(ministereId)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Set<STUser> getSelectedUsers() {
		return null;
	}

	public String deleteSelectedUsers() throws ClientException {

		String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();

		if (!documentsListsManager.isWorkingListEmpty(selectionListName)) {
			List<DocumentModel> usersToDelete = documentsListsManager.getWorkingList(selectionListName);
			List<DocumentModel> usersDeleted = new ArrayList<DocumentModel>();
			List<String> userNamesForbidden = new ArrayList<String>();

			if (usersToDelete != null && !usersToDelete.isEmpty()) {
				for (DocumentModel user : usersToDelete) {
					if (canDeleteUser(user.getTitle())) {
						userManager.deleteUser(user.getTitle());
						usersDeleted.add(user);
					} else {
						userNamesForbidden.add(user.getTitle());
					}
				}

				if (usersDeleted.isEmpty()) {
					facesMessages.add(StatusMessage.Severity.WARN, "Aucun utilisateur n'a pu être supprimé.");
				} else {
					documentsListsManager.removeFromWorkingList(selectionListName, usersDeleted);
					users.removeAll(usersDeleted);
					facesMessages.add(StatusMessage.Severity.INFO, "Les utilisateurs sélectionnés ont été supprimés.");
				}
				if (!userNamesForbidden.isEmpty()) {
					facesMessages.add(StatusMessage.Severity.WARN, "Les utilisateurs suivants n'ont pas été supprimés (droits insuffisants) : " + StringUtil.join(userNamesForbidden, ", ", ""));
				}
			} else {
				facesMessages.add(StatusMessage.Severity.WARN, "Vous n'avez pas selectionné d'utilisateur.");
			}
		}

		return contentViewActions.getCurrentContentView().getName();
	}

	public String sendMail() throws Exception {

		final STMailService stMailService = STServiceLocator.getSTMailService();
		String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();
		if (!documentsListsManager.isWorkingListEmpty(selectionListName)) {

			List<DocumentModel> usersToMail = documentsListsManager.getWorkingList(selectionListName);

			List<String> recipients = new ArrayList<String>();
			for (DocumentModel user : usersToMail) {
				String email = user.getAdapter(STUser.class).getEmail();
				if (email != null && !email.isEmpty()) {
					recipients.add(email);
				}
			}
			List<ExportBlob> blobs = new ArrayList<ExportBlob>();
			if (listFiles != null && !listFiles.isEmpty()) {
				for (Blob document : listFiles) {
					blobs.add(new ExportBlob("Documents", document));
				}
			}
			if (recipients.size() > 0) {

				//FEV Envoi de mails en masse
				stMailService.sendMailWithAttachmentsAsBCC(recipients, formObjetMail, formTexteMail, blobs);
//				stMailService.sendMailWithAttachments(recipients, formObjetMail, formTexteMail, blobs);

				facesMessages.add(StatusMessage.Severity.INFO, "Le courrier a bien été envoyé");
			} else {
				facesMessages.add(StatusMessage.Severity.WARN, "Les utilisateurs selectionnés n'ont pas d'emails.");
			}
			formObjetMail = "";
			formTexteMail = "";
			listFiles = new ArrayList<Blob>();
		}
		return "view_recherche_utilisateur_resultats";
	}

	public void annulerMail() {
		formObjetMail = "";
		formTexteMail = "";
	}

	public void resetErrorProperties() {
		setIsErrorOccurred(false);
		setErrorName(null);
		setListErrorName(null);
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
	protected boolean verifyFileLength(CoreSession session, File fichier) {
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
		if (param == null || (fichier.getTotalSpace() <= (Long.parseLong(param) * 1048576))) {
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
			// get file info
			UploadItem item = event.getUploadItem();
			// get fileName
			String fileName = item.getFileName();
			if (!fileName.startsWith("/") || !fileName.startsWith("\\")) {
				fileName = "/" + fileName; // on remplace le slash initial pour éviter les errreurslors de la création
											// du zip
			}
			// get file content
			Blob blob = FileUtils.createSerializableBlob(new FileInputStream(item.getFile()), fileName, null);
			// delete file info
			item.getFile().delete();
			currentElement = blob;

			// If list is still null we have to intialize it or else with juste have to add new element
			if (getListFiles() == null) {
				setListFiles(new ArrayList<Blob>());
			}
			getListFiles().add(currentElement);

			--uploadsAvailable;
		}
	}

	public void setCurrentElement(Blob currentElement) {
		this.currentElement = currentElement;
	}

	public Blob getCurrentElement() {
		return currentElement;
	}

	protected boolean checkUploadAvailable(UploadEvent event) {
		if (event == null || event.getUploadItem() == null || event.getUploadItem().getFileName() == null) {
			errorName = "le fichier est vide !";
			setIsErrorOccurred(true);
			resetProperties();
			return false;
		}

		return true;
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
			if (getListFiles() != null) {
				for (Iterator<Blob> iterator = getListFiles().iterator(); iterator.hasNext();) {
					Blob documentItem = iterator.next();
					if (documentItem.getFilename().equals(removeFileName)) {
						iterator.remove();
					}
				}
			}
		} else {
			// otherwise remove all files
			setListFiles(null);
			LOGGER.info(documentManager, STLogEnumImpl.DEL_FILE_UPLOAD_FONC,
					"Suppression de tous les fichiers effectuée");
		}
		resetErrorProperties();
	}

	public void resetProperties() {
		currentElement = null;
		removeFileName = null;
		setListFiles(null);
	}

	/**
	 * Getter/setter pour les fichiers.
	 */

	public String getFormObjetMail() {
		return formObjetMail;
	}

	public void setFormObjetMail(String formObjetMail) {
		this.formObjetMail = formObjetMail;
	}

	public String getFormTexteMail() {
		return formTexteMail;
	}

	public void setFormTexteMail(String formTextMail) {
		this.formTexteMail = formTextMail;
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(final int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
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

	public void setListErrorName(List<String> listErrorName) {
		this.listErrorName = listErrorName;
	}

	public List<String> getListErrorName() {
		return listErrorName;
	}

	public String getErrorName() {
		if (getListErrorName() != null && getListErrorName().size() > 0) {
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

	public Collection<UploadItem> getUploadedFiles() {
		if (fileUploadHolder != null) {
			return fileUploadHolder.getUploadedFiles();
		} else {
			return null;
		}
	}

	public void setUploadedFiles(final Collection<UploadItem> uploadedFiles) {
		if (fileUploadHolder != null) {
			Collection<UploadItem> alreadyUploadedItems = getUploadedFiles();
			alreadyUploadedItems.addAll(uploadedFiles);
			fileUploadHolder.setUploadedFiles(alreadyUploadedItems);
		}
	}

	public void setListFiles(List<Blob> listFiles) {
		this.listFiles = listFiles;
	}

	public List<Blob> getListFiles() {
		return listFiles;
	}

	public String getRemoveFileName() {
		return removeFileName;
	}

	public void setRemoveFileName(String removeFileName) {
		this.removeFileName = removeFileName;
	}

	/**
	 * Permet de définir les types de fichiers acceptés (pour la FEV 410 il n'y avait pas de limitation, la fonction
	 * renvoit donc la valeur par défaut (vide).
	 * 
	 * @return acceptedTypes
	 */
	public String getAcceptedTypes() throws ClientException {
		return acceptedTypes;
	}

	public void setAcceptedTypes(final String acceptedTypes) {
		this.acceptedTypes = acceptedTypes;
	}

	public void selectAll() {
		String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();
		List<DocumentModel> workingList = documentsListsManager.getWorkingList(selectionListName);
		if (workingList == null || workingList.isEmpty()) {
			documentsListsManager.addToWorkingList(selectionListName, users);
		} else {
			documentsListsManager.setWorkingList(selectionListName, users);
		}
	}

	public void deselectAll() {
		String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();
		documentsListsManager.setWorkingList(selectionListName, new ArrayList<DocumentModel>());
	}
}
