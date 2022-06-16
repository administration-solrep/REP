package fr.dila.reponses.rest.helper;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.core.service.STServiceLocator;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

public final class PasswordHelper {

    private PasswordHelper() {
        // do nothing
    }

    public static boolean isPasswordOutdated(CoreSession session, Logger logger) {
        try {
            if (
                ReponsesServiceLocator
                    .getProfilUtilisateurService()
                    .isUserPasswordOutdated(session, session.getPrincipal().getName())
            ) {
                STServiceLocator.getSTUserService().forceChangeOutdatedPassword(session.getPrincipal().getName());
                return true;
            }
            return false;
        } catch (NuxeoException e) {
            logger.warn("Impossible de vérifier la validité de la date de changement de mot de passe", e);
            return false;
        }
    }

    public static boolean isPasswordTemporary(CoreSession session, Logger logger) {
        try {
            if (STServiceLocator.getSTUserService().isUserPasswordResetNeeded(session.getPrincipal().getName())) {
                return true;
            }
            return false;
        } catch (NuxeoException e) {
            logger.warn("Impossible de vérifier si le mot de passe est temporaire", e);
            return false;
        }
    }
}
