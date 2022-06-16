package fr.dila.reponses.ui.services.impl;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.reponses.ui.bean.FavorisPlanClassementDTO;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.FavorisPlanClassementComponentService;
import fr.dila.reponses.ui.services.FavorisUIService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.HashMap;
import java.util.Map;

public class FavorisPlanClassementComponentServiceImpl implements FavorisPlanClassementComponentService {
    private static final String SESSION_KEY = "favorisPlanClassement";

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> returnMap = new HashMap<>();
        getRequiredService(FavorisUIService.class);
        FavorisPlanClassementDTO dto = getRequiredService(FavorisUIService.class).getFavorisDTO(context.getSession());
        returnMap.put("favorisPlanClassementMap", dto);
        returnMap.put("actionRemove", context.getAction(ReponsesActionEnum.REMOVE_FAVORIS_PC));
        // On sauvegarde en session le DTO et l'item actif
        context.getWebcontext().getUserSession().put(SESSION_KEY, dto);
        return returnMap;
    }
}
