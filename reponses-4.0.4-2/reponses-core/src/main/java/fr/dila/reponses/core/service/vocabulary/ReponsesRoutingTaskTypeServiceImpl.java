package fr.dila.reponses.core.service.vocabulary;

import com.google.common.collect.ImmutableList;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.vocabulary.ReponsesRoutingTaskTypeService;
import fr.dila.ss.core.service.vocabulary.RoutingTaskTypeServiceImpl;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class ReponsesRoutingTaskTypeServiceImpl
    extends RoutingTaskTypeServiceImpl
    implements ReponsesRoutingTaskTypeService {
    /**
     * List des types d'étapes qui ne peuvent pas être ajoutées manuellement sur une feuille de route
     */
    private static final List<Integer> ROUTING_TASK_TYPE_TO_EXCLUDE = ImmutableList.of(
        Integer.valueOf(VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE),
        Integer.valueOf(VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION),
        Integer.valueOf(VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION)
    );

    @Override
    public List<ImmutablePair<Integer, String>> getEntries() {
        return getEntriesFiltered(ROUTING_TASK_TYPE_TO_EXCLUDE);
    }
}
