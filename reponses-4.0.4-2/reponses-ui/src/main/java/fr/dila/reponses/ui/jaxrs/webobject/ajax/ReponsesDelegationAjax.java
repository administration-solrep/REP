package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.bean.DelegationsDonneesList;
import fr.dila.reponses.ui.bean.DelegationsRecuesList;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DelegationForm;
import fr.dila.reponses.ui.th.bean.DelegationsDonneesListForm;
import fr.dila.reponses.ui.th.bean.DelegationsRecuesListForm;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.pages.user.AbstractUserObject;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "DelegationAjax")
public class ReponsesDelegationAjax extends AbstractUserObject {

    @GET
    public ThTemplate getHome(
        @SwBeanParam DelegationsRecuesListForm delegationsRecuesListForm,
        @SwBeanParam DelegationsDonneesListForm delegationsDonneesListForm
    ) {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }
        context.putInContextData(ReponsesContextDataKey.DELEGATIONS_RECUES, delegationsRecuesListForm);
        context.putInContextData(ReponsesContextDataKey.DELEGATIONS_DONNEES, delegationsDonneesListForm);

        template.setContext(context);

        DelegationsRecuesList delegationsRecues = ReponsesUIServiceLocator
            .getDelegationUIService()
            .fetchDelegationsRecues(context);
        DelegationsDonneesList delegationsDonnees = ReponsesUIServiceLocator
            .getDelegationUIService()
            .fetchDelegationsDonnees(context);

        template.getData().put("resultListRecues", delegationsRecues);
        template.getData().put("resultListDonnees", delegationsDonnees);
        template.getData().put("lstColonnesRecues", delegationsRecues.getListeColonnes());
        template.getData().put("lstColonnesDonnees", delegationsDonnees.getListeColonnes());
        template.getData().put("dataUrlRecues", "/admin/delegation/recues");
        template.getData().put("dataAjaxUrlRecues", "/ajax/delegation/recues");
        template.getData().put("dataUrlDonnees", "/admin/delegation/donnees");
        template.getData().put("dataAjaxUrlDonnees", "/ajax/delegation/donnees");
        template.getData().put("nbDelegationsRecues", delegationsRecues.getListe().size());
        template.getData().put("nbDelegationsDonnees", delegationsDonnees.getListe().size());
        template.getData().put("resultFormRecues", delegationsRecuesListForm);
        template.getData().put("resultFormDonnees", delegationsDonneesListForm);

        return template;
    }

    @GET
    @Path("recues")
    public ThTemplate getRecues(@SwBeanParam DelegationsRecuesListForm delegationsRecuesListForm) {
        context.putInContextData(ReponsesContextDataKey.DELEGATIONS_RECUES, delegationsRecuesListForm);
        ThTemplate template = new AjaxLayoutThTemplate("fragments/table/tableDelegationsRecues", context);
        template.setData(new HashMap<>());

        DelegationsRecuesList delegationsRecues = ReponsesUIServiceLocator
            .getDelegationUIService()
            .fetchDelegationsRecues(context);

        template.getData().put("resultListRecues", delegationsRecues);
        template.getData().put("lstColonnesRecues", delegationsRecues.getListeColonnes());
        template.getData().put("resultFormRecues", delegationsRecuesListForm);
        template.getData().put("dataUrlRecues", "/admin/delegation/recues");
        template.getData().put("dataAjaxUrlRecues", "/ajax/delegation/recues");

        return template;
    }

    @GET
    @Path("donnees")
    public ThTemplate getDonnees(@SwBeanParam DelegationsDonneesListForm delegationsDonneesListForm) {
        context.putInContextData(ReponsesContextDataKey.DELEGATIONS_DONNEES, delegationsDonneesListForm);

        ThTemplate template = new AjaxLayoutThTemplate("fragments/table/tableDelegationsDonnees", context);
        template.setData(new HashMap<>());

        DelegationsDonneesList delegationsDonnees = ReponsesUIServiceLocator
            .getDelegationUIService()
            .fetchDelegationsDonnees(context);

        template.getData().put("resultListDonnees", delegationsDonnees);
        template.getData().put("lstColonnesDonnees", delegationsDonnees.getListeColonnes());
        template.getData().put("resultFormDonnees", delegationsDonneesListForm);
        template.getData().put("dataUrlDonnees", "/admin/delegation/donnees");
        template.getData().put("dataAjaxUrlDonnees", "/ajax/delegation/donnees");
        return template;
    }

    @POST
    @Path("creation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDelegation(@SwBeanParam DelegationForm delegationForm) {
        context.putInContextData(ReponsesContextDataKey.DELEGATION_FORM, delegationForm);

        ReponsesUIServiceLocator.getDelegationUIService().createDelegation(context);

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString("reponses.delegation.created"));
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("modification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDelegation(@SwBeanParam DelegationForm delegationForm) {
        context.putInContextData(ReponsesContextDataKey.DELEGATION_FORM, delegationForm);

        ReponsesUIServiceLocator.getDelegationUIService().updateDelegation(context);

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString("reponses.delegation.updated"));
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("suppression")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDelegation(@FormParam("id") String id) {
        context.putInContextData(STContextDataKey.ID, id);

        ReponsesUIServiceLocator.getDelegationUIService().deleteDelegation(context);

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString("reponses.delegation.deleted"));
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
