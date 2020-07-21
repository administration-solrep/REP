package fr.dila.reponses.web.suggestion;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

public interface ISuggestionProvider {

    public  static final int MAX_RESULTS = 10;
    
	public abstract String getName();

	public abstract List<String> getSuggestions(Object input)
			throws ClientException, Exception;

}