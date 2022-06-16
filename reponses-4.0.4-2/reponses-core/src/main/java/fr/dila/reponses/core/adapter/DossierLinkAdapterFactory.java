package fr.dila.reponses.core.adapter;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.caselink.DossierLinkImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de document vers DossierLink.
 *
 * @author arolin
 */
public class DossierLinkAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA, CaseLinkConstants.CASE_LINK_SCHEMA);
        checkDocumentFacet(doc, CaseLinkConstants.CASE_LINK_FACET);
        return new DossierLinkImpl(doc);
    }
}
