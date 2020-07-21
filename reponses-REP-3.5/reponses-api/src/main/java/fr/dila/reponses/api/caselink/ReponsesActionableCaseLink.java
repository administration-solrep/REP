package fr.dila.reponses.api.caselink;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.cm.caselink.ActionableCaseLink;

public interface ReponsesActionableCaseLink extends ActionableCaseLink {
	/**
	 * Valide le CaseLink
	 * 
	 * @param session
	 * @param sendMail
	 *            Si true, les mails de distribution sont envoyés
	 * @throws ClientException
	 */
	public void validate(CoreSession session, final boolean sendMail) throws ClientException;
}
