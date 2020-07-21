package fr.dila.reponses.webtest.helper;

import java.io.IOException;
import java.util.Calendar;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.reponses.webtest.utils.MailUtils;
import fr.dila.reponses.webtest.utils.PasswordUtils;
import fr.sword.naiad.commons.webtest.helper.AbstractImapHelper;
import fr.sword.naiad.commons.webtest.mail.ImapConsult;

public class ReponsesImapHelper extends AbstractImapHelper {

	private static final String				SUBJECT_RESULTATS_ALERTES	= "Résultats de vos alertes";
	private static final String				SUBJECT_CREATION_MAIL		= "Création de votre compte temporaire pour l'application Réponses";
	private static final String				SUBJECT_CREATION_TACHE		= "Création d'une tâche";
	private static final String				MAILBOX_HOST_DEFAULT		= "idlv-mail-hms.lyon-dev2.local";
	public static final String				MAILBOX_USER_DEFAULT		= "postmaster@reponses.com";
	private static final String				MAILBOX_PASSWORD_DEFAULT	= "postmaster";
	private static final long				SEC_TO_MILLISEC				= 1000L;
	private static final int				TIMEOUT_MAIL_SEC			= 220;

	private static final ReponsesImapHelper	instance					= new ReponsesImapHelper();

	private static final Log				LOGGER						= LogFactory.getLog(ReponsesImapHelper.class);

	protected ReponsesImapHelper() {
		super(MAILBOX_HOST_DEFAULT, MAILBOX_USER_DEFAULT, MAILBOX_PASSWORD_DEFAULT);
	}

	public static ReponsesImapHelper getInstance() {
		return instance;
	}

	public void outputMessages(Message[] messages) throws MessagingException, IOException {
		if (messages == null) {
			return;
		}
		for (Message message : messages) {
			if (message == null) {
				LOGGER.warn("Le message est nul, passe au suivant");
				continue;
			}
			LOGGER.info("Description : " + message.getDescription());
			LOGGER.info("Sujet : " + message.getSubject());
			LOGGER.info("From : " + output(message.getFrom()));
			LOGGER.info("To : " + output(message.getReplyTo()));
			LOGGER.info("Contenu : " + message.getContent().toString());
		}
	}

	public Message[] waitForWelcomingMessages(ImapConsult imap) throws MessagingException, IOException,
			InterruptedException {
		imap.close();
		imap.connect();
		Thread.sleep(15000);
		Message[] messages = waitForMessages(imap, getSearchForSubjectWelcome(), TIMEOUT_MAIL_SEC);
		outputMessages(messages);
		return messages;
	}

	public Message[] waitForTaskMessages(ImapConsult imap) throws MessagingException, IOException, InterruptedException {
		imap.close();
		imap.connect();
		Thread.sleep(15000);
		Message[] messages = waitForMessages(imap, getSearchForSubjectTask(), TIMEOUT_MAIL_SEC);
		outputMessages(messages);
		return messages;
	}

	/**
	 * Retourne l'url de la tache
	 * 
	 * @param message
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public String getDossierLink(Message message) throws MessagingException, IOException {
		if (message == null || message.getContent() == null) {
			return null;
		}
		return MailUtils.getUrl(message.getContent().toString());
	}

	/**
	 * Retourne le mot de passe du message
	 * 
	 * @param message
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public String getPassword(Message message) throws MessagingException, IOException {
		if (message == null || message.getContent() == null) {
			return null;
		}
		return PasswordUtils.getPassword(message.getContent().toString());
	}

	public void clearInbox(ImapConsult imap) throws MessagingException {
		imap.clearInboxMail();
	}

	private String output(Address[] addresses) {
		String result = "";
		for (Address address : addresses) {
			result += address.toString();
		}
		return result;
	}

	public SearchTerm getSearchForSubjectWelcome() {
		SearchTerm term = getSearchTermForSubject(SUBJECT_CREATION_MAIL);
		return term;
	}

	public SearchTerm getSearchForSubjectTask() {
		SearchTerm term = getSearchTermForSubject(SUBJECT_CREATION_TACHE);
		return term;
	}

	public SearchTerm getSearchForSubjectResultatAlerte() {
		SearchTerm term = getSearchTermForSubject(SUBJECT_RESULTATS_ALERTES);
		return term;
	}

	private SearchTerm getSearchTermForSubject(String subject) {
		FlagTerm nonDeletedTerm = new FlagTerm(new Flags(Flags.Flag.DELETED), false);
		SubjectTerm subjectTerm = new SubjectTerm(subject);
		SearchTerm term = new AndTerm(subjectTerm, nonDeletedTerm);
		return term;
	}

	public Message[] waitForAlerteMessages(ImapConsult imap) throws MessagingException, IOException {
		Message[] messages = waitForMessages(imap, getSearchForSubjectResultatAlerte(), TIMEOUT_MAIL_SEC);
		outputMessages(messages);
		return messages;
	}
	
	public Message[] waitForMessagesSubject(ImapConsult imap, String subject) throws MessagingException, IOException {
		Message[] messages = waitForMessages(imap, getSearchTermForSubject(subject), TIMEOUT_MAIL_SEC);
		outputMessages(messages);
		return messages;
	}

	/**
	 * Retourne vrai si le message contient la phrase 1 résultat(s)
	 * 
	 * @param alert
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public boolean mailContientUnResultat(Message message) throws IOException, MessagingException {
		if (message == null || message.getContent() == null) {
			return false;
		}
		String content = StringUtils.EMPTY;
		if (message.isMimeType("multipart/*")) {
			Multipart multiPart = (Multipart) message.getContent();
			BodyPart bodyPart = multiPart.getBodyPart(0);
			content = (String) bodyPart.getContent();
		}
		Boolean result = content.toString().contains("1 résultat(s)");
		return result;
	}

	/**
	 * Repris de l'AbstractImapHelper Dans certains cas, on a msgs == 1 et msgs[0] == null, ce qui pose problème
	 * 
	 * @param imap
	 * @param term
	 *            les termes recherchés dans le mail
	 * @param timeoutSec
	 *            le timeout en secondes
	 * @return
	 * @throws MessagingException
	 */
	protected Message[] waitForMessages(ImapConsult imap, SearchTerm term, int timeoutSec) throws MessagingException {
		imap.close();
		imap.connect();
		long currentTime = Calendar.getInstance().getTimeInMillis();
		long endTime = currentTime + timeoutSec * SEC_TO_MILLISEC;
		Message[] msgs = imap.searchInboxMessage(term);
		while (currentTime < endTime) {
			if (msgs.length > 0) {
				if (msgs[0] != null || msgs.length > 1) {
					break;
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LOGGER.error("erro during sleep", e);
			}
			currentTime = Calendar.getInstance().getTimeInMillis();
			msgs = imap.searchInboxMessage(term);
		}

		return msgs;
	}

}
