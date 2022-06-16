package fr.dila.reponses.core.service;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation du service de document routing de Réponses.
 *
 * @author jtremeaux
 */
public class DocumentRoutingServiceImpl
    extends fr.dila.ss.core.service.DocumentRoutingServiceImpl
    implements DocumentRoutingService {

    /**
     * Default constructor
     */
    public DocumentRoutingServiceImpl() {
        super();
    }

    @Override
    public void validateMoveRouteStepBefore(DocumentModel routeStepDoc) {
        // NOP
    }

    @Override
    public boolean isEtapeObligatoireUpdater(CoreSession session, DocumentModel routeStepDoc) {
        // Traite uniquement les étapes de feuille de route et pas les conteneurs
        if (routeStepDoc.hasFacet("Folderish")) {
            return true;
        }
        final SSRouteStep routeStep = routeStepDoc.getAdapter(SSRouteStep.class);
        final List<String> groups = session.getPrincipal().getGroups();

        if (routeStep.isObligatoireSGG()) {
            // Droit de modifier les étapes obligatoires SGG
            return groups.contains(ReponsesBaseFunctionConstant.FDR_OBLIGATOIRE_SGG_UPDATER);
        }

        // Seul l'administrateur ministériel a le droit de modifier les étapes obligatoires ministère
        if (routeStep.isObligatoireMinistere()) {
            return groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER);
        }

        return true;
    }

    @Override
    public DocumentModel getCurrentEtape(final CoreSession session, final DossierLink dossierLink) {
        if (dossierLink == null) {
            return null;
        } else {
            String routingTaskId = null;
            try {
                // ajout du try/catch : retourne null si on a pas les droits en lecture sur le caseLink
                routingTaskId = dossierLink.getRoutingTaskId();
            } catch (final Exception e) {
                return null;
            }
            return new UnrestrictedGetDocumentRunner(session).getById(routingTaskId);
        }
    }
}
