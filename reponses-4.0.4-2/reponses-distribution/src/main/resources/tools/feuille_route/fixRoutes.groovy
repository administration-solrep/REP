import javax.transaction.Status;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;

/**
 * script groovy d'annulation de feuille de route en running si aucun dossier n'y est attaché
 * 
 */
class FixUtils {    
    public static final String MODE_INFO = "INFO";
    public static final String MODE_FIX = "FIX";

    /**
    * Annule la feuille de route
    *
    **/
    public static void cancelFeuilleRoute (final CoreSession session, final String feuilleId) {
        if (feuilleId == null) {
            print "Aucune feuille de route à annuler";
        } else {
            DocumentModel feuilleDoc = session.getDocument(new IdRef(feuilleId));
            SSFeuilleRoute feuilleRoute = feuilleDoc.getAdapter(SSFeuilleRoute.class);
            print "Mise à l'état 'cancel' de la feuille de route id : " + feuilleId;
            feuilleRoute.cancel(session);
        }
    }

    public static void printInfo(Long count) {
        if (count == null) {
            print "Aucune feuille de route";
        } else {
            print "Nombre de feuille de route orphelines : " + count;
        }
    }
    
    /**
     *
     * Dénombre les feuilles de route orphelines
     */
    public static Long countFeuilleRoute (final CoreSession session) {
        String queryCount = "select count(id) FROM FEUILLE_ROUTE WHERE id in (SELECT id FROM MISC WHERE lifecyclestate = 'running') AND id not in (SELECT id FROM docri_participatingdocuments)";
        IterableQueryResult resCount = null;
        String[] colonnes = [FlexibleQueryMaker.COL_COUNT];
        try {
            resCount = QueryUtils.doSqlQuery(session, colonnes, queryCount , null);
            Iterator<Map<String, Serializable>> iterator = resCount.iterator();
            if (iterator.hasNext()) {
                Map<String, Serializable> row = iterator.next();
                return (Long) row.get(FlexibleQueryMaker.COL_COUNT);
            } else {
                print "Aucun résultat";
                return null;
            }
        } catch (ClientException exc) {
            print "Impossible de récupérer le nombre de question"
            print exc;
        } finally {
            if (resCount != null) {
                resCount.close();
            }    
        }
        return null;
    }

    public static boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean fixRoutes(final CoreSession session, Long count) {
        String query = "select id FROM FEUILLE_ROUTE WHERE id in (SELECT id FROM MISC WHERE lifecyclestate = 'running') AND id not in (SELECT id FROM docri_participatingdocuments)";        
        IterableQueryResult res = null;
        String[] colonnes = [FlexibleQueryMaker.COL_ID];
        long limit = (long) 50;
        try {
            for (Long offset = (long) 0; offset <= count; offset += limit) {
                Long borne = (count<offset+limit?count:offset+limit);
                print "Début traitement feuilles de route de " + offset + " à " + borne;
                if (!isTransactionAlive()) {
                    TransactionHelper.startTransaction();
                }
                res = QueryUtils.doSqlQuery(session, colonnes, query, null, limit, 0);
                final Iterator<Map<String, Serializable>> iterator = res.iterator();
                while (iterator.hasNext()) {
                    final Map<String, Serializable> row = iterator.next();
                    String id = (String) row.get(FlexibleQueryMaker.COL_ID);
                    cancelFeuilleRoute(session, id);
                }
                if (res != null) {
                    res.close();
                }
                TransactionHelper.commitOrRollbackTransaction();
                print "Fin traitement feuilles de route de " + offset + " à " + borne;
            }
        } catch (ClientException e) {
            print "Impossible de récupérer les feuille de route orphelines"
            print e;
            return false;
        } finally {
            if (res != null) {
                res.close();
            }
        }
        return true;
    }

    public static boolean run(final CoreSession session, final String mode) {        
        Long count = countFeuilleRoute(session);
        printInfo(count);
        if (MODE_FIX.equals(mode)) {
            if (count == null) {
                print "Dénombrement null des feuilles de routes";
                return false;
            } else {
                return fixRoutes(session, count);
            }
        } else if (!MODE_INFO.equals(mode)) {
            print "Mode non reconnu - Vous devez spécifier : -ctx \"mode='INFO'|'FIX'\" "
        }
        return true;
    }
}

print "Début du script groovy d'annulation de feuille de route";
print "-------------------------------------------------------------------------------";
String mode = Context.get("mode");

if(StringUtils.isBlank(mode)) {
	print "Argument mode non trouvé. mode par défaut : 'INFO' ";
    mode = FixUtils.MODE_INFO;    
}
mode = mode.replace("'", "");
if (!FixUtils.run (Session, mode)) {
	print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
    print "-------------------------------------------------------------------------------";
	return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
} 
    
print "-------------------------------------------------------------------------------";
print "Fin du script groovy d'annulation de feuille de route";
return "Fin du script groovy";
