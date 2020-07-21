package fr.dila.reponses.web.report;

import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

@Name("reportingActions")
@Scope(ScopeType.CONVERSATION)
public class ReportingActionsBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -8870324536789519518L;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    private static HttpServletResponse getHttpServletResponse() {
        ServletResponse response = null;
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            response = (ServletResponse) facesContext.getExternalContext().getResponse();
        }

        if (response != null && response instanceof HttpServletResponse) {
            return (HttpServletResponse) response;
        }
        return null;
    }

    /**
     * Génération de la fiche PDF du dossier courant
     * 
     * @throws Exception
     */
    public void generatePdf() throws Exception {
        
        DocumentModel dossierDoc = navigationContext.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        Long numDosier = dossier.getNumeroQuestion();
        String idQuestion = dossier.getQuestionId();
        Question question = dossier.getQuestion(documentManager);
        String origine = question.getOrigineQuestion();
        String type = question.getTypeQuestion();
        // [Type Question] - [Origine Question Court (AN ou SENAT)] [Numéro]
        String name = type + "_" + origine + "_" + numDosier;

        HttpServletResponse response = getHttpServletResponse();
        if (response == null) {
            return;
        }

        response.reset();

        OutputStream outputStream = response.getOutputStream();

        ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
        archiveService.generateBirtPdf(documentManager, outputStream, idQuestion, dossierDoc);

        // prepare pdf response
        response.setHeader("Content-Type", "application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + name + ".pdf");

        FacesContext.getCurrentInstance().responseComplete();
    }
}
