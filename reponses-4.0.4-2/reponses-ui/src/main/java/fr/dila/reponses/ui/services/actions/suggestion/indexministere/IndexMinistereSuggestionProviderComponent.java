package fr.dila.reponses.ui.services.actions.suggestion.indexministere;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class IndexMinistereSuggestionProviderComponent
    extends ServiceEncapsulateComponent<IndexMinistereSuggestionProviderService, IndexMinistereSuggestionProviderServiceImpl> {

    /**
     * Default constructor
     */
    public IndexMinistereSuggestionProviderComponent() {
        super(IndexMinistereSuggestionProviderService.class, new IndexMinistereSuggestionProviderServiceImpl());
    }
}
