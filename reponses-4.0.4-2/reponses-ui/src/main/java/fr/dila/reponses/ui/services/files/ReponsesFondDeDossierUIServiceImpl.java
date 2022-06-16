package fr.dila.reponses.ui.services.files;

import static fr.dila.reponses.api.constant.DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA;
import static fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE;
import static fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE;
import static fr.dila.reponses.core.service.ReponsesServiceLocator.getFondDeDossierService;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER_ACTIONS;
import static fr.dila.ss.api.constant.SSTreeConstants.DELETE_ALL_CHOICE;
import static fr.dila.ss.ui.enums.SSActionCategory.FOND_DOSSIER_FOLDER_ACTIONS;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static fr.dila.st.ui.services.actions.STActionsServiceLocator.getSTCorbeilleActionService;

import com.google.common.collect.ImmutableList;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.ui.bean.actions.ReponsesDossierActionDTO;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.ui.bean.FondDTO;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.actions.impl.SSTreeManagerActionServiceImpl;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.FileSchemaUtils;
import fr.dila.st.ui.bean.DossierDTO;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.platform.usermanager.exceptions.GroupAlreadyExistsException;

/**
 * Action Service de gestion de l'arborescence du fond de dossier.
 *
 */
public class ReponsesFondDeDossierUIServiceImpl
    extends SSTreeManagerActionServiceImpl
    implements ReponsesFondDeDossierUIService {
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesFondDeDossierUIServiceImpl.class);

    private static final List<String> ACCEPTED_FILE_TYPES = ImmutableList.of(
        "doc",
        "docx",
        "jpg",
        "odp",
        "ods",
        "odt",
        "pdf",
        "png",
        "ppt",
        "pptx",
        "rtf",
        "vsd",
        "xls",
        "xlsx",
        "zip"
    );

    @Override
    public FondDTO getFondDTO(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel fddDocument = getFondDeDossierDocument(context);
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        List<FondDeDossierFolder> repertoiresRacine = getFondDeDossierService()
            .getVisibleChildrenFolder(session, fddDocument, ssPrincipal);

        setActionsInContext(context);

        FondDTO fondDTO = new FondDTO(Boolean.FALSE);
        List<DossierDTO> fddDTOList = repertoiresRacine
            .stream()
            .map(folder -> toFddDto(folder, context))
            .collect(Collectors.toList());
        fondDTO.setDossiers(fddDTOList);
        fondDTO.setAcceptedFileTypes(ACCEPTED_FILE_TYPES);
        fondDTO.setFondExportAction(context.getAction(SSActionEnum.FOND_DOSSIER_EXPORT));

        return fondDTO;
    }

    private static DocumentModel getFondDeDossierDocument(SpecificContext context) {
        // the Dossier document is the current Document
        DocumentModel doc = context.getCurrentDocument();
        Dossier dossier = doc.getAdapter(Dossier.class);
        FondDeDossier fondDeDossier = dossier.getFondDeDossier(context.getSession());
        return Optional
            .ofNullable(fondDeDossier)
            .map(FondDeDossier::getDocument)
            .orElseThrow(() -> new NuxeoException("Le fond de dossier du document " + doc.getId() + " est nul"));
    }

    private DossierDTO toFddDto(FondDeDossierFolder fddFolder, SpecificContext context) {
        DossierDTO dfd = new DossierDTO();
        dfd.setId(fddFolder.getId());
        dfd.setNom(fddFolder.getName());
        dfd.setLstActions(context.getActions(FOND_DOSSIER_FOLDER_ACTIONS));

        context.putInContextData(ACTION_CATEGORY_KEY, SSActionCategory.FOND_DOSSIER_FILE_ACTIONS);

        // Récupération des enfants du répertoire
        dfd.setLstDocuments(
            getFondDeDossierService()
                .getChildrenFile(context.getSession(), fddFolder.getDocument())
                .stream()
                .map(ssTreeFile -> toDocumentDTO(context, ssTreeFile))
                .collect(Collectors.toList())
        );

        return dfd;
    }

    private void setActionsInContext(SpecificContext context) {
        ReponsesDossierActionDTO dossierAction;
        if (context.containsKeyInContextData(DOSSIER_ACTIONS)) {
            dossierAction = context.getFromContextData(DOSSIER_ACTIONS);
        } else {
            dossierAction = new ReponsesDossierActionDTO();
            context.putInContextData(DOSSIER_ACTIONS, dossierAction);
        }

        if (!dossierAction.getCanUserUpdateFondDossier()) {
            dossierAction.setCanUserUpdateFondDossier(canUserUpdateFondDossier(context));
        }
    }

    /**
     * Retourne vrai si l'utilisateur a le droit de modifier le fond de dossier.
     */
    @Override
    public boolean canUserUpdateFondDossier(SpecificContext context) {
        // L'utilisateur ne peut pas modifier les dossiers à l'état terminé
        DocumentModel dossierDoc = context.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        if (dossier.isDone()) {
            return false;
        }

        CoreSession session = context.getSession();
        STLockActionService lockActions = STActionsServiceLocator.getSTLockActionService();
        // L'utilisateur peut uniquement modifier les dossiers qu'il a verrouillé
        if (!lockActions.getCanUnlockDoc(dossierDoc, session)) {
            return false;
        }

        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        // L'administrateur fonctionnel peut modifier les fonds de dossier
        if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.FOND_DOSSIER_ADMIN_UPDATER)) {
            return true;
        }

        // L'administrateur ministériel peut modifier les fonds de dossier des  dossiers de ses ministères
        if (
            ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.FOND_DOSSIER_ADMIN_MIN_UPDATER) &&
            ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant())
        ) {
            return true;
        }

        // L'utilisateur lambda peut modifier les fond de dossier des dossiers qui lui sont distribués
        return getSTCorbeilleActionService().isDossierLoadedInCorbeille(context);
    }

    @Override
    public void addFile(SpecificContext context) {
        Dossier dossier = context.getCurrentDocument().getAdapter(Dossier.class);
        CoreSession session = context.getSession();

        DocumentModel fondDeDossierDoc = dossier.getFondDeDossier(session).getDocument();

        String niveauVisibilite = getDefaultNiveauVisibilite(context);
        DocumentModel fondDeDossierFileDoc = createBareFile(session, fondDeDossierDoc, niveauVisibilite);

        ImmutablePair<String, Blob> file = getFile(context);
        String fileName = file.getLeft();
        Blob content = file.getRight();

        FileSchemaUtils.setContent(fondDeDossierFileDoc, content);

        if (checkFileCreation(context, fondDeDossierFileDoc)) {
            createSpecificFile(
                session,
                fondDeDossierDoc,
                fondDeDossierFileDoc.getAdapter(FondDeDossierFile.class),
                niveauVisibilite
            );

            context
                .getMessageQueue()
                .addSuccessToQueue(getString(SSTreeManagerActionServiceImpl.SUCCESS_MSG_ADD_FILE, fileName));
        } else {
            context.getMessageQueue().addErrorToQueue(getString(ERROR_MSG_ADD_FILE, fileName));
        }
    }

    @Override
    public void editFile(SpecificContext context) {
        CoreSession session = context.getSession();

        LOGGER.info(session, STLogEnumImpl.UPDATE_FILE_FONC);

        String fondDeDossierFolderId = context.getFromContextData(ID);
        DocumentModel fondDeDossierFolderDoc = getSelectedDocument(session, fondDeDossierFolderId);
        DocumentModel fondDeDossierRacineDoc = session.getDocument(fondDeDossierFolderDoc.getParentRef());
        DocumentModel dossierDoc = session.getDocument(fondDeDossierRacineDoc.getParentRef());

        DocumentModel oldFileDoc = context.getCurrentDocument();

        ImmutablePair<String, Blob> newFile = getFile(context);
        String newFileName = newFile.getLeft();

        String niveauVisibilite = getDefaultNiveauVisibilite(context);

        if (newFileName != null) {
            updateFile(
                context,
                dossierDoc,
                fondDeDossierFolderDoc,
                oldFileDoc,
                newFileName,
                newFile.getRight(),
                niveauVisibilite
            );

            context.getMessageQueue().addSuccessToQueue(getString(SUCCESS_MSG_EDIT_FILE, newFileName));
        } else {
            FondDeDossier fondDeDossier = getFondDeDossier(session, fondDeDossierRacineDoc.getParentRef());

            moveFile(context, dossierDoc, fondDeDossier, oldFileDoc, niveauVisibilite);

            context.getMessageQueue().addSuccessToQueue(getString(SUCCESS_MSG_EDIT_FILE, oldFileDoc.getTitle()));
        }
    }

    /**
     * mise à jour d'un document existant
     */
    private void updateFile(
        SpecificContext context,
        DocumentModel dossierDoc,
        DocumentModel fondDeDossierFolderDoc,
        DocumentModel oldFileDoc,
        String newFileName,
        Blob newFileContent,
        String niveauVisibilite
    ) {
        CoreSession session = context.getSession();

        DocumentModel newFileDoc = getFondDeDossierService()
            .createBareFondDeDossierFichier(session, fondDeDossierFolderDoc, newFileName, newFileContent);

        if (newFileDoc.getAdapter(BlobHolder.class).getBlob().getFilename() == null) {
            context.getMessageQueue().addErrorToQueue(getString(ERROR_MSG_NO_FILE_SELECTED));
        } else if (
            newFileDoc.getProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE
            ) ==
            null &&
            niveauVisibilite == null
        ) {
            context.getMessageQueue().addErrorToQueue(getString(ERROR_MSG_NO_VISIBILITY_SELECTED));
        } else if (verifyNewFileName(newFileName, context, getAllowedFileTypes(context))) {
            // if niveauVisibilite is not defined, we get it
            if (
                newFileDoc.getProperty(
                    FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                    FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE
                ) ==
                null
            ) {
                newFileDoc.setProperty(
                    FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                    FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE,
                    niveauVisibilite
                );
            }

            try {
                getFondDeDossierService().updateFddFile(session, oldFileDoc, newFileDoc, dossierDoc);
            } catch (GroupAlreadyExistsException gaee) {
                LOGGER.error(session, STLogEnumImpl.UPDATE_FILE_FONC, gaee);
                context.getMessageQueue().addErrorToQueue(ERROR_MSG_NO_VISIBILITY_SELECTED);
            }
        }
    }

    /**
     * déplacement du fichier existant dans un nouveau dossier
     */
    private static void moveFile(
        SpecificContext context,
        DocumentModel dossierDoc,
        FondDeDossier fondDeDossier,
        DocumentModel oldFileDoc,
        String niveauVisibilite
    ) {
        CoreSession session = context.getSession();

        if (hasFondDeDossierFileVisibilityChanged(oldFileDoc, niveauVisibilite)) {
            try {
                getFondDeDossierService().moveFddFile(session, fondDeDossier, oldFileDoc, dossierDoc, niveauVisibilite);
            } catch (GroupAlreadyExistsException gaee) {
                LOGGER.error(session, STLogEnumImpl.UPDATE_FILE_FONC, gaee);
                context.getMessageQueue().addErrorToQueue(ERROR_MSG_EDIT_FILE);
            }
        } else {
            // le niveau de visibilité du fichier n'a pas changé donc il ne faut rien faire
            context.getMessageQueue().addWarnToQueue(getString(WARN_MSG_SAME_VISIBILITY));
        }
    }

    @Override
    public void deleteFile(SpecificContext context) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        CoreSession session = context.getSession();

        FondDeDossierService fondDeDossierService = getFondDeDossierService();

        DocumentModel fondDeDossierFileDoc = Optional
            .ofNullable((String) context.getFromContextData(ID))
            .filter(StringUtils::isNotBlank)
            .map(fileIdToDelete -> session.getDocument(new IdRef(fileIdToDelete)))
            .orElseThrow(() -> new STValidationException("fondDossier.delete.empty.doc.id.error"));

        FondDeDossierFile fondDeDossierFile = fondDeDossierFileDoc.getAdapter(FondDeDossierFile.class);

        String fileName = fondDeDossierFile.getFilename();
        fondDeDossierService.logDeleteFileFromFDD(dossierDoc, fileName, session);

        setChoixSuppression(DELETE_ALL_CHOICE);
        if (deleteFile(session, dossierDoc, fondDeDossierFileDoc)) {
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            if (!fondDeDossierService.havePieceJointeDossier(session, dossier)) {
                // si plus de PJ, on passe hasPJ à false
                dossier.setHasPJ(false);
                dossier.save(session);
            }

            context.getMessageQueue().addSuccessToQueue(getString(SUCCESS_MSG_DELETE_FILE, fileName));
        } else {
            context.getMessageQueue().addErrorToQueue(getString(ERROR_MSG_DELETE_FILE, fileName));
        }
    }

    private static String getDefaultNiveauVisibilite(SpecificContext context) {
        String fondDeDossierFolderId = context.getFromContextData(ID);
        return Optional
            .ofNullable(fondDeDossierFolderId)
            .map(
                idRepertoire ->
                    getFondDeDossierService()
                        .getDefaultNiveauVisibiliteFromRepository(idRepertoire, context.getSession())
            )
            .orElseThrow(() -> new STValidationException("fondDossier.visibility.error"));
    }

    @Override
    protected boolean checkFileCreation(SpecificContext context, DocumentModel fileDoc) {
        boolean isFileCanBeCreated = super.checkFileCreation(context, fileDoc);
        if (
            fileDoc.getProperty(FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA, FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE) ==
            null
        ) {
            context.getMessageQueue().addErrorToQueue(getString(ERROR_MSG_NO_VISIBILITY_SELECTED));
            isFileCanBeCreated = false;
        }
        return isFileCanBeCreated;
    }

    private static boolean hasFondDeDossierFileVisibilityChanged(
        DocumentModel currentElement,
        String newNiveauVisibilite
    ) {
        String oldNiveauVisibilite = (String) currentElement.getProperty(
            FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
            FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE
        );

        return Optional
            .ofNullable(oldNiveauVisibilite)
            .map(visibility -> !visibility.equals(newNiveauVisibilite))
            .orElse(false);
    }

    private static FondDeDossier getFondDeDossier(CoreSession session, DocumentRef dossierDocRef) {
        DocumentModel dossierDoc = session.getDocument(dossierDocRef);
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        return dossier.getFondDeDossier(session);
    }

    @Override
    protected List<String> getAllowedFileTypes(SpecificContext context) {
        return ACCEPTED_FILE_TYPES;
    }

    protected void createSpecificFile(
        CoreSession session,
        DocumentModel fondDeDossierDoc,
        SSTreeFile fichier,
        String niveauVisibilite
    ) {
        getFondDeDossierService()
            .createFondDeDossierFile(
                session,
                fondDeDossierDoc,
                fichier.getFilename(),
                niveauVisibilite,
                fichier.getContent()
            );
    }

    protected DocumentModel createBareFile(
        CoreSession session,
        DocumentModel fondDeDossierDoc,
        String niveauVisibilite
    ) {
        DocumentModel bareFile = getFondDeDossierService().createBareFondDeDossierFichier(session);
        bareFile.setProperty(
            FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
            FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE,
            niveauVisibilite
        );

        Dossier dossier = session.getParentDocument(fondDeDossierDoc.getRef()).getAdapter(Dossier.class);
        String idMinistere = dossier.getIdMinistereAttributaireCourant();
        bareFile.setProperty(
            FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
            FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE,
            idMinistere
        );

        return bareFile;
    }

    @Override
    protected DocumentModel createBareFile(SpecificContext context) {
        return null;
    }

    @Override
    protected void createSpecificFile(Blob content, String fileName, SpecificContext context) {}

    @Override
    protected String getAddFileSuccessMessage() {
        return SUCCESS_MSG_ADD_FILE;
    }

    @Override
    protected String getAddFileErrorMessage() {
        return ERROR_MSG_ADD_FILE;
    }
}
