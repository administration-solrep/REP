package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.constant.VocabularyConstants.VALIDATION_STATUT_ETAPE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.reponses.api.service.vocabulary.ValidationStatutEtapeService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ValidationStatutEtapeServiceImpl
    extends AbstractCommonVocabularyServiceImpl<Integer>
    implements ValidationStatutEtapeService {

    public ValidationStatutEtapeServiceImpl() {
        super(VALIDATION_STATUT_ETAPE, VOCABULARY, "statuts de validation d'étape");
    }

    @Override
    protected Integer getId(DocumentModel doc) {
        return Integer.valueOf(getDefaultId(doc));
    }
}
