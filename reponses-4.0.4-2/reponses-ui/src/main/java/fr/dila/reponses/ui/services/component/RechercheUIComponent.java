package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.RechercheUIService;
import fr.dila.reponses.ui.services.impl.RechercheUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RechercheUIComponent extends ServiceEncapsulateComponent<RechercheUIService, RechercheUIServiceImpl> {

    /**
     * Default constructor
     */
    public RechercheUIComponent() {
        super(RechercheUIService.class, new RechercheUIServiceImpl());
    }
}
