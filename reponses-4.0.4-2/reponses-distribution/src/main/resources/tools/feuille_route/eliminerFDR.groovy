import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.transaction.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import fr.dila.cm.cases.CaseConstants;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.flux.QuestionStateChange
import fr.dila.reponses.api.cases.flux.Renouvellement
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesParametreConstant
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.service.SSArchiveServiceImpl;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.StringUtil;

/**
 * Script groovy pour supprimer des feuilles de route orphelines
 *  
 */
class TransactionUtils {
    public static boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }
}

class FixDossierUtils {
	
	/* 
	 * ################### VARIABLES DE FIN DE SCRIPT ############################ 
	 */
	private static final int HEURE_FIN_SEMAINE = 7;
	private static final int MINUTE_FIN_SEMAINE = 50;
	private static final int HEURE_FIN_WEEKEND = 20;
	private static final int MINUTE_FIN_WEEKEND = 0;
	
	private static final String QUERY_ORPHANS_FDR = "select ID from hierarchy where hierarchy.ID in (select id from feuille_route where id not in (select distinct(id) from docri_participatingdocuments)) and parentid != '02bdd5d2-8fcb-4961-b60b-27d3903e38e5'";
	
	private static final String QUERY_COUNT_ORPHANS_FDR = "select count(ID) from hierarchy where hierarchy.ID in (select id from feuille_route where id not in (select distinct(id) from docri_participatingdocuments)) and parentid != '02bdd5d2-8fcb-4961-b60b-27d3903e38e5'";
	
    public static boolean elimination (session) {
    	if (!TransactionUtils.isTransactionAlive()) {
            TransactionHelper.startTransaction();
        }
    	
    	// Initialisation 
    	Calendar today = Calendar.getInstance();
    	int dayOfWeek = today.get(Calendar.DAY_OF_WEEK); 
    	Calendar finScriptDate = Calendar.getInstance();    	
    	
    		// Si on est en week end
    	if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
    		finScriptDate.set(Calendar.HOUR_OF_DAY, HEURE_FIN_WEEKEND);
        	finScriptDate.set(Calendar.MINUTE, MINUTE_FIN_WEEKEND);
    	} else {
    		// Si on est en semaine
    		finScriptDate.set(Calendar.HOUR_OF_DAY, HEURE_FIN_SEMAINE);
        	finScriptDate.set(Calendar.MINUTE, MINUTE_FIN_SEMAINE);
    	}
    	
    	final STLogger LOGGER = STLogFactory.getLog(SSArchiveServiceImpl.class);
    	Object[] params = [];
    	String[] colsCount = [FlexibleQueryMaker.COL_COUNT];
    	String[] colsId = [FlexibleQueryMaker.COL_ID];
    	
    	IterableQueryResult resCount = QueryUtils.doSqlQuery(session, colsCount, QUERY_COUNT_ORPHANS_FDR, params);
    	int countFdr = 0;
    	if (resCount != null) {
	        Iterator<Map<String, Serializable>> iterator = resCount.iterator();
	        if (iterator.hasNext()) {
	        	Map<String, Serializable> row = iterator.next();
	        	countFdr = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
	        }
	        resCount.close();
        }
    	
        print countFdr + " feuilles de route à supprimer";
        long limit = (long) 10;     
        int nbFdrDeleted = 0;
        int borne = 0;
        
        for (Long offset = (long) 0; offset < countFdr; offset += limit) {        	
        	if (Calendar.getInstance().after(finScriptDate)) {
        		// On a atteint la date de fin du script, on sort de la boucle for
        		print "Date de fin de script atteinte";
        		break; 
        	}
        	if (!TransactionUtils.isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
        	
        	borne = (countFdr<offset+limit?countFdr:offset+limit);
        	
        	print "Récupération feuille de route de " + offset + " à " + borne;
        	IterableQueryResult resFeuilleRouteDocs = QueryUtils.doSqlQuery(session, colsId,
        			QUERY_ORPHANS_FDR, params, limit, 0);
        	
        	if (resFeuilleRouteDocs != null) {
    	        Iterator<Map<String, Serializable>> iterator = resFeuilleRouteDocs.iterator();
    	        while (iterator.hasNext()) {    	        	
    	        	Map<String, Serializable> row = iterator.next();
    	        	String idFdr = (String) row.get(FlexibleQueryMaker.COL_ID);
    	        	try {
                    	LOGGER.info(session, SSLogEnumImpl.DEL_FDR_TEC, idFdr);
                    	session.removeDocument(new IdRef(idFdr));
                    } catch (final Exception e) {
                        throw new ClientException(e);
                    }
                    nbFdrDeleted ++;                    
    	        }
    	        resFeuilleRouteDocs.close();
            }        	
        	session.save();
        	TransactionHelper.commitOrRollbackTransaction();
        	if (!TransactionUtils.isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
        	
        	print "Fin traitement dossiers de " + offset + " à " + borne;
        }
        TransactionHelper.commitOrRollbackTransaction();
        if (!TransactionUtils.isTransactionAlive()) {
            TransactionHelper.startTransaction();
        }
        print nbFdrDeleted + " feuilles de route supprimées";
        
        if (nbFdrDeleted == countFdr) {
        	print "################################ TOUTES LES FEUILLES DE ROUTE ORPHELINES ONT ETE SUPPRIMEES ######################################";
        } else {
        	print "######################## IL RESTE DES FEUILLES DE ROUTE A SUPPRIMER #############################";
        }
        
		return true;
    }
}

print "Début script groovy élimination FDR";
print "-------------------------------------------------------------------------------";


if (!FixDossierUtils.elimination (Session)) {
    print "-------------------------------------------------------------------------------";
    print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
    return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
}
 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy élimination FDR";
return "Fin du script groovy ";


