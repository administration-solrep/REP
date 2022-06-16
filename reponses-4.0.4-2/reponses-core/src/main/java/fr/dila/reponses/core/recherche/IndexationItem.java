package fr.dila.reponses.core.recherche;

import fr.dila.reponses.api.enumeration.IndexationTypeEnum;

public class IndexationItem {
    private final String vocabulary;
    private final String label;
    private final IndexationTypeEnum indexationType;

    public IndexationItem(String vocabulary, String label) {
        this.vocabulary = vocabulary;
        this.label = label;
        indexationType = IndexationTypeEnum.fromString(vocabulary);
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public String getLabel() {
        return label;
    }

    public IndexationTypeEnum getIndexationType() {
        return indexationType;
    }
}
