package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.services.ReponsesModeleFdrFicheUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.ReponsesModeleFdrForm;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ UserSessionHelper.class, SSActionsServiceLocator.class, ReponsesUIServiceLocator.class })
public class ReponsesModeleFeuilleRouteAjaxTest {
    private ReponsesModeleFeuilleRouteAjax controlleur;

    @Mock
    private CoreSession session;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private SpecificContext context;

    @Mock
    private ModeleFeuilleRouteActionService modeleAction;

    @Mock
    private ReponsesModeleFdrFicheUIService modeleFDRFicheUIService;

    @Before
    public void before() throws Exception {
        controlleur = new ReponsesModeleFeuilleRouteAjax();

        Whitebox.setInternalState(controlleur, "context", context);

        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);

        when(context.getSession()).thenReturn(session);
        when(SSActionsServiceLocator.getModeleFeuilleRouteActionService()).thenReturn(modeleAction);
        when(ReponsesUIServiceLocator.getReponsesModeleFdrFicheUIService()).thenReturn(modeleFDRFicheUIService);
    }

    @Test
    public void testValiderModele() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(modeleAction.canValidateRoute(context)).thenReturn(true);

        ReponsesModeleFdrForm form = new ReponsesModeleFdrForm();
        form.setId("id");

        JsonResponse reponse = (JsonResponse) controlleur.validerModele(form, true).getEntity();

        Mockito.verify(modeleFDRFicheUIService).updateModele(context, form);
        Mockito.verify(modeleAction).validateRouteModel(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }
}
