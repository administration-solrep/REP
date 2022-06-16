package fr.dila.reponses.ui.jaxrs.webobject.page.admin;

import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.reponses.ui.th.model.ReponsesUtilisateurTemplate;
import fr.dila.ss.ui.jaxrs.webobject.page.admin.SSHistoriqueMigration;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIService;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "HistoriqueMigration")
public class ReponsesHistoriqueMigration extends SSHistoriqueMigration {

    @Override
    protected ThTemplate getMyTemplate(SpecificContext context) {
        if (context.getWebcontext().getPrincipal().isMemberOf(STBaseFunctionConstant.ESPACE_ADMINISTRATION_READER)) {
            return new ReponsesAdminTemplate();
        } else {
            return new ReponsesUtilisateurTemplate();
        }
    }

    @Override
    protected SSMigrationGouvernementUIService getMigrationGouvernementUIService() {
        return ReponsesUIServiceLocator.getReponsesMigrationGouvernementUIService();
    }
}
