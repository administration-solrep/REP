package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.impl.RepRoutingTaskFilterServiceImpl;
import fr.dila.ss.ui.services.SSRoutingTaskFilterService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RepRoutingTaskFilterComponent
    extends ServiceEncapsulateComponent<SSRoutingTaskFilterService, RepRoutingTaskFilterServiceImpl> {

    /**
     * Default constructor
     */
    public RepRoutingTaskFilterComponent() {
        super(SSRoutingTaskFilterService.class, new RepRoutingTaskFilterServiceImpl());
    }
}
