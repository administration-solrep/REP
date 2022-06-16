package fr.dila.reponses.ui.services;

import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.st.ui.th.model.SpecificContext;

public interface ReponsesModeleFdrListUIService {
    /*
     * Récupérer la liste des modèles à afficher pour la substitution
     */
    ModeleFDRList getModelesFDRSubstitution(SpecificContext context);
}
