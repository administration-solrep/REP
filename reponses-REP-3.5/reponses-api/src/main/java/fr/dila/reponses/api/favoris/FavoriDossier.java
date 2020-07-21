package fr.dila.reponses.api.favoris;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.DossierCommon;

public interface FavoriDossier extends DossierCommon {
    /**
     * Retourne le document model 
     * @return le document
     */
    DocumentModel getDocument();
    
    /**
     * retourne l'id du document pointé par le favori
     * @return
     */
    String getTargetDocumentId();
    
    /**
     * definit l'id du document pointé par le favori
     */
    void setTargetDocumentId(String id);
    
    /**
     * definit le titre du favori
     * @param title
     */
    void setTitle(String title);
    
    /**
     * save the document and update it
     * @param session
     */
    void save(CoreSession session) throws ClientException;
}
