package fr.dila.reponses.ui.services.actions;

import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * L'action service seam chargé de gérer les actions du requêteur dans suivi.
 *
 * @author admin
 *
 */
public interface RequeteurActionService {
    /**
     * Sauvegarde un document à partir des valeurs mise dans le bean seam
     * smartQueryActions
     *
     * @param docType
     * @return
     */
    RequeteExperte saveQueryAsRequeteExperte(SpecificContext context, String queryPart);

    /**
     *
     * Met à jour une requête à partir des valeurs mise dans le bean seam
     * smartQueryActions
     *
     * @param docType
     * @return
     */
    DocumentModel updateRequeteExperte(SpecificContext context, DocumentModel doc, String queryPart);

    /**
     * Crée la requête et renvoie vers sa page de résultats
     *
     * @return
     * @throws ClientException
     */
    DocumentModel createRequete(SpecificContext context, DocumentModel doc);

    String getFullQuery(String where);

    /**
     * Retourne la requête complête, à partir du document courant, ou d'une
     * requête sauvegardée en tant que currentSmartFolder.
     *
     * @return
     * @throws ClientException
     *
     **/
    String getFullQuery(CoreSession session, RequeteExperte req);

    /**
     * Publie la requête dans l'espace utilisateur.
     *
     * @return
     */
    void publish(SpecificContext context, DocumentModel doc);

    /**
     * Annule le passage à la publication de la requête
     *
     * @return
     */
    void unpublish(SpecificContext context, DocumentModel doc);

    /**
     * Retourne le document qui contient toutes les requêtes préparamêtrées
     *
     * @return la racine de toutes les requêtes sauvegardées
     * @throws ClientException
     */
    DocumentModel getBibliothequeStandard(CoreSession session);

    /**
     * Retourne vrai si l'utilisateur courant peut publier des requêtes.
     *
     * @return Condition
     * @throws ClientException
     */
    boolean canPublish(CoreSession session, DocumentModel doc);

    /**
     * Retourne vrai si l'utilisateur courant peut supprimer la requête.
     *
     * @return Condition
     * @throws ClientException
     */
    boolean canDelete(CoreSession session, DocumentModel doc);

    /**
     * Retourne vrai si l'utilisateur courant peut éditer la requête
     *
     * @return Condition
     * @throws ClientException
     */
    boolean canEdit(CoreSession session, DocumentModel doc);
}
