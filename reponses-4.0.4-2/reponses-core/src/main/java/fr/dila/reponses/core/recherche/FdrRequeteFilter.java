package fr.dila.reponses.core.recherche;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 *
 * Un filtre qui prend en paramêtre un object
 * RequeteFeuilleRoute.
 * @author admin
 *
 */
public class FdrRequeteFilter {
    protected DocumentModel fdrRequete;

    public FdrRequeteFilter(DocumentModel requeteFeuilleDeRouteModel) {
        this.fdrRequete = requeteFeuilleDeRouteModel;
    }

    public boolean accept(DocumentModel step) {
        String taskTypeAccepted;
        try {
            taskTypeAccepted = (String) fdrRequete.getProperty("requeteFeuilleRoute", "typeStep");
        } catch (NuxeoException e) {
            taskTypeAccepted = "";
        }
        TaskTypeStepFilter taskTypeFilter = new TaskTypeStepFilter(taskTypeAccepted);
        return taskTypeFilter.accept(step);
    }
}
