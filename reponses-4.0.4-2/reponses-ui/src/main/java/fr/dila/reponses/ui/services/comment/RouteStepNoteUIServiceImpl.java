package fr.dila.reponses.ui.services.comment;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.actions.impl.RouteStepNoteActionServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * WebBean permettant de gérer les notes d'étapes.
 *
 * @author jtremeaux
 */
public class RouteStepNoteUIServiceImpl extends RouteStepNoteActionServiceImpl implements RouteStepNoteUIService {

    @Override
    public boolean isEditableNote(
        SpecificContext context,
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel routeStepDoc,
        DocumentModel currentDoc
    ) {
        // Vérifie l'état de l'étape de feuille de route
        if (!super.isEditableNote(context, session, dossierDoc, routeStepDoc, currentDoc)) {
            return false;
        }

        // Le destinataire de la distribution peut modifier les notes d'étape
        if (ReponsesActionsServiceLocator.getReponsesDossierDistributionActionService().isDossierLinkLoaded(context)) {
            return true;
        }

        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        // L'administrateur fonctionnel peut modifier les notes d'étapes
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_UPDATER)) {
            return true;
        }

        // L'administrateur fonctionnel peut modifier les notes d'étapes de son ministère
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_MIN_UPDATER)) {
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            return ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant());
        }

        return false;
    }
}
