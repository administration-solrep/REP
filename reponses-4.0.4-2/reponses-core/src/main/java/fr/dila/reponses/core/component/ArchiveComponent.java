package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.core.service.ArchiveServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ArchiveComponent extends ServiceEncapsulateComponent<ArchiveService, ArchiveServiceImpl> {

    public ArchiveComponent() {
        super(ArchiveService.class, new ArchiveServiceImpl());
    }
}
