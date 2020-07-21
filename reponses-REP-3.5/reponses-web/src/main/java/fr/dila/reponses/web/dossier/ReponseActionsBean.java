package fr.dila.reponses.web.dossier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.web.dossier.DossierLockActionsBean;

@Name("reponseActions")
@Scope(ScopeType.CONVERSATION)
/**
 * Bean regroupant les actions sur les réponses d'un dossier et leurs version
 */
public class ReponseActionsBean implements Serializable {

	private static final long					serialVersionUID	= 2039040947678396838L;

	private static final Log					log					= LogFactory.getLog(ReponseActionsBean.class);

	private static final String					REPONSE_MODIFIED	= "La réponse a été enregistrée";

	public static final String					REPONSE_UPDATED		= "ReponseUpdatedEvent";

	private static final String					REPONSE_BRISER		= "label.journal.comment.removeSignature";

	@In(create = true, required = false)
	protected transient FacesMessages			facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor		resourcesAccessor;

	@In(create = true, required = true)
	protected transient CoreSession				documentManager;

	@In(required = true, create = true)
	protected transient SSPrincipal				ssPrincipal;

	@In(create = true, required = false)
	protected transient NavigationContext		navigationContext;

	@In(create = true, required = false)
	protected transient DossierLockActionsBean	dossierLockActions;

	@In(create = true, required = false)
	protected transient DossierActionsBean		dossierActions;

	/**
	 * Reponse document link to the current Dossier document.
	 */
	protected DocumentModel						reponse;

	/**
	 * current displayed Reponse document : one of the reponse version.
	 */
	protected DocumentModel						currentReponse;

	protected List<DocumentModel>				listReponseVersion;

	protected transient Long					currentReponseNumeroVersion;

	protected Boolean							editingReponse		= false;

	/**
	 * Retourne le Numéro de la version de la réponse consultée Si currentReponseNumeroVersion est null, renvoie le
	 * numéro de version majeure
	 */
	@Factory(value = "currentReponseNumeroVersion", scope = ScopeType.EVENT)
	public Long getCurrentReponseNumeroVersion() {
		return currentReponseNumeroVersion;
	}

	/**
	 * Définit le numéro de la version consultée.
	 * 
	 * @param numeroVersion
	 * @throws ClientException
	 */
	public void setCurrentReponseNumeroVersion(Long numeroVersion) throws ClientException {
		if (log.isDebugEnabled()) {
			log.debug("setCurrentReponseNumeroVersion(" + numeroVersion + ")");
		}
		int lastVersionNumber = getReponseMajorVersionNumber();
		if (log.isDebugEnabled()) {
			log.debug("setCurrentReponseNumeroVersion(" + numeroVersion + ") vn=" + lastVersionNumber + " -- cur="
					+ currentReponseNumeroVersion);
		}
		// compare to lastVersion + 1 : lastVersion + 1 sera le numero de version de la version courante editable
		if (numeroVersion != null && numeroVersion <= lastVersionNumber + 1
				&& !numeroVersion.equals(currentReponseNumeroVersion)) {
			currentReponseNumeroVersion = numeroVersion;
		} else {
			throw new ClientException("Param numeroVersion : '" + numeroVersion + "' is not valid.");
		}
	}

