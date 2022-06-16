package fr.dila.reponses.ui.services.files;

import fr.dila.ss.ui.bean.FondDTO;
import fr.dila.ss.ui.services.actions.SSTreeManagerActionService;
import fr.dila.st.ui.th.model.SpecificContext;

/**
 * Action Service de gestion de l'arborescence du fond de dossier.
 *
 */
public interface ReponsesFondDeDossierUIService extends SSTreeManagerActionService {
    /**
     * Construit le DTO associé au fdd du dossier courant.
     *
     * @param context SpecificContext
     * @return Objet FondDTO
     */
    FondDTO getFondDTO(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur a le droit de modifier le fond de dossier.
     */
    boolean canUserUpdateFondDossier(SpecificContext context);

    void addFile(SpecificContext context);

    void editFile(SpecificContext context);

    void deleteFile(SpecificContext context);
}
