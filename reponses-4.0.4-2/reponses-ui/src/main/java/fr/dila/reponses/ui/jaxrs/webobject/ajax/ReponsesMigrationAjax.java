package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.jaxrs.webobject.page.admin.ReponsesMigration;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.jaxrs.webobject.ajax.SSMigrationAjax;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIService;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "MigrationAjax")
public class ReponsesMigrationAjax extends SSMigrationAjax {

    public ReponsesMigrationAjax() {
        super();
    }

    @Override
    public Map<String, String> getMigrationTypes() {
        return ReponsesMigration.MIGRATION_TYPES;
    }

    @Override
    public Map<String, List<String>> getActions() {
        return ReponsesMigration.ACTIONS;
    }

    @Override
    protected SSMigrationGouvernementUIService getMigrationGouvernementUIService() {
        return ReponsesUIServiceLocator.getReponsesMigrationGouvernementUIService();
    }
}
