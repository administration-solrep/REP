package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.ReponsesSelectValueUIService;
import fr.dila.reponses.ui.services.impl.ReponsesSelectValueUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesSelectValueUIComponent
    extends ServiceEncapsulateComponent<ReponsesSelectValueUIService, ReponsesSelectValueUIServiceImpl> {

    public ReponsesSelectValueUIComponent() {
        super(ReponsesSelectValueUIService.class, new ReponsesSelectValueUIServiceImpl());
    }
}
