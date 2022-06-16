/**
 *
 */
package fr.dila.reponses.api.service;

import fr.dila.reponses.api.vocabulary.VocabularyConnector;
import fr.dila.reponses.api.vocabulary.VocabularyGroupConnector;
import fr.dila.st.api.service.VocabularyService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jgomez
 *
 */
public interface ReponsesVocabularyService extends VocabularyService, Serializable {
    // *************************************************************
    // Methodes spécifique à l'application Réponses
    // *************************************************************
    /**
     * Renvoie un connecteur sur un vocabulaire, à partir duquel on peut faire des recherches
     * et des tests d'appartenance.
     * @param directoryName Le vocabulaire du connecteur
     * @return le connecteur
     */
    VocabularyConnector getVocabularyConnector(String directoryName);

    /**
     * Renvoie un groupe de connecteur?
     * Les tests et les suggestions sont effectués sur l'ensemble des vocabulaires.
     * @param directoryNames La liste des vocabulaires
     * @return le connecteur du groupe
     *
     */
    VocabularyGroupConnector getVocabularyConnectorGroup(String... directoryNames);

    /**
     * Retourne la liste des vocabulaires du service
     * @return La liste des vocabulaires
     */
    List<String> getVocabularyList();

    /**
     * Une map vocabulaire/zone (une zone étant un groupe de vocabulaire lié, par exemple
     * la zone Assemblée Nationnale liant les AN_rubrique, AN_analyse, et AN_TA)
     * @return
     */
    Map<String, List<String>> getMapVocabularyToZone();

    /**
     * Renvoie le connecteur de l'Assemblée
     * @return Le groupe de connecteur de la zone AN
     */
    VocabularyConnector getIndexation_AN();

    /**
     * Renvoie le connecteur des rubriques de l'AN
     * @return le connecteur des rubriques de l'AN
     */
    VocabularyConnector getIndexationAN_Rubrique();

    /**
     * Renvoie le connecteur des TA de l'AN
     * @return le connecteur des TA de l'AN
     */
    VocabularyConnector getIndexationAN_TARubrique();

    /**
     * Renvoie le connecteur des analyses de l'AN
     * @return le connecteur des analyses de l'AN
     */
    VocabularyConnector getIndexation_ANAnalyse();

    /** Indexation Sénat **/
    /**
     * Renvoie le connecteur du groupe SE
     * @return le connecteur du groupe de la zone Sénat
     */
    VocabularyConnector getIndexation_SE();

    /**
     * Renvoie le connecteur des thèmes du Sénat
     * @return le connecteur des thèmes du Sénat
     */
    VocabularyConnector getIndexationSE_Theme();

    /**
     * Renvoie le connecteur des rubriques du Sénat
     * @return le connecteur des rubriques du Sénat
     */
    VocabularyConnector getIndexationSE_Rubrique();

    /**
     * Renvoie le connecteur des renvois du Sénat
     * @return le connecteur des renvois du Sénat
     */
    VocabularyConnector getIndexationSE_Renvoi();

    // *************************************************************
    // Indexation Ministère
    // *************************************************************
    /**
     * Renvoie le connecteur des mots-clés ministère
     * @return le connecteur des mots-clés ministère
     */
    VocabularyConnector getIndexationMinistere();

    /**
     * Retourne la liste des zones d'indexation de l'application Réponses
     * @return
     */
    Set<String> getZones();
}
