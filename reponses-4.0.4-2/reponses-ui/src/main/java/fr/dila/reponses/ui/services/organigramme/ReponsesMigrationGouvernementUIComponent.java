package fr.dila.reponses.ui.services.organigramme;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesMigrationGouvernementUIComponent
    extends ServiceEncapsulateComponent<ReponsesMigrationGouvernementUIService, ReponsesMigrationGouvernementUIServiceImpl> {

    public ReponsesMigrationGouvernementUIComponent() {
        super(ReponsesMigrationGouvernementUIService.class, new ReponsesMigrationGouvernementUIServiceImpl());
    }
}
