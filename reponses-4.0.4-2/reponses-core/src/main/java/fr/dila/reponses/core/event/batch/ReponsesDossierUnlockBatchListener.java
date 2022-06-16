package fr.dila.reponses.core.event.batch;

import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.event.batch.DossierUnlockBatchListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Batch de déverrouillage de document
 *
 * @author ARN
 */
public class ReponsesDossierUnlockBatchListener extends DossierUnlockBatchListener implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesDossierUnlockBatchListener.class);

    public ReponsesDossierUnlockBatchListener() {
        super();
    }

    @Override
    protected String getDelai(CoreSession session) {
        // on récupère le paramètre sur la date maximal de verrouillage
        STParametreService paramService = STServiceLocator.getSTParametreService();
        //Valeur de levé du verrou dans réponses par défaut : 24
        String delaiVerrou = "24";
        try {
            delaiVerrou = paramService.getParametreValue(session, STParametreConstant.DELAI_VERROU_SUPPRIME);
        } catch (NuxeoException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_PARAM_TEC, exc);
        }

        return delaiVerrou;
    }

    @Override
    public STLogEnum getStoppageCode() {
        return STLogEnumImpl.CANCEL_B_UNLOCK_DOC_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
