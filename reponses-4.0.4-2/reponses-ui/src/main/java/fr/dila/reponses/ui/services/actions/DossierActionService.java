package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.ui.bean.DossierSaveForm;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface DossierActionService {
    boolean isDossierContainsMinistere(SSPrincipal ssPrincipal, DocumentModel dossierDoc);

    /**
     * Retourne vrai si l'utilisateur courant peut éditer les dossiers connexes.
     *
     * @return Condition
     */
    boolean canReadDossierConnexe(SSPrincipal ssPrincipal);

    /**
     * Retourne vrai si l'utilisateur courant peut lire les dossiers allotis.
     *
     * @return Condition
     */
    boolean canReadAllotissement(SSPrincipal ssPrincipal);

    /**
     * Retourne vrai si le ministère interpellé dossier en cours fait partie des ministères de l'utilisateur.
     *
     * @return Condition
     */
    boolean isCurrentDossierInUserMinistere(SpecificContext context);

    /**
     * Indique si le dossier a été arbitré par le passé
     *
     * @return
     */
    boolean isDossierArbitrated(SpecificContext context);

    /**
     * Enregistre les nouvelles données de l'indexation complémentaire d'une question liée à un {@link Dossier}
     * @param context le contexte qui contient le {@link Dossier} en tant que currentDocument
     * @param newDossier le DTO qui contient les nouvelles données de l'indexation complémentaire à enregistrer
     */
    void saveIndexationComplementaire(SpecificContext context, DossierSaveForm newDossier);

    /**
     * Retourne vrai si l'utilisateur appartient à un poste qui a participé au
     * dossier.
     *
     * @return Condition
     */
    boolean isUserMailboxInDossier(SpecificContext context, DocumentModel dossierDoc);

    /**
     * Retourne vrai si l'utilisateur a le droit de modifier un lot
     * @param ssPrincipal
     * @return
     */
    boolean canUpdateAllotissement(SSPrincipal ssPrincipal);

    /**
     * Retourne vrai si le dossier peut être redémarré
     * @param context
     * @return
     */
    boolean isFeuilleRouteRestartable(SpecificContext context);
}
