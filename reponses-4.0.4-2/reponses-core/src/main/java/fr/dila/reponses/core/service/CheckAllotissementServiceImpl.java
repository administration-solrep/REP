package fr.dila.reponses.core.service;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.exception.CheckAllotissementException;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.CheckAllotissementService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

public class CheckAllotissementServiceImpl implements CheckAllotissementService {
    private static final STLogger LOGGER = STLogFactory.getLog(CheckAllotissementServiceImpl.class);
    private static final String LABEL_REPONSES_DOSSIER_CONNEXE_DROIT = "reponses.dossier.connexe.droit";

    private static final List<String> COMPATIBLE_QUESTION_STATES = ImmutableList.of(
        VocabularyConstants.ETAT_QUESTION_EN_COURS,
        VocabularyConstants.ETAT_QUESTION_RENOUVELEE,
        VocabularyConstants.ETAT_QUESTION_RAPPELE,
        VocabularyConstants.ETAT_QUESTION_SIGNALEE,
        VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE
    );

    @Override
    public void checkQuestionCanBeAllotie(Question question, CoreSession session) throws CheckAllotissementException {
        checkUserIsAdminOrHasUpdatableDl(question, session);

        checkQuestionHasValidatedTransmissionStep(question, session);

        checkQuestionIsInAllotissableState(question, session);

        checkQuestionIsQe(question);

        checkReponseIsSignee(question, session);
    }

    @Override
    public void checkQuestionEligibilityInLot(Question question, boolean update, CoreSession session)
        throws CheckAllotissementException {
        checkSessionHasEnoughRights(question, session);

        checkQuestionDejaAllotie(question, update, session);

        checkReponseIsSignee(question, session);
    }

    @Override
    public void checkListQuestionIsNotEmpty(Set<Question> listQuestions) throws CheckAllotissementException {
        if (listQuestions.isEmpty()) {
            throw new CheckAllotissementException(ResourceHelper.getString("reponses.allotissement.aucun.dossier"));
        }
    }

    @Override
    public List<String> checkLotCoherence(
        CoreSession session,
        Question questionDirectrice,
        Set<Question> listQuestion,
        boolean updateMode
    ) {
        // vérification des questions du lot
        List<String> warnings = checkLotQuestions(listQuestion, session, questionDirectrice);

        // vérification de la question directrice
        try {
            checkQuestionEligibilityInLot(questionDirectrice, updateMode, session);
        } catch (CheckAllotissementException e) {
            warnings.add(e.getMessage());
        }

        return warnings;
    }

    private List<String> checkLotQuestions(
        Set<Question> listQuestion,
        CoreSession session,
        Question questionDirectrice
    ) {
        DocumentModel dossierDoc = session.getDocument(questionDirectrice.getDossierRef());
        List<String> warnings = new ArrayList<>();
        for (Question question : listQuestion) {
            try {
                checkSessionHasEnoughRights(question, session);
                checkQuestionMinistereAttributaire(session, dossierDoc, question);
            } catch (CheckAllotissementException e) {
                warnings.add(e.getMessage());
            }
        }

        return warnings;
    }

    private void checkQuestionMinistereAttributaire(CoreSession session, DocumentModel dossierDoc, Question question)
        throws CheckAllotissementException {
        DocumentModel dossierDoc2 = session.getDocument(question.getDossierRef());
        if (!haveSameMinistereAttributaire(dossierDoc, dossierDoc2)) {
            throw new CheckAllotissementException(
                ResourceHelper.getString("reponses.dossier.connexe.ministere.different")
            );
        }
    }

    private void checkSessionHasEnoughRights(Question question, CoreSession session)
        throws CheckAllotissementException {
        if (!session.hasPermission(question.getDocument().getRef(), SecurityConstants.READ)) {
            throw new CheckAllotissementException(
                ResourceHelper.getString(LABEL_REPONSES_DOSSIER_CONNEXE_DROIT, question.getSourceNumeroQuestion())
            );
        }
    }

    private boolean haveSameMinistereAttributaire(DocumentModel dossierDoc1, DocumentModel dossierDoc2) {
        Dossier dossier1 = dossierDoc1.getAdapter(Dossier.class);
        Dossier dossier2 = dossierDoc2.getAdapter(Dossier.class);
        return Objects.equal(
            dossier1.getIdMinistereAttributaireCourant(),
            dossier2.getIdMinistereAttributaireCourant()
        );
    }

    private void checkQuestionIsQe(Question selectedQuestion) throws CheckAllotissementException {
        if (BooleanUtils.isFalse(selectedQuestion.isQuestionTypeEcrite())) {
            throw new CheckAllotissementException(
                ResourceHelper.getString("reponses.allotissement.liste.error.raison.qe")
            );
        }
    }

