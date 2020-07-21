package fr.dila.reponses.core.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.reponses.api.service.QuestionConnexeService.HashTarget;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

/**
 * Gestion des hashages pour les questions connexes lors de la sauvegarde d'un document.
 * 
 * @author asatre
 */
public class HashConnexiteCreationListener implements PostCommitEventListener {
    private static final Log log = LogFactory.getLog(HashConnexiteCreationListener.class);

    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        if (events.containsEventName(DocumentEventTypes.DOCUMENT_UPDATED) || events.containsEventName(DocumentEventTypes.DOCUMENT_CREATED)) {
            for (Event event : events) {
                handleEvent(event);
            }
        }
    }

    protected void handleEvent(Event event) throws ClientException {
        if (!(event.getContext() instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext ctx = (DocumentEventContext) event.getContext();

        // Traite uniquement les modifications ou la creation de document
        if (!(event.getName().equals(DocumentEventTypes.DOCUMENT_UPDATED) || event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED))) {
            return;
        }

        // Traite uniquement les modifications de document ayant pour type question
        DocumentModel model = ctx.getSourceDocument();
        String docType = model.getType();
        if (!DossierConstants.QUESTION_DOCUMENT_TYPE.equals(docType)) {
            return;
        }

        Question question = model.getAdapter(Question.class);
        CoreSession session = ctx.getCoreSession();
        
        if(event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)) {
            question.setIdMinistereAttributaire(model.getPropertyValue("qu:idMinistereInterroge").toString());
            question.setIntituleMinistereAttributaire(model.getPropertyValue("qu:intituleMinistere").toString());
            question.setIdMinistereRattachement(model.getPropertyValue("qu:idMinistereInterroge").toString());
            question.setIntituleMinistereRattachement(model.getPropertyValue("qu:intituleMinistere").toString());
            session.saveDocument(question.getDocument());
        }
            
        if (!question.isQuestionTypeEcrite()) {
            return;
        }


        QuestionConnexeService questionConnexeService = ReponsesServiceLocator.getQuestionConnexeService();

        boolean modif = false;

        // verification du hash sur le titre
        // ce hash est null si c'est une question AN
        try {
            String hashTitle = questionConnexeService.getHash(question, HashTarget.TITLE, session);
            String oldHashTitle = question.getHashConnexiteTitre();
            if (!(hashTitle == null && oldHashTitle == null || hashTitle != null && hashTitle.equals(oldHashTitle))) {
                // mise a jour du hash pour cette question
                question.setHashConnexiteTitre(hashTitle);
                modif = true;
            }
        } catch (Exception e) {
            log.error("Le hash sur le titre n'est pas calculable pour la question N°" + question.getNumeroQuestion(), e);
        }

        // verification du hash sur le texte
        try {
            String hashTexte = questionConnexeService.getHash(question, HashTarget.TEXTE, session);
            String oldHashTexte = question.getHashConnexiteTexte();
            if (!(hashTexte == null && oldHashTexte == null || hashTexte != null && hashTexte.equals(oldHashTexte))) {
                question.setHashConnexiteTexte(hashTexte);
                modif = true;
            }
        } catch (Exception e) {
            log.error("Le hash sur le texte n'est pas calculable pour la question N°" + question.getNumeroQuestion(), e);
        }

        // verification du hash sur l'indexation AN
        try {
            String hashAN = questionConnexeService.getHash(question, HashTarget.INDEXATION_AN, session);
            String oldHashAN = question.getHashConnexiteAN();
            if (!(hashAN == null && oldHashAN == null || hashAN != null && hashAN.equals(oldHashAN))) {
                question.setHashConnexiteAN(hashAN);
                modif = true;
            }
        } catch (Exception e) {
            log.error("Le hash sur l'indexation AN n'est pas calculable pour la question N°" + question.getNumeroQuestion(), e);
        }

        // verification du hash sur l'indexation Senat
        try {
            String hashSE = questionConnexeService.getHash(question, HashTarget.INDEXATION_SE, session);
            String oldHashSE = question.getHashConnexiteSE();
            if (!(hashSE == null && oldHashSE == null || hashSE != null && hashSE.equals(oldHashSE))) {
                question.setHashConnexiteSE(hashSE);
                modif = true;
            }
        } catch (Exception e) {
            log.error("Le hash sur l'indexation Senat n'est pas calculable pour la question N°" + question.getNumeroQuestion(), e);
        }

        if (modif) {
            try {
                session.saveDocument(question.getDocument());
            } catch (Exception e) {
                log.error("La question N°" + question.getNumeroQuestion() + "n'as pu être sauvegardée après le hashage", e);
            }
        }
    }
}
