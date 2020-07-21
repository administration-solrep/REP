package fr.dila.reponses.web.document;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.platform.reporting.api.ReportInstance;

import fr.dila.reponses.api.archivage.ListeElimination;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;

/**
 * Ce WebBean permet d'injecter les classes des objets métiers pour les rendre disponible dans le contexte
 * Seam.
 * 
 * @author jtremeaux
 */
@Name("documentModelActions")
@Scope(ScopeType.APPLICATION)
@Install(precedence = FRAMEWORK + 2)
public class DocumentModelActionsBean extends fr.dila.st.web.document.DocumentModelActionsBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Retourne l'interface de l'objet métier Dossier.
     * 
     * @return Interface de l'objet métier
     */
    @Factory(value = "Dossier", scope = ScopeType.APPLICATION)
    public Class<Dossier> getDossier() {
        return Dossier.class;
    }
    
    /**
     * Retourne l'interface de l'objet métier Question.
     * 
     * @return Interface de l'objet métier
     */
    @Factory(value = "Question", scope = ScopeType.APPLICATION)
    public Class<Question> getQuestion() {
        return Question.class;
    }
    
    /**
     * Retourne l'interface de l'objet métier ListeElimination.
     * 
     * @return Interface de l'objet métier
     */
    @Factory(value = "ListeElimination", scope = ScopeType.APPLICATION)
    public Class<ListeElimination> getListeElimination() {
        return ListeElimination.class;
    }
    
    /**
     * Retourne l'interface de l'objet métier BirtReportInstance.
     * 
     * @return Interface de l'objet métier
     */
    @Factory(value = "ReportInstance", scope = ScopeType.APPLICATION)
    public Class<ReportInstance> getReportInstance() {
        return ReportInstance.class;
    }
}
