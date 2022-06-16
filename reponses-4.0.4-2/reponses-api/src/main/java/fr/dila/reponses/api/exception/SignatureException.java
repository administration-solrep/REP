package fr.dila.reponses.api.exception;

import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Exception lever si le dossier n'a pu être signé car la réponse est vide
 * @author asatre
 *
 */
public class SignatureException extends NuxeoException {

    public SignatureException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 7669604108482462974L;
}
