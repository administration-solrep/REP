package fr.dila.ss.api.tree;

import org.nuxeo.ecm.core.api.Blob;

public interface SSTreeFile extends SSTreeNode {

   /**
    * Récupère le nom du noeud.
    */
   String getFilename();

   /**
    * définit le nom du noeud.
    * 
    * @param name
    */
   void setFilename(String filename);
   
   /**
    * récupère le mime type du document
    * @return
    */
   String getFileMimeType();

    /**
     * 
     * @return
     */
    Blob getContent();
    
    /**
     * 
     * @param content
     */
    void setContent(Blob content);
    
    /**
     * Récupère l'information major_version du schema uid
     * @return
     */
    Long getMajorVersion();
}
