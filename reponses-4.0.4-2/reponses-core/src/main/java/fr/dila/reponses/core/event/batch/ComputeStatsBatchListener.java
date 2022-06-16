package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

/**
 * Batch de mise à jour des statistiques
 *
 * @author bgd
 */
public class ComputeStatsBatchListener extends AbstractBatchEventListener implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ComputeStatsBatchListener.class);

    public ComputeStatsBatchListener() {
        super(LOGGER, ReponsesEventConstant.COMPUTE_STAT_BATCH_EVENT);
    }

    @Override
    protected void processEvent(final CoreSession session, final Event event) {
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_STATS_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            final StatsService statsService = ReponsesServiceLocator.getStatsService();
            statsService.computeStats(session, batchLoggerModel);
            EventProducer eventProducer = STServiceLocator.getEventProducer();
            Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
            InlineEventContext inlineEventContext = new InlineEventContext(
                session,
                session.getPrincipal(),
                eventProperties
            );
            eventProducer.fireEvent(inlineEventContext.newEvent(ReponsesEventConstant.GENERATE_REPORT_EVENT));

            commitAndRestartTransaction(session, false);
        } catch (Exception exc) {
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PROCESS_B_STATS_TEC, exc);
            errorCount++;
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        try {
            STServiceLocator
                .getSuiviBatchService()
                .createBatchResultFor(
                    batchLoggerModel,
                    "Génération des statistiques de la page d'accueil",
                    endTime - startTime
                );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_STATS_TEC);
    }

    @Override
    public STLogEnum getStoppageCode() {
        return ReponsesLogEnumImpl.CANCEL_B_STATS_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
