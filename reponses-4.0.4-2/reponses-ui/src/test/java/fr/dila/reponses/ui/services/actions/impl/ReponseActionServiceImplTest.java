package fr.dila.reponses.ui.services.actions.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponseActionService;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ReponseActionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponseActionService service = new ReponseActionServiceImpl();

    @Mock
    private ReponseService reponseService;

    @Mock
    private CoreSession session;

    @Mock
    private SpecificContext context;

    @Mock
    private DocumentModel dossierDoc;

    @Mock
    private Reponse reponse;

    @Mock
    private DocumentModel reponseDoc;

    @Mock
    private DocumentModel reponseOldVersionDocument;

    @Mock
    private Reponse reponseOldVersion;

    @Mock
    private SolonAlertManager alertManager;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getReponseService()).thenReturn(reponseService);
        // nouvelle version
        when(reponseService.getReponseMajorVersionNumber(session, reponseDoc)).thenReturn(2);
        when(reponseService.getReponseOldVersionDocument(session, reponseDoc, 2)).thenReturn(reponseOldVersionDocument);

        when(reponseOldVersionDocument.getAdapter(Reponse.class)).thenReturn(reponseOldVersion);
        // pas de changement de texte
        when(reponse.getTexteReponse()).thenReturn("oldTexte");
        when(reponseOldVersion.getTexteReponse()).thenReturn("oldTexte");

        when(reponseDoc.getAdapter(Reponse.class)).thenReturn(reponse);
    }

    private void initSaveReponse() {
        when(context.getMessageQueue()).thenReturn(alertManager);
        when(context.getSession()).thenReturn(session);
    }

    @Test
    public void testSaveReponse() {
        initSaveReponse();
        service.saveReponse(context, reponseDoc, dossierDoc);
        verify(reponseService).saveReponseAndErratum(session, reponseDoc, dossierDoc);
    }

    @Test
    public void testSaveReponse_hasSignature() {
        initSaveReponse();
        when(reponseService.isReponseSignee(session, dossierDoc)).thenReturn(true);

        service.saveReponse(context, reponseDoc, dossierDoc);
        verifyNoBriserSignature();
    }

    @Test
    public void testSaveReponse_hasNoSignature() {
        initSaveReponse();
        when(reponseService.isReponseSignee(session, dossierDoc)).thenReturn(false);

        service.saveReponse(context, reponseDoc, dossierDoc);
        verifyNoBriserSignature();
    }

    /** Vérifie qu'on ne brise pas la signature dans la sauvegarde de la réponse. */
    private void verifyNoBriserSignature() {
        verify(reponseService, Mockito.never()).briserSignatureReponse(session, reponse, dossierDoc);
    }

    @Test
    public void reponseHasNotChangedIfSameText() {
        Assert.assertFalse(service.reponseHasChanged(session, reponseDoc));
    }

    @Test
    public void reponseHasChangedIfNewText() {
        when(reponse.getTexteReponse()).thenReturn("newTexte");
        Assert.assertTrue(service.reponseHasChanged(session, reponseDoc));
    }

    @Test
    public void reponseHasChangedIfNoVersion() {
        when(reponseService.getReponseMajorVersionNumber(session, reponseDoc)).thenReturn(0);
        Assert.assertTrue(service.reponseHasChanged(session, reponseDoc));
    }

    @Test
    public void reponseHasNotChangedIfNoTextAndNoVersion() {
        when(reponseService.getReponseMajorVersionNumber(session, reponseDoc)).thenReturn(0);
        when(reponse.getTexteReponse()).thenReturn(null);
        Assert.assertFalse(service.reponseHasChanged(session, reponseDoc));
    }
}
