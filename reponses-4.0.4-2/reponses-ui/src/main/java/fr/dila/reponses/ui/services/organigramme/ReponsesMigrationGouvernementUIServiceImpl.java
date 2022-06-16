package fr.dila.reponses.ui.services.organigramme;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.ReponsesHistoriqueMigrationDTO;
import fr.dila.reponses.ui.jaxrs.webobject.page.admin.ReponsesMigration;
import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.migration.MigrationInfo;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSChangementGouvernementService;
import fr.dila.ss.api.service.SSOrganigrammeService;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

public class ReponsesMigrationGouvernementUIServiceImpl
    extends SSMigrationGouvernementUIServiceImpl
    implements ReponsesMigrationGouvernementUIService {

    @Override
    public SSChangementGouvernementService getChangementGouvernementService() {
        return ReponsesServiceLocator.getReponsesChangementGouvernementService();
    }

    /**
     * Réponses : c'est un événement de migration d'entité qui est lancé en vue de l'interception par MigrationEntiteListener.
     */
    @Override
    protected void fireEvent(SpecificContext context, CoreSession session, SSPrincipal ssPrincipal) {
        EventProducer eventProducer = STServiceLocator.getEventProducer();
        Map<String, Serializable> eventProperties = new HashMap<>();
        eventProperties.put(SSEventConstant.MIGRATION_GVT_DATA, (ArrayList<MigrationInfo>) getMigrationsList(context));

        InlineEventContext eventContext = new InlineEventContext(session, ssPrincipal, eventProperties);
        eventProducer.fireEvent(eventContext.newEvent(ReponsesEventConstant.MIGRATION_ENTITE_EVENT));
    }

    @Override
    public List<ReponsesHistoriqueMigrationDTO> getListHistoriqueMigrationDTO() {
        return new ArrayList<>(
            getMigrationLoggerModelList()
                .stream()
                .map(this::toReponsesHistoriqueMigrationDTO)
                .sorted(Comparator.comparing(ReponsesHistoriqueMigrationDTO::getDateDebut).reversed())
                .collect(Collectors.toList())
        );
    }

    private ReponsesHistoriqueMigrationDTO toReponsesHistoriqueMigrationDTO(MigrationLoggerModel migrationLoggerModel) {
        ReponsesHistoriqueMigrationDTO reponsesHistoriqueMigrationDTO = new ReponsesHistoriqueMigrationDTO();
        toSSHistoriqueMigrationDTO(migrationLoggerModel, reponsesHistoriqueMigrationDTO);

        String directionsPilotes =
            migrationLoggerModel.getNorDossierClosCurrent() + "/" + migrationLoggerModel.getNorDossierClosCount();
        reponsesHistoriqueMigrationDTO.setDirectionPilote(directionsPilotes);

        return reponsesHistoriqueMigrationDTO;
    }

    @Override
    public Map<String, List<String>> getActions() {
        return ReponsesMigration.ACTIONS;
    }

    @Override
    protected SSOrganigrammeService getOrganigrammeService() {
        return ReponsesServiceLocator.getReponsesOrganigrammeService();
    }
}
