package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Dossier;
import java.io.File;
import java.io.IOException;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service associé à la manipulation de fichiers PDF au niveau métier
 *
 * @author tlombard
 * @author ygbinu
 */
public interface PdfDossierService {
    File generateDossierPdf(CoreSession session, Dossier dossier) throws IOException;
}
