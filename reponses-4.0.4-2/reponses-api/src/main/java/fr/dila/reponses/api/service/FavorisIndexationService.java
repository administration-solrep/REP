package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentRef;

public interface FavorisIndexationService {
    /**
     * Retourne le répertoire des favoris du plan de classement de l'utilisateur
     * courant
     *
     * @param session
     *            CoreSession de l'utilisateur
     * @return le répertoire des favoris du plan de classement
     */
    DocumentRef getFavorisIndexationRootRef(CoreSession session);

    void addFavoris(CoreSession session, String origine, String cle, String cleParent);
}
