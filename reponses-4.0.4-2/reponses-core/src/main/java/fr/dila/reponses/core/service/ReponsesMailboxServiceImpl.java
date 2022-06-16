package fr.dila.reponses.core.service;

import fr.dila.reponses.api.service.ReponsesMailboxService;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.core.util.SSMailboxUtils;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.MailboxServiceImpl;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.runtime.api.Framework;

/**
 * Implémentation du service Mailbox pour Réponses.
 *
 * @author jtremeaux
 */
public class ReponsesMailboxServiceImpl extends MailboxServiceImpl implements ReponsesMailboxService {
    private static final Log LOG = LogFactory.getLog(ReponsesMailboxServiceImpl.class);

    private static final String SQL_PROCEDURE_UPDATE = "computePrecomptageMailbox";
    private static final String MSG_END_FMT = "Fin des mises à jour du precomptage (%d / %d mailbox).";

    /**
     * Default constructor
     */
    public ReponsesMailboxServiceImpl() {
        super();
    }

    @Override
    public void updatePrecomptageMailboxes(Collection<String> mailboxDocIds, CoreSession session) {
        int nbOk = 0;
        for (String docId : mailboxDocIds) {
            try {
                LOG.info("Maj precomptage [" + docId + "].");
                if (Framework.isTestModeSet() || Framework.isDevModeSet()) {
                    LOG.warn("Maj precomptage [" + docId + "] : SKIPPED IN TEST MODE.");
                } else {
                    final String functionCall = SQL_PROCEDURE_UPDATE + "('" + docId + "')";
                    QueryUtils.execSqlFunction(session, functionCall, null);
                }
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
    public void updatePrecomptageMailboxesAndSetProperty(CoreSession session, Collection<DocumentModel> mailboxes) {
        List<String> ids = mailboxes.stream().map(DocumentModel::getId).collect(Collectors.toList());
        updatePrecomptageMailboxes(ids, session);
        QueryHelper.invalidateAllCache(session);
    }

    @Override
    public Set<String> findAllMailboxDocsId(final CoreSession session) {
        final StringBuilder sbuilder = new StringBuilder("SELECT m.ecm:uuid as id FROM ");
        sbuilder.append(getMailboxType());
        sbuilder.append(" as m ");

        final Set<String> result = new HashSet<>();

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

    @Override
    public String getMailboxListQuery(CoreSession session, String userSelection, String posteSelection) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();

        Set<String> postesId = SSMailboxUtils.getSelectedPostes(
            (SSPrincipal) session.getPrincipal(),
            posteSelection,
            userSelection
        );
        Set<String> mailboxPosteIds = mailboxPosteService.getMailboxPosteIdSetFromPosteIdSet(postesId);

        List<String> mailboxDocIds = getMailboxDocIds(session, mailboxPosteIds);

        if (mailboxDocIds.isEmpty()) {
            return "('0')";
        }

        return "(" + StringUtil.join(mailboxDocIds, ",", "'") + ")";
    }
}
