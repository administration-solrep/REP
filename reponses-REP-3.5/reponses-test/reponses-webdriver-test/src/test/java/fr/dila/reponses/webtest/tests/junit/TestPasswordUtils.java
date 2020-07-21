package fr.dila.reponses.webtest.tests.junit;
import junit.framework.Assert;

import org.junit.Test;

import fr.dila.reponses.webtest.utils.PasswordUtils;

public class TestPasswordUtils {

	
	/**
	 * Récupère le mot de passe
	 */
	@Test
	public void testGetPassword(){
		String mailContent = "Bienvenue dans l'application Réponses\n" +
				"\n"+
				"- login : Tomato31\n" + 
				"- mot de passe : ooQJVaPV";
		String password = PasswordUtils.getPassword(mailContent);
		Assert.assertEquals( "ooQJVaPV", password);
	}

}
