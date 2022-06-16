package fr.dila.reponses.ui.services;

import fr.dila.reponses.ui.bean.FavorisPlanClassementDTO;
import org.nuxeo.ecm.core.api.CoreSession;

public interface FavorisUIService {
    FavorisPlanClassementDTO getFavorisDTO(CoreSession session);
}
