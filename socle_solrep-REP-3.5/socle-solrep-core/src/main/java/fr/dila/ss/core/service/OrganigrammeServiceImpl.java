package fr.dila.ss.core.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.st.api.exception.LocalizedClientException;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Implémentation du service de gestion de l'organigramme.
 * 
 * @author FEO
 */
public class OrganigrammeServiceImpl extends fr.dila.st.core.service.organigramme.OrganigrammeServiceImpl {
	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= -2392698015083550568L;

	/**
	 * Default constructor
	 */
	public OrganigrammeServiceImpl() {
		super();
	}

	@Override
	protected void validateDeleteNode(CoreSession coreSession, OrganigrammeNode node) throws ClientException {
		// Cherche les postes dans les enfants si des FDR
		// actives sont liées à ces postes ou si un des utilisateurs du poste n'est associé à aucun autre
		SSFeuilleRouteService fdrService = SSServiceLocator.getSSFeuilleRouteService();
		boolean isActive = false;
		boolean onePosteOnly = false;
		List<OrganigrammeNode> nodeList = getChildrenList(null, node, Boolean.TRUE);

		if (node instanceof PosteNode) {
			isActive = fdrService.isActiveOrFutureRouteStepForPosteId(coreSession, node.getId());
			if (isActive) {
				throw new LocalizedClientException("organigramme.error.delete.fdrActive");
			}

			onePosteOnly = STServiceLocator.getSTPostesService().userHasOnePosteOnly((PosteNode) node);
			if (onePosteOnly) {
				throw new LocalizedClientException("organigramme.error.delete.onlyOnePoste");
			}

		}

		for (OrganigrammeNode childNode : nodeList) {
			validateDeleteNode(coreSession, childNode);
		}
	}
}
