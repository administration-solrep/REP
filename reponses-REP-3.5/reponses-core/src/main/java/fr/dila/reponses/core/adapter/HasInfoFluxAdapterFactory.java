package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.flux.HasInfosFluxImpl;
/**
 * 
 * L'adapteur pour un objet comportant des informations de flux.
 * @author jgomez
 *
 */
//TODO JGZ: Pour l'instant, on prend un objet ayant le schéma question
// L'idéal serait d'avoir un schéma spécifique contenant uniquement les informations de flux.
public class HasInfoFluxAdapterFactory implements DocumentAdapterFactory {
    protected void checkDocument(DocumentModel doc) {
    if (!doc.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA)) {
            throw new CaseManagementRuntimeException(
                    "Document should contain schema " + DossierConstants.QUESTION_DOCUMENT_SCHEMA +" ,your type :" + doc.getType());
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc,@SuppressWarnings("rawtypes") Class arg1) {
        checkDocument(doc);
        return new HasInfosFluxImpl(doc);
    }
}
