package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.constant.VocabularyConstants.QUESTION_ORIGINE_AN;
import static fr.dila.reponses.api.constant.VocabularyConstants.QUESTION_ORIGINE_SENAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.entry;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.service.DossierQueryService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.service.STParametreService;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author arolin
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/service/test-feuille-route-model-framework.xml")
public class DossierQueryServiceIT {
    @Inject
    private CoreSession session;

    @Inject
    private DossierQueryService dossierQueryService;

    @Inject
    private STParametreService paramService;

    @Inject
    private ReponseFeature reponseFeature;

    private Dossier an100_1;
    private Dossier an111_1;
    private Dossier senat200_1;
    private Dossier senat200_2;
    private Dossier senat100_1;
    private Dossier senat100_2;

    @Before
    public void setup() {
        paramService.clearCache();

        an100_1 = reponseFeature.createDossier(session, 100L, QUESTION_ORIGINE_AN);
        an111_1 = reponseFeature.createDossier(session, 111L, QUESTION_ORIGINE_AN);

        reponseFeature.createDossier(session, 500L, QUESTION_ORIGINE_AN);

        senat100_1 = reponseFeature.createDossier(session, 100L, QUESTION_ORIGINE_SENAT);
        senat100_2 = reponseFeature.createDossier(session, 100L, QUESTION_ORIGINE_SENAT);
        senat200_1 = reponseFeature.createDossier(session, 200L, QUESTION_ORIGINE_SENAT);
        senat200_2 = reponseFeature.createDossier(session, 200L, QUESTION_ORIGINE_SENAT);

        reponseFeature.createDossier(session, 900L, QUESTION_ORIGINE_SENAT);
    }

    @Test
    public void testService() {
        assertThat(dossierQueryService).isNotNull();
    }

    @Test
    public void testArgs() {
        Throwable exc = catchThrowable(() -> dossierQueryService.getMapDossierOrigineIdsFromNumero(null, null));
        assertThat(exc).isInstanceOf(NullPointerException.class).hasMessage("null CoreSession");

        exc = catchThrowable(() -> dossierQueryService.getMapDossierOrigineIdsFromNumero(session, null));
        assertThat(exc).isInstanceOf(NullPointerException.class).hasMessage("null numeroQuestion");

        exc = catchThrowable(() -> dossierQueryService.getDossierIdsFromNumeroAndOrigine(null, null, null));
        assertThat(exc).isInstanceOf(NullPointerException.class).hasMessage("null CoreSession");

        exc = catchThrowable(() -> dossierQueryService.getDossierIdsFromNumeroAndOrigine(session, null, null));
        assertThat(exc).isInstanceOf(NullPointerException.class).hasMessage("null numeroQuestion");

        exc = catchThrowable(() -> dossierQueryService.getDossierIdsFromNumeroAndOrigine(session, "100", null));
        assertThat(exc).isNull();
    }

    @Test
    public void testGetMapDossierOrigineIdsFromNumero() {
        Map<String, String> map = dossierQueryService.getMapDossierOrigineIdsFromNumero(session, "111");
        assertThat(map).contains(entry(an111_1.getDocument().getId(), QUESTION_ORIGINE_AN));

        map = dossierQueryService.getMapDossierOrigineIdsFromNumero(session, "200");
        assertThat(map)
            .contains(
                entry(senat200_1.getDocument().getId(), QUESTION_ORIGINE_SENAT),
                entry(senat200_2.getDocument().getId(), QUESTION_ORIGINE_SENAT)
            );

        map = dossierQueryService.getMapDossierOrigineIdsFromNumero(session, "100");
        assertThat(map)
            .contains(
                entry(an100_1.getDocument().getId(), QUESTION_ORIGINE_AN),
                entry(senat100_1.getDocument().getId(), QUESTION_ORIGINE_SENAT),
                entry(senat100_2.getDocument().getId(), QUESTION_ORIGINE_SENAT)
            );

        map = dossierQueryService.getMapDossierOrigineIdsFromNumero(session, "2369584741684");
        assertThat(map).isEmpty();
    }

    @Test
    public void testGetDossiersFromNumeroAndOrigine() {
        List<String> ids = dossierQueryService.getDossierIdsFromNumeroAndOrigine(session, "100", QUESTION_ORIGINE_AN);
        assertThat(ids).containsOnly(an100_1.getDocument().getId());

        ids = dossierQueryService.getDossierIdsFromNumeroAndOrigine(session, "100", QUESTION_ORIGINE_SENAT);
        assertThat(ids).containsOnly(senat100_1.getDocument().getId(), senat100_2.getDocument().getId());

        ids = dossierQueryService.getDossierIdsFromNumeroAndOrigine(session, "3516843516165", QUESTION_ORIGINE_SENAT);
        assertThat(ids).isEmpty();

        // get dossiers from numero question only
        ids = dossierQueryService.getDossierIdsFromNumeroAndOrigine(session, "111", null);
        assertThat(ids).containsOnly(an111_1.getDocument().getId());

        ids = dossierQueryService.getDossierIdsFromNumeroAndOrigine(session, "200", null);
        assertThat(ids).containsOnly(senat200_1.getDocument().getId(), senat200_2.getDocument().getId());

        ids = dossierQueryService.getDossierIdsFromNumeroAndOrigine(session, "100", null);
        assertThat(ids)
            .containsOnly(
                an100_1.getDocument().getId(),
                senat100_1.getDocument().getId(),
                senat100_2.getDocument().getId()
            );

        ids = dossierQueryService.getDossierIdsFromNumeroAndOrigine(session, "3516843516165", null);
        assertThat(ids).isEmpty();
    }
}
