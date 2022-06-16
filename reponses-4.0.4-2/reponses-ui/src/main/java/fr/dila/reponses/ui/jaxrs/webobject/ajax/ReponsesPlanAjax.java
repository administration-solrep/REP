package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_DIVERS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_EDITION;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_FDR;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_RENVOIS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.PLAN_CLASSEMENT_DOSSIERS_ACTIONS_GENERAL;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.reponses.api.service.FavorisIndexationService;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.DossierListUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.cli.MissingArgumentException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ReponsesPlanAjax")
public class ReponsesPlanAjax extends SolonWebObject {

    public ReponsesPlanAjax() {
        super();
    }

    @GET
    @Path("/liste")
    public ThTemplate getResults(
        @QueryParam("cle") String cle,
        @QueryParam("cleParent") String cleParent,
        @QueryParam("origine") String origine,
        @SwBeanParam DossierListForm form
    )
        throws MissingArgumentException {
        Map<String, Object> otherParameter = new HashMap<>();
        // Je déclare mon template et j'instancie mon context
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/table/tableDossiers");
        SpecificContext context = getMyContext();
        template.setContext(context);

        context.getContextData().put("origine", origine);
        context.getContextData().put("cle", cle);
        context.getContextData().put("cleParent", cleParent);
        context.getContextData().put("form", form);

        context.setNavigationContextTitle(
            new Breadcrumb(
                String.format("Mots-clés \"%s\" et \"%s\"", cleParent, cle),
                "/classement/liste",
                Breadcrumb.TITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();

        form = ObjectHelper.requireNonNullElseGet(form, DossierListForm::newForm);

        DossierListUIService service = ReponsesUIServiceLocator.getDossierListUIService();
        RepDossierList lstResults = service.getDossiersFromPlanClassement(context);

        map.put(STTemplateConstants.RESULT_LIST, lstResults);
        form.setColumnVisibility(lstResults.getListeColonnes());
        map.put(STTemplateConstants.RESULT_FORM, form);

        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.LST_SORTED_COLONNES, lstResults.getListeSortedColonnes());
        map.put(STTemplateConstants.LST_SORTABLE_COLONNES, lstResults.getListeSortableAndVisibleColonnes());
        map.put(STTemplateConstants.DATA_URL, "/classement/liste");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/classement/liste");

        otherParameter.put("origine", origine);
        otherParameter.put("cle", cle);
        otherParameter.put("cleParent", cleParent);
        map.put(STTemplateConstants.OTHER_PARAMETER, otherParameter);
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(PLAN_CLASSEMENT_DOSSIERS_ACTIONS_GENERAL));
        map.put(SSTemplateConstants.FDR_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_FDR));
        map.put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_EDITION));
        map.put(ReponsesTemplateConstants.RENVOI_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_RENVOIS));
        map.put(SSTemplateConstants.DIVERS_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_DIVERS));

        template.setData(map);

        return template;
    }

    @GET
    @Path("/addFavoris")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFavoris(
        @QueryParam("origine") String origine,
        @QueryParam("cle") String cle,
        @QueryParam("cleParent") String cleParent
    ) {
        getRequiredService(FavorisIndexationService.class).addFavoris(context.getSession(), origine, cle, cleParent);
        context.getMessageQueue().addToastSuccess(getString("favoris.plan.classement.add.success"));
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @GET
    @Path("/removeFavoris")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeFavoris(@QueryParam("key") String docId) {
        context.getSession().removeDocument(new IdRef(docId));
        context.getMessageQueue().addToastSuccess(getString("favoris.plan.classement.remove.success"));
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected SpecificContext getMyContext() {
        return new SpecificContext();
    }
}
