package fr.dila.reponses.ui.services.actions.organigramme;

import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;

public interface ReponsesOrganigrammeInjectionActionService {
    /**
     * Exporte le gouvernement dans un fichier Excel .xls
     */
    void exportGouvernement(CoreSession session);

    /**
     * Réalise l'injection du gouvernement.
     *
     * @param context SpecificContext
     */
    void executeInjection(SpecificContext context);
}
