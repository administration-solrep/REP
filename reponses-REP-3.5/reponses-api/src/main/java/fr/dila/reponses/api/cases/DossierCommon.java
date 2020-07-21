package fr.dila.reponses.api.cases;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

public interface DossierCommon {
    public Dossier getDossier(CoreSession session) throws ClientException;
}
