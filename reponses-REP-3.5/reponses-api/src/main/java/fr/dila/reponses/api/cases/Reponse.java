package fr.dila.reponses.api.cases;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.flux.RErratum;

/**
 * DocumentDila document.
 * <p>
 * Represents a question item inside a dossier.
 *
 */
public interface Reponse extends Serializable {
	
    /**
     *  Access au document encapsule
     */
    DocumentModel getDocument();
    
    /**
     * Gets the content of the reponse.
     */
    String getTexteReponse();

    void setTexteReponse(String texteReponse);
    
    String getSignature();
    
    void setSignature(String signature);
    
    String getAuthorRemoveSignature();
    
    void setAuthorRemoveSignature(String signature);
    
    /**
     * Gets the reponse author id.
     */
    String getIdAuteurReponse();

    void setIdAuteurReponse(String idAuteurReponse);
    
    /**
     * Gets the reponse JO number.
     */
    Long getNumeroJOreponse();

    void setNumeroJOreponse(Long numeroJOreponse);
    
    /**
     * Gets the reponse JO date.
     */
    Calendar getDateJOreponse();

    void setDateJOreponse(Calendar dateJOreponse);
    
    /**
     * Gets the reponse JO page.
     */
    Long getPageJOreponse();

    void setPageJOreponse(Long pageJOreponse);
    
    
    /**
     * G&S des errata de réponses
     * @return
     */
    List<RErratum> getErrata();
    
    void setErrata(List<RErratum> errata);
    
    Boolean isPublished();

    void setIsSignatureValide(Boolean isSignatureValide);
    
    Boolean getIsSignatureValide();
    
    /**
     * Retourne si la reponse est signee (c'est-a-dire qu'elle a une signature non vide)
     * @return
     */
    Boolean isSignee();

    /**
     * Met en place l'auteur de la réponse
     * @param reponseAuthor
     * @return
     */
    void setAuteur(String reponseAuthor);

    /**
     * Retourne l'auteur de la réponse
     * @return
     */
    String getAuteur();
    
    
    /**
     * Met en place l'erratum courant de la réponse.
     * @param reponseAuthor
     * @return
     */
    void setCurrentErratum(String erratum);

    /**
     * Retourne l'erratum courant de la réponse.
     * @return
     */
    String getCurrentErratum();

}
