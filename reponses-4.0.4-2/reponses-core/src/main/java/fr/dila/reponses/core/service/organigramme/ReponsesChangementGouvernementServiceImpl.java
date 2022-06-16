package fr.dila.reponses.core.service.organigramme;

import static fr.dila.ss.api.constant.SSConstant.MAILBOX_POSTE_ID_PREFIX;
import static java.util.Collections.singletonList;

import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.core.migration.ClosDetailModelImpl;
import fr.dila.reponses.core.migration.LancerDetailModelImpl;
import fr.dila.reponses.core.migration.MailBoxDetailModelImpl;
import fr.dila.reponses.core.migration.MigrationLoggerModelImpl;
import fr.dila.reponses.core.migration.ModeleFDRDetailModelImpl;
import fr.dila.reponses.core.migration.StepDetailModelImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.api.migration.MigrationDiscriminatorConstants;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.ss.api.service.SSOrganigrammeService;
import fr.dila.ss.core.service.SSAbstractChangementGouvernementService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.Query;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.transaction.TransactionHelper;

public class ReponsesChangementGouvernementServiceImpl
    extends SSAbstractChangementGouvernementService
    implements ReponsesChangementGouvernementService {
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesChangementGouvernementServiceImpl.class);

    public ReponsesChangementGouvernementServiceImpl() {
        // Do nothing
    }

    @Override
    protected String getQueryFromDossWhereClosSql() {
        return " FROM " + DossierConstants.DOSSIER_SCHEMA + " ";
    }

    @Override
    protected SSOrganigrammeService getSSOrganigrammeService() {
        return ReponsesServiceLocator.getReponsesOrganigrammeService();
    }

    @Override
    public void updateMailBox(
        CoreSession session,
        OrganigrammeNode oldNode,
        OrganigrammeNode newNode,
        MigrationLoggerModel migrationLoggerModel
    ) {
        super.updateMailBox(session, oldNode, newNode, migrationLoggerModel);
        session.save();

        if (TransactionHelper.isTransactionActive()) {
            TransactionHelper.commitOrRollbackTransaction();
            TransactionHelper.startTransaction();
            updatePrecomptage(session, oldNode, newNode);
        }
    }

    private void updatePrecomptage(CoreSession session, OrganigrammeNode oldNode, OrganigrammeNode newNode) {
        final String oldPostMailboxId = MAILBOX_POSTE_ID_PREFIX + oldNode.getId();
        final String newPostMailboxId = MAILBOX_POSTE_ID_PREFIX + newNode.getId();
        StringBuilder query = getMailboxesHavingGroupsField();

        List<DocumentModel> mailboxesToUpdate = getOldMlbxPosteList(session, query, singletonList(oldPostMailboxId));
        mailboxesToUpdate.addAll(getNewMlbxPosteList(session, query, singletonList(newPostMailboxId)));

        ReponsesServiceLocator.getMailboxService().updatePrecomptageMailboxesAndSetProperty(session, mailboxesToUpdate);
    }

    @Override
    protected List<DocumentModel> getDossiersToUpdate(CoreSession session, List<String> params, StringBuilder query) {
        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            DossierConstants.DOSSIER_DOCUMENT_TYPE,
            query.toString(),
            params.toArray()
        );
    }

    @Override
    protected List<DocumentModel> getNewMlbxPosteList(CoreSession session, StringBuilder query, List<String> params) {
        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            ReponsesConstant.REPONSES_MAILBOX_TYPE,
            query.toString(),
            params.toArray()
        );
    }

    @Override
    protected List<DocumentModel> getOldMlbxPosteList(CoreSession session, StringBuilder query, List<String> params) {
        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            ReponsesConstant.REPONSES_MAILBOX_TYPE,
            query.toString(),
            params.toArray()
        );
    }

    @Override
    protected StringBuilder getMailboxesHavingGroupsField() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT m.ecm:uuid as id FROM ");
        query.append(ReponsesConstant.REPONSES_MAILBOX_TYPE);
        query.append(" as m WHERE m.");
        query.append(MailboxConstants.GROUPS_FIELD);
        query.append(" = ? ");
        return query;
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
        final FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
        feuilleRouteModelService.migrerModeleFdrDirection(
            session,
            oldMinistereNode,
            oldDirectionNode,
            newMinistereNode,
            newDirectionNode,
            migrationLoggerModel,
            desactivateModelFdr
        );
    }

    @Override
    public void migrerModeleFdrMinistere(
        CoreSession session,
        EntiteNode oldNode,
        EntiteNode newNode,
        MigrationLoggerModel migrationLoggerModel,
        Boolean desactivateModelFdr
    ) {
        final FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
        feuilleRouteModelService.migrerModeleFdrMinistere(
            session,
            oldNode,
            newNode,
            migrationLoggerModel,
            desactivateModelFdr
        );
    }

    @Override
    public void updateDossierDirectionRattachement(
        CoreSession session,
        OrganigrammeNode oldMinistereNode,
        OrganigrammeNode oldDirectionNode,
        OrganigrammeNode newMinistereNode,
        OrganigrammeNode newDirectionNode,
        MigrationLoggerModel migrationLoggerModel
    ) {
        migrationLoggerModel.setNorDossierClos(0);
        flushMigrationLogger(migrationLoggerModel);

        // récupère les ids des noeuds ministères
        final String oldMinistereNodeId = oldMinistereNode.getId();
        final String newMinistereNodeId = newMinistereNode.getId();

        // récupération requete id dossier clos
        final StringBuilder endQuery = new StringBuilder(getQueryFromDossWhereClosSql());
        // ajout filtre sur ministereattache
        endQuery
            .append(" WHERE ")
            .append(DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT)
            .append(" = '")
            .append(oldMinistereNodeId)
            .append("' ");
        // ajout filtre sur directionattache si nécessaire

        final StringBuilder querySelectIds = new StringBuilder("SELECT id ").append(endQuery);

        // maj migration logger
        LOGGER.info(session, STLogEnumImpl.MIGRATE_MINISTERE_TEC, "Ministère interrogé à mettre à jour.");

        flushMigrationLogger(migrationLoggerModel);

        final List<MigrationDetailModel> migrationsDetails = new ArrayList<>();

        // update set ministereattache = new valeur[, directionattache = new valeur (si
        // nécessaire)] where id in
        // list
        final List<Integer> resultUpdate = new ArrayList<>();
        getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    StringBuilder query = new StringBuilder("UPDATE " + DossierConstants.DOSSIER_SCHEMA + " SET ");
                    query.append(DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT).append(" = :ministere ");

                    query.append("WHERE id IN (").append(querySelectIds).append(")");
                    Query queryType = entityManager.createNativeQuery(query.toString());
                    queryType.setParameter("ministere", newMinistereNodeId);

                    int updateExecuted = queryType.executeUpdate();

                    if (newDirectionNode != null && oldDirectionNode != null) {
                        query = new StringBuilder("UPDATE " + DossierConstants.QUESTION_DOCUMENT_SCHEMA + " SET ");
                        query.append(DossierConstants.DOSSIER_ID_DIRECTION_PILOTE_QUESTION).append(" = :newDirection ");
                        query
                            .append(", ")
                            .append(DossierConstants.DOSSIER_INTITULE_DIRECTION_PILOTE_QUESTION)
                            .append(" = :newDirectionIntitule ");

                        query.append(
                            "WHERE " + DossierConstants.DOSSIER_ID_DIRECTION_PILOTE_QUESTION + " =:oldDirection"
                        );
                        queryType = entityManager.createNativeQuery(query.toString());
                        queryType.setParameter("newDirection", newDirectionNode.getId());
                        queryType.setParameter("newDirectionIntitule", newDirectionNode.getLabel());
                        queryType.setParameter("oldDirection", oldDirectionNode.getId());

                        updateExecuted += queryType.executeUpdate();
                    }

                    resultUpdate.add(updateExecuted);
                }
            );
        Integer count = resultUpdate.get(0);
        migrationLoggerModel.setNorDossierClosCurrent(count);
        migrationLoggerModel.setNorDossierClosCount(count); // One shot => same number
        for (MigrationDetailModel migrationDetailModel : migrationsDetails) {
            migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
            flushMigrationDetail(migrationDetailModel);
        }

        flushMigrationLogger(migrationLoggerModel);
    }

    @Override
    public MigrationDetailModel createMigrationDetailFor(
        MigrationLoggerModel migrationLoggerModel,
        String type,
        String detail,
        String statut
    ) {
        return getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    final MigrationLoggerModel model = entityManager.merge(migrationLoggerModel);
                    entityManager.persist(model);

                    MigrationDetailModel migrationDetail = null;
                    switch (type) {
                        case MigrationDiscriminatorConstants.FDR:
                            migrationDetail = new ModeleFDRDetailModelImpl();
                            break;
                        case MigrationDiscriminatorConstants.STEP:
                            migrationDetail = new StepDetailModelImpl();
                            break;
                        case MigrationDiscriminatorConstants.LANCE:
                            migrationDetail = new LancerDetailModelImpl();
                            break;
                        case MigrationDiscriminatorConstants.CLOS:
                            migrationDetail = new ClosDetailModelImpl();
                            break;
                        case MigrationDiscriminatorConstants.MAILBOX:
                            migrationDetail = new MailBoxDetailModelImpl();
                            break;
                        default:
                            break;
                    }

                    if (migrationDetail != null) {
                        migrationDetail.setMigration(migrationLoggerModel);
                        migrationDetail.setDetail(detail);
                        migrationDetail.setStartDate(Calendar.getInstance().getTime());
                        migrationDetail.setStatut(statut);
                        entityManager.persist(migrationDetail);
                    }
                    LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, detail);

                    return migrationDetail;
                }
            );
    }

    @Override
    public MigrationLoggerModel createMigrationLogger(final String ssPrincipal) {
        return getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    final MigrationLoggerModel migrationLoggerModel = new MigrationLoggerModelImpl();
                    migrationLoggerModel.setStartDate(Calendar.getInstance().getTime());
                    migrationLoggerModel.setPrincipalName(ssPrincipal);

                    entityManager.persist(migrationLoggerModel);

                    return migrationLoggerModel;
                }
            );
    }
}
