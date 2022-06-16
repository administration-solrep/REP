package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.impl.DossierUIServiceImpl;
import fr.dila.ss.ui.services.SSDossierUIService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

@SuppressWarnings("rawtypes")
public class DossierUIComponent extends ServiceEncapsulateComponent<SSDossierUIService, DossierUIServiceImpl> {

    /**
     * Default constructor
     */
    public DossierUIComponent() {
        super(SSDossierUIService.class, new DossierUIServiceImpl());
    }
}
