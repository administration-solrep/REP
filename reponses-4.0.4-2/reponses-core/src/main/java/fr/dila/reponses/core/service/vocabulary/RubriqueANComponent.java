package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.RubriqueANService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RubriqueANComponent extends ServiceEncapsulateComponent<RubriqueANService, RubriqueANServiceImpl> {

    public RubriqueANComponent() {
        super(RubriqueANService.class, new RubriqueANServiceImpl());
    }
}
