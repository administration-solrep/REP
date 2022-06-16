package fr.dila.reponses.api.historique;

import java.io.Serializable;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Représente un document HistoriqueAttribution
 *
 */
public interface HistoriqueAttribution extends Serializable {
    /**
     * Récupère le DocumentModel
     * @return
     */
    DocumentModel getDocument();

    /**
     * Récupère l'id ministère d'attribution
     * @return String id du ministère d'attribution
     */
    String getMinAttribution();

    /**
     * Renseigne le ministère d'attribution
     * @param minAttribution
     */
    void setMinAttribution(String minAttribution);

    /**
     * Récupère dans le type complexe la date d'attribution
     * @return Calendar la date d'attribution
     */
    Calendar getDateAttribution();

    /**
     * Renseigne la date d'attribution
     * @param dateAttribution
     */
    void setDateAttribution(Calendar dateAttribution);

    /**
     * Récupère le type d'attribution (réaffectation ou réattribution)
     * @return
     */
    String getTypeAttribution();

    /**
     * Renseigne le type d'attribution
     * @param typeAttribution
     */
    void setTypeAttribution(String typeAttribution);
}
