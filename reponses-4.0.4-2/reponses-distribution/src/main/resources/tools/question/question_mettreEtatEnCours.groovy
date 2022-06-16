import java.lang.String;
import java.util.Calendar;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.apache.commons.lang.StringUtils;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.st.api.dossier.STDossier;

String dossierUuid = Context.get("dossierUUID");
if(StringUtils.isBlank(dossierUuid)) {
	return "Argument dossierUUID non trouvé. Vous devez spécifier : -ctx \"dossierUUID='uuidDossier'\" ";
} else {
	println("Running runStep : dossierUUID = " + dossierUuid);

	dossierUuid = dossierUuid.replace("'", "");
	
	DocumentModel model = Session.getDocument(new IdRef(dossierUuid));

	if(model == null){
		return "Argument dossierUuid non valide, dossier non trouvé";
	} else{
		// Récupération de la question
		Dossier dossier = model.getAdapter(Dossier.class);
		Question question = dossier.getQuestion(Session);
		if (question == null) {
			return "Question non trouvée";
		}
		
		try {
			question.setEtatQuestion(VocabularyConstants.ETAT_QUESTION_EN_COURS, Calendar.getInstance());
			Session.saveDocument(question.getDocument());
			Session.save();
		} catch (Exception e) {
			println "Impossible de passer l'état de la question à 'en cours'";
			return "Echec de l'exécution du script";
		}
		return "Le script s'est correctement exécuté";
	}
}
