package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.recherche.STRechercheService;
/**
 * 
 * 
 * @author JGZ
 * La classe de service pour les recherches
 *
 */

public interface RechercheService extends STRechercheService{
    
	/**
	 * Crée un objet requête avec un nom.
	 * @param session la session utilisateur
	 * @param name  le nom de la requête
	 * @return
	 * @throws ClientException
	 */
	public Requete createRequete(CoreSession session, String name) throws ClientException;

	
	/**
	 * Retourne une requête pas encore sauvegardé.
	 * @param session la session utilisateur
	 * @param name le nom de la requête
	 * @return
	 * @throws ClientException
	 */
	public Requete getRequete(CoreSession session, String name) throws ClientException;
	
	/**
	 * Retourne la liste des documents retournée par l'exécution de la requête.
	 * @param session : la session de l'utilisateur
	 * @param requete : la requête pour la recherche
	 * @return
	 * @throws ClientException
	 */
    public DocumentModelList query(CoreSession session,Requete requete) throws ClientException ;
    
    
    /**
     * Renvoie la clause WHERE d'un document model de type requête
     * @param model
     * @return
     * @throws ClientException
     */
     public String getWhereClause(Requete requete,String ... modelNames) throws ClientException;
    
    /**
     * Retourne la requête complête, construite à partir de différents models. 
     * @param model
     * @param modelNames
     * @return
     * @throws ClientException
     */
    String getFullQuery(CoreSession session,Requete requete, String... modelNames) throws ClientException;

    /**
     * Recherche une question par son origine + numéro
     * @param session
     * @param title
     * @return
     * @throws ClientException
     */
    Question searchQuestionBySourceNumero(CoreSession session, String sourcenumero) throws ClientException;

}
