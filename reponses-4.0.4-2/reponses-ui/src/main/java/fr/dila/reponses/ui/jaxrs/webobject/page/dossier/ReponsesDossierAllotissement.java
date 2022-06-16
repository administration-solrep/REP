package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.ui.bean.AllotissementListDTO;
import fr.dila.reponses.ui.enums.ReponsesActionCategory;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliDossierAllotissement")
public class ReponsesDossierAllotissement extends SolonWebObject {

    public ReponsesDossierAllotissement() {
        super();
    }

    @GET
    public ThTemplate getAllotissement() {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        Dossier dossier = context.getCurrentDocument().getAdapter(Dossier.class);
        AllotissementListDTO allotissementListDTO = ReponsesActionsServiceLocator
            .getAllotissementActionService()
            .getListQuestionsAllotis(context);

        template.getData().put(STTemplateConstants.LST_COLONNES, allotissementListDTO.getLstColonnes());
        template.getData().put(STTemplateConstants.RESULT_LIST, allotissementListDTO.getListQuestionsAlloties());
        template.getData().put("dossierDirecteur", allotissementListDTO.getNomDossierDirecteur());

        template.getData().put("tabActions", context.getActions(ReponsesActionCategory.ALLOTISSEMENT_ACTIONS));
        template.getData().put("questionNumber", dossier.getQuestion(context.getSession()).getSourceNumeroQuestion());

        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAllotissement() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), null).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/dossier/onglets/allotissement", getMyContext());
    }
}
