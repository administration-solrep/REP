package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.actions.ReportingActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.io.IOException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ReportingActionServiceImpl implements ReportingActionService {

    @Override
    public File generateFichePdf(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel dossierDoc = context.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        String idQuestion = dossier.getQuestionId();

        return ReponsesServiceLocator.getArchiveService().generateBirtPdf(session, idQuestion, dossierDoc);
    }

    @Override
    public File generateDossierPdf(SpecificContext context) throws IOException {
        CoreSession session = context.getSession();
        DocumentModel dossierDoc = context.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        return ReponsesServiceLocator.getPdfService().generateDossierPdf(session, dossier);
    }
}
