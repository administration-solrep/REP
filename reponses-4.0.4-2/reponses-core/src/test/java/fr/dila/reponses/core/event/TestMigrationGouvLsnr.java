package fr.dila.reponses.core.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Lists;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.mailbox.MailboxImpl;
import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingStatusEnum;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.ReponsesMigrationService;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.service.organigramme.OrganigrammeNodeTimbreDTOImpl;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ ReponseFeature.class, SolonMockitoFeature.class })
public class TestMigrationGouvLsnr {
    @Inject
    protected MigrationGouvernementListener listener;

    @Inject
    protected CoreSession session;

    @Mock
    @RuntimeService
    protected STMinisteresService ministeresService;

    @Mock
    @RuntimeService
    protected STGouvernementService gouvService;

    @Mock
    @RuntimeService
    protected EtatApplicationService etatApplicationService;

    @Mock
    @RuntimeService
    protected STPostesService postesService;

    @Mock
    @RuntimeService
    protected MailboxPosteService mailboxService;

    @Mock
    @RuntimeService
    protected ReponsesMigrationService migrationService;

    @Mock
    @RuntimeService
    protected FeuilleRouteModelService fdrService;

    @Mock
    @RuntimeService
    protected UpdateTimbreService updateTimbreService;

    @Mock
    @RuntimeService
    protected OrganigrammeService orgaService;

    DocumentModel loggerDoc;

