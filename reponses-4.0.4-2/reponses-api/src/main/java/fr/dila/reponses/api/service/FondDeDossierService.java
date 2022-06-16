package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSFondDeDossierService;
import fr.dila.st.api.dossier.STDossier;
import java.util.List;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface du service pour la gestion de l'arborescence du fond de dossier.
 *
 * @author ARN
 */
public interface FondDeDossierService extends SSFondDeDossierService {
    /**
     * Récupère le niveau de visibilité associé au répertoire.
     *
     * @param idRepertoire
     *            document fondDeDossier à initialiser
     * @param session
     *            session
     *
     */
    String getDefaultNiveauVisibiliteFromRepository(String idRepertoire, CoreSession session);

    /**
     * Récupère le choix de suppression par défaut.
     *
     * @param numeroVersion
     *            numero de version du document
     *
     */
    String getDefaultChoixSuppressionFichierFondDeDossier(String numeroVersion);

    /**
     * Création d'un fichier dans l'arborescence du fond de dossier.
     *
     * @param session
     *            session
     * @param fondDeDossierDoc
     *            documentModel du fond de dossier
     * @param fileName
     *            nom du document à créer
     * @param niveauVisibilite
     *            niveauVisibilite
     * @param content
     *            le contenu du fichier
     * @return le document créé
     *
     */
    DocumentModel createFondDeDossierFile(
        CoreSession session,
        DocumentModel fondDeDossierDoc,
        String fileName,
        String niveauVisibilite,
        Blob content
    );

    /**
     * Suppression d'un document (ou de ses fils dan le cas d'un document de type répertoire) dans l'arborescence du
     * fond de dossier.
     *
     * @param docId
     *            id du document concerné par la suppression
     * @param session
     *            session
     *
     */
    void delete(String docId, CoreSession session, DocumentModel dossierDoc);

    /**
     * Remplace la version courante du document par l'avant dernière version.
     *
     * @param session
     *            session
     * @param fddFileId
     *            id du ficheri fond de dossier concerné par la restauration
     *
     *
     */
    @Override
    void restoreToPreviousVersion(CoreSession session, String fddFileId, DocumentModel dossierDoc);

    /**
     * Déplacement d'un document dans l'arborescence du fond de dossier.
     *
     * @param session
     *            session
     * @param fondDeDossier
     *            fondDeDossier du dossier courant
     * @param fichierFdd
     *            documentModel contenant les infos sur le fond de dossier
     * @param newVisibility
     *
     *
     */
    void moveFddFile(
        CoreSession session,
        FondDeDossier fondDeDossier,
        DocumentModel fichierFdd,
        DocumentModel dossierDoc,
        String newVisibility
    );

    /**
     * Renvoie la liste des choix de suppressions d'un fichier du fond de dossier en fonction du numéro de version
     *
     * @param numeroVersion
     *            numero de version du document
     * @return Liste des choix de suppressions
     *
     */
    List<String> getChoixSuppressionFichierFondDeDossierList(String numeroVersion);

    /**
     * Renvoie les fils directs (répertoire) du document racine "fond de dossier"
     *
     * @param session
     *            session
     * @param fondDeDossierDocument
     *            document fondDeDossier
     * @return Liste des fils direct du fdd
     *
     */
    List<FondDeDossierFile> getFondDeDossierPublicDocument(CoreSession session, DocumentModel fondDeDossierDocument);

    /**
     * Définit si le niveau de visibilité du fichier associé au document a changé.
     *
     * @param fondDeDossier
     *            document fondDeDossier lié au dossier courant
     * @param fichierFdd
     *            document contenant le nom du fichier
     * @param session
     *            session
     * @return vrai si le nom du fichier est unique
     *
     */
    boolean hasFondDeDossierFileVisibilityChanged(
        FondDeDossier fondDeDossier,
        DocumentModel fichierFdd,
        CoreSession session
    );

    /**
     * Retourne la liste des fichiers dans le fond de dossier donné, en respectant les droits de visibilité
     *
     * @param session
     *            session
     * @param dossier
     *            document FondDeDossier
     * @return Liste des fichiers dans le fond de dossier
     */
    List<DocumentModel> getFddDocuments(CoreSession session, STDossier dossier);

    /**
     * Retourne true si le répertoire de fond de dossier passé en paramètre est visible par l'utilisateur
     *
     * @param dossier
     *            Dossier
     * @param repName
     *            nom du repertoire de fond de dossier
     * @param currentUser
     *            Utilisateur
     * @return True si le répertoire est visible
     */
    boolean isRepertoireVisible(Dossier dossier, String repName, SSPrincipal currentUser);

    /**
     * vérifie la validité d'une chaine de caractère en fonction des caractères non autorisés
     *
     * @param fondDeDossierFileDoc
     * @return vrai si la string en paramètre ne contient pas l'un des caractères interdits, faux sinon
     */
    boolean isFondDeDossierFileNameCorrect(DocumentModel fondDeDossierFileDoc);

    List<FondDeDossierFolder> getVisibleChildrenFolder(
        CoreSession session,
        DocumentModel fondDeDossierDoc,
        SSPrincipal ssPrincipal
    );

    /**
     * Modification d'un document dans l'arborescence du fond de dossier.
     *
     * @param session
     * @param oldFichierDoc
     * @param newFichierDoc
     * @param dossierDoc
     *
     */
    void updateFddFile(
        CoreSession session,
        DocumentModel oldFichierDoc,
        DocumentModel newFichierDoc,
        DocumentModel dossierDoc
    );

    /**
     * Find Fond De Dossier From Dossier
     */
    DocumentModel getFondDeDossierFromDossier(final CoreSession session, final DocumentModel dossier);

    /**
     * Permet de savoir si le dossier a des pièces jointes
     */
    boolean havePieceJointeDossier(CoreSession coreSession, Dossier dossier);

    void logDeleteFileFromFDD(DocumentModel dossierDoc, String fileName, CoreSession session);
}
