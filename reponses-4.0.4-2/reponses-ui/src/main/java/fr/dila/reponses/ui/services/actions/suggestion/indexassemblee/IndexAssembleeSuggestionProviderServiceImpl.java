package fr.dila.reponses.ui.services.actions.suggestion.indexassemblee;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;

import fr.dila.reponses.core.recherche.SuggestionUtils;
import fr.dila.reponses.ui.services.actions.suggestion.SuggestionConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public class IndexAssembleeSuggestionProviderServiceImpl implements IndexAssembleeSuggestionProviderService {
    private static final String NAME = "INDEX_ASSEMBLEE_PROVIDER";

    @Override
    public List<String> getSuggestions(String input, SpecificContext context) {
        return SuggestionUtils.getSuggestions(
            input,
            SuggestionConstants.MAX_RESULTS,
            TA_RUBRIQUE.getValue(),
            AN_RUBRIQUE.getValue(),
            SE_THEME.getValue(),
            SE_RUBRIQUE.getValue()
        );
    }

    @Override
    public String getName() {
        return NAME;
    }
}
