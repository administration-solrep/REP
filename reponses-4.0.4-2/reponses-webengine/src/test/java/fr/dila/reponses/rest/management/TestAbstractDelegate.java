package fr.dila.reponses.rest.management;

import static fr.dila.st.api.constant.STWebserviceConstant.CHERCHER_ERRATA_QUESTIONS;
import static fr.dila.st.api.constant.STWebserviceConstant.CHERCHER_QUESTIONS;
import static fr.sword.xsd.reponses.Civilite.M;
import static fr.sword.xsd.reponses.Civilite.MME;
import static fr.sword.xsd.reponses.QuestionSource.SENAT;
import static fr.sword.xsd.reponses.QuestionType.QE;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.core.cases.QuestionImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.sword.xsd.reponses.Auteur;
import fr.sword.xsd.reponses.QuestionId;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class, MockitoFeature.class })
public class TestAbstractDelegate {
    @Mock
    private CloseableCoreSession session;

    @Mock
    private SSPrincipal principal;

    @Mock
    private DocumentModel document;

    @Test
    public void testStripCDATA() {
        assertThat(AbstractDelegate.stripCDATA(null)).isNull();

        assertThat(AbstractDelegate.stripCDATA("")).isEmpty();

        final String content = "mon contenu";
        String[] testStr = {
            content,
            "<![CDATA[" + content + "]]&gt;",
            "   <![CDATA[" + content + "]]&gt;",
            "   <![CDATA[" + content + "]]&gt;   ",
            "<![CDATA[" + content + "]]&gt;   ",
            "<![CDATA[" + content + "]]&gt;   autre données",
            "<![CDATA[" + content + "]]&gt;  ]]&gt; autre données"
        };

        assertThat(testStr).allSatisfy(str -> assertThat(AbstractDelegate.stripCDATA(str)).isEqualTo(content));

        final String errorEnd = "<![CDATA[" + content + "]]";
        Throwable throwable = catchThrowable(() -> AbstractDelegate.stripCDATA(errorEnd));
        assertThat(throwable).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldHasRight() {
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getGroups()).thenReturn(singletonList(CHERCHER_QUESTIONS));

        assertThat(AbstractDelegate.hasRight(session, CHERCHER_QUESTIONS)).isTrue();
    }

    @Test
    public void hasRightShouldReturnFalse() {
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getGroups()).thenReturn(singletonList(CHERCHER_ERRATA_QUESTIONS));

