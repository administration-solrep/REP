package fr.dila.reponses.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.SSJournalService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SSServiceLocator.class, ReponsesServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class DossierDistributionServiceImplTest {
    private static final String DOCUMENT_ROUTE_ID = "db2ac2be-64a3-4809-bdf3-3bb422b3551a";
    private static final String GET_LAST_DOCUMENT_ROUTE_QUERY =
        "SELECT * FROM Dossier WHERE " +
        STSchemaConstant.DOSSIER_SCHEMA_PREFIX +
        ":" +
        STSchemaConstant.DOSSIER_LAST_DOCUMENT_ROUTE_PROPERTY +
        " = '" +
        DOCUMENT_ROUTE_ID +
        "'";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private DossierDistributionServiceImpl service;

    @Mock
    private SSJournalService journalService;

    @Mock
    private ReponsesArbitrageService arbitrageService;

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel dossierDoc;

    @Mock
    private DocumentModel dossierLinkDoc;

    @Mock
    private STDossierLink dossierLink;

    @Mock
    private DocumentModel etapeDoc;

    @Mock
    private SSRouteStep etape;

    @Mock
    private ActionableCaseLink acl;

    @Before
    public void setUp() {
        service = new DossierDistributionServiceImpl();
    }

    @Test
    public void validerEtapeRefusWithoutArbitrage() {
        PowerMockito.mockStatic(SSServiceLocator.class);
        when(SSServiceLocator.getSSJournalService()).thenReturn(journalService);

        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getReponsesArbitrageService()).thenReturn(arbitrageService);

        when(dossierLinkDoc.getAdapter(STDossierLink.class)).thenReturn(dossierLink);
        when(arbitrageService.isStepPourArbitrage(dossierLink)).thenReturn(false);
        when(dossierLink.getRoutingTaskId()).thenReturn(DOCUMENT_ROUTE_ID);
        when(session.getDocument(new IdRef(dossierLink.getRoutingTaskId()))).thenReturn(etapeDoc);
        when(etapeDoc.getAdapter(SSRouteStep.class)).thenReturn(etape);
        when(etapeDoc.getType()).thenReturn(SSConstant.ROUTE_STEP_DOCUMENT_TYPE);
        when(etape.getDocument()).thenReturn(etapeDoc);
        when(dossierLinkDoc.getAdapter(ActionableCaseLink.class)).thenReturn(acl);

        service.validerEtapeRefus(session, dossierDoc, dossierLinkDoc);

        verify(etape).setAutomaticValidated(false);
        verify(etape).setValidationStatus(SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_DEFAVORABLE_VALUE);
        verify(session).saveDocument(etape.getDocument());
        verify(acl).refuse(session);
        verify(journalService)
            .journaliserActionEtapeFDR(
                session,
                etape,
                dossierDoc,
                STEventConstant.DOSSIER_AVIS_DEFAVORABLE,
                STEventConstant.COMMENT_AVIS_DEFAVORABLE
            );
    }

    @Test
    public void testGetDossierFromDocumentRouteId() {
        DocumentModelList dossierDocs = new DocumentModelListImpl();
        dossierDocs.add(dossierDoc);

        when(session.query(GET_LAST_DOCUMENT_ROUTE_QUERY)).thenReturn(dossierDocs);

        DocumentModel resultDoc = service.getDossierFromDocumentRouteId(session, DOCUMENT_ROUTE_ID);

        assertThat(resultDoc).isEqualTo(dossierDoc);
    }

    @Test
    public void testGetDossierFromDocumentRouteIdWithEmptyDossierList() {
        when(session.query(GET_LAST_DOCUMENT_ROUTE_QUERY)).thenReturn(new DocumentModelListImpl());

        Throwable throwable = catchThrowable(() -> service.getDossierFromDocumentRouteId(session, DOCUMENT_ROUTE_ID));
        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessage("Aucun dossier ne correspond à l'id de la feuille de route : %s", DOCUMENT_ROUTE_ID);
    }
}
