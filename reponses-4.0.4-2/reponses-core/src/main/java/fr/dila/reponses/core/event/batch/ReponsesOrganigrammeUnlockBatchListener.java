package fr.dila.reponses.core.event.batch;

import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.event.batch.OrganigrammeUnlockBatchListener;
import fr.dila.st.core.factory.STLogFactory;

public class ReponsesOrganigrammeUnlockBatchListener extends OrganigrammeUnlockBatchListener implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesOrganigrammeUnlockBatchListener.class);

    @Override
    public STLogEnum getStoppageCode() {
        return STLogEnumImpl.CANCEL_B_UNLOCK_ORGA_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
