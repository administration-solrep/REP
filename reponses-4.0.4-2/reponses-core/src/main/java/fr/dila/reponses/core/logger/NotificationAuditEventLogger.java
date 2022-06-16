package fr.dila.reponses.core.logger;

import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.event.CaseManagementEventConstants.EventNames;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.logger.STNotificationAuditEventLogger;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.VersioningChangeNotifier;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener qui enregistre l'Audit log pour l'application Réponses.
 *
 * @author bby, ARN
 */
public class NotificationAuditEventLogger extends STNotificationAuditEventLogger {
    private static final Log LOGGER = LogFactory.getLog(NotificationAuditEventLogger.class);

    static {
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.DOSSIER_ARBITRAGE_SGG_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.DOSSIER_REATTRIBUTION_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.DOSSIER_REORIENTATION_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.DOSSIER_PARAPHEUR_UPDATE_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.DOSSIER_BRISER_SIGNATURE_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.DOSSIER_AJOUT_FOND_DOSSIER_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.DOSSIER_MODIF_FOND_DOSSIER_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.DOSSIER_SUPP_FOND_DOSSIER_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.EVENT_DEMANDE_ELIMINATION);
        SET_EVENT_DOSSIER.remove(STEventConstant.EVENT_ARCHIVAGE_DOSSIER);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.WEBSERVICE_ENVOYER_ERRATA_REPONSES_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.WEBSERVICE_ENVOYER_QUESTIONS_ERRATA_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.WEBSERVICE_ENVOYER_QUESTIONS_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.WEBSERVICE_ENVOYER_REPONSES_EVENT);
        SET_EVENT_DOSSIER.add(STEventConstant.DOSSIER_POUR_VALIDATION_PM);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_DOSSIER_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.BORDEREAU_JO_UPDATE_EVENT);
        SET_EVENT_DOSSIER.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_EVENT);

        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_ATTRIBUTION_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_CHANGEMENT_ETAT_QUESTION_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_DOSSIER_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_QUESTION_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_REPONSES_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_LEGISLATURE_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_REPONSES_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_EVENT);
        SET_EVENT_ADMIN.add(ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_EVENT);
    }

    @Override
    protected void loggerProcess(Event event) {
        if (AUDIT_LOGGER == null) {
            LOGGER.warn("Can not reach AuditLogger");
            return;
        }
        String eventName = event.getName();
        if (event.getContext() instanceof DocumentEventContext) {
            DocumentEventContext docCtx = (DocumentEventContext) event.getContext();
            DocumentModel model = docCtx.getSourceDocument();

            if (model != null) {
                LOGGER.debug("--------------------NotificationAuditEventLogger handleEvent() calling : " + model);

                String docType = model.getType();
                if (
                    DocumentEventTypes.DOCUMENT_CREATED.equals(eventName) ||
                    DocumentEventTypes.DOCUMENT_UPDATED.equals(eventName)
                ) {
                    // log technique de la création et modification du dossier et de la feuille de route
                    if (DossierConstants.REPONSE_DOCUMENT_TYPE.equals(docType)) {
                        logReponse(event, docCtx);
                    } else if (DossierConstants.DOSSIER_DOCUMENT_TYPE.equals(docType)) {
                        if (DocumentEventTypes.DOCUMENT_CREATED.equals(eventName)) {
                            logDossier(event, docCtx);
                        }
                    } else if (SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equals(docType)) {
                        logFeuilleDeRoute(event, docCtx);
                    }
                } else if (VersioningChangeNotifier.CORE_EVENT_ID_VERSIONING_CHANGE.equals(eventName)) {
                    // HACK Nouvelle version de la reponse
                    if (
                        docCtx.getSourceDocument() != null &&
                        docCtx.getSourceDocument().getType() != null &&
                        docCtx.getSourceDocument().getType().equals(DossierConstants.REPONSE_DOCUMENT_TYPE)
                    ) {
                        docCtx.setCategory(STEventConstant.CATEGORY_PARAPHEUR);
                        Long major = (Long) model.getProperty(
                            STSchemaConstant.UID_SCHEMA,
                            STSchemaConstant.UID_MAJOR_VERSION_PROPERTY
                        );
                        docCtx.setComment(ReponsesEventConstant.COMMENT_REPONSE_VERSIONING + major);
                        logReponse(event, docCtx);
                        defaultHandleEvent(event);
                    }
                } else if (
                    EventNames.afterCaseSentEvent.name().equals(eventName) &&
                    DossierConstants.DOSSIER_DOCUMENT_TYPE.equals(docType)
                ) {
                    // Nouvelle version de la reponse
                    String comment =
                        "Transfert du dossier à [" +
                        docCtx.getProperty("eventContextParticipants_type_FOR_ACTION") +
                        "]";

                    if (docCtx.getProperty(CaseManagementEventConstants.EVENT_CONTEXT_COMMENT) != null) {
                        comment += " " + docCtx.getProperty(CaseManagementEventConstants.EVENT_CONTEXT_COMMENT);
                    }
                    docCtx.setCategory(STEventConstant.CATEGORY_FEUILLE_ROUTE);
                    docCtx.setComment(comment);
                    logDossier(event, docCtx);
                    defaultHandleEvent(event);
                } else if (SET_EVENT_DOSSIER.contains(eventName)) {
                    logDossier(event, docCtx);
                    logEvent(event, docCtx);
                } else if (ReponsesEventConstant.DOSSIER_REPONSE_UPDATE_EVENT.equals(eventName)) {
                    if (DossierConstants.REPONSE_DOCUMENT_TYPE.equals(docType)) {
                        logReponse(event, docCtx);
                    } else {
                        logDossier(event, docCtx);
                        logEvent(event, docCtx);
                    }
                }
            }
        } else {
            if (SET_EVENT_ADMIN.contains(eventName)) {
                defaultHandleEvent(event);
            }
        }
    }

    /**
     * Log Event notification dossier
     *
     * @param event
     * @param context
     * @throws Exception
     */
    private void logFeuilleDeRoute(Event event, DocumentEventContext context) {
        Long numeroQuestion = null;
        // récupération des dossiers via la feuille de route
        ReponsesFeuilleRoute feuilleRoute = context.getSourceDocument().getAdapter(ReponsesFeuilleRoute.class);
        List<String> dossierIds = feuilleRoute.getAttachedDocuments();
        if (dossierIds != null) {
            for (String idDossier : dossierIds) {
                DocumentModel dossierDoc = context.getCoreSession().getDocument(new IdRef(idDossier));
                if (dossierDoc != null) {
                    Dossier dossier = dossierDoc.getAdapter(Dossier.class);

                    numeroQuestion = dossier.getNumeroQuestion();
                }
                // on passe an paramètre le type d'action, l'utilisateur et l'id
                Object[] datas = new Object[] { event.getName(), context.getPrincipal().getName(), numeroQuestion };
                NotificationMessageLogger.getInstance().logFeuilleDeRoute(event, LOGGER, datas);
            }
        }
    }

    /**
     * Log Event notification dossier
     *
     * @param event
     * @param context
     * @throws ClientException
     */
    private void logDossier(Event event, DocumentEventContext context) {
        // récupération Dossier
        DocumentEventContext dossierCtx = (DocumentEventContext) event.getContext();
        DocumentModel dossierDoc = dossierCtx.getSourceDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        CoreSession session = dossierCtx.getCoreSession();
        if (dossier != null) {
            // on passe en paramètre le type d'action, l'utilisateur, l'origine, l'auteur et le numéro de question du
            // dossier
            Question question = dossier.getQuestion(session);
            String origineQuestion = null;
            String nomCompletAuteur = null;
            if (question != null) {
                origineQuestion = question.getOrigineQuestion();
                nomCompletAuteur = question.getNomCompletAuteur();
            }
            Object[] datas = new Object[] {
                event.getName(),
                context.getPrincipal().getName(),
                origineQuestion,
                nomCompletAuteur,
                dossier.getNumeroQuestion()
            };
            NotificationMessageLogger.getInstance().logDossier(event, LOGGER, datas);
        } else {
            LOGGER.info("L'événement suivant ne se réfère pas à un dossier " + event.getName());
        }
    }

    /**
     * Méthode pour permettre aux dossiers allotis d'avoir dans leurs journaux le même log que dans le dossier qui a été
     * modifié par un utilisateur.
     *
     * @param event
     * @param dossierCtx
     * @throws ClientException
     */
    private void logEvent(Event event, DocumentEventContext dossierCtx) {
        final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        Allotissement allotissement = null;
        // récupération Dossier
        DocumentModel dossierDoc = dossierCtx.getSourceDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        CoreSession session = dossierCtx.getCoreSession();
        if (dossier != null) {
            try {
                if (dossier.getDossierLot() != null) {
                    allotissement = allotissementService.getAllotissement(dossier.getDossierLot(), session);
                }
            } catch (NuxeoException e) {
                LOGGER.info("pas d'allotissement découvert pour le dossier id " + dossierDoc.getId());
            }
            // Dans le cas d'allotissement, on ajoute au journal de log pour les allotissements aussi
            if (allotissement != null) {
                List<String> idsDossiers = allotissement.getIdDossiers();
                for (String id : idsDossiers) {
                    DocumentModel dossierDocAlloti;
                    try {
                        dossierDocAlloti = session.getDocument(new IdRef(id));
                        final DocumentEventContext envContext = new DocumentEventContext(
                            session,
                            dossierCtx.getPrincipal(),
                            dossierDocAlloti
                        );
                        envContext.setComment(dossierCtx.getComment());
                        envContext.setCategory(STEventConstant.CATEGORY_PARAPHEUR);
                        defaultHandleEvent(envContext.newEvent(event.getName()));
                    } catch (NuxeoException e) {
                        LOGGER.warn(
                            "Erreur de récupération d'id pour dossier alloti du dossier id " +
                            dossierDoc.getId() +
                            " - pas de log dans le journal effectué"
                        );
                    }
                }
            } else {
                defaultHandleEvent(event);
            }
        }
    }

    /**
     * Log event notification Reponse
     *
     * @param event
     * @param context
     */
    private void logReponse(Event event, DocumentEventContext context) {
        DocumentModel model = context.getSourceDocument();
        Reponse reponse = model.getAdapter(Reponse.class);
        ReponseService reponseService = ReponsesServiceLocator.getReponseService();

        if (reponse != null) {
            DocumentModel dossierModel = reponseService.getDossierFromReponse(
                context.getCoreSession(),
                reponse.getDocument()
            );
            Dossier dossier = null;
            if (dossierModel != null) {
                dossier = dossierModel.getAdapter(Dossier.class);
            }
            if (dossier != null) {
                Question question = dossier.getQuestion(context.getCoreSession());
                String origineQuestion = null;
                String nomCompletAuteur = null;
                if (question != null) {
                    origineQuestion = question.getOrigineQuestion();
                    nomCompletAuteur = question.getNomCompletAuteur();
                }
                Object[] datas = new Object[] {
                    event.getName(),
                    context.getPrincipal().getName(),
                    origineQuestion,
                    nomCompletAuteur,
                    dossier.getNumeroQuestion(),
                    reponse.getIdAuteurReponse()
                };
                NotificationMessageLogger.getInstance().logReponse(event, LOGGER, datas);
            }
        }
    }
}
