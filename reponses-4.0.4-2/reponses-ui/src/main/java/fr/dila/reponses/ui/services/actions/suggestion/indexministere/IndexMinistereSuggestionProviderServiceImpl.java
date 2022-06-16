package fr.dila.reponses.ui.services.actions.suggestion.indexministere;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.MOTSCLEF_MINISTERE;

import fr.dila.reponses.core.recherche.SuggestionUtils;
import fr.dila.reponses.ui.services.actions.suggestion.SuggestionConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public class IndexMinistereSuggestionProviderServiceImpl implements IndexMinistereSuggestionProviderService {
    private static final String NAME = "INDEX_MINISTERE_PROVIDER";

    @Override
    public List<String> getSuggestions(String input, SpecificContext context) {
        return SuggestionUtils.getSuggestions(input, SuggestionConstants.MAX_RESULTS, MOTSCLEF_MINISTERE.getValue());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
