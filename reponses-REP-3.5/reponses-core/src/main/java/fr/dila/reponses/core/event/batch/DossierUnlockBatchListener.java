package fr.dila.reponses.core.event.batch;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Batch de déverrouillage de document
 * 
 * @author ARN
 */
public class DossierUnlockBatchListener extends fr.dila.st.core.event.batch.DossierUnlockBatchListener {

    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(DossierUnlockBatchListener.class);

    public DossierUnlockBatchListener(){
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
        } catch (ClientException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_PARAM_TEC, exc);
        }
        
        return delaiVerrou;
    }
}
