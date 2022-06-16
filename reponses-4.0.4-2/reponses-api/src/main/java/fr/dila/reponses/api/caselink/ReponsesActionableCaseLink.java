package fr.dila.reponses.api.caselink;

import fr.dila.cm.caselink.ActionableCaseLink;
import org.nuxeo.ecm.core.api.CoreSession;

public interface ReponsesActionableCaseLink extends ActionableCaseLink {
    /**
     * Valide le CaseLink
     *
     * @param session
     * @param sendMail
     *            Si true, les mails de distribution sont envoyés
     */
    public void validate(CoreSession session, final boolean sendMail);
}
