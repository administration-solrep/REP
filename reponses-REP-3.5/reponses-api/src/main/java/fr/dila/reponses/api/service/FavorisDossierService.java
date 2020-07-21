package fr.dila.reponses.api.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.naming.NameAlreadyBoundException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 * Service qui permet de gérer les favoris de dossiers.
 * 
 * @author jgomez
 */
public interface FavorisDossierService extends Serializable {
    /**
     * Ajoute la liste de dossiers aux favoris de l'utilisateur.
     * 
     * @param session La session de l'utilisateur.
     * @param docs La liste des documentModels à ajouter
     * @return 
     * @throws ClientException 
     *
     */
    void add(CoreSession session,DocumentModelList docs, String currentRepertoire) throws ClientException;
    
  
    /**
     * Supprime la liste de dossiers des favoris de l'utilisateur.
     * 
     * @param session La session de l'utilisateur.
     * @param docs La liste des documentModels à supprimer.
     * @return 
     * @throws ClientException 
     *
     */
    void delete(CoreSession session,DocumentModelList docs) throws ClientException;

    /**
     * Crée un favoris de dossier dans le repertoire courant en prenant comme source le dossier passé en paramètre
     * @param dossierSource
     * @param session La session de l'utilisateur
     * @return 
     * 
     */
    DocumentModel createFavorisDossier(CoreSession session, DocumentModel dossierSource, String currentRepertoire)  throws ClientException;

    
    /**
     * 
     * Crée un répertoire de favoris
     * @param session
     * @param repertoireName Le nom du répertoire
     * @param dateValidite La date de validite du répertoire
     * @return Le répertoire sous forme de document model
     * @throws ClientException 
     * @throws NameAlreadyBoundException 
     */
    DocumentModel createFavorisRepertoire(CoreSession session,String repertoireName, Calendar dateValidite) throws ClientException, NameAlreadyBoundException;
    
    /**
     * Renvoie la liste des favoris de dossier de l'utilisateur
     * @param session La session de l'utilisateur
     */
    List<DocumentModel> getFavoris(CoreSession session,String parentId)  throws ClientException ;

    /**
     * Renvoie la liste des répertoire de favoris de dossier de l'utilisateur
     * @param session La session de l'utilisateur
     */
    public List<DocumentModel> getFavorisRepertoires(CoreSession session) throws ClientException;
    
    /**
     * Retourne le document model du repertoire courant
     * @param session
     * @return
     * @throws ClientException
     */
    DocumentModel getCurrentRepertoireDocument(CoreSession session, String currentRepertoire) throws ClientException;

    /**
     * Retourne le document racine des favoris
     * @param session
     * @return
     * @throws ClientException
     */
    DocumentModel getRootFavorisDossier(CoreSession session) throws ClientException;
    
    
    /**
     * suppression des favoris à la date de fin indiquée
     * 
     * supprimer les favoris dont la date de validité est inferieur à la date courante
     * 
     * @param session
     * @param currentDate 
     */
    void removeOldFavoris(CoreSession session, Calendar currentDate) throws ClientException;


}
