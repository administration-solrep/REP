package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.LegislatureService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class LegislatureComponent extends ServiceEncapsulateComponent<LegislatureService, LegislatureServiceImpl> {

    public LegislatureComponent() {
        super(LegislatureService.class, new LegislatureServiceImpl());
    }
}
