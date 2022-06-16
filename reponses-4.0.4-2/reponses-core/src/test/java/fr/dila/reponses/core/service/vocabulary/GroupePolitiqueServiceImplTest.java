package fr.dila.reponses.core.service.vocabulary;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.VocabularyConstants.GROUPE_POLITIQUE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.ID_PROPERTY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.LABEL_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.service.vocabulary.GroupePolitiqueService;
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
public class GroupePolitiqueServiceImplTest {
    private GroupePolitiqueService service;

    @Mock
    private DocumentModel groupePolitique1Doc;

    @Mock
    private DocumentModel groupePolitique2Doc;

    @Mock
    private DocumentModel groupePolitique3Doc;

    @Mock
    @RuntimeService
    private VocabularyService vocabularyService;

    @Before
    public void setUp() {
        service = new GroupePolitiqueServiceImpl();
    }

    @Test
    public void getGroupesPolitique() {
        ImmutablePair<String, String> groupePolitique1 = ImmutablePair.of("UMP (SENAT)", "UMP (SENAT)");
        ImmutablePair<String, String> groupePolitique2 = ImmutablePair.of("Groupe UC", "Groupe UC");
        ImmutablePair<String, String> groupePolitique3 = ImmutablePair.of("ECOLO (AN)", "ECOLO (AN)");

        when(vocabularyService.getAllEntry(GROUPE_POLITIQUE))
            .thenReturn(
                new DocumentModelListImpl(newArrayList(groupePolitique1Doc, groupePolitique2Doc, groupePolitique3Doc))
            );

        when(groupePolitique1Doc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(groupePolitique1.getLeft());
        when(groupePolitique1Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(groupePolitique1.getRight());

        when(groupePolitique2Doc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(groupePolitique2.getLeft());
        when(groupePolitique2Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(groupePolitique2.getRight());

        when(groupePolitique3Doc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(groupePolitique3.getLeft());
        when(groupePolitique3Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(groupePolitique3.getRight());

        List<ImmutablePair<String, String>> groupesPolitique = service.getEntries();

        assertThat(groupesPolitique).containsExactly(groupePolitique3, groupePolitique2, groupePolitique1);
    }

    @Test
    public void getGroupesPolitiqueWithError() {
        when(vocabularyService.getAllEntry(GROUPE_POLITIQUE)).thenReturn(null);

        Throwable throwable = catchThrowable(() -> service.getEntries());
        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessage("Une erreur s'est produite lors de la récupération des groupes politiques");
    }
}
