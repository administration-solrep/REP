package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesMigrationService;
import fr.dila.reponses.core.service.ReponsesMigrationServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesMigrationComponent
    extends ServiceEncapsulateComponent<ReponsesMigrationService, ReponsesMigrationServiceImpl> {

    public ReponsesMigrationComponent() {
        super(ReponsesMigrationService.class, new ReponsesMigrationServiceImpl());
    }
}
