package fr.dila.reponses.api.Exception;

import fr.dila.ss.api.exception.SSException;

public class CopieStepException extends SSException {
	
	/**
	 * Exception lever si une étape de feuille de route ne peut pas être copié
	 * @author BBE
	 */
	private static final long serialVersionUID = -4452460069321849297L;

	public CopieStepException(String message) {
		super(message);
	}

}
