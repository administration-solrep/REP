package fr.dila.reponses.ui.services.impl;

import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.microsoft.sqlserver.jdbc.StringUtils;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.bean.RequetePersoForm;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.RequeteurActionService;
import fr.dila.reponses.ui.services.actions.nxql.ReponsesSmartNXQLQueryActionService;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.enumeration.OperatorEnum;
import fr.dila.ss.ui.bean.RequeteLigneDTO;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.SecurityService;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.platform.query.core.CoreQueryPageProviderDescriptor;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesActionsServiceLocator.class, STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class RequeteUIServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private RequeteurActionService requeteurActionService;

    @Mock
    private ReponsesSmartNXQLQueryActionService reponsesSmartNXQLQueryActionService;

    @Mock
    private CoreQueryPageProviderDescriptor descriptor;

    @Mock
    private UserWorkspaceService userWorkspaceService;

    @Mock
    private SecurityService securityService;

    @Mock
    private CoreSession session;

    @Spy
    private RequeteUIServiceImpl service;

    private SpecificContext context;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        when(ReponsesActionsServiceLocator.getRequeteurActionService()).thenReturn(requeteurActionService);
        when(ReponsesActionsServiceLocator.getReponsesSmartNXQLQueryActionService())
            .thenReturn(reponsesSmartNXQLQueryActionService);

        when(STServiceLocator.getUserWorkspaceService()).thenReturn(userWorkspaceService);
        when(STServiceLocator.getSecurityService()).thenReturn(securityService);

        context = new SpecificContext();
    }

    @Test
    public void testGetDossiersByRequeteExperte() {
        DocumentModel requeteDoc = mock(DocumentModel.class);
        RequeteExperte requete = mock(RequeteExperte.class);
        DossierListForm listForm = new DossierListForm();

        context.putInContextData(ID, "id");
        context.putInContextData(ReponsesContextDataKey.DOSSIER_LIST_FORM, listForm);
        context.setSession(session);

        when(session.getDocument(any())).thenReturn(requeteDoc);
        when(requeteDoc.getAdapter(RequeteExperte.class)).thenReturn(requete);
        when(requeteurActionService.getFullQuery(session, requete)).thenReturn("Requête NXQL");
        doReturn(null).when(service).getDossiersByQuery(any(), any(), any(), any());
        doReturn(new RepDossierList()).when(service).getDossiersByQuery(session, "Requête NXQL", listForm, requeteDoc);

        RepDossierList list = service.getDossiersByRequeteExperte(context);
        assertNotNull(list);
    }

    @Test
    public void testGetDossiersByRequeteCriteria() {
        List<RequeteLigneDTO> requeteCriteria = new ArrayList<>();
        DossierListForm listForm = new DossierListForm();
        Calendar cal = SolonDateConverter.DATE_SLASH.parseToCalendar("01/01/2020");
        String startDate = SolonDateConverter.DATETIME_DASH_REVERSE_T_SECOND_COLON_Z.format(cal, true);
        cal.add(Calendar.HOUR, 23);
        cal.add(Calendar.MINUTE, 59);
        cal.add(Calendar.SECOND, 59);
        String endDate = SolonDateConverter.DATETIME_DASH_REVERSE_T_SECOND_COLON_Z.format(cal, true);
        String query =
            "e2.ecm:currentLifeCycleState = 'validated' " +
            "AND q.qu:dateCaduciteQuestion < TIMESTAMP '" +
            startDate +
            "' OR q.qu:datePublicationJO NOT BETWEEN TIMESTAMP '" +
            startDate +
            "' AND TIMESTAMP '" +
            endDate +
            "' AND q.qu:typeQuestion IN ('QE','QOAE')";

        requeteCriteria.add(
            buildRequeteLigne(null, "e2.ecm:currentLifeCycleState", OperatorEnum.EGAL, asList("validated"))
        );
        requeteCriteria.add(
            buildRequeteLigne("ET", "q.qu:dateCaduciteQuestion", OperatorEnum.PLUS_PETIT, asList("01/01/2020"))
        );
        requeteCriteria.add(
            buildRequeteLigne(
                "OU",
                "q.qu:datePublicationJO",
                OperatorEnum.PAS_ENTRE,
                asList("01/01/2020", "01/01/2020")
            )
        );
        requeteCriteria.add(buildRequeteLigne("ET", "q.qu:typeQuestion", OperatorEnum.DANS, asList("QE", "QOAE")));
        context.putInContextData(ReponsesContextDataKey.REQUETE_PERSO_LIGNES, requeteCriteria);
        context.putInContextData(ReponsesContextDataKey.DOSSIER_LIST_FORM, listForm);
        context.setSession(session);

        when(reponsesSmartNXQLQueryActionService.getFullQuery(session, query)).thenReturn(query);
        doReturn(null).when(service).getDossiersByQuery(any(), any(), any(), any());
        doReturn(new RepDossierList()).when(service).getDossiersByQuery(session, query, listForm, null);

        RepDossierList list = service.getDossiersByRequeteCriteria(context);
        assertNotNull(list);
    }

    private RequeteLigneDTO buildRequeteLigne(String andOr, String field, OperatorEnum operator, List<String> value) {
        RequeteLigneDTO dto = new RequeteLigneDTO();
        dto.setAndOr(andOr);
        ChampDescriptor champ = new ChampDescriptor();
        champ.setField(field);
        dto.setChamp(champ);
        dto.setOperator(operator);
        dto.setValue(value);
        return dto;
    }

    @Test
    public void testSaveRequetePerso() {
        String pathString = "pathString";
        String goodQuery = "q.qu:typeQuestion IN ('QE')";
        String badQuery = "q.qu:typeQuestion IN ('QE";
        RequetePersoForm form = new RequetePersoForm();
        form.setTitre("titre");
        form.setCommentaire("commentaire");
        form.setRequete(goodQuery);
        DocumentModel userWorkSpaceDoc = mock(DocumentModel.class);
        DocumentModel requeteDoc = mock(DocumentModel.class);
        DocumentModel savedDoc = mock(DocumentModel.class);
        RequeteExperte requeteExperte = mock(RequeteExperte.class);
        SSPrincipal principal = mock(SSPrincipal.class);
        context.setSession(session);
        when(userWorkspaceService.getCurrentUserPersonalWorkspace(session)).thenReturn(userWorkSpaceDoc);
        when(userWorkSpaceDoc.getPathAsString()).thenReturn(pathString);
        when(session.createDocumentModel(pathString, form.getTitre(), STRequeteConstants.SMART_FOLDER_DOCUMENT_TYPE))
            .thenReturn(requeteDoc);
        when(requeteDoc.getAdapter(RequeteExperte.class)).thenReturn(requeteExperte);
        when(reponsesSmartNXQLQueryActionService.getFullQuery(session, goodQuery)).thenReturn("query");
        when(reponsesSmartNXQLQueryActionService.getFullQuery(session, badQuery)).thenReturn("bad query");
        doReturn(null).when(service).getDossiersByQuery(any(), eq("query"), any(), eq(requeteDoc));
        doThrow(new QueryParseException())
            .when(service)
            .getDossiersByQuery(any(), eq("bad query"), any(), eq(requeteDoc));
        when(session.createDocument(requeteDoc)).thenReturn(savedDoc);
        when(session.getPrincipal()).thenReturn(principal);
        when(savedDoc.getId()).thenReturn("id");

        context.putInContextData(ReponsesContextDataKey.REQUETE_PERSO_FORM, form);

        String resultOK = service.saveRequetePerso(context);

        assertNotNull(resultOK);
        assertEquals("id", resultOK);

        form.setRequete(badQuery);

        context.putInContextData(ReponsesContextDataKey.REQUETE_PERSO_FORM, form);

        String resultBadRequete = service.saveRequetePerso(context);
        assertNotNull(resultBadRequete);
        assertEquals(StringUtils.EMPTY, resultBadRequete);
        assertEquals(
            "La requête saisie n'est pas valide",
            context.getMessageQueue().getErrorQueue().get(0).getAlertMessage().get(0)
        );
        verify(requeteExperte).setWhereClause(form.getRequete());
    }
}
