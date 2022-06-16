package fr.dila.reponses.ui.enums;

import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.bean.EliminationDonneesDTO;
import fr.dila.reponses.ui.bean.RequetePersoForm;
import fr.dila.reponses.ui.bean.actions.DossierDistributionActionDTO;
import fr.dila.reponses.ui.bean.actions.ReponsesDossierActionDTO;
import fr.dila.reponses.ui.bean.actions.ReponsesRoutingActionDTO;
import fr.dila.reponses.ui.th.bean.DelegationForm;
import fr.dila.reponses.ui.th.bean.DelegationsDonneesListForm;
import fr.dila.reponses.ui.th.bean.DelegationsRecuesListForm;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.FavorisTravailForm;
import fr.dila.reponses.ui.th.bean.ReponsesModeleFdrForm;
import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.st.ui.enums.ContextDataKey;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public enum ReponsesContextDataKey implements ContextDataKey {
    BIRT_FORMAT(BirtOutputFormat.class),
    CURRENT_GOUVERNEMENT(String.class),
    DELEGATIONS_DONNEES(DelegationsDonneesListForm.class),
    DELEGATIONS_RECUES(DelegationsRecuesListForm.class),
    DELEGATION_FORM(DelegationForm.class),
    DOSSIER(ConsultDossierDTO.class),
    DOSSIER_ACTIONS(ReponsesDossierActionDTO.class, "dossierActions"),
    DOSSIER_DISTRIBUTION_ACTIONS(DossierDistributionActionDTO.class, "dossierDistributionActions"),
    DOSSIER_IDS(List.class),
    DOSSIER_IDS_STRING(String.class),
    DOSSIER_LIST_FORM(DossierListForm.class),
    FAVORIS_ID(String.class),
    FAVORIS_TRAVAIL_FORM(FavorisTravailForm.class),
    LISTE_ELIMINATION(EliminationDonneesDTO.class, "listeElimination"),
    MODELE_FDR_FORM(ReponsesModeleFdrForm.class),
    NEXT_GOUVERNEMENT(String.class),
    OBSERVATIONS(String.class),
    ORGANIGRAMME_ID(String.class),
    QUESTION_DIR(String.class),
    REQUETE_PERSO_FORM(RequetePersoForm.class),
    REQUETE_PERSO_ID(String.class, "idRequetePerso"),
    REQUETE_PERSO_LABEL(String.class, "labelRequetePerso"),
    REQUETE_PERSO_LIGNES(List.class),
    ROUTING_ACTIONS(ReponsesRoutingActionDTO.class, "routingActions"),
    ROUTING_TASK_TYPE(String.class, "routingTaskType"),
    SEARCHED_QUESTION(String.class),
    SEARCH_FORM_KEYS(Map.class),
    SELECTED_DOSSIERS(List.class),
    STAT_ID(String.class),
    ZIP_FILENAME_STATS(String.class, "zipFilename");

    private final Class<?> valueType;
    private final String specificKey;

    ReponsesContextDataKey(Class<?> valueType) {
        this(valueType, null);
    }

    ReponsesContextDataKey(Class<?> valueType, String specificKey) {
        this.valueType = valueType;
        this.specificKey = specificKey;
    }

    @Override
    public String getName() {
        return StringUtils.defaultIfBlank(specificKey, name());
    }

    @Override
    public Class<?> getValueType() {
        return valueType;
    }
}
