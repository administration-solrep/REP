import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.transaction.Status;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.reponses.api.cases.Reponse;
import fr.dila.st.core.query.QueryUtils;

/**
 * Script groovy pour supprimer les balises provenant de word d'une réponse
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

class FixDossierUtils {    

    public static final String DEBUT_BALISE = "<!--";
    public static final String FIN_BALISE = "-->";

    public static boolean fixReponses (final CoreSession session) {  
		String queryReponses = "SELECT DISTINCT r.ecm:uuid AS id FROM Question AS q,Dossier AS d,Reponse AS r " +
				"WHERE (((q.qu:typeQuestion = 'QE') AND (r.ecm:fulltext_txtReponse = '\${!--[if} \${gte} \${mso}')) " +
				"AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid ) " +
				"AND q.qu:etatQuestion = 'en cours'";
        Long count = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(queryReponses), null);
        print count + " reponses trouvées";
        long limit = (long) 100;
        for (Long offset = (long) 0; offset <= count; offset += limit) {
            Long borne = (count<offset+limit?count:offset+limit);
            print "Récupération réponses de " + offset + " à " + borne;
            if (!TransactionUtils.isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
            List<DocumentModel> reponses = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, queryReponses, null, limit, 0);
            for (DocumentModel documentModel : reponses) {
                Reponse reponse = documentModel.getAdapter(Reponse.class);
                print "------------- " + reponse.getDocument().getTitle() + " -------------";
                if (!reponse.isSignee()) {
                    String chaine = getTexteParasite(session, reponse);
                    print "*******************";
                    while (StringUtils.isNotEmpty(chaine)) {
                        print "La chaine suivante sera effacée de la réponse : ";
                        print chaine;
                        String reponseTexte = reponse.getTexteReponse();
                        reponse.setTexteReponse(StringUtils.replace(reponseTexte, chaine, ""));
                        chaine = getTexteParasite(session, reponse);
                        print "*******************";
                    }
                    print "La réponse après passage du script est maintenant : ";
                    print reponse.getTexteReponse();
                    session.saveDocument(reponse.getDocument());                    
                } else {
                    print "La réponse " + reponse.getDocument().getTitle() + " est déjà signée, aucune action effectuée !"
                }
            }
            session.save();
            TransactionHelper.commitOrRollbackTransaction();
            print "Fin traitement réponses de " + offset + " à " + borne;
        }        
        
        
        return true;
    }

    public static String getTexteParasite(final CoreSession session, final Reponse reponse) {
        String reponseTexte = reponse.getTexteReponse();
        StringBuilder repTexteNettoye = new StringBuilder();
        repTexteNettoye.append(DEBUT_BALISE);
        String texte = StringUtils.substringBetween(reponseTexte, DEBUT_BALISE, FIN_BALISE);
        if (StringUtils.isNotEmpty(texte)) {
            repTexteNettoye.append(texte);
            repTexteNettoye.append(FIN_BALISE);
        } else {
            repTexteNettoye = new StringBuilder("");
        }
        
        return repTexteNettoye.toString();
    }
}

print "Début du script groovy de suppression des balises word";
print "-------------------------------------------------------------------------------";

if (!FixDossierUtils.fixReponses(Session)) {
    print "-------------------------------------------------------------------------------";
    print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
    return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
}
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de suppression des balises word";
return "Fin du script groovy - voir logs serveur";
