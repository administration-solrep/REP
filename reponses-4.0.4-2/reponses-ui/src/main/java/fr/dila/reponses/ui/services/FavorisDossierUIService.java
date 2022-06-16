package fr.dila.reponses.ui.services;

import fr.dila.reponses.ui.bean.FavorisTravailList;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface FavorisDossierUIService {
    FavorisTravailList getFavoris(SpecificContext context);

    RepDossierList getDossierList(SpecificContext context);

    List<SelectValueDTO> getFavorisSelectValueDtos(SpecificContext context);

    void createAndAddFavoris(SpecificContext context);

    void addFavoris(SpecificContext context);

    void removeFavoris(SpecificContext context);
}
