package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.bean.PlanClassementDTO;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.PlanClassementComponentService;
import fr.dila.reponses.ui.services.ReponsesMailboxListComponentService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.impl.PlanClassementComponentServiceImpl;
import fr.dila.ss.ui.jaxrs.webobject.ajax.SSCorbeilleAjax;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "CorbeilleAjax")
public class ReponsesCorbeille extends SSCorbeilleAjax {

    public ReponsesCorbeille() {
        super();
    }

    @GET
    @Path("plan")
    public ThTemplate getPlan(@QueryParam("type") String type, @QueryParam("key") String key) {
        // Je déclare mon template et j'instancie mon context
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/components/planClassementArbre");
        SpecificContext context = getMyContext();
        Map<String, Object> mapContext = new HashMap<>();
        if (StringUtils.isNotBlank(type)) {
            mapContext.put(PlanClassementComponentServiceImpl.ASSEMBLEE_KEY, type);
        }
        if (StringUtils.isNotBlank(key)) {
            mapContext.put(PlanClassementComponentServiceImpl.SELECTED_KEY, key);
        }
        context.setContextData(mapContext);
        template.setContext(context);

        PlanClassementComponentService service = ReponsesUIServiceLocator.getPlanClassementComponentService();
        Map<String, Object> serviceMap = service.getData(context);
        PlanClassementDTO dto = (PlanClassementDTO) serviceMap.get("planClassementMap");

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.TREE_LIST, dto.getChilds());
        map.put(STTemplateConstants.LEVEL, 1);
        map.put(SSTemplateConstants.MY_ID, "");
        map.put(SSTemplateConstants.TOGGLER_ID, "");
        map.put(STTemplateConstants.IS_OPEN, true);
        map.put(STTemplateConstants.ACTION, context.getAction(ReponsesActionEnum.ADD_FAVORIS_PC));
        map.put(STTemplateConstants.TITLE, "plan de classement");
        map.put(
            PlanClassementComponentServiceImpl.ACTIVE_KEY,
            serviceMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY)
        );
        template.setData(map);
        return template;
    }

    @Override
    protected ReponsesMailboxListComponentService getMyService() {
        return ReponsesUIServiceLocator.getReponsesMailboxListComponentService();
    }
}
