package fr.dila.ss.web.helper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.ss.web.admin.organigramme.OrganigrammeManagerActionsBean;

/**
 * Bean Seam permettant de récupérer les noms des postes / ministères de l'organigramme.
 * 
 * @author jtremeaux
 */
@Name("organigrammeHelper")
@Scope(ScopeType.EVENT)
public class OrganigrammeHelperBean implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long							serialVersionUID	= 1L;

	/**
	 * Logger.
	 */
	private static final Log							LOG					= LogFactory
																					.getLog(OrganigrammeHelperBean.class);

	@In(create = true, required = false)
	protected transient CoreSession						documentManager;

	@In(create = true, required = false)
	protected transient OrganigrammeManagerActionsBean	organigrammeManagerActions;

	/**
	 * Default constructor
	 */
	public OrganigrammeHelperBean() {
		// do nothing
	}

	/**
	 * Retourne le nom des ministères en fonction de son identifiant technique.
	 * 
	 * @param organigrammeNodeType
	 *            Type de noeud de l'organigramme
	 * @param nodeId
	 *            Identifiant technique du noeud de l'organigramme
	 * @return Libellé du ministère
	 */
	public String getOrganigrammeNodeLabel(String selectionType, String nodeId) {
		// Étapes sans destinataire
		if (StringUtils.isEmpty(nodeId)) {
			return "";
		}

		String label = "**nom du ministère inconnu**";
		try {
			label = organigrammeManagerActions.getOrganigrammeNodeLabel(selectionType, nodeId);
		} catch (ClientException e) {
			LOG.warn(e, e);
		}
		return label;
	}
}
