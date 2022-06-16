package fr.dila.reponses.core.event;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingStatusEnum;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ReponsesMigrationService;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.logger.MigrationLogger;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Gestion de la migration d'un gouvernement.
 *
 * @author asatre
 */
public class MigrationMinistereRattachementListener implements PostCommitEventListener {
    private static final MigrationLogger LOGGER = MigrationLogger.getInstance();

    public MigrationMinistereRattachementListener() {
        super();
    }

    @Override
    public void handleEvent(final EventBundle events) {
        if (events.containsEventName(ReponsesEventConstant.MIGRATION_GVT_CLOSE_EVENT)) {
            for (final Event event : events) {
                handleEvent(event);
            }
        }
    }

    protected void handleEvent(final Event event) {
        if (!event.getName().equals(ReponsesEventConstant.MIGRATION_GVT_CLOSE_EVENT)) {
            return;
        }

        final CoreSession coreSession = event.getContext().getCoreSession();
        final Map<String, Serializable> properties = event.getContext().getProperties();

        @SuppressWarnings("unchecked")
        final Map<String, String> newTimbre = (Map<String, String>) properties.get(
            ReponsesEventConstant.MIGRATION_GVT_NEW_TIMBRE_MAP
        );
        final String reponsesLoggingId = (String) properties.get(ReponsesEventConstant.MIGRATION_GVT_CURRENT_LOGGING);
        final String newTimbreUnchangedEntity = (String) properties.get(
            ReponsesEventConstant.NEW_TIMBRE_UNCHANGED_ENTITY
        );
        final String newTimbreDeactivateEntity = (String) properties.get(
            ReponsesEventConstant.NEW_TIMBRE_DEACTIVATE_ENTITY
        );

        LOGGER.logMigration(ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC, "Début de la migration des timbres");
        runMigrationUnrestricted(
            coreSession,
            newTimbre,
            reponsesLoggingId,
            newTimbreDeactivateEntity,
            newTimbreUnchangedEntity
        );
        LOGGER.logMigration(ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC, "Fin de la migration des timbres");
    }

