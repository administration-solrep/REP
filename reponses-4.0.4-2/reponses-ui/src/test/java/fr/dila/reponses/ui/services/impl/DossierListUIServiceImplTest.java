package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.ROUTING_TASK_TYPE;
import static fr.dila.reponses.ui.jaxrs.webobject.page.AbstractReponsesTravail.PARAM_MIN_ATTRIB;
import static fr.dila.reponses.ui.jaxrs.webobject.page.AbstractReponsesTravail.PARAM_POSTE_ID;
import static fr.dila.reponses.ui.services.impl.DossierListUIServiceImpl.MISSING_PARAM_ERROR;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.PlanClassementService;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.ReponsesMailboxService;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.contentview.RechercheResultPageProvider;
import fr.dila.reponses.ui.contentview.RepCorbeillePageProvider;
import fr.dila.reponses.ui.jaxrs.webobject.page.AbstractReponsesTravail;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.DossierTravailListForm;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.STPageProviderHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.MissingArgumentException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.mock.SerializableMode;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.core.CoreQueryPageProviderDescriptor;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        ReponsesServiceLocator.class,
        SpecificContext.class,
        WebContext.class,
        UserSession.class,
        Framework.class,
        STServiceLocator.class,
        SSServiceLocator.class,
        STPageProviderHelper.class
    }
)
@PowerMockIgnore("javax.management.*")
public class DossierListUIServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Spy
    private DossierListUIServiceImpl service;

    @Mock
    private PlanClassementService planService;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private Map<String, Object> contextData;

    private CoreSession session;

    @Mock
    private RechercheResultPageProvider rechercheProvider;

    @Mock
    private RepCorbeillePageProvider corbeilleProvider;

    @Mock
    private PageProviderService pageProviderService;

    @Mock
    private CoreQueryPageProviderDescriptor descriptor;

    @Mock
    private ProfilUtilisateurService profilService;

    @Mock
    private OrganigrammeService orgaService;

    @Mock
    private ReponsesMailboxService mailboxService;

    @Mock
    private MailboxPosteService mailboxPosteService;

    @Mock
    private ReponsesVocabularyService vocService;

    @Mock
    private Mailbox mailbox;

    @Before
    public void before() {
        userSession = PowerMockito.mock(UserSession.class);
        PowerMockito.spy(UserSession.class);

        when(context.getWebcontext()).thenReturn(webContext);
        when(context.getContextData()).thenReturn(contextData);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(context.getSession()).thenReturn(session);

        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);
        PowerMockito.mockStatic(SSServiceLocator.class);
        PowerMockito.mockStatic(Framework.class);
        PowerMockito.mockStatic(STPageProviderHelper.class);

        session =
            Mockito.mock(CoreSession.class, Mockito.withSettings().serializable(SerializableMode.ACROSS_CLASSLOADERS));

        when(ReponsesServiceLocator.getPlanClassementService()).thenReturn(planService);
        when(ReponsesServiceLocator.getMailboxService()).thenReturn(mailboxService);
        when(STServiceLocator.getOrganigrammeService()).thenReturn(orgaService);
        when(SSServiceLocator.getMailboxPosteService()).thenReturn(mailboxPosteService);
        when(ReponsesServiceLocator.getVocabularyService()).thenReturn(vocService);
        when(Framework.getService(PageProviderService.class)).thenReturn(pageProviderService);
        when(pageProviderService.getPageProviderDefinition("dossierPageProvider")).thenReturn(descriptor);
        when(pageProviderService.getPageProviderDefinition("corbeillePageProvider")).thenReturn(descriptor);
        when(rechercheProvider.getResultsCount()).thenReturn(10L);
        when(corbeilleProvider.getResultsCount()).thenReturn(10L);

        when(ReponsesServiceLocator.getProfilUtilisateurService()).thenReturn(profilService);
        when(profilService.getUserColumn(any())).thenReturn(new ArrayList<>());

        when(orgaService.getOrganigrammeNodeById("1234", OrganigrammeType.MINISTERE)).thenReturn(new EntiteNodeImpl());
        when(mailboxPosteService.getMailboxPoste(session, "2")).thenReturn(mailbox);
        when(mailbox.getTitle()).thenReturn("Titre de poste");
    }

    @Test
    public void testGetDossierFromPlanClassementError() {
        Mockito.doReturn(rechercheProvider).when(service).buildRechercheProvider(anyString(), any(), any(), any());

        // On vérifie que le manque d'au moins un des paramètres lance une erreur
        try {
            service.getDossiersFromPlanClassement(new SpecificContext());
            fail("Une exception doit être levée");
        } catch (Exception e) {
            assertEquals(MISSING_PARAM_ERROR, e.getMessage());
        }

        // On instancie un contexte auquel on rajoute l'origine
        SpecificContext ctx = new SpecificContext();
        ctx.getContextData().put("origine", DossierConstants.ORIGINE_QUESTION_AN);
        try {
            service.getDossiersFromPlanClassement(ctx);
            fail("Une exception doit être levée");
        } catch (Exception e) {
            assertEquals(MISSING_PARAM_ERROR, e.getMessage());
        }

        // On instancie un contexte auquel on rajoute la clé
        ctx = new SpecificContext();
        ctx.getContextData().put("cle", "key");
        try {
            service.getDossiersFromPlanClassement(ctx);
            fail("Une exception doit être levée");
        } catch (Exception e) {
            assertEquals(MISSING_PARAM_ERROR, e.getMessage());
        }

        // On instancie un contexte auquel on rajoute la clé
        ctx = new SpecificContext();
        ctx.getContextData().put("cleParent", "parentKey");
        try {
            service.getDossiersFromPlanClassement(ctx);
            fail("Une exception doit être levée");
        } catch (Exception e) {
            assertEquals(MISSING_PARAM_ERROR, e.getMessage());
        }
    }

    @Test
    public void testGetDossierFromCorbeilleError() {
        Mockito.doReturn(corbeilleProvider).when(service).buildCorbeilleProvider(any(), any(), any(), any());

        // On vérifie que le manque d'au moins un des paramètres lance une erreur
        SpecificContext ctx = new SpecificContext();
        checkArgs(ctx);

        // On instancie un contexte auquel on rajoute l'origine
        ctx = new SpecificContext();
        ctx.getContextData().put("minAttribId", "1234");
        checkArgs(ctx);

        // On instancie un contexte auquel on rajoute la clé
        ctx = new SpecificContext();
        ctx.getContextData().put("routingTaskType", "2");
        ctx.getContextData().put("posteId", "2");
        ctx.getContextData().put("isSignale", "true");
        checkArgs(ctx);
    }

    private void checkArgs(SpecificContext ctx) {
        assertThrowsMissingArgumentExc(() -> service.getDossiersFromMinCorbeille(ctx));
        assertThrowsMissingArgumentExc(() -> service.getDossiersFromPosteCorbeille(ctx));
        assertThrowsMissingArgumentExc(() -> service.getDossiersFromSignaleCorbeille(ctx));
    }

    private void assertThrowsMissingArgumentExc(ThrowingCallable throwingCallable) {
        Throwable throwable = catchThrowable(throwingCallable);
        Assertions.assertThat(throwable).isInstanceOf(MissingArgumentException.class).hasMessage(MISSING_PARAM_ERROR);
    }

    @Test
    public void testGetDossierFromPlanClassementWrongForm() {
        Mockito.doReturn(rechercheProvider).when(service).buildRechercheProvider(anyString(), any(), any(), any());

        DossierListForm form = new DossierListForm();

        when(contextData.get("origine")).thenReturn(DossierConstants.ORIGINE_QUESTION_AN);
        when(contextData.get("cle")).thenReturn("key");
        when(contextData.get("cleParent")).thenReturn("parentKey");
        when(contextData.get("form")).thenReturn(form);

        try {
            // On vérifie qu'on gère le négatif => appel avec les valeurs par défaut
            form.setPage(-1);
            form.setSize(1);
            service.getDossiersFromPlanClassement(context);

            Mockito
                .verify(planService)
                .getDossierFromIndexationQuery(DossierConstants.ORIGINE_QUESTION_AN, "parentKey", "key");

            // suite de vérification du négatif => appel avec les valeurs par défaut
            form.setPage(1);
            form.setSize(-1);
            service.getDossiersFromPlanClassement(context);

            Mockito
                .verify(planService, Mockito.times(2))
                .getDossierFromIndexationQuery(DossierConstants.ORIGINE_QUESTION_AN, "parentKey", "key");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetDossierFromMinCorbeilleWrongForm() {
        DossierTravailListForm form = new DossierTravailListForm();
        Mockito.doReturn(corbeilleProvider).when(service).buildCorbeilleProvider(any(), any(), any(), any());

        when(context.getFromContextData(PARAM_MIN_ATTRIB)).thenReturn("1234");
        when(context.getFromContextData(ROUTING_TASK_TYPE)).thenReturn("2");
        when(contextData.get("form")).thenReturn(form);

        try {
            // On vérifie qu'on gère le négatif => appel avec les valeurs par défaut
            form.setPage(-1);
            form.setSize(1);
            service.getDossiersFromMinCorbeille(context);

            Mockito.verify(corbeilleProvider).getCurrentPage();

            // suite de vérification du négatif => appel avec les valeurs par défaut
            form.setPage(1);
            form.setSize(-1);
            service.getDossiersFromMinCorbeille(context);

            Mockito.verify(corbeilleProvider, Mockito.times(2)).getCurrentPage();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetDossierFromPosteCorbeilleWrongForm() {
        DossierTravailListForm form = new DossierTravailListForm();
        Mockito.doReturn(corbeilleProvider).when(service).buildCorbeilleProvider(any(), any(), any(), any());

        when(context.getFromContextData(PARAM_MIN_ATTRIB)).thenReturn("1234");
        when(context.getFromContextData(PARAM_POSTE_ID)).thenReturn("2");
        when(contextData.get("form")).thenReturn(form);

        try {
            // On vérifie qu'on gère le négatif => appel avec les valeurs par défaut
            form.setPage(-1);
            form.setSize(1);
            service.getDossiersFromPosteCorbeille(context);

            Mockito.verify(corbeilleProvider).getCurrentPage();

            // suite de vérification du négatif => appel avec les valeurs par défaut
            form.setPage(1);
            form.setSize(-1);
            service.getDossiersFromPosteCorbeille(context);

            Mockito.verify(corbeilleProvider, Mockito.times(2)).getCurrentPage();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetDossierFromSignalCorbeilleWrongForm() throws MissingArgumentException {
        DossierTravailListForm form = new DossierTravailListForm();
        Mockito.doReturn(corbeilleProvider).when(service).buildCorbeilleProvider(any(), any(), any(), any());

        when(context.getFromContextData("minAttribId")).thenReturn("1234");
        when(context.getFromContextData("isSignale")).thenReturn(Boolean.TRUE);
        when(contextData.get("form")).thenReturn(form);

        // On vérifie qu'on gère le négatif => appel avec les valeurs par défaut
        form.setPage(-1);
        form.setSize(1);
        service.getDossiersFromSignaleCorbeille(context);

        Mockito.verify(corbeilleProvider).getCurrentPage();

        // suite de vérification du négatif => appel avec les valeurs par défaut
        form.setPage(1);
        form.setSize(-1);
        service.getDossiersFromSignaleCorbeille(context);
        Mockito.verify(corbeilleProvider, Mockito.times(2)).getCurrentPage();
    }

    @Test
    public void testGetDossierFromPlanClassementOK() throws MissingArgumentException {
        DossierListForm form = new DossierListForm();

        Mockito.doReturn(rechercheProvider).when(service).buildRechercheProvider(any(), any(), any(), any());
        when(rechercheProvider.getCurrentPage()).thenReturn(buildFakeListDossiers());
        when(contextData.get("origine")).thenReturn(DossierConstants.ORIGINE_QUESTION_AN);
        when(contextData.get("cle")).thenReturn("key");
        when(contextData.get("cleParent")).thenReturn("parentKey");
        when(contextData.get("form")).thenReturn(form);
        when(contextData.get("nbTotal")).thenReturn(10);

        // cas avec les valeurs par défaut
        RepDossierList lstDoss = service.getDossiersFromPlanClassement(context);
        Mockito
            .verify(planService)
            .getDossierFromIndexationQuery(DossierConstants.ORIGINE_QUESTION_AN, "parentKey", "key");
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());

        // Cas avec des valeurs non autorisées doit être équivalent aux valeurs par défaut
        form.setPage(0);
        form.setSize(100);
        lstDoss = service.getDossiersFromPlanClassement(context);
        Mockito
            .verify(planService, Mockito.times(2))
            .getDossierFromIndexationQuery(DossierConstants.ORIGINE_QUESTION_AN, "parentKey", "key");
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());

        // cas différent des valeurs par défaut
        form.setPage(2);
        form.setSize(20);
        lstDoss = service.getDossiersFromPlanClassement(context);
        Mockito
            .verify(planService, Mockito.times(3))
            .getDossierFromIndexationQuery(DossierConstants.ORIGINE_QUESTION_AN, "parentKey", "key");
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());
    }

    @Test
    public void testGetDossierFromMinCorbeilleOK() throws MissingArgumentException {
        DossierTravailListForm form = new DossierTravailListForm();

        Mockito.doReturn(corbeilleProvider).when(service).buildCorbeilleProvider(any(), any(), any(), any());
        when(corbeilleProvider.getCurrentPage()).thenReturn(buildFakeListDossiers());
        when(context.getFromContextData(PARAM_MIN_ATTRIB)).thenReturn("1234");
        when(context.getFromContextData(ROUTING_TASK_TYPE)).thenReturn("2");
        when(contextData.get("form")).thenReturn(form);

        // cas avec les valeurs par défaut
        RepDossierList lstDoss = service.getDossiersFromMinCorbeille(context);
        Mockito.verify(corbeilleProvider).getCurrentPage();
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());

        // cas différent des valeurs par défaut
        form.setPage(2);
        form.setSize(20);
        lstDoss = service.getDossiersFromMinCorbeille(context);
        Mockito.verify(corbeilleProvider, Mockito.times(2)).getCurrentPage();
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());
    }

    @Test
    public void testGetDossierFromPosteCorbeilleOK() throws MissingArgumentException {
        DossierTravailListForm form = new DossierTravailListForm();

        Mockito.doReturn(corbeilleProvider).when(service).buildCorbeilleProvider(any(), any(), any(), any());
        when(corbeilleProvider.getCurrentPage()).thenReturn(buildFakeListDossiers());
        when(context.getFromContextData(PARAM_MIN_ATTRIB)).thenReturn("1234");
        when(context.getFromContextData(PARAM_POSTE_ID)).thenReturn("2");
        when(contextData.get("form")).thenReturn(form);

        // cas avec les valeurs par défaut
        RepDossierList lstDoss = service.getDossiersFromPosteCorbeille(context);
        Mockito.verify(corbeilleProvider).getCurrentPage();
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());

        // Cas avec des valeurs non autorisées doit être équivalent aux valeurs par défaut
        form.setPage(0);
        form.setSize(100);
        lstDoss = service.getDossiersFromPosteCorbeille(context);
        Mockito.verify(corbeilleProvider, Mockito.times(2)).getCurrentPage();
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());

        // cas différent des valeurs par défaut
        form.setPage(2);
        form.setSize(20);
        lstDoss = service.getDossiersFromPosteCorbeille(context);
        Mockito.verify(corbeilleProvider, Mockito.times(3)).getCurrentPage();
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());
    }

    @Test
    public void testGetDossierFromSignalCorbeilleOK() throws MissingArgumentException {
        DossierTravailListForm form = new DossierTravailListForm();

        Mockito.doReturn(corbeilleProvider).when(service).buildCorbeilleProvider(any(), any(), any(), any());
        when(corbeilleProvider.getCurrentPage()).thenReturn(buildFakeListDossiers());
        when(context.getFromContextData(PARAM_MIN_ATTRIB)).thenReturn("1234");
        when(context.getFromContextData("isSignale")).thenReturn(Boolean.TRUE);
        when(contextData.get("form")).thenReturn(form);

        // cas avec les valeurs par défaut
        RepDossierList lstDoss = service.getDossiersFromSignaleCorbeille(context);
        Mockito.verify(corbeilleProvider).getCurrentPage();
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());

        // Cas avec des valeurs non autorisées doit être équivalent aux valeurs par défaut
        form.setPage(0);
        form.setSize(100);
        lstDoss = service.getDossiersFromSignaleCorbeille(context);
        Mockito.verify(corbeilleProvider, Mockito.times(2)).getCurrentPage();
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());

        // cas différent des valeurs par défaut
        form.setPage(2);
        form.setSize(20);
        lstDoss = service.getDossiersFromSignaleCorbeille(context);
        Mockito.verify(corbeilleProvider, Mockito.times(3)).getCurrentPage();
        assertNotNull(lstDoss);
        assertEquals(10, lstDoss.getNbTotal().intValue());
        assertNotNull(lstDoss.getListe());
        assertEquals(10, lstDoss.getListe().size());
    }

    @Test
    public void testBuildRechercheProvider() {
        PageProvider provider = initProvider(new RechercheResultPageProvider());
        when(STPageProviderHelper.getPageProvider(any(), any(), Mockito.anyVararg())).thenReturn(provider);

        CoreSession fakeSession = Mockito.mock(
            CoreSession.class,
            Mockito.withSettings().serializable(SerializableMode.ACROSS_CLASSLOADERS)
        );
        RechercheResultPageProvider providerTest = service.buildRechercheProvider(
            "Select q.ecm:uuid as id from Question q",
            new ArrayList<>(),
            new DossierListForm(),
            fakeSession
        );

        assertNotNull(providerTest);
        assertEquals(0, providerTest.getCurrentPageIndex());
        assertEquals(10, providerTest.getPageSize());

        DossierListForm form = new DossierListForm();
        form.setPage(5);
        form.setSize(50);
        providerTest =
            service.buildRechercheProvider(
                "Select q.ecm:uuid as id from Question q",
                new ArrayList<>(),
                form,
                fakeSession
            );

        assertNotNull(providerTest);
        assertEquals(4, providerTest.getCurrentPageIndex());
        assertEquals(50, providerTest.getPageSize());
    }

    @Test
    public void testBuildCorbeilleProvider() {
        PageProvider provider = initProvider(new RepCorbeillePageProvider());
        when(STPageProviderHelper.getPageProvider(any(), any(), Mockito.anyVararg())).thenReturn(provider);

        CoreSession fakeSession = Mockito.mock(
            CoreSession.class,
            Mockito.withSettings().serializable(SerializableMode.ACROSS_CLASSLOADERS)
        );
        RepCorbeillePageProvider providerTest = service.buildCorbeilleProvider(
            "test",
            new ArrayList<>(),
            new DossierTravailListForm(),
            fakeSession
        );

        assertNotNull(providerTest);
        assertEquals(0, providerTest.getCurrentPageIndex());
        assertEquals(10, providerTest.getPageSize());

        DossierTravailListForm form = new DossierTravailListForm();
        form.setPage(5);
        form.setSize(50);
        providerTest = service.buildCorbeilleProvider("test", new ArrayList<>(), form, fakeSession);

        assertNotNull(providerTest);
        assertEquals(4, providerTest.getCurrentPageIndex());
        assertEquals(50, providerTest.getPageSize());
    }

    private List<Map<String, Serializable>> buildFakeListDossiers() {
        List<Map<String, Serializable>> lstDocs = new ArrayList<>();
        RepDossierListingDTO q = Mockito.mock(RepDossierListingDTO.class);

        for (int i = 0; i < 10; i++) {
            lstDocs.add(q);
        }
        return lstDocs;
    }

    private PageProvider initProvider(PageProvider provider) {
        provider.setDefinition(descriptor);
        provider.setProperties(new HashMap<>());
        return provider;
    }
}
