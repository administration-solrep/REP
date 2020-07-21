package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.security.principal.STPrincipal;

/**
 * Service de l'arbitrage réponses
 *
 */
public interface ReponsesArbitrageService {

	/**
     * Création d'une étape Pour arbitrage SGG.
     * 
     * @param session Session
     * @return Nouvelle étape Pour arbitrage SGG
     * @throws ClientException
     */
    DocumentRouteStep createStepArbitrageSGG(final CoreSession session) throws ClientException;

    /**
     * Renvoie true sur l'ont se trouve sur une étape "Pour Arbitrage"
     * 
     * @return
     * @throws ClientException
     */
	boolean isStepPourArbitrage(STDossierLink dossierLink) throws ClientException;

    /**
     * Ajout d'une étape Arbitrage SGG à la suite de l'étape en cours
     * 
     * @param session session
     * @param routingTaskId id de l'étape en cours
     * @throws ClientException
     */
	void addStepArbitrageSGG(CoreSession session, DocumentModel dossierDoc,	DocumentModel dossierLinkDoc) throws ClientException;

	/**
	 * Vérifie que le dossier n'est pas bloqué pour arbitrage
	 */
	boolean canAddStepArbitrageSGG(DocumentModel dossierDoc);

	/**
	 * Met à jour le dossier (et son lot s'il existe) après un arbitrage
	 * @param session
	 * @param dossierDoc
	 * @throws ClientException 
	 */
	void updateDossierAfterArbitrage(CoreSession session, DocumentModel dossierDoc) throws ClientException;
	
	/**
	 * Vérifie que l'utilisateur a le droit d'utilisation de la fonctionnalité de réattribution directe
	 * @param session
	 * @param principal
	 * @return
	 */
	boolean canUseReattributionDirecte(CoreSession session, STPrincipal principal);
	
	/**
	 * Effectue l'attribution après un arbitrage.
	 * Note l'étape en cours comme "non concerné", puis attribue le dossier au ministère passé en paramètre
	 * + ajoute la note sur l'étape en cours
	 * @param session
	 * @param dossierLink 
	 * @param dossierDoc le dossier à attribuer
	 * @param idMinistere le ministère destinataire
	 * @param arbitrageObservations 
	 * @throws ReponsesException 
	 */
	void attributionAfterArbitrage(CoreSession session, DossierLink dossierLink, DocumentModel dossierDoc, String idMinistere, String arbitrageObservations) throws ReponsesException;

	/**
	 * Lance l'action de réattribution directe sur une dossier
	 * @param session
	 * @param dossierLink
	 * @param dossierDoc
	 * @param idMinistere
	 * @throws ReponsesException
	 */
	void reattributionDirecte(CoreSession session, DossierLink dossierLink,
			DocumentModel dossierDoc, String idMinistere, String observations)
			throws ReponsesException;
}
