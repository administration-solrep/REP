package fr.dila.reponses.core.event.batch;

import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.core.event.batch.CloseUsersConnectionsBatchListener;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;

public class ReponsesCloseUsersConnectionsBatchListener
    extends CloseUsersConnectionsBatchListener
    implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesCloseUsersConnectionsBatchListener.class);

    @Override
    public STLogEnum getStoppageCode() {
        return SSLogEnumImpl.CANCEL_B_CLOSE_USERS_CONNEC_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
