package fr.dila.reponses.core.component;

import fr.dila.reponses.core.service.FondDeDossierServiceImpl;
import fr.dila.ss.api.service.SSFondDeDossierService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSFondDeDossierComponent
    extends ServiceEncapsulateComponent<SSFondDeDossierService, FondDeDossierServiceImpl> {

    public SSFondDeDossierComponent() {
        super(SSFondDeDossierService.class, new FondDeDossierServiceImpl());
    }
}
