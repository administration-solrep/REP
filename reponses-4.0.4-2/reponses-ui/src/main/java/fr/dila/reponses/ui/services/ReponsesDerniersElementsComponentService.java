package fr.dila.reponses.ui.services;

import fr.dila.ss.ui.bean.DernierElementDTO;
import fr.dila.ss.ui.services.SSDerniersElementsComponentService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface ReponsesDerniersElementsComponentService extends SSDerniersElementsComponentService {
    @Override
    List<DernierElementDTO> getDernierElementsDTOFromDocs(SpecificContext context);
}
