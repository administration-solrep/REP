package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getFondDeDossierUIService;
import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getParapheurUIService;
import static fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator.getReponsesDossierDistributionActionService;
import static fr.dila.reponses.ui.services.impl.ParapheurUIServiceImpl.toSelectValueDTO;
import static fr.dila.ss.ui.enums.SSActionCategory.DOSSIER_TAB_ACTIONS;
import static fr.dila.ss.ui.jaxrs.webobject.ajax.AbstractSSDossierAjax.DOSSIER_AJAX_WEBOBJECT;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_CONTENT;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_DETAILS;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static java.lang.Boolean.FALSE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.ui.bean.CompareVersionDTO;
import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.reponses.ui.bean.VocSugUI;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.jaxrs.webobject.ajax.AbstractSSDossierAjax;
import fr.dila.ss.ui.services.SSDossierUIService;
import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = DOSSIER_AJAX_WEBOBJECT)
public class ReponsesDossierAjax extends AbstractSSDossierAjax {
    public static final String DOSSIER_ID = "dossierId";
    public static final String DOSSIER_LINK_ID = "dossierLinkId";
    public static final String MINISTERE_ID = "ministereId";
    public static final String OBSERVATIONS = "observations";

    public ReponsesDossierAjax() {
        super();
    }

    SSDossierUIService<ConsultDossierDTO> getDossierUIService() {
        return ReponsesUIServiceLocator.getDossierUIService();
    }

    protected SSFeuilleRouteUIService getSSFeuilleRouteUIService() {
        return SSUIServiceLocator.getSSFeuilleRouteUIService();
    }

    @Path("deverrouille")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deverouilleDossier(@FormParam(DOSSIER_ID) String id) {
        SpecificContext context = new SpecificContext();

        context.setCurrentDocument(id);
        STActionsServiceLocator.getDossierLockActionService().unlockCurrentDossier(context);
        // Comme je recharge la page si pas d'erreur je mets en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        SSServiceLocator
            .getSsProfilUtilisateurService()
            .addDossierToListDerniersDossierIntervention(context.getSession(), id);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("verrouille")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response verouilleDossier(@FormParam(DOSSIER_ID) String id) {
        SpecificContext context = new SpecificContext();

        context.setCurrentDocument(id);
        STActionsServiceLocator.getDossierLockActionService().lockCurrentDossier(context);

        // Comme je recharge la page si pas d'erreur je met en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        SSServiceLocator
            .getSsProfilUtilisateurService()
            .addDossierToListDerniersDossierIntervention(context.getSession(), id);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @GET
    @Path("version")
    public ThTemplate getDossierByVersion(
        @QueryParam(DOSSIER_ID) String id,
        @QueryParam(DOSSIER_LINK_ID) String dossierLinkId,
        @QueryParam("idVersion") String idVersion,
        @QueryParam("format") String format
    ) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/dossier/onglets/parapheur");

        context.setCurrentDocument(id);
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);
        template.setContext(context);

        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        ParapheurDTO dto = getParapheurUIService().getParapheur(context);
        DocumentModel reponseVersion = context.getSession().getDocument(new IdRef(idVersion));

        if (reponseVersion.isVersion()) {
            dto.setIsEdit(FALSE);
        }

        dto.setVersion(toSelectValueDTO(reponseVersion));
        dto.setReponseNote(reponseVersion.getAdapter(Reponse.class).getTexteReponse());

        List<SelectValueDTO> versionDTOs = getParapheurUIService().getVersionDTOs(context);
        template.getData().put("versions", versionDTOs);
        template.getData().put("parapheurDto", dto);
        template.getData().put("tabActions", context.getActions(DOSSIER_TAB_ACTIONS));
        template.getData().put("comparerAction", context.getAction(ReponsesActionEnum.COMPARER_VERSIONS_PARAPHEUR));
        // Valeurs possibles pour le champ format
        List<SelectValueDTO> formats = new ArrayList<>();
        formats.add(new SelectValueDTO("html", "Html"));
        formats.add(new SelectValueDTO("text", "Text"));
        formats.add(new SelectValueDTO("xml", "Xml"));
        template.getData().put("formatListValues", formats);
        template.getData().put("format", format);

        return template;
    }

    @GET
    @Path("suggestions")
    public String getIndexationSuggestions(
        @QueryParam("typeSelection") String typeSelection,
        @QueryParam("input") String input
    )
        throws JsonProcessingException {
        Map<String, VocSugUI> vocMap = ReponsesActionsServiceLocator.getIndexActionService().getVocMap();
        VocSugUI sugg = vocMap.get(typeSelection);
        List<String> list = sugg.getSuggestions(input);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(list);
    }

