package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.ecm.platform.routing.api.DocumentRouteTableElement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.notification.WsNotification;
import fr.dila.reponses.api.service.WsNotificationService;
import fr.dila.reponses.core.notification.WsUtils;
import fr.dila.reponses.rest.client.WSNotificationCaller;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.UnrestrictedQueryRunner;
import fr.dila.st.rest.api.WSNotification;
import fr.dila.st.rest.client.WSProxyFactory;
import fr.dila.st.rest.client.WSProxyFactoryException;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;
import fr.sword.xsd.reponses.NotificationType;
import fr.sword.xsd.reponses.TraitementStatut;

/**
 * Service notification des entités (ministere ou parlementaire) qui sont doté d'un webservice de notification.
 * 
 * @author bgamard
 * 
 */
public class WsNotificationServiceImpl implements WsNotificationService {

	private static final STLogger	LOGGER						= STLogFactory.getLog(WsNotificationServiceImpl.class);

	private static DocumentModel	wsNotificationFolder;
	private static final String		WEBSERVICE_NON_NOTIFIABLE	= "Webservice non notifiable.";

	@Override
	public void notifierEntite(String docId, String webservice, CoreSession session) throws ClientException {
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		DocumentModel doc = null;
		try {
			doc = session.getDocument(new IdRef(docId));
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC,
					"Impossible de trouver le document à notifier : " + docId, e);
			return;
		}

