package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.reponses.core.service.QuestionConnexeServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class QuestionConnexeComponent
    extends ServiceEncapsulateComponent<QuestionConnexeService, QuestionConnexeServiceImpl> {

    public QuestionConnexeComponent() {
        super(QuestionConnexeService.class, new QuestionConnexeServiceImpl());
    }
}
