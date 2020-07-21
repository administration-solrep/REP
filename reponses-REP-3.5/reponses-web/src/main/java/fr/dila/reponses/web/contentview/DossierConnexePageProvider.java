package fr.dila.reponses.web.contentview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.web.client.DossierConnexeDTO;
import fr.dila.reponses.web.connexe.DossierConnexeActionsBean;
import fr.dila.st.web.contentview.AbstractDTOPageProvider;

/**
 * Provider pour les dossiers connexes.
 * 
 * @author asatre
 */
public class DossierConnexePageProvider extends AbstractDTOPageProvider {

    private static final String DOSSIER_CONNEXE_ACTIONS = "dossierConnexeActions";
    private static final long serialVersionUID = 1L;

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) throws ClientException {
        Map<String, Serializable> props = getProperties();
        DossierConnexeActionsBean bean =  (DossierConnexeActionsBean) props.get(DOSSIER_CONNEXE_ACTIONS);
        
        List<DossierConnexeDTO> items =  bean.getListResult();

        if (!items.isEmpty()) {
            Collections.sort(items, new Comparator<DossierConnexeDTO>() {
                @Override
                public int compare(DossierConnexeDTO o1, DossierConnexeDTO o2) {
                    return o1.getNumeroDossier().compareTo(o2.getNumeroDossier());
                }
            });
        }
        
        resultsCount = items.size();
        currentItems = new ArrayList<Map<String,Serializable>>(items);
    }
}
