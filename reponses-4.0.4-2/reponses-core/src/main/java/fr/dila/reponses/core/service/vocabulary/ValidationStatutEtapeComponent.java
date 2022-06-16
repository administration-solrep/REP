package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.ValidationStatutEtapeService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ValidationStatutEtapeComponent
    extends ServiceEncapsulateComponent<ValidationStatutEtapeService, ValidationStatutEtapeServiceImpl> {

    public ValidationStatutEtapeComponent() {
        super(ValidationStatutEtapeService.class, new ValidationStatutEtapeServiceImpl());
    }
}
