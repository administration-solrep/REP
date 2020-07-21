import java.util.ArrayList;
import java.util.List;


import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;

import fr.dila.st.core.query.QueryUtils;

import com.google.common.collect.Lists;


/**
 * script groovy de modification des permissions sur les dossiers links : ce script s'assure que les dossiers links d'un ministère possèdent bien le droit pour le ministère
 * !!!! ATTENTION !!!!! Ce script n'est pas à exécuter en l'état, il faut toujours s'assurer que les ministères définis dans la méthode runTraitement(CoreSession session) correspondent bien aux ministères à modifier
 */
class FixDossierPermsUtils {
	
	public static List<String> getListDocsToModify(String idMinistereAttributaire, CoreSession session) {
		
		List<String> ids = new ArrayList<String>();
		try {
			ids.addAll(QueryUtils.doQueryForIds(session,"SELECT * FROM DossierLink WHERE drl:idMinistereAttributaire='"+idMinistereAttributaire+"'"));
		} catch (Exception e) {

			println("Une erreur est survenue lors de la récupération des DossierLinks pour la corbeille du ministère attributaire : "+idMinistereAttributaire);
			e.printStackTrace();
		}
		return ids;
	}

	public static boolean checkIfDocumentHasPerm(String permUserLabel, DocumentModel doc) {
		boolean hasPerm = false;
		try {

			ACP listOfPerms = doc.getACP();
			ACL listOfDroits = listOfPerms.getACL("security");
			if (listOfDroits != null) {
				for (int i = 0; i < listOfDroits.size(); i++) {
					// On vérifie que les droits sont correctes pour l'utilisateur (libellé des utilisateur +
					// lecture/écriture + autorisé
					if (listOfDroits.get(i).getUsername().equals(permUserLabel) && listOfDroits.get(i).isGranted()
							&& listOfDroits.get(i).getPermission().equals("ReadWrite")) {
						println("Le document : "+doc.getId()+" disposait déjà du droit pour le ministère");
						hasPerm = true;
					}
				}
			}

		} catch (ClientException e) {

			println("Une erreur est survenue lors de la vérification de la présence de la permission dans le document : "+doc.getId());
			e.printStackTrace();
		}
		return hasPerm;
	}

	public static boolean addPermissionToDoc(String permLabel, DocumentModel doc, CoreSession session) {
		boolean addPerm = false;
		try {

			ACP listOfPerms = doc.getACP();
			ACL listOfDroits = listOfPerms.getACL("security");
			ACE nouvellePerm = new ACE(permLabel, "ReadWrite", true);
			listOfDroits.add(nouvellePerm);
			session.setACP(doc.getRef(), listOfPerms, false);
			session.save();
			addPerm = true;

		} catch (ClientException e) {
			println("Une erreur est survenue lors de l'ajout de la permission au document : "+doc.getId());
			e.printStackTrace();
		}

		return addPerm;
	}
	
	public static void traiterMinistereAttrib(String idMinistere,CoreSession session) {
		println("Début du traitement du ministère "+idMinistere);
		try {
			List<String> lstIds = getListDocsToModify(idMinistere, session);

			if (lstIds != null && !lstIds.isEmpty()) {
				for (String id : lstIds) {
					DocumentModel doc = session.getDocument(new IdRef(id));
					String permLabel = "dossier_link_updater_min-" + idMinistere;
					boolean hasPerm = checkIfDocumentHasPerm(permLabel, doc);
					if (!hasPerm) {
						if (!addPermissionToDoc(permLabel, doc, session)) {
							println("La permission" +permLabel+ " n'a pas été ajoutée au document "+id);
						} else {

							println("On a bien rajouté la permission "+permLabel+" au document : "+id);
						}
					}

				}
			}

		} catch (ClientException e) {
			println("Une erreur est survenue lors du traitement du ministère "+idMinistere);
			e.printStackTrace();
		}
		println("Fin du traitement du ministère "+idMinistere);
	}

	public static void runTraitement(CoreSession session) {
		List<String> lstMinisteres = Lists.newArrayList("60005127","60005130","60005144","60005147","60005136");
		
		for(String ministereId : lstMinisteres){
			traiterMinistereAttrib(ministereId,session);
		}

	}
	
	
}

println("Début du script de mise à jour des permissions sur les dossiers links");

FixDossierPermsUtils.runTraitement(Session);	
println("Fin du script de mise à jour des permissions sur les dossiers links");

return "Le script a été exécuté, consultez le fichier server.log pour le suivi de son déroulement";