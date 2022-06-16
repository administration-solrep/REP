package fr.dila.reponses.core.recherche.traitement;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_RETIRE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_CADUQUE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_RETIREE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.core.feature.SolonMockitoFeature;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class EtatQuestionTraitementTest {
    private EtatQuestionTraitement traitement;

    @Mock
    private Requete requete;

    @Captor
    private ArgumentCaptor<List<String>> etatQuestionListCaptor;

    @Before
    public void setUp() {
        traitement = new EtatQuestionTraitement();
    }

    @Test
    public void doBeforeQueryWithoutEtat() {
        when(requete.getEtatCaduque()).thenReturn(false);
        when(requete.getEtatClotureAutre()).thenReturn(false);
        when(requete.getEtat(ETAT_RETIRE)).thenReturn(false);

        List<String> expectedEtatQuestionList = new ArrayList<>();
        when(requete.getEtatQuestionList()).thenReturn(expectedEtatQuestionList);

        traitement.doBeforeQuery(requete);

        verify(requete, times(2)).setEtatQuestionList(etatQuestionListCaptor.capture());
        assertThat(etatQuestionListCaptor.getAllValues().get(0)).isEqualTo(expectedEtatQuestionList);
        assertThat(etatQuestionListCaptor.getAllValues().get(1)).isNull();
    }

    @Test
    public void doBeforeQueryWithEtats() {
        when(requete.getEtatCaduque()).thenReturn(true);
        when(requete.getEtatClotureAutre()).thenReturn(true);
        when(requete.getEtat(ETAT_RETIRE)).thenReturn(true);

        List<String> expectedEtatQuestionList = newArrayList(
            ETAT_QUESTION_CADUQUE,
            ETAT_QUESTION_CLOTURE_AUTRE,
            ETAT_QUESTION_RETIREE
        );
        when(requete.getEtatQuestionList()).thenReturn(expectedEtatQuestionList);

        traitement.doBeforeQuery(requete);

        verify(requete).setEtatQuestionList(etatQuestionListCaptor.capture());
        assertThat(etatQuestionListCaptor.getValue()).isEqualTo(expectedEtatQuestionList);
        verify(requete, never()).setEtatQuestionList(null);
    }
}
