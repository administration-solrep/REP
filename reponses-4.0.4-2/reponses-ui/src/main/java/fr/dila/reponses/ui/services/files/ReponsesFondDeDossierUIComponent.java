package fr.dila.reponses.ui.services.files;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesFondDeDossierUIComponent
    extends ServiceEncapsulateComponent<ReponsesFondDeDossierUIService, ReponsesFondDeDossierUIServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesFondDeDossierUIComponent() {
        super(ReponsesFondDeDossierUIService.class, new ReponsesFondDeDossierUIServiceImpl());
    }
}
