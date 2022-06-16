package fr.dila.reponses.api.exception;

import fr.dila.ss.api.exception.SSException;
import org.nuxeo.ecm.core.api.NuxeoException;

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

    public ReponsesException(String message, NuxeoException cause) {
        super(message, cause);
    }

    public ReponsesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReponsesException(Throwable cause) {
        super(cause);
    }

    public ReponsesException(NuxeoException cause) {
        super(cause);
    }
}
