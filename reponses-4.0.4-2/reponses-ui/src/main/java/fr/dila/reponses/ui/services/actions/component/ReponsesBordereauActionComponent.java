package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.ReponsesBordereauActionService;
import fr.dila.reponses.ui.services.actions.impl.ReponsesBordereauActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesBordereauActionComponent
    extends ServiceEncapsulateComponent<ReponsesBordereauActionService, ReponsesBordereauActionServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesBordereauActionComponent() {
        super(ReponsesBordereauActionService.class, new ReponsesBordereauActionServiceImpl());
    }
}
