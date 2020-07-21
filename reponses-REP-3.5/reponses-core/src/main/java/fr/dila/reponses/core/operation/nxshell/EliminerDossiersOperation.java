package fr.dila.reponses.core.operation.nxshell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.Status;

import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Une opération pour éliminer les dossiers.
 */
@Operation(
        id = EliminerDossiersOperation.ID,
        category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
        label = "EliminerDossiers",
        description = "Elimine les dossiers d'une liste d'élimination")
public class EliminerDossiersOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Eliminer.Dossiers"; 

    /**
     * Logger.
     */
    private static final STLogger LOGGER = STLogFactory.getLog(EliminerDossiersOperation.class);
    
    
    private static final String QUERY_DOSSIERS_IN_LIST = "SELECT d.ecm:uuid as id FROM Dossier as d WHERE d.dos:listeElimination = ?";
	
	private static final String QUERY_COUNT_DOSSIER_IN_LIST = "select count(dossier_reponse.id) from DOSSIER_REPONSE where LISTEELIMINATION = ?";
    
    @Context
    protected CoreSession session;
    
    @Param(name = "idList", required = true)
	protected String idList;

	private DocumentRoutingService documentRoutingService;

	private JournalService journalService;

	private CorbeilleService corbeilleService;

    @OperationMethod
    public void run() throws Exception {
        
        LOGGER.info(session, ReponsesLogEnumImpl.INIT_OPERATION_ELIMINER_DOSSIER_TEC, "Début opération " + ID);

        if (!checkParameters(idList)) {
        	LOGGER.warn(session, ReponsesLogEnumImpl.FAIL_OPERATION_ELIMINER_DOSSIER_TEC, "Les paramètres reçus ne sont pas valides");
        	return;
        }
        
     // Chargement des services
        documentRoutingService = ReponsesServiceLocator.getDocumentRoutingService();
        journalService = STServiceLocator.getJournalService();
        corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        
        int nbDossiersTotal = getNbDossiersToDelete(idList);
        LOGGER.info(session, ReponsesLogEnumImpl.PROCESS_OPERATION_ELIMINER_DOSSIER_TEC, "Nombre de dossiers à supprimer : " + nbDossiersTotal);
        
        long limit = (long) 100;     
        int nbDossiersDeleted = 0;
        long borne = 0;
        
        List<String> params = new ArrayList<String>();
    	params.add(idList);
        
        for (Long offset = (long) 0; offset < nbDossiersTotal; offset += limit) {
        	if (!isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
        	borne = (nbDossiersTotal<offset+limit?nbDossiersTotal:offset+limit);
        	LOGGER.info(session, ReponsesLogEnumImpl.PROCESS_OPERATION_ELIMINER_DOSSIER_TEC, "Récupération dossiers de " + offset + " à " + borne);
        	List<DocumentModel> dossiersDocs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, DossierConstants.DOSSIER_DOCUMENT_TYPE,
        			QUERY_DOSSIERS_IN_LIST, params.toArray(), limit, 0);
        	
        	nbDossiersDeleted += deleteDossiers(dossiersDocs, nbDossiersDeleted);
        	LOGGER.info(session, ReponsesLogEnumImpl.PROCESS_OPERATION_ELIMINER_DOSSIER_TEC, "Fin traitement dossiers de " + offset + " à " + borne);
        }
        
        session.removeDocument(new IdRef(idList));
        session.save();
        LOGGER.info(session, ReponsesLogEnumImpl.PROCESS_OPERATION_ELIMINER_DOSSIER_TEC, "Liste " + idList + " supprimée");
        
        LOGGER.info(session, ReponsesLogEnumImpl.END_OPERATION_ELIMINER_DOSSIER_TEC, "Fin de l'opération" + ID);
        return;
    }
    
    private boolean checkParameters(String idList) throws ClientException {
    	
    	IdRef refList = new IdRef(idList);
    	if (session.exists(refList)) {
    		DocumentModel listDoc = session.getDocument(refList);
    		if (listDoc.getAdapter(ListeElimination.class).isEnCours()) {
    			LOGGER.warn(session, ReponsesLogEnumImpl.FAIL_OPERATION_ELIMINER_DOSSIER_TEC, "L'état de la liste (en cours), ne lui permet pas d'être supprimée");
    			return false;
    		}
    	} else {
    		LOGGER.warn(session, ReponsesLogEnumImpl.FAIL_OPERATION_ELIMINER_DOSSIER_TEC, "L'uid de la liste transmis en paramètre est incorrect : aucune liste n'a été retrouvée");
    		return false;
    	}
    	
    	return true;
    }
    
    private int getNbDossiersToDelete(String idList) throws ClientException {
    	
    	List<String> params = new ArrayList<String>();
    	params.add(idList);
    	
    	IterableQueryResult resCount = QueryUtils.doSqlQuery(session, new String[]{FlexibleQueryMaker.COL_COUNT}, QUERY_COUNT_DOSSIER_IN_LIST, params.toArray());
    	int countDossiers = 0;
    	if (resCount != null) {
	        Iterator<Map<String, Serializable>> iterator = resCount.iterator();
	        if (iterator.hasNext()) {
	        	Map<String, Serializable> row = iterator.next();
	        	countDossiers = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
	        }
	        resCount.close();
        }
    	return countDossiers;
    }    
    
    private int deleteDossiers(List<DocumentModel> dossiersDocs, int nbDossiersAlreadyDeleted) throws ClientException {
    	int nbDossiersDeleted = nbDossiersAlreadyDeleted;
    	for (DocumentModel doc : dossiersDocs) {            	
            try {
                // suppression des DossierLink
                List<DocumentModel> dossiersLink = corbeilleService.findDossierLinkUnrestricted(session, doc.getId());
                LOGGER.info(session, STLogEnumImpl.DEL_DL_TEC, dossiersLink);
                for (DocumentModel docLink : dossiersLink) {
                    session.removeDocument(docLink.getRef());
                }

                // suppression de la feuille de route
                List<DocumentRoute> routes = documentRoutingService.getDocumentRoutesForAttachedDocument(session, doc.getId());
                if (routes != null && !routes.isEmpty()) {
                    for (DocumentRoute route : routes) {
                    	List<String> attachedDocuments = route.getAttachedDocuments();
                    	while (attachedDocuments.contains(doc.getId())) {
                    		attachedDocuments.remove(doc.getId());
                    	}
                    	route.setAttachedDocuments(attachedDocuments);
                    	session.saveDocument(route.getDocument());
                    }
                }

                // suppression du Dossier
                LOGGER.info(session, STLogEnumImpl.DEL_DOSSIER_TEC, doc);
                session.removeDocument(doc.getRef());

                // Journalise l'action dans une session unrestricted
                journalService.journaliserActionAdministration(session, doc, STEventConstant.EVENT_ARCHIVAGE_DOSSIER, STEventConstant.COMMENT_ARCHIVAGE_DOSSIER);
            } catch (final Exception e) {
                throw new ClientException(e);
            }
            nbDossiersDeleted ++;
            if (nbDossiersDeleted % 20 == 0) {
            	session.save();
            	TransactionHelper.commitOrRollbackTransaction();
            	if (!isTransactionAlive()) {
                    TransactionHelper.startTransaction();
                }
            	LOGGER.info(session, ReponsesLogEnumImpl.PROCESS_OPERATION_ELIMINER_DOSSIER_TEC, nbDossiersDeleted + " dossiers supprimés");
            }
    	}
    	return nbDossiersDeleted;
    }
    
    private boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }
}
