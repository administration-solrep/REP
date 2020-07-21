package fr.dila.ss.api.documentmodel;

import java.io.Serializable;
import java.util.Calendar;

import fr.dila.st.api.domain.STDomainObject;

public interface SSInfoUtilisateurConnection extends STDomainObject, Serializable {

	/**
	 * Getter user
	 * 
	 * @return the user login
	 */
	String getUserName();

	void setUserName(String userName);

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	/**
	 * Date de la connection d'un utilisateur
	 * 
	 * @return la ate de la connection d'un utilisateur
	 */
	Calendar getDateConnection();

	void setDateConnection(Calendar dateConnection);

	/**
	 * indique si l'utilisateur a ete deja log out
	 * 
	 * @return
	 */
	public Boolean getIsLogout();

	public void setIsLogout(Boolean isLogout);

	// FEV531
	/**
	 * Courriel de l'utilisateur
	 */
	String getCourriel();

	void setCourriel(String courriel);

	/**
	 * Téléphone de l'utilisateur
	 * 
	 * @return
	 */
	String getTelephone();

	void setTelephone(String telephone);

	/**
	 * Ministère de rattachement de l'utilisateur
	 * 
	 * @return
	 */
	String getMinistereRattachement();

	void setMinistereRattachement(String ministereRattachement);

	/**
	 * Direction de l'utilisateur
	 * 
	 * @return
	 */
	String getDirection();

	void setDirection(String direction);

	/**
	 * Poste de l'utilisateur
	 * 
	 * @return
	 */
	String getPoste();

	void setPoste(String poste);

	/**
	 * Date de création de l'utilisateur
	 * 
	 * @return
	 */
	Calendar getDateCreation();

	void setDateCreation(Calendar dateCreation);

	Calendar getDateDerniereConnexion();

	void setDateDerniereConnexion(Calendar dateDerniereConnexion);

}
