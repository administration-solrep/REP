package fr.dila.reponses.api.cases;

import fr.dila.st.api.domain.STDomainObject;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fond De Dossier .
 * <p>
 * Represents the "fond de dossier" item in the dossier.
 *
 */
public interface FondDeDossier extends STDomainObject {
    List<DocumentModel> getRepertoireDocument(CoreSession session);

    void setRepertoireParlementId(String documentId);

    void setRepertoireMinistereId(String documentId);

    void setRepertoireSggId(String documentId);

    String getRepertoireParlementId();

    String getRepertoireMinistereId();

    String getRepertoireSggId();
}
