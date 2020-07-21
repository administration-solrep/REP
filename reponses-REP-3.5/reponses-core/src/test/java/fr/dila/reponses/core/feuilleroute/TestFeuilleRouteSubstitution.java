package fr.dila.reponses.core.feuilleroute;

import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STConstant;

/**
 * Test de la substitution de feuilles de routes.
 * 
 * @author jtremeaux
 */
public class TestFeuilleRouteSubstitution extends ReponsesRepositoryTestCase  {
    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    private FeuilleRouteService feuilleRouteService;

    private DocumentRoute defaultRoute;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        assertNotNull(feuilleRouteService);
    }

    /**
     * Vérifie la substitution de feuilles de routes.
     * 
     * @throws Exception
     */
    public void testSubstitution() throws Exception {
    	openSession();
    	
    	// Crée la feuille de route par défaut
    	defaultRoute = createDefaultRoute(session);
    	DocumentModel routeDoc = defaultRoute.getDocument();
        
    	// Valide la feuille de route
        validateRoute(defaultRoute);
        
        // Cree un dossier
        Dossier dossier = createDossier();
        DocumentModel dossierDoc = dossier.getDocument();
        
        // Démarre la feuille de route associée au dossier
        DocumentRoute routeInstance = getDossierDistributionService().startDefaultRoute(session, dossier);
        DocumentModel routeInstanceDoc = routeInstance.getDocument();
        assertEquals("Instance de feuille de route initiale", routeInstanceDoc.getId(), dossier.getLastDocumentRoute());

        // Récupère le CaseLink de user1
        closeSession();
        session = openSessionAs(getUserManager().getPrincipal(user1));
            
        Mailbox userMailbox = getPersonalMailbox(user1);
        List<CaseLink> links = getDistributionService().getReceivedCaseLinks(session, userMailbox, 0, 0);
        assertEquals(1, links.size());
        
        // Substitue la feuille de route
        closeSession();
        session = openSessionAs(getUserManager().getPrincipal(administrator));
        DocumentModel routeInstance2Doc = getDossierDistributionService().substituerFeuilleRoute(session, dossierDoc, routeInstanceDoc, routeDoc, "substitution");
        assertNotNull(routeInstance2Doc);
        getDossierDistributionService().startRouteAfterSubstitution(session, routeInstanceDoc, routeInstance2Doc, "substitution");
        dossierDoc = session.getDocument(dossierDoc.getRef());
        dossier = dossierDoc.getAdapter(Dossier.class);

        assertEquals("Instance de feuille de route après substitution", routeInstance2Doc.getId(), dossier.getLastDocumentRoute());
        assertNotSame(routeInstanceDoc.getId(), routeInstance2Doc.getId());
        assertNotSame(dossier.getLastDocumentRoute(), routeInstanceDoc.getId());
        
        closeSession();
    }
    
    /**
     * Vérifie l'ajout d'une feuille de route.
     * 
     * @throws Exception
     */
    public void testAjout() throws Exception {
    	openSession();
    	
        // Crée la feuille de route par défaut
        defaultRoute = createDefaultRoute(session);
        DocumentModel routeDoc = defaultRoute.getDocument();
        
        // Valide la feuille de route
        validateRoute(defaultRoute);
        
        // Cree un dossier
        Dossier dossier = createDossier();
        DocumentModel dossierDoc = dossier.getDocument();
        
        // Ajoute la feuille de route
        closeSession();
        session = openSessionAs(getUserManager().getPrincipal(administrator));
        DocumentModel routeInstance2Doc = getDossierDistributionService().substituerFeuilleRoute(session, dossierDoc, null, routeDoc, "substitution");
        assertNotNull(routeInstance2Doc);
        getDossierDistributionService().startRouteAfterSubstitution(session, null, routeInstance2Doc, "substitution");
        dossierDoc = session.getDocument(dossierDoc.getRef());
        dossier = dossierDoc.getAdapter(Dossier.class);

        assertEquals("Instance de feuille de route après substitution", routeInstance2Doc.getId(), dossier.getLastDocumentRoute());
        assertNotSame(null, routeInstance2Doc.getId());
        assertNotSame(dossier.getLastDocumentRoute(), null);
        
        closeSession();
    }

    /**
     * Retourne la route par défaut (crée ses étapes par effet de bord).
     * 
     * @param session Session
     * @return Route par défaut
     * @throws Exception
     */
    private DocumentRoute createDefaultRoute(CoreSession session) throws Exception {
        
        DocumentModel fdrRoot = createOrGetFeuilleRouteModelFolder(session);
        
        DocumentModel route = createDocumentModel(session, DEFAULT_ROUTE_NAME, STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE, fdrRoot.getPathAsString());
        ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
        assertNotNull(feuilleRoute);

        feuilleRoute.setFeuilleRouteDefaut(true);
        session.saveDocument(feuilleRoute.getDocument());
        session.save();

        // Etape 1
        DocumentModel step1 = createDocumentModel(session, "Tâche de validation user1", STConstant.ROUTE_STEP_DOCUMENT_TYPE, route.getPathAsString());
        Mailbox user1Mailbox = getPersonalMailbox(user1);
        step1.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user1Mailbox.getId());
        session.saveDocument(step1);

        session.save();

        return route.getAdapter(DocumentRoute.class);
    }
}
