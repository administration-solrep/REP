import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.transaction.Status;

import fr.dila.cm.cases.CaseConstants;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.flux.QuestionStateChange
import fr.dila.reponses.api.cases.flux.Renouvellement
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesParametreConstant
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesMigrationService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.StringUtil;

/**
 * Script groovy pour briser le cachet serveur de la signature de réponses pour un ou l'ensemble des ministères
 * Prend en paramètres facultatifs le type d'appel (info ou exécution) et un id ministère (si non présent = l'ensemble des ministères) 
 *  
 */
class BriseSignatureUtils {
	
	
	/**
	 * Affiche le nombre de dossiers à modifier ainsi que leur source + numéro
	 */
	private static void printInfoSignatures(session, dossiersDocList) {		
		print "Nombre de dossiers dont la signature doit être brisée : " + dossiersDocList.size();
		for (DocumentModel dossierDoc : dossiersDocList) {
			Dossier dossier = dossierDoc.getAdapter(Dossier.class);
			Question question = dossier.getQuestion(session);
			print "Dossier " + question.getOrigineQuestion() + " " + question.getNumeroQuestion();
		}
	}
	
	
	public static boolean briseSignature (session, mode, ministere) {
		// Action différente suivant le mode :
		// INFO : affichage des informations pour le ou tous les ministères
		// EXEC : mise à jour du cachet serveur pour le ou tous les ministères
		ReponsesMigrationService migrationService = ReponsesServiceLocator.getReponsesMigrationService(); 

		List<DocumentModel> dossiersDocList = migrationService.getLstDossiersEligibleBriseSignature(session, ministere);
		
		// on affiche dans tous les cas les infos à réaliser
		// en cas d'erreur dans l'affichage on sort du script
		printInfoSignatures(session, dossiersDocList);
		if (mode.equalsIgnoreCase("EXEC")) {
			migrationService.briserSignatureDossiers(session, dossiersDocList);
		} else if (!mode.equalsIgnoreCase("INFO")){
			print "Argument mode non reconnu. Sortie de script";
			return false;
		}
		
		return true;
	}
}

print "Début script groovy de brisure cachet serveur";
print "-------------------------------------------------------------------------------";
String mode = Context.get("mode");
String ministere = Context.get("ministere");

if (StringUtil.isBlank(mode)) {
	print "Argument mode non trouvé. Mode par défaut sélectionné : INFO.";
	mode = "INFO";
}
mode = mode.replace("'", "");

if (StringUtil.isBlank(ministere)) {
	print "Argument ministere non trouvé. Vous devez préciser au moins -ctx \"ministere='XXXXXXXX'\" pour un ministère précis, ou -ctx \"ministere='ALL'\" pour tous.";
	print "-------------------------------------------------------------------------------";
    print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
    return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
} else {
	print "Argument ministere trouvé. Action sur le ministère " + ministere + ".";
}
ministere = ministere.replace("'", "");

if (!BriseSignatureUtils.briseSignature (Session, mode, ministere)) {
    print "-------------------------------------------------------------------------------";
    print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
    return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
}
 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de brisure cachet serveur";
return "Fin du script groovy ";

