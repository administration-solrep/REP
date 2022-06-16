package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.ui.services.ReponsesUIServiceLocator.getFavorisDossierUIService;

import fr.dila.reponses.ui.bean.FavorisTravailList;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.FavorisTravailComponentService;
import fr.dila.st.ui.services.impl.FragmentServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.HashMap;
import java.util.Map;

public class FavorisTravailComponentServiceImpl extends FragmentServiceImpl implements FavorisTravailComponentService {

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> returnMap = new HashMap<>();
        FavorisTravailList favoris = getFavorisDossierUIService().getFavoris(context);
        returnMap.put("favoris", favoris);
        returnMap.put("action", context.getAction(ReponsesActionEnum.FAVORI_TRAVAIL_SUPPRIMER));
        returnMap.put("actionSupprimerTout", context.getAction(ReponsesActionEnum.FAVORI_TRAVAIL_SUPPRIMER_TOUT));
        return returnMap;
    }
}
