package fr.dila.reponses.web.feuilleroute;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.web.dossier.DossierDistributionActionsBean;
import fr.dila.ss.api.security.principal.SSPrincipal;

/**
 * WebBean permettant de gérer les notes d'étapes.
 *
 * @author jtremeaux
 */
@Name("routeStepNoteActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK + 1)
public class RouteStepNoteActionsBean extends fr.dila.ss.web.feuilleroute.RouteStepNoteActionsBean implements Serializable {
    @In(create = true, required = false)
    protected transient DossierDistributionActionsBean dossierDistributionActions;

    @In(required = true, create = true)
    protected transient SSPrincipal ssPrincipal;

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isEditableNote(DocumentModel routeStepDoc) throws ClientException {
        // Vérifie l'état de l'étape de feuille de route
        if (!super.isEditableNote(routeStepDoc)) {
            return false;
        }
        
        // Le destinataire de la distribution peut modifier les notes d'étape
        if (dossierDistributionActions.isDossierLinkLoaded()) {
            return true;
        }
        
        // L'administrateur fonctionnel peut modifier les notes d'étapes
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_UPDATER)) {
            return true;
        }
        
        // L'administrateur fonctionnel peut modifier les notes d'étapes de son ministère
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_MIN_UPDATER)) {
            DocumentModel dossierDoc = getCurrentDossierDoc();
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            if (ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant())) {
                return true;
            }
        }
        
        return false;
    }
}
