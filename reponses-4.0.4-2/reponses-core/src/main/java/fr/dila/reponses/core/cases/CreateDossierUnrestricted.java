package fr.dila.reponses.core.cases;

import fr.dila.cm.security.CaseManagementSecurityConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

/**
 * Création d'un dossier.
 *
 * @author jtremeaux
 */
public class CreateDossierUnrestricted extends UnrestrictedSessionRunner {
    protected final String parentPath;

    protected DocumentModel caseDoc;

    public CreateDossierUnrestricted(CoreSession session, DocumentModel caseDoc, String parentPath) {
        super(session);
        this.caseDoc = caseDoc;

        this.parentPath = parentPath;
    }

    @Override
    public void run() {
        String caseTitle = DublincoreSchemaUtils.getTitle(caseDoc);
        caseDoc.setPathInfo(parentPath, caseTitle);
        caseDoc = session.createDocument(caseDoc);

        // Donne le droit de lecture / écriture sur la mailbox utilisateur du propriétaire des dossiers
        ACP acp = caseDoc.getACP();
        ACL acl = acp.getOrCreateACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
        //        acl.add(new ACE(CaseManagementSecurityConstants.MAILBOX_PREFIX + mailbox.getId(), SecurityConstants.READ_WRITE, true));

        // Donne le droit de lecture / écriture au créateur des dossiers
        acl.add(new ACE(ReponsesBaseFunctionConstant.DOSSIER_CREATOR, SecurityConstants.READ_WRITE, true));
        acp.addACL(acl);
        session.setACP(caseDoc.getRef(), acp, true);

        caseDoc.detach(true);
    }

    public DocumentModel getDossier() {
        return caseDoc;
    }
}
