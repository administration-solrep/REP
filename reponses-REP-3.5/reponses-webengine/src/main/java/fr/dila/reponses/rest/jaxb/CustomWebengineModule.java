package fr.dila.reponses.rest.jaxb;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.webengine.app.WebEngineModule;


public class CustomWebengineModule extends WebEngineModule {

	private static final Log LOGGER = LogFactory.getLog(CustomWebengineModule.class);
	
    @Override
    public Set<Class<?>> getClasses() {
    	
    	if(LOGGER.isTraceEnabled()) {
    		LOGGER.trace("CustomWebengineModule - getClasses");
    	}
    	
        Set<Class<?>> result = super.getClasses();
        result.add(CustomMarshallerProvider.class);
        
    	if(LOGGER.isTraceEnabled()) {
    		LOGGER.trace("CustomWebengineModule - added " + CustomMarshallerProvider.class.getName());
    	}
        
        return result;
    }
}