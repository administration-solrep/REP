package fr.dila.reponses.core.export;

import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public class RepDossierIdConfig extends AbstractRepDossierConfig<String> {

    public RepDossierIdConfig(List<String> docIds) {
        super(docIds);
    }

    @Override
    protected String[] getDataCells(CoreSession session, String item) {
        IdRef docRef = new IdRef(item);
        if (session.exists(docRef)) {
            DocumentModel document = session.getDocument(docRef);
            return getDataCellsForDossier(session, document);
        } else {
            return EMPTY_DOSSIER_LINE;
        }
    }
}
