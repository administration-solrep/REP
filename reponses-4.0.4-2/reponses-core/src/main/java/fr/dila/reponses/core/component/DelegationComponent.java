package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.DelegationService;
import fr.dila.reponses.core.service.DelegationServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DelegationComponent extends ServiceEncapsulateComponent<DelegationService, DelegationServiceImpl> {

    public DelegationComponent() {
        super(DelegationService.class, new DelegationServiceImpl());
    }
}
