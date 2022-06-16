package fr.dila.reponses.ui.services;

import fr.dila.st.ui.services.STUtilisateursUIService;
import fr.dila.st.ui.th.model.SpecificContext;

public interface ReponsesUtilisateursUIService extends STUtilisateursUIService {
    void createOccasionalUser(SpecificContext context);
}
