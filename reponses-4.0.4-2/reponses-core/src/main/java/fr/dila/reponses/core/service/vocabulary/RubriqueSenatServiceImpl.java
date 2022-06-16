package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.reponses.api.service.vocabulary.RubriqueSenatService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class RubriqueSenatServiceImpl
    extends AbstractCommonVocabularyServiceImpl<String>
    implements RubriqueSenatService {

    public RubriqueSenatServiceImpl() {
        super(SE_RUBRIQUE.getValue(), VOCABULARY, "rubriques du Sénat");
    }

    @Override
    protected String getId(DocumentModel doc) {
        return getDefaultId(doc);
    }
}
