package fr.dila.reponses.ui.services;

import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface ParapheurUIService {
    ParapheurDTO getParapheur(SpecificContext context);

    List<SelectValueDTO> getVersionDTOs(SpecificContext context);
}
