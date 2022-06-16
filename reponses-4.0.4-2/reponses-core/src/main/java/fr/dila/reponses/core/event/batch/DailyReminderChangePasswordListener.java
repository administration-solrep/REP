package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.batch.AbstractDailyReminderChangePasswordListener;
import fr.dila.st.core.factory.STLogFactory;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Batch d'envoi des mails journaliers de prévenance de renouvellement de mot de passe
 *
 * @author JBT
 *
 */
public class DailyReminderChangePasswordListener
    extends AbstractDailyReminderChangePasswordListener
    implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(DailyReminderChangePasswordListener.class);

    public DailyReminderChangePasswordListener() {
        super();
    }

    @Override
    protected Set<STUser> getUsersList(CoreSession session) {
        return ReponsesServiceLocator.getProfilUtilisateurService().getToRemindChangePasswordUserList(session);
    }

    @Override
    protected int getDelayForUser(CoreSession session, STUser user) {
        return ReponsesServiceLocator.getProfilUtilisateurService().getNumberDayBeforeOutdatedPassword(session, user);
    }

    @Override
    public STLogEnum getStoppageCode() {
        return STLogEnumImpl.CANCEL_B_DAILY_MAIL_CHANGE_PASS_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
