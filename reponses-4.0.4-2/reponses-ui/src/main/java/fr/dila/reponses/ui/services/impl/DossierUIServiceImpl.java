package fr.dila.reponses.ui.services.impl;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.core.flux.DelaiCalculateur;
import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.bean.QuestionHeaderDTO;
import fr.dila.reponses.ui.bean.actions.DossierDistributionActionDTO;
import fr.dila.reponses.ui.bean.actions.ReponsesDossierActionDTO;
import fr.dila.reponses.ui.bean.actions.ReponsesRoutingActionDTO;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.DossierActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesDocumentRoutingActionService;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.actions.CorbeilleActionDTO;
import fr.dila.ss.ui.bean.actions.SSNavigationActionDTO;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSDossierUIService;
import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.actions.DossierLockActionDTO;
import fr.dila.st.ui.bean.actions.STLockActionDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class DossierUIServiceImpl implements SSDossierUIService<ConsultDossierDTO> {
    public static final String DOSSIER_ID_KEY = "dossierId";

    ReponsesDocumentRoutingActionService getReponsesDocumentRoutingActionService() {
        return ReponsesActionsServiceLocator.getReponsesDocumentRoutingActionService();
    }

    @Override
    public ConsultDossierDTO getDossierConsult(SpecificContext context) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        ConsultDossierDTO dto = MapDoc2Bean.docToBean(dossierDoc, ConsultDossierDTO.class);

        CoreSession session = context.getSession();
        Question question = dossier.getQuestion(session);
        dto.setSignale(BooleanUtils.isTrue(question.getEtatSignale()));
        dto.setRenouvele(BooleanUtils.isTrue(question.getEtatRenouvele()));
        dto.setUrgent(BooleanUtils.isTrue(question.getEtatRappele()));

        dto.setQuestionInfo(MapDoc2Bean.docToBean(question.getDocument(), QuestionHeaderDTO.class));

        dto.setIsSigned(dossier.getReponse(session).isSignee());
        context.putInContextData(ReponsesContextDataKey.DOSSIER, dto);

        DossierLockActionDTO lockAction = new DossierLockActionDTO();
        lockAction.setCanLockCurrentDossier(
            STActionsServiceLocator.getDossierLockActionService().getCanLockCurrentDossier(context)
        );
        context.putInContextData(STContextDataKey.DOSSIER_LOCK_ACTIONS, lockAction);

        STLockActionDTO stLockAction = new STLockActionDTO();
        SSPrincipal principal = (SSPrincipal) session.getPrincipal();
        STLockActionService lockActionService = STActionsServiceLocator.getSTLockActionService();
        stLockAction.setCurrentDocIsLockActionnableByCurrentUser(
            lockActionService.currentDocIsLockActionnableByCurrentUser(session, dossierDoc, principal)
        );
        context.putInContextData(STContextDataKey.LOCK_ACTIONS, stLockAction);

        //MAJ des info de verrou du dossier
        dto.setIsVerrouille(!lockAction.getCanLockCurrentDossier());
        dto.setLockTime(lockActionService.getLockTime(dossierDoc, session));
        dto.setLockOwner(lockActionService.getLockOwnerName(dossierDoc, session));

        dto.setIsDone(dossier.isDone());

        SSFeuilleRouteUIService feuilleRouteUIService = SSUIServiceLocator.getSSFeuilleRouteUIService();
        if (dto.getIsDone()) {
            dto.setDateLastStep(feuilleRouteUIService.getLastStepDate(context, dossier.getLastDocumentRoute()));
        } else {
            dto.setNextStepLabel(feuilleRouteUIService.getNextStepLabels(context, dossier.getLastDocumentRoute()));
            dto.setActualStepLabel(feuilleRouteUIService.getCurrentStepLabel(context));
        }

        DossierActionService dossierActionService = ReponsesActionsServiceLocator.getDossierActionService();
        ReponsesDossierActionDTO dossierActions = new ReponsesDossierActionDTO();
        dossierActions.setIsDossierContainsMinistere(
            dossierActionService.isDossierContainsMinistere(principal, dossierDoc)
        );
        dossierActions.setCanReadDossierConnexe(dossierActionService.canReadDossierConnexe(principal));
        dossierActions.setCanReadAllotissement(dossierActionService.canReadAllotissement(principal));

        dossierActions.setCanUpdateAllotissement(dossierActionService.canUpdateAllotissement(principal));
        dossierActions.setIsCurrentDossierInUserMinistere(
            dossierActionService.isCurrentDossierInUserMinistere(context)
        );
        dossierActions.setIsDossierArbitrated(dossierActionService.isDossierArbitrated(context));
        dossierActions.setIsFeuilleRouteRestartable(dossierActionService.isFeuilleRouteRestartable(context));
        dossierActions.setMessageActionRedemarrerQuestion(
            BooleanUtils.isTrue(dossier.getReponse(session).isPublished())
                ? "dossier.action.fdr.redemarrer.popup.message.erratum"
                : "dossier.action.fdr.redemarrer.popup.message"
        );

        dossierActions.setCanUserBriserReponse(
            ReponsesActionsServiceLocator.getReponseActionService().canUserBriserReponse(context)
        );

        context.putInContextData(ReponsesContextDataKey.DOSSIER_ACTIONS, dossierActions);

        DossierDistributionActionDTO dossierDistributionActions = new DossierDistributionActionDTO();
        dossierDistributionActions.setIsDossierLinkLoaded(
            ReponsesActionsServiceLocator.getReponsesDossierDistributionActionService().isDossierLinkLoaded(context)
        );
        context.putInContextData(ReponsesContextDataKey.DOSSIER_DISTRIBUTION_ACTIONS, dossierDistributionActions);

        ReponsesRoutingActionDTO routingActionsDto = new ReponsesRoutingActionDTO();
        ReponsesDocumentRoutingActionService reponsesDocumentRoutingActionService = ReponsesActionsServiceLocator.getReponsesDocumentRoutingActionService();
        routingActionsDto.setHasRelatedRoute(
            reponsesDocumentRoutingActionService.hasRelatedRoute(session, dossier.getDocument())
        );
        routingActionsDto.setIsFeuilleRouteVisible(
            reponsesDocumentRoutingActionService.isFeuilleRouteVisible(session, principal, dossier.getDocument())
        );
        routingActionsDto.setIsStepTransmissionAssemblees(
            reponsesDocumentRoutingActionService.isStepTransmissionAssemblees(context)
        );
        routingActionsDto.setIsStepInMinistere(reponsesDocumentRoutingActionService.isStepInMinistere(context));
        routingActionsDto.setIsNextStepReorientationOrReattributionOrArbitrage(
            reponsesDocumentRoutingActionService.isNextStepReorientationOrReattributionOrArbitrage(context)
        );
        routingActionsDto.setIsStepSignature(reponsesDocumentRoutingActionService.isStepSignature(context));
        routingActionsDto.setIsFirstStepInBranchOrParallel(
            reponsesDocumentRoutingActionService.isFirstStepInBranchOrParallel(context)
        );
        routingActionsDto.setIsStepPourArbitrage(reponsesDocumentRoutingActionService.isStepPourArbitrage(context));
        routingActionsDto.setIsStepPourReattribution(
            reponsesDocumentRoutingActionService.isStepPourReattribution(context)
        );
        routingActionsDto.setIsStepPourReorientation(reponsesDocumentRoutingActionService.isStepPourReorientation(context));

        routingActionsDto.setIsStepPourInformation(reponsesDocumentRoutingActionService.isStepPourInformation(context));
        routingActionsDto.setIsRootStep(reponsesDocumentRoutingActionService.isRootStep(context));
        context.putInContextData(ReponsesContextDataKey.ROUTING_ACTIONS, routingActionsDto);

        context.putInContextData(
            SSContextDataKey.IS_IN_PROGRESS_STEP,
            reponsesDocumentRoutingActionService.isInProgressStep(context)
        );

        CorbeilleActionDTO corbeilleActionsDTO = new CorbeilleActionDTO();
        corbeilleActionsDTO.setIsDossierLoadedInCorbeille(
            SSActionsServiceLocator.getSSCorbeilleActionService().isDossierLoadedInCorbeille(context)
        );
        context.putInContextData(SSContextDataKey.CORBEILLE_ACTIONS, corbeilleActionsDTO);

        SSNavigationActionDTO navigationActionDTO = new SSNavigationActionDTO();
        navigationActionDTO.setIsFromEspaceTravail(
            SSActionsServiceLocator.getNavigationActionService().isFromEspaceTravail(context)
        );
        context.putInContextData(SSContextDataKey.NAVIGATION_ACTIONS, navigationActionDTO);

        STParametreService paramService = STServiceLocator.getSTParametreService();
        Integer reponseDureeTraitement = Integer.parseInt(
            paramService.getParametreValue(session, STParametreConstant.QUESTION_DUREE_TRAITEMENT)
        );

        dto.setDelai(
            DelaiCalculateur.computeDelaiExpirationFdr(
                question,
                question.getEtatQuestionSimple(),
                reponseDureeTraitement
            )
        );

        return dto;
    }

    @Override
    public void loadDossierActions(SpecificContext context, ThTemplate template) {
        SSDossierUIService.super.loadDossierActions(context, template);

        Map<String, Object> map = template.getData();

        map.put(
            ReponsesTemplateConstants.RENVOI_ACTIONS,
            context.getActions(SSActionCategory.DOSSIER_TOPBAR_ACTIONS_RENVOI)
        );
        map.put(
            SSTemplateConstants.ROUTING_ACTIONS,
            context.getFromContextData(ReponsesContextDataKey.ROUTING_ACTIONS)
        );
    }
}
