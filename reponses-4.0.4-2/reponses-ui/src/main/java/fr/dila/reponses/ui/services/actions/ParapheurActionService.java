package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Action service permettant de gérer le parapheur.
 *
 * @author asatre
 */
public interface ParapheurActionService {
    boolean canSaveDossier(SpecificContext context, DocumentModel dossierDoc);

    void saveDossier(SpecificContext context, ParapheurDTO data);

    boolean canUserUpdateParapheur(SpecificContext context);
}
