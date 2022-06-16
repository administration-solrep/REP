package fr.dila.reponses.ui.helper;

import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Helper pour listes de dossiers.
 *
 * @author tlombard
 */
public class RepDossierListHelper {

    private RepDossierListHelper() {
        // Default constructor
    }

    /**
     * Construit un objet DossierList à partir de la liste de DTO docList.
     *
     * @param docList
     * @param titre
     * @param sousTitre
     * @param form
     * @param lstUserColumn
     * @param total
     * @return un objet DossierList
     */
    public static RepDossierList buildDossierList(
        List<Map<String, Serializable>> docList,
        String titre,
        String sousTitre,
        DossierListForm form,
        List<String> lstUserColumn,
        int total,
        boolean fromCorbeille
    ) {
        RepDossierList lstResults = new RepDossierList();
        lstResults.buildColonnes(form, lstUserColumn, fromCorbeille);

        lstResults.setNbTotal(total);
        lstResults.setTitre(titre);
        lstResults.setSousTitre(sousTitre);

        // On fait le mapping des documents vers notre DTO
        for (Map<String, Serializable> doc : docList) {
            if (doc instanceof RepDossierListingDTO) {
                RepDossierListingDTO dto = (RepDossierListingDTO) doc;

                lstResults.getListe().add(dto);
            }
        }
        return lstResults;
    }
}
