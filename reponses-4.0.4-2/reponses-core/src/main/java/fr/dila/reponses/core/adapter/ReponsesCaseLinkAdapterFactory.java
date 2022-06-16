package fr.dila.reponses.core.adapter;

import static fr.dila.cm.caselink.CaseLinkConstants.CASE_LINK_FACET;
import static fr.dila.cm.caselink.CaseLinkConstants.CASE_LINK_SCHEMA;

import fr.dila.reponses.core.caselink.ReponsesActionableCaseLinkImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ReponsesCaseLinkAdapterFactory implements STDocumentAdapterFactory {

    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentFacet(doc, CASE_LINK_FACET);
        checkDocumentSchemas(doc, CASE_LINK_SCHEMA);
        return new ReponsesActionableCaseLinkImpl(doc);
    }
}
