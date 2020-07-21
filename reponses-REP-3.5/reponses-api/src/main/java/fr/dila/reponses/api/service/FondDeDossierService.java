package fr.dila.reponses.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSFondDeDossierService;

/**
 * Interface du service pour la gestion de l'arborescence du fond de dossier.
 * 
 * @author ARN
 */
public interface FondDeDossierService extends SSFondDeDossierService {

	/**
	 * Récupère le niveau de visibilité associé au répertoire.
	 * 
	 * @param fondDeDossier
	 *            document fondDeDossier à initialiser
	 * @param session
	 *            session
	 * @throws ClientException
	 */
	String getDefaultNiveauVisibiliteFromRepository(String idRepertoire, CoreSession session);

	/**
	 * Récupère le choix de suppression par défaut.
	 * 
	 * @param numeroVersion
	 *            numero de version du document
	 * @throws ClientException
	 */
	String getDefaultChoixSuppressionFichierFondDeDossier(String numeroVersion);

	/**
	 * Création d'un fichier dans l'arborescence du fond de dossier.
	 * 
	 * @param session
	 *            session
	 * @param fondDeDossierDoc
	 *            documentModel du fond de dossier
	 * @param docName
	 *            nom du document à créer
	 * @param niveauVisibilite
	 *            niveauVisibilite (utilisé si document de type ElementFondDeDossier)
	 * 
	 * @return le document créé
	 * @throws ClientException
	 */
	DocumentModel createFondDeDossierFile(CoreSession session, DocumentModel fondDeDossierDoc, String docName,
			String niveauVisibilite) throws ClientException;

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
	 * @throws ClientException
	 */
	DocumentModel createFondDeDossierFile(CoreSession session, DocumentModel fondDeDossierDoc, String fileName,
			String niveauVisibilite, Blob content) throws ClientException;

	/**
	 * Suppression d'un document (ou de ses fils dan le cas d'un document de type répertoire) dans l'arborescence du
	 * fond de dossier.
	 * 
	 * @param docId
	 *            id du document concerné par la suppression
	 * @param session
	 *            session
	 * @throws ClientException
	 */
	void delete(String docId, CoreSession session, DocumentModel dossierDoc) throws ClientException;

	/**
	 * Remplace la version courante du document par l'avant dernière version.
	 * 
	 * @param session
	 *            session
	 * @param fddFileId
	 *            id du ficheri fond de dossier concerné par la restauration
	 * 
	 * @throws ClientException
	 */
	@Override
	void restoreToPreviousVersion(CoreSession session, String fddFileId, DocumentModel dossierDoc)
			throws ClientException;

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
	 * @throws ClientException
	 */
	void moveFddFile(CoreSession session, FondDeDossier fondDeDossier, DocumentModel fichierFdd,
			DocumentModel dossierDoc, String newVisibility) throws ClientException;

	/**
	 * Renvoie la liste des choix de suppressions d'un fichier du fond de dossier en fonction du numéro de version
	 * 
	 * @param numeroVersion
	 *            numero de version du document
	 * @return Liste des choix de suppressions
	 * @throws ClientException
	 */
	List<String> getChoixSuppressionFichierFondDeDossierList(String numeroVersion) throws ClientException;

	/**
	 * Renvoie les fils directs (répertoire) du document racine "fond de dossier"
	 * 
	 * @param session
	 *            session
	 * @param fondDeDossierDocument
	 *            document fondDeDossier
	 * @return Liste des fils direct du fdd
	 * @throws ClientException
	 */
	List<FondDeDossierFile> getFondDeDossierPublicDocument(CoreSession session, DocumentModel fondDeDossierDocument)
			throws ClientException;

	/**
	 * Définit si le nom du fichier associé au document est unique pour les répertoires "Fond de dossier" du dossier
	 * courant.
	 * 
	 * @param fondDeDossier
	 *            document fondDeDossier lié au dossier courant
	 * @param fichierFdd
	 *            document contenant le nom du fichier
	 * @param session
	 *            session
	 * @return vrai si le nom du fichier est unique
	 * @throws ClientException
	 */
	Boolean isFondDeDossierFileNameUnique(FondDeDossier fondDeDossier, DocumentModel fichierFdd, CoreSession session)
			throws ClientException;

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
	 * @throws ClientException
	 */
	Boolean hasFondDeDossierFileVisibilityChanged(FondDeDossier fondDeDossier, DocumentModel fichierFdd,
			CoreSession session) throws ClientException;

	/**
	 * Retourne la liste des fichiers dans le fond de dossier donné, en respectant les droits de visibilité
	 * 
	 * @param session
	 *            session
	 * @param fddDoc
	 *            document FondDeDossier
	 * @return Liste des fichiers dans le fond de dossier
	 */
	List<DocumentModel> getFddDocuments(CoreSession session, Dossier dossier, SSPrincipal currentUser)
			throws ClientException;

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
	public boolean isRepertoireVisible(Dossier dossier, String repName, SSPrincipal currentUser);

	/**
	 * vérifie la validité d'une chaine de caractère en fonction des caractères non autorisés
	 * 
	 * @param filename
	 * @return vrai si la string en paramètre ne contient pas l'un des caractères interdits, faux sinon
	 */
	Boolean isFondDeDossierFileNameCorrect(FondDeDossier fondDeDossierDoc, DocumentModel currentFondDeDossierElement)
			throws ClientException;

	List<FondDeDossierFolder> getVisibleChildrenFolder(CoreSession session, DocumentModel fondDeDossierDoc,
			SSPrincipal ssPrincipal) throws ClientException;

	/**
	 * Modification d'un document dans l'arborescence du fond de dossier.
	 * 
	 * @param session
	 * @param oldFichierDoc
	 * @param newFichierDoc
	 * @param dossierDoc
	 * @throws ClientException
	 */
	void updateFddFile(CoreSession session, DocumentModel oldFichierOld, DocumentModel newFichierOld,
			DocumentModel dossierDoc) throws ClientException;

	/**
	 * Find Fond De Dossier From Dossier
	 */
	DocumentModel getFondDeDossierFromDossier(final CoreSession session, final DocumentModel dossier);

	/**
	 * Permet de savoir si le dossier a des pièces jointes
	 */
	public boolean havePieceJointeDossier(CoreSession coreSession, Dossier dossier) throws ClientException;

	void logDeleteFileFromFDD(DocumentModel dossierDoc, String fileName, CoreSession session) throws ClientException;

}
