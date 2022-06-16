package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.DossierService;
import fr.dila.reponses.core.service.DossierServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DossierComponent extends ServiceEncapsulateComponent<DossierService, DossierServiceImpl> {

    public DossierComponent() {
        super(DossierService.class, new DossierServiceImpl());
    }
}
