package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.SuiviActionService;
import fr.dila.reponses.ui.services.actions.impl.SuiviActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SuiviActionComponent extends ServiceEncapsulateComponent<SuiviActionService, SuiviActionServiceImpl> {

    public SuiviActionComponent() {
        super(SuiviActionService.class, new SuiviActionServiceImpl());
    }
}
