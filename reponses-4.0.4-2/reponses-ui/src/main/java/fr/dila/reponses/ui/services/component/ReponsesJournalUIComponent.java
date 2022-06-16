package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.impl.ReponsesJournalUIServiceImpl;
import fr.dila.ss.ui.services.SSJournalUIService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesJournalUIComponent
    extends ServiceEncapsulateComponent<SSJournalUIService, ReponsesJournalUIServiceImpl> {

    public ReponsesJournalUIComponent() {
        super(SSJournalUIService.class, new ReponsesJournalUIServiceImpl());
    }
}
