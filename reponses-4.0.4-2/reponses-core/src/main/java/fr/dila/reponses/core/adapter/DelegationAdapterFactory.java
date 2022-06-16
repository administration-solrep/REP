package fr.dila.reponses.core.adapter;

import fr.dila.reponses.core.constant.DelegationConstant;
import fr.dila.reponses.core.domain.user.DelegationImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique des adapteurs de DocumentModel vers Delegation.
 *
 * @author jtremeaux
 */
public class DelegationAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public DelegationAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentType(doc, DelegationConstant.DELEGATION_DOCUMENT_TYPE);
        return new DelegationImpl(doc);
    }
}
