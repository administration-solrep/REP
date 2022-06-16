package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.IndexActionService;
import fr.dila.reponses.ui.services.actions.impl.IndexActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class IndexActionComponent extends ServiceEncapsulateComponent<IndexActionService, IndexActionServiceImpl> {

    /**
     * Default constructor
     */
    public IndexActionComponent() {
        super(IndexActionService.class, new IndexActionServiceImpl());
    }
}
