package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.constant.DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA;
import static fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE;
import static fr.dila.st.api.constant.STSchemaConstant.COMMON_SCHEMA;
import static fr.dila.st.api.constant.STSchemaConstant.DUBLINCORE_SCHEMA;
import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.CREATE_FILE_FONC;
import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.DEL_FILE_FONC;
import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.UPDATE_FILE_FONC;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
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
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.schema.FileSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringHelper;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.versioning.VersioningService;

/**
 * Implémentation du service pour la gestion de l'arborescence du fond de dossier.
 *
 * @author arolin
 */
public class FondDeDossierServiceImpl extends SSFondDeDossierServiceImpl implements FondDeDossierService {
    private static final long serialVersionUID = 8292406959483724550L;

    private static final STLogger LOGGER = STLogFactory.getLog(FondDeDossierServiceImpl.class);

    /*
     * Liste de caractères non autorisés dans le nom de document
     */
    private static final char[] FORBIDDEN_CHARS = { '\'' };

    protected static final String SQL_REQUETE_DOSSIERS_HAVE_FILE =
        "select d.id as id from \"DOSSIER_REPONSE\" d, \"FONDDOSSIER\" f,\"FILE\" fi, \"HIERARCHY\" h " +
        "where d.id IN (%s) " +
        "AND d.iddocumentfdd = f.id " +
        "AND (h.parentid = f.repertoire_ministere OR h.parentid = f.repertoire_parlement OR h.parentid = f.repertoire_sgg) " +
        "AND h.id = fi.id";

    @Override
    public DocumentModel createFondDeDossierFile(
        CoreSession session,
        DocumentModel fondDeDossierDoc,
        String fileName,
        String niveauVisibilite,
        Blob content
    ) {
        if (LOGGER.isDebugEnable()) {
            LOGGER.debug(session, CREATE_FILE_FONC, "Création d'un fichier dans l'arborescence du fond de dossier");
        }
        if (fondDeDossierDoc == null) {
            throw new NuxeoException("document 'element fdd' not found");
        }

        String parentDocId = getRepositoryDocIdLinkToVisibilityLevel(
            fondDeDossierDoc.getAdapter(FondDeDossier.class),
            session,
            niveauVisibilite
        );
        DocumentModel parentDoc = session.getDocument(new IdRef(parentDocId));
        // create a document with the right type
        if (content != null) {
            content.setFilename(fileName);
        }
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

    protected DocumentModel initFondDeDossierFile(
        CoreSession session,
        DocumentModel parentDoc,
        String filename,
        String niveauVisibilite,
        String idMinistereAjoute
    )
        throws SSException {
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
    public List<FondDeDossierFolder> getVisibleChildrenFolder(
        CoreSession session,
        DocumentModel fondDeDossierDoc,
        SSPrincipal currentUser
    ) {
        DocumentModel dossierModel = session.getParentDocument(fondDeDossierDoc.getRef());
        Dossier dossier = dossierModel.getAdapter(Dossier.class);

        List<FondDeDossierFolder> folders = getChildrenFolder(session, fondDeDossierDoc);
        List<FondDeDossierFolder> foldersReturn = new ArrayList<>(folders);
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
                isVisible =
                    ministereIds.contains(idMinistere) ||
                    currentUser.getGroups().contains(ReponsesBaseFunctionConstant.FOND_DOSSIER_REPERTOIRE_SGG);
            }
        }

        return isVisible;
    }

