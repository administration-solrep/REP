import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.StringUtil;

/**
 * Script groovy pour initialiser les dates de rappel d'une question
 * 
 * Paramètres :
 * - question : la question à traiter (AN XXXXX ou SENAT XXXXX) (obligatoire)
 * - date : la date de rappel pour le lot de la question au format JJ/MM/AAAA (obligatoire)
 * - legislature : legislature correspondante à la question (facultatif) - legislature courante par défaut
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

    public static boolean fixDossierBySourceQuestion (session, numQuest, dateStr, legislature) {
		Question question = getQuestionFromParam (session, numQuest, legislature);
		if (question == null) {
		    print "Impossible de récupérer la question " + numQuest;
            return false;
        }
		Calendar date = null;
		try {
		    date = DateUtil.parse(dateStr);
		} catch (ClientException ce) {
		    print "Erreur dans le parsing de date - " + ce.getMessage();
		    return false;
		}
		
		if (date == null) {
		    print "date is null";
		    return false;
		}
		
	    if (StringUtil.isEmpty(question.getDateRappelQuestion())) {
	        printTraitement(numQuest, dateStr);
            question.setEtatQuestion(VocabularyConstants.ETAT_QUESTION_RAPPELE, date);
            session.saveDocument(question.getDocument());
            session.save();
        } else {
            printErrAlreadyExist(question.getSourceNumeroQuestion());
            return false;
        }
        
		return true;
    }
    
    public static void printTraitement(String numQuest, String dateStr) {
        print "Mise à jour date rappel question " + numQuest + " à la valeur : " + dateStr;
    }
    
    public static void printErrAlreadyExist(String numQuest) {
        print "La question " + numQuest + " dispose déjà d'une question de rappel";
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

print "Début script groovy de mise à jour des dates de rappel";
print "-------------------------------------------------------------------------------";
String question = Context.get("question");
String date = Context.get("date");
String legislature = Context.get("legislature");

if(StringUtil.isBlank(question)) {
    print "-------------------------------------------------------------------------------";
    print "Fin du script groovy de mise à jour des dates de rappel";
	return "Argument question non trouvé. Vous devez au moins spécifier : -ctx \"question='sourceNumeroQuestion',date='JJ/MM/AAAA'\" ";
}
if(StringUtil.isBlank(date)) {
    print "-------------------------------------------------------------------------------";
    print "Fin du script groovy de mise à jour des dates de rappel";
    return "Argument date non trouvé. Vous devez au moins spécifier : -ctx \"question='sourceNumeroQuestion',date='JJ/MM/AAAA'\" ";
}
if(StringUtil.isBlank(legislature)) {
	legislature = FixDossierUtils.getCurrentLegislature(Session);
}

question = question.replace("'", "");
date = date.replace("'", "");
legislature = legislature.replace("'", "");

if (!FixDossierUtils.fixDossierBySourceQuestion (Session, question, date, legislature)) {
    print "-------------------------------------------------------------------------------";
    print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
    return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
}
 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de mise à jour des dates de rappel";
return "Fin du script groovy - voir logs serveur";
