package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_DIVERS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_EDITION;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_FDR;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_RENVOIS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.RECHERCHE_DOSSIER_ACTIONS_GENERAL;
import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesSuivi.REPONSE_CHAMP_CONTRIB_NAME;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.bean.RequetePersoForm;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.RequeteUIService;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.ss.ui.bean.RequeteExperteDTO;
import fr.dila.ss.ui.jaxrs.webobject.ajax.SSRequeteExperteAjax;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.requete.recherchechamp.RechercheChampService;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.validators.annot.SwRequired;
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
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ReponsesSuiviAjax")
public class ReponsesSuiviAjax extends SSRequeteExperteAjax {

    public ReponsesSuiviAjax() {
        super();
    }

    @POST
    @Path("/resultats")
    public ThTemplate getResultsFromRequete(@SwBeanParam DossierListForm form) {
        ThTemplate template = new AjaxLayoutThTemplate("fragments/recherche/result-list", context);

        form = ObjectHelper.requireNonNullElseGet(form, DossierListForm::newForm);

        context.putInContextData(ReponsesContextDataKey.DOSSIER_LIST_FORM, form);
        context.putInContextData(
            ReponsesContextDataKey.REQUETE_PERSO_LIGNES,
            UserSessionHelper
                .getUserSessionParameter(context, getDtoSessionKey(context), RequeteExperteDTO.class)
                .getRequetes()
        );

        RepDossierList lstResults = ReponsesUIServiceLocator
            .getRequeteUIService()
            .getDossiersByRequeteCriteria(context);

        String titre = StringUtils.defaultIfBlank(lstResults.getTitre(), "Résultats de la requête");

        context.putInContextData(STContextDataKey.IS_ACTION_MASS, true);

        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, lstResults);

        form.setColumnVisibility(lstResults.getListeColonnes());
        map.put(STTemplateConstants.RESULT_FORM, form);
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.LST_SORTED_COLONNES, lstResults.getListeSortedColonnes());
        map.put(STTemplateConstants.LST_SORTABLE_COLONNES, lstResults.getListeSortableAndVisibleColonnes());
        map.put(STTemplateConstants.TITRE, titre);
        map.put(STTemplateConstants.SOUS_TITRE, lstResults.getSousTitre());
        map.put(STTemplateConstants.DISPLAY_TABLE, lstResults.getNbTotal() > 0);
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(RECHERCHE_DOSSIER_ACTIONS_GENERAL));
        map.put(SSTemplateConstants.FDR_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_FDR));
        map.put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_EDITION));
        map.put(ReponsesTemplateConstants.RENVOI_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_RENVOIS));
        map.put(SSTemplateConstants.DIVERS_ACTIONS, context.getActions(DOSSIERS_MASS_ACTIONS_DIVERS));
        map.put(STTemplateConstants.DATA_URL, "/suivi/resultats");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/suivi/resultats");

        template.setData(map);
        UserSessionHelper.putUserSessionParameter(context, getResultsSessionKey(context), map);

        return template;
    }

    @Path("/requete")
    public Object getResultsFromRequete() {
        ThTemplate template = new AjaxLayoutThTemplate("fragments/table/tableDossiers", context);

        return newObject("ReponsesSuiviRequete", context, template);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("sauvegardePerso")
    public Response saveRequetePerso(@SwBeanParam RequetePersoForm form, @FormParam("idRequete") String idRequete) {
        if (StringUtils.isNotBlank(idRequete)) {
            context.setCurrentDocument(idRequete);
        }
        context.putInContextData(ReponsesContextDataKey.REQUETE_PERSO_FORM, form);

        String id = ReponsesUIServiceLocator.getRequeteUIService().saveRequetePerso(context);

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            addMessageQueueInSession();
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), id).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("editerPerso")
    public Response updateRequetePerso(@SwRequired @FormParam("idRequete") String idRequete) {
        context.setCurrentDocument(idRequete);

        context.putInContextData(
            ReponsesContextDataKey.REQUETE_PERSO_LIGNES,
            UserSessionHelper
                .getUserSessionParameter(context, getDtoSessionKey(context), RequeteExperteDTO.class)
                .getRequetes()
        );

        RequetePersoForm form = ReponsesUIServiceLocator.getRequeteUIService().getRequetePersoFormWithId(context);
        context.putInContextData(ReponsesContextDataKey.REQUETE_PERSO_FORM, form);

        String id = ReponsesUIServiceLocator.getRequeteUIService().saveRequetePerso(context);

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            addMessageQueueInSession();
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), id).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/modifier")
    public Response editRequetePerso(@QueryParam("id") String id) {
        RechercheChampService champService = STServiceLocator.getRechercheChampService();
        RequeteUIService requeteService = ReponsesUIServiceLocator.getRequeteUIService();

        RequeteExperteDTO requeteExperteDTO = UserSessionHelper.getUserSessionParameter(
            context,
            getDtoSessionKey(context),
            RequeteExperteDTO.class
        );
        if (requeteExperteDTO == null) {
            requeteExperteDTO = new RequeteExperteDTO();
            List<ChampDescriptor> champs = champService.getChamps(getContribName());
            requeteExperteDTO.setChamps(champs);
        }

        context.setCurrentDocument(id);

        requeteExperteDTO.setRequetes(requeteService.getRequeteLignesFromRequete(context));
        if (requeteExperteDTO.getRequetes() != null) {
            UserSessionHelper.putUserSessionParameter(context, getDtoSessionKey(context), requeteExperteDTO);
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), id).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("supprimer")
    public Response supprimerRequetePerso(@QueryParam("id") String id) {
        context.setCurrentDocument(id);

        ReponsesUIServiceLocator.getRequeteUIService().deleteRequetePerso(context);

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected String getContribName() {
        return REPONSE_CHAMP_CONTRIB_NAME;
    }

    @Override
    public String getSuffixForSessionKeys(SpecificContext context) {
        return "_REP";
    }
}
