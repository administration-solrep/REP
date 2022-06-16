package fr.dila.reponses.core.service.vocabulary;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_SCHEMA;
import static fr.dila.reponses.api.constant.VocabularyConstants.LEGISLATURE;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.ID_PROPERTY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.LABEL_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.service.vocabulary.LegislatureService;
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
public class LegislatureServiceImplTest {
    private LegislatureService service;

    @Mock
    private DocumentModel legislature13Doc;

    @Mock
    private DocumentModel legislature14Doc;

    @Mock
    private DocumentModel legislature15Doc;

    @Mock
    @RuntimeService
    private VocabularyService vocabularyService;

    @Before
    public void setUp() {
        service = new LegislatureServiceImpl();
    }

    @Test
    public void getLegislatures() {
        ImmutablePair<Integer, String> legislature13 = ImmutablePair.of(13, "13eme legislature");
        ImmutablePair<Integer, String> legislature14 = ImmutablePair.of(14, "14eme legislature");
        ImmutablePair<Integer, String> legislature15 = ImmutablePair.of(15, "15eme legislature");

        when(vocabularyService.getAllEntry(LEGISLATURE))
            .thenReturn(new DocumentModelListImpl(newArrayList(legislature13Doc, legislature14Doc, legislature15Doc)));

        when(legislature13Doc.getProperty(LEGISLATURE_SCHEMA, ID_PROPERTY))
            .thenReturn(legislature13.getLeft().toString());
        when(legislature13Doc.getProperty(LEGISLATURE_SCHEMA, LABEL_PROPERTY)).thenReturn(legislature13.getRight());

        when(legislature14Doc.getProperty(LEGISLATURE_SCHEMA, ID_PROPERTY))
            .thenReturn(legislature14.getLeft().toString());
        when(legislature14Doc.getProperty(LEGISLATURE_SCHEMA, LABEL_PROPERTY)).thenReturn(legislature14.getRight());

        when(legislature15Doc.getProperty(LEGISLATURE_SCHEMA, ID_PROPERTY))
            .thenReturn(legislature15.getLeft().toString());
        when(legislature15Doc.getProperty(LEGISLATURE_SCHEMA, LABEL_PROPERTY)).thenReturn(legislature15.getRight());

        List<ImmutablePair<Integer, String>> legislatures = service.getEntries();

        assertThat(legislatures).containsExactly(legislature13, legislature14, legislature15);
    }

    @Test
    public void getLegislaturesWithError() {
        when(vocabularyService.getAllEntry(LEGISLATURE)).thenReturn(null);

        Throwable throwable = catchThrowable(() -> service.getEntries());
        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessage("Une erreur s'est produite lors de la récupération des législatures");
    }
}
