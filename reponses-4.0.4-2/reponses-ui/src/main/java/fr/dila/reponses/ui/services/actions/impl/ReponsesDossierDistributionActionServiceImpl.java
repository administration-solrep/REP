package fr.dila.reponses.ui.services.actions.impl;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getDossierDistributionService;
import static fr.dila.reponses.core.service.ReponsesServiceLocator.getReponsesArbitrageService;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.REFRESH_CORBEILLE_KEY;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static fr.dila.st.ui.services.actions.STActionsServiceLocator.getDossierLockActionService;
import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.exception.CopieStepException;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.exception.SignatureException;
import fr.dila.reponses.api.exception.StepValidationException;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.ReponseActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesDocumentRoutingActionService;
import fr.dila.reponses.ui.services.actions.ReponsesDossierDistributionActionService;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.exception.PosteNotFoundException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.enums.AlertType;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

public class ReponsesDossierDistributionActionServiceImpl implements ReponsesDossierDistributionActionService {
    private static final String LABEL_FEUILLE_ROUTE_VALIDATION_INTERNAL_ERROR =
        "label.reponses.feuilleRoute.validation.internal.error";

    public static final STLogger LOGGER = STLogFactory.getLog(ReponsesDossierDistributionActionService.class);

    public static final String SELECTED_MIN_REATTR = "selectedMinForReattribution";
    private static final String ERROR_ADD_STEP = "feedback.reponses.dossier.error.cannotAddStepToRoute";
    private static final String LABEL_ETAPE_VALIDATION_REATTRIBUTION =
        "label.reponses.feuilleRoute.message.etape.validation.reattribution";

    @Override
    public boolean nonConcerneReattribution(SpecificContext context) {
        String minReattribution = context.getFromContextData(STContextDataKey.MINISTERE_ID);

        if (StringUtils.isEmpty(minReattribution)) {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("feedback.reponses.error.missing.parameter"));
            return false;
        }

