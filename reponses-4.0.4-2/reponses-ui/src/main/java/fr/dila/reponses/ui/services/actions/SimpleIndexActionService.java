package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.core.recherche.IndexationProvider;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface SimpleIndexActionService {
    /**
     *
     * @return indexationProviderMap Un map chargé de faire le lien entre une
     *         zone d'indexation et une boite de suggestion.
     *
     */
    Map<String, IndexationProvider> getIndexationProviderMap();

    String addIndex(SpecificContext context, DocumentModel indexableDocument, IndexationProvider provider);

    void removeIndex(DocumentModel indexableDocument, String[] item);

    /**
     * Retourne un document adapté en ReponseIndexableDocument
     *
     * @param doc
     * @return Le document adapté
     */
    ReponsesIndexableDocument getDocumentAdapted(DocumentModel doc);
}
