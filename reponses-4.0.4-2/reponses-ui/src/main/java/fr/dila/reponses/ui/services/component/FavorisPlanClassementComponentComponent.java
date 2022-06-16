package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.FavorisPlanClassementComponentService;
import fr.dila.reponses.ui.services.impl.FavorisPlanClassementComponentServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FavorisPlanClassementComponentComponent
    extends ServiceEncapsulateComponent<FavorisPlanClassementComponentService, FavorisPlanClassementComponentServiceImpl> {

    /**
     * Default constructor
     */
    public FavorisPlanClassementComponentComponent() {
        super(FavorisPlanClassementComponentService.class, new FavorisPlanClassementComponentServiceImpl());
    }
}
