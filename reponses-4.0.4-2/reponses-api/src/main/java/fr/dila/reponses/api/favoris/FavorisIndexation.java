package fr.dila.reponses.api.favoris;

import java.io.Serializable;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Favoris d'indexation
 * @author bgamard
 *
 */
public interface FavorisIndexation extends Serializable {
    enum TypeIndexation {
        AN,
        SENAT
    }

    public String getTypeIndexation();

    public void setTypeIndexation(String typeIndexation);

    public String getNiveau1();

    public void setNiveau1(String niveau1);

    public String getNiveau2();

    public void setNiveau2(String niveau2);

    /**
     * Retourne le document model
     * @return le document
     */
    DocumentModel getDocument();

    /**
     * Setter pour le document model.
     * @param doc
     */
    void setDocument(DocumentModel doc);
}
