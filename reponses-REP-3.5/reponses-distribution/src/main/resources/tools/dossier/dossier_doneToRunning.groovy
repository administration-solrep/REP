import java.lang.String;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.apache.commons.lang.StringUtils;

import fr.dila.reponses.api.cases.Dossier;
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
		//Passage à l'état running
		println("changement du statut vers \"running\" ");
		Dossier dossier = model.getAdapter(Dossier.class);
		String state = dossier.getDocument().getCurrentLifeCycleState();
		String resultat="";
		if(state.equals(STDossier.DossierState.done.name())){
			println("dossier relancé : " + dossier.getQuestionId() );
			dossier.getDocument().followTransition(STDossier.DossierTransition.backToRunning.name());
			Session.saveDocument(dossier.getDocument());
			resultat = "réussite   ";
		}else{
			println("Le dossier n'est pas à l'état done. Script inutile ici.");
			resultat = "échec  ";
		}
		return "Fin du script : "+resultat;
	}
}
