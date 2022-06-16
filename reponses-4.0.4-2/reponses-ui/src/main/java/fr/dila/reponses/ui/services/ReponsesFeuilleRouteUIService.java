package fr.dila.reponses.ui.services;

import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.st.ui.th.model.SpecificContext;

public interface ReponsesFeuilleRouteUIService extends SSFeuilleRouteUIService {
    /**
     * Ajout des premières étapes d'un modèle après sa création
     *
     * @param context
     */
    void addFirstEtapesModele(SpecificContext context);
}
