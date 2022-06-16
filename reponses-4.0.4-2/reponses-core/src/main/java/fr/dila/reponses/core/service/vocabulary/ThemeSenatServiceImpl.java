package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.reponses.api.service.vocabulary.ThemeSenatService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ThemeSenatServiceImpl extends AbstractCommonVocabularyServiceImpl<String> implements ThemeSenatService {

    public ThemeSenatServiceImpl() {
        super(SE_THEME.getValue(), VOCABULARY, "thèmes du Sénat");
    }

    @Override
    protected String getId(DocumentModel doc) {
        return getDefaultId(doc);
    }
}
