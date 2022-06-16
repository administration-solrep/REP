package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ReponsesDossierDistributionActionService {
    /**
     * Réattribution à un autre ministère
     *
     * @param context
     * @return vrai si la réattribution a pu se faire
     */
    boolean nonConcerneReattribution(SpecificContext context);

    /**
     * Retourne vrai si un DossierLink a été chargé, c'est à dire que l'utilisateur peut agir sur l'étape en cours en
     * tant que destinataire ou administrateur.
     *
     * @return Condition
     */
    boolean isDossierLinkLoaded(SpecificContext context);

    /**
     * Donne un avis défavorable sur le DossierLink en cours. Sans ajout d'étape.
     * Refus de signature
     */
    void donnerAvisDefavorableEtPoursuivre(SpecificContext context);

    /**
     * Donne un avis défavorable sur le DossierLink en cours. Avec ajout d'étape.
     */
    void donnerAvisDefavorableEtInsererTaches(SpecificContext context);

    /**
     * Donne un avis défavorable sur le DossierLink en cours. Et retourne le dossier au ministère attributaire dans une
     * étape pour attribution.
     */
    void donnerAvisDefavorableEtRetourBdcAttributaire(SpecificContext context);

    /**
     * Retourne l'étape "validation PM" de l'instance de la feuille de route associé au dossier.
     *
     * @return Etape validation premier ministre
     */
    DocumentModel getValidationPMStep(SpecificContext context);

    /**
     * Demande un arbitrage du SGG sur le DossierLink en cours et valide l'étape non concerné
     *
     * @return Vue de la liste des dossiers
     */
    boolean demandeArbitrageSGG(SpecificContext context);

    /**
     * Met le dossier courant en attente.
     */
    void mettreEnAttente(SpecificContext context);

    /**
     * Effectue la fonctionnalité d'attribution après arbitrage
     */
    void attributionApresArbitrage(SpecificContext context);

    void reattributionDirecte(SpecificContext context);

    /**
     * Cas de l'étape non concerné sans passage par la popup isNextStepReorientationOrReattribution == true
     */
    void nonConcerne(SpecificContext context);

    void nonConcerneReorientation(SpecificContext context);

    /**
     * Substitution de la feuille de route en cours : - Annule la feuille de route ; - Démarre une nouvelle feuille de
     * route.
     */
    void substituerRoute(SpecificContext context);

    /**
     * Redémarre un dossier dont la feuille de route a été terminée.
     */
    void redemarrerDossier(SpecificContext context);

    List<FondDeDossierFile> getListeDocumentPublicReponse(SpecificContext context);

    /**
     * Donne un avis favorable sur le DossierLink en cours.
     *
     * @param context SpecificContext
     */
    void donnerAvisFavorable(SpecificContext context);

    /**
     * ajoute les étapes 'Pour retour', 'Pour signature' et 'pour validation
     * retour Premier ministre' au dossier courant.
     */
    void validationRetourPM(SpecificContext context);
}
