package fr.dila.reponses.ui.jaxrs.webobject.page.admin.modele;

import fr.dila.reponses.ui.bean.IndexationDTO;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesModeleFdrFicheUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.IndexActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.th.bean.ReponsesModeleFdrForm;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.ss.ui.jaxrs.webobject.page.admin.modele.SSModeleFeuilleRoute;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ModeleFeuilleRoute")
public class ReponsesModeleFeuilleRoute extends SSModeleFeuilleRoute {

    public ReponsesModeleFeuilleRoute() {
        super();
    }

    @GET
    @Path("/modele/modification")
    public ThTemplate getModeleFdrModification(@QueryParam("id") String id) {
        template.setContext(context);
        context.setCurrentDocument(id);

        // si pas le droit de consulter le modele -> retour liste modele
        if (!ReponsesActionsServiceLocator.getReponsesModeleFeuilleRouteActionService().canUserReadRoute(context)) {
            return getRechercheModeleFdr();
        }

        ReponsesModeleFdrForm modeleForm = new ReponsesModeleFdrForm();
        ReponsesUIServiceLocator.getReponsesModeleFdrFicheUIService().getModeleFdrForm(context, modeleForm);

        setTemplateDataModeleModification(template, modeleForm);
        context.putInContextData(SSContextDataKey.MODELE_FORM, modeleForm);

        Map<String, Object> map = buildMapModeleModification(id, modeleForm);
        // Données spécifiques Reponses
        map.put(ADD_FIRST_STEP_ACTION_KEY, context.getAction(ReponsesActionEnum.ADD_FIRST_STEP_MODELE_ACTION));
        template.setData(map);
        return template;
    }

    @GET
    @Path("/modele/creation")
    public ThTemplate getModeleFdrCreation() {
        template.setName("pages/admin/modele/createModeleFDR");
        template.setContext(context);

        // si pas le droit de consulter le modele -> retour liste modele
        if (!ReponsesActionsServiceLocator.getReponsesModeleFeuilleRouteActionService().canUserCreateRoute(context)) {
            throw new STAuthorizationException(ResourceHelper.getString("admin.modele.message.error.right"));
        }

        ReponsesModeleFdrForm modeleForm = UserSessionHelper.getUserSessionParameter(
            context,
            SSUserSessionKey.MODELE_FORM
        );
        // si retour de la création après erreur -> récupérer le formulaire en session
        if (modeleForm != null) {
            if (modeleForm.getIndexationDTO() == null) {
                modeleForm.setIndexationDTO(new IndexationDTO());
            }
        } else {
            modeleForm = new ReponsesModeleFdrForm();
        }
        IndexActionService indexActionService = ReponsesActionsServiceLocator.getIndexActionService();
        indexActionService.initIndexationDtoDirectories(modeleForm.getIndexationDTO());

        setNavigationContextCreationModele();
        context.putInContextData(SSContextDataKey.MODELE_FORM, modeleForm);

        Map<String, Object> map = buildMapCreationModele(modeleForm);
        template.setData(map);

        return template;
    }

    @POST
    @Path("/modele/sauvegarde")
    @Produces("text/html;charset=UTF-8")
    public Object saveFormModele(@SwBeanParam ReponsesModeleFdrForm modeleForm) {
        ReponsesModeleFdrFicheUIService modeleFDRFicheUIService = ReponsesUIServiceLocator.getReponsesModeleFdrFicheUIService();
        String id = modeleForm.getId();
        boolean isCreation = false;

        if (StringUtils.isNotBlank(id)) {
            // Si modeleForm.id != null
            // update
            context.setCurrentDocument(id);
            context.putInContextData(ReponsesContextDataKey.MODELE_FDR_FORM, modeleForm);
            modeleFDRFicheUIService.updateModele(context, modeleForm);
        } else {
            // Sinon creation
            modeleFDRFicheUIService.createModele(context, modeleForm);
            isCreation = true;
        }
        return endSaveFormModele(modeleForm, isCreation);
    }

    @Override
    protected List<SelectValueDTO> getTypeEtapeAjout(String idModele) {
        return ReponsesUIServiceLocator.getSelectValueUIService().getRoutingTaskTypesFiltered();
    }

    @Override
    public ThTemplate getMyTemplate() {
        return new ReponsesAdminTemplate();
    }
}
