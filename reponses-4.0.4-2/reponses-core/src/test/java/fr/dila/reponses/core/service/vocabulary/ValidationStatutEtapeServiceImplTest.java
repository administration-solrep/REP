package fr.dila.reponses.core.service.vocabulary;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.VocabularyConstants.VALIDATION_STATUT_ETAPE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.ID_PROPERTY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.LABEL_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.service.vocabulary.ValidationStatutEtapeService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class ValidationStatutEtapeServiceImplTest {
    private ValidationStatutEtapeService service;

    @Mock
    private DocumentModel statut1Doc;

    @Mock
    private DocumentModel statut2Doc;

    @Mock
    private DocumentModel statut3Doc;

    @Mock
    @RuntimeService
    private VocabularyService vocabularyService;

    @Before
    public void setUp() {
        service = new ValidationStatutEtapeServiceImpl();
    }

    @Test
    public void getValidationStatutsEtape() {
        ImmutablePair<Integer, String> statut1 = ImmutablePair.of(1, "validée manuellement");
        ImmutablePair<Integer, String> statut2 = ImmutablePair.of(2, "invalidée");
        ImmutablePair<Integer, String> statut3 = ImmutablePair.of(3, "validée automatiquement");

        when(vocabularyService.getAllEntry(VALIDATION_STATUT_ETAPE))
            .thenReturn(new DocumentModelListImpl(newArrayList(statut1Doc, statut2Doc, statut3Doc)));

        when(statut1Doc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(statut1.getLeft().toString());
        when(statut1Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(statut1.getRight());

        when(statut2Doc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(statut2.getLeft().toString());
        when(statut2Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(statut2.getRight());

        when(statut3Doc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(statut3.getLeft().toString());
        when(statut3Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(statut3.getRight());

        List<ImmutablePair<Integer, String>> statuts = service.getEntries();

        assertThat(statuts).containsExactly(statut1, statut2, statut3);
    }

    @Test
    public void getValidationStatutsEtapeWithError() {
        when(vocabularyService.getAllEntry(VALIDATION_STATUT_ETAPE)).thenReturn(null);

        Throwable throwable = catchThrowable(() -> service.getEntries());
        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessage("Une erreur s'est produite lors de la récupération des statuts de validation d'étape");
    }
}
