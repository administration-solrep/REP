package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;

import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.constant.ReponsesExceptionConstants;
import fr.dila.reponses.api.constant.ReponsesStatsEventConstants;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;

/**
 * Test du service de configuration de l'application.
 * 
 * 
 */
public class TestReponsesExportService extends ReponsesRepositoryTestCase {
		
	private ReponsesExportService exportService;
	
	private String userWorkspacePath;
	
	private SSPrincipal principal;
	
	@Override
    protected void deployRepositoryContrib() throws Exception {     
        super.deployRepositoryContrib();
        deployBundle("org.nuxeo.ecm.platform.userworkspace.types");
        
        deployBundle("org.nuxeo.ecm.platform.userworkspace.core");
    }
	
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        openSession();
        
     // Vérifie la présence du service
        exportService = ReponsesServiceLocator.getReponsesExportService();
        assertNotNull(exportService);
        UserWorkspaceService userWorkspaceService = STServiceLocator.getUserWorkspaceService();
        assertNotNull(userWorkspaceService);
        userWorkspacePath = userWorkspaceService.getCurrentUserPersonalWorkspace(session, null).getPathAsString();
        assertNotNull(userWorkspacePath);
        principal = new SSPrincipalImpl(session.getPrincipal().getName(), false, true, false);
        assertNotNull(principal);
        assertNotNull(session);
        
