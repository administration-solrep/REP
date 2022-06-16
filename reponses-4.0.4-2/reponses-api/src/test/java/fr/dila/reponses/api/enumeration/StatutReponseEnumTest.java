package fr.dila.reponses.api.enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Map;
import org.junit.Test;

public class StatutReponseEnumTest {

    @Test
    public void getLabelKeys() {
        Map<String, String> labelKeys = StatutReponseEnum.getLabelKeys();

        assertThat(labelKeys)
            .containsExactly(
                entry("EN_ATTENTE", "statutreponse.enattente"),
                entry("REPONDU", "statutreponse.repondu"),
                entry("PUBLIE", "statutreponse.publie")
            );
    }
}
