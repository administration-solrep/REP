package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesMailboxService;
import fr.dila.reponses.core.service.ReponsesMailboxServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesMailboxComponent
    extends ServiceEncapsulateComponent<ReponsesMailboxService, ReponsesMailboxServiceImpl> {

    public ReponsesMailboxComponent() {
        super(ReponsesMailboxService.class, new ReponsesMailboxServiceImpl());
    }
}
