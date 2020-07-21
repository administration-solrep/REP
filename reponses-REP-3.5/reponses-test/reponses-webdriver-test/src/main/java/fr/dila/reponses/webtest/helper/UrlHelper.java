package fr.dila.reponses.webtest.helper;

public final class UrlHelper extends fr.sword.naiad.commons.webtest.helper.UrlHelper {

	private static final String			QA_APP_URL	= "http://idlv-solrep-rep-qa.lyon-dev2.local:8180/";
	private static final String			DEV_APP_URL	= "http://localhost:8180/";
	
	public static final String			DEFAULT_APP_URL	= QA_APP_URL;
	private static final String			REPONSES_NAME	= "reponses";
	private static final String			AUTOMATION_SITE	= "/site/automation";

	private static volatile UrlHelper	instance;

	public static UrlHelper getInstance() {
		if (instance == null) {
			synchronized (UrlHelper.class) {
				UrlHelper helper = new UrlHelper();
				instance = helper;
			}
		}
		return instance;
	}

	public String getReponsesUrl() {
		String appurl = getAppUrl();
		if (!appurl.endsWith("/")) {
			appurl += "/";
		}
		appurl += REPONSES_NAME;
		return appurl;
	}

	public String getAutomationUrl() {
		String repurl = getReponsesUrl();
		return repurl + AUTOMATION_SITE;
	}

	private UrlHelper() {
		super(DEFAULT_APP_URL);
	}

}