        closeSession();
    }

    public void testGetOrCreateExportStatRootDoc() throws ClientException {
        openSession();        
        
        DocumentModel statRoot = exportService.getOrCreateExportStatRootDoc(session, userWorkspacePath);
        assertNotNull(statRoot);
        String id = statRoot.getId();
        // On vérifie que la méthode ne recréé pas un répertoire déjà existant 
        statRoot = exportService.getOrCreateExportStatRootDoc(session, userWorkspacePath);
        assertEquals(id, statRoot.getId());
        
        closeSession();
    }

    public void testGetOrCreateExportStatRootPath() throws ClientException {
        openSession();
        
        String statRootPath = exportService.getOrCreateExportStatRootPath(session, userWorkspacePath);
        assertNotNull(statRootPath);
        // On vérifie que la méthode ne créé pas un deuxième document si un existe
        String statRootPath2 = exportService.getOrCreateExportStatRootPath(session, userWorkspacePath);
        assertEquals(statRootPath, statRootPath2);
        
        closeSession();
    }    

    public void testCreationExportStatDocument() throws ClientException {
        openSession();        
        
        // On tente une récupération du document qui n'existe pas => doit retourner null
        DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertNull(exportStatDoc);
        // On appelle la méthode de récupération ou création => le document doit exister après
        exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
        assertNotNull(exportStatDoc);
        // On garde l'id du document
        String id = exportStatDoc.getId();
        // On le récupère pour vérifier qu'il n'est pas créé une deuxième fois
        exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
        assertEquals(id, exportStatDoc.getId());
        // On vérifie que la méthode de récupération retourne bien le document
        exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertEquals(id, exportStatDoc.getId());
        
        closeSession();
    }
    
    public void testFlagInitExportStatForUser() throws ClientException {
        openSession();
        
        DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertNull(exportStatDoc);
        // On créé le document
        exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
        assertNotNull(exportStatDoc);
        assertEquals("done", exportStatDoc.getCurrentLifeCycleState());
        // On place le flag
        boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
        assertTrue(result);        
        exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
        assertEquals(STLifeCycleConstant.EXPORTING_STATE, exportStatDoc.getCurrentLifeCycleState());
        // On place le flag de nouveau => faux car le flag est déjà présent
        result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
        assertFalse(result);
        
        closeSession();
    }
    
    public void testGetExportStatHorodatageRequest() throws ClientException {
        openSession();
        
        // La date d'horodatage est mise à jour en même temps que le flag de début d'export
        // On vérifie donc que c'est bien le cas
        Calendar date = Calendar.getInstance();
        String nowString = DateUtil.formatWithHour(date.getTime());
        // On place le flag (vérifie en même temps que le document d'export est créé s'il n'existe pas)
        boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
        assertTrue(result);
        DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        ExportDocument exportStat = exportStatDoc.getAdapter(ExportDocument.class);
        String dateRequestString = DateUtil.formatWithHour(exportStat.getDateRequest().getTime());        
        assertEquals(nowString, dateRequestString);
        
        // On vérifie avec la méthode présente dans le service
        String dateRequestByService = exportService.getExportStatHorodatageRequest(session, principal, userWorkspacePath);        
        assertEquals(dateRequestString, dateRequestByService);
        
        closeSession();
    }
    
    public void testFlagEndExportStatForUser() throws ClientException {
        openSession();
        
        // On place le flag
        boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
        assertTrue(result);        
        DocumentModel exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
        // On vérifie que le document est bien à l'état d'export en cours
        assertEquals(STLifeCycleConstant.EXPORTING_STATE, exportStatDoc.getCurrentLifeCycleState());
        String id = exportStatDoc.getId();
        
        // on retire le flag        
        exportService.flagEndExportStatForUser(session, principal, userWorkspacePath);
        exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);
        // On vérifie que le document récupéré est bien le même et que le flag est desactivé
        assertNotNull(exportStatDoc);
        assertEquals(id, exportStatDoc.getId());
        assertEquals("done", exportStatDoc.getCurrentLifeCycleState());
        
        closeSession();
    }
    
    public void testIsCurrentlyExportingStat() throws ClientException {
        openSession();
        // Le document n'existe pas, il ne peut pas être en cours d'export
        assertFalse(exportService.isCurrentlyExportingStat(session, principal, userWorkspacePath));
        // On place le flag
        boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
        assertTrue(result);        
        assertTrue(exportService.isCurrentlyExportingStat(session, principal, userWorkspacePath));
        
        // on retire le flag        
        exportService.flagEndExportStatForUser(session, principal, userWorkspacePath);
        assertFalse(exportService.isCurrentlyExportingStat(session, principal, userWorkspacePath));
        
        closeSession();
    }
    
    public void testDeleteExportStatDoc() throws ClientException {
        openSession();
        
        Calendar dateLimit = Calendar.getInstance();
        // On retire 1 jour à la date limite
        dateLimit.add(Calendar.DAY_OF_MONTH, -1);
        
        int nbSuppression = exportService.removeOldExportStat(session, dateLimit);
        // Pas de document existant, donc suppression à 0
        assertEquals(0, nbSuppression);
        
        // On appelle la méthode de récupération ou création => le document doit exister après
        DocumentModel exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);        
        assertNotNull(exportStatDoc);
        
        // On teste le cas où la dateRequest is null
        nbSuppression = exportService.removeOldExportStat(session, dateLimit);
        // document existant, donc suppression à 1
        assertEquals(1, nbSuppression);
        exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertNull(exportStatDoc);
        
        // On recréé le document
        // On appelle la méthode de récupération ou création => le document doit exister après
        exportStatDoc = exportService.getOrCreateExportStatDoc(session, principal, userWorkspacePath);        
        assertNotNull(exportStatDoc);
        
        // On simule un export
        boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
        assertTrue(result);
        exportService.flagEndExportStatForUser(session, principal, userWorkspacePath);
        assertFalse(exportService.isCurrentlyExportingStat(session, principal, userWorkspacePath));
        
        nbSuppression = exportService.removeOldExportStat(session, dateLimit);
        // document existant, mais trop récent donc suppression à 0
        assertEquals(0, nbSuppression);
        
        exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertNotNull(exportStatDoc);
        ExportDocument exportStat = exportStatDoc.getAdapter(ExportDocument.class);
        // On change la dateRequest pour le test 
        Calendar oldDate = Calendar.getInstance();
        oldDate.add(Calendar.YEAR, -1);
        exportStat.setDateRequest(oldDate);
        exportStat.save(session);
        session.save();
        
        nbSuppression = exportService.removeOldExportStat(session, dateLimit);
        // document existant, et ancien donc suppression à 1
        assertEquals(1, nbSuppression);
        exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertNull(exportStatDoc);
        
        closeSession();
    }
    
    public void testExportStatAlone() throws Exception {
        openSession();
        ArrayList<String> formats = new ArrayList<String>();
        Map<String, String> reportsMultiExportMap = new HashMap<String, String>();
        Map<String, String> reportsNamesMap = new HashMap<String, String>();
        Map<String, String> reportsTitlesMap = new HashMap<String, String>();
        formats.add(ReponsesStatsEventConstants.FORMAT_XLS_VALUE);
        formats.add(ReponsesStatsEventConstants.FORMAT_PDF_VALUE);

        // reportsMulti size = 0 reportsNames size = 0 reportsTitles size = 0

        // On tente une récupération du document qui n'existe pas => doit retourner null
        DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertNull(exportStatDoc);
        
        // Test si reportsMapMinistere is empty et flag non posé (implique dateRequest non renseignée et exception)
        try {
            exportService.exportStat(session, userWorkspacePath, principal, reportsMultiExportMap, reportsNamesMap, reportsTitlesMap, formats);
        } catch(Exception exc) {
            assertTrue(exc instanceof ReponsesException);
            assertEquals(ReponsesExceptionConstants.EMPTY_REPORTS_EXC, exc.getMessage());
        }
        getDocAndAssertTitleAndContentIsNull();
        
        // reportsMulti size = 1 reportsNames size = 0 reportsTitles size = 0
        reportsMultiExportMap.put("test", "MIN");
        try {
            exportService.exportStat(session, userWorkspacePath, principal, reportsMultiExportMap, reportsNamesMap, reportsTitlesMap, formats);
        } catch(Exception exc) {
            assertTrue(exc instanceof ReponsesException);
            assertEquals(ReponsesExceptionConstants.EMPTY_REPORTS_EXC, exc.getMessage());
        }
        
        // reportsNames size = 1 reportsNames size = 1 reportsTitles size = 0
        reportsNamesMap.put("test", "Archibald");
        try {
            exportService.exportStat(session, userWorkspacePath, principal, reportsMultiExportMap, reportsNamesMap, reportsTitlesMap, formats);
        } catch(Exception exc) {
            assertTrue(exc instanceof ReponsesException);
            assertEquals(ReponsesExceptionConstants.EMPTY_REPORTS_EXC, exc.getMessage());
        }

        // Test si maps non vides mais tailles différentes entres elles, flag non posé
            // reportsNames size = 1 reportsNames size = 2 reportsTitles size = 1
        reportsNamesMap.put("test1", "Tintin");
        reportsTitlesMap.put("test", "Capitaine");
        try {
            exportService.exportStat(session, userWorkspacePath, principal, reportsMultiExportMap, reportsNamesMap, reportsTitlesMap, formats);
        } catch(Exception exc) {
            assertTrue(exc instanceof ReponsesException);
            assertEquals(ReponsesExceptionConstants.DIFFERENTS_SIZES_REPORTS_EXC, exc.getMessage());
        }
        getDocAndAssertTitleAndContentIsNull();
        
            // reportsNames size = 1 reportsNames size = 1 reportsTitles size = 2
        reportsNamesMap.remove("test1");
        reportsTitlesMap.put("test1", "Reporter");
        try {
            exportService.exportStat(session, userWorkspacePath, principal, reportsMultiExportMap, reportsNamesMap, reportsTitlesMap, formats);
        } catch(Exception exc) {
            assertTrue(exc instanceof ReponsesException);
            assertEquals(ReponsesExceptionConstants.DIFFERENTS_SIZES_REPORTS_EXC, exc.getMessage());
        }
        getDocAndAssertTitleAndContentIsNull();

            // reportsNames size = 2 reportsNames size = 1 reportsTitles size = 1
        reportsTitlesMap.remove("test1");
        reportsMultiExportMap.put("test1", "DIR");
        try {
            exportService.exportStat(session, userWorkspacePath, principal, reportsMultiExportMap, reportsNamesMap, reportsTitlesMap, formats);
        } catch(Exception exc) {
            assertTrue(exc instanceof ReponsesException);
            assertEquals(ReponsesExceptionConstants.DIFFERENTS_SIZES_REPORTS_EXC, exc.getMessage());
        }
        getDocAndAssertTitleAndContentIsNull();

        closeSession();
    }
    
    private void getDocAndAssertTitleAndContentIsNull () throws ClientException {
        // récupération du document 
        DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertNotNull(exportStatDoc);
        ExportDocument exportStat = exportStatDoc.getAdapter(ExportDocument.class);
        assertNull(exportStat.getTitle());
        assertNull(exportStat.getFileContent());
    }
    
    public void testExportStatWithFlag() throws ClientException {
        openSession();
        ArrayList<String> formats = new ArrayList<String>();
        Map<String, String> reportsMultiExportMap = new HashMap<String, String>();
        Map<String, String> reportsNamesMap = new HashMap<String, String>();
        Map<String, String> reportsTitlesMap = new HashMap<String, String>();
        formats.add(ReponsesStatsEventConstants.FORMAT_XLS_VALUE);
        formats.add(ReponsesStatsEventConstants.FORMAT_PDF_VALUE);
        
        // On tente une récupération du document qui n'existe pas => doit retourner null
        DocumentModel exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertNull(exportStatDoc);
        
        // On place le flag (créé le document d'export en même temps)
        boolean result = exportService.flagInitExportStatForUser(session, principal, userWorkspacePath);
        assertTrue(result);
        
        // récupération du document qui existe à présent
        exportStatDoc = exportService.getExportStatDocForUser(session, principal, userWorkspacePath);
        assertNotNull(exportStatDoc);
        
        // Test si reportsMapMinistere is empty et flag posé (implique dateRequest renseignée)
        try {
            exportService.exportStat(session, userWorkspacePath, principal, reportsMultiExportMap, reportsNamesMap, reportsTitlesMap, formats);
        } catch(Exception exc) {
            assertTrue(exc instanceof ReponsesException);
            assertEquals(ReponsesExceptionConstants.EMPTY_REPORTS_EXC, exc.getMessage());
        }        
        // on retire le flag        
        exportService.flagEndExportStatForUser(session, principal, userWorkspacePath);
        
        getDocAndAssertTitleAndContentIsNull();               
        
        closeSession();
    }
}
