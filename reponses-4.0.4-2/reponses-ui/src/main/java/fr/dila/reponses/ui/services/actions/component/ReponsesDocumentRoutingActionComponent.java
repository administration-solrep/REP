package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.ReponsesDocumentRoutingActionService;
import fr.dila.reponses.ui.services.actions.impl.ReponsesDocumentRoutingActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesDocumentRoutingActionComponent
    extends ServiceEncapsulateComponent<ReponsesDocumentRoutingActionService, ReponsesDocumentRoutingActionServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesDocumentRoutingActionComponent() {
        super(ReponsesDocumentRoutingActionService.class, new ReponsesDocumentRoutingActionServiceImpl());
    }
}
