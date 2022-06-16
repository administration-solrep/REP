package fr.dila.reponses.ui.jaxrs.webobject.ajax.dossier;

import static fr.dila.st.ui.th.model.SpecificContext.MESSAGE_QUEUE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.AllotissementActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.st.ui.assertions.ResponseAssertions;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesActionsServiceLocator.class, UserSessionHelper.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesDossierAllotissementAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesDossierAllotissementAjax page;

    @Mock
    private SpecificContext context;

    @Mock
    private AllotissementActionService allotissementActionService;

    @Before
    public void before() {
        page = new ReponsesDossierAllotissementAjax();

        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        when(ReponsesActionsServiceLocator.getAllotissementActionService()).thenReturn(allotissementActionService);

        PowerMockito.mockStatic(UserSessionHelper.class);

        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());

        Whitebox.setInternalState(page, "context", context);
    }

    @Test
    public void testAddToLot() {
        String searchedQuestion = "searched-question";
        String dossierId = "dossier-id";

        Response response = page.addToLot(searchedQuestion, dossierId);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(context).setCurrentDocument(dossierId);
        verify(context).putInContextData(ReponsesContextDataKey.SEARCHED_QUESTION, searchedQuestion);

        verify(allotissementActionService).addToLot(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, MESSAGE_QUEUE, context.getMessageQueue());
    }
}
