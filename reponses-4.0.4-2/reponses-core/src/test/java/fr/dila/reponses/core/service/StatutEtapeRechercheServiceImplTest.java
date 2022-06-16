package fr.dila.reponses.core.service;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.VocabularyConstants.LEGISLATURE;
import static fr.dila.reponses.api.constant.VocabularyConstants.STATUT_ETAPE_RECHERCHE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.ID_PROPERTY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.LABEL_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.service.vocabulary.StatutEtapeRechercheService;
import fr.dila.reponses.core.service.vocabulary.StatutEtapeRechercheServiceImpl;
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
public class StatutEtapeRechercheServiceImplTest {
    private StatutEtapeRechercheService service;

    @Mock
    private DocumentModel statutEtape1Doc;

    @Mock
    private DocumentModel statutEtape2Doc;

    @Mock
    private DocumentModel statutEtape3Doc;

    @Mock
    @RuntimeService
    private VocabularyService vocabularyService;

    @Before
    public void setUp() {
        service = new StatutEtapeRechercheServiceImpl();
    }

    @Test
    public void getStatutsEtape() {
        ImmutablePair<String, String> statutEtape1 = ImmutablePair.of("running_all", "Étape en cours");
        ImmutablePair<String, String> statutEtape2 = ImmutablePair.of("ready_all", "Étape à venir");
        ImmutablePair<String, String> statutEtape3 = ImmutablePair.of("done_1", "validée manuellement");

        when(vocabularyService.getAllEntry(STATUT_ETAPE_RECHERCHE))
            .thenReturn(new DocumentModelListImpl(newArrayList(statutEtape1Doc, statutEtape2Doc, statutEtape3Doc)));

        when(statutEtape1Doc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(statutEtape1.getLeft());
        when(statutEtape1Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(statutEtape1.getRight());

        when(statutEtape2Doc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(statutEtape2.getLeft());
        when(statutEtape2Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(statutEtape2.getRight());

        when(statutEtape3Doc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(statutEtape3.getLeft());
        when(statutEtape3Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(statutEtape3.getRight());

        List<ImmutablePair<String, String>> statutsEtape = service.getEntries();

        assertThat(statutsEtape).containsExactly(statutEtape3, statutEtape2, statutEtape1);
    }

    @Test
    public void getStatutsEtapeWithError() {
        when(vocabularyService.getAllEntry(LEGISLATURE)).thenReturn(null);

        Throwable throwable = catchThrowable(() -> service.getEntries());
        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessage("Une erreur s'est produite lors de la récupération des statuts d'étape");
    }
}
