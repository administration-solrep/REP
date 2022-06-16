package fr.dila.reponses.ui.services.organigramme;

import fr.dila.reponses.ui.bean.ReponsesHistoriqueMigrationDTO;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIService;
import java.util.List;

public interface ReponsesMigrationGouvernementUIService extends SSMigrationGouvernementUIService {
    /**
     * Renvoie l'historique des migrations au complet.
     * @return une liste d'objets ReponsesHistoriqueMigrationDTO
     */
    List<ReponsesHistoriqueMigrationDTO> getListHistoriqueMigrationDTO();
}
