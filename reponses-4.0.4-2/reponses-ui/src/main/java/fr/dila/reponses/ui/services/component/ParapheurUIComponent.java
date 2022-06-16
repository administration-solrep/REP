package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.ParapheurUIService;
import fr.dila.reponses.ui.services.impl.ParapheurUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ParapheurUIComponent extends ServiceEncapsulateComponent<ParapheurUIService, ParapheurUIServiceImpl> {

    /**
     * Default constructor
     */
    public ParapheurUIComponent() {
        super(ParapheurUIService.class, new ParapheurUIServiceImpl());
    }
}
