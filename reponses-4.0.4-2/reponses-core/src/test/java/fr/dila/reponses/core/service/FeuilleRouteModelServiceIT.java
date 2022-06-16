package fr.dila.reponses.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.core.ReponseFeature;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class FeuilleRouteModelServiceIT {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private FeuilleRouteModelService feuilleRouteModelService;

    private static final String FDR_NAME = "feuille de route";
    private static final String MINISTERE_ID1 = "12345";
    private static final String MINISTERE_ID2 = "54321";
    private static final String DIRECTION_ID1 = "67890";
    private static final String DIRECTION_ID2 = "09876";

    private DocumentModel fdrDocModel1, fdrDocModel2, fdrDocModel3, fdrDocModel4;

    /** Injection des données pour le test. */
    @Before
    public void setUp() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            fdrDocModel1 = reponseFeature.createFeuilleRoute(session, FDR_NAME);
            ReponsesFeuilleRoute feuilleRoute1 = fdrDocModel1.getAdapter(ReponsesFeuilleRoute.class);

            feuilleRoute1.setMinistere(MINISTERE_ID1);
            feuilleRoute1.setIdDirectionPilote(DIRECTION_ID1);

            session.saveDocument(fdrDocModel1);

            fdrDocModel2 = reponseFeature.createFeuilleRoute(session, FDR_NAME);
            ReponsesFeuilleRoute feuilleRoute2 = fdrDocModel2.getAdapter(ReponsesFeuilleRoute.class);

            feuilleRoute2.setMinistere(MINISTERE_ID1);
            feuilleRoute2.setIdDirectionPilote(DIRECTION_ID2);

            session.saveDocument(fdrDocModel2);

            fdrDocModel3 = reponseFeature.createFeuilleRoute(session, FDR_NAME);
            ReponsesFeuilleRoute feuilleRoute3 = fdrDocModel3.getAdapter(ReponsesFeuilleRoute.class);

            feuilleRoute3.setMinistere(MINISTERE_ID2);
            feuilleRoute3.setIdDirectionPilote(DIRECTION_ID1);

            session.saveDocument(fdrDocModel3);

            fdrDocModel4 = reponseFeature.createFeuilleRoute(session, FDR_NAME);
            ReponsesFeuilleRoute feuilleRoute4 = fdrDocModel4.getAdapter(ReponsesFeuilleRoute.class);

            feuilleRoute4.setMinistere(MINISTERE_ID2);
            feuilleRoute4.setIdDirectionPilote(DIRECTION_ID2);

            session.saveDocument(fdrDocModel4);

            session.save();
        }
    }

    @Test
    public void testGetFdrModelFromMinistereAndDirection_allEmpty() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            List<DocumentModel> listDocModel = feuilleRouteModelService.getFdrModelFromMinistereAndDirection(
                session,
                null,
                null,
                false
            );
            assertThat(listDocModel).containsExactly(fdrDocModel1, fdrDocModel2, fdrDocModel3, fdrDocModel4);

            listDocModel =
                feuilleRouteModelService.getFdrModelFromMinistereAndDirection(session, MINISTERE_ID1, null, false);
            assertThat(listDocModel).containsExactly(fdrDocModel1, fdrDocModel2);

            listDocModel =
                feuilleRouteModelService.getFdrModelFromMinistereAndDirection(
                    session,
                    MINISTERE_ID1,
                    DIRECTION_ID1,
                    true
                );
            assertThat(listDocModel).containsExactly(fdrDocModel1);

            listDocModel =
                feuilleRouteModelService.getFdrModelFromMinistereAndDirection(session, null, DIRECTION_ID1, true);
            assertThat(listDocModel).containsExactly(fdrDocModel1, fdrDocModel3);
        }
    }
}
