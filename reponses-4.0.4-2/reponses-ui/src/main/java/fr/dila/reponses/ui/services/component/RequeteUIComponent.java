package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.RequeteUIService;
import fr.dila.reponses.ui.services.impl.RequeteUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RequeteUIComponent extends ServiceEncapsulateComponent<RequeteUIService, RequeteUIServiceImpl> {

    /**
     * Default constructor
     */
    public RequeteUIComponent() {
        super(RequeteUIService.class, new RequeteUIServiceImpl());
    }
}
