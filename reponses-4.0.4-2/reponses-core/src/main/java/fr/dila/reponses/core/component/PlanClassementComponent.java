package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.PlanClassementService;
import fr.dila.reponses.core.service.PlanClassementServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class PlanClassementComponent
    extends ServiceEncapsulateComponent<PlanClassementService, PlanClassementServiceImpl> {

    public PlanClassementComponent() {
        super(PlanClassementService.class, new PlanClassementServiceImpl());
    }
}
