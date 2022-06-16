package fr.dila.reponses.ui.services.dossier;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.enums.MassActionType;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesDocumentRoutingActionService;
import fr.dila.reponses.ui.services.actions.ReponsesDossierDistributionActionService;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Bean de gestion des actions de masse
 *
 */
public class DossierMassUIServiceImpl implements DossierMassUIService {
    private static final STLogger LOGGER = STLogFactory.getLog(DossierMassUIServiceImpl.class);
    protected static final String ERROR_ETAPE = "dossier.action.masse.error.etape";
    protected static final String ERROR_OCCURRED = "feedback.reponses.traitement.error";
    protected static final String ERROR_ETAPE_PARALLELE = "dossier.action.masse.error.etapeParallele";
    protected static final String ERROR_ADD_STEP = "feedback.reponses.dossier.error.cannotAddStepToRoute";
    protected static final String ERROR_FDR_NOT_FOUND = "feedback.reponses.dossier.error.noRouteFound";
    protected static final String ERROR_DOSSIER_LOCK = "dossier.action.masse.error.dossier.lock";
    protected static final String ERROR_DOSSIER_ISLOCKED = "dossier.action.masse.error.dossier.isLocked";
    protected static final String ERROR_DOSSIER_RIGHT = "dossier.action.masse.error.dossier.right";
    protected static final String INFO_DIRECTION_PILOTE = "dossier.action.masse.info.direction.pilote";

    protected int doneMassDocument = 0;
    protected MassActionType massActionType;
    protected Set<Dossier> dossiersOk = new HashSet<>();
    protected Set<Dossier> dossiersEnErreur = new HashSet<>();
    protected Set<String> infosErreursMassActions = new HashSet<>();

