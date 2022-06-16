package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.enumeration.DailyBatchEnum;
import fr.dila.reponses.api.enumeration.MonthlyBatchEnum;
import fr.dila.reponses.api.enumeration.QuestionTypesEnum;
import fr.dila.reponses.api.enumeration.WeeklyBatchEnum;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

/**
 * Lanceur général des batchs de l'application REPONSES
 *
 */
public class LanceurGeneralBatchListener extends AbstractBatchEventListener implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(LanceurGeneralBatchListener.class);

    private static final String BATCH_HEBDO_PREFIX = "batch-hebdo-";

    public LanceurGeneralBatchListener() {
        super(LOGGER, STEventConstant.LANCEUR_BATCH_EVENT);
    }

    @Override
    protected void processEvent(final CoreSession session, final Event event) {
        final Calendar today = Calendar.getInstance();
        final long startTime = today.getTimeInMillis();
        Integer weekDay = today.get(Calendar.DAY_OF_WEEK);
        try {
            LOGGER.info(session, STLogEnumImpl.INIT_B_LANCEUR_GENERAL_TEC);
            final EventProducer eventProducer = STServiceLocator.getEventProducer();
            final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
            eventProperties.put(STEventConstant.BATCH_EVENT_PROPERTY_PARENT_ID, batchLoggerId);

            for (DailyBatchEnum dailyBatch : DailyBatchEnum.values()) {
                InlineEventContext inlineEventContext = new InlineEventContext(
                    session,
                    session.getPrincipal(),
                    eventProperties
                );
                eventProducer.fireEvent(inlineEventContext.newEvent(dailyBatch.getEventName()));
                commitAndRestartTransaction(session, true);
            }

            STParametreService parametreService = STServiceLocator.getSTParametreService();

            // Batchs hebdomadaires
            for (WeeklyBatchEnum weeklyBatch : WeeklyBatchEnum.values()) {
                if (ReponsesEventConstant.EXTRACTION_QUESTIONS_BATCH_EVENT.equals(weeklyBatch.getEventName())) {
                    // Cas particulier du batch d'extraction des questions : pas de paramètre, il
                    // est déclenché tous les vendredis
                    if (today.get(Calendar.DAY_OF_WEEK) == 6) {
                        executeExtractionQuestionsBatch(session, true, eventProducer, eventProperties);
                    }
                } else {
                    String paramValue = parametreService.getParametreValue(
                        session,
                        BATCH_HEBDO_PREFIX + weeklyBatch.getEventName()
                    );
                    if (paramValue == null) {
                        LOGGER.error(
                            session,
                            STLogEnumImpl.FAIL_PROCESS_B_LANCEUR_GENERAL_TEC,
                            weeklyBatch.getEventName() +
                            " est déclaré comme étant un batch hebdomadaire, mais n'a pas de paramètre pour fixer son jour d'éxécution"
                        );
                    } else {
                        JoursSemaine jourSemaine = JoursSemaine.valueOf(paramValue.trim().toUpperCase());
                        if (jourSemaine != null) {
                            Integer batchDay = jourSemaine.getCalValue();
                            if (batchDay.equals(weekDay)) {
                                InlineEventContext inlineEventContext = new InlineEventContext(
                                    session,
                                    session.getPrincipal(),
                                    eventProperties
                                );
                                eventProducer.fireEvent(inlineEventContext.newEvent(weeklyBatch.getEventName()));
                                commitAndRestartTransaction(session, true);
                            }
                        }
                    }
                }
            }

            // batchs mensuels déclenchés tous les 28ème jour du mois
            if (today.get(Calendar.DAY_OF_MONTH) == 28) {
                for (MonthlyBatchEnum monthlyBatch : MonthlyBatchEnum.values()) {
                    if (ReponsesEventConstant.EXTRACTION_QUESTIONS_BATCH_EVENT.equals(monthlyBatch.getEventName())) {
                        executeExtractionQuestionsBatch(session, false, eventProducer, eventProperties);
                    } else {
                        InlineEventContext inlineEventContext = new InlineEventContext(
                            session,
                            session.getPrincipal(),
                            eventProperties
                        );
                        eventProducer.fireEvent(inlineEventContext.newEvent(monthlyBatch.getEventName()));
                        commitAndRestartTransaction(session, true);
                    }
                }
            }
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_PROCESS_B_LANCEUR_GENERAL_TEC, exc);
            ++errorCount;
        }
        final long endTime = Calendar.getInstance().getTimeInMillis();
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Exécution du lanceur général",
                endTime - startTime
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, STLogEnumImpl.END_B_LANCEUR_GENERAL_TEC);
    }

    private void executeExtractionQuestionsBatch(
        final CoreSession session,
        final boolean open,
        final EventProducer eventProducer,
        final Map<String, Serializable> eventProperties
    ) {
        if (open) {
            eventProperties.put("QSTATUT", "OUVERTES");
        } else {
            eventProperties.put("QSTATUT", "FERMEES");
        }
        eventProperties.put("QTYPE", QuestionTypesEnum.QE.toString());
        eventProperties.put("QSOURCE", "AN");

        InlineEventContext inlineEventContext = new InlineEventContext(
            session,
            session.getPrincipal(),
            eventProperties
        );
        eventProducer.fireEvent(inlineEventContext.newEvent(ReponsesEventConstant.EXTRACTION_QUESTIONS_BATCH_EVENT));

        eventProperties.put("QSOURCE", "SENAT");

        inlineEventContext = new InlineEventContext(session, session.getPrincipal(), eventProperties);
        eventProducer.fireEvent(inlineEventContext.newEvent(ReponsesEventConstant.EXTRACTION_QUESTIONS_BATCH_EVENT));
        commitAndRestartTransaction(session, true);
    }

    @Override
    public STLogEnum getStoppageCode() {
        return STLogEnumImpl.CANCEL_B_LANCEUR_GENERAL_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
