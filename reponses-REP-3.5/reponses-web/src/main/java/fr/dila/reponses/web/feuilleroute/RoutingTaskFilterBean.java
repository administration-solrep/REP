package fr.dila.reponses.web.feuilleroute;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import fr.dila.reponses.api.constant.VocabularyConstants;

/**
 * WebBean qui permet de filtrer les types d'étape de feuille de route.
 * Prend en paramètre des documents éléments du vocabulaire routing_task.
 *
 * @author jtremeaux
 * @author bgamard
 */
@Name("routingTaskFilter")
@Scope(ScopeType.EVENT)
public class RoutingTaskFilterBean implements Filter {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    @In(required = true, create = true)
    protected NuxeoPrincipal currentUser;

    @Override
    public boolean accept(DocumentModel doc) {
        // L'étape "Pour réorientation" ne peut pas être créée
        if (VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION.equals(doc.getId())) {
            return false;
        }

        // L'étape "Pour réattribution" ne peut pas être créée
        if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(doc.getId())) {
            return false;
        }
        
        // L'étape "Pour arbitrage" ne peut pas être créée
        if (VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(doc.getId())) {
            return false;
        }
        
        return true;
    }
}
