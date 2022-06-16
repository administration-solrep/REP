package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.DelegationUIService;
import fr.dila.reponses.ui.services.impl.DelegationUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DelegationUIServiceComponent
    extends ServiceEncapsulateComponent<DelegationUIService, DelegationUIServiceImpl> {

    /**
     * Default constructor
     */
    public DelegationUIServiceComponent() {
        super(DelegationUIService.class, new DelegationUIServiceImpl());
    }
}
