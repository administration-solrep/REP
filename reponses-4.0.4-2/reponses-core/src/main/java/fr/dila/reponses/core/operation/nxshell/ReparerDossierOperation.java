package fr.dila.reponses.core.operation.nxshell;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

/**
 * Une opération pour réparer un dossier
 *
 * Paramètre : - id : id du dossier (obligatoire) - ministereId : id du ministère (pas encore utilisé) prévu pour
 * changer le champ idMinistereAttributaireCourant du dossier - mode : si mode = info, affiche seulement les feuilles de
 * route trouvée, ne modifie pas le dossier
 *
 * @author feo
 */
@Operation(
    id = ReparerDossierOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "ReparerDossier",
    description = "Permet la réparation de la feuille de route d'un dossier"
)
public class ReparerDossierOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Reparer.Dossier";

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(ReparerDossierOperation.class);

    private static final String MODE_INFO = "info";

    @Context
    protected CoreSession session;

    @Param(name = "id", required = true)
    protected String id;

    @Param(name = "mode", required = false)
    private String mode = "";

    @Param(name = "startRoute", required = false)
    private String startRouteId = "";

    @Param(name = "stopRoute", required = false)
    private String stopRouteId = "";

    @OperationMethod
    public void run() throws OperationException {
        LOG.info("-------------------------------------------------------------------------------");
        LOG.info("Début opération Réponses.Reparer.Dossier");

        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
        final DocumentRoutingService routingService = SSServiceLocator.getDocumentRoutingService();
        final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();

        if (StringUtils.isEmpty(id)) {
            throw new OperationException("Le paramètre id ne doit pas être vide");
        }

        DocumentModel dossierDoc = session.getDocument(new IdRef(id));

        if (dossierDoc == null) {
            throw new OperationException("Aucun dossier trouvé avec l'id : " + id);
        }

        FeuilleRoute startRoute = null;
        FeuilleRoute stopRoute = null;
        if (StringUtils.isNotEmpty(startRouteId) && StringUtils.isNotEmpty(stopRouteId)) {
            throw new OperationException("Vous ne pouvez pas utiliser startRoute et stopRoute simultanément");
        } else {
            if (StringUtils.isNotEmpty(startRouteId)) {
                DocumentModel startRouteDoc = session.getDocument(new IdRef(startRouteId));
                if (startRouteDoc != null) {
                    try {
                        startRoute = startRouteDoc.getAdapter(FeuilleRoute.class);
                    } catch (Exception e) {
                        throw new OperationException(
                            "L'id de la route à démarrer ne correspond pas à une feuille de route",
                            e
                        );
                    }
                } else {
                    throw new OperationException(
                        "L'id de la route à démarrer ne correspond pas à une feuille de route"
                    );
                }
            }

            if (StringUtils.isNotEmpty(stopRouteId)) {
                DocumentModel stopRouteDoc = session.getDocument(new IdRef(stopRouteId));
                if (stopRouteDoc != null) {
                    try {
                        stopRoute = stopRouteDoc.getAdapter(FeuilleRoute.class);
                    } catch (Exception e) {
                        throw new OperationException(
                            "L'id de la route à arrêter ne correspond pas a une feuille de route",
                            e
                        );
                    }
                } else {
                    throw new OperationException("L'id de la route à arrêter ne correspond pas a une feuille de route");
                }
            }
        }

        Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        List<FeuilleRoute> routeList = routingService.getRoutesForAttachedDocument(session, id);

        List<FeuilleRoute> routeListReady = new ArrayList<>();
        List<FeuilleRoute> routeListRunning = new ArrayList<>();
        List<FeuilleRoute> routeListCanceled = new ArrayList<>();
        List<FeuilleRoute> routeListDone = new ArrayList<>();

        int nbRouteTotal = routeList.size();
        LOG.info("Feuille de route trouvée pour le dossier : " + nbRouteTotal);

        if (nbRouteTotal > 0) {
            LOG.info("Liste des feuilles de route ");
            for (FeuilleRoute route : routeList) {
                String titre = route.getDocument().getTitle();
                String lifeCycle = route.getDocument().getCurrentLifeCycleState();
                String idRoute = route.getDocument().getId();

                LOG.info("Titre feuille de route : " + titre);
                LOG.info("Id feuille de route : " + idRoute);
                LOG.info("LifeCycle feuille de route : " + lifeCycle);

                if (FeuilleRouteElement.ElementLifeCycleState.ready.toString().equals(lifeCycle)) {
                    routeListReady.add(route);
                } else if (FeuilleRouteElement.ElementLifeCycleState.running.toString().equals(lifeCycle)) {
                    routeListRunning.add(route);
                } else if (FeuilleRouteElement.ElementLifeCycleState.canceled.toString().equals(lifeCycle)) {
                    routeListCanceled.add(route);
                } else if (FeuilleRouteElement.ElementLifeCycleState.done.toString().equals(lifeCycle)) {
                    routeListDone.add(route);
                }
            }
        }

        if (mode.equals(MODE_INFO)) {
            return;
        }

        if (startRoute != null) {
            if (!isRouteInList(startRoute, routeList)) {
                throw new OperationException("La route à démarrer n'appartient pas au dossier");
            }
            if (startRoute.getDocument().getCurrentLifeCycleState().equals(STDossier.DossierState.done.name())) {
                startRoute.backToReady(session);
            }
            startRoute.run(session);
            dossier.setLastDocumentRoute(startRoute.getDocument().getId());
            session.saveDocument(dossier.getDocument());
            session.save();
            LOG.info("Route démarrée");
            return;
        }

        if (stopRoute != null) {
            if (!isRouteInList(stopRoute, routeList)) {
                throw new OperationException("La route à stopper n'appartient pas au dossier");
            }
            stopRoute.cancel(session);
            LOG.info("Route arrêtée");
            return;
        }

        boolean startNewRoute = true;

        if (routeListRunning.size() == 1) {
            // route ok
            // Verifier le ministère attributaire
            LOG.info("Une route en cours trouvée pour le dossier");
            FeuilleRoute route = routeListRunning.get(0);
            SSFeuilleRoute stRoute = route.getDocument().getAdapter(SSFeuilleRoute.class);
            String ministereRouteId = stRoute.getMinistere();

            OrganigrammeNode node = ministeresService.getEntiteNode(ministereRouteId);
            if (node == null || !node.isActive()) {
                LOG.info("Le ministere de la route n'est pas actif : " + ministereRouteId);
            }

            // log.info("Changement du ministere attributaire courant par celui de la route");
            // dossier.setIdMinistereAttributaireCourant(ministereRouteId);
            // session.save();

            startNewRoute = false;
        } else if (routeListRunning.size() > 1) {
            // plus d'une route en cours ?
            LOG.info("Plus d'une route en cours pour le dossier");
            LOG.info(
                "Pas de réparation implémentée pour ce cas, veulliez arrêter une route avec le paramètre stopRoute"
            );

            startNewRoute = false;
        } else if (routeListRunning.size() < 1) {
            // pas de route en cours
            LOG.info("Pas de route en cours pour le dossier");
            if (routeListReady.size() == 1) {
                LOG.info("Une route en ready trouvée pour le dossier");
                FeuilleRoute route = routeListReady.get(0);

                LOG.info("Démarrage de la route : " + route.getDocument().getId());
                route.run(session);
                dossier.setLastDocumentRoute(route.getDocument().getId());
                session.saveDocument(dossier.getDocument());
                session.save();

                startNewRoute = false;
            } else if (routeListReady.size() > 1) {
                // plus d'une route en ready affecté au dossier ?
                LOG.info("Plus d'une route en ready pour le dossier");
                LOG.info(
                    "Pas de réparation implémentée pour ce cas, veulliez démarrer une route avec le paramètre startRoute"
                );

                startNewRoute = false;
            }
        }

        if (startNewRoute) {
            if (StringUtils.isEmpty(dossier.getIdMinistereAttributaireCourant())) {
                LOG.info("Lancement de la recherche de feuille de route et démarrage en-cours");
                dossierDistributionService.startDefaultRoute(session, dossier);
                LOG.info("Fin de l'attibution de feuille de route");
            }
        }

        LOG.info("Fin de l'opération");
        LOG.info("-------------------------------------------------------------------------------");
        return;
    }

    private boolean isRouteInList(FeuilleRoute route, List<FeuilleRoute> routeList) {
        for (FeuilleRoute item : routeList) {
            if (item.getDocument().getId().equals(route.getDocument().getId())) {
                return true;
            }
        }

        return false;
    }
}
