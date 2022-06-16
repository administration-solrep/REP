package fr.dila.reponses.core.recherche;

import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.api.vocabulary.VocabularyConnector;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Utilité pour les suggestions.
 *
 * @author jgomez
 */
public class SuggestionUtils {

    /**
     * Exemple d'appel : getSuggestions("hel",10,"senatTheme","senatRubrique") retourne tous les mots-clés commençant
     * par hel dans les vocabulaires senatTheme et senatRubrique
     *
     * @param suggestion
     *            Les premières lettres du mot
     * @param maxResult
     *            La limitation du nombre de résultats
     * @param directoryNameList
     * @return
     */
    public static List<String> getSuggestions(String suggestion, int maxResult, String... directoryNameList) {
        ReponsesVocabularyService vocService = ReponsesServiceLocator.getVocabularyService();
        VocabularyConnector elt = vocService.getVocabularyConnectorGroup(directoryNameList);
        List<String> results = elt.getSuggestion(suggestion);
        if (results.size() > maxResult) {
            results = results.subList(0, maxResult);
        }
        return results;
    }

    /**
     * Exemple d'appel : getSimpleSuggestions("hel",10,"senatTheme","senatRubrique") retourne tous les mots-clés
     * commençant par hel dans les vocabulaires senatTheme et senatRubrique en retournant une paire (vocname, label)
     *
     * @param suggestion
     *            Les premières lettres du mot
     * @param maxResult
     *            La limitation du nombre de résultats
     * @param directoryNameList
     * @return
     */
    public static List<IndexationItem> getSimpleSuggestions(String suggestion, int maxResult, List<String> vocs) {
        ReponsesVocabularyService vocService = ReponsesServiceLocator.getVocabularyService();
        String[] varargs = vocs.toArray(new String[vocs.size()]);
        VocabularyConnector elt = vocService.getVocabularyConnectorGroup(varargs);
        List<String> results = elt.getSuggestion(suggestion);
        if (results.size() > maxResult) {
            results = results.subList(0, maxResult);
        }
        // Retrouve la provenance des suggestions (pas pratique, il faudrait le faire avant
        // peut-être en mettant en place un VocabulaireConnector spécifique).
        List<IndexationItem> items = addVocPrefixToResults(vocs, vocService, results);
        return items;
    }

    private static List<IndexationItem> addVocPrefixToResults(
        List<String> vocs,
        ReponsesVocabularyService vocService,
        List<String> results
    ) {
        List<IndexationItem> items = new ArrayList<>();
        for (String result : results) {
            for (String voc : vocs) {
                if (vocService.checkData(voc, "label", result) && !voc.equals("TA_rubrique")) {
                    items.add(new IndexationItem(voc, result));
                }
            }
        }
        return items;
    }
}
