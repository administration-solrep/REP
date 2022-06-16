package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.DossierActionService;
import fr.dila.reponses.ui.services.actions.impl.DossierActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DossierActionComponent
    extends ServiceEncapsulateComponent<DossierActionService, DossierActionServiceImpl> {

    /**
     * Default constructor
     */
    public DossierActionComponent() {
        super(DossierActionService.class, new DossierActionServiceImpl());
    }
}
