package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.core.recherche.IndexationProvider;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.actions.SimpleIndexActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SimpleIndexActionServiceImpl implements SimpleIndexActionService {
    private static final Log LOG = LogFactory.getLog(SimpleIndexActionServiceImpl.class);

    public static final String VALUE_PARAMETER_NAME = "value";

    public static final String INDEXATION_PARAMETER_NAME = "indexationProvider";

    public Map<String, IndexationProvider> getIndexationProviderMap() {
        return ReponsesServiceLocator
            .getVocabularyService()
            .getZones()
            .stream()
            .collect(Collectors.toMap(Function.identity(), IndexationProvider::new));
    }

    public String addIndex(SpecificContext context, DocumentModel indexableDocument, IndexationProvider provider) {
        ReponsesIndexableDocument indexableDoc = getDocumentAdapted(indexableDocument);
        if (provider == null || indexableDoc == null) {
            context.getMessageQueue().addErrorToQueue("message.add.index.keyword_not_in_list");
            return StringUtils.EMPTY;
        }
        // Suppression des espaces inutiles
        provider.setLabel(StringUtils.trim(provider.getLabel()));
        if (StringUtils.isBlank(provider.getLabel())) {
            context.getMessageQueue().addErrorToQueue("message.add.new.index.not.empty");
            return StringUtils.EMPTY;
        }
        try {
            indexableDoc.addVocEntry(provider.getVocabulary(), provider.getLabel());
        } catch (Exception e) {
            context.getMessageQueue().addErrorToQueue("message.add.index.keyword_not_in_list");
            LOG.warn("echec de l'ajout de l'index ");
        }
        provider.reset();
        return StringUtils.EMPTY;
    }

    public void removeIndex(DocumentModel indexableDocument, String[] item) {
        String vocabulary = item[0].trim();
        String label = item[1].trim();
        getDocumentAdapted(indexableDocument).removeVocEntry(vocabulary, label);
    }

    public ReponsesIndexableDocument getDocumentAdapted(DocumentModel doc) {
        return doc.getAdapter(ReponsesIndexableDocument.class);
    }
}
