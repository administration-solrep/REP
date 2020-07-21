package fr.dila.reponses.core.event.batch;

import java.util.Calendar;
import java.util.Date;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

public class PurgeAuditTrailListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(PurgeAuditTrailListener.class);

    public PurgeAuditTrailListener(){
    	super(LOGGER, null);
    }
    
    @Override
    protected void processEvent(final CoreSession session, final Event event) throws ClientException {
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_PURGE_AUDIT_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();
        final JournalService journalService = STServiceLocator.getJournalService();
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        
        String delaiConservationJournalMois = paramService.getParametreValue(session, STParametreConstant.CONSERVATION_DONNEE_JOURNAL);
        int nbMonth = Integer.parseInt(delaiConservationJournalMois);

        if (nbMonth > 0) {
            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.MONTH, -nbMonth);

            Date dateLimit = new Date(c1.getTimeInMillis());
            journalService.purger(dateLimit);
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
        	suiviBatchService.createBatchResultFor(batchLoggerModel, "Purge de l'audit trail", endTime-startTime);
        } catch (Exception e) {
        	LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC,e);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_PURGE_AUDIT_TEC);
    }
}
