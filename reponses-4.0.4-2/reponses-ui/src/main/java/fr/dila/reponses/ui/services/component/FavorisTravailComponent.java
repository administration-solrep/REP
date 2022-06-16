package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.FavorisTravailComponentService;
import fr.dila.reponses.ui.services.impl.FavorisTravailComponentServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FavorisTravailComponent
    extends ServiceEncapsulateComponent<FavorisTravailComponentService, FavorisTravailComponentServiceImpl> {

    /**
     * Default constructor
     */
    public FavorisTravailComponent() {
        super(FavorisTravailComponentService.class, new FavorisTravailComponentServiceImpl());
    }
}
