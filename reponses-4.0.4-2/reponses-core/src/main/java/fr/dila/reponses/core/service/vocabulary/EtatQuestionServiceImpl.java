package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.reponses.api.service.vocabulary.EtatQuestionService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class EtatQuestionServiceImpl
    extends AbstractCommonVocabularyServiceImpl<String>
    implements EtatQuestionService {

    public EtatQuestionServiceImpl() {
        super(ETAT_QUESTION, VOCABULARY, "états de questions");
    }

    @Override
    protected String getId(DocumentModel doc) {
        return getDefaultId(doc);
    }
}
