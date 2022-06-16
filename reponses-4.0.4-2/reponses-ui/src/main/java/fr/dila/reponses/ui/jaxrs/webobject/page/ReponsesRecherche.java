package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_DIVERS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_EDITION;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_FDR;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_RENVOIS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.RECHERCHE_DOSSIER_ACTIONS_GENERAL;
import static fr.dila.reponses.ui.jaxrs.webobject.ajax.ReponsesRechercheAjax.SEARCH_FORMS_KEY;

import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.enumeration.IndexModeEnum;
import fr.dila.reponses.api.enumeration.StatutReponseEnum;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.helper.RechercheHelper;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.SearchForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.reponses.ui.th.model.ReponsesRechercheTemplate;
import fr.dila.ss.ui.jaxrs.webobject.page.SSRecherche;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.validators.annot.SwRegex;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliRecherche")
public class ReponsesRecherche extends SSRecherche {
    private static final Map<String, String> STATUS_REPONSE = ResourceHelper.getStrings(
        StatutReponseEnum.getLabelKeys()
    );

    private static final Map<String, String> ORIGINE_QUESTION = ImmutableMap.of(
        DossierConstants.ORIGINE_QUESTION_AN,
        "Assemblée nationale",
        DossierConstants.ORIGINE_QUESTION_SENAT,
        "Sénat"
    );

    private static final Map<String, String> ETAT_QUESTION = ImmutableMap
        .<String, String>builder()
        .put(VocabularyConstants.ETAT_QUESTION_RETIREE, ResourceHelper.getString("recherche.etat.question.retiree"))
        .put(VocabularyConstants.ETAT_QUESTION_CADUQUE, ResourceHelper.getString("recherche.etat.question.caduque"))
        .put(
            VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE,
            ResourceHelper.getString("recherche.etat.question.cloture.autre")
        )
        .put(
            VocabularyConstants.ETAT_QUESTION_RENOUVELEE,
            ResourceHelper.getString("recherche.etat.question.renouvelee")
        )
        .put(VocabularyConstants.ETAT_QUESTION_SIGNALEE, ResourceHelper.getString("recherche.etat.question.signalee"))
        .put(VocabularyConstants.ETAT_QUESTION_RAPPELE, ResourceHelper.getString("recherche.etat.question.rappele"))
        .put(
            VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE,
            ResourceHelper.getString("recherche.etat.question.reattribuee")
        )
        .build();

    private static final Map<String, String> RECHERCHE_EXACTE = ImmutableMap.of(
        RequeteConstants.APPLIQUER_RECHERCHE_EXACTE,
        "Recherche exacte"
    );

    private static final Map<String, String> RECHERCHE_DANS = ImmutableMap.of(
        RequeteConstants.DANS_TEXTE_QUESTION,
        "Le texte de la question",
        RequeteConstants.DANS_TEXTE_REPONSE,
        "Le texte de la réponse",
        RequeteConstants.DANS_TITRE,
        "Le titre"
    );

    private static final Map<String, String> RECHERCHE_SUR = ResourceHelper.getStrings(IndexModeEnum.getLabelKeys());

    public ReponsesRecherche() {
        super();
    }

    @Path("rechercher")
    public Object getRecherche() {
        //Je déclare mon template et j'instancie mon context
        ThTemplate template = getMyTemplate();
        template.setName("pages/recherche");
        context.removeNavigationContextTitle();
        template.setContext(context);

        Map<String, Object> sessionSearchForms = UserSessionHelper.getUserSessionParameter(context, SEARCH_FORMS_KEY);

        //Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(
            "rechercheDto",
            ReponsesUIServiceLocator
                .getRechercheUIService()
                .initRechercheDTO(RechercheHelper.convertTo(sessionSearchForms))
        );
        map.put("statusReponse", STATUS_REPONSE);
        map.put("origineQuestion", ORIGINE_QUESTION);
        map.put("etatQuestion", ETAT_QUESTION);
        map.put("rechercheExacte", RECHERCHE_EXACTE);
        map.put("rechercheDans", RECHERCHE_DANS);
        map.put("rechercheSur", RECHERCHE_SUR);
        template.setData(map);

        return newObject("AppliRechercheResultats", context, template);
    }

    @POST
    @Path("resultats")
    public ThTemplate doRecherche(@SwBeanParam SearchForm form, RepDossierList lstResults) {
        // Je déclare mon template et j'instancie mon context
        ThTemplate template = getMyTemplate();
        template.setName("pages/results");
        template.setContext(context);

        DossierListForm resultForm = DossierListForm.newForm();

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();

        map.put("myForm", form);

        context.putInContextData(STContextDataKey.IS_ACTION_MASS, true);

        resultForm.setColumnVisibility(lstResults.getListeColonnes());
        map.put(STTemplateConstants.RESULT_FORM, resultForm);

        map.put(STTemplateConstants.RESULT_LIST, lstResults);
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(RECHERCHE_DOSSIER_ACTIONS_GENERAL));
        map.put(SSTemplateConstants.FDR_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_FDR));
        map.put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_EDITION));
        map.put(ReponsesTemplateConstants.RENVOI_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_RENVOIS));
        map.put(SSTemplateConstants.DIVERS_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_DIVERS));
        map.put(STTemplateConstants.DATA_URL, "/recherche/resultats");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/recherche/resultats");

        template.setData(map);
        return template;
    }

    @Path("rapide")
    public Object doRechercheRapide(@QueryParam("question") @SwRegex(NUM_QUESTION_KEY_REGEX) String question) {
        ThTemplate template = buildTemplateRapideSearch(question);

        return newObject("AppliRechercheRapideResultats", context, template);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesRechercheTemplate();
    }
}
