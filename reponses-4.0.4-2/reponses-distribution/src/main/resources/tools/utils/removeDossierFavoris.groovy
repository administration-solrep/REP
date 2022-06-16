import java.lang.StringBuilder;
import java.util.List;

import javax.transaction.Status;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.st.core.query.QueryUtils;

/**
 * script groovy de suppression de dossier de favoris
 * 
 */
class TransactionUtils {
    public static boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }
}    

class CleanUtils {    
    public static boolean run(final CoreSession session, final String utilisateur, final String nomDossierFavori) {
        boolean response = true;
        final StringBuilder query = new StringBuilder();
        query.append("SELECT f.ecm:uuid as id FROM FavorisDossierRepertoire AS f WHERE f.dc:creator='")
        	.append(utilisateur).append("' AND f.dc:title='").append(nomDossierFavori).append("'");
        Long count = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(query.toString()), null);
        if (count != 1) {
        	print count + " dossier(s) de favoris trouvé(s)";
        	print "Un et un seul dossier doit être trouvé";
        	response = false;
        } else {
	        print count + " dossier de favoris trouvé";
	        if (!TransactionUtils.isTransactionAlive()) {
	                TransactionHelper.startTransaction();
	            }
	        List<DocumentModel> docModelList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, query.toString(), null, 1, 0);
	        for (DocumentModel docModel : docModelList) {
	                try {
	                    session.removeDocument(docModel.getRef());
	                } catch (ClientException ce) {
	                    print "Impossible de supprimer le dossier de favoris " + nomDossierFavori + " id : " + docModel.getId();
	                    response = false;
	                }
	            }
	        session.save();
	        TransactionHelper.commitOrRollbackTransaction();
	        print "Fin traitement du dossier de favoris " + nomDossierFavori + " pour l'utilisateur " + utilisateur + " : suppression effectuée avec succès";
	    }
	    
        return response;
    }
}
    
print "Début du script groovy de de suppression de dossier de favoris";
print "-------------------------------------------------------------------------------";
String utilisateur = Context.get("utilisateur");
print utilisateur;
String nomDossierFavori = Context.get("nomDossierFavori");
print nomDossierFavori;

if(StringUtils.isBlank(utilisateur) || StringUtils.isBlank(nomDossierFavori)) {
	print "Argument 'utilisateur' ou 'nomDossierFavori' non trouvé.";
	print "Vous devez spécifier : -ctx \"utilisateur='identifiantUtilisateur',nomDossierFavori='nomDossierFavori'\" ";
	print "Fin du script - au moins une donnée n'a pas été renseignée";
	print "-------------------------------------------------------------------------------";
	return "Fin du script - au moins une donnee n'a pas ete renseignee";
}
utilisateur = utilisateur.replace("'", "");
nomDossierFavori = nomDossierFavori.replace("'", "");
if (!CleanUtils.run (Session, utilisateur, nomDossierFavori)) {
	print "Fin du script - Echec lors de la suppression du dossier de favoris";
	print "-------------------------------------------------------------------------------";
	return "Fin du script - Echec lors de la suppression du dossier de favoris";
} 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de suppression de dossier de favoris";
return "Fin du script groovy";
