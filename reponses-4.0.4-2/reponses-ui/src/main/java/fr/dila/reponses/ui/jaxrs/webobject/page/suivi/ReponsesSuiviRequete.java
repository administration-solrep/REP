package fr.dila.reponses.ui.jaxrs.webobject.page.suivi;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_DIVERS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_EDITION;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_FDR;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_RENVOIS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.RECHERCHE_DOSSIER_ACTIONS_GENERAL;
import static fr.dila.st.ui.enums.STContextDataKey.ID;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.RequeteUIService;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ReponsesSuiviRequete")
public class ReponsesSuiviRequete extends SolonWebObject {

    public ReponsesSuiviRequete() {
        super();
    }

    @GET
    public ThTemplate getResultsFromRequete(
        @SwBeanParam DossierListForm form,
        @QueryParam("idRequete") String idRequete
    ) {
        // Je déclare mon template et j'instancie mon context
        context.putInContextData(ID, idRequete);
        context.putInContextData(ReponsesContextDataKey.DOSSIER_LIST_FORM, form);

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();

        form = ObjectHelper.requireNonNullElseGet(form, DossierListForm::newForm);

        RequeteUIService service = ReponsesUIServiceLocator.getRequeteUIService();
        RepDossierList lstResults = service.getDossiersByRequeteExperte(context);

        map.put(STTemplateConstants.RESULT_LIST, lstResults);

        Map<String, Object> otherParameter = new HashMap<>();
        otherParameter.put("idRequete", idRequete);
        map.put(STTemplateConstants.OTHER_PARAMETER, otherParameter);
        map.put("isRequeteGenerale", service.isRequeteGenerale(context.getSession(), idRequete));

        context.putInContextData(STContextDataKey.IS_ACTION_MASS, true);

        form.setColumnVisibility(lstResults.getListeColonnes());
        map.put(STTemplateConstants.RESULT_FORM, form);
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.LST_SORTED_COLONNES, lstResults.getListeSortedColonnes());
        map.put(STTemplateConstants.LST_SORTABLE_COLONNES, lstResults.getListeSortableAndVisibleColonnes());
        map.put(STTemplateConstants.TITRE, lstResults.getTitre());
        map.put(STTemplateConstants.SOUS_TITRE, lstResults.getSousTitre());
        map.put(STTemplateConstants.DISPLAY_TABLE, lstResults.getNbTotal() > 0);
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(RECHERCHE_DOSSIER_ACTIONS_GENERAL));
        map.put(SSTemplateConstants.FDR_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_FDR));
        map.put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_EDITION));
        map.put(ReponsesTemplateConstants.RENVOI_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_RENVOIS));
        map.put(SSTemplateConstants.DIVERS_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_DIVERS));
        map.put(STTemplateConstants.DATA_URL, "/suivi/requete");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/suivi/requete");

        context.setNavigationContextTitle(
            new Breadcrumb(lstResults.getTitre(), "/suivi/requete?idRequete=" + idRequete, Breadcrumb.TITLE_ORDER)
        );
        template.setContext(context);
        template.setData(map);

        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/recherche/result-list", getMyContext());
    }
}
