package fr.dila.ss.web.contentview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.ss.web.feuilleroute.DocumentRoutingActionsBean;
import fr.dila.st.web.contentview.AbstractDTOPageProvider;

/**
 * Provider pour la creation des étapes de feuille de route en masse
 * 
 * @author nvezian
 * 
 */
public class EtapesFeuilleDeRoutePageProvider extends AbstractDTOPageProvider{

    private static final String ETAPE_FDR_ACTIONS = "routingActions";
    private static final long serialVersionUID = 1L;


    @Override
	protected void fillCurrentPageMapList(CoreSession coreSession) throws ClientException {
        DocumentRoutingActionsBean DocumentRoutingBean = getBean();
        currentItems = new ArrayList<Map<String, Serializable>>();
        
        currentItems.addAll(DocumentRoutingBean.getLstEtapes());
        
        resultsCount = currentItems.size();
    }

    private DocumentRoutingActionsBean getBean(){
        Map<String, Serializable> props = getProperties();
        return (DocumentRoutingActionsBean) props.get(ETAPE_FDR_ACTIONS);
    }
}
