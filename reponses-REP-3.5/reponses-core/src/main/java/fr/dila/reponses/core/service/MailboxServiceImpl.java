package fr.dila.reponses.core.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.reponses.api.service.MailboxService;
import fr.dila.reponses.core.event.UpdateMailboxStepCountListener;
import fr.dila.st.core.query.QueryUtils;

/**
 * Implémentation du service Mailbox pour Réponses.
 * 
 * @author jtremeaux
 */
public class MailboxServiceImpl extends fr.dila.st.core.service.MailboxServiceImpl implements MailboxService {

    private static final long serialVersionUID = 3227642228671001591L;

    private static final Log LOG = LogFactory.getLog(UpdateMailboxStepCountListener.class);

    private static final String SQL_PROCEDURE_UPDATE = "computePrecomptageMailbox";
    private static final String MSG_END_FMT = "Fin des mises à jour du precomptage (%d / %d mailbox).";

    /**
     * Default constructor
     */
    public MailboxServiceImpl(){
    	super();
    }
    
    @Override
    public void updatePrecomptageMailboxes(Collection<String> mailboxDocIds, CoreSession session) {

        int nbOk = 0;
        for (String docId : mailboxDocIds) {
            try {
                LOG.info("Maj precomptage [" + docId + "].");
                final String functionCall = SQL_PROCEDURE_UPDATE + "('" + docId + "')";
                QueryUtils.execSqlFunction(session, functionCall, null);
                ++nbOk;
            } catch (Exception e) {
                LOG.error("Echec de la mise a jour du precomptage pour [" + docId + "]", e);
            }
        }
        if (nbOk == mailboxDocIds.size()) {
            LOG.info(String.format(MSG_END_FMT, nbOk, mailboxDocIds.size()));
        } else {
            LOG.error(String.format(MSG_END_FMT, nbOk, mailboxDocIds.size()));
        }
    }

    @Override
    public Set<String> findAllMailboxDocsId(final CoreSession session) throws ClientException {

        final StringBuilder sbuilder = new StringBuilder("SELECT m.ecm:uuid as id FROM ");
        sbuilder.append(getMailboxType());
        sbuilder.append(" as m ");

        final Set<String> result = new HashSet<String>();

        IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, sbuilder.toString(), null);

        try {
            final Iterator<Map<String, Serializable>> itres = res.iterator();
            while (itres.hasNext()) {
                Map<String, Serializable> data = itres.next();
                result.add((String) data.get("id"));

            }
        } finally {
            res.close();
        }

        return result;

    }
}
