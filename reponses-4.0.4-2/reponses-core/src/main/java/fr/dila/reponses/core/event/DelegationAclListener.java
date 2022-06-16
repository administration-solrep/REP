package fr.dila.reponses.core.event;

import fr.dila.reponses.api.domain.user.Delegation;
import fr.dila.reponses.core.constant.DelegationConstant;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener qui permet d'envoyer un mail après la création / modification d'une délégation.
 *
 * @author feo
 */
public class DelegationAclListener implements EventListener {

    @Override
    public void handleEvent(Event event) {
        // Traite uniquement les évènements de création / modification de documents
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }

        if (
            !(
                (event.getName().equals(DocumentEventTypes.DOCUMENT_UPDATED)) ||
                (event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED))
            )
        ) {
            return;
        }

        // Traite uniquement les documents de type délégation
        DocumentEventContext context = (DocumentEventContext) ctx;
        DocumentModel delegationDoc = context.getSourceDocument();
        if (!DelegationConstant.DELEGATION_DOCUMENT_TYPE.equals(delegationDoc.getType())) {
            return;
        }
        CoreSession session = context.getCoreSession();

        Delegation delegation = delegationDoc.getAdapter(Delegation.class);
        ACP acp = delegationDoc.getACP();
        ACL functionAcl = acp.getOrCreateACL("delegation-destinataire-acl");
        functionAcl.clear();
        functionAcl.add(new ACE(delegation.getDestinataireId(), SecurityConstants.READ, true));
        acp.addACL(functionAcl);
        session.setACP(delegationDoc.getRef(), acp, true);
    }
}