    @Override
    public List<FondDeDossierFile> getFondDeDossierPublicDocument(
        CoreSession session,
        DocumentModel fondDeDossierDocument
    ) {
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
                } else if (
                    repositoryName.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME)
                ) {
                    defaultNiveauVisibilite =
                        ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL;
                } else if (
                    repositoryName.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_NAME)
                ) {
                    defaultNiveauVisibilite = ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL;
                }
            }
        } catch (NuxeoException e) {
            throw new RuntimeException(
                "erreur lors de la récupération du niveau de visibilité par défaut du répertoire",
                e
            );
        }

        return defaultNiveauVisibilite;
    }

    protected String getRepositoryDocIdLinkToVisibilityLevel(
        FondDeDossier fondDeDossier,
        CoreSession session,
        String visibilityLevel
    ) {
        String repositoryDocId = null;
        // get RepositoryName
        if (fondDeDossier != null) {
            if (visibilityLevel.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_LABEL)) {
                repositoryDocId = fondDeDossier.getRepertoireParlementId();
            } else if (
                visibilityLevel.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_LABEL)
            ) {
                repositoryDocId = fondDeDossier.getRepertoireSggId();
            } else if (
                visibilityLevel.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL)
            ) {
                repositoryDocId = fondDeDossier.getRepertoireMinistereId();
            } else {
                throw new NuxeoException("Niveau de visibilite non supporte [" + visibilityLevel + "]");
            }
        }

        return repositoryDocId;
    }

    @Override
    public void delete(String docId, CoreSession session, DocumentModel dossierDoc) {
        if (LOGGER.isDebugEnable()) {
            LOGGER.debug(session, DEL_FILE_FONC, "delete method");
        }

        // get document
        DocumentModel docModel = session.getDocument(new IdRef(docId));

        if (docModel == null) {
            throw new NuxeoException("id document not found");
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
    public void logDeleteFileFromFDD(DocumentModel dossierDoc, String fileName, CoreSession session) {
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
     *
     */
    private void delete(final CoreSession session, final DocumentModel doc) {
        String documentType = doc.getType();
        if (documentType.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE)) {
            // delete "Répertoire" children id.
            for (DocumentModel child : session.getChildren(doc.getRef())) {
                delete(session, child);
            }
        } else if (documentType.equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE)) {
            deleteFile(session, doc);
        } else {
            throw new NuxeoException("document type unknown");
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
    public void restoreToPreviousVersion(CoreSession session, String currentFileFddDocId, DocumentModel dossierDoc) {
        // get fileFdd document
        DocumentRef currentFileFddDocRef = new IdRef(currentFileFddDocId);

        UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(
            session,
            FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
            STSchemaConstant.UID_SCHEMA
        );

        DocumentModel currentFileDoc = uGet.getById(currentFileFddDocId);
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
    public void moveFddFile(
        CoreSession session,
        FondDeDossier fondDeDossier,
        DocumentModel fichierFdd,
        DocumentModel dossierDoc,
        String newVisibility
    ) {
        if (LOGGER.isDebugEnable()) {
            LOGGER.debug(session, UPDATE_FILE_FONC, "update method");
        }

        // get old document parent
        DocumentRef oldParentRef = fichierFdd.getParentRef();
        DocumentModel oldParentModel = new UnrestrictedGetDocumentRunner(session).getByRef(oldParentRef);

        // get new document parent from niveauvisibilite
        String parentModelId = getRepositoryDocIdLinkToVisibilityLevel(fondDeDossier, session, newVisibility);
        // check if new document parent is old document parent
        boolean documentHasMoved = !oldParentModel.getId().equals(parentModelId);

        if (!documentHasMoved) {
            throw new NuxeoException("error : document has not moved !");
        }

        // copy the old document and all versions in new repository
        // update document path
        String fileName = fichierFdd.getTitle();
        // get old document from session
        String oldDocumentId = fichierFdd.getId();
        DocumentModel oldFichierFdd = new UnrestrictedGetDocumentRunner(
            session,
            FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
            DUBLINCORE_SCHEMA,
            COMMON_SCHEMA
        )
        .getById(oldDocumentId);
        oldFichierFdd = moveVersionableDocumentToFolder(new IdRef(parentModelId), fileName, oldFichierFdd, session);

        // set fichier fdd niveauVisibilite
        oldFichierFdd.setProperty(
            FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
            FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE,
            newVisibility
        );

        // save fichier fdd
        session.saveDocument(oldFichierFdd);
        session.save();

        String formattedComment = MessageFormat.format(
            ReponsesEventConstant.COMMENT_DEPL_FOND_DOSSIER,
            fileName,
            newVisibility
        );
        // journalisation de l'action dans les logs
        logFondDossierUpdate(session, dossierDoc, formattedComment);
    }

    @Override
    public void updateFddFile(
        final CoreSession session,
        DocumentModel oldFichierDoc,
        DocumentModel newFichierDoc,
        final DocumentModel dossierDoc
    ) {
        // force unlock liveEdit
        STServiceLocator.getSTLockService().unlockDocUnrestricted(session, oldFichierDoc);

        FondDeDossierFile newFichier = newFichierDoc.getAdapter(FondDeDossierFile.class);
        FondDeDossierFile oldFichier = oldFichierDoc.getAdapter(FondDeDossierFile.class);

        String oldName = oldFichierDoc.getTitle();

        // Modification de l'ancien fichier
        // on récupère le nom du fichier
        final String docName = StringHelper.removeSpacesAndAccent(newFichier.getFilename());
        // set document name
        DublincoreSchemaUtils.setTitle(oldFichierDoc, docName);
        // set document file properties
        oldFichier.setFilename(newFichier.getTitle());
        //		oldFichierDoc.setProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_FILENAME_PROPERTY,
        //				newFichier.getTitle());
        oldFichierDoc.setProperty(
            STSchemaConstant.FILE_SCHEMA,
            STSchemaConstant.FILE_CONTENT_PROPERTY,
            newFichier.getContent()
        );
        oldFichierDoc.setProperty(
            ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE,
            FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE,
            newFichier.getNiveauVisibilite()
        );
        oldFichierDoc.setProperty(
            ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE,
            ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE,
            newFichier.getNiveauVisibilite()
        );

        // incrementation du numero de version
        oldFichierDoc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);

        String numeroVersionStr = oldFichier.getNumeroVersion();
        oldFichier.setNumeroVersion(String.valueOf(Integer.parseInt(numeroVersionStr) + 1));
        // create document in session
        session.move(
            oldFichierDoc.getRef(),
            new IdRef(
                getRepositoryDocIdLinkToVisibilityLevel(
                    dossierDoc.getAdapter(Dossier.class).getFondDeDossier(session),
                    session,
                    newFichier.getNiveauVisibilite()
                )
            ),
            docName
        );
        session.saveDocument(oldFichierDoc);
        // commit modification
        session.save();
        String formattedComment = MessageFormat.format(
            ReponsesEventConstant.COMMENT_MODIF_FOND_DOSSIER,
            oldName,
            docName
        );
        // journalisation de l'action dans les logs
        logFondDossierUpdate(session, dossierDoc, formattedComment);
    }

    /**
     *
     * Move a document and linked versions into another folder.
     *
     */
    protected static DocumentModel moveVersionableDocumentToFolder(
        DocumentRef newFolderRef,
        String newVersionName,
        DocumentModel versionableDocument,
        CoreSession session
    ) {
        return session.move(versionableDocument.getRef(), newFolderRef, newVersionName);
    }

    @Override
    public List<String> getChoixSuppressionFichierFondDeDossierList(String numeroVersion) {
        List<String> listChoixSuppressionLabel = new ArrayList<>();
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
    public boolean hasFondDeDossierFileVisibilityChanged(
        FondDeDossier fondDeDossier,
        DocumentModel fichierFdd,
        CoreSession session
    ) {
        // get old document parent
        DocumentRef oldParentRef = fichierFdd.getParentRef();
        DocumentModel oldParentModel = session.getDocument(oldParentRef);

        // set new visibility to old document
        String niveauVisibilite = (String) fichierFdd.getProperty(
            FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
            FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE
        );
        // get new document parent from niveauvisibilite
        String parentModelId = getRepositoryDocIdLinkToVisibilityLevel(fondDeDossier, session, niveauVisibilite);
        // check if new document parent is old document parent
        return !oldParentModel.getId().equals(parentModelId);
    }

    @Override
    public List<DocumentModel> getFddDocuments(CoreSession session, STDossier stDossier) {
        Dossier dossier = (Dossier) stDossier;
        FondDeDossier fdd = dossier.getFondDeDossier(session);
        final SSPrincipal currentUser = (SSPrincipal) session.getPrincipal();

        // recupere tous les repertoires
        DocumentRef[] refs = listAllRepertoireIds(fdd);
        List<DocumentModel> folders = session.getDocuments(refs);

        List<DocumentModel> docs = new ArrayList<>();

        // recupere les fichiers visibles
        for (DocumentModel folder : folders) {
            if (isRepertoireVisible(dossier, folder.getName(), currentUser)) {
                List<DocumentModel> files = session.getChildren(
                    folder.getRef(),
                    ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE
                );
                if (!files.isEmpty()) {
                    docs.add(folder);
                    for (DocumentModel doc : files) {
                        if (!STLifeCycleConstant.DELETED_STATE.equals(doc.getCurrentLifeCycleState())) {
                            docs.add(doc);
                        }
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
    protected void logFondDossierCreate(CoreSession session, DocumentModel docModel, String filename) {
        logFondDossierEvent(
            session,
            docModel,
            ReponsesEventConstant.COMMENT_AJOUT_FOND_DOSSIER + filename,
            ReponsesEventConstant.DOSSIER_AJOUT_FOND_DOSSIER_EVENT
        );
    }

    /**
     * Enregistre dans les logs la mise à jour d'un document dans le fond de dossier.
     *
     * @param session
     * @param docModel
     * @throws Exception
     */
    protected void logFondDossierUpdate(CoreSession session, DocumentModel docModel, String comment) {
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
    protected void logFondDossierDelete(CoreSession session, DocumentModel docModel, String fileName) {
        logFondDossierEvent(
            session,
            docModel,
            ReponsesEventConstant.COMMENT_SUPP_FOND_DOSSIER + fileName,
            ReponsesEventConstant.DOSSIER_SUPP_FOND_DOSSIER_EVENT
        );
    }

    protected void logFondDossierEvent(CoreSession session, DocumentModel docModel, String comment, String eventName) {
        final JournalService journalService = STServiceLocator.getJournalService();
        try {
            journalService.journaliserActionFDD(session, docModel, eventName, comment);
        } catch (Exception e) {
            throw new NuxeoException("erreur lors de l'enregistrement du log", e);
        }
    }

    @Override
    public boolean isFondDeDossierFileNameCorrect(DocumentModel fondDeDossierFileDoc) {
        // on verifie que le nom du fichier n'est pas déjà présent comme nom de document dans l'arborescence
        String newDocumentName = null;
        CoreSession session = fondDeDossierFileDoc.getCoreSession();

        Blob blob = FileSchemaUtils.getContent(fondDeDossierFileDoc);
        if (blob != null) {
            newDocumentName = blob.getFilename();
        }
        if (StringUtils.isBlank(newDocumentName)) {
            LOGGER.debug(session, CREATE_FILE_FONC, "newDocumentName is null !");
            return false;
        }
        if (LOGGER.isDebugEnable()) {
            LOGGER.debug(session, CREATE_FILE_FONC, "newDocumentName = " + newDocumentName);
        }
        return !StringUtils.containsAny(newDocumentName, FORBIDDEN_CHARS);
    }

    @Override
    public void initFondDeDossier(CoreSession session, DocumentModel fondDeDossierDoc) {
        if (LOGGER.isDebugEnable()) {
            LOGGER.debug(session, CREATE_FILE_FONC, "init fond de dossier : create repository");
        }
        if (fondDeDossierDoc == null) {
            throw new NuxeoException("Document FondDeDossier null : creation des répertoires fils impossible");
        }
        FondDeDossier fondDeDossier = fondDeDossierDoc.getAdapter(FondDeDossier.class);
        // Crée les 3 répertoires standard du fond de dossier
        DocumentModel repertoire =
            this.createFondDeDossierRepertoire(
                    session,
                    fondDeDossierDoc,
                    ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_PARLEMENT_NAME
                );
        fondDeDossier.setRepertoireParlementId(repertoire.getId());
        repertoire =
            this.createFondDeDossierRepertoire(
                    session,
                    fondDeDossierDoc,
                    ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_SGG_MINISTERE_NAME
                );
        fondDeDossier.setRepertoireSggId(repertoire.getId());
        repertoire =
            this.createFondDeDossierRepertoire(
                    session,
                    fondDeDossierDoc,
                    ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_NAME
                );
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
    public List<FondDeDossierFile> getChildrenFile(CoreSession session, DocumentModel repertoireParent) {
        return (List<FondDeDossierFile>) super.getChildrenFile(session, repertoireParent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FondDeDossierFile> getChildrenFile(CoreSession session, DocumentRef repertoireParentRef) {
        return (List<FondDeDossierFile>) super.getChildrenFile(session, repertoireParentRef);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FondDeDossierFolder> getChildrenFolder(CoreSession session, DocumentModel repertoireParent) {
        return (List<FondDeDossierFolder>) super.getChildrenFolder(session, repertoireParent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FondDeDossierFolder> getChildrenFolder(CoreSession session, DocumentRef repertoireParentRef) {
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
    public boolean havePieceJointeDossier(CoreSession coreSession, Dossier dossier) {
        // On analyse si le dossier a des pièces jointes
        // On récupère la liste des répertoires
        List<DocumentModel> listRepertoiresFondDossier = dossier
            .getFondDeDossier(coreSession)
            .getRepertoireDocument(coreSession);
        for (DocumentModel folder : listRepertoiresFondDossier) {
            List<DocumentModel> files = coreSession.getChildren(
                folder.getRef(),
                ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE
            );
            // Dans chaque répertoire, on récupère les documents qui ne sont pas supprimés
            for (DocumentModel doc : files) {
                if (!STLifeCycleConstant.DELETED_STATE.equals(doc.getCurrentLifeCycleState())) {
                    return true;
                }
            }
        }
        return false;
    }
}
