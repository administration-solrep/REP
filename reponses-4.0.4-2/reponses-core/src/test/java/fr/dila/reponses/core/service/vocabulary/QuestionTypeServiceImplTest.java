package fr.dila.reponses.core.service.vocabulary;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.VocabularyConstants.TYPE_QUESTION_RECHERCHE;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QC;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QE;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QG;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QOSD;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.ID_PROPERTY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.LABEL_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.enumeration.QuestionTypesEnum;
import fr.dila.reponses.api.service.vocabulary.QuestionTypeService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class QuestionTypeServiceImplTest {
    private QuestionTypeService service;

    @Mock
    private DocumentModel typeQuestionQEDoc;

    @Mock
    private DocumentModel typeQuestionQOSDDoc;

    @Mock
    private DocumentModel typeQuestionQCDoc;

    @Mock
    private DocumentModel typeQuestionQGDoc;

    @Mock
    @RuntimeService
    private VocabularyService vocabularyService;

    @Before
    public void setUp() {
        service = new QuestionTypeServiceImpl();
    }

    @Test
    public void getQuestionTypes() {
        ImmutablePair<QuestionTypesEnum, String> questionTypeQE = ImmutablePair.of(QE, "Questions Ecrites");
        ImmutablePair<QuestionTypesEnum, String> questionTypeQOSD = ImmutablePair.of(
            QOSD,
            "Questions Orales Sans Débat"
        );
        ImmutablePair<QuestionTypesEnum, String> questionTypeQC = ImmutablePair.of(QC, "Questions Cribles");
        ImmutablePair<QuestionTypesEnum, String> questionTypeQG = ImmutablePair.of(QG, "Questions au Gouvernement");

        when(vocabularyService.getAllEntry(TYPE_QUESTION_RECHERCHE))
            .thenReturn(
                new DocumentModelListImpl(
                    newArrayList(typeQuestionQEDoc, typeQuestionQOSDDoc, typeQuestionQCDoc, typeQuestionQGDoc)
                )
            );

        when(typeQuestionQEDoc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(questionTypeQE.getLeft().name());
        when(typeQuestionQEDoc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(questionTypeQE.getRight());

        when(typeQuestionQOSDDoc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(questionTypeQOSD.getLeft().name());
        when(typeQuestionQOSDDoc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(questionTypeQOSD.getRight());

        when(typeQuestionQCDoc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(questionTypeQC.getLeft().name());
        when(typeQuestionQCDoc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(questionTypeQC.getRight());

        when(typeQuestionQGDoc.getProperty(VOCABULARY, ID_PROPERTY)).thenReturn(questionTypeQG.getLeft().name());
        when(typeQuestionQGDoc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(questionTypeQG.getRight());

        List<ImmutablePair<QuestionTypesEnum, String>> questionTypes = service.getEntries();

        assertThat(questionTypes).containsExactly(questionTypeQG, questionTypeQC, questionTypeQE, questionTypeQOSD);
    }

    @Test
    public void getQuestionTypesWithError() {
        when(vocabularyService.getAllEntry(TYPE_QUESTION_RECHERCHE)).thenReturn(null);

        Throwable throwable = catchThrowable(() -> service.getEntries());
        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessage("Une erreur s'est produite lors de la récupération des types de question");
    }
}
