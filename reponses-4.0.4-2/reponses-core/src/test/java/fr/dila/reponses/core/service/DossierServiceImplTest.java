package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DOCUMENT_TYPE;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierService;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.Calendar;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesServiceLocator.class, QueryUtils.class })
@PowerMockIgnore("javax.management.*")
public class DossierServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private AllotissementService allotissementService;

    @Mock
    private CoreSession session;

    private DossierService service;

    @Mock
    private Dossier dossier;

    @Mock
    private DocumentModel dossierDocRappel;

    @Mock
    private Dossier dossierRappel;

    @Mock
    private DocumentModel questionDoc;

    @Mock
    private Question question;

    @Mock
    private Question questionRappel;

    @Mock
    private Allotissement allotissement;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getAllotissementService()).thenReturn(allotissementService);

        PowerMockito.mockStatic(QueryUtils.class);
        List<String> idsQuestion = Lists.newArrayList("q1");
        when(
            QueryUtils.doUFNXQLQueryForIdsList(
                Mockito.eq(session),
                Mockito.anyString(),
                Mockito.eq(new String[] { "d1" })
            )
        )
            .thenReturn(idsQuestion);
        DocumentModelList questions = new DocumentModelListImpl(Lists.newArrayList(questionDoc));
        when(QueryUtils.retrieveDocuments(session, QUESTION_DOCUMENT_TYPE, idsQuestion)).thenReturn(questions);

        service = new DossierServiceImpl();

        when(dossier.getQuestion(session)).thenReturn(question);
        when(questionDoc.getAdapter(Question.class)).thenReturn(question);
        when(question.getDateRappelQuestion()).thenReturn(Calendar.getInstance());
        when(dossier.getDossierLot()).thenReturn("12345");
        when(allotissementService.getAllotissement("12345", session)).thenReturn(allotissement);
        when(allotissement.getIdDossiers()).thenReturn(Lists.newArrayList("d1"));
        when(session.getDocument(new IdRef("d2"))).thenReturn(dossierDocRappel);
        when(dossierDocRappel.getAdapter(Dossier.class)).thenReturn(dossierRappel);
        when(dossierRappel.getQuestion(session)).thenReturn(questionRappel);
    }

    @Test
    public void getQERappels() {
        List<Question> qeRappels = service.getQERappels(session, dossier);
        Assertions.assertThat(qeRappels).contains(question);
    }

    @Test
    public void getQERappelsForDossierWithoutLot() {
        when(dossier.getDossierLot()).thenReturn(null);
        List<Question> qeRappels = service.getQERappels(session, dossier);
        Assertions.assertThat(qeRappels).isEmpty();
    }
}
