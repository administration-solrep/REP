package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ExtractionService;
import fr.dila.reponses.core.service.ExtractionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ExtractionComponent extends ServiceEncapsulateComponent<ExtractionService, ExtractionServiceImpl> {

    public ExtractionComponent() {
        super(ExtractionService.class, new ExtractionServiceImpl());
    }
}
