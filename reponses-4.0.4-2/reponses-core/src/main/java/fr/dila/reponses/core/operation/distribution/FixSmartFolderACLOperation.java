package fr.dila.reponses.core.operation.distribution;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.ss.api.constant.SSBaseFunctionConstant;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.util.ObjectHelper;
import java.util.stream.Stream;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;

/**
 * Opération effectuant la remise en état des ACLs des SmartFolder tels que
 * spécifiés dans les spécifications de Reponses2NG.
 *
 * @see "SPF00121 - spécifications des évolutions fonctionnelles Réponses.docx §6.1 -> Les listes de questions rappelées/signalées/renouvelées doivent être interdites à tous par défaut, accessibles par les groupes disposant de la fonction BdcRequetesPreparamReader (historique), accessibles par les profils « Administrateur fonctionnel », « Administrateur ministériel » et « Vigie » (specs S2NG)"
 * @author tlombard
 */
@Operation(
    id = FixSmartFolderACLOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    description = "Remet en état les ACL des SmartFolder tels que spécifiés"
)
@STVersion(version = "4.0.0")
public class FixSmartFolderACLOperation {
    public static final String ID = "Reponses.Fix.SmartFolder.ACL";

    @Context
    protected OperationContext context;

    private CoreSession session;

    public FixSmartFolderACLOperation() {
        super();
    }

    @OperationMethod
    public void fixAllSmartFolderAcls() {
        if (context.getPrincipal().isAdministrator()) {
            session = context.getCoreSession();
            Stream
                .of("r17-questions-renouvelees", "r18-questions-rappelees", "r16-questions-signalees")
                .map(RequeteConstants.BIBLIO_REQUETES_ROOT::concat)
                .map(PathRef::new)
                .forEach(this::fixOneSmartFolderACLs);
        } else {
            throw new NuxeoException("Seulement un administrateur peut exécuter cette opération.");
        }
    }

    /**
     * Sur chaque document on reprend l'ensemble des droits.
     * <ul>
     * <li>EveryOne | ReadWrite | false</li>
     * <li>Administrateur fonctionnel | Read | true</li>
     * <li>Administrateur ministériel | Read | true</li>
     * <li>BDC Requetes Preparam | Read | true</li>
     * <li>Vigie du SGG | Read | true</li>
     * </ul>
     *
     * @param docRef
     */
    private void fixOneSmartFolderACLs(DocumentRef docRef) {
        ACP acp = ObjectHelper.requireNonNullElseGet(session.getACP(docRef), ACPImpl::new);
        acp.addACL(new ACLImpl()); // clear local acls
        acp.blockInheritance(ACL.LOCAL_ACL, context.getPrincipal().getActingUser());

        ACL acl = acp.getOrCreateACL(ACL.LOCAL_ACL);
        acl.add(ACE.builder(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME, SecurityConstants.READ).build());
        acl.add(ACE.builder(STBaseFunctionConstant.ADMIN_MINISTERIEL_GROUP_NAME, SecurityConstants.READ).build());
        acl.add(ACE.builder(ReponsesBaseFunctionConstant.BDC_REQUETES_PREPARAM_READER, SecurityConstants.READ).build());
        acl.add(ACE.builder(SSBaseFunctionConstant.VIGIE_DU_SGG_GROUP_NAME, SecurityConstants.READ).build());

        acp.addACL(acl);

        session.setACP(docRef, acp, true);
    }
}
