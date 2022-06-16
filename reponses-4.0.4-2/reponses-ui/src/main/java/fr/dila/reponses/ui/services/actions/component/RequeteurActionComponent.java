package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.RequeteurActionService;
import fr.dila.reponses.ui.services.actions.impl.RequeteurActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RequeteurActionComponent
    extends ServiceEncapsulateComponent<RequeteurActionService, RequeteurActionServiceImpl> {

    public RequeteurActionComponent() {
        super(RequeteurActionService.class, new RequeteurActionServiceImpl());
    }
}
