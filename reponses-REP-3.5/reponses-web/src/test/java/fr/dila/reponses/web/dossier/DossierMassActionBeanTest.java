package fr.dila.reponses.web.dossier;

import static org.mockito.Mockito.*;
import junit.framework.TestCase;

import org.jboss.seam.faces.FacesMessages;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessorBean;

import com.google.inject.internal.Lists;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.core.service.AllotissementServiceImpl;
import fr.dila.reponses.core.service.CorbeilleServiceImpl;
import fr.dila.reponses.core.service.DossierDistributionServiceImpl;
import fr.dila.reponses.core.service.ReponsesArbitrageServiceImpl;
import fr.dila.reponses.web.context.NavigationContextBean;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.dossier.DossierMassActionsBean.MassActionType;
import fr.dila.reponses.web.feuilleroute.DocumentRoutingActionsBean;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
import fr.dila.st.web.dossier.DossierLockActionsBean;
import fr.dila.st.web.lock.STLockActionsBean;

public class DossierMassActionBeanTest extends TestCase {
	DossierMassActionsBean massActionBean;

	public void initialize() {
		massActionBean = new DossierMassActionsBean();
		massActionBean.contentViewActions = mock(ContentViewActions.class);
		massActionBean.dossierDistributionService = mock(DossierDistributionServiceImpl.class);
		massActionBean.arbitrageService = mock(ReponsesArbitrageServiceImpl.class);
		massActionBean.corbeilleService = mock(CorbeilleServiceImpl.class);
		massActionBean.allotissementService = mock(AllotissementServiceImpl.class);
		massActionBean.documentsListsManager = mock(DocumentsListsManagerBean.class);

		massActionBean.setCorbeilleActions(mock(CorbeilleActionsBean.class));
		massActionBean.setDocumentManager(mock(CoreSession.class));
		massActionBean.setDossierActions(mock(DossierActionsBean.class));
		massActionBean.setDossierDistributionActions(mock(DossierDistributionActionsBean.class));
		massActionBean.setDossierLockActions(mock(DossierLockActionsBean.class));
		massActionBean.setFacesMessages(mock(FacesMessages.class));
		massActionBean.setNavigationContext(mock(NavigationContextBean.class));
		massActionBean.setReponseActions(mock(ReponseActionsBean.class));
		massActionBean.setResourcesAccessor(mock(ResourcesAccessorBean.class));
		massActionBean.setRoutingActions(mock(DocumentRoutingActionsBean.class));
		massActionBean.setSsPrincipal(mock(SSPrincipalImpl.class));
		massActionBean.setStLockActions(mock(STLockActionsBean.class));

	}
	
	public Dossier initMassActionCorbeilleTest() throws ClientException{
		//Initialisation du test
		DocumentModel mockDocModel = mock(DocumentModelImpl.class);		
		DossierCommon mockDoc = mock(DossierCommon.class);
		Dossier fakeDossierToValidate = mock(Dossier.class);
				
		massActionBean.massActionType = MassActionType.MASSE_ACTION_AVIS_FAVORABLE;
		when(massActionBean.corbeilleActions.getCurrentView()).thenReturn(
				ReponsesViewConstant.ESPACE_CORBEILLE_VIEW);
		when(massActionBean.documentsListsManager.getWorkingList(anyString())).thenReturn(Lists.newArrayList(mockDocModel));
		when(mockDocModel.getAdapter(DossierCommon.class)).thenReturn(mockDoc);
		when(mockDoc.getDossier(massActionBean.documentManager)).thenReturn(fakeDossierToValidate);
		when(fakeDossierToValidate.getDocument()).thenReturn(mockDocModel);
		
		return fakeDossierToValidate;
	}
	
	public void resetMassActions(){
		massActionBean.dossiersEnErreur.clear();
		massActionBean.dossiersOk.clear();
		massActionBean.infosErreursMassActions.clear();
		massActionBean.doneMassDocument = 0;
	}

	/**
	 * Cette méthode à pour but de tester la gestion des erreurs lors des actions de masse dans la corbeille
	 * 
	 * @throws ClientException
	 */
	@Test
	public void testConfirmMassActionKO() throws ClientException {
		//Initialisation du bean d'action de masse
		initialize();
				
		if (massActionBean != null) {

			Dossier fakeDossierToValidate = initMassActionCorbeilleTest();

			massActionBean.confirmMassAction(0);
			assertEquals("L'initialisation ne prévoit qu'un seul dossier, il devrait être en erreur (car pas de FdR)",1,massActionBean.dossiersEnErreur.size());
			assertEquals("On a traité 100% des dossiers",100,massActionBean.getProgressPercent());
			assertTrue(massActionBean.infosErreursMassActions.contains("feedback.reponses.dossier.error.noRouteFound"));
			
			resetMassActions();
			if (fakeDossierToValidate != null) {
				//On attribue artificiellement une feuille de route
				fakeDossierToValidate.setLastDocumentRoute("1234");
				
				fakeDossierToValidate.setIsArbitrated(false);
				//On lance une exception lors de la récupération 
				when(fakeDossierToValidate.hasFeuilleRoute()).thenReturn(true);
				when(massActionBean.corbeilleService.findUpdatableDossierLinkForDossier(massActionBean.documentManager, fakeDossierToValidate.getDocument())).thenThrow(new ClientException("Erreur manuelle"));
				massActionBean.confirmMassAction(0);
				assertEquals("L'initialisation ne prévoit qu'un seul dossier, il devrait être en erreur (car exception lors de la récupération des dossiers éditables)",1,massActionBean.dossiersEnErreur.size());
				assertEquals("On a traité 100% des dossiers",100,massActionBean.getProgressPercent());
				assertTrue(massActionBean.infosErreursMassActions.contains(DossierMassActionsBean.ERROR_OCCURRED));
			}
			
			
		} else{
			fail("Le bean de mass action n'a pas été initialisé");
		}

	}

}
