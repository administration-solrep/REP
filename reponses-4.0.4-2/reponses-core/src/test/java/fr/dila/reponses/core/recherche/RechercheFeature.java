package fr.dila.reponses.core.recherche;

import com.google.inject.Binder;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.core.service.STServiceLocator;
import java.util.function.BiConsumer;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RunnerFeature;

/**
 *
 * @author jgomez
 *
 */

// Briser la classe de test en 4 morceaux : requete simple , avancee, index et metadonnées
@Features(ReponseFeature.class)
public class RechercheFeature implements RunnerFeature {
    public static final String BASE_QUERY = "ufnxql:SELECT DISTINCT q.ecm:uuid AS id FROM Question AS q";

    private static final String MY_REQUETE = "myRequete";
    private static final Log LOG = LogFactory.getLog(RechercheFeature.class);

    @Inject
    private CoreFeature coreFeature;

    @Inject
    protected RechercheService rs;

    private EnvReponseFixture fixture;

    @Override
    public void configure(FeaturesRunner runner, Binder binder) {
        coreFeature = runner.getFeature(CoreFeature.class);
    }

    @Override
    public void beforeSetup(FeaturesRunner runner) {
        LOG.debug("ENTER SETUP");
        STServiceLocator.getSTParametreService().clearCache();
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            rs = ReponsesServiceLocator.getRechercheService();
            Assert.assertNotNull(rs);
            fixture = new EnvReponseFixture();
            fixture.setUpEnv(session);
        }
        LOG.debug("EXIT SETUP");
    }

    public Requete createRequete(CoreSession session) {
        return createRequete(session, MY_REQUETE);
    }

    public Requete createRequete(CoreSession session, String request) {
        return rs.createRequete(session, request);
    }

    public EnvReponseFixture getFixture() {
        return fixture;
    }

    public Question getQuestion1(CoreSession session) {
        return fixture.getQuestion1(session);
    }

    public void runTest(BiConsumer<CoreSession, Requete> cons) {
        runTest(MY_REQUETE, cons);
    }

    public void runTest(String request, BiConsumer<CoreSession, Requete> cons) {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Requete requete = createRequete(session, request);
            cons.accept(session, requete);
        }
    }
}
