package fr.dila.reponses.api.exception;

import java.util.List;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Exception remonté lors de l'import de l'organigramme
 *
 */
public class ImportOrganigrammeException extends NuxeoException {
    private static final long serialVersionUID = -7032242998188508480L;

    private final List<String> errorMessages;

    public ImportOrganigrammeException(List<String> errorMessages) {
        super();
        this.errorMessages = errorMessages;
    }

    public List<String> getMessageKeys() {
        return errorMessages;
    }
}
