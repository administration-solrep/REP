package fr.dila.reponses.api.service;

/**
 * Interface pour les controles
 *
 * @author asatre
 *
 */
public interface ControleService {
    /**
     * Comparaison de 2 textes de réponses (balise html, ponctuations,... supprimés)
     *
     * @param reponse1
     * @param reponse2
     * @return
     */
    boolean compareReponses(String reponse1, String reponse2);
}
