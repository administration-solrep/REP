package fr.dila.reponses.core.event.batch;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Batch de suppression des favoris
 * 
 * @author lbosse
 */
public class EraseFavorisListener extends AbstractBatchEventListener {
	
    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(EraseFavorisListener.class);

    public EraseFavorisListener(){
    	super(LOGGER, ReponsesEventConstant.ERASE_FAVORIS_BATCH_EVENT);
    }
    
    @Override
    protected void processEvent(final CoreSession session, final Event event) throws ClientException {
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_DEL_FAV_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();
        // on récupère la date courante
        final Calendar currentDate = Calendar.getInstance();
            
        //on supprime tous les favoris dont la date de validité est inferieure à la date courante
        final FavorisDossierService favorisDossierService = ReponsesServiceLocator.getFavorisDossierService();
        favorisDossierService.removeOldFavoris(session, currentDate);
        session.save();
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
        	suiviBatchService.createBatchResultFor(batchLoggerModel, "Suppression des favoris arrivés à expiration", endTime-startTime);
        } catch (Exception e) {
        	LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC,e);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_DEL_FAV_TEC);
    }

}
