package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.constant.VocabularyConstants.GROUPE_POLITIQUE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.reponses.api.service.vocabulary.GroupePolitiqueService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class GroupePolitiqueServiceImpl
    extends AbstractCommonVocabularyServiceImpl<String>
    implements GroupePolitiqueService {

    public GroupePolitiqueServiceImpl() {
        super(GROUPE_POLITIQUE, VOCABULARY, "groupes politiques");
    }

    @Override
    protected String getId(DocumentModel doc) {
        return getDefaultId(doc);
    }
}
