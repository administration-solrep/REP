package fr.dila.reponses.core.service;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.api.service.CheckAllotissementService;
import fr.dila.reponses.api.service.ControleService;
import fr.dila.reponses.api.service.DelegationService;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.DossierBordereauService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.DossierQueryService;
import fr.dila.reponses.api.service.DossierService;
import fr.dila.reponses.api.service.DossierSignatureService;
import fr.dila.reponses.api.service.ExtractionService;
import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.api.service.JetonService;
import fr.dila.reponses.api.service.PdfDossierService;
import fr.dila.reponses.api.service.PlanClassementService;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesAlertService;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.api.service.ReponsesCorbeilleTreeService;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.api.service.ReponsesInjectionGouvernementService;
import fr.dila.reponses.api.service.ReponsesLoginManager;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.api.service.ReponsesMailboxService;
import fr.dila.reponses.api.service.ReponsesMigrationService;
import fr.dila.reponses.api.service.ReponsesOrganigrammeService;
import fr.dila.reponses.api.service.ReponsesUserManager;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.api.service.WsNotificationService;
import fr.dila.reponses.api.service.vocabulary.EtatQuestionService;
import fr.dila.reponses.api.service.vocabulary.GroupePolitiqueService;
import fr.dila.reponses.api.service.vocabulary.LegislatureService;
import fr.dila.reponses.api.service.vocabulary.QuestionTypeService;
import fr.dila.reponses.api.service.vocabulary.ReponsesRoutingTaskTypeService;
import fr.dila.reponses.api.service.vocabulary.RubriqueANService;
import fr.dila.reponses.api.service.vocabulary.RubriqueSenatService;
import fr.dila.reponses.api.service.vocabulary.StatutEtapeRechercheService;
import fr.dila.reponses.api.service.vocabulary.TeteAnalyseANService;
import fr.dila.reponses.api.service.vocabulary.ThemeSenatService;
import fr.dila.reponses.api.service.vocabulary.ValidationStatutEtapeService;
import fr.dila.reponses.core.service.organigramme.ReponsesChangementGouvernementService;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * ServiceLocator de l'application Réponses : permet de retourner les services de l'application.
 *
 * @author jtremeaux
 * @author sly
 */
public final class ReponsesServiceLocator {

    /**
     * Utility class
     */
    private ReponsesServiceLocator() {
        // do nothing
    }

    public static AllotissementService getAllotissementService() {
        return getRequiredService(AllotissementService.class);
    }

    public static CheckAllotissementService getCheckAllotissementService() {
        return getRequiredService(CheckAllotissementService.class);
    }

    public static ArchiveService getArchiveService() {
        return getRequiredService(ArchiveService.class);
    }

    public static ReponsesCorbeilleService getCorbeilleService() {
        return getRequiredService(ReponsesCorbeilleService.class);
    }

    public static ReponsesCorbeilleTreeService getCorbeilleTreeService() {
        return getRequiredService(ReponsesCorbeilleTreeService.class);
    }

    public static DossierBordereauService getDossierBordereauService() {
        return getRequiredService(DossierBordereauService.class);
    }

    public static DossierDistributionService getDossierDistributionService() {
        return getRequiredService(DossierDistributionService.class);
    }

    public static DossierSignatureService getDossierSignatureService() {
        return getRequiredService(DossierSignatureService.class);
    }

    public static FavorisDossierService getFavorisDossierService() {
        return getRequiredService(FavorisDossierService.class);
    }

    public static FondDeDossierService getFondDeDossierService() {
        return getRequiredService(FondDeDossierService.class);
    }

    public static JetonService getJetonService() {
        return getRequiredService(JetonService.class);
    }

    public static FeuilleRouteModelService getFeuilleRouteModelService() {
        return getRequiredService(FeuilleRouteModelService.class);
    }

    public static ReponseFeuilleRouteService getFeuilleRouteService() {
        return getRequiredService(ReponseFeuilleRouteService.class);
    }

    public static ReponsesLoginManager getLoginManager() {
        return getRequiredService(ReponsesLoginManager.class);
    }

    public static RechercheService getRechercheService() {
        return getRequiredService(RechercheService.class);
    }

    public static QuestionConnexeService getQuestionConnexeService() {
        return getRequiredService(QuestionConnexeService.class);
    }

