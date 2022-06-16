package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.ss.core.event.batch.UserDesactivationBatchListener;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;

/**
 * Batch de désactivation des utilisateurs et d'information de l'administrateur.
 *
 * @author arn
 */
public class ReponsesUserDesactivationBatchListener extends UserDesactivationBatchListener implements StoppableBatch {

    @Override
    public STLogEnum getStoppageCode() {
        return ReponsesLogEnumImpl.CANCEL_B_DEACTIVATE_USERS_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
