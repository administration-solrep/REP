package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DOCUMENT_TYPE;
import static fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_INSTANCE;
import static fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_STEP_FOLDER;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.retrieveDocuments;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.google.common.base.Joiner;
import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Allotissement.TypeAllotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.cases.flux.RErratum;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.exception.AllotissementException;
import fr.dila.reponses.api.exception.AllotissementException.AllotissementExceptionRaison;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.core.cases.flux.RErratumImpl;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DocUtil;
import fr.dila.st.core.util.StringHelper;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.versioning.VersioningService;
import org.nuxeo.runtime.transaction.TransactionHelper;

public class AllotissementServiceImpl implements AllotissementService {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(AllotissementServiceImpl.class);

    @Override
    public boolean isAllotit(final Question question, final CoreSession session) {
        final StringBuilder query = new StringBuilder(" SELECT d.ecm:uuid as id ");
        query.append(" FROM ");
        query.append(DossierConstants.DOSSIER_DOCUMENT_TYPE);
        query.append(" as d WHERE d.dos:");
        query.append(DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID);
        query.append(" = ? AND d.dos:");
        query.append(DossierConstants.DOSSIER_NOM_DOSSIER_LOT);
        query.append(" IS NOT NULL ");

        final Long count = QueryUtils.doCountQuery(
            session,
            QueryUtils.ufnxqlToFnxqlQuery(query.toString()),
            new Object[] { question.getDocument().getId() }
        );

        return count > 0;
    }

    @Override
    public boolean isAllotit(final Dossier dossier) {
        return StringUtils.isNotEmpty(dossier.getDossierLot());
    }

