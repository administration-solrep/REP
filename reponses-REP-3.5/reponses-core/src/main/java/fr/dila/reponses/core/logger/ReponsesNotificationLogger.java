package fr.dila.reponses.core.logger;

import fr.dila.st.core.logger.NotificationLogger;

/**
 * Implémentation du logger Log4J pour l'application Réponses.
 * 
 * @see NotificationLogger
 * @author bby, jtremeaux
 */
public class ReponsesNotificationLogger extends NotificationLogger {
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new instance of Log.
     * 
     * @param name Nom du logger
     */
    public ReponsesNotificationLogger(String name) {
        super(name);
    }

    @Override
    protected String getMessage() {
        // TODO Loguer systématiquement certaines infos : login, identifiant de session, identifiant du dossier en cours.
        return "Reponses";
    }
}
