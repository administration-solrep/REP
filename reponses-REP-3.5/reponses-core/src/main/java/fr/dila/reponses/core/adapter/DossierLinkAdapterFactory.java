package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.caselink.DossierLinkImpl;

/**
 * Adapteur de document vers DossierLink.
 * 
 * @author arolin
 */
public class DossierLinkAdapterFactory implements DocumentAdapterFactory {

    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasSchema(DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA)) {
            throw new CaseManagementRuntimeException(
                    "Document should contain schema " + DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA);
        }
        if (!doc.hasSchema(CaseLinkConstants.CASE_LINK_SCHEMA)) {
            throw new CaseManagementRuntimeException(
                    "Document should contain schema " + CaseLinkConstants.CASE_LINK_SCHEMA);
        }
        if (!doc.hasFacet(CaseLinkConstants.CASE_LINK_FACET)) {
            throw new CaseManagementRuntimeException(
                    "Document should contain facet " + CaseLinkConstants.CASE_LINK_FACET);
        }
    }

	@Override
	public Object getAdapter(DocumentModel doc,@SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new DossierLinkImpl(doc);
	}

}
