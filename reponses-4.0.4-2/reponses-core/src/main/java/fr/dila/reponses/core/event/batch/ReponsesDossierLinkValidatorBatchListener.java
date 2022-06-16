package fr.dila.reponses.core.event.batch;

import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.core.event.batch.DossierLinkValidatorBatchListener;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;

public class ReponsesDossierLinkValidatorBatchListener
    extends DossierLinkValidatorBatchListener
    implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesCleanDeletedAlertBatchListener.class);

    @Override
    public STLogEnum getStoppageCode() {
        return SSLogEnumImpl.CANCEL_B_VALID_STEP_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
