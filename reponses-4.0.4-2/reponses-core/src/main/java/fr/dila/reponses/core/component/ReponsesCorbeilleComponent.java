package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.core.service.ReponsesCorbeilleServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesCorbeilleComponent
    extends ServiceEncapsulateComponent<ReponsesCorbeilleService, ReponsesCorbeilleServiceImpl> {

    public ReponsesCorbeilleComponent() {
        super(ReponsesCorbeilleService.class, new ReponsesCorbeilleServiceImpl());
    }
}
