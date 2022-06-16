package fr.dila.reponses.ui.services.impl;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.IndexationDTO;
import fr.dila.reponses.ui.services.ReponsesModeleFdrFicheUIService;
import fr.dila.reponses.ui.services.actions.IndexActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.th.bean.ReponsesModeleFdrForm;
import fr.dila.ss.ui.services.impl.SSAbstractModeleFdrFicheUIService;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ReponsesModeleFdrFicheUIServiceImpl
    extends SSAbstractModeleFdrFicheUIService<ReponsesModeleFdrForm, ReponsesFeuilleRoute>
    implements ReponsesModeleFdrFicheUIService {

    @Override
    protected ReponsesModeleFdrForm convertDocToModeleForm(
        SpecificContext context,
        DocumentModel doc,
        ReponsesModeleFdrForm modeleForm
    ) {
        OrganigrammeService organigrammeService = ReponsesServiceLocator.getReponsesOrganigrammeService();
        ReponsesFeuilleRoute modele = getFeuilleRouteAdapter(doc);
        super.convertDocToModeleForm(context, doc, modeleForm);

        modeleForm.setTitreQuestion(modele.getTitreQuestion());
        modeleForm.setIdDirection(modele.getIdDirectionPilote());
        modeleForm.setLibelleDirection(
            StringUtils.isNotEmpty(modele.getIdDirectionPilote())
                ? organigrammeService
                    .getOrganigrammeNodeById(modele.getIdDirectionPilote(), OrganigrammeType.DIRECTION)
                    .getLabel()
                : ""
        );
        if (modeleForm.getIdDirection() != null) {
            modeleForm.setMapDirection(
                (
                    Collections.singletonMap(
                        modeleForm.getIdDirection(),
                        STServiceLocator
                            .getOrganigrammeService()
                            .getOrganigrammeNodeById(modeleForm.getIdDirection(), OrganigrammeType.DIRECTION)
                            .getLabel()
                    )
                )
            );
        }
        modeleForm.setIndexationDTO(getIndexationDTO(doc));
        return modeleForm;
    }

    @Override
    protected ReponsesFeuilleRoute convertFormToFeuilleRoute(
        ReponsesModeleFdrForm modeleForm,
        ReponsesFeuilleRoute modele
    ) {
        DocumentModel modeleDoc = modele.getDocument();
        MapDoc2Bean.beanToDoc(modeleForm, modele.getDocument());
        ReponsesFeuilleRoute modeleReponses = getFeuilleRouteAdapter(modeleDoc);
        ReponsesModeleFdrForm reponsesModeleForm = modeleForm;
        modeleReponses.setTitreQuestion(reponsesModeleForm.getTitreQuestion());
        modeleReponses.setIdDirectionPilote(reponsesModeleForm.getIdDirection());
        modeleReponses.setIntituleDirectionPilote(reponsesModeleForm.getLibelleDirection());
        modeleReponses.setANAnalyses(reponsesModeleForm.getAnAnalyseComp());
        modeleReponses.setANRubrique(reponsesModeleForm.getAnRubriqueComp());
        modeleReponses.setANTeteAnalyse(reponsesModeleForm.getAnTAnalyseComp());
        modeleReponses.setSenatRenvois(reponsesModeleForm.getSeRenvoiComp());
        modeleReponses.setSenatRubrique(reponsesModeleForm.getSeRubriqueComp());
        modeleReponses.setSenatThemes(reponsesModeleForm.getSeThemeComp());
        modeleReponses.setMotsClesMinistere(reponsesModeleForm.getMotsClesMinisteres());
        return modeleReponses;
    }

    private static IndexationDTO getIndexationDTO(DocumentModel doc) {
        IndexActionService indexActionService = ReponsesActionsServiceLocator.getIndexActionService();

        IndexationDTO indexationDto = new IndexationDTO();
        indexationDto.setIndexationAN(
            indexActionService.getListIndexByZoneInMap(doc, VocabularyConstants.INDEXATION_ZONE_AN)
        );
        indexationDto.setIndexationSENAT(
            indexActionService.getListIndexByZoneInMap(doc, VocabularyConstants.INDEXATION_ZONE_SENAT)
        );
        indexationDto.setIndexationMinistere(
            indexActionService.getListIndexByZoneInMap(doc, VocabularyConstants.INDEXATION_ZONE_MINISTERE)
        );
        indexationDto.setDirectoriesAN(indexActionService.getDirectoriesByZone(VocabularyConstants.INDEXATION_ZONE_AN));
        indexationDto.setDirectoriesSENAT(
            indexActionService.getDirectoriesByZone(VocabularyConstants.INDEXATION_ZONE_SENAT)
        );
        indexationDto.setDirectoriesMinistere(
            indexActionService.getDirectoriesByZone(VocabularyConstants.INDEXATION_ZONE_MINISTERE)
        );

        return indexationDto;
    }
}
