package fr.dila.ss.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.api.tree.SSTreeFolder;
import fr.dila.ss.api.tree.SSTreeNode;

public interface SSTreeService extends Serializable {
	// /////////////////
	// méthodes concernant les fichiers
	// ////////////////
	/**
	 * Créé (persiste) un fichier dans un répertoire
	 * 
	 * @param session
	 *            session utilisée pour créé le fichier
	 * @param bareFileDoc
	 *            le documentModel du fichier non persisté
	 * @return le documentModel du fichier créé
	 */
	DocumentModel persistFileInFolder(final CoreSession session, DocumentModel bareFileDoc) throws SSException;

	/**
	 * Créé un fichier dans un répertoire parent
	 * 
	 * @param session
	 *            session utilisée pour créé le fichier
	 * @param parent
	 *            le répertoire parent du document
	 * @param documentType
	 *            le type de document à créer
	 * @param fileName
	 *            le nom du document à créer
	 * @param content
	 *            le contenu du fichier (peut être null)
	 * @return le documentModel du fichier créé
	 */
	DocumentModel createFileInFolder(final CoreSession session, DocumentModel parent, String documentType,
			String fileName, Blob content) throws SSException;

	/**
	 * Créé un fichier <b>non persisté</b> dans un répertoire parent
	 * 
	 * @param session
	 *            session utilisée pour créé le fichier
	 * @param parent
	 *            le répertoire parent du document
	 * @param documentType
	 *            le type de document à créer
	 * @param fileName
	 *            le nom du document à créer
	 * @param content
	 *            le contenu du fichier (peut être null)
	 * @return le documentModel du fichier initialisé
	 */
	DocumentModel createBareFileInFolder(final CoreSession session, DocumentModel parent, String documentType,
			String fileName, Blob content) throws SSException;

	/**
	 * Supprime un fichier
	 */
	void deleteFile(final CoreSession session, DocumentModel document) throws SSException;

	// /////////////////
	// methodes concernant l'ajout et la suppression de répertoires
	// ////////////////
	/**
	 * creation d'un nouveau répertoire dans le répertoire sélectionné.
	 * 
	 */
	DocumentModel createNewFolder(final CoreSession session, final String folderType, final DocumentModel parent,
			final String folderName) throws SSException;

	/**
	 * creation d'un nouveau répertoire avant le répertoire sélectionné.
	 * 
	 */
	DocumentModel createNewFolderBefore(final CoreSession session, final String folderType,
			final DocumentModel currentDoc, final String folderName) throws SSException;

	/**
	 * creation d'un nouveau répertoire après le répertoire sélectionné.
	 * 
	 */
	DocumentModel createNewFolderAfter(final CoreSession session, final String folderType,
			final DocumentModel currentDoc, final String folderName) throws SSException;

	/**
	 * Change le titre du répertoire sélectionné.
	 */
	DocumentModel renameFolder(final CoreSession session, final DocumentModel folderDoc, final String newTitle)
			throws ClientException;

	/**
	 * Supprime le document sélectionné on supprime aussi les documents fils du répertoire.
	 */
	void deleteFolder(final CoreSession session, final DocumentModel folderDoc) throws ClientException;

	// /////////////////
	// methodes concernant la récupération
	// ////////////////
	/**
	 * Récupère les enfants du parent Renseigne la profondeur des SSTreeNode enfant à profondeur parent +1
	 */
	List<SSTreeNode> getChildrenNode(final CoreSession session, final SSTreeNode parent) throws ClientException;

	/**
	 * Récupère les enfants du parent Renseigne la profondeur des SSTreeNode enfant à profondeur parent +1
	 */
	List<SSTreeNode> getChildrenNode(final CoreSession session, final DocumentRef parentRef, final int parentDepth)
			throws ClientException;

	/**
	 * 
	 * @param session
	 * @param parentDoc
	 * @return
	 * @throws ClientException
	 */
	List<? extends SSTreeFolder> getChildrenFolder(final CoreSession session, final DocumentModel parentDoc)
			throws ClientException;

	/**
	 * Récupère les enfants répertoire d'un parent
	 * 
	 * @param session
	 * @param parentDocRef
	 * @return
	 * @throws ClientException
	 */
	List<? extends SSTreeFolder> getChildrenFolder(final CoreSession session, final DocumentRef parentDocRef)
			throws ClientException;

	/**
	 * 
	 * @param session
	 * @param parentDoc
	 * @return
	 * @throws ClientException
	 */
	List<? extends SSTreeFile> getChildrenFile(final CoreSession session, final DocumentModel parentDoc)
			throws ClientException;

	/**
	 * Récupère les enfants fichier d'un parent
	 * 
	 * @param session
	 * @param parentDocRef
	 * @return
	 * @throws ClientException
	 */
	List<? extends SSTreeFile> getChildrenFile(final CoreSession session, final DocumentRef parentDocRef)
			throws ClientException;

	/**
	 * restaure la précédente version du document de l'arborescence
	 * 
	 * @param session
	 * @param currentDocId
	 *            id du document à restaurer
	 * @param dossierDoc
	 *            le dossier lié à l'arborescence
	 * @throws ClientException
	 */
	void restoreToPreviousVersion(CoreSession session, String currentDocId, DocumentModel dossierDoc)
			throws ClientException;

	/**
	 * détermine si un répertoire est vide
	 * 
	 * @param documentManager
	 * @param folder
	 * @return true si vide, false sinon
	 */
	Boolean isFolderEmpty(CoreSession documentManager, SSTreeFolder folder) throws ClientException;

	/**
	 * Met à jour un fichier de l'arborescence (montée de version, modification nom, contenu, auteur)
	 * 
	 * @param session
	 * @param fichier
	 * @param fileName
	 * @param blob
	 * @param currentUser
	 * @param dossierDocument
	 * @throws ClientException
	 */
	void updateFile(CoreSession session, DocumentModel fichier, String fileName, Blob blob, NuxeoPrincipal currentUser,
			DocumentModel dossierDocument) throws ClientException;

	/**
	 * Renomme un document et met à jour son path en conséquence
	 * @param session
	 * @param document
	 * @param newTitle
	 * @return
	 * @throws ClientException
	 */
	DocumentModel renameDocument(CoreSession session, DocumentModel document, String newTitle)
			throws ClientException;
}
