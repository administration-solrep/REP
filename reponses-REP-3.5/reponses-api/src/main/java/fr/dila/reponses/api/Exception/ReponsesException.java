package fr.dila.reponses.api.Exception;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.ss.api.exception.SSException;

/**
 * Classe Exception de Reponses 
 */
public class ReponsesException extends SSException {

    private static final long serialVersionUID = -2474729877394967886L;

    public ReponsesException() {
    	super("Exception REPONSES");
    }

    public ReponsesException(String message) {
        super(message);
    }

    public ReponsesException(String message, ClientException cause) {
        super(message, cause);
    }

    public ReponsesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReponsesException(Throwable cause) {
        super(cause);
    }

    public ReponsesException(ClientException cause) {
        super(cause);
    }

}
