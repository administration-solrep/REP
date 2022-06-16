package fr.dila.reponses.rest.management;

import static org.apache.commons.lang3.StringUtils.SPACE;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.core.notification.WsUtils;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.jeton.JetonServiceDto;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.xsd.reponses.ChercherRetourPublicationRequest;
import fr.sword.xsd.reponses.ChercherRetourPublicationResponse;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;
import fr.sword.xsd.reponses.ControleQuestion;
import fr.sword.xsd.reponses.ControleQuestionReponses;
import fr.sword.xsd.reponses.MinistrePublication;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.ReferencePublication;
import fr.sword.xsd.reponses.ResultatControlePublication;
import fr.sword.xsd.reponses.ResultatControlePublicationQR;
import fr.sword.xsd.reponses.RetourPublication;
import fr.sword.xsd.reponses.TraitementStatut;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Permet de gérer les webservices controlePublication et chercherRetourPublication
 *
 * @author sly
 * @author bgd
 */
public class ControleDelegate extends AbstractDelegate {
    /**
     * Logger.
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ControleDelegate.class);

    private static final String PUBLICATION_REPONSE = ResourceHelper.getString(
        "label.journal.comment.controlePublication.reponse"
    );
    private static final String PUBLICATION_QUESTION = ResourceHelper.getString(
        "label.journal.comment.controlePublication.question"
    );

    private static final String IMPOSSIBLE_RECUP_QUEST = "Impossible de récupérer la question ";

    public ControleDelegate(final CoreSession documentManager) {
        super(documentManager);
    }

    /**
     * Webservice controlePublication Lors de l'appel de controle publication, on crée des jetons de retour publication
     * à récupérer avec le webservice chercherRetourPublication.
     *
     * @param request
     * @return response
     */
    public ControlePublicationResponse controlePublication(ControlePublicationRequest request) {
        ControlePublicationResponse response = new ControlePublicationResponse();

        Map<String, Object> resultsAccess = checkWebserviceAccess();
        if (!resultsAccess.isEmpty()) {
            ResultatControlePublication rcq = new ResultatControlePublication();
            rcq.setStatut((TraitementStatut) resultsAccess.get(MAP_TRAITEMENT_STATUT));
            rcq.setMessageErreur((String) resultsAccess.get(MAP_MESSAGE_ERREUR));
            response.getResultatControleQuestion().add(rcq);
            return response;
        }

        final String webservice = STWebserviceConstant.CONTROLE_PUBLICATION;

        // /!\ hasRightAndOrigineUtilisateur renseigne questionId.source par effet de bord
        QuestionId questionId = new QuestionId();
        if (!hasRightAndOrigineUtilisateur(session, webservice, questionId)) {
            ResultatControlePublication rcq = new ResultatControlePublication();
            rcq.setStatut(TraitementStatut.KO);
            rcq.setMessageErreur(USER_NON_AUTORISE);
            response.getResultatControleQuestion().add(rcq);
            return response;
        }

        String source = questionId.getSource().toString();

        // Controle question
        request.getQuestion().forEach(controleQuestion -> controleQuestion(response, controleQuestion));

        // Controle question réponses
        request
            .getQuestionReponse()
            .forEach(controleQuestionReponses -> controleQuestionReponses(response, source, controleQuestionReponses));

        // Controle erratum question
        request
            .getErratumQuestion()
            .forEach(
                controleErratumQuestion ->
                    controleErratum(
                        controleErratumQuestion.getIdQuestion(),
                        response.getResultatControleErratumQuestion()
                    )
            );

        // Controle erratum réponse
        request
            .getErratumReponse()
            .forEach(
                controleErratumReponse ->
                    controleErratum(
                        controleErratumReponse.getIdQuestion(),
                        response.getResultatControleErratumReponse()
                    )
            );

        return response;
    }

    private void controleQuestion(ControlePublicationResponse response, ControleQuestion controleQuestion) {
        if (!isTransactionAlive()) {
            TransactionHelper.startTransaction();
        }

        ResultatControlePublication resultatControleQuestion = new ResultatControlePublication();
        resultatControleQuestion.setIdQuestion(controleQuestion.getIdQuestion());
        resultatControleQuestion.setStatut(TraitementStatut.OK);
        try {
            publierQuestion(controleQuestion.getIdQuestion(), controleQuestion.getReferencePublication());
        } catch (NuxeoException e) {
            resultatControleQuestion.setStatut(TraitementStatut.KO);
            resultatControleQuestion.setMessageErreur(e.getMessage());
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PUBLISH_QUESTION_TEC, e);
        }

