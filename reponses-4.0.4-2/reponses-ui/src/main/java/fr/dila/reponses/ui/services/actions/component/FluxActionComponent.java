package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.FluxActionService;
import fr.dila.reponses.ui.services.actions.impl.FluxActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FluxActionComponent extends ServiceEncapsulateComponent<FluxActionService, FluxActionServiceImpl> {

    public FluxActionComponent() {
        super(FluxActionService.class, new FluxActionServiceImpl());
    }
}
