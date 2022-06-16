package fr.dila.reponses.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.reponses.api.cases.Question;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuestionUtilsTest {
    @Mock
    private Question question;

    @Test
    public void joinMotsClefs() {
        when(question.getMotsClef()).thenReturn(ImmutableList.of("mot 1", "mot 2", "mot 3"));

        String motsClefs = QuestionUtils.joinMotsClefs(question);

        assertThat(motsClefs).isEqualTo("mot 1, mot 2, mot 3");
    }

    @Test
    public void joinMotsClefsWithEmptyList() {
        when(question.getMotsClef()).thenReturn(Collections.emptyList());

        String motsClefs = QuestionUtils.joinMotsClefs(question);

        assertThat(motsClefs).isEqualTo("");
    }

    @Test
    public void joinMotsClefsWithoutQuestion() {
        assertThat(QuestionUtils.joinMotsClefs(null)).isNull();
    }
}
