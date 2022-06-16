package fr.dila.reponses.ui.services.actions.component;

import fr.dila.reponses.ui.services.files.ReponsesFondDeDossierUIService;
import fr.dila.reponses.ui.services.files.ReponsesFondDeDossierUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FondDeDossierManagerActionComponent
    extends ServiceEncapsulateComponent<ReponsesFondDeDossierUIService, ReponsesFondDeDossierUIServiceImpl> {

    public FondDeDossierManagerActionComponent() {
        super(ReponsesFondDeDossierUIService.class, new ReponsesFondDeDossierUIServiceImpl());
    }
}
