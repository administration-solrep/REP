package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.constant.VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doUFNXQLQueryAndMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.google.common.collect.ImmutableMap;
import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.cases.CaseDistribConstants;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.exception.CopieStepException;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.api.service.SSJournalService;
import fr.dila.ss.core.service.SSFeuilleRouteServiceImpl;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.PosteNotFoundException;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.core.util.StringHelper;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

/**
 * Implémentation du service permettant d'effectuer des actions spécifiques sur les instances de feuille de route dans
 * l'application Réponses.
 *
 * @author jtremeaux
 */
public class ReponseFeuilleRouteServiceImpl extends SSFeuilleRouteServiceImpl implements ReponseFeuilleRouteService {
    /**
     * UID.
     */
    private static final long serialVersionUID = -2392698015083550568L;

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog(ReponseFeuilleRouteServiceImpl.class);
    private static final String MINISTERE_INEXISTANT_MSG =
        "Le ministère de rattachement du poste de l'étape précédente n'existe plus, le refus de validation avec retour à l'étape précédente n'est pas possible";
    private static final String POSTE_INEXISTANT_MSG =
        "Le poste de l'étape précédente n'existe plus, le refus de validation avec retour à l'étape précédente n'est pas possible";

    @Override
    public DocumentModel getValidationPMStep(final CoreSession session, final String feuilleRouteInstanceId) {
        final DocumentRoutingService service = SSServiceLocator.getDocumentRoutingService();

        // Récupération de toutes les étapes de feuille de route
        final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(feuilleRouteInstanceId));
        final SSFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(SSFeuilleRoute.class);
        final List<RouteTableElement> listRouteTableElement = service.getFeuilleRouteElements(feuilleRoute, session);

        // Recherche de l'étape Validation premier ministre
        for (final RouteTableElement routeTableElement : listRouteTableElement) {
            if (!routeTableElement.getDocument().isFolder()) {
                //final STRouteStep step = routeTableElement.getRouteStep();
                final SSRouteStep step = routeTableElement.getElement().getDocument().getAdapter(SSRouteStep.class);
                if (VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM.equals(step.getType())) {
                    return step.getDocument();
                }
            }
        }

