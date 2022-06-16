package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.AllotissementActionService;
import fr.dila.reponses.ui.services.actions.impl.AllotissementActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class AllotissementActionComponent
    extends ServiceEncapsulateComponent<AllotissementActionService, AllotissementActionServiceImpl> {

    /**
     * Default constructor
     */
    public AllotissementActionComponent() {
        super(AllotissementActionService.class, new AllotissementActionServiceImpl());
    }
}
