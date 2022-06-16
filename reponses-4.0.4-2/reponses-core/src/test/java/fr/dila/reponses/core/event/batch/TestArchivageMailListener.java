package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.batch.BatchLoggerModelImpl;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.event.impl.EventImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class, ReponsesServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class TestArchivageMailListener {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    protected ArchiveService archiveService;

    @Mock
    protected STMailService mailService;

    @Mock
    protected ProfileService profilService;

    @Mock
    protected STParametreService paramService;

    @Mock
    private SuiviBatchService suiviBatchService;

    @Mock
    private EventService eventService;

    @Mock
    private CoreSession session;

    private BatchLoggerModel batchLoggerModel = new BatchLoggerModelImpl();

    private ArchivageMailListener listener;
    private Event event;

    private List<STUser> buildMockUsers() {
        List<STUser> lstUsers = new ArrayList<>();

        return lstUsers;
    }

    @Before
    public void setUp() {
        Mockito
            .when(profilService.getUsersFromBaseFunction(STBaseFunctionConstant.ADMIN_FONCTIONNEL_EMAIL_RECEIVER))
            .thenReturn(buildMockUsers());

        PowerMockito.mockStatic(STServiceLocator.class);
        Mockito.when(STServiceLocator.getSuiviBatchService()).thenReturn(suiviBatchService);
        Mockito.when(suiviBatchService.createBatchLogger(Mockito.anyString())).thenReturn(batchLoggerModel);
        Mockito.when(STServiceLocator.getEventService()).thenReturn(eventService);
        Mockito.when(STServiceLocator.getSTMailService()).thenReturn(mailService);
        Mockito.when(STServiceLocator.getProfileService()).thenReturn(profilService);
        Mockito.when(STServiceLocator.getSTParametreService()).thenReturn(paramService);

        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        Mockito.when(ReponsesServiceLocator.getArchiveService()).thenReturn(archiveService);
        Mockito.when(archiveService.countQuestionArchivable(Mockito.any())).thenReturn(5L);

        listener = new ArchivageMailListener();
        event = new EventImpl(ReponsesEventConstant.ARCHIVAGE_MAIL_BATCH_EVENT, new EventContextImpl());
    }

    @Test
    public void testEventNoParameter() {
        Mockito
            .when(
                paramService.getParametreValue(
                    Mockito.any(),
                    Mockito.eq(STParametreConstant.NOTIFICATION_MAIL_DOSSIER_ATTENTE_ELIMINATION_OBJET)
                )
            )
            .thenReturn(null);
        Mockito
            .when(
                paramService.getParametreValue(
                    Mockito.any(),
                    Mockito.eq(STParametreConstant.NOTIFICATION_MAIL_DOSSIER_ATTENTE_ELIMINATION_TEXT)
                )
            )
            .thenReturn(null);

        listener.processEvent(session, event);
        // Vérification de l'envoi de mail de fin avec les valeurs par défaut (sans paramétrage)
        Mockito
            .verify(mailService)
            .sendMailToUserList(
                buildMockUsers(),
                "[Réponses] Dossiers en attente d'élimination",
                "5 dossiers sont en attente d'élimination (fin de DUA atteinte)"
            );
    }

    @Test
    public void testEventWithParameters() {
        Mockito
            .when(
                paramService.getParametreValue(
                    Mockito.any(),
                    Mockito.eq(STParametreConstant.NOTIFICATION_MAIL_DOSSIER_ATTENTE_ELIMINATION_OBJET)
                )
            )
            .thenReturn("Objet");
        Mockito
            .when(
                paramService.getParametreValue(
                    Mockito.any(),
                    Mockito.eq(STParametreConstant.NOTIFICATION_MAIL_DOSSIER_ATTENTE_ELIMINATION_TEXT)
                )
            )
            .thenReturn("${nombreDossiers} dossiers pendant ${dureeDUA}");
        Mockito
            .when(
                paramService.getParametreValue(
                    Mockito.any(),
                    Mockito.eq(STParametreConstant.DELAI_CONSERVATION_DONNEES)
                )
            )
            .thenReturn("6 mois");

        listener.processEvent(session, event);

        // Vérification de l'envoi de mail de fin avec les valeurs paramétrées
        Mockito.verify(mailService).sendMailToUserList(buildMockUsers(), "Objet", "5 dossiers pendant 6 mois");
    }
}
