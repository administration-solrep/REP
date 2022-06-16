package fr.dila.reponses.core.service.vocabulary;

import fr.dila.reponses.api.service.vocabulary.EtatQuestionService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class EtatQuestionComponent extends ServiceEncapsulateComponent<EtatQuestionService, EtatQuestionServiceImpl> {

    public EtatQuestionComponent() {
        super(EtatQuestionService.class, new EtatQuestionServiceImpl());
    }
}
