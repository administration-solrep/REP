package fr.dila.reponses.api.vocabulary;

/**
 *
 * Wrapper autour d'un groupe de vocabulaire (ou d'un directory).
 * L'objet est censé être contruit à partir du service de vocabulaire.
 * Permet de traiter un groupe de vocabulaires de la même façon qu'un vocabulaire simple.
 * @author jgomez
 *
 */
public interface VocabularyGroupConnector extends VocabularyConnector {
    /**
     * Ajoute un connecteur de vocabulaire à ce groupe.
     * @param elt Le connecteur à ajouter.
     */
    public void add(VocabularyConnector elt);
}
