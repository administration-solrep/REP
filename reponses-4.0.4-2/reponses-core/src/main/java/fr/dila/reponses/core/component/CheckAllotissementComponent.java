package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.CheckAllotissementService;
import fr.dila.reponses.core.service.CheckAllotissementServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class CheckAllotissementComponent
    extends ServiceEncapsulateComponent<CheckAllotissementService, CheckAllotissementServiceImpl> {

    public CheckAllotissementComponent() {
        super(CheckAllotissementService.class, new CheckAllotissementServiceImpl());
    }
}