    public void configureEvent() {
        Mockito.when(etatApplicationService.isApplicationTechnicallyRestricted()).thenReturn(false);

        Map<String, OrganigrammeNodeTimbreDTO> newGouv = buildNewGouv();
        Map<String, String> timbreMap = newGouv
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getId()));

        loggerDoc = session.createDocumentModel(ReponsesLoggingConstant.REPONSES_LOGGING_DOCUMENT_TYPE);
        loggerDoc = session.createDocument(loggerDoc);

        listener.mapDto = newGouv;
        listener.newTimbre = timbreMap;
        listener.newTimbreDeactivateEntity = "deactivate_entity";
        listener.newTimbreUnchangeEntity = "unchanged_entity";
        listener.nextGouvernement = "new";
        listener.currentGouvernement = "old";
        listener.reponsesLoggingId = loggerDoc.getId();

        listener.feuilleRouteModelService = fdrService;
        listener.gouvernementService = gouvService;
        listener.mailboxPosteService = mailboxService;
        listener.migrationService = migrationService;
        listener.ministeresService = ministeresService;
        listener.organigrammeService = orgaService;
        listener.updateTimbreService = updateTimbreService;
        listener.postesService = postesService;
    }

    private PosteNode buildFakePoste(String id, boolean bdc) {
        PosteNodeImpl poste = new PosteNodeImpl();
        poste.setId(id);
        poste.setPosteBdc(bdc);
        poste.setLabel("BDC " + id);

        return poste;
    }

    private UniteStructurelleNode buildFakeUnite(String id) {
        UniteStructurelleNodeImpl unite = new UniteStructurelleNodeImpl();
        unite.setId(id);
        unite.setLabel("Unité " + id);
        unite.setSubPostesList(Lists.newArrayList(buildFakePoste(id + "-1", true)));

        return unite;
    }

    private void configureMinistereMigration() {
        Mockito
            .when(postesService.getPosteBdcInEntite(Mockito.anyString()))
            .thenReturn(buildFakePoste("posteTest", false));
        Mockito.when(postesService.getPosteBdcListInEntite(Mockito.anyString())).thenReturn(Lists.newArrayList());

        Mockito.when(ministeresService.getEntiteNode("1")).thenReturn(buildEntite("1"));
        Mockito.when(ministeresService.getEntiteNode("2")).thenReturn(buildEntite("2"));
        Mockito.when(ministeresService.getEntiteNode("3")).thenReturn(buildEntite("3"));
        Mockito.when(ministeresService.getEntiteNode("4")).thenReturn(buildEntite("4"));
        Mockito
            .when(mailboxService.getOrCreateMailboxPosteNotUnrestricted(Mockito.any(), Mockito.anyString()))
            .thenReturn(buildFakeMailBox());
    }

    private Mailbox buildFakeMailBox() {
        return new MailboxImpl(session.createDocumentModel(MailboxConstants.MAILBOX_DOCUMENT_TYPE));
    }

    private EntiteNode buildEntite(String id) {
        EntiteNodeImpl entite = new EntiteNodeImpl();
        Map<String, OrganigrammeNodeTimbreDTO> mapData = buildNewGouv();

        entite.setNorMinistere("ECO");
        entite.setSubPostesList(Lists.newArrayList(buildFakePoste(id + "-1", false)));
        entite.setSubUnitesStructurellesList(Lists.newArrayList(buildFakeUnite(id + "-2")));

        entite.setId(id);

        if (mapData.containsKey(id)) {
            entite.setLabel(mapData.get(id).getLabel());
        }

        return entite;
    }

    private Map<String, OrganigrammeNodeTimbreDTO> buildNewGouv() {
        Map<String, OrganigrammeNodeTimbreDTO> mapGouv = new HashMap<>();
        OrganigrammeNodeTimbreDTO min1 = new OrganigrammeNodeTimbreDTOImpl(40L, 30L, 20L, 10L);
        min1.setType(OrganigrammeType.MINISTERE);
        min1.setBreakingSeal(true);
        min1.setLabel("Migration ministère 1");
        min1.setLabelNextTimbre("Ministère migré");
        min1.setMigratingDossiersClos(true);
        min1.setOrder(1);
        min1.setId("1");
        mapGouv.put("1", min1);

        OrganigrammeNodeTimbreDTO min2 = new OrganigrammeNodeTimbreDTOImpl(40L, 30L, 20L, 10L);
        min2.setType(OrganigrammeType.MINISTERE);
        min2.setBreakingSeal(false);
        min2.setLabel("Migration ministère 2");
        min2.setLabelNextTimbre("Ministère migré 2");
        min2.setMigratingDossiersClos(false);
        min2.setOrder(2);
        min2.setId("2");
        mapGouv.put("2", min2);

        OrganigrammeNodeTimbreDTO min3 = new OrganigrammeNodeTimbreDTOImpl(40L, 30L, 20L, 10L);
        min3.setType(OrganigrammeType.MINISTERE);
        min3.setBreakingSeal(false);
        min3.setLabel("Migration ministère 3");
        min3.setLabelNextTimbre("Pas de migration ministère");
        min3.setMigratingDossiersClos(false);
        min3.setOrder(3);
        min3.setId("unchanged_entity");
        mapGouv.put("3", min3);

        OrganigrammeNodeTimbreDTO min4 = new OrganigrammeNodeTimbreDTOImpl(0L, 0L, 0L, 0L);
        min4.setType(OrganigrammeType.MINISTERE);
        min4.setBreakingSeal(false);
        min4.setLabel("Migration ministère 4");
        min4.setLabelNextTimbre("Migration ministère désactivé");
        min4.setMigratingDossiersClos(false);
        min4.setOrder(4);
        min4.setId("deactivate_entity");
        mapGouv.put("4", min4);

        return mapGouv;
    }

    @Test
    public void testMigrateEntiteOK() {
        configureEvent();
        configureMinistereMigration();
        listener.processMigration(session);

        ReponsesLogging logger = loggerDoc.getAdapter(ReponsesLogging.class);

        assertNotNull(logger);
        assertEquals(ReponsesLoggingStatusEnum.SUCCESS, logger.getStatus());
        assertMigrationOKResponse(logger.getReponsesLoggingLines());
    }

    @Test
    public void testMigrateEntiteFail() {
        configureEvent();
        configureMinistereMigration();

        Mockito
            .doThrow(Exception.class)
            .when(fdrService)
            .migrateMinistereFeuilleRouteModel(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
            );
        listener.processMigration(session);

        ReponsesLogging logger = loggerDoc.getAdapter(ReponsesLogging.class);

        assertNotNull(logger);
        assertEquals(ReponsesLoggingStatusEnum.FAILURE, logger.getStatus());
        assertMigrationFailResponse(logger.getReponsesLoggingLines());
    }

    public void assertMigrationOKResponse(List<String> lstLogLines) {
        assertNotNull(lstLogLines);
        assertEquals(4, lstLogLines.size());

        assertAllMinistereMigrated(lstLogLines);

        DocumentModel doc1 = session.getDocument(new IdRef(lstLogLines.get(0)));
        ReponsesLoggingLine logLine1 = doc1.getAdapter(ReponsesLoggingLine.class);

        assertNotNull(logLine1.getFullLog());
        assertEquals(5, logLine1.getFullLog().size());
        assertEquals(
            "Changement de gouvernement : [Migration ministère 1] vers [Migration ministère 1]",
            logLine1.getFullLog().get(0)
        );
        assertEquals("Migration des modèles de feuille de route", logLine1.getFullLog().get(1));
        assertEquals("Fin de migration des modèles de feuille de route", logLine1.getFullLog().get(2));
        assertEquals(
            "Migration des dossiers impossible pas de poste bdc dans Migration ministère 1",
            logLine1.getFullLog().get(3)
        );
        assertEquals(
            "Fin changement de gouvernement : [Migration ministère 1] vers [Migration ministère 1]",
            logLine1.getFullLog().get(4)
        );
    }

    public void assertMigrationFailResponse(List<String> lstLogLines) {
        assertNotNull(lstLogLines);
        assertEquals(4, lstLogLines.size());
        assertAllMinistereMigrated(lstLogLines);
    }

    private void assertAllMinistereMigrated(List<String> lstLogLines) {
        DocumentModel doc1 = session.getDocument(new IdRef(lstLogLines.get(0)));
        ReponsesLoggingLine logLine1 = doc1.getAdapter(ReponsesLoggingLine.class);
        assertEquals(
            "Migration de l'organigramme : [Migration ministère 1] vers [Migration ministère 1]",
            logLine1.getMessage()
        );
        assertNotNull(logLine1.getFullLog());

        DocumentModel doc2 = session.getDocument(new IdRef(lstLogLines.get(1)));
        ReponsesLoggingLine logLine2 = doc2.getAdapter(ReponsesLoggingLine.class);
        assertEquals(
            "Migration de l'organigramme : [Migration ministère 2] vers [Migration ministère 2]",
            logLine2.getMessage()
        );

        DocumentModel doc3 = session.getDocument(new IdRef(lstLogLines.get(2)));
        ReponsesLoggingLine logLine3 = doc3.getAdapter(ReponsesLoggingLine.class);
        assertEquals(
            "Migration de l'organigramme : [Migration ministère 3] vers [Migration ministère 3]",
            logLine3.getMessage()
        );

        DocumentModel doc4 = session.getDocument(new IdRef(lstLogLines.get(3)));
        ReponsesLoggingLine logLine4 = doc4.getAdapter(ReponsesLoggingLine.class);
        assertEquals(
            "Migration de l'organigramme : désactivation du ministère [Migration ministère 4]",
            logLine4.getMessage()
        );
    }
}
