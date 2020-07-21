package fr.dila.reponses.core.event.batch;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Batch d'envoi des mails journaliers de distribution des dossiers
 * 
 * @author admin
 * 
 */
public class DailyDistributionMailListener extends AbstractBatchEventListener {

    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(DailyDistributionMailListener.class);

    public DailyDistributionMailListener(){
    	super(LOGGER, ReponsesEventConstant.DAILY_DISTRIBUTION_MAIL_BATCH_EVENT);
    }
    
    @Override
    protected void processEvent(final CoreSession session, final Event event) throws ClientException {
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_DAILYMAIL_DIS_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            FeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
            feuilleRouteService.sendDailyDistributionMail(session);
        } catch (Exception exc) {
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PROCESS_B_DAILYMAIL_DIS_TEC, exc);
            errorCount++;
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
        	suiviBatchService.createBatchResultFor(batchLoggerModel, "Batch d'envoi des mails journaliers de distribution des dossiers", endTime-startTime);
        } catch (Exception e) {
        	LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC,e);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_DAILYMAIL_DIS_TEC);
    }
}