        TransactionHelper.commitOrRollbackTransaction();
        response.getResultatControleQuestion().add(resultatControleQuestion);
    }

    private void controleQuestionReponses(
        ControlePublicationResponse response,
        String source,
        ControleQuestionReponses controleQuestionReponse
    ) {
        if (!isTransactionAlive()) {
            TransactionHelper.startTransaction();
        }

        ResultatControlePublicationQR resultatControlePublicationQR = new ResultatControlePublicationQR();
        resultatControlePublicationQR.setStatut(TraitementStatut.OK);

        try {
            controleQuestionReponse
                .getQuestion()
                .forEach(
                    controleQuestion -> {
                        publierReponse(
                            source,
                            controleQuestion.getIdQuestion(),
                            controleQuestionReponse.getReponses().getReferencePublication()
                        );
                        resultatControlePublicationQR.getIdQuestion().add(controleQuestion.getIdQuestion());
                        checkMinistereReponse(
                            controleQuestion.getIdQuestion(),
                            controleQuestionReponse.getReponses().getMinistreJo()
                        );
                    }
                );
        } catch (NuxeoException e) {
            resultatControlePublicationQR.setStatut(TraitementStatut.KO);
            resultatControlePublicationQR.setMessageErreur(e.getMessage());
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_VALIDATE_QUESTION_TEC, e);
        }

        TransactionHelper.commitOrRollbackTransaction();
        response.getResultatControleQuestionReponse().add(resultatControlePublicationQR);
    }

    private static void controleErratum(QuestionId question, List<ResultatControlePublication> reponse) {
        ResultatControlePublication resultatControleQuestion = new ResultatControlePublication();
        resultatControleQuestion.setIdQuestion(question);
        resultatControleQuestion.setStatut(TraitementStatut.OK);
        reponse.add(resultatControleQuestion);
    }

    private void publierQuestion(final QuestionId questionId, final ReferencePublication referencePublication) {
        final Dossier dossier = getDossierFromQuestionId(questionId);
        final Question question = dossier.getQuestion(session);

        if (BooleanUtils.isFalse(dossier.hasFeuilleRoute())) {
            String message = ResourceHelper.getString(
                "message.journal.publier.question.erreur",
                question.getSourceNumeroQuestion()
            );
            throw new ReponsesException(message);
        }
        String commentaire = "";
        String commentaireModification = "";

        GregorianCalendar referencePublicationDatePublication = referencePublication
            .getDatePublication()
            .toGregorianCalendar();
        if (
            question.getDatePublicationJO() == null ||
            !question.getDatePublicationJO().equals(referencePublicationDatePublication)
        ) {
            question.setDatePublicationJO(referencePublicationDatePublication);
            commentaireModification +=
                ResourceHelper.getString(
                    "label.journal.comment.controlePublication.datePublication",
                    SolonDateConverter.DATE_SLASH.format(referencePublicationDatePublication)
                ) +
                SPACE;
        }

        String pageJo = String.valueOf(referencePublication.getPageJo());
        if (!pageJo.equals(question.getPageJO())) {
            question.setPageJO(pageJo);
            commentaireModification +=
                ResourceHelper.getString("label.journal.comment.controlePublication.pageJO", pageJo) + SPACE;
        }
        if (StringUtils.isNotBlank(commentaireModification)) {
            commentaire =
                ResourceHelper.getString(
                    ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_COMMENT,
                    question.getSourceNumeroQuestion(),
                    commentaireModification,
                    PUBLICATION_QUESTION
                );
            // On journalise seulement en cas de question clotûrée
            if (question.getDateClotureQuestion() != null) {
                STServiceLocator
                    .getJournalService()
                    .journaliserActionBordereau(
                        session,
                        dossier.getDocument(),
                        ReponsesEventConstant.BORDEREAU_JO_UPDATE_EVENT,
                        commentaire
                    );
            }
        } else {
            String comment = ResourceHelper.getString("label.journal.comment.controlePublication.aucuneModification");
            commentaire =
                ResourceHelper.getString(
                    ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_COMMENT,
                    question.getSourceNumeroQuestion(),
                    comment,
                    PUBLICATION_QUESTION
                );
        }

        session.saveDocument(question.getDocument());
        session.save();

        // log du WS dans le journal d'administration
        logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_EVENT, commentaire);
    }

    private void publierReponse(
        final String source,
        final QuestionId questionId,
        final ReferencePublication referencePublication
    ) {
        final JetonService jetonService = ReponsesServiceLocator.getJetonService();

        final Dossier dossier = getDossierFromQuestionId(questionId);
        final Question question = dossier.getQuestion(session);
        final Reponse reponse = dossier.getReponse(session);

        if (question.getDateTransmissionAssemblees() == null) {
            String message = ResourceHelper.getString(
                "message.journal.publier.reponse.erreur",
                question.getSourceNumeroQuestion()
            );
            throw new ReponsesException(message);
        }

        String commentaireModification = "";
        String commentaire = "";

        if (reponse.getDateJOreponse() == null) {
            GregorianCalendar datePublication = referencePublication.getDatePublication().toGregorianCalendar();
            reponse.setDateJOreponse(datePublication);
            commentaireModification +=
                ResourceHelper.getString(
                    "label.journal.comment.controlePublication.datePublication",
                    SolonDateConverter.DATE_SLASH.format(datePublication)
                ) +
                SPACE;
        }
        if (reponse.getNumeroJOreponse() == null) {
            int numeroPublication = referencePublication.getNoPublication();
            reponse.setNumeroJOreponse(Long.valueOf(numeroPublication));
            commentaireModification +=
                ResourceHelper.getString(
                    "label.journal.comment.controlePublication.numeroPublication",
                    numeroPublication
                ) +
                SPACE;
        }
        if (reponse.getPageJOreponse() == null) {
            int pageJo = referencePublication.getPageJo();
            reponse.setPageJOreponse(Long.valueOf(pageJo));
            commentaireModification +=
                ResourceHelper.getString("label.journal.comment.controlePublication.pageJO", pageJo) + SPACE;
        }

        if (StringUtils.isNotBlank(commentaireModification)) {
            commentaire =
                ResourceHelper.getString(
                    ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_COMMENT,
                    question.getSourceNumeroQuestion(),
                    commentaireModification,
                    PUBLICATION_REPONSE
                );
            // log dans le journal du dossier
            STServiceLocator
                .getJournalService()
                .journaliserActionBordereau(
                    session,
                    dossier.getDocument(),
                    ReponsesEventConstant.BORDEREAU_JO_UPDATE_EVENT,
                    commentaire
                );
        } else {
            String comment = ResourceHelper.getString("label.journal.comment.controlePublication.aucuneModification");
            commentaire =
                ResourceHelper.getString(
                    ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_COMMENT,
                    question.getSourceNumeroQuestion(),
                    comment,
                    PUBLICATION_REPONSE
                );
        }

        jetonService.addDocumentInBasket(
            session,
            STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION,
            source,
            dossier.getDocument(),
            dossier.getNumeroQuestion().toString(),
            null,
            null
        );

        session.saveDocument(dossier.getDocument());
        session.saveDocument(question.getDocument());
        session.saveDocument(reponse.getDocument());
        session.save();

        // log dans le journal d'administration
        logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_EVENT, commentaire);
    }

    /**
     * Vérification et envoi de mail lorsque le ministère de la réponse ne correspondent pas
     *
     * @param questionId
     * @param ministre
     *            JO publication de la réponse
     *
     *
     */
    private void checkMinistereReponse(final QuestionId questionId, final MinistrePublication ministre) {
        final Dossier dossier = getDossierFromQuestionId(questionId);

        if (
            ministre == null ||
            ministre.getId() == null ||
            !ministre.getId().toString().equals(dossier.getIdMinistereAttributaireCourant())
        ) {
            sendMailToAdminTechnique(questionId);
        }
    }

    /**
     * Webservice chercherRetourPublication
     *
     * @param request
     * @return response
     */
    public ChercherRetourPublicationResponse chercherRetourPublication(final ChercherRetourPublicationRequest request) {
        ChercherRetourPublicationResponse response = new ChercherRetourPublicationResponse();

        Map<String, Object> resultsAccess = checkWebserviceAccess();
        if (!resultsAccess.isEmpty()) {
            response.setStatut((TraitementStatut) resultsAccess.get(MAP_TRAITEMENT_STATUT));
            response.setMessageErreur((String) resultsAccess.get(MAP_MESSAGE_ERREUR));
            return response;
        }

        response.setStatut(TraitementStatut.OK);
        response.setDernierRenvoi(true);
        response.setMessageErreur("");

        final String webservice = STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION;

        if (!hasRight(session, webservice)) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(USER_NON_AUTORISE);
            return response;
        }

        final String idMinistereUtilisateur = getFirstMinistereLoginFromSession(session);
        JetonServiceDto dto = null;
        try {
            dto =
                metaChercherDocument(
                    idMinistereUtilisateur,
                    request.getJetonRetourPublication(),
                    webservice,
                    request.getIdQuestion()
                );
        } catch (final NumberFormatException e) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(JETON_NON_RECONNU);
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC, e);
            return response;
        } catch (final IllegalArgumentException | NuxeoException e) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(e.getMessage());
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_SERVICE_TEC, e);
            return response;
        }

        if (dto == null) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(JETON_NON_ADEQUAT);
            return response;
        }

        if (dto.getNextJetonNumber() != null) {
            response.setJetonRetourPublication(dto.getNextJetonNumber().toString());
        }

        if (dto.isLastSending() != null) {
            response.setDernierRenvoi(dto.isLastSending());
        }

        final List<DocumentModel> docList = dto.getDocumentList();
        if (docList == null) {
            return response;
        }

        final List<RetourPublication> resultQuestions = new ArrayList<>();

        if (!docList.isEmpty()) {
            for (final DocumentModel documentModel : docList) {
                final fr.dila.reponses.api.cases.Question appQuestion = documentModel.getAdapter(
                    fr.dila.reponses.api.cases.Question.class
                );
                final QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);
                Dossier dossier = null;
                try {
                    dossier = getDossierFromQuestionId(qid);
                } catch (final NuxeoException e) {
                    response.setStatut(TraitementStatut.KO);
                    response.setMessageErreur(
                        response.getMessageErreur() +
                        IMPOSSIBLE_RECUP_QUEST +
                        qid.getNumeroQuestion() +
                        " : Dossier introuvable."
                    );
                    LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, "id : " + qid.getNumeroQuestion(), e);
                    continue;
                }

                if (!dossier.getIdMinistereAttributaireCourant().equals(idMinistereUtilisateur)) {
                    response.setStatut(TraitementStatut.KO);
                    response.setMessageErreur(
                        response.getMessageErreur() +
                        IMPOSSIBLE_RECUP_QUEST +
                        qid.getNumeroQuestion() +
                        " : l'utilisateur n'appartient pas au ministere courant de la question. \n"
                    );
                    continue;
                }
                // Convertir en retour Publication
                resultQuestions.add(getRetourPublicationFromQuestion(appQuestion));

                // log dans le journal technique
                logWebServiceActionDossier(
                    ReponsesEventConstant.WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_EVENT,
                    ReponsesEventConstant.WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_COMMENT,
                    dossier
                );
            }
        }
        response.getRetourPublication().addAll(resultQuestions);

        // log l'action dans le journal d'administration
        if (resultQuestions.isEmpty()) {
            logWebServiceAction(
                ReponsesEventConstant.WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_EVENT,
                ReponsesEventConstant.WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_COMMENT
            );
        }

        return response;
    }

    private RetourPublication getRetourPublicationFromQuestion(final Question question) {
        final RetourPublication retPubli = new RetourPublication();
        Dossier dossier = null;
        try {
            dossier = session.getDocument(question.getDossierRef()).getAdapter(Dossier.class);
        } catch (final NuxeoException e) {
            String message = ResourceHelper.getString(
                "label.journal.comment.question",
                question.getSourceNumeroQuestion()
            );
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, message);
            return null;
        }
        if (dossier == null) {
            return null;
        }
        final Reponse reponse = dossier.getReponse(session);

        final Calendar date = reponse.getDateJOreponse();
        final Long pageJo = reponse.getPageJOreponse();

        if (date == null || pageJo == null || pageJo.equals(0L)) {
            return null;
        }

        retPubli.setDatePublicationJo(DateUtil.calendarToXMLGregorianCalendar(date));
        retPubli.setIdQuestion(WsUtils.getQuestionIdFromQuestion(question));
        retPubli.setPageJo(pageJo.intValue());

        return retPubli;
    }

    /**
     * envoie du mail contrôle publication
     */
    private void sendMailToAdminTechnique(final QuestionId questionId) {
        final STParametreService parametreService = STServiceLocator.getSTParametreService();
        final STMailService mailService = STServiceLocator.getSTMailService();

        try {
            final String mailObjet = parametreService.getParametreValue(
                session,
                STParametreConstant.OBJET_ALERTE_CONTROLE_PUBLICATION
            );
            final String mailTexte = parametreService.getParametreValue(
                session,
                STParametreConstant.TEXTE_ALERTE_CONTROLE_PUBLICATION
            );

            final String adminMail = parametreService.getParametreValue(
                session,
                STParametreConstant.MAIL_ADMIN_TECHNIQUE
            );
            final Map<String, Object> params = new HashMap<>();
            params.put("numero_question", questionId.getSource().toString() + questionId.getNumeroQuestion());

            mailService.sendTemplateMail(adminMail, mailObjet, mailTexte, params);
        } catch (final Exception e) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                "Impossible d'envoyer le mail d'alerte de différence de données (ministère) aux administrateurs fonctionnels",
                e
            );
        }
    }
}
