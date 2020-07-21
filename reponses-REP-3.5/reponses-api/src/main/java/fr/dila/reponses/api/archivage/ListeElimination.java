package fr.dila.reponses.api.archivage;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ListeElimination {
    /**
     * Retourne le document model 
     * @return le document
     */
    DocumentModel getDocument();
    
    /**
     * Retourne true si la liste est celle en cours
     * @return
     */
    Boolean isEnCours();
    
    /**
     * Positionne la liste comme étant en cours
     */
    void setEnCours(Boolean enCours);
    
    /**
     * definit le titre de la liste
     * @param title
     */
    void setTitle(String title);
    
    /**
     * Sauvegarde le document
     * @param session
     */
    void save(CoreSession session) throws ClientException;

    /**
     * Retourne true si la liste est en train d'être supprimée
     * @return
     */
    Boolean isSuppressionEnCours();
    
    /**
     * Positionne la liste comme étant en cours de suppression
     */
    void setSuppressionEnCours(Boolean enCours);
    
    /**
     * Retourne true si la liste est en train d'être abandonnée
     * @return
     */
    Boolean isAbandonEnCours();
    
    /**
     * Positionne la liste comme étant en cours d'abandon
     */
    void setAbandonEnCours(Boolean enCours);
}
