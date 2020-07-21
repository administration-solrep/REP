package fr.dila.reponses.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.cases.SetDossierRightUnrestricted;
import fr.dila.st.core.event.RollbackEventListener;

/**
 * Gestionnaire d'évènements qui permet de donner les ACE au dossier lors de sa création 
 * 
 * @author sly
 */
public class DossierRightSetterListener extends RollbackEventListener  {
    @Override
    public void handleDocumentEvent(Event event , DocumentEventContext ctx) throws ClientException {
        // Traite uniquement les évènements de document sur le point d'être créé
        if (!(event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED))) {
            return;
        }
        
        // Traite uniquement les documents de type Dossier
        DocumentModel doc = ctx.getSourceDocument();
        CoreSession session = ctx.getCoreSession();
        String docType = doc.getType();
        if (!DossierConstants.DOSSIER_DOCUMENT_TYPE.equals(docType)) {
            return;
        }
        Dossier dossier = doc.getAdapter(Dossier.class);
        
        
        SetDossierRightUnrestricted runner = new SetDossierRightUnrestricted(session, dossier);
        runner.runUnrestricted();
    }
    
}
