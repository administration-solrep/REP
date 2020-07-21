package fr.dila.reponses.core.adapter;

import static fr.dila.cm.caselink.CaseLinkConstants.CASE_LINK_FACET;
import static fr.dila.cm.caselink.CaseLinkConstants.CASE_LINK_SCHEMA;
import static fr.dila.cm.cases.CaseConstants.DISTRIBUTION_SCHEMA;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.cases.HasParticipants;
import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.core.caselink.ReponsesActionableCaseLinkImpl;

public class ReponsesCaseLinkAdapterFactory implements DocumentAdapterFactory {

    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocument(doc);
        HasParticipants adapter = doc.getAdapter(HasParticipants.class);
        return new ReponsesActionableCaseLinkImpl(doc, adapter);
    }

    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasFacet(CASE_LINK_FACET)) {
            throw new CaseManagementRuntimeException(
                    "Document should have facet " + CASE_LINK_FACET);
        }
        if (!doc.hasSchema(DISTRIBUTION_SCHEMA)) {
            throw new CaseManagementRuntimeException(
                    "Document should contain schema " + DISTRIBUTION_SCHEMA);
        }
        if (!doc.hasSchema(CASE_LINK_SCHEMA)) {
            throw new CaseManagementRuntimeException(
                    "Document should contain schema " + CASE_LINK_SCHEMA);
        }
    }

//    protected boolean isActionable(DocumentModel doc) {
//        try {
//            Boolean actionable = (Boolean) doc.getProperty(STSchemaConstant.CASE_LINK_SCHEMA, STSchemaConstant.CASE_LINK_IS_ACTIONABLE_PROPERTY);
//            if (actionable == null) {
//                return false;
//            }
//            return actionable.booleanValue();
//        } catch (PropertyException e) {
//            throw new ClientRuntimeException(e);
//        } catch (ClientException e) {
//            throw new ClientRuntimeException(e);
//        }
//    }
}
