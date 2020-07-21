package fr.dila.reponses.core.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.versioning.VersioningService;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.api.tree.SSTreeFolder;
import fr.dila.ss.core.service.SSFondDeDossierServiceImpl;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;

/**
 * Implémentation du service pour la gestion de l'arborescence du fond de dossier.
 * 
 * @author arolin
 */
public class FondDeDossierServiceImpl extends SSFondDeDossierServiceImpl implements FondDeDossierService {

	private static final long		serialVersionUID				= 8292406959483724550L;

	private static final Log		log								= LogFactory.getLog(FondDeDossierServiceImpl.class);

	/*
	 * Liste de caractères non autorisés dans le nom de document
	 */
	private String[]				forbiddenChars					= { "\'" };

	protected static final String	SQL_REQUETE_DOSSIERS_HAVE_FILE	= "select d.id as id from \"DOSSIER_REPONSE\" d, \"FONDDOSSIER\" f,\"FILE\" fi, \"HIERARCHY\" h "
																			+ "where d.id IN (%s) "
																			+ "AND d.iddocumentfdd = f.id "
																			+ "AND (h.parentid = f.repertoire_ministere OR h.parentid = f.repertoire_parlement OR h.parentid = f.repertoire_sgg) "
																			+ "AND h.id = fi.id";

	@Override
	public DocumentModel createFondDeDossierFile(CoreSession session, DocumentModel fondDeDossierDoc, String fileName,
			String niveauVisibilite, Blob content) throws ClientException {
		if (log.isDebugEnabled()) {
			log.debug("Création d'un fichier dans l'arborescence du fond de dossier");
		}
		if (fondDeDossierDoc == null) {
			throw new ClientException("document 'element fdd' not found");
		}

		String parentDocId = getRepositoryDocIdLinkToVisibilityLevel(fondDeDossierDoc.getAdapter(FondDeDossier.class),
				session, niveauVisibilite);
		DocumentModel parentDoc = session.getDocument(new IdRef(parentDocId));
		// create a document with the right type
		DocumentModel elementFondDossierModel = createBareFondDeDossierFichier(session, parentDoc, fileName, content);

		FondDeDossierFile fichier = elementFondDossierModel.getAdapter(FondDeDossierFile.class);
		// set element fond de dossier specific properties
		fichier.setNiveauVisibilite(niveauVisibilite);
		fichier.setNumeroVersion("1");

		// setter le ministere interpelle courant comme ministre aui a ajouter ce document
		DocumentModel dossierDoc = session.getParentDocument(fondDeDossierDoc.getRef());
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		String idMinistere = dossier.getIdMinistereAttributaireCourant();
		fichier.setMinistereAjoute(idMinistere);

		elementFondDossierModel = persistFileInFolder(session, elementFondDossierModel);

		// journalisation de l'action dans les logs
		logFondDossierCreate(session, dossierDoc, fileName);

		// setter le paramètre hasPJ pour permettre une recherche plus rapide de cette information
		dossier.setHasPJ(true);
		dossier.save(session);

		return elementFondDossierModel;
	}

	@Override
	public DocumentModel createFondDeDossierFile(CoreSession session, DocumentModel fondDeDossierDoc, String fileName,
			String niveauVisibilite) throws ClientException {
		if (fondDeDossierDoc == null) {
			throw new ClientException("document 'element fdd' not found");
		}
		return createFondDeDossierFile(session, fondDeDossierDoc, fileName, niveauVisibilite, null);
	}

	protected DocumentModel initFondDeDossierFile(CoreSession session, DocumentModel parentDoc, String filename,
			String niveauVisibilite, String idMinistereAjoute) throws SSException {
		// create a document with the right type
		DocumentModel elementFondDossierModel = createBareFondDeDossierFichier(session, parentDoc, filename, null);

		FondDeDossierFile fichier = elementFondDossierModel.getAdapter(FondDeDossierFile.class);
		// set element fond de dossier specific properties
		fichier.setNiveauVisibilite(niveauVisibilite);
		fichier.setNumeroVersion("1");

		// setter le ministere interpelle courant comme ministre aui a ajouter ce document
		fichier.setMinistereAjoute(idMinistereAjoute);
		return fichier.getDocument();
	}

