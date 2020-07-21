package fr.dila.reponses.web.suggestion;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.recherche.SuggestionUtils;

@Name("indexMinistereProvider")
@Scope(ScopeType.CONVERSATION)
public class IndexMinistereSuggestionProvider implements ISuggestionProvider,
		Serializable {
	private static final long serialVersionUID = 1L;
	private static final String NAME = "INDEX_MINISTERE_PROVIDER";


	public List<String> getSuggestions(Object input) throws Exception {
	    return SuggestionUtils.getSuggestions((String) input,MAX_RESULTS,
                VocabularyConstants.MOTSCLEF_MINISTERE);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