        return null;
    }

    @Override
    public void addStepAttente(final CoreSession session, final DossierLink dossierLink) {
        final DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
        final ReponsesRouteStep etape = etapeDoc.getAdapter(ReponsesRouteStep.class);
        final String mailboxId = etape.getDistributionMailboxId();
        final String docId = (String) etapeDoc.getParentRef().reference();
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(docId, session);
        final int currentStepIndex = steps.indexOf(etapeDoc);
        final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        final DocumentModel dossierDoc = dossierDistributionService.getDossierFromDocumentRouteId(
            session,
            etape.getDocumentRouteId()
        );
        final DocumentModel dossierLinkDoc = dossierLink.getDocument();

        // Ajout de l'étape pour attente
        final FeuilleRouteStep newCurrentStep = createStepAttente(session, mailboxId);
        documentRoutingService.addRouteElementToRoute(
            etapeDoc.getParentRef(),
            currentStepIndex + 1,
            newCurrentStep,
            session
        );

        // Validation de l'étape en cours
        dossierDistributionService.validerEtape(session, dossierDoc, dossierLinkDoc);
    }

    /**
     * Création d'une étape Pour attente.
     *
     * @param session
     *            Session
     * @return Nouvelle étape Pour attente
     *
     */
    protected FeuilleRouteStep createStepAttente(final CoreSession session, final String mailboxId) {
        final DocumentModel newStepModel = session.createDocumentModel(SSConstant.ROUTE_STEP_DOCUMENT_TYPE);
        final ReponsesRouteStep newStep = newStepModel.getAdapter(ReponsesRouteStep.class);
        newStep.setType(VocabularyConstants.ROUTING_TASK_TYPE_ATTENTE);

        newStep.setDistributionMailboxId(mailboxId);

        return newStep;
    }

    @Override
    public void addStepAfterReorientation(final CoreSession session, final DocumentModel etapeDoc) {
        final ReponsesRouteStep step = getPreviousStep(session, etapeDoc);
        if (isStepInMinistereAttributaire(step, etapeDoc.getAdapter(ReponsesRouteStep.class))) {
            addStepAfterReorientation(session, etapeDoc, step.getDistributionMailboxId());
        } else {
            throw new PosteNotFoundException(
                ResourceHelper.getString("feedback.reponses.dossier.poste.reorientation.error")
            );
        }
    }

    private boolean isStepInMinistereAttributaire(ReponsesRouteStep step, ReponsesRouteStep currentStep) {
        return (
            currentStep != null &&
            step != null &&
            (
                currentStep.getMinistereId().contains(step.getMinistereId()) ||
                step.getMinistereId().contains(currentStep.getMinistereId()) ||
                step.getMinistereId().equals(currentStep.getMinistereId())
            )
        );
    }

    @Override
    public void addStepAfterReorientation(
        final CoreSession session,
        final DocumentModel etapeDoc,
        final String mailboxId
    ) {
        // création de la mailbox si nécéssaire
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxId));

        // Récupere le step courant
        final String parentDocId = (String) etapeDoc.getParentRef().reference();

        // récupération des étapes contenues dans parent
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(parentDocId, session);

        final int currentStepIndex = steps.indexOf(etapeDoc);

        // Ajout du step réorientation avec le poste du BDC du ministère attributaire
        // trouvé
        // Récupération du BDC du ministère attributaire
        String mailboxIdBdC = "";
        final String documentRouteId =etapeDoc.getAdapter(SSRouteStep.class).getDocumentRouteId();
        String idMinistereAttributaire = getIdMinistereAttributaireCourant(session, new IdRef(documentRouteId));
        final PosteNode posteNodeBDC = STServiceLocator
                .getSTPostesService()
                .getPosteBdcInEntite(idMinistereAttributaire);

        if (posteNodeBDC != null && mailboxPosteService != null) {
            mailboxPosteService.getOrCreateMailboxPoste(session, posteNodeBDC.getId());
            mailboxIdBdC = mailboxPosteService.getPosteMailboxId(posteNodeBDC.getId());
        }

        final FeuilleRouteStep newStep = documentRoutingService.createNewRouteStep(
            session,
            !mailboxIdBdC.isEmpty() ? mailboxIdBdC : mailboxId,
            ROUTING_TASK_TYPE_REORIENTATION
        );
        documentRoutingService.addRouteElementToRoute(etapeDoc.getParentRef(), currentStepIndex + 1, newStep, session);
    }

    @Override
    public void addStepValidationRetourPM(
        final CoreSession session,
        final DocumentModel etapeDoc,
        final DocumentModel dossierDoc
    ) {
        final SSRouteStep step = etapeDoc.getAdapter(SSRouteStep.class);
        final String mailboxId = step.getDistributionMailboxId();
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final SSJournalService journalService = SSServiceLocator.getSSJournalService();

        // Récupération du BDC du ministère attributaire
        final String documentRouteId = step.getDocumentRouteId();
        String idMinistereAttributaire = getIdMinistereAttributaireCourant(session, new IdRef(documentRouteId));
        final PosteNode posteNodeBDC = STServiceLocator
            .getSTPostesService()
            .getPosteBdcInEntite(idMinistereAttributaire);
        if (posteNodeBDC != null && mailboxPosteService != null) {
            mailboxPosteService.getOrCreateMailboxPoste(session, posteNodeBDC.getId());
            final String mailboxIdBdC = mailboxPosteService.getPosteMailboxId(posteNodeBDC.getId());

            final String docId = (String) etapeDoc.getParentRef().reference();
            final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(docId, session);
            final int currentStepIndex = steps.indexOf(etapeDoc);

            // Ajout de l'étape pour validation retour PM
            final FeuilleRouteStep newStepValidationRetourPM = createStepValidationRetourPM(session, mailboxId);
            documentRoutingService.addRouteElementToRoute(
                etapeDoc.getParentRef(),
                currentStepIndex + 1,
                newStepValidationRetourPM,
                session
            );

            // Ajout de l'étape pour signature
            final FeuilleRouteStep newStepSignature = createStepSignature(session, mailboxIdBdC);
            documentRoutingService.addRouteElementToRoute(
                etapeDoc.getParentRef(),
                currentStepIndex + 1,
                newStepSignature,
                session
            );

            // Ajout de l'étape pour retour
            final FeuilleRouteStep newStepRetour = createStepRetour(session, mailboxIdBdC);
            documentRoutingService.addRouteElementToRoute(
                etapeDoc.getParentRef(),
                currentStepIndex + 1,
                newStepRetour,
                session
            );
            journalService.journaliserActionEtapeFDR(
                session,
                step,
                dossierDoc,
                STEventConstant.DOSSIER_POUR_VALIDATION_PM,
                STEventConstant.COMMENT_DOSSIER_POUR_VALIDATION_PM
            );
        } else {
            throw new ReponsesException(
                "Ajout étape validation pour retour Premier ministre : postNodeBDC ou mailboxPosteService nulls"
            );
        }
    }

    /**
     * Création d'une étape Pour retour.
     *
     * @param session
     *            Session
     * @return Nouvelle étape Pour retour
     *
     */
    protected FeuilleRouteStep createStepRetour(final CoreSession session, final String mailboxId) {
        final DocumentModel newStepModel = session.createDocumentModel(SSConstant.ROUTE_STEP_DOCUMENT_TYPE);
        final ReponsesRouteStep newStep = newStepModel.getAdapter(ReponsesRouteStep.class);
        newStep.setType(VocabularyConstants.ROUTING_TASK_TYPE_RETOUR);

        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
        newStep.setDistributionMailboxId(mailboxId);

        return newStep;
    }

    /**
     * Création d'une étape Pour validation retour Premier ministre.
     *
     * @param session
     *            Session
     * @return Nouvelle étape Pour validation retour Premier ministre
     *
     */
    protected FeuilleRouteStep createStepValidationRetourPM(final CoreSession session, final String mailboxId) {
        final DocumentModel newStepModel = session.createDocumentModel(SSConstant.ROUTE_STEP_DOCUMENT_TYPE);
        final ReponsesRouteStep newStep = newStepModel.getAdapter(ReponsesRouteStep.class);
        newStep.setType(VocabularyConstants.ROUTING_TASK_TYPE_RETOUR_VALIDATION_PM);

        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
        newStep.setDistributionMailboxId(mailboxId);

        return newStep;
    }

    /**
     * Création d'une étape Pour signature.
     *
     * @param session
     *            Session
     * @return Nouvelle étape Pour signature
     *
     */
    protected FeuilleRouteStep createStepSignature(final CoreSession session, final String mailboxId) {
        final DocumentModel newStepModel = session.createDocumentModel(SSConstant.ROUTE_STEP_DOCUMENT_TYPE);
        final ReponsesRouteStep newStep = newStepModel.getAdapter(ReponsesRouteStep.class);
        newStep.setType(VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE);

        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
        newStep.setDistributionMailboxId(mailboxId);

        return newStep;
    }

    private ReponsesRouteStep getPreviousStep(final CoreSession session, final DocumentModel etapeDoc) {
        // Récupere le step courant
        final String parentDocId = (String) etapeDoc.getParentRef().reference();

        final DocumentModel parentRouteElement = session.getParentDocument(etapeDoc.getRef());
        // on ajoute pas d'étape si on est dans une branche parallèle
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(parentRouteElement.getType())) {
            final StepFolder stepFolder = parentRouteElement.getAdapter(StepFolder.class);
            if (stepFolder.isParallel()) {
                // Le parent est un conteneur de tâches parallèles copie de
                // tâche impossible
                throw new NuxeoException("FeuilleRouteService : Insertion d'étape impossible");
            }
        }

        // Pour vérification des droits
        final NuxeoPrincipal principal = session.getPrincipal();

        // récupération des étapes contenues dans parent
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(parentDocId, session);

        final int currentStepIndex = steps.indexOf(etapeDoc);
        int loopCurrentIndex = currentStepIndex;

        DocumentModel stepModel = null;
        ReponsesRouteStep step = null;
        while (loopCurrentIndex > 0) {
            // Recherche d'un step précédent valide pour la copie
            stepModel = steps.get(loopCurrentIndex - 1);
            if (stepModel.hasFacet("RouteStep")) {
                step = stepModel.getAdapter(ReponsesRouteStep.class);

                final String type = step.getType();

                // on passe les étapes impression, information ou réattribution
                if (
                    !VocabularyConstants.ROUTING_TASK_TYPE_IMPRESSION.equals(type) &&
                    !VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION.equals(type)
                ) {
                    // si on a pas la fct ReorientationRedacteur on a trouvé
                    // l'étape
                    if (!principal.isMemberOf(ReponsesBaseFunctionConstant.NC_REORIENTATION_REDACTEUR_READER)) {
                        // étape copiable
                        break;
                        // Sinon si l'étape n'est pas réattribution on a trouvé
                        // l'étape
                    } else if (!VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(type)) {
                        // étape copiable
                        break;
                    }
                }
            }

            loopCurrentIndex--;
        }

        if (loopCurrentIndex == 0 || step == null) {
            // Retour KO si insertion impossible et message à user
            throw new NuxeoException("FeuilleRouteService : Insertion d'étape impossible");
        } else if (!isPosteActive(step)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Le poste correspondant à l'étape n'est plus actif");
            }
            throw new PosteNotFoundException();
        }

        return step;
    }

    private boolean isPosteActive(final ReponsesRouteStep step) {
        // Vérifie que le poste correspondant à la dernière étape n'a pas été
        // supprimé
        final String distributionMailboxId = step.getDistributionMailboxId();
        final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(distributionMailboxId);
        final PosteNode poste = STServiceLocator.getSTPostesService().getPoste(posteId);
        return !poste.getDeleted() && poste.isActive();
    }

    @Override
    public void addStepAfterReattribution(
        final CoreSession session,
        final DossierLink dossierLink,
        final String mailboxId
    ) {
        // Récupère l'étape et son conteneur
        final DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
        final String parentDocId = (String) etapeDoc.getParentRef().reference();

        // on ajoute pas d'étape si on est dans une branche parallèle
        if (checkStepIsParallel(session, etapeDoc)) {
            throw new NuxeoException("FeuilleRouteService : Insertion d'étape impossible");
        }

        // Récupération des étapes contenues dans parent
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(parentDocId, session);

        final int currentStepIndex = steps.indexOf(etapeDoc);

        // Ajout du step réattribution avec le poste passé en paramètre
        final FeuilleRouteStep newStep = documentRoutingService.createNewRouteStep(
            session,
            mailboxId,
            VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION
        );
        documentRoutingService.addRouteElementToRoute(etapeDoc.getParentRef(), currentStepIndex + 1, newStep, session);

        // Avis non concerné sur l'étape en cours
        final ReponsesRouteStep etape = etapeDoc.getAdapter(ReponsesRouteStep.class);
        final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        final DocumentModel dossierDoc = dossierDistributionService.getDossierFromDocumentRouteId(
            session,
            etape.getDocumentRouteId()
        );
        final DocumentModel dossierLinkDoc = dossierLink.getDocument();
        dossierDistributionService.validerEtapeNonConcerne(session, dossierDoc, dossierLinkDoc);
    }

    @Override
    public void addStepsSignatureAndTransmissionAssemblees(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final SSRouteStep routeStep
    ) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        final STLockService lockService = STServiceLocator.getSTLockService();
        final SSJournalService journalService = SSServiceLocator.getSSJournalService();
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        // Récupération de la feuille de route
        final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));

        // Récupération de l'étape transmission aux assemblées
        DocumentModel transmissionStepDoc = null;
        if (VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(routeStep.getType())) {
            transmissionStepDoc = routeStep.getDocument();
        } else {
            final List<DocumentModel> nextSteps = findNextSteps(
                session,
                dossier.getLastDocumentRoute(),
                routeStep.getDocument(),
                null
            );
            for (final DocumentModel nextStepDoc : nextSteps) {
                final SSRouteStep nextStep = nextStepDoc.getAdapter(SSRouteStep.class);
                if (VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(nextStep.getType())) {
                    transmissionStepDoc = nextStepDoc;
                }
            }
        }
        if (transmissionStepDoc == null) {
            throw new NuxeoException(
                "La prochaine étape de feuille de route n'est pas Pour transmission aux assemblées"
            );
        }

        final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(
            transmissionStepDoc.getParentRef().toString(),
            session
        );
        final int transmissionStepIndex = steps.indexOf(transmissionStepDoc);

        // Si la feuille de route possède déjà des étapes Pour signature et Pour transmission au assemblée
        // après l'étape Pour transmission en cours, On ne les recrée pas
        if (
            steps != null &&
            steps.size() > transmissionStepIndex + 2 &&
            VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE.equals(
                steps.get(transmissionStepIndex + 1).getAdapter(SSRouteStep.class).getType()
            ) &&
            VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(
                steps.get(transmissionStepIndex + 2).getAdapter(SSRouteStep.class).getType()
            )
        ) {
            return;
        }

        SSRouteStep stepSignature = null;
        SSRouteStep stepTransmission = null;

        // Récupération de la mailbox de l'étape Pour signature et Pour
        // transmission aux assemblées de l'instance de feuille de route du
        // dossier courant
        //final List<DocumentRouteTableElement> routeElements = documentRoutingService.getFeuilleRouteElements(feuilleRoute, session);
        Integer index = 0;
        String stepSignatureMailboxId = null;
        String stepTransmissionAssembleesMailboxId = null;
        for (final DocumentModel stepDoc : steps) {
            if (index <= transmissionStepIndex) {
                stepSignature = getStepSignature(session, stepDoc);
                if (stepSignature != null) {
                    stepSignatureMailboxId = stepSignature.getDistributionMailboxId();
                }
                stepTransmission = getStepTransmission(session, stepDoc);
                if (stepTransmission != null) {
                    stepTransmissionAssembleesMailboxId = stepTransmission.getDistributionMailboxId();
                }
                index++;
            }
        }

        // Si les étapes ne sont pas trouvés on met le poste BDC par défaut
        if (stepSignatureMailboxId == null || stepTransmissionAssembleesMailboxId == null) {
            final OrganigrammeNode posteBdc = STServiceLocator
                .getSTPostesService()
                .getPosteBdcInEntite(dossier.getIdMinistereAttributaireCourant());
            if (posteBdc != null) {
                final String mailboxId = mailboxPosteService.getPosteMailboxId(posteBdc.getId());
                mailboxPosteService.getOrCreateMailboxPoste(
                    session,
                    mailboxPosteService.getPosteIdFromMailboxId(mailboxId)
                );
                if (stepSignatureMailboxId == null) {
                    stepSignatureMailboxId = mailboxId;
                }
                if (stepTransmissionAssembleesMailboxId == null) {
                    stepTransmissionAssembleesMailboxId = mailboxId;
                }
            }
        }

        // Sans poste BDC, impossible de continuer
        if (stepSignatureMailboxId == null || stepTransmissionAssembleesMailboxId == null) {
            throw new NuxeoException(
                "La feuille de route ne comporte pas d'étapes Pour signature et/ou Pour transmission aux assemblées, et le ministère attributaire ne comporte pas de poste BDC"
            );
        } else {
            // maj dossier avec label next step
            final OrganigrammeNode posteBdc = STServiceLocator
                .getSTPostesService()
                .getPoste(mailboxPosteService.getPosteIdFromMailboxId(stepSignatureMailboxId));
            dossier.setLabelNextStep(posteBdc.getLabel());
        }

        if (stepSignature != null && stepTransmission != null) {
            OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
            List<String> minStepPostIdList = new ArrayList<>();
            OrganigrammeNode minStepNode = organigrammeService.getOrganigrammeNodeById(
                stepSignature.getMinistereId(),
                OrganigrammeType.MINISTERE
            );
            String curentStepPostId = stepTransmission
                .getDistributionMailboxId()
                .substring(stepTransmission.getDistributionMailboxId().indexOf('-') + 1);

            for (PosteNode posteNode : organigrammeService.getAllSubPostes(minStepNode)) {
                minStepPostIdList.add(posteNode.getId());
            }

            if (
                !stepSignature.getMinistereId().equals(stepTransmission.getMinistereId()) ||
                !minStepPostIdList.contains(curentStepPostId)
            ) {
                throw new CopieStepException("feedback.reponses.dossier.error.copie.etape.posteInconnue.transmission");
            }
        }

        lockService.lockDoc(session, feuilleRouteDoc);

        // Ajout des étapes signature et transmission aux assemblées
        final FeuilleRouteStep newStepSignature = documentRoutingService.createNewRouteStep(
            session,
            stepSignatureMailboxId,
            VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE
        );
        final FeuilleRouteStep newStepTransmissionAssemblees = documentRoutingService.createNewRouteStep(
            session,
            stepTransmissionAssembleesMailboxId,
            VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE
        );
        documentRoutingService.addRouteElementToRoute(
            transmissionStepDoc.getParentRef(),
            transmissionStepIndex + 1,
            newStepSignature,
            session
        );
        documentRoutingService.addRouteElementToRoute(
            transmissionStepDoc.getParentRef(),
            transmissionStepIndex + 2,
            newStepTransmissionAssemblees,
            session
        );

        // Journalise l'ajout des deux étapes
        journalService.journaliserActionEtapeFDR(
            session,
            (SSRouteStep) newStepSignature,
            dossierDoc,
            STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE,
            STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE
        );
        journalService.journaliserActionEtapeFDR(
            session,
            (SSRouteStep) newStepTransmissionAssemblees,
            dossierDoc,
            STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE,
            STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE
        );

        lockService.unlockDoc(session, feuilleRouteDoc);
    }

    private static SSRouteStep getStepSignature(CoreSession session, DocumentModel stepDoc) {
        return getStepType(session, stepDoc, VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE);
    }

    private static SSRouteStep getStepTransmission(CoreSession session, DocumentModel stepDoc) {
        return getStepType(session, stepDoc, VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE);
    }

    private static SSRouteStep getStepType(CoreSession session, DocumentModel stepDoc, String routingTaskType) {
        if (stepDoc.isFolder()) {
            session.getChildren(stepDoc.getRef()).forEach(step -> getStepType(session, step, routingTaskType));
        } else {
            final SSRouteStep step = stepDoc.getAdapter(SSRouteStep.class);
            if (routingTaskType.equals(step.getType())) {
                return step;
            }
        }
        return null;
    }

    @Override
    public boolean addStepAfterReject(final CoreSession session, final String routingTaskId, Dossier dossier) {
        final DocumentModel currentStepDoc = session.getDocument(new IdRef(routingTaskId));
        final FeuilleRouteStep currentStepDocRoute = currentStepDoc.getAdapter(FeuilleRouteStep.class);
        final String docId = (String) currentStepDoc.getParentRef().reference();

        final DocumentModel parentRouteElementDoc = session.getParentDocument(currentStepDoc.getRef());

        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(parentRouteElementDoc.getType())) {
            final StepFolder stepFolder = parentRouteElementDoc.getAdapter(StepFolder.class);
            if (stepFolder.isParallel()) {
                // Le parent est un conteneur de tâches parallèles copie de tâche impossible
                throw new SSException("feedback.reponses.route.error.retour");
            }
        }

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

        final DocumentModelList stepsDoc = documentRoutingService.getOrderedRouteElement(docId, session);
        final int currentStepIndex = stepsDoc.indexOf(currentStepDoc);

        SSRouteStep currentStep = currentStepDoc.getAdapter(SSRouteStep.class);
        if (currentStep.getType().equals(VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE)) {
            addStepsSignatureAndTransmissionAssemblees(session, dossier.getDocument(), currentStep);
            return true;
        }

        int loopCurrentIndex = currentStepIndex;
        SSRouteStep step = null;

        DocumentModel stepModel = null;
        while (loopCurrentIndex > 0) {
            // Recherche d'un step précédent valide pour la copie
            stepModel = stepsDoc.get(loopCurrentIndex - 1);
            if (stepModel.hasFacet("RouteStep")) {
                step = stepModel.getAdapter(SSRouteStep.class);
                final String validation = step.getValidationStatus();
                final String type = step.getType();
                checkArbitrage(type);
                final boolean alreadyDuplicated = step.isAlreadyDuplicated();
                // On cherche parmis les étapes validées
                if (
                    SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_VALIDE_AUTOMATIQUEMENT_VALUE.equals(
                        validation
                    ) ||
                    SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE.equals(validation)
                ) {
                    // on passe les étapes impression, information ou réattribution non déjà dupliquée
                    if (
                        !getTypeEtapeImpression().equals(type) &&
                        !getTypeEtapeInformation().equals(type) &&
                        !isTypeEtapeReattribution(type) &&
                        !alreadyDuplicated
                    ) {
                        // étape copiable
                        break;
                    }
                }
            }
            --loopCurrentIndex;
        }

        if (loopCurrentIndex == 0) {
            // Retour KO si insertion impossible et message à user
            throw new SSException("feedback.reponses.route.error.retour");
        } else {
            // Copie du step précédemment trouvé
            final FeuilleRouteStep stepDoc = stepModel.getAdapter(FeuilleRouteStep.class);
            OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();

            //Si le step contient des postes se trouvant dans plusieurs ministères, on a un tableau de plusieurs id
            List<String> ministereIdList = Arrays.asList(step.getMinistereId().split(","));

            //On recupère le premier noeud non deleted
            OrganigrammeNode minStepNode = (OrganigrammeNode) ministereIdList
                .stream()
                .map(
                    ministereId -> organigrammeService.getOrganigrammeNodeById(ministereId, OrganigrammeType.MINISTERE)
                )
                .filter(
                    mininistereNode -> mininistereNode != null && !((OrganigrammeNode) mininistereNode).getDeleted()
                )
                .findFirst()
                .orElse(null);

            if (minStepNode != null) {
                String stepPostId = step
                    .getDistributionMailboxId()
                    .substring(step.getDistributionMailboxId().indexOf('-') + 1);
                OrganigrammeNode posteStepNode = organigrammeService.getOrganigrammeNodeById(
                    stepPostId,
                    OrganigrammeType.POSTE
                );
                if (posteStepNode != null && !posteStepNode.getDeleted()) {
                    final FeuilleRouteStep newStep = copyStep(session, stepDoc);
                    if (newStep != null) {
                        documentRoutingService.addRouteElementToRoute(
                            currentStepDoc.getParentRef(),
                            currentStepIndex + 1,
                            newStep,
                            session
                        );
                    }
                    // Copie du step courant
                    SSRouteStep loadingStep = currentStepDocRoute.getDocument().getAdapter(SSRouteStep.class);
                    if(!loadingStep.getType().equals(ROUTING_TASK_TYPE_REORIENTATION)){
                        final FeuilleRouteStep newCurrentStep = copyStep(session, currentStepDocRoute);
                        documentRoutingService.addRouteElementToRoute(
                                currentStepDoc.getParentRef(),
                                currentStepIndex + 2,
                                newCurrentStep,
                                session
                        );
                    } else {
                        DocumentModel previousStepModel = stepsDoc.get(currentStepIndex - 1);
                        final FeuilleRouteStep newCurrentStep = copyStep(session, previousStepModel.getAdapter(FeuilleRouteStep.class));
                        documentRoutingService.addRouteElementToRoute(
                                currentStepDoc.getParentRef(),
                                currentStepIndex + 2,
                                newCurrentStep,
                                session
                        );
                    }

                } else {
                    throw new CopieStepException(POSTE_INEXISTANT_MSG);
                }
            } else {
                throw new CopieStepException(MINISTERE_INEXISTANT_MSG);
            }
        }
        return true;
    }

    @Override
    public boolean isNextStepReorientationOrReattributionOrArbitrage(final CoreSession session, final String routingTaskId) {
        final DocumentModel currentStepDoc = session.getDocument(new IdRef(routingTaskId));
        DocumentModel nextStepDoc;
        final String docId = (String) currentStepDoc.getParentRef().reference();

        DocumentRoutingService service;
        service = SSServiceLocator.getDocumentRoutingService();
        final DocumentModelList stepList = service.getOrderedRouteElement(docId, session);

        final int index = stepList.indexOf(currentStepDoc);

        if (stepList.size() > index + 1) {
            nextStepDoc = stepList.get(index + 1);
            if (nextStepDoc.hasFacet(FeuilleRouteConstant.FACET_FEUILLE_ROUTE_STEP)) {
                final ReponsesRouteStep nextStep = nextStepDoc.getAdapter(ReponsesRouteStep.class);

                if (
                    ROUTING_TASK_TYPE_REORIENTATION.equals(nextStep.getType()) ||
                    VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(nextStep.getType()) ||
                    VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(nextStep.getType())
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isNextStepReattributionOrArbitrage(final CoreSession session, final String routingTaskId) {
        final DocumentModel currentStepDoc = session.getDocument(new IdRef(routingTaskId));
        DocumentModel nextStepDoc;
        final String docId = (String) currentStepDoc.getParentRef().reference();

        DocumentRoutingService service;
        service = SSServiceLocator.getDocumentRoutingService();
        final DocumentModelList stepList = service.getOrderedRouteElement(docId, session);

        final int index = stepList.indexOf(currentStepDoc);

        if (stepList.size() > index + 1) {
            nextStepDoc = stepList.get(index + 1);
            if (nextStepDoc.hasFacet(FeuilleRouteConstant.FACET_FEUILLE_ROUTE_STEP)) {
                final ReponsesRouteStep nextStep = nextStepDoc.getAdapter(ReponsesRouteStep.class);

                if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(nextStep.getType()) || VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(nextStep.getType())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isNextStepArbitrage(final CoreSession session, final String routingTaskId) {
        final DocumentModel currentStepDoc = session.getDocument(new IdRef(routingTaskId));
        DocumentModel nextStepDoc;
        final String docId = (String) currentStepDoc.getParentRef().reference();

        DocumentRoutingService service;
        service = SSServiceLocator.getDocumentRoutingService();
        final DocumentModelList stepList = service.getOrderedRouteElement(docId, session);

        final int index = stepList.indexOf(currentStepDoc);

        if (stepList.size() > index + 1) {
            nextStepDoc = stepList.get(index + 1);
            if (nextStepDoc.hasFacet(FeuilleRouteConstant.FACET_FEUILLE_ROUTE_STEP)) {
                final ReponsesRouteStep nextStep = nextStepDoc.getAdapter(ReponsesRouteStep.class);

                if (VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(nextStep.getType())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isNextStepTransmissionAssemblees(
        final CoreSession session,
        final String feuilleRouteDocId,
        final DocumentModel stepDoc
    ) {
        final List<DocumentModel> nextSteps = findNextSteps(session, feuilleRouteDocId, stepDoc, null);
        for (final DocumentModel nextStepDoc : nextSteps) {
            final SSRouteStep nextStep = nextStepDoc.getAdapter(SSRouteStep.class);
            if (VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(nextStep.getType())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Vérifie si l'étape passée en paramètre est une étape parallèle
     *
     * @param session
     * @param l
     *            'étape
     * @return vrai si l'étape est parallèle
     *
     */
    private boolean checkStepIsParallel(CoreSession session, DocumentModel etapeDoc) {
        DocumentModel parentRouteElement = session.getParentDocument(etapeDoc.getRef());
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(parentRouteElement.getType())) {
            final StepFolder stepFolder = parentRouteElement.getAdapter(StepFolder.class);
            if (stepFolder.isParallel()) {
                // Le parent est un conteneur de tâches parallèles copie de
                // tâche impossible
                return true;
            }
        }
        return false;
    }

    /**
     * Permet de retourner l'identifiant du ministère attributaire de la feuille de route
     *
     * @param session
     *            : La session en cours
     * @param documentRouteId
     *            : l'identifiant de la feuille de route
     * @return : l'identifiant du ministère attributaire au format String
     *
     */
    private String getIdMinistereAttributaireCourant(CoreSession session, IdRef documentRouteId) {
        String idMinistereAttributaireCourant = null;
        if (session.exists(documentRouteId)) {
            DocumentModel routeDoc = session.getDocument(documentRouteId);
            SSFeuilleRoute route = routeDoc.getAdapter(SSFeuilleRoute.class);
            List<String> dossiersIds = route.getAttachedDocuments();
            String dossierId = dossiersIds.get(0);
            IdRef dossierRef = new IdRef(dossierId);
            if (session.exists(dossierRef)) {
                DocumentModel dossierDoc = session.getDocument(dossierRef);
                Dossier dossier = dossierDoc.getAdapter(Dossier.class);
                idMinistereAttributaireCourant = dossier.getIdMinistereAttributaireCourant();
            }
        }

        return idMinistereAttributaireCourant;
    }

    @Override
    public void addStepAfterRejectReattribution(final CoreSession session, final String routingTaskId) {
        // Récupere le step courant
        UnrestrictedGetDocumentRunner getDoc = new UnrestrictedGetDocumentRunner(
            session,
            STSchemaConstant.ROUTING_TASK_SCHEMA
        );
        final DocumentModel currentStepDoc = getDoc.getById(routingTaskId);
        final String parentDocId = (String) currentStepDoc.getParentRef().reference();

        // on ajoute pas d'étape si on est dans une branche parallèle
        if (checkStepIsParallel(session, currentStepDoc)) {
            throw new ReponsesException("FeuilleRouteService : Insertion d'étape impossible");
        }

        ReponsesRouteStep currentStep = currentStepDoc.getAdapter(ReponsesRouteStep.class);
        IdRef routeRef = new IdRef(currentStep.getDocumentRouteId());
        String idMinistereAttributaireCourant = getIdMinistereAttributaireCourant(session, routeRef);

        if (idMinistereAttributaireCourant == null) {
            throw new ReponsesException("Le ministère attributaire n'a pas pu être retrouvé");
        } else {
            // récupération des étapes contenues dans parent
            final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
            final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(parentDocId, session);

            final int currentStepIndex = steps.indexOf(currentStepDoc);

            final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
            final OrganigrammeNode posteBdc = STServiceLocator
                .getSTPostesService()
                .getPosteBdcInEntite(idMinistereAttributaireCourant);
            String stepMailboxId = null;
            if (posteBdc != null) {
                final String mailboxId = mailboxPosteService.getPosteMailboxId(posteBdc.getId());
                mailboxPosteService.getOrCreateMailboxPoste(
                    session,
                    mailboxPosteService.getPosteIdFromMailboxId(mailboxId)
                );
                stepMailboxId = mailboxId;
            }
            if (stepMailboxId == null) {
                throw new ReponsesException("Le ministère attributaire ne comporte pas de poste BDC");
            }

            // Ajout du step reattribution avec le poste BDC
            final FeuilleRouteStep newStep = documentRoutingService.createNewRouteStep(
                session,
                stepMailboxId,
                getTypeEtapeAttribution()
            );
            documentRoutingService.addRouteElementToRoute(
                currentStepDoc.getParentRef(),
                currentStepIndex + 1,
                newStep,
                session
            );
        }
    }

    @Override
    public Calendar getDateDebutEcheance(final CoreSession session, final STDossier dossier) {
        // la date de début pour le calcul des échéances est la date de
        // publication JO de la question.
        final Dossier dossierDoc = dossier.getDocument().getAdapter(Dossier.class);
        return dossierDoc.getQuestion(session).getDatePublicationJO();
    }

    /**
     *
     * Retourne la liste des dossiers ayant été validé depuis moins de 7 jours sur les poste en paramètre.
     *
     * @param session
     *            la session de l'utilisateur. L'utilsateur courant doit avoir les postes accédés
     * @param poste
     *            ou rechercher les validations
     * @return une liste des dossiers validés (sans doublon)
     *
     */
    @Override
    public List<Dossier> getLastWeekValidatedDossiers(final CoreSession session, final Collection<String> posteIds) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();

        final Set<String> mailboxIds = mailboxPosteService.getMailboxPosteIdSetFromPosteIdSet(posteIds);
        if (CollectionUtils.isEmpty(mailboxIds)) {
            throw new NuxeoException(
                "les mailbox des postes" + StringUtils.join(posteIds, ",") + " n'ont pas été trouvées"
            );
        }

        DateTime dateExpiration = new DateTime();
        dateExpiration = dateExpiration.minusDays(7);
        final StringBuffer selectEtapeValide = new StringBuffer()
            .append("select r.ecm:uuid AS id from RouteStep AS r WHERE r.rtsk:dateFinEtape > DATE ")
            .append(DateUtil.convert(dateExpiration))
            .append(" AND r.ecm:currentLifeCycleState IN ('done')")
            .append(" AND r.rtsk:distributionMailboxId IN (")
            .append(StringHelper.getQuestionMark(mailboxIds.size()))
            .append(")");
        final List<DocumentModel> etapes = queryDocs(
            session,
            selectEtapeValide.toString(),
            mailboxIds.toArray(new String[mailboxIds.size()]),
            null
        );

        // Récupération des dossiers associés à cette étape.
        final Set<String> idDossiers = new HashSet<>();
        final List<Dossier> dossiers = new ArrayList<>();
        for (final DocumentModel etapeDoc : etapes) {
            final FeuilleRouteStep etape = etapeDoc.getAdapter(FeuilleRouteStep.class);
            for (final DocumentModel doc : etape.getAttachedDocuments(session)) {
                if (idDossiers.add(doc.getId())) {
                    dossiers.add(doc.getAdapter(Dossier.class));
                }
            }
        }
        return dossiers;
    }

    @Override
    public List<SSRouteStep> getLastDayValidatedSteps(final CoreSession session) {
        DateTime dateExpiration = new DateTime();
        dateExpiration = dateExpiration.minusDays(1);
        final StringBuffer selectEtapeValide = new StringBuffer()
            .append(
                "select r.ecm:uuid AS id from " +
                FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP +
                " AS r WHERE r.rtsk:dateDebutEtape > DATE "
            )
            .append(DateUtil.convert(dateExpiration))
            .append(" AND r.ecm:currentLifeCycleState IN ('running')");
        final List<DocumentModel> etapes = queryDocs(session, selectEtapeValide.toString(), new Object[] {}, null);

        // Récupération des dossiers associés à cette étape.
        final List<SSRouteStep> steps = new ArrayList<>();
        for (final DocumentModel stepDoc : etapes) {
            final SSRouteStep step = stepDoc.getAdapter(SSRouteStep.class);
            steps.add(step);
        }
        return steps;
    }

    @Override
    public Map<String, List<String>> getRunningStepsSinceDaysByPoste(final CoreSession session, int days) {
        LocalDate since = LocalDate.now().minusDays(days);
        String query =
            "select r.ecm:uuid AS id, r.rtsk:distributionMailboxId AS mailboxId from " +
            FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP +
            " AS r WHERE r.rtsk:dateDebutEtape <= DATE '" +
            SolonDateConverter.DATE_DASH_REVERSE.format(since) +
            "' AND r.ecm:currentLifeCycleState IN ('running')";

        List<ImmutablePair<String, String>> listMap = doUFNXQLQueryAndMapping(
            session,
            query,
            null,
            (Map<String, Serializable> rowData) ->
                new ImmutablePair<>((String) rowData.get("id"), (String) rowData.get("mailboxId"))
        );

        return listMap
            .stream()
            .collect(groupingBy(ImmutablePair::getRight)) // group by id poste
            .entrySet()
            .stream()
            .collect(toMap(Map.Entry::getKey, e -> getLeftAsList(e.getValue()))); // collect id routestep from ImmutablePair<idRouteStep,idPoste>
    }

    private List<String> getLeftAsList(List<ImmutablePair<String, String>> list) {
        return list.stream().map(ImmutablePair::getLeft).collect(toList());
    }

    @Override
    public String getTypeEtapeAttribution() {
        return VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION;
    }

    @Override
    public String getTypeEtapeInformation() {
        return VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION;
    }

    @Override
    public String getTypeEtapeImpression() {
        return VocabularyConstants.ROUTING_TASK_TYPE_IMPRESSION;
    }

    @Override
    protected Boolean isTypeEtapeReattribution(final String typeEtape) {
        return VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(typeEtape);
    }

    @Override
    public boolean canDistributeStep(
        final CoreSession session,
        final SSRouteStep routeStep,
        final List<DocumentModel> docs
    ) {
        return !VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION.equals(routeStep.getType());
    }

    @Override
    public void doValidationAutomatiqueOperation(
        final CoreSession session,
        final SSRouteStep routeStep,
        final List<DocumentModel> docs
    ) {
        final SSJournalService journalService = SSServiceLocator.getSSJournalService();
        updateRouteStepFieldAfterValidation(session, routeStep, docs, null);

        // s'exécute uniquement si les DossierLink n'ont pas été distribués
        if (!canDistributeStep(session, routeStep, docs)) {
            if (VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION.equals(routeStep.getType())) {
                routeStep.setValidationStatus(
                    SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_VALIDE_AUTOMATIQUEMENT_VALUE
                );
                routeStep.setDateFinEtape(Calendar.getInstance());
                // Journalisation de l'évènement
                for (final DocumentModel doc : docs) {
                    journalService.journaliserActionEtapeFDR(
                        session,
                        routeStep,
                        doc,
                        STEventConstant.DOSSIER_AVIS_FAVORABLE,
                        STEventConstant.COMMENT_AVIS_FAVORABLE
                    );
                }
            }
        }

        // S'exécute dans tous les cas
        if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(routeStep.getType())) {
            final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
            final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
            final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
            // Changement de ministère
            for (final DocumentModel doc : docs) {
                final Dossier dossier = doc.getAdapter(Dossier.class);
                final String idMinistereReattribution = dossier.getIdMinistereReattribution();

                if (idMinistereReattribution != null && !idMinistereReattribution.isEmpty()) {
                    // Commentaire setNouveauMinistereCourant : On ne met à jour
                    // qu'au moment de la validation de l'étape de validation de
                    // réattribution
                    // dossierDistributionService.setNouveauMinistereCourant(session,
                    // doc, idMinistereReattribution);
                    final String ministerePrec = dossier.getIdMinistereAttributairePrecedent();
                    // Changement du ministère dans le champ dénormalisé des
                    // DossierLink
                    final List<DocumentModel> listDossiersLink = corbeilleService.findDossierLink(session, doc.getId());
                    for (final DocumentModel dossierLinkDoc : listDossiersLink) {
                        final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);

                        // Renseigne l'ID et le label du ministère interpellé
                        // courant
                        dossierLink.setIdMinistereAttributaire(idMinistereReattribution);
                        final OrganigrammeNode ministereNode = ministeresService.getEntiteNode(
                            idMinistereReattribution
                        );
                        String intituleMinistere = "";
                        if (ministereNode != null) {
                            intituleMinistere = ministereNode.getLabel();
                        }
                        dossierLink.setIntituleMinistere(intituleMinistere);
                        session.saveDocument(dossierLinkDoc);

                        dossierDistributionService.correctCounterAfterChangeOnMinistereAttributaire(
                            session,
                            dossierLink,
                            ministerePrec
                        );
                    }
                }
            }
        }
    }

    @Override
    public boolean isRootStep(final CoreSession session, final String routingTaskId) {
        final DocumentModel stepDoc = new UnrestrictedGetDocumentRunner(session).getById(routingTaskId);
        final DocumentModel parentDoc = session.getDocument(stepDoc.getParentRef());
        return parentDoc.getType().equals(SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE);
    }

    @Override
    public void updateApplicationFieldsAfterValidation(
        final CoreSession session,
        final DocumentModel routeStepDoc,
        final List<DocumentModel> dossierDocList,
        final List<STDossierLink> caseLinkList
    ) {
        /*
         * Il n'y a pas de champ spécifique à l'application Réponses sur les  étapes de feuille de route
         */

    }

    @Override
    public void sendMailAfterDistribution(final CoreSession session, final SSRouteStep routeStep) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final String texte = paramService.getParametreValue(
            session,
            STParametreConstant.TEXTE_MAIL_NOTIFICATION_CREATION_TACHE
        );
        final String objet = paramService.getParametreValue(
            session,
            STParametreConstant.OBJET_MAIL_NOTIFICATION_CREATION_TACHE
        );

        final List<String> listIdDossiers = routeStep.getFeuilleRoute(session).getAttachedDocuments();

        // Détermine la liste des utilisateurs du poste

        final String mailboxId = routeStep.getDistributionMailboxId();
        final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(mailboxId);
        final PosteNode posteNode = STServiceLocator.getSTPostesService().getPoste(posteId);
        List<STUser> userList = posteNode.getUserList();

        // Filtrage, sauf pour les étapes "Pour information"
        if (!VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION.equals(routeStep.getType())) {
            userList = ReponsesServiceLocator.getProfilUtilisateurService().getFilteredUserList(session, userList);
        }

        // Envoi du mail
        final STMailService mailService = STServiceLocator.getSTMailService();
        mailService.sendHtmlMailToUserListWithLinkToDossiers(session, userList, objet, texte, listIdDossiers);
    }

    @Override
    public void sendDailyDistributionMail(final CoreSession session) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final STMailService mailService = STServiceLocator.getSTMailService();
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        final STPostesService postesService = STServiceLocator.getSTPostesService();
        final ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator.getProfilUtilisateurService();
        final String objet = paramService.getParametreValue(
            session,
            ReponsesParametreConstant.OBJET_DAILY_DISTRIBUTION
        );
        final String texte = paramService.getParametreValue(
            session,
            ReponsesParametreConstant.TEXTE_DAILY_DISTRIBUTION
        );

        final List<DossierLink> linkList = getLastDayDossierLink(session);
        final Map<String, List<DossierLink>> posteToLink = new HashMap<>();

        for (final DossierLink link : linkList) {
            final String mailboxId = link.getDistributionMailbox();
            final String posteId = mailboxPosteService.getPosteIdFromMailboxId(mailboxId);

            List<DossierLink> listDossierLinkPoste = posteToLink.get(posteId);
            if (listDossierLinkPoste == null) {
                listDossierLinkPoste = new ArrayList<>();
            }
            listDossierLinkPoste.add(link);
            posteToLink.put(posteId, listDossierLinkPoste);
        }

        for (final Entry<String, List<DossierLink>> entry : posteToLink.entrySet()) {
            final Map<String, Object> mailParams = new HashMap<>();

            final PosteNode poste = postesService.getPoste(entry.getKey());

            mailParams.put("poste", poste.getLabel());

            List<STUser> userList = poste.getUserList();
            userList =
                profilUtilisateurService.getFilteredUserList(
                    session,
                    userList,
                    ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_JOURNALIER
                );

            if (!userList.isEmpty()) {
                // liste des dossiers concernés
                final List<Map<String, String>> dossierList = new ArrayList<>();
                for (final DossierLink link : entry.getValue()) {
                    final Map<String, String> questionMap = new HashMap<>();
                    questionMap.put("question", link.getSourceNumeroQuestion());
                    final String corbeille = ResourceHelper.getString(link.getRoutingTaskLabel());
                    questionMap.put("corbeille", corbeille);
                    dossierList.add(questionMap);
                }
                mailParams.put("liste_question", dossierList);

                // Envoi du mail
                mailService.sendTemplateHtmlMailToUserList(session, userList, objet, texte, mailParams);
            }
        }
    }

    /**
     * Récupère les dossiers link dont la date est après la date de la veille
     *
     * @param session
     * @return
     *
     */
    private List<DossierLink> getLastDayDossierLink(final CoreSession session) {
        final Calendar dateExpiration = Calendar.getInstance();
        dateExpiration.add(Calendar.DAY_OF_MONTH, -1);
        final StringBuffer selectLinkValide = new StringBuffer()
            .append("select l.ecm:uuid AS id from DossierLink AS l WHERE l.cslk:date > ? ")
            .append(" AND l.ecm:currentLifeCycleState IN ('todo')");

        String schemas = String.join(
            ",",
            CaseDistribConstants.DISTRIBUTION_SCHEMA,
            DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA
        );
        final List<DocumentModel> dossierLinks = queryDocs(
            session,
            selectLinkValide.toString(),
            new Object[] { dateExpiration },
            new PrefetchInfo(schemas)
        );

        // Récupération des dossiers links
        final List<DossierLink> linkList = new ArrayList<>();
        for (final DocumentModel linkDoc : dossierLinks) {
            final DossierLink link = linkDoc.getAdapter(DossierLink.class);
            linkList.add(link);
        }
        return linkList;
    }

    @Override
    public String getDossierLinkListToEmailForAutomaticValidationQuery() {
        /**
         * Requête récupérant les dossiers link dont l'option validation automatique n'a pas été sélectionné, dont la
         * date d'échéance est dépassé et qui n'a pas déjà été validée.
         */
        final StringBuilder getDossierLinkListToValidateQuery = new StringBuilder("SELECT * FROM ")
            .append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE)
            .append(" WHERE ")
            .append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH)
            .append(" = 'todo' AND  ")
            .append(STSchemaConstant.ACTIONABLE_CASE_LINK_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.ACTIONABLE_CASE_LINK_AUTOMATIC_VALIDATION_PROPERTY)
            .append(" = 0 AND ")
            .append(DossierConstants.DOSSIER_REPONSES_LINK_PREFIX)
            .append(":")
            .append(STDossierLinkConstant.DOSSIER_LINK_IS_MAIL_SEND_PROPERTY)
            .append(" = 0 AND ")
            .append(STSchemaConstant.ACTIONABLE_CASE_LINK_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.ACTIONABLE_CASE_LINK_DUE_DATE_PROPERTY)
            .append(" < TIMESTAMP '%s' ");
        return getDossierLinkListToValidateQuery.toString();
    }

    protected Map<String, Object> getMailTemplateParameters(ActionableCaseLink acl) {
        return Optional
            .ofNullable(acl.getDocument().getAdapter(DossierLink.class))
            .map(dossierLink -> ImmutableMap.of("numero_question", (Object) dossierLink.getNumeroQuestion()))
            .orElseGet(
                () -> {
                    LOGGER.warn("Erreur de récupération du numero de question");
                    return ImmutableMap.of();
                }
            );
    }

    @Override
    public void initDirectionFdr(final CoreSession session) {
        final String feuilleRouteModelFolderId = ReponsesServiceLocator
            .getFeuilleRouteModelService()
            .getFeuilleRouteModelFolderId(session);
        final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
        final String query = "SELECT * FROM FeuilleRoute WHERE ecm:parentId='" + feuilleRouteModelFolderId + "'";
        final List<DocumentModel> feuilleRouteModelList = session.query(query);
        for (final DocumentModel feuilleRouteDoc : feuilleRouteModelList) {
            final ReponsesFeuilleRoute fdr = feuilleRouteDoc.getAdapter(ReponsesFeuilleRoute.class);
            final String ministere = fdr.getMinistere();
            final SSFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(SSFeuilleRoute.class);
            final List<RouteTableElement> listRouteTableElement = SSServiceLocator
                .getDocumentRoutingService()
                .getFeuilleRouteElements(feuilleRoute, session);
            boolean directionTrouve = false;
            for (int i = listRouteTableElement.size() - 1; i >= 0; i--) {
                final SSRouteStep routeStep = listRouteTableElement
                    .get(i)
                    .getElement()
                    .getDocument()
                    .getAdapter(SSRouteStep.class);
                if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeStep.getDocument().getType())) {
                    final PosteNode poste = getPosteByStep(routeStep);
                    final List<OrganigrammeNode> listDirections = usService.getDirectionFromPoste(poste.getId());
                    directionTrouve = initDirectionPiloteInFdr(session, listDirections, ministere, fdr);
                }
                if (directionTrouve) {
                    break;
                }
            }
        }
    }

    /**
     * Parcourt la liste des entités parentes des directions pour trouver la direction associée au ministere
     *
     * @param session
     * @param listDirections
     * @param ministereId
     * @param fdr
     * @return
     *
     */
    private boolean initDirectionPiloteInFdr(
        final CoreSession session,
        final List<OrganigrammeNode> listDirections,
        final String ministereId,
        final ReponsesFeuilleRoute fdr
    ) {
        for (final OrganigrammeNode direction : listDirections) {
            final UniteStructurelleNode usNode = (UniteStructurelleNode) direction;
            final List<String> ministeres = usNode.getParentEntiteIds();
            if (ministeres.contains(ministereId)) {
                fdr.setIdDirectionPilote(direction.getId());
                fdr.setIntituleDirectionPilote(direction.getLabel());
                fdr.save(session);
                return true;
            }
        }
        return false;
    }

    /**
     * Recupère le poste associé à l'étape passée en paramètre
     *
     * @param step
     * @return
     *
     */
    private PosteNode getPosteByStep(final SSRouteStep step) {
        final String distributionMailboxId = step.getDistributionMailboxId();
        final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(distributionMailboxId);

        return STServiceLocator.getSTPostesService().getPoste(posteId);
    }

    @Override
    protected void checkArbitrage(String type) throws ReponsesException {
        if (VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(type)) {
            throw new ReponsesException("feedback.reponses.route.error.retour.arbitrage");
        }
    }

    @Override
    protected String getLabelForStep(DocumentModel stepDoc) {
        SSRouteStep step = stepDoc.getAdapter(SSRouteStep.class);
        String labelPoste = ObjectHelper.requireNonNullElseGet(
            step.getPosteLabel(),
            () -> getPosteByStep(step).getLabel()
        );
        String labelStepType = VocabularyConstants.LIST_LIBELLE_ROUTING_TASK_PAR_ID.get(step.getType());
        return labelStepType + " - " + labelPoste;
    }

    private List<DocumentModel> queryDocs(
        CoreSession session,
        String query,
        Object[] params,
        PrefetchInfo prefetchInfo
    ) {
        return QueryHelper.doUFNXQLQueryAndFetchForDocuments(session, query, params, 0, 0, prefetchInfo);
    }

    @Override
    public String getDirectionsRunningSteps(CoreSession session, Dossier dossier) {
        List<DocumentModel> runningStepDocList = getRunningSteps(session, dossier.getLastDocumentRoute());
        StringBuilder libelleDirections = new StringBuilder();
        if (runningStepDocList != null) {
            for (DocumentModel runningStepDoc : runningStepDocList) {
                SSRouteStep routeStep = runningStepDoc.getAdapter(SSRouteStep.class);
                String label = getLabelFromRoute(routeStep);

                if (libelleDirections.toString().isEmpty()) {
                    libelleDirections.append(label);
                } else {
                    libelleDirections.append(", ").append(label);
                }
            }
        }

        return libelleDirections.toString();
    }

    private String getLabelFromRoute(SSRouteStep routeStep) {
        if (routeStep.getDirectionLabel() != null) {
            return routeStep.getDirectionLabel();
        }
        String[] splitedMailBoxId = routeStep.getDistributionMailboxId().split("-");
        final List<OrganigrammeNode> uniteStructurelleList = STServiceLocator
            .getSTUsAndDirectionService()
            .getDirectionFromPoste(splitedMailBoxId[splitedMailBoxId.length - 1]);
        StringBuilder libelleDirections = new StringBuilder();
        for (final OrganigrammeNode node : uniteStructurelleList) {
            if (libelleDirections.toString().isEmpty()) {
                libelleDirections.append(node.getLabel());
            } else {
                libelleDirections.append(", ").append(node.getLabel());
            }
        }

        return libelleDirections.toString();
    }
}
