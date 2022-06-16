package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.FavorisUIService;
import fr.dila.reponses.ui.services.impl.FavorisUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FavorisUIComponent extends ServiceEncapsulateComponent<FavorisUIService, FavorisUIServiceImpl> {

    public FavorisUIComponent() {
        super(FavorisUIService.class, new FavorisUIServiceImpl());
    }
}
