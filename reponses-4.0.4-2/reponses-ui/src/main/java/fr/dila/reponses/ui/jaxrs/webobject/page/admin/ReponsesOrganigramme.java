package fr.dila.reponses.ui.jaxrs.webobject.page.admin;

import com.google.common.collect.ImmutableList;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.reponses.ui.th.model.ReponsesUtilisateurTemplate;
import fr.dila.ss.ui.jaxrs.webobject.page.admin.SSOrganigramme;
import fr.dila.ss.ui.services.SSOrganigrammeManagerService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.impl.SSOrganigrammeManagerServiceImpl;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliOrganigramme")
public class ReponsesOrganigramme extends SSOrganigramme {

    public ReponsesOrganigramme() {
        super();
    }

    @Override
    @Path("/consult")
    public Object getUserOrganigramme() {
        ThTemplate template = getMyTemplate(context);

        template.setName("pages/admin/user/organigramme");
        template.setContext(context);
        context.setNavigationContextTitle(
            new Breadcrumb("Gestion de l'organigramme", ORGANIGRAMME_URL, ORGANIGRAMME_ORDER)
        );
        // On vide les valeurs du formulaire de mise à jour des timbres
        UserSessionHelper.putUserSessionParameter(context, ReponsesMiseAJourTimbres.PARAMETRE_TIMBRE_KEY, null);

        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();

            template.setData(map);
        }

        SSOrganigrammeManagerService service = SSUIServiceLocator.getSSOrganigrammeManagerService();
        template
            .getData()
            .put(
                SSOrganigrammeManagerServiceImpl.ORGANIGRAMME_BASE_ACTIONS_KEY,
                service.loadBaseAdminOrganigrammeActions(context.getSession())
            );
        template
            .getData()
            .put(
                SSOrganigrammeManagerServiceImpl.ORGANIGRAMME_MAIN_ACTIONS_KEY,
                service.loadMainAdminOrganigrammeActions(context.getSession())
            );

        template.getData().put("acceptedTypes", ImmutableList.of("xls", "xlsx"));

        return newObject("OrganigrammeAjax", context, template);
    }

    @Produces("application/vnd.ms-excel")
    @GET
    @Path("/download")
    public Response download(@QueryParam("fileName") String fileName) {
        return FileDownloadUtils.getResponse(FileUtils.getAppTmpFolder(), FileUtils.sanitizePathTraversal(fileName));
    }

    @Override
    protected ThTemplate getMyTemplate(SpecificContext context) {
        if (context.getWebcontext().getPrincipal().isMemberOf("EspaceAdministrationReader")) {
            return new ReponsesAdminTemplate();
        } else {
            return new ReponsesUtilisateurTemplate();
        }
    }
}