    private void checkAllotissementError(CoreSession session, Dossier dossierDirecteur, Question curQuestion) {
        DocumentModel docDossier = session.getDocument(curQuestion.getDossierRef());
        String idMinCourant = PropertyUtil.getStringProperty(
            docDossier,
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT
        );
        if (!StringUtils.equals(dossierDirecteur.getIdMinistereAttributaireCourant(), idMinCourant)) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_MINISTERE,
                "Allotissement du dossier " +
                curQuestion.getSourceNumeroQuestion() +
                " avec " +
                dossierDirecteur.getNumeroQuestion() +
                " impossible. Il ne possède pas le même ministère attributaire que le dossier directeur." +
                "Les dossiers d'un même lot doivent appartenir au même ministère"
            );
        }

        String reponseID = PropertyUtil.getStringProperty(
            docDossier,
            DossierConstants.DOSSIER_SCHEMA,
            DossierConstants.DOSSIER_DOCUMENT_REPONSE_ID
        );
        if (StringUtils.isNotBlank(reponseID)) {
            DocumentModel docReponse = session.getDocument(new IdRef(reponseID));
            if (
                StringUtils.isNotBlank(
                    PropertyUtil.getStringProperty(
                        docReponse,
                        DossierConstants.REPONSE_DOCUMENT_SCHEMA,
                        DossierConstants.DOSSIER_SIGNATURE_REPONSE
                    )
                )
            ) {
                throw new AllotissementException(
                    AllotissementExceptionRaison.ERROR_ETAT_DOSSIER,
                    "La réponse est signée pour la question : " +
                    curQuestion.getSourceNumeroQuestion() +
                    ", allotissement impossible"
                );
            }
        }
    }

    private Boolean createLotUnRestricted(
        final Question questionDirectrice,
        final List<Question> questions,
        final CoreSession session,
        final NuxeoPrincipal realUser
    ) {
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
        final JournalService journalService = STServiceLocator.getJournalService();

        final DocumentModel docDossierDirecteur = session.getDocument(questionDirectrice.getDossierRef());
        final Dossier dossierDirecteur = docDossierDirecteur.getAdapter(Dossier.class);

        final List<String> listIdDossier = new ArrayList<>();
        listIdDossier.add(docDossierDirecteur.getId());

        if (!dossierDirecteur.hasFeuilleRoute()) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_FDR,
                "Le dossier " +
                dossierDirecteur.getNumeroQuestion() +
                " n'a pas de feuille de route, allotissement impossible."
            );
        }

        final DocumentModel routeDirectriceDocumentModel = session.getDocument(
            new IdRef(dossierDirecteur.getLastDocumentRoute())
        );
        final SSFeuilleRoute docRouteDirectrice = routeDirectriceDocumentModel.getAdapter(SSFeuilleRoute.class);
        try {
            documentRoutingService.lockDocumentRoute(docRouteDirectrice, session);
        } catch (final FeuilleRouteAlreadyLockedException e) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_LOCK,
                "Le dossier " + dossierDirecteur.getNumeroQuestion() + " est verrouillé, allotissement impossible."
            );
        }

        final List<Dossier> dossiers = new ArrayList<>();
        dossiers.add(dossierDirecteur);

        final DossierLink dossierLink = getLastStepDirecteurInfo(session, docDossierDirecteur);

        final Allotissement allotissement = createAllotissementDoc(session, listIdDossier);
        for (final Question question : questions) {
            checkAllotissementError(session, dossierDirecteur, question);
            addQuestionToRouteDirectrice(
                session,
                listIdDossier,
                dossiers,
                dossierLink,
                question,
                routeDirectriceDocumentModel.getId(),
                allotissement.getNom(),
                dossierDirecteur.isArbitrated()
            );
        }
        final String comment =
            "Création de l'allotissement " + prepareIdsDossierForLog(session, listIdDossier).toString();
        journalService.journaliserActionForUser(
            session,
            realUser,
            docDossierDirecteur,
            ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT,
            comment,
            STEventConstant.CATEGORY_PARAPHEUR
        );

        // on link tous les dossiers à la fdr directrice
        docRouteDirectrice.setAttachedDocuments(listIdDossier);
        session.saveDocument(routeDirectriceDocumentModel);

        allotissement.setIdDossiers(listIdDossier);
        session.saveDocument(allotissement.getDocument());
        session.save();

        reponseService.saveReponse(
            session,
            dossierDirecteur.getReponse(session).getDocument(),
            dossierDirecteur.getDocument()
        );
        documentRoutingService.unlockDocumentRoute(docRouteDirectrice, session);

        session.save();
        return true;
    }

    private Allotissement createAllotissementDoc(final CoreSession session, final List<String> listIdDossier) {
        final String title = String.valueOf(Calendar.getInstance().getTimeInMillis());
        DocumentModel modelDesired = session.createDocumentModel(
            "/case-management/allotissements-root",
            title,
            DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE
        );
        final Allotissement allotissement = modelDesired.getAdapter(Allotissement.class);
        allotissement.setIdDossiers(listIdDossier);
        allotissement.setNom(title);
        modelDesired = session.createDocument(allotissement.getDocument());

        for (final String idDoc : listIdDossier) {
            final DocumentModel dossierDoc = session.getDocument(new IdRef(idDoc));
            final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            dossier.setDossierLot(title);
            session.saveDocument(dossierDoc);
        }

        session.save();

        return modelDesired.getAdapter(Allotissement.class);
    }

    private StringBuilder prepareIdsDossierForLog(final CoreSession session, final List<String> idDossiers) {
        final StringBuilder idsDossier = new StringBuilder(" [");
        int index = 0;
        for (final String idDossier : idDossiers) {
            final DocumentModel doc = session.getDocument(new IdRef(idDossier));
            final Dossier dossier = doc.getAdapter(Dossier.class);
            idsDossier.append(dossier.getQuestion(session).getSourceNumeroQuestion());
            if (++index < idDossiers.size()) {
                idsDossier.append(", ");
            }
        }
        idsDossier.append("]");
        return idsDossier;
    }

    /**
     * Parcourt les étapes de la feuille de route du dossier à allotir pour
     * vérifier que l'étape en cours se trouve à un poste du ministère
     * attributaire
     *
     * @param routeStepsToCheck
     * @param dossierToCheck
     * @throws AllotissementException
     *
     */
    private void checkCurrentStepMin(final List<DocumentModel> routeStepsToCheck, final Dossier dossierToCheck)
        throws AllotissementException {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        ReponsesRouteStep step = null;
        final String running = FeuilleRouteElement.ElementLifeCycleState.running.name();
        final Set<String> ministereIds = new HashSet<>();
        for (final DocumentModel stepModel : routeStepsToCheck) {
            if (stepModel.getCurrentLifeCycleState().equals(running)) {
                ministereIds.clear();
                // Allotissement possible ssi le poste de l'etape courante est
                // relié au ministere attributaire
                step = stepModel.getAdapter(ReponsesRouteStep.class);
                final List<EntiteNode> organigrammeList = mailboxPosteService.getMinistereListFromMailbox(
                    step.getDistributionMailboxId()
                );

                for (final EntiteNode entiteNode : organigrammeList) {
                    ministereIds.add(entiteNode.getId().toString());
                }

                final String ministereAttributaire = dossierToCheck.getIdMinistereAttributaireCourant();

                if (StringUtils.isBlank(ministereAttributaire) || !ministereIds.contains(ministereAttributaire)) {
                    throw new AllotissementException(
                        AllotissementExceptionRaison.ERROR_FDR,
                        "L'étape courante du dossier " +
                        dossierToCheck.getNumeroQuestion() +
                        " n'est pas liée à son ministère attributaire, allotissement impossible."
                    );
                }
            }
        }
        if (step == null) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_FDR,
                "Le dossier " +
                dossierToCheck.getNumeroQuestion() +
                " n'a pas d'étape en cours, allotissement impossible."
            );
        }
    }

    /**
     * Ajoute une question à la route directrice Vérifie qu'une feuille de route
     * est présente pour la question à ajouter, qu'il n'est pas verrouillé et
     * qu'il a une étape en cours
     *
     * JBT : on passe à l'état canceled les anciennes feuilles de route des
     * questions du lot
     *
     * @param session
     *            CoreSession - la session en cours
     * @param listIdDossier
     *            List&lt;String&gt; - la liste des ids des dossiers présents
     *            dans le lot
     * @param listDossier
     *            List&lt;Dossier&gt; - la liste des dossiers présents dans le
     *            lot
     * @param dossierLinkDirecteur
     *            DossierLink - le dossierLink du dossier directeur du lot
     * @param question
     *            Question - la question à ajouter au lot
     * @param routeDirectriceId
     *            String - id de la route du dossier directeur
     * @param nomAllotissement
     *            String - le nom de l'allotissement pour lequel ajouter le
     *            dossier de la question
     *
     */
    private void addQuestionToRouteDirectrice(
        final CoreSession session,
        final List<String> listIdDossier,
        final List<Dossier> listDossier,
        final DossierLink dossierLinkDirecteur,
        final Question question,
        final String routeDirectriceId,
        final String nomAllotissement,
        final boolean isArbitrated
    ) {
        // Chargement des service
        final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        final FeuilleRouteModelService fdrModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

        DocumentModel dossierDoc = session.getDocument(question.getDossierRef());
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        if (!dossier.hasFeuilleRoute()) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_FDR,
                "Le dossier " + dossier.getNumeroQuestion() + " n'a pas de feuille de route, allotissement impossible."
            );
        }

        final DocumentModel routeDocumentModel = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
        final SSFeuilleRoute docRoute = routeDocumentModel.getAdapter(SSFeuilleRoute.class);

        final List<DocumentModel> routeSteps = fdrModelService.getAllRouteElement(
            dossier.getLastDocumentRoute(),
            session
        );
        // Parcourt les étapes de la feuille de route du dossier à allotir
        // pour vérifier que l'étape en cours est dans un poste identique au
        // ministère attributaire
        // du dossier directeur du lot
        checkCurrentStepMin(routeSteps, dossier);

        try {
            documentRoutingService.lockDocumentRoute(docRoute, session);
        } catch (final FeuilleRouteAlreadyLockedException e) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_LOCK,
                "Le dossier " + dossier.getNumeroQuestion() + " est verrouillé, allotissement impossible."
            );
        }

        // on supprime les steps de la feuille de route de la question qui sont
        // à venir
        final String done = FeuilleRouteElement.ElementLifeCycleState.done.name();
        for (final DocumentModel stepModel : routeSteps) {
            if (!stepModel.getCurrentLifeCycleState().equals(done)) {
                documentRoutingService.softDeleteStep(session, stepModel);
            }
        }
        session.save();

        final List<DocumentModel> listDossierLink = corbeilleService.findDossierLink(session, dossierDoc.getId());
        String name = "";

        if (listDossierLink == null || listDossierLink.isEmpty()) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_FDR,
                "Le dossier " + dossier.getNumeroQuestion() + " n'a pas d'étape en cours, allotissement impossible."
            );
        }

        for (final DocumentModel documentModel : listDossierLink) {
            name = documentModel.getId();
            dossierDistributionService.deleteDossierLink(session, documentModel.getAdapter(DossierLink.class));
        }

        // on passe à l'état canceled les anciennes feuille de route, puis on la
        // change
        if (!Objects.equals(dossier.getLastDocumentRoute(), routeDirectriceId)) {
            final DocumentModel feuilleDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
            final FeuilleRoute feuilleRoute = feuilleDoc.getAdapter(FeuilleRoute.class);
            feuilleRoute.cancel(session);
        }
        dossier.setLastDocumentRoute(routeDirectriceId);
        session.saveDocument(dossier.getDocument());

        // on duplique les droits du dossier directeur dans le dossier
        final ACP acpDirecteur = dossierLinkDirecteur.getDossier(session).getDocument().getACP();
        session.setACP(dossier.getDocument().getRef(), acpDirecteur, true);
        session.save();

        // on duplique le caseLinkDirecteur et on le rattache au dossier courant
        DocumentModel docCopy = session.copy(
            dossierLinkDirecteur.getDocument().getRef(),
            dossierLinkDirecteur.getDocument().getParentRef(),
            name
        );
        docCopy.setProperty("case_link", "caseDocumentId", dossierDoc.getId());
        final Calendar now = Calendar.getInstance();
        docCopy.setProperty("case_link", "date", now);
        DublincoreSchemaUtils.setModifiedDate(docCopy, now);
        DublincoreSchemaUtils.setTitle(docCopy, "Dossier n°" + dossier.getNumeroQuestion());
        docCopy = session.saveDocument(docCopy);

        // re-set all fields
        dossierDistributionService.setDossierLinksFields(session, docCopy);

        // on duplique les droits du caseLinkDirecteur directeur dans le
        // caseLink
        final ACP acpDLDirecteur = dossierLinkDirecteur.getDocument().getACP();
        session.setACP(docCopy.getRef(), acpDLDirecteur, true);
        session.saveDocument(docCopy);

        // on ajoute le dossier sur la feuille de route du dossier directeur
        listIdDossier.add(dossierDoc.getId());
        listDossier.add(dossier);
        session.save();

        // On met à jour l'arbitrage
        dossier.setIsArbitrated(isArbitrated);
        // On met à jour la signature de ce dossier avec la signature du dossier
        // directeur
        dossierDoc = dossier.getDocument();
        dossier.setDossierLot(nomAllotissement);
        updateSignatureFor(session, dossierDoc, dossierLinkDirecteur.getDossier(session).getReponse(session));
        session.saveDocument(dossierDoc);

        documentRoutingService.unlockDocumentRoute(docRoute, session);
        session.save();
    }

    @Override
    public boolean updateLot(
        final Question questionDirectrice,
        final List<Question> listQuestions,
        final CoreSession session,
        final TypeAllotissement type
    ) {
        final NuxeoPrincipal currentUser = session.getPrincipal();
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                try {
                    switch (type) {
                        case SUPPR:
                            removeFromLot(questionDirectrice, listQuestions, session, currentUser);
                            break;
                        case AJOUT:
                            addToLot(questionDirectrice, listQuestions, session, currentUser);
                            break;
                        default:
                            break;
                    }
                } catch (final NuxeoException e) {
                    TransactionHelper.setTransactionRollbackOnly();
                    LOGGER.error(
                        session,
                        ReponsesLogEnumImpl.FAIL_CREATE_ALLOT_TEC,
                        "Erreur dans la mise à jour du lot ",
                        e
                    );
                    throw e;
                }
            }
        }
            .runUnrestricted();
        return true;
    }

    @Override
    public boolean createLot(
        final Question questionDirectrice,
        final List<Question> listQuestions,
        final CoreSession session
    ) {
        final NuxeoPrincipal currentUser = session.getPrincipal();
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                try {
                    createLotUnRestricted(questionDirectrice, listQuestions, session, currentUser);
                } catch (final NuxeoException e) {
                    TransactionHelper.setTransactionRollbackOnly();
                    throw e;
                }
            }
        }
            .runUnrestricted();
        return true;
    }

    @Override
    public Allotissement getAllotissement(final String nom, final CoreSession session) {
        if (StringUtils.isEmpty(nom)) {
            return null;
        }
        final StringBuilder query = new StringBuilder("SELECT a.ecm:uuid as id FROM ");
        query.append(DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE);
        query.append(" as a WHERE a.allot:nom = ? ");

        final List<DocumentModel> docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE,
            query.toString(),
            new Object[] { nom }
        );
        return docs.isEmpty() ? null : docs.get(0).getAdapter(Allotissement.class);
    }

    /**
     * methode appelee avec une session unrestricted
     *
     * @param questionDirectrice
     * @param listQuestions
     * @param session
     *
     */
    private void removeFromLot(
        final Question questionDirectrice,
        final List<Question> listQuestions,
        final CoreSession session,
        final NuxeoPrincipal realUser
    ) {
        final DocumentModel docDossierDirecteur = session.getDocument(questionDirectrice.getDossierRef());
        final Dossier dossierDirecteur = docDossierDirecteur.getAdapter(Dossier.class);

        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final JournalService journalService = STServiceLocator.getJournalService();

        if (dossierDirecteur.getLastDocumentRoute() == null) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_FDR,
                "Le dossier " +
                dossierDirecteur.getNumeroQuestion() +
                " n'a pas de feuille de route, désallotissement impossible."
            );
        }

        final DocumentModel routeDirectriceDocumentModel = session.getDocument(
            new IdRef(dossierDirecteur.getLastDocumentRoute())
        );
        final SSFeuilleRoute docRouteDirectrice = routeDirectriceDocumentModel.getAdapter(SSFeuilleRoute.class);
        // Récupérer l'état du lock avant d'effectuer des modifications
        final boolean wasLock = LockUtils.isLocked(session, routeDirectriceDocumentModel.getRef());
        if (!LockUtils.isLockedBy(session, routeDirectriceDocumentModel.getRef(), realUser)) {
            try {
                documentRoutingService.lockDocumentRoute(docRouteDirectrice, session);
            } catch (final FeuilleRouteAlreadyLockedException e) {
                throw new AllotissementException(
                    AllotissementExceptionRaison.ERROR_LOCK,
                    "La feuille de route du dossier " +
                    dossierDirecteur.getNumeroQuestion() +
                    " est verrouillée par un autre utilisateur, désallotissement impossible."
                );
            }
        }

        final List<String> listIdDossier = docRouteDirectrice.getAttachedDocuments();
        final Allotissement allotissement = getAllotissement(dossierDirecteur.getDossierLot(), session);
        final StringBuilder idsDossier = prepareIdsDossierForLog(session, allotissement.getIdDossiers());
        final List<String> questionRetirees = new ArrayList<>();
        String srcNumQuestion = "";
        for (final Question q : listQuestions) {
            final DocumentModel dossierDoc = session.getDocument(q.getDossierRef());
            final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

            if (!listIdDossier.contains(dossierDoc.getId())) {
                // si le dossier est pas lié on le passe
                continue;
            }

            // on duplique la feuille de route du dossier directeur dans le
            // répertoire des fdr
            final DocumentModel docCopy = session.copy(
                routeDirectriceDocumentModel.getRef(),
                routeDirectriceDocumentModel.getParentRef(),
                routeDirectriceDocumentModel.getName()
            );
            final FeuilleRoute docRouteCopy = docCopy.getAdapter(FeuilleRoute.class);

            // initialise le champs denormalisé "documentRouteId dans les étapes
            // de la fdr
            documentRoutingService.initFeuilleRouteStep(session, docCopy);

            // on rattache que ce dossier a la fdr
            final List<String> idDossiers = new ArrayList<>();
            idDossiers.add(dossierDoc.getId());
            docRouteCopy.setAttachedDocuments(idDossiers);
            session.saveDocument(docCopy);

            // on duplique les droits de la feuille de route directrice dans la
            // copie
            final ACP acpDirecteur = routeDirectriceDocumentModel.getACP();
            session.setACP(docCopy.getRef(), acpDirecteur, true);
            session.saveDocument(docCopy);

            session.save();

            // modification de la fdr dans le dossier : les traces sont là pour
            // valider la présence d'un bug
            LOGGER.info(
                ReponsesLogEnumImpl.DEL_ALLOT_TEC,
                "Feuille de route du dossier avant suppression du lot " +
                dossier.getNumeroQuestion() +
                " :" +
                dossier.getDocument().getId() +
                " : " +
                dossier.getLastDocumentRoute()
            );
            dossier.setLastDocumentRoute(docCopy.getId());
            LOGGER.info(
                ReponsesLogEnumImpl.DEL_ALLOT_TEC,
                "Feuille de route du dossier après suppression du lot " +
                dossier.getNumeroQuestion() +
                " :" +
                dossier.getDocument().getId() +
                " : " +
                dossier.getLastDocumentRoute()
            );
            // delinkage de la reference du lot dans le dossier
            dossier.setDossierLot(null);
            session.saveDocument(dossier.getDocument());
            session.save();
            final DocumentModelList stepTable = documentRoutingService.getOrderedRouteElement(
                routeDirectriceDocumentModel.getId(),
                session
            );

            final Map<String, String> mapOldToNew = new HashMap<>();
            // on recupere l'id du step en cours pour mettre le dossierLink a
            // jour
            final DocumentModelList stepTableCopy = documentRoutingService.getOrderedRouteElement(
                docCopy.getId(),
                session
            );
            int i = 0;
            for (final DocumentModel stepDoc : stepTableCopy) {
                mapAllRunningStep(mapOldToNew, stepDoc, stepTable.get(i), session);
                i++;
            }

            if (!mapOldToNew.isEmpty()) {
                // on met le dossierLink à jour
                final List<DocumentModel> listDossierLink = corbeilleService.findDossierLink(
                    session,
                    dossierDoc.getId()
                );
                DossierLink dossierLink = null;
                for (final DocumentModel documentModel : listDossierLink) {
                    dossierLink = documentModel.getAdapter(DossierLink.class);
                    if (dossierLink.isActionnable()) {
                        final ActionableCaseLink acl = dossierLink.getDocument().getAdapter(ActionableCaseLink.class);
                        final String routingTaskId = mapOldToNew.get(acl.getStepId());
                        if (StringUtils.isEmpty(routingTaskId)) {
                            throw new AllotissementException(
                                AllotissementExceptionRaison.ERROR_FDR,
                                "Impossible de distribuer le dossier dans la corbeille"
                            );
                        }
                        acl.setStepId(routingTaskId);
                        dossierLink.setRoutingTaskId(routingTaskId);
                        session.saveDocument(dossierLink.getDocument());
                    }
                }
            } else if (!q.isRepondue()) {
                // On n'a pas trouvé de dossier link, et si la question n'est
                // pas répondue, elle doit être dans une
                // corbeille
                throw new AllotissementException(
                    AllotissementExceptionRaison.ERROR_FDR,
                    "Impossible de distribuer le dossier dans la corbeille"
                );
            }

            // on enleve le dossier sur la feuille de route du dossier directeur
            listIdDossier.remove(dossierDoc.getId());

            // journalisation du dossier retiré
            srcNumQuestion = q.getSourceNumeroQuestion();
            questionRetirees.add(srcNumQuestion);
            final String comment = "Dossier " + srcNumQuestion + " retiré du lot " + idsDossier.toString();
            journalService.journaliserActionForUser(
                session,
                realUser,
                dossierDoc,
                ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT,
                comment,
                STEventConstant.CATEGORY_PARAPHEUR
            );

            session.save();
        }
        // on save tous les dossiers encore liés à la fdr directrice
        docRouteDirectrice.setAttachedDocuments(listIdDossier);
        if (listIdDossier.isEmpty()) {
            // annule la feuille de route directrice si elle n'est plus
            // rattachée à aucun dossier
            routeDirectriceDocumentModel.getAdapter(FeuilleRoute.class).cancel(session);
        }
        session.saveDocument(routeDirectriceDocumentModel);
        session.save();
        final String pluriel = questionRetirees.size() > 1 ? "s" : "";
        final String comment =
            "Dossier" + pluriel + " désalloti" + pluriel + " : " + StringUtils.join(questionRetirees, ", ");
        if (listIdDossier.size() > 1) {
            final DocumentModel dossierDoc = session.getDocument(new IdRef(listIdDossier.get(0)));
            journalService.journaliserActionForUser(
                session,
                realUser,
                dossierDoc,
                ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT,
                comment,
                STEventConstant.CATEGORY_PARAPHEUR
            );
            allotissement.setIdDossiers(listIdDossier);
            session.saveDocument(allotissement.getDocument());
        } else {
            for (final String dossierId : allotissement.getIdDossiers()) {
                final DocumentModel doc = session.getDocument(new IdRef(dossierId));
                doc.getAdapter(Dossier.class).setDossierLot(null);
                session.saveDocument(doc);
            }
            // on delete l'allotissement
            LOGGER.info(session, ReponsesLogEnumImpl.DEL_ALLOT_TEC, allotissement.getDocument());
            DocumentModel allotissementDoc = allotissement.getDocument();
            DocumentRef allotRef = allotissementDoc.getRef();
            session.followTransition(allotRef, STLifeCycleConstant.TO_DELETE_TRANSITION);
        }

        // Dans le cas ou la route directrice n'était pas verrouillé au sébut de
        // la méthode, on la déverouille
        if (!wasLock) {
            documentRoutingService.unlockDocumentRoute(docRouteDirectrice, session);
        }
        session.save();
    }

    private void mapAllRunningStep(
        final Map<String, String> mapOldToNew,
        final DocumentModel stepDoc,
        final DocumentModel oldStep,
        final CoreSession session
    ) {
        if (FeuilleRouteElement.ElementLifeCycleState.running.name().equals(stepDoc.getCurrentLifeCycleState())) {
            if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(stepDoc.getType())) {
                final DocumentModelList listOld = session.getChildren(oldStep.getRef());
                final DocumentModelList listNew = session.getChildren(stepDoc.getRef());
                int stepIndex = 0;
                for (final DocumentModel stepDocNew : listNew) {
                    mapAllRunningStep(mapOldToNew, stepDocNew, listOld.get(stepIndex), session);
                    stepIndex++;
                }
            } else {
                mapOldToNew.put(oldStep.getId(), stepDoc.getId());
            }
        }
    }

    /**
     * Methode appelée avec une session unrestricted
     *
     * @param questionDirectrice
     * @param listQuestions
     * @param session
     *
     */
    private void addToLot(
        final Question questionDirectrice,
        final List<Question> listQuestions,
        final CoreSession session,
        final NuxeoPrincipal realUser
    ) {
        // Chargement des services
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
        final JournalService journalService = STServiceLocator.getJournalService();

        final DocumentModel docDossierDirecteur = session.getDocument(questionDirectrice.getDossierRef());
        final Dossier dossierDirecteur = docDossierDirecteur.getAdapter(Dossier.class);

        final String idMinistereAttributaireDirecteur = dossierDirecteur.getIdMinistereAttributaireCourant();

        if (!dossierDirecteur.hasFeuilleRoute()) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_FDR,
                "Le dossier " +
                dossierDirecteur.getNumeroQuestion() +
                " n'a pas de feuille de route, allotissement impossible."
            );
        }

        final DocumentModel routeDirectriceDocumentModel = session.getDocument(
            new IdRef(dossierDirecteur.getLastDocumentRoute())
        );
        final SSFeuilleRoute docRouteDirectrice = routeDirectriceDocumentModel.getAdapter(SSFeuilleRoute.class);

        try {
            documentRoutingService.lockDocumentRoute(docRouteDirectrice, session);
        } catch (final FeuilleRouteAlreadyLockedException e) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_LOCK,
                "Le dossier " + dossierDirecteur.getNumeroQuestion() + " est verrouillé, allotissement impossible."
            );
        }

        final Allotissement allotissement = getAllotissement(dossierDirecteur.getDossierLot(), session);
        final List<String> listIdDossier = docRouteDirectrice.getAttachedDocuments();

        final DossierLink dossierLink = getLastStepDirecteurInfo(session, docDossierDirecteur);

        final List<Dossier> listDossier = new ArrayList<>();
        final String comment = " ajouté au lot" + prepareIdsDossierForLog(session, listIdDossier).toString();
        for (final Question q : listQuestions) {
            if (q.getIdMinistereAttributaire().equals(idMinistereAttributaireDirecteur)) {
                addQuestionToRouteDirectrice(
                    session,
                    listIdDossier,
                    listDossier,
                    dossierLink,
                    q,
                    routeDirectriceDocumentModel.getId(),
                    allotissement.getNom(),
                    dossierDirecteur.isArbitrated()
                );

                journalService.journaliserActionForUser(
                    session,
                    realUser,
                    q.getDossier(session).getDocument(),
                    ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT,
                    "Dossier " + q.getSourceNumeroQuestion() + comment,
                    STEventConstant.CATEGORY_PARAPHEUR
                );
                session.save();
            } else {
                throw new AllotissementException(
                    AllotissementExceptionRaison.ERROR_FDR,
                    "La question N° " +
                    q.getNumeroQuestion() +
                    " n'est pas liée au même ministère attributaire, allotissement impossible."
                );
            }
        }

        // on link tous les dossiers à la fdr directrice
        docRouteDirectrice.setAttachedDocuments(listIdDossier);
        session.saveDocument(routeDirectriceDocumentModel);

        allotissement.setIdDossiers(listIdDossier);
        session.saveDocument(allotissement.getDocument());
        session.save();

        reponseService.saveReponse(
            session,
            dossierDirecteur.getReponse(session).getDocument(),
            dossierDirecteur.getDocument()
        );

        documentRoutingService.unlockDocumentRoute(docRouteDirectrice, session);

        session.save();
    }

    private DossierLink getLastStepDirecteurInfo(final CoreSession session, final DocumentModel docDossierDirecteur) {
        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        final List<DocumentModel> listDossierLinkDirecteur = corbeilleService.findDossierLink(
            session,
            docDossierDirecteur.getId()
        );

        DossierLink dossierLinkDirecteur = null;
        DossierLink dossierLink = null;
        for (final DocumentModel documentModel : listDossierLinkDirecteur) {
            dossierLinkDirecteur = documentModel.getAdapter(DossierLink.class);
            if (dossierLinkDirecteur.isActionnable()) {
                if (dossierLink == null) {
                    dossierLink = dossierLinkDirecteur;
                } else {
                    throw new AllotissementException(
                        AllotissementExceptionRaison.ERROR_FDR,
                        "La feuille de route directrice n'est pas allotissable."
                    );
                }
            }
        }

        if (dossierLink == null) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_FDR,
                "La feuille de route directrice est vide ou terminée, allotissement impossible."
            );
        }

        // Allotissement possible ssi le poste de l'etape courante est relié au
        // ministere attributaire

        final DocumentModel currentStep = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
        final ReponsesRouteStep stepDirecteur = currentStep.getAdapter(ReponsesRouteStep.class);

        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        final List<EntiteNode> organigrammeList = mailboxPosteService.getMinistereListFromMailbox(
            stepDirecteur.getDistributionMailboxId()
        );
        final Set<String> ministereIds = new HashSet<>();

        for (final EntiteNode entiteNode : organigrammeList) {
            ministereIds.add(entiteNode.getId().toString());
        }

        final Dossier dossier = docDossierDirecteur.getAdapter(Dossier.class);
        final String ministereAttributaireDirecteur = dossier.getIdMinistereAttributaireCourant();

        if (
            StringUtils.isBlank(ministereAttributaireDirecteur) ||
            !ministereIds.contains(ministereAttributaireDirecteur)
        ) {
            throw new AllotissementException(
                AllotissementExceptionRaison.ERROR_FDR,
                "L'étape courante du dossier " +
                dossier.getNumeroQuestion() +
                " n'est pas lié à son ministère attributaire, allotissement impossible."
            );
        }

        return dossierLink;
    }

    @Override
    public void updateTexteLinkedReponses(final CoreSession session, final DocumentModel repDoc) {
        final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(
            session,
            DossierConstants.DOSSIER_SCHEMA
        );

        final Reponse reponse = repDoc.getAdapter(Reponse.class);
        final FeuilleRoute docRouteDirectrice = getLastRoutefromReponse(session, reponse);
        if (docRouteDirectrice != null) {
            for (final String idDoc : docRouteDirectrice.getAttachedDocuments()) {
                final DocumentModel dossierDocLinked = uGet.getById(idDoc);
                final Dossier dossierLinked = dossierDocLinked.getAdapter(Dossier.class);
                final Reponse repLinked = dossierLinked.getReponse(session);
                if (!repLinked.getDocument().getId().equals(repDoc.getId())) {
                    repLinked.setTexteReponse(reponse.getTexteReponse());

                    // copie de l'erratum
                    if (reponse.isPublished()) {
                        final RErratum currentErratum = new RErratumImpl();
                        currentErratum.setPageJo(0);
                        currentErratum.setTexteErratum(reponse.getCurrentErratum());
                        currentErratum.setTexteConsolide(reponse.getTexteReponse());
                        final List<RErratum> erratums = repLinked.getErrata();
                        erratums.add(currentErratum);
                        repLinked.setErrata(erratums);
                        repLinked.setCurrentErratum(reponse.getCurrentErratum());
                    }

                    session.saveDocument(repLinked.getDocument());
                }
            }
        }

        session.save();
    }

    @Override
    public void updateVersionLinkedReponses(final CoreSession session, final DocumentModel repDoc) {
        final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(
            session,
            DossierConstants.DOSSIER_SCHEMA
        );

        final Reponse reponse = repDoc.getAdapter(Reponse.class);
        final FeuilleRoute docRouteDirectrice = getLastRoutefromReponse(session, reponse);

        if (docRouteDirectrice != null) {
            for (final String idDoc : docRouteDirectrice.getAttachedDocuments()) {
                final DocumentModel dossierDocLinked = uGet.getById(idDoc);
                final Dossier dossierLinked = dossierDocLinked.getAdapter(Dossier.class);
                final Reponse repLinked = dossierLinked.getReponse(session);
                if (!repLinked.getDocument().getId().equals(repDoc.getId())) {
                    repLinked.getDocument().putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
                    session.saveDocument(repLinked.getDocument());
                    session.save();
                }
            }
        }
    }

    @Override
    public void updateSignatureLinkedReponses(final CoreSession session, final Reponse reponse) {
        final FeuilleRoute docRouteDirectrice = getLastRoutefromReponse(session, reponse);
        if (docRouteDirectrice != null) {
            for (final String dossierId : docRouteDirectrice.getAttachedDocuments()) {
                final DocumentModel dossierAllotiDoc = session.getDocument(new IdRef(dossierId));
                updateSignatureFor(session, dossierAllotiDoc, reponse);
            }
        }

        session.save();
    }

    /**
     * Met à jour la signature pour un dossier donné
     *
     * @param session
     * @param dossier
     *            DocumentModel - Document pour lequel la signature est à mettre
     *            à jour
     * @param reponse
     *            Reponse - La réponse existante
     *
     */
    private void updateSignatureFor(final CoreSession session, final DocumentModel dossierDoc, final Reponse reponse) {
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        final Reponse reponseDossier = dossier.getReponse(session);
        if (reponseDossier != null) {
            reponseDossier.setSignature(reponse.getSignature());
            reponseDossier.setIsSignatureValide(reponse.getIsSignatureValide());
            // Ajout de l'auteur du retrait de la signature le cas échéant
            reponseDossier.setAuthorRemoveSignature(reponse.getAuthorRemoveSignature());
            session.saveDocument(reponseDossier.getDocument());
        }
    }

    private FeuilleRoute getLastRoutefromReponse(final CoreSession session, final Reponse reponse) {
        final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(
            session,
            DossierConstants.DOSSIER_SCHEMA
        );
        final DocumentModel dossierDoc = uGet.getByRef(reponse.getDocument().getParentRef());
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        if (!dossier.hasFeuilleRoute()) {
            return null;
        }

        uGet.setPrefetchInfo(SCHEMA_FEUILLE_ROUTE_STEP_FOLDER, SCHEMA_FEUILLE_ROUTE_INSTANCE);
        final DocumentModel routeDirectriceDocumentModel = uGet.getByRef(new IdRef(dossier.getLastDocumentRoute()));
        return routeDirectriceDocumentModel.getAdapter(FeuilleRoute.class);
    }

    @Override
    public List<Question> getQuestionAlloties(final CoreSession session, final Allotissement allotissement) {
        List<String> idsQuestions = getQuestionAllotiesIds(session, allotissement);
        DocumentModelList questions = retrieveDocuments(session, QUESTION_DOCUMENT_TYPE, idsQuestions);
        Collections.sort(questions, keepOGOrderComparator(idsQuestions, DocumentModel::getId));
        return DocUtil.adapt(questions, Question.class);
    }

    /**
     * Récupère la liste d'ids de Question de l'allotissement en concervant l'ordre
     * correspondant aux ids de dossiers
     *
     * @param session CoreSession
     * @param allotissement allotissement
     * @return liste d'ids de Questions
     */
    private List<String> getQuestionAllotiesIds(final CoreSession session, final Allotissement allotissement) {
        List<String> idDossiers = allotissement.getIdDossiers();
        if (CollectionUtils.isEmpty(idDossiers)) {
            return Collections.emptyList();
        }

        String questionMarks = StringHelper.getQuestionMark(idDossiers.size());
        String query = format(
            "Select distinct d.ecm:uuid as did, d.dos:idDocumentQuestion AS qid From Dossier AS d WHERE testAcl(d.dos:idDocumentQuestion) = 1 AND d.ecm:uuid IN (%s)",
            questionMarks
        );
        return QueryUtils
            .doUFNXQLQueryAndMapping(
                session,
                query,
                idDossiers.toArray(new String[0]),
                (Map<String, Serializable> rowData) ->
                    new ImmutablePair<>((String) rowData.get("did"), (String) rowData.get("qid"))
            )
            .stream()
            .sorted(keepOGOrderComparator(idDossiers, ImmutablePair::getLeft))
            .map(ImmutablePair::getRight)
            .collect(toList());
    }

    /**
     * Comparator de 2 items qui se base sur l'ordre des ces items dans une
     * liste initiale
     */
    private <T> Comparator<T> keepOGOrderComparator(List<String> ids, Function<T, String> func) {
        return Comparator.comparingInt(t -> ids.indexOf(func.apply(t)));
    }

    @Override
    public List<Question> getQuestionAllotiesWithOrderOrigineNumero(
        final CoreSession session,
        final Allotissement allotissement
    ) {
        List<String> idQuestions = getQuestionAllotiesIds(session, allotissement);

        final StringBuilder questionQuery = new StringBuilder("SELECT q.ecm:uuid AS id FROM ");
        questionQuery.append(DossierConstants.QUESTION_DOCUMENT_TYPE);
        questionQuery.append(" AS q ");
        questionQuery.append(
            String.format(" WHERE q.ecm:uuid IN (%s) ", StringHelper.getQuestionMark(idQuestions.size()))
        );
        questionQuery.append(" ORDER BY q.qu:origineQuestion ASC, q.qu:numeroQuestion ASC ");
        final List<DocumentModel> questionDocs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            DossierConstants.QUESTION_DOCUMENT_TYPE,
            questionQuery.toString(),
            idQuestions.toArray(new Object[0])
        );

        return DocUtil.adapt(questionDocs, Question.class);
    }

    @Override
    public void removeDossierFromLotIfNeeded(final CoreSession session, final Dossier dossier)
        throws AllotissementException {
        if (dossier == null) {
            return;
        }
        if (isAllotit(dossier)) {
            try {
                final Question question = dossier.getQuestion(session);
                final List<Question> questions = new ArrayList<>();
                questions.add(question);
                updateLot(question, questions, session, TypeAllotissement.SUPPR);
                LOGGER.info(
                    ReponsesLogEnumImpl.DEL_ALLOT_TEC,
                    "Dossier " +
                    dossier.getDocument().getId() +
                    " :  " +
                    dossier.getNumeroQuestion() +
                    " : " +
                    " retiré de son lot, nouvelle feuille de route : " +
                    dossier.getLastDocumentRoute()
                );
            } catch (final NuxeoException e) {
                LOGGER.error(
                    session,
                    ReponsesLogEnumImpl.DEL_ALLOT_TEC,
                    String.format(
                        "Impossible de retirer le dossier %s de son lot %s",
                        dossier.getDocument().getId(),
                        dossier.getDossierLot()
                    )
                );
                throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "Retrait dossier impossible");
            }
        }
    }

    @Override
    public boolean createLotWS(final Question question, final List<Question> listQuestions, final CoreSession session) {
        final NuxeoPrincipal currentUser = session.getPrincipal();
        boolean error = false;
        try {
            RepositoryManager repositoryManager = STServiceLocator.getRepositoryManager();
            CoreInstance.doPrivileged(
                repositoryManager.getDefaultRepositoryName(),
                privilegedSession -> {
                    try {
                        createLotUnRestricted(question, listQuestions, privilegedSession, currentUser);
                    } catch (final Exception e) {
                        sendErrorMailLotToAdmin(session, question, listQuestions, e);
                    }
                }
            );
        } catch (final NuxeoException e) {
            LOGGER.error(session, ReponsesLogEnumImpl.DEL_ALLOT_TEC, e);
            error = true;
        }
        return !error;
    }

    /**
     * Envoie du mail d'erreur aux admin fonctionnels
     *
     * @param session
     * @param questionDirectrice
     * @param listQuestions
     * @param exceptionMessage
     */
    protected void sendErrorMailLotToAdmin(
        final CoreSession session,
        final Question questionDirectrice,
        final List<Question> listQuestions,
        final Throwable throwable
    ) {
        final StringBuilder email = new StringBuilder("Question : ");
        email
            .append(questionDirectrice.getTypeQuestion())
            .append(" ")
            .append(questionDirectrice.getOrigineQuestion())
            .append(" ")
            .append(questionDirectrice.getNumeroQuestion());
        email.append("\nQuestion de rappel : ");
        for (final Question question : listQuestions) {
            email
                .append(question.getTypeQuestion())
                .append(" ")
                .append(question.getOrigineQuestion())
                .append(" ")
                .append(question.getNumeroQuestion())
                .append("\n");
        }

        LOGGER.info(session, STLogEnumImpl.SEND_MAIL_TEC, "Send mail : \n" + email.toString(), throwable);

        try {
            final ProfileService profileService = STServiceLocator.getProfileService();
            final List<STUser> users = profileService.getUsersFromBaseFunction(
                STBaseFunctionConstant.ADMIN_FONCTIONNEL_EMAIL_RECEIVER
            );

            final String text = email.toString() + "\n" + throwable.getMessage();
            final String object = "[Réponses] Allotissement de question impossible";

            STServiceLocator.getSTMailService().sendMailToUserList(users, object, text);
        } catch (final Exception e1) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                "Erreur d'envoi du mail lors de l'allotissement",
                e1
            );
        }
    }

    @Override
    public boolean validateDossierRappel(
        final CoreSession session,
        final Dossier dossierRappele,
        final String idMinistereQuestion,
        final Question questionRappel
    ) {
        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();

        try {
            // Test de l'état de la question rappelée
            final Question questionRappele = dossierRappele.getQuestion(session);
            if (questionRappele != null) {
                final QuestionStateChange quState = questionRappele.getEtatQuestion(session);
                if (quState != null && quState.getNewState().equals(VocabularyConstants.ETAT_QUESTION_REPONDU)) {
                    throw new AllotissementException(
                        AllotissementExceptionRaison.ERROR_ETAT_DOSSIER,
                        "Le dossier " +
                        dossierRappele.getNumeroQuestion() +
                        " est à l'état répondu, allotissement impossible."
                    );
                }
            }

            // Test de l'état de la feuille de route
            new UnrestrictedSessionRunner(session) {

                @Override
                public void run() {
                    // Test de la présence de la feuille de route
                    if (!dossierRappele.hasFeuilleRoute()) {
                        throw new AllotissementException(
                            AllotissementExceptionRaison.ERROR_FDR,
                            "Le dossier " +
                            dossierRappele.getNumeroQuestion() +
                            " n'a pas de feuille de route, allotissement impossible."
                        );
                    }

                    // Allotissement possible si le poste de l'etape courante
                    // est relié au ministere attributaire
                    final List<DocumentModel> listDossierLinkDirecteurDoc = corbeilleService.findDossierLink(
                        session,
                        dossierRappele.getDocument().getId()
                    );

                    final String ministereAttributaireDirecteur = dossierRappele.getIdMinistereAttributaireCourant();

                    DossierLink dossierLink = null;
                    for (final DocumentModel documentModel : listDossierLinkDirecteurDoc) {
                        dossierLink = documentModel.getAdapter(DossierLink.class);
                        if (dossierLink.isActionnable()) {
                            final DocumentModel currentStepDoc = session.getDocument(
                                new IdRef(dossierLink.getRoutingTaskId())
                            );
                            final ReponsesRouteStep currentStep = currentStepDoc.getAdapter(ReponsesRouteStep.class);

                            final List<EntiteNode> organigrammeList = mailboxPosteService.getMinistereListFromMailbox(
                                currentStep.getDistributionMailboxId()
                            );

                            final Set<String> ministereIds = new HashSet<>();
                            for (final EntiteNode entiteNode : organigrammeList) {
                                ministereIds.add(entiteNode.getId().toString());
                            }

                            if (
                                StringUtils.isBlank(ministereAttributaireDirecteur) ||
                                !ministereIds.contains(ministereAttributaireDirecteur)
                            ) {
                                throw new AllotissementException(
                                    AllotissementExceptionRaison.ERROR_FDR,
                                    "L'étape courante du dossier " +
                                    dossierRappele.getNumeroQuestion() +
                                    " n'est pas lié à son ministère attributaire, allotissement impossible."
                                );
                            }
                        }
                    }

                    // Test de la présence d'une étape active
                    if (dossierLink == null) {
                        throw new AllotissementException(
                            AllotissementExceptionRaison.ERROR_FDR,
                            "La feuille de route directrice est vide ou terminée, allotissement impossible."
                        );
                    }

                    // Allotissement possible si la question et le dossier
                    // rappelé ont le même ministère attributaire
                    if (
                        StringUtils.isBlank(ministereAttributaireDirecteur) ||
                        !ministereAttributaireDirecteur.equals(idMinistereQuestion)
                    ) {
                        throw new AllotissementException(
                            AllotissementExceptionRaison.ERROR_MINISTERE,
                            "Le dossier rappelé " +
                            dossierRappele.getNumeroQuestion() +
                            " n'est pas attribué au même ministère que la question créée, allotissement impossible."
                        );
                    }
                }
            }
                .runUnrestricted();
        } catch (final Exception e) {
            sendErrorMailLotToAdmin(
                session,
                dossierRappele.getQuestion(session),
                Collections.singletonList(questionRappel),
                e
            );
            return false;
        }

        return true;
    }

    @Override
    public void createOrAddToLotRappel(
        final Question questionDirectrice,
        final Question questionRappel,
        final CoreSession session
    ) {
        final STLockService stLockService = STServiceLocator.getSTLockService();
        final JournalService journalService = STServiceLocator.getJournalService();
        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
        final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();

        try {
            new UnrestrictedSessionRunner(session) {

                @Override
                public void run() {
                    DocumentModel dossierDirecteurDoc = null;
                    DocumentModel routeDirectriceDoc = null;
                    try {
                        dossierDirecteurDoc = session.getDocument(questionDirectrice.getDossierRef());
                        Dossier dossierDirecteur = dossierDirecteurDoc.getAdapter(Dossier.class);

                        routeDirectriceDoc = session.getDocument(new IdRef(dossierDirecteur.getLastDocumentRoute()));
                        final FeuilleRoute routeDirectrice = routeDirectriceDoc.getAdapter(FeuilleRoute.class);

                        // Allotissement possible si le poste de l'etape
                        // courante est relié au ministere attributaire
                        final List<DocumentModel> listDossierLinkDirecteurDoc = corbeilleService.findDossierLink(
                            session,
                            dossierDirecteurDoc.getId()
                        );

                        // force unlock
                        stLockService.unlockDoc(session, routeDirectriceDoc);
                        stLockService.unlockDoc(session, dossierDirecteurDoc);

                        stLockService.lockDoc(session, routeDirectriceDoc);
                        stLockService.lockDoc(session, dossierDirecteurDoc);

                        // Nouvelle feuille de route pour la question de rappel,
                        // celle du lot
                        final DocumentModel dossierRappelDoc = session.getDocument(questionRappel.getDossierRef());
                        final Dossier dossierRappel = dossierRappelDoc.getAdapter(Dossier.class);
                        dossierRappel.setLastDocumentRoute(routeDirectriceDoc.getId());
                        session.saveDocument(dossierRappel.getDocument());

                        // On duplique les droits du dossier directeur dans le
                        // dossier
                        final ACP dossierDirecteurAcp = dossierDirecteur.getDocument().getACP();
                        session.setACP(dossierRappel.getDocument().getRef(), dossierDirecteurAcp, true);
                        session.save();

                        for (final DocumentModel dossierLinkDirecteurDoc : listDossierLinkDirecteurDoc) {
                            final DossierLink dossierLinkDirecteur = dossierLinkDirecteurDoc.getAdapter(
                                DossierLink.class
                            );

                            // On duplique le caseLinkDirecteur et on le
                            // rattache au dossier courant
                            DocumentModel dossierLinkDirecteurCopyDoc = session.copy(
                                dossierLinkDirecteur.getDocument().getRef(),
                                dossierLinkDirecteur.getDocument().getParentRef(),
                                dossierLinkDirecteurDoc.getName()
                            );
                            dossierLinkDirecteurCopyDoc.setProperty(
                                "case_link",
                                "caseDocumentId",
                                dossierRappelDoc.getId()
                            );
                            final Calendar now = Calendar.getInstance();
                            dossierLinkDirecteurCopyDoc.setProperty("case_link", "date", now);
                            DublincoreSchemaUtils.setModifiedDate(dossierLinkDirecteurCopyDoc, now);
                            DublincoreSchemaUtils.setTitle(
                                dossierLinkDirecteurCopyDoc,
                                "Dossier n°" + dossierRappel.getNumeroQuestion()
                            );
                            dossierLinkDirecteurCopyDoc = session.saveDocument(dossierLinkDirecteurCopyDoc);

                            // Mise à jour des champs du dossierLink
                            dossierDistributionService.setDossierLinksFields(session, dossierLinkDirecteurCopyDoc);

                            // on duplique les droits du dossierLink directeur
                            // dans le dossierLink copié
                            final ACP dossierLinkDirecteurAcp = dossierLinkDirecteur.getDocument().getACP();
                            session.setACP(dossierLinkDirecteurCopyDoc.getRef(), dossierLinkDirecteurAcp, true);
                            session.saveDocument(dossierLinkDirecteurCopyDoc);
                        }

                        // Gestion allotissement
                        Allotissement allotissement = getAllotissement(dossierDirecteur.getDossierLot(), session);
                        // On ajoute le dossier sur la feuille de route du
                        // dossier directeur
                        final List<String> listDossierIds = new ArrayList<>(routeDirectrice.getAttachedDocuments());
                        if (!listDossierIds.contains(dossierRappelDoc.getId())) {
                            listDossierIds.add(dossierRappelDoc.getId());
                        }
                        // Si l'allotissement est null, on en créé un nouveau
                        if (allotissement == null) {
                            // creation de l'allotssement
                            allotissement = createAllotissementDoc(session, listDossierIds);
                            dossierDirecteur.setDossierLot(allotissement.getNom());
                            dossierDirecteurDoc = session.saveDocument(dossierDirecteurDoc);
                            dossierDirecteur = dossierDirecteurDoc.getAdapter(Dossier.class);
                        } else {
                            // Sinon, on met à jour l'existant avec le dossier
                            // de rappel
                            dossierRappel.setDossierLot(allotissement.getNom());
                            session.saveDocument(dossierRappel.getDocument());
                            allotissement.setIdDossiers(listDossierIds);
                            session.saveDocument(allotissement.getDocument());
                            session.save();
                        }

                        final StringBuilder sb = prepareIdsDossierForLog(session, allotissement.getIdDossiers());
                        journalService.journaliserActionParapheur(
                            session,
                            dossierRappel.getDocument(),
                            ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT,
                            "Dossier ajouté au lot" + sb.toString()
                        );

                        session.save();

                        // On link tous les dossiers à la feuille de route
                        // directrice
                        routeDirectrice.setAttachedDocuments(listDossierIds);
                        session.saveDocument(routeDirectriceDoc);

                        reponseService.saveReponse(
                            session,
                            dossierDirecteur.getReponse(session).getDocument(),
                            dossierDirecteur.getDocument()
                        );

                        session.save();
                    } catch (final Exception e) {
                        LOGGER.error(session, ReponsesLogEnumImpl.FAIL_CREATE_ALLOT_TEC, e);
                        sendErrorMailLotToAdmin(
                            session,
                            questionDirectrice,
                            Collections.singletonList(questionRappel),
                            e
                        );
                    } finally {
                        if (routeDirectriceDoc != null) {
                            stLockService.unlockDoc(session, routeDirectriceDoc);
                        }
                        if (dossierDirecteurDoc != null) {
                            stLockService.unlockDoc(session, dossierDirecteurDoc);
                        }
                    }
                }
            }
                .runUnrestricted();
        } catch (final NuxeoException e) {
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_CREATE_ALLOT_TEC, e);
        }
    }

    @Override
    public Set<String> extractDossiersDirecteurs(List<DocumentModel> listDossiersDoc, CoreSession session) {
        Set<String> lstDossiersDirecteurs = new TreeSet<>();
        Set<String> nomAllotissement = new TreeSet<>();
        List<DocumentModel> lstAllotissementModel = null;

        for (DocumentModel dossierDoc : listDossiersDoc) {
            Dossier doss = dossierDoc.getAdapter(Dossier.class);
            if (doss != null && StringUtils.isNotBlank(doss.getDossierLot())) {
                nomAllotissement.add(String.format("'%s'", doss.getDossierLot()));
            }
        }

        // Si on a bien des dossiers allotis alors on va récupérer le premier
        // dossier de chaque lot qui est donc le
        // directeur
        // Pas besoin de faire quoi que ce soit si on n'a pas de dossier allotis
        if (!nomAllotissement.isEmpty()) {
            Joiner joiner = Joiner.on(",").skipNulls();
            StringBuilder query = new StringBuilder("SELECT a.ecm:uuid as id FROM ");
            query.append(DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE);
            query.append(String.format(" as a WHERE a.allot:nom IN (%s) ", joiner.join(nomAllotissement)));
            Object[] params = new Object[] {};
            lstAllotissementModel =
                QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                    session,
                    DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE,
                    query.toString(),
                    params
                );

            for (DocumentModel allotDoc : lstAllotissementModel) {
                Allotissement allot = allotDoc.getAdapter(Allotissement.class);

                // On vérifie qd même qu'on a bien des dossiers
                if (allot.getIdDossiers() != null && !allot.getIdDossiers().isEmpty()) {
                    lstDossiersDirecteurs.add(allot.getIdDossiers().get(0));
                }
            }
        }

        return lstDossiersDirecteurs;
    }
}
