package fr.dila.reponses.api.cases;

import org.nuxeo.ecm.core.api.CoreSession;

public interface DossierCommon {
    Dossier getDossier(CoreSession session);
}
