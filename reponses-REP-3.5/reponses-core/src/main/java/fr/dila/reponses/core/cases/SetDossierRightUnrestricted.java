package fr.dila.reponses.core.cases;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.st.api.service.SecurityService;
import fr.dila.st.core.service.STServiceLocator;

public class SetDossierRightUnrestricted extends UnrestrictedSessionRunner{

    protected Dossier dossier;
    
    public SetDossierRightUnrestricted(CoreSession session,Dossier dossier) {
        super(session);
        this.dossier=dossier;
    }
    
    @Override
    public void run() throws ClientException {
        SecurityService securityService = STServiceLocator.getSecurityService();
        securityService.addAceToSecurityAcl(session, dossier.getDocument(), ReponsesBaseFunctionConstant.DOSSIER_CREATOR, SecurityConstants.READ_WRITE);

//        if (dossier.isPublished(session)){
//            try {
                securityService.addAceToSecurityAcl(session, dossier.getDocument(), ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_READER, SecurityConstants.READ_WRITE);
//            } catch (ClientException e) {
//                throw new ClientException("Les Droits sur la recherche n'ont pas pu etre ajoutés au dossier");
//            }
//        }
    }
}
