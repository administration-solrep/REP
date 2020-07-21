package fr.dila.reponses.core.operation.nxshell;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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

import fr.dila.cm.cases.CaseConstants;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Une opération pour réparer un dossier
 * 
 * Paramètre : - id : id du dossier (obligatoire) - ministereId : id du ministère (pas encore utilisé) prévu pour
 * changer le champ idMinistereAttributaireCourant du dossier - mode : si mode = info, affiche seulement les feuilles de
 * route trouvée, ne modifie pas le dossier
 * 
 * @author feo
 */
@Operation(id = ReparerDossierOperation.ID, category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY, label = "ReparerDossier", description = "Permet la réparation de la feuille de route d'un dossier")
public class ReparerDossierOperation {
	/**
	 * Identifiant technique de l'opération.
	 */
	public static final String	ID				= "Reponses.Reparer.Dossier";

	/**
	 * Logger.
	 */
	private static final Log	log				= LogFactory.getLog(ReparerDossierOperation.class);

	private static final String	MODE_INFO		= "info";

	@Context
	protected CoreSession		session;

	@Param(name = "id", required = true)
	protected String			id;

	@Param(name = "mode", required = false)
	String						mode			= "";

	@Param(name = "startRoute", required = false)
	String						startRouteId	= "";

	@Param(name = "stopRoute", required = false)
	String						stopRouteId		= "";

