package fr.dila.reponses.core.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingLineDetail;
import fr.dila.reponses.api.logging.ReponsesLoggingStatusEnum;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.core.service.organigramme.ReponsesChangementGouvernementService;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.api.migration.MigrationDiscriminatorConstants;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.logger.MigrationLogger;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Implémentation du service de feuille de route de l'application Réponses.
 *
 * @author jtremeaux
 */
public class FeuilleRouteModelServiceImpl
    extends fr.dila.ss.core.service.FeuilleRouteModelServiceImpl
    implements FeuilleRouteModelService {
    /**
     * UID.
     */
    private static final long serialVersionUID = -2392698015083550568L;

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(FeuilleRouteModelServiceImpl.class);

    private static final String UFNXQL_SELECT_FDR_QUERY_START =
        "SELECT f.ecm:uuid AS id FROM " +
        SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE +
        " AS f WHERE f.ecm:parentId = ? " +
        " AND f." +
        SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY +
        " = ? AND f.ecm:currentLifeCycleState = 'validated' ";

    private static final String UFNXQL_SELECT_DEFAULT_FDR_QUERY =
        "SELECT f.ecm:uuid AS id FROM " +
        SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE +
        " AS f WHERE f.ecm:parentId = ?" +
        " AND f.fdr:feuilleRouteDefaut = 1 " +
        " AND f.ecm:currentLifeCycleState = 'validated' ";

    private static final String UFNXQL_SELECT_DEFAULT_FDR_ERRATA_QUERY =
        "SELECT f.ecm:uuid AS id FROM " +
        SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE +
        " AS f WHERE f.ecm:parentId = ? " +
        " AND f.fdr:defautErrata = 1 " +
        " AND f.ecm:currentLifeCycleState = 'validated' ";

    private static final String UFNXQL_SELECT_ORDER_BY_POS = " ORDER BY f.ecm:pos ";

    @Override
    public SSFeuilleRoute getDefaultRouteQuestion(final CoreSession session) {
        final String fdrModelRootDocId = getFeuilleRouteModelFolderId(session);
        return getDefaultRouteQuestion(session, fdrModelRootDocId);
    }

    protected SSFeuilleRoute getDefaultRouteQuestion(final CoreSession session, final String fdrModelRootDocId) {
        final Object params[] = new Object[] { fdrModelRootDocId };
        final DocumentRef refs[] = QueryUtils.doUFNXQLQueryForIds(
            session,
            UFNXQL_SELECT_DEFAULT_FDR_QUERY + UFNXQL_SELECT_ORDER_BY_POS,
            params,
            1,
            0
        );
        if (refs == null || refs.length == 0) {
            return null;
        } else {
            return session.getDocument(refs[0]).getAdapter(SSFeuilleRoute.class);
        }
    }

    protected SSFeuilleRoute getDefaultRouteForDossier(
        final CoreSession session,
        final String fdrModelRootDocId,
        final String ministere
    ) {
        final Object[] params = new Object[] { fdrModelRootDocId, ministere };

        final DocumentRef refs[] = QueryUtils.doUFNXQLQueryForIds(
            session,
            UFNXQL_SELECT_FDR_QUERY_START + UFNXQL_SELECT_ORDER_BY_POS,
            params
        );
        if (refs == null || refs.length == 0) {
            return null;
        } else {
            for (final DocumentRef ref : refs) {
                final DocumentModel feuilleRouteDoc = session.getDocument(ref);
                final ReponsesFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(ReponsesFeuilleRoute.class);
                if (feuilleRoute.isFeuilleRouteDefaut()) {
                    return feuilleRoute;
                }
            }
            return session.getDocument(refs[0]).getAdapter(SSFeuilleRoute.class);
        }
    }

    @Override
    public SSFeuilleRoute selectRouteForDossier(final CoreSession session, final Dossier dossier) {
        // Erreur si le dossier est null
        if (dossier == null) {
            throw new NuxeoException("Dossier non valide");
        }

        final String fdrModelRootDocId = getFeuilleRouteModelFolderId(session);

        if (log.isDebugEnabled()) {
            log.debug("Selection de la feuille de route pour le dossier : " + dossier.getNumeroQuestion());
        }

        // Si le dossier n'a pas de question on renvoie la FDR par défaut
        final Question question = dossier.getQuestion(session);
        if (question == null) {
            return getDefaultRouteQuestion(session, fdrModelRootDocId);
        }

        // String typeQuestion = question.getTypeQuestion();
        if (!question.isQuestionTypeEcrite()) {
            if (log.isDebugEnabled()) {
                log.debug("Pas de feuille de route pour les question != QE");
            }
            return null;
        }

        final QuestionStateChange qscEtatQuestion = question.getEtatQuestion(session);
        if (qscEtatQuestion == null) {
            if (log.isDebugEnabled()) {
                log.debug("Pas de feuille de route pour les question sans etat");
            }
            return null;
        }

        Boolean isErratum = false;
        final Reponse reponse = dossier.getReponse(session);
        if (reponse != null) {
            isErratum = reponse.getErrata() != null;
        }

        final String etatQuestion = qscEtatQuestion.getNewState();
        if (
            VocabularyConstants.ETAT_QUESTION_REPONDU.equals(etatQuestion) &&
            Boolean.FALSE.equals(isErratum) ||
            VocabularyConstants.ETAT_QUESTION_RETIREE.equals(etatQuestion) ||
            VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion) ||
            VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion)
        ) {
            if (log.isDebugEnabled()) {
                log.debug("Pas de feuille de route pour les question repondu/retiree/caduque/cloture_autre");
            }
            return null;
        }

        SSFeuilleRoute route = null;
        // Si on a pas d'origine on renvoie la FDR par défaut
        // sinon on dispatch suivant l'origine
        final String origine = question.getOrigineQuestion();
        if (DossierConstants.ORIGINE_QUESTION_AN.equals(origine)) {
            if (log.isDebugEnabled()) {
                log.debug("Selection d'un feuille de route pour origine AN");
            }
            route = selectRouteForDossierFromAN(session, fdrModelRootDocId, dossier, question);
        } else if (DossierConstants.ORIGINE_QUESTION_SENAT.equals(origine)) {
            if (log.isDebugEnabled()) {
                log.debug("Selection d'un feuille de route pour origine SENAT");
            }
            route = selectRouteForDossierFromSenat(session, fdrModelRootDocId, dossier, question);
        } else if (StringUtils.isEmpty(origine)) {
            if (log.isDebugEnabled()) {
                log.debug("Selection d'un feuille de route pour origine NON DEFINIE");
            }
            route = getDefaultRouteQuestion(session, fdrModelRootDocId);
        }

        return route;
    }

    /**
     * Retourne la route pour une question en provenance de l'AN
     *
     * @param session
     *            session
     * @param dossier
     *            dossier
     * @return feuille de route
     *
     */
    protected SSFeuilleRoute selectRouteForDossierFromAN(
        final CoreSession session,
        final String fdrModelRootDocId,
        final Dossier dossier,
        final Question question
    ) {
        final String idMinistere = dossier.getIdMinistereAttributaireCourant();

        if (StringUtils.isEmpty(idMinistere)) {
            if (log.isDebugEnabled()) {
                log.debug("selectRouteForDossierFromAN : return default route for empty idMinistere");
            }
            return getDefaultRouteQuestion(session, fdrModelRootDocId);
        }

        final List<String> anRubriques = question.getAssNatRubrique();
        final List<String> anTeteAnalyses = question.getAssNatTeteAnalyse();
        final List<String> anAnalyses = question.getAssNatAnalyses();

        // Recherche sur le titre de la question et le ministere

        // Recherche sur les rubriques / têtes d'analyse AN ET les analyses de la question et le ministere
        if (!anAnalyses.isEmpty()) {
            final Object[] params = new Object[2 + anRubriques.size() + anTeteAnalyses.size() + anAnalyses.size()];

            final StringBuilder query = new StringBuilder(UFNXQL_SELECT_FDR_QUERY_START);
            int index = 0;
            params[index++] = fdrModelRootDocId;
            params[index++] = idMinistere;

            for (final String rubrique : anRubriques) {
                query.append(" AND f.ixa:AN_rubrique IN ( ? ) ");
                params[index++] = rubrique;
            }

            for (final String teteAnalyse : anTeteAnalyses) {
                query.append(" AND f.ixa:TA_rubrique IN ( ? ) ");
                params[index++] = teteAnalyse;
            }

            for (final String analyse : anAnalyses) {
                query.append(" AND f.ixa:AN_analyse IN ( ? ) ");
                params[index++] = analyse;
            }

            query.append(UFNXQL_SELECT_ORDER_BY_POS);

            final DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, query.toString(), params, 2, 0);
            if (refs.length == 1) {
                if (log.isDebugEnabled()) {
                    log.debug("selectRouteForDossierFromAN : return route based on indexation rubrique/TA/analyse");
                }
                return session.getDocument(refs[0]).getAdapter(SSFeuilleRoute.class);
            } // sinon 0 ou plus de une ==> ignoré
        }

        // Recherche sur les rubriques/themes AN de la question et le ministere
        if (!anRubriques.isEmpty()) {
            final Object[] params = new Object[2 + anRubriques.size() + anTeteAnalyses.size()];

            final StringBuilder query = new StringBuilder(UFNXQL_SELECT_FDR_QUERY_START);
            int index = 0;
            params[index++] = fdrModelRootDocId;
            params[index++] = idMinistere;

            for (final String rubrique : anRubriques) {
                query.append(" AND f.ixa:AN_rubrique IN ( ? ) ");
                params[index++] = rubrique;
            }

            for (final String teteAnalyse : anTeteAnalyses) {
                query.append(" AND f.ixa:TA_rubrique IN ( ? ) ");
                params[index++] = teteAnalyse;
            }

            query.append(UFNXQL_SELECT_ORDER_BY_POS);

            final DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, query.toString(), params, 2, 0);
            if (refs.length == 1) {
                if (log.isDebugEnabled()) {
                    log.debug("selectRouteForDossierFromAN : return route based on indexation rubrique/TA");
                }
                return session.getDocument(refs[0]).getAdapter(SSFeuilleRoute.class);
            } // sinon 0 ou plus de une ==> ignoré
        }

        // Recherche sur les rubriques AN de la question et le ministere
        if (!anRubriques.isEmpty()) {
            final Object[] params = new Object[2 + anRubriques.size()];

            final StringBuilder query = new StringBuilder(UFNXQL_SELECT_FDR_QUERY_START);
            int index = 0;
            params[index++] = fdrModelRootDocId;
            params[index++] = idMinistere;

            for (final String rubrique : anRubriques) {
                query.append(" AND f.ixa:AN_rubrique IN ( ? ) ");
                params[index++] = rubrique;
            }

            query.append(UFNXQL_SELECT_ORDER_BY_POS);

            final DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, query.toString(), params, 2, 0);
            if (refs.length == 1) {
                if (log.isDebugEnabled()) {
                    log.debug("selectRouteForDossierFromAN : return route based on indexation rubrique");
                }
                return session.getDocument(refs[0]).getAdapter(SSFeuilleRoute.class);
            } // sinon 0 ou plus de une ==> ignoré
        }

        if (log.isDebugEnabled()) {
            log.debug("selectRouteForDossierFromAN : return default route");
        }
        return getDefaultRouteForDossier(session, fdrModelRootDocId, idMinistere);
    }

    /**
     * Selection de la feuille de route pour une question du sénat
     *
     * @param session
     *            session
     * @param dossier
     *            dossier
     * @return feuille de route
     *
     */
    protected SSFeuilleRoute selectRouteForDossierFromSenat(
        final CoreSession session,
        final String fdrModelRootDocId,
        final Dossier dossier,
        final Question question
    ) {
        final String idMinistere = dossier.getIdMinistereAttributaireCourant();

        if (StringUtils.isEmpty(idMinistere)) {
            if (log.isDebugEnabled()) {
                log.debug("selectRouteForDossierFromSenat : return default because idMinistere empty");
            }
            return getDefaultRouteQuestion(session, fdrModelRootDocId);
        }

        final String titreQuestion = question.getSenatQuestionTitre();
        final List<String> seThemes = question.getSenatQuestionThemes();
        final List<String> seRubriques = question.getSenatQuestionRubrique();

        // Recherche sur le titre de la question et le ministere
        StringBuilder query = new StringBuilder(UFNXQL_SELECT_FDR_QUERY_START)
            .append(" AND f.fdr:titreQuestion = ? ")
            .append(UFNXQL_SELECT_ORDER_BY_POS);
        Object[] params = new Object[] { fdrModelRootDocId, idMinistere, titreQuestion };

        if (log.isDebugEnabled()) {
            log.debug("Recherche sur le titre de la question et le ministere : " + query);
        }

        DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, query.toString(), params, 2, 0);
        if (refs.length == 1) {
            if (log.isDebugEnabled()) {
                log.debug("selectRouteForDossierFromSenat : return route based on titre");
            }
            return session.getDocument(refs[0]).getAdapter(SSFeuilleRoute.class);
        }

        // Recherche sur les themes sénat de la question et le ministere
        if (!seThemes.isEmpty()) {
            query = new StringBuilder(UFNXQL_SELECT_FDR_QUERY_START);
            params = new Object[2 + seThemes.size()];
            int index = 0;
            params[index++] = fdrModelRootDocId;
            params[index++] = idMinistere;

            for (final String theme : seThemes) {
                query.append(" AND f.ixa:SE_theme IN ( ? ) ");
                params[index++] = theme;
            }

            query.append(UFNXQL_SELECT_ORDER_BY_POS);

            if (log.isDebugEnabled()) {
                log.debug("Recherche sur les themes sénat de la question et le ministere : " + query);
            }
            refs = QueryUtils.doUFNXQLQueryForIds(session, query.toString(), params, 2, 0);
            if (refs.length == 1) {
                log.info("selectRouteForDossierFromSenat : return route based on seTheme");
                return session.getDocument(refs[0]).getAdapter(SSFeuilleRoute.class);
            }
        }

        // Recherche sur les rubriques sénat de la question et le ministere
        if (!seRubriques.isEmpty()) {
            query = new StringBuilder(UFNXQL_SELECT_FDR_QUERY_START);
            params = new Object[2 + seRubriques.size()];
            int index = 0;
            params[index++] = fdrModelRootDocId;
            params[index++] = idMinistere;

            for (final String rubrique : seRubriques) {
                query.append(" AND f.ixa:SE_rubrique IN ( ? ) ");
                params[index++] = rubrique;
            }

            query.append(UFNXQL_SELECT_ORDER_BY_POS);

            log.debug("Recherche sur les rubriques sénat de la question et le ministere : " + query);
            refs = QueryUtils.doUFNXQLQueryForIds(session, query.toString(), params, 2, 0);
            if (refs.length == 1) {
                if (log.isDebugEnabled()) {
                    log.debug("selectRouteForDossierFromSenat : return route based on SE_rubrique");
                }
                return session.getDocument(refs[0]).getAdapter(SSFeuilleRoute.class);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("selectRouteForDossierFromSenat : return default route ");
        }
        return getDefaultRouteForDossier(session, fdrModelRootDocId, idMinistere);
    }

    @Override
    public SSFeuilleRoute getDefaultRouteErrata(final CoreSession session) {
        final String fdrModelRootDocId = getFeuilleRouteModelFolderId(session);

        return getDefaultRouteErrata(session, fdrModelRootDocId);
    }

    protected SSFeuilleRoute getDefaultRouteErrata(final CoreSession session, final String fdrModelRootDocId) {
        final Object[] params = new Object[] { fdrModelRootDocId };

        final DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(
            session,
            UFNXQL_SELECT_DEFAULT_FDR_ERRATA_QUERY + UFNXQL_SELECT_ORDER_BY_POS,
            params,
            1,
            0
        );
        if (refs == null || refs.length == 0) {
            return null;
        } else {
            return session.getDocument(refs[0]).getAdapter(SSFeuilleRoute.class);
        }
    }

    @Override
    public void migrateMinistereFeuilleRouteModel(
        final CoreSession session,
        final String oldMinistereId,
        final String newMinistereId,
        final String oldMailboxId,
        final String newMailboxId,
        final ReponsesLoggingLine reponsesLoggingLine
    ) {
        final STLockService lockService = STServiceLocator.getSTLockService();
        final String feuilleRouteModelFolderId = getFeuilleRouteModelFolderId(session);

        session.save();

        // Modification des modèles de feuille de route
        final String query =
            "SELECT * FROM FeuilleRoute WHERE ecm:parentId='" +
            feuilleRouteModelFolderId +
            "' AND fdr:ministere='" +
            oldMinistereId +
            "'";
        final List<DocumentModel> feuilleRouteModelList = session.query(query);
        for (final DocumentModel feuilleRouteDoc : feuilleRouteModelList) {
            final SSFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(SSFeuilleRoute.class);

            DocumentModel detailDoc = session.createDocumentModel(
                ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_DOCUMENT_TYPE
            );
            detailDoc.setPathInfo(
                reponsesLoggingLine.getDocument().getPathAsString(),
                String.valueOf(Calendar.getInstance().getTimeInMillis()) + "-" + oldMinistereId + "-" + newMinistereId
            );
            detailDoc = session.createDocument(detailDoc);

            final List<String> list = reponsesLoggingLine.getReponsesLoggingLineDetails();
            list.add(detailDoc.getId());

            reponsesLoggingLine.setReponsesLoggingLineDetails(list);
            reponsesLoggingLine.save(session);

            final ReponsesLoggingLineDetail reponsesLoggingLineDetail = detailDoc.getAdapter(
                ReponsesLoggingLineDetail.class
            );
            final List<String> listLog = reponsesLoggingLineDetail.getFullLog();

            reponsesLoggingLineDetail.setMessage("Migration du modèle de feuille de route " + feuilleRoute.getName());

            listLog.add("Migration du modèle de feuille de route " + feuilleRoute.getName());

            if (log.isDebugEnabled()) {
                log.debug("Migration du modèle de feuille de route " + feuilleRoute.getName());
            }

            try {
                if (session.getLockInfo(feuilleRouteDoc.getRef()) != null) {
                    lockService.unlockDocUnrestricted(session, feuilleRouteDoc);
                }
                lockService.lockDoc(session, feuilleRoute.getDocument());

                feuilleRoute.setMinistere(newMinistereId);
                feuilleRoute.save(session);

                final List<DocumentModel> routeSteps = getAllRouteElement(feuilleRouteDoc.getId(), session);
                for (final DocumentModel routeStepDoc : routeSteps) {
                    final SSRouteStep routeStep = routeStepDoc.getAdapter(SSRouteStep.class);
                    if (routeStep.getDistributionMailboxId().equals(oldMailboxId)) {
                        routeStep.setDistributionMailboxId(newMailboxId);
                        routeStep.save(session);
                    }
                }

                final DocumentRoutingService docRoutingService = SSServiceLocator.getDocumentRoutingService();
                if (!feuilleRoute.isDraft()) {
                    docRoutingService.invalidateRouteModelNotUnrestricted(
                        feuilleRoute.getFeuilleRoute(session),
                        session
                    );
                }
                reponsesLoggingLineDetail.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
            } catch (final Exception e) {
                final StringBuilder error = new StringBuilder("Migration impossible de la feuille de route : ")
                    .append(feuilleRoute.getName())
                    .append(" du ministère : ")
                    .append(feuilleRoute.getMinistere());

                listLog.add(error.toString());

                log.error(error.toString(), e);
                MigrationLogger.getInstance().logMigration(SSLogEnumImpl.FAIL_MIGRATE_FDR_TEC, error.toString());

                reponsesLoggingLineDetail.setStatus(ReponsesLoggingStatusEnum.FAILURE);
                reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.FAILURE);

                reponsesLoggingLine.save(session);
            } finally {
                lockService.unlockDoc(session, feuilleRoute.getDocument());
            }

            feuilleRoute.save(session);

            listLog.add("Fin de migration du modèle de feuille de route " + feuilleRoute.getName());

            if (log.isDebugEnabled()) {
                log.debug("Fin de migration du modèle de feuille de route " + feuilleRoute.getName());
            }

            reponsesLoggingLineDetail.setFullLog(listLog);
            reponsesLoggingLineDetail.save(session);
            session.save();
        }

        session.save();
    }

    @Override
    public List<DocumentModel> getAllRouteElement(final String feuilleRouteId, final CoreSession session) {
        List<DocumentModel> stepDocList = null;
        final StringBuilder query = new StringBuilder(
            "select r.ecm:uuid AS id from " + FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP + " AS r WHERE "
        )
            .append(" r.rtsk:")
            .append(SSFeuilleRouteConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY)
            .append(" = ? ");

        final Object params[] = new Object[] { feuilleRouteId };
        stepDocList =
            QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                session,
                FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP,
                query.toString(),
                params
            );

        return stepDocList;
    }

    @Override
    public List<DocumentModel> getAllReadyOrRunningRouteElement(
        final String feuilleRouteId,
        final CoreSession session
    ) {
        List<DocumentModel> stepDocList = null;
        final StringBuilder query = new StringBuilder(
            "select r.ecm:uuid AS id from " + FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP + " AS r WHERE "
        )
            .append(" r.rtsk:")
            .append(SSFeuilleRouteConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY)
            .append(" = ? ")
            .append(" AND r.")
            .append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH)
            .append(" IN ('running', 'ready')");

        final Object[] params = new Object[] { feuilleRouteId };
        stepDocList =
            QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                session,
                FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP,
                query.toString(),
                params
            );

        return stepDocList;
    }

    @Override
    public List<DocumentModel> getFdrModelFromMinistereAndDirection(
        final CoreSession session,
        final String ministereId,
        final String directionId,
        final boolean hasDirection
    ) {
        // récupère l'id du répertoire des feuilles de route
        final String modelFolderId = getFeuilleRouteModelFolderId(session);

        // migre les modeles de feuille de route pour les ministères
        final StringBuilder query = new StringBuilder();
        query.append("SELECT f.ecm:uuid as id FROM ");
        query.append(SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE);
        query.append(" as f WHERE f.ecm:parentId = ? ");

        final List<String> params = new ArrayList<>();
        params.add(modelFolderId);

        if (StringUtils.isNotEmpty(ministereId)) {
            query.append(" and f.");
            query.append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX);
            query.append(":");
            query.append(SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY);
            query.append(" = ? ");

            params.add(ministereId);
        }
        if (hasDirection) {
            query.append(" and f.");
            query.append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX);
            query.append(":");
            query.append(ReponsesConstant.REPONSES_FEUILLE_ROUTE_ID_DIRECTION_PILOTE_PROPERTY_NAME);
            query.append(" = ? ");

            params.add(directionId);
        }

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE,
            query.toString(),
            params.toArray()
        );
    }

    @Override
    public void migrerModeleFdrDirection(
        CoreSession session,
        EntiteNode oldMinistereNode,
        UniteStructurelleNode oldDirectionNode,
        EntiteNode newMinistereNode,
        UniteStructurelleNode newDirectionNode,
        MigrationLoggerModel migrationLoggerModel,
        Boolean desactivateModelFdr
    ) {
        final boolean hasDirection = oldDirectionNode != null;

        // récupère les ids des noeuds ministères
        final String oldMinistereNodeId = oldMinistereNode.getId();
        final String newMinistereNodeId = newMinistereNode.getId();
        String oldDirectionNodeId = null;
        String newDirectionNodeId = null;

        if (hasDirection) {
            oldDirectionNodeId = oldDirectionNode.getId();
            newDirectionNodeId = newDirectionNode.getId();
        }
        final List<DocumentModel> fdrModelToUpdate = getFdrModelFromMinistereAndDirection(
            session,
            oldMinistereNodeId,
            oldDirectionNodeId,
            hasDirection
        );

        migrationLoggerModel.setModeleFdr(0);
        ReponsesChangementGouvernementService reponsesChangementGouvernementService = ReponsesServiceLocator.getReponsesChangementGouvernementService();
        reponsesChangementGouvernementService.flushMigrationLogger(migrationLoggerModel);

        if (CollectionUtils.isEmpty(fdrModelToUpdate)) {
            migrationLoggerModel.setModeleFdr(1);
            migrationLoggerModel.setModeleFdrCount(0);
            migrationLoggerModel.setModeleFdrCurrent(0);
            reponsesChangementGouvernementService.flushMigrationLogger(migrationLoggerModel);
            final MigrationDetailModel migrationDetailModel = reponsesChangementGouvernementService.createMigrationDetailFor(
                migrationLoggerModel,
                MigrationDiscriminatorConstants.FDR,
                "Aucun modèle de feuille de route",
                "OK"
            );
            migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
            reponsesChangementGouvernementService.flushMigrationDetail(migrationDetailModel);
        } else {
            final int size = fdrModelToUpdate.size();
            log.info(size + " modèles de feuille de route à mettre à jour.");
            migrationLoggerModel.setModeleFdrCount(size);

            int index = 0;
            migrationLoggerModel.setModeleFdrCurrent(index);
            reponsesChangementGouvernementService.flushMigrationLogger(migrationLoggerModel);
            final DocumentRoutingService documentRoutingService = ReponsesServiceLocator.getDocumentRoutingService();

            for (final DocumentModel documentModel : fdrModelToUpdate) {
                if (desactivateModelFdr) {
                    final MigrationDetailModel migrationDetailModel = reponsesChangementGouvernementService.createMigrationDetailFor(
                        migrationLoggerModel,
                        MigrationDiscriminatorConstants.FDR,
                        "Désactivation du modèle de feuille de route '" + documentModel.getTitle() + "' : Début",
                        "KO"
                    );
                    SSFeuilleRoute routeModel = documentModel.getAdapter(SSFeuilleRoute.class);
                    if (routeModel.isValidated()) {
                        SSServiceLocator.getDocumentRoutingService().lockDocumentRoute(routeModel, session);
                        documentRoutingService.invalidateRouteModel(routeModel, session);
                        SSServiceLocator.getDocumentRoutingService().unlockDocumentRoute(routeModel, session);
                    }
                    migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
                    migrationDetailModel.setStatut("OK");
                    reponsesChangementGouvernementService.flushMigrationDetail(migrationDetailModel);
                }

                final MigrationDetailModel migrationDetailModel = reponsesChangementGouvernementService.createMigrationDetailFor(
                    migrationLoggerModel,
                    MigrationDiscriminatorConstants.FDR,
                    "Migration modèle de feuille de route '" + documentModel.getTitle() + "' : Début",
                    "KO"
                );
                final MigrationDetailModel migrationDetailModel2 = reponsesChangementGouvernementService.createMigrationDetailFor(
                    migrationLoggerModel,
                    MigrationDiscriminatorConstants.FDR,
                    "Renommage du modèle de feuille de route '" + documentModel.getTitle() + "' : Début",
                    "KO"
                );

                // on verrouille le document pour s'assurer que personne ne le modifie
                final DocumentRef docRef = documentModel.getRef();
                // si le document est verrouillé, on le déverrouille
                if (documentModel.isLocked()) {
                    session.removeLock(docRef);
                }
                // on verrouille le document pour s'assurer que personne ne le modifie
                session.setLock(docRef);

                final ReponsesFeuilleRoute feuilleRoute = documentModel.getAdapter(ReponsesFeuilleRoute.class);
                feuilleRoute.setMinistere(newMinistereNodeId);
                if (hasDirection) {
                    feuilleRoute.setIdDirectionPilote(newDirectionNodeId);
                    feuilleRoute.setIntituleDirectionPilote(newDirectionNode.getLabel());
                }

                migrationDetailModel2.setEndDate(Calendar.getInstance().getTime());
                migrationDetailModel2.setStatut("OK");
                reponsesChangementGouvernementService.flushMigrationDetail(migrationDetailModel2);
                feuilleRoute.save(session);
                // on enlève le verrou
                session.removeLock(docRef);

                migrationLoggerModel.setModeleFdrCurrent(++index);
                reponsesChangementGouvernementService.flushMigrationLogger(migrationLoggerModel);
                migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
                migrationDetailModel.setStatut("OK");
                reponsesChangementGouvernementService.flushMigrationDetail(migrationDetailModel);
            }
        }
        migrationLoggerModel.setModeleFdr(1);
        reponsesChangementGouvernementService.flushMigrationLogger(migrationLoggerModel);
    }
}
