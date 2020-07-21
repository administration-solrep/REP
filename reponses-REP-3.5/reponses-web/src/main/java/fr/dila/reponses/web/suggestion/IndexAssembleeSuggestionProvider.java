package fr.dila.reponses.web.suggestion;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.recherche.SuggestionUtils;

@Name("indexAssembleeProvider")
@Scope(ScopeType.CONVERSATION)
public class IndexAssembleeSuggestionProvider implements ISuggestionProvider,
		Serializable {

	private static final long serialVersionUID = 1L;
	private static final String NAME = "INDEX_ASSEMBLEE_PROVIDER";
	
	public List<String> getSuggestions(Object input) {
		return SuggestionUtils.getSuggestions((String) input,MAX_RESULTS,
		                                        VocabularyConstants.TA_RUBRIQUE,VocabularyConstants.AN_RUBRIQUE,
		                                        VocabularyConstants.SE_THEME,VocabularyConstants.SE_RUBRIQUE);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
