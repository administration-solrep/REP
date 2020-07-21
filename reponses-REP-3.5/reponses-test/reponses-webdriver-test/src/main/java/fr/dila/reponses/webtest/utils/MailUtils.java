package fr.dila.reponses.webtest.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.dila.reponses.webtest.helper.UrlHelper;
/**
 * Classe utilitaire relative aux mails
 * @author jbt
 *
 */
public class MailUtils {

	/**
	 * Récupère le lien vers le dossier contenu dans un mèl
	 * 
	 * @param mailContent le contenu du mail
	 * 
	 * @return url : l'url contenue dans le mèl
	 */
	public static String getUrl(String mailContent) {
		String pattern = "\"(.+)@view_cm_case";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(mailContent);
		String url = null;
		if (m.find()) {
			url = m.group(1);
		}
		if (url != null ) {
			String appUrl = UrlHelper.getInstance().getReponsesUrl();
			if(!appUrl.endsWith("/")) {
				appUrl += "/";
			}
			url = url.replace("http://i-solrep-apache-01/reponses/", appUrl);
		}
		return url;
	}
}
