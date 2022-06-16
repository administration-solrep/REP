package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.api.constant.VocabularyConstants.INDEXATION_ZONE_AN;
import static fr.dila.reponses.api.constant.VocabularyConstants.INDEXATION_ZONE_MINISTERE;
import static fr.dila.reponses.api.constant.VocabularyConstants.INDEXATION_ZONE_SENAT;
import static fr.dila.reponses.api.constant.VocabularyConstants.ORIGINE_QUESTION;
import static fr.dila.reponses.api.constant.VocabularyConstants.TYPE_QUESTION;
import static java.util.stream.Collectors.toList;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.service.DossierService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.BordereauDTO;
import fr.dila.reponses.ui.bean.IndexationDTO;
import fr.dila.reponses.ui.services.BordereauUIService;
import fr.dila.reponses.ui.services.actions.IndexActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesBordereauActionService;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

public class BordereauUIServiceImpl implements BordereauUIService {

    @Override
    public BordereauDTO getBordereau(SpecificContext context) {
        DocumentModel dossierDoc = context.getCurrentDocument();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        // Création du DTO avec les données de la question
        Question question = dossier.getQuestion(context.getSession());
        BordereauDTO dto = MapDoc2Bean.docToBean(question.getDocument(), BordereauDTO.class);

        // Mise à jour des ID/Vocabulaire en label pour l'affichage
        dto.setOrigineQuestion(getEntryLabel(ORIGINE_QUESTION, dto.getOrigineQuestion()));
        dto.setTypeQuestion(getEntryLabel(TYPE_QUESTION, dto.getTypeQuestion()));
        dto.setIntituleMinistereRattachement(getEntiteNodeLabel(dto.getMinistereRattachement()));
        dto.setIntituleDirectionPilote(
            STServiceLocator.getSTUsAndDirectionService().getLabel(dto.getDirectionPilote())
        );
        ReponsesBordereauActionService bordereauActionService = ReponsesActionsServiceLocator.getReponsesBordereauActionService();
        DossierService dossierService = ReponsesServiceLocator.getDossierService();
        dto.setPartMiseAJour(bordereauActionService.getPartMiseAJour(question));

        Reponse reponse = dossier.getReponse(context.getSession());
        dto.setPartReponse(bordereauActionService.getPartReponse(reponse));
        dto.setPartIndexationAN(bordereauActionService.getPartIndexationAN(question));
        dto.setPartIndexationSENAT(bordereauActionService.getPartIndexationSE(question));
        dto.setPartEditableIndexationComplementaire(
            bordereauActionService.getPartEditableIndexationComplementaire(context, dossierDoc.getId())
        );
        dto.setPartIndexationComplementaireAN(bordereauActionService.getPartIndexationComplementaireAN(question));
        dto.setPartIndexationComplementaireSE(bordereauActionService.getPartIndexationComplementaireSE(question));
        dto.setPartIndexationComplementaireMotCle(
            bordereauActionService.getPartIndexationComplementaireMotCle(question)
        );
        dto.setPartFeuilleRoute(bordereauActionService.getPartFeuilleRoute(question, dossier));
        dto.setPartEditMinistereRattachement(bordereauActionService.getPartEditMinistereRattachement(context));
        dto.setPartEditDirectionPilote(bordereauActionService.getPartEditDirectionPilote(context));
        dto.setIsQuestionTypeEcrite(question.isQuestionTypeEcrite());
        dto.setIsEdit(STActionsServiceLocator.getDossierLockActionService().getCanUnlockCurrentDossier(context));

        dto.setDatePublicationJOReponse(reponse.getDateJOreponse());
        dto.setPageJOReponse(reponse.getPageJOreponse());
        dto.setRenouvellements(mapRenouvellementDates(bordereauActionService.getRenouvellements(question)));
        dto.setHistoriquesAttributionDto(
            bordereauActionService.getHistoriqueAttributionFeuilleRoute(context.getSession(), dossier)
        );
        dto.setListingUnitesStruct(
            dossierService.getListingUnitesStruct(context.getCurrentDocument(), context.getSession())
        );
        if (dto.getMinistereRattachement() != null) {
            dto.setMapMinistereRattachement(
                Collections.singletonMap(
                    dto.getMinistereRattachement(),
                    STServiceLocator
                        .getOrganigrammeService()
                        .getOrganigrammeNodeById(dto.getMinistereRattachement(), OrganigrammeType.MINISTERE)
                        .getLabel()
                )
            );
        }

        SSRouteStep currentStep = ReponsesActionsServiceLocator
            .getReponsesDocumentRoutingActionService()
            .getCurrentStep(dossierDoc, context.getSession());
        if (currentStep != null) {
            dto.setTacheCoursDate(currentStep.getDateDebutEtape());
            dto.setTacheCoursPoste(
                STServiceLocator
                    .getSTPostesService()
                    .getPoste(
                        SSServiceLocator
                            .getMailboxPosteService()
                            .getPosteIdFromMailboxId(currentStep.getDistributionMailboxId())
                    )
                    .getLabel()
            );
            dto.setTacheCoursDeadline(currentStep.getDeadLine());
            dto.setTacheCoursAuto(currentStep.isAutomaticValidation());
        }
        // Date de Réponse attendu du signalement
        question.getDateReponseSignalement().ifPresent(dto::setDateAttendueReponseSignalement);

        dto.setTacheFinalePoste(dossierService.getFinalStepLabel(context.getSession(), dossier));
        dto.setIndexationDTO(getIndexationDTO(context, dossier));
        dto.setCurrentIndexationDTO(getCurrentIndexationDTO(context, dossier));

        // Mise à jour du DTO avec les données du dossier
        dto = MapDoc2Bean.docToBean(dossierDoc, dto);

        // Mise à jour des ID/Vocabulaire en label pour l'affichage
        dto.setMinistereAttributaireCourant(getEntiteNodeLabel(dto.getMinistereAttributaireCourant()));
        return dto;
    }

