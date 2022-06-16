package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.QuestionTypeService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class QuestionTypeComponent extends ServiceEncapsulateComponent<QuestionTypeService, QuestionTypeServiceImpl> {

    public QuestionTypeComponent() {
        super(QuestionTypeService.class, new QuestionTypeServiceImpl());
    }
}
