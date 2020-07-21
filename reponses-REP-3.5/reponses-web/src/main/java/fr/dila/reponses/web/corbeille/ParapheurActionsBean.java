package fr.dila.reponses.web.corbeille;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.web.client.ParapheurDTO;
import fr.dila.reponses.web.client.ParapheurDTOImpl;
import fr.dila.reponses.web.dossier.DossierActionsBean;
import fr.dila.reponses.web.dossier.DossierDistributionActionsBean;
import fr.dila.reponses.web.dossier.ReponseActionsBean;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.web.context.NavigationContextBean;
import fr.dila.st.web.dossier.DossierLockActionsBean;

/**
 * WebBean permettant de gérer le parapheur.
 * 
 * @author asatre
 */
@Name("parapheurActions")
@Scope(ScopeType.CONVERSATION)
public class ParapheurActionsBean implements Serializable {

    private static final long serialVersionUID = -2958008334264868582L;

    @In(create = true, required = true)
    protected transient DossierLockActionsBean dossierLockActions;

    @In(create = true, required = true)
    protected transient ReponseActionsBean reponseActions;

    @In(create = true, required = true)
    protected transient DossierDistributionActionsBean dossierDistributionActions;
    
    @In(create = true, required = true)
    protected transient DossierActionsBean dossierActions;

    @In(required = true, create = true)
    protected transient SSPrincipal ssPrincipal;
    
    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;
    
    @In(create = true, required = true)
    protected transient CoreSession documentManager;
    
    @In(create = true)
    protected transient CorbeilleActionsBean corbeilleActions;

    public ParapheurDTO initAndGetParapheurDTO() throws ClientException {
        Boolean lockCurrentDossier = dossierLockActions.getCanLockCurrentDossier();
        Boolean unLockCurrentDossier = dossierLockActions.getCanUnlockCurrentDossier();
        Boolean reponseAtLastVersion = reponseActions.isReponseAtLastVersion();
        Boolean userUpdateParapheur = canUserUpdateParapheur();
        Boolean reponseSignee = dossierActions.isReponseSignee();
        List<DocumentModel> listReponseVersion = reponseActions.getReponseVersionList();
        Boolean reponsePublished = reponseActions.isReponsePublished();
        Boolean reponseHasTexte = reponseActions.reponseHasTexte();
        Boolean reponseHasErratum = reponseActions.reponseHasErratum();
        Boolean userMailboxInDossier = dossierActions.isUserMailboxInDossier();
        Boolean isAdminFonctionnel = ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER);
        return new ParapheurDTOImpl(lockCurrentDossier, unLockCurrentDossier, reponseAtLastVersion, userUpdateParapheur, reponseSignee,
                listReponseVersion, reponsePublished, reponseHasTexte, reponseHasErratum, userMailboxInDossier, isAdminFonctionnel);
    }
    
    /**
     * Retourne vrai si l'utilisateur peut modifier le parapheur.
     * 
     * @return Condition
     * @throws ClientException
     */
    private boolean canUserUpdateParapheur() throws ClientException {
        // L'utilisateur peut modifier le parapheur si il est destinataire de la distribution
        if (corbeilleActions.getCurrentDossierLink() != null) {
            return true;
        }

        // L'administrateur fonctionnel peut modifier le parapheur des dossiers en cours
        // Avoir l'état du dossier n'est pas très interessant (toujours en cours). On préfère savoir
        // si la feuille de route est en cours (c'est à dire si le dossier est actif)
        final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER) && dossier.isRunning()
                && dossier.isActive(documentManager)) {
            return true;
        }

        // L'administrateur ministériel peut modifier le parapheur des dossiers en cours de son ministère
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER) && dossier.isRunning()
                && dossier.isActive(documentManager) && ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant())) {
            return true;
        }

        return false;
    }
}