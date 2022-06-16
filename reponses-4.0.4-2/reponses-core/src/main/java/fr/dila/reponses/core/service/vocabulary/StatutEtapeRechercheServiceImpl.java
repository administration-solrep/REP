package fr.dila.reponses.core.service.vocabulary;

import static fr.dila.reponses.api.constant.VocabularyConstants.STATUT_ETAPE_RECHERCHE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.reponses.api.service.vocabulary.StatutEtapeRechercheService;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class StatutEtapeRechercheServiceImpl
    extends AbstractCommonVocabularyServiceImpl<String>
    implements StatutEtapeRechercheService {

    public StatutEtapeRechercheServiceImpl() {
        super(STATUT_ETAPE_RECHERCHE, VOCABULARY, "statuts d'étape");
    }

    @Override
    protected String getId(DocumentModel doc) {
        return getDefaultId(doc);
    }
}
