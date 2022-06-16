package fr.dila.reponses.core.recherche;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 *
 * @author jgomez
 *
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class RechercheTestCase {
    public static final String BASE_QUERY = "ufnxql:SELECT DISTINCT q.ecm:uuid AS id FROM Question AS q";

    private static final String MY_REQUETE = "myRequete";
    protected RechercheService rs;
    protected Dossier dossier1;
    protected Question question1;
    protected Reponse reponse1;

    @Inject
    private CoreFeature coreFeature;

    @Test
    public Requete createRequete() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            rs = ReponsesServiceLocator.getRechercheService();
            Assert.assertNotNull(rs);
            EnvReponseFixture fixture = new EnvReponseFixture();
            fixture.setUpEnv(session);
            dossier1 = fixture.getDossier1(session);
            question1 = dossier1.getQuestion(session);
            reponse1 = fixture.getReponse1(session);
            return rs.createRequete(session, MY_REQUETE);
        }
    }
}
