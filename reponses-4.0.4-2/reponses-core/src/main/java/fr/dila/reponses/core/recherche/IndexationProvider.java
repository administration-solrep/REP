package fr.dila.reponses.core.recherche;

import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Une classe pour le widget simpleIndexation. Permet d'offrir des suggestions en regroupant les différents vocabulaires d'une même zone dans une même richSuggestionBox.
 *
 * @author jgomez
 *
 */
public class IndexationProvider {
    private static final int MAX_RESULTS = 10;

    private String label, vocabulary, zone;

    private static final Log log = LogFactory.getLog(IndexationProvider.class);

    public IndexationProvider(String zone) {
        this.zone = zone;
        this.label = "";
    }

    public void reset() {
        label = "";
        vocabulary = "";
    }

    public List<IndexationItem> getSuggestions(Object input) {
        List<IndexationItem> items = new ArrayList<>();
        try {
            ReponsesVocabularyService vocservice = ReponsesServiceLocator.getVocabularyService();
            Map<String, List<String>> zoneMap = vocservice.getMapVocabularyToZone();
            items = SuggestionUtils.getSimpleSuggestions((String) input, MAX_RESULTS, zoneMap.get(zone));
        } catch (Exception e) {
            log.warn("Erreur non identifiée dans la recherche de suggestion ", e);
        }
        return items;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String voc) {
        this.vocabulary = voc;
    }
}