    @Path("validationRetourPM")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response validationRetourPM(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        SpecificContext context = new SpecificContext();

        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        context.setCurrentDocument(dossierId);

        getReponsesDossierDistributionActionService().validationRetourPM(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("reattribution")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response reattributionQuestion(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId,
        @FormParam(MINISTERE_ID) String ministereId
    ) {
        SpecificContext context = new SpecificContext();
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        context.setCurrentDocument(dossierId);
        context.putInContextData(STContextDataKey.MINISTERE_ID, ministereId);

        getReponsesDossierDistributionActionService().nonConcerneReattribution(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("reattributionDirect")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doReattributionDirect(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId,
        @FormParam(MINISTERE_ID) String ministereId,
        @FormParam(OBSERVATIONS) String observations
    ) {
        context.setCurrentDocument(dossierId);
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);
        context.putInContextData(STContextDataKey.MINISTERE_ID, ministereId);
        context.putInContextData(ReponsesContextDataKey.OBSERVATIONS, observations);

        getReponsesDossierDistributionActionService().reattributionDirecte(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("arbitragesgg")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doArbitrageSgg(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        context.setCurrentDocument(dossierId);

        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        getReponsesDossierDistributionActionService().demandeArbitrageSGG(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("arbitrageDossier")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doArbitrageDossier(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId,
        @FormParam(MINISTERE_ID) String ministereId,
        @FormParam(OBSERVATIONS) String observations
    ) {
        context.setCurrentDocument(dossierId);
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);
        context.putInContextData(STContextDataKey.MINISTERE_ID, ministereId);
        context.putInContextData(ReponsesContextDataKey.OBSERVATIONS, observations);

        getReponsesDossierDistributionActionService().attributionApresArbitrage(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("rejetRetour")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejetDossierRetour(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        context.setCurrentDocument(dossierId);

        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        getReponsesDossierDistributionActionService().donnerAvisDefavorableEtInsererTaches(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("signature")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response signeEtapeSuivante(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        context.setCurrentDocument(dossierId);

        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        getReponsesDossierDistributionActionService().donnerAvisFavorable(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("rejetPoursuivre")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejetDossierPoursuivre(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        context.setCurrentDocument(dossierId);

        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        getReponsesDossierDistributionActionService().donnerAvisDefavorableEtPoursuivre(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("reorientation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response reorientationQuestion(
        @FormParam(DOSSIER_ID) String id,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        context.setCurrentDocument(id);
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        getReponsesDossierDistributionActionService().nonConcerneReorientation(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("refusReattribution")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response refusReattribution(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        context.setCurrentDocument(dossierId);

        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        getReponsesDossierDistributionActionService().donnerAvisDefavorableEtRetourBdcAttributaire(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("attente")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response mettreEnAttente(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        getReponsesDossierDistributionActionService().mettreEnAttente(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("etapeSuivante")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response etapeSuivante(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        context.setCurrentDocument(dossierId);

        getReponsesDossierDistributionActionService().donnerAvisFavorable(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("refusDeSignature")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response refusDeSignature(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId
    ) {
        context.setCurrentDocument(dossierId);

        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        getReponsesDossierDistributionActionService().donnerAvisDefavorableEtPoursuivre(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("ajout/fichier/fdd")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDocumentFondDossier(
        @FormDataParam(DOSSIER_ID) String dossierId,
        @FormDataParam("groupId") String groupId,
        FormDataMultiPart multipart
    ) {
        if (CollectionUtils.isEmpty(multipart.getFields("documentFile"))) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("fondDossier.add.empty.file.error"));
        } else {
            context.setCurrentDocument(dossierId);

            context.putInContextData(ID, groupId);
            for (FormDataBodyPart field : multipart.getFields("documentFile")) {
                context.putInContextData(FILE_CONTENT, ((BodyPartEntity) field.getEntity()).getInputStream());
                context.putInContextData(FILE_DETAILS, field.getFormDataContentDisposition());
                getFondDeDossierUIService().addFile(context);
            }
        }

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("editDocumentFondDossier")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editDocumentFondDossier(
        @FormDataParam("documentId") String documentId,
        @FormDataParam("groupId") String groupId,
        @FormDataParam("documentFile") InputStream uploadedInputStream,
        @FormDataParam("documentFile") FormDataContentDisposition fileDetail
    ) {
        context.setCurrentDocument(documentId);

        context.putInContextData(ID, groupId);
        context.putInContextData(FILE_CONTENT, uploadedInputStream);
        context.putInContextData(FILE_DETAILS, fileDetail);

        getFondDeDossierUIService().editFile(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("deleteDocumentFondDossier")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDocumentFondDossier(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam("documentIdToDelete") String documentIdToDelete
    ) {
        context.setCurrentDocument(dossierId);
        context.putInContextData(ID, documentIdToDelete);

        getFondDeDossierUIService().deleteFile(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @GET
    @Path("compare")
    public ThTemplate compareVersions(
        @QueryParam(DOSSIER_ID) String dossierId,
        @QueryParam(DOSSIER_LINK_ID) String dossierLinkId,
        @QueryParam("firstVersion") String firstVersion,
        @QueryParam("lastVersion") String lastVersion
    ) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/components/comparator-text");

        context.setCurrentDocument(dossierId);
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);
        template.setContext(context);

        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        CompareVersionDTO dto = ReponsesActionsServiceLocator
            .getComparateurActionService()
            .getVersionTexts(context, firstVersion, lastVersion);

        template.getData().put("dto", dto);

        return template;
    }

    @POST
    @Path("redemarrer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response redemarrerDossier(@FormParam(DOSSIER_ID) String id) {
        context.setCurrentDocument(id);

        getReponsesDossierDistributionActionService().redemarrerDossier(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("connexe")
    public Object getDossierConnexe() {
        return newObject("AppliDossierConnexe", context);
    }

    @POST
    @Path("casserCachetServeur")
    @Produces(MediaType.APPLICATION_JSON)
    public Response casserCachetServeur(@FormParam(DOSSIER_ID) String dossierId) {
        context.setCurrentDocument(dossierId);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        ReponsesActionsServiceLocator.getReponseActionService().briserReponse(context);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
