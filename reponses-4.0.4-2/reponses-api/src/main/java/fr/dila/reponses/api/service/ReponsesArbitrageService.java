package fr.dila.reponses.api.service;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

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
     *
     */
    FeuilleRouteStep createStepArbitrageSGG(final CoreSession session);

    /**
     * Renvoie true sur l'ont se trouve sur une étape "Pour Arbitrage"
     *
     * @return
     *
     */
    boolean isStepPourArbitrage(STDossierLink dossierLink);

    /**
     * Ajout d'une étape Arbitrage SGG à la suite de l'étape en cours
     *
     * @param session session
     * @param routingTaskId id de l'étape en cours
     *
     */
    void addStepArbitrageSGG(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc);

    /**
     * Vérifie que le dossier n'est pas bloqué pour arbitrage
     */
    boolean canAddStepArbitrageSGG(DocumentModel dossierDoc);

    /**
     * Met à jour le dossier (et son lot s'il existe) après un arbitrage
     * @param session
     * @param dossierDoc
     *
     */
    void updateDossierAfterArbitrage(CoreSession session, DocumentModel dossierDoc);

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
    void attributionAfterArbitrage(
        CoreSession session,
        DossierLink dossierLink,
        DocumentModel dossierDoc,
        String idMinistere,
        String arbitrageObservations
    )
        throws ReponsesException;

    /**
     * Lance l'action de réattribution directe sur une dossier
     * @param session
     * @param dossierLink
     * @param dossierDoc
     * @param idMinistere
     * @throws ReponsesException
     */
    void reattributionDirecte(
        CoreSession session,
        DossierLink dossierLink,
        DocumentModel dossierDoc,
        String idMinistere,
        String observations
    )
        throws ReponsesException;
}
