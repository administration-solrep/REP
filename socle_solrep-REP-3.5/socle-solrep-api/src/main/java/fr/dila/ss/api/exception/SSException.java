package fr.dila.ss.api.exception;

import org.nuxeo.ecm.core.api.ClientException;

public class SSException extends ClientException {
    
    /**
     * 
     */
    private static final long serialVersionUID = -4025730491287895725L;

    public SSException(String message) {
        super(message);
    }
    
    public SSException(String message, Throwable exc) {
        super(message, exc);
    }
    
    public SSException(String message, ClientException cause) {
        super(message, cause);
    }

    public SSException(Throwable cause) {
        super(cause);
    }

    public SSException(ClientException cause) {
        super(cause);
    }
    
}
