package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.flux.HasInfoFlux;
import fr.dila.reponses.ui.services.actions.FluxActionService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class FluxActionServiceImpl implements FluxActionService {

    public HasInfoFlux getHasInfoFlux(CoreSession session, Dossier dossier) {
        if (dossier == null) {
            return null;
        }
        DocumentModel questionDoc = dossier.getQuestion(session).getDocument();
        return questionDoc.getAdapter(HasInfoFlux.class);
    }
}
