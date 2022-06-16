package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import static fr.dila.ss.ui.enums.SSActionCategory.FOND_DOSSIER_FOLDER_ACTIONS;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.bean.FondDTO;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.jaxrs.weboject.model.WebObjectExportModel;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliDossierFond")
public class ReponsesDossierFond extends SolonWebObject implements WebObjectExportModel {

    public ReponsesDossierFond() {
        super();
    }

    @GET
    public ThTemplate getFondDeDossier() {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        FondDTO fondDto = ReponsesUIServiceLocator.getFondDeDossierUIService().getFondDTO(context);

        template.getData().put("fondExportAction", context.getAction(SSActionEnum.FOND_DOSSIER_EXPORT));
        template.getData().put("fondAjoutActions", context.getActions(FOND_DOSSIER_FOLDER_ACTIONS));
        template.getData().put(STTemplateConstants.LST_COLONNES, fondDto.getLstColonnes());
        template.getData().put("fondDto", fondDto);
        template.getData().put("repDossierId", context.getCurrentDocument().getId());

        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveFondDeDossier() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), null).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/dossier/onglets/fond", getMyContext());
    }

    @Path("export/zip")
    @GET
    public Response exportFondDossier() {
        StreamingOutput outputStream = getOutputStream(context, ReponsesUIServiceLocator.getArchiveUIService()::export);

        DocumentModel dossierDoc = context.getCurrentDocument();
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        final Question question = dossier.getQuestion(context.getSession());
        final String zipFilename = question.getOrigineQuestion() + "_" + question.getNumeroQuestion() + ".zip";

        return FileDownloadUtils.getZipResponse(outputStream, zipFilename);
    }
}
