package fr.dila.reponses.ui.jaxrs.webobject.page;

import fr.dila.reponses.ui.bean.AllotissementConfigDTO;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.AllotissementActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.th.model.ReponsesLayoutThTemplate;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliAllotissement")
public class ReponsesAllotissement extends SolonWebObject {
    private static final String PARAM_LIST_QUESTIONS_ERROR_SEARCH = "listQuestionsErrorSearch";
    private static final String PARAM_LIST_QUESTIONS_AVEC_LOT_SEARCH = "listQuestionsAvecLotSearch";
    private static final String PARAM_LIST_QUESTIONS_SEARCH = "listQuestionsSearch";
    private static final int LIST_MODELE_ORDER = Breadcrumb.SUBTITLE_ORDER + 1;

    protected AllotissementActionService getAllotissementActionService() {
        return ReponsesActionsServiceLocator.getAllotissementActionService();
    }

    public ReponsesAllotissement() {
        super();
    }

    @GET
    public ThTemplate getHome(@QueryParam("idDossiers") List<String> selectedFolders) {
        //Je déclare mon template et j'instancie mon context
        ThTemplate template = getMyTemplate(context);

        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        context.setNavigationContextTitle(new Breadcrumb("Création de lot", "/allotissement", LIST_MODELE_ORDER));

        context.putInContextData(ReponsesContextDataKey.SELECTED_DOSSIERS, selectedFolders);

        AllotissementConfigDTO allotissementConfigDTO = getAllotissementActionService()
            .getListQuestionsAllotisSearch(context);
        template.getData().put(PARAM_LIST_QUESTIONS_ERROR_SEARCH, allotissementConfigDTO.getListQuestionsErrorSearch());
        template
            .getData()
            .put(PARAM_LIST_QUESTIONS_AVEC_LOT_SEARCH, allotissementConfigDTO.getListQuestionsAvecLotSearch());
        template.getData().put(PARAM_LIST_QUESTIONS_SEARCH, allotissementConfigDTO.getListQuestionsSearch());
        template.getData().put(STTemplateConstants.LST_COLONNES, allotissementConfigDTO.getLstColonnes());
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        return template;
    }

    protected ThTemplate getMyTemplate(SpecificContext context) {
        return new ReponsesLayoutThTemplate("pages/allotissementListQuestions", context);
    }
}
