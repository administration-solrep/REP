package fr.dila.reponses.api.feuilleroute;

import fr.dila.st.api.feuilleroute.STRouteStep;

/**
 * Interface des documents de type étape de feuille de route.
 * 
 * @author jtremeaux
 */
public interface ReponsesRouteStep extends STRouteStep {
    
    boolean isReaffectation();
    
    void setReaffectation(boolean isReaffectation);
}
