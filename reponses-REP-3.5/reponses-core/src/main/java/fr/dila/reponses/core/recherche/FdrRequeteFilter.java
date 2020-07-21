package fr.dila.reponses.core.recherche;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;


/**
 * 
 * Un filtre qui prend en paramêtre un object
 * RequeteFeuilleRoute. 
 * @author admin
 * 
 */

//TODO JGZ: Créer une facette sur le schéma requeteFeuilleDeRoute, de manière 
// à pourvoir adapter le document Model
public class FdrRequeteFilter {
    protected DocumentModel fdrRequete;
    
    public FdrRequeteFilter(DocumentModel requeteFeuilleDeRouteModel){
        this.fdrRequete = requeteFeuilleDeRouteModel;
    }
    
    public boolean accept(DocumentModel step){
        String taskTypeAccepted;
        try {
            taskTypeAccepted = (String) fdrRequete.getProperty("requeteFeuilleRoute", "typeStep");
        } catch (ClientException e) {
            taskTypeAccepted = "";
        }
        TaskTypeStepFilter taskTypeFilter = new TaskTypeStepFilter(taskTypeAccepted);
        return taskTypeFilter.accept(step);
    }
}
