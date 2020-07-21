package fr.dila.ss.web.document;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.st.api.domain.user.Delegation;
import fr.dila.st.api.feuilleroute.STRouteStep;

/**
 * Ce WebBean permet d'injecter les classes des objets métiers pour les rendre disponible dans le contexte
 * Seam.
 * 
 * @author jtremeaux
 */
@Name("documentModelActions")
@Scope(ScopeType.APPLICATION)
@Install(precedence = FRAMEWORK + 1)
public class DocumentModelActionsBean extends fr.dila.st.web.document.DocumentModelActionsBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public DocumentModelActionsBean(){
    	super();
    }
    
    /**
     * Retourne l'interface de l'objet métier STRouteStep.
     * 
     * @return Interface de l'objet métier
     */
    @Factory(value = "STRouteStep", scope = ScopeType.APPLICATION)
    public Class<STRouteStep> getSTRrouteStep() {
        return STRouteStep.class;
    }
    
    /**
     * Retourne l'interface de l'objet métier StepFolder.
     * 
     * @return Interface de l'objet métier
     */
    @Factory(value = "StepFolder", scope = ScopeType.APPLICATION)
    public Class<StepFolder> getStepFolder() {
        return StepFolder.class;
    }
    
    /**
     * Retourne l'interface de l'objet métier Delegation.
     * 
     * @return Interface de l'objet métier
     */
    @Factory(value = "Delegation", scope = ScopeType.APPLICATION)
    public Class<Delegation> getDelegation() {
        return Delegation.class;
    }
}
