package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.ReponsesFeuilleRouteUIService;
import fr.dila.reponses.ui.services.impl.ReponsesFeuilleRouteUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesFeuilleRouteUIComponent
    extends ServiceEncapsulateComponent<ReponsesFeuilleRouteUIService, ReponsesFeuilleRouteUIServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesFeuilleRouteUIComponent() {
        super(ReponsesFeuilleRouteUIService.class, new ReponsesFeuilleRouteUIServiceImpl());
    }
}
