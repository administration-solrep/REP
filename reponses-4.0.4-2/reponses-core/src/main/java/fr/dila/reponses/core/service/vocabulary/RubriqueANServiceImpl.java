package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.reponses.api.service.vocabulary.RubriqueANService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class RubriqueANServiceImpl extends AbstractCommonVocabularyServiceImpl<String> implements RubriqueANService {

    public RubriqueANServiceImpl() {
        super(AN_RUBRIQUE.getValue(), VOCABULARY, "rubriques de l'Assemblée Nationale");
    }

    @Override
    protected String getId(DocumentModel doc) {
        return getDefaultId(doc);
    }
}
