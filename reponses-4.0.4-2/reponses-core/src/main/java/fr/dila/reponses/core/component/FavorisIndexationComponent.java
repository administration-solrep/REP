package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.FavorisIndexationService;
import fr.dila.reponses.core.service.FavorisIndexationServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FavorisIndexationComponent
    extends ServiceEncapsulateComponent<FavorisIndexationService, FavorisIndexationServiceImpl> {

    public FavorisIndexationComponent() {
        super(FavorisIndexationService.class, new FavorisIndexationServiceImpl());
    }
}
