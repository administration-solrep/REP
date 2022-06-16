package fr.dila.reponses.core.component;

import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.service.ReponsesVocabularyServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ReponsesVocabularyComponent
    extends ServiceEncapsulateComponent<ReponsesVocabularyService, ReponsesVocabularyServiceImpl> {

    public ReponsesVocabularyComponent() {
        super(ReponsesVocabularyService.class, new ReponsesVocabularyServiceImpl());
    }
}
