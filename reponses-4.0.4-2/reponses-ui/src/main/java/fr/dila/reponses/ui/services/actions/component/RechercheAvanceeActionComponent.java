package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.actions.RechercheAvanceeActionService;
import fr.dila.reponses.ui.services.actions.impl.RechercheAvanceeActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RechercheAvanceeActionComponent
    extends ServiceEncapsulateComponent<RechercheAvanceeActionService, RechercheAvanceeActionServiceImpl> {

    public RechercheAvanceeActionComponent() {
        super(RechercheAvanceeActionService.class, new RechercheAvanceeActionServiceImpl());
    }
}
