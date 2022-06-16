package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesInjectionGouvernementService;
import fr.dila.reponses.core.service.ReponsesInjectionGouvernementServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesInjectionGouvernementComponent
    extends ServiceEncapsulateComponent<ReponsesInjectionGouvernementService, ReponsesInjectionGouvernementServiceImpl> {

    public ReponsesInjectionGouvernementComponent() {
        super(ReponsesInjectionGouvernementService.class, new ReponsesInjectionGouvernementServiceImpl());
    }
}
