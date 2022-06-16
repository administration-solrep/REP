package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getFavorisDossierService;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.FAVORI_TRAVAIL_DOSSIERS_ACTIONS;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER_IDS;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER_IDS_STRING;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.FAVORIS_ID;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getFavorisDossierUIService;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static java.util.Objects.requireNonNull;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.FavorisTravailForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "FavorisTravailAjax")
public class ReponsesFavorisTravailAjax extends SolonWebObject {
    public static final String IDFAVORI = "idFavori";

    public ReponsesFavorisTravailAjax() {
        super();
    }

    /**
     * Cette méthode permet de changer de formulaire suivant le choix
     * de créer un nouveau favori ou d'ajouter à la liste des favoris existant
     */
    @GET
    @Path("/type")
    public ThTemplate getFormType(@QueryParam("type") String type, @QueryParam("idDossiers") String idDossiers) {
        ThTemplate template = new AjaxLayoutThTemplate();
        Map<String, Object> map = new HashMap<>();
        boolean typeCreation = StringUtils.isNotBlank(type) && "NOUVEAU".equals(type);
        map.put("isCreation", typeCreation);
        if (typeCreation) {
            FavorisTravailForm favorisForm = new FavorisTravailForm();
            favorisForm.setIdDossiers(idDossiers);
            map.put("favorisForm", favorisForm);
            template.setName("fragments/components/favoris/favorisTravailFormCreation");
        } else {
            template.setName("fragments/components/favoris/favorisTravailSelectFavoris");
            getFavorisDossierUIService().getFavorisSelectValueDtos(context);
            map.put("favoris", getFavorisDossierUIService().getFavorisSelectValueDtos(context));
            map.put("idDossiers", idDossiers);
        }
        template.setContext(context);
        template.setData(map);
        return template;
    }

    /**
     * @param favoris correspond à l'id du favori sélectionné
     * @param idDossiers chaine d'id de dossiers séparés par des ;
     *
     */
    @POST
    @Path("/addToFavorites")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToExistFavoris(
        @FormParam("favoris") String favoris,
        @FormParam("idDossiers") String idDossiers
    ) {
        context.putInContextData(FAVORIS_ID, favoris);
        context.putInContextData(DOSSIER_IDS_STRING, idDossiers);
        ReponsesUIServiceLocator.getFavorisDossierUIService().addFavoris(context);

        // Comme je recharge la page si pas d'erreur je mets en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    /**
     * Consultation des dossiers: affichage des dossiers du favori
     */
    @GET
    public ThTemplate getHomeDossiers(@QueryParam(IDFAVORI) String idFavori, @SwBeanParam DossierListForm resultForm) {
        return getListeDossiers(idFavori, resultForm);
    }

    @GET
    @Path("listeDossiers")
    public ThTemplate getListeDossiers(@QueryParam(IDFAVORI) String idFavori, @SwBeanParam DossierListForm resultForm) {
        requireNonNull(idFavori, "null idFavori");
        context.getContextData().put(IDFAVORI, idFavori);

        Map<String, Object> otherParameter = new HashMap<>();
        otherParameter.put(IDFAVORI, idFavori);
        template.setContext(context);

        resultForm = ObjectHelper.requireNonNullElseGet(resultForm, DossierListForm::newForm);

        context.getContextData().put("form", resultForm);

        RepDossierList resultList = getFavorisDossierUIService().getDossierList(context);
        resultForm.setColumnVisibility(resultList.getListeColonnes());

        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, resultList);
        map.put(STTemplateConstants.LST_COLONNES, resultList.getListeColonnes());
        map.put(ReponsesTemplateConstants.NB_DOSSIER, resultList.getNbTotal());
        map.put(STTemplateConstants.TITRE, resultList.getTitre());
        map.put(STTemplateConstants.SOUS_TITRE, resultList.getSousTitre());
        map.put(STTemplateConstants.LST_SORTED_COLONNES, resultList.getListeSortedColonnes());
        map.put(STTemplateConstants.LST_SORTABLE_COLONNES, resultList.getListeSortableAndVisibleColonnes());
        map.put(STTemplateConstants.RESULT_FORM, resultForm);

        map.put(STTemplateConstants.DATA_URL, "/favoris/listeDossiers");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/favoris/listeDossiers");
        map.put(STTemplateConstants.OTHER_PARAMETER, otherParameter);
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(FAVORI_TRAVAIL_DOSSIERS_ACTIONS));
        map.put(IDFAVORI, idFavori);
        map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        template.setData(map);
        return template;
    }

    @POST
    @Path("removeToFavorites")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeToFavorites(
        @FormParam(IDFAVORI) String idFavori,
        @FormParam("idDossiers[]") List<String> idDossiers
    ) {
        context.putInContextData(FAVORIS_ID, idFavori);
        context.putInContextData(DOSSIER_IDS, idDossiers);
        ReponsesUIServiceLocator.getFavorisDossierUIService().removeFavoris(context);

        // Comme je recharge la page si pas d'erreur je mets en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("/supprimer")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response supprimerFavori(@FormParam("id") String id) {
        context.getSession().removeDocument(new IdRef(id));
        context.getMessageQueue().addToastSuccess(getString("favori.remove.success"));

        // Comme je recharge la page si pas d'erreur je mets en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), null).build();
    }

    /**
     * Vider mes favoris de travail en supprimant tous les favoris
     */
    @GET
    @Path("/vider")
    @Produces(MediaType.APPLICATION_JSON)
    public Response supprimerTousLesFavoris() {
        getFavorisDossierService().removeFavorisRepertoires(context.getSession());
        context.getMessageQueue().addToastSuccess(getString("favoris.remove.success"));

        // Comme je recharge la page si pas d'erreur je mets en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/table/tableDossiers", getMyContext());
    }
}
