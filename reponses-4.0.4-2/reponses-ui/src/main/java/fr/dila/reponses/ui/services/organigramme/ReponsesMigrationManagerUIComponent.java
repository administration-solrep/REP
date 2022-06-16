package fr.dila.reponses.ui.services.organigramme;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesMigrationManagerUIComponent
    extends ServiceEncapsulateComponent<ReponsesMigrationManagerUIService, ReponsesMigrationManagerUIServiceImpl> {

    /**
     * Default constructor
     */
    public ReponsesMigrationManagerUIComponent() {
        super(ReponsesMigrationManagerUIService.class, new ReponsesMigrationManagerUIServiceImpl());
    }
}
