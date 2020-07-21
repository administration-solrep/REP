package fr.dila.ss.web.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

import fr.dila.ss.api.security.principal.SSPrincipal;

/**
 * WebBean qui permet de récupérer le principal du socle transverse.
 * 
 * @author admin
 */
@Startup
@Name("userSession")
@Scope(ScopeType.SESSION)
@Install(precedence = Install.APPLICATION + 1)
public class UserSessionBean extends org.nuxeo.ecm.webapp.security.UserSessionBean {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 7639281445209754L;

    /**
     * Default constructor
     */
    public UserSessionBean(){
    	super();
    }
    
    /**
     * Retourne le principal du socle transverse.
     * 
     * @return Principal du socle transverse
     */
    @Factory(value = "ssPrincipal", scope = ScopeType.SESSION)
    public SSPrincipal getCurrentNuxeoPrincipal() {
        return (SSPrincipal) getCurrentUser();
    }

}
