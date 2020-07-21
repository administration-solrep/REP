package fr.dila.ss.core.service;

import org.nuxeo.ecm.platform.comment.api.CommentManager;

import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.api.service.SSBirtService;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.ss.api.service.SSFondDeDossierService;
import fr.dila.ss.api.service.SSTreeService;
import fr.dila.ss.api.service.SSUtilisateurConnectionMonitorService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ServiceUtil;

/**
 * ServiceLocator du socle SOLREP : permet de rechercher les services du socle SOLREP.
 * 
 * @author jtremeaux
 */
public class SSServiceLocator extends STServiceLocator {

	/**
	 * utility class
	 */
	protected SSServiceLocator() {
		// do nothing
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
	 * Retourne le service DocumentRouting.
	 * 
	 * @return Service DocumentRouting
	 */
	public static DocumentRoutingService getDocumentRoutingService() {
		return ServiceUtil.getService(DocumentRoutingService.class);
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
	 * Retourne le service MailboxPoste.
	 * 
	 * @return Service MailboxPoste
	 */
	public static MailboxPosteService getMailboxPosteService() {
		return ServiceUtil.getService(MailboxPosteService.class);
	}

	/**
	 * Retourne le service STFeuilleRoute.
	 * 
	 * @return Service STFeuilleRoute
	 */
	public static SSFeuilleRouteService getSSFeuilleRouteService() {
		return ServiceUtil.getService(SSFeuilleRouteService.class);
	}

	/**
	 * Retourne le service SSBirtService.
	 * 
	 * @return Service SSBirtService
	 */
	public static SSBirtService getSSBirtService() {
		return ServiceUtil.getService(SSBirtService.class);
	}

	/**
	 * Retourne le service de gestion de statistiques des utilisateurs connecte
	 * 
	 * @return {@link SSUtilisateurConnectionMonitorService}
	 */
	public static SSUtilisateurConnectionMonitorService getUtilisateurConnectionMonitorService() {
		return ServiceUtil.getService(SSUtilisateurConnectionMonitorService.class);
	}

	/**
	 * Retourn le service SSFondDeDossierService
	 * 
	 * @return Service SSFondDeDossierService
	 */
	public static SSFondDeDossierService getSSFondDeDossierService() {
		return ServiceUtil.getService(SSFondDeDossierService.class);
	}

	public static SSTreeService getSSTreeService() {
		return ServiceUtil.getService(SSTreeService.class);
	}

	public static CommentManager getCommentManager() {
		return ServiceUtil.getService(CommentManager.class);
	}

}
