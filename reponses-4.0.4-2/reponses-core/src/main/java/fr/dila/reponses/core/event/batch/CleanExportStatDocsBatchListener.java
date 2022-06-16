package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;

public class CleanExportStatDocsBatchListener extends AbstractBatchEventListener implements StoppableBatch {
    private static STLogger LOGGER = STLogFactory.getLog(CleanExportStatDocsBatchListener.class);

    public CleanExportStatDocsBatchListener() {
        super(LOGGER, ReponsesEventConstant.CLEAN_EXPORT_STAT_BATCH_EVENT);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_DEL_EXP_STAT_TEC);
        long startTime = Calendar.getInstance().getTimeInMillis();
        // on récupère la date courante
        final Calendar dateLimit = Calendar.getInstance();
        // On retire un jour
        dateLimit.add(Calendar.DAY_OF_MONTH, -1);

        //on supprime tous les favoris dont la date de validité est supérieurs à la date limite
        final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();

        int nbSuppression = 0;
        try {
            nbSuppression = exportService.removeOldExportStat(session, dateLimit);
            session.save();
        } catch (NuxeoException exc) {
            LOGGER.error(session, ReponsesLogEnumImpl.FAIL_DEL_EXP_STAT_TEC);
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Suppression des exports statistiques existants depuis 24h",
                nbSuppression,
                endTime - startTime
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, ReponsesLogEnumImpl.END_B_DEL_EXP_STAT_TEC);
    }

    @Override
    public STLogEnum getStoppageCode() {
        return ReponsesLogEnumImpl.CANCEL_B_DEL_EXP_STAT_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
