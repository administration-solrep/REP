package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.core.service.AllotissementServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class AllotissementComponent
    extends ServiceEncapsulateComponent<AllotissementService, AllotissementServiceImpl> {

    public AllotissementComponent() {
        super(AllotissementService.class, new AllotissementServiceImpl());
    }
}
