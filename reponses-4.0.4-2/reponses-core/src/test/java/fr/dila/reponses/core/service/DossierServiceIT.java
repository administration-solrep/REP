package fr.dila.reponses.core.service;

import static org.junit.Assert.assertFalse;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.core.ReponseFeature;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class DossierServiceIT {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    private String idDirectionPilote = "12345678";

    /** Injection des données pour le test. */
    @Before
    public void setUp() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Dossier dossier = reponseFeature.createDossier(session);
            Question question = dossier.getQuestion(session);
            question.setIdDirectionPilote(idDirectionPilote);
            session.saveDocument(question.getDocument());
            session.save();
        }
    }

    @Test
    public void testGetDossierRattacheToDirection() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DossierServiceImpl service = new DossierServiceImpl();
            assertFalse(service.getDossierRattacheToDirection(session, idDirectionPilote).isEmpty());
        }
    }
}
