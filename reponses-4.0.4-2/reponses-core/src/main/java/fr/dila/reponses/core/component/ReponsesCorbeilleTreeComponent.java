package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesCorbeilleTreeService;
import fr.dila.reponses.core.service.ReponsesCorbeilleTreeServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesCorbeilleTreeComponent
    extends ServiceEncapsulateComponent<ReponsesCorbeilleTreeService, ReponsesCorbeilleTreeServiceImpl> {

    public ReponsesCorbeilleTreeComponent() {
        super(ReponsesCorbeilleTreeService.class, new ReponsesCorbeilleTreeServiceImpl());
    }
}
