package fr.dila.reponses.core.service;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingLineDetail;
import fr.dila.reponses.api.logging.ReponsesLoggingStatusEnum;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.api.service.ReponsesMigrationService;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.logger.MigrationLogger;
import fr.dila.st.core.service.AbstractPersistenceDefaultComponent;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Query;
import javax.transaction.Status;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Service de migration de REPONSES
 *
 */
public class ReponsesMigrationServiceImpl
    extends AbstractPersistenceDefaultComponent
    implements ReponsesMigrationService {
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesMigrationServiceImpl.class);
    // Constantes de chaines de caractères pour logs
    private static final String CLOSED_BRACKET = "]";
    private static final String BRACKET_VERS_BRACKET = "] vers [";
    private static final String MIGRATION_DOSSIER = "Migration du dossier ";
    private static final String END_LOG = " : Fin ";
    private static final String SPACE = " ";

    private static final String QUERY_STEP_FROM_STEPID =
        "SELECT * FROM " +
        STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE +
        " WHERE " +
        CaseLinkConstants.STEP_DOCUMENT_ID_FIELD +
        " = '?' AND " +
        STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH +
        " = 'todo' ";
    /**
     * Requête de recherche pour tous les dossiers dont la signature est valide pour la législature en cours et pour
     * lesquels il y a une étape "Pour transmission aux assemblées" à venir <br>
     * <br>
     *
     * select dossier_reponse.id from reponse inner join dossier_reponse inner join question on question.id =
     * dossier_reponse.iddocumentquestion on reponse.id = dossier_reponse.iddocumentreponse where
     * dossier_reponse.lastdocumentroute in (select documentrouteid from routing_task inner join misc on routing_task.id
     * = misc.id where routing_task.type = 11 and misc.lifecyclestate = 'ready') and legislaturequestion = 14 and
     * issignaturevalide = 1 and etatquestion = 'en cours'
     */
    private static final String QUERY_DOSSIERS_IDS_SQL =
        "select dossier_reponse.id as id from reponse inner join dossier_reponse inner join question on question.id = dossier_reponse.iddocumentquestion on reponse.id = dossier_reponse.iddocumentreponse where dossier_reponse.lastdocumentroute in (select documentrouteid from routing_task inner join misc on routing_task.id = misc.id where routing_task.type = 11 and misc.lifecyclestate = 'ready') and signature is not null and datepublicationjoreponse is null";

    @Override
    public void migrateAllCloseDossierMinistereRattachement(
        final CoreSession session,
        final String oldMinistereId,
        final OrganigrammeNode newMinistere,
        final ReponsesLoggingLine reponsesLoggingLine,
        final ReponsesLogging reponsesLogging
    ) {
        final OrganigrammeNode oldMinistere = STServiceLocator.getSTMinisteresService().getEntiteNode(oldMinistereId);

        if (reponsesLoggingLine.getEndCount() == null) {
            reponsesLoggingLine.setEndCount(0L);
        }
        reponsesLoggingLine.save(session);
        saveAndCommitTransaction(session);

        final ReponsesLoggingLineDetail lineDetail1 = createLoggingDetail(session, reponsesLoggingLine, "1");

        lineDetail1.setMessage(
            "[STATISTIQUES] Changement du ministère de rattachement de [" +
            oldMinistere.getLabel() +
            BRACKET_VERS_BRACKET +
            newMinistere.getLabel() +
            CLOSED_BRACKET
        );

        addLogToReponsesLoggingLineDetail(
            lineDetail1,
            session,
            "[STATISTIQUES] Debut changement du ministère de rattachement de [" +
            oldMinistere.getLabel() +
            BRACKET_VERS_BRACKET +
            newMinistere.getLabel() +
            CLOSED_BRACKET,
            ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC
        );

        saveAndCommitTransaction(session);
        final List<Integer> resultUpdate = new ArrayList<>();
        // Changement des ministereRattachement valant oldMinistereId vers newMinistereId
        try {
            accept(
                true,
                entityManager -> {
                    final Query nativeQuery = entityManager.createNativeQuery(
                        " UPDATE QUESTION SET intituleministererattachement = :intituleministererattachement, idministererattachement = :idministererattachement  WHERE  idministererattachement = :oldidministererattachement  "
                    );
                    nativeQuery.setParameter("idministererattachement", newMinistere.getId());
                    nativeQuery.setParameter("intituleministererattachement", newMinistere.getLabel());
                    nativeQuery.setParameter("oldidministererattachement", oldMinistereId);
                    int nbUpdate = nativeQuery.executeUpdate();
                    resultUpdate.add(new Integer(nbUpdate));
                    entityManager.flush();
                }
            );
            lineDetail1.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
            reponsesLogging.save(session);
            addLogToReponsesLoggingLineDetail(
                lineDetail1,
                session,
                "[STATISTIQUES] Changement du ministère de rattachement de [" +
                oldMinistere.getLabel() +
                BRACKET_VERS_BRACKET +
                newMinistere.getLabel() +
                "] : " +
                resultUpdate.get(0) +
                " questions mises à jour",
                ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC
            );
        } catch (Exception exc) {
            saveAndCommitTransaction(session);
            lineDetail1.setStatus(ReponsesLoggingStatusEnum.FAILURE);
            addLogToReponsesLoggingLineDetail(
                lineDetail1,
                session,
                "Echec de changement du ministère de rattachement de [" +
                oldMinistere.getLabel() +
                BRACKET_VERS_BRACKET +
                newMinistere.getLabel() +
                CLOSED_BRACKET,
                ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC
            );
            saveAndCommitTransaction(session);
            throw new NuxeoException(exc);
        }
        addLogToReponsesLoggingLineDetail(
            lineDetail1,
            session,
            "[STATISTIQUES] Fin changement du ministère de rattachement de [" +
            oldMinistere.getLabel() +
            BRACKET_VERS_BRACKET +
            newMinistere.getLabel() +
            CLOSED_BRACKET,
            ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC
        );
        saveAndCommitTransaction(session);
    }

    @Override
    public void migrateDossierClos(
        final CoreSession session,
        final String oldMinistereId,
        final OrganigrammeNode newMinistere,
        final ReponsesLoggingLine reponsesLoggingLine,
        final ReponsesLogging reponsesLogging
    ) {
        final UpdateTimbreService upTimbreServ = ReponsesServiceLocator.getUpdateTimbreService();
        final OrganigrammeNode oldMinistere = STServiceLocator.getSTMinisteresService().getEntiteNode(oldMinistereId);

        final String queryUpdateDossiersClos =
            "UPDATE QUESTION SET intituleministererattachement = :intituleministererattachement, idministererattachement = :idministererattachement" +
            " WHERE id in (" +
            upTimbreServ.getQueryClosedQuestionsForMinistere(oldMinistereId) +
            ")";

        if (reponsesLoggingLine.getEndCount() == null) {
            reponsesLoggingLine.setEndCount(0L);
        }
        reponsesLoggingLine.save(session);
        saveAndCommitTransaction(session);

        final ReponsesLoggingLineDetail lineDetail1 = createLoggingDetail(session, reponsesLoggingLine, "1");

        lineDetail1.setMessage(
            "Migration des questions closes de [" +
            oldMinistere.getLabel() +
            BRACKET_VERS_BRACKET +
            newMinistere.getLabel() +
            CLOSED_BRACKET
        );

        addLogToReponsesLoggingLineDetail(
            lineDetail1,
            session,
            "Debut migration des questions closes de [" +
            oldMinistere.getLabel() +
            BRACKET_VERS_BRACKET +
            newMinistere.getLabel() +
            CLOSED_BRACKET,
            ReponsesLogEnumImpl.MIGRATE_TIMBRE_TEC
        );

        saveAndCommitTransaction(session);
        Long nbQuestionsBefore = reponsesLoggingLine.getEndCount();

        try {
            accept(
                true,
                entityManager -> {
                    final Query nativeQuery = entityManager.createNativeQuery(queryUpdateDossiersClos);
                    nativeQuery.setParameter("idministererattachement", newMinistere.getId());
                    nativeQuery.setParameter("intituleministererattachement", newMinistere.getLabel());
                    Long nbUpdate = Long.valueOf(nativeQuery.executeUpdate());
                    reponsesLoggingLine.setEndCount(reponsesLoggingLine.getEndCount() + nbUpdate);
                    reponsesLogging.setCloseEndCount(reponsesLogging.getCloseEndCount() + nbUpdate);
                    entityManager.flush();
                }
            );
            lineDetail1.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
            reponsesLogging.save(session);
            Long nbQuestionsCloseMigrees = reponsesLoggingLine.getEndCount() - nbQuestionsBefore;
            addLogToReponsesLoggingLineDetail(
                lineDetail1,
                session,
                "Migration des questions closes de [" +
                oldMinistere.getLabel() +
                BRACKET_VERS_BRACKET +
                newMinistere.getLabel() +
                "] : " +
                nbQuestionsCloseMigrees +
                " questions mises à jour",
                ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC
            );
        } catch (Exception exc) {
            saveAndCommitTransaction(session);
            lineDetail1.setStatus(ReponsesLoggingStatusEnum.FAILURE);
            addLogToReponsesLoggingLineDetail(
                lineDetail1,
                session,
                "Echec de migration des questions closes de [" +
                oldMinistere.getLabel() +
                BRACKET_VERS_BRACKET +
                newMinistere.getLabel() +
                CLOSED_BRACKET,
                ReponsesLogEnumImpl.MIGRATE_TIMBRE_TEC
            );
            saveAndCommitTransaction(session);
            throw new NuxeoException(exc);
        }
        addLogToReponsesLoggingLineDetail(
            lineDetail1,
            session,
            "Fin migration questions closes de [" +
            oldMinistere.getLabel() +
            BRACKET_VERS_BRACKET +
            newMinistere.getLabel() +
            CLOSED_BRACKET,
            ReponsesLogEnumImpl.MIGRATE_TIMBRE_TEC
        );
        saveAndCommitTransaction(session);
    }

    @Override
    public void migrateAllDossiersForReaffectation(
        final CoreSession session,
        final OrganigrammeNodeTimbreDTO oldMinistereDto,
        final OrganigrammeNode newMinistere,
        final String oldMailboxId,
        final String newMailboxId,
        ReponsesLoggingLine reponsesLoggingLine,
        final ReponsesLogging reponsesLogging,
        final Mailbox newMailbox,
        final OrganigrammeNode newPosteBdc
    ) {
        // Services
        final STLockService lockService = STServiceLocator.getSTLockService();
        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        final UpdateTimbreService updateTimbreService = ReponsesServiceLocator.getUpdateTimbreService();
        final FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
        final JetonService jetonService = ReponsesServiceLocator.getJetonService();
        final DossierDistributionService distributionService = ReponsesServiceLocator.getDossierDistributionService();
        final OrganigrammeNode oldMinistere = STServiceLocator
            .getSTMinisteresService()
            .getEntiteNode(oldMinistereDto.getId());

        reponsesLoggingLine.setEndCount(0L);
        reponsesLoggingLine.save(session);

        // Commit de la transaction en cours
        saveAndCommitTransaction(session);

        final String newMinistereId = newMinistere.getId();
        final String newMinistereLabel = newMinistere.getLabel();

        migrateDossiersEnReattribution(
            session,
            oldMinistereDto,
            reponsesLoggingLine,
            corbeilleService,
            oldMinistere,
            newMinistereId,
            newMinistereLabel
        );

        final ReponsesLoggingLineDetail lineDetail2 = createLoggingDetail(session, reponsesLoggingLine, "2");

        lineDetail2.setMessage("Récuperation des dossiers de [" + oldMinistere.getLabel() + CLOSED_BRACKET);

        // Récupération des dossiers contenu dans l'ancien ministère
        List<DocumentModel> dossierList = null;
        try {
            dossierList =
                updateTimbreService.getMigrableDossiersForMinistere(session, oldMinistereDto.getId().toString());
            if (dossierList.size() > 1) {
                addLogToReponsesLoggingLineDetail(
                    lineDetail2,
                    session,
                    dossierList.size() + " dossiers trouvés",
                    STLogEnumImpl.GET_DOSSIER_TEC
                );
            } else {
                addLogToReponsesLoggingLineDetail(
                    lineDetail2,
                    session,
                    dossierList.size() + " dossier trouvé",
                    STLogEnumImpl.GET_DOSSIER_TEC
                );
            }
        } catch (final NuxeoException ce) {
            lineDetail2.setStatus(ReponsesLoggingStatusEnum.FAILURE);
            addLogToReponsesLoggingLineDetail(
                lineDetail2,
                session,
                "Erreur lors de la migration des dossiers : Dossiers non trouvés",
                STLogEnumImpl.FAIL_GET_DOSSIER_TEC
            );
            throw new NuxeoException("Erreur lors de la migration des dossiers : Dossiers non trouvés", ce);
        }

        lineDetail2.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
        lineDetail2.save(session);

        saveAndCommitTransaction(session);

        // Liste des feuilles de route déjà traitées
        final Set<String> listFeuilleRoute = new HashSet<>();

        Boolean changeMinistere = Boolean.FALSE;
        for (DocumentModel dossierDoc : dossierList) {
            changeMinistere = Boolean.FALSE;
            final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            final Question question = dossier.getQuestion(session);
            final Reponse reponse = dossier.getReponse(session);
            final String sourceNumeroQuestion = question.getSourceNumeroQuestion();

            final ReponsesLoggingLineDetail lineDetail3 = createLoggingDetail(session, reponsesLoggingLine, "3");

            lineDetail3.setMessage("Dossier " + sourceNumeroQuestion);

            addLogToReponsesLoggingLineDetail(
                lineDetail3,
                session,
                MIGRATION_DOSSIER + sourceNumeroQuestion + " : Début ",
                STLogEnumImpl.MIGRATE_DOSSIER_TEC
            );

            try {
                final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
                final SSFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(SSFeuilleRoute.class);

                // Si le dossier est vérouillé on le déverouille (+ sa feuille de route)
                if (session.getLockInfo(dossierDoc.getRef()) != null) {
                    lockService.unlockDoc(session, dossierDoc);
                    lockService.unlockDoc(session, feuilleRouteDoc);
                }

                if (reponse != null && !reponse.isSignee() && reponse.getDateJOreponse() == null) {
                    // migration ministere attributaire ssi la reponse n'est pas signée ni publiée
                    addLogToReponsesLoggingLineDetail(
                        lineDetail3,
                        session,
                        MIGRATION_DOSSIER + sourceNumeroQuestion + " : Changement du ministère attributaire ",
                        STLogEnumImpl.MIGRATE_DOSSIER_TEC
                    );

                    dossier.addHistoriqueAttribution(
                        session,
                        STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_REAFFECTATION,
                        newMinistereId.toString()
                    );

                    // Changement du ministère courant
                    dossierDoc =
                        distributionService.setNouveauMinistereCourant(session, dossierDoc, newMinistereId.toString());

                    addLogToReponsesLoggingLineDetail(
                        lineDetail3,
                        session,
                        MIGRATION_DOSSIER + sourceNumeroQuestion + " : création des jetons ",
                        STLogEnumImpl.MIGRATE_DOSSIER_TEC
                    );

                    // Création du jeton
                    jetonService.addDocumentInBasket(
                        session,
                        STWebserviceConstant.CHERCHER_ATTRIBUTIONS,
                        question.getOrigineQuestion(),
                        dossierDoc,
                        sourceNumeroQuestion,
                        null,
                        null
                    );
                    jetonService.addDocumentInBasket(
                        session,
                        STWebserviceConstant.CHERCHER_ATTRIBUTIONS_DATE,
                        question.getOrigineQuestion(),
                        dossierDoc,
                        sourceNumeroQuestion,
                        null,
                        null
                    );

                    LOGGER.info(session, STLogEnumImpl.CREATE_JETON_TEC, "Fin création des jetons");
                    changeMinistere = Boolean.TRUE;
                } else {
                    changeMinistere = Boolean.FALSE;
                    if (reponse != null && reponse.getDateJOreponse() != null) {
                        addLogToReponsesLoggingLineDetail(
                            lineDetail3,
                            session,
                            "Migration du dossier impossible, la réponse du dossier " +
                            sourceNumeroQuestion +
                            " est publiée",
                            STLogEnumImpl.FAIL_MIGRATE_DOSSIER_TEC
                        );
                    } else if (reponse != null && reponse.isSignee()) {
                        addLogToReponsesLoggingLineDetail(
                            lineDetail3,
                            session,
                            MIGRATION_DOSSIER +
                            sourceNumeroQuestion +
                            " : la réponse est signée, pas de changement du ministère attributaire ",
                            STLogEnumImpl.MIGRATE_DOSSIER_TEC
                        );
                    }
                }

                // gestion allottissement
                if (StringUtils.isNotBlank(dossier.getDossierLot())) {
                    // Pas de modification de la feuille de route et d'envoi du jeton si c'est déjà fait
                    if (listFeuilleRoute.contains(dossier.getLastDocumentRoute())) {
                        lineDetail3.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
                        reponsesLoggingLine.setEndCount(reponsesLoggingLine.getEndCount() + 1);
                        addLogToReponsesLoggingLineDetail(
                            lineDetail3,
                            session,
                            MIGRATION_DOSSIER + sourceNumeroQuestion + END_LOG,
                            STLogEnumImpl.MIGRATE_DOSSIER_TEC
                        );
                        reponsesLoggingLine.save(session);
                        saveAndCommitTransaction(session);
                        continue;
                    }
                    listFeuilleRoute.add(dossier.getLastDocumentRoute());
                }

                addLogToReponsesLoggingLineDetail(
                    lineDetail3,
                    session,
                    MIGRATION_DOSSIER + sourceNumeroQuestion + " : déplacement dans les corbeilles ",
                    STLogEnumImpl.MIGRATE_DOSSIER_TEC
                );

                if (!oldMailboxId.equals(newMailboxId)) {
                    // Récupère les étapes de feuille de route à plat
                    final List<DocumentModel> routeSteps = feuilleRouteModelService.getAllReadyOrRunningRouteElement(
                        feuilleRoute.getDocument().getId(),
                        session
                    );

                    LOGGER.info(
                        session,
                        SSLogEnumImpl.GET_STEP_TEC,
                        "Recuperation step Ready or Running : " + routeSteps.size()
                    );

                    // Changement des postes des étapes BDC
                    for (final DocumentModel stepDoc : routeSteps) {
                        final ReponsesRouteStep step = stepDoc.getAdapter(ReponsesRouteStep.class);

                        LOGGER.info(
                            session,
                            SSLogEnumImpl.GET_STEP_TEC,
                            "Step [id=" +
                            stepDoc.getId() +
                            ", ready=" +
                            step.isReady() +
                            ", running=" +
                            step.isRunning() +
                            CLOSED_BRACKET
                        );
                        if (step.isReady() && step.getDistributionMailboxId().equals(oldMailboxId)) {
                            step.setDistributionMailboxId(newMailboxId);
                            session.saveDocument(stepDoc);
                        }

                        if (step.isRunning()) {
                            // on modifie la step
                            if (step.getDistributionMailboxId().equals(oldMailboxId)) {
                                step.setDistributionMailboxId(newMailboxId);
                                step.setPosteLabel(newPosteBdc.getLabel());

                                step.setMinistereLabel(((EntiteNode) newMinistere).getEdition());
                                step.setMinistereId(((EntiteNode) newMinistere).getId().toString());

                                session.saveDocument(stepDoc);

                                // Changement des ministères et des droits des DossierLink (changement mailbox)
                                migrateCaseLinkForStep(
                                    session,
                                    newMinistere,
                                    newMailbox,
                                    newPosteBdc,
                                    stepDoc.getId(),
                                    dossierDoc,
                                    changeMinistere,
                                    oldMinistereDto.getId()
                                );
                            } else {
                                step.setMinistereLabel(((EntiteNode) newMinistere).getEdition());
                                step.setMinistereId(newMinistere.getId());
                                session.saveDocument(stepDoc);
                                // Changement des ministères et des droits des DossierLink (même mailbox)
                                migrateCaseLinkForStepNoCopy(
                                    session,
                                    newMinistere,
                                    stepDoc.getId(),
                                    dossierDoc,
                                    changeMinistere,
                                    oldMinistereDto.getId()
                                );
                            }
                        }
                    }
                }

                lineDetail3.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
                reponsesLoggingLine.setEndCount(reponsesLoggingLine.getEndCount() + 1);
                reponsesLoggingLine.save(session);
                addLogToReponsesLoggingLineDetail(
                    lineDetail3,
                    session,
                    MIGRATION_DOSSIER + sourceNumeroQuestion + END_LOG,
                    STLogEnumImpl.MIGRATE_DOSSIER_TEC
                );
                saveAndCommitTransaction(session);
            } catch (final Exception e) {
                LOGGER.error(
                    session,
                    STLogEnumImpl.FAIL_MIGRATE_DOSSIER_TEC,
                    "Erreur lors de la migration du dossier : " + sourceNumeroQuestion,
                    e
                );
                saveAndCommitTransaction(session);

                lineDetail3.setStatus(ReponsesLoggingStatusEnum.FAILURE);
                addLogToReponsesLoggingLineDetail(
                    lineDetail3,
                    session,
                    MIGRATION_DOSSIER + sourceNumeroQuestion + " : erreur lors de la migration ",
                    STLogEnumImpl.FAIL_MIGRATE_DOSSIER_TEC
                );
                reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.FAILURE);
            } finally {
                if (reponsesLogging.getEndCount() == null) {
                    reponsesLogging.setEndCount(0L);
                }
                // mise a jour en live du compteur général
                reponsesLogging.setEndCount(reponsesLogging.getEndCount() + 1);
                reponsesLogging.save(session);
                reponsesLoggingLine.save(session);
                saveAndCommitTransaction(session);
            }
        }
    }

    /**
     * Migration de l'ensemble des dossiers en cours de réattribution.
     */
    private void migrateDossiersEnReattribution(
        final CoreSession session,
        final OrganigrammeNodeTimbreDTO oldMinistereDto,
        ReponsesLoggingLine reponsesLoggingLine,
        final ReponsesCorbeilleService corbeilleService,
        final OrganigrammeNode oldMinistere,
        final String newMinistereId,
        final String newMinistereLabel
    ) {
        final ReponsesLoggingLineDetail lineDetail1 = createLoggingDetail(session, reponsesLoggingLine, "1");

        lineDetail1.setMessage(
            "Changement du ministère de réattribution de [" +
            oldMinistere.getLabel() +
            BRACKET_VERS_BRACKET +
            newMinistereLabel +
            CLOSED_BRACKET
        );
        addLogToReponsesLoggingLineDetail(
            lineDetail1,
            session,
            "Debut changement du ministère de réattribution de [" +
            oldMinistere.getLabel() +
            BRACKET_VERS_BRACKET +
            newMinistereLabel +
            CLOSED_BRACKET,
            STLogEnumImpl.MIGRATE_MINISTERE_TEC
        );
        // Changement des ministereReattribution valant oldMinistereId vers newMinistereId
        final List<DocumentModel> dossierReattribution = corbeilleService.findDossierFromMinistereReattribution(
            session,
            oldMinistereDto.getId()
        );
        Boolean firstDossierReatribution = true;
        String listeDossiersChangementMinistereAttributaire = null;
        for (final DocumentModel dossierDoc : dossierReattribution) {
            final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            final Question question = dossier.getQuestion(session);
            final String sourceNumeroQuestion = question.getSourceNumeroQuestion();
            dossier.setIdMinistereReattribution(newMinistereId);

            if (oldMinistereDto.getId().equals(dossier.getIdMinistereAttributairePrecedent())) {
                // migre le ministere attributaire precedent
                dossier.setIdMinistereAttributairePrecedent(newMinistereId);
                if (listeDossiersChangementMinistereAttributaire == null) {
                    listeDossiersChangementMinistereAttributaire = sourceNumeroQuestion;
                } else {
                    listeDossiersChangementMinistereAttributaire =
                        listeDossiersChangementMinistereAttributaire + ", " + sourceNumeroQuestion;
                }
            }
            if (firstDossierReatribution) {
                addLogToReponsesLoggingLineDetail(
                    lineDetail1,
                    session,
                    "Changement du ministère de réattribution des dossiers : " + sourceNumeroQuestion,
                    ReponsesLogEnumImpl.UPDATE_MIN_REAT_TEC
                );
            } else {
                addLogToReponsesLoggingLineDetail(
                    lineDetail1,
                    session,
                    sourceNumeroQuestion,
                    ReponsesLogEnumImpl.UPDATE_MIN_REAT_TEC
                );
            }

            question.save(session);
            dossier.save(session);
            saveAndCommitTransaction(session);
            firstDossierReatribution = false;
        }

        if (listeDossiersChangementMinistereAttributaire != null) {
            addLogToReponsesLoggingLineDetail(
                lineDetail1,
                session,
                "Changement du ministère attributaire précédent des dossiers " +
                listeDossiersChangementMinistereAttributaire,
                ReponsesLogEnumImpl.UPDATE_MIN_ATTRI_TEC
            );
        }

        lineDetail1.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
        addLogToReponsesLoggingLineDetail(
            lineDetail1,
            session,
            "Fin changement du ministère de réattribution de [" +
            oldMinistere.getLabel() +
            BRACKET_VERS_BRACKET +
            newMinistereLabel +
            CLOSED_BRACKET,
            ReponsesLogEnumImpl.UPDATE_MIN_REAT_TEC
        );

        lineDetail1.save(session);
        saveAndCommitTransaction(session);
    }

    // ##############################################################################################
    // ### UTILITAIRES
    // #############################################################################################

    private ReponsesLoggingLineDetail createLoggingDetail(
        final CoreSession session,
        ReponsesLoggingLine reponsesLoggingLine,
        final String pos
    ) {
        DocumentModel detailDoc = session.createDocumentModel(
            ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_DOCUMENT_TYPE
        );
        detailDoc.setPathInfo(
            reponsesLoggingLine.getDocument().getPathAsString(),
            Calendar.getInstance().getTimeInMillis() + "-" + pos
        );

        detailDoc = session.createDocument(detailDoc);
        final List<String> listDetail = reponsesLoggingLine.getReponsesLoggingLineDetails();
        listDetail.add(detailDoc.getId());
        reponsesLoggingLine.setReponsesLoggingLineDetails(listDetail);
        reponsesLoggingLine.save(session);
        session.save();
        return detailDoc.getAdapter(ReponsesLoggingLineDetail.class);
    }

    /**
     * Ajoute un message de log au détail d'une migration effecture le reponsesLoggingLineDetail.save
     *
     * @param reponsesLoggingLineDetail
     * @param session
     * @param message
     * @param log
     * @return
     *
     */
    private List<String> addLogToReponsesLoggingLineDetail(
        final ReponsesLoggingLineDetail reponsesLoggingLineDetail,
        final CoreSession session,
        final String message,
        final STLogEnum log
    ) {
        final List<String> listLog = reponsesLoggingLineDetail.getFullLog();
        listLog.add(message);
        MigrationLogger.getInstance().logMigration(log, message);
        reponsesLoggingLineDetail.setFullLog(listLog);
        reponsesLoggingLineDetail.save(session);
        return listLog;
    }

    private void migrateCaseLinkForStepNoCopy(
        final CoreSession session,
        final OrganigrammeNode newMinistere,
        final String stepDocId,
        final DocumentModel dossierDoc,
        boolean changeMinistere,
        final String oldMinId
    ) {
        final DossierDistributionService distributionService = ReponsesServiceLocator.getDossierDistributionService();
        final DocumentModelList listDossierLink = getDossierLinkFromStep(session, stepDocId);
        for (final DocumentModel dossierLinkDoc : listDossierLink) {
            final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);

            dossierLink.setIdMinistereAttributaire(newMinistere.getId().toString());

            if (dossierDoc.getId().equals(dossierLink.getDossierId())) {
                distributionService.initDossierLinkAcl(session, dossierDoc, dossierLinkDoc);
            } else {
                // gestion allotissement mise a jour des dossiers liés
                DocumentModel docDossier = dossierLink.getDossier(session).getDocument();
                if (changeMinistere) {
                    docDossier =
                        distributionService.setNouveauMinistereCourant(session, docDossier, newMinistere.getId());
                }

                distributionService.initDossierLinkAcl(session, docDossier, dossierLinkDoc);
            }

            distributionService.setDossierLinksFields(session, dossierLinkDoc, oldMinId);
            session.saveDocument(dossierLinkDoc);
        }
    }

    /**
     * Récupère la liste des dossiers link associé à une étape
     *
     * @param session
     * @param stepDocId
     *            id de l'étape
     * @return
     *
     */
    private DocumentModelList getDossierLinkFromStep(final CoreSession session, final String stepDocId) {
        return session.query(StringUtils.replace(QUERY_STEP_FROM_STEPID, "?", stepDocId));
    }

    private void migrateCaseLinkForStep(
        final CoreSession session,
        final OrganigrammeNode newMinistere,
        final Mailbox newMailbox,
        final OrganigrammeNode newPosteBdc,
        final String stepDocId,
        final DocumentModel dossierDoc,
        boolean changeMinistere,
        final String oldMinId
    ) {
        final DossierDistributionService distributionService = ReponsesServiceLocator.getDossierDistributionService();
        final DocumentModelList listDossierLink = getDossierLinkFromStep(session, stepDocId);
        for (final DocumentModel dossierLinkDoc : listDossierLink) {
            final DossierLink oldDossierLink = dossierLinkDoc.getAdapter(DossierLink.class);

            DocumentModel docCopy = session.copy(
                dossierLinkDoc.getRef(),
                newMailbox.getDocument().getRef(),
                dossierLinkDoc.getName()
            );
            session.saveDocument(docCopy);

            docCopy.setPathInfo(newMailbox.getDocument().getPathAsString(), dossierLinkDoc.getName());

            final DossierLink dossierLink = docCopy.getAdapter(DossierLink.class);
            dossierLink.setIdMinistereAttributaire(newMinistere.getId().toString());

            final List<String> participant = new ArrayList<>();
            participant.add(SSConstant.MAILBOX_POSTE_ID_PREFIX + newPosteBdc.getId());
            dossierLink.setAllActionParticipant(participant);
            dossierLink.setInitialActionInternalParticipant(participant);

            docCopy = session.saveDocument(dossierLink.getDocument());

            final ACP acpDLDirecteur = dossierLinkDoc.getACP();
            session.setACP(docCopy.getRef(), acpDLDirecteur, true);
            session.saveDocument(docCopy);

            if (dossierDoc.getId().equals(oldDossierLink.getDossierId())) {
                distributionService.initDossierLinkAcl(session, dossierDoc, docCopy);
            } else {
                // gestion allotissement mise a jour des dossiers liés
                DocumentModel docDossier = oldDossierLink.getDossier(session).getDocument();
                if (changeMinistere) {
                    docDossier =
                        distributionService.setNouveauMinistereCourant(session, docDossier, newMinistere.getId());
                }

                distributionService.initDossierLinkAcl(session, docDossier, docCopy);
            }

            distributionService.initDossierDistributionAcl(session, dossierDoc, participant);
            distributionService.deleteDossierLink(session, oldDossierLink);
            distributionService.setDossierLinksFields(session, docCopy, oldMinId);

            session.saveDocument(docCopy);
        }
    }

    @Override
    public List<DocumentModel> getLstDossiersEligibleBriseSignature(CoreSession session, String ministereId) {
        String query = QUERY_DOSSIERS_IDS_SQL;
        if (!"ALL".equals(ministereId)) {
            LOGGER.info(session, STLogEnumImpl.GET_PARAM_TEC, "Argument ministère : " + ministereId);
            query = addMinistereFilter(ministereId);
        }

        final ArrayList<DocumentModel> dossiersDocList = new ArrayList<>();

        IterableQueryResult res = null;
        try {
            String[] colID = new String[1];
            List<Object> params = new ArrayList<>();
            colID[0] = FlexibleQueryMaker.COL_ID;

            res = QueryUtils.doSqlQuery(session, colID, query, params.toArray());
            final Iterator<Map<String, Serializable>> iterator = res.iterator();
            while (iterator.hasNext()) {
                final Map<String, Serializable> row = iterator.next();
                String idDossier = (String) row.get(FlexibleQueryMaker.COL_ID);
                IdRef dossierRef = new IdRef(idDossier);
                if (session.exists(dossierRef)) {
                    dossiersDocList.add(session.getDocument(dossierRef));
                } else {
                    LOGGER.warn(
                        session,
                        STLogEnumImpl.FAIL_GET_DOSSIER_TEC,
                        "Dossier non trouvé pour le traitement : " + idDossier
                    );
                }
            }
        } catch (NuxeoException ex) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_GET_DOSSIER_TEC,
                "Une erreur est survenue lors de la récupération de la liste des dossiers",
                ex
            );
        } finally {
            if (res != null) {
                res.close();
            }
        }

        return dossiersDocList;
    }

    private static String addMinistereFilter(String ministere) {
        return QUERY_DOSSIERS_IDS_SQL + " and idministereattributaire = '" + ministere + "'";
    }

    @Override
    public List<String> briserSignatureDossiers(
        final CoreSession session,
        final List<DocumentModel> dossiersDocList,
        final List<String> lstDossiersEnErreur
    ) {
        final ReponseService repServ = ReponsesServiceLocator.getReponseService();
        final AllotissementService allotServ = ReponsesServiceLocator.getAllotissementService();
        int nombreDossiersTraites = 0;
        int nombreDossiersATraiter = dossiersDocList.size();
        List<String> dossiersTraites = new ArrayList<>();

        for (DocumentModel dossierDoc : dossiersDocList) {
            if (!isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            Question question = dossier.getQuestion(session);
            try {
                IdRef reponseRef = new IdRef(dossier.getReponseId());
                if (session.exists(reponseRef)) {
                    DocumentModel reponseDoc = session.getDocument(reponseRef);
                    Reponse reponse = reponseDoc.getAdapter(Reponse.class);
                    if (allotServ.isAllotit(dossier)) {
                        // Si le dossier est alloti, on vérifie qu'il n'a pas été mis à jour avant
                        if (reponse.getSignature() != null) {
                            repServ.briserSignatureReponse(session, reponse, dossierDoc);
                        }
                        dossiersTraites.add(question.getOrigineQuestion() + SPACE + dossier.getNumeroQuestion());
                    } else {
                        repServ.briserSignatureReponse(session, reponse, dossierDoc);
                        dossiersTraites.add(question.getOrigineQuestion() + SPACE + dossier.getNumeroQuestion());
                    }
                } else {
                    LOGGER.warn(
                        session,
                        ReponsesLogEnumImpl.FAIL_GET_REPONSE_TEC,
                        "La réponse n'a pas été trouvée pour le dossier " + dossierDoc.getId()
                    );
                    lstDossiersEnErreur.add(question.getOrigineQuestion() + SPACE + dossier.getNumeroQuestion());
                }
            } catch (NuxeoException exc) {
                LOGGER.error(
                    session,
                    ReponsesLogEnumImpl.FAIL_DEL_SIGN_REPONSE_TEC,
                    "La réponse du dossier " + dossierDoc.getId() + " n'a pas pu avoir sa signature brisée, cause : ",
                    exc
                );
                lstDossiersEnErreur.add(question.getOrigineQuestion() + SPACE + dossier.getNumeroQuestion());
            } finally {
                ++nombreDossiersTraites;
                if (nombreDossiersTraites % 20 == 0) {
                    LOGGER.info(
                        session,
                        ReponsesLogEnumImpl.DEL_SIGN_REPONSE_TEC,
                        "Traitement en cours : " +
                        nombreDossiersTraites +
                        " traités / " +
                        nombreDossiersATraiter +
                        " au total"
                    );
                    saveAndCommitTransaction(session);
                }
            }
        }

        saveAndCommitTransaction(session);

        LOGGER.info(
            session,
            ReponsesLogEnumImpl.DEL_SIGN_REPONSE_TEC,
            "Fin du traitement. Nombre de dossiers traités : " +
            nombreDossiersTraites +
            " traités / " +
            nombreDossiersATraiter +
            " au total"
        );
        return dossiersTraites;
    }

    private boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * sauvegarde la session, commit la transaction et en démarre une nouvelle
     *
     * @param session
     *
     */
    private void saveAndCommitTransaction(final CoreSession session) {
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
    }
}
