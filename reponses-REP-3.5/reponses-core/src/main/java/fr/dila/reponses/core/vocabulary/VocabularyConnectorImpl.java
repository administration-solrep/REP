package fr.dila.reponses.core.vocabulary;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.reponses.api.vocabulary.VocabularyConnector;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.service.VocabularyService;


/**
 * 
 * Un élément d'indexation est par exemple une rubrique de l'assemblée nationale, ou du sénat
 * @author jgomez
 *
 */ 
public class VocabularyConnectorImpl implements VocabularyConnector{
    
    
    protected String directoryName;
    
    protected VocabularyService service = null;
    
    
    public VocabularyConnectorImpl(String directoryName,VocabularyService service) throws ClientException{
        super();
        if (service == null){
            throw new ClientException("Le service ne peut pas être nul");
        }
        this.service = service;
        this.directoryName = directoryName;
    }
    
    /**
     * @see VocabularyConnector#getSuggestion(String)
     */
    public Boolean check(String valueToCheck){
            return service.checkData(directoryName,STVocabularyConstants.COLUMN_LABEL, valueToCheck);
    }
    
    /**
     * @see VocabularyConnector#getSuggestion(String)
     */
    public List<String> getSuggestion(String suggestion){
        return service.getSuggestions(suggestion, directoryName);
    }
}
