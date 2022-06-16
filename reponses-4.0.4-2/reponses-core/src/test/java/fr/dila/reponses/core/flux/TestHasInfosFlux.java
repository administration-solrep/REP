package fr.dila.reponses.core.flux;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.flux.HasInfoFlux;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.recherche.EnvReponseFixture;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestHasInfosFlux {
    private static final Log LOG = LogFactory.getLog(TestHasInfosFlux.class);

    @Inject
    private CoreFeature coreFeature;

    private EnvReponseFixture fixture;

    @Before
    public void setUp() {
        LOG.debug("ENTER SETUP");
        (STServiceLocator.getSTParametreService()).clearCache();
        if (fixture == null) {
            fixture = new EnvReponseFixture();
            try (CloseableCoreSession session = coreFeature.openCoreSession()) {
                fixture.setUpEnv(session);
            }
        }
        LOG.debug("EXIT SETUP");
    }

    @Test
    public void testAdapter() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            LOG.info("Test adapt to HasInfoFlux");
            Assert.assertNotNull(getFluxAdapter(session));
        }
    }

    @Test
    public void testIsUrgent() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            LOG.info("Test isUrgent");
            HasInfoFlux fluxAdapter = getFluxAdapter(session);
            Assert.assertFalse(fluxAdapter.isUrgent());

            Question question1 = fixture.getQuestion1(session);

            question1.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_RAPPELE, Calendar.getInstance(), "10");
            question1.save(session);
            session.save();

            fluxAdapter = getFluxAdapter(session);
            Assert.assertTrue(fluxAdapter.isUrgent());
        }
    }

    @Test
    public void testIsSignale() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            LOG.info("Test isSignale");
            HasInfoFlux fluxAdapter = getFluxAdapter(session);
            Assert.assertFalse(fluxAdapter.isSignale());
        }
    }

    @Test
    public void testIsRenouvelle() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            LOG.info("Test isRenouvelle");
            HasInfoFlux fluxAdapter = getFluxAdapter(session);
            Assert.assertFalse(fluxAdapter.isRenouvelle());
        }
    }

    private HasInfoFlux getFluxAdapter(CoreSession session) {
        HasInfoFlux fluxAdapter = fixture.getQuestion1(session).getDocument().getAdapter(HasInfoFlux.class);
        return fluxAdapter;
    }
}
