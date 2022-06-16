package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponseServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponseComponent extends ServiceEncapsulateComponent<ReponseService, ReponseServiceImpl> {

    public ReponseComponent() {
        super(ReponseService.class, new ReponseServiceImpl());
    }
}
