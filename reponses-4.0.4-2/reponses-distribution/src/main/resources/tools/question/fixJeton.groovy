import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.st.api.jeton.JetonDoc;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Script groovy pour changer un dossier de jeton pour qu'il puisse de nouveau être obtenu
 * 
 * Paramètre :
 * - question : question du dossier (obligatoire) (format : "[AN|SENAT] XXXX")
 * - owner : proprietaire du jeton à modifier (obligatoire)
 * - typeWS : le type de WS associé au dossier (obligatoire)
 * - numeroJeton : le numéro de jeton à remplacer (obligatoire)
 * - legislature : legislature correspondante à la question (facultatif)
 */
class FixJetonUtils {    

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

    public static JetonDoc getJetonByIdDossier(session, idDossier, owner, typeWS, numeroJeton) {
        String queryJeton = "SELECT * FROM JetonDoc WHERE jtd:id_owner = '" + owner + "' AND jtd:type_webservice = '" + typeWS + "' AND jtd:id_doc='" + idDossier + "' AND jtd:id_jeton=" + numeroJeton;
		DocumentModelList listJeton = session.query(queryJeton);
		if(listJeton == null || listJeton.isEmpty()) {
			print "Pas de jeton correspondant aux paramètres passés";
			return null;
		}
		
		return listJeton.get(0).getAdapter(JetonDoc.class);
    }

    public static boolean setJetonInBasket(CoreSession session, JetonDoc jeton) {
        if (jeton == null) {
            return false;
        } else {
            print "Mise à jour numéro jeton du numéro " + jeton.getNumeroJeton() + " vers le panier";
            jeton.setNumeroJeton(Long.valueOf(-999));
            session.saveDocument(jeton.getDocument());
            session.save();
            return true;
        }
    }

    public static boolean fixJetonBySourceQuestion (session, question, owner, typeWS, numeroJeton, legislature) {
        if (StringUtils.isEmpty(question)) {
            print "Le paramètre question ne doit pas être vide";
			return false;
        }
        if (StringUtils.isEmpty(owner)) {
            print "Le paramètre owner ne doit pas être vide";
			return false;
        }
        if (StringUtils.isEmpty(typeWS)) {
            print "Le paramètre typeWS ne doit pas être vide";
			return false;
        }
        if (StringUtils.isEmpty(numeroJeton)) {
            print "Le paramètre numeroJeton ne doit pas être vide";
			return false;
        }
        if (StringUtils.isEmpty(legislature)) {
            print "Le paramètre legislature ne doit pas être vide";
			return false;
        }

		print "Récupération dossier " + question;
        DocumentModel dossierDoc = getDossierDocBySourceQuestion (session, question, legislature);        
        if (dossierDoc == null) {
			print "Document dossier est null";
			return false;
        }

		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		JetonDoc jetonDoc = getJetonByIdDossier (session, dossierDoc.getId(), owner, typeWS, numeroJeton);
        // Dans certains cas, le document model d'une question est passé et non un dossier        
        if (jetonDoc == null) {
            jetonDoc = getJetonByIdDossier (session, dossier.getQuestionId(), owner, typeWS, numeroJeton);
        }

		return setJetonInBasket(session, jetonDoc);
    }
    
    /**
    * Retourne la législature courante
    *
    */
    public static String getCurrentLegislature(session) {
    	return STServiceLocator.getSTParametreService().getParametreValue(session, ReponsesParametreConstant.LEGISLATURE_COURANTE); 
    }
}

print "Début script groovy de remise de jetonDoc dans le panier";
print "-------------------------------------------------------------------------------";
String question = Context.get("question");
String owner = Context.get("owner");
String typeWS = Context.get("typeWS");
String numeroJeton = Context.get("numeroJeton");
String legislature = Context.get("legislature");

if(StringUtils.isBlank(question)) {
	return "Argument question non trouvé. Vous devez au moins spécifier : -ctx \"question='sourceQuestion',owner='proprietaireWS',typeWS='typeWS',numeroJeton=numero\" ";
}
if(StringUtils.isBlank(owner)) {
	return "Argument owner non trouvé. Vous devez au moins spécifier : -ctx \"question='sourceQuestion',owner='proprietaireWS',typeWS='typeWS',numeroJeton=numero\" ";
}
if(StringUtils.isBlank(typeWS)) {
	return "Argument typeWS non trouvé. Vous devez au moins spécifier : -ctx \"question='sourceQuestion',owner='proprietaireWS',typeWS='typeWS',numeroJeton=numero\" ";
}
if(StringUtils.isBlank(numeroJeton)) {
	return "Argument numeroJeton non trouvé. Vous devez au moins spécifier : -ctx \"question='sourceQuestion',owner='proprietaireWS',typeWS='typeWS',numeroJeton=numero\" ";
}
if(StringUtils.isBlank(legislature)) {
	legislature = FixJetonUtils.getCurrentLegislature(Session);
}
question = question.replace("'", "");
owner = owner.replace("'", "");
typeWS = typeWS.replace("'", "");
numeroJeton = numeroJeton.replace("'","");
legislature = legislature.replace("'", "");
if (!FixJetonUtils.fixJetonBySourceQuestion (Session, question, owner, typeWS, numeroJeton, legislature)) {
	print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
	return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
} 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de remise de jetonDoc dans le panier";
return "Fin du script groovy";
