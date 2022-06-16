package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.PdfDossierService;
import fr.dila.reponses.core.service.PdfDossierServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class PdfDossierComponent extends ServiceEncapsulateComponent<PdfDossierService, PdfDossierServiceImpl> {

    public PdfDossierComponent() {
        super(PdfDossierService.class, new PdfDossierServiceImpl());
    }
}
