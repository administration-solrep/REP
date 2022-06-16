package fr.dila.reponses.ui.services.impl;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.reponses.ui.bean.RequetePersoDTO;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.RequeteurActionService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.automation.core.util.PageProviderHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.runtime.api.Framework;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        Framework.class,
        PageProviderHelper.class,
        ReponsesActionsServiceLocator.class,
        ServiceUtil.class,
        STServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class SuiviMenuComponentServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SuiviMenuComponentServiceImpl suiviMenuComponentServiceImpl = new SuiviMenuComponentServiceImpl();

    @Mock
    private CoreSession session;

    @Mock
    private UserWorkspaceService userWorkspaceService;

    @Mock
    private RequeteurActionService requeteurActionService;

    @Mock
    private PageProviderDefinition definition;

    @Mock
    private PageProviderService pageProviderService;

    @Mock
    private CoreQueryDocumentPageProvider provider;

    @Mock
    private SpecificContext context;

    private DocumentModel newDocumentModel(String title, String id) {
        DocumentModel doc = Mockito.mock(DocumentModel.class);
        when(doc.getTitle()).thenReturn(title);
        when(doc.getId()).thenReturn(id);

        return doc;
    }

    @Test
    public void testGetRequetesGenerales() throws Exception {
        // Given
        mockStatic(ServiceUtil.class);
        when(ServiceUtil.getRequiredService(Mockito.any())).thenReturn(pageProviderService);
        when(pageProviderService.getPageProviderDefinition("requetesGeneralesPageProvider")).thenReturn(definition);

        mockStatic(PageProviderHelper.class);
        when(
            (CoreQueryDocumentPageProvider) PageProviderHelper.getPageProvider(
                Mockito.eq(session),
                Mockito.eq(definition),
                Mockito.any()
            )
        )
            .thenReturn(provider);

        List<DocumentModel> requetesGeneralesDocs = asList(
            newDocumentModel("t1", "id1"),
            newDocumentModel("t2", "id2")
        );
        when(provider.getCurrentPage()).thenReturn(requetesGeneralesDocs);

        mockStatic(ReponsesActionsServiceLocator.class);
        when(ReponsesActionsServiceLocator.getRequeteurActionService()).thenReturn(requeteurActionService);

        DocumentModel biblioStandardDoc = Mockito.mock(DocumentModel.class);
        when(requeteurActionService.getBibliothequeStandard(Mockito.any())).thenReturn(biblioStandardDoc);
        when(biblioStandardDoc.getId()).thenReturn("my-super-id");

        mockStatic(Framework.class);
        when(Framework.expandVars(Mockito.anyString())).thenAnswer(i -> i.getArguments()[0]);

        when(context.getSession()).thenReturn(session);

        // When
        List<Action> requetesGenerales = suiviMenuComponentServiceImpl.getRequetesGenerales(context);

        // Then
        Assertions.assertThat(requetesGenerales).isNotEmpty();

        Assertions.assertThat(requetesGenerales.get(0).getLabel()).isEqualTo("t1");
        Assertions
            .assertThat(requetesGenerales.get(0).getLink())
            .isEqualTo("/suivi/requete?idRequete=id1#main_content");

        Assertions.assertThat(requetesGenerales.get(1).getLabel()).isEqualTo("t2");
        Assertions
            .assertThat(requetesGenerales.get(1).getLink())
            .isEqualTo("/suivi/requete?idRequete=id2#main_content");
    }

    @Test
    public void testGetRequetesPersonelles() throws Exception {
        // Given
        mockStatic(ServiceUtil.class);
        when(ServiceUtil.getRequiredService(Mockito.any())).thenReturn(pageProviderService);
        when(pageProviderService.getPageProviderDefinition("requetesPersonellesPageProvider")).thenReturn(definition);

        mockStatic(PageProviderHelper.class);
        when(
            (CoreQueryDocumentPageProvider) PageProviderHelper.getPageProvider(
                Mockito.eq(session),
                Mockito.eq(definition),
                Mockito.any()
            )
        )
            .thenReturn(provider);

        List<DocumentModel> requetesPersonnellesDocs = asList(
            newDocumentModel("t1", "id1"),
            newDocumentModel("t2", "id2")
        );
        when(provider.getCurrentPage()).thenReturn(requetesPersonnellesDocs);

        mockStatic(STServiceLocator.class);
        when(STServiceLocator.getUserWorkspaceService()).thenReturn(userWorkspaceService);

        DocumentModel userWorkspaceDoc = Mockito.mock(DocumentModel.class);
        when(userWorkspaceService.getCurrentUserPersonalWorkspace(Mockito.any())).thenReturn(userWorkspaceDoc);
        when(userWorkspaceDoc.getId()).thenReturn("my-super-id");

        mockStatic(Framework.class);
        when(Framework.expandVars(Mockito.anyString())).thenAnswer(i -> i.getArguments()[0]);

        when(context.getSession()).thenReturn(session);

        // When
        List<RequetePersoDTO> requetesGenerales = suiviMenuComponentServiceImpl.getRequetesPersonelles(context);

        // Then
        Assertions.assertThat(requetesGenerales).isNotEmpty();

        Assertions.assertThat(requetesGenerales.get(0).getLabel()).isEqualTo("t1");
        Assertions
            .assertThat(requetesGenerales.get(0).getLink())
            .isEqualTo("/suivi/requete?idRequete=id1#main_content");

        Assertions.assertThat(requetesGenerales.get(1).getLabel()).isEqualTo("t2");
        Assertions
            .assertThat(requetesGenerales.get(1).getLink())
            .isEqualTo("/suivi/requete?idRequete=id2#main_content");
    }
}
