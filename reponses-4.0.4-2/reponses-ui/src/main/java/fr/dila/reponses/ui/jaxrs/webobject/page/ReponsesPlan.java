package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_DIVERS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_EDITION;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_FDR;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_RENVOIS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.PLAN_CLASSEMENT_DOSSIERS_ACTIONS_GENERAL;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.impl.PlanClassementComponentServiceImpl;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.reponses.ui.th.model.ReponsesPlanClassementTemplate;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.apache.commons.cli.MissingArgumentException;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliClassement")
public class ReponsesPlan extends SolonWebObject {

    public ReponsesPlan() {
        super();
    }

    @GET
    public ThTemplate getHome() {
        ThTemplate template = getMyTemplate();
        template.setName("pages/planClassement");
        context.removeNavigationContextTitle();
        Map<String, Object> map = new HashMap<>();
        // Sur le home on désactive la clé sélectionnée
        map.put(PlanClassementComponentServiceImpl.ACTIVE_KEY, "");
        context.setContextData(map);
        template.setContext(context);

        return template;
    }

    @GET
    @Path("liste")
    public ThTemplate getListe(
        @QueryParam("cle") String cle,
        @QueryParam("cleParent") String cleParent,
        @QueryParam("origine") String origine,
        @SwBeanParam DossierListForm resultform
    )
        throws MissingArgumentException {
        ThTemplate template = getMyTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                String.format("Mots-clés \"%s\" et \"%s\"", cleParent, cle),
                "/classement/liste",
                Breadcrumb.TITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        // Map du context global (service de chargement du layout)
        Map<String, Object> contextMap = new HashMap<>();
        Map<String, Object> otherParameter = new HashMap<>();

        otherParameter.put("origine", origine);
        otherParameter.put("cle", cle);
        otherParameter.put("cleParent", cleParent);
        contextMap.put(PlanClassementComponentServiceImpl.ACTIVE_KEY, cleParent + "__" + cle);
        context.setContextData(contextMap);

        template.setName("pages/listeQuestionPlan");
        template.setContext(context);

        context.getContextData().put("origine", origine);
        context.getContextData().put("cle", cle);
        context.getContextData().put("cleParent", cleParent);
        context.getContextData().put("form", resultform);

        RepDossierList lstResults = ReponsesUIServiceLocator
            .getDossierListUIService()
            .getDossiersFromPlanClassement(context);

        resultform.setColumnVisibility(lstResults.getListeColonnes());

        // Map pour mon contenu spécifique
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, lstResults);
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.LST_SORTED_COLONNES, lstResults.getListeSortedColonnes());
        map.put(STTemplateConstants.LST_SORTABLE_COLONNES, lstResults.getListeSortableAndVisibleColonnes());
        map.put("nbDossier", lstResults.getNbTotal());
        map.put(STTemplateConstants.RESULT_FORM, resultform);
        map.put(STTemplateConstants.DATA_URL, "/classement/liste");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/classement/liste");
        map.put(STTemplateConstants.OTHER_PARAMETER, otherParameter);
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(PLAN_CLASSEMENT_DOSSIERS_ACTIONS_GENERAL));
        map.put(SSTemplateConstants.FDR_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_FDR));
        map.put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_EDITION));
        map.put(ReponsesTemplateConstants.RENVOI_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_RENVOIS));
        map.put(SSTemplateConstants.DIVERS_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_DIVERS));

        template.setData(map);

        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesPlanClassementTemplate();
    }
}
