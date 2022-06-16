package fr.dila.reponses.ui.services;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.bean.RequetePersoForm;
import fr.dila.ss.ui.bean.RequeteLigneDTO;
import fr.dila.ss.ui.enums.EtapeLifeCycle;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service UI dédié aux fonctionnalités de requêtage.
 *
 * @author fskaff
 *
 */
public interface RequeteUIService {
    /**
     * Retourne les résultats d'une {@link RequeteExperte} à partir d'un idRequete
     *
     * @param context
     * @return
     */
    RepDossierList getDossiersByRequeteExperte(SpecificContext context);

    /**
     * Retourne vrai s'il s'agit d'une requête générale et faux si personnelle
     *
     * @param session
     * @param idRequete
     * @return
     */
    boolean isRequeteGenerale(CoreSession session, String idRequete);

    /**
     * Retourne des résultats à partir d'une liste de {@link RequeteLigneDTO}
     *
     * @param context
     * @return
     */
    RepDossierList getDossiersByRequeteCriteria(SpecificContext context);

    /**
     * Renvoie les éléments à afficher dans le requêteur de Réponses
     *
     * @return une liste de EtapeLifeCycle
     */
    List<EtapeLifeCycle> getRequeteurStepStates();

    /**
     * Renvoie le formulaire pour la création d'une requete personnelle
     *
     * @param context
     * @return
     */
    RequetePersoForm getRequetePersoForm(List<RequeteLigneDTO> lignes);

    /**
     * Création d'une requete personnelle
     *
     * @param context
     * @param form
     */
    String saveRequetePerso(SpecificContext context);

    /**
     * Récupération d'une requete personnelle
     * @param context
     * @return
     */
    RequetePersoForm getRequetePerso(SpecificContext context);

    /**
     * Suppression d'une requete personnelle
     * @param context
     */
    void deleteRequetePerso(SpecificContext context);

    /**
     * Recuperation des lignes de la requête à partir de la clause where
     * @param context
     * @return
     */
    List<RequeteLigneDTO> getRequeteLignesFromRequete(SpecificContext context);

    /**
     * Retourne le formulaire de sauvegarde de requetes à partir d'une id et d'une liste de critères
     *
     * @param context
     * @return
     */
    RequetePersoForm getRequetePersoFormWithId(SpecificContext context);
}