	@Override
	public List<FondDeDossierFolder> getVisibleChildrenFolder(CoreSession session, DocumentModel fondDeDossierDoc,
			SSPrincipal currentUser) throws ClientException {
		DocumentModel dossierModel = session.getParentDocument(fondDeDossierDoc.getRef());
		Dossier dossier = dossierModel.getAdapter(Dossier.class);

		List<FondDeDossierFolder> folders = getChildrenFolder(session, fondDeDossierDoc);
		List<FondDeDossierFolder> foldersReturn = new ArrayList<FondDeDossierFolder>(folders);
		for (FondDeDossierFolder folder : folders) {
			if (!isRepertoireVisible(dossier, folder.getName(), currentUser)) {
				foldersReturn.remove(folder);
			}
		}
		return foldersReturn;
	}

	private DocumentRef[] listAllRepertoireIds(FondDeDossier fondDeDossier) {
		DocumentRef[] refs = new DocumentRef[3];
		if (fondDeDossier != null) {
			refs[0] = new IdRef(fondDeDossier.getRepertoireParlementId());
			refs[1] = new IdRef(fondDeDossier.getRepertoireSggId());
			refs[2] = new IdRef(fondDeDossier.getRepertoireMinistereId());
		}
		return refs;
	}

	@Override
	public boolean isRepertoireVisible(Dossier dossier, String repName, SSPrincipal currentUser) {

		// pour chaque type de répertoire on signale si l'utilisateur est
		boolean isVisible = false;
		if (ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME.equals(repName)) {
			// répertoire accessible à tous les utilisateurs
			isVisible = true;
		} else {
			String idMinistere = dossier.getIdMinistereAttributaireCourant();
			// on récupère le ministère d'appartenance de l'utilisateur
			Set<String> ministereIds = currentUser.getMinistereIdSet();

			if (ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_NAME.equals(repName)) {
				// si l'utilisateur fait parti du ministère d'appartenance de l'acte, il peut visualiser le répertoire
				isVisible = ministereIds.contains(idMinistere);
			} else if (ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME.equals(repName)) {
				// si l'utilisateur fait parti d'un profil SGG ou si l'utilisateur fait parti du ministère
				// d'appartenance de l'acte,
				// il peut visualiser le répertoire
				isVisible = ministereIds.contains(idMinistere)
						|| currentUser.getGroups().contains(ReponsesBaseFunctionConstant.FOND_DOSSIER_REPERTOIRE_SGG);
			}
		}

		return isVisible;
	}

	@Override
	public List<FondDeDossierFile> getFondDeDossierPublicDocument(CoreSession session,
			DocumentModel fondDeDossierDocument) throws ClientException {
		FondDeDossier fdd = fondDeDossierDocument.getAdapter(FondDeDossier.class);
		String repertoireParlementId = fdd.getRepertoireParlementId();
		// on récupère les fils du répertoires
		return getChildrenFile(session, new IdRef(repertoireParlementId));
	}

