package fr.dila.reponses.ui.services.actions.nxql;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesSmartNXQLQueryActionComponent
    extends ServiceEncapsulateComponent<ReponsesSmartNXQLQueryActionService, ReponsesSmartNXQLQueryActionServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesSmartNXQLQueryActionComponent() {
        super(ReponsesSmartNXQLQueryActionService.class, new ReponsesSmartNXQLQueryActionServiceImpl());
    }
}
