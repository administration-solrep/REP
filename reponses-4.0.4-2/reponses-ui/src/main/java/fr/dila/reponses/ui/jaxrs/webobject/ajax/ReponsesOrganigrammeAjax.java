package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.services.actions.organigramme.ReponsesOrganigrammeInjectionActionServiceImpl.EXPORT_GOUVERNEMENT_FILE_NAME;

import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.jaxrs.webobject.ajax.SSOrganigrammeAjax;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.utils.FileDownloadUtils;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammeAjax")
public class ReponsesOrganigrammeAjax extends SSOrganigrammeAjax {

    public ReponsesOrganigrammeAjax() {
        super();
    }

    @Produces("application/vnd.ms-excel")
    @GET
    @Path("/export")
    public Response exportGouvernement() {
        SSUIServiceLocator.getSSOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(SSActionEnum.EXPORT_GOUVERNEMENT, "/admin/organigramme/export");
        ReponsesActionsServiceLocator
            .getReponsesOrganigrammeInjectionActionService()
            .exportGouvernement(context.getSession());
        return FileDownloadUtils.getResponse(FileUtils.getAppTmpFolder(), EXPORT_GOUVERNEMENT_FILE_NAME);
    }

    @Path("importer")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmImportGouvernement() {
        SSUIServiceLocator.getSSOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(SSActionEnum.IMPORT_GOUVERNEMENT, "/admin/organigramme/importer");
        ReponsesActionsServiceLocator.getReponsesOrganigrammeInjectionActionService().executeInjection(context);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
