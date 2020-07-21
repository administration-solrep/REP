package fr.dila.reponses.core.service;

import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.api.service.ControleService;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.DossierBordereauService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.DossierSignatureService;
import fr.dila.reponses.api.service.ExtractionService;
import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.api.service.JetonService;
import fr.dila.reponses.api.service.MailboxService;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesAlertService;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.api.service.ReponsesInjectionGouvernementService;
import fr.dila.reponses.api.service.ReponsesLoginManager;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.api.service.ReponsesMigrationService;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.api.service.WsNotificationService;
import fr.dila.st.core.util.ServiceUtil;

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
	private ReponsesServiceLocator(){
		// do nothing
	}
	
    /**
     * Retourne le service de gestion des allotissements.
     * 
     * @return Service {@link AllotissementService}
     */
    public static AllotissementService getAllotissementService() {
        return ServiceUtil.getService(AllotissementService.class);
    }

    /**
     * Retourn le service Archive
     * 
     * @return Service Archive
     */
    public static ArchiveService getArchiveService() {
        return ServiceUtil.getService(ArchiveService.class);
    }

    /**
     * Retourne le service Corbeille.
     * 
     * @return Service Corbeille
     */
    public static CorbeilleService getCorbeilleService() {
        return ServiceUtil.getService(CorbeilleService.class);
    }

    /**
     * Retourne le service DossierBordereau.
     * 
     * @return service DossierBordereau
     */
    public static DossierBordereauService getDossierBordereauService() {
        return ServiceUtil.getService(DossierBordereauService.class);
    }

    /**
     * Retourne le service DossierDistribution.
     * 
     * @return Service DossierDistribution
     */
    public static DossierDistributionService getDossierDistributionService() {
        return ServiceUtil.getService(DossierDistributionService.class);
    }

    /**
     * 
     * Retourne le service de signature des dossiers.
     * 
     * @param <DossierSignatureService>
     * @return Service DossierSignature
     */
    public static DossierSignatureService getDossierSignatureService() {
        return ServiceUtil.getService(DossierSignatureService.class);
    }

    /**
     * Retourne le service Favoris de dossier.
     * 
     * @return Service Recherche
     */
    public static FavorisDossierService getFavorisDossierService() {
        return ServiceUtil.getService(FavorisDossierService.class);
    }

    /**
     * Retourne le service FondDeDossier.
     * 
     * @return Service FondDeDossier
     */
    public static FondDeDossierService getFondDeDossierService() {
        return ServiceUtil.getService(FondDeDossierService.class);
    }

    /**
     * Retourne le service de gestion des jetons.
     * 
     * @return Service de gestion des jetons
     */
    public static JetonService getJetonService() {
        return ServiceUtil.getService(JetonService.class);
    }

    /**
     * Retourne le service FeuilleRouteModel.
     * 
     * @return Service FeuilleRouteModel
     */
    public static FeuilleRouteModelService getFeuilleRouteModelService() {
        return ServiceUtil.getService(FeuilleRouteModelService.class);
    }

    /**
     * Retourne le service FeuilleRoute.
     * 
     * @return Service FeuilleRoute
     */
    public static FeuilleRouteService getFeuilleRouteService() {
        return ServiceUtil.getService(FeuilleRouteService.class);
    }

    /**
     * Retourne le service User de Réponses.
     * 
     * @return {@link ReponsesLoginManager}
     */
    public static ReponsesLoginManager getLoginManager() {
        return ServiceUtil.getService(ReponsesLoginManager.class);
    }

    /**
     * Retourne le service Recherche.
     * 
     * @return Service Recherche
     */
    public static RechercheService getRechercheService() {
        return ServiceUtil.getService(RechercheService.class);
    }

    /**
     * 
     * Retourne le service de gestion des questions connexes
     * 
     * @return Service {@link QuestionConnexeService}
     */
    public static QuestionConnexeService getQuestionConnexeService() {
        return ServiceUtil.getService(QuestionConnexeService.class);
    }

    /**
     * Retourne le service Reponse.
     * 
     * @return service Reponse
     */
    public static ReponseService getReponseService() {
        return ServiceUtil.getService(ReponseService.class);
    }

    /**
     * Retourne le service Vocabulary.
     * 
     * @return Service Vocabulary
     */
    public static ReponsesVocabularyService getVocabularyService() {
        return ServiceUtil.getService(ReponsesVocabularyService.class);
    }

    /**
     * 
     * Retourne le service de notification (WEBSERVICE).
     * 
     * @param <WsNotificationService>
     * @return Service de notification
     */
    public static WsNotificationService getWsNotificationService() {
        return ServiceUtil.getService(WsNotificationService.class);
    }

    public static StatsService getStatsService() {
        return ServiceUtil.getService(StatsService.class);
    }

    /**
     * Retourne le service de gestion du profil utilisateur
     * 
     * @return {@link ProfilUtilisateurService}
     */
    public static ProfilUtilisateurService getProfilUtilisateurService() {
        return ServiceUtil.getService(ProfilUtilisateurService.class);
    }

    /**
     * Retourne le service de gestion de la mise a jour des timbres
     * 
     * @return {@link UpdateTimbreService}
     */
    public static UpdateTimbreService getUpdateTimbreService() {
        return ServiceUtil.getService(UpdateTimbreService.class);
    }

    public static MailboxService getMailboxService() {
        return ServiceUtil.getService(MailboxService.class);
    }
    
    /**
     * Retourne le service Mail.
     * 
     * @return {@link ReponsesMailService}
     */
    public static ReponsesMailService getReponsesMailService() {
        return ServiceUtil.getService(ReponsesMailService.class);
    }
    
    public static ControleService getControleService(){
    	return ServiceUtil.getService(ControleService.class);
    }

    public static ReponsesAlertService getAlertService() {
        return ServiceUtil.getService(ReponsesAlertService.class);
    }

    /**
     * Retourne le service d'export
     * @return {@link ReponsesExportService}
     */
    public static ReponsesExportService getReponsesExportService() {
        return ServiceUtil.getService(ReponsesExportService.class);
    }
    
    /**
     * Retourne le service d'injection de gouvernement
     * @return {@link ReponsesInjectionGouvernementService}
     */
    public static ReponsesInjectionGouvernementService getReponsesInjectionGouvernementService() {
    	return ServiceUtil.getService(ReponsesInjectionGouvernementService.class);
    }

    /**
     * Retourne le service de l'arbitrage réponses
     * @return
     */
	public static ReponsesArbitrageService getReponsesArbitrageService() {
		return ServiceUtil.getService(ReponsesArbitrageService.class);
	}
	
	/**
	 * Retourne le service de migration réponses
	 * @return
	 */
	public static ReponsesMigrationService getReponsesMigrationService() {
		return ServiceUtil.getService(ReponsesMigrationService.class);
	}
	
	/**
	 * Retourne le service de documentrouting
	 * @return
	 */
	public static DocumentRoutingService getDocumentRoutingService() {
		return ServiceUtil.getService(DocumentRoutingService.class);
	}
	
	public static ExtractionService getExtractionService() {
		return ServiceUtil.getService(ExtractionService.class);
	}
}
