package fr.dila.reponses.webtest.utils;

import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import fr.dila.reponses.webtest.helper.UrlHelper;

public class AutomationUtils {

	private static final String	PRM_VALUE							= "prm:value";
	private static final String	CHEMIN_PARAM_MAIL_ADMIN				= "/case-management/workspaces/admin/parametre/adresse-mail-administrateur-application";
	private static final String	PARAMETER_NAME						= "name";
	private static final String	OPERATION_NOTIFICATION_SENDEVENT	= "Notification.SendEvent";
	private static final String	LOGIN								= "Administrator";
	private static final String	PASSWORD							= "4ju7Ix662Eo1d77u";
	private static final String	BATCHNAME_ALERTE					= "sendAlertEvent";

	protected AutomationUtils() {
	}

	public static void sendBatchAlerte() throws Exception {
		sendBatch(BATCHNAME_ALERTE);
	}

	/**
	 * Exécuter un batch
	 * 
	 * @param batchname
	 *            le nom du batch
	 * @throws Exception
	 */
	private static void sendBatch(String batchname) throws Exception {
		HttpAutomationClient client = new HttpAutomationClient(UrlHelper.getInstance().getAutomationUrl());
		Session session = client.getSession(LOGIN, PASSWORD);
		session.newRequest(OPERATION_NOTIFICATION_SENDEVENT).set(PARAMETER_NAME, batchname).execute();
		client.shutdown();
	}

	/**
	 * Changement du mail de l'administrateur technique par le nuxéo shell
	 * 
	 * @param nouvelleAdresse
	 * @throws Exception
	 */
	public static void changerMailAdministrateurTechnique(String nouvelleAdresse) throws Exception {
		HttpAutomationClient client = new HttpAutomationClient(UrlHelper.getInstance().getAutomationUrl());
		Session session = client.getSession(LOGIN, PASSWORD);
		PropertyMap map = new PropertyMap();
		map.set(PRM_VALUE, nouvelleAdresse);
		session.newRequest(DocumentService.UpdateDocument).setInput(new PathRef(CHEMIN_PARAM_MAIL_ADMIN))
				.set("properties", map).execute();
		client.shutdown();
	}
}
