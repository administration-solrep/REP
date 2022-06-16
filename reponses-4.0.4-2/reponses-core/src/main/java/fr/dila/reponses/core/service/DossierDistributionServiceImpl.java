package fr.dila.reponses.core.service;

import static fr.dila.st.api.constant.STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_XPATH;
import static fr.dila.st.core.query.QueryHelper.toPrefetchInfo;
import static fr.dila.st.core.service.STServiceLocator.getWorkManager;
import static java.util.stream.Collectors.toList;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.core.persister.CaseItemInCasePersister;
import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.event.CaseManagementEventConstants.EventNames;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.security.CaseManagementSecurityConstants;
import fr.dila.cm.service.CaseManagementPersister;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.exception.StepValidationException;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.mailbox.ReponsesMailbox;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.DossierSignatureService;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.cases.CreateDossierUnrestricted;
import fr.dila.reponses.core.work.SendNotificationDossierRestartWork;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.core.util.CommentUtils;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STAclConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.trash.TrashService;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;
import org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants;
import org.nuxeo.runtime.api.Framework;

/**
 * Service de distribution des dossiers de l'application Réponses.
 *
 * @author jtremeaux
 */
public class DossierDistributionServiceImpl
    extends fr.dila.ss.core.service.DossierDistributionServiceImpl
    implements DossierDistributionService {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -2190477126847702120L;

    private static final Log LOG = LogFactory.getLog(DossierDistributionServiceImpl.class);

    private static final String UNFXQL_REQUETE_QUESTION =
        "SELECT q." +
        STSchemaConstant.ECM_UUID_XPATH +
        " AS id FROM " +
        DossierConstants.QUESTION_DOCUMENT_TYPE +
        " AS q WHERE q." +
        DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_NUMERO_QUESTION +
        "= ? AND q." +
        DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_TYPE_QUESTION +
        "= ? AND q." +
        DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_ORIGINE_QUESTION +
        "= ? AND q." +
        DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_LEGISLATURE_QUESTION +
        "= ?";

    private CaseManagementPersister persister;

    public DossierDistributionServiceImpl() {
        super();
        this.persister = new CaseItemInCasePersister();
    }

    public void setPersister(final CaseManagementPersister persister) {
        this.persister = persister;
    }

    private String getParentDocumentPathForCase(final CoreSession session) {
        return persister.getParentDocumentPathForCase(session);
    }

    @Override
    public void initDossierDistributionAcl(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final List<String> mailboxIdList
    ) {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                final DocumentModel dossierDocToUpdate = dossierDoc;
                final ACP acp = dossierDocToUpdate.getACP();
                ACL mailboxACL = acp.getOrCreateACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
                final Set<ACE> aceSet = new HashSet<>(Arrays.asList(mailboxACL.getACEs()));
                aceSet.addAll(getDossierDistributionAce(mailboxIdList));
                acp.removeACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
                mailboxACL = acp.getOrCreateACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
                mailboxACL.addAll(aceSet);
                acp.addACL(mailboxACL);
                session.setACP(dossierDocToUpdate.getRef(), acp, true);
            }
        }
            .runUnrestricted();
    }

    /**
     * Construit la liste des ACE à ajouter lors de la distribution d'un dossier.
     *
     * @param mailboxIdList
     *            Liste des mailbox de distribution
     * @return Liste des ACE à ajouter
     *
     */
    private List<ACE> getDossierDistributionAce(final List<String> mailboxIdList) {
        final List<ACE> aceList = new ArrayList<>();
        for (final String mailboxId : mailboxIdList) {
            // Ajoute la mailbox poste destinataire de la distribution
            aceList.add(
                new ACE(CaseManagementSecurityConstants.MAILBOX_PREFIX + mailboxId, SecurityConstants.READ_WRITE, true)
            );
        }
        return aceList;
    }

    @Override
    public Dossier createDossier(final CoreSession session, final DocumentModel caseDoc) {
        final String parentPath = getParentDocumentPathForCase(session);
        final CreateDossierUnrestricted dossierCreator = new CreateDossierUnrestricted(session, caseDoc, parentPath);
        dossierCreator.runUnrestricted();
        final DocumentModel dossierDoc = dossierCreator.getDossier();
        return dossierDoc.getAdapter(Dossier.class);
    }

    @Override
    public Dossier initDossier(final CoreSession session, final Long numeroQuestion) {
        final DocumentModel caseDoc = session.createDocumentModel(DossierConstants.DOSSIER_DOCUMENT_TYPE);
        final String name = "Dossier " + numeroQuestion;
        DublincoreSchemaUtils.setTitle(caseDoc, name); // used to create the path of the case

        return createDossier(session, caseDoc);
    }

    @Override
    public Dossier createDossier(
        final CoreSession session,
        Dossier dossier,
        final Question question,
        final Reponse reponse,
        String etatQuestion
    ) {
        // Chargement des services
        final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
        final EventProducer eventProducer = STServiceLocator.getEventProducer();
        final JournalService journalService = STServiceLocator.getJournalService();
        final DocumentModel dossierDoc = dossier.getDocument();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Dossier path : " + dossierDoc.getPathAsString());
            LOG.debug("Dossier name : " + dossierDoc.getName());
            LOG.debug("Dossier id : " + dossierDoc.getId());
        }

        // definit le numero de question avant d'appeler setDossierProperties
        // qui l'utilise pour générer le titre
        dossier.setNumeroQuestion(question.getNumeroQuestion());

        // create dossier title and set name in path
        dossier = dossier.setDossierProperties(session);

        // Le ministère attributaire du dossier est normalement pris du webservice,
        // S'il est nul, on prend celui du ministère interrogé (cas des ajouts de dossiers depuis l'UI).
        if (dossier.getIdMinistereAttributaireCourant() == null) {
            dossier.setIdMinistereAttributaireCourant(question.getIdMinistereInterroge());
        }

        // Lève un événement de création du dossier
        final Map<String, Serializable> eventProperties = new HashMap<>();
        eventProperties.put(ReponsesEventConstant.DOSSIER_EVENT_PARAM, dossier);
        eventProperties.put(ReponsesEventConstant.QUESTION_EVENT_PARAM, question);
        final InlineEventContext inlineEventContext = new InlineEventContext(
            session,
            session.getPrincipal(),
            eventProperties
        );
        try {
            eventProducer.fireEvent(inlineEventContext.newEvent(ReponsesEventConstant.ABOUT_TO_CREATE_DOSSIER));
        } catch (final NuxeoException e2) {
            LOG.warn("Erreur de lancement de l'event" + ReponsesEventConstant.ABOUT_TO_CREATE_DOSSIER);
        }

        // Dossier
        dossier.save(session);
        if (LOG.isDebugEnabled()) {
            LOG.debug("dossier new path : " + dossierDoc.getPathAsString());
            LOG.debug("dossier new id : " + dossierDoc.getId());
        }

        // create question
        dossier.createQuestion(session, question, etatQuestion);

        // create empty reponse and update parapheur
        dossier.createReponse(session, question.getNumeroQuestion(), reponse);

        dossier.save(session);
        // create fond de dossier
        final FondDeDossier fondDeDossier = dossier.createFondDeDossier(session);

        // create fond de dossier initial repository
        fondDeDossierService.initFondDeDossier(session, fondDeDossier.getDocument());
        dossier.setFondDeDossierId(fondDeDossier.getDocument().getId());

        // Mise en place des flags pour les requêtes pré-paramétrées
        dossier.setEtapeRedactionAtteinte(false);
        dossier.setEtapeSignatureAtteinte(false);

        // save dossier elements in session
        dossier.save(session);

        // journalisation de l'action dans les logs
        try {
            journalService.journaliserActionBordereau(
                session,
                dossier.getDocument(),
                STEventConstant.EVENT_DOSSIER_CREATION,
                STEventConstant.COMMENT_DOSSIER_CREATION
            );
        } catch (final NuxeoException e) {
            throw new NuxeoException("erreur lors de l'enregistrement du log", e);
        }

        // Passe immédiatement le dossier à l'état "running"
        dossier.getDocument().followTransition(STDossier.DossierTransition.toRunning.name());

        return dossier;
    }

    @Override
    public SSFeuilleRoute startDefaultRoute(final CoreSession session, final Dossier dossier) {
        final DocumentModel dossierDoc = dossier.getDocument();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Démarrage de la feuille de route par défaut sur le document <" + dossierDoc.getRef() + ">");
        }

        // Récupère le modèle de feuille de route

        // Verification des propriétés de la question
        // pour savoir si il faut aller chercher une feuille de route
        Question question = dossier.getQuestion(session);
        if (question != null) {
            if (!question.isQuestionTypeEcrite()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Pas de feuille de route pour les question != QE");
                }
                return null;
            }

            final QuestionStateChange qscEtatQuestion = question.getEtatQuestion(session);
            if (qscEtatQuestion == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Pas de feuille de route pour les question sans etat");
                }
                return null;
            }

            final String etatQuestion = qscEtatQuestion.getNewState();
            if (
                VocabularyConstants.ETAT_QUESTION_REPONDU.equals(etatQuestion) ||
                VocabularyConstants.ETAT_QUESTION_RETIREE.equals(etatQuestion) ||
                VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion) ||
                VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion)
            ) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Pas de feuille de route pour les question repondu/retiree/caduque/cloture_autre");
                }
                return null;
            }
        }

        final FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();

        final FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
        if (route == null) {
            try {
                final ReponsesMailService reponsesMailService = ReponsesServiceLocator.getReponsesMailService();
                reponsesMailService.sendMailNoRouteFound(session, question);
            } catch (final Exception e) {
                LOG.warn("Echec d'envoi du mail de route non trouvée");
            }

            LOG.warn("Feuille de route par défaut non trouvée");
            return null;
        }

        // Crée une nouvelle instance de la feuille de route
        LOG.debug("Nouvelle instance de la route <" + route.getName() + ">");
        final List<String> docIds = new ArrayList<>();
        docIds.add(dossierDoc.getId());
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        // Crée la nouvelle instance de feuille de route (l'instance n'est pas encore démarrée)
        final DocumentModel newRouteInstanceDoc = documentRoutingService.createNewInstance(
            session,
            route.getDocument(),
            docIds
        );

        final ReponsesFeuilleRoute feuilleRouteInstance = newRouteInstanceDoc.getAdapter(ReponsesFeuilleRoute.class);
        feuilleRouteInstance.setTypeCreation(STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_INSTANCIATION);

        dossier.addHistoriqueAttribution(
            session,
            STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_INSTANCIATION,
            feuilleRouteInstance.getMinistere()
        );

        // Renseigne le ministère interpellé courant
        setNouveauMinistereCourant(session, dossier.getDocument(), feuilleRouteInstance.getMinistere());
        question = dossier.getQuestion(session);

        setMinistereRattachement(session, question, question.getIdMinistereAttributaire());
        setDirectionPilote(session, question, feuilleRouteInstance.getIdDirectionPilote());
        session.saveDocument(question.getDocument());
        // Renseigne la dernière feuille de route du dossier exécutée sur le dossier
        dossier.setLastDocumentRoute(newRouteInstanceDoc.getId());
        dossier.save(session);

        // calcul des échéances de feuille de route
        final ReponseFeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        feuilleRouteService.calculEcheanceFeuilleRoute(session, dossier);

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                feuilleRouteInstance.save(session);
                feuilleRouteInstance.run(session);
            }
        }
            .runUnrestricted();

        return feuilleRouteInstance;
    }

    @Override
    public void setDossierPathName(final DocumentModel dossierDoc) {
        final String path = dossierDoc.getPathAsString();
        if (LOG.isDebugEnabled()) {
            if (StringUtils.isEmpty(path)) {
                LOG.debug("doc Model dossier récupéré : path is empty");
            } else {
                LOG.debug("doc Model dossier récupéré path = :" + path);
            }
        }

        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        // on fixe le nom du dossier cree :
        final Long identifiantQuestion = dossier.getNumeroQuestion();

        if (identifiantQuestion != null) {
            final String title = "Dossier " + identifiantQuestion;

            // set Dossier name
            dossierDoc.setPathInfo(path, title);
        }
    }

    @Override
    public Signalement getLastSignalement(final DocumentModel questionDoc) {
        final Question question = questionDoc.getAdapter(Question.class);

        final List<Signalement> signalements = question.getSignalements();
        if (signalements != null && !signalements.isEmpty()) {
            return signalements.iterator().next();
        }
        return null;
    }

    @Override
    public Renouvellement getLastRenouvellement(final DocumentModel questionDoc) {
        final Question question = questionDoc.getAdapter(Question.class);

        final List<Renouvellement> renouvellements = question.getRenouvellements();
        if (renouvellements != null && !renouvellements.isEmpty()) {
            // we take the first element, which should be the most recent
            // signalement
            return renouvellements.iterator().next();
        }
        return null;
    }

    /**
     * Recherche des Dossier par numero de question
     *
     * @see fr.dila.reponses.api.service.DossierDistributionService#getDossierFrom(org.nuxeo.ecm.core.api.CoreSession,
     *      java.lang.String)
     */
    @Override
    public DocumentModelList getDossierFrom(final CoreSession session, final String numeroQuestion) {
        DocumentModelList listDossier = null;
        if (StringUtils.isBlank(numeroQuestion)) {
            throw new IllegalArgumentException("Le numero de la question ne peut pas etre nulle");
        }
        if (session == null) {
            throw new IllegalArgumentException("La session ne peut pas etre nulle");
        }
        try {
            /*
             * Sample SELECT * FROM Dossier WHERE question:numeroQuestion="36001"
             */
            final String query =
                "SELECT * FROM Dossier WHERE " +
                STSchemaConstant.DOSSIER_SCHEMA_PREFIX +
                ":numeroQuestion=\"" +
                numeroQuestion +
                "\"";
            return session.query(query);
        } catch (final Exception e) {
            listDossier = new DocumentModelListImpl();

            LOG.error("Le dossier numero : " + numeroQuestion + "n'a pas pu etre recupéré", e);
        }
        return listDossier;
    }

    @Override
    public void setDossierLinksFields(final CoreSession session, final DocumentModel dossierLinkDoc) {
        setDossierLinksFields(session, dossierLinkDoc, null);
    }

    @Override
    public void setDossierLinksFields(
        final CoreSession session,
        final DocumentModel dossierLinkDoc,
        String oldMinistereId
    ) {
        final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);

        // Récupère le dossier lié au DossierLink
        final DocumentModel dossierDoc = session.getDocument(new IdRef(dossierLink.getDossierId()));
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        // récupération de la question
        final Question question = dossier.getQuestion(session);

        // on crée le champ de tri du case link et le champs "sourceNumeroQuestion"
        setDossierLinksSortField(dossierLinkDoc, question);

        // on récupère une copie du champ date de publication
        Calendar datePublicationJO = Calendar.getInstance();
        if (question.getDatePublicationJO() != null) {
            datePublicationJO = (Calendar) question.getDatePublicationJO().clone();
        }
        dossierLink.setDatePublicationJO(datePublicationJO);

        // Renseigne le nom complet de l'auteur
        dossierLink.setNomCompletAuteur(question.getNomCompletAuteur());

        // Renseigne le type de question
        dossierLink.setTypeQuestion(question.getTypeQuestion());

        updateDenormalisation(dossierLink, question);

        // on recupere les metadonneées de l'etape courante si le dossier link est actionnable
        final ActionableCaseLink actionnableDossierLink = dossierLinkDoc.getAdapter(ActionableCaseLink.class);

        // Récupère l'étape en cours de la feuille de route à partir du CaseLink
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final DocumentModel routeStepDoc = documentRoutingService.getFeuilleRouteStep(session, actionnableDossierLink);
        final SSRouteStep routeStep = routeStepDoc.getAdapter(SSRouteStep.class);

        // Renseigne l'identifiant technique de l'étape en cours
        final String routingTaskId = routeStepDoc.getId();
        dossierLink.setRoutingTaskId(routingTaskId);

        // Renseigne le type d'étape en cours
        final String routingTaskType = routeStep.getType();
        dossierLink.setRoutingTaskType(routingTaskType);

        // Renseigne le nom de l'étape en cours
        final ReponsesVocabularyService vocabularyService = ReponsesServiceLocator.getVocabularyService();
        final String routingTaskLabel = vocabularyService.getEntryLabel(
            STVocabularyConstants.ROUTING_TASK_TYPE_VOCABULARY,
            routingTaskType
        );
        dossierLink.setRoutingTaskLabel(routingTaskLabel);

        // Renseigne le libellé de la maibox poste de distribution
        final MailboxService mailboxService = STServiceLocator.getMailboxService();
        final String mailboxId = routeStep.getDistributionMailboxId();
        final String routingTaskMailboxLabel = mailboxService.getMailboxTitle(session, mailboxId);
        dossierLink.setRoutingTaskMailboxLabel(routingTaskMailboxLabel);

        // Renseigne l'ID du ministère attributaire courant
        final String idMinistereAttributaire = dossier.getIdMinistereAttributaireCourant();
        dossierLink.setIdMinistereAttributaire(idMinistereAttributaire);

        // Renseigne l'intitulé du ministère attributaire courant
        OrganigrammeNode ministereNode = null;
        if (idMinistereAttributaire != null) {
            ministereNode = STServiceLocator.getSTMinisteresService().getEntiteNode(idMinistereAttributaire);
        }
        String intituleMinistere = "";
        if (ministereNode != null) {
            intituleMinistere = ministereNode.getLabel();
        }
        dossierLink.setIntituleMinistere(intituleMinistere);

        // on récupère la propriété "mail envoyé" de l'étape courante
        final Boolean etapeIsMailSend = (Boolean) routeStepDoc.getProperty(
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            SSFeuilleRouteConstant.ROUTING_TASK_IS_MAIL_SEND_PROPERTY
        );
        dossierLink.setCurrentStepIsMailSendProperty(etapeIsMailSend);

        // on met a jour les compteur des mailboc coninternalParticipantMap.get(k)cerné par ce case link
        correctCounterAfterChangeOnMinistereAttributaire(session, dossierLink, oldMinistereId);
    }

    @Override
    public void correctCounterAfterChangeOnMinistereAttributaire(
        final CoreSession session,
        final DossierLink dossierLink,
        final String ministerePrecId
    ) {
        final String routingTaskType = dossierLink.getRoutingTaskType();
        final String ministereId = dossierLink.getIdMinistereAttributaire();

        final Set<String> mailboxIdSet = new HashSet<>();
        final Map<String, List<String>> internalParticipantMap = dossierLink.getInitialInternalParticipants();
        for (final List<String> participants : internalParticipantMap.values()) {
            mailboxIdSet.addAll(participants);
        }

        final MailboxService mailboxService = STServiceLocator.getMailboxService();
        for (final String mailboxId : mailboxIdSet) {
            final Mailbox mailbox = mailboxService.getMailbox(session, mailboxId);
            final DocumentModel mailboxDoc = mailbox.getDocument();
            final ReponsesMailbox reponsesMailbox = mailboxDoc.getAdapter(ReponsesMailbox.class);

            if (ministerePrecId != null) {
                reponsesMailbox.decrPreComptage(ministerePrecId, routingTaskType);
            }
            reponsesMailbox.incrPreComptage(ministereId, routingTaskType);
            session.saveDocument(mailboxDoc);

            notifyMailboxModification(session, mailbox.getId());
        }
    }

    @Override
    public void updateDenormalisation(final DossierLink dossierLink, final Question question) {
        dossierLink.setEtatsQuestion(question.getEtatsQuestion());
        dossierLink.setMotsCles(question.getMotsCles());
        dossierLink.setDatePublicationJO(question.getDatePublicationJO());
        dossierLink.setEtatQuestionSimple(question.getEtatQuestionSimple());
        // Renseigne la date de Signalement de la question si elle est signalée
        if (Boolean.TRUE.equals(question.isSignale())) {
            Calendar dateSignalementQuestion = (Calendar) question.getDateSignalementQuestion().clone();
            dossierLink.setDateSignalementQuestion(dateSignalementQuestion);
        }
    }

    @Override
    public void setDossierLinksSortField(final DocumentModel documentModel, final Question question) {
        final Long numeroQuestionLong = question.getNumeroQuestion();
        final String originDossier = question.getOrigineQuestion();

        if (numeroQuestionLong != null) {
            int questioNumberLength;
            // we extract the question number from the dossier title
            final String numeroQuestion = numeroQuestionLong.toString();
            questioNumberLength = numeroQuestion.length();

            // to sort in numeric order instead of alphanumeric order we had the char '0' before the question number if
            // needed
            String leadingZero = "000000000000000000000";
            leadingZero =
                leadingZero.substring(0, ReponsesConstant.DOSSIER_QUESTION_NUMBER_MAX_LENGTH - questioNumberLength);

            // add leading zero and origin (AN or SE)
            final String sortField = originDossier + leadingZero + numeroQuestion;
            final String sourceNumeroQuestion = originDossier + " " + numeroQuestion;

            // set the unused dublicore field "description" as sort field
            documentModel.setProperty(
                DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
                DossierConstants.DOSSIER_REPONSES_LINK_NUMERO_QUESTION_PROPERTY,
                numeroQuestionLong
            );
            documentModel.setProperty(
                DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
                DossierConstants.DOSSIER_REPONSES_LINK_SORT_FIELD_PROPERTY,
                sortField
            );
            documentModel.setProperty(
                DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA,
                DossierConstants.DOSSIER_REPONSES_LINK_SOURCE_NUMERO_QUESTION_PROPERTY,
                sourceNumeroQuestion
            );
        }
    }

    @Override
    public DocumentModel setNouveauMinistereCourant(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final String idMinistere
    ) {
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        final Question question = dossier.getQuestion(session);
        String labelMinistereAttributaire;
        if (idMinistere == null) {
            labelMinistereAttributaire = "Min. inconnu";
        } else {
            labelMinistereAttributaire =
                STServiceLocator.getSTMinisteresService().getEntiteNode(idMinistere).getLabel();
        }

        dossier.setIdMinistereAttributairePrecedent(dossier.getIdMinistereAttributaireCourant());
        dossier.setIdMinistereAttributaireCourant(idMinistere);

        // Changement du ministere attributaire dans la question du dossier
        question.setIdMinistereAttributaire(idMinistere);
        question.setIntituleMinistereAttributaire(labelMinistereAttributaire);

        question.save(session);
        dossier.save(session);

        return dossier.getDocument();
    }

    @Override
    public DocumentModel reattribuerDossier(
        final CoreSession session,
        final Dossier dossier,
        final DossierLink dossierLink,
        final String idNewMinistere,
        String statusRunningStep,
        ReponsesRouteStep etapeCourante
    ) {
        final String intituleMinistere = STServiceLocator
            .getSTMinisteresService()
            .getEntiteNode(idNewMinistere)
            .getLabel();
        // Mise à jour des données du dossier reponses
        dossier.setIdMinistereAttributairePrecedent(dossier.getIdMinistereAttributaireCourant());
        dossier.setIdMinistereAttributaireCourant(idNewMinistere);
        dossier.setIdMinistereReattribution(null);

        // Mise à jour des données de l'étape
        dossierLink.setIdMinistereAttributaire(idNewMinistere);
        dossierLink.setIntituleMinistere(intituleMinistere);

        // Mise à jour des données de la question
        final Question question = dossier.getQuestion(session);
        question.setIdMinistereAttributaire(idNewMinistere);
        question.setIdMinistereRattachement(idNewMinistere);
        question.setIntituleMinistereAttributaire(intituleMinistere);
        question.setIntituleMinistereRattachement(intituleMinistere);
        try {
            // Sélectionne une nouvelle route
            LOG.debug("Retourne de la feuille de route pour le dossier " + dossier.getNumeroQuestion());
            // Récupère le modèle de feuille de route
            final FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
            final FeuilleRoute newRoute = feuilleRouteModelService.selectRouteForDossier(session, dossier);
            if (newRoute == null) {
                // Aucune feuille de route pour la réattribution, on nettoie les modifs faites sur le dossier et on lance l'exception
                dossier.getDocument().refresh();
                throw new NuxeoException("Feuille de route non trouvée");
            }
            // On a une feuille de route ici, la réattribution peut se dérouler donc on valide les modifications faites sur la question et le dossier
            if (etapeCourante != null) {
                setUserNameValidator(session, etapeCourante);
            }
            dossier.save(session);
            question.save(session);
            return reattribuerDossier(session, newRoute, dossier, statusRunningStep);
        } catch (final NuxeoException exc) {
            throw new ReponsesException(
                ResourceHelper.getString("feedback.reponses.dossier.error.noRouteFoundReattribution"),
                exc
            );
        }
    }

    /**
     * Renseigne le nom de l'agent qui a validé l'étape
     * @param session
     * @param etapeCourante
     */
    private void setUserNameValidator(CoreSession session, ReponsesRouteStep etapeCourante) {
        final NuxeoPrincipal principal = session.getPrincipal();
        String userId = principal.getOriginatingUser();
        if (userId == null) {
            userId = principal.getName();
        }
        final String userFullName = STServiceLocator.getSTUserService().getUserFullNameAndCivilite(userId);
        etapeCourante.setValidationUserLabel(userFullName);
        etapeCourante.setValidationUserId(userId);
        etapeCourante.save(session);
    }

    /**
     * Reattribue sur la nouvelle feuille de route passée en paramètre
     */
    public DocumentModel reattribuerDossier(
        final CoreSession session,
        final FeuilleRoute newRoute,
        final Dossier dossier,
        final String statusRunningStep
    ) {
        if (newRoute == null) {
            throw new NuxeoException("Feuille de route non trouvée");
        } else {
            final DocumentModel oldRouteDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
            return reattribuerFeuilleRoute(session, oldRouteDoc, newRoute.getDocument(), statusRunningStep);
        }
    }

    @Override
    public void startRouteAfterSubstitution(
        final CoreSession session,
        final DocumentModel oldFeuilleRouteDoc,
        final DocumentModel newFeuilleRouteDoc,
        final String typeCreation
    ) {
        // Renseigne le type de création de l'instance de feuille de route
        final ReponsesFeuilleRoute newFeuilleRoute = newFeuilleRouteDoc.getAdapter(ReponsesFeuilleRoute.class);
        newFeuilleRoute.setTypeCreation(typeCreation);
        final String fdrMinistere = newFeuilleRoute.getMinistere();

        final List<Dossier> dossierList = new ArrayList<>();
        for (final String dossierId : newFeuilleRoute.getAttachedDocuments()) {
            final DocumentModel dossierDoc = session.getDocument(new IdRef(dossierId));
            final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            dossierList.add(dossier);
        }

        // Démarre l'instance de feuille de route
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                // Renseigne la dernière feuille de route exécutée sur le dossier
                for (final Dossier dossier : dossierList) {
                    dossier.setLastDocumentRoute(newFeuilleRoute.getDocument().getId());

                    if (
                        STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_REATTRIBUTION.equals(typeCreation) ||
                        STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_REAFFECTATION.equals(typeCreation)
                    ) {
                        dossier.addHistoriqueAttribution(session, typeCreation, fdrMinistere);
                    }
                    // Renseigne le nouveau ministère dans le dossier et la question
                    setNouveauMinistereCourant(session, dossier.getDocument(), newFeuilleRoute.getMinistere());
                    final Question question = dossier.getQuestion(session);

                    setMinistereRattachement(session, question, question.getIdMinistereAttributaire());
                    setDirectionPilote(session, question, newFeuilleRoute.getIdDirectionPilote());
                    session.saveDocument(question.getDocument());

                    dossier.save(session);
                    session.save();
                }

                // Démarre l'instance de feuille de route
                newFeuilleRoute.save(session);
                newFeuilleRoute.run(session);
            }
        }
            .runUnrestricted();

        // Récupère le service de feuilles de route
        final ReponseFeuilleRouteService routingService = ReponsesServiceLocator.getFeuilleRouteService();

        // récupérer les dossiers de nouveau avec le CoreSession de base
        List<String> dossierIds = dossierList
            .stream()
            .map(Dossier::getDocument)
            .map(DocumentModel::getId)
            .collect(toList());
        // calcul des échéances de feuille de route
        session
            .getDocuments(dossierIds, toPrefetchInfo(DOSSIER_LAST_DOCUMENT_ROUTE_XPATH))
            .stream()
            .map(d -> d.getAdapter(Dossier.class))
            .forEach(dossier -> routingService.calculEcheanceFeuilleRoute(session, dossier));
    }

    @Override
    public void specificStepsOperation(final DossierLink dlink, final CoreSession session) throws Exception {
        final String routingTaskType = dlink.getRoutingTaskType();

        // On verifie que routingTask a bien les bonnes valeurs.
        if (!VocabularyConstants.ROUTING_TASK_TYPE_REDACTION_INTERFACEE.equals(routingTaskType)) {
            return;
        }

        // Création des paramètes utiles
        final String idDoc = dlink.getDossierId();
        final String loginMinistere = dlink.getIdMinistereAttributaire();

        if (StringUtils.isEmpty(idDoc) || StringUtils.isEmpty(loginMinistere)) {
            return;
        }
        final DocumentModel dossierModel = new UnrestrictedGetDocumentRunner(session, DossierConstants.DOSSIER_SCHEMA)
        .getById(idDoc);
        final Dossier dossier = dossierModel.getAdapter(Dossier.class);
        final String questionId = dossier.getQuestionId();

        // Création du jeton
        if (StringUtils.isNotEmpty(questionId)) {
            final JetonService jetonService = ReponsesServiceLocator.getJetonService();
            final DocumentModel question = session.getDocument(new IdRef(questionId));
            jetonService.addDocumentInBasket(
                session,
                STWebserviceConstant.CHERCHER_QUESTIONS,
                loginMinistere,
                question,
                dossier.getNumeroQuestion().toString(),
                null,
                null
            );
        }
    }

    @Override
    public void initDossierLinkAcl(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel dossierLinkDoc
    ) {
        final List<ACE> aceList = getDossierLinkDistributionAce(dossierDoc);

        final ACP acp = dossierLinkDoc.getACP();
        final ACL functionAcl = acp.getOrCreateACL(STAclConstant.ACL_SECURITY);
        for (final ACE ace : aceList) {
            functionAcl.add(ace);
        }
        acp.addACL(functionAcl);
        session.setACP(dossierLinkDoc.getRef(), acp, true);
        session.save();
    }

    /**
     * Construit la liste des ACE à ajouter lors de la distribution d'un dossier.
     *
     * @param dossierDoc
     *            Dossier
     * @return Liste des ACE à ajouter
     *
     */
    protected List<ACE> getDossierLinkDistributionAce(final DocumentModel dossierDoc) {
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        final String ministereAttributaireCourantId = dossier.getIdMinistereAttributaireCourant();

        final List<ACE> aceList = new ArrayList<>();

        // Ajoute les administrateurs fonctionnels
        aceList.add(
            new ACE(ReponsesBaseFunctionConstant.FDR_INSTANCE_ADMIN_UPDATER, SecurityConstants.READ_WRITE, true)
        );

        // Ajoute les administrateur du ministère interpellé courant
        final String group = STAclConstant.DOSSIER_LINK_UPDATER_MIN_ACE_PREFIX + ministereAttributaireCourantId;
        aceList.add(new ACE(group, SecurityConstants.READ_WRITE, true));

        return aceList;
    }

    @Override
    public DocumentModel reattribuerFeuilleRoute(
        final CoreSession session,
        final DocumentModel oldFeuilleRouteDoc,
        final DocumentModel newRouteModelDoc,
        String statusRunningStep
    ) {
        final SSFeuilleRoute oldFeuilleRoute = oldFeuilleRouteDoc.getAdapter(SSFeuilleRoute.class);

        // On annule l'ancienne instance
        final List<String> attachedDocumentIds = oldFeuilleRoute.getAttachedDocuments();

        // On récupère les étapes de l'ancienne fdr
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final List<DocumentModel> stepsOldFdr = documentRoutingService.getOrderedRouteElement(
            oldFeuilleRouteDoc.getId(),
            session
        );

        // Crée la nouvelle instance de feuille de route (l'instance n'est pas encore démarrée)
        final DocumentModel newFeuilleRouteDoc = documentRoutingService.createNewInstance(
            session,
            newRouteModelDoc,
            attachedDocumentIds
        );
        final SSFeuilleRoute newFeuilleRoute = newFeuilleRouteDoc.getAdapter(SSFeuilleRoute.class);
        final DocumentModel firstStepDoc = session.getChildren(newFeuilleRouteDoc.getRef()).get(0);
        documentRoutingService.lockDocumentRoute(newFeuilleRoute, session);

        // On copie les étapes de l'ancienne feuille de route à la nouvelle, en gardant que les étapes à l'état done et
        // running
        DocumentModel runningStepDoc = null;
        for (final DocumentModel etapeDoc : stepsOldFdr) {
            if (
                FeuilleRouteElement.ElementLifeCycleState.done.name().equals(etapeDoc.getCurrentLifeCycleState()) ||
                FeuilleRouteElement.ElementLifeCycleState.running.name().equals(etapeDoc.getCurrentLifeCycleState())
            ) {
                final DocumentModel newStepDoc = session.copy(
                    etapeDoc.getRef(),
                    newFeuilleRouteDoc.getRef(),
                    etapeDoc.getName()
                );

                // Les étapes copiées sont rattachées à la nouvelle FDR
                linkNewStepToFdr(session, newFeuilleRouteDoc, newStepDoc);

                session.orderBefore(newFeuilleRouteDoc.getRef(), newStepDoc.getName(), firstStepDoc.getName());

                moveCommentInNewStep(session, etapeDoc, newStepDoc);

                // La nouvelle étape courante est sauvée pour être validée
                if (
                    FeuilleRouteElement.ElementLifeCycleState.running.name().equals(etapeDoc.getCurrentLifeCycleState())
                ) {
                    runningStepDoc = newStepDoc;
                }
            }
        }

        String typeCreation = STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_REATTRIBUTION;

        // On valide l'étape courante
        if (runningStepDoc != null) {
            final ReponsesRouteStep runningStep = runningStepDoc.getAdapter(ReponsesRouteStep.class);

            if (runningStep.isReaffectation()) {
                typeCreation = STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_REAFFECTATION;
            }

            runningStep.setDone(session);
            setUserNameValidator(session, runningStep);
            runningStep.setAutomaticValidated(false);
            runningStep.setValidationStatus(statusRunningStep);
            runningStep.setDateFinEtape(Calendar.getInstance());
            runningStep.save(session);
        }

        oldFeuilleRoute.cancel(session);
        documentRoutingService.unlockDocumentRoute(newFeuilleRoute, session);

        // On lance l'évenement de réattribution qui démarre la fdr
        fireSubstitutionFeuilleDeRouteEvent(session, typeCreation, oldFeuilleRouteDoc, newFeuilleRouteDoc);
        return newFeuilleRouteDoc;
    }

    private void linkNewStepToFdr(CoreSession session, DocumentModel newFeuilleRouteDoc, DocumentModel elementDoc) {
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        if (elementDoc.isFolder()) {
            for (DocumentModel childDoc : documentRoutingService.getOrderedRouteElement(elementDoc.getId(), session)) {
                linkNewStepToFdr(session, newFeuilleRouteDoc, childDoc);
            }
        } else {
            ReponsesRouteStep repRouteStep = elementDoc.getAdapter(ReponsesRouteStep.class);
            repRouteStep.setDocumentRouteId(newFeuilleRouteDoc.getId());
            repRouteStep.save(session);
        }
    }

    @Override
    public void retirerFeuilleRoute(final CoreSession session, final Dossier dossier) {
        if (Boolean.FALSE.equals(dossier.hasFeuilleRoute())) {
            // pas de feuille de route, on ne fait rien
            return;
        }

        final String feuilleRouteId = dossier.getLastDocumentRoute();
        LOG.info("Feuille de route sur le dossier = " + feuilleRouteId);
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        final SSFeuilleRoute feuilleRoute = session
            .getDocument(new IdRef(feuilleRouteId))
            .getAdapter(SSFeuilleRoute.class);
        final DocumentModelList listStepsDoc = documentRoutingService.getOrderedRouteElement(feuilleRouteId, session);

        documentRoutingService.lockDocumentRoute(feuilleRoute, session);
        final List<DocumentModel> listCurrentStepsDoc = new ArrayList<>();
        // suppression des étapes futures
        for (final DocumentModel stepDoc : listStepsDoc) {
            if (FeuilleRouteElement.ElementLifeCycleState.ready.name().equals(stepDoc.getCurrentLifeCycleState())) {
                documentRoutingService.softDeleteStep(session, stepDoc);
            }

            if (FeuilleRouteElement.ElementLifeCycleState.running.name().equals(stepDoc.getCurrentLifeCycleState())) {
                listCurrentStepsDoc.add(stepDoc);
            }
        }

        session.saveDocument(feuilleRoute.getDocument());

        // Si l'étape courante est nulle, la feuille de route est terminée, on ne fait rien
        if (listCurrentStepsDoc.isEmpty()) {
            LOG.warn("Tentative de retrait d'une feuille de route déjà terminé, rien de fait");
        } else {
            // validation de l'étape courante
            for (final DocumentModel currentStepDoc : listCurrentStepsDoc) {
                updateStepValidationStatus(
                    session,
                    currentStepDoc,
                    SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_VALIDE_AUTOMATIQUEMENT_VALUE,
                    dossier.getDocument()
                );
            }

            new UnrestrictedSessionRunner(session) {

                @Override
                public void run() {
                    final List<DocumentModel> dossierLinkDocList = corbeilleService.findDossierLink(
                        session,
                        dossier.getDocument().getId()
                    );
                    for (final DocumentModel dossierLinkDoc : dossierLinkDocList) {
                        final ActionableCaseLink actionableCaseLink = dossierLinkDoc.getAdapter(
                            ActionableCaseLink.class
                        );
                        if (actionableCaseLink.isActionnable() && actionableCaseLink.isTodo()) {
                            actionableCaseLink.validate(session);
                        }
                    }
                }
            }
                .runUnrestricted();
        }
        documentRoutingService.unlockDocumentRoute(feuilleRoute, session);
    }

    @Override
    public void addCommentToStep(final CoreSession session, final DocumentModel etapeDoc, final String message) {
        // Ajout d'une note d'étape
        final CommentableDocument commentableDoc = etapeDoc.getAdapter(CommentableDocument.class);

        final DocumentModel myComment = CommentUtils.initComment(session, message);
        commentableDoc.addComment(myComment);
        session.saveDocument(etapeDoc);

        // automatically validate the comments
        if (CommentsConstants.COMMENT_LIFECYCLE.equals(myComment.getLifeCyclePolicy())) {
            new UnrestrictedSessionRunner(session) {

                @Override
                public void run() {
                    session.followTransition(myComment.getRef(), CommentsConstants.TRANSITION_TO_PUBLISHED_STATE);
                    session.save();
                }
            }
                .runUnrestricted();
        }
    }

    @Override
    public void addCommentAndStepForReaffectation(final CoreSession session, final DocumentModel dossierDoc) {
        final ReponseFeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();

        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        // Fonctionnellement il ne peut y avoir qu'une étape en cours à ce moment (l'étape Pour réattribution)
        final List<DocumentModel> runningSteps = feuilleRouteService.getRunningSteps(
            session,
            dossier.getLastDocumentRoute()
        );
        if (runningSteps == null || runningSteps.isEmpty()) {
            throw new NuxeoException("Pas d'etape en cours pour le dossier [" + dossier.getDocument().getId() + "]");
        } else if (runningSteps.size() > 1) {
            throw new NuxeoException(
                "Plus d'une etape en cours pour le dossier [" + dossier.getDocument().getId() + "]"
            );
        }
        final DocumentModel etapeDoc = runningSteps.get(0);

        // Tag sur l'étape de feuille de route
        final ReponsesRouteStep routeStep = etapeDoc.getAdapter(ReponsesRouteStep.class);
        routeStep.setReaffectation(true);
        session.saveDocument(etapeDoc);
        addCommentToStep(session, etapeDoc, "Réaffectation");
    }

    @Override
    public void deleteDossierLink(final CoreSession session, final DossierLink dossierLink) {
        final NuxeoPrincipal principal = session.getPrincipal();
        final EventProducer eventProducer = STServiceLocator.getEventProducer();
        final STDossierLink flink = dossierLink;

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                try {
                    final Map<String, Serializable> eventProperties = new HashMap<>();
                    eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_CASE_LINK, flink.getDocument());
                    eventProperties.put("category", CaseManagementEventConstants.DISTRIBUTION_CATEGORY);
                    final DocumentEventContext envContext = new DocumentEventContext(
                        session,
                        principal,
                        flink.getDocument()
                    );
                    envContext.setProperties(eventProperties);
                    eventProducer.fireEvent(envContext.newEvent(EventNames.beforeCaseLinkRemovedEvent.name()));

                    final DocumentRef ref = flink.getDocument().getRef();
                    final String current = session.getCurrentLifeCycleState(ref);
                    if (current.equals("todo")) {
                        session.followTransition(ref, "toDone");
                    }
                    DocumentModel flinkDoc = session.getDocument(flink.getDocument().getRef());
                    // get flink document unrestricted since trash service uses document coreSession
                    Framework.getService(TrashService.class).trashDocument(flinkDoc);

                    envContext.getProperties().remove(CaseManagementEventConstants.EVENT_CONTEXT_CASE_LINK);
                    eventProducer.fireEvent(envContext.newEvent(EventNames.afterCaseLinkRemovedEvent.name()));
                } catch (final Exception e) {
                    throw new NuxeoException(e);
                }
            }
        }
            .runUnrestricted();
    }

    @Override
    public String retrieveDocumentQuestionId(
        final CoreSession session,
        final long numeroQuestion,
        final String type,
        final String source,
        final long legislature
    ) {
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("Le type ne peut pas etre nul");
        }
        if (StringUtils.isBlank(source)) {
            throw new IllegalArgumentException("La source de la question ne peut pas etre nulle");
        }
        if (session == null) {
            throw new IllegalArgumentException("La session ne peut pas etre nulle");
        }

        try {
            final Serializable[] params = new Serializable[] { numeroQuestion, type, source, legislature };
            // on peut rechercher directement les ids (au plus 1 resultat : les 4 valeurs identifient la question)
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, UNFXQL_REQUETE_QUESTION, params);
            if (ids == null || ids.isEmpty()) {
                return null;
            } else {
                if (ids.size() > 1) {
                    final StringBuilder strBuilder = new StringBuilder("Recherche de la question [")
                        .append(source)
                        .append(" - ")
                        .append(legislature)
                        .append(" - ")
                        .append(type)
                        .append(" - ")
                        .append(numeroQuestion)
                        .append("] a retourné plusieurs resultat [");
                    for (final String id : ids) {
                        strBuilder.append(id).append(", ");
                    }
                    strBuilder.append("]");
                    LOG.warn(strBuilder.toString());
                }
                return ids.get(0);
            }
        } catch (final NuxeoException e) {
            if (LOG.isErrorEnabled()) {
                final StringBuilder strBuilder = new StringBuilder("Recherche de la question [")
                    .append(source)
                    .append(" - ")
                    .append(legislature)
                    .append(" - ")
                    .append(type)
                    .append(" - ")
                    .append(numeroQuestion)
                    .append("] a echoué");
                LOG.error(strBuilder.toString(), e);
            }
            throw new NuxeoException("exception occured in retrieveDocumentQuestionId", e);
        }
    }

    /**
     * Test sur l'existence d'une question
     *
     * @see fr.dila.reponses.api.service.DossierDistributionService#isExistingQuestion(CoreSession, long, String, String, long)
     */
    @Override
    public final boolean isExistingQuestion(
        final CoreSession session,
        final long numeroQuestion,
        final String type,
        final String source,
        final long legislature
    ) {
        return retrieveDocumentQuestionId(session, numeroQuestion, type, source, legislature) != null;
    }

    // Surchage de restart dossier dans Réponses afin de gérer les dossiers allotis
    @Override
    public void restartDossier(final CoreSession session, final DocumentModel dossierDoc) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Redémarrage du dossier [" + dossierDoc.getName() + "]");
        }

        // Vérifie que le dossier est à l'état terminé
        if (!dossierDoc.getCurrentLifeCycleState().equals(STDossier.DossierState.done.name())) {
            throw new NuxeoException("Le dossier doit être à l'état done");
        }

        // Vérifie que la feuille de route est à l'état terminé
        final DocumentModel routeInstanceDoc = getLastDocumentRouteForDossier(session, dossierDoc);
        if (!routeInstanceDoc.getCurrentLifeCycleState().equals(STDossier.DossierState.done.name())) {
            throw new NuxeoException("La feuille de route doit être à l'état done");
        }

        final List<DocumentModel> listDossier = new ArrayList<>();
        // ajout du dossier à redémarrer
        listDossier.add(dossierDoc);

        // charge les dossiers allotis
        final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        final JournalService journalService = STServiceLocator.getJournalService();

        final Dossier dossierToRestart = dossierDoc.getAdapter(Dossier.class);
        final Allotissement allotissement = allotissementService.getAllotissement(
            dossierToRestart.getDossierLot(),
            session
        );
        if (allotissement != null) {
            final List<String> idDossierList = allotissement.getIdDossiers();
            idDossierList.remove(dossierDoc.getId());
            listDossier.addAll(session.getDocuments(idDossierList, null));
        }

        final SSPrincipal user = (SSPrincipal) session.getPrincipal();
        final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
        final boolean isReponsePublished = reponseService.isReponsePublished(session, dossierDoc);

        if (isReponsePublished) {
            new UnrestrictedSessionRunner(session) {

                @Override
                public void run() {
                    for (final DocumentModel dossierModel : listDossier) {
                        dossierModel.followTransition(STDossier.DossierTransition.backToRunning.name());
                    }
                    // Redémarre la feuille de route
                    final SSFeuilleRoute feuilleRouteInstance = routeInstanceDoc.getAdapter(SSFeuilleRoute.class);
                    feuilleRouteInstance.backToReady(session);
                    feuilleRouteInstance.run(session);
                }
            }
                .runUnrestricted();

            String currentLifeCycleState = dossierDoc.getCurrentLifeCycleState();
            journalService.journaliserActionFDR(
                session,
                dossierDoc,
                STEventConstant.EVENT_FEUILLE_ROUTE_RESTART,
                currentLifeCycleState.equals("done")
                    ? STEventConstant.COMMENT_FEUILLE_ROUTE_RESTART
                    : STEventConstant.COMMENT_FEUILLE_ROUTE_ET_DOSSIER_RESTART
            );

            if (LOG.isInfoEnabled()) {
                LOG.info("Le dossier a été redémarré pour procéder à un erratum");
            }
        } else {
            new UnrestrictedSessionRunner(session) {

                @Override
                public void run() {
                    final JetonService jetonService = ReponsesServiceLocator.getJetonService();
                    final STParametreService paramService = STServiceLocator.getSTParametreService();
                    // Redémarre la feuille de route
                    final SSFeuilleRoute feuilleRouteInstance = routeInstanceDoc.getAdapter(SSFeuilleRoute.class);
                    feuilleRouteInstance.backToReady(session);
                    feuilleRouteInstance.run(session);

                    for (final DocumentModel dossierModel : listDossier) {
                        Dossier dossier = dossierModel.getAdapter(Dossier.class);
                        Question question = dossier.getQuestion(session);
                        question.setEtatQuestion(
                            session,
                            VocabularyConstants.ETAT_QUESTION_EN_COURS,
                            new GregorianCalendar(),
                            paramService.getParametreValue(session, STParametreConstant.DELAI_QUESTION_SIGNALEE)
                        );
                        dossier.setIsRedemarre(Boolean.TRUE);
                        session.saveDocument(dossier.getDocument());
                        session.saveDocument(question.getDocument());
                        jetonService.addDocumentInBasket(
                            session,
                            STWebserviceConstant.CHERCHER_CHANGEMENT_ETAT_QUESTION,
                            dossier.getIdMinistereAttributaireCourant(),
                            dossierModel,
                            dossier.getNumeroQuestion().toString(),
                            null,
                            null
                        );
                        dossierModel.followTransition(STDossier.DossierTransition.backToRunning.name());
                    }

                    sendNotificationDossierRestart(dossierToRestart.getQuestion(session), user);
                }
            }
                .runUnrestricted();

            String currentLifeCycleState = dossierDoc.getCurrentLifeCycleState();
            journalService.journaliserActionFDR(
                session,
                dossierDoc,
                STEventConstant.EVENT_FEUILLE_ROUTE_RESTART,
                currentLifeCycleState.equals("done")
                    ? STEventConstant.COMMENT_FEUILLE_ROUTE_RESTART
                    : STEventConstant.COMMENT_FEUILLE_ROUTE_ET_DOSSIER_RESTART
            );

            if (LOG.isInfoEnabled()) {
                LOG.info(
                    "Feuille de route de la question [" +
                    dossierToRestart.getQuestion(session).getSourceNumeroQuestion() +
                    "] redémarrée."
                );
                if (currentLifeCycleState.equals("done")) {
                    LOG.info("Dossier redémarré.");
                }
            }
        }
    }

    private void sendNotificationDossierRestart(Question question, SSPrincipal user) {
        SendNotificationDossierRestartWork notificationWork = new SendNotificationDossierRestartWork(
            question.getId(),
            question.getOrigineQuestion(),
            user.toString()
        );
        getWorkManager().schedule(notificationWork, true);
    }

    @Override
    public void notifyMailboxModification(final CoreSession session, final String mailboxDocId) {
        final Boolean isMigrationEnCours = ReponsesServiceLocator.getUpdateTimbreService().isMigrationEnCours(session);
        // si migration en cours on ne fait pas l'update du precalcul qui est fait en fin de chgt de gvt
        if (Boolean.FALSE.equals(isMigrationEnCours)) {
            final Map<String, Serializable> props = new HashMap<>();
            props.put(ReponsesEventConstant.EVT_CONTEXT_MAILBOX_DOC_ID, mailboxDocId);
            final EventContext ctx = new EventContextImpl(session, session.getPrincipal());
            ctx.setProperties(props);
            final Event event = ctx.newEvent(ReponsesEventConstant.EVT_UPDATE_MAILBOX_STEP_COUNT);

            final EventProducer eventProducer = STServiceLocator.getEventProducer();
            eventProducer.fireEvent(event);
        }
    }

    @Override
    public void setMinistereRattachement(final CoreSession session, final Question question, final String idMinistere) {
        if (idMinistere != null) {
            final String intituleMinistere = STServiceLocator
                .getSTMinisteresService()
                .getEntiteNode(idMinistere)
                .getLabel();
            question.setIdMinistereRattachement(idMinistere);
            question.setIntituleMinistereRattachement(intituleMinistere);
        }
    }

    @Override
    public void setDirectionPilote(final CoreSession session, final Question question, final String idDirection) {
        if (idDirection != null) {
            final String directionLable = STServiceLocator
                .getSTUsAndDirectionService()
                .getUniteStructurelleNode(idDirection)
                .getLabel();
            question.setIdDirectionPilote(idDirection);
            question.setIntituleDirectionPilote(directionLable);
        }
    }

    @Override
    public void validerEtape(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel dossierLinkDoc
    ) {
        final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
        markDossierAsArbitrated(session, dossierLink, dossierDoc);

        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        final DocumentModel etapeDoc = new UnrestrictedGetDocumentRunner(
            session,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.ROUTING_TASK_SCHEMA
        )
        .getById(dossierLink.getRoutingTaskId());
        final ReponsesRouteStep etapeCourante = etapeDoc.getAdapter(ReponsesRouteStep.class);

        // Traitement du type réattribution
        // Recherche d'une nouvelle route
        // Mettre à jour l'historique des attributions (cf : dossierService.reattribuerDossier)
        // flux vers les assemblées pour informer de la réattribution (cf : FeuilleRouteSubstitutionListener)
        if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(etapeCourante.getType())) {
            reattribuerDossier(
                session,
                dossier,
                dossierLink,
                dossier.getIdMinistereReattribution(),
                SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE,
                etapeCourante
            );
        } else {
            // Si on part d'une étape Pour transmission aux assemblées, on vérifie la signautre, créé le jeton et on passe la question à
            // Répondu
            if (VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(etapeCourante.getType())) {
                validerTransmissionAssemblees(session, dossier);
            }
            // Si on part d'une étape Pour signature, on signe la réponse
            if (VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE.equals(etapeCourante.getType())) {
                final DossierSignatureService dossierSignatureService = ReponsesServiceLocator.getDossierSignatureService();
                dossierSignatureService.signerDossier(dossier, session);
            }

            // S'il n'y a qu'une étape active, que la prochaine étape est Pour transmission aux assemblées et que le
            // dossier n'est pas signé,
            // on ajoute 2 étapes
            checkNextStep(session, dossier, etapeCourante);
            super.validerEtape(session, dossierDoc, dossierLinkDoc, etapeDoc);
        }
        session.save();
    }

    /**
     * Si la prochaine étape est "pour transmission aux assemblées", le dossier doit être signé
     * @param session
     * @param dossier
     * @param etapeCourante
     */
    private void checkNextStep(final CoreSession session, Dossier dossier, ReponsesRouteStep etapeCourante) {
        boolean verifierDossier = false;
        try {
            final ReponseFeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
            boolean isNextStepTransmissionAssemblees = feuilleRouteService.isNextStepTransmissionAssemblees(
                session,
                dossier.getLastDocumentRoute(),
                etapeCourante.getDocument()
            );
            int nbActiveSteps = feuilleRouteService.getRunningSteps(session, dossier.getLastDocumentRoute()).size();
            if (isNextStepTransmissionAssemblees && nbActiveSteps == 1) {
                final DossierSignatureService dossierSignatureService = ReponsesServiceLocator.getDossierSignatureService();
                verifierDossier = dossierSignatureService.verifierDossier(dossier, session);
                if (!verifierDossier) {
                    ReponsesServiceLocator
                        .getReponsesMailService()
                        .sendMailAfterSignatureError(session, dossier.getDocument());
                    feuilleRouteService.addStepsSignatureAndTransmissionAssemblees(
                        session,
                        dossier.getDocument(),
                        etapeCourante
                    );
                }
            }
        } catch (final NuxeoException e) {
            LOG.warn("La vérification du dossier a rencontré une erreur", e);
        }
    }

    /**
     * Traitement spécifique de l'étape "pour transmission aux assemblées"
     * @param session
     * @param dossier
     *
     */
    private void validerTransmissionAssemblees(final CoreSession session, Dossier dossier) {
        // On doit vérifier la signature avant de valider l'étape
        final DossierSignatureService dossierSignatureService = ReponsesServiceLocator.getDossierSignatureService();
        boolean verifierDossier = dossierSignatureService.verifierDossier(dossier, session);
        if (verifierDossier) {
            final Question question = dossier.getQuestion(session);
            if (dossier.getReponse(session).getErrata().isEmpty()) {
                ReponsesServiceLocator
                    .getJetonService()
                    .createJetonTransmissionsAssemblees(session, question, STWebserviceConstant.CHERCHER_REPONSES);
            } else {
                ReponsesServiceLocator
                    .getJetonService()
                    .createJetonTransmissionsAssemblees(
                        session,
                        question,
                        STWebserviceConstant.CHERCHER_ERRATA_REPONSES
                    );
            }
        } else {
            throw new StepValidationException("", StepValidationException.CAUSEEXC.SIGNATURE_INVALID);
        }
    }

    @Override
    public void validerEtapeRefus(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel dossierLinkDoc
    ) {
        final STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
        markDossierAsArbitrated(session, dossierLink, dossierDoc);
        super.validerEtapeRefus(session, dossierDoc, dossierLinkDoc);
    }

    @Override
    public void validerEtapeNonConcerne(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel dossierLinkDoc
    ) {
        final STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
        markDossierAsArbitrated(session, dossierLink, dossierDoc);
        super.validerEtapeNonConcerne(session, dossierDoc, dossierLinkDoc);
    }

    @Override
    public void rejeterDossierLink(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel dossierLinkDoc
    ) {
        final ReponseFeuilleRouteService fdrService = ReponsesServiceLocator.getFeuilleRouteService();

        final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
        // Récupération de l'étape courante
        final DocumentModel etapeDoc = new UnrestrictedGetDocumentRunner(session, STSchemaConstant.ROUTING_TASK_SCHEMA)
        .getById(dossierLink.getRoutingTaskId());
        final ReponsesRouteStep etapeCourante = etapeDoc.getAdapter(ReponsesRouteStep.class);

        markDossierAsArbitrated(session, dossierLink, dossierDoc);

        // Cas du refus de réattribution
        if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(etapeCourante.getType())) {
            fdrService.addStepAfterRejectReattribution(session, dossierLink.getRoutingTaskId());
        }

        super.rejeterDossierLink(session, dossierDoc, dossierLink.getDocument());
    }

    /**
     * Vérifie l'étape en cours est "Pour arbitrage" et marque le dossier comme arbitré
     *
     *
     *
     */
    private void markDossierAsArbitrated(
        final CoreSession session,
        final STDossierLink dossierLink,
        final DocumentModel dossierDoc
    ) {
        ReponsesArbitrageService arbService = ReponsesServiceLocator.getReponsesArbitrageService();

        if (arbService.isStepPourArbitrage(dossierLink)) {
            arbService.updateDossierAfterArbitrage(session, dossierDoc);
        }
    }

    /**
     * Effecute la copie des notes de l'ancienne étape vers la nouvelle
     *
     * @param session
     * @param oldStepDoc
     * @param newStepDoc
     *
     */
    private void moveCommentInNewStep(
        CoreSession session,
        final DocumentModel oldStepDoc,
        final DocumentModel newStepDoc
    ) {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(newStepDoc.getType())) {
                    String queryNoteMessage = "select item as id from rtsk_comments where id = ?";

                    try (
                        IterableQueryResult res = QueryUtils.doSqlQuery(
                            session,
                            new String[] { FlexibleQueryMaker.COL_ID },
                            queryNoteMessage,
                            new Object[] { oldStepDoc.getId() }
                        )
                    ) {
                        int count = 0;
                        final Iterator<Map<String, Serializable>> iterator = res.iterator();
                        while (iterator.hasNext()) {
                            final Map<String, Serializable> row = iterator.next();
                            String idComment = (String) row.get(FlexibleQueryMaker.COL_ID);
                            IdRef commentRef = new IdRef(idComment);
                            DocumentModel commentDoc = session.getDocument(commentRef);

                            List<String> commentIds = PropertyUtil.getStringListProperty(
                                newStepDoc,
                                STSchemaConstant.ROUTING_TASK_SCHEMA,
                                STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY
                            );
                            commentIds.add(commentDoc.getId());
                            newStepDoc.setProperty(
                                STSchemaConstant.ROUTING_TASK_SCHEMA,
                                STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY,
                                commentIds
                            );

                            session.saveDocument(commentDoc);
                            session.save();

                            count++;
                        }

                        ReponsesRouteStep step = newStepDoc.getAdapter(ReponsesRouteStep.class);
                        step.setNumberOfComments(count);
                    }
                }
            }
        }
            .runUnrestricted();
    }
}
