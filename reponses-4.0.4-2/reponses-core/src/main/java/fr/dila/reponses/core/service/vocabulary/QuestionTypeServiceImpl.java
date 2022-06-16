package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.constant.VocabularyConstants.TYPE_QUESTION_RECHERCHE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.reponses.api.enumeration.QuestionTypesEnum;
import fr.dila.reponses.api.service.vocabulary.QuestionTypeService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class QuestionTypeServiceImpl
    extends AbstractCommonVocabularyServiceImpl<QuestionTypesEnum>
    implements QuestionTypeService {

    public QuestionTypeServiceImpl() {
        super(TYPE_QUESTION_RECHERCHE, VOCABULARY, "types de question");
    }

    @Override
    protected QuestionTypesEnum getId(DocumentModel doc) {
        return QuestionTypesEnum.fromString(getDefaultId(doc));
    }

    @Override
    public String idAsString(QuestionTypesEnum id) {
        if (id == null) {
            return null;
        }

        return id.getType();
    }
}
