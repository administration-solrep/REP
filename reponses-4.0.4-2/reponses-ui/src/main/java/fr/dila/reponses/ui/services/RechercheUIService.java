package fr.dila.reponses.ui.services;

import fr.dila.reponses.ui.bean.RechercheDTO;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service UI dédié aux fonctionnalités de recherche.
 *
 * @author tlombard
 */
public interface RechercheUIService {
    /**
     * Renvoie la liste des dossiers dont le paramètre correspond à l'origine et au
     * numéro de la question (recherche rapide).
     *
     * @param origineNumero le champ origineNumero
     * @return une liste de dossiers
     */
    RepDossierList getDossiersByOrigineNumero(String origineNumero, DossierListForm listForm, CoreSession session);

    RechercheDTO initRechercheDTO(RechercheDTO dto);

    RepDossierList getDossiersForRechercheAvancee(CoreSession session, RechercheDTO dto, DossierListForm listForm);

    void exportAllDossiers(SpecificContext context);
}
