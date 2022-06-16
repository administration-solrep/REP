package fr.dila.reponses.core.event;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.service.SSChangementGouvernementService;
import fr.dila.ss.core.event.SSAbstractSendMigrationDetailsListener;

public class ReponsesSendMigrationDetailsListener extends SSAbstractSendMigrationDetailsListener {

    @Override
    protected SSChangementGouvernementService getChangementGouvernementService() {
        return ReponsesServiceLocator.getReponsesChangementGouvernementService();
    }

    @Override
    protected String getObjectPrefix() {
        return "[Réponses]";
    }
}
