import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList
import fr.dila.reponses.api.cases.flux.QuestionStateChange
import fr.dila.reponses.api.cases.flux.Renouvellement
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesParametreConstant
import fr.dila.reponses.api.constant.VocabularyConstants
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.StringUtil;

/**
 * Script groovy pour supprimer les renouvellements enregistrés en double dans un dossier
 * Si X n'est pas renseigné, affiche la liste des renouvellements
 * 
 * Paramètres :
 * - question : la question à traiter (AN XXXXX ou SENAT XXXXX) (obligatoire)
 * - legislature : legislature correspondante à la question (facultatif) - legislature courante par défaut
 * - mode : info ou delete (facultatif) - INFO par défaut
 *  
 */
class FixDossierUtils {    

	/**
	 * Retourne le document d'une question par son numéro de question et numéro de legislature
	 * 
	 * @return DocumentModel Questiondoc correspondant aux paramètres de question, null si non trouvé
	 */
	public static DocumentModel getQuestionDocBySourceQuestion (session, numQuest, legislature) {
		String queryQuestion = "SELECT * FROM Question WHERE qu:sourceNumeroQuestion='" + numQuest + "' AND qu:legislatureQuestion = " + legislature + " AND qu:typeQuestion = 'QE'";
		DocumentModelList listQuestion = session.query(queryQuestion);
		if(listQuestion == null || listQuestion.isEmpty()) {
			print "Aucune question ne correspond aux paramètres passés";
			return null;
		}
		if (listQuestion.size() >1) {
			print  "Plusieurs questions correspondent aux paramètres passés. ";
			return null;
		}
		
		return listQuestion.get(0);
	}

    public static boolean fixDossierBySourceQuestion (session, numQuest, legislature) {
		Question question = getQuestionFromParam (session, numQuest, legislature);
		if (question == null) {
            return false;
        }
		
		ArrayList<Map<String, Serializable>> listeQscModel= new ArrayList<Map<String, Serializable>>();
		List<Renouvellement> renouvellements = question.getRenouvellements();
        List<QuestionStateChange> etats = question.getEtatQuestionHistorique();
        Map<Date, Renouvellement> renouvellementsCleaned = new HashMap<Date, Renouvellement>();
        Map<Calendar, QuestionStateChange> etatsCleaned = new HashMap<Calendar, QuestionStateChange>();
        
        for (Renouvellement r : renouvellements) {
            Date d = r.getDateEffet();
            renouvellementsCleaned.put(d, r);
        }
        for (QuestionStateChange state : etats) {
            Calendar changeDate = state.getChangeDate();
            if (!etatsCleaned.containsKey(changeDate)) {
                etatsCleaned.put(changeDate, state);
            } else if (etatsCleaned.get(changeDate).getNewState().equals(VocabularyConstants.ETAT_QUESTION_RENOUVELEE)){
                continue;
            }
            Map<String, Serializable> qscMap = new HashMap<String, Serializable>();
            qscMap.put(DossierConstants.DOSSIER_ETAT_QUESTION_DATE_PROPERTY, state.getChangeDate());
            qscMap.put(DossierConstants.DOSSIER_ETAT_QUESTION_VALUE_PROPERTY, state.getNewState());
            listeQscModel.add(qscMap);
        }
        
        renouvellements = new ArrayList<Renouvellement>(renouvellementsCleaned.values());
        question.setRenouvellements(renouvellements);
        PropertyUtil.setProperty(question.getDocument(),DossierConstants.QUESTION_DOCUMENT_SCHEMA,DossierConstants.DOSSIER_ETAT_QUESTION, listeQscModel);
        
        session.saveDocument(question.getDocument());
        session.save();
        
		return true;
    }
    
    public static boolean showDossierBySourceQuestion (session, numQuest, legislature) {
        Question question = getQuestionFromParam (session, numQuest, legislature);
        if (question == null) {
            return false;
        }
        List<Renouvellement> renouvellements = question.getRenouvellements();
        List<QuestionStateChange> etats = question.getEtatQuestionHistorique();
        print "Renouvellement : "
        for (Renouvellement r : renouvellements) {
            print DateUtil.formatForClient(r.getDateEffet());
        }
        print "Changement d'état : "
        for (QuestionStateChange state : etats) {
            Calendar changeDate = state.getChangeDate();
            print state.getNewState() + " - " + DateUtil.formatDDMMYYYYSlash(changeDate);
        }
        return true;
    }
    
    public static Question getQuestionFromParam (session, numQuest, legislature) {
        if (StringUtil.isEmpty(numQuest)) {
            print "Le paramètre question ne doit pas être vide";
            return null;
        }
        print "Récupération question " + numQuest;
        DocumentModel questionDoc = getQuestionDocBySourceQuestion (session, numQuest, legislature);        
        if (questionDoc == null) {
            print "Document question est null";
            return null;
        }

        return questionDoc.getAdapter(Question.class);
    }
    
    /**
    * Retourne la législature courante
    *
    */
    public static String getCurrentLegislature(session) {
    	return STServiceLocator.getSTParametreService().getParametreValue(session, ReponsesParametreConstant.LEGISLATURE_COURANTE); 
    }
}

print "Début script groovy de suppression des renouvellements doublés";
print "-------------------------------------------------------------------------------";
String question = Context.get("question");
String legislature = Context.get("legislature");
String mode = Context.get("mode");

if(StringUtil.isBlank(question)) {
    print "-------------------------------------------------------------------------------";
    print "Fin du script groovy de suppression des renouvellements doublés";
	return "Argument question non trouvé. Vous devez au moins spécifier : -ctx \"question='sourceQuestion'\" ";
}
if(StringUtil.isBlank(legislature)) {
	legislature = FixDossierUtils.getCurrentLegislature(Session);
}

question = question.replace("'", "");
legislature = legislature.replace("'", "");

if(StringUtil.isBlank(mode)) {
    // Mode info
    print "Mode = INFO";
    if (!FixDossierUtils.showDossierBySourceQuestion (Session, question, legislature)) {
        print "-------------------------------------------------------------------------------";
        print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
        return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
    }
} else {
    mode = mode.replace("'", "");
    if (mode.equals("info")) {
        print "Mode = INFO";
        if (!FixDossierUtils.showDossierBySourceQuestion (Session, question, legislature)) {
            print "-------------------------------------------------------------------------------";
            print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
            return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
        }
    } else if (mode.equals("delete")) {
        print "Mode = DELETE";
     // Mode suppression
        if (!FixDossierUtils.fixDossierBySourceQuestion (Session, question, legislature)) {
            print "-------------------------------------------------------------------------------";
            print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
            return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
        }
    } else {
        print "-------------------------------------------------------------------------------";
        print "Fin du script - Une erreur a provoqué son arrêt";
        return "Argument mode non reconnu. mode=info, ou mode=delete";
    }
    
}
 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de suppression des renouvellements doublés";
return "Fin du script groovy - voir logs serveur";


