package fr.dila.reponses.ui.jaxrs.webobject.page.recherche;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_DIVERS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_EDITION;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_FDR;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_RENVOIS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.RECHERCHE_DOSSIER_ACTIONS_GENERAL;

import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.SearchForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliRechercheRapideResultats")
public class ReponsesRechercheRapideResults extends SolonWebObject {

    public ReponsesRechercheRapideResults() {
        super();
    }

    @POST
    public Object getPostResults() {
        return doRecherche();
    }

    @GET
    public Object getGetResults() {
        return doRecherche();
    }

    protected Object doRecherche() {
        template.setContext(context);

        RepDossierList lstResults = new RepDossierList();

        String nor = UserSessionHelper.getUserSessionParameter(context, SSUserSessionKey.NOR);
        DossierListForm dossierlistForm = UserSessionHelper.getUserSessionParameter(
            context,
            SSUserSessionKey.SEARCH_RESULT_FORM
        );
        dossierlistForm = ObjectHelper.requireNonNullElseGet(dossierlistForm, DossierListForm::newForm);

        if (StringUtils.isNotEmpty(nor)) {
            lstResults =
                ReponsesUIServiceLocator
                    .getRechercheUIService()
                    .getDossiersByOrigineNumero(nor, dossierlistForm, context.getSession());

            if (lstResults.getNbTotal() == 1) {
                String dossierId = lstResults.getListe().get(0).getDossierId();
                final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
                List<DocumentModel> dossiersLinks = corbeilleService.findUpdatableDossierLinkForDossiers(
                    context.getSession(),
                    Collections.singletonList(dossierId)
                );
                // On redirige vers le dossier trouvé
                if (dossiersLinks.isEmpty()) {
                    // Pas de dossierLink actif : recherche sans action attendue
                    return redirect("dossier/" + dossierId + "/parapheur");
                } else if (dossiersLinks.size() == 1) {
                    UserSessionHelper.putUserSessionParameter(context, SpecificContext.LAST_TEMPLATE, ThTemplate.class);
                    // Un seul dossierLink possible -> on le sélectionne
                    String dossierLinkId = dossiersLinks.get(0).getId();
                    return redirect("dossier/" + dossierId + "/parapheur?dossierLinkId=" + dossierLinkId);
                }
            }
        }

        dossierlistForm.setColumnVisibility(lstResults.getListeColonnes());

        Map<String, Object> map = new HashMap<>();
        map.put("myForm", new SearchForm()); // rajouté pour éviter source is null for getProperty(null, "legislature")
        map.put(STTemplateConstants.RESULT_FORM, dossierlistForm);

        context.putInContextData(STContextDataKey.IS_ACTION_MASS, true);

        map.put(STTemplateConstants.RESULT_LIST, lstResults);
        map.put(STTemplateConstants.TITRE, lstResults.getTitre());
        map.put(STTemplateConstants.SOUS_TITRE, lstResults.getSousTitre());
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.LST_SORTED_COLONNES, lstResults.getListeSortedColonnes());
        map.put(STTemplateConstants.LST_SORTABLE_COLONNES, lstResults.getListeSortableAndVisibleColonnes());
        map.put(STTemplateConstants.DISPLAY_TABLE, !lstResults.getListe().isEmpty());
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(RECHERCHE_DOSSIER_ACTIONS_GENERAL));
        map.put(SSTemplateConstants.FDR_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_FDR));
        map.put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_EDITION));
        map.put(ReponsesTemplateConstants.RENVOI_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_RENVOIS));
        map.put(SSTemplateConstants.DIVERS_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_DIVERS));
        map.put(STTemplateConstants.DATA_URL, "/recherche/rapide");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/recherche/rapide");

        template.setData(map);

        return template;
    }
}
