package fr.dila.reponses.core.export;

import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class RepDossierConfig extends AbstractRepDossierConfig<DocumentModel> {

    public RepDossierConfig(List<DocumentModel> dossiers) {
        super(dossiers);
    }

    @Override
    protected String[] getDataCells(CoreSession session, DocumentModel item) {
        return getDataCellsForDossier(session, item);
    }
}
