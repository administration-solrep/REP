package fr.dila.ss.core.documentmodel;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ss.api.constant.SSInfoUtilisateurConnectionConstants;
import fr.dila.ss.api.documentmodel.SSInfoUtilisateurConnection;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.dila.st.core.util.PropertyUtil;

public class SSInfoUtilisateurConnectionImpl extends STDomainObjectImpl implements SSInfoUtilisateurConnection {

	private static final long	serialVersionUID	= -5032915223533916100L;

	public SSInfoUtilisateurConnectionImpl(DocumentModel doc) {
		super(doc);
	}

	@Override
	public String getUserName() {
		return getStringProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_USER_PROPERTY);
	}

	@Override
	public void setUserName(String userName) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_USER_PROPERTY, userName);

	}

	@Override
	public String getFirstName() {
		return getStringProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_FIRST_NAME_PROPERTY);

	}

	@Override
	public void setFirstName(String firstName) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_FIRST_NAME_PROPERTY, firstName);

	}

	@Override
	public String getLastName() {
		return getStringProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_LAST_NAME_PROPERTY);

	}

	@Override
	public void setLastName(String lastName) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_LAST_NAME_PROPERTY, lastName);

	}

	@Override
	public Calendar getDateConnection() {
		return getDateProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DATE_CONNECTION_PROPERTY);

	}

	@Override
	public void setDateConnection(Calendar dateConnection) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DATE_CONNECTION_PROPERTY,
				dateConnection);

	}

	@Override
	public Boolean getIsLogout() {
		return PropertyUtil.getBooleanProperty(document,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.IINFO_UTILISATEUR_CONNECTION__IS_LOGOUT);
	}

	@Override
	public void setIsLogout(Boolean isLogout) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.IINFO_UTILISATEUR_CONNECTION__IS_LOGOUT, isLogout);
	}

	@Override
	public String getCourriel() {
		return getStringProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_COURRIEL_PROPERTY);

	}

	@Override
	public void setCourriel(String courriel) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_COURRIEL_PROPERTY, courriel);

	}

	@Override
	public String getTelephone() {
		return getStringProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_TELEPHONE_PROPERTY);

	}

	@Override
	public void setTelephone(String telephone) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_TELEPHONE_PROPERTY, telephone);

	}

	@Override
	public String getMinistereRattachement() {
		return getStringProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_MINISTERE_RATTACHEMENT_PROPERTY);

	}

	@Override
	public void setMinistereRattachement(String ministereRattachement) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_MINISTERE_RATTACHEMENT_PROPERTY,
				ministereRattachement);

	}

	@Override
	public String getDirection() {
		return getStringProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DIRECTION_PROPERTY);

	}

	@Override
	public void setDirection(String direction) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DIRECTION_PROPERTY, direction);

	}

	@Override
	public String getPoste() {
		return getStringProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_POSTE_PROPERTY);

	}

	@Override
	public void setPoste(String poste) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_POSTE_PROPERTY, poste);

	}

	@Override
	public Calendar getDateCreation() {
		return getDateProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DATE_CREATION_PROPERTY);

	}

	@Override
	public void setDateCreation(Calendar dateCreation) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DATE_CREATION_PROPERTY, dateCreation);

	}

	@Override
	public Calendar getDateDerniereConnexion() {
		return getDateProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DATE_DERNIERE_CONNEXION_PROPERTY);

	}

	@Override
	public void setDateDerniereConnexion(Calendar dateDerniereConnexion) {
		setProperty(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DATE_DERNIERE_CONNEXION_PROPERTY,
				dateDerniereConnexion);

	}
}
