package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Allotissement.TypeAllotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.exception.AllotissementException;
import fr.dila.reponses.api.exception.CheckAllotissementException;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.CheckAllotissementService;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.AllotissementConfigDTO;
import fr.dila.reponses.ui.bean.AllotissementDTO;
import fr.dila.reponses.ui.bean.AllotissementListDTO;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.AllotissementActionService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.actions.DossierLockActionDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

public class AllotissementActionServiceImpl implements AllotissementActionService {
    private static final STLogger LOG = STLogFactory.getLog(AllotissementActionService.class);

    private static final String LABEL_REPONSES_DOSSIER_CONNEXE_DROIT = "reponses.dossier.connexe.droit";

    @Override
    public boolean existLot(DocumentModel doc) {
        Dossier dossier = doc.getAdapter(Dossier.class);
        return dossier.getDossierLot() != null && !dossier.getDossierLot().isEmpty();
    }

    @Override
    public boolean isDossierDirecteur(CoreSession session, DocumentModel doc, String sourceNumeroQuestion) {
        Dossier dossier = doc.getAdapter(Dossier.class);
        AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        Allotissement allotissement = allotissementService.getAllotissement(dossier.getDossierLot(), session);
        if (allotissement != null && !allotissement.getIdDossiers().isEmpty()) {
            List<Question> questionList = allotissementService.getQuestionAlloties(session, allotissement);
            if (!questionList.isEmpty()) {
                Question questionDirectrice = questionList.get(0);
                if (questionDirectrice.getSourceNumeroQuestion().equals(sourceNumeroQuestion)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public AllotissementListDTO getListQuestionsAllotis(SpecificContext context) {
        CoreSession session = context.getSession();
        Dossier dossier = context.getCurrentDocument().getAdapter(Dossier.class);
        setActionsInContext(context);
        AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        Allotissement allotissement = allotissementService.getAllotissement(dossier.getDossierLot(), session);
        List<AllotissementDTO> listQuestions = new ArrayList<>();

        AllotissementListDTO allotissementDTO = new AllotissementListDTO("");

        if (allotissement != null && !allotissement.getIdDossiers().isEmpty()) {
            List<Question> questionList = allotissementService.getQuestionAllotiesWithOrderOrigineNumero(
                session,
                allotissement
            );
            Question dossierDirecteur = allotissementService.getQuestionAlloties(session, allotissement).get(0);

            allotissementDTO.setNomDossierDirecteur(dossierDirecteur.getSourceNumeroQuestion());

            for (Question question : questionList) {
                AllotissementDTO dto = new AllotissementDTO(
                    question.getDossierRef().toString(),
                    question.getSourceNumeroQuestion(),
                    question.getNomCompletAuteur(),
                    question.getIntituleMinistereAttributaire(),
                    String.join(", ", question.getMotsClef()),
                    question.getEtatQuestion(session).getNewState()
                );
                dto.setDirecteur(dossierDirecteur.getId().equals(question.getId()));
                listQuestions.add(dto);
            }
        }
        allotissementDTO.setListQuestionsAlloties(listQuestions);
        return allotissementDTO;
    }

    /**
     * Permet de renseigner dans le context l'information pour appliquer le filtre
     *
     * @param context
     */
    private void setActionsInContext(SpecificContext context) {
        DossierLockActionDTO dossierLockAction;
        if (context.containsKeyInContextData(STContextDataKey.DOSSIER_LOCK_ACTIONS)) {
            dossierLockAction = context.getFromContextData(STContextDataKey.DOSSIER_LOCK_ACTIONS);
        } else {
            dossierLockAction = new DossierLockActionDTO();
            context.putInContextData(STContextDataKey.DOSSIER_LOCK_ACTIONS, dossierLockAction);
        }

        if (!dossierLockAction.getCanLockCurrentDossier()) {
            dossierLockAction.setCanLockCurrentDossier(
                STActionsServiceLocator.getDossierLockActionService().getCanLockCurrentDossier(context)
            );
        }
    }

    private List<Question> getListQuestionsFromDossierIds(List<String> lstIds, CoreSession session) {
        return session
            .getDocuments(lstIds, new PrefetchInfo(DossierConstants.DOSSIER_SCHEMA))
            .stream()
            .map(dossierDoc -> dossierDoc.getAdapter(Dossier.class))
            .map(selectedDossier -> selectedDossier.getQuestion(session))
            .collect(Collectors.toList());
    }

    @Override
    public void removeFromLot(SpecificContext context) {
        List<String> lstIds = context.getFromContextData(ReponsesContextDataKey.SELECTED_DOSSIERS);
        if (context.getCurrentDocument() != null) {
            CoreSession session = context.getSession();
            List<Question> listQuestions = getListQuestionsFromDossierIds(lstIds, session);

            if (CollectionUtils.isNotEmpty(listQuestions)) {
                try {
                    AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
                    Dossier dossier = context.getCurrentDocument().getAdapter(Dossier.class);
                    Allotissement allot = allotissementService.getAllotissement(dossier.getDossierLot(), session);
                    List<Question> questionList = allotissementService.getQuestionAllotiesWithOrderOrigineNumero(
                        session,
                        allot
                    );
                    allotissementService.updateLot(
                        questionList.get(0),
                        listQuestions,
                        session,
                        TypeAllotissement.SUPPR
                    );
                    context.getCurrentDocument().refresh();
                    context
                        .getMessageQueue()
                        .addToastSuccess(ResourceHelper.getString("allotissement.message.retirer"));
                } catch (AllotissementException e) {
                    LOG.error(session, ReponsesLogEnumImpl.DEL_ALLOT_TEC, context.getCurrentDocument(), e);
                    context.getMessageQueue().addWarnToQueue(e.getMessage());
                }
            }
        }
    }

    @Override
    public void addToLot(SpecificContext context) {
        String searchedQuestion = context.getFromContextData(ReponsesContextDataKey.SEARCHED_QUESTION);
        CoreSession session = context.getSession();
        if (StringUtils.isBlank(searchedQuestion)) {
            context
                .getMessageQueue()
                .addWarnToQueue(ResourceHelper.getString("reponses.allotissement.question.search.vide"));
        } else {
            RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
            Question question = null;
            try {
                question = rechercheService.searchQuestionBySourceNumero(session, searchedQuestion.trim());
            } catch (NuxeoException e) {
                LOG.warn(context.getSession(), ReponsesLogEnumImpl.FAIL_GET_QUESTION_TEC, searchedQuestion.trim(), e);
            }
            if (question == null) {
                context
                    .getMessageQueue()
                    .addWarnToQueue(
                        ResourceHelper.getString("reponses.allotissement.recherche.vide", searchedQuestion)
                    );
                return;
            }

            List<Question> listQuestionAjouter = Collections.singletonList(question);
            if (context.getCurrentDocument() != null) {
                AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
                Dossier dossier = context.getCurrentDocument().getAdapter(Dossier.class);
                Allotissement allot = allotissementService.getAllotissement(dossier.getDossierLot(), session);
                List<Question> questionList = allotissementService.getQuestionAlloties(session, allot);
                Question questionDirectrice = questionList.get(0);

                try {
                    ReponsesServiceLocator
                        .getCheckAllotissementService()
                        .checkQuestionCanBeAllotie(questionDirectrice, session);
                } catch (CheckAllotissementException e) {
                    String msg =
                        e.getMessage() +
                        " " +
                        questionDirectrice.getOrigineQuestion() +
                        " " +
                        questionDirectrice.getNumeroQuestion();
                    context.getMessageQueue().addWarnToQueue(msg);
                    return;
                }
                if (isLotCoherent(context, questionDirectrice, Collections.singleton(question), true)) {
                    try {
                        allotissementService.updateLot(
                            questionDirectrice,
                            listQuestionAjouter,
                            session,
                            TypeAllotissement.AJOUT
                        );
                        context
                            .getMessageQueue()
                            .addToastSuccess(ResourceHelper.getString("allotissement.message.ajouter"));
                    } catch (AllotissementException e) {
                        LOG.error(session, ReponsesLogEnumImpl.FAIL_CREATE_ALLOT_TEC, context.getCurrentDocument(), e);
                        context
                            .getMessageQueue()
                            .addErrorToQueue(
                                ResourceHelper.getString(
                                    "allotissement.error.addToLot",
                                    question.getSourceNumeroQuestion(),
                                    e.getMessage()
                                )
                            );
                    }
                }
            }
        }
    }

    @Override
    public AllotissementConfigDTO getListQuestionsAllotisSearch(SpecificContext context) {
        AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        CheckAllotissementService checkAllotissementService = ReponsesServiceLocator.getCheckAllotissementService();
        List<AllotissementDTO> listQuestionsErrorSearch = new ArrayList<>();
        List<AllotissementDTO> listQuestionsAvecLotSearch = new ArrayList<>();
        List<AllotissementDTO> listQuestionsSearch = new ArrayList<>();
        List<String> lstIds = context.getFromContextData(ReponsesContextDataKey.SELECTED_DOSSIERS);

        List<Question> questionsAllotiesSelected = getListQuestionsFromDossierIds(lstIds, context.getSession());

        CoreSession session = context.getSession();

        for (Question selectedQuestion : questionsAllotiesSelected) {
            QuestionStateChange etatCourantQuestion = selectedQuestion.getEtatQuestion(session);
            AllotissementDTO lot = new AllotissementDTO(
                selectedQuestion.getDossierRef().toString(),
                selectedQuestion.getSourceNumeroQuestion(),
                selectedQuestion.getNomCompletAuteur(),
                selectedQuestion.getIntituleMinistereAttributaire(),
                selectedQuestion.getMotsCles(),
                etatCourantQuestion.getNewState()
            );

            if (!session.hasPermission(selectedQuestion.getDocument().getRef(), SecurityConstants.READ)) {
                context
                    .getMessageQueue()
                    .addWarnToQueue(
                        ResourceHelper.getString(
                            LABEL_REPONSES_DOSSIER_CONNEXE_DROIT,
                            selectedQuestion.getSourceNumeroQuestion()
                        )
                    );
                continue;
            }

            try {
                checkAllotissementService.checkQuestionCanBeAllotie(selectedQuestion, session);

                if (BooleanUtils.isTrue(allotissementService.isAllotit(selectedQuestion, session))) {
                    listQuestionsAvecLotSearch.add(lot);
                } else {
                    listQuestionsSearch.add(lot);
                }
            } catch (CheckAllotissementException e) {
                lot.setErreurAllotissement(e.getMessage());
                listQuestionsErrorSearch.add(lot);
            }
        }

        AllotissementConfigDTO config = new AllotissementConfigDTO();
        config.setListQuestionsAvecLotSearch(listQuestionsAvecLotSearch);
        config.setListQuestionsErrorSearch(listQuestionsErrorSearch);
        config.setListQuestionsSearch(listQuestionsSearch);

        return config;
    }

    @Override
    public void createOrUpdateLot(SpecificContext context) {
        String questionDir = context.getFromContextData(ReponsesContextDataKey.QUESTION_DIR);
        CoreSession session = context.getSession();
        RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        // recherche de la question directrice
        Question questionDirectrice = null;
        try {
            questionDirectrice = rechercheService.searchQuestionBySourceNumero(session, questionDir.trim());
        } catch (NuxeoException e) {
            LOG.warn(context.getSession(), ReponsesLogEnumImpl.FAIL_GET_QUESTION_TEC, questionDir.trim(), e);
        }
        if (questionDirectrice == null) {
            context
                .getMessageQueue()
                .addWarnToQueue(ResourceHelper.getString("reponses.allotissement.recherche.vide", questionDir));
            return;
        }

        if (!session.hasPermission(questionDirectrice.getDocument().getRef(), SecurityConstants.READ)) {
            context
                .getMessageQueue()
                .addWarnToQueue(
                    ResourceHelper.getString(
                        LABEL_REPONSES_DOSSIER_CONNEXE_DROIT,
                        questionDirectrice.getSourceNumeroQuestion()
                    )
                );
            return;
        }
        CheckAllotissementService checkAllotissementService = ReponsesServiceLocator.getCheckAllotissementService();

        try {
            checkAllotissementService.checkQuestionCanBeAllotie(questionDirectrice, session);
        } catch (CheckAllotissementException e) {
            String msg =
                e.getMessage() +
                " " +
                questionDirectrice.getOrigineQuestion() +
                " " +
                questionDirectrice.getNumeroQuestion();
            context.getMessageQueue().addWarnToQueue(msg);
            return;
        }

        final String questionDirectriceID = questionDirectrice.getId();
        List<String> autresQuestions = context.getFromContextData(ReponsesContextDataKey.SELECTED_DOSSIERS);
        // ajout des questions selectionnés à la liste du lot
        Set<Question> listQuestion = autresQuestions
            .stream()
            .map(id -> session.getDocument(new IdRef(id)).getAdapter(Dossier.class).getQuestion(session))
            .filter(question -> !question.getId().equals(questionDirectriceID))
            .collect(Collectors.toSet());

        boolean updateMode = ReponsesServiceLocator.getAllotissementService().isAllotit(questionDirectrice, session);
        if (isLotCoherent(context, questionDirectrice, listQuestion, updateMode)) {
            // pas d'erreur, on crée le lot
            createOrUpdateLot(updateMode, questionDirectrice, new ArrayList<>(listQuestion), context);
        }
    }

    @Override
    public boolean isLotCoherent(
        SpecificContext context,
        Question questionDirectrice,
        Set<Question> listQuestion,
        boolean updateMode
    ) {
        SolonAlertManager sam = context.getMessageQueue();
        CoreSession session = context.getSession();

        CheckAllotissementService checkAllotissementService = ReponsesServiceLocator.getCheckAllotissementService();
        try {
            checkAllotissementService.checkListQuestionIsNotEmpty(listQuestion);
            for (Question question : listQuestion) {
                checkAllotissementService.checkQuestionCanBeAllotie(question, session);
            }
        } catch (CheckAllotissementException e) {
            sam.addWarnToQueue(e.getMessage());
            return false;
        }

        List<String> warnings = checkAllotissementService.checkLotCoherence(
            session,
            questionDirectrice,
            listQuestion,
            updateMode
        );
        warnings.forEach(sam::addWarnToQueue);

        return warnings.isEmpty();
    }

    private void createOrUpdateLot(
        boolean modeUpdate,
        Question questionDirectrice,
        List<Question> listQuestion,
        SpecificContext context
    ) {
        AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        try {
            if (modeUpdate) {
                allotissementService.updateLot(
                    questionDirectrice,
                    listQuestion,
                    context.getSession(),
                    TypeAllotissement.AJOUT
                );
            } else {
                allotissementService.createLot(questionDirectrice, new ArrayList<>(listQuestion), context.getSession());
            }
            context.getMessageQueue().addInfoToQueue("reponses.allotissement.ok");
        } catch (AllotissementException e) {
            LOG.error(context.getSession(), ReponsesLogEnumImpl.FAIL_CREATE_ALLOT_TEC, e);
            context.getMessageQueue().addErrorToQueue(e.getMessage());
        }
    }
}
