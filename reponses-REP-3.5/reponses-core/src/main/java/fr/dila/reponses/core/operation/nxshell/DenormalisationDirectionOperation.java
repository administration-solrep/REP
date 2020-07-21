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
 * Une opération pour dénormaliser les libellés des directions des étapes de feuille de route passées
 * 
 * @author bgamard
 */
@Operation(
        id = DenormalisationDirectionOperation.ID,
        category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
        label = "DenormaliserDirection",
        description = "Dénormalise les libellés des directions sur les étapes de feuille de route validées")
public class DenormalisationDirectionOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Denormaliser.Direction"; 

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(DenormalisationDirectionOperation.class);
    
    @Context
    protected CoreSession session;

    @OperationMethod
    public void run() throws Exception {
        
        log.info("Début opération " + ID);

        final StatsService statsService = ReponsesServiceLocator.getStatsService();
        statsService.denormaliserDirection(session);
        
        log.info("Fin de l'opération");
        return;
    }
}
