package fr.dila.reponses.core.organigramme;

import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparateur sur les ordres protocolaires pour les organigrammeNodeTimbres.
 *
 */
public class ReponsesOrderTimbreNodeComparator implements Comparator<OrganigrammeNodeTimbreDTO>, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1845639752822034014L;

    /**
     * default constructor
     */
    public ReponsesOrderTimbreNodeComparator() {
        super();
    }

    /**
     * Renvoi 0 si node1 et node2 sont null, ou node1.getOrder()==node2.getOrder()
     * Renvoi 1 si node2 est null, ou node1.getOrder()>node2.getOrder()
     * Renvoi -1 si node1 est null, ou node1.getOrder()<node2.getOrder()
     *
     * @param node1
     * @param node2
     * @return
     */
    public int compare(OrganigrammeNodeTimbreDTO node1, OrganigrammeNodeTimbreDTO node2) {
        int result = 0;
        if (node1 == null) {
            if (node2 != null) {
                result = -1;
            }
            return result;
        }
        if (node2 == null) {
            return 1;
        }
        if (node1.getOrder() == node2.getOrder()) {
            return node1.getLabel().compareTo(node2.getLabel());
        } else {
            if (node1.getOrder() > node2.getOrder()) {
                result = 1;
            } else {
                result = -1;
            }
        }

        return result;
    }
}
