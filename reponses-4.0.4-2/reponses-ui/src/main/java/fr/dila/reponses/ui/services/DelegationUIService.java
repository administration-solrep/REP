package fr.dila.reponses.ui.services;

import fr.dila.reponses.ui.bean.DelegationsDonneesList;
import fr.dila.reponses.ui.bean.DelegationsRecuesList;
import fr.dila.reponses.ui.th.bean.DelegationForm;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Action service permettant de gérer la délégation des droits.
 *
 * @author jtremeaux
 */
public interface DelegationUIService {
    DocumentModel createDelegation(SpecificContext context);

    DocumentModel updateDelegation(SpecificContext context);

    DelegationForm fetchDelegation(SpecificContext context);

    void deleteDelegation(SpecificContext context);

    List<SelectValueDTO> getProfilsForCurrentUser(SpecificContext context);

    DelegationsDonneesList fetchDelegationsDonnees(SpecificContext context);

    DelegationsRecuesList fetchDelegationsRecues(SpecificContext context);
}
