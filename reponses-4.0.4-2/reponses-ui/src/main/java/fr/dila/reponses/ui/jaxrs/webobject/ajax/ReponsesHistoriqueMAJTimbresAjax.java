package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.bean.HistoriqueMAJTimbresList;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "HistoriqueMAJTimbres")
public class ReponsesHistoriqueMAJTimbresAjax extends SolonWebObject {
    private static final String DATA_URL = "/admin/timbres/historique";
    private static final String DATA_AJAX_URL = "/ajax/timbres/historique";

    public ReponsesHistoriqueMAJTimbresAjax() {
        super();
    }

    @GET
    public ThTemplate getHistorique() {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        template.setContext(context);

        HistoriqueMAJTimbresList historiqueList = ReponsesUIServiceLocator
            .getReponsesMigrationManagerUIService()
            .getHistoriqueMAJTimbresList(context.getSession());

        template.getData().put(STTemplateConstants.RESULT_LIST, historiqueList);
        template.getData().put(STTemplateConstants.NB_RESULTS, historiqueList.getListe().size());
        template.getData().put(STTemplateConstants.LST_COLONNES, historiqueList.getListeColonnes());
        template.getData().put(STTemplateConstants.DATA_URL, DATA_URL);
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, DATA_AJAX_URL);

        return template;
    }

    @GET
    @Path("rafraichir")
    public ThTemplate refreshHistorique() {
        template = getMyTemplate();
        template.setContext(context);
        return getHistorique();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        ThTemplate myTemplate = new AjaxLayoutThTemplate("fragments/table/tableHistoriqueMAJTimbres", getMyContext());
        myTemplate.setData(new HashMap<>());
        return myTemplate;
    }
}
