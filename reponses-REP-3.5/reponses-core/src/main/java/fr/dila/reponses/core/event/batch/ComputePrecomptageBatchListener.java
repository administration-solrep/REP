package fr.dila.reponses.core.event.batch;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

public class ComputePrecomptageBatchListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ComputePrecomptageBatchListener.class);
    
    public ComputePrecomptageBatchListener(){
    	super(LOGGER, ReponsesEventConstant.COMPUTE_PRECOMPTAGE_BATCH_EVENT);
    }
    
    @Override
    protected void processEvent(final CoreSession session, final Event event) throws ClientException {
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_PRECOMPTAGE_TEC);        
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            final String function_call = "computePrecomptage()";
            QueryUtils.execSqlFunction(session, function_call, null);
            LOGGER.info(session, ReponsesLogEnumImpl.PROCESS_B_PRECOMPTAGE_TEC, "Recalcul effectué");
        } catch(Exception exc) {
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PROCESS_B_PRECOMPTAGE_TEC, exc);
            errorCount++;
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
        	suiviBatchService.createBatchResultFor(batchLoggerModel, "Recalcul effectué", endTime-startTime);
        } catch (Exception e) {
        	LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC,e);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_PRECOMPTAGE_TEC);
    }
}
