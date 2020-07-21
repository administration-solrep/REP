package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.cases.Dossier;

/**
 * Service de signature des dossiers de l'application Réponses.
 * 
 * @author sly
 */
public interface DossierSignatureService  {


    /**
     * 
     * Signe un dossier avec le ws dictao
     * 
     * @param dossier
     * @param session
     * @return
     * @throws Exception
     */
    public Boolean signerDossier(Dossier dossier, CoreSession session ) throws ClientException;
      
   /**
    * 
    * Vérifie que la signature d'un dossier correspond bien à son texte, via le service dictao.
    * 
    * @param dossier
    * @param session
    * @return
    * @throws Exception
    */
    public Boolean verifierDossier(Dossier dossier, CoreSession session) throws ClientException;
    
	/**
	 * Génère une clé utilisée dans la requête de signature (balise URI).
	 * 
	 * @param dossier
	 * @param session
	 * @return une clé String unique par dossier, signifiante mais par imposée
	 *         par DICTAO (il faut juste un String non null, on a choisi de
	 *         l'utiliser pour identifier le dossier).
	 */
	public String generateKey(Dossier dossier, CoreSession session);

}