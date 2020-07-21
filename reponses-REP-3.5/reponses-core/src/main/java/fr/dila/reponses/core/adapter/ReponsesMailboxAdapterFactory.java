package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.core.mailbox.ReponsesMailboxImpl;

/**
 * Adapteur de document vers ReponsesMailbox.
 * 
 * @author arolin
 */
public class ReponsesMailboxAdapterFactory implements DocumentAdapterFactory {

	@Override
	public Object getAdapter(DocumentModel doc,@SuppressWarnings("rawtypes") Class arg1) {
        if (!checkDocument(doc)) {
            return null;
        }
        
        return new ReponsesMailboxImpl(doc);
	}
	
    protected Boolean checkDocument(DocumentModel doc) {
        // Vérifie si le document est une Mailbox
        if (!doc.hasFacet(MailboxConstants.MAILBOX_FACET)) {
            return false;
        }
        
        // Si c'est une mailbox, il doit contenir le  schéma reponsesMailbox (sinon c'est une erreur)
        if (!doc.hasSchema(ReponsesConstant.REPONSES_MAILBOX_SCHEMA)) {
            throw new CaseManagementRuntimeException("Document should contain schema " + ReponsesConstant.REPONSES_MAILBOX_SCHEMA);
        }
        return true;
    }

}
