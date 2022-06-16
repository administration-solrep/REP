package fr.dila.reponses.ui.services.actions.suggestion.indexassemblee;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class IndexAssembleeSuggestionProviderComponent
    extends ServiceEncapsulateComponent<IndexAssembleeSuggestionProviderService, IndexAssembleeSuggestionProviderServiceImpl> {

    /**
     * Default constructor
     */
    public IndexAssembleeSuggestionProviderComponent() {
        super(IndexAssembleeSuggestionProviderService.class, new IndexAssembleeSuggestionProviderServiceImpl());
    }
}
