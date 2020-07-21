package fr.dila.reponses.web.utilisateur;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.time.DateUtils;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.component.list.UIEditableList;
import org.nuxeo.ecm.platform.usermanager.exceptions.UserAlreadyExistsException;

import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.ReponsesUserManager;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STUserManager;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

@Name("userManagerActions")
@Scope(CONVERSATION)
@Install(precedence = Install.APPLICATION)
public class UserManagerActionsBean extends fr.dila.ss.web.admin.utilisateur.UserManagerActionsBean {

	private static final long			serialVersionUID	= 656312626467063386L;

	private static final STLogger		LOGGER				= STLogFactory.getLog(UserManagerActionsBean.class);

	@In(create = true, required = false)
	protected CoreSession				documentManager;

	@In(create = true)
	protected UtilisateurActionsBean	utilisateurActions;
	
	/**
     * Id of the editable list component where selection ids are put.
     * <p>
     * Component must be an instance of {@link UIEditableList}
     */
    @RequestParameter
    protected String suggestionSelectionListId;
    
    protected String selectedValue;


	/**
	 * Renvoi un utilisateur occasionnel vide pour sa création avec une date fin dans 6 mois.
	 * 
	 * @return newUser
	 * @throws ClientException
	 */
	public DocumentModel getNewUserOccasional() throws ClientException {
		if (newUser == null) {
			newUser = userManager.getBareUserModel();

			STUser user = newUser.getAdapter(STUser.class);
			user.setTemporary(true);
			user.setOccasional(true);
			user.setDateFin(getDatePlusSixMonth());
			user.setDateDebut(Calendar.getInstance());
		}

		return newUser;
	}

	/**
	 * Retourne la vue create_user_occasional
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String getCreateUserOccasionalView() throws ClientException {
		// pour initialiser le document
		newUser = null;
		getNewUserOccasional();
		return ReponsesViewConstant.CREATE_USER_OCCASIONAL;
	}

	/**
	 * Validation de la date de fin pour la création d'utilisateur temporaire
	 * 
	 * @param facesContext
	 * @param uIComponent
	 * @param object
	 * @throws ValidatorException
	 * 
	 */
	@Override
	public void validateDateFin(FacesContext facesContext, UIComponent component, Object object)
			throws ValidatorException {
		Date inDateFin = (Date) object;
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

		if (newUser != null) {
			STUser user = newUser.getAdapter(STUser.class);
			if (user.isOccasional()) {
				if (inDateFin == null) {
					FacesMessage message = new FacesMessage();
					message.setSummary("Un utilisateur temporaire doit avoir une date de fin.");
					throw new ValidatorException(message);
				} else if (inDateFin.compareTo(today) < 0) {
					FacesMessage message = new FacesMessage();
					message.setSummary("La date de fin doit être au moins égale à la date du jour");
					throw new ValidatorException(message);
				} else if (inDateFin.after(getDatePlusSixMonth().getTime())) {
					FacesMessage message = new FacesMessage();
					message.setSummary("La date de fin doit être au plus dans les six mois à venir");
					throw new ValidatorException(message);
				}
			} else {
				super.validateDateFin(facesContext, component, object);
			}
		} else {
			super.validateDateFin(facesContext, component, object);
		}
	}

	/**
	 * Retourne true si l'utilisateur créer est occasionel
	 * 
	 * @return
	 */
	public boolean isCreatedUserOccasional() {
		if (newUser != null) {
			STUser user = newUser.getAdapter(STUser.class);
			return user.isOccasional();
		}
		return false;
	}

