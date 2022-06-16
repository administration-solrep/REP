package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.core.service.DocumentRoutingServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DocumentRoutingComponent
    extends ServiceEncapsulateComponent<DocumentRoutingService, DocumentRoutingServiceImpl> {

    public DocumentRoutingComponent() {
        super(DocumentRoutingService.class, new DocumentRoutingServiceImpl());
    }
}