	/**
	 * 
	 * @return true si currentReponse correspond a la reponse editable par l'utilisateur (soit la dernière version)
	 */
	public Boolean isReponseAtLastVersion() {
		try {
			Boolean res = getReponseMajorVersionNumber() + 1 == getCurrentReponseNumeroVersion().intValue();
			if (log.isDebugEnabled()) {
				log.debug("in isReponseAtLastVersion --> " + res);
			}
			return res;
		} catch (ClientException e) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("feedback.reponses.document.reponse.version.error"));
			log.error("Exception reponseAtLastVersion", e);
			return false;
		}
	}

	/**
	 * Get the current Reponse From Dossier .
	 * 
	 * @throws ClientException
	 */
	@Factory(value = "reponse", scope = ScopeType.EVENT)
	public DocumentModel getReponse() throws ClientException {
		DocumentModel doc = navigationContext.getCurrentDocument();
		final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		DocumentModel reponseTemp = reponseService.getReponseFromDossier(documentManager, doc);

		// recupere la reponse dans tous les cas pour verifier
		// qu'on n'a pas changé de document et donc de reponse
		// change les attributs si la reponse était null ou a changé (changement de dossier selectionné)

		if (this.reponse == null || !reponseTemp.getId().equals(this.reponse.getId())) {
			this.reponse = reponseTemp;
			if (dossierActions.isDossierContainsMinistere()) {
				this.currentReponse = reponseTemp;
				this.currentReponseNumeroVersion = Long.valueOf(reponseService.getReponseMajorVersionNumber(
						documentManager, reponse) + 1); // +1 car correspond au prochain numero de version qui sera
														// appliqué à la reponse en cours d'edition
			} else {
				this.currentReponseNumeroVersion = Long.valueOf(reponseService.getReponseMajorVersionNumber(
						documentManager, reponse));
			}
		}
		return this.reponse;
	}

	/**
	 * Get the Question Number Max Length.
	 * 
	 * @throws ClientException
	 */
	public String getQuestionNumberMaxLength() throws ClientException {
		return String.valueOf(ReponsesConstant.DOSSIER_QUESTION_NUMBER_MAX_LENGTH);
	}

	/**
	 * Get all the reponse version From a Dossier.
	 * 
	 */
	public List<DocumentModel> getReponseVersionList() throws ClientException {
		DocumentModel reponseTemp = getReponse();

		final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		List<DocumentModel> reponseList = reponseService.getReponseVersionDocumentList(documentManager, reponseTemp);
		// add the new editable version to the previous version
		if (dossierActions.isDossierContainsMinistere()) {
			reponseList.add(reponse);
		}

		this.listReponseVersion = reponseList;
		if (log.isDebugEnabled()) {
			log.debug("in getReponseVersionList list size:" + reponseList.size());
		}
		return reponseList;
	}

	/**
	 * Get a reponse version From a Dossier.
	 * 
	 */
	@Factory(value = "currentReponse", scope = ScopeType.EVENT)
	public DocumentModel getCurrentReponse() throws ClientException {
		// get reponse from dossier

		Long Version = Long.valueOf(getReponseMajorVersionNumber());
		Long currentVersionValue = getCurrentReponseNumeroVersion();
		if (log.isDebugEnabled()) {
			log.debug("getCurrentReponse for version :" + currentVersionValue);
		}

		if (dossierActions.isDossierContainsMinistere()) {
			if (currentVersionValue.equals(Version + 1)) {
				this.currentReponse = getReponse();
			} else {
				final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
				this.currentReponse = reponseService.getReponseOldVersionDocument(documentManager, getReponse(),
						currentVersionValue.intValue());
			}
		} else {
			try {
				final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
				this.currentReponse = reponseService.getReponseOldVersionDocument(documentManager, getReponse(),
						currentVersionValue.intValue());
			} catch (ClientException e) {
				this.currentReponse = null;
			}
		}
		currentReponseNumeroVersion = currentVersionValue;
		return currentReponse;
	}

	/**
	 * Get a reponse version From a Dossier document.
	 * 
	 */
	public int getReponseMajorVersionNumber() throws ClientException {
		return getReponseMajorVersionNumber(getReponse());
	}
	
	private int getReponseMajorVersionNumber(DocumentModel reponseDoc) throws ClientException {
		final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		int versionNumber = reponseService.getReponseMajorVersionNumber(documentManager, reponse);
		if (log.isDebugEnabled()) {
			log.debug("major version number = " + versionNumber);
		}
		return versionNumber;
	}

	public Boolean isReponseViewable() throws ClientException {
		// La réponse est visible, si elle est publiée ou si elle est editable pour l'utilisateur
		if (currentReponse != null && documentManager != null) {
			Reponse reponse = currentReponse.getAdapter(Reponse.class);
			Boolean isReponseEditable = documentManager.hasPermission(navigationContext.getCurrentDocument().getRef(),
					SecurityConstants.WRITE);
			return reponse.isPublished() || isReponseEditable;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * 
	 * @return true si il y a eu une modification de la réponse
	 * @throws ClientException
	 * @throws PropertyException
	 */
	public Boolean reponseHasChanged() throws PropertyException, ClientException {
		DocumentModel reponse = getReponse();
		Reponse currentReponse = reponse.getAdapter(Reponse.class);
		String currentTextReponse = currentReponse.getTexteReponse();
		int reponseVersionNumber = getReponseMajorVersionNumber(reponse);
		// reponseVersionNumber = 0 si pas encore de reponse versionnée => testé si version courante est no nulle
		// reponseVersionNumber > 0 si deja au moins une reponse versionné => comparé le contenue de la reponse
		if (reponseVersionNumber > 0) {
			final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
			// recupere le dernier snapshot pour comparer le contenu de la reponse
			DocumentModel reponseOldVersionDocument = reponseService.getReponseOldVersionDocument(documentManager,
					reponse, reponseVersionNumber);
			Reponse reponseOldVersion = reponseOldVersionDocument.getAdapter(Reponse.class);
			String lastTextReponse = reponseOldVersion.getTexteReponse();

			if (StringUtils.isBlank(lastTextReponse)) {
				lastTextReponse = "";
			}

			return !lastTextReponse.equals(currentTextReponse);

		} else {
			// pas de réponse produite si currentTextReponse == null dans ce la reponse n'a pas changé
			// sinon renvoyé vrai
			return currentTextReponse != null;
		}
	}

	/**
	 * unlock the dossier if we have the right.
	 */
	public String quitEditingReponse() throws ClientException {
		editingReponse = false;
		reponse = null;
		dossierLockActions.unlockCurrentDossier();

		// Refresh de la contentview
		// Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);

		// Refresh de la reponse
		Events.instance().raiseEvent(STEventConstant.CURRENT_DOCUMENT_CHANGED_EVENT);

		return null;
	}

	/**
	 * Sauvegarde les modifications de la réponse.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String saveReponse() throws ClientException {
		// get the current reponse value
		reponse = currentReponse;

		// Sauvegarde la reponse
		final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		reponseService.saveReponseAndErratum(documentManager, reponse, navigationContext.getCurrentDocument());

		if (reponse != null && reponse.hasSchema(DossierConstants.REPONSE_DOCUMENT_SCHEMA)) {
			Reponse rep = reponse.getAdapter(Reponse.class);
			if (rep.getSignature() == null) {
				facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(REPONSE_MODIFIED),
						resourcesAccessor.getMessages().get(reponse.getType()));
			}
		}

		// update current version : the new displayed reponse must be the last
		currentReponseNumeroVersion = (long) (getReponseMajorVersionNumber() + 1);

		return null;
	}

	public String cancelReponse() throws ClientException {
		return quitEditingReponse();
	}

	/**
	 * Verrouille la réponse et renvoie le bon document
	 * 
	 * @throws ClientException
	 */
	public String editReponse() throws ClientException {
		editingReponse = true;
		dossierLockActions.lockCurrentDossier();

		// Refresh de la contentview
		// Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);

		// Refresh de la reponse
		Events.instance().raiseEvent(STEventConstant.CURRENT_DOCUMENT_CHANGED_EVENT);

		return null;
	}

	public void resetReponse() throws ClientException {
		if (dossierLockActions.getCanUnlockCurrentDossier()) {
			dossierLockActions.unlockCurrentDossier();
		}

		editingReponse = false;

		reponse = null;

		currentReponse = null;

		currentReponseNumeroVersion = null;

		listReponseVersion = new ArrayList<DocumentModel>();
	}

	public Boolean getEditingReponse() {
		return editingReponse;
	}

	public void setEditingReponse(Boolean editingReponse) {
		this.editingReponse = editingReponse;
	}

	@Observer(REPONSE_UPDATED)
	public void resetReponseOnEvent() {
		reponse = null;
	}

	/**
	 * Supression du cchet serveur (signature) sur la reponse
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String briserReponse() throws ClientException {
		// get the current reponse value
		reponse = currentReponse;

		if (reponse != null && reponse.hasSchema(DossierConstants.REPONSE_DOCUMENT_SCHEMA)) {
			// Sauvegarde la reponse
			final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
			reponseService.briserSignatureReponse(documentManager, reponse.getAdapter(Reponse.class),
					navigationContext.getCurrentDocument());

			// Invalide le dossier
			navigationContext.invalidateCurrentDocument();
			facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(REPONSE_BRISER),
					resourcesAccessor.getMessages().get(reponse.getType()));
			EventManager.raiseEventsOnDocumentChange(reponse);

			// update current version : the new displayed reponse must be the last
			currentReponseNumeroVersion = (long) (getReponseMajorVersionNumber() + 1);
		}
		return null;
	}

	/**
	 * Retourne vraie si la réponse est publiée
	 * 
	 * @return
	 * @throws ClientException
	 */
	public Boolean isReponsePublished() throws ClientException {
		DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		if (dossierDoc != null && dossierDoc.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
			ReponseService reponseService = ReponsesServiceLocator.getReponseService();
			DocumentModel reponseDoc = reponseService.getReponseFromDossier(documentManager, dossierDoc);
			if (reponseDoc != null) {
				Reponse reponse = reponseDoc.getAdapter(Reponse.class);
				if (reponse != null) {
					return reponse.isPublished();
				}
			}
		}
		return false;
	}

	/**
	 * Retourne vraie si la réponse a du texte
	 * 
	 * @return
	 * @throws ClientException
	 */
	public Boolean reponseHasTexte() throws ClientException {
		if (currentReponse != null) {
			Reponse rep = currentReponse.getAdapter(Reponse.class);
			return !(rep.getTexteReponse() == null || rep.getTexteReponse().isEmpty());
		}
		return true;
	}

	/**
	 * Retourne vrai si la réponse possède un erratum
	 * 
	 * @return
	 */
	public Boolean reponseHasErratum() {
		if (currentReponse != null) {
			Reponse rep = currentReponse.getAdapter(Reponse.class);
			if (rep.getErrata() == null) {
				return false;
			}
			return rep.getErrata().size() > 0;
		}
		return false;
	}
}
