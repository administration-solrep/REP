package fr.dila.reponses.api.enumeration;

import static fr.dila.reponses.api.enumeration.IndexModeEnum.INDEX_COMPL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.entry;

import java.util.Map;
import org.junit.Test;

public class IndexModeEnumTest {
    private static final String INVALID_VALUE = "ZZZZ";

    @Test
    public void getLabelKeys() {
        Map<String, String> labelKeys = IndexModeEnum.getLabelKeys();

        assertThat(labelKeys)
            .containsExactly(
                entry("INDEX_COMPL", "indexmode.compl"),
                entry("INDEX_ORIG", "indexmode.orig"),
                entry("TOUS", "indexmode.tous")
            );
    }

    @Test
    public void fromValue() {
        IndexModeEnum indexMode = IndexModeEnum.fromValue("INDEX_COMPL");

        assertThat(indexMode).isEqualTo(INDEX_COMPL);
    }

    @Test
    public void fromValueWithError() {
        Throwable throwable = catchThrowable(() -> IndexModeEnum.fromValue(INVALID_VALUE));
        assertThat(throwable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Valeur inattendue '" + INVALID_VALUE + "'");
    }
}
