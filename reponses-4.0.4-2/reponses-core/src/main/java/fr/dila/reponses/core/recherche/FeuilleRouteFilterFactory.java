package fr.dila.reponses.core.recherche;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.impl.LifeCycleFilter;

public class FeuilleRouteFilterFactory {
    public static final Filter RUNNING_STATE_FILTER = new LifeCycleFilter(
        FeuilleRouteElement.ElementLifeCycleState.running.toString(),
        false
    );
    public static final Filter PM_VALIDATION_TSK_TYPE_FILTER = new TaskTypeStepFilter(
        VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM
    );
}
