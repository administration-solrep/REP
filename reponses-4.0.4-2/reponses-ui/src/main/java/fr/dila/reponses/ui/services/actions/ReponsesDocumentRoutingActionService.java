package fr.dila.reponses.ui.services.actions;

import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.actions.SSDocumentRoutingActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ReponsesDocumentRoutingActionService extends SSDocumentRoutingActionService {
    boolean isStepTransmissionAssemblees(SpecificContext context);

    /**
     * Vérifie si le dossier a une étape en cours dans le ministère de l'utilisateur
     *
     * @return vrai si l'utilisateur dispose de droit spécifiques (rw sur tous les dossiers), si le dossierLink est
     *         rattaché à une mailbox d'un ministere de l'utilisateur, faux sinon Si le dossierLink n'exsite pas (cas de
     *         l'affichage du verrou en cas de clic sur un lien dans un mail), on récupère le document dossier en cours
     *         pour parcourir ses étapes en cours et vérifier qu'il existe une étape rattachée au ministère de
     *         l'utilisateur
     */
    boolean isStepInMinistere(SpecificContext context);

    /**
     * Vérifie l'étape de feuille de route suivante, valide l'étape si c'est réattribution ou réorientation, dans le cas
     * de la réattribution on test si le ministère du poste associé à l'étape réattribution est différent du ministère
     * du poste de l'étape en cours.
     *
     * @return true si réattribution ou réorientation
     */
    boolean isNextStepReorientationOrReattributionOrArbitrage(SpecificContext context);

    /**
     * Renvoie true sur l'ont se trouve sur une étape "Pour signature"
     *
     * @return
     */
    boolean isStepSignature(SpecificContext context);

    /**
     * Renvoie true si on est dans la premiere étape ou dans une branche parallèle. Utilisé comme filtre dans les
     * actions du dossier
     *
     * @return
     */
    boolean isFirstStepInBranchOrParallel(SpecificContext context);

    /**
     * Renvoie true sur l'ont se trouve sur une étape "Pour Arbitrage"
     *
     * @return
     */
    boolean isStepPourArbitrage(SpecificContext context);

    /**
     * Renvoie true sur l'ont se trouve sur une étape "Pour réattribution"
     *
     * @return
     */
    boolean isStepPourReattribution(SpecificContext context);

    /**
     * Renvoie true sur l'ont se trouve sur une étape "Pour réorientation"
     *
     * @return
     */
    boolean isStepPourReorientation(SpecificContext context);

    /**
     * Renvoie true sur l'ont se trouve sur une étape "Pour information"
     *
     * @return
     */
    boolean isStepPourInformation(SpecificContext context);

    /**
     * Renvoie true si l'étape du DossierLink courant est à la racine de la feuille de route Utilisé comme filtre dans
     * les actions du dossier
     *
     * @return
     */
    boolean isRootStep(SpecificContext context);

    List<DocumentModel> getCurrentSteps(DocumentModel dossierDoc, CoreSession session);

    SSRouteStep getCurrentStep(DocumentModel dossierDoc, CoreSession session);
}