    public static ReponseService getReponseService() {
        return getRequiredService(ReponseService.class);
    }

    public static ReponsesVocabularyService getVocabularyService() {
        return ServiceUtil.getService(ReponsesVocabularyService.class);
    }

    public static WsNotificationService getWsNotificationService() {
        return getRequiredService(WsNotificationService.class);
    }

    public static StatsService getStatsService() {
        return getRequiredService(StatsService.class);
    }

    public static ProfilUtilisateurService getProfilUtilisateurService() {
        return getRequiredService(ProfilUtilisateurService.class);
    }

    public static UpdateTimbreService getUpdateTimbreService() {
        return getRequiredService(UpdateTimbreService.class);
    }

    public static ReponsesMailboxService getMailboxService() {
        return getRequiredService(ReponsesMailboxService.class);
    }

    public static ReponsesMailService getReponsesMailService() {
        return getRequiredService(ReponsesMailService.class);
    }

    public static ControleService getControleService() {
        return getRequiredService(ControleService.class);
    }

    public static ReponsesAlertService getAlertService() {
        return getRequiredService(ReponsesAlertService.class);
    }

    public static ReponsesExportService getReponsesExportService() {
        return getRequiredService(ReponsesExportService.class);
    }

    public static ReponsesInjectionGouvernementService getReponsesInjectionGouvernementService() {
        return getRequiredService(ReponsesInjectionGouvernementService.class);
    }

    public static ReponsesArbitrageService getReponsesArbitrageService() {
        return getRequiredService(ReponsesArbitrageService.class);
    }

    public static ReponsesMigrationService getReponsesMigrationService() {
        return getRequiredService(ReponsesMigrationService.class);
    }

    public static DocumentRoutingService getDocumentRoutingService() {
        return getRequiredService(DocumentRoutingService.class);
    }

    public static ExtractionService getExtractionService() {
        return getRequiredService(ExtractionService.class);
    }

    public static PlanClassementService getPlanClassementService() {
        return getRequiredService(PlanClassementService.class);
    }

    public static DossierService getDossierService() {
        return getRequiredService(DossierService.class);
    }

    public static ReponsesOrganigrammeService getReponsesOrganigrammeService() {
        return getRequiredService(ReponsesOrganigrammeService.class);
    }

    public static GroupePolitiqueService getGroupePolitiqueService() {
        return getRequiredService(GroupePolitiqueService.class);
    }

    public static LegislatureService getLegislatureService() {
        return getRequiredService(LegislatureService.class);
    }

    public static QuestionTypeService getQuestionTypeService() {
        return getRequiredService(QuestionTypeService.class);
    }

    public static StatutEtapeRechercheService getStatutEtapeRechercheService() {
        return getRequiredService(StatutEtapeRechercheService.class);
    }

    public static ValidationStatutEtapeService getValidationStatutEtapeService() {
        return getRequiredService(ValidationStatutEtapeService.class);
    }

    public static EtatQuestionService getEtatQuestionService() {
        return getRequiredService(EtatQuestionService.class);
    }

    public static RubriqueANService getRubriqueANService() {
        return getRequiredService(RubriqueANService.class);
    }

    public static RubriqueSenatService getRubriqueSenatService() {
        return getRequiredService(RubriqueSenatService.class);
    }

    public static TeteAnalyseANService getTeteAnalyseANService() {
        return getRequiredService(TeteAnalyseANService.class);
    }

    public static ThemeSenatService getThemeSenatService() {
        return getRequiredService(ThemeSenatService.class);
    }

    public static ReponsesChangementGouvernementService getReponsesChangementGouvernementService() {
        return getRequiredService(ReponsesChangementGouvernementService.class);
    }

    public static DossierQueryService getDossierQueryService() {
        return getRequiredService(DossierQueryService.class);
    }

    public static DelegationService getDelegationService() {
        return getRequiredService(DelegationService.class);
    }

    public static ReponsesUserManager getReponsesUserManager() {
        return (ReponsesUserManager) ServiceUtil.getRequiredService(UserManager.class);
    }

    public static PdfDossierService getPdfService() {
        return getRequiredService(PdfDossierService.class);
    }

    public static ReponsesRoutingTaskTypeService getReponsesRoutingTaskTypeService() {
        return getRequiredService(ReponsesRoutingTaskTypeService.class);
    }
}
