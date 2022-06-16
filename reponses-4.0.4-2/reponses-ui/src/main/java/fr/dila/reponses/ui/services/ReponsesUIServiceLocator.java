package fr.dila.reponses.ui.services;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.services.actions.suggestion.indexassemblee.IndexAssembleeSuggestionProviderService;
import fr.dila.reponses.ui.services.actions.suggestion.indexministere.IndexMinistereSuggestionProviderService;
import fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderService;
import fr.dila.reponses.ui.services.comment.RouteStepNoteUIService;
import fr.dila.reponses.ui.services.dossier.DossierMassUIService;
import fr.dila.reponses.ui.services.files.ReponsesFondDeDossierUIService;
import fr.dila.reponses.ui.services.organigramme.ReponsesMigrationGouvernementUIService;
import fr.dila.reponses.ui.services.organigramme.ReponsesMigrationManagerUIService;
import fr.dila.ss.ui.services.SSDossierUIService;
import fr.dila.st.ui.services.actions.suggestion.nomauteur.NomAuteurSuggestionProviderService;

public final class ReponsesUIServiceLocator {

    /**
     * Utility class
     */
    private ReponsesUIServiceLocator() {
        // do nothing
    }

    public static ArchiveUIService getArchiveUIService() {
        return getRequiredService(ArchiveUIService.class);
    }

    public static DossierListUIService getDossierListUIService() {
        return getRequiredService(DossierListUIService.class);
    }

    public static PlanClassementComponentService getPlanClassementComponentService() {
        return getRequiredService(PlanClassementComponentService.class);
    }

    @SuppressWarnings("unchecked")
    public static SSDossierUIService<ConsultDossierDTO> getDossierUIService() {
        return getRequiredService(SSDossierUIService.class);
    }

    public static StatistiquesUIService getStatistiquesUIService() {
        return getRequiredService(StatistiquesUIService.class);
    }

    public static BordereauUIService getBordereauUIService() {
        return getRequiredService(BordereauUIService.class);
    }

    public static ParapheurUIService getParapheurUIService() {
        return getRequiredService(ParapheurUIService.class);
    }

    public static RechercheUIService getRechercheUIService() {
        return getRequiredService(RechercheUIService.class);
    }

    public static ReponsesSelectValueUIService getSelectValueUIService() {
        return getRequiredService(ReponsesSelectValueUIService.class);
    }

    public static MinistereSuggestionProviderService getMinistereSuggestionProviderService() {
        return getRequiredService(MinistereSuggestionProviderService.class);
    }

    public static IndexMinistereSuggestionProviderService getIndexMinistereSuggestionProviderService() {
        return getRequiredService(IndexMinistereSuggestionProviderService.class);
    }

    public static IndexAssembleeSuggestionProviderService getIndexAssembleeSuggestionProviderService() {
        return getRequiredService(IndexAssembleeSuggestionProviderService.class);
    }

    public static NomAuteurSuggestionProviderService getNomAuteurSuggestionProviderService() {
        return getRequiredService(NomAuteurSuggestionProviderService.class);
    }

    public static RequeteUIService getRequeteUIService() {
        return getRequiredService(RequeteUIService.class);
    }

    public static ReponsesFondDeDossierUIService getFondDeDossierUIService() {
        return getRequiredService(ReponsesFondDeDossierUIService.class);
    }

    public static ReponsesMigrationManagerUIService getReponsesMigrationManagerUIService() {
        return getRequiredService(ReponsesMigrationManagerUIService.class);
    }

    public static RouteStepNoteUIService getRouteStepNoteUIService() {
        return getRequiredService(RouteStepNoteUIService.class);
    }

    public static ReponsesMigrationGouvernementUIService getReponsesMigrationGouvernementUIService() {
        return getRequiredService(ReponsesMigrationGouvernementUIService.class);
    }

    public static DelegationUIService getDelegationUIService() {
        return getRequiredService(DelegationUIService.class);
    }

    public static ReponsesUtilisateursUIService getReponsesUtilisateursUIService() {
        return getRequiredService(ReponsesUtilisateursUIService.class);
    }

    public static ReponsesFeuilleRouteUIService getReponsesFeuilleRouteUIService() {
        return getRequiredService(ReponsesFeuilleRouteUIService.class);
    }

    public static FavorisDossierUIService getFavorisDossierUIService() {
        return getRequiredService(FavorisDossierUIService.class);
    }

    public static DossierMassUIService getDossierMassUIService() {
        return getRequiredService(DossierMassUIService.class);
    }

    public static ReponsesModeleFdrListUIService getReponsesModeleFdrListUIService() {
        return getRequiredService(ReponsesModeleFdrListUIService.class);
    }

    public static ReponsesModeleFdrFicheUIService getReponsesModeleFdrFicheUIService() {
        return getRequiredService(ReponsesModeleFdrFicheUIService.class);
    }

    public static ReponsesMailboxListComponentService getReponsesMailboxListComponentService() {
        return getRequiredService(ReponsesMailboxListComponentService.class);
    }
}
