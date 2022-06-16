package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.FavorisDossierUIService;
import fr.dila.reponses.ui.services.impl.FavorisDossierUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FavorisDossierUIComponent
    extends ServiceEncapsulateComponent<FavorisDossierUIService, FavorisDossierUIServiceImpl> {

    public FavorisDossierUIComponent() {
        super(FavorisDossierUIService.class, new FavorisDossierUIServiceImpl());
    }
}
