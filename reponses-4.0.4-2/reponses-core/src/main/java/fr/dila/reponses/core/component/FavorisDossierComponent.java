package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.reponses.core.service.FavorisDossierServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FavorisDossierComponent
    extends ServiceEncapsulateComponent<FavorisDossierService, FavorisDossierServiceImpl> {

    public FavorisDossierComponent() {
        super(FavorisDossierService.class, new FavorisDossierServiceImpl());
    }
}
