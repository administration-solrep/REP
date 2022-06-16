package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.impl.ReponsesHeaderServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.HeaderService;

public class ReponsesHeaderComponent extends ServiceEncapsulateComponent<HeaderService, ReponsesHeaderServiceImpl> {

    public ReponsesHeaderComponent() {
        super(HeaderService.class, new ReponsesHeaderServiceImpl());
    }
}
