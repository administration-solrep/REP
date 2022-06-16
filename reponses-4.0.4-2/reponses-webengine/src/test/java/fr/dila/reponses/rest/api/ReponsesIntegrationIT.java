package fr.dila.reponses.rest.api;

import com.google.inject.Inject;
import java.io.ByteArrayOutputStream;
import org.apache.poi.util.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.jaxrs.test.CloseableClientResponse;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponsesWebEngineFeature.class)
public class ReponsesIntegrationIT {
    @Inject
    private ReponsesWebEngineFeature reponsesWebEngineFeature;

    @Test
    public void testPing() throws Exception {
        try (
            CloseableClientResponse response = reponsesWebEngineFeature.getResponse(
                ReponsesWebEngineFeature.RequestType.GET,
                "ping"
            )
        ) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtils.copy(response.getEntityInputStream(), output);
            Assert.assertEquals("pong", output.toString());
        }
    }
}
