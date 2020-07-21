package fr.dila.reponses.web.fonddossier;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.usermanager.exceptions.GroupAlreadyExistsException;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.ss.api.constant.SSFondDeDossierConstants;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.web.tree.SSTreeManagerActionsBean;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.StringUtil;

/**
 * ActionBean de gestion de l'arborescence du fond de dossier.
 * 
 */
@Synchronized(timeout = 10000)
@Name("fondDeDossierManagerActions")
@Scope(ScopeType.SESSION)
public class FondDeDossierManagerActionsBean extends SSTreeManagerActionsBean implements Serializable {

	private static final long					serialVersionUID					= -3486558978767123945L;

	private static final STLogger				LOGGER								= STLogFactory
																							.getLog(FondDeDossierManagerActionsBean.class);

	@In(required = false, create = true)
	protected transient CorbeilleActionsBean	corbeilleActions;

	private String								niveauVisibilite;

	// file properties
	public static final String					ERROR_MSG_NO_VISIBILITY_SELECTED	= "feedback.reponses.document.fdd.document.error.unselected.visibility";

	protected String							acceptedTypes						= ReponsesConstant.ALLOWED_UPLOAD_FILE_TYPE;

	public String								typeAction;

	public List<SelectItem>						selectItems;

	@Remove
	@Destroy
	public void destroy() {
		LOGGER.debug(STLogEnumImpl.DESTROY_FDD_MANAGER_BEAN_TEC);
	}

	/*
	 * (non-Javadoc)
	 * @see fr.dila.ss.web.tree.SSTreeManagerActionsBean#deleteFile()
	 */
	@Override
	public void deleteFile() throws ClientException {

		DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();

		fondDeDossierService.logDeleteFileFromFDD(dossierDoc, selectedNodeName, documentManager);
		super.deleteFile();
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		if (!fondDeDossierService.havePieceJointeDossier(documentManager, dossier)) {
			// si plus de PJ, on passe hasPJ à false
			dossier.setHasPJ(false);
			dossier.save(documentManager);
		}

	}

	/**
	 * Reset the temp fdd properties
	 * 
	 */
	@Override
	public void resetProperties() {
		super.resetProperties();
		resetFileUploadProperties();
		niveauVisibilite = null;
	}

	public void resetAllProperties() {
		super.resetProperties();
		resetFileUploadProperties();
		resetErrorProperties();
		niveauVisibilite = null;
	}

	/**
	 * Reset the file fdd properties
	 * 
	 */
	@Override
	public void resetFileUploadProperties() {
		uploadsAvailable = ReponsesConstant.MAX_FILE_UPLOAD_FOND_DOSSIER;
	}

	/**
	 * Retourne vrai si l'utilisateur a le droit de modifier le fond de dossier.
	 * 
	 * @throws ClientException
	 */
	public boolean canUserUpdateFondDossier() throws ClientException {
		// L'utilisateur ne peut pas modifier les dossiers à l'état terminé
		DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		if (dossier.isDone()) {
			return false;
		}

		// L'utilisateur peut uniquement modifier les dossiers qu'il a verrouillé
		if (!lockActions.getCanUnlockCurrentDoc()) {
			return false;
		}

		// L'administrateur fonctionnel peut modifier les fonds de dossier
		if (ssPrincipal.getGroups().contains(ReponsesBaseFunctionConstant.FOND_DOSSIER_ADMIN_UPDATER)) {
			return true;
		}

		// L'administrateur ministériel peut modifier les fonds de dossier des dossiers de ses ministères
		if (ssPrincipal.getGroups().contains(ReponsesBaseFunctionConstant.FOND_DOSSIER_ADMIN_MIN_UPDATER)
				&& ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant())) {
			return true;
		}

