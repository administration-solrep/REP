package fr.dila.reponses.api.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import javax.naming.NameAlreadyBoundException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 * Service qui permet de gérer les favoris de dossiers.
 *
 * @author jgomez
 */
public interface FavorisDossierService extends Serializable {
    void add(CoreSession session, Collection<String> ids, String repertoireName);

    /**
     * Ajoute la liste de dossiers aux favoris de l'utilisateur.
     *
     * @param session La session de l'utilisateur.
     * @param docs La liste des documentModels à ajouter
     * @return
     *
     *
     */
    void add(CoreSession session, DocumentModelList docs, String currentRepertoire);

    /**
     * Supprime la liste de dossiers des favoris de l'utilisateur.
     *
     * @param session La session de l'utilisateur.
     * @param docs La liste des documentModels à supprimer.
     * @return
     *
     *
     */
    void delete(CoreSession session, final String favoriId, DocumentModelList docs);

    void delete(final CoreSession session, final String favoriId, final Collection<String> dossierIds);

    /**
     * Crée un favoris de dossier dans le repertoire courant en prenant comme source le dossier passé en paramètre
     * @param dossierSource
     * @param session La session de l'utilisateur
     * @return
     *
     */
    DocumentModel createFavorisDossier(CoreSession session, DocumentModel dossierSource, String currentRepertoire);

    /**
     *
     * Crée un répertoire de favoris
     * @param session
     * @param repertoireName Le nom du répertoire
     * @param dateValidite La date de validite du répertoire
     * @return Le répertoire sous forme de document model
     *
     * @throws NameAlreadyBoundException
     */
    DocumentModel createFavorisRepertoire(CoreSession session, String repertoireName, Calendar dateValidite);

    /**
     * Renvoie la liste des favoris de dossier de l'utilisateur
     * @param session La session de l'utilisateur
     */
    List<DocumentModel> getFavoris(CoreSession session, String parentId);

    /**
     * Renvoie la liste des répertoire de favoris de dossier de l'utilisateur
     * @param session La session de l'utilisateur
     */
    public List<DocumentModel> getFavorisRepertoires(CoreSession session);

    /**
     * Retourne le document model du repertoire courant
     * @param session
     * @return
     *
     */
    DocumentModel getCurrentRepertoireDocument(CoreSession session, String currentRepertoire);

    /**
     * Retourne le document racine des favoris
     * @param session
     * @return
     *
     */
    DocumentModel getRootFavorisDossier(CoreSession session);

    /**
     * @return {true} si le nom du favori n'est pas déjà utilisé
     */
    boolean isNomFavoriLibre(final CoreSession session, final String repertoireName);

    /**
     * suppression des favoris à la date de fin indiquée
     *
     * supprimer les favoris dont la date de validité est inferieur à la date courante
     *
     * @param session
     * @param currentDate
     */
    void removeOldFavoris(CoreSession session, Calendar currentDate);

    List<String> getFavorisRepertoiresId(CoreSession session);

    void removeFavorisRepertoires(CoreSession session);
}
