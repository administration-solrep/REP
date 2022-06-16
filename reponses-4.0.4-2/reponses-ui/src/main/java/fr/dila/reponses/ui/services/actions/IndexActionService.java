package fr.dila.reponses.ui.services.actions;

import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.ui.bean.IndexationDTO;
import fr.dila.reponses.ui.bean.IndexationItemDTO;
import fr.dila.reponses.ui.bean.VocSugUI;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface IndexActionService {
    Map<String, VocSugUI> getVocMap();

    List<String[]> getListIndexByZone(DocumentModel indexableDocument, String indexationzoneName);

    Map<String, IndexationItemDTO> getListIndexByZoneInMap(DocumentModel indexableDocument, String indexationzoneName);

    ReponsesIndexableDocument getDocumentAdapted(DocumentModel doc);

    List<String> getDirectoriesByZone(String indexationZone);

    DocumentModel getCurrentIndexation(DocumentModel questionDoc, CoreSession session);

    void initIndexationDtoDirectories(IndexationDTO indexationDTO);
}
