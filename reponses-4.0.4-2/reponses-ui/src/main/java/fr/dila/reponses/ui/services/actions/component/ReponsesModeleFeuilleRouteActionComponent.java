package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.ReponsesModeleFeuilleRouteActionService;
import fr.dila.reponses.ui.services.actions.impl.ReponsesModeleFeuilleRouteActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesModeleFeuilleRouteActionComponent
    extends ServiceEncapsulateComponent<ReponsesModeleFeuilleRouteActionService, ReponsesModeleFeuilleRouteActionServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesModeleFeuilleRouteActionComponent() {
        super(ReponsesModeleFeuilleRouteActionService.class, new ReponsesModeleFeuilleRouteActionServiceImpl());
    }
}
