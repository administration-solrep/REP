package fr.dila.reponses.api.cases;

import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * DocumentDila document.
 * <p>
 * Represents allotissement between question.
 *
 */
public interface Allotissement extends Serializable {
    public enum TypeAllotissement {
        AJOUT,
        SUPPR
    }

    List<String> getIdDossiers();

    void setIdDossiers(List<String> idDossiers);

    String getNom();

    void setNom(String nom);

    DocumentModel getDocument();
}
