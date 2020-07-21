package fr.dila.reponses.core.event.batch;

import java.util.List;

import javax.activation.DataSource;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.core.util.ExcelUtil;

/**
 * Surcharge du batch des dossiers bloqués de solrep. 
 *
 */
public class BlockedRouteAlertListener extends fr.dila.ss.core.event.batch.BlockedRouteAlertBatchListener {

	private static final String QUERY_DOSSIERS_BLOQUES = "SELECT id FROM dossier_reponse dos WHERE dos.id IN (SELECT id from misc where misc.lifecyclestate = 'running') "
    			+ " AND dos.lastdocumentroute IN (SELECT documentrouteid FROM routing_task r1 WHERE r1.id IN (SELECT id FROM misc WHERE misc.lifecyclestate = 'ready'))"
    			+ " AND dos.lastdocumentroute NOT IN (SELECT documentrouteid FROM routing_task r2 WHERE r2.id IN (SELECT id FROM misc where misc.lifecyclestate = 'running'))";
	
    public BlockedRouteAlertListener() {
        super();
    }

    @Override
    protected DataSource generateData(CoreSession session, List<String> idsDossiersDoc) {
        DataSource fichierExcelResultat = null;
        fichierExcelResultat = ExcelUtil.creationListDossierExcelFromIds(session, idsDossiersDoc);
        return fichierExcelResultat;
    }
    
    @Override
    protected String getQuery() {
    	return QUERY_DOSSIERS_BLOQUES;
    }
}
