package fr.dila.reponses.ui.services.actions.impl;

import static fr.dila.st.core.util.ResourceHelper.getString;
import static fr.dila.st.ui.services.actions.STActionsServiceLocator.getDossierLockActionService;

import fr.dila.reponses.api.cases.Allotissement.TypeAllotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.exception.AllotissementException;
import fr.dila.reponses.api.exception.CheckAllotissementException;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.DossierConnexeDTO;
import fr.dila.reponses.ui.bean.DossierConnexeList;
import fr.dila.reponses.ui.bean.DossierConnexeMinistereDTO;
import fr.dila.reponses.ui.services.actions.DossierConnexeActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

public class DossierConnexeActionServiceImpl implements DossierConnexeActionService {
    private static final STLogger LOG = STLogFactory.getLog(DossierConnexeActionService.class);
    private static final String AUTRES_MINISTERES = "Autres ministères";

    @Override
    public List<DossierConnexeMinistereDTO> getListMinisteres(SpecificContext context) {
        List<DossierConnexeMinistereDTO> list = new ArrayList<>();
        DocumentModel currentDossier = context.getCurrentDocument();

        if (currentDossier != null) {
            CoreSession session = context.getSession();
            Dossier dossier = currentDossier.getAdapter(Dossier.class);
            Question question = dossier.getQuestion(session);
            QuestionConnexeService questionConnexeService = ReponsesServiceLocator.getQuestionConnexeService();
            Map<String, List<String>> map = questionConnexeService.getMinisteresMap(question, session);
            for (Entry<String, List<String>> entry : map.entrySet()) {
                DossierConnexeMinistereDTO dossierConn = new DossierConnexeMinistereDTO();

                dossierConn.setMinistereId(entry.getKey());

                if (StringUtils.isNotBlank(entry.getKey())) {
                    EntiteNode ministere = STServiceLocator.getSTMinisteresService().getEntiteNode(entry.getKey());
                    String ministereSuffixe;
                    if (
                        ministere.getDateFin() != null &&
                        DateUtil.dateToGregorianCalendar(ministere.getDateFin()).before(Calendar.getInstance())
                    ) {
                        ministereSuffixe = " (" + ministere.getParentGouvernement().getLabel() + ")";
                    } else {
                        ministereSuffixe = " (Gouvernement en cours)";
                    }
                    dossierConn.setMinistereLabel(ministere.getLabel() + ministereSuffixe);
                } else {
                    dossierConn.setMinistereLabel(AUTRES_MINISTERES);
                }

                // Pas besoin de rajouter la stat si on n'a pas de dossier pour
                // le minitère attributaire
                if (CollectionUtils.isNotEmpty(entry.getValue())) {
                    dossierConn.setNbDossiers(entry.getValue().size());
                    list.add(dossierConn);
                }
            }
        }
        return list;
    }

    private boolean isUserAdminOrSGG(NuxeoPrincipal ssPrincipal) {
        List<PosteNode> listNode = STServiceLocator
            .getSTPostesService()
            .getPostesNodes(((SSPrincipal) ssPrincipal).getPosteIdSet());

        return listNode.stream().anyMatch(poste -> poste.isChargeMissionSGG() || poste.isPosteBdc());
    }

    @Override
    public DossierConnexeList getListDossierConnexe(SpecificContext context) {
        CoreSession session = context.getSession();
        Question question = context.getCurrentDocument().getAdapter(Dossier.class).getQuestion(session);
        String ministereID = context.getFromContextData(SSContextDataKey.MINISTERE_ID);

        boolean admin = isUserAdminOrSGG(session.getPrincipal());
        List<String> idsQuestion = ReponsesServiceLocator
            .getQuestionConnexeService()
            .getMinisteresDossiersConnexe(question, ministereID, session);

        DossierConnexeList list = new DossierConnexeList();

        list.setDossiers(
            idsQuestion
                .stream()
                .map(id -> session.getDocument(new IdRef(id)).getAdapter(Question.class))
                .map(quest -> mapDossierConnexeFromQuestion(quest, session, context, admin))
                .collect(Collectors.toList())
        );
        list.setIsSameMinistere(
            list.getDossiers().stream().allMatch(dossier -> dossier.getMinAttributaire().equals(ministereID))
        );

        list.setTitre(
            STServiceLocator
                .getOrganigrammeService()
                .getOrganigrammeNodeById(ministereID, OrganigrammeType.MINISTERE)
                .getLabel()
        );

        return list;
    }

