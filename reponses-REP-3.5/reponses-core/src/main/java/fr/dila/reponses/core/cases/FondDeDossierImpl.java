package fr.dila.reponses.core.cases;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.dila.st.core.util.PropertyUtil;


public class FondDeDossierImpl extends STDomainObjectImpl implements FondDeDossier {
	
    private static final long serialVersionUID = 1L;
    
    public FondDeDossierImpl(DocumentModel document) {
        super(document);
    }

    @Override
    public List<DocumentModel> getRepertoireDocument(CoreSession session) {
        List<DocumentModel> repertoireDocuments = new ArrayList<DocumentModel>();
        DocumentRef fddRef = document.getRef();
        try {
            DocumentModelList documentModelList = session.getChildren(fddRef);
            repertoireDocuments.addAll(documentModelList);
        } catch (ClientException e) {
            throw new CaseManagementRuntimeException("getRepertoireDocument fail ", e);
        }
        return repertoireDocuments;
    }
    
    @Override
    public void setRepertoireParlementId(String documentId){
        PropertyUtil.setProperty(document, DossierConstants.FOND_DE_DOSSIER_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ID_REPERTOIRE_PARLEMENT, documentId);
    }
    
    @Override
    public void setRepertoireMinistereId(String documentId){
        PropertyUtil.setProperty(document, DossierConstants.FOND_DE_DOSSIER_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ID_REPERTOIRE_MINISTERE, documentId);
    }
    
    @Override
    public void setRepertoireSggId(String documentId){
        PropertyUtil.setProperty(document, DossierConstants.FOND_DE_DOSSIER_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ID_REPERTOIRE_SGG, documentId);
    }
    
    @Override
    public String getRepertoireParlementId(){
        return PropertyUtil.getStringProperty(document, DossierConstants.FOND_DE_DOSSIER_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ID_REPERTOIRE_PARLEMENT);
    }
    
    @Override
    public String getRepertoireMinistereId(){
        return PropertyUtil.getStringProperty(document, DossierConstants.FOND_DE_DOSSIER_DOCUMENT_SCHEMA,
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ID_REPERTOIRE_MINISTERE);
    }
    
    @Override
    public String getRepertoireSggId(){ 
        return PropertyUtil.getStringProperty(document, DossierConstants.FOND_DE_DOSSIER_DOCUMENT_SCHEMA,
            ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_ID_REPERTOIRE_SGG);
        }

}
