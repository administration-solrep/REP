package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.service.ReponseFeuilleRouteServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponseFeuilleRouteComponent
    extends ServiceEncapsulateComponent<ReponseFeuilleRouteService, ReponseFeuilleRouteServiceImpl> {

    public ReponseFeuilleRouteComponent() {
        super(ReponseFeuilleRouteService.class, new ReponseFeuilleRouteServiceImpl());
    }
}