    @Override
    public void masseFdrActionDonnerAvisFavorable(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_AVIS_FAVORABLE);
    }

    @Override
    public void masseFdrActionDonnerAvisDefavorableEtInsererTaches(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_AVIS_DEFAVORABLE_ET_INSERER_TACHES);
    }

    @Override
    public void masseFdrActionDonnerAvisDefavorableEtPoursuivre(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_AVIS_DEFAVORABLE_ET_POURSUIVRE);
    }

    @Override
    public void masseFdrActionNonConcerneReattribution(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_NON_CONCERNE_REATTRIBUTION);
    }

    @Override
    public void masseFdrActionReattributionDirecte(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_REATTRIBUTION_DIRECTE);
    }

    @Override
    public void masseFdrActionNonConcerneReorientation(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_NON_CONCERNE_REORIENTATION);
    }

    @Override
    public void masseFdrActionNonConcerneReaffectation(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_NON_CONCERNE_REAFFECTATION);
    }

    @Override
    public void masseFdrActionDemandeArbitrageSGG(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_DEMANDE_ARBITRAGE_SGG);
    }

    @Override
    public void masseActionModificationMinistereRattachement(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT);
    }

    @Override
    public void masseActionModificationDirectionPilote(SpecificContext context) {
        processMassAction(context, MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE);
    }

    /**
     * Vérifie que l'action est compatible avec l'étape en cours
     *
     * @param massActionType
     * @param dossierLink
     * @return
     */
    private boolean isActionCompatibleWithStep(MassActionType massActionType, DossierLink dossierLink) {
        String type = dossierLink.getRoutingTaskType();

        // Si l'étape est "pour arbitrage" seule l'action de masse "valider" est
        // compatible
        if (
            VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(type) &&
            MassActionType.MASSE_ACTION_AVIS_FAVORABLE != massActionType
        ) {
            return false;
        }

        // Si l'étape est "pour réattribution", seules les actions de masse
        // "valider" et "demande arbitrage sgg" sont compatibles
        return (
            !VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(type) ||
            MassActionType.MASSE_ACTION_AVIS_FAVORABLE == massActionType ||
            MassActionType.MASSE_ACTION_DEMANDE_ARBITRAGE_SGG == massActionType
        );
    }

    /**
     * Lance la procédure d'action de masse : intialise la liste des dossiers sélectionnés, vérifie qu'elle n'est pas
     * null lance le parcourt des dossier, vérifie la feuille de route (au besoin ; voir checkFdrDossier) déverrouille
     * le dossier (au besoin ; voir unlockDocIfLocked) puis parcourt les dossier link accessibles l'utilisateur et liés
     * au dossier pour procéder à l'action de masse selectionnée (voir processSpecificMassAction)
     *
     * @param action
     */
    private void processMassAction(SpecificContext context, final MassActionType action) {
        CoreSession session = context.getSession();
        List<String> dossierIds = context.getFromContextData(ReponsesContextDataKey.DOSSIER_IDS);
        try {
            // Initialisation de la liste des dossiers à reattribuer
            initMasseFdrAction(context, isCheckAllotNecessary(action), dossierIds);
            // Si l'utilisateur peut utiliser la fonctionnalité
            for (final Dossier dossier : dossiersOk) {
                // Set du contexte courant sur les documents de travail
                DocumentModel dossierDoc = dossier.getDocument();
                context.setCurrentDocument(dossierDoc);
                List<DocumentModel> dossierLinkDocs = ReponsesServiceLocator
                    .getCorbeilleService()
                    .findUpdatableDossierLinkForDossier(session, dossier.getDocument());
                if (checkBeforeProcess(context, action, dossier, dossierLinkDocs)) {
                    for (final DocumentModel dossierLinkDoc : dossierLinkDocs) {
                        // On charge le dossier link
                        final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
                        if (dossierLink != null && isActionCompatibleWithStep(action, dossierLink)) {
                            processSpecificMassAction(context, action, dossier, dossierLinkDoc);
                        } else {
                            addDossierInErrorList(dossier, ERROR_OCCURRED);
                            LOGGER.info(session, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, dossierLinkDoc);
                            break;
                        }
                    }
                }
            }
            doneMassDocument++;
            endMassAction(context);
        } catch (final Exception exc) {
            infosErreursMassActions.add(exc.getMessage());
            rollbackMassAction(context, exc);
            throw new NuxeoException(exc);
        }
    }

    private boolean checkBeforeProcess(
        SpecificContext context,
        MassActionType action,
        Dossier dossier,
        List<DocumentModel> dossierLinkDocs
    ) {
        CoreSession session = context.getSession();
        return (
            checkFdrDossier(session, action, dossier) &&
            checkDossierNotLockByOtherUser(context, dossier) &&
            checkCanLockDoc(session, dossier) &&
            checkMultipleDossierLink(session, action, dossier, dossierLinkDocs)
        );
    }

    /**
     * Lance l'action sur le dossier et dossier link en paramètre
     *
     * @param action
     * @param dossier
     * @param dossierLinkDoc
     * @throws ClientException
     */
    private void processSpecificMassAction(
        SpecificContext context,
        final MassActionType action,
        final Dossier dossier,
        final DocumentModel dossierLinkDoc
    ) {
        CoreSession session = context.getSession();
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkDoc.getId());
        DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
        ReponsesDossierDistributionActionService dossierDistributionActions = ReponsesActionsServiceLocator.getReponsesDossierDistributionActionService();
        ReponseFeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        DossierDistributionService distributionService = ReponsesServiceLocator.getDossierDistributionService();
        ReponsesDocumentRoutingActionService documentRoutingActionService = ReponsesActionsServiceLocator.getReponsesDocumentRoutingActionService();

        switch (action) {
            case MASSE_ACTION_AVIS_FAVORABLE:
                dossierDistributionActions.donnerAvisFavorable(context);
                break;
            case MASSE_ACTION_AVIS_DEFAVORABLE_ET_INSERER_TACHES:
                if (!documentRoutingActionService.isFirstStepInBranchOrParallel(context)) {
                    STActionsServiceLocator
                        .getDossierLockActionService()
                        .lockDossier(context, session, dossier.getDocument());
                    dossierDistributionActions.donnerAvisDefavorableEtInsererTaches(context);
                } else {
                    addDossierInErrorList(dossier, ERROR_ETAPE);
                }
                break;
            case MASSE_ACTION_AVIS_DEFAVORABLE_ET_POURSUIVRE:
                dossierDistributionActions.donnerAvisDefavorableEtPoursuivre(context);
                break;
            case MASSE_ACTION_NON_CONCERNE_REATTRIBUTION:
                if (
                    !feuilleRouteService.isNextStepReorientationOrReattributionOrArbitrage(
                        session,
                        dossierLink.getRoutingTaskId()
                    ) &&
                    feuilleRouteService.isRootStep(session, dossierLink.getRoutingTaskId())
                ) {
                    STActionsServiceLocator
                        .getDossierLockActionService()
                        .lockDossier(context, session, dossier.getDocument());
                    try {
                        if (!dossierDistributionActions.nonConcerneReattribution(context)) {
                            addDossierInErrorList(dossier, ERROR_OCCURRED);
                        }
                    } finally {
                        STActionsServiceLocator.getDossierLockActionService().unlockDossier(context);
                    }
                } else {
                    addDossierInErrorList(dossier, ERROR_ETAPE);
                }
                break;
            case MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT:
            case MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE:
                if (!processModifyMinistereOrDirection(context, action)) {
                    addDossierInErrorList(dossier, ERROR_OCCURRED);
                }
                break;
            case MASSE_ACTION_NON_CONCERNE_REAFFECTATION:
                if (
                    !feuilleRouteService.isNextStepReorientationOrReattributionOrArbitrage(
                        session,
                        dossierLink.getRoutingTaskId()
                    ) &&
                    feuilleRouteService.isRootStep(session, dossierLink.getRoutingTaskId())
                ) {
                    // On ne valide l'étape que si la réattribution a pu se faire
                    STActionsServiceLocator
                        .getDossierLockActionService()
                        .lockDossier(context, session, dossier.getDocument());
                    try {
                        if (dossierDistributionActions.nonConcerneReattribution(context)) {
                            distributionService.addCommentAndStepForReaffectation(session, dossier.getDocument());
                        } else {
                            addDossierInErrorList(dossier, ERROR_OCCURRED);
                        }
                    } finally {
                        STActionsServiceLocator.getDossierLockActionService().unlockDossier(context);
                    }
                } else {
                    addDossierInErrorList(dossier, ERROR_ETAPE);
                }
                break;
            case MASSE_ACTION_NON_CONCERNE_REORIENTATION:
                if (
                    !feuilleRouteService.isNextStepReorientationOrReattributionOrArbitrage(
                        session,
                        dossierLink.getRoutingTaskId()
                    ) &&
                    feuilleRouteService.isRootStep(session, dossierLink.getRoutingTaskId())
                ) {
                    dossierDistributionActions.nonConcerneReorientation(context);
                } else {
                    addDossierInErrorList(dossier, ERROR_ETAPE);
                }
                break;
            case MASSE_ACTION_DEMANDE_ARBITRAGE_SGG:
                if (!dossierDistributionActions.demandeArbitrageSGG(context)) {
                    addDossierInErrorList(dossier, ERROR_ADD_STEP);
                }
                break;
            case MASSE_ACTION_REATTRIBUTION_DIRECTE:
                ReponsesArbitrageService arbitrageService = ReponsesServiceLocator.getReponsesArbitrageService();
                if (!arbitrageService.isStepPourArbitrage(dossierLink)) {
                    String reattributionMinistere = context.getFromContextData(STContextDataKey.MINISTERE_ID);
                    String reattributionObservations = context.getFromContextData(SSContextDataKey.COMMENT_CONTENT);
                    try {
                        arbitrageService.reattributionDirecte(
                            session,
                            dossierLink,
                            dossier.getDocument(),
                            reattributionMinistere,
                            reattributionObservations
                        );
                        context
                            .getMessageQueue()
                            .addInfoToQueue(
                                ResourceHelper.getString("feedback.reponses.dossier.info.reattributionDirect")
                            );
                    } catch (ReponsesException e) {
                        addDossierInErrorList(dossier, e.getMessage());
                    }
                } else {
                    addDossierInErrorList(dossier, ERROR_ETAPE);
                }
                break;
            default:
                addDossierInErrorList(dossier, ERROR_OCCURRED);
                break;
        }
    }

    private boolean processModifyMinistereOrDirection(SpecificContext context, MassActionType action) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        CoreSession session = context.getSession();
        LockUtils.lockIfNeeded(session, dossierDoc.getRef());
        Question question = getQuestionFromCurrentDossier(context);
        DossierDistributionService distributionService = ReponsesServiceLocator.getDossierDistributionService();
        try {
            if (MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT.equals(action)) {
                String selectedMinForRattachement = context.getFromContextData(STContextDataKey.MINISTERE_ID);
                if (!checkObligatoryStringParameters(selectedMinForRattachement)) {
                    infosErreursMassActions.add("feedback.reponses.dossier.error.selectMin");
                    return false;
                }
                distributionService.setMinistereRattachement(session, question, selectedMinForRattachement);
            } else if (MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE.equals(action)) {
                String selectedDirectionPilote = context.getFromContextData(STContextDataKey.DIRECTION_ID);
                if (!checkObligatoryStringParameters(selectedDirectionPilote)) {
                    infosErreursMassActions.add("feedback.reponses.dossier.error.selectDirectionPilote");
                    return false;
                }
                distributionService.setDirectionPilote(session, question, selectedDirectionPilote);
                context
                    .getMessageQueue()
                    .addInfoToQueue(ResourceHelper.getString(INFO_DIRECTION_PILOTE, question.getNumeroQuestion()));
            }
            session.saveDocument(question.getDocument());
        } finally {
            LockUtils.unlockDocument(session, dossierDoc.getRef());
        }
        return true;
    }

    /**
     * En cas d'erreur dans l'action de masse, marque la transaction en rollback, affiche un message à l'utilisateur, et
     * log l'exception
     *
     * @param exc
     */
    private void rollbackMassAction(SpecificContext context, Exception exc) {
        TransactionHelper.setTransactionRollbackOnly();
        LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_PROCESS_MASS_FONC, exc);
    }

    /**
     * Initialise les variables avant une action de masse
     *
     * @return la selectionList utilisée
     */
    private void initMasseFdrAction(SpecificContext context, boolean checkAllotissement, List<String> dossierIds) {
        doneMassDocument = 0;
        dossiersOk = new HashSet<>();
        dossiersEnErreur = new HashSet<>();
        infosErreursMassActions = new HashSet<>();

        if (CollectionUtils.isNotEmpty(dossierIds)) {
            initDossierList(context, dossierIds, checkAllotissement);
        } else {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("feedback.reponses.dossier.error.selectedDossier"));
            throw new NuxeoException();
        }
    }

    /**
     * finalise l'action de masse. Reinitialise la workinglist, reset du document courant, et affiche un message
     * d'erreur à l'utilisateur pour les dossiers qui ont été en erreur. Vide les listes des dossiers traités et en
     * erreur
     *
     * @param selectionList
     */
    private void endMassAction(SpecificContext context) {
        CoreSession session = context.getSession();
        // Message de compte rendu en cas d'erreur
        if (CollectionUtils.isNotEmpty(dossiersEnErreur)) {
            final List<String> messages = new ArrayList<>();
            for (final Dossier dossier : dossiersEnErreur) {
                messages.add(dossier.getQuestion(session).getSourceNumeroQuestion());
            }
            context.getMessageQueue().addWarnToQueue("Dossiers en erreur : " + StringUtils.join(messages, ", "));
        }

        if (!infosErreursMassActions.isEmpty()) {
            StringBuilder messagesErreur = new StringBuilder();
            for (final String erreur : infosErreursMassActions) {
                messagesErreur.append(ResourceHelper.getString(erreur)).append("\n");
            }
            context.getMessageQueue().addWarnToQueue(messagesErreur.toString());
        }
    }

    /**
     * Vérifie la présence d'une feuille de route dans un dossier si l'action est différente d'une modification de
     * ministère de rattachement ou direction pilote (ces deux actions ne nécessitent pas une feuille de route)
     */
    private boolean checkFdrDossier(CoreSession session, MassActionType action, Dossier dossier) {
        if (
            action != MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT &&
            action != MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE &&
            Boolean.FALSE.equals(dossier.hasFeuilleRoute())
        ) {
            LOGGER.warn(session, SSLogEnumImpl.FAIL_GET_FDR_FONC, dossier.getDocument());
            addDossierInErrorList(dossier, ERROR_FDR_NOT_FOUND);
            return false;
        }

        // Un dossier arbitré ne peut pas faire l'objet d'un nouvel arbitrage ou
        // reattribution
        if (
            Boolean.TRUE.equals(dossier.isArbitrated()) &&
            (
                action == MassActionType.MASSE_ACTION_DEMANDE_ARBITRAGE_SGG ||
                action == MassActionType.MASSE_ACTION_NON_CONCERNE_REATTRIBUTION ||
                action == MassActionType.MASSE_ACTION_NON_CONCERNE_REATTRIBUTION_PLAN_CLASSEMENT
            )
        ) {
            LOGGER.warn(session, ReponsesLogEnumImpl.FAIL_ADD_STEP_ARBITRAGE_FONC, dossier.getDocument());
            addDossierInErrorList(dossier, "label.responses.dossier.toolbar.arbitrageDossierIndisponible");
            return false;
        }

        // Un dossier non arbitré ne peut pas faire l'objet d'une réattribution
        // directe
        if (
            Boolean.FALSE.equals(dossier.isArbitrated()) && action == MassActionType.MASSE_ACTION_REATTRIBUTION_DIRECTE
        ) {
            LOGGER.warn(session, SSLogEnumImpl.FAIL_REATTR_DOSSIER_FONC, dossier.getDocument());
            addDossierInErrorList(dossier, "feedback.reponses.dossier.error.cannotReattributionDirecte");
            return false;
        }
        return true;
    }

    /**
     * Vérifie que l'utilisateur n'a pas le droit de valider plusieurs étape en même temps (parallèle)
     * pour les action suivante : DonnerAvisFavorable, DonnerAvisDéfavorableAvecRtour,
     * DonnerAvisDéfavorableEtPoursuivre, Réorientation
     * @param session
     * @param action
     * @param dossier
     * @return
     */
    private boolean checkMultipleDossierLink(
        CoreSession session,
        MassActionType action,
        Dossier dossier,
        List<DocumentModel> dossierLinkDocs
    ) {
        if (
            dossierLinkDocs.size() > 1 &&
            (
                MassActionType.MASSE_ACTION_AVIS_FAVORABLE == action ||
                MassActionType.MASSE_ACTION_AVIS_DEFAVORABLE_ET_INSERER_TACHES == action ||
                MassActionType.MASSE_ACTION_AVIS_DEFAVORABLE_ET_POURSUIVRE == action ||
                MassActionType.MASSE_ACTION_NON_CONCERNE_REORIENTATION == action
            )
        ) {
            addDossierInErrorList(dossier, ERROR_ETAPE_PARALLELE);
            LOGGER.warn(session, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, dossier.getDocument());
            return false;
        }
        return true;
    }

    /**
     * Vérifie que le dossier n'est pas verrouillé par un autre utilisateur
     */
    private boolean checkDossierNotLockByOtherUser(SpecificContext context, Dossier dossier) {
        CoreSession session = context.getSession();
        NuxeoPrincipal principal = context.getWebcontext().getPrincipal();
        DocumentModel dossierDoc = dossier.getDocument();
        STLockActionService lockActionService = STActionsServiceLocator.getSTLockActionService();
        if (
            dossierDoc.isLocked() &&
            !lockActionService.currentDocIsLockActionnableByCurrentUser(session, dossierDoc, principal)
        ) {
            addDossierInErrorList(dossier, ERROR_DOSSIER_LOCK);
            LOGGER.warn(session, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, dossier.getDocument());
            return false;
        }
        return true;
    }

    /**
     * Vérifie que l'utilisateur possède le droit de verrouiller le dossier
     */
    private boolean checkCanLockDoc(CoreSession session, Dossier dossier) {
        if (!STActionsServiceLocator.getSTLockActionService().getCanLockDoc(dossier.getDocument(), session)) {
            if (STServiceLocator.getSTLockService().isLocked(session, dossier.getDocument())) {
                addDossierInErrorList(dossier, ERROR_DOSSIER_ISLOCKED);
            } else {
                addDossierInErrorList(dossier, ERROR_DOSSIER_RIGHT);
            }
            LOGGER.warn(session, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, dossier.getDocument());
            return false;
        }
        return true;
    }

    /**
     * initialise la liste des dossiers à traiter pour l'action de masse.
     */
    protected void initDossierList(
        SpecificContext context,
        final List<String> dossiersIds,
        boolean checkAllotissement
    ) {
        CoreSession session = context.getSession();

        final Set<String> dossierATraiter = new HashSet<>();

        for (final String dossierId : dossiersIds) {
            final DocumentModel dossierDoc = session.getDocument(new IdRef(dossierId));
            final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            if (checkAllotissement) {
                if (!dossierATraiter.contains(dossier.getDocument().getId())) {
                    final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
                    final Allotissement allotissement = allotissementService.getAllotissement(
                        dossier.getDossierLot(),
                        session
                    );
                    if (allotissement != null) {
                        List<String> idDossiersAllot = allotissement.getIdDossiers();
                        if (CollectionUtils.isNotEmpty(idDossiersAllot)) {
                            dossierATraiter.addAll(idDossiersAllot);
                            // ajout du dossier directeur qui se trouve en premier dans la liste
                            dossiersOk.add(
                                QueryHelper.getDocument(session, idDossiersAllot.get(0)).getAdapter(Dossier.class)
                            );
                        }
                    } else {
                        dossierATraiter.add(dossier.getDocument().getId());
                        dossiersOk.add(dossier);
                    }
                }
            } else {
                dossierATraiter.add(dossier.getDocument().getId());
                dossiersOk.add(dossier);
            }
        }
    }

    /**
     * Vérifie les paramètres String obligatoires (l'id ministère par exemple)
     *
     * @return
     */
    private boolean checkObligatoryStringParameters(String... parameters) {
        for (String parameter : parameters) {
            if (StringUtils.isEmpty(parameter)) {
                infosErreursMassActions.add("feedback.reponses.error.missing.parameter");
                return false;
            }
        }
        return true;
    }

    /**
     * Ajoute un dossier à la liste des dossiers en erreur et l'origine de l'erreur
     *
     * @param dossier
     * @param resourcesMessage
     */
    private void addDossierInErrorList(Dossier dossier, String resourcesMessage) {
        dossiersEnErreur.add(dossier);
        infosErreursMassActions.add(ResourceHelper.getString(resourcesMessage));
    }

    private boolean isCheckAllotNecessary(MassActionType massActionType) {
        return (
            MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE != massActionType &&
            MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT != massActionType
        );
    }

    /**
     * Récupère la Question du currentDossier
     *
     * @return
     */
    private Question getQuestionFromCurrentDossier(SpecificContext context) {
        final DocumentModel dossierDoc = context.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        return dossier.getQuestion(context.getSession());
    }
}
