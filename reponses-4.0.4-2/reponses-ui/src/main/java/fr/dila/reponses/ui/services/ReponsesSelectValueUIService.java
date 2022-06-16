package fr.dila.reponses.ui.services;

import fr.dila.ss.ui.services.SSSelectValueUIService;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.util.List;

public interface ReponsesSelectValueUIService extends SSSelectValueUIService {
    List<SelectValueDTO> getAllMinisteres();

    List<SelectValueDTO> getEtatsQuestions();

    List<SelectValueDTO> getGroupesPolitiques();

    List<SelectValueDTO> getLegislatures();

    List<SelectValueDTO> getQuestionTypes();

    List<SelectValueDTO> getRubriquesAN();

    List<SelectValueDTO> getRubriquesSenat();

    List<SelectValueDTO> getStatutsEtapeRecherche();

    List<SelectValueDTO> getRouteStepLifeCycleStates();

    List<SelectValueDTO> getTetesAnalyseAN();

    List<SelectValueDTO> getThemesSenat();

    List<SelectValueDTO> getValidationStatutsEtape();

    List<SelectValueDTO> getRoutingTaskTypesFiltered();
}
