package fr.dila.reponses.core.event;

import static java.lang.String.format;

import com.google.common.collect.Lists;
import fr.dila.reponses.api.constant.ReponsesStatsEventConstants;
import fr.dila.reponses.api.service.ReponsesExportService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import org.nuxeo.ecm.automation.core.mail.BlobDataSource;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

public class ExportListener implements PostCommitEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ExportListener.class);

    @Override
    public void handleEvent(EventBundle events) {
        if (
            !events.containsEventName(ReponsesStatsEventConstants.EXPORT_STATS_EXPORT_EVENT) &&
            !events.containsEventName(ReponsesStatsEventConstants.EXPORT_STATS_MAIL_EVENT)
        ) {
            return;
        }
        LOGGER.debug(STLogEnumImpl.DEFAULT, "Traitement de la demande d'export prise en compte");
        for (final Event event : events) {
            if (ReponsesStatsEventConstants.EXPORT_STATS_EXPORT_EVENT.equals(event.getName())) {
                LOGGER.debug(STLogEnumImpl.DEFAULT, "On traite l'export de stat");
                handleStatExportEvent(event);
            } else if (ReponsesStatsEventConstants.EXPORT_STATS_MAIL_EVENT.equals(event.getName())) {
                LOGGER.debug(STLogEnumImpl.DEFAULT, "On traite l'export de stat avec un envoi de mail");
                handleStatMailEvent(event);
            }
        }
    }

    private void handleStatExportEvent(Event event) {
        final EventContext eventCtx = event.getContext();
        // récupération des propriétés de l'événement
        final Map<String, Serializable> eventProperties = eventCtx.getProperties();
        final String userWorkspacePath = getUserWorkspacePath(eventProperties);
        final SSPrincipal user = (SSPrincipal) eventProperties.get(ReponsesStatsEventConstants.USER_PROPERTY);
        final List<BirtOutputFormat> formats = getPropertyValue(
            eventProperties,
            ReponsesStatsEventConstants.FORMATS_EXPORT_PROPERTY
        );
        final Map<String, String> reportsMultiExportMap = getPropertyValue(
            eventProperties,
            ReponsesStatsEventConstants.REPORTS_HAVE_MULTI_PROPERTY
        );
        final Map<String, String> reportsNamesMap = getPropertyValue(
            eventProperties,
            ReponsesStatsEventConstants.REPORTS_NAMES_PROPERTY
        );
        final Map<String, String> reportsTitlesMap = getPropertyValue(
            eventProperties,
            ReponsesStatsEventConstants.REPORTS_TITLES_PROPERTY
        );

        try (CloseableCoreSession session = SessionUtil.openSession()) {
            final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
            if (session != null) {
                try {
                    exportService.exportStat(
                        session,
                        userWorkspacePath,
                        user,
                        reportsMultiExportMap,
                        reportsNamesMap,
                        reportsTitlesMap,
                        formats
                    );
                    try {
                        sendMailExportOK(
                            session,
                            user.getName(),
                            exportService.getExportHorodatageRequest(
                                session,
                                exportService.getExportStatDocForUser(session, user, userWorkspacePath)
                            )
                        );
                    } catch (NuxeoException exc) {
                        LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
                    }
                } catch (Exception exc) {
                    LOGGER.error(session, SSLogEnumImpl.FAIL_EXPORT_STATS_TEC, exc);
                    sendMailExportKO(
                        session,
                        user.getName(),
                        exportService.getExportHorodatageRequest(
                            session,
                            exportService.getExportStatDocForUser(session, user, userWorkspacePath)
                        ),
                        exc.getMessage()
                    );
                } finally {
                    exportService.flagEndExportStatForUser(session, user, userWorkspacePath);
                }
            } else {
                LOGGER.warn(null, STLogEnumImpl.FAIL_GET_SESSION_TEC);
            }
        } catch (Exception exc) {
            LOGGER.error(null, STLogEnumImpl.FAIL_GET_SESSION_TEC, exc);
        }
    }

    private void handleStatMailEvent(Event event) {
        final EventContext eventCtx = event.getContext();
        // récupération des propriétés de l'événement
        final Map<String, Serializable> eventProperties = eventCtx.getProperties();
        final String userWorkspacePath = getUserWorkspacePath(eventProperties);
        final SSPrincipal user = (SSPrincipal) eventProperties.get(ReponsesStatsEventConstants.USER_PROPERTY);
        final List<BirtOutputFormat> formats = getPropertyValue(
            eventProperties,
            ReponsesStatsEventConstants.FORMATS_EXPORT_PROPERTY
        );
        final Map<String, String> reportsMultiExportMap = getPropertyValue(
            eventProperties,
            ReponsesStatsEventConstants.REPORTS_HAVE_MULTI_PROPERTY
        );
        final Map<String, String> reportsNamesMap = getPropertyValue(
            eventProperties,
            ReponsesStatsEventConstants.REPORTS_NAMES_PROPERTY
        );
        final Map<String, String> reportsTitlesMap = getPropertyValue(
            eventProperties,
            ReponsesStatsEventConstants.REPORTS_TITLES_PROPERTY
        );

        try (CloseableCoreSession session = SessionUtil.openSession()) {
            final ReponsesExportService exportService = ReponsesServiceLocator.getReponsesExportService();
            try {
                exportService.exportStat(
                    session,
                    userWorkspacePath,
                    user,
                    reportsMultiExportMap,
                    reportsNamesMap,
                    reportsTitlesMap,
                    formats
                );
                Blob zipBlob = exportService
                    .getExportStatDocForUser(session, user, userWorkspacePath)
                    .getAdapter(ExportDocument.class)
                    .getFileContent();
                String objet = "[REPONSES] Votre demande d'export statistique";
                String dateDemande = SolonDateConverter.getClientConverter().formatNow();
                String stat = reportsTitlesMap.values().iterator().next();

                OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
                final String userMail = organigrammeService.getMailFromUsername(user.getName());

                if (zipBlob != null) {
                    LOGGER.debug(STLogEnumImpl.DEFAULT, "Fichier excel généré, on envoie le mail");
                    sendMailWithAttachment(
                        session,
                        userMail,
                        objet,
                        format(
                            "Bonjour, vous trouverez en pièce jointe l'export demandé le %s, pour la statistique %s.",
                            dateDemande,
                            stat
                        ),
                        zipBlob.getFilename(),
                        new BlobDataSource(zipBlob)
                    );
                } else {
                    String corpsErrorTemplate = format(
                        "Bonjour, l'export pour la statistique %s, demandé le %s, a échoué. Le message remonté est le suivant : %n",
                        stat,
                        dateDemande
                    );
                    corpsErrorTemplate = corpsErrorTemplate + "Impossible de générer le fichier d'export";
                    sendMail(session, userMail, objet, corpsErrorTemplate);
                }
            } catch (Exception exc) {
                LOGGER.error(session, SSLogEnumImpl.FAIL_EXPORT_STATS_TEC, exc);
                sendMailExportKO(
                    session,
                    user.getName(),
                    exportService.getExportHorodatageRequest(
                        session,
                        exportService.getExportStatDocForUser(session, user, userWorkspacePath)
                    ),
                    exc.getMessage()
                );
            } finally {
                exportService.flagEndExportStatForUser(session, user, userWorkspacePath);
            }
        } catch (Exception exc) {
            LOGGER.error(null, STLogEnumImpl.FAIL_GET_SESSION_TEC, exc);
        }
    }

    private String getUserWorkspacePath(final Map<String, Serializable> eventProperties) {
        return (String) eventProperties.get(ReponsesStatsEventConstants.USER_WS_PATH_PROPERTY);
    }

    @SuppressWarnings("unchecked")
    private <T> T getPropertyValue(final Map<String, Serializable> eventProperties, String property) {
        return (T) eventProperties.get(property);
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
        String corpsTemplate =
            "Bonjour, l'export statistique, demandé le " +
            dateRequest +
            ", a échoué. " +
            "Le message remonté est le suivant : \n" +
            messageStack;

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
        } catch (final NuxeoException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
        }
    }

    private void sendMailWithAttachment(
        CoreSession session,
        String adresse,
        String objet,
        String corps,
        String fileName,
        DataSource attachment
    ) {
        try {
            if (adresse != null) {
                final STMailService mailService = STServiceLocator.getSTMailService();
                mailService.sendMailWithAttachement(Lists.newArrayList(adresse), objet, corps, fileName, attachment);
            } else {
                LOGGER.warn(session, STLogEnumImpl.FAIL_GET_MAIL_TEC);
            }
        } catch (final NuxeoException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc);
        }
    }
}
