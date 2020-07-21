package fr.dila.reponses.core.vocabulary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.reponses.api.vocabulary.VocabularyConnector;
import fr.dila.reponses.api.vocabulary.VocabularyGroupConnector;
import fr.dila.st.api.service.VocabularyService;


/**
 * L'implémentation d'un groupe de connecteur de vocabulaire.
 * Permet d'effectuer les mêmes actions que sur un connecteur de vocabulaire.
 * @author jgomez
 *
 */
public class VocabularyConnectorGroupImpl implements VocabularyGroupConnector{
    
    
    protected VocabularyService service = null;
    protected List<VocabularyConnector> indexationEltList;
    
    public VocabularyConnectorGroupImpl(VocabularyService service) throws ClientException{
        super();
        if (service == null){
            throw new ClientException("Le service ne peut pas être nul");
        }
        this.service = service;
        this.indexationEltList = new ArrayList<VocabularyConnector>();
    }
    /**
     * @see VocabularyGroupConnector#add(VocabularyConnector)
     */
    public void add(VocabularyConnector elt){
        indexationEltList.add(elt);
    }
    
    /**
     * @see VocabularyConnector#check(String)
     */
    public Boolean check(String valueToCheck){
        boolean check = false;
        for (VocabularyConnector elt : indexationEltList){
            check = check || elt.check(valueToCheck);
        } 
        return check;
    }
    
    /**
     * @see VocabularyConnector#getSuggestion(String)
     */
    public List<String> getSuggestion(String suggestion){
        List<String> result_list = new ArrayList<String>();
        for (VocabularyConnector elt : indexationEltList){
            result_list.addAll(elt.getSuggestion(suggestion));
        }
        List<String> result_list_uniq = new ArrayList<String>(new HashSet<String>(result_list));
        return result_list_uniq;
    }

}
