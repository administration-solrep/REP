package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.core.service.ReponsesArbitrageServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesArbitrageComponent
    extends ServiceEncapsulateComponent<ReponsesArbitrageService, ReponsesArbitrageServiceImpl> {

    public ReponsesArbitrageComponent() {
        super(ReponsesArbitrageService.class, new ReponsesArbitrageServiceImpl());
    }
}
