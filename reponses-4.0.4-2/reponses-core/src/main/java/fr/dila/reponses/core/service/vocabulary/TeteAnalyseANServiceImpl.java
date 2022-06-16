package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_ANALYSE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.reponses.api.service.vocabulary.TeteAnalyseANService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class TeteAnalyseANServiceImpl
    extends AbstractCommonVocabularyServiceImpl<String>
    implements TeteAnalyseANService {

    public TeteAnalyseANServiceImpl() {
        super(AN_ANALYSE.getValue(), VOCABULARY, "titres de l'Assemblée Nationale");
    }

    @Override
    protected String getId(DocumentModel doc) {
        return getDefaultId(doc);
    }
}
