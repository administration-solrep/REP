package fr.dila.reponses.api.enumeration;

import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QC;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QE;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QG;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QOAD;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QOAE;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QOSD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.List;
import org.junit.Test;

public class QuestionTypesEnumTest {
    private static final String INVALID_VALUE = "ZZ";

    @Test
    public void fromValue() {
        QuestionTypesEnum questionType = QuestionTypesEnum.fromString("QE");

        assertThat(questionType).isEqualTo(QE);
    }

    @Test
    public void fromValueWithError() {
        Throwable throwable = catchThrowable(() -> QuestionTypesEnum.fromString(INVALID_VALUE));
        assertThat(throwable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Valeur inattendue '" + INVALID_VALUE + "'");
    }

    @Test
    public void toStrings() {
        List<String> values = QuestionTypesEnum.toStrings();

        assertThat(values)
            .containsExactly(
                QG.toString(),
                QC.toString(),
                QE.toString(),
                QOAE.toString(),
                QOAD.toString(),
                QOSD.toString()
            );
    }

    @Test
    public void isValueInEnumIsTrue() {
        assertThat(QuestionTypesEnum.isValueInEnum("QOSD")).isTrue();
    }

    @Test
    public void isValueInEnumIsFalse() {
        assertThat(QuestionTypesEnum.isValueInEnum(INVALID_VALUE)).isFalse();
    }
}
