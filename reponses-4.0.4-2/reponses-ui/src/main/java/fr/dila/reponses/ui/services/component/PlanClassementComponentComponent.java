package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.PlanClassementComponentService;
import fr.dila.reponses.ui.services.impl.PlanClassementComponentServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class PlanClassementComponentComponent
    extends ServiceEncapsulateComponent<PlanClassementComponentService, PlanClassementComponentServiceImpl> {

    /**
     * Default constructor
     */
    public PlanClassementComponentComponent() {
        super(PlanClassementComponentService.class, new PlanClassementComponentServiceImpl());
    }
}
