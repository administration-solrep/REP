package fr.dila.reponses.ui.jaxrs.webobject.ajax.journal;

import fr.dila.reponses.ui.bean.actions.ReponsesDossierActionDTO;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.jaxrs.webobject.ajax.journal.SsJournalAjax;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "JournalAjax")
public class RepJournalAjax extends SsJournalAjax {

    public RepJournalAjax() {
        super();
    }

    @Override
    protected void buildContext(SpecificContext context, String dossierID) {
        super.buildContext(context, dossierID);

        ReponsesDossierActionDTO dossierActions = new ReponsesDossierActionDTO();
        dossierActions.setIsDossierContainsMinistere(
            ReponsesActionsServiceLocator
                .getDossierActionService()
                .isDossierContainsMinistere(
                    (SSPrincipal) context.getSession().getPrincipal(),
                    context.getCurrentDocument()
                )
        );

        context.putInContextData(ReponsesContextDataKey.DOSSIER_ACTIONS, dossierActions);
    }
}
