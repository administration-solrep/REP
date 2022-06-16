package fr.dila.reponses.ui.services.impl;

import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.ss.ui.services.SSRoutingTaskFilterService;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

public class RepRoutingTaskFilterServiceImpl implements SSRoutingTaskFilterService {
    private static final Map<String, Boolean> ROUTING_TASK_TYPE_ACCEPTED = new ImmutableMap.Builder<String, Boolean>()
        .put(VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION, false)
        .put(VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION, false)
        .put(VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE, false)
        .build();

    @Override
    public boolean accept(CoreSession session, String routingTaskType) {
        return ROUTING_TASK_TYPE_ACCEPTED
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().equals(routingTaskType))
            .findFirst()
            .map(Map.Entry::getValue)
            .orElse(true);
    }
}
