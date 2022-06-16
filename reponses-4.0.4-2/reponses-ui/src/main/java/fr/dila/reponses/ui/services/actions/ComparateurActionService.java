package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.ui.bean.CompareVersionDTO;
import fr.dila.st.ui.th.model.SpecificContext;

public interface ComparateurActionService {
    public CompareVersionDTO getVersionTexts(SpecificContext context, String idVersion1, String idVersion2);
}
