package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.SimpleIndexActionService;
import fr.dila.reponses.ui.services.actions.impl.SimpleIndexActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SimpleIndexActionComponent
    extends ServiceEncapsulateComponent<SimpleIndexActionService, SimpleIndexActionServiceImpl> {

    public SimpleIndexActionComponent() {
        super(SimpleIndexActionService.class, new SimpleIndexActionServiceImpl());
    }
}
