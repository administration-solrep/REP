package fr.dila.reponses.ui.services.actions.impl;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.VocabularyConstants.INDEXATION_ZONE_AN;
import static fr.dila.reponses.api.constant.VocabularyConstants.INDEXATION_ZONE_SENAT;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_ANALYSE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.ui.bean.VocSugUI;
import fr.dila.reponses.ui.services.actions.IndexActionService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class IndexActionServiceImplTest {
    private IndexActionService service;

    @Mock
    private DocumentModel doc;

    @Mock
    private ReponsesIndexableDocument indexableDoc;

    @Mock
    private CoreSession session;

    @Mock
    @RuntimeService
    private ReponsesVocabularyService vocabularyService;

    @Before
    public void setUp() {
        service = new IndexActionServiceImpl();
    }

    @Test
    public void getVocMap() {
        String theme = "Theme";
        String rubrique = "Rubrique";

        when(vocabularyService.getVocabularyList()).thenReturn(newArrayList(theme, rubrique));

        Map<String, VocSugUI> results = service.getVocMap();

        assertThat(results)
            .containsOnlyKeys(theme, rubrique)
            .extractingFromEntries(
                entry -> entry.getValue().getVocabularyLabel(),
                entry -> entry.getValue().getIndexLabel()
            )
            .containsExactlyInAnyOrder(tuple(theme, null), tuple(rubrique, null));
    }

    @Test
    public void getListIndexByZoneForZoneAN() {
        String[] libellesRubriqueAN = new String[3];
        libellesRubriqueAN[0] = AN_RUBRIQUE.getValue();
        libellesRubriqueAN[1] = "libelle rubrique AN";
        libellesRubriqueAN[2] = "label.vocabulary.AN_rubrique";

        String[] libellesAnalyse = new String[3];
        libellesAnalyse[0] = AN_ANALYSE.getValue();
        libellesAnalyse[1] = "";
        libellesAnalyse[2] = "label.vocabulary.AN_analyse";

        String[] libellesRubriqueTA = new String[3];
        libellesRubriqueTA[0] = TA_RUBRIQUE.getValue();
        libellesRubriqueTA[1] = "";
        libellesRubriqueTA[2] = "label.vocabulary.TA_rubrique";

        List<String[]> listIndexByZone = newArrayList(libellesRubriqueAN, libellesAnalyse, libellesRubriqueTA);

        when(doc.getAdapter(ReponsesIndexableDocument.class)).thenReturn(indexableDoc);
        when(indexableDoc.getListIndexByZone(INDEXATION_ZONE_AN)).thenReturn(listIndexByZone);

        List<String[]> results = service.getListIndexByZone(doc, INDEXATION_ZONE_AN);

        assertThat(results).containsExactly(libellesRubriqueAN, libellesAnalyse);
    }

    @Test
    public void getListIndexByZoneForZoneSenat() {
        String[] libellesThemeSenat = new String[3];
        libellesThemeSenat[0] = SE_THEME.getValue();
        libellesThemeSenat[1] = "libelle theme";
        libellesThemeSenat[2] = "label.vocabulary.SE_theme";

        String[] libellesRubrique = new String[3];
        libellesRubrique[0] = SE_RUBRIQUE.getValue();
        libellesRubrique[1] = "libelle rubrique";
        libellesRubrique[2] = "label.vocabulary.SE_rubrique";

        List<String[]> listIndexByZone = newArrayList(libellesThemeSenat, libellesRubrique);

        when(doc.getAdapter(ReponsesIndexableDocument.class)).thenReturn(indexableDoc);
        when(indexableDoc.getListIndexByZone(INDEXATION_ZONE_SENAT)).thenReturn(listIndexByZone);

        List<String[]> results = service.getListIndexByZone(doc, INDEXATION_ZONE_SENAT);

        assertThat(results).containsExactly(libellesThemeSenat, libellesRubrique);
    }

    @Test
    public void getDocumentAdapted() {
        service.getDocumentAdapted(doc);

        verify(doc).getAdapter(ReponsesIndexableDocument.class);
    }

    @Test
    public void getDirectoriesByZone() {
        Map<String, List<String>> mapVocabularyToZone = new HashMap<>(1);
        mapVocabularyToZone.put(INDEXATION_ZONE_AN, newArrayList(AN_RUBRIQUE.getValue(), AN_ANALYSE.getValue()));

        when(vocabularyService.getMapVocabularyToZone()).thenReturn(mapVocabularyToZone);

        List<String> results = service.getDirectoriesByZone(INDEXATION_ZONE_AN);

        assertThat(results).containsExactly(AN_RUBRIQUE.getValue(), AN_ANALYSE.getValue());
    }

    @Test
    public void getCurrentIndexationWithNullDoc() {
        service.getCurrentIndexation(null, session);

        verify(session).createDocumentModel(QUESTION_DOCUMENT_TYPE);
    }
}