    private DossierConnexeDTO mapDossierConnexeFromDossier(
        Dossier dossierToMap,
        CoreSession session,
        SpecificContext context,
        boolean isAdmin
    ) {
        Question questionToMap = dossierToMap.getQuestion(session);
        Reponse reponseToMap = dossierToMap.getReponse(session);
        DossierConnexeDTO dossier = mapDossierConnexe(dossierToMap, questionToMap, reponseToMap, session, isAdmin);

        if (dossier == null) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("dossier.error.question.notfound"));
        }

        return dossier;
    }

    private DossierConnexeDTO mapDossierConnexe(
        Dossier dossierToMap,
        Question questionToMap,
        Reponse reponseToMap,
        CoreSession session,
        boolean isAdmin
    ) {
        DossierConnexeDTO dossier = null;
        try {
            dossier =
                new DossierConnexeDTO(
                    dossierToMap.getDocument().getId(),
                    questionToMap.getSourceNumeroQuestion(),
                    questionToMap.getNomCompletAuteur(),
                    questionToMap.getIdMinistereAttributaire(),
                    questionToMap.getMotsCles(),
                    questionToMap.getEtatQuestionSimple()
                );
            dossier.setDossierTextQuestion(questionToMap.getTexteQuestion());
            dossier.setDossierTextReponse(reponseToMap.getTexteReponse());

            AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
            boolean disabledCheck = allotissementService.isAllotit(questionToMap, session);
            if (!disabledCheck && !isAdmin) {
                final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
                List<DocumentModel> dossierLinkList = corbeilleService.findUpdatableDossierLinkForDossier(
                    session,
                    dossierToMap.getDocument()
                );
                disabledCheck = CollectionUtils.isEmpty(dossierLinkList);
            }

            dossier.setDisabled(disabledCheck);
        } catch (Exception e) {
            LOG.error(session, ReponsesLogEnumImpl.FAIL_GET_QUESTION_TEC, e);
        }

        return dossier;
    }

    private DossierConnexeDTO mapDossierConnexeFromQuestion(
        Question questionToMap,
        CoreSession session,
        SpecificContext context,
        boolean isAdmin
    ) {
        Reponse reponseToMap = ReponsesServiceLocator.getQuestionConnexeService().getReponse(questionToMap, session);

        Dossier dossierToMap = session.getDocument(questionToMap.getDossierRef()).getAdapter(Dossier.class);
        DossierConnexeDTO dossier = mapDossierConnexe(dossierToMap, questionToMap, reponseToMap, session, isAdmin);
        if (dossier == null) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("dossier.error.question.notfound"));
        }

        return dossier;
    }

    @Override
    public DossierConnexeDTO getQuestion(SpecificContext context) {
        String dossierId = context.getFromContextData(STContextDataKey.DOSSIER_ID);
        if (StringUtils.isBlank(dossierId)) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("dossier.error.notfound"));
            return null;
        }

        boolean isAdmin = isUserAdminOrSGG(context.getSession().getPrincipal());
        Dossier dossier = context.getSession().getDocument(new IdRef(dossierId)).getAdapter(Dossier.class);

        // Pas besoin de connaitre le ministère attributaire ici
        return mapDossierConnexeFromDossier(dossier, context.getSession(), context, isAdmin);
    }

    @Override
    public void setReponseQuestion(SpecificContext context) {
        DocumentModel currentDoc = context.getCurrentDocument();
        IdRef idDocToCopy = new IdRef(context.getFromContextData(STContextDataKey.ID));
        CoreSession session = context.getSession();

        boolean lockable = getDossierLockActionService().getCanLockCurrentDossier(context);
        if (!lockable) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("dossier.error.notlockable"));
            return;
        } else if (!session.exists(idDocToCopy)) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("dossier.error.response.copy.notfound"));
            return;
        }

        getDossierLockActionService().lockCurrentDossier(context);

        Reponse reponseToCopy = session.getDocument(idDocToCopy).getAdapter(Dossier.class).getReponse(session);

        Dossier dossier = currentDoc.getAdapter(Dossier.class);

        Reponse reponse = dossier.getReponse(session);
        reponse.setTexteReponse(reponseToCopy.getTexteReponse());
        final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
        reponseService.saveReponse(session, reponse.getDocument(), currentDoc);

        getDossierLockActionService().unlockCurrentDossier(context);
        context.getMessageQueue().addInfoToQueue(ResourceHelper.getString("dossier.info.response.copy"));
    }

    private void addError(SpecificContext context, String key, Object arguments) {
        context.getMessageQueue().addErrorToQueue(ResourceHelper.getString(key, arguments));
    }

    @Override
    public void createLot(SpecificContext context) {
        DocumentModel currentDoc = context.getCurrentDocument();
        if (currentDoc != null) {
            CoreSession session = context.getSession();
            Dossier currentDossier = currentDoc.getAdapter(Dossier.class);
            Question currentQuestion = currentDossier.getQuestion(session);

            try {
                ReponsesServiceLocator
                    .getCheckAllotissementService()
                    .checkQuestionCanBeAllotie(currentQuestion, session);
            } catch (CheckAllotissementException e) {
                String msg = String.join(
                    " ",
                    e.getMessage(),
                    currentQuestion.getOrigineQuestion(),
                    Long.toString(currentQuestion.getNumeroQuestion())
                );
                context.getMessageQueue().addWarnToQueue(msg);
                return;
            }

            List<Question> listQuestions = getListQuestionFromSelectedIds(context, currentQuestion, currentDossier);
            if (
                !listQuestions.isEmpty() &&
                !context.getMessageQueue().hasMessageInQueue() &&
                ReponsesActionsServiceLocator
                    .getAllotissementActionService()
                    .isLotCoherent(context, currentQuestion, new HashSet<>(listQuestions), false)
            ) {
                try {
                    ReponsesServiceLocator.getAllotissementService().createLot(currentQuestion, listQuestions, session);
                    context.getMessageQueue().addToastSuccess(getString("reponses.dossier.connexe.creer.lot.success"));
                } catch (AllotissementException e) {
                    context.getMessageQueue().addErrorToQueue(e.getMessage());
                }
            }
        }
    }

    private List<Question> getListQuestionFromSelectedIds(
        SpecificContext context,
        Question currentQuestion,
        Dossier currentDossier
    ) {
        List<Question> listQuestions = new ArrayList<>();
        List<String> lstSelectedDossier = context.getFromContextData(STContextDataKey.IDS);
        CoreSession session = context.getSession();
        if (CollectionUtils.isNotEmpty(lstSelectedDossier)) {
            for (String selectedDossierId : lstSelectedDossier) {
                Dossier selectedDossier = session.getDocument(new IdRef(selectedDossierId)).getAdapter(Dossier.class);
                Question selectedQuestion = selectedDossier.getQuestion(session);

                if (session.hasPermission(currentQuestion.getDocument().getRef(), SecurityConstants.READ)) {
                    if (ReponsesServiceLocator.getAllotissementService().isAllotit(selectedQuestion, session)) {
                        addError(
                            context,
                            "reponses.dossier.connexe.deja.allotit",
                            selectedQuestion.getSourceNumeroQuestion()
                        );
                    } else if (!compareDossiersMinisteres(currentDossier, selectedDossier)) {
                        addError(
                            context,
                            "reponses.dossier.connexe.ministere.different",
                            selectedQuestion.getSourceNumeroQuestion()
                        );
                    } else {
                        listQuestions.add(selectedQuestion);
                    }
                }
            }
        }
        return listQuestions;
    }

    @Override
    public void updateLot(SpecificContext context) {
        DocumentModel currentDoc = context.getCurrentDocument();
        if (currentDoc != null) {
            AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

            CoreSession session = context.getSession();
            Dossier currentDossier = currentDoc.getAdapter(Dossier.class);
            Question currentQuestion = currentDossier.getQuestion(session);
            List<Question> listQuestions = getListQuestionFromSelectedIds(context, currentQuestion, currentDossier);

            if (!listQuestions.isEmpty() && !context.getMessageQueue().hasMessageInQueue()) {
                try {
                    allotissementService.updateLot(currentQuestion, listQuestions, session, TypeAllotissement.AJOUT);
                    context
                        .getMessageQueue()
                        .addToastSuccess(getString("reponses.dossier.connexe.ajouter.lot.success"));
                } catch (AllotissementException e) {
                    context.getMessageQueue().addErrorToQueue(e.getMessage());
                }
            }
        }
    }

    private boolean compareDossiersMinisteres(Dossier dossier1, Dossier dossier2) {
        return dossier1.getIdMinistereAttributaireCourant().equals(dossier2.getIdMinistereAttributaireCourant());
    }
}
