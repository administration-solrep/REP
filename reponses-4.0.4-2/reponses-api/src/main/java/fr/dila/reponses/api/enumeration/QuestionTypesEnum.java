package fr.dila.reponses.api.enumeration;

import fr.dila.reponses.api.constant.VocabularyConstants;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum QuestionTypesEnum {
    QG(VocabularyConstants.QUESTION_TYPE_QG),
    QC(VocabularyConstants.QUESTION_TYPE_QC),
    QE(VocabularyConstants.QUESTION_TYPE_QE),
    QOAE(VocabularyConstants.QUESTION_TYPE_QOAE),
    QOAD(VocabularyConstants.QUESTION_TYPE_QOAD),
    QOSD(VocabularyConstants.QUESTION_TYPE_QOSD);

    private final String type;

    QuestionTypesEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static boolean isValueInEnum(String value) {
        return Stream.of(values()).anyMatch(questionType -> Objects.equals(questionType.toString(), value));
    }

    public static List<String> toStrings() {
        return Stream.of(values()).map(QuestionTypesEnum::toString).collect(Collectors.toList());
    }

    public static QuestionTypesEnum fromString(String value) {
        return Stream
            .of(values())
            .filter(questionType -> Objects.equals(questionType.getType(), value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Valeur inattendue '" + value + "'"));
    }
}
