package fr.dila.reponses.core.flux;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.flux.HasInfoFlux;
import fr.dila.reponses.core.recherche.RechercheTestCase;

public class TestHasInfosFlux extends RechercheTestCase {

	private static final Log	LOG	= LogFactory.getLog(TestHasInfosFlux.class);

	@Override
	public void setUp() throws Exception {
		LOG.debug("ENTER SETUP");
		super.setUp();
		assertNotNull(question1);
		LOG.debug("EXIT SETUP");
	}

	private HasInfoFlux getFluxAdapter() {
		HasInfoFlux fluxAdapter = question1.getDocument().getAdapter(HasInfoFlux.class);
		return fluxAdapter;
	}

	public void testAdapter() {
		LOG.info("Test adapt to HasInfoFlux");
		assertNotNull(getFluxAdapter());
	}

	public void testIsUrgent() {
		LOG.info("Test isUrgent");
		HasInfoFlux fluxAdapter = getFluxAdapter();
		assertFalse(fluxAdapter.isUrgent());
		question1.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_RAPPELE, Calendar.getInstance(), "10");
		assertTrue(fluxAdapter.isUrgent());
	}

	public void testIsSignale() {
		LOG.info("Test isSignale");
		HasInfoFlux fluxAdapter = getFluxAdapter();
		assertFalse(fluxAdapter.isSignale());
	}

	public void testIsRenouvelle() {
		LOG.info("Test isRenouvelle");
		HasInfoFlux fluxAdapter = getFluxAdapter();
		assertFalse(fluxAdapter.isRenouvelle());
	}

}
