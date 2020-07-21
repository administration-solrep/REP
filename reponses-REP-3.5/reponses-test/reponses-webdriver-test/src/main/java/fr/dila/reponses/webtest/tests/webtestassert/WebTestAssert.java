package fr.dila.reponses.webtest.tests.webtestassert;

import fr.sword.naiad.commons.webtest.logger.FunctionalLogger;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/**
 * Une classe qui wrappe les assertions pour afficher des messages de check dans les sorties des tests 
 * @author user
 *
 */
public class WebTestAssert {

	/**
	 * Protect constructor since it is a static only class
	 */
	protected WebTestAssert() {
	}

	static public void assertTrue(FunctionalLogger logger, String checkMessage, String conditionFailedMessage, boolean condition) {
		logger.startCheck(checkMessage);
		try{
			Assert.assertTrue(conditionFailedMessage, condition);
		} catch (AssertionFailedError e){
			logger.checkFailed(conditionFailedMessage);
			throw e;
		}
		finally{
			logger.endCheck();
		}
	}

	static public void assertFalse(FunctionalLogger logger, String checkMessage, String conditionFailedMessage, boolean condition) {
		logger.startCheck(checkMessage);
		try{
			Assert.assertFalse(conditionFailedMessage, condition);
		} catch (AssertionFailedError e){
			logger.checkFailed(conditionFailedMessage);
			throw e;
		}
		finally{
			logger.endCheck();
		}
	}

}
