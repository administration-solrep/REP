package fr.dila.reponses.core.event.batch;

import fr.dila.ss.core.event.batch.CleanDeletedDossierLinkBatchListener;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;

public class ReponsesCleanDeletedDossierLinkBatchListener
    extends CleanDeletedDossierLinkBatchListener
    implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesCleanDeletedDossierLinkBatchListener.class);

    @Override
    public STLogEnum getStoppageCode() {
        return STLogEnumImpl.CANCEL_B_DEL_DOC_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
