package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.event.batch.CleanDeletedDocumentBatchListener;
import fr.dila.st.core.factory.STLogFactory;
import java.util.LinkedList;

/**
 * Batch de nettoyage des allotissements à l'état deleted
 *
 */
public class CleanDeletedAllotissementBatchListener
    extends CleanDeletedDocumentBatchListener
    implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CleanDeletedAllotissementBatchListener.class);

    /**
     * Default constructor
     */
    public CleanDeletedAllotissementBatchListener() {
        super(ReponsesEventConstant.CLEAN_DELETED_ALLOT_EVENT, getDocumentTypes());
    }

    protected static final LinkedList<String> getDocumentTypes() {
        LinkedList<String> documents = new LinkedList<String>();
        documents.add(DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE);
        return documents;
    }

    @Override
    public STLogEnum getStoppageCode() {
        return STLogEnumImpl.CANCEL_B_DEL_DOC_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
