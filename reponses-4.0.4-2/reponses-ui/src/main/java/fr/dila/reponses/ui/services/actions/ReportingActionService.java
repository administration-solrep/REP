package fr.dila.reponses.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.io.IOException;

public interface ReportingActionService {
    /**
     * Génération de la fiche PDF du dossier courant
     */
    File generateFichePdf(SpecificContext context);

    /**
     * Génération du dossier au format PDF pour l'impression
     */
    File generateDossierPdf(SpecificContext context) throws IOException;
}
