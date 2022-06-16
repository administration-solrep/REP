package fr.dila.reponses.ui.services.impl;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.reponses.ui.services.ParapheurUIService;
import fr.dila.reponses.ui.services.actions.ParapheurActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Features;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesActionsServiceLocator.class, ReponsesServiceLocator.class })
@PowerMockIgnore("javax.management.*")
@Features(SolonMockitoFeature.class)
public class ParapheurUIServiceImplTest {
    private static final String REPONSE_VERSION_ID = "reponseVersionId";

    private static final String REPONSE_ID = "reponseId";

    private static final String VERSION_EN_COURS_LABEL = "Version en cours";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ParapheurUIService service = new ParapheurUIServiceImpl();

    @Mock
    private SpecificContext context;

    @Mock
    private DocumentModel dossierDoc;

    @Mock
    private Dossier dossier;

    @Mock
    private CoreSession session;

    @Mock
    private Question question;

    @Mock
    private DocumentModel questionDoc;

    @Mock
    private Reponse reponse;

    @Mock
    private DocumentModel reponseDoc;

    @Mock
    private DocumentModel reponseVersionDoc;

    @Mock
    private Reponse reponseVersion;

    @Mock
    private ParapheurActionService parapheurActionService;

    @Mock
    private ReponseService reponseService;

    @Before
    public void setUp() {
        when(reponseVersionDoc.getCoreSession()).thenReturn(session);
        when(context.getSession()).thenReturn(session);
        when(context.getCurrentDocument()).thenReturn(dossierDoc);
        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(dossier.getQuestion(session)).thenReturn(question);
        when(question.getDocument()).thenReturn(questionDoc);
        when(questionDoc.getType()).thenReturn(DossierConstants.QUESTION_DOCUMENT_TYPE);
        when(dossier.getReponse(session)).thenReturn(reponse);
        when(reponse.getDocument()).thenReturn(reponseDoc);
        when(reponseDoc.getType()).thenReturn(DossierConstants.REPONSE_DOCUMENT_TYPE);
        when(reponseDoc.getId()).thenReturn(REPONSE_ID);
        when(reponseVersionDoc.getId()).thenReturn(REPONSE_VERSION_ID);
        when(reponseVersionDoc.isVersion()).thenReturn(TRUE);
        when(reponseVersionDoc.getAdapter(Reponse.class)).thenReturn(reponseVersion);

        mockStatic(ReponsesActionsServiceLocator.class);
        when(ReponsesActionsServiceLocator.getParapheurActionService()).thenReturn(parapheurActionService);
        when(parapheurActionService.canSaveDossier(context, dossierDoc)).thenReturn(TRUE);

        mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getReponseService()).thenReturn(reponseService);
        when(reponseService.getReponseVersionDocumentList(session, reponseDoc))
            .thenReturn(singletonList(reponseVersionDoc));
        when(reponseService.getReponseMajorVersionNumber(session, reponseDoc)).thenReturn(1);
        when(reponseService.getReponseMajorVersionNumber(session, reponseVersionDoc)).thenReturn(1);
        when(reponseVersion.getAuteur()).thenReturn("toto");
    }

    @Test
    public void testGetParapheur() {
        ParapheurDTO parapheur = service.getParapheur(context);
        Assert.assertNotNull(parapheur);
        assertTrue(parapheur.getIsEdit());
        assertThat(parapheur.getVersion())
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(REPONSE_ID, VERSION_EN_COURS_LABEL);
    }

    @Test
    public void testGetVersionDTOs() {
        List<SelectValueDTO> versionDTOs = service.getVersionDTOs(context);

        assertThat(versionDTOs)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(tuple(REPONSE_VERSION_ID, "1 - toto"), tuple(REPONSE_ID, VERSION_EN_COURS_LABEL));
    }
}
