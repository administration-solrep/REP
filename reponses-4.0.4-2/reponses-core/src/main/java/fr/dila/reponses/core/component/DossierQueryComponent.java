package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.DossierQueryService;
import fr.dila.reponses.core.service.DossierQueryServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DossierQueryComponent extends ServiceEncapsulateComponent<DossierQueryService, DossierQueryServiceImpl> {

    public DossierQueryComponent() {
        super(DossierQueryService.class, new DossierQueryServiceImpl());
    }
}
