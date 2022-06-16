package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.bean.ReponsesHistoriqueMigrationDTO;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.jaxrs.webobject.ajax.SSHistoriqueMigrationAjax;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIService;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.List;
import javax.ws.rs.GET;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "HistoriqueMigrationAjax")
public class ReponsesHistoriqueMigrationAjax extends SSHistoriqueMigrationAjax {

    @GET
    @Override
    public ThTemplate getHistorique() {
        template.getData().put(SSTemplateConstants.MIGRATIONS, getMigrations());
        return template;
    }

    @Override
    protected SSMigrationGouvernementUIService getMigrationGouvernementUIService() {
        return ReponsesUIServiceLocator.getReponsesMigrationGouvernementUIService();
    }

    @Override
    public String getTableCaption() {
        List<ReponsesHistoriqueMigrationDTO> migrations = ReponsesUIServiceLocator
            .getReponsesMigrationGouvernementUIService()
            .getListHistoriqueMigrationDTO();

        StringBuilder sbTableCaption = new StringBuilder(ResourceHelper.getString("historique.migration.title"));
        sbTableCaption.append(" - ");
        sbTableCaption.append(ResourceHelper.getString("historique.nb.migrations(" + migrations.size() + ")"));

        return sbTableCaption.toString();
    }

    /**
     * Gets migrations
     *
     * @return the list of migrations
     */
    private List<ReponsesHistoriqueMigrationDTO> getMigrations() {
        return ReponsesUIServiceLocator.getReponsesMigrationGouvernementUIService().getListHistoriqueMigrationDTO();
    }
}
