package fr.dila.reponses.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.exception.AllotissementException;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        AllotissementServiceImpl.class,
        IdRef.class,
        SSServiceLocator.class,
        ReponsesServiceLocator.class,
        STServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class AllotissementServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private AllotissementService allotissementService;

    @Mock
    private DocumentRoutingService documentRoutingService;

    @Mock
    private ReponseService reponseService;

    @Mock
    private JournalService journalService;

    @Mock
    private Question questionDirectrice;

    @Mock
    private Question questionAllotie;

    @Mock
    private List<Question> questions;

    @Mock
    private CloseableCoreSession session;

    @Mock
    private NuxeoPrincipal user;

    @Mock
    private DocumentRef questionDirectriceRef;

    @Mock
    private DocumentRef questionAllotieRef;

    @Mock
    private IdRef routeDirectriceDocRef;

    @Mock
    private DocumentModel dossierDirecteurDoc;

    @Mock
    private DocumentModel dossierAllotieDoc;

    @Mock
    private DocumentModel routeDirectriceDoc;

    @Mock
    private Dossier dossierDirecteur;

    @Mock
    private Dossier dossierAllotie;

    @Mock
    private SSFeuilleRoute routeDirectrice;

    @Mock
    private DossierLink dossierLinkDirecteur;

    @Mock
    private Allotissement allotissement;

    @Mock
    private Reponse reponseDirectrice;

    @Before
    public void setUp() throws Exception {
        allotissementService = PowerMockito.spy(new AllotissementServiceImpl());

        PowerMockito.mockStatic(SSServiceLocator.class);
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        when(SSServiceLocator.getDocumentRoutingService()).thenReturn(documentRoutingService);
        when(ReponsesServiceLocator.getReponseService()).thenReturn(reponseService);
        when(STServiceLocator.getJournalService()).thenReturn(journalService);

        String idDosDirecteur = "1";
        String lastDocRouteDosDir = "2";
        questions = new ArrayList<>();
        questions.add(questionAllotie);

        when(questionDirectrice.getDossierRef()).thenReturn(questionDirectriceRef);
        when(session.getDocument(questionDirectriceRef)).thenReturn(dossierDirecteurDoc);

        when(dossierDirecteurDoc.getAdapter(Dossier.class)).thenReturn(dossierDirecteur);
        when(dossierDirecteurDoc.getId()).thenReturn(idDosDirecteur);

        when(dossierDirecteur.hasFeuilleRoute()).thenReturn(true);
        when(dossierDirecteur.getLastDocumentRoute()).thenReturn(lastDocRouteDosDir);
        when(dossierDirecteur.getReponse(session)).thenReturn(reponseDirectrice);
        PowerMockito.whenNew(IdRef.class).withArguments(lastDocRouteDosDir).thenReturn(routeDirectriceDocRef);
        when(session.getDocument(routeDirectriceDocRef)).thenReturn(routeDirectriceDoc);
        when(routeDirectriceDoc.getAdapter(SSFeuilleRoute.class)).thenReturn(routeDirectrice);

        when(questionAllotie.getDossierRef()).thenReturn(questionAllotieRef);
        when(session.getDocument(questionAllotieRef)).thenReturn(dossierAllotieDoc);
        when(dossierAllotieDoc.getAdapter(Dossier.class)).thenReturn(dossierAllotie);
    }

    @Test
    public void createLot() throws Exception {
        when(dossierDirecteur.getIdMinistereAttributaireCourant()).thenReturn("id");
        when(dossierAllotie.getIdMinistereAttributaireCourant()).thenReturn("id");

        PowerMockito
            .doReturn(dossierLinkDirecteur)
            .when(allotissementService, "getLastStepDirecteurInfo", any(), any());
        PowerMockito.doReturn(allotissement).when(allotissementService, "createAllotissementDoc", any(), any());
        PowerMockito
            .doNothing()
            .when(
                allotissementService,
                "addQuestionToRouteDirectrice",
                any(CoreSession.class),
                any(List.class),
                any(List.class),
                any(DossierLink.class),
                any(Question.class),
                any(String.class),
                any(String.class),
                any(boolean.class)
            );
        PowerMockito.doReturn(new StringBuilder()).when(allotissementService, "prepareIdsDossierForLog", any(), any());
        PowerMockito.doNothing().when(allotissementService, "checkAllotissementError", any(), any(), any());

        Boolean lotCreated = Whitebox.invokeMethod(
            allotissementService,
            "createLotUnRestricted",
            questionDirectrice,
            questions,
            session,
            user
        );

        assertEquals(true, lotCreated);
        verify(documentRoutingService, times(1)).lockDocumentRoute(eq(routeDirectrice), eq(session));
        verify(journalService, times(1)).journaliserActionForUser(any(), any(), any(), any(), any(), any());
        verify(reponseService, times(1)).saveReponse(any(), any(), any());
        verify(documentRoutingService, times(1)).unlockDocumentRoute(eq(routeDirectrice), eq(session));
        verify(session, times(2)).save();
        verify(session, times(2)).saveDocument(any());
        verify(session, times(1)).saveDocument(eq(routeDirectriceDoc));

        //On vérifie que les méthodes privées ont bien été appelées
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke("getLastStepDirecteurInfo", any(), any());
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke("createAllotissementDoc", any(), any());
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke(
                "addQuestionToRouteDirectrice",
                any(CoreSession.class),
                any(List.class),
                any(List.class),
                any(DossierLink.class),
                any(Question.class),
                any(String.class),
                any(String.class),
                any(boolean.class)
            );
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke("prepareIdsDossierForLog", any(), any());
    }

    @Test(expected = AllotissementException.class)
    public void createLotDossierSansFDR() throws Exception {
        String idDosDirecteur = "1";
        questions = new ArrayList<>();
        questions.add(questionAllotie);

        when(questionDirectrice.getDossierRef()).thenReturn(questionDirectriceRef);
        when(session.getDocument(questionDirectriceRef)).thenReturn(dossierDirecteurDoc);
        when(dossierDirecteurDoc.getAdapter(Dossier.class)).thenReturn(dossierDirecteur);
        when(dossierDirecteurDoc.getId()).thenReturn(idDosDirecteur);
        // Pas de feuille de route
        when(dossierDirecteur.hasFeuilleRoute()).thenReturn(false);

        Boolean lotCreated = Whitebox.invokeMethod(
            allotissementService,
            "createLotUnRestricted",
            questionDirectrice,
            questions,
            session,
            user
        );

        assertEquals(false, lotCreated);
        verify(documentRoutingService, times(0)).lockDocumentRoute(eq(routeDirectrice), eq(session));
        verify(journalService, times(0)).journaliserActionForUser(any(), any(), any(), any(), any(), any());
        verify(reponseService, times(0)).saveReponse(any(), any(), any());
        verify(documentRoutingService, times(0)).unlockDocumentRoute(eq(routeDirectrice), eq(session));
        verify(session, times(0)).save();
        verify(session, times(0)).saveDocument(any());
        verify(session, times(0)).saveDocument(eq(routeDirectriceDoc));

        //On vérifie que les méthodes privées ont bien été appelées
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke("getLastStepDirecteurInfo", any(), any());
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke("createAllotissementDoc", any(), any());
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke(
                "addQuestionToRouteDirectrice",
                any(CoreSession.class),
                any(List.class),
                any(List.class),
                any(DossierLink.class),
                any(Question.class),
                any(String.class),
                any(String.class),
                any(boolean.class)
            );
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke("prepareIdsDossierForLog", any(), any());
    }

    @Test(expected = AllotissementException.class)
    public void createLotMinistereDifferent() throws Exception {
        when(dossierDirecteur.getIdMinistereAttributaireCourant()).thenReturn("id1");
        when(dossierAllotie.getIdMinistereAttributaireCourant()).thenReturn("id2");

        PowerMockito
            .doReturn(dossierLinkDirecteur)
            .when(allotissementService, "getLastStepDirecteurInfo", any(), any());
        PowerMockito.doReturn(allotissement).when(allotissementService, "createAllotissementDoc", any(), any());
        PowerMockito
            .doNothing()
            .when(
                allotissementService,
                "addQuestionToRouteDirectrice",
                any(CoreSession.class),
                any(List.class),
                any(List.class),
                any(DossierLink.class),
                any(Question.class),
                any(String.class),
                any(String.class),
                any(boolean.class)
            );
        PowerMockito.doReturn(new StringBuilder()).when(allotissementService, "prepareIdsDossierForLog", any(), any());

        Boolean lotCreated = Whitebox.invokeMethod(
            allotissementService,
            "createLotUnRestricted",
            questionDirectrice,
            questions,
            session,
            user
        );

        assertEquals(true, lotCreated);
        verify(documentRoutingService, times(1)).lockDocumentRoute(eq(routeDirectrice), eq(session));
        verify(journalService, times(0)).journaliserActionForUser(any(), any(), any(), any(), any(), any());
        verify(reponseService, times(0)).saveReponse(any(), any(), any());
        verify(documentRoutingService, times(0)).unlockDocumentRoute(eq(routeDirectrice), eq(session));
        verify(session, times(0)).save();
        verify(session, times(0)).saveDocument(any());
        verify(session, times(0)).saveDocument(eq(routeDirectriceDoc));

        //On vérifie que les méthodes privées ont bien été appelées
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke("getLastStepDirecteurInfo", any(), any());
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke("createAllotissementDoc", any(), any());
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke(
                "addQuestionToRouteDirectrice",
                any(CoreSession.class),
                any(List.class),
                any(List.class),
                any(DossierLink.class),
                any(Question.class),
                any(String.class),
                any(String.class),
                any(boolean.class)
            );
        PowerMockito
            .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
            .invoke("prepareIdsDossierForLog", any(), any());
    }

    @Test(expected = AllotissementException.class)
    public void createLotWithExceptionCannotLock() throws Exception {
        when(dossierDirecteur.getIdMinistereAttributaireCourant()).thenReturn("id");
        when(dossierAllotie.getIdMinistereAttributaireCourant()).thenReturn("id");

        PowerMockito
            .doReturn(dossierLinkDirecteur)
            .when(allotissementService, "getLastStepDirecteurInfo", any(), any());
        PowerMockito.doReturn(allotissement).when(allotissementService, "createAllotissementDoc", any(), any());
        PowerMockito
            .doNothing()
            .when(
                allotissementService,
                "addQuestionToRouteDirectrice",
                any(CoreSession.class),
                any(List.class),
                any(List.class),
                any(DossierLink.class),
                any(Question.class),
                any(String.class),
                any(String.class),
                any(boolean.class)
            );
        PowerMockito.doReturn(new StringBuilder()).when(allotissementService, "prepareIdsDossierForLog", any(), any());

        //On ne peut pas vérouiller le dossier
        PowerMockito
            .doThrow(new FeuilleRouteAlreadyLockedException())
            .when(documentRoutingService)
            .lockDocumentRoute(any(), any());

        Boolean lotCreated = false;
        try {
            lotCreated =
                Whitebox.invokeMethod(
                    allotissementService,
                    "createLotUnRestricted",
                    questionDirectrice,
                    questions,
                    session,
                    user
                );
        } finally {
            assertFalse(lotCreated);
            verify(documentRoutingService, times(1)).lockDocumentRoute(eq(routeDirectrice), eq(session));
            verify(journalService, times(0)).journaliserActionForUser(any(), any(), any(), any(), any(), any());
            verify(reponseService, times(0)).saveReponse(any(), any(), any());
            verify(documentRoutingService, times(0)).unlockDocumentRoute(any(), any());
            verify(session, times(0)).save();
            verify(session, times(0)).saveDocument(any());

            //On vérifie que les méthodes privées ont bien été appelées ou pas
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke("getLastStepDirecteurInfo", any(), any());
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke("createAllotissementDoc", any(), any());
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke(
                    "addQuestionToRouteDirectrice",
                    any(CoreSession.class),
                    any(List.class),
                    any(List.class),
                    any(DossierLink.class),
                    any(Question.class),
                    any(String.class),
                    any(String.class),
                    any(boolean.class)
                );
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke("prepareIdsDossierForLog", any(), any());
        }
    }

    @Test(expected = AllotissementException.class)
    public void createLotWithExceptionIdNotCorrect() throws Exception {
        //Les IDs sont différents
        when(dossierDirecteur.getIdMinistereAttributaireCourant()).thenReturn("id");
        when(dossierAllotie.getIdMinistereAttributaireCourant()).thenReturn("id2");

        PowerMockito
            .doReturn(dossierLinkDirecteur)
            .when(allotissementService, "getLastStepDirecteurInfo", any(), any());
        PowerMockito.doReturn(allotissement).when(allotissementService, "createAllotissementDoc", any(), any());
        PowerMockito
            .doNothing()
            .when(
                allotissementService,
                "addQuestionToRouteDirectrice",
                any(CoreSession.class),
                any(List.class),
                any(List.class),
                any(DossierLink.class),
                any(Question.class),
                any(String.class),
                any(String.class),
                any(boolean.class)
            );
        PowerMockito.doReturn(new StringBuilder()).when(allotissementService, "prepareIdsDossierForLog", any(), any());

        Boolean lotCreated = false;
        try {
            lotCreated =
                Whitebox.invokeMethod(
                    allotissementService,
                    "createLotUnRestricted",
                    questionDirectrice,
                    questions,
                    session,
                    user
                );
        } finally {
            assertFalse(lotCreated);
            verify(documentRoutingService, times(1)).lockDocumentRoute(eq(routeDirectrice), eq(session));
            verify(journalService, times(0)).journaliserActionForUser(any(), any(), any(), any(), any(), any());
            verify(reponseService, times(0)).saveReponse(any(), any(), any());
            verify(documentRoutingService, times(0)).unlockDocumentRoute(any(), any());
            verify(session, times(0)).save();
            verify(session, times(0)).saveDocument(any());

            //On vérifie que les méthodes privées ont bien été appelées ou pas
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
                .invoke("getLastStepDirecteurInfo", any(), any());
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(1))
                .invoke("createAllotissementDoc", any(), any());
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke(
                    "addQuestionToRouteDirectrice",
                    any(CoreSession.class),
                    any(List.class),
                    any(List.class),
                    any(DossierLink.class),
                    any(Question.class),
                    any(String.class),
                    any(String.class),
                    any(boolean.class)
                );
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke("prepareIdsDossierForLog", any(), any());
        }
    }

    @Test(expected = AllotissementException.class)
    public void createLotWithExceptionHasNotFdr() throws Exception {
        //On a pas de feuille de route !
        when(dossierDirecteur.hasFeuilleRoute()).thenReturn(false);

        when(dossierDirecteur.getIdMinistereAttributaireCourant()).thenReturn("id");
        when(dossierAllotie.getIdMinistereAttributaireCourant()).thenReturn("id");

        PowerMockito
            .doReturn(dossierLinkDirecteur)
            .when(allotissementService, "getLastStepDirecteurInfo", any(), any());
        PowerMockito.doReturn(allotissement).when(allotissementService, "createAllotissementDoc", any(), any());
        PowerMockito
            .doNothing()
            .when(
                allotissementService,
                "addQuestionToRouteDirectrice",
                any(CoreSession.class),
                any(List.class),
                any(List.class),
                any(DossierLink.class),
                any(Question.class),
                any(String.class),
                any(String.class),
                any(boolean.class)
            );
        PowerMockito.doReturn(new StringBuilder()).when(allotissementService, "prepareIdsDossierForLog", any(), any());

        Boolean lotCreated = false;
        try {
            lotCreated =
                Whitebox.invokeMethod(
                    allotissementService,
                    "createLotUnRestricted",
                    questionDirectrice,
                    questions,
                    session,
                    user
                );
        } finally {
            assertFalse(lotCreated);
            verify(documentRoutingService, times(0)).lockDocumentRoute(any(), any());
            verify(journalService, times(0)).journaliserActionForUser(any(), any(), any(), any(), any(), any());
            verify(reponseService, times(0)).saveReponse(any(), any(), any());
            verify(documentRoutingService, times(0)).unlockDocumentRoute(any(), any());
            verify(session, times(0)).save();
            verify(session, times(0)).saveDocument(any());

            //On vérifie que les méthodes privées ont bien été appelées ou pas
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke("getLastStepDirecteurInfo", any(), any());
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke("createAllotissementDoc", any(), any());
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke(
                    "addQuestionToRouteDirectrice",
                    any(CoreSession.class),
                    any(List.class),
                    any(List.class),
                    any(DossierLink.class),
                    any(Question.class),
                    any(String.class),
                    any(String.class),
                    any(boolean.class)
                );
            PowerMockito
                .verifyPrivate(allotissementService, VerificationModeFactory.times(0))
                .invoke("prepareIdsDossierForLog", any(), any());
        }
    }
}
