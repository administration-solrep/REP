package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.actions.DossierActionService;
import fr.dila.reponses.ui.services.actions.ReponseActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ReponseActionServiceImpl implements ReponseActionService {
    private static final Log LOG = LogFactory.getLog(ReponseActionServiceImpl.class);

    @Override
    public boolean isReponseAtLastVersion(CoreSession session, DocumentModel dossierDoc) {
        DocumentModel reponse = dossierDoc.getAdapter(Dossier.class).getReponse(session).getDocument();
        boolean res =
            getReponseMajorVersionNumber(session, reponse) +
            1 ==
            getCurrentReponseNumeroVersion(session, dossierDoc, reponse).intValue();
        if (LOG.isDebugEnabled()) {
            LOG.debug("in isReponseAtLastVersion --> " + res);
        }
        return res;
    }

    private Long getCurrentReponseNumeroVersion(CoreSession session, DocumentModel dossierDoc, DocumentModel reponse) {
        DossierActionService dossierActions = ReponsesActionsServiceLocator.getDossierActionService();
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
        if (dossierActions.isDossierContainsMinistere(ssPrincipal, dossierDoc)) {
            // +1 car correspond au prochain numero de version qui sera
            // appliqué à la reponse en cours d'edition
            return Long.valueOf(reponseService.getReponseMajorVersionNumber(session, reponse) + 1);
        } else {
            return Long.valueOf(reponseService.getReponseMajorVersionNumber(session, reponse));
        }
    }

    private int getReponseMajorVersionNumber(CoreSession session, DocumentModel reponse) {
        final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
        int versionNumber = reponseService.getReponseMajorVersionNumber(session, reponse);
        if (LOG.isDebugEnabled()) {
            LOG.debug("major version number = " + versionNumber);
        }
        return versionNumber;
    }

    @Override
    public void saveReponse(SpecificContext context, DocumentModel reponseDoc, DocumentModel dossierDoc) {
        final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
        CoreSession session = context.getSession();

        // Sauvegarde la reponse
        reponseService.saveReponseAndErratum(session, reponseDoc, dossierDoc);

        Reponse rep = reponseDoc.getAdapter(Reponse.class);
        if (rep.getSignature() == null) {
            context.getMessageQueue().addInfoToQueue("La réponse a été enregistrée");
        }
    }

    @Override
    public boolean reponseHasChanged(CoreSession session, DocumentModel reponseDoc) {
        Reponse reponse = reponseDoc.getAdapter(Reponse.class);
        String currentTextReponse = reponse.getTexteReponse();
        int reponseVersionNumber = getReponseMajorVersionNumber(session, reponseDoc);
        // reponseVersionNumber = 0 si pas encore de reponse versionnée => testé
        // si version courante est no nulle
        // reponseVersionNumber > 0 si deja au moins une reponse versionné =>
        // comparé le contenue de la reponse
        if (reponseVersionNumber > 0) {
            final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
            // recupere le dernier snapshot pour comparer le contenu de la
            // reponse
            DocumentModel reponseOldVersionDocument = reponseService.getReponseOldVersionDocument(
                session,
                reponseDoc,
                reponseVersionNumber
            );
            Reponse reponseOldVersion = reponseOldVersionDocument.getAdapter(Reponse.class);
            return !StringUtils.equals(currentTextReponse, reponseOldVersion.getTexteReponse());
        } else {
            // pas de réponse produite si currentTextReponse == null dans ce la
            // reponse n'a pas changé
            // sinon renvoyé vrai
            return currentTextReponse != null;
        }
    }

    @Override
    public void briserReponse(SpecificContext context) {
        CoreSession session = context.getSession();
        Reponse reponse = context.getCurrentDocument().getAdapter(Dossier.class).getReponse(session);
        if (reponse != null) {
            ReponsesServiceLocator
                .getReponseService()
                .briserSignatureReponse(session, reponse, context.getCurrentDocument());
        }
    }

    @Override
    public boolean canUserBriserReponse(SpecificContext context) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        CoreSession session = context.getSession();
        return (
            STActionsServiceLocator.getDossierLockActionService().getCanUnlockCurrentDossier(context) &&
            ReponsesActionsServiceLocator.getReponseActionService().isReponseAtLastVersion(session, dossierDoc) &&
            ReponsesActionsServiceLocator.getParapheurActionService().canUserUpdateParapheur(context) &&
            ReponsesServiceLocator.getReponseService().isReponseSignee(session, dossierDoc)
        );
    }
}
