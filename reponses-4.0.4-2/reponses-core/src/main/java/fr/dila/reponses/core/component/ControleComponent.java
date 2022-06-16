package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ControleService;
import fr.dila.reponses.core.service.ControleServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ControleComponent extends ServiceEncapsulateComponent<ControleService, ControleServiceImpl> {

    public ControleComponent() {
        super(ControleService.class, new ControleServiceImpl());
    }
}
