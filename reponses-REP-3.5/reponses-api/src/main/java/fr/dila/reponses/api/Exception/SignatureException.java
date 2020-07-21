package fr.dila.reponses.api.Exception;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Exception lever si le dossier n'a pu être signé car la réponse est vide
 * @author asatre
 *
 */
public class SignatureException extends ClientException {

	public SignatureException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 7669604108482462974L;

}