	@OperationMethod
	public void run() throws Exception {

		log.info("-------------------------------------------------------------------------------");
		log.info("Début opération Réponses.Reparer.Dossier");

		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		final DocumentRoutingService routingService = SSServiceLocator.getDocumentRoutingService();
		final DossierDistributionService dossierDistributionService = ReponsesServiceLocator
				.getDossierDistributionService();

		if (StringUtils.isEmpty(id)) {
			throw new OperationException("Le paramètre id ne doit pas être vide");
		}

		DocumentModel dossierDoc = session.getDocument(new IdRef(id));

		if (dossierDoc == null) {
			throw new OperationException("Aucun dossier trouvé avec l'id : " + id);
		}

		DocumentRoute startRoute = null;
		DocumentRoute stopRoute = null;
		if (!StringUtils.isEmpty(startRouteId) && !StringUtils.isEmpty(stopRouteId)) {
			throw new OperationException("Vous ne pas utiliser startRoute et stopRoute simultanément");
		} else {
			if (!StringUtils.isEmpty(startRouteId)) {

				DocumentModel startRouteDoc = session.getDocument(new IdRef(startRouteId));
				if (startRouteDoc != null) {
					try {
						startRoute = startRouteDoc.getAdapter(DocumentRoute.class);
					} catch (Exception e) {
						throw new OperationException(
								"L'id de la route à démarrer ne correspond pas à une feuille de route", e);
					}
				} else {
					throw new OperationException("L'id de la route à démarrer ne correspond pas à une feuille de route");
				}
			}

			if (!StringUtils.isEmpty(stopRouteId)) {

				DocumentModel stopRouteDoc = session.getDocument(new IdRef(stopRouteId));
				if (stopRouteDoc != null) {
					try {
						stopRoute = stopRouteDoc.getAdapter(DocumentRoute.class);
					} catch (Exception e) {
						throw new OperationException(
								"L'id de la route à arrêter ne correspond pas a une feuille de route", e);
					}
				} else {
					throw new OperationException("L'id de la route à arrêter ne correspond pas a une feuille de route");
				}
			}
		}

		Dossier dossier = dossierDoc.getAdapter(Dossier.class);

		List<DocumentRoute> routeList = routingService.getDocumentRoutesForAttachedDocument(session, id, null);

		List<DocumentRoute> routeListReady = new ArrayList<DocumentRoute>();
		List<DocumentRoute> routeListRunning = new ArrayList<DocumentRoute>();
		List<DocumentRoute> routeListCanceled = new ArrayList<DocumentRoute>();
		List<DocumentRoute> routeListDone = new ArrayList<DocumentRoute>();

		int nbRouteTotal = routeList.size();
		log.info("Feuille de route trouvée pour le dossier : " + nbRouteTotal);

		if (nbRouteTotal > 0) {
			log.info("Liste des feuilles de route ");
			for (DocumentRoute route : routeList) {

				String titre = route.getDocument().getTitle();
				String lifeCycle = route.getDocument().getCurrentLifeCycleState();
				String idRoute = route.getDocument().getId();

				log.info("Titre feuille de route : " + titre);
				log.info("Id feuille de route : " + idRoute);
				log.info("LifeCycle feuille de route : " + lifeCycle);

				if (DocumentRouteElement.ElementLifeCycleState.ready.toString().equals(lifeCycle)) {
					routeListReady.add(route);
				} else if (DocumentRouteElement.ElementLifeCycleState.running.toString().equals(lifeCycle)) {
					routeListRunning.add(route);
				} else if (DocumentRouteElement.ElementLifeCycleState.canceled.toString().equals(lifeCycle)) {
					routeListCanceled.add(route);
				} else if (DocumentRouteElement.ElementLifeCycleState.done.toString().equals(lifeCycle)) {
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
			log.info("Route démarrée");
			return;
		}

		if (stopRoute != null) {
			if (!isRouteInList(stopRoute, routeList)) {
				throw new OperationException("La route à stopper n'appartient pas au dossier");
			}
			stopRoute.cancel(session);
			log.info("Route arrêtée");
			return;
		}

		boolean startNewRoute = true;

		if (routeListRunning.size() == 1) {
			// route ok
			// Verifier le ministère attributaire
			log.info("Une route en cours trouvée pour le dossier");
			DocumentRoute route = routeListRunning.get(0);
			STFeuilleRoute stRoute = route.getDocument().getAdapter(STFeuilleRoute.class);
			String ministereRouteId = stRoute.getMinistere();

			OrganigrammeNode node = ministeresService.getEntiteNode(ministereRouteId);
			if (node == null || !node.isActive()) {
				log.info("Le ministere de la route n'est pas actif : " + ministereRouteId);
			}

			// log.info("Changement du ministere attributaire courant par celui de la route");
			// dossier.setIdMinistereAttributaireCourant(ministereRouteId);
			// session.save();

			startNewRoute = false;

		} else if (routeListRunning.size() > 1) {
			// plus d'une route en cours ?
			log.info("Plus d'une route en cours pour le dossier");
			log.info("Pas de réparation implémentée pour ce cas, veulliez arrêter une route avec le paramètre stopRoute");

			startNewRoute = false;
		} else if (routeListRunning.size() < 1) {
			// pas de route en cours
			log.info("Pas de route en cours pour le dossier");
			if (routeListReady.size() == 1) {
				log.info("Une route en ready trouvée pour le dossier");
				DocumentRoute route = routeListReady.get(0);

				log.info("Démarrage de la route : " + route.getDocument().getId());
				route.run(session);
				dossier.setLastDocumentRoute(route.getDocument().getId());
				session.saveDocument(dossier.getDocument());
				session.save();

				startNewRoute = false;
			} else if (routeListReady.size() > 1) {
				// plus d'une route en ready affecté au dossier ?
				log.info("Plus d'une route en ready pour le dossier");
				log.info("Pas de réparation implémentée pour ce cas, veulliez démarrer une route avec le paramètre startRoute");

				startNewRoute = false;
			}
		}

		if (startNewRoute) {

			if (StringUtils.isEmpty(dossier.getIdMinistereAttributaireCourant())) {
				log.info("Lancement de la recherche de feuille de route et démarrage en-cours");
				dossierDistributionService.startDefaultRoute(session, dossier);
				log.info("Fin de l'attibution de feuille de route");
			}
		}

		log.info("Fin de l'opération");
		log.info("-------------------------------------------------------------------------------");
		return;
	}

	private boolean isRouteInList(DocumentRoute route, List<DocumentRoute> routeList) {

		for (DocumentRoute item : routeList) {
			if (item.getDocument().getId().equals(route.getDocument().getId())) {
				return true;
			}
		}

		return false;
	}

}
