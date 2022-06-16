package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.ReponsesMailboxListComponentService;
import fr.dila.reponses.ui.services.impl.ReponsesMailboxListComponentServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesMailboxListComponentComponent
    extends ServiceEncapsulateComponent<ReponsesMailboxListComponentService, ReponsesMailboxListComponentServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesMailboxListComponentComponent() {
        super(ReponsesMailboxListComponentService.class, new ReponsesMailboxListComponentServiceImpl());
    }
}
