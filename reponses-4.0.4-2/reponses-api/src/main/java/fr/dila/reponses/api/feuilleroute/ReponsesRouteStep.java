package fr.dila.reponses.api.feuilleroute;

import fr.dila.ss.api.feuilleroute.SSRouteStep;

/**
 * Interface des documents de type étape de feuille de route.
 *
 * @author jtremeaux
 */
public interface ReponsesRouteStep extends SSRouteStep {
    boolean isReaffectation();

    void setReaffectation(boolean isReaffectation);
}
