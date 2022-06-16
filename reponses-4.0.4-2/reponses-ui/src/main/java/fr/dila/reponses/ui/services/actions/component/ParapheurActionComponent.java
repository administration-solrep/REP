package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.ParapheurActionService;
import fr.dila.reponses.ui.services.actions.impl.ParapheurActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ParapheurActionComponent
    extends ServiceEncapsulateComponent<ParapheurActionService, ParapheurActionServiceImpl> {

    public ParapheurActionComponent() {
        super(ParapheurActionService.class, new ParapheurActionServiceImpl());
    }
}
