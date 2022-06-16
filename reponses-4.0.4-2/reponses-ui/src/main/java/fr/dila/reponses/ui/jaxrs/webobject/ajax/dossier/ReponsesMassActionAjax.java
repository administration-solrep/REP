package fr.dila.reponses.ui.jaxrs.webobject.ajax.dossier;

import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.jaxrs.weboject.model.WebObjectExportModel;
import fr.dila.ss.ui.th.bean.DossierMailForm;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxMassAction")
public class ReponsesMassActionAjax extends SolonWebObject implements WebObjectExportModel {
    public static final String ID_DOSSIERS = "idDossiers[]";
    public static final String ID_MINISTERE = "ministereId";
    public static final String ID_DIRECTION = "directionId";
    public static final String OBSERVATIONS = "observations";

    public ReponsesMassActionAjax() {
        super();
    }

    @Path("favorable")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response donnerAvisFavorable(@FormParam(ID_DOSSIERS) List<String> dossiers) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);

        ReponsesUIServiceLocator.getDossierMassUIService().masseFdrActionDonnerAvisFavorable(context);
        return saveMessageAndReturnResponse(context);
    }

    @Path("defavorableRetour")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response donnerAvisDefavorableRetour(@FormParam(ID_DOSSIERS) List<String> dossiers) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);

        ReponsesUIServiceLocator.getDossierMassUIService().masseFdrActionDonnerAvisDefavorableEtInsererTaches(context);
        return saveMessageAndReturnResponse(context);
    }

    @Path("defavorablePoursuivre")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response donnerAvisDefavorablePoursuivre(@FormParam(ID_DOSSIERS) List<String> dossiers) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);

        ReponsesUIServiceLocator.getDossierMassUIService().masseFdrActionDonnerAvisDefavorableEtPoursuivre(context);
        return saveMessageAndReturnResponse(context);
    }

    /**
     * Envoyer une liste de dossiers par mail
     */
    @POST
    @Path("sendMail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendDossiersMail(
        @SwBeanParam DossierMailForm dossierMailForm,
        @FormParam("idDossiers[]") List<String> dossiers
    ) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);
        context.putInContextData(SSContextDataKey.DOSSIER_MAIL_FORM, dossierMailForm);

        ReponsesUIServiceLocator.getArchiveUIService().masseEnvoyerMailDossier(context);

        return saveMessageAndReturnResponse(context);
    }

    @Path("export/zip")
    @POST
    @Produces("application/zip")
    public Response exportDossierList(@FormParam("id[]") List<String> dossiers) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);
        StreamingOutput outputStream = getOutputStream(
            context,
            ReponsesUIServiceLocator.getArchiveUIService()::masseExport
        );

        final String zipFilename = "export_" + SolonDateConverter.DATE_DASH.formatNow() + ".zip";

        return FileDownloadUtils.getZipResponse(outputStream, zipFilename);
    }

    @Path("reattribution")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response reattribution(
        @FormParam(ID_DOSSIERS) List<String> dossiers,
        @FormParam(ID_MINISTERE) String idMinistere
    ) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);
        context.putInContextData(STContextDataKey.MINISTERE_ID, idMinistere);

        ReponsesUIServiceLocator.getDossierMassUIService().masseFdrActionNonConcerneReattribution(context);
        return saveMessageAndReturnResponse(context);
    }

    @Path("reattributionDirect")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response reattributionDirect(
        @FormParam(ID_DOSSIERS) List<String> dossiers,
        @FormParam(ID_MINISTERE) String idMinistere,
        @FormParam(OBSERVATIONS) String observations
    ) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);
        context.putInContextData(STContextDataKey.MINISTERE_ID, idMinistere);
        context.putInContextData(SSContextDataKey.COMMENT_CONTENT, observations);

        ReponsesUIServiceLocator.getDossierMassUIService().masseFdrActionReattributionDirecte(context);
        return saveMessageAndReturnResponse(context);
    }

    @Path("reorientation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response reorientation(@FormParam(ID_DOSSIERS) List<String> dossiers) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);

        ReponsesUIServiceLocator.getDossierMassUIService().masseFdrActionNonConcerneReorientation(context);
        return saveMessageAndReturnResponse(context);
    }

    @Path("reaffectation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response reaffectation(
        @FormParam(ID_DOSSIERS) List<String> dossiers,
        @FormParam(ID_MINISTERE) String idMinistere
    ) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);
        context.putInContextData(STContextDataKey.MINISTERE_ID, idMinistere);

        ReponsesUIServiceLocator.getDossierMassUIService().masseFdrActionNonConcerneReaffectation(context);
        return saveMessageAndReturnResponse(context);
    }

    @Path("arbitrage")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response demandeArbitrage(@FormParam(ID_DOSSIERS) List<String> dossiers) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);

        ReponsesUIServiceLocator.getDossierMassUIService().masseFdrActionDemandeArbitrageSGG(context);
        return saveMessageAndReturnResponse(context);
    }

    @Path("changeMinRattachement")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeMinistereRattachement(
        @FormParam(ID_DOSSIERS) List<String> dossiers,
        @FormParam(ID_MINISTERE) String ministereId
    ) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);
        context.putInContextData(STContextDataKey.MINISTERE_ID, ministereId);

        ReponsesUIServiceLocator.getDossierMassUIService().masseActionModificationMinistereRattachement(context);
        return saveMessageAndReturnResponse(context);
    }

    @Path("changeDirPilote")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeDirectionPilote(
        @FormParam(ID_DOSSIERS) List<String> dossiers,
        @FormParam(ID_DIRECTION) String directionId
    ) {
        context.putInContextData(ReponsesContextDataKey.DOSSIER_IDS, dossiers);
        context.putInContextData(STContextDataKey.DIRECTION_ID, directionId);

        ReponsesUIServiceLocator.getDossierMassUIService().masseActionModificationDirectionPilote(context);
        return saveMessageAndReturnResponse(context);
    }

    private Response saveMessageAndReturnResponse(SpecificContext context) {
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
}
