package fr.dila.reponses.webtest.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Classe utilitaire relative au mot de passe Réponses
 * @author jgz
 *
 */
public class PasswordUtils {

	/**
	 * Récupère le mot de passe à partir du contenu du mail de bienvenue
	 * @param mailContent le contenu du mail
	 * @return
	 */
	public static String getPassword(String mailContent) {
		String pattern = "mot de passe : (.+)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(mailContent);
		String password = null;
		if (m.find()) {
			password = m.group(1);
		}
		return password;
	}
}
