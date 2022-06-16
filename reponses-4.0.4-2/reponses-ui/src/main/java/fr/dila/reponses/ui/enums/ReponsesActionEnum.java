package fr.dila.reponses.ui.enums;

import fr.dila.st.ui.enums.ActionEnum;

public enum ReponsesActionEnum implements ActionEnum {
    ADD_FAVORIS_PC,
    ADD_FIRST_STEP_MODELE_ACTION,
    ADMIN_DELEGATION_CREATE,
    ADMIN_DELEGATION_MODIFY,
    ADMIN_MIGRATION_TIMBRES,
    ADMIN_PARAM_ARCHIVAGE,
    CASSER_CACHET_SERVEUR,
    COMPARER_VERSIONS_PARAPHEUR,
    FAVORI_TRAVAIL_SUPPRIMER,
    FAVORI_TRAVAIL_SUPPRIMER_TOUT,
    MAJ_TIMBRES,
    RECHERCHE_DOSSIER_EXPORT,
    REMOVE_FAVORIS_PC,
    UTILISATEUR_CREATE_USER;

    @Override
    public String getName() {
        return name();
    }
}
