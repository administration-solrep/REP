package fr.dila.reponses.core.event;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.flux.HasInfoFlux;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;

import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Gestionnaire d'évènements qui permet de traiter les évènements de
 * modification de la question
 *
 * @author asatre
 */
public class QuestionUpdateListener implements EventListener {

    /**
     * Constructeur de ReponseUpdateListener.
     */
    public QuestionUpdateListener() {}

    @Override
    public void handleEvent(Event event) {
        // Traite uniquement les évènements de modification de reponse
        EventContext ctx = event.getContext();
        DocumentEventContext context = (DocumentEventContext) ctx;
        if (
            !(
                event.getName().equals(DocumentEventTypes.DOCUMENT_UPDATED) ||
                event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)
            )
        ) {
            return;
        }

        // Traite uniquement les documents de type Question
        DocumentModel repDoc = context.getSourceDocument();
        String docType = repDoc.getType();
        if (!DossierConstants.QUESTION_DOCUMENT_TYPE.equals(docType)) {
            return;
        }

        /**
         * calcul des champs de denormalisation
         */
        Question question = repDoc.getAdapter(Question.class);

        HasInfoFlux hasInfoFlux = question.getDocument().getAdapter(HasInfoFlux.class);
        String oldEtatsQuestion = question.getEtatsQuestion();
        String newEtatsQuestion = hasInfoFlux.computeEtatsQuestion();

        ReponsesIndexableDocument reponsesIndexableDocument = question
            .getDocument()
            .getAdapter(ReponsesIndexableDocument.class);
        String newMotsCles = reponsesIndexableDocument.getMotsClef();
        String oldMotsCles = question.getMotsCles();

        boolean modif = false;
        if (!newEtatsQuestion.equals(oldEtatsQuestion)) {
            question.setEtatsQuestion(newEtatsQuestion);
            modif = true;
        }
        if (!newMotsCles.equals(oldMotsCles)) {
            question.setMotsCles(newMotsCles);
            modif = true;
        }

        if (modif) {
            CoreSession session = ctx.getCoreSession();

            // maj des DossierLink de la question
            DocumentModel dossierDoc = session.getDocument(question.getDossierRef());
            StringBuilder queryDL = new StringBuilder("SELECT * FROM ")
                .append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE)
                .append(" where cslk:caseDocumentId = '" + dossierDoc.getId() + "' AND ")
                .append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH)
                .append(" = 'todo' ");

            CoreInstance.doPrivileged(session, uSession -> {
                final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
                uSession.query(queryDL.toString()).stream().map(DossierLink::adapt).forEach(dl -> {
                    dossierDistributionService.updateDenormalisation(dl, question);
                    dl.save(uSession);
                });
            });

            session.saveDocument(question.getDocument());
            session.save();
        }
    }
}
