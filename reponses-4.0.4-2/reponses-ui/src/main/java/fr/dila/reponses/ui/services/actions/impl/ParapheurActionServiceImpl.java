package fr.dila.reponses.ui.services.actions.impl;

import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getReponseActionService;
import static fr.dila.st.ui.mapper.MapDoc2Bean.beanToDoc;
import static fr.dila.st.ui.services.actions.STActionsServiceLocator.getDossierLockActionService;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.reponses.ui.services.actions.ParapheurActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSCorbeilleActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ParapheurActionServiceImpl implements ParapheurActionService {

    @Override
    public void saveDossier(SpecificContext context, ParapheurDTO data) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        if (!canSaveDossier(context, dossierDoc)) {
            context.getMessageQueue().addWarnToQueue("Le parapheur ne peut pas être sauvegardé");
            return;
        }

        // save reponse
        DocumentModel reponseDoc = dossierDoc.getAdapter(Dossier.class).getReponse(context.getSession()).getDocument();
        beanToDoc(data, reponseDoc, true);
        ReponsesActionsServiceLocator.getReponseActionService().saveReponse(context, reponseDoc, dossierDoc);
    }

    @Override
    public boolean canUserUpdateParapheur(SpecificContext context) {
        // L'utilisateur peut modifier le parapheur si il est destinataire de la
        // distribution
        SSCorbeilleActionService ssCorbeilleActions = SSActionsServiceLocator.getSSCorbeilleActionService();
        if (ssCorbeilleActions.getCurrentDossierLink(context) != null) {
            return true;
        }

        // L'administrateur fonctionnel peut modifier le parapheur des dossiers
        // en cours
        // Avoir l'état du dossier n'est pas très interessant (toujours en
        // cours). On préfère savoir
        // si la feuille de route est en cours (c'est à dire si le dossier est
        // actif)
        final Dossier dossier = context.getCurrentDocument().getAdapter(Dossier.class);
        CoreSession session = context.getSession();
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        if (
            ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER) &&
            dossier.isRunning() &&
            dossier.isActive(session)
        ) {
            return true;
        }

        // L'administrateur ministériel peut modifier le parapheur des dossiers
        // en cours de son ministère
        if (
            ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER) &&
            dossier.isRunning() &&
            dossier.isActive(session) &&
            ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant())
        ) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canSaveDossier(SpecificContext context, DocumentModel dossierDoc) {
        CoreSession session = context.getSession();

        // Si la réponse est signée, on refuse la sauvegarde du dossier
        if (ReponsesServiceLocator.getReponseService().isReponseSignee(session, dossierDoc)) {
            return false;
        }

        boolean canUnlockCurrentDossier = getDossierLockActionService().getCanUnlockCurrentDossier(context);
        boolean reponseAtLastVersion = getReponseActionService().isReponseAtLastVersion(session, dossierDoc);
        boolean canUserUpdateParapheur = canUserUpdateParapheur(context);
        return canUnlockCurrentDossier && reponseAtLastVersion && canUserUpdateParapheur;
    }
}
