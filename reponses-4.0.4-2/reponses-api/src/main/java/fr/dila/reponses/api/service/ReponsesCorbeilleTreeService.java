package fr.dila.reponses.api.service;

import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.ss.api.security.principal.SSPrincipal;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service qui permet de gérer l'arbre des corbeilles
 *
 */
public interface ReponsesCorbeilleTreeService {
    public enum EtatSignalement {
        QUESTIONS_SIGNALEES,
        QUESTIONS_NON_SIGNALEES
    }

    /**
     * Retourne la liste des corbeilles de niveau 1 pour le type de regroupement
     * donné (par ministère, par poste, par état signalé).
     *
     * @param session
     * @param regroupement
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @return
     */
    Map<String, Integer> getCorbeilleTreeNiveau1(
        CoreSession session,
        TypeRegroupement regroupement,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    );

    /**
     * Retourne la liste des corbeilles de niveau 2 pour le type de regroupement et
     * l'élément de niveau 1 donnés.
     *
     * @param session
     * @param regroupement
     * @param parentId
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @return
     */
    Map<String, Integer> getCorbeilleTreeNiveau2(
        CoreSession session,
        TypeRegroupement regroupement,
        String parentId,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    );

    /**
     * Retourne les ministères parents des postes de l'utilisateur
     *
     * @param ssPrincipal
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @return
     */
    Set<String> getMinisteresParentsFromPostes(
        SSPrincipal ssPrincipal,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    );
}
