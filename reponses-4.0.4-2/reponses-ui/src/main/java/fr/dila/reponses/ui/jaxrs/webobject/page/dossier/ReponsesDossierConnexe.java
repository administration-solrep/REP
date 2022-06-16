package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.ui.bean.DossierConnexeDTO;
import fr.dila.reponses.ui.bean.DossierConnexeList;
import fr.dila.reponses.ui.bean.DossierConnexeMinistereDTO;
import fr.dila.reponses.ui.bean.DossierConnexeMinistereList;
import fr.dila.reponses.ui.enums.ReponsesActionCategory;
import fr.dila.reponses.ui.services.actions.DossierConnexeActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.impl.DossierUIServiceImpl;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliDossierConnexe")
public class ReponsesDossierConnexe extends SolonWebObject {
    private static final String DOSSIER_ID = DossierUIServiceImpl.DOSSIER_ID_KEY;

    public ReponsesDossierConnexe() {
        super();
    }

    private DossierConnexeActionService getDossierConnexeActionService() {
        return ReponsesActionsServiceLocator.getDossierConnexeActionService();
    }

    @GET
    public ThTemplate getListeQuestionsConnexes(@QueryParam(DOSSIER_ID) String dossierId) {
        context.setCurrentDocument(dossierId);

        DossierConnexeMinistereList dossierConnexeMinistereList = new DossierConnexeMinistereList();
        List<DossierConnexeMinistereDTO> dossiers = getDossierConnexeActionService().getListMinisteres(context);
        dossierConnexeMinistereList.setDossiers(dossiers);

        template.getData().put(STTemplateConstants.LST_COLONNES, dossierConnexeMinistereList.getLstColonnes());
        template.getData().put(STTemplateConstants.RESULT_LIST, dossierConnexeMinistereList.getDossiers());

        return template;
    }

    @GET
    @Path("liste")
    public ThTemplate getListeQuestionsConnexesMinistere(
        @QueryParam(DOSSIER_ID) String dossierId,
        @QueryParam("idMinistere") String idMinistere
    ) {
        ThTemplate template = new AjaxLayoutThTemplate(
            "fragments/dossier/onglets/connexe/listeQuestionConnexe",
            context
        );

        context.setCurrentDocument(dossierId);

        context.putInContextData(SSContextDataKey.MINISTERE_ID, idMinistere);
        context.putInContextData(
            "alloti",
            StringUtils.isNotBlank(context.getCurrentDocument().getAdapter(Dossier.class).getDossierLot())
        );
        DossierConnexeList dossierConnexeList = getDossierConnexeActionService().getListDossierConnexe(context);

        template.getData().put(STTemplateConstants.LST_COLONNES, dossierConnexeList.getLstColonnes());
        template.getData().put(STTemplateConstants.RESULT_LIST, dossierConnexeList.getDossiers());
        template.getData().put("tabActions", context.getActions(ReponsesActionCategory.DOSSIER_CONNEXE_ACTIONS));
        template
            .getData()
            .put("returnActions", context.getActions(ReponsesActionCategory.DOSSIER_CONNEXE_RETOUR_ACTIONS));
        template.getData().put("idMinistere", idMinistere);
        template.getData().put("title", dossierConnexeList.getTitre());
        template
            .getData()
            .put(
                "selectionDisabled",
                dossierConnexeList.getDossiers().stream().allMatch(DossierConnexeDTO::isDisabled)
            );

        return template;
    }

    @GET
    @Path("question")
    public ThTemplate getQuestion(
        @QueryParam(DOSSIER_ID) String dossierId,
        @QueryParam("selectedDossierId") String selectedDossierId,
        @QueryParam("idMinistere") String idMinistere
    ) {
        ThTemplate template = new AjaxLayoutThTemplate("fragments/dossier/onglets/connexe/questionConnexe", context);

        context.setCurrentDocument(dossierId);
        context.putInContextData(STContextDataKey.DOSSIER_ID, selectedDossierId);

        DossierConnexeDTO dto = getDossierConnexeActionService().getQuestion(context);

        template.getData().put("dto", dto);
        template
            .getData()
            .put("returnActions", context.getActions(ReponsesActionCategory.DOSSIER_CONNEXE_RETOUR_ACTIONS));
        template
            .getData()
            .put(
                "returnMinistereActions",
                context.getActions(ReponsesActionCategory.DOSSIER_CONNEXE_RETOUR_MINISTERE_ACTIONS)
            );
        template
            .getData()
            .put(
                "returnQuestionActions",
                context.getActions(ReponsesActionCategory.DOSSIER_CONNEXE_RETOUR_QUESTION_COPIE)
            );
        template.getData().put("idMinistere", idMinistere);
        template.getData().put("title", dto.getSourceNumeroQuestion());

        return template;
    }

    @POST
    @Path("lot")
    @Produces(MediaType.APPLICATION_JSON)
    public Response creerOuAjouterQuestionLot(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam("idDossiers[]") List<String> idDossiers,
        @FormParam("alloti") Boolean alloti
    ) {
        context.setCurrentDocument(dossierId);
        context.putInContextData(STContextDataKey.IDS, idDossiers);

        if (alloti) {
            getDossierConnexeActionService().createLot(context);
        } else {
            getDossierConnexeActionService().updateLot(context);
        }

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("copier")
    @Produces(MediaType.APPLICATION_JSON)
    public Response copierReponse(
        @FormParam(DOSSIER_ID) String dossierId,
        @FormParam("selectedDossierId") String selectedDossierId
    ) {
        context.setCurrentDocument(dossierId);
        context.putInContextData(STContextDataKey.ID, selectedDossierId);
        getDossierConnexeActionService().setReponseQuestion(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveDossierConnexe() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), null).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate(
            "fragments/dossier/onglets/connexe/listeQuestionConnexeMinistere",
            getMyContext()
        );
    }
}
