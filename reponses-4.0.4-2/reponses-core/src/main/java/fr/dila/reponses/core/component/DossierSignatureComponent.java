package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.DossierSignatureService;
import fr.dila.reponses.core.service.DossierSignatureServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class DossierSignatureComponent
    extends ServiceEncapsulateComponent<DossierSignatureService, DossierSignatureServiceImpl> {

    public DossierSignatureComponent() {
        super(DossierSignatureService.class, new DossierSignatureServiceImpl());
    }
}
