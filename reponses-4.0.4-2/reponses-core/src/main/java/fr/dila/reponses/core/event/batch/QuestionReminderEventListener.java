package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.core.event.work.QuestionReminderEventStep;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.event.batch.AbstractChunkedBatchJobEventListener;
import fr.dila.st.core.event.work.AbstractBatchProcessorStep;
import fr.dila.st.core.event.work.StepContext;
import fr.dila.st.core.event.work.StepDTO;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.CoreSession;

public class QuestionReminderEventListener extends AbstractChunkedBatchJobEventListener<StepDTO> {
    private static final STLogger LOGGER = STLogFactory.getLog(QuestionReminderEventListener.class);

    public QuestionReminderEventListener() {
        super(LOGGER, ReponsesEventConstant.QUESTION_REMINDER_EVENT, 5);
    }

    @Override
    protected List<StepDTO> readItems(CoreSession session) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();

        String delaiTraitementEtape = paramService.getParametreValue(
            session,
            ReponsesParametreConstant.DELAI_TRAITEMENT_ETAPE_PARAMETER_NAME
        );

        Map<String, List<String>> items = ReponsesServiceLocator
            .getFeuilleRouteService()
            .getRunningStepsSinceDaysByPoste(session, Integer.parseInt(delaiTraitementEtape));

        return items.entrySet().stream().map(e -> new StepDTO(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    @Override
    protected String getBatchResultJobMessage() {
        return "Lanceur du traitement des questions en souffrance";
    }

    @Override
    protected AbstractBatchProcessorStep<StepDTO> newBatchProcessorStep(StepContext stepContext, List<StepDTO> items) {
        return new QuestionReminderEventStep(stepContext, items);
    }
}
