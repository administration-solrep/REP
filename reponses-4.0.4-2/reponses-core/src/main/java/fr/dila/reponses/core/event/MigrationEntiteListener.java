package fr.dila.reponses.core.event;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.service.SSChangementGouvernementService;
import fr.dila.ss.core.event.SSAbstractMigrationGouvernementListener;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;

public class MigrationEntiteListener extends SSAbstractMigrationGouvernementListener {

    @Override
    public void handleEvent(final EventBundle events) {
        if (events.containsEventName(ReponsesEventConstant.MIGRATION_ENTITE_EVENT)) {
            for (final Event event : events) {
                handleEvent(event);
            }
        }
    }

    @Override
    protected void handleEvent(final Event event) {
        if (!event.getName().equals(ReponsesEventConstant.MIGRATION_ENTITE_EVENT)) {
            return;
        }

        coreSession = event.getContext().getCoreSession();

        initServices();

        execMigrationOrganigramme(event, coreSession);
    }

    @Override
    protected SSChangementGouvernementService initChangementGouvernementService() {
        return ReponsesServiceLocator.getReponsesChangementGouvernementService();
    }

    @Override
    protected void initServices() {
        super.initServices();

        feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
    }

    @Override
    protected OrganigrammeService initOrganigrammeService() {
        return ReponsesServiceLocator.getReponsesOrganigrammeService();
    }
}
