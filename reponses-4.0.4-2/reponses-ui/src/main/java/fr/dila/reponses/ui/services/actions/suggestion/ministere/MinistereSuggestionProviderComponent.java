package fr.dila.reponses.ui.services.actions.suggestion.ministere;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class MinistereSuggestionProviderComponent
    extends ServiceEncapsulateComponent<MinistereSuggestionProviderService, MinistereSuggestionProviderServiceImpl> {

    /**
     * Default constructor
     */
    public MinistereSuggestionProviderComponent() {
        super(MinistereSuggestionProviderService.class, new MinistereSuggestionProviderServiceImpl());
    }
}
