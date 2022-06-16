package fr.dila.reponses.core.service;

import fr.dila.reponses.api.service.ReponsesAlertService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.service.STAlertService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestReponseAlertSchedulerService {

    @Test
    public void testService() {
        STAlertService alertService = ReponsesServiceLocator.getAlertService();
        Assert.assertNotNull(alertService);
        Assert.assertTrue(alertService instanceof ReponsesAlertService);
    }
}
