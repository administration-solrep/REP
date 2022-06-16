package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_SCHEMA;
import static fr.dila.reponses.api.constant.VocabularyConstants.LEGISLATURE;

import fr.dila.reponses.api.service.vocabulary.LegislatureService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class LegislatureServiceImpl extends AbstractCommonVocabularyServiceImpl<Integer> implements LegislatureService {

    public LegislatureServiceImpl() {
        super(LEGISLATURE, LEGISLATURE_SCHEMA, "législatures");
    }

    @Override
    protected Integer getId(DocumentModel doc) {
        return Integer.valueOf(getDefaultId(doc));
    }
}
