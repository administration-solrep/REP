import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunVoid;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import org.nuxeo.ecm.core.api.CoreSession;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.util.LdapSessionUtil;
import org.nuxeo.ecm.directory.ldap.LDAPSession;

import fr.dila.st.core.util.ServiceUtil;
import org.nuxeo.ecm.platform.audit.service.NXAuditEventsService;


class SolonRunVoid implements  RunVoid{

            public void runWith(EntityManager em) throws ClientException {

			LDAPSession session = LdapSessionUtil.getLdapSession(STConstant.ORGANIGRAMME_USER_DIR);
            DocumentModelList usersList = session.getEntries();
            	    for (DocumentModel user : usersList) {
                STUser sTUser = user.getAdapter(STUser.class);

             
                String firstName = sTUser.getFirstName();
                String lastName = sTUser.getLastName();
                String userName = sTUser.getUsername();

                println("start updating journal for user :"+userName);
                		        

                    if (firstName == null) {
                        firstName = "";
                    }
                    if (lastName == null) {
                        lastName = "";
                    }
                Query nativeQuery = em.createNativeQuery("UPDATE nxp_logs SET log_principal_name = :log_principal_name WHERE log_principal_name = :userName");
                nativeQuery.setParameter("log_principal_name", firstName +" "+lastName);
                nativeQuery.setParameter("userName", userName);
                nativeQuery.executeUpdate();
                em.flush();
                println("end updating journal for user :"+userName);
                }
	

            }
}    

	SolonRunVoid runVoid  = new SolonRunVoid() ;  

        try {
           ((NXAuditEventsService) ServiceUtil.getLocalService(org.nuxeo.ecm.platform.audit.api.NXAuditEvents.class)).getOrCreatePersistenceProvider().run(true, runVoid);
        } catch (ClientException e) {
        	return e.getMessage() ;
            e.printStackTrace();
        }


return "Fin du script groovy";