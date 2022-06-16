package fr.dila.reponses.ui.services.actions;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.reponses.ui.services.actions.nxql.ReponsesSmartNXQLQueryActionService;
import fr.dila.reponses.ui.services.actions.organigramme.ReponsesOrganigrammeInjectionActionService;

public final class ReponsesActionsServiceLocator {

    /**
     * Utility class
     */
    private ReponsesActionsServiceLocator() {
        // do nothing
    }

    public static ReponsesDocumentRoutingActionService getReponsesDocumentRoutingActionService() {
        return getRequiredService(ReponsesDocumentRoutingActionService.class);
    }

    public static DossierActionService getDossierActionService() {
        return getRequiredService(DossierActionService.class);
    }

    public static ReponseActionService getReponseActionService() {
        return getRequiredService(ReponseActionService.class);
    }

    public static ReponsesDossierDistributionActionService getReponsesDossierDistributionActionService() {
        return getRequiredService(ReponsesDossierDistributionActionService.class);
    }

    public static ReponsesBordereauActionService getReponsesBordereauActionService() {
        return getRequiredService(ReponsesBordereauActionService.class);
    }

    public static RequeteurActionService getRequeteurActionService() {
        return getRequiredService(RequeteurActionService.class);
    }

    public static ReponsesSmartNXQLQueryActionService getReponsesSmartNXQLQueryActionService() {
        return getRequiredService(ReponsesSmartNXQLQueryActionService.class);
    }

    public static IndexActionService getIndexActionService() {
        return getRequiredService(IndexActionService.class);
    }

    public static ParapheurActionService getParapheurActionService() {
        return getRequiredService(ParapheurActionService.class);
    }

    public static ReponsesOrganigrammeInjectionActionService getReponsesOrganigrammeInjectionActionService() {
        return getRequiredService(ReponsesOrganigrammeInjectionActionService.class);
    }

    public static ComparateurActionService getComparateurActionService() {
        return getRequiredService(ComparateurActionService.class);
    }

    public static ReponsesModeleFeuilleRouteActionService getReponsesModeleFeuilleRouteActionService() {
        return getRequiredService(ReponsesModeleFeuilleRouteActionService.class);
    }

    public static SuiviActionService getSuiviActionService() {
        return getRequiredService(SuiviActionService.class);
    }

    public static AllotissementActionService getAllotissementActionService() {
        return getRequiredService(AllotissementActionService.class);
    }

    public static ReportingActionService getReportingActionService() {
        return getRequiredService(ReportingActionService.class);
    }

    public static DossierConnexeActionService getDossierConnexeActionService() {
        return getRequiredService(DossierConnexeActionService.class);
    }
}
