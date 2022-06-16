package fr.dila.reponses.ui.services.files;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.DossierConstants.FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA;
import static fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE;
import static fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE;
import static fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER_ACTIONS;
import static fr.dila.st.api.constant.STSchemaConstant.FILE_CONTENT_PROPERTY;
import static fr.dila.st.api.constant.STSchemaConstant.FILE_SCHEMA;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_CONTENT;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_DETAILS;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.InputStream;
import java.text.MessageFormat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.runtime.api.Framework;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

import com.sun.jersey.core.header.FormDataContentDisposition;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.actions.ReponsesDossierActionDTO;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSTreeService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.FondDTO;
import fr.dila.st.core.util.BlobUtils;
import fr.dila.st.ui.bean.DossierDTO;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        ReponsesServiceLocator.class,
        SSServiceLocator.class,
        STActionsServiceLocator.class,
        Framework.class,
        BlobUtils.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesFondDeDossierUIServiceImplTest {
    private static final String FOND_DOSSIER_FOLDER_NAME = "fond-dossier-folder-name";
    private static final String FOND_DOSSIER_FOLDER_ID = "fond-dossier-folder-id";
    private static final String FOND_DOSSIER_FILE_ID = "fond-dossier-file-id";
    private static final String FILE_NAME = "filename.pdf";
    private static final String ID_MINISTERE = "id-ministere-attr";
    private static final String VISIBILITY_LEVEL = FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL;
    private static final DocumentRef FOND_DE_DOSSIER_DOC_REF = new IdRef("fond-de-dossier-doc-ref");

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesFondDeDossierUIService fondDeDossierUIService;

    @Mock
    private SpecificContext context;

    @Mock
    private DocumentModel dossierDoc;

    @Mock
    private Dossier dossier;

    @Mock
    private CoreSession session;

    @Mock
    private SSPrincipal ssPrincipal;

    @Mock
    private FondDeDossier fondDeDossier;

    @Mock
    private DocumentModel fondDeDossierDoc;

    @Mock
    private FondDeDossierFolder fondDeDossierFolder;

    @Mock
    private DocumentModel fondDeDossierFileDoc;

    @Mock
    private FondDeDossierFile fondDeDossierFile;

    @Mock
    private InputStream fileContent;

    @Mock
    private BlobHolder blobHolder;

    @Mock
    private Blob blobContent;

    @Mock
    private FormDataContentDisposition fileDetails;

    @Mock
    private FondDeDossierService fondDeDossierService;

    @Mock
    private SSTreeService treeService;

    @Mock
    private STLockActionService lockActions;

    @Mock
    private SolonAlertManager alertManager;

    @Before
    public void setUp() {
        fondDeDossierUIService = new ReponsesFondDeDossierUIServiceImpl();

        mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getFondDeDossierService()).thenReturn(fondDeDossierService);

        mockStatic(SSServiceLocator.class);
        when(SSServiceLocator.getSSTreeService()).thenReturn(treeService);

        mockStatic(STActionsServiceLocator.class);
        when(STActionsServiceLocator.getSTLockActionService()).thenReturn(lockActions);

        mockStatic(BlobUtils.class);

        when(context.getCurrentDocument()).thenReturn(dossierDoc);
        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(context.getSession()).thenReturn(session);
        when(dossier.getFondDeDossier(session)).thenReturn(fondDeDossier);
        when(context.getFromContextData(FILE_DETAILS)).thenReturn(fileDetails);
        when(context.getFromContextData(ID)).thenReturn(FOND_DOSSIER_FOLDER_ID);
        when(context.getFromContextData(FILE_CONTENT)).thenReturn(fileContent);
        when(fileDetails.getFileName()).thenReturn(FILE_NAME);
        when(fondDeDossier.getDocument()).thenReturn(fondDeDossierDoc);
        when(fondDeDossierService.getDefaultNiveauVisibiliteFromRepository(FOND_DOSSIER_FOLDER_ID, session))
            .thenReturn(VISIBILITY_LEVEL);
        when(fondDeDossierService.createBareFondDeDossierFichier(session)).thenReturn(fondDeDossierFileDoc);
        when(fondDeDossierDoc.getRef()).thenReturn(FOND_DE_DOSSIER_DOC_REF);
        when(session.getParentDocument(FOND_DE_DOSSIER_DOC_REF)).thenReturn(dossierDoc);
        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(dossier.getIdMinistereAttributaireCourant()).thenReturn(ID_MINISTERE);
        when(fondDeDossierFileDoc.getAdapter(BlobHolder.class)).thenReturn(blobHolder);
        when(blobHolder.getBlob()).thenReturn(blobContent);
        when(fondDeDossierFile.getFilename()).thenReturn(FILE_NAME);
        when(context.getMessageQueue()).thenReturn(alertManager);
    }

    @Test
    public void testSaveReponseIfDossierLink() {
        when(session.getPrincipal()).thenReturn(ssPrincipal);
        when(context.containsKeyInContextData(DOSSIER_ACTIONS)).thenReturn(false);

        when(fondDeDossierDoc.getAdapter(FondDeDossier.class)).thenReturn(fondDeDossier);
        when(fondDeDossierFolder.getId()).thenReturn(FOND_DOSSIER_FOLDER_ID);
        when(fondDeDossierFolder.getName()).thenReturn(FOND_DOSSIER_FOLDER_NAME);
        when(fondDeDossierService.getVisibleChildrenFolder(session, fondDeDossierDoc, ssPrincipal))
            .thenReturn(newArrayList(fondDeDossierFolder));

        FondDTO fondDTO = fondDeDossierUIService.getFondDTO(context);

        assertThat(fondDTO.getDossiers())
            .extracting(DossierDTO::getId, DossierDTO::getNom)
            .containsExactly(tuple(FOND_DOSSIER_FOLDER_ID, FOND_DOSSIER_FOLDER_NAME));
        verify(context).putInContextData(eq(DOSSIER_ACTIONS), any(ReponsesDossierActionDTO.class));
    }

    @Test
    public void testAddDocumentFondDossier() {
        String successMessage = MessageFormat.format("Le fichier {0} a bien été ajouté au fond de dossier", FILE_NAME);

        when(BlobUtils.createSerializableBlob(fileContent, FILE_NAME, null)).thenReturn(blobContent);
        when(blobContent.getLength()).thenReturn(1L);
        when(blobContent.getFilename()).thenReturn(FILE_NAME);
        when(fondDeDossierService.isFondDeDossierFileNameCorrect(fondDeDossierFileDoc)).thenReturn(true);
        when(
            fondDeDossierFileDoc.getProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE
            )
        )
            .thenReturn(VISIBILITY_LEVEL);
        when(fondDeDossierFileDoc.getProperty(FILE_SCHEMA, FILE_CONTENT_PROPERTY)).thenReturn(blobContent);
        when(fondDeDossierFileDoc.getAdapter(FondDeDossierFile.class)).thenReturn(fondDeDossierFile);
        when(fondDeDossierFile.getContent()).thenReturn(blobContent);

        fondDeDossierUIService.addFile(context);

        verify(fondDeDossierFileDoc)
            .setProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE,
                VISIBILITY_LEVEL
            );
        verify(fondDeDossierFileDoc)
            .setProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE,
                ID_MINISTERE
            );
        verify(fondDeDossierFileDoc).setProperty(FILE_SCHEMA, FILE_CONTENT_PROPERTY, blobContent);
        verify(fondDeDossierService)
            .createFondDeDossierFile(session, fondDeDossierDoc, FILE_NAME, VISIBILITY_LEVEL, blobContent);
        verify(alertManager).addSuccessToQueue(successMessage);
    }

    @Test
    public void testAddDocumentFondDossierWithWrongFilename() {
        String badFileName = "filename'.pdf";

        when(fileDetails.getFileName()).thenReturn(badFileName);
        when(blobContent.getLength()).thenReturn(1L);
        when(BlobUtils.createSerializableBlob(fileContent, badFileName, null)).thenReturn(blobContent);
        when(blobContent.getFilename()).thenReturn(badFileName);

        fondDeDossierUIService.addFile(context);

        verify(fondDeDossierFileDoc)
            .setProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE,
                VISIBILITY_LEVEL
            );
        verify(fondDeDossierFileDoc)
            .setProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE,
                ID_MINISTERE
            );
        verify(fondDeDossierFileDoc).setProperty(FILE_SCHEMA, FILE_CONTENT_PROPERTY, blobContent);
        verify(fondDeDossierService, never())
            .createFondDeDossierFile(session, fondDeDossierDoc, badFileName, VISIBILITY_LEVEL, blobContent);
        verify(alertManager, Mockito.times(2)).addErrorToQueue(Matchers.anyString());
    }

    @Test
    public void testAddDocumentFondDossierWithWrongExtension() {
        String badFileName = "filename.unknown";

        when(fileDetails.getFileName()).thenReturn(badFileName);
        when(blobContent.getLength()).thenReturn(1L);
        when(BlobUtils.createSerializableBlob(fileContent, badFileName, null)).thenReturn(blobContent);
        when(blobContent.getFilename()).thenReturn(badFileName);

        fondDeDossierUIService.addFile(context);

        verify(fondDeDossierFileDoc)
            .setProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE,
                VISIBILITY_LEVEL
            );
        verify(fondDeDossierFileDoc)
            .setProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE,
                ID_MINISTERE
            );
        verify(fondDeDossierFileDoc).setProperty(FILE_SCHEMA, FILE_CONTENT_PROPERTY, blobContent);
        verify(fondDeDossierService, never())
            .createFondDeDossierFile(session, fondDeDossierDoc, badFileName, VISIBILITY_LEVEL, blobContent);
        verify(alertManager, Mockito.times(2)).addErrorToQueue(Matchers.anyString());
    }

    @Test
    public void testAddEmptyFile() {
        String badFileName = "filename.pdf";

        when(blobContent.getFilename()).thenReturn(FILE_NAME);
        when(blobContent.getLength()).thenReturn(0L);
        when(BlobUtils.createSerializableBlob(fileContent, FILE_NAME, null)).thenReturn(blobContent);
        when(blobContent.getFilename()).thenReturn(FILE_NAME);

        fondDeDossierUIService.addFile(context);

        verify(fondDeDossierFileDoc)
            .setProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_NIVEAU_VISIBILITE,
                VISIBILITY_LEVEL
            );
        verify(fondDeDossierFileDoc)
            .setProperty(
                FOND_DE_DOSSIER_ELEMENT_DOCUMENT_SCHEMA,
                FOND_DE_DOSSIER_FICHIER_MINISTERE_AJOUTE,
                ID_MINISTERE
            );
        verify(fondDeDossierFileDoc).setProperty(FILE_SCHEMA, FILE_CONTENT_PROPERTY, blobContent);
        verify(fondDeDossierService, never())
            .createFondDeDossierFile(session, fondDeDossierDoc, badFileName, VISIBILITY_LEVEL, blobContent);
        verify(alertManager, Mockito.times(2)).addErrorToQueue(Matchers.anyString());
    }

    @Test
    public void testDeleteDocumentFondDossier() {
        String successMessage = MessageFormat.format(
            "Le fichier {0} a bien été supprimé du fond de dossier",
            FILE_NAME
        );

        when(context.getCurrentDocument()).thenReturn(dossierDoc);
        when(context.getFromContextData(ID)).thenReturn(FOND_DOSSIER_FILE_ID);
        when(session.getDocument(new IdRef(FOND_DOSSIER_FILE_ID))).thenReturn(fondDeDossierFileDoc);
        when(fondDeDossierFileDoc.getAdapter(FondDeDossierFile.class)).thenReturn(fondDeDossierFile);
        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(fondDeDossierService.havePieceJointeDossier(session, dossier)).thenReturn(false);

        fondDeDossierUIService.deleteFile(context);

        verify(fondDeDossierService).logDeleteFileFromFDD(dossierDoc, FILE_NAME, session);
        verify(treeService).deleteFile(session, fondDeDossierFileDoc);
        verify(dossier).setHasPJ(false);
        verify(dossier).save(session);
        verify(alertManager).addSuccessToQueue(successMessage);
    }
}
