package fr.dila.reponses.core.operation.nxshell;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

/**
 * Une opération pour dénormaliser les libellés des étapes suivantes de feuille de route des dossiers
 * 
 * 
 */
@Operation(
        id = DenormalisationEtapeSuivanteOperation.ID,
        category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
        label = "DenormaliserEtapeSuivante",
        description = "Dénormalise les libellés des étapes suivantes de feuille de route pour les dossiers")
public class DenormalisationEtapeSuivanteOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Denormaliser.EtapeSuivante"; 

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(DenormalisationEtapeSuivanteOperation.class);
    
    @Context
    protected CoreSession session;

    @OperationMethod
    public void run() throws Exception {
        
        log.info("Début opération " + ID);

        final StatsService statsService = ReponsesServiceLocator.getStatsService();
        statsService.denormaliserEtapeSuivante(session);
        
        log.info("Fin de l'opération");
        return;
    }
}
