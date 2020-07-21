package fr.dila.reponses.web.flux;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.flux.HasInfoFlux;

/**
 * Gestion des actions relatives aux flux (signalement, renouvellement).
 * 
 * @author jgomez
 */
@Name("fluxActions")
@Scope(ScopeType.CONVERSATION)
public class FluxActionsBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long						serialVersionUID	= 1L;

	@In(create = true, required = true)
	protected transient CoreSession					documentManager;

	@In(create = true, required = false)
	protected transient DocumentsListsManagerBean	documentsListsManager;

	/**
	 * Retourne l'adapter permettant un accès aux informations de flux
	 * 
	 * @param dossier
	 * @return l'adapteur
	 * @throws ClientException
	 */
	public HasInfoFlux getHasInfoFlux(Dossier dossier) throws ClientException {
		if (dossier == null) {
			return null;
		}
		DocumentModel questionDoc = dossier.getQuestion(documentManager).getDocument();
		return questionDoc.getAdapter(HasInfoFlux.class);
	}

}
