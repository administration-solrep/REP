package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.ss.ui.jaxrs.webobject.page.AbstractSSDossier.DOSSIER_WEBOBJECT;

import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.th.model.ReponsesLayoutThTemplate;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.jaxrs.webobject.page.AbstractSSDossier;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = DOSSIER_WEBOBJECT)
public class ReponsesDossier extends AbstractSSDossier {

    public ReponsesDossier() {
        super("parapheur"); // onglet par défaut
    }

    @Override
    protected void setSpecificDataForGetDossier(ThTemplate template, String id, String tab) {
        Map<String, Object> map = template.getData();

        ConsultDossierDTO dossier = (ConsultDossierDTO) map.get(SSTemplateConstants.MON_DOSSIER);
        if (dossier != null && dossier.getQuestionInfo() != null) {
            context.setNavigationContextTitle(
                new Breadcrumb(
                    String.format(
                        "Consultation du dossier %s %s",
                        dossier.getQuestionInfo().getTypeQuestion(),
                        dossier.getQuestionInfo().getNumeroQuestion()
                    ),
                    "/dossier/" + id + "/" + tab,
                    Breadcrumb.SUBTITLE_ORDER,
                    context.getWebcontext().getRequest()
                )
            );
        } else {
            context.setNavigationContextTitle(
                new Breadcrumb(
                    "Consultation d'un dossier inconnu",
                    String.format("/dossier/%s/%s", id, tab),
                    Breadcrumb.SUBTITLE_ORDER,
                    context.getWebcontext().getRequest()
                )
            );
        }
        if (context.getNavigationContext().size() > 1) {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        } else {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, "");
        }

        map.put(STTemplateConstants.LOCK_ACTIONS, context.getActions(SSActionCategory.DOSSIER_TOPBAR_ACTIONS_LOCKS));

        ReponsesUIServiceLocator.getDossierUIService().loadDossierActions(context, template);

        // On passe le dossier à l'état lu
        SSUIServiceLocator.getSSDossierDistributionUIService().changeReadStateDossierLink(context);
    }

    @GET
    @Path("editerPdf/{id}")
    @Produces("application/pdf")
    public Response afficherPdfNewTab(@PathParam("id") String id) {
        context.setCurrentDocument(id);

        File file = ReponsesActionsServiceLocator.getReportingActionService().generateFichePdf(context);
        return FileDownloadUtils.getAttachmentPdf(file, file.getName());
    }

    @GET
    @Path("imprimer/{id}")
    @Produces("application/pdf")
    public Response imprimerPdfNewTab(@PathParam("id") String id) throws IOException {
        context.setCurrentDocument(id);
        File file = ReponsesActionsServiceLocator.getReportingActionService().generateDossierPdf(context);
        String filename =
            file.getName().substring(0, file.getName().indexOf('-')).replace("DossierFinal_", "") + ".pdf";

        return FileDownloadUtils.getAttachmentPdf(file, filename);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesLayoutThTemplate();
    }
}
