package fr.dila.reponses.core.event;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.core.export.RepRechercheDossierConfig;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.core.util.QuestionUtils;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.event.AbstractExportDossierListener;
import fr.dila.st.api.constant.STRechercheExportEventConstants;
import fr.dila.st.core.util.FileUtils;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.activation.DataSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ExportDossierRechercheListener extends AbstractExportDossierListener {
    private static final String EXPORT_FILENAME = FileUtils.generateCompleteXlsFilename("export_dossiers");

    public ExportDossierRechercheListener() {
        super(STRechercheExportEventConstants.EVENT_NAME, EXPORT_FILENAME);
    }

    @Override
    protected long countItemsToExport(CoreSession session, Map<String, Serializable> eventProperties) {
        return QueryUtils.doCountQuery(
            session,
            (String) eventProperties.get(STRechercheExportEventConstants.PARAM_QUERY)
        );
    }

    @Override
    protected DataSource processExport(CoreSession session, Map<String, Serializable> eventProperties)
        throws Exception {
        List<RepDossierListingDTO> dossiersToExport = getDossiersToExport(
            session,
            (SSPrincipal) session.getPrincipal(),
            (String) eventProperties.get(STRechercheExportEventConstants.PARAM_QUERY)
        );

        RepRechercheDossierConfig rechercheDossierConfig = new RepRechercheDossierConfig(dossiersToExport);
        return rechercheDossierConfig.getDataSource(session);
    }

    private static List<RepDossierListingDTO> getDossiersToExport(
        CoreSession session,
        SSPrincipal principal,
        String query
    ) {
        List<DocumentModel> documents = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            DossierConstants.QUESTION_DOCUMENT_TYPE,
            query,
            null
        );

        return documents
            .stream()
            .map(doc -> convertToDTO(session, principal, doc))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private static RepDossierListingDTO convertToDTO(CoreSession session, SSPrincipal principal, DocumentModel doc) {
        Dossier dossier = null;
        Question quest = null;
        RepDossierListingDTO dto = null;

        if (doc.getType().equals(DossierConstants.DOSSIER_DOCUMENT_TYPE)) {
            dossier = doc.getAdapter(Dossier.class);
            quest = dossier.getQuestion(session);
        } else if (doc.getType().equals(DossierConstants.QUESTION_DOCUMENT_TYPE)) {
            quest = doc.getAdapter(Question.class);
            dossier = quest.getDossier(session);
        }

        if (quest != null && dossier != null) {
            dto = createRepDossierListingDTO(session, principal, dossier, quest);
        }

        return dto;
    }

    private static RepDossierListingDTO createRepDossierListingDTO(
        CoreSession session,
        SSPrincipal principal,
        Dossier dossier,
        Question quest
    ) {
        RepDossierListingDTO dto = new RepDossierListingDTO();

        ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        List<DocumentModel> dossiersLinks = corbeilleService.findUpdatableDossierLinkForDossier(
            session,
            principal,
            dossier.getDocument()
        );

        List<String> routingTasks = getRoutingTasks(dossiersLinks);
        if (CollectionUtils.isNotEmpty(routingTasks)) {
            dto.setRoutingTaskType(StringUtils.join(routingTasks, ","));
        }

        Set<String> etatsQuestion = new HashSet<>();
        if (BooleanUtils.isTrue(quest.getEtatRappele())) {
            etatsQuestion.add(VocabularyConstants.ETAT_DOSSIER_RAPPELE);
        }

        if (BooleanUtils.isTrue(quest.getEtatSignale())) {
            etatsQuestion.add(VocabularyConstants.ETAT_DOSSIER_SIGNALE);
        }

        if (BooleanUtils.isTrue(quest.getEtatRenouvele())) {
            etatsQuestion.add(VocabularyConstants.ETAT_DOSSIER_RENOUVELE);
        }

        if (CollectionUtils.isNotEmpty(etatsQuestion)) {
            dto.setEtatQuestion(StringUtils.join(etatsQuestion, ","));
        }

        dto.setOrigineQuestion(quest.getOrigineQuestion());
        dto.setSourceNumeroQuestion(
            dossier.getNumeroQuestion() != null ? dossier.getNumeroQuestion().toString() : null
        );
        dto.setTypeQuestion(quest.getTypeQuestion());
        dto.setDatePublicationJO(quest.getDatePublicationJO() != null ? quest.getDatePublicationJO().getTime() : null);
        dto.setAuteur(quest.getNomCompletAuteur());
        dto.setMinistereAttributaire(quest.getIntituleMinistereAttributaire());
        dto.setMotCles(QuestionUtils.joinMotsClefs(quest));
        dto.setDelai(QuestionUtils.getDelaiExpiration(session, quest));

        ReponseFeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        List<DocumentModel> runningStepDocList = feuilleRouteService.getRunningSteps(
            session,
            dossier.getLastDocumentRoute()
        );

        String libelleDirections = "";
        if (CollectionUtils.isNotEmpty(runningStepDocList)) {
            libelleDirections =
                runningStepDocList
                    .stream()
                    .map(runningStepDoc -> runningStepDoc.getAdapter(SSRouteStep.class))
                    .map(SSRouteStep::getDirectionLabel)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }
        dto.setDirectionRunningStep(libelleDirections);

        return dto;
    }

    private static List<String> getRoutingTasks(List<DocumentModel> dossiersLinks) {
        final Map<String, String> libelleRoutingTask = VocabularyConstants.LIST_LIBELLE_ROUTING_TASK_PAR_ID;
        return dossiersLinks
            .stream()
            .map(dossierLinkDoc -> dossierLinkDoc.getAdapter(DossierLink.class))
            .map(dossierLink -> libelleRoutingTask.get(dossierLink.getRoutingTaskType()))
            .collect(Collectors.toList());
    }
}
