package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.DossierDistributionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DossierDistributionComponent
    extends ServiceEncapsulateComponent<DossierDistributionService, DossierDistributionServiceImpl> {

    public DossierDistributionComponent() {
        super(DossierDistributionService.class, new DossierDistributionServiceImpl());
    }
}
