package fr.dila.reponses.api.domain;

import java.util.Calendar;

/**
 * 
 * @author admin
 */
public interface JetonDoc extends fr.dila.st.api.jeton.JetonDoc {
    /**
     * Récupère l'id ministère d'attribution lié à ce jeton
     * @return String id du ministère d'attribution
     */
    String getMinAttribution();
    
    /**
     * Renseigne le ministère d'attribution de la question liée à ce jeton
     * @param minAttribution
     */
    void setMinAttribution(String minAttribution);

    /**
     * Récupère dans le type complexe la date d'attribution liée à ce jeton
     * @return Calendar la date d'attribution
     */
    Calendar getDateAttribution();

    /**
     * Renseigne la date d'attribution de la question liée à ce jeton
     * @param dateAttribution
     */
    void setDateAttribution(Calendar dateAttribution);
    
    /**
     * Récupère le type d'attribution (réaffectation ou réattribution) lié à ce jeton
     * @return
     */
    String getTypeAttribution();

    /**
     * Renseigne le type d'attribution de la question liée à ce jeton
     * @param typeAttribution
     */
    void setTypeAttribution(String typeAttribution);
}
