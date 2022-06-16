package fr.dila.reponses.ui.services.component;

import fr.dila.reponses.ui.services.DossierListUIService;
import fr.dila.reponses.ui.services.impl.DossierListUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DossierListUIComponent
    extends ServiceEncapsulateComponent<DossierListUIService, DossierListUIServiceImpl> {

    /**
     * Default constructor
     */
    public DossierListUIComponent() {
        super(DossierListUIService.class, new DossierListUIServiceImpl());
    }
}
