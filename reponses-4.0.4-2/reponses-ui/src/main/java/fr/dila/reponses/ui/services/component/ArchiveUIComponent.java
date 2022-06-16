package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.ArchiveUIService;
import fr.dila.reponses.ui.services.impl.ReponsesArchiveUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ArchiveUIComponent extends ServiceEncapsulateComponent<ArchiveUIService, ReponsesArchiveUIServiceImpl> {

    /**
     * Default constructor
     */
    public ArchiveUIComponent() {
        super(ArchiveUIService.class, new ReponsesArchiveUIServiceImpl());
    }
}
