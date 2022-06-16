package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DOCUMENT_TYPE;
import static fr.dila.reponses.core.service.ReponsesServiceLocator.getAllotissementService;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doUFNXQLQueryForIdsList;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.retrieveDocuments;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.DossierService;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.schema.RoutingTaskSchemaUtils;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.core.util.DocUtil;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;

public class DossierServiceImpl implements DossierService {
    private static final String RENVOI_INDEXATION_QUESTION_RAPPEL = "Question de rappel";

    @Override
    public List<DocumentModel> getDossierRattacheToDirection(CoreSession session, String directionId) {
        // récupère les dossiers
        final StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ");
        query.append(DossierConstants.QUESTION_DOCUMENT_TYPE);
        query.append(" WHERE ");
        query.append("qu:");
        query.append(DossierConstants.DOSSIER_ID_DIRECTION_PILOTE_QUESTION);
        query.append(" = '");
        query.append(directionId);
        query.append("'");

        query.append(" and ecm:isProxy = 0");

        return session.query(query.toString());
    }

    @Override
    public Optional<Dossier> getDossierDirecteur(CoreSession session, Dossier dossier) {
        Allotissement lot = getAllotissementService().getAllotissement(dossier.getDossierLot(), session);
        return ofNullable(lot)
            .map(Allotissement::getIdDossiers)
            .filter(CollectionUtils::isNotEmpty)
            .map(l -> l.get(0))
            .map(id -> session.getDocument(new IdRef(id)).getAdapter(Dossier.class));
    }

    @Override
    public Set<String> getListingUnitesStruct(DocumentModel dossierDoc, CoreSession session) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        return ReponsesServiceLocator
            .getFeuilleRouteService()
            .getSteps(session, dossierDoc)
            .stream()
            .map(RoutingTaskSchemaUtils::getMailboxId)
            .filter(Objects::nonNull)
            .map(mailboxPosteService::getIdDirectionFromMailbox)
            .collect(Collectors.toSet());
    }

    @Override
    public String getFinalStepLabel(CoreSession session, Dossier dossier) {
        return Optional
            .ofNullable(dossier.getLastDocumentRoute())
            .map(r -> ReponsesServiceLocator.getFeuilleRouteService().getValidationPMStep(session, r))
            .map(l -> l.getAdapter(SSRouteStep.class))
            .map(SSRouteStep::getPosteLabel)
            .orElse("");
    }

    @Override
    public List<Question> getQuestionsAlloties(CoreSession session, Dossier dossier) {
        return Optional
            .ofNullable(getAllotissementService().getAllotissement(dossier.getDossierLot(), session))
            .map(a -> getAllotissementService().getQuestionAlloties(session, a))
            .orElseGet(ArrayList::new);
    }

    @Override
    public List<Question> getQERappels(CoreSession session, Dossier dossier) {
        List<String> idDossiersLot = getIdDossiersLot(session, dossier);
        if (idDossiersLot.isEmpty()) {
            return emptyList();
        }

        String query = getQueryQuestions(idDossiersLot, "ecm:uuid AS id");
        List<String> idsQuestions = doUFNXQLQueryForIdsList(session, query, idDossiersLot.toArray(new String[0]));
        DocumentModelList questions = retrieveDocuments(session, QUESTION_DOCUMENT_TYPE, idsQuestions);
        return DocUtil.adapt(questions, Question.class);
    }

    @Override
    public List<String> getSourceNumeroQERappels(CoreSession session, Dossier dossier) {
        List<String> idDossiersLot = getIdDossiersLot(session, dossier);
        if (idDossiersLot.isEmpty()) {
            return emptyList();
        }

        String query = getQueryQuestions(idDossiersLot, "qu:sourceNumeroQuestion AS sourceNumeroQuestion");
        return QueryUtils.doUFNXQLQueryAndMapping(
            session,
            query,
            idDossiersLot.toArray(new String[0]),
            (Map<String, Serializable> rowData) -> (String) rowData.get("sourceNumeroQuestion")
        );
    }

    private List<String> getIdDossiersLot(CoreSession session, Dossier dossier) {
        Allotissement allotissement = getAllotissementService().getAllotissement(dossier.getDossierLot(), session);
        return ofNullable(allotissement).map(Allotissement::getIdDossiers).orElseGet(Collections::emptyList);
    }

    private String getQueryQuestions(List<String> idDossiers, String prop) {
        String questionMarks = StringHelper.getQuestionMark(idDossiers.size());
        return format(
            "Select distinct q.%s From Dossier AS d, Question AS q WHERE testAcl(q.ecm:uuid) = 1 AND d.ecm:uuid IN (%s) AND d.dos:idDocumentQuestion = q.ecm:uuid AND q.ixa:SE_renvoi = '%s'",
            prop,
            questionMarks,
            RENVOI_INDEXATION_QUESTION_RAPPEL
        );
    }
}
