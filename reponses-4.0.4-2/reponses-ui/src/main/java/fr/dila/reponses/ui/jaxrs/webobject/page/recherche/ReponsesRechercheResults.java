package fr.dila.reponses.ui.jaxrs.webobject.page.recherche;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_DIVERS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_EDITION;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_FDR;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_RENVOIS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.RECHERCHE_DOSSIER_ACTIONS_GENERAL;
import static fr.dila.reponses.ui.enums.ReponsesActionEnum.RECHERCHE_DOSSIER_EXPORT;
import static fr.dila.reponses.ui.jaxrs.webobject.ajax.ReponsesRechercheAjax.SEARCH_FORMS_KEY;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.helper.RechercheHelper;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliRechercheResultats")
public class ReponsesRechercheResults extends SolonWebObject {

    public ReponsesRechercheResults() {
        super();
    }

    @GET
    public ThTemplate getResults() {
        if (UserSessionHelper.getUserSessionParameter(context, SEARCH_FORMS_KEY, Map.class) != null) {
            return doRecherche();
        } else {
            return template;
        }
    }

    @POST
    public ThTemplate getResultsFromAjax() {
        return doRecherche();
    }

    private ThTemplate doRecherche() {
        // Je déclare mon template et j'instancie mon context
        template.setContext(context);

        @SuppressWarnings("unchecked")
        Map<String, Object> sessionSearchForms = UserSessionHelper.getUserSessionParameter(
            context,
            SEARCH_FORMS_KEY,
            Map.class
        );

        DossierListForm listForm = (DossierListForm) sessionSearchForms.get("results");
        listForm = ObjectHelper.requireNonNullElseGet(listForm, DossierListForm::newForm);

        RepDossierList lstResults = ReponsesUIServiceLocator
            .getRechercheUIService()
            .getDossiersForRechercheAvancee(
                context.getSession(),
                RechercheHelper.convertTo(sessionSearchForms),
                listForm
            );

        listForm.setColumnVisibility(lstResults.getListeColonnes());

        Map<String, Object> map = new HashMap<>();
        if (template.getData() != null) {
            map = template.getData();
        }

        context.putInContextData(STContextDataKey.IS_ACTION_MASS, true);

        map.put(STTemplateConstants.RESULT_FORM, listForm);
        map.put(STTemplateConstants.RESULT_LIST, lstResults);
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.LST_SORTED_COLONNES, lstResults.getListeSortedColonnes());
        map.put(STTemplateConstants.LST_SORTABLE_COLONNES, lstResults.getListeSortableAndVisibleColonnes());
        map.put(STTemplateConstants.DISPLAY_TABLE, lstResults.getNbTotal() > 0);
        map.put(STTemplateConstants.DATA_URL, "/recherche/rechercher");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/recherche/resultats");
        map.put(STTemplateConstants.TITRE, lstResults.getTitre());
        map.put(STTemplateConstants.SOUS_TITRE, lstResults.getSousTitre());
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(RECHERCHE_DOSSIER_ACTIONS_GENERAL));
        map.put(SSTemplateConstants.FDR_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_FDR));
        map.put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_EDITION));
        map.put(SSTemplateConstants.DIVERS_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_DIVERS));
        map.put(ReponsesTemplateConstants.RENVOI_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_RENVOIS));
        map.put(ReponsesTemplateConstants.EXPORT_ACTION, context.getAction(RECHERCHE_DOSSIER_EXPORT));

        template.setData(map);
        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/recherche/result-list", getMyContext());
    }

    @Override
    protected SpecificContext getMyContext() {
        return new SpecificContext();
    }
}
