package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.ReponseActionService;
import fr.dila.reponses.ui.services.actions.impl.ReponseActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponseActionComponent
    extends ServiceEncapsulateComponent<ReponseActionService, ReponseActionServiceImpl> {

    public ReponseActionComponent() {
        super(ReponseActionService.class, new ReponseActionServiceImpl());
    }
}