		Dossier dossier = null;
		if (doc.getType().equals(STConstant.DOSSIER_DOCUMENT_TYPE)) {
			dossier = doc.getAdapter(Dossier.class);
		} else if (doc.getType().equals(DossierConstants.QUESTION_DOCUMENT_TYPE)) {
			Question question = doc.getAdapter(Question.class);
			try {
				dossier = session.getDocument(question.getDossierRef()).getAdapter(Dossier.class);
			} catch (ClientException e) {
				LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC,
						"Impossible de trouver le dossier à notifier : " + docId, e);
				return;
			}
		}

		if (dossier == null) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC,
					"Type de document non pris en charge par la notification : " + docId);
			return;
		}

		if (!dossier.hasFeuilleRoute()) {
			LOGGER.error(session, SSLogEnumImpl.FAIL_GET_FDR_TEC, "Ce dossier ne comporte pas de feuille de route : "
					+ docId);
			return;
		}

		List<String> postesToNotify = new ArrayList<String>();

		if (webservice.equals(STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION)) {
			// Dans le cas de la notification pour le webservice STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION,
			// on notifie la dernière étape Pour rédaction interfacée
			final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
			DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
			STFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(STFeuilleRoute.class);
			List<DocumentRouteTableElement> routeElements = documentRoutingService.getRouteElements(feuilleRoute,
					session);
			STRouteStep stepToNotify = null;
			for (DocumentRouteTableElement routeElement : routeElements) {
				DocumentModel stepDoc = routeElement.getElement().getDocument();
				STRouteStep step = stepDoc.getAdapter(STRouteStep.class);
				if (VocabularyConstants.ROUTING_TASK_TYPE_REDACTION_INTERFACEE.equals(step.getType())) {
					stepToNotify = step;
				}
			}
			if (stepToNotify != null) {
				postesToNotify
						.add(mailboxPosteService.getPosteIdFromMailboxId(stepToNotify.getDistributionMailboxId()));
			}
		} else {
			// Dans les autre cas, on notifie les étapes en cours
			List<DocumentModel> runningStepsDoc = ReponsesServiceLocator.getFeuilleRouteService().getRunningSteps(
					session, dossier.getLastDocumentRoute());
			for (DocumentModel runningStepDoc : runningStepsDoc) {
				STRouteStep runningStep = runningStepDoc.getAdapter(STRouteStep.class);
				postesToNotify.add(mailboxPosteService.getPosteIdFromMailboxId(runningStep.getDistributionMailboxId()));
			}
		}

		List<PosteNode> listNode = STServiceLocator.getSTPostesService().getPostesNodes(postesToNotify);

		// On notifie les étapes sélectionnées
		for (OrganigrammeNode organigrammeNode : listNode) {
			PosteNode posteNode = (PosteNode) organigrammeNode;

			String wsUrl = posteNode.getWsUrl();
			if (wsUrl != null && !wsUrl.isEmpty()) {
				callNotifier(dossier.getQuestion(session), posteNode, webservice, session);
			}
		}
	}

	@Override
	public void retryNotifications(CoreSession session) throws ClientException {
		final STMailService stMailService = STServiceLocator.getSTMailService();
		final STPostesService postesService = STServiceLocator.getSTPostesService();
		List<DocumentModel> notificationsDoc = session.getChildren(getWsNotificationFolder(session).getRef());
		for (DocumentModel notificationDoc : notificationsDoc) {
			WsNotification notification = notificationDoc.getAdapter(WsNotification.class);
			PosteNode posteNode = postesService.getPoste(notification.getPosteId());

			if (notification.getNbEssais() < 3) {
				try {
					Question question = session.getDocument(new IdRef(notification.getIdQuestion())).getAdapter(
							Question.class);
					notifyWebservice(question, posteNode, notification.getWebservice(), session);
					// La notification est passée sans encombres, on peut donc supprimer le document notification qui
					// avait été créé pour le rejeu
					session.removeDocument(notificationDoc.getRef());
				} catch (Exception e) {
					notification.setNbEssais(notification.getNbEssais() + 1);
					session.saveDocument(notificationDoc);
				}
			} else {
				// Trop d'essais effectués, abandon de la notification et envoi d'un mail aux utilisateurs du poste
				stMailService.sendMailNotificationToUserList(session, posteNode.getUserList(),
						"[Réponses] Echec de la notification au webservice",
						"Le nombre d'essais maximum pour notifier le webservice du poste " + posteNode.getLabel()
								+ " a été atteint, et la notification a été abandonnée.");
				session.removeDocument(notificationDoc.getRef());
			}
		}

		session.save();
	}

	private void callNotifier(Question question, PosteNode posteNode, String webservice, CoreSession session)
			throws ClientException {
		try {
			notifyWebservice(question, posteNode, webservice, session);
		} catch (Exception e) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_NOTIFICATION_WSNOTIFICATION_TEC);

			// On regarde si le webservice n'est pas notifiable, auquel cas il ne faut pas créé de notification
			if (WEBSERVICE_NON_NOTIFIABLE.equals(e.getMessage())) {
				LOGGER.info(session, ReponsesLogEnumImpl.FAIL_NOTIFICATION_WSNOTIFICATION_TEC,
						WEBSERVICE_NON_NOTIFIABLE + " : " + webservice);
			} else {
				// Sinon, on créé une notification a re-exécuter plus tard
				DocumentModel notificationDoc = session.createDocumentModel(getWsNotificationFolder(session)
						.getPathAsString(), "notification-" + posteNode.getId(),
						ReponsesSchemaConstant.NOTIFICATION_TYPE);
				WsNotification notification = notificationDoc.getAdapter(WsNotification.class);
				notification.setPosteId(posteNode.getId().toString());
				notification.setWebservice(webservice);
				notification.setIdQuestion(question.getDocument().getId());
				session.createDocument(notificationDoc);
				session.saveDocument(notificationDoc);
			}
		}
	}

	private void notifyWebservice(Question question, PosteNode posteNode, String webservice, CoreSession session)
			throws WSProxyFactoryException, ClientException, Exception {

		String url = posteNode.getWsUrl();
		String username = posteNode.getWsUser();
		String password = posteNode.getWsPassword();
		String keyAlias = posteNode.getWsKeyAlias();

		WSProxyFactory proxyFactory = new WSProxyFactory(url, null, username, password, keyAlias);
		WSNotification wsNotificationService = proxyFactory.getService(WSNotification.class);
		EnvoyerNotificationRequest request = new EnvoyerNotificationRequest();
		NotificationType notificationType = null;
		if (webservice.equals(STWebserviceConstant.CHERCHER_QUESTIONS)) {
			notificationType = NotificationType.REPONSES_QUESTIONS;
		}
		if (webservice.equals(STWebserviceConstant.CHERCHER_ERRATA_QUESTIONS)) {
			notificationType = NotificationType.REPONSES_QUESTIONS_ERRATA;
		}
		if (webservice.equals(STWebserviceConstant.CHERCHER_CHANGEMENT_ETAT_QUESTION)) {
			notificationType = NotificationType.REPONSES_CHANGEMENT_ETAT;
		}
		if (webservice.equals(STWebserviceConstant.CHERCHER_ATTRIBUTIONS)) {
			notificationType = NotificationType.REPONSES_ATTRIBUTIONS;
		}
		if (webservice.equals(STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION)) {
			notificationType = NotificationType.REPONSES_RETOUR_PUBLICATION;
		}
		if (notificationType == null) {
			throw new ClientException(WEBSERVICE_NON_NOTIFIABLE);
		}
		request.setTypeNotification(notificationType);
		request.getIdQuestions().add(WsUtils.getQuestionIdFromQuestion(question));
		WSNotificationCaller wsNotificationCaller = new WSNotificationCaller(wsNotificationService);
		EnvoyerNotificationResponse response = wsNotificationCaller.notifier(request);
		if (response.getStatutTraitement() != TraitementStatut.OK) {
			throw new ClientException("Echec d'envoi de la notification.");
		}
	}

	private DocumentModel getWsNotificationFolder(CoreSession session) throws ClientException {
		if (wsNotificationFolder == null) {
			StringBuilder sb = new StringBuilder("SELECT * FROM ")
					.append(ReponsesSchemaConstant.NOTIFICATION_FOLDER_TYPE);
			DocumentModelList list = new UnrestrictedQueryRunner(session, sb.toString()).findAll();
			if (list == null || list.size() <= 0) {
				throw new ClientException("Racine des notifications non trouvée");
			} else if (list.size() > 1) {
				throw new ClientException("Plusieurs racines de notifications trouvées");
			}

			wsNotificationFolder = list.get(0);
		}
		return wsNotificationFolder;
	}
}
