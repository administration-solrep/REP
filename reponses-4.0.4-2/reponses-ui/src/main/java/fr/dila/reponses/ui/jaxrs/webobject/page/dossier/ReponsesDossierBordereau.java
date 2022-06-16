package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import static fr.dila.ss.ui.enums.SSActionCategory.DOSSIER_TAB_ACTIONS;

import fr.dila.reponses.ui.bean.DossierSaveForm;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliDossierBordereau")
public class ReponsesDossierBordereau extends SolonWebObject {

    public ReponsesDossierBordereau() {
        super();
    }

    @GET
    public ThTemplate getBordereau() {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        template.getData().put("bordereauDto", ReponsesUIServiceLocator.getBordereauUIService().getBordereau(context));

        template.getData().put("tabActions", context.getActions(DOSSIER_TAB_ACTIONS));

        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Object saveBordereau(@SwBeanParam DossierSaveForm data) {
        ReponsesActionsServiceLocator.getDossierActionService().saveIndexationComplementaire(context, data);

        //Comme je recharge la page si pas d'erreur je met en session les messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), null).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/dossier/onglets/bordereau", getMyContext());
    }
}
