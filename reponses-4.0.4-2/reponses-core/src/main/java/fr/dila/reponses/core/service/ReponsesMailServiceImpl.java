package fr.dila.reponses.core.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STMailServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

public class ReponsesMailServiceImpl extends STMailServiceImpl implements ReponsesMailService {
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesMailServiceImpl.class);

    /**
     * Default constructor
     */
    public ReponsesMailServiceImpl() {
        super();
    }

    @Override
    public void sendMailUserPasswordCreation(final CoreSession session, final String userId, final String password) {
        final DocumentModel doc = STServiceLocator.getUserManager().getUserModel(userId);

        // Traite uniquement les documents de type User
        final String docType = doc.getType();
        if (!"user".equals(docType)) {
            return;
        }
        final STUser user = doc.getAdapter(STUser.class);

        final String email = user.getEmail();
        final String fname = user.getFirstName();
        final String lname = user.getLastName();

        final STParametreService stParamService = STServiceLocator.getSTParametreService();
        String subject;
        String message;
        if (user.isPermanent()) {
            subject =
                stParamService.getParametreValue(
                    session,
                    STParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_OBJET
                );
            message =
                stParamService.getParametreValue(
                    session,
                    STParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_TEXT
                ) +
                USER_SEND_MAIL_PASSWORD_TEMPLATE;
        } else {
            subject =
                stParamService.getParametreValue(
                    session,
                    ReponsesParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_TEMPORAIRE_OBJET
                );
            message =
                stParamService.getParametreValue(
                    session,
                    ReponsesParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_TEMPORAIRE_TEXT
                ) +
                USER_SEND_MAIL_PASSWORD_TEMPLATE;
        }

        message = String.format(message, userId, password);

        final Address[] emailAddress = new Address[1];
        try {
            LOGGER.info(
                session,
                STLogEnumImpl.SEND_MAIL_TEC,
                "Envoi d'un mél ayant pour objet : " + subject + " à l'adresse suivante : " + email
            );
            emailAddress[0] = new InternetAddress(email, fname + " " + lname);
            sendMail(message, subject, MAIL_SESSION, emailAddress);
        } catch (final Exception e) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                "Erreur lors de l'envoi d'un mél ayant pour objet : " + subject + " à l'adresse suivante : " + email,
                e
            );
            throw new NuxeoException("Erreur d'envoi du mail de création du mot de passe", e);
        }
    }

    @Override
    public void sendMailAfterSignatureError(final CoreSession session, final DocumentModel dossierDoc) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();

        String texte = paramService.getParametreValue(
            session,
            ReponsesParametreConstant.NOTIFICATION_MAIL_ECHEC_SIGNATURE_TEXTE
        );
        final String objet = paramService.getParametreValue(
            session,
            ReponsesParametreConstant.NOTIFICATION_MAIL_ECHEC_SIGNATURE_OBJET
        );

        // Récuperation des adresses e-mail des administrateurs fonctionnels
        final List<String> destinataires = getAdminFonctionnelMailReceiver();

        // Remplacement de la variable question
        final Question question = dossierDoc.getAdapter(Dossier.class).getQuestion(session);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("question", question.getSourceNumeroQuestion());
        texte = StringHelper.renderFreemarker(texte, paramMap);

        try {
            STServiceLocator.getSTMailService().sendTemplateMail(destinataires, objet, texte, paramMap);
        } catch (final Exception e) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                "Mail suite à l'échec de signature d'un dossier",
                e
            );
        }
    }

    @Override
    public void sendMailNoRouteFound(final CoreSession session, final Question question) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();

        String texte = paramService.getParametreValue(
            session,
            ReponsesParametreConstant.NOTIFICATION_MAIL_AUCUNE_ROUTE_TEXTE
        );
        final String objet = paramService.getParametreValue(
            session,
            ReponsesParametreConstant.NOTIFICATION_MAIL_AUCUNE_ROUTE_OBJET
        );

        // Récuperation des adresses e-mail des administrateurs fonctionnels
        final List<String> destinataires = getAdminFonctionnelMailReceiver();

        // Remplacement de la variable question
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("question", question.getSourceNumeroQuestion());
        texte = StringHelper.renderFreemarker(texte, paramMap);

        try {
            STServiceLocator.getSTMailService().sendTemplateMail(destinataires, objet, texte, paramMap);
        } catch (final Exception e) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                "Mail suite à l'échec de la recherche d'un modèle de feuille de route pour un dossier",
                e
            );
        }
    }

    /**
     * Récuperation des adresses e-mail des administrateurs fonctionnels
     *
     * @return une liste d'adresse mail
     */
    private List<String> getAdminFonctionnelMailReceiver() {
        final List<STUser> users = STServiceLocator
            .getProfileService()
            .getUsersFromBaseFunction(STBaseFunctionConstant.ADMIN_FONCTIONNEL_EMAIL_RECEIVER);

        // Récuperation des adresses e-mail des administrateurs fonctionnels
        final List<String> destinataires = new ArrayList<>();
        for (final STUser user : users) {
            final String mail = user.getEmail();
            if (StringUtils.isNotEmpty(mail)) {
                destinataires.add(mail);
            }
        }
        return destinataires;
    }

    @Override
    public void sendMailAfterStateChangedQuestion(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final String nouvelEtat
    ) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        final STPostesService posteService = STServiceLocator.getSTPostesService();
        final List<String> listIdDossiers = new ArrayList<>();
        final List<STUser> userList = new ArrayList<>();

        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        listIdDossiers.add(dossierDoc.getId());
        final DocumentModel fdrDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
        final List<DocumentModel> runningSteps = ReponsesServiceLocator
            .getFeuilleRouteService()
            .getRunningSteps(session, fdrDoc.getId());
        for (final DocumentModel runningStep : runningSteps) {
            // Récupération des utilisateurs du poste
            final String mailboxId = runningStep.getAdapter(SSRouteStep.class).getDistributionMailboxId();
            final String posteId = mailboxPosteService.getPosteIdFromMailboxId(mailboxId);
            try {
                final PosteNode posteNode = posteService.getPoste(posteId);
                userList.addAll(posteNode.getUserList());
            } catch (final Exception e) {
                LOGGER.error(session, STLogEnumImpl.FAIL_GET_POSTE_TEC, "Poste de l'étape en cours", e);
            }
            // Récupération des utilisateurs du BDC
            try {
                final String idMinistere = dossier.getIdMinistereAttributaireCourant();
                final PosteNode posteNodeBDC = posteService.getPosteBdcInEntite(idMinistere);
                if (posteNodeBDC == null) {
                    LOGGER.warn(
                        session,
                        STLogEnumImpl.FAIL_GET_POSTE_TEC,
                        "Inexistence du poste BDC de l'entite " + idMinistere
                    );
                } else {
                    userList.addAll(posteNodeBDC.getUserList());
                }
            } catch (final Exception e) {
                LOGGER.error(session, STLogEnumImpl.FAIL_GET_POSTE_TEC, "Poste BDC de l'étape en cours", e);
            }
        }
        // Récupération des utilisateurs SGG
        try {
            final PosteNode posteNodeSGG = posteService.getSGGPosteNode();
            userList.addAll(posteNodeSGG.getUserList());
        } catch (final Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_POSTE_TEC, "Poste SGG", e);
        }

        String texte = "";
        String objet = "";
        // Remplissage du message en fonction de l'état du dossier
        if (VocabularyConstants.ETAT_QUESTION_SIGNALEE.equals(nouvelEtat)) {
            texte = paramService.getParametreValue(session, ReponsesParametreConstant.TEXT_MAIL_DOSSIER_SIGNALEMENT);
            objet = paramService.getParametreValue(session, ReponsesParametreConstant.OBJET_MAIL_DOSSIER_SIGNALEMENT);
        } else if (VocabularyConstants.ETAT_QUESTION_RENOUVELEE.equals(nouvelEtat)) {
            texte = paramService.getParametreValue(session, ReponsesParametreConstant.TEXT_MAIL_DOSSIER_RENOUVELLEMENT);
            objet =
                paramService.getParametreValue(session, ReponsesParametreConstant.OBJET_MAIL_DOSSIER_RENOUVELLEMENT);
        } else if (VocabularyConstants.ETAT_QUESTION_RAPPELE.equals(nouvelEtat)) {
            texte = paramService.getParametreValue(session, ReponsesParametreConstant.TEXT_MAIL_DOSSIER_RAPPEL);
            objet = paramService.getParametreValue(session, ReponsesParametreConstant.OBJET_MAIL_DOSSIER_RAPPEL);
        }

        // Envoi du mail
        try {
            STServiceLocator
                .getSTMailService()
                .sendHtmlMailToUserListWithLinkToDossiers(session, userList, objet, texte, listIdDossiers);
        } catch (final NuxeoException e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, "Mail suite au changement d'état d'un dossier", e);
        }
    }

    @Override
    public void sendDailyRetiredMail(final CoreSession session) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        String objet = "";
        String texte = "";

        // Récupération de tous les retraits du jour
        final List<Question> questionListe = getLastDayRetiredQuestion(session);

        if (!questionListe.isEmpty()) {
            LOGGER.info(
                session,
                ReponsesLogEnumImpl.END_B_DAILYMAIL_RET_TEC,
                "Nombre de questions retirées :" + questionListe.size()
            );
            // Traitement des retraits de type CADUQUE
            try {
                final List<Question> questionCaduqueListe = getCaduqueListeQuestion(questionListe, session);
                if (!questionCaduqueListe.isEmpty()) {
                    objet =
                        paramService.getParametreValue(session, ReponsesParametreConstant.OBJET_DAILY_RETIRED_CADUQUE);
                    texte =
                        paramService.getParametreValue(session, ReponsesParametreConstant.TEXTE_DAILY_RETIRED_CADUQUE);
                    processForEachRetiredState(session, objet, texte, questionCaduqueListe);
                }
            } catch (final Exception e) {
                LOGGER.error(
                    session,
                    STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                    "Mail de notification des retraits de type CADUQUE",
                    e
                );
            }
            // Traitement des retraits de type RETIREE
            try {
                final List<Question> questionRetireeListe = getRetireeListeQuestion(questionListe, session);
                if (!questionRetireeListe.isEmpty()) {
                    objet =
                        paramService.getParametreValue(session, ReponsesParametreConstant.OBJET_DAILY_RETIRED_RETIREE);
                    texte =
                        paramService.getParametreValue(session, ReponsesParametreConstant.TEXTE_DAILY_RETIRED_RETIREE);
                    processForEachRetiredState(session, objet, texte, questionRetireeListe);
                }
            } catch (final Exception e) {
                LOGGER.error(
                    session,
                    STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                    "Mail de notification des retraits de type RETIREE",
                    e
                );
            }
            // Traitement des retraits de type CLOTURE_AUTRE
            try {
                final List<Question> questionClotureeAutreListe = getClotureeAutreListeQuestion(questionListe, session);
                if (!questionClotureeAutreListe.isEmpty()) {
                    objet =
                        paramService.getParametreValue(
                            session,
                            ReponsesParametreConstant.OBJET_DAILY_RETIRED_CLOTURE_AUTRE
                        );
                    texte =
                        paramService.getParametreValue(
                            session,
                            ReponsesParametreConstant.TEXTE_DAILY_RETIRED_CLOTURE_AUTRE
                        );
                    processForEachRetiredState(session, objet, texte, questionClotureeAutreListe);
                }
            } catch (final Exception e) {
                LOGGER.error(
                    session,
                    STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                    "Mail de notification des retraits de type CLOTURE_AUTRE",
                    e
                );
            }
        } else {
            LOGGER.info(session, ReponsesLogEnumImpl.END_B_DAILYMAIL_RET_TEC, "Aucune question n'a été retirée");
        }
    }

    /**
     * Renvoie une liste de question ayant fait l'objet d'un retrait sur le dernier jour
     *
     * @param session
     * @return la liste de questions correspondantes
     */
    private List<Question> getLastDayRetiredQuestion(final CoreSession session) {
        final Calendar dateExpiration = Calendar.getInstance();
        dateExpiration.add(Calendar.DAY_OF_MONTH, -1);
        final StringBuffer selectRetiredQuestion = new StringBuffer()
            .append("select q.ecm:uuid AS id from Question AS q WHERE q.qu:dateRetraitQuestion > ? ")
            .append(" OR q.qu:dateCaduciteQuestion > ? OR q.qu:dateClotureQuestion > ? ");
        final List<DocumentModel> questionRetirees = QueryHelper.doUFNXQLQueryAndFetchForDocuments(
            session,
            selectRetiredQuestion.toString(),
            new Object[] { dateExpiration, dateExpiration, dateExpiration },
            0,
            0,
            new PrefetchInfo(DossierConstants.QUESTION_DOCUMENT_SCHEMA)
        );

        // Récupération des questions retirées
        final List<Question> questionListe = new ArrayList<>();
        for (final DocumentModel questionRetiree : questionRetirees) {
            final Question question = questionRetiree.getAdapter(Question.class);
            questionListe.add(question);
        }
        return questionListe;
    }

    /**
     * Renvoie la liste des questions caduques d'une liste de question
     *
     * @param questionsRetirees
     * @param session
     * @return
     */
    private List<Question> getCaduqueListeQuestion(final List<Question> questionsRetirees, CoreSession session) {
        final List<Question> resultatListe = new ArrayList<>();
        for (final Question question : questionsRetirees) {
            final QuestionStateChange etatQuestion = question.getEtatQuestion(session);
            if (VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion.getNewState())) {
                resultatListe.add(question);
            }
        }
        return resultatListe;
    }

    /**
     * Renvoie la liste des questions cloturées_autre d'une liste de question
     *
     * @param questionsRetirees
     * @param session
     * @return
     */
    private List<Question> getClotureeAutreListeQuestion(final List<Question> questionsRetirees, CoreSession session) {
        final List<Question> resultatListe = new ArrayList<>();
        for (final Question question : questionsRetirees) {
            final QuestionStateChange etatQuestion = question.getEtatQuestion(session);
            if (VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion.getNewState())) {
                resultatListe.add(question);
            }
        }
        return resultatListe;
    }

    /**
     * Renvoie la liste des questions retirées d'une liste de question
     *
     * @param questionsRetirees
     * @param session
     * @return
     */
    private List<Question> getRetireeListeQuestion(final List<Question> questionsRetirees, CoreSession session) {
        final List<Question> resultatListe = new ArrayList<>();
        for (final Question question : questionsRetirees) {
            final QuestionStateChange etatQuestion = question.getEtatQuestion(session);
            if (VocabularyConstants.ETAT_QUESTION_RETIREE.equals(etatQuestion.getNewState())) {
                resultatListe.add(question);
            }
        }
        return resultatListe;
    }

    private void processForEachRetiredState(
        final CoreSession session,
        final String objetMail,
        final String texteMail,
        final List<Question> listeQuestion
    ) {
        final STMailService mailService = STServiceLocator.getSTMailService();
        final STPostesService posteService = STServiceLocator.getSTPostesService();
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        final Map<STUser, List<Question>> userToQuestion = new HashMap<>();
        final List<STUser> userList = new ArrayList<>();
        final String queryLastStep =
            " SELECT id as id FROM ROUTING_TASK where DOCUMENTROUTEID = ? AND dateFinEtape = ( SELECT MAX(dateFinEtape) FROM ROUTING_TASK where DOCUMENTROUTEID = ? ) ";
        IterableQueryResult res = null;
        String idLastStepBeforeRetired = null;

        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
        for (final Question question : listeQuestion) {
            final Dossier dossier = question.getDossier(session);

            // Récupération de la dernière étape avant le retrait
            try {
                res =
                    QueryUtils.doSqlQuery(
                        session,
                        new String[] { FlexibleQueryMaker.COL_ID },
                        queryLastStep,
                        new Object[] { dossier.getLastDocumentRoute(), dossier.getLastDocumentRoute() }
                    );
                final Iterator<Map<String, Serializable>> iterator = res.iterator();
                if (iterator.hasNext()) {
                    final Map<String, Serializable> row = iterator.next();
                    idLastStepBeforeRetired = (String) row.get(FlexibleQueryMaker.COL_ID);
                }
            } catch (final NuxeoException e) {
                LOGGER.error(
                    session,
                    SSLogEnumImpl.FAIL_GET_STEP_TEC,
                    "Impossible de récupérer la dernière étape pour une question retirée",
                    e
                );
            } finally {
                if (res != null) {
                    res.close();
                }
            }

            if (idLastStepBeforeRetired != null) {
                final DocumentModel lastStepBeforeRetired = session.getDocument(new IdRef(idLastStepBeforeRetired));
                // Récupération des utilisateurs du poste
                final String mailboxId = lastStepBeforeRetired.getAdapter(SSRouteStep.class).getDistributionMailboxId();
                final String posteId = mailboxPosteService.getPosteIdFromMailboxId(mailboxId);
                try {
                    final PosteNode posteNode = posteService.getPoste(posteId);
                    userList.addAll(posteNode.getUserList());
                } catch (final Exception e) {
                    LOGGER.error(session, STLogEnumImpl.FAIL_GET_POSTE_TEC, "Poste étape en cours", e);
                }
                // Récupération des utilisateurs du BDC
                for (final EntiteNode entiteNodeMinistere : ministeresService.getMinistereParentFromPoste(posteId)) {
                    try {
                        final PosteNode posteNodeBDC = posteService.getPosteBdcInEntite(entiteNodeMinistere.getId());
                        userList.addAll(posteNodeBDC.getUserList());
                    } catch (final Exception e) {
                        LOGGER.error(session, STLogEnumImpl.FAIL_GET_POSTE_TEC, "Poste BDC étape en cours", e);
                    }
                }
            }
            for (final STUser user : userList) {
                List<Question> listquestionPoste = userToQuestion.get(user);
                if (listquestionPoste == null) {
                    listquestionPoste = new ArrayList<>();
                }
                if (!listquestionPoste.contains(question)) {
                    listquestionPoste.add(question);
                    userToQuestion.put(user, listquestionPoste);
                }
            }
            userList.clear();
        }
        for (final Entry<STUser, List<Question>> entry : userToQuestion.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                final Map<String, Object> mailParams = new HashMap<>();
                // liste des question concernées
                final List<Map<String, String>> questionList = new ArrayList<>();
                for (final Question question : entry.getValue()) {
                    final Map<String, String> questionMap = new HashMap<>();
                    questionMap.put("question", question.getSourceNumeroQuestion());
                    questionList.add(questionMap);
                }
                mailParams.put("liste_question", questionList);
                final List<STUser> usersListToSendMail = new ArrayList<>();
                usersListToSendMail.add(entry.getKey());

                // Envoi du mail
                mailService.sendTemplateHtmlMailToUserList(
                    session,
                    usersListToSendMail,
                    objetMail,
                    texteMail,
                    mailParams
                );
            }
        }
    }

    @Override
    protected String getMailArchiveSuffix() {
        return "reponses_";
    }
}
