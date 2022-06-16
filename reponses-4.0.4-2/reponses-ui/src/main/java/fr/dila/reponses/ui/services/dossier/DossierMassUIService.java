package fr.dila.reponses.ui.services.dossier;

import fr.dila.st.ui.th.model.SpecificContext;

public interface DossierMassUIService {
    /*
     * Action de masse Donner avis favorable
     */
    void masseFdrActionDonnerAvisFavorable(SpecificContext context);
    /*
     * Action de masse Donner avis défavorable et retour étape précédente
     */
    void masseFdrActionDonnerAvisDefavorableEtInsererTaches(SpecificContext context);
    /*
     * Action de masse Donner avis défavorable et poursuivre
     */
    void masseFdrActionDonnerAvisDefavorableEtPoursuivre(SpecificContext context);
    /*
     * Action de masse Réattribution
     */
    void masseFdrActionNonConcerneReattribution(SpecificContext context);
    /*
     * Action de masse Réattribution direct
     */
    void masseFdrActionReattributionDirecte(SpecificContext context);
    /*
     * Action de masse Réorientation
     */
    void masseFdrActionNonConcerneReorientation(SpecificContext context);
    /*
     * Action de masse Réaffectation
     */
    void masseFdrActionNonConcerneReaffectation(SpecificContext context);
    /*
     * Action de masse Demande arbitrage
     */
    void masseFdrActionDemandeArbitrageSGG(SpecificContext context);
    /*
     * Action de masse Modification ministere rattachement
     */
    void masseActionModificationMinistereRattachement(SpecificContext context);
    /*
     * Action de masse Modification direction pilote
     */
    void masseActionModificationDirectionPilote(SpecificContext context);
}
