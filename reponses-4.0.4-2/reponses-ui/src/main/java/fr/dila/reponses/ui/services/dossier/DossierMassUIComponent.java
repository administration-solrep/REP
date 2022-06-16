package fr.dila.reponses.ui.services.dossier;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DossierMassUIComponent
    extends ServiceEncapsulateComponent<DossierMassUIService, DossierMassUIServiceImpl> {

    public DossierMassUIComponent() {
        super(DossierMassUIService.class, new DossierMassUIServiceImpl());
    }
}
