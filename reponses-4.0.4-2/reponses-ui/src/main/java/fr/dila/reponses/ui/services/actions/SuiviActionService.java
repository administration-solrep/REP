package fr.dila.reponses.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 * Action service permettant de gérer l'espace de suivi.
 *
 * @author jtremeaux
 */
public interface SuiviActionService {
    /**
     * Donne la liste des requêtes sauvegardé.
     *
     * @return
     */
    DocumentModelList getSavedRequetes(CoreSession session);

    boolean isEditingAlert(DocumentModel currentDoc);

    /**
     * Supprime le document requête.
     *
     * @param doc
     * @return
     */
    void delete(SpecificContext context, CoreSession session, DocumentModel doc);
}
