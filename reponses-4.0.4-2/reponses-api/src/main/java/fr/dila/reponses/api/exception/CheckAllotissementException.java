package fr.dila.reponses.api.exception;

/**
 * Exception sur la vérification préalable à l'allotissement d'une question.
 */
public class CheckAllotissementException extends Exception {
    private static final long serialVersionUID = -3686823087320845657L;

    public CheckAllotissementException(String msg) {
        super(msg);
    }
}
