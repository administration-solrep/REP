package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.organigramme.ReponsesMigrationManagerUIService;
import fr.dila.reponses.ui.th.bean.MisesAJourTimbresFormDTO;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "MiseAJourTimbresAjax")
public class ReponsesMiseAJourTimbresAjax extends SolonWebObject {
    public static final int MAJ_TIMBRES_ORDER = Breadcrumb.TITLE_ORDER;
    public static final String NAVIGATION_RECAPITULATIF_TITLE = "majtimbres.recapitulatif.page.title";
    public static final String DATA_RECAPITULATIF_URL = "/admin/timbres/recapitulatif#main_content";
    public static final String DATA_HISTORIQUE_URL = "/admin/timbres/historique#main_content";
    public static final String PARAMETRE_TIMBRE_KEY = "parametresTimbres";

    public ReponsesMiseAJourTimbresAjax() {
        super();
    }

    @POST
    public ThTemplate getRecap(@FormParam("id") String id) {
        ThTemplate template = new ReponsesAdminTemplate();
        template.setData(new HashMap<>());
        template.setContext(context);
        template.setName("pages/admin/majTimbres/recapitulatif");
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_RECAPITULATIF_TITLE,
                DATA_RECAPITULATIF_URL,
                MAJ_TIMBRES_ORDER + 2,
                context.getWebcontext().getRequest()
            )
        );

        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        return template;
    }

    @POST
    @Path("parametre")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFormTimbres(@SwBeanParam MisesAJourTimbresFormDTO parametres) {
        UserSessionHelper.putUserSessionParameter(context, PARAMETRE_TIMBRE_KEY, parametres);
        final ReponsesMigrationManagerUIService service = ReponsesUIServiceLocator.getReponsesMigrationManagerUIService();
        service.saveParametrageTimbres(parametres);
        service.checkBeforeRecapitulatif(context);
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), DATA_RECAPITULATIF_URL).build();
    }

    @POST
    @Path("maj")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doMaj() {
        ReponsesUIServiceLocator.getReponsesMigrationManagerUIService().updateTimbre(context);
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), DATA_HISTORIQUE_URL).build();
    }
}
