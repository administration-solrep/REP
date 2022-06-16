package fr.dila.reponses.core.recherche.traitement;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.enumeration.StatutReponseEnum.EN_ATTENTE;
import static fr.dila.reponses.api.enumeration.StatutReponseEnum.PUBLIE;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.enumeration.StatutReponseEnum;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.core.feature.SolonMockitoFeature;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
public class ReponsePublieeTraitementTest {
    private ReponsePublieeTraitement traitement;

    @Mock
    private Requete requete;

    @Captor
    private ArgumentCaptor<String> clauseCaracteristiqueCaptor;

    @Before
    public void setUp() {
        traitement = new ReponsePublieeTraitement();
    }

    @Test
    public void doBeforeQueryWithoutCaracteristiqueQuestion() {
        when(requete.getCaracteristiqueQuestion()).thenReturn(new ArrayList<>());

        traitement.doBeforeQuery(requete);

        verify(requete).setClauseCaracteristiques(clauseCaracteristiqueCaptor.capture());
        assertThat(clauseCaracteristiqueCaptor.getValue()).isNull();
    }

    @Test
    public void doBeforeQueryWithCaracteristiqueQuestion() {
        List<StatutReponseEnum> expectedCaracteristiqueQuestion = newArrayList(EN_ATTENTE, PUBLIE);
        when(requete.getCaracteristiqueQuestion())
            .thenReturn(expectedCaracteristiqueQuestion.stream().map(StatutReponseEnum::name).collect(toList()));

        traitement.doBeforeQuery(requete);

        verify(requete).setClauseCaracteristiques(clauseCaracteristiqueCaptor.capture());
        assertThat(clauseCaracteristiqueCaptor.getValue())
            .isEqualTo(
                expectedCaracteristiqueQuestion
                    .stream()
                    .map(StatutReponseEnum::getWhereClause)
                    .collect(Collectors.joining(" OR "))
            );
    }
}