	@Override
	public String getDefaultNiveauVisibiliteFromRepository(String idRepertoire, CoreSession session) {
		String defaultNiveauVisibilite = null;
		// get repository document
		try {
			if (idRepertoire != null) {
				DocumentModel docModel = session.getDocument(new IdRef(idRepertoire));
				String repositoryName = docModel.getName();
				// get the label linked to the repository name
				if (repositoryName.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME)) {
					defaultNiveauVisibilite = ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL;
				} else if (repositoryName
						.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME)) {
					defaultNiveauVisibilite = ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL;
				} else if (repositoryName
						.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_NAME)) {
					defaultNiveauVisibilite = ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL;
				}
			}
		} catch (ClientException e) {
			throw new RuntimeException(
					"erreur lors de la récupération du niveau de visibilité par défaut du répertoire", e);
		}

		return defaultNiveauVisibilite;
	}

	protected String getRepositoryDocIdLinkToVisibilityLevel(FondDeDossier fondDeDossier, CoreSession session,
			String visibilityLevel) {
		String repositoryDocId = null;
		// get RepositoryName
		if (fondDeDossier != null) {
			if (visibilityLevel.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL)) {
				repositoryDocId = fondDeDossier.getRepertoireParlementId();
			} else if (visibilityLevel
					.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL)) {
				repositoryDocId = fondDeDossier.getRepertoireSggId();
			} else if (visibilityLevel
					.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL)) {
				repositoryDocId = fondDeDossier.getRepertoireMinistereId();
			} else {
				throw new ClientRuntimeException("Niveau de visibilite non supporte [" + visibilityLevel + "]");
			}
		}

		return repositoryDocId;
	}

	@Override
	public void delete(String docId, CoreSession session, DocumentModel dossierDoc) throws ClientException {
		if (log.isDebugEnabled()) {
			log.debug("delete method");
		}

		// get document
		DocumentModel docModel = session.getDocument(new IdRef(docId));

		if (docModel == null) {
			throw new ClientException("id document not found");
		}
		// journalisation de l'action dans les logs
		logFondDossierDelete(session, dossierDoc, "");
		delete(session, docModel);
		// On regarde si on a encore des documents dans le fond de dossier
		Dossier dossier = docModel.getAdapter(Dossier.class);
		if (dossier != null && !havePieceJointeDossier(session, dossier)) {
			// si plus de PJ, on passe hasPJ à false
			dossier.setHasPJ(false);
			dossier.save(session);
		}
	}

	@Override
	public void logDeleteFileFromFDD(DocumentModel dossierDoc, String fileName, CoreSession session)
			throws ClientException {

		// journalisation de l'action dans les logs
		logFondDossierDelete(session, dossierDoc, fileName);
	}

	/**
	 * Effectue la suppression d'un document FondDeDossierFichier - descend l'arborescence si un FondDeDossierRepertoire
	 * est donné
	 * 
	 * @param session
	 *            CoreSession
	 * @param doc
	 *            le fondDeDossierFichier à supprimer
	 * @throws ClientException
	 */
	private void delete(final CoreSession session, final DocumentModel doc) throws ClientException {
		String documentType = doc.getType();
		if (documentType.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE)) {
			// delete "Répertoire" children id.
			for (DocumentModel child : session.getChildren(doc.getRef())) {
				delete(session, child);
			}
		} else if (documentType.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE)) {
			deleteFile(session, doc);
		} else {
			throw new ClientException("document type unknown");
		}
		// On regarde si on a encore des documents dans le fond de dossier
		Dossier dossier = doc.getAdapter(Dossier.class);
		if (dossier != null && !havePieceJointeDossier(session, dossier)) {
			// si plus de PJ, on passe hasPJ à false
			dossier.setHasPJ(false);
			dossier.save(session);
		}
	}

	@Override
	public void restoreToPreviousVersion(CoreSession session, String currentFileFddDocId, DocumentModel dossierDoc)
			throws ClientException {
		// get fileFdd document
		DocumentRef currentFileFddDocRef = new IdRef(currentFileFddDocId);
		DocumentModel currentFileDoc = new UnrestrictedGetDocumentRunner(session).getById(currentFileFddDocId);
		FondDeDossierFile versionFileN1 = currentFileDoc.getAdapter(FondDeDossierFile.class);
		// get fileFdd visibility
		String niveauVisibilite = versionFileN1.getNiveauVisibilite();
		// get version number
		Long majorVersionLong = versionFileN1.getMajorVersion();
		// get all version
		List<DocumentModel> versionList = session.getVersions(currentFileFddDocRef);
		// get the last version
		DocumentModel lastVersion = versionList.get(majorVersionLong.intValue() - 2);
		DocumentRef lastVersionDocRef = lastVersion.getRef();
		// update current document with last version
		lastVersion = session.restoreToVersion(currentFileFddDocRef, lastVersionDocRef);

		FondDeDossierFile versionFileN = lastVersion.getAdapter(FondDeDossierFile.class);
		boolean lastVersionToDelete = false;
		if (!versionFileN.getNiveauVisibilite().equals(niveauVisibilite)) {
			// keep old visibility level
			versionFileN.setNiveauVisibilite(niveauVisibilite);
			String numeroVersionN = String.valueOf((Integer.parseInt(versionFileN1.getNumeroVersion()) - 1));
			versionFileN.setNumeroVersion(numeroVersionN);

			// save update
			lastVersion = session.saveDocument(lastVersion);
			lastVersionToDelete = true;
		}
		// get new version list
		versionList = session.getVersions(lastVersion.getRef());

		// remove the last version document (currentDoc before save) and old version we retrieve
		// remove the last version document
		session.removeDocument(versionList.get(versionList.size() - 1).getRef());
		// remove old version we retrieve
		if (lastVersionToDelete) {
			session.removeDocument(lastVersionDocRef);
		}

		// change the document path
		if (DublincoreSchemaUtils.getTitle(lastVersion) != null) {
			String newPathName = DublincoreSchemaUtils.getTitle(lastVersion);
			DocumentRef docParentRef = lastVersion.getParentRef();
			// lastVersion.setPathInfo(lastVersion.getPathAsString(), oldName);
			moveVersionableDocumentToFolder(docParentRef, newPathName, lastVersion, session);
		}
		// save change in session
		session.save();
		// journalisation de l'action dans les logs
		logFondDossierDelete(session, dossierDoc, lastVersion.getTitle());
	}

	@Override
	public void moveFddFile(CoreSession session, FondDeDossier fondDeDossier, DocumentModel fichierFdd,
			DocumentModel dossierDoc, String newVisibility) throws ClientException {
		if (log.isDebugEnabled()) {
			log.debug("update method");
		}
		// get old document from session
		String oldDocumentId = fichierFdd.getId();
		DocumentModel oldFichierFdd = new UnrestrictedGetDocumentRunner(session).getById(oldDocumentId);

		// get old document parent
		DocumentRef oldParentRef = fichierFdd.getParentRef();
		DocumentModel oldParentModel = new UnrestrictedGetDocumentRunner(session).getByRef(oldParentRef);

		String fileName = fichierFdd.getTitle();
		// get new document parent from niveauvisibilite
		String parentModelId = getRepositoryDocIdLinkToVisibilityLevel(fondDeDossier, session, newVisibility);
		// check if new document parent is old document parent
		Boolean documentHasMoved = !oldParentModel.getId().equals(parentModelId);

		if (!documentHasMoved) {
			throw new ClientException("error : document has not moved !");
		}

		// copy the old document and all versions in new repository
		// update document path
		oldFichierFdd = moveVersionableDocumentToFolder(new IdRef(parentModelId), fileName, oldFichierFdd, session);

		// set fichier fdd niveauVisibilite
		oldFichierFdd.setProperty(DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE, newVisibility);

		// save fichier fdd
		session.saveDocument(oldFichierFdd);
		session.save();

		String formattedComment = MessageFormat.format(ReponsesEventConstant.COMMENT_DEPL_FOND_DOSSIER, fileName,
				newVisibility);
		// journalisation de l'action dans les logs
		logFondDossierUpdate(session, dossierDoc, formattedComment);
	}

	@Override
	public void updateFddFile(final CoreSession session, DocumentModel oldFichierDoc, DocumentModel newFichierDoc,
			final DocumentModel dossierDoc) throws ClientException {

		// force unlock liveEdit
		STServiceLocator.getSTLockService().unlockDocUnrestricted(session, oldFichierDoc);

		FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
		String oldName = oldFichierDoc.getTitle();
		// on récupère le nom du fichier
		final String docName = StringUtil.removeSpacesAndAccent(newFichier.getFilename());
		// set document name
		DublincoreSchemaUtils.setTitle(oldFichierDoc, docName);
		// set document file properties
		oldFichierDoc.setProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_FILENAME_PROPERTY,
				newFichier.getFilename());
		oldFichierDoc.setProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_CONTENT_PROPERTY,
				newFichier.getContent());
		oldFichierDoc.setProperty(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE,
				newFichier.getNiveauVisibilite());
		oldFichierDoc.setProperty(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE,
				newFichier.getNiveauVisibilite());

		// incrementation du numero de version
		oldFichierDoc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
		FondDeDossierFile oldFichier = oldFichierDoc.getAdapter(FondDeDossierFile.class);
		String numeroVersionStr = oldFichier.getNumeroVersion();
		oldFichier.setNumeroVersion(String.valueOf(Integer.valueOf(numeroVersionStr) + 1));
		// create document in session
		session.move(
				oldFichierDoc.getRef(),
				new IdRef(getRepositoryDocIdLinkToVisibilityLevel(dossierDoc.getAdapter(Dossier.class)
						.getFondDeDossier(session), session, newFichier.getNiveauVisibilite())), docName);
		session.saveDocument(oldFichierDoc);
		// commit modification
		session.save();
		String formattedComment = MessageFormat.format(ReponsesEventConstant.COMMENT_MODIF_FOND_DOSSIER, oldName,
				docName);
		// journalisation de l'action dans les logs
		logFondDossierUpdate(session, dossierDoc, formattedComment);
	}

	/**
	 * 
	 * Move a document and linked versions into another folder.
	 * 
	 */
	protected DocumentModel moveVersionableDocumentToFolder(DocumentRef newFolderRef, String newVersionName,
			DocumentModel versionableDocument, CoreSession session) throws ClientException {
		// move the document and all versions in new repository
		DocumentRef versionableDocumentRef = versionableDocument.getRef();
		try {
			// move document and document verson to new Folder
			session.move(versionableDocumentRef, newFolderRef, newVersionName);
		} catch (ClientException e) {
			throw new ClientException("erreur lors du déplacement du document dans un autre répertoire", e);
		}
		// get new document
		DocumentModel newVersionableDocument = new UnrestrictedGetDocumentRunner(session)
				.getByRef(versionableDocumentRef);
		// return document
		return newVersionableDocument;
	}

	@Override
	public List<String> getChoixSuppressionFichierFondDeDossierList(String numeroVersion) throws ClientException {
		List<String> listChoixSuppressionLabel = new ArrayList<String>();
		listChoixSuppressionLabel.add(SSTreeConstants.DELETE_ALL_CHOICE);
		if (numeroVersion != null && !numeroVersion.equals("1")) {
			listChoixSuppressionLabel.add(SSTreeConstants.DELETE_CURRENT_VERSION_CHOICE);
		}
		return listChoixSuppressionLabel;
	}

	@Override
	public String getDefaultChoixSuppressionFichierFondDeDossier(String numeroVersion) {
		String defaultChoixSuppressionFichierFondDeDossier = SSTreeConstants.DELETE_ALL_CHOICE;
		if (numeroVersion != null && !numeroVersion.equals("1")) {
			defaultChoixSuppressionFichierFondDeDossier = SSTreeConstants.DELETE_CURRENT_VERSION_CHOICE;
		}
		return defaultChoixSuppressionFichierFondDeDossier;
	}

	@Override
	public Boolean isFondDeDossierFileNameUnique(FondDeDossier fondDeDossier, DocumentModel fichierFdd,
			CoreSession session) throws ClientException {
		// on verifie que le nom du fichier n'est pas déjà présent comme nom de document dans l'arborescence
		String newDocumentName = (String) fichierFdd.getProperty(STSchemaConstant.FILE_SCHEMA,
				STSchemaConstant.FILE_FILENAME_PROPERTY);
		if (StringUtils.isBlank(newDocumentName)) {
			log.warn("newDocumentName is null !");
			return false;
		}
		if (log.isDebugEnabled()) {
			log.debug("newDocumentName =" + newDocumentName);
		}
		// note : optimisation possible => nuxeo sql query ?
		// String query = "select * from FondDeDossierFichier where efd:filename = '" + newDocumentName +"'";
		// on récupère la liste des répertoire
		List<FondDeDossierFolder> repertoireList = getChildrenFolder(session, fondDeDossier.getDocument());
		List<FondDeDossierFile> filesList = new ArrayList<FondDeDossierFile>();
		for (FondDeDossierFolder folder : repertoireList) {
			filesList.addAll(getChildrenFile(session, folder.getDocument()));
		}

		for (FondDeDossierFile file : filesList) {
			if (!file.isDeleted()) {
				DocumentRef fddFileRef = file.getDocument().getRef();
				String fileName = file.getFilename();
				// on vérifie le nom du fichier
				if (newDocumentName.equals(fileName)) {
					return false;
				}
				// on récupère les différentes versions du fichier
				List<DocumentModel> fddFileversionList = session.getVersions(fddFileRef);
				if (fddFileversionList.size() > 0) {
					// on parcourt les différentes version du fichier
					for (DocumentModel versionDoc : fddFileversionList) {
						// on vérifie le nom de la version
						fileName = DublincoreSchemaUtils.getTitle(versionDoc);
						if (newDocumentName.equals(fileName)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public Boolean hasFondDeDossierFileVisibilityChanged(FondDeDossier fondDeDossier, DocumentModel fichierFdd,
			CoreSession session) throws ClientException {
		// get old document parent
		DocumentRef oldParentRef = fichierFdd.getParentRef();
		DocumentModel oldParentModel = session.getDocument(oldParentRef);

		// set new visibility to old document
		String niveauVisibilite = (String) fichierFdd.getProperty(
				DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE);
		// get new document parent from niveauvisibilite
		String parentModelId = getRepositoryDocIdLinkToVisibilityLevel(fondDeDossier, session, niveauVisibilite);
		// check if new document parent is old document parent
		Boolean hasChanged = !oldParentModel.getId().equals(parentModelId);
		return hasChanged;
	}

	@Override
	public List<DocumentModel> getFddDocuments(CoreSession session, Dossier dossier, SSPrincipal currentUser)
			throws ClientException {
		FondDeDossier fdd = dossier.getFondDeDossier(session);

		// recupere tous les repertoires
		DocumentRef[] refs = listAllRepertoireIds(fdd);
		List<DocumentModel> folders = session.getDocuments(refs);

		List<DocumentModel> docs = new ArrayList<DocumentModel>();
		docs.addAll(folders);

		// recupere les fichiers visibles
		for (DocumentModel folder : folders) {
			if (isRepertoireVisible(dossier, folder.getName(), currentUser)) {
				List<DocumentModel> files = session.getChildren(folder.getRef(),
						ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE);
				for (DocumentModel doc : files) {
					if (!STLifeCycleConstant.DELETED_STATE.equals(doc.getCurrentLifeCycleState())) {
						docs.add(doc);
					}
				}
			}
		}

		return docs;
	}

	/**
	 * Enregistre dans les logs l'ajout d'un document dans le fond de dossier.
	 * 
	 * @param session
	 * @param docModel
	 * @throws Exception
	 */
	protected void logFondDossierCreate(CoreSession session, DocumentModel docModel, String filename)
			throws ClientException {
		logFondDossierEvent(session, docModel, ReponsesEventConstant.COMMENT_AJOUT_FOND_DOSSIER + filename,
				ReponsesEventConstant.DOSSIER_AJOUT_FOND_DOSSIER_EVENT);
	}

	/**
	 * Enregistre dans les logs la mise à jour d'un document dans le fond de dossier.
	 * 
	 * @param session
	 * @param docModel
	 * @throws Exception
	 */
	protected void logFondDossierUpdate(CoreSession session, DocumentModel docModel, String comment)
			throws ClientException {

		logFondDossierEvent(session, docModel, comment, ReponsesEventConstant.DOSSIER_MODIF_FOND_DOSSIER_EVENT);
	}

	/**
	 * Enregistre dans les logs la suppression d'un document dans le fond de dossier.
	 * 
	 * @param session
	 * @param docModel
	 * @param fileName
	 * @throws Exception
	 */
	protected void logFondDossierDelete(CoreSession session, DocumentModel docModel, String fileName)
			throws ClientException {
		logFondDossierEvent(session, docModel, ReponsesEventConstant.COMMENT_SUPP_FOND_DOSSIER + fileName,
				ReponsesEventConstant.DOSSIER_SUPP_FOND_DOSSIER_EVENT);
	}

	protected void logFondDossierEvent(CoreSession session, DocumentModel docModel, String comment, String eventName)
			throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		try {
			journalService.journaliserActionFDD(session, docModel, eventName, comment);
		} catch (Exception e) {
			throw new ClientException("erreur lors de l'enregistrement du log", e);
		}
	}

	@Override
	public Boolean isFondDeDossierFileNameCorrect(FondDeDossier fondDeDossier, DocumentModel fichierFdd)
			throws ClientException {
		// on verifie que le nom du fichier n'est pas déjà présent comme nom de document dans l'arborescence
		String newDocumentName = (String) fichierFdd.getProperty(STSchemaConstant.FILE_SCHEMA,
				STSchemaConstant.FILE_FILENAME_PROPERTY);
		if (StringUtils.isBlank(newDocumentName)) {
			log.warn("newDocumentName is null !");
			return false;
		}
		if (log.isDebugEnabled()) {
			log.debug("newDocumentName =" + newDocumentName);
		}
		for (String s : forbiddenChars) {
			if (newDocumentName.contains(s)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void initFondDeDossier(CoreSession session, DocumentModel fondDeDossierDoc) throws ClientException {
		if (log.isDebugEnabled()) {
			log.debug("init fond de dossier : create repository");
		}
		if (fondDeDossierDoc == null) {
			throw new ClientException("Document FondDeDossier null : creation des répertoires fils impossible");
		}
		FondDeDossier fondDeDossier = fondDeDossierDoc.getAdapter(FondDeDossier.class);
		// Crée les 3 répertoires standard du fond de dossier
		DocumentModel repertoire = this.createFondDeDossierRepertoire(session, fondDeDossierDoc,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME);
		fondDeDossier.setRepertoireParlementId(repertoire.getId());
		repertoire = this.createFondDeDossierRepertoire(session, fondDeDossierDoc,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME);
		fondDeDossier.setRepertoireSggId(repertoire.getId());
		repertoire = this.createFondDeDossierRepertoire(session, fondDeDossierDoc,
				ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_NAME);
		fondDeDossier.setRepertoireMinistereId(repertoire.getId());

		session.saveDocument(fondDeDossier.getDocument());
		session.save();
	}

	@Override
	public String getFondDeDossierRepertoireType() {
		return ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE;
	}

	@Override
	protected SSTreeFolder getFolderImplFromDoc(DocumentModel doc) {
		return doc.getAdapter(FondDeDossierFolder.class);
	}

	@Override
	protected SSTreeFile getFileImplFromDoc(DocumentModel doc) {
		return doc.getAdapter(FondDeDossierFile.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FondDeDossierFile> getChildrenFile(CoreSession session, DocumentModel repertoireParent)
			throws ClientException {
		return (List<FondDeDossierFile>) super.getChildrenFile(session, repertoireParent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FondDeDossierFile> getChildrenFile(CoreSession session, DocumentRef repertoireParentRef)
			throws ClientException {
		return (List<FondDeDossierFile>) super.getChildrenFile(session, repertoireParentRef);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FondDeDossierFolder> getChildrenFolder(CoreSession session, DocumentModel repertoireParent)
			throws ClientException {
		return (List<FondDeDossierFolder>) super.getChildrenFolder(session, repertoireParent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FondDeDossierFolder> getChildrenFolder(CoreSession session, DocumentRef repertoireParentRef)
			throws ClientException {
		return (List<FondDeDossierFolder>) super.getChildrenFolder(session, repertoireParentRef);
	}

	/**
	 * Find Fond De Dossier From Dossier
	 */
	@Override
	public DocumentModel getFondDeDossierFromDossier(final CoreSession session, final DocumentModel dossier) {
		DocumentModel fddDoc = null;
		final Dossier dossierObject = dossier.getAdapter(Dossier.class);
		if (dossierObject != null) {
			final FondDeDossier fdd = dossierObject.getFondDeDossier(session);
			if (fdd != null) {
				fddDoc = fdd.getDocument();
			}
		}
		return fddDoc;
	}

	@Override
	public boolean havePieceJointeDossier(CoreSession coreSession, Dossier dossier) throws ClientException {

		// On analyse si le dossier a des pièces jointes
		Boolean hasPJ = false;
		// On récupère la liste des répertoires
		List<DocumentModel> listRepertoiresFondDossier = dossier.getFondDeDossier(coreSession).getRepertoireDocument(
				coreSession);
		for (DocumentModel folder : listRepertoiresFondDossier) {
			List<DocumentModel> files = coreSession.getChildren(folder.getRef(),
					ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE);
			// Dans chaque répertoire, on récupère les documents qui ne sont pas supprimés
			for (DocumentModel doc : files) {
				if (!STLifeCycleConstant.DELETED_STATE.equals(doc.getCurrentLifeCycleState())) {
					return true;
				}
			}
		}
		return hasPJ;
	}
}