    private void checkQuestionIsInAllotissableState(Question selectedQuestion, CoreSession session)
        throws CheckAllotissementException {
        QuestionStateChange etatCourantQuestion = selectedQuestion.getEtatQuestion(session);
        LOGGER.debug(
            session,
            ReponsesLogEnumImpl.GET_ATTR_QUEST_FONC,
            "Vérification état question : " + etatCourantQuestion.getNewState()
        );
        // Vérifie les états courants
        if (!COMPATIBLE_QUESTION_STATES.contains(etatCourantQuestion.getNewState())) {
            throw new CheckAllotissementException(ResourceHelper.getString("reponses.allotissement.liste.error.etat"));
        }
    }

    private void checkQuestionHasValidatedTransmissionStep(Question selectedQuestion, CoreSession session)
        throws CheckAllotissementException {
        Dossier dossier = selectedQuestion.getDossier(session);

        final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
        final SSFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(SSFeuilleRoute.class);
        final List<RouteTableElement> listRouteTableElement = ReponsesServiceLocator
            .getDocumentRoutingService()
            .getFeuilleRouteElements(feuilleRoute, session);
        SSRouteStep lastValidatedTransmissionAssemblee = null;

        // Recherche de l'étape Validation premier ministre
        for (final RouteTableElement routeTableElement : listRouteTableElement) {
            LOGGER.debug(session, SSLogEnumImpl.GET_STEP_TEC, "Parcours de la feuile de route");
            if (!routeTableElement.getDocument().isFolder()) {
                final SSRouteStep step = routeTableElement.getElement().getDocument().getAdapter(SSRouteStep.class);
                if (
                    step.isDone() &&
                    !SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_DEFAVORABLE_VALUE.equals(
                        step.getValidationStatus()
                    ) &&
                    VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(step.getType())
                ) {
                    LOGGER.debug(session, SSLogEnumImpl.GET_STEP_TEC, "Etape de transmission aux assemblées trouvée");
                    lastValidatedTransmissionAssemblee = step;
                }
            }
        }
        // Si on a atteint l'étape pour signature et
        if (lastValidatedTransmissionAssemblee != null) {
            throw new CheckAllotissementException("reponses.allotissement.liste.error.raison.transmise");
        }
    }

    private void checkUserIsAdminOrHasUpdatableDl(Question selectedQuestion, CoreSession session)
        throws CheckAllotissementException {
        LOGGER.debug(session, ReponsesLogEnumImpl.GET_ATTR_QUEST_FONC, "Vérification des postes de l'utilisateur");
        if (!isUserInPosteChargeMissionSGGOrBdc((SSPrincipal) session.getPrincipal())) {
            LOGGER.debug(
                session,
                ReponsesLogEnumImpl.GET_ATTR_QUEST_FONC,
                "Poste différent de chargé de mission ou BDC on vérifie les dossiers links"
            );
            final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
            List<DocumentModel> dossierLinkList = corbeilleService.findUpdatableDossierLinkForDossier(
                session,
                session.getDocument(selectedQuestion.getDossierRef())
            );
            if (CollectionUtils.isEmpty(dossierLinkList)) {
                LOGGER.debug(session, ReponsesLogEnumImpl.GET_ATTR_QUEST_FONC, "Pas de dossiers links");
                throw new CheckAllotissementException(
                    ResourceHelper.getString("reponses.allotissement.liste.error.raison.corbeille")
                );
            }
        }
    }

    private void checkReponseIsSignee(Question question, CoreSession session) throws CheckAllotissementException {
        Dossier dossier = question.getDossier(session);

        Reponse reponse = dossier.getReponse(session);

        if (reponse.isSignee()) {
            throw new CheckAllotissementException(
                ResourceHelper.getString("reponses.allotissement.liste.error.raison.cachet.serveur")
            );
        }
    }

    private boolean isUserInPosteChargeMissionSGGOrBdc(SSPrincipal ssPrincipal) {
        List<PosteNode> listNode = STServiceLocator.getSTPostesService().getPostesNodes(ssPrincipal.getPosteIdSet());

        return listNode.stream().anyMatch(poste -> poste.isChargeMissionSGG() || poste.isPosteBdc());
    }

    private void checkQuestionDejaAllotie(Question question, boolean update, CoreSession session)
        throws CheckAllotissementException {
        AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        if (!update && BooleanUtils.isTrue(allotissementService.isAllotit(question, session))) {
            throw new CheckAllotissementException(
                ResourceHelper.getString("reponses.dossier.connexe.deja.allotit", question.getSourceNumeroQuestion())
            );
        }
    }
}
