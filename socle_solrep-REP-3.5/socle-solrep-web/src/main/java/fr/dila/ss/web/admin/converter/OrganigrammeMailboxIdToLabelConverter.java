package fr.dila.ss.web.admin.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Converter JSF qui fournit le label d'un poste.
 * 
 * @author Fabio Esposito
 */
public class OrganigrammeMailboxIdToLabelConverter implements Converter {

	/**
	 * Default constructor
	 */
	public OrganigrammeMailboxIdToLabelConverter() {
		// do nothing
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String string) {
		return string;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if (object instanceof String && !StringUtils.isEmpty((String) object)) {
			final String mailboxid = (String) object;
			try {
				final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(mailboxid);
				final OrganigrammeNode node = STServiceLocator.getSTPostesService().getPoste(posteId);
				if (node != null) {
					return node.getLabel();
				} else {
					return "**poste inconnu**";
				}
			} catch (ClientException e) {
				return null;
			}
		}
		return null;
	}

}
