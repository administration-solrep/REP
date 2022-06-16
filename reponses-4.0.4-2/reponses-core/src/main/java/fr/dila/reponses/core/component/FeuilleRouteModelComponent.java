package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.core.service.FeuilleRouteModelServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FeuilleRouteModelComponent
    extends ServiceEncapsulateComponent<FeuilleRouteModelService, FeuilleRouteModelServiceImpl> {

    public FeuilleRouteModelComponent() {
        super(FeuilleRouteModelService.class, new FeuilleRouteModelServiceImpl());
    }
}
