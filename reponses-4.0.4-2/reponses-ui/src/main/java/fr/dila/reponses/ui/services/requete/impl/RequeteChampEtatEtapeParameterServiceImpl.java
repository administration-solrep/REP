package fr.dila.reponses.ui.services.requete.impl;

import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.api.constant.SSRechercheChampConstants;
import fr.dila.st.core.requete.recherchechamp.ChampParameter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class RequeteChampEtatEtapeParameterServiceImpl implements ChampParameter {

    @Override
    public Map<String, Serializable> getAdditionalParameters() {
        return ImmutableMap.of(
            SSRechercheChampConstants.OPTIONS_PARAMETER_NAME,
            new ArrayList<>(ReponsesUIServiceLocator.getSelectValueUIService().getRouteStepLifeCycleStates())
        );
    }
}
