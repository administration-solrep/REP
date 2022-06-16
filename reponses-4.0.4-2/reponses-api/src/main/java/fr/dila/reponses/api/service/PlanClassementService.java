package fr.dila.reponses.api.service;

import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

public interface PlanClassementService {
    /**
     * Retourne la liste des indexations de niveau 1 pour le mode donnée (AN : Rubrique, Senat : Rubrique+Thème).
     *
     * @param treeMode
     * @return map des indexations
     */
    Map<String, Integer> getPlanClassementNiveau1(CoreSession session, String treeMode);

    /**
     * Retourne la liste des indexations de niveau 2 pour le mode et l'indexation de niveau 1 donnés.
     *
     * @param treeMode
     * @param indexation
     * @return map des indexations
     */
    Map<String, Integer> getPlanClassementNiveau2(CoreSession session, String treeMode, String indexation);

    /**
     * Permet de récupérer la requête exécutée pour afficher les dossiers par indexation
     * @param origine
     * @param indexNiv1
     * @param indexNiv2
     * @return
     */
    String getDossierFromIndexationQuery(String origine, String indexNiv1, String indexNiv2);
}
