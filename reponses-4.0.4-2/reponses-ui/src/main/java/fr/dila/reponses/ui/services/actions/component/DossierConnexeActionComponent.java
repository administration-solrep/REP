package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.DossierConnexeActionService;
import fr.dila.reponses.ui.services.actions.impl.DossierConnexeActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DossierConnexeActionComponent
    extends ServiceEncapsulateComponent<DossierConnexeActionService, DossierConnexeActionServiceImpl> {

    public DossierConnexeActionComponent() {
        super(DossierConnexeActionService.class, new DossierConnexeActionServiceImpl());
    }
}
