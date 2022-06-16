package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderServiceImpl.QU_DATE_DEBUT_MINISTERE_INTERROGE;
import static fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderServiceImpl.QU_DATE_FIN_MINISTERE_INTERROGE;
import static fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderServiceImpl.QU_ID_MINISTERE_INTERROGE;
import static fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderServiceImpl.QU_INTITULE_MINISTERE;
import static java.util.function.Function.identity;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.dila.reponses.api.enumeration.QuestionTypesEnum;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.ReponsesSelectValueUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.enums.EtapeLifeCycle;
import fr.dila.ss.ui.services.impl.SSSelectValueUIServiceImpl;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ReponsesSelectValueUIServiceImpl
    extends SSSelectValueUIServiceImpl
    implements ReponsesSelectValueUIService {

    @Override
    public List<SelectValueDTO> getAllMinisteres() {
        return getSelectValues(
            ReponsesUIServiceLocator.getMinistereSuggestionProviderService().getAllMinistereListRecherche(),
            entry -> (String) entry.get(QU_ID_MINISTERE_INTERROGE),
            ReponsesSelectValueUIServiceImpl::getLabelFromMinistereInterroge
        );
    }

    @Override
    public List<SelectValueDTO> getEtatsQuestions() {
        return getSelectValues(ReponsesServiceLocator.getEtatQuestionService(), Object::toString);
    }

    @Override
    public List<SelectValueDTO> getGroupesPolitiques() {
        return getSelectValues(ReponsesServiceLocator.getGroupePolitiqueService(), identity());
    }

    @Override
    public List<SelectValueDTO> getLegislatures() {
        return getSelectValues(ReponsesServiceLocator.getLegislatureService(), Object::toString);
    }

    @Override
    public List<SelectValueDTO> getQuestionTypes() {
        return getSelectValues(ReponsesServiceLocator.getQuestionTypeService(), QuestionTypesEnum::name);
    }

    @Override
    public List<SelectValueDTO> getRubriquesAN() {
        return getSelectValues(ReponsesServiceLocator.getRubriqueANService(), Object::toString);
    }

    @Override
    public List<SelectValueDTO> getRubriquesSenat() {
        return getSelectValues(ReponsesServiceLocator.getRubriqueSenatService(), Object::toString);
    }

    @Override
    public List<SelectValueDTO> getStatutsEtapeRecherche() {
        return getSelectValues(ReponsesServiceLocator.getStatutEtapeRechercheService(), identity());
    }

    @Override
    public List<SelectValueDTO> getRouteStepLifeCycleStates() {
        return getSelectValues(
            ReponsesUIServiceLocator.getRequeteUIService().getRequeteurStepStates(),
            EtapeLifeCycle::getValue,
            state -> ResourceHelper.getString(state.getLabelKey())
        );
    }

    @Override
    public List<SelectValueDTO> getTetesAnalyseAN() {
        return getSelectValues(ReponsesServiceLocator.getTeteAnalyseANService(), Object::toString);
    }

    @Override
    public List<SelectValueDTO> getThemesSenat() {
        return getSelectValues(ReponsesServiceLocator.getThemeSenatService(), Object::toString);
    }

    @Override
    public List<SelectValueDTO> getValidationStatutsEtape() {
        return getSelectValues(ReponsesServiceLocator.getValidationStatutEtapeService(), Object::toString);
    }

    @Override
    public List<SelectValueDTO> getRoutingTaskTypesFiltered() {
        return getSelectValues(ReponsesServiceLocator.getReponsesRoutingTaskTypeService(), Object::toString);
    }

    private static String getLabelFromMinistereInterroge(Map<String, Serializable> ministereInterroge) {
        StringBuilder label = new StringBuilder(ministereInterroge.get(QU_INTITULE_MINISTERE).toString());
        if (isNotBlank(ministereInterroge.get(QU_DATE_DEBUT_MINISTERE_INTERROGE).toString())) {
            label
                .append(' ')
                .append(ministereInterroge.get(QU_DATE_DEBUT_MINISTERE_INTERROGE))
                .append(ministereInterroge.get(QU_DATE_FIN_MINISTERE_INTERROGE));
        }
        return label.toString();
    }
}
