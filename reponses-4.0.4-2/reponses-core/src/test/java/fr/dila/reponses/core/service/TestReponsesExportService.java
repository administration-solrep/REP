package fr.dila.reponses.core.service;

import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.constant.SSExceptionConstants;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test du service de configuration de l'application.
 *
 *
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
@Deploy("org.nuxeo.ecm.platform.userworkspace.types")
@Deploy("org.nuxeo.ecm.platform.userworkspace.core")
public class TestReponsesExportService {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponsesExportService exportService;

    private String userWorkspacePath;

    private SSPrincipal principal;

    @Before
    public void setUp() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Vérifie la présence du service
            UserWorkspaceService userWorkspaceService = STServiceLocator.getUserWorkspaceService();
            Assert.assertNotNull(userWorkspaceService);
            userWorkspacePath = userWorkspaceService.getCurrentUserPersonalWorkspace(session).getPathAsString();
            Assert.assertNotNull(userWorkspacePath);
            principal = new SSPrincipalImpl(session.getPrincipal().getName(), false, true, false);
            Assert.assertNotNull(principal);
        }
    }

    @Test
    public void testGetOrCreateExportStatRootDoc() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel statRoot = exportService.getOrCreateExportStatRootDoc(session, userWorkspacePath);
            Assert.assertNotNull(statRoot);
            String id = statRoot.getId();
            // On vérifie que la méthode ne recréé pas un répertoire déjà existant
            statRoot = exportService.getOrCreateExportStatRootDoc(session, userWorkspacePath);
            Assert.assertEquals(id, statRoot.getId());
        }
    }

    @Test
    public void testGetOrCreateExportStatRootPath() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String statRootPath = exportService.getOrCreateExportStatRootPath(session, userWorkspacePath);
            Assert.assertNotNull(statRootPath);
            // On vérifie que la méthode ne créé pas un deuxième document si un existe
            String statRootPath2 = exportService.getOrCreateExportStatRootPath(session, userWorkspacePath);
            Assert.assertEquals(statRootPath, statRootPath2);
        }
    }

    @Test
    public void testCreationExportStatDocument() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // On tente une récupération du document qui n'existe pas => doit retourner null
            DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            Assert.assertNull(exportStatDoc);
            // On appelle la méthode de récupération ou création =TestReponsesExportService.> le document doit exister après
            exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
            Assert.assertNotNull(exportStatDoc);
            // On garde l'id du document
            String id = exportStatDoc.getId();
            // On le récupère pour vérifier qu'il n'est pas créé une deuxième fois
            exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
            Assert.assertEquals(id, exportStatDoc.getId());
            // On vérifie que la méthode de récupération retourne bien le document
            exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            Assert.assertEquals(id, exportStatDoc.getId());
        }
    }

    @Test
    public void testFlagInitExportStatForUser() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            Assert.assertNull(exportStatDoc);
            // On créé le document
            exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
            Assert.assertNotNull(exportStatDoc);
            Assert.assertEquals("done", exportStatDoc.getCurrentLifeCycleState());
            // On place le flag
            boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
            Assert.assertTrue(result);
            exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
            Assert.assertEquals(STLifeCycleConstant.EXPORTING_STATE, exportStatDoc.getCurrentLifeCycleState());
            // On place le flag de nouveau => faux car le flag est déjà présent
            result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
            Assert.assertFalse(result);
        }
    }

    @Test
    public void testGetExportStatHorodatageRequest() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // La date d'horodatage est mise à jour en même temps que le flag de début d'export
            // On vérifie donc que c'est bien le cas
            String nowString = SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.formatNow();
            // On place le flag (vérifie en même temps que le document d'export est créé s'il n'existe pas)
            boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
            Assert.assertTrue(result);
            DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            ExportDocument exportStat = exportStatDoc.getAdapter(ExportDocument.class);
            String dateRequestString = SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.format(
                exportStat.getDateRequest()
            );
            Assert.assertEquals(nowString, dateRequestString);

            // On vérifie avec la méthode présente dans le service
            String dateRequestByService = exportService.getExportStatHorodatageRequest(
                session,
                principal,
                userWorkspacePath
            );
            Assert.assertEquals(dateRequestString, dateRequestByService);
        }
    }

    @Test
    public void testFlagEndExportStatForUser() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // On place le flag
            boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
            Assert.assertTrue(result);
            DocumentModel exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
            // On vérifie que le document est bien à l'état d'export en cours
            Assert.assertEquals(STLifeCycleConstant.EXPORTING_STATE, exportStatDoc.getCurrentLifeCycleState());
            String id = exportStatDoc.getId();

            // on retire le flag
            exportService.flagEndExportStatForUser(session, principal, userWorkspacePath);
            exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
            // On vérifie que le document récupéré est bien le même et que le flag est desactivé
            Assert.assertNotNull(exportStatDoc);
            Assert.assertEquals(id, exportStatDoc.getId());
            Assert.assertEquals("done", exportStatDoc.getCurrentLifeCycleState());
        }
    }

    @Test
    public void testIsCurrentlyExportingStat() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Le document n'existe pas, il ne peut pas être en cours d'export
            Assert.assertFalse(exportService.isCurrentlyExportingStat(session, principal, userWorkspacePath));
            // On place le flag
            boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
            Assert.assertTrue(result);
            Assert.assertTrue(exportService.isCurrentlyExportingStat(session, principal, userWorkspacePath));

            // on retire le flag
            exportService.flagEndExportStatForUser(session, principal, userWorkspacePath);
            Assert.assertFalse(exportService.isCurrentlyExportingStat(session, principal, userWorkspacePath));
        }
    }

    @Test
    public void testDeleteExportStatDoc() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Calendar dateLimit = Calendar.getInstance();
            // On retire 1 jour à la date limite
            dateLimit.add(Calendar.DAY_OF_MONTH, -1);

            int nbSuppression = exportService.removeOldExportStat(session, dateLimit);
            // Pas de document existant, donc suppression à 0
            Assert.assertEquals(0, nbSuppression);

            // On appelle la méthode de récupération ou création => le document doit exister après
            DocumentModel exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
            Assert.assertNotNull(exportStatDoc);

            // On teste le cas où la dateRequest is null
            nbSuppression = exportService.removeOldExportStat(session, dateLimit);
            // document existant, donc suppression à 1
            Assert.assertEquals(1, nbSuppression);
            exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            Assert.assertNull(exportStatDoc);

            // On recréé le document
            // On appelle la méthode de récupération ou création => le document doit exister après
            exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
            Assert.assertNotNull(exportStatDoc);

            // On simule un export
            boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
            Assert.assertTrue(result);
            exportService.flagEndExportStatForUser(session, principal, userWorkspacePath);
            Assert.assertFalse(exportService.isCurrentlyExportingStat(session, principal, userWorkspacePath));

            nbSuppression = exportService.removeOldExportStat(session, dateLimit);
            // document existant, mais trop récent donc suppression à 0
            Assert.assertEquals(0, nbSuppression);

            exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            Assert.assertNotNull(exportStatDoc);
            ExportDocument exportStat = exportStatDoc.getAdapter(ExportDocument.class);
            // On change la dateRequest pour le test
            Calendar oldDate = Calendar.getInstance();
            oldDate.add(Calendar.YEAR, -1);
            exportStat.setDateRequest(oldDate);
            exportStat.save(session);
            session.save();

            nbSuppression = exportService.removeOldExportStat(session, dateLimit);
            // document existant, et ancien donc suppression à 1
            Assert.assertEquals(1, nbSuppression);
            exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            Assert.assertNull(exportStatDoc);
        }
    }

    @Test
    public void testExportStatAlone() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            List<BirtOutputFormat> formats = new ArrayList<>();
            Map<String, String> reportsMultiExportMap = new HashMap<String, String>();
            Map<String, String> reportsNamesMap = new HashMap<String, String>();
            Map<String, String> reportsTitlesMap = new HashMap<String, String>();
            formats.add(BirtOutputFormat.XLS);
            formats.add(BirtOutputFormat.PDF);

            // reportsMulti size = 0 reportsNames size = 0 reportsTitles size = 0

            // On tente une récupération du document qui n'existe pas => doit retourner null
            DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            Assert.assertNull(exportStatDoc);

            // Test si reportsMapMinistere is empty et flag non posé (implique dateRequest non renseignée et exception)
            try {
                exportService.exportStat(
                    session,
                    userWorkspacePath,
                    principal,
                    reportsMultiExportMap,
                    reportsNamesMap,
                    reportsTitlesMap,
                    formats
                );
            } catch (Exception exc) {
                Assert.assertTrue(exc instanceof ReponsesException);
                Assert.assertEquals(SSExceptionConstants.EMPTY_REPORTS_EXC, exc.getMessage());
            }
            getDocAndAssertTitleAndContentIsNull(session);

            // reportsMulti size = 1 reportsNames size = 0 reportsTitles size = 0
            reportsMultiExportMap.put("test", "MIN");
            try {
                exportService.exportStat(
                    session,
                    userWorkspacePath,
                    principal,
                    reportsMultiExportMap,
                    reportsNamesMap,
                    reportsTitlesMap,
                    formats
                );
            } catch (Exception exc) {
                Assert.assertTrue(exc instanceof ReponsesException);
                Assert.assertEquals(SSExceptionConstants.EMPTY_REPORTS_EXC, exc.getMessage());
            }

            // reportsNames size = 1 reportsNames size = 1 reportsTitles size = 0
            reportsNamesMap.put("test", "Archibald");
            try {
                exportService.exportStat(
                    session,
                    userWorkspacePath,
                    principal,
                    reportsMultiExportMap,
                    reportsNamesMap,
                    reportsTitlesMap,
                    formats
                );
            } catch (Exception exc) {
                Assert.assertTrue(exc instanceof ReponsesException);
                Assert.assertEquals(SSExceptionConstants.EMPTY_REPORTS_EXC, exc.getMessage());
            }

            // Test si maps non vides mais tailles différentes entres elles, flag non posé
            // reportsNames size = 1 reportsNames size = 2 reportsTitles size = 1
            reportsNamesMap.put("test1", "Tintin");
            reportsTitlesMap.put("test", "Capitaine");
            try {
                exportService.exportStat(
                    session,
                    userWorkspacePath,
                    principal,
                    reportsMultiExportMap,
                    reportsNamesMap,
                    reportsTitlesMap,
                    formats
                );
            } catch (Exception exc) {
                Assert.assertTrue(exc instanceof ReponsesException);
                Assert.assertEquals(SSExceptionConstants.DIFFERENTS_SIZES_REPORTS_EXC, exc.getMessage());
            }
            getDocAndAssertTitleAndContentIsNull(session);

            // reportsNames size = 1 reportsNames size = 1 reportsTitles size = 2
            reportsNamesMap.remove("test1");
            reportsTitlesMap.put("test1", "Reporter");
            try {
                exportService.exportStat(
                    session,
                    userWorkspacePath,
                    principal,
                    reportsMultiExportMap,
                    reportsNamesMap,
                    reportsTitlesMap,
                    formats
                );
            } catch (Exception exc) {
                Assert.assertTrue(exc instanceof ReponsesException);
                Assert.assertEquals(SSExceptionConstants.DIFFERENTS_SIZES_REPORTS_EXC, exc.getMessage());
            }
            getDocAndAssertTitleAndContentIsNull(session);

            // reportsNames size = 2 reportsNames size = 1 reportsTitles size = 1
            reportsTitlesMap.remove("test1");
            reportsMultiExportMap.put("test1", "DIR");
            try {
                exportService.exportStat(
                    session,
                    userWorkspacePath,
                    principal,
                    reportsMultiExportMap,
                    reportsNamesMap,
                    reportsTitlesMap,
                    formats
                );
            } catch (Exception exc) {
                Assert.assertTrue(exc instanceof ReponsesException);
                Assert.assertEquals(SSExceptionConstants.DIFFERENTS_SIZES_REPORTS_EXC, exc.getMessage());
            }
            getDocAndAssertTitleAndContentIsNull(session);
        }
    }

    private void getDocAndAssertTitleAndContentIsNull(CoreSession session) {
        // récupération du document
        DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        Assert.assertNotNull(exportStatDoc);
        ExportDocument exportStat = exportStatDoc.getAdapter(ExportDocument.class);
        Assert.assertNull(exportStat.getTitle());
        Assert.assertNull(exportStat.getFileContent());
    }

    @Test
    public void testExportStatWithFlag() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            List<BirtOutputFormat> formats = new ArrayList<>();
            Map<String, String> reportsMultiExportMap = new HashMap<String, String>();
            Map<String, String> reportsNamesMap = new HashMap<String, String>();
            Map<String, String> reportsTitlesMap = new HashMap<String, String>();
            formats.add(BirtOutputFormat.XLS);
            formats.add(BirtOutputFormat.PDF);

            // On tente une récupération du document qui n'existe pas => doit retourner null
            DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            Assert.assertNull(exportStatDoc);

            // On place le flag (créé le document d'export en même temps)
            boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
            Assert.assertTrue(result);

            // récupération du document qui existe à présent
            exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
            Assert.assertNotNull(exportStatDoc);

            // Test si reportsMapMinistere is empty et flag posé (implique dateRequest renseignée)
            try {
                exportService.exportStat(
                    session,
                    userWorkspacePath,
                    principal,
                    reportsMultiExportMap,
                    reportsNamesMap,
                    reportsTitlesMap,
                    formats
                );
            } catch (Exception exc) {
                Assert.assertTrue(exc instanceof ReponsesException);
                Assert.assertEquals(SSExceptionConstants.EMPTY_REPORTS_EXC, exc.getMessage());
            }
            // on retire le flag
            exportService.flagEndExportStatForUser(session, principal, userWorkspacePath);

            getDocAndAssertTitleAndContentIsNull(session);
        }
    }
}
