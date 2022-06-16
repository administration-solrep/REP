package fr.dila.reponses.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.service.STUserManager;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    { SSServiceLocator.class, Framework.class, STServiceLocator.class, SSServiceLocator.class, QueryUtils.class }
)
@Ignore
public class TestMailboxService {
    @Mock
    CoreSession session;

    @Mock
    IterableQueryResult iterQuery;

    @Mock
    Iterator<Map<String, Serializable>> iter;

    @Mock
    CaseManagementDocumentTypeService caseTypeService;

    @Mock
    STUserManager userManager;

    @Mock
    MailboxPosteService mailboxPosteService;

    @Mock
    SSPrincipalImpl principal;

    @Mock
    DocumentModel doc;

    @Mock
    STUser user;

    @Mock
    List<DocumentModel> lstDocs;

    @Mock
    Iterator<DocumentModel> iterator;

    ReponsesMailboxServiceImpl service = new ReponsesMailboxServiceImpl();

    @Before
    public void before() {
        PowerMockito.mockStatic(Framework.class);
        PowerMockito.mockStatic(QueryUtils.class);
        PowerMockito.mockStatic(STServiceLocator.class);
        PowerMockito.mockStatic(SSServiceLocator.class);

        Mockito.when(STServiceLocator.getCaseManagementDocumentTypeService()).thenReturn(caseTypeService);
        Mockito.when(STServiceLocator.getUserManager()).thenReturn(userManager);
        Mockito.when(SSServiceLocator.getMailboxPosteService()).thenReturn(mailboxPosteService);

        Mockito.when(caseTypeService.getMailboxType()).thenReturn("Mailbox");

        Mockito.when(session.getPrincipal()).thenReturn(principal);
        Mockito.when(principal.getPosteIdSet()).thenReturn(Sets.newHashSet("1", "2", "3"));
        Mockito
            .when(mailboxPosteService.getMailboxPosteIdSetFromPosteIdSet(Mockito.any()))
            .thenReturn(Sets.newHashSet("mailbox-1", "mailbox-2", "mailbox-3"));
    }

    @Test
    public void testUpdatePrecomptageMailboxes() {
        PowerMockito.when(Framework.isTestModeSet()).thenReturn(true);
        service.updatePrecomptageMailboxes(Lists.newArrayList("1", "2", "3"), session);
        PowerMockito.verifyStatic(Mockito.never());
        QueryUtils.execSqlFunction(Mockito.any(), Mockito.any(), Mockito.any());

        PowerMockito.when(Framework.isTestModeSet()).thenReturn(false);
        PowerMockito.when(Framework.isDevModeSet()).thenReturn(true);
        service.updatePrecomptageMailboxes(Lists.newArrayList("1", "2", "3"), session);
        PowerMockito.verifyStatic(Mockito.never());
        QueryUtils.execSqlFunction(Mockito.any(), Mockito.any(), Mockito.any());

        PowerMockito.when(Framework.isTestModeSet()).thenReturn(false);
        PowerMockito.when(Framework.isDevModeSet()).thenReturn(false);
        service.updatePrecomptageMailboxes(Lists.newArrayList("1", "2", "3"), session);
        PowerMockito.verifyStatic(Mockito.times(3));
        QueryUtils.execSqlFunction(Mockito.any(), Mockito.any(), Mockito.any());
    }

    private Map<String, Serializable> buildMockDataMap() {
        Map<String, Serializable> mapData = new HashMap<>();
        mapData.put("data", "1");

        return mapData;
    }

    @Test
    public void testFindAllMailboxDocsId() {
        Mockito.when(QueryUtils.doUFNXQLQuery(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(iterQuery);
        Mockito.when(iterQuery.iterator()).thenReturn(iter);
        Mockito.when(iter.hasNext()).thenReturn(true, true, true, false);
        Mockito.when(iter.next()).thenReturn(buildMockDataMap());

        Set<String> result = service.findAllMailboxDocsId(session);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void testGetMailboxListQuery() {
        String mailbox = service.getMailboxListQuery(session, null, null);
        assertNotNull(mailbox);
        assertEquals("('0')", mailbox);

        mailbox = service.getMailboxListQuery(session, "", "");
        assertNotNull(mailbox);
        assertEquals("('0')", mailbox);

        Mailbox mail1 = Mockito.mock(Mailbox.class);
        DocumentModel docMail1 = Mockito.mock(DocumentModel.class);
        Mockito
            .when(
                QueryUtils.doUFNXQLQueryAndFetchForDocuments(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())
            )
            .thenReturn(lstDocs);
        Mockito.when(lstDocs.iterator()).thenReturn(iterator);
        Mockito.when(iterator.hasNext()).thenReturn(true, true, true, false);
        Mockito.when(iterator.next()).thenReturn(docMail1);
        Mockito.when(docMail1.getId()).thenReturn("mailbox-1", "mailbox-2", "mailbox-3");
        Mockito.when(docMail1.getAdapter(Mailbox.class)).thenReturn(mail1);
        Mockito.when(mail1.getDocument()).thenReturn(docMail1);

        mailbox = service.getMailboxListQuery(session, null, "testPoste");
        assertNotNull(mailbox);
        assertEquals("('mailbox-1','mailbox-2','mailbox-3')", mailbox);

        Mockito
            .when(
                QueryUtils.doUFNXQLQueryAndFetchForDocuments(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())
            )
            .thenReturn(lstDocs);
        Mockito.when(lstDocs.iterator()).thenReturn(iterator);
        Mockito.when(iterator.hasNext()).thenReturn(true, true, true, false);
        Mockito.when(iterator.next()).thenReturn(docMail1);
        Mockito.when(docMail1.getId()).thenReturn("mailbox-1", "mailbox-2", "mailbox-3");
        Mockito.when(docMail1.getAdapter(Mailbox.class)).thenReturn(mail1);
        Mockito.when(mail1.getDocument()).thenReturn(docMail1);
        Mockito.when(userManager.getUserModel(Mockito.anyString())).thenReturn(doc);
        Mockito.when(doc.getAdapter(Mockito.any())).thenReturn(user);
        Mockito.when(user.getPostes()).thenReturn(Lists.newArrayList("1", "2", "3"));

        mailbox = service.getMailboxListQuery(session, "testUser", null);
        assertNotNull(mailbox);
        assertEquals("('mailbox-1','mailbox-2','mailbox-3')", mailbox);
    }
}
