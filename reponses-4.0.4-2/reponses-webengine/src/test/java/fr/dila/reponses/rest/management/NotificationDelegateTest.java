package fr.dila.reponses.rest.management;

import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED;
import static fr.sword.xsd.reponses.TraitementStatut.KO;
import static fr.sword.xsd.reponses.TraitementStatut.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.service.EtatApplicationService;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class, MockitoFeature.class })
public class NotificationDelegateTest {
    private NotificationDelegate delegate;

    @Mock
    private CloseableCoreSession session;

    @Mock
    private SSPrincipal principal;

    @Mock
    private EtatApplication etatApplication;

    @Mock
    @RuntimeService
    private EtatApplicationService etatApplicationService;

    @Before
    public void setUp() {
        delegate = new NotificationDelegate(session);

        when(session.getPrincipal()).thenReturn(principal);

        when(etatApplicationService.getEtatApplicationDocument(session)).thenReturn(etatApplication);
    }

    @Test
    public void shouldNotify() {
        EnvoyerNotificationRequest request = new EnvoyerNotificationRequest();

        when(etatApplication.getRestrictionAcces()).thenReturn(false);

        EnvoyerNotificationResponse response = delegate.notify(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatutTraitement()).isEqualTo(OK);
    }

    @Test
    public void notifyShouldReturnKoResponse() {
        EnvoyerNotificationRequest request = new EnvoyerNotificationRequest();

        when(etatApplication.getRestrictionAcces()).thenReturn(true);
        when(principal.isMemberOf(ADMIN_ACCESS_UNRESTRICTED)).thenReturn(false);

        EnvoyerNotificationResponse response = delegate.notify(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatutTraitement()).isEqualTo(KO);
    }
}
