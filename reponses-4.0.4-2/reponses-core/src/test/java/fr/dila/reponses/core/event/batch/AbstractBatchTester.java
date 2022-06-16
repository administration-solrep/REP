package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.batch.BatchLoggerModelImpl;
import fr.dila.st.core.feature.SolonMockitoFeature;
import javax.inject.Inject;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.event.impl.EventImpl;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ ReponseFeature.class, SolonMockitoFeature.class })
public abstract class AbstractBatchTester {
    @Inject
    protected EventService eventService;

    @Mock
    @RuntimeService
    protected SuiviBatchService suiviBatch;

    public void launchEvent(String eventName) {
        Mockito.when(suiviBatch.createBatchLogger(eventName)).thenReturn(new BatchLoggerModelImpl());
        Event event = new EventImpl(eventName, new EventContextImpl());
        eventService.fireEvent(event);
    }
}
