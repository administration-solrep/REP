package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.ReponsesModeleFdrListUIService;
import fr.dila.reponses.ui.services.impl.ReponsesModeleFdrListUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesModeleFdrListUIComponent
    extends ServiceEncapsulateComponent<ReponsesModeleFdrListUIService, ReponsesModeleFdrListUIServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesModeleFdrListUIComponent() {
        super(ReponsesModeleFdrListUIService.class, new ReponsesModeleFdrListUIServiceImpl());
    }
}
