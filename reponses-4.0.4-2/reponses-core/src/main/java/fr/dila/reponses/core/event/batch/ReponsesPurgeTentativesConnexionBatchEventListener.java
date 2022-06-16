package fr.dila.reponses.core.event.batch;

import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.event.batch.PurgeTentativesConnexionBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;

public class ReponsesPurgeTentativesConnexionBatchEventListener
    extends PurgeTentativesConnexionBatchEventListener
    implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(
        ReponsesPurgeTentativesConnexionBatchEventListener.class
    );

    @Override
    public STLogEnum getStoppageCode() {
        return STLogEnumImpl.CANCEL_B_PURGE_TENTATIVES_CONNEXION_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
