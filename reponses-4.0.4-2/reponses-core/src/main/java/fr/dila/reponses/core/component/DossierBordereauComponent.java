package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.DossierBordereauService;
import fr.dila.reponses.core.service.DossierBordereauServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DossierBordereauComponent
    extends ServiceEncapsulateComponent<DossierBordereauService, DossierBordereauServiceImpl> {

    public DossierBordereauComponent() {
        super(DossierBordereauService.class, new DossierBordereauServiceImpl());
    }
}
