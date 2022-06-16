package fr.dila.reponses.api.service;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant d'effectuer des actions spécifiques sur les instances de feuille de route dans Réponses.
 *
 * @author jtremeaux
 */
public interface ReponseFeuilleRouteService extends SSFeuilleRouteService {
    /**
     * Retourne l'étape "Validation premier ministre" correspondant au dossier.
     *
     * @param session
     *            Session
     * @param feuilleRouteInstanceId
     *            Identifiant technique de l'instance de feuille de route
     * @return Etape validation PM
     *
     */
    DocumentModel getValidationPMStep(CoreSession session, String feuilleRouteInstanceId);

    /**
     * Ajout d'une étape Pour attente à la suite de l'étape en cours puis validation de l'étape en cours
     *
     * @param session
     *            session
     * @param dossierLink
     *            dossierLink en cours
     *
     */
    void addStepAttente(CoreSession session, DossierLink dossierLink);

    /**
     * Ajout des étapes 'pour retour', 'Pour signature' et 'Pour validation retour Premier ministre' à la suite de
     * l'étape en cours
     *
     * @param session
     *            session
     * @param etapeDoc
     *            doc de l'étape en cours
     *
     */
    void addStepValidationRetourPM(
        final CoreSession session,
        final DocumentModel etapeDoc,
        final DocumentModel dossierDoc
    );

    /**
     * Vérifie l'étape de feuille de route suivante, valide l'étape si c'est réattribution ou réorientation, dans le cas
     * de la réattribution on test si le ministère du poste associé à l'étape réattribution est différent du ministère
     * du poste de l'étape en cours.
     *
     * @param session
     *            session
     * @param routingTaskId
     *            id de l'étape en cours
     * @return true si réattribution ou réorientation
     *
     */
    boolean isNextStepReorientationOrReattributionOrArbitrage(CoreSession session, String routingTaskId);

    /**
     * Vérifie l'étape de feuille de route suivante, valide l'étape si c'est réattribution, dans le cas
     * de la réattribution on test si le ministère du poste associé à l'étape réattribution est différent du ministère
     * du poste de l'étape en cours.
     *
     * @param session
     *            session
     * @param routingTaskId
     *            id de l'étape en cours
     * @return true si réattribution
     *
     */
    boolean isNextStepReattributionOrArbitrage(CoreSession session, String routingTaskId);

    /**
     * Ajout d'une étape réorientation suite à un non concerné sur l'étape en cours
     *
     * @param session
     * @param routingTaskId
     * @return
     *
     */
    void addStepAfterReorientation(CoreSession session, DocumentModel etapeDoc);

    /**
     * Ajout d'une étape réorientation suite à un non concerné sur l'étape en cours
     *
     * @param session
     * @param etapeDoc
     * @param mailboxId
     *            id de la mailbox du poste à ajouter
     *
     */
    void addStepAfterReorientation(CoreSession session, DocumentModel etapeDoc, String mailboxId);

    /**
     * Ajout d'une étape réattribution suite à un non concerné sur l'étape en cours
     *
     * @param session
     * @param dossierLink
     *            dossierLink en cours
     * @param mailboxId
     *            id de la mailbox du poste à ajouter
     *
     */
    void addStepAfterReattribution(CoreSession documentManager, DossierLink dossierLink, String mailboxId);

    /**
     * Ajout d'une étape attribution suite à un rejet de réattribution sur l'étape en cours
     *
     * @param session
     * @param routingTaskId
     *
     */
    void addStepAfterRejectReattribution(CoreSession session, String routingTaskId);

    /**
     *
     * Retourne la liste des dossiers ayant été validé depuis moins de 7 jours sur le poste de l'utilisateur.
     *
     * @param session
     *            la session de l'utilisateur
     * @return
     *
     */
    List<Dossier> getLastWeekValidatedDossiers(CoreSession session, Collection<String> posteIds);

    /**
     * Ajoute des étapes pour signature et pour transmission aux assemblées après l'étape donnée
     *
     * @param session
     * @param routingTaskId
     *
     */
    void addStepsSignatureAndTransmissionAssemblees(
        CoreSession session,
        DocumentModel dossierDoc,
        SSRouteStep routeStep
    );

    /**
     * Ajout de nouvelles étapes suite au rejet de l'étape en cours.
     *
     * @param session
     *            Session
     * @param routingTaskId
     *            id de l'étape en cours
     * @param dossier
     *            dossier
     * @throws ClientException
     */
    boolean addStepAfterReject(CoreSession session, String routingTaskId, Dossier dossier);

    /**
     * Retourne true si la prochaine étape est de type Pour tranmission aux assemblées
     *
     * @param session
     * @param routingTaskId
     * @return
     *
     */
    boolean isNextStepTransmissionAssemblees(CoreSession session, String feuilleRouteDocId, DocumentModel stepDoc);

    /**
     * Retourne true si l'étape passée en paramètre est à la racine de sa feuille de route
     *
     * @param session
     *            CoreSession
     * @param routingTaskId
     *            id de l'étape de feuille de route
     * @return boolean
     *
     */
    boolean isRootStep(CoreSession session, String routingTaskId);

    /**
     * Envoi du mail après la distribution d'un dossier
     *
     * @param session
     * @param routeStep
     *
     */
    void sendMailAfterDistribution(CoreSession session, SSRouteStep routeStep);

    /**
     * Retourne la liste des étapes validées durant les dernières 24h
     *
     * @param session
     * @return
     *
     */
    List<SSRouteStep> getLastDayValidatedSteps(CoreSession session);

    /**
     * Envoi des mails aux utilisateurs dont des dossiers ont transités par leur corbeille durant les dernières 24h
     *
     * @param session
     *
     */
    void sendDailyDistributionMail(CoreSession session);

    void initDirectionFdr(CoreSession session);

    /**
     * Indique si l'étape suivante celle en cours est "pour arbitrage"
     *
     * @param session
     * @param routingTaskId
     * @return
     *
     */
    boolean isNextStepArbitrage(CoreSession session, String routingTaskId);

    Map<String, List<String>> getRunningStepsSinceDaysByPoste(final CoreSession session, int days);

    String getDirectionsRunningSteps(CoreSession session, Dossier dossier);
}
