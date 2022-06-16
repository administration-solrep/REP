package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.RubriqueSenatService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RubriqueSenatComponent
    extends ServiceEncapsulateComponent<RubriqueSenatService, RubriqueSenatServiceImpl> {

    public RubriqueSenatComponent() {
        super(RubriqueSenatService.class, new RubriqueSenatServiceImpl());
    }
}
