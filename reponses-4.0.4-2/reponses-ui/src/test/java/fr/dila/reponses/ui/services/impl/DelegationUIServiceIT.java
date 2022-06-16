package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.core.operation.nxshell.DataInjectionOperation.USER_ADMINSGG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.nuxeo.ecm.core.api.CoreInstance.openCoreSession;

import fr.dila.reponses.api.domain.user.Delegation;
import fr.dila.reponses.core.constant.DelegationConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.ReponseUIFeature;
import fr.dila.reponses.ui.services.DelegationUIService;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.ui.th.model.SpecificContext;
import java.time.LocalDate;
import java.util.Arrays;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ ReponseUIFeature.class })
@Deploy("org.nuxeo.ecm.platform.userworkspace.types")
@Deploy("org.nuxeo.ecm.platform.userworkspace.api")
@Deploy("org.nuxeo.ecm.platform.userworkspace.core")
@Deploy("org.nuxeo.ecm.automation.server:OSGI-INF/pageprovider-contrib.xml")
public class DelegationUIServiceIT {
    @Inject
    private CoreSession mainSession;

    @Inject
    private UserManager um;

    private NuxeoPrincipal adminsgg;

    @Inject
    private DelegationUIService uiService;

    private SpecificContext context;

    private CloseableCoreSession session;

    @Test
    public void testService() {
        assertThat(uiService).isNotNull();
    }

    @Before
    public void setup() {
        context = new SpecificContext();
        adminsgg = um.getPrincipal(USER_ADMINSGG);
        session = openCoreSession(mainSession.getRepositoryName(), adminsgg);
        context.setSession(session);
    }

    @Test
    public void testFetchDelegationsDonnees() {
        // Given
        DocumentModel userDelegationRoot = ReponsesServiceLocator.getDelegationService().getDelegationRoot(session);
        DocumentModel doc = session.createDocumentModel(
            userDelegationRoot.getPathAsString(),
            "user",
            DelegationConstant.DELEGATION_DOCUMENT_TYPE
        );

        Delegation delegation = doc.getAdapter(Delegation.class);
        delegation.setDateDebut(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
        delegation.setDateFin(DateUtil.localDateToGregorianCalendar(LocalDate.now().plusDays(1)));
        delegation.setDestinataireId("user");
        delegation.setSourceId(session.getPrincipal().getName());
        delegation.setProfilListId(Arrays.asList("administrators"));
        session.createDocument(doc);
        session.save();
    }

    @Test
    public void testFetchDelegationsRecues() {
        // Given
        NuxeoPrincipal user = um.getPrincipal("user");
        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(mainSession.getRepositoryName(), user)) {
            DocumentModel userDelegationRoot = ReponsesServiceLocator
                .getDelegationService()
                .getDelegationRoot(userSession);
            DocumentModel doc = userSession.createDocumentModel(
                userDelegationRoot.getPathAsString(),
                USER_ADMINSGG,
                DelegationConstant.DELEGATION_DOCUMENT_TYPE
            );

            Delegation delegation = doc.getAdapter(Delegation.class);
            delegation.setDateDebut(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
            delegation.setDateFin(DateUtil.localDateToGregorianCalendar(LocalDate.now().plusDays(1)));
            delegation.setDestinataireId(USER_ADMINSGG);
            delegation.setSourceId(session.getPrincipal().getName());
            delegation.setProfilListId(Arrays.asList("administrators"));
            userSession.createDocument(doc);
            userSession.save();
        }
    }

    @After
    public void tearDown() {
        session.close();
    }
}
