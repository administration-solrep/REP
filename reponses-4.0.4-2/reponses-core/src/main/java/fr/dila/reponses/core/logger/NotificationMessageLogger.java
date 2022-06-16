package fr.dila.reponses.core.logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.event.Event;

/**
 *
 *
 * @author bby
 */
class NotificationMessageLogger {
    private static final String FR_DILA_REPONSES_SERVER_LOGGER = "fr.dila.reponses.server.logger";

    private static final String MESSAGE_FORMATTER_FILE = "messageFormatter.properties";

    private static final String LOG_DOSSIER_KEY = "log.dossier";

    private static final String LOG_REPONSE_KEY = "log.reponse";

    private static final String LOG_FDR_KEY = "log.feuilleRoute";

    private static Properties properties = new Properties();

    private static NotificationMessageLogger instance;

    /**
     * Constructeur privé.
     */
    private NotificationMessageLogger() {
        String filename = FileUtils.getResourcePathFromContext(MESSAGE_FORMATTER_FILE);
        if (filename != null && !filename.isEmpty()) {
            FileInputStream fos = null;

            try {
                fos = new FileInputStream(filename);
                properties.load(fos);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Fichier formatter de log non trouvé");
            } catch (IOException e) {
                throw new IllegalArgumentException("Probleme d'acces au fichier formatter de log");
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    // NOP
                }
            }
        } else {
            InputStream stream = Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(MESSAGE_FORMATTER_FILE);
            try {
                properties.load(stream);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Fichier formatter de log non trouvé");
            } catch (IOException e) {
                throw new IllegalArgumentException("Probleme d'acces au fichier formatter de log");
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    // NOP
                }
            }
        }
    }

    public static NotificationMessageLogger getInstance() {
        if (instance == null) {
            instance = new NotificationMessageLogger();
        }
        return instance;
    }

    /**
     * Log les evenements sur le dossier
     *
     * @param event
     *            Evenement
     * @param log
     *            logger
     * @param datas
     *            donnees
     */
    public void logDossier(Event event, Log log, Object[] datas) {
        logEventType(event, log, datas, LOG_DOSSIER_KEY);
    }

    /**
     * Log les evenements sur un type d'action précise à définir
     *
     * @param event
     *            Evenement
     * @param log
     *            logger
     * @param datas
     *            donnees
     * @param typeEvent
     *            type d'événement à logger
     */
    protected void logEventType(Event event, Log log, Object[] datas, String typeEvent) {
        if (log.isInfoEnabled()) {
            check(log, datas);
            MessageFormat formatter = new MessageFormat(properties.getProperty(typeEvent));
            String formatMessage = formatter.format(datas);
            log.info(formatMessage);
            //
            Log logNotification = LogFactory.getLog(FR_DILA_REPONSES_SERVER_LOGGER);
            logNotification.info(formatMessage);
        }
    }

    /**
     * Log les evenements sur la reponse
     *
     * @param event
     *            Evenement
     * @param log
     *            logger
     * @param datas
     *            donnees
     */
    public void logReponse(Event event, Log log, Object[] datas) {
        logEventType(event, log, datas, LOG_REPONSE_KEY);
    }

    /**
     * Log les evenements sur le dossier
     *
     * @param event
     *            Evenement
     * @param log
     *            logger
     * @param datas
     *            donnees
     */
    public void logFeuilleDeRoute(Event event, Log log, Object[] datas) {
        logEventType(event, log, datas, LOG_FDR_KEY);
    }

    /**
     * Check data
     *
     * @param log
     * @param datas
     * @return
     */
    private boolean check(Log log, Object[] datas) {
        if (properties == null) {
            throw new IllegalArgumentException("Fichier formatter de log non trouve");
        }
        if (log == null) {
            throw new IllegalArgumentException("Logger ne peut pas être nul");
        }
        return true;
    }
}
