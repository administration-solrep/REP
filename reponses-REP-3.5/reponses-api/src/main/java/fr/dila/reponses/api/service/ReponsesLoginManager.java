package fr.dila.reponses.api.service;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

public interface ReponsesLoginManager extends Serializable {

	/**
	 * Update "simple" sur l'utilisateur courant sans event ni sync <br/><br/>
	 * <b>UNIQUEMENT POUR UPDATE APRES LOGIN DE L'UTILISATEUR<b>.
	 * 
	 * @param session
	 * @throws ClientException
	 */
	void updateUserAfterLogin(CoreSession session, String username) throws ClientException;
	
}