	/**
	 * retourne la date du jour + 6 mois
	 * 
	 * @return
	 */
	private Calendar getDatePlusSixMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 6);
		return cal;
	}

	/**
	 * Réinitialise le document newUser
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String resetOccasionalUserCreation() throws ClientException {
		newUser = null;
		return utilisateurActions.navigateToEspaceUtilisateur();
	}

	/**
	 * Création d'un utilisateur temporaire
	 * 
	 * @return
	 * @throws ClientException
	 * @throws UserAlreadyExistsException
	 */
	public String createUserOccasional() throws ClientException, UserAlreadyExistsException {
		try {
			ReponsesUserManager reponsesUserManager = (ReponsesUserManager) userManager;

			selectedUser = reponsesUserManager.createUserOccasional(documentManager, newUser);

			if (selectedUser.getContextData(STConstant.MAIL_SEND_ERROR) != null) {
				boolean mailError = (Boolean) selectedUser.getContextData(STConstant.MAIL_SEND_ERROR);
				if (mailError) {
					selectedUser.putContextData(STConstant.MAIL_SEND_ERROR, null);
					facesMessages.add(StatusMessage.Severity.INFO,
							resourcesAccessor.getMessages().get("info.userManager.userCreated"));
					facesMessages.add(StatusMessage.Severity.ERROR, "Erreur lors de l'envoi du mél de mot de passe");
					newUser = null;
					return null;
				}
			}

			newUser = null;
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.userManager.userCreated"));
			resetUsers();
			return ReponsesViewConstant.CREATE_USER_OCCASIONAL;
		} catch (UserAlreadyExistsException e) {
			facesMessages.add(StatusMessage.Severity.ERROR,
					resourcesAccessor.getMessages().get("error.userManager.userAlreadyExists"));
			return null;
		} catch (ClientException e) {
			facesMessages.add(StatusMessage.Severity.ERROR, e.getMessage());
			return null;
		}
	}

	@Override
	public String createUser() throws ClientException, UserAlreadyExistsException {
		try {
			ReponsesUserManager reponsesUserManager = (ReponsesUserManager) userManager;
			
			boolean profilsOk = addProfilesToUser(newUser);
			if(!profilsOk) {
				facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("error.userManager.emptyProfils"));
				return null;
			}
			
			selectedUser = reponsesUserManager.createUser(newUser);
			((STUserManager) userManager).updateUserPostes(selectedUser, userPostes);
			newUser = null;
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.userManager.userCreated"));
			resetUsers();

			if (selectedUser.getContextData(STConstant.MAIL_SEND_ERROR) != null
					&& (Boolean) selectedUser.getContextData(STConstant.MAIL_SEND_ERROR)) {
				selectedUser.putContextData(STConstant.MAIL_SEND_ERROR, null);
				facesMessages.add(StatusMessage.Severity.ERROR, "Erreur lors de l'envoi du mél de mot de passe");
			}

			return viewUser();
		} catch (UserAlreadyExistsException e) {
			facesMessages.add(StatusMessage.Severity.ERROR,
					resourcesAccessor.getMessages().get("error.userManager.userAlreadyExists"));
			return null;
		} catch (ClientException e) {
			facesMessages.add(StatusMessage.Severity.ERROR, e.getMessage());
			return null;
		}
	}

	@Override
	public String changePassword() throws ClientException {
		// Ajout de la date de changement du mot de passe
		STUser user = selectedUser.getAdapter(STUser.class);
		if (user != null) {
			try {
				ReponsesServiceLocator.getProfilUtilisateurService().changeDatePassword(documentManager,
						user.getUsername());
			} catch (Exception e) {
				LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_PROFIL_UTILISATEUR_TEC);
			}
		}
		return super.changePassword();
	}

	@Override
	public String forcedChangePassword() throws ClientException {
		// Ajout de la date de changement du mot de passe
		STUser user = selectedUser.getAdapter(STUser.class);
		if (user != null) {
			final ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator
					.getProfilUtilisateurService();
			ProfilUtilisateur profilUtilisateur = (ProfilUtilisateur) profilUtilisateurService.getProfilUtilisateur(
					documentManager, user.getUsername());

			if (profilUtilisateur != null) {
				// Mise à jour de la date
				profilUtilisateur.setDernierChangementMotDePasse(Calendar.getInstance());
				documentManager.saveDocument(profilUtilisateur.getDocument());
				documentManager.save();
			}
		}
		return super.forcedChangePassword();
	}

	/**
	 * Adds selection from selector as a list element
	 * <p>
	 * Must pass request parameter "suggestionSelectionListId" holding the
	 * binding to model. Selection will be retrieved using the
	 * {@link #getSelectedValue()} method.
	 * @throws ClientException 
	 */
	public void addBoundSelectionToList(ActionEvent event) throws ClientException {
		UIComponent component = event.getComponent();
		if (component == null) {
			return;
		}
		
		ProfileService profilService = STServiceLocator.getProfileService();
		Map<String, DocumentModel> profilMap = profilService.getProfilMap();
		
		DocumentModel profilToAdd = profilMap.get(getSelectedValue());
		
		if(!userProfils.contains(profilToAdd) ) {
			userProfils.add(profilToAdd);
		}
	}

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
}
