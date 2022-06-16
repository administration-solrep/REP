package fr.dila.reponses.core.adapter;

import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.core.mailbox.ReponsesMailboxImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de document vers ReponsesMailbox.
 *
 * @author arolin
 */
public class ReponsesMailboxAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentFacet(doc, MailboxConstants.MAILBOX_FACET);
        checkDocumentSchemas(doc, ReponsesConstant.REPONSES_MAILBOX_SCHEMA);

        return new ReponsesMailboxImpl(doc);
    }
}