    protected void migrateMin(
        CoreSession session,
        String reponsesLoggingId,
        Map<String, String> newTimbre,
        String newTimbreUnchangedEntity,
        String newTimbreDeactivateEntity
    ) {
        final ReponsesMigrationService migrationService = ReponsesServiceLocator.getReponsesMigrationService();
        final UpdateTimbreService updateTimbreService = ReponsesServiceLocator.getUpdateTimbreService();
        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
        final ReponsesLogging reponsesLogging = session
            .getDocument(new IdRef(reponsesLoggingId))
            .getAdapter(ReponsesLogging.class);
        Boolean hasError = Boolean.FALSE;
        List<String> listLog = new ArrayList<>();
        for (Entry<String, String> entry : newTimbre.entrySet()) {
            final String currentMin = entry.getKey();
            final String newMin = entry.getValue();
            final DocumentModel lineDoc = createLine(reponsesLogging, "-" + currentMin + "-" + newMin, session);
            final ReponsesLoggingLine reponsesLoggingLine = lineDoc.getAdapter(ReponsesLoggingLine.class);
            try {
                if (!newTimbreDeactivateEntity.equals(newMin) && !newTimbreUnchangedEntity.equals(newMin)) {
                    final List<String> listLine = reponsesLogging.getReponsesLoggingLines();
                    listLine.add(reponsesLoggingLine.getDocument().getId());
                    reponsesLogging.setReponsesLoggingLines(listLine);

                    reponsesLogging.save(session);
                    reponsesLoggingLine.setStartDate(Calendar.getInstance());

                    Long closeCount = updateTimbreService.getCloseCountForMinistere(session, currentMin);
                    if (closeCount == null) {
                        throw new ReponsesException("Fail to count closed questions for min " + currentMin);
                    }

                    reponsesLoggingLine.setPrevisionalCount(closeCount);
                    listLog = reponsesLoggingLine.getFullLog();

                    final OrganigrammeNode oldMinistere = ministeresService.getEntiteNode(currentMin);
                    final OrganigrammeNode newMinistere = ministeresService.getEntiteNode(newMin);

                    String message =
                        "Migration de l'organigramme Questions Closes: [" +
                        oldMinistere.getLabel() +
                        "] vers [" +
                        newMinistere.getLabel() +
                        "]";
                    reponsesLoggingLine.setMessage(message);

                    LOGGER.logMigration(ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC, message);

                    listLog =
                        addLogToLine(
                            reponsesLoggingLine,
                            listLog,
                            session,
                            "Migration des ministeres de rattachement Questions Closes"
                        );

                    migrationService.migrateAllCloseDossierMinistereRattachement(
                        session,
                        currentMin,
                        newMinistere,
                        reponsesLoggingLine,
                        reponsesLogging
                    );

                    listLog =
                        addLogToLine(
                            reponsesLoggingLine,
                            listLog,
                            session,
                            "Fin de Migration des ministeres de rattachement Questions Closes"
                        );
                    if (ReponsesLoggingStatusEnum.FAILURE.equals(reponsesLoggingLine.getStatus())) {
                        hasError = Boolean.TRUE;
                    } else {
                        reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
                    }
                }
            } catch (final Exception e) {
                rollbackAndCommitTransaction();
                // on catch tout pour mettre le status en failure
                reponsesLoggingLine.setStatus(ReponsesLoggingStatusEnum.FAILURE);
                String message = "Erreur lors de la migration du ministère : " + currentMin;
                listLog.add(message);
                LOGGER.logErrorMigration(ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC, message, e);
                hasError = Boolean.TRUE;
            }
            reponsesLoggingLine.setEndDate(Calendar.getInstance());
            reponsesLoggingLine.setFullLog(listLog);
            reponsesLoggingLine.save(session);
            reponsesLogging.save(session);
            saveAndCommitTransaction(session);
        }

        session.save();
        reponsesLogging.setEndDate(Calendar.getInstance());

        if (hasError) {
            reponsesLogging.setStatus(ReponsesLoggingStatusEnum.FAILURE);
        } else {
            reponsesLogging.setStatus(ReponsesLoggingStatusEnum.SUCCESS);
        }
        reponsesLogging.save(session);
        saveAndCommitTransaction(session);
    }

    private void runMigrationUnrestricted(
        final CoreSession coreSession,
        final Map<String, String> newTimbre,
        final String reponsesLoggingId,
        final String newTimbreDeactivateEntity,
        final String newTimbreUnchangedEntity
    ) {
        new UnrestrictedSessionRunner(coreSession) {

            @Override
            public void run() {
                migrateMin(session, reponsesLoggingId, newTimbre, newTimbreUnchangedEntity, newTimbreDeactivateEntity);
            }
        }
            .runUnrestricted();
    }

    private DocumentModel createLine(final ReponsesLogging reponsesLogging, String pos, CoreSession session) {
        DocumentModel lineDoc = session.createDocumentModel(
            ReponsesLoggingConstant.REPONSES_LOGGING_LINE_DOCUMENT_TYPE
        );
        lineDoc.setPathInfo(
            reponsesLogging.getDocument().getPathAsString(),
            String.valueOf(Calendar.getInstance().getTimeInMillis()) + pos
        );
        lineDoc = session.createDocument(lineDoc);

        saveAndCommitTransaction(session);
        return lineDoc;
    }

    private List<String> addLogToLine(
        ReponsesLoggingLine reponsesLoggingLine,
        final List<String> listLog,
        final CoreSession session,
        final String message
    ) {
        listLog.add(message);
        LOGGER.logMigration(ReponsesLogEnumImpl.MIGRATE_MIN_RATT_TEC, message);
        reponsesLoggingLine.setFullLog(listLog);
        reponsesLoggingLine.save(session);
        return listLog;
    }

    private void saveAndCommitTransaction(final CoreSession session) {
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
    }

    private void rollbackAndCommitTransaction() {
        TransactionHelper.setTransactionRollbackOnly();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
    }
}
