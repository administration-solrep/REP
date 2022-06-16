package fr.dila.reponses.api.recherche;

import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ReponsesIndexableDocument {
    /**
     * Ajoute une donnée d'indexation (vocabulaire, valeur) au document.
     * @param vocabularyLabel : Le vocabulaire
     * @param indexLabel : La valeur
     * @return
     *
     */
    void addVocEntry(String vocabularyLabel, String indexLabel);

    /**
     * Enlève une donnée d'indexation (vocabulaire, valeur) au document.
     * @param vocabularyLabel : Le vocabulaire
     * @param indexLabel : La valeur
     * @return
     *
     */
    void removeVocEntry(String vocabulary, String label);

    /**
     * Retourne la liste des valeurs pour une zone d'indexation donnée.
     * Une zone d'indexation est un ensemble de vocabualaire - exemple AN pour les rubriques, les TA, les analyses
     * @param indexationzoneName
     * @return La liste des valeurs (un tableau contenant vocabulaire, libellé, label du vocabulaire) sur le document pour la zone donnée.
     *
     */
    List<String[]> getListIndexByZone(String indexationzoneName);

    /**
     * Retourne une liste de tous les mots-clef sur le document
     * @return
     *
     */
    String getMotsClef();

    /**
     * Retourne le document model
     * @return
     */
    DocumentModel getDocument();

    /**
     * Retourne l'ensemble des entrées relatif à un vocabulaire sur le document
     * @param voc
     * @param indexList
     *
     */
    void setVocEntries(String voc, List<String> indexList);

    /**
     * Place une liste d'entrées relatives à un vocabulaire sur un document.
     * @param voc
     * @return
     *
     */
    List<String> getVocEntries(String voc);

    /* --- elements d'indexation --- */
    /**
     * Gets the Question Analyse Assemblee Nationale.
     */
    List<String> getAssNatAnalyses();

    void setAssNatAnalyses(List<String> analyseAssNat);

    /**
     * Gets the Question Rubrique Assemblee Nationale.
     */
    List<String> getAssNatRubrique();

    void setAssNatRubrique(List<String> rubriqueAssNat);

    /**
     * Gets the Question Tete Analyse Assemblee Nationale.
     */
    List<String> getAssNatTeteAnalyse();

    void setAssNatTeteAnalyse(List<String> teteAnalyseAssNat);

    /**
     * Gets the Question Senat Themes.
     */
    List<String> getSenatQuestionThemes();

    void setSenatQuestionThemes(List<String> senatQuestionTheme);

    /**
     * Gets the Question Senat Rubrique.
     */
    List<String> getSenatQuestionRubrique();

    void setSenatQuestionRubrique(List<String> senatRubrique);

    /**
     * Gets the Question Senat Renvois.
     */
    List<String> getSenatQuestionRenvois();

    void setSenatQuestionRenvois(List<String> senatRenvois);

    /**
     * mot cle ministere
     */
    List<String> getMotsClefMinistere();

    void setMotsClefMinistere(List<String> motClefsMinistere);
}
