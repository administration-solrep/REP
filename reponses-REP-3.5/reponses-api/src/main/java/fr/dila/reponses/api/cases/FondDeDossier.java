package fr.dila.reponses.api.cases;

import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.domain.STDomainObject;

/**
 * Fond De Dossier .
 * <p>
 * Represents the "fond de dossier" item in the dossier.
 *
 */
public interface FondDeDossier extends STDomainObject {

    //TODO get the file; repository and Fond de dossier item
	
	/**
	 * Get the Repertoire document model list.
	 */
	List<DocumentModel> getRepertoireDocument(CoreSession session);
	
	void setRepertoireParlementId(String documentId);
	
	void setRepertoireMinistereId(String documentId);
	
	void setRepertoireSggId(String documentId);
	
	String getRepertoireParlementId();
	
	String getRepertoireMinistereId();
	
	String getRepertoireSggId();
	
}
