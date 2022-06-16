package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.ComparateurActionService;
import fr.dila.reponses.ui.services.actions.impl.ComparateurActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ComparateurActionComponent
    extends ServiceEncapsulateComponent<ComparateurActionService, ComparateurActionServiceImpl> {

    /**
     * Default constructor
     */
    public ComparateurActionComponent() {
        super(ComparateurActionService.class, new ComparateurActionServiceImpl());
    }
}
