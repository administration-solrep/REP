package fr.dila.reponses.core.service;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;

/**
 * Tests du service d'arbitrage
 */
public class TestArbitrageService extends ReponsesRepositoryTestCase {

    private ReponsesArbitrageService arbitrageService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
		
        arbitrageService = ReponsesServiceLocator.getReponsesArbitrageService();
        assertNotNull(arbitrageService);
    }

    /**
     * test si on se trouve sur une étape "Pour Arbitrage"
     * @throws Exception 
     */
	public void testIsStepPourArbitrage() throws Exception {
		openSession();
		DocumentModel dossierLinkDoc = createDocument(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, "dossierLink");
		DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
		dossierLink.setRoutingTaskType(VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE);		
		assertTrue(arbitrageService.isStepPourArbitrage(dossierLink));		
		closeSession();
	}

	/**
	 * test le dossier n'est pas bloqué pour arbitrage
	 * @throws Exception 
	 */
	public void testCanAddStepArbitrageSGG() throws Exception {
		openSession();
		
		DocumentModel dossierDoc = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "dossier");
		DocumentModel dossierLinkDoc = createDocument(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, "dossierLink");
		
		// Test avec  doc autre que dossier : 
		assertFalse(arbitrageService.canAddStepArbitrageSGG(dossierLinkDoc));
		
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		dossier.setIsArbitrated(false);
		// Si le dossier n'a pas été arbitré, on peut ajouter une etape pour arbitrage
		assertTrue(arbitrageService.canAddStepArbitrageSGG(dossierDoc));
		
		dossier.setIsArbitrated(true);
		// Si le dossier a été arbitré, on ne peut pas ajouter une etape pour arbitrage
		assertFalse(arbitrageService.canAddStepArbitrageSGG(dossierDoc));
		
		closeSession();
	}

	/**
	 * test de la mise à jour du dossier après un arbitrage
	 * @throws Exception 
	 */
	public void testUpdateDossierAfterArbitrage() throws Exception {
		openSession();
		
		DocumentModel dossierDoc = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "dossier");
		DocumentModel dossierLinkDoc = createDocument(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, "dossierLink");
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		
		// On test avec un document qui n'est pas un dossier
		try {
			arbitrageService.updateDossierAfterArbitrage(session, dossierLinkDoc);
		} catch (ReponsesException exc) {
			assertEquals(STLogEnumImpl.FAIL_GET_DOSSIER_TEC.getText(), exc.getMessage());
		}
		
		assertFalse(dossier.isArbitrated());
		// On test le cas nominal : 
		arbitrageService.updateDossierAfterArbitrage(session, dossierDoc);
		assertTrue(dossier.isArbitrated());
		
		closeSession();
	}
}