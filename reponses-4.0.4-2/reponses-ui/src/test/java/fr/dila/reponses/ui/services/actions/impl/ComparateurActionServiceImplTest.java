package fr.dila.reponses.ui.services.actions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.CompareVersionDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ComparateurActionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    ComparateurActionServiceImpl service = new ComparateurActionServiceImpl();

    @Mock
    SpecificContext context;

    @Mock
    DocumentModel dossierDoc;

    @Mock
    Dossier dossier;

    @Mock
    DocumentModel currentVersionDoc;

    @Mock
    ReponseService reponsesService;

    @Mock
    DocumentModel version1Doc;

    @Mock
    DocumentModel version2Doc;

    @Mock
    Reponse reponse1;

    @Mock
    Reponse currentReponse;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getReponseService()).thenReturn(reponsesService);
    }

    @Test
    public void testGetVersionText() {
        List<DocumentModel> versionDocs = new ArrayList<>();
        versionDocs.add(version1Doc);
        versionDocs.add(version2Doc);

        when(context.getCurrentDocument()).thenReturn(dossierDoc);
        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(dossier.getReponse(context.getSession())).thenReturn(currentReponse);
        when(currentReponse.getDocument()).thenReturn(currentVersionDoc);
        when(reponsesService.getReponseVersionDocumentList(context.getSession(), currentVersionDoc))
            .thenReturn(versionDocs);
        when(currentVersionDoc.getId()).thenReturn("1");
        when(version1Doc.getId()).thenReturn("2");
        when(version2Doc.getId()).thenReturn("3");
        when(version1Doc.getAdapter(Reponse.class)).thenReturn(reponse1);
        when(currentVersionDoc.getAdapter(Reponse.class)).thenReturn(currentReponse);
        when(currentReponse.getTexteReponse()).thenReturn("texte réponse en cours");
        when(reponse1.getTexteReponse()).thenReturn("texte réponse 1");

        CompareVersionDTO dto = service.getVersionTexts(context, "1", "2");

        assertNotNull(dto);
        assertEquals("texte réponse en cours", dto.getTextFirst());
        assertEquals("texte réponse 1", dto.getTextLast());
    }
}
