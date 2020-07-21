package fr.dila.reponses.core.service;

import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.service.STAlertService;

public class TestReponseAlertSchedulerService extends ReponsesRepositoryTestCase {

	public void testService() {
		STAlertService alertService = ReponsesServiceLocator.getAlertService();
		assertNotNull(alertService);
		assertTrue(alertService instanceof ReponsesAlertServiceImpl);
	}
	
}
