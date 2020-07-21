package fr.dila.reponses.core.event.batch;

import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.batch.AbstractDailyReminderChangePasswordListener;

/**
 * Batch d'envoi des mails journaliers de prévenance de renouvellement de mot de passe
 * 
 * @author JBT
 * 
 */
public class DailyReminderChangePasswordListener extends AbstractDailyReminderChangePasswordListener {
    
    public DailyReminderChangePasswordListener(){
    	super();
    }

	@Override
	protected Set<STUser> getUsersList(CoreSession session)	throws ClientException {
		return ReponsesServiceLocator.getProfilUtilisateurService().getToRemindChangePasswordUserList(session);
	}

	@Override
	protected int getDelayForUser(CoreSession session, STUser user) throws ClientException {
		return ReponsesServiceLocator.getProfilUtilisateurService().getNumberDayBeforeOutdatedPassword(session, user);
	}
}
