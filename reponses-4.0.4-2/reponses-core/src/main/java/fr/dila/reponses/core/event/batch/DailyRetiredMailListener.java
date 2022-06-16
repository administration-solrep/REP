package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.time.Duration;
import java.time.Instant;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

/**
 * Batch d'envoi des mails journaliers de notification des retraits
 *
 * @author JBT
 *
 */
public class DailyRetiredMailListener extends AbstractBatchEventListener implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(DailyRetiredMailListener.class);

    public DailyRetiredMailListener() {
        super(LOGGER, ReponsesEventConstant.DAILY_RETIRED_MAIL_BATCH_EVENT);
    }

    @Override
    protected void processEvent(final CoreSession session, final Event event) {
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_DAILYMAIL_RET_TEC);
        Instant startTime = Instant.now();

        try {
            ReponsesMailService reponsesMailService = ReponsesServiceLocator.getReponsesMailService();
            reponsesMailService.sendDailyRetiredMail(session);
        } catch (Exception exc) {
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PROCESS_B_DAILYMAIL_RET_TEC, exc);
            errorCount++;
        }
        Instant endTime = Instant.now();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Batch d'envoi des mails journaliers de notification des retraits",
                Duration.between(startTime, endTime).toMillis()
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_DAILYMAIL_RET_TEC);
    }

    @Override
    public STLogEnum getStoppageCode() {
        return ReponsesLogEnumImpl.CANCEL_B_DAILYMAIL_RET_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