		// L'utilisateur lambda peut modifier les fond de dossier des dossiers qui lui sont distribués
		return corbeilleActions.isDossierLoadedInCorbeille();
	}

	@Override
	protected boolean checkFileCreation() throws ClientException {
		if (super.checkFileCreation()) {
			if (getCurrentElement().getProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_FILENAME_PROPERTY) == null) {
				getListErrorName().add(resourcesAccessor.getMessages().get(ERROR_MSG_NO_FILE_SELECTED));
				setIsErrorOccurred(true);
				return false;
			} else if (!isFondDeDossierFileNameUnique()) {
				getListErrorName().add(resourcesAccessor.getMessages().get(ERROR_MSG_NAME_ALREADY_EXIST));
				setIsErrorOccurred(true);
				return false;
			} else if (!isFondDeDossierFileNameCorrect()) {
				getListErrorName().add(resourcesAccessor.getMessages().get(ERROR_MSG_NAME_INCORRECT));
				setIsErrorOccurred(true);
				return false;
			} else if (getCurrentElement().getProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
					ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE) == null
					&& niveauVisibilite == null) {
				getListErrorName().add(resourcesAccessor.getMessages().get(ERROR_MSG_NO_VISIBILITY_SELECTED));
				setIsErrorOccurred(true);
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Check if FondDeDossier FileName is unique.
	 * 
	 * @throws ClientException
	 */
	protected Boolean isFondDeDossierFileNameUnique() throws ClientException {
		// get current Dossier from session
		FondDeDossier fondDeDossierDoc = getFondDeDossierCourant();

		// check if FondDeDossier FileName is unique
		final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
		return fondDeDossierService.isFondDeDossierFileNameUnique(fondDeDossierDoc, getCurrentElement(),
				documentManager);
	}

	/**
	 * Check if FondDeDossier FileName is unique.
	 * 
	 * @throws ClientException
	 */
	protected Boolean hasFondDeDossierFileVisibilityChanged() throws ClientException {
		String oldNiveauVisibilite = null;
		if (getSelectedNodeId() != null) {
			DocumentModel selectedDoc = getSelectedDocument(getSelectedNodeId());
			if (selectedDoc != null) {
				oldNiveauVisibilite = (String) getSelectedDocument(getSelectedNodeId()).getProperty(
						DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
						ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE);
			}
		}

		if (oldNiveauVisibilite == null) {
			return false;
		} else {
			return !oldNiveauVisibilite.equals(getCurrentElement().getProperty(
					DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
					ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE));
		}
	}

	/**
	 * Get current Fond De Dossier from session.
	 * 
	 * @throws ClientException
	 */
	protected FondDeDossier getFondDeDossierCourant() throws ClientException {
		FondDeDossier fondDeDossierDoc = null;
		// get current Dossier from session
		DocumentModel doc = navigationContext.getCurrentDocument();
		Dossier dossierDoc = doc.getAdapter(Dossier.class);
		// get FondDeDossier Document from dossier
		fondDeDossierDoc = dossierDoc.getFondDeDossier(documentManager);
		return fondDeDossierDoc;
	}

	/**
	 * Mise jour d'un noeud dans l'arborescence du fond de dossier.
	 * 
	 * @return null
	 * @throws ClientException
	 * @author ARN
	 */
	public String updateNode() throws ClientException {
		LOGGER.info(documentManager, STLogEnumImpl.UPDATE_FILE_FONC);
		if (getCurrentFondDeDossierElement() == null) {
			errorName = resourcesAccessor.getMessages().get(ERROR_MSG_DOCUMENT_EMPTY);
			setIsErrorOccurred(true);
		} else if (getCurrentFondDeDossierElement().getProperty(STSchemaConstant.FILE_SCHEMA,
				STSchemaConstant.FILE_FILENAME_PROPERTY) == null) {
			errorName = resourcesAccessor.getMessages().get(ERROR_MSG_NO_FILE_SELECTED);
			setIsErrorOccurred(true);
		} else if (getCurrentFondDeDossierElement().getProperty(
				DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE) == null
				&& niveauVisibilite == null) {
			errorName = resourcesAccessor.getMessages().get(ERROR_MSG_NO_VISIBILITY_SELECTED);
			setIsErrorOccurred(true);
		} else if (!isFondDeDossierFileNameCorrect()) {
			errorName = resourcesAccessor.getMessages().get(ERROR_MSG_NAME_INCORRECT);
			setIsErrorOccurred(true);
		} else {
			// if niveauVisibilite is not defined, we get it
			if (getCurrentFondDeDossierElement().getProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
					ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE) == null) {
				getCurrentFondDeDossierElement().setProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
						ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE, niveauVisibilite);
			}
			final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
			if (!isFondDeDossierFileNameUnique()) {
				if (uploadsAvailable != 0 && hasFondDeDossierFileVisibilityChanged()) {
					// move the file
					try {
						// get current Dossier from session
						FondDeDossier fondDeDossierDoc = getFondDeDossierCourant();
						String newNiveauVisibilite = (String) getCurrentElement().getProperty(
								DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
								ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE);
						fondDeDossierService.moveFddFile(documentManager, fondDeDossierDoc,
								getSelectedDocument(getSelectedNodeId()), navigationContext.getCurrentDocument(),
								newNiveauVisibilite);
						treeChangeEvent();
						resetErrorProperties();
					} catch (GroupAlreadyExistsException gaee) {
						setIsErrorOccurred(true);
						facesMessages.add(StatusMessage.Severity.WARN, "erreur lors de la modification");
					}
				} else {
					// the fileName is not unique and the visibility level has not changed => error
					errorName = resourcesAccessor.getMessages().get(ERROR_MSG_NAME_ALREADY_EXIST);
					setIsErrorOccurred(true);
				}
			} else {
				try {
					fondDeDossierService.updateFddFile(documentManager, getSelectedDocument(getSelectedNodeId()),
							getCurrentFondDeDossierElement(), navigationContext.getCurrentDocument());
					treeChangeEvent();
					resetErrorProperties();
				} catch (GroupAlreadyExistsException gaee) {
					setIsErrorOccurred(true);
					facesMessages.add(StatusMessage.Severity.WARN, "erreur lors de la modification");
				}
			}
		}
		// si la methode s'est executée avec erreur, on nettoie les infos de l'upload
		if (isErrorOccurred) {
			resetFileUploadProperties();
		} else {
			resetProperties();
		}
		return null;
	}

	/**
	 * Get the current Fond de Dossier element stored in session.
	 * 
	 * @throws ClientException
	 */
	@Factory(value = "currentElement", scope = EVENT)
	public DocumentModel getCurrentFondDeDossierElement() throws ClientException {
		// get fond de dossier element
		if (getCurrentElement() == null) {
			if (typeAction == null) {
				// do nothing
			} else if (typeAction.equals("create")) {
				this.initCurrentFondDeDossierElement();
			} else if (selectedNodeId != null && (typeAction.equals("update") || typeAction.equals("delete"))) {
				this.getCurrentFondDeDossierElementFromId(selectedNodeId);
			}
		}
		return getCurrentElement();
	}

	/**
	 * Get the current Fond de Dossier element stored in session.
	 * 
	 * @throws ClientException
	 */
	public DocumentModel getCurrentFondDeDossierElementFromId(String idFddElement) throws ClientException {
		// get fond de dossier element from session
		DocumentRef docRef = new IdRef(idFddElement);
		DocumentModel currentFondDeDossierElement = documentManager.getDocument(docRef);
		this.setCurrentElement(currentFondDeDossierElement);
		return currentFondDeDossierElement;
	}

	/**
	 * Init the current Fond de Dossier element.
	 * 
	 */
	@Factory(value = "initFondDeDossierFichier", scope = EVENT)
	public DocumentModel initCurrentFondDeDossierElement() throws ClientException {
		// init fond de dossier element with service
		if (getCurrentElement() == null) {
			final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
			DocumentModel elementFondDeDossierModel = fondDeDossierService
					.createBareFondDeDossierFichier(documentManager);
			this.setCurrentElement(elementFondDeDossierModel);
		}
		return getCurrentElement();
	}

	public String getNiveauVisibilite() throws ClientException {
		if (typeAction == null) {
			niveauVisibilite = "default value";
		} else if (typeAction.equals("create")
				&& (niveauVisibilite == null || niveauVisibilite.equals("default value"))) {
			final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
			niveauVisibilite = fondDeDossierService.getDefaultNiveauVisibiliteFromRepository(selectedNodeId,
					documentManager);
			setCurrentElement(getCurrentFondDeDossierElement());
			getCurrentElement().setProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
					ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE, niveauVisibilite);
		} else if (typeAction.equals("update")
				&& (niveauVisibilite == null || niveauVisibilite.equals("default value"))) {
			if (getSelectedNodeId() != null) {
				DocumentModel selectedDoc = getSelectedDocument(getSelectedNodeId());
				if (selectedDoc != null) {
					niveauVisibilite = (String) getSelectedDocument(getSelectedNodeId()).getProperty(
							DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
							ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE);
				} else {
					niveauVisibilite = "default value";
				}
			}
		}

		return niveauVisibilite;
	}

	public void setNiveauVisibilite(String niveauVisibilite) {
		this.niveauVisibilite = niveauVisibilite;
	}

	public List<SelectItem> getSelectItems() throws ClientException {
		if (selectItems == null) {
			selectItems = new ArrayList<SelectItem>();
			final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();

			FondDeDossier currentFondDeDossier = getFondDeDossierCourant();

			List<FondDeDossierFolder> repertoiresRacine = fondDeDossierService.getVisibleChildrenFolder(
					documentManager, currentFondDeDossier.getDocument(), ssPrincipal);
			for (FondDeDossierFolder elementFddNoeud : repertoiresRacine) {
				selectItems.add(new SelectItem(fondDeDossierService.getDefaultNiveauVisibiliteFromRepository(
						elementFddNoeud.getId(), documentManager)));
			}
		}
		return selectItems;
	}

	/**
	 * Listener called when user change fichier fdd niveauVisibilite.
	 * 
	 */
	public void valueListener(ValueChangeEvent event) throws Exception {
		if (event != null && event.getNewValue() != null && event.getNewValue().toString() != null) {
			String newValue = event.getNewValue().toString();
			setCurrentElement(getCurrentFondDeDossierElement());
			if (getCurrentElement() != null) {
				getCurrentElement().setProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
						ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE, newValue);
				this.niveauVisibilite = newValue;
			} else {
				errorName = "une erreur est survenue lors de la selection : veuillez reselectionner le niveau de visibilite";
			}
		}
	}

	public void setTypeAction(String typeAction) {
		this.typeAction = typeAction;
	}

	public String getTypeAction() {
		return typeAction;
	}

	public String getChoixSuppression() {
		if (choixSuppression == null) {
			final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
			choixSuppression = fondDeDossierService.getDefaultChoixSuppressionFichierFondDeDossier(numeroVersion);
		}
		return choixSuppression;
	}

	public void setChoixSuppression(String choixSuppression) {
		this.choixSuppression = choixSuppression;
	}

	public List<SelectItem> getSelectItemsDelete() throws ClientException {
		selectItemsDelete = new ArrayList<SelectItem>();
		final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
		List<String> choixSuppressionLabelList = fondDeDossierService
				.getChoixSuppressionFichierFondDeDossierList(numeroVersion);
		for (String choixSuppressionLabel : choixSuppressionLabelList) {
			selectItemsDelete.add(new SelectItem(choixSuppressionLabel));
		}

		return selectItemsDelete;
	}

	public void setSelectItemsDelete(List<SelectItem> selectItemsDelete) {
		this.selectItemsDelete = selectItemsDelete;
	}

	public String getAcceptedTypes() {
		return acceptedTypes;
	}

	/**
	 * Réduit une String dont la taille dépasse 15 caractères. Utilisée pour les noms de fichiers trop long
	 * 
	 * @param name
	 *            chaine à réduire
	 * @return String chaine réduite si initialement supérieure à 15 caractères, sinon renvoyée à l'identique
	 */
	public String getShorterName(String name) {
		return StringUtil.getShorterName(name);
	}

	/**
	 * Check if FondDeDossier FileName is correct.
	 * 
	 * @throws ClientException
	 */
	protected Boolean isFondDeDossierFileNameCorrect() throws ClientException {
		return ReponsesServiceLocator.getFondDeDossierService().isFondDeDossierFileNameCorrect(
				getFondDeDossierCourant(), getCurrentElement());
	}

	@Override
	protected void createSpecificFile(SSTreeFile fichier, String filename) throws ClientException {
		// get current Dossier from session
		FondDeDossier fondDeDossierDoc = getFondDeDossierCourant();
		final FondDeDossierService fddService = ReponsesServiceLocator.getFondDeDossierService();
		fddService.createFondDeDossierFile(documentManager, fondDeDossierDoc.getDocument(), fichier.getFilename(),
				niveauVisibilite, fichier.getContent());
	}

	@Override
	protected DocumentModel createBareFile() throws SSException {
		final FondDeDossierService fddService = ReponsesServiceLocator.getFondDeDossierService();
		DocumentModel bareFile = fddService.createBareFondDeDossierFichier(documentManager);
		try {
			bareFile.setProperty(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE,
					ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE, niveauVisibilite);
			DocumentModel dossierDoc = documentManager.getParentDocument(getFondDeDossierCourant().getDocument()
					.getRef());
			Dossier dossier = dossierDoc.getAdapter(Dossier.class);
			String idMinistere = dossier.getIdMinistereAttributaireCourant();
			bareFile.setProperty(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE,
					ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE, idMinistere);
		} catch (ClientException exc) {
			throw new SSException("Impossible de renseigner la propriété", exc);
		}
		return bareFile;
	}

	@Override
	protected void treeChangeEvent() {
		super.treeChangeEvent();
		Events.instance().raiseEvent(SSFondDeDossierConstants.FOND_DE_DOSSIER_TREE_CHANGED_EVENT);
	}
}