        assertThat(AbstractDelegate.hasRight(session, CHERCHER_QUESTIONS)).isFalse();
    }

    @Test
    public void hasRightShouldReturnFalseWithNullUserGroupList() {
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getGroups()).thenReturn(null);

        assertThat(AbstractDelegate.hasRight(session, CHERCHER_QUESTIONS)).isFalse();
    }

    @Test
    public void shouldGetMinisteresIdSetFromLogin() {
        Set<String> expectedResult = singleton("12345");
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getMinistereIdSet()).thenReturn(expectedResult);

        assertThat(AbstractDelegate.getMinisteresIdSetFromLogin(session)).isEqualTo(expectedResult);
    }

    @Test
    public void getMinisteresIdSetFromLoginShouldReturnEmptySet() {
        when(session.getPrincipal()).thenReturn(null);

        assertThat(AbstractDelegate.getMinisteresIdSetFromLogin(session)).isEmpty();
    }

    @Test
    public void shouldGetFirstMinistereLoginFromSession() {
        String firstMinistereId = "12345";
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getMinistereIdSet()).thenReturn(ImmutableSet.of(firstMinistereId, "12346", "12347"));

        assertThat(AbstractDelegate.getFirstMinistereLoginFromSession(session)).isEqualTo(firstMinistereId);
    }

    @Test
    public void getFirstMinistereLoginFromSessionShouldReturnNull() {
        when(session.getPrincipal()).thenReturn(null);

        assertThat(AbstractDelegate.getFirstMinistereLoginFromSession(session)).isNull();
    }

    @Test
    public void shouldGetAuteurFromQuestion() {
        String civiliteAuteur = "civiliteAuteur";
        String groupePolitiqueKey = "groupePolitique";
        String idMandatKey = "idMandat";
        String nomAuteurKey = "nomAuteur";
        String prenomAuteurKey = "prenomAuteur";
        String circonscriptionAuteurKey = "circonscriptionAuteur";

        Map<String, String> values = new ImmutableMap.Builder<String, String>()
            .put(civiliteAuteur, "MME")
            .put(groupePolitiqueKey, "Groupe Politique")
            .put(idMandatKey, "Id Mandat")
            .put(nomAuteurKey, "Nom")
            .put(prenomAuteurKey, "Prenom")
            .put(circonscriptionAuteurKey, "Circonscription Auteur")
            .build();

        Question question = new QuestionImpl(document);
        question.setCiviliteAuteur(values.get(civiliteAuteur));
        question.setGroupePolitique(values.get(groupePolitiqueKey));
        question.setIdMandat(values.get(idMandatKey));
        question.setNomAuteur(values.get(nomAuteurKey));
        question.setPrenomAuteur(values.get(prenomAuteurKey));
        question.setCirconscriptionAuteur(values.get(circonscriptionAuteurKey));

        doAnswer(
                invocation -> {
                    String property = (String) invocation.getArguments()[1];
                    return values.get(property);
                }
            )
            .when(document)
            .getProperty(any(String.class), any(String.class));

        Auteur result = AbstractDelegate.getAuteurFromQuestion(question);

        assertThat(result.getCivilite()).isEqualTo(MME);
        assertThat(result.getGrpPol()).isEqualTo(values.get(groupePolitiqueKey));
        assertThat(result.getIdMandat()).isEqualTo(values.get(idMandatKey));
        assertThat(result.getNom()).isEqualTo(values.get(nomAuteurKey));
        assertThat(result.getPrenom()).isEqualTo(values.get(prenomAuteurKey));
        assertThat(result.getCirconscription()).isEqualTo(values.get(circonscriptionAuteurKey));
    }

    @Test
    public void getAuteurFromQuestionShouldReturnEmptyValues() {
        when(document.getProperty(any(String.class), any(String.class))).thenReturn(null);

        Auteur result = AbstractDelegate.getAuteurFromQuestion(new QuestionImpl(document));

        assertThat(result.getCivilite()).isEqualTo(M);
        assertThat(result.getGrpPol()).isEmpty();
        assertThat(result.getIdMandat()).isEmpty();
        assertThat(result.getNom()).isEmpty();
        assertThat(result.getPrenom()).isEmpty();
        assertThat(result.getCirconscription()).isEmpty();
    }

    @Test
    public void shouldIsValidIdQuestion() {
        QuestionId idQuestion = new QuestionId();
        idQuestion.setNumeroQuestion(1);
        idQuestion.setSource(SENAT);
        idQuestion.setType(QE);

        assertThat(AbstractDelegate.isValidIdQuestion(idQuestion)).isTrue();
    }

    @Test
    public void isValidIdQuestionShouldFalseWithNullQuestionId() {
        assertThat(AbstractDelegate.isValidIdQuestion(null)).isFalse();
    }

    @Test
    public void isValidIdQuestionShouldFalseWithNumeroIsZero() {
        QuestionId idQuestion = new QuestionId();
        idQuestion.setNumeroQuestion(0);
        idQuestion.setSource(SENAT);
        idQuestion.setType(QE);

        assertThat(AbstractDelegate.isValidIdQuestion(null)).isFalse();
    }

    @Test
    public void isValidIdQuestionShouldFalseWithNullSource() {
        QuestionId idQuestion = new QuestionId();
        idQuestion.setNumeroQuestion(1);
        idQuestion.setType(QE);

        assertThat(AbstractDelegate.isValidIdQuestion(null)).isFalse();
    }

    @Test
    public void isValidIdQuestionShouldFalseWithNullType() {
        QuestionId idQuestion = new QuestionId();
        idQuestion.setNumeroQuestion(1);
        idQuestion.setSource(SENAT);

        assertThat(AbstractDelegate.isValidIdQuestion(null)).isFalse();
    }
}
