package fr.dila.reponses.api.service;

import fr.dila.st.api.service.MailboxService;
import java.util.Collection;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ReponsesMailboxService extends MailboxService {
    void updatePrecomptageMailboxes(Collection<String> mailboxDocIds, CoreSession session);

    Set<String> findAllMailboxDocsId(CoreSession session);

    String getMailboxListQuery(CoreSession session, String userSelection, String posteSelection);

    void updatePrecomptageMailboxesAndSetProperty(CoreSession session, Collection<DocumentModel> mailboxDocIds);
}
