package fr.dila.ss.api.domain.feuilleroute;

import fr.dila.ecm.platform.routing.api.DocumentRouteStepsContainer;

/**
 * Interface des documents de type conteneur d'étape de feuille de route.
 * 
 * @author jtremeaux
 */
public interface StepFolder extends DocumentRouteStepsContainer {

    /**
     * Retourne le type d'exécution du conteneur (parallèle ou série).
     * 
     * @return Type d'exécution
     */
    String getExecution();

    /**
     * Renseigne le type d'exécution du conteneur (parallèle ou série).
     * 
     * @param execution Type d'exécution
     */
    void setExecution(String execution);
    
    /**
     * Retourne vrai si le conteneur est de type série.
     * 
     * @return Vrai si le conteneur est de type série
     */
    boolean isSerial();
    
    /**
     * Retourne vrai si le conteneur est de type parallèle.
     * 
     * @return Vrai si le conteneur est de type parallèle
     */
    boolean isParallel();
}
