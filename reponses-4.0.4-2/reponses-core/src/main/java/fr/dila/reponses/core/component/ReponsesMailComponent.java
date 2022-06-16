package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.core.service.ReponsesMailServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesMailComponent extends ServiceEncapsulateComponent<ReponsesMailService, ReponsesMailServiceImpl> {

    public ReponsesMailComponent() {
        super(ReponsesMailService.class, new ReponsesMailServiceImpl());
    }
}
