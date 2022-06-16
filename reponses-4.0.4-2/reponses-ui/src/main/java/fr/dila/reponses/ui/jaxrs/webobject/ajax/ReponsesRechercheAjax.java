package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.recherche.IndexationItem;
import fr.dila.reponses.core.recherche.IndexationProvider;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.recherche.BlocDatesForm;
import fr.dila.reponses.ui.th.bean.recherche.FeuilleRouteForm;
import fr.dila.reponses.ui.th.bean.recherche.MotsClesForm;
import fr.dila.reponses.ui.th.bean.recherche.RechercheGeneraleForm;
import fr.dila.reponses.ui.th.bean.recherche.TexteIntegralForm;
import fr.dila.ss.ui.jaxrs.webobject.ajax.SSRechercheAjax;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "RechercheAjax")
public class ReponsesRechercheAjax extends SSRechercheAjax {
    public static final String SEARCH_FORMS_KEY = "searchForms";

    public ReponsesRechercheAjax() {
        super();
        this.suggestionFunctions.put(
                VocabularyConstants.INDEXATION_ZONE_AN,
                input -> {
                    IndexationProvider indexationProviderAn = new IndexationProvider(
                        VocabularyConstants.INDEXATION_ZONE_AN
                    );
                    return indexationProviderAn
                        .getSuggestions(input)
                        .stream()
                        .map(this::getIndexSuggestion)
                        .collect(Collectors.toList());
                }
            );
        this.suggestionFunctions.put(
                VocabularyConstants.INDEXATION_ZONE_SENAT,
                input -> {
                    IndexationProvider indexationProviderSenat = new IndexationProvider(
                        VocabularyConstants.INDEXATION_ZONE_SENAT
                    );
                    return indexationProviderSenat
                        .getSuggestions(input)
                        .stream()
                        .map(this::getIndexSuggestion)
                        .collect(Collectors.toList());
                }
            );
        this.suggestionFunctions.put(
                VocabularyConstants.INDEXATION_ZONE_MINISTERE,
                input -> {
                    IndexationProvider indexationProviderMinistere = new IndexationProvider(
                        VocabularyConstants.INDEXATION_ZONE_MINISTERE
                    );
                    return indexationProviderMinistere
                        .getSuggestions(input)
                        .stream()
                        .map(this::getIndexSuggestion)
                        .collect(Collectors.toList());
                }
            );
    }

    @Path("/resultats")
    public Object getResults(
        @SwBeanParam RechercheGeneraleForm general,
        @SwBeanParam BlocDatesForm dates,
        @SwBeanParam FeuilleRouteForm feuilleRoute,
        @SwBeanParam TexteIntegralForm texteIntegral,
        @SwBeanParam MotsClesForm motsCles,
        @SwBeanParam DossierListForm results
    ) {
        // Je déclare mon template et j'instancie mon context
        SpecificContext context = getMyContext();
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/recherche/result-list");
        template.setContext(context);

        context.setNavigationContextTitle(
            new Breadcrumb(
                "Recherche",
                "/recherche/rechercher#focusResultat",
                Breadcrumb.TITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();

        if (general.getMinistereRattachement() != null) {
            general.setMapMinistereRattachement(
                Collections.singletonMap(
                    general.getMinistereRattachement(),
                    STServiceLocator
                        .getOrganigrammeService()
                        .getOrganigrammeNodeById(general.getMinistereRattachement(), OrganigrammeType.MINISTERE)
                        .getLabel()
                )
            );
        }

        if (general.getDirectionPilote() != null) {
            general.setMapDirectionPilote(
                Collections.singletonMap(
                    general.getDirectionPilote(),
                    STServiceLocator
                        .getOrganigrammeService()
                        .getOrganigrammeNodeById(general.getDirectionPilote(), OrganigrammeType.DIRECTION)
                        .getLabel()
                )
            );
        }

        if (feuilleRoute.getDirection() != null) {
            feuilleRoute.setMapDirection(
                Collections.singletonMap(
                    feuilleRoute.getDirection(),
                    STServiceLocator
                        .getOrganigrammeService()
                        .getOrganigrammeNodeById(feuilleRoute.getDirection(), OrganigrammeType.DIRECTION)
                        .getLabel()
                )
            );
        }

        if (feuilleRoute.getPoste() != null) {
            feuilleRoute.setMapPoste(
                Collections.singletonMap(
                    feuilleRoute.getPoste(),
                    STServiceLocator
                        .getOrganigrammeService()
                        .getOrganigrammeNodeById(feuilleRoute.getPoste(), OrganigrammeType.POSTE)
                        .getLabel()
                )
            );
        }

        map.put("general", general);
        map.put("dates", dates);
        map.put("feuilleRoute", feuilleRoute);
        map.put("texteIntegral", texteIntegral);
        map.put("motsCles", motsCles);
        map.put("results", results);
        map.put(STTemplateConstants.DATA_URL, "/recherche/rechercher");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/recherche/resultats");
        context.setContextData(map);

        UserSessionHelper.putUserSessionParameter(context, SEARCH_FORMS_KEY, map);

        return newObject("AppliRechercheResultats", context);
    }

    @Path("/rapide")
    public Object getRapideResults(@SwBeanParam DossierListForm form) {
        form = ObjectHelper.requireNonNullElseGet(form, DossierListForm::newForm);

        ThTemplate template = buildTemplateRapideSearch(form);
        return newObject("AppliRechercheRapideResultats", context, template);
    }

    @POST
    @Path("/reinit")
    public void reinitSearch() {
        UserSessionHelper.putUserSessionParameter(getMyContext(), SEARCH_FORMS_KEY, null);
    }

    @Path("demanderElimination")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response demanderElimination(@FormParam("idDossiers[]") List<String> idDossiers) {
        ReponsesUIServiceLocator.getArchiveUIService().demanderElimination(context, idDossiers);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("exporterResultats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response exporterExcel() {
        //Récupération des critères de recherche dans la session
        @SuppressWarnings("unchecked")
        Map<String, Object> sessionSearchForms = UserSessionHelper.getUserSessionParameter(
            context,
            SEARCH_FORMS_KEY,
            Map.class
        );
        context.putInContextData(ReponsesContextDataKey.SEARCH_FORM_KEYS, sessionSearchForms);

        ReponsesUIServiceLocator.getRechercheUIService().exportAllDossiers(context);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected SpecificContext getMyContext() {
        return new SpecificContext();
    }

    private String getIndexSuggestion(IndexationItem item) {
        return ResourceHelper.getString("label.vocabulary." + item.getVocabulary()) + " : " + item.getLabel();
    }
}
