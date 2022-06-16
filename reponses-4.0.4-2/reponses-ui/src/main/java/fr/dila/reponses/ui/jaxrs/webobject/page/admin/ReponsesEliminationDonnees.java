package fr.dila.reponses.ui.jaxrs.webobject.page.admin;

import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.bean.PaginationForm;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "EliminationDonnees")
public class ReponsesEliminationDonnees extends SolonWebObject {
    public static final int DONNEES_ORDER = Breadcrumb.TITLE_ORDER;
    public static final String DONNEES_KEY = "donnees";
    public static final String DATA_URL = "/admin/eliminationDonnees/liste";
    public static final String DATA_URL_CONSULT = "/admin/eliminationDonnees/consultation";
    public static final String NAVIGATION_TITLE = "menu.admin.param.archivage.title";

    public ReponsesEliminationDonnees() {
        super();
    }

    @Path("liste")
    public Object getListe(@SwBeanParam PaginationForm eliminationDonneesListform) {
        verifyAction(ReponsesActionEnum.ADMIN_PARAM_ARCHIVAGE, DATA_URL);
        ThTemplate template = getMyTemplate();
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        template.setContext(context);
        template.setName("pages/admin/donnees/liste");
        context.setNavigationContextTitle(
            new Breadcrumb(NAVIGATION_TITLE, DATA_URL, DONNEES_ORDER, context.getWebcontext().getRequest())
        );

        return newObject("EliminationDonneesAjax", context, template);
    }

    @Path("consultation")
    public Object getListeConsult(@QueryParam("id") String id) {
        verifyAction(ReponsesActionEnum.ADMIN_PARAM_ARCHIVAGE, DATA_URL_CONSULT);

        ThTemplate template = getMyTemplate();
        template.setData(new HashMap<>());
        template.setContext(context);
        template.setName("pages/admin/donnees/consultation");
        context.setCurrentDocument(id);
        context.setNavigationContextTitle(
            new Breadcrumb(
                ReponsesUIServiceLocator.getArchiveUIService().getTitreListeElimination(context),
                DATA_URL_CONSULT,
                DONNEES_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );
        template.getData().put("idListe", id);
        return newObject("EliminationDonneesConsultationAjax", context, template);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesAdminTemplate();
    }
}
