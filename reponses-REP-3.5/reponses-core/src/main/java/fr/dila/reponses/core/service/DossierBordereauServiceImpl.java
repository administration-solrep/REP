package fr.dila.reponses.core.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.DossierBordereauService;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Implémentation du service de distribution des dossiers de Reponses.
 * 
 * @author asatre
 */
public class DossierBordereauServiceImpl extends fr.dila.st.core.service.LogDocumentUpdateServiceImpl implements DossierBordereauService{

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Default constructor
     */
    public DossierBordereauServiceImpl(){
    	super();
    }
    
    @Override
    protected Map<String, Object> getMap(DocumentModel dossierDocument) throws ClientException{
    	// on récupère toutes les propriétés liées au bordereau-
         return dossierDocument.getProperties(DossierConstants.INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA);
    }
    
    @Override
    protected void fireEvent(final CoreSession session, final DocumentModel ancienDossierOrQuestion, final Entry<String, Object> entry, final Object nouveauDossierValue, final String ancienDossierValueLabel) throws ClientException {
    	final JournalService journalService = STServiceLocator.getJournalService();
    	DocumentModel ancienDossier; 
        if(ancienDossierOrQuestion.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA)){
    		ancienDossier = session.getDocument(ancienDossierOrQuestion.getAdapter(Question.class).getDossierRef());
    	} else {
    		ancienDossier = ancienDossierOrQuestion;
    	}
    	
    	// journalisation de l'action dans les logs
		ReponsesVocabularyService vocService = ReponsesServiceLocator.getVocabularyService();
		String bordereauLabel = vocService.getLabelFromId(STVocabularyConstants.BORDEREAU_LABEL, entry.getKey().substring(8), STVocabularyConstants.COLUMN_LABEL);
		String comment = bordereauLabel + " : '" + (nouveauDossierValue != null ? nouveauDossierValue : "") + "' remplace '" + (ancienDossierValueLabel != null ?  ancienDossierValueLabel : "") + "'";
		journalService.journaliserActionBordereau(session, ancienDossier, STEventConstant.BORDEREAU_UPDATE, comment);
	}

    @Override
    protected Set<String> getUnLoggableEntry() throws ClientException {
        return new HashSet<String>();
    }

    

}
