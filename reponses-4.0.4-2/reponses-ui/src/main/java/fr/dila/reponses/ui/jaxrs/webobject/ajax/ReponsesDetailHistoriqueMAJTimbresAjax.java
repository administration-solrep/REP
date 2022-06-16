package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.ui.bean.DetailHistoriqueMAJTimbresList;
import fr.dila.reponses.ui.bean.DetailMigrationHistoriqueMAJTimbresList;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "DetailHistoriqueMAJTimbres")
public class ReponsesDetailHistoriqueMAJTimbresAjax extends SolonWebObject {
    public static final int MAJ_TIMBRES_ORDER = Breadcrumb.TITLE_ORDER;
    public static final String DATA_URL = "/admin/timbres/historique";
    public static final String DATA_DETAIL_URL = "/admin/timbres/detail?message=";
    public static final String DATA_AJAX_URL = "/ajax/timbres/historique";

    public ReponsesDetailHistoriqueMAJTimbresAjax() {
        super();
    }

    @GET
    public ThTemplate getDetail(@QueryParam("id") String id) {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        template.setContext(context);

        context.setCurrentDocument(id);
        String titre = context.getCurrentDocument().getAdapter(ReponsesLogging.class).getMessage();

        context.setNavigationContextTitle(
            new Breadcrumb(titre, DATA_DETAIL_URL + id, MAJ_TIMBRES_ORDER + 1, context.getWebcontext().getRequest())
        );

        DetailHistoriqueMAJTimbresList detailhistoriqueList = ReponsesUIServiceLocator
            .getReponsesMigrationManagerUIService()
            .getDetailHistoriqueMAJTimbresList(context);

        template.getData().put(STTemplateConstants.RESULT_LIST, detailhistoriqueList);
        template.getData().put(STTemplateConstants.LST_COLONNES, detailhistoriqueList.getListeColonnes());
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        template.getData().put(STTemplateConstants.ID, id);
        template.getData().put(STTemplateConstants.TITRE, titre);

        return template;
    }

    @POST
    @Path("consulter")
    public ThTemplate consulterDetail(@FormParam("id") String id) {
        template = new AjaxLayoutThTemplate();
        template.setName("fragments/table/tableDetailMigrationHistoriqueMAJTimbres");
        template.setContext(context);

        context.setCurrentDocument(id);
        DetailMigrationHistoriqueMAJTimbresList detailMigrationhistoriqueList = ReponsesUIServiceLocator
            .getReponsesMigrationManagerUIService()
            .getDetailMigrationHistoriqueMAJTimbresList(context);

        template.getData().put(ReponsesTemplateConstants.ID_DETAIL, id);
        template.getData().put(STTemplateConstants.RESULT_LIST, detailMigrationhistoriqueList);
        template.getData().put(STTemplateConstants.LST_COLONNES, detailMigrationhistoriqueList.getListeColonnes());

        return template;
    }

    @POST
    @Path("rafraichir")
    public ThTemplate refreshDetail(@FormParam("id") String id) {
        template = getMyTemplate();
        template.setContext(context);
        return getDetail(id);
    }

    @POST
    @Path("rafraichirDetail")
    public ThTemplate refreshDetailMigration(@FormParam("id") String id) {
        template = getMyTemplate();
        template.setContext(context);
        return consulterDetail(id);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        ThTemplate myTemplate = new AjaxLayoutThTemplate(
            "fragments/table/tableDetailHistoriqueMAJTimbres",
            getMyContext()
        );
        myTemplate.setData(new HashMap<>());
        return myTemplate;
    }
}
