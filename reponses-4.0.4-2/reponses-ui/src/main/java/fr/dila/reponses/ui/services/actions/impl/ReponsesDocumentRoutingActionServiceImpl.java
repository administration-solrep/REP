package fr.dila.reponses.ui.services.actions.impl;

import static fr.dila.reponses.api.constant.VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION;
import static java.util.Optional.ofNullable;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesDocumentRoutingActionService;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.impl.SSDocumentRoutingActionServiceImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ReponsesDocumentRoutingActionServiceImpl
    extends SSDocumentRoutingActionServiceImpl
    implements ReponsesDocumentRoutingActionService {

    @Override
    public boolean isFeuilleRouteVisible(CoreSession sesson, SSPrincipal ssPrincipal, DocumentModel dossierDoc) {
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.FEUILLE_ROUTE_VIEWER)) {
            return true;
        }
        return ReponsesActionsServiceLocator
            .getDossierActionService()
            .isDossierContainsMinistere(ssPrincipal, dossierDoc);
    }

    @Override
    public boolean isStepTransmissionAssemblees(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        return (
            dossierLink != null &&
            VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(dossierLink.getRoutingTaskType())
        );
    }

    @Override
    public boolean isStepInMinistere(SpecificContext context) {
        SSPrincipal ssPrincipal = (SSPrincipal) context.getSession().getPrincipal();
        if (
            ssPrincipal.isAdministrator() ||
            ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER)
        ) {
            // L'administrateur fonctionnel a le droit de lecture et d'ecriture
            // sur tous les dossiers
            return true;
        }

        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        String mailboxId;

        if (dossierLink == null) {
            if (context.getCurrentDocument() != null) {
                Dossier dossier = context.getCurrentDocument().getAdapter(Dossier.class);

                if (
                    ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant()) &&
                    ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER)
                ) {
                    // Si le dossier appartient à son Ministère, l'admin ministériel peut vérouiller le dossier
                    return true;
                }

                List<DocumentModel> stepsDoc = ReponsesServiceLocator
                    .getFeuilleRouteService()
                    .getRunningSteps(context.getSession(), dossier.getLastDocumentRoute());

                if (stepsDoc != null) {
                    for (DocumentModel stepDoc : stepsDoc) {
                        ReponsesRouteStep step = stepDoc.getAdapter(ReponsesRouteStep.class);
                        mailboxId = step.getDistributionMailboxId();
                        if (isMailboxIdInUserPostes(ssPrincipal, mailboxId)) {
                            return true;
                        }
                    }
                }
            }
        } else {
            mailboxId = dossierLink.getDistributionMailbox();
            return isMailboxIdInUserPostes(ssPrincipal, mailboxId);
        }

        return false;
    }

    /**
     * vérifie que l'id de mailbox passé en paramètre est une mailbox associée à l'utilisateur courant. S'il s'agit
     * d'une mailbox d'un poste SGG on retourne faux, les utilisateurs n'ayant pas le droit de faire une action sur un
     * dossier d'un poste SGG
     *
     * @param mailboxId
     * @return vrai si l'utilisateur courant dispose de la mailbox parmi ses postes
     */
    protected boolean isMailboxIdInUserPostes(SSPrincipal ssPrincipal, String mailboxId) {
        if (mailboxId != null) {
            MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
            String posteId = mailboxPosteService.getPosteIdFromMailboxId(mailboxId);
            PosteNode posteNode = STServiceLocator.getSTPostesService().getPoste(posteId);
            if (posteNode.isChargeMissionSGG()) {
                // aucun utilisateur ne peut faire une action sur un dossier d'un poste SGG
                return false;
            }

            // on recupere le ministere du poste
            List<EntiteNode> ministereNode = STServiceLocator
                .getSTMinisteresService()
                .getMinistereParentFromPoste(posteId);
            if (ministereNode != null) {
                for (EntiteNode entiteNode : ministereNode) {
                    if (ssPrincipal.getMinistereIdSet().contains(entiteNode.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isNextStepReorientationOrReattributionOrArbitrage(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        if (dossierLink != null) {
            return ReponsesServiceLocator
                .getFeuilleRouteService()
                .isNextStepReorientationOrReattributionOrArbitrage(
                    context.getSession(),
                    dossierLink.getRoutingTaskId()
                );
        }
        return false;
    }

    @Override
    public boolean isStepSignature(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        if (dossierLink != null) {
            return VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE.equals(dossierLink.getRoutingTaskType());
        }
        return false;
    }

    @Override
    public boolean isFirstStepInBranchOrParallel(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        if (dossierLink != null) {
            return ReponsesServiceLocator
                .getFeuilleRouteService()
                .isFirstStepInBranchOrParallel(context.getSession(), dossierLink.getRoutingTaskId());
        }
        return false;
    }

    @Override
    public boolean isStepPourArbitrage(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        if (dossierLink != null) {
            return ReponsesServiceLocator.getReponsesArbitrageService().isStepPourArbitrage(dossierLink);
        }
        return false;
    }

    @Override
    public boolean isStepPourReattribution(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        if (dossierLink != null) {
            return VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(dossierLink.getRoutingTaskType());
        }
        return false;
    }

    @Override
    public boolean isStepPourReorientation(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
                .getSSCorbeilleActionService()
                .getCurrentDossierLink(context);
        if (dossierLink != null) {
            return VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION.equals(dossierLink.getRoutingTaskType());
        }
        return false;
    }


    @Override
    public boolean isStepPourInformation(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        return ofNullable(dossierLink)
            .map(d -> ROUTING_TASK_TYPE_INFORMATION.equals(d.getRoutingTaskType()))
            .orElse(false);
    }

    @Override
    public boolean isRootStep(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        if (dossierLink != null) {
            return ReponsesServiceLocator
                .getFeuilleRouteService()
                .isRootStep(context.getSession(), dossierLink.getRoutingTaskId());
        }
        return false;
    }

    /**
     * Retourne les étapes de feuille de route courantes
     *
     * @return
     */
    @Override
    public List<DocumentModel> getCurrentSteps(DocumentModel dossierDoc, CoreSession session) {
        if (dossierDoc == null) {
            return new ArrayList<>();
        }
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        return ReponsesServiceLocator.getFeuilleRouteService().getRunningSteps(session, dossier.getLastDocumentRoute());
    }

    /**
     * Retourne la premiere étape en cours
     *
     * @return
     */
    @Override
    public SSRouteStep getCurrentStep(DocumentModel dossierDoc, CoreSession session) {
        List<DocumentModel> currentSteps = getCurrentSteps(dossierDoc, session);

        if (currentSteps != null && !currentSteps.isEmpty()) {
            return currentSteps.get(0).getAdapter(SSRouteStep.class);
        } else {
            return null;
        }
    }

    @Override
    public boolean canUserSubstituerFeuilleRoute(SpecificContext context) {
        if (context.getSession().getPrincipal().isMemberOf(ReponsesBaseFunctionConstant.FDR_INSTANCE_SUBSTITUTOR)) {
            return super.canUserSubstituerFeuilleRoute(context);
        }
        return false;
    }
}
