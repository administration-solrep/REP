import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import fr.dila.cm.cases.CaseConstants;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.constant.ReponsesParametreConstant
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Script groovy pour réparer un dossier qui a vu sa validation être interrompue en milieu d'execution (caselink à l'état "done", mais étape "running")
 * 
 * Paramètre :
 * - question : question du dossier (obligatoire)
 * - legislature : legislature correspondante à la question (facultatif)
 */
class FixDossierUtils {    

	/**
	 * Retourne le document d'un dossier par son numéro de question et numéro de legislature
	 * 
	 * @return DocumentModel Dossierdoc correspondant aux paramètres de question, null si non trouvé
	 */
	public static DocumentModel getDossierDocBySourceQuestion (session, question, legislature) {
		String queryQuestion = "SELECT * FROM Question WHERE qu:sourceNumeroQuestion='" + question + "' AND qu:legislatureQuestion = " + legislature + " AND qu:typeQuestion = 'QE'";
		DocumentModelList listQuestion = session.query(queryQuestion);
		if(listQuestion == null || listQuestion.isEmpty()) {
			print "Pas de question correspondant aux paramètres passés";
			return null;
		}
		if (listQuestion.size() >1) {
			print  "Plusieurs questions correspondant aux paramètres passés. ";
			return null;
		}
		
		Question quest = listQuestion.get(0).getAdapter(Question.class);		
		return session.getDocument(quest.getDossierRef());
	}

	/**
	 * Retourne le DocumentModelList des dossier link par l'id du dossier
	 * 
	 * @return DocumentModelList DossierLink correspondants à l'id en parametre, null si non trouvé
	 */
	public static DocumentModelList getDossiersLinkDoneListByIdDossier (session, idDossier) {
		String queryDossierLink = "SELECT * FROM DossierLink WHERE cslk:caseDocumentId='" + idDossier + "' and ecm:currentLifeCycleState = 'done'";
		DocumentModelList listDossierLink = session.query(queryDossierLink);
		if(listDossierLink == null || listDossierLink.isEmpty()) {
			print "Pas de dossier link correspondants à l'id passé";
			return null;
		}
		return listDossierLink;
	}

	/**
	 * Récupère la liste des étapes en cours d'une feuille de route
	 * @param routeId id de la route dont on souhaite récupérer les étapes en cours
	 * @return DocumentModelList liste des étapes en cours, null si non trouvée, ou aucune
	 */
	public static DocumentModelList getRunningStepForRouteId (session, routeId) {
		String queryRoute = "SELECT * FROM RouteStep WHERE rtsk:documentRouteId='" + routeId + "' and ecm:currentLifeCycleState = 'running'";
		DocumentModelList listRunningStep = session.query(queryRoute);
		if(listRunningStep == null || listRunningStep.isEmpty()) {
			print "Pas d'étape en cours pour à l'id de route passé : " + routeId;
			return null;
		}
		return listRunningStep;
	}

	/**
	 * Supprime un caseLink en utilisant son id
	 * @param caseLinkId id du caseLink a supprimer
	 * 
	 */
	public static void removeCaseLinkById (session, caseLinkId) {
		print "Suppression caseLink id : " + caseLinkId;
		session.removeDocument(new IdRef(caseLinkId));
	}

	/**
	 * Passe une étape de feuille de route de l'état running à l'état done
	 * @param stepId id de l'étape à modifier
	 *
	 */
	public static void setRunningStepToDone (session, stepId) {
		print "Passage d'étape de running à done pour étape id : " + stepId;
		session.followTransition(new IdRef(stepId), "toDone")
	}

	/*
	 * Redémarre une feuille de route
	 * @param id id de la feuille de route à redémarrer
	 *
	 */
	public static void restartFeuilleRoute (session, routeId) {
		DocumentRoute startRoute = null;
		DocumentModel startRouteDoc = session.getDocument(new IdRef(routeId));
        if (startRouteDoc != null) {
            try {
                startRoute = startRouteDoc.getAdapter(DocumentRoute.class);
			} catch (Exception e) {
	            print "L'id de la route à démarrer ne correspond pas à une feuille de route" + e;
				return;
        	}
        } else {
            print "L'id de la route à démarrer ne correspond pas à une feuille de route";
			return;
        }
		if (startRoute != null) {
            if (startRoute.getDocument().getCurrentLifeCycleState().equals(DocumentRouteElement.ElementLifeCycleState.done.toString())) {
                startRoute.backToReady(session);
            }
            startRoute.run(session);
            print "Route démarrée";
			return;
        } 
	}	

    public static boolean fixDossierBySourceQuestion (session, question, legislature) {        
        
        if (StringUtils.isEmpty(question)) {
            print "Le paramètre question ne doit pas être vide";
			return false;
        }
		print "Récupération dossier " + question;
        DocumentModel dossierDoc = getDossierDocBySourceQuestion (session, question, legislature);        
        if (dossierDoc == null) {
			print "Document dossier est null";
			return false;
        }

		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		String routeId = dossier.getLastDocumentRoute();

		DocumentModelList listDossierLink = getDossiersLinkDoneListByIdDossier (session, dossierDoc.getId());
		DocumentModelList listRunningSteps = getRunningStepForRouteId (session, routeId);		
		boolean restart = false;
		
		// On supprime les dossiers link à l'état done pour les étapes toujours en running
		// et on passe lesdites étapes à l'état done
		for(DocumentModel dossierLinkDoc : listDossierLink) {
			DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
			String stepId = dossierLink.getRoutingTaskId();
			DocumentModel stepDoc = session.getDocument(new IdRef(stepId));
			if (listRunningSteps.contains(stepDoc)) {
				print "Suppression des DossierLinks à done pour les étapes en running";
				removeCaseLinkById (session, dossierLinkDoc.getId());
				setRunningStepToDone (session, stepId);
				restart = true;
			}
		}
		
		if (restart) {
			restartFeuilleRoute (session, routeId);
		}
		return true;
    }
    
    /**
    * Retourne la législature courante
    *
    */
    public static String getCurrentLegislature(session) {
    	return STServiceLocator.getSTParametreService().getParametreValue(session, ReponsesParametreConstant.LEGISLATURE_COURANTE); 
    }
}

print "Début script groovy de déblocage de feuille de route";
print "-------------------------------------------------------------------------------";
String question = Context.get("question");
String legislature = Context.get("legislature");

if(StringUtils.isBlank(question)) {
	return "Argument question non trouvé. Vous devez au moins spécifier : -ctx \"question='sourceQuestion'\" ";
}
if(StringUtils.isBlank(legislature)) {
	legislature = FixDossierUtils.getCurrentLegislature(Session);
}
question = question.replace("'", "");
legislature = legislature.replace("'", "");
if (!FixDossierUtils.fixDossierBySourceQuestion (Session, question, legislature)) {
	print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
	return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
} 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de déblocage de feuille de route";
return "Fin du script groovy";
