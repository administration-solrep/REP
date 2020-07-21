package fr.dila.reponses.api.feuilleroute;

import java.io.Serializable;


import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;

/**
 * Interface des documents de type feuille de route.
 * 
 * @author jtremeaux
 */
public interface ReponsesFeuilleRoute extends DocumentRoute, STFeuilleRoute, Serializable {
    
    /**
     * Retourne le titre de la question.
     * 
     * @return Titre de la question
     */
    String getTitreQuestion();

    /**
     * Renseigne le titre de la question.
     */
    void setTitreQuestion(String titreQuestion);
    
    
    /**
     * Gets the Question id Ministere rattachement.
     */
    String getIdDirectionPilote();

    void setIdDirectionPilote(String idDirectionPilote);

    /**
     * Gets the Question intitule  pilote.
     */
    String getIntituleDirectionPilote();

    void setIntituleDirectionPilote(String intituleDirectionPilote);
    
    
    
}
