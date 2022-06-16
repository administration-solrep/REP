package fr.dila.reponses.core.event.batch;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.util.BatchHelper;
import fr.dila.st.api.util.FrameworkHelper;
import fr.dila.st.core.event.batch.BatchLoggerModelImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.event.impl.EventImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        SessionUtil.class,
        STServiceLocator.class,
        ReponsesServiceLocator.class,
        QueryUtils.class,
        BatchHelper.class,
        FrameworkHelper.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesBlockedRouteAlertBatchListenerTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    protected EtatApplicationService etatApplicationServiceMock;

    @Mock
    protected EtatApplication etatApplicationMock;

    @Mock
    private UpdateTimbreService updateTimbreServiceMock;

    @Spy
    private ReponsesBlockedRouteAlertBatchListener listenerSpy;

    @Mock
    private CloseableCoreSession sessionMock;

    @Mock
    private SuiviBatchService suiviBatchServiceMock;

    @Mock
    private IterableQueryResult iterableQueryResultMock;

    @Mock
    private Iterator<Map<String, Serializable>> iteratorMock;

    private Event event;

    @Before
    public void before() {
        String eventName = SSEventConstant.BLOCKED_ROUTES_ALERT_EVENT;
        event = new EventImpl(eventName, new EventContextImpl());
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getEtatApplicationService()).thenReturn(etatApplicationServiceMock);
        when(etatApplicationServiceMock.getEtatApplicationDocument(any(CoreSession.class)))
            .thenReturn(etatApplicationMock);
        when(STServiceLocator.getSuiviBatchService()).thenReturn(suiviBatchServiceMock);
        when(suiviBatchServiceMock.createBatchLogger(eventName)).thenReturn(new BatchLoggerModelImpl());

        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getUpdateTimbreService()).thenReturn(updateTimbreServiceMock);

        PowerMockito.mockStatic(SessionUtil.class);
        when(SessionUtil.openSession()).thenReturn(sessionMock);

        PowerMockito.mockStatic(QueryUtils.class);

        PowerMockito.mockStatic(BatchHelper.class);
        when(BatchHelper.canBatchBeLaunched()).thenReturn(true);

        when(
            QueryUtils.doSqlQuery(
                sessionMock,
                new String[] { FlexibleQueryMaker.COL_ID },
                listenerSpy.getQuery(),
                new Object[] {}
            )
        )
            .thenReturn(iterableQueryResultMock);

        when(iterableQueryResultMock.iterator()).thenReturn(iteratorMock);
        when(iteratorMock.hasNext()).thenReturn(false);

        PowerMockito.mockStatic(FrameworkHelper.class);
    }

    @Test
    public void test_launch_shouldNotBeStopped() {
        prepareLaunchAndVerifyProcess(false, false, false, true);
    }

    @Test
    public void test_launch_shouldBeStopped_in_devmode() {
        prepareLaunchAndVerifyProcess(false, false, true, false);
    }

    @Test
    public void test_launch_shouldBeStopped() {
        prepareLaunchAndVerifyProcess(false, true, false, false);

        prepareLaunchAndVerifyProcess(true, false, false, false);

        prepareLaunchAndVerifyProcess(true, true, false, false);
    }

    @Test
    public void test_launch_shouldBeStopped_if_technical_restriction() {
        prepareTestMocks(false, false, false);
        when(BatchHelper.canBatchBeLaunched()).thenReturn(false);

        listenerSpy.handleEvent(event);
        verifyProcessEvent(false);
    }

    private void verifyProcessEvent(boolean exec) {
        Mockito.verify(listenerSpy, Mockito.times(exec ? 1 : 0)).processEvent(sessionMock, event);
    }

    private void prepareTestMocks(boolean isRestriction, boolean isMigration, boolean devMode) {
        when(etatApplicationMock.getRestrictionAcces()).thenReturn(isRestriction);
        when(updateTimbreServiceMock.isMigrationEnCours(any(CoreSession.class))).thenReturn(isMigration);

        when(FrameworkHelper.isDevModeSet()).thenReturn(devMode);
    }

    private void prepareLaunchAndVerifyProcess(
        boolean isRestriction,
        boolean isMigration,
        boolean devMode,
        boolean expectedToProcess
    ) {
        prepareTestMocks(isRestriction, isMigration, devMode);
        listenerSpy.handleEvent(event);
        verifyProcessEvent(expectedToProcess);
    }
}
