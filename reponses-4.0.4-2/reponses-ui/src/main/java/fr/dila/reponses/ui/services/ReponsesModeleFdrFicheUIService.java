package fr.dila.reponses.ui.services;

import fr.dila.reponses.ui.th.bean.ReponsesModeleFdrForm;
import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.st.ui.th.model.SpecificContext;

public interface ReponsesModeleFdrFicheUIService {
    ReponsesModeleFdrForm getModeleFdrForm(SpecificContext context, ReponsesModeleFdrForm form);

    FdrDTO getFeuileRouteModele(SpecificContext context);

    void updateModele(SpecificContext context, ReponsesModeleFdrForm modeleForm);

    ReponsesModeleFdrForm createModele(SpecificContext context, ReponsesModeleFdrForm modeleForm);

    /*
     * Récupère le formulaire de consultation d'un modèle pour la substitution
     */
    ReponsesModeleFdrForm consultModeleSubstitution(SpecificContext context, ReponsesModeleFdrForm form);

    /*
     * Récupérer le dto de la feuille de route du modèle pour substitution
     */
    FdrDTO getFeuileRouteModeleSubstitution(SpecificContext context);
}
