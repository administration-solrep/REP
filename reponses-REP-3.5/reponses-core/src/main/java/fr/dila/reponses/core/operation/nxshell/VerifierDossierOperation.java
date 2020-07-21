package fr.dila.reponses.core.operation.nxshell;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.core.query.QueryUtils;

/**
 * Une opération pour vérifier les dossiers.
 * 
 * @author bgamard
 */
@Operation(
        id = VerifierDossierOperation.ID,
        category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
        label = "VerifierDossier",
        description = "Vérifie les dossiers")
public class VerifierDossierOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Verifier.Dossier"; 

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(VerifierDossierOperation.class);
    
    @Context
    protected CoreSession session;

    @OperationMethod
    public void run() throws Exception {
        
        log.info("Début opération " + ID);

        log.info("Recherche des incohérences de dénormalisation du ministère attributaire");
        
        IterableQueryResult res = null;
        try {
            String query = "select qu.sourcenumeroquestion, dos.ministereattributairecourant, qu.idministereattributaire, dos.id " +
            		" from dossier_reponse dos, question qu " +
            		" where dos.iddocumentquestion = qu.id " +
            		" and dos.ministereattributairecourant != qu.idministereattributaire";
            res = QueryUtils.doSqlQuery(session,
                    new String[] { "qu:sourceNumeroQuestion", "dos:ministereAttributaireCourant", "qu:idMinistereAttributaire", "dc:title" },
                    query, new Object[] {});
            Iterator<Map<String, Serializable>> iterator = res.iterator();
            
            if (iterator.hasNext()) {
                while (iterator.hasNext()) {
                    Map<String, Serializable> row = iterator.next();
                    String numeroQuestion = (String) row.get("qu:sourceNumeroQuestion");
                    String minDossier = (String) row.get("dos:ministereAttributaireCourant");
                    String minQuestion = (String) row.get("qu:idMinistereAttributaire");
                    String idDossier = (String) row.get("dc:title");
                    log.info(numeroQuestion + " : Le ministère attributaire sur le dossier et la question " +
                    		"sont différents (Dossier - id: " + idDossier + ": " + minDossier+ ", Question: " + minQuestion + ")");
                }
            } else {
                log.info("Aucune incohérence détectée sur les ministères attributaires");
            }
        } finally {
            if (res != null) {
                res.close();
            }
        }
        
        log.info("Fin de l'opération" + ID);
        return;
    }
}
