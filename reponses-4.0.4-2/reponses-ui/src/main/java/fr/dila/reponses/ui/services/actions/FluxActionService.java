package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.flux.HasInfoFlux;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Gestion des actions relatives aux flux (signalement, renouvellement).
 *
 * @author jgomez
 */
public interface FluxActionService {
    /**
     * Retourne l'adapter permettant un accès aux informations de flux
     *
     * @param dossier
     * @return l'adapteur
     */
    HasInfoFlux getHasInfoFlux(CoreSession session, Dossier dossier);
}
