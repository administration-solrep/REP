package fr.dila.reponses.core.recherche;

import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.impl.LifeCycleFilter;

import fr.dila.ecm.platform.routing.api.DocumentRouteElement.ElementLifeCycleState;
import fr.dila.reponses.api.constant.VocabularyConstants;

public class FeuilleRouteFilterFactory {

    public final static Filter RUNNING_STATE_FILTER = new LifeCycleFilter(ElementLifeCycleState.running.toString(), false);
    public final static Filter PM_VALIDATION_TSK_TYPE_FILTER = new TaskTypeStepFilter(VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM);
}
