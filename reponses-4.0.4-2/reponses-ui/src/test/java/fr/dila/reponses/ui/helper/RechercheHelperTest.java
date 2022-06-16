package fr.dila.reponses.ui.helper;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;

public class RechercheHelperTest {

    @Test
    public void getMotsClesByIndexationType() {
        List<String> motsCles = ImmutableList.of(
            "Tête d'analyse : Tête d'analyse : Liste générale",
            "Rubrique : Tête d'analyse : Liste générale",
            "Tête d'analyse : associations gestionnaires",
            "Test"
        );

        List<String> results = RechercheHelper.getMotsClesByIndexationType(motsCles, TA_RUBRIQUE);

        assertThat(results).containsExactly("Tête d'analyse : Liste générale", "associations gestionnaires");
    }
}
