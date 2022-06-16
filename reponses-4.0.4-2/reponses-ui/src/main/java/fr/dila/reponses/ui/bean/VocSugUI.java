package fr.dila.reponses.ui.bean;

import fr.dila.reponses.core.recherche.SuggestionUtils;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VocSugUI {
    private static final int MAX_RESULT = 10;
    /**
     * Garde l'index qui est prêt à être ajouté
     */
    private String indexLabel;
    private static final Log LOG = LogFactory.getLog(VocSugUI.class);

    private String vocabularyLabel;

    public VocSugUI(String vocId) {
        vocabularyLabel = vocId;
    }

    public void reset() {
        indexLabel = null;
    }

    public String getIndexLabel() {
        return indexLabel;
    }

    public void setIndexLabel(String indexLabel) {
        this.indexLabel = indexLabel;
    }

    // Une category est une référence à un vocabulaire
    public String getVocabularyLabel() {
        return this.vocabularyLabel;
    }

    public void setVocabularyLabel(String vocabularyLabel) {
        this.vocabularyLabel = vocabularyLabel;
    }

    public List<String> getSuggestions(Object input) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suggestions : " + vocabularyLabel);
        }
        return SuggestionUtils.getSuggestions((String) input, MAX_RESULT, vocabularyLabel);
    }
}
