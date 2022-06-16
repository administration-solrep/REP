package fr.dila.reponses.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ReponseActionService {
    boolean isReponseAtLastVersion(CoreSession session, DocumentModel dossierDoc);

    void saveReponse(SpecificContext context, DocumentModel reponseDoc, DocumentModel dossierDoc);

    boolean reponseHasChanged(CoreSession session, DocumentModel reponseDoc);

    /**
     * Supression du cachet serveur (signature) sur la reponse
     */
    void briserReponse(SpecificContext context);

    boolean canUserBriserReponse(SpecificContext context);
}
