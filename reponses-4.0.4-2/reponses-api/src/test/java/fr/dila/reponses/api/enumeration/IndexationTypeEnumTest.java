package fr.dila.reponses.api.enumeration;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RENVOI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.Test;

public class IndexationTypeEnumTest {
    private static final String INVALID_VALUE = "ZZ_rubrique";

    @Test
    public void fromValue() {
        IndexationTypeEnum indexationType = IndexationTypeEnum.fromString("SE_renvoi");

        assertThat(indexationType).isEqualTo(SE_RENVOI);
    }

    @Test
    public void fromValueWithError() {
        Throwable throwable = catchThrowable(() -> IndexationTypeEnum.fromString(INVALID_VALUE));
        assertThat(throwable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Valeur inattendue '" + INVALID_VALUE + "'");
    }
}
