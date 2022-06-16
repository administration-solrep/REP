package fr.dila.reponses.api.notification;

import java.io.Serializable;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface WsNotification extends Serializable {
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

    public String getPosteId();

    public void setPosteId(String posteId);

    public String getWebservice();

    public void setWebservice(String webservice);

    int getNbEssais();

    void setNbEssais(int nbEssais);

    public String getIdQuestion();

    public void setIdQuestion(String idQuestion);
}
