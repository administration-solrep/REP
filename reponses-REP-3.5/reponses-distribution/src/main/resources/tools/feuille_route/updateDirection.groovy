import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Status;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.feuilleroute.DocumentRouteTableElement;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.exception.PosteNotFoundException;

/**
 * script groovy de remplissage de direction pour les modele de feuille de route
 * 
 */
class DirectionUtils {    

	public static PosteNode getStepPoste(final STRouteStep step) throws ClientException, PosteNotFoundException {
        final String distributionMailboxId = step.getDistributionMailboxId();
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        final String posteId = mailboxPosteService.getPosteIdFromMailboxId(distributionMailboxId);
        final STPostesService postesService = STServiceLocator.getSTPostesService();
        final DocumentModel posteDoc = postesService.getPoste(posteId);
        final PosteNode poste = posteDoc.getAdapter(PosteNode.class);
        return poste;
    }
}

class TransactionUtils {
    public static boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }
}
    


    
print "Début du script groovy de remplissage de direction pour les modele de feuille de route";
print "-------------------------------------------------------------------------------";

    final FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();

    final String feuilleRouteModelFolderId = feuilleRouteModelService.getFeuilleRouteModelFolderId(Session);
    final STUsAndDirectionService dirService = STServiceLocator.getSTUsAndDirectionService();
    
    final String query = "SELECT f.ecm:uuid as id FROM FeuilleRoute as f WHERE f.ecm:parentId='" + feuilleRouteModelFolderId + "'";
    Long count = QueryUtils.doCountQuery(Session, QueryUtils.ufnxqlToFnxqlQuery(query), null);
    print count + " feuilles de route trouvees";
    long limit = (long) 100;

    for (Long offset = (long) 0; offset <= count; offset += limit) {
        Long borne = (count<offset+limit?count:offset+limit);
        print "Récupération feuilles de route de " + offset + " à " + borne;
        if (!TransactionUtils.isTransactionAlive()) {
            TransactionHelper.startTransaction();
        }
        List<DocumentModel> feuilleRouteModelList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(Session, query, null, limit, 0);
        for (DocumentModel feuilleRouteDoc : feuilleRouteModelList) {
            ReponsesFeuilleRoute fdr = feuilleRouteDoc.getAdapter(ReponsesFeuilleRoute.class);
            String ministere = fdr.getMinistere();
            final STFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(STFeuilleRoute.class);
            final List<DocumentRouteTableElement> listRouteTableElement = SSServiceLocator.getDocumentRoutingService().getFeuilleRouteElements(feuilleRoute, Session);
            boolean directionTrouve = false; 
            for (int i = listRouteTableElement.size() - 1; i >= 0; i--) {
                STRouteStep routeStep = listRouteTableElement.get(i).getRouteStep();
                if (STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeStep.getDocument().getType())) {                    
                     PosteNode poste = DirectionUtils.getStepPoste(routeStep);
                     List<OrganigrammeNode> listDirection = dirService.getDirectionFromPoste(poste.getId());

                     for (OrganigrammeNode direction : listDirection) {
                         UniteStructurelleNode us = (UniteStructurelleNode) direction;
                         List<String> ministeres = us.getEntiteParentList();
                         if (ministeres != null && ministeres.contains(ministere)) {
                             fdr.setIdDirectionPilote(direction.getId());
                             fdr.setIntituleDirectionPilote(direction.getLabel());
                             fdr.save(Session);
                             directionTrouve = true;
                             break;
                         }
                    }
                }
                if(directionTrouve) {
                    break;
                }
            }
        }
        TransactionHelper.commitOrRollbackTransaction();
        print "Fin traitement feuilles de route de " + offset + " à " + borne;
    }
    
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de remplissage de direction pour les modele de feuille de route";
return "Fin du script groovy";
