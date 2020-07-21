package fr.dila.reponses.core.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.activation.DataSource;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

import com.google.common.collect.Lists;

import fr.dila.reponses.api.constant.RechercheExportEventConstants;
import fr.dila.reponses.api.constant.ReponsesStatsEventConstants;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.core.util.ExcelUtil;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.SessionUtil;

public class ExportListener implements PostCommitEventListener {

	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger LOGGER = STLogFactory.getLog(ExportListener.class);

	@Override
	public void handleEvent(EventBundle events) throws ClientException {
		if (!events.containsEventName(ReponsesStatsEventConstants.EXPORT_STATS_EVENT)
				&& !events.containsEventName(RechercheExportEventConstants.EVENT_NAME)) {
			return;
		}
		LOGGER.debug(STLogEnumImpl.DEFAULT, "Traitement de la demande d'export prise en compte");
		for (final Event event : events) {
			if (ReponsesStatsEventConstants.EXPORT_STATS_EVENT.equals(event.getName())) {
				LOGGER.debug(STLogEnumImpl.DEFAULT, "On traite l'export de stat");
				handleStatEvent(event);
			} else if (RechercheExportEventConstants.EVENT_NAME.equals(event.getName())) {
				LOGGER.debug(STLogEnumImpl.DEFAULT, "On traite l'export de résultat de recherche");
				handleSearchResultEvent(event);
			}
		}
	}

	private void handleSearchResultEvent(Event event) {

		final EventContext eventCtx = event.getContext();
		final Map<String, Serializable> eventProperties = eventCtx.getProperties();
		final String query = (String) eventProperties.get(RechercheExportEventConstants.PARAM_QUERY);
		final String mail = (String) eventProperties.get(RechercheExportEventConstants.PARAM_MAIL);
		final CoreSession session = (CoreSession) eventProperties.get(RechercheExportEventConstants.PARAM_SESSION);
		String objet = "[REPONSES] Votre demande d'export des résultats";
		String dateDemande = DateUtil.formatForClient(new Date());
		String corpsErrorTemplate = "Bonjour, l'export des résultats de recherche, demandé le " + dateDemande
				+ ", a échoué. " + "Le message remonté est le suivant : \n";
		try {
			DataSource fichierExcelResultat = ExcelUtil.creationExportAllResultExcel(session, query);

			if (fichierExcelResultat != null) {
				LOGGER.debug(STLogEnumImpl.DEFAULT, "Fichier excel généré, on envoie le mail");

				sendMailWithAttachment(session, mail, objet, "Bonjour, l'export des resultats, demandé le "
						+ dateDemande + ", est terminé.", fichierExcelResultat);
			} else {
				corpsErrorTemplate = corpsErrorTemplate + "Impossible de générer le fichier d'export";
				sendMail(session, mail, objet, corpsErrorTemplate);
			}

		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_SESSION_TEC, exc);
			corpsErrorTemplate = corpsErrorTemplate + exc.getMessage();
			sendMail(session, mail, objet, corpsErrorTemplate);
		} finally {
			if (session != null) {
				SessionUtil.close(session);
			}
		}

	}

	private void handleStatEvent(Event event) {
		final EventContext eventCtx = event.getContext();
		// récupération des propriétés de l'événement
		final Map<String, Serializable> eventProperties = eventCtx.getProperties();
		final String userWorkspacePath = (String) eventProperties
				.get(ReponsesStatsEventConstants.USER_WS_PATH_PROPERTY);
		final SSPrincipal user = (SSPrincipal) eventProperties.get(ReponsesStatsEventConstants.USER_PROPERTY);
		@SuppressWarnings("unchecked")
		final ArrayList<String> formats = (ArrayList<String>) eventProperties
				.get(ReponsesStatsEventConstants.FORMATS_EXPORT_PROPERTY);
		@SuppressWarnings("unchecked")
		final Map<String, String> reportsMultiExportMap = (Map<String, String>) eventProperties
				.get(ReponsesStatsEventConstants.REPORTS_HAVE_MULTI_PROPERTY);
		@SuppressWarnings("unchecked")
		final Map<String, String> reportsNamesMap = (Map<String, String>) eventProperties
				.get(ReponsesStatsEventConstants.REPORTS_NAMES_PROPERTY);
		@SuppressWarnings("unchecked")
		final Map<String, String> reportsTitlesMap = (Map<String, String>) eventProperties
				.get(ReponsesStatsEventConstants.REPORTS_TITLES_PROPERTY);

		CoreSession session = null;
		try {
			session = SessionUtil.getCoreSession();
			final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
			if (session != null) {
				try {
					exportService.exportStat(session, userWorkspacePath, user, reportsMultiExportMap, reportsNamesMap,
							reportsTitlesMap, formats);
					try {
						sendMailExportOK(
								session,
								user.getName(),
								exportService.getExportHorodatageRequest(session,
										exportService.getExportStatDocForUser(session, user, userWorkspacePath)));
					} catch (ClientException exc) {
						LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
					}
				} catch (Exception exc) {
					LOGGER.error(session, SSLogEnumImpl.FAIL_EXPORT_STATS_TEC, exc);
					sendMailExportKO(
							session,
							user.getName(),
							exportService.getExportHorodatageRequest(session,
									exportService.getExportStatDocForUser(session, user, userWorkspacePath)),
							exc.getMessage());
				} finally {
					exportService.flagEndExportStatForUser(session, user, userWorkspacePath);
				}
			} else {
				LOGGER.warn(null, STLogEnumImpl.FAIL_GET_SESSION_TEC);
			}
		} catch (Exception exc) {
			LOGGER.error(null, STLogEnumImpl.FAIL_GET_SESSION_TEC, exc);
		} finally {
			if (session != null) {
				SessionUtil.close(session);
			}
		}
	}

	private void sendMailExportOK(CoreSession session, String user, String dateRequest) {
		String userMail = STServiceLocator.getSTUserService().getUserInfo(user, "m");
		String objet = "[REPONSES] Votre demande d'export statistique ";
		String corpsTemplate = "Bonjour, l'export statistique, demandé le " + dateRequest + ", est terminé.";
		sendMail(session, userMail, objet, corpsTemplate);
	}

	private void sendMailExportKO(CoreSession session, String user, String dateRequest, String messageStack) {
		String userMail = STServiceLocator.getSTUserService().getUserInfo(user, "m");
		String objet = "[REPONSES] Votre demande d'export statistique ";
		String corpsTemplate = "Bonjour, l'export statistique, demandé le " + dateRequest + ", a échoué. "
				+ "Le message remonté est le suivant : \n" + messageStack;

		sendMail(session, userMail, objet, corpsTemplate);
	}

	private void sendMail(CoreSession session, String adresse, String objet, String corps) {
		final STMailService mailService = STServiceLocator.getSTMailService();
		try {
			if (adresse != null) {
				mailService.sendTemplateMail(adresse, objet, corps, null);
			} else {
				LOGGER.warn(session, STLogEnumImpl.FAIL_GET_MAIL_TEC);
			}
		} catch (final ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
		}
	}

	private void sendMailWithAttachment(CoreSession session, String adresse, String objet, String corps,
			DataSource attachment) throws Exception {
		final STMailService mailService = STServiceLocator.getSTMailService();
		try {
			if (adresse != null) {
				mailService.sendMailWithAttachement(Lists.newArrayList(adresse), objet, corps, "export_dossier.xls",
						attachment);
			} else {
				LOGGER.warn(session, STLogEnumImpl.FAIL_GET_MAIL_TEC);
			}
		} catch (final ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
		}
	}
}