    private static IndexationDTO getIndexationDTO(SpecificContext context, Dossier dossier) {
        IndexActionService indexActionService = ReponsesActionsServiceLocator.getIndexActionService();

        IndexationDTO indexationDto = new IndexationDTO();
        indexationDto.setIndexationAN(
            indexActionService.getListIndexByZoneInMap(
                dossier.getQuestion(context.getSession()).getDocument(),
                INDEXATION_ZONE_AN
            )
        );
        indexationDto.setIndexationSENAT(
            indexActionService.getListIndexByZoneInMap(
                dossier.getQuestion(context.getSession()).getDocument(),
                INDEXATION_ZONE_SENAT
            )
        );
        indexationDto.setIndexationMinistere(
            indexActionService.getListIndexByZoneInMap(
                dossier.getQuestion(context.getSession()).getDocument(),
                INDEXATION_ZONE_MINISTERE
            )
        );
        indexationDto.setDirectoriesAN(indexActionService.getDirectoriesByZone(INDEXATION_ZONE_AN));
        indexationDto.setDirectoriesSENAT(indexActionService.getDirectoriesByZone(INDEXATION_ZONE_SENAT));
        indexationDto.setDirectoriesMinistere(indexActionService.getDirectoriesByZone(INDEXATION_ZONE_MINISTERE));

        return indexationDto;
    }

    private static IndexationDTO getCurrentIndexationDTO(SpecificContext context, Dossier dossier) {
        IndexActionService indexActionService = ReponsesActionsServiceLocator.getIndexActionService();

        DocumentModel currentIndexation = indexActionService.getCurrentIndexation(
            dossier.getQuestion(context.getSession()).getDocument(),
            context.getSession()
        );

        IndexationDTO currentIndexationDto = new IndexationDTO();
        currentIndexationDto.setIndexationAN(
            indexActionService.getListIndexByZoneInMap(currentIndexation, INDEXATION_ZONE_AN)
        );
        currentIndexationDto.setIndexationSENAT(
            indexActionService.getListIndexByZoneInMap(currentIndexation, INDEXATION_ZONE_SENAT)
        );
        currentIndexationDto.setIndexationMinistere(
            indexActionService.getListIndexByZoneInMap(currentIndexation, INDEXATION_ZONE_MINISTERE)
        );

        return currentIndexationDto;
    }

    private static String getEntryLabel(String directoryName, String entry) {
        return Optional
            .ofNullable(entry)
            .map(entryId -> ReponsesServiceLocator.getVocabularyService().getEntryLabel(directoryName, entryId))
            .orElse(null);
    }

    private static String getEntiteNodeLabel(String entite) {
        return Optional
            .ofNullable(entite)
            .filter(StringUtils::isNotBlank)
            .map(entiteId -> STServiceLocator.getSTMinisteresService().getEntiteNode(entiteId).getLabel())
            .orElse(null);
    }

    private static List<Calendar> mapRenouvellementDates(List<Renouvellement> renouvellements) {
        return renouvellements
            .stream()
            .map(renouvellement -> DateUtil.toCalendarFromNotNullDate(renouvellement.getDateEffet()))
            .collect(toList());
    }
}
