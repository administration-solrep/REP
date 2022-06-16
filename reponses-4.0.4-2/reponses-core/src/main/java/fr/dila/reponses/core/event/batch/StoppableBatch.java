package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.event.batch.STBatch;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.util.BatchHelper;
import fr.dila.st.core.service.STServiceLocator;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Caractérise un batch qui vérifie qu'il peut s'exécuter avant de le faire.
 *
 * @author tlombard
 */
public interface StoppableBatch extends STBatch {
    /**
     * Réalise les vérifications d'utilisation de l'application avant le lancement
     * d'un batch :
     * <ul>
     * <li>Vérification qu'aucune migration n'est en cours</li>
     * <li>Vérification qu'aucune restriction d'accès n'est active</li>
     * </ul>
     * Si le batch n'est pas supposé être lancé on loggue un message dans ce sens.
     *
     * @return true ssi une des vérifications retourne true, cad si on n'est pas
     *         sensé lancer le batch.
     */
    @Override
    default boolean canBatchBeLaunched(final CoreSession session) {
        if (!BatchHelper.canBatchBeLaunched()) {
            return false;
        }

        final String INFO_BATCH_CANNOT_BE_LAUNCHED =
            "Le batch %s ne peut pas être lancé : une migration est considérée en cours ou une restriction d'accès est active !";

        boolean canBeLaunched =
            !(
                STServiceLocator
                    .getEtatApplicationService()
                    .getEtatApplicationDocument(session)
                    .getRestrictionAcces() ||
                ReponsesServiceLocator.getUpdateTimbreService().isMigrationEnCours(session)
            );

        if (!canBeLaunched) {
            getStoppageLogger()
                .info(
                    session,
                    getStoppageCode(),
                    String.format(INFO_BATCH_CANNOT_BE_LAUNCHED, this.getClass().getName())
                );
        }
        return canBeLaunched;
    }

    /**
     * Renvoie le code du message à utiliser pour un arrêt du batch avant exécution.
     */
    STLogEnum getStoppageCode();

    /**
     * Renvoie le logger à utiliser pour renvoyer le code.
     */
    STLogger getStoppageLogger();
}
