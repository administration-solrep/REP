package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.jaxrs.webobject.ajax.etape.SSEtapeAjax;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxEtape")
public class ReponsesEtapeAjax extends SSEtapeAjax {

    public ReponsesEtapeAjax() {
        super();
    }

    @Path("saveFirstEtape")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveFisrtEtapeModele(@SwBeanParam CreationEtapeDTO creationEtapeDTO) {
        context.putInContextData(SSContextDataKey.CREATION_ETAPE_DTO, creationEtapeDTO);
        ReponsesUIServiceLocator.getReponsesFeuilleRouteUIService().addFirstEtapesModele(context);

        // Comme je recharge la page si pas d'erreur je met en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected List<SelectValueDTO> getTypeEtapeAjout() {
        return ReponsesUIServiceLocator.getSelectValueUIService().getRoutingTaskTypesFiltered();
    }
}
