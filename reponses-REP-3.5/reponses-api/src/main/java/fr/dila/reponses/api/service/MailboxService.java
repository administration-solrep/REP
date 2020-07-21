package fr.dila.reponses.api.service;

import java.util.Collection;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

public interface MailboxService extends fr.dila.st.api.service.MailboxService {

    void updatePrecomptageMailboxes(Collection<String> mailboxDocIds, CoreSession session);

    Set<String> findAllMailboxDocsId(CoreSession session) throws ClientException;

}