        final DocumentModel dossierDoc = context.getCurrentDocument();
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        if (BooleanUtils.isTrue(dossier.isArbitrated())) {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("feedback.reponses.dossier.error.cannotReattribution"));
            return false;
        }

        final CoreSession session = context.getSession();
        final DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        final DocumentModel etapeDoc = new UnrestrictedGetDocumentRunner(session, STSchemaConstant.ROUTING_TASK_SCHEMA)
        .getById(dossierLink.getRoutingTaskId());
        final SSRouteStep etape = etapeDoc.getAdapter(SSRouteStep.class);
        final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
        final ReponsesFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(ReponsesFeuilleRoute.class);

        // Là on indique qu'on a déjà cliqué une fois sur le bouton
        dossierLink.setDateDebutValidation(Calendar.getInstance());
        dossierLink.save(session);

        // Récupère le poste BDC du ministère, ajoute l'étape réattribution et
        // stocke le ministère choisi dans le dossier
        final OrganigrammeNode posteBdc = STServiceLocator.getSTPostesService().getPosteBdcInEntite(minReattribution);
        if (posteBdc == null) {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("feedback.reponses.dossier.error.reattributionNoBdc"));
            return false;
        }
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        final String mailboxId = mailboxPosteService.getPosteMailboxId(posteBdc.getId());
        mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
        ReponsesServiceLocator.getFeuilleRouteService().addStepAfterReattribution(session, dossierLink, mailboxId);

        // Changement du ministère courant des dossiers liés à la feuille de
        // route
        for (final String dossierId : feuilleRoute.getAttachedDocuments()) {
            final DocumentModel dossierAttachDoc = session.getDocument(new IdRef(dossierId));
            final Dossier dossierAttach = dossierAttachDoc.getAdapter(Dossier.class);
            dossierAttach.setIdMinistereReattribution(minReattribution);
            session.saveDocument(dossierAttachDoc);
        }
        context
            .getMessageQueue()
            .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.etapePourReattribution"));
        STActionsServiceLocator.getDossierLockActionService().unlockCurrentDossier(context);

        // Journalise l'action
        SSServiceLocator
            .getSSJournalService()
            .journaliserActionEtapeFDR(
                session,
                etape,
                dossierDoc,
                ReponsesEventConstant.DOSSIER_REATTRIBUTION_EVENT,
                ReponsesEventConstant.COMMENT_DOSSIER_REATTRIBUTION
            );

        UserSessionHelper.putUserSessionParameter(
            context,
            SSMailboxListComponentServiceImpl.REFRESH_CORBEILLE_KEY,
            true
        );
        return true;
    }

    /**
     * Vérifie les paramètres String obligatoires (l'id ministère par exemple)
     *
     * @return
     */
    private boolean checkObligatoryStringParameters(SpecificContext context, String... parameters) {
        for (String parameter : parameters) {
            if (StringUtils.isEmpty(parameter)) {
                context
                    .getMessageQueue()
                    .addWarnToQueue(ResourceHelper.getString("feedback.reponses.error.missing.parameter"));
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isDossierLinkLoaded(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        if (dossierLink == null) {
            return false;
        } else {
            // Vérifie la lecture sur le dossier link
            String routingTaskType = dossierLink.getRoutingTaskType();
            return routingTaskType != null;
        }
    }

    /**
     * Donne un avis défavorable sur le DossierLink en cours. Sans ajout d'étape.
     * Refus de signature
     */
    @Override
    public void donnerAvisDefavorableEtPoursuivre(SpecificContext context) {
        if (donnerAvisDefavorable(context, false)) {
            context
                .getMessageQueue()
                .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.refusEtContinue"));
        }
    }

    /**
     * Donne un avis défavorable sur le DossierLink en cours. Avec ajout d'étape.
     */
    @Override
    public void donnerAvisDefavorableEtInsererTaches(SpecificContext context) {
        donnerAvisDefavorable(context, true);
    }

    /**
     * Donne un avis défavorable sur le DossierLink en cours. Et retourne le dossier au ministère attributaire dans une
     * étape pour attribution.
     */
    @Override
    public void donnerAvisDefavorableEtRetourBdcAttributaire(SpecificContext context) {
        if (donnerAvisDefavorable(context, false)) {
            context
                .getMessageQueue()
                .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.refusEtRetourBDC"));
        }
    }

    private boolean donnerAvisDefavorable(SpecificContext context, boolean ajoutTache) {
        final DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        final DocumentModel dossierDoc = context.getCurrentDocument();
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        CoreSession session = context.getSession();
        Reponse reponse = dossier.getReponse(session);
        checkAndUpdateReponse(session, reponse.getDocument());

        boolean isStepAjoute = false;
        try {
            // Là on indique qu'on a déjà cliqué une fois sur valider l'étape
            dossierLink.setDateDebutValidation(Calendar.getInstance());
            dossierLink.save(session);
            // update old etape field and savegetDossierFrom
            if (ajoutTache) {
                isStepAjoute =
                    ReponsesServiceLocator
                        .getFeuilleRouteService()
                        .addStepAfterReject(session, dossierLink.getRoutingTaskId(), dossier);
            }
            getDossierDistributionService().rejeterDossierLink(session, dossierDoc, dossierLink.getDocument());
        } catch (final CopieStepException e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_UPDATE_DOSSIER_FONC, e);
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            return false;
        } catch (SSException e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_UPDATE_DOSSIER_FONC, e.getMessage());
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString(ERROR_ADD_STEP));
            return false;
        } catch (final NuxeoException e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_UPDATE_DOSSIER_FONC, e);
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString(ERROR_ADD_STEP));
        } finally {
            getDossierLockActionService().unlockDossier(context);
        }

        // Si message non null : Cas où les étape on été créés manuellement
        // le message affiché est donc spécifique
        if (ajoutTache && isStepAjoute) {
            context
                .getMessageQueue()
                .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.refusEtRetour"));
        } else if (ajoutTache) {
            context
                .getMessageQueue()
                .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.refusEtRetourAjoutManuelle"));
        }

        UserSessionHelper.putUserSessionParameter(
            context,
            SSMailboxListComponentServiceImpl.REFRESH_CORBEILLE_KEY,
            true
        );

        return true;
    }

    /**
     * Vérifie si la réponse a été modifiée, et incrémente la version au besoin
     */
    private void checkAndUpdateReponse(CoreSession session, DocumentModel reponseDoc) {
        ReponseActionService reponseActions = ReponsesActionsServiceLocator.getReponseActionService();
        if (reponseActions.reponseHasChanged(session, reponseDoc)) {
            ReponsesServiceLocator.getReponseService().incrementReponseVersion(session, reponseDoc);
        }
    }

    /**
     * Retourne l'étape "validation PM" de l'instance de la feuille de route associé au dossier.
     *
     * @return Etape validation premier ministre
     */
    @Override
    public DocumentModel getValidationPMStep(SpecificContext context) {
        final DocumentModel dossierDoc = context.getCurrentDocument();
        final STDossier dossier = dossierDoc.getAdapter(STDossier.class);
        final String feuilleRouteInstanceId = dossier.getLastDocumentRoute();

        return ReponsesServiceLocator
            .getFeuilleRouteService()
            .getValidationPMStep(context.getSession(), feuilleRouteInstanceId);
    }

    @Override
    public void donnerAvisFavorable(SpecificContext context) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        CoreSession session = context.getSession();
        Reponse reponse = dossier.getReponse(session);
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);

        checkAndUpdateReponse(session, reponse.getDocument());

        try {
            if (!VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION.equals(dossierLink.getRoutingTaskType())) {
                if (
                    VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE.equals(dossierLink.getRoutingTaskType()) &&
                    StringUtils.isEmpty(reponse.getTexteReponse())
                ) {
                    throw new SignatureException(
                        ResourceHelper.getString("label.reponses.feuilleRoute.message.etape.signature.error")
                    );
                }
                // Là on indique qu'on a déjà cliqué une fois sur valider l'étape
                dossierLink.setDateDebutValidation(Calendar.getInstance());
                dossierLink.save(session);
            }
            getDossierDistributionService().validerEtape(session, dossierDoc, dossierLink.getDocument());

            if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(dossierLink.getRoutingTaskType())) {
                displayMessageInfo(
                    context,
                    LABEL_ETAPE_VALIDATION_REATTRIBUTION,
                    dossier.getNumeroQuestion(),
                    getLabelEntite(dossier.getIdMinistereAttributaireCourant())
                );
            } else if (
                isBlank(dossier.getLabelNextStep()) ||
                VocabularyConstants.LIBELLE_ROUTING_TASK_TERMINE.equals(dossier.getLabelNextStep())
            ) {
                displayMessageInfo(
                    context,
                    "label.reponses.feuilleRoute.message.etape.validation.termine",
                    dossier.getNumeroQuestion()
                );
            } else {
                displayMessageInfo(
                    context,
                    "label.reponses.feuilleRoute.message.etape.validation",
                    dossier.getNumeroQuestion(),
                    dossier.getLabelNextStep()
                );
            }
        } catch (final SignatureException se) {
            // le dossier n'a pas été signé
            LOGGER.warn(session, ReponsesLogEnumImpl.FAIL_SIGN_DOSSIER_FONC, se);
            context.getMessageQueue().addErrorToQueue(se.getMessage());
        } catch (final StepValidationException sve) {
            if (StepValidationException.CAUSEEXC.SIGNATURE_INVALID.equals(sve.getCauseExc())) {
                try {
                    manageInvalidTransmissionAssemblees(context, session, dossierLink, dossierDoc);
                } catch (CopieStepException e) {
                    context.getMessageQueue().addErrorToQueue(e.getMessage());
                }
            } else {
                displayInternalError(context, sve);
            }
        } catch (final ReponsesException re) {
            LOGGER.warn(session, SSLogEnumImpl.FAIL_UPDATE_FDR_TEC, re.getMessage());
            if (LOGGER.isDebugEnable()) {
                LOGGER.warn(session, SSLogEnumImpl.FAIL_UPDATE_FDR_TEC, re);
            }
            context.getMessageQueue().addErrorToQueue(re.getMessage());
        } catch (final NuxeoException ce) {
            displayInternalError(context, ce);
        } finally {
            getDossierLockActionService().unlockCurrentDossier(context);
            context.getWebcontext().getUserSession().put(REFRESH_CORBEILLE_KEY, TRUE);
        }
    }

    private void manageInvalidTransmissionAssemblees(
        SpecificContext context,
        CoreSession session,
        DossierLink dossierLink,
        DocumentModel dossierDoc
    ) {
        final ReponseFeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        try {
            DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
            final ReponsesRouteStep etapeCourante = etapeDoc.getAdapter(ReponsesRouteStep.class);
            feuilleRouteService.addStepsSignatureAndTransmissionAssemblees(session, dossierDoc, etapeCourante);
            getDossierDistributionService().validerEtapeRefus(session, dossierDoc, dossierLink.getDocument());
            String labelNextStep = dossierDoc.getAdapter(Dossier.class).getLabelNextStep();
            String validationMessage = MessageFormat.format(
                getString("label.reponses.feuilleRoute.validation.signature.invalid"),
                labelNextStep
            );
            context.getMessageQueue().addWarnToQueue(validationMessage);
        } catch (ReponsesException e) {
            displayInternalError(context, e);
        }
    }

    private void displayInternalError(SpecificContext context, NuxeoException ne) {
        LOGGER.warn(context.getSession(), STLogEnumImpl.FAIL_UPDATE_DOSSIER_FONC, ne);
        context.getMessageQueue().addErrorToQueue(LABEL_FEUILLE_ROUTE_VALIDATION_INTERNAL_ERROR);
    }

    @Override
    public boolean demandeArbitrageSGG(SpecificContext context) {
        final DocumentModel dossierDoc = context.getCurrentDocument();
        ReponsesArbitrageService reponsesArbitrageService = getReponsesArbitrageService();
        if (!reponsesArbitrageService.canAddStepArbitrageSGG(dossierDoc)) {
            context.getMessageQueue().addErrorToQueue(ERROR_ADD_STEP);
            throw new ReponsesException(ERROR_ADD_STEP);
        }

        CoreSession session = context.getSession();
        try {
            lockDossierIfNotLock(context, dossierDoc);
            DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
                .getSSCorbeilleActionService()
                .getCurrentDossierLink(context);

            final DocumentModel dossierLinkDoc = dossierLink.getDocument();
            reponsesArbitrageService.addStepArbitrageSGG(session, dossierDoc, dossierLinkDoc);
            context
                .getMessageQueue()
                .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.etapePourArbitrage"));
            // Là on indique qu'on a déjà cliqué une fois sur le bouton
            dossierLink.setDateDebutValidation(Calendar.getInstance());
            dossierLink.save(session);

            // valider étape non concerné
            getDossierDistributionService()
                .validerEtapeNonConcerne(session, context.getCurrentDocument(), dossierLink.getDocument());
        } catch (NuxeoException exc) {
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ADD_STEP_ARBITRAGE_FONC, exc);
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString(ERROR_ADD_STEP));
            return false;
        } finally {
            getDossierLockActionService().unlockDossier(context);
        }

        return true;
    }

    @Override
    public void mettreEnAttente(SpecificContext context) {
        final DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        ReponsesServiceLocator.getFeuilleRouteService().addStepAttente(context.getSession(), dossierLink);
        getDossierLockActionService().unlockCurrentDossier(context);
        context
            .getMessageQueue()
            .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.etapePourAttente"));
    }

    @Override
    public void attributionApresArbitrage(SpecificContext context) {
        final DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        final String reattributionMinistere = context.getFromContextData(STContextDataKey.MINISTERE_ID);
        if (!checkObligatoryStringParameters(context, reattributionMinistere)) {
            return;
        }

        CoreSession session = context.getSession();
        Reponse reponse = context.getCurrentDocument().getAdapter(Dossier.class).getReponse(session);
        checkAndUpdateReponse(session, reponse.getDocument());

        // Récupération du dossier
        final DocumentModel dossierDoc = context.getCurrentDocument();
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        final String reattributionObservations = context.getFromContextData(ReponsesContextDataKey.OBSERVATIONS);
        try {
            getReponsesArbitrageService()
                .attributionAfterArbitrage(
                    session,
                    dossierLink,
                    dossierDoc,
                    reattributionMinistere,
                    reattributionObservations
                );
        } catch (final NuxeoException exc) {
            LOGGER.error(session, SSLogEnumImpl.FAIL_REATTR_DOSSIER_FONC, exc);

            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString(LABEL_FEUILLE_ROUTE_VALIDATION_INTERNAL_ERROR));
            return;
        }

        // si le dossier a été réattribué on recharge le label de(s) étape(s)

        displayMessageInfo(
            context,
            LABEL_ETAPE_VALIDATION_REATTRIBUTION,
            dossier.getNumeroQuestion(),
            getLabelEntite(dossier.getIdMinistereAttributaireCourant())
        );
    }

    /**
     * Affiche un message à l'utilisateur en mode info
     *
     * @param validationFormat
     * @param arguments
     */
    private void displayMessageInfo(SpecificContext context, String validationFormat, Object... arguments) {
        context.getMessageQueue().addInfoToQueue(ResourceHelper.getString(validationFormat, arguments));
    }

    /**
     * Récupère le label d'une entite par son id
     *
     * @param idMinistere
     */
    private String getLabelEntite(String idMinistere) {
        final OrganigrammeNode node = STServiceLocator.getSTMinisteresService().getEntiteNode(idMinistere);
        return ofNullable(node).map(OrganigrammeNode::getLabel).orElse(EMPTY);
    }

    @Override
    public void reattributionDirecte(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        final DocumentModel dossierDoc = context.getCurrentDocument();
        if (BooleanUtils.isFalse(dossierDoc.getAdapter(Dossier.class).isArbitrated())) {
            context
                .getMessageQueue()
                .addWarnToQueue(ResourceHelper.getString("feedback.reponses.dossier.error.cannotReattributionDirecte"));
            return;
        }

        String reattributionMinistere = context.getFromContextData(STContextDataKey.MINISTERE_ID);
        if (!checkObligatoryStringParameters(context, reattributionMinistere)) {
            return;
        }

        String reattributionObservations = context.getFromContextData(ReponsesContextDataKey.OBSERVATIONS);
        final CoreSession session = context.getSession();
        try {
            // Là on indique qu'on a déjà cliqué une fois sur le bouton
            dossierLink.setDateDebutValidation(Calendar.getInstance());
            dossierLink.save(session);
            getReponsesArbitrageService()
                .reattributionDirecte(
                    session,
                    dossierLink,
                    dossierDoc,
                    reattributionMinistere,
                    reattributionObservations
                );
        } catch (final NuxeoException exc) {
            LOGGER.error(session, SSLogEnumImpl.FAIL_REATTR_DOSSIER_FONC, exc);
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString(LABEL_FEUILLE_ROUTE_VALIDATION_INTERNAL_ERROR));
            return;
        }

        getDossierLockActionService().unlockCurrentDossier(context);

        // si le dossier a été réattribué on recharge le label de(s) étape(s)
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        displayMessageInfo(
            context,
            LABEL_ETAPE_VALIDATION_REATTRIBUTION,
            dossier.getNumeroQuestion(),
            getLabelEntite(dossier.getIdMinistereAttributaireCourant())
        );
    }

    @Override
    public void nonConcerne(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        // Là on indique qu'on a déjà cliqué une fois sur le bouton
        dossierLink.setDateDebutValidation(Calendar.getInstance());
        CoreSession session = context.getSession();
        dossierLink.save(session);

        // Valide l'étape "non concerné"
        getDossierDistributionService()
            .validerEtapeNonConcerne(session, context.getCurrentDocument(), dossierLink.getDocument());

        getDossierLockActionService().unlockCurrentDossier(context);

        context
            .getMessageQueue()
            .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.nonConcerne"));
    }

    /**
     * Réorientation au sein d'un même ministère
     */
    @Override
    public void nonConcerneReorientation(SpecificContext context) {
        DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        final DocumentModel dossierDoc = context.getCurrentDocument();
        CoreSession session = context.getSession();
        final DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));

        try {
            lockDossierIfNotLock(context, dossierDoc);
            ReponsesServiceLocator.getFeuilleRouteService().addStepAfterReorientation(session, etapeDoc);
            context
                .getMessageQueue()
                .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.etapePourReorientation"));

            // On realise l'etape non concerné
            nonConcerne(context);
        } catch (final PosteNotFoundException e) {
            context.getMessageQueue().addWarnToQueue(e.getMessage());
            LOGGER.warn(session, STLogEnumImpl.FAIL_GET_POSTE_FONC);
        } catch (final FeuilleRouteNotLockedException e) {
            context
                .getMessageQueue()
                .addWarnToQueue(ResourceHelper.getString("feedback.reponses.dossier.fdr.not.lock.error"));
            LOGGER.warn(session, STLogEnumImpl.FAIL_GET_POSTE_FONC);
        } finally {
            getDossierLockActionService().unlockDossier(context);
        }

        final SSRouteStep etape = etapeDoc.getAdapter(SSRouteStep.class);
        // Journalise l'action
        SSServiceLocator
            .getSSJournalService()
            .journaliserActionEtapeFDR(
                session,
                etape,
                dossierDoc,
                ReponsesEventConstant.DOSSIER_REORIENTATION_EVENT,
                ReponsesEventConstant.COMMENT_DOSSIER_REORIENTATION
            );
    }

    @Override
    public void substituerRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel newRouteDoc = session.getDocument(
            new IdRef(context.getFromContextData(SSContextDataKey.ID_MODELE))
        );

        if (newRouteDoc == null) {
            // Aucun modèle n'est sélectionné
            context
                .getMessageQueue()
                .addWarnToQueue(ResourceHelper.getString("feedback.reponses.document.route.no.valid.route"));
            return;
        }

        DocumentModel dossierDoc = context.getCurrentDocument();
        // Récupère l'ancienne instance de feuille de route
        ReponsesDocumentRoutingActionService routingActions = ReponsesActionsServiceLocator.getReponsesDocumentRoutingActionService();
        final SSFeuilleRoute oldRoute = routingActions.getRelatedRoute(session, dossierDoc);
        DocumentModel oldRouteDoc = null;
        if (oldRoute != null) {
            oldRouteDoc = oldRoute.getDocument();
        }

        // Substitue la feuille de route
        getDossierDistributionService()
            .substituerFeuilleRoute(
                session,
                dossierDoc,
                oldRouteDoc,
                newRouteDoc,
                STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_SUBSTITUTION
            );

        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        final Question question = dossier.getQuestion(session);
        if (BooleanUtils.isTrue(question.isQuestionTypeEcrite())) {
            final ReponsesFeuilleRoute newFeuilleRoute = newRouteDoc.getAdapter(ReponsesFeuilleRoute.class);
            getDossierDistributionService()
                .setDirectionPilote(session, question, newFeuilleRoute.getIdDirectionPilote());
            session.saveDocument(question.getDocument());
        }

        // Journalise de l'évenement
        STServiceLocator
            .getJournalService()
            .journaliserActionFDR(
                session,
                dossierDoc,
                STEventConstant.DOSSIER_SUBSTITUER_FEUILLE_ROUTE,
                STEventConstant.COMMENT_SUBSTITUER_FEUILLE_ROUTE
            );

        // envoi message information utilisateur
        context.getMessageQueue().addSuccessToQueue("fdr.substituer.action.message.success");
    }

    @Override
    public void redemarrerDossier(SpecificContext context) {
        // Redémarre le dossier
        final DocumentModel dossierDoc = context.getCurrentDocument();
        CoreSession session = context.getSession();
        DossierLockActionService dossierLockActionService = STActionsServiceLocator.getDossierLockActionService();

        if (
            !LockUtils.isLockedByCurrentUser(session, dossierDoc.getRef()) &&
            dossierLockActionService.getCanLockDossier(dossierDoc, session)
        ) {
            dossierDoc.setLock();
        }

        if (LockUtils.isLockedByCurrentUser(session, dossierDoc.getRef())) {
            getDossierDistributionService().restartDossier(session, dossierDoc);

            // Affiche un message d'information
            context
                .getMessageQueue()
                .addMessageToQueue(
                    ResourceHelper.getString("reponses.distribution.action.restart.success"),
                    AlertType.TOAST_SUCCESS
                );
        }
    }

    @Override
    public List<FondDeDossierFile> getListeDocumentPublicReponse(SpecificContext context) {
        final FondDeDossierService fddService = ReponsesServiceLocator.getFondDeDossierService();
        CoreSession session = context.getSession();
        final DocumentModel fddDocument = fddService.getFondDeDossierFromDossier(session, context.getCurrentDocument());
        if (fddDocument != null) {
            return fddService.getFondDeDossierPublicDocument(session, fddDocument);
        }
        return Collections.emptyList();
    }


    @Override
    public void validationRetourPM(SpecificContext context) {
        final CoreSession session = context.getSession();
        final DossierLink dossierLink = (DossierLink) SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLink(context);
        Dossier dossier = dossierLink.getDossier(session);

        try {
            final DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
            ReponsesServiceLocator
                .getFeuilleRouteService()
                .addStepValidationRetourPM(session, etapeDoc, dossier.getDocument());
            context
                .getMessageQueue()
                .addInfoToQueue(ResourceHelper.getString("feedback.reponses.dossier.info.etapePourValidationPM"));
        } catch (ReponsesException exc) {
            context.getMessageQueue().addErrorToQueue(ERROR_ADD_STEP);
            LOGGER.error(session, STLogEnumImpl.FAIL_UPDATE_DOSSIER_FONC, exc);
        }
    }

    private void lockDossierIfNotLock(SpecificContext context, DocumentModel dossierDoc) {
        DossierLockActionService lockActionService = STActionsServiceLocator.getDossierLockActionService();
        CoreSession session = context.getSession();
        if (
            !LockUtils.isLocked(session, dossierDoc.getRef()) &&
            lockActionService.getCanLockDossier(dossierDoc, session)
        ) {
            lockActionService.lockDossier(context, session, dossierDoc);
        }
    }
}
