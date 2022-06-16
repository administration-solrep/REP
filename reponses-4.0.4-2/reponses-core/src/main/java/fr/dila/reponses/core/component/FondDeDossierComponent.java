package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.core.service.FondDeDossierServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FondDeDossierComponent
    extends ServiceEncapsulateComponent<FondDeDossierService, FondDeDossierServiceImpl> {

    public FondDeDossierComponent() {
        super(FondDeDossierService.class, new FondDeDossierServiceImpl());
    }
}
