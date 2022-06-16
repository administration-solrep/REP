package fr.dila.reponses.core.operation.nxshell;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.enumeration.QuestionTypesEnum;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

@Operation(
    id = ExtraireQuestionsOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "ExtraireQuestions",
    description = "Permet d'extraire les questions au format xml"
)
public class ExtraireQuestionsOperation {
    public static final String ID = "Reponses.Extraire.Question";

    private static final STLogger LOGGER = STLogFactory.getLog(ExtraireQuestionsOperation.class);

    private static final String QTYPE_PROPERTY = "QTYPE";
    private static final String QSTATUT_PROPERTY = "QSTATUT";
    private static final String QSOURCE_PROPERTY = "QSOURCE";

    private static final String ORIGINE_SENAT = "SENAT";
    private static final String ORIGINE_AN = "AN";

    @Context
    protected CoreSession session;

    @Param(name = "statut", required = true)
    protected String statut;

    @Param(name = "origine", required = false)
    protected String origine = null;

    @Param(name = "type", required = false)
    protected String type = "QE";

    @OperationMethod
    public void run() {
        try {
            LOGGER.info(
                session,
                ReponsesLogEnumImpl.INIT_OPERATION_EXTR_QUEST_TEC,
                "-------------------------------------------------------------------------------"
            );

            final EventProducer eventProducer = STServiceLocator.getEventProducer();
            final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
            String statutParam = null;
            if ("encours".equals(statut)) {
                statutParam = "OUVERTES";
            } else if ("closes".equals(statut)) {
                statutParam = "FERMEES";
            } else {
                LOGGER.error(
                    session,
                    ReponsesLogEnumImpl.FAIL_PROCESS_OPERATION_EXTR_QUEST_TEC,
                    "Le paramètre 'statut' doit être égal à 'encours' ou 'closes'"
                );
                return;
            }

            if (!QuestionTypesEnum.isValueInEnum(type)) {
                StringBuilder message = new StringBuilder(
                    "Le paramètre facultatif type doit être égal à l'une des valeurs suivantes : "
                );
                message.append(StringUtils.join(QuestionTypesEnum.toStrings(), ",", "'"));
                LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PROCESS_OPERATION_EXTR_QUEST_TEC, message.toString());
                return;
            }

            // chargement des propriétés
            eventProperties.put(QSTATUT_PROPERTY, statutParam);
            eventProperties.put(QTYPE_PROPERTY, type);

            // Pas d'origine spécifiée, on lance l'évènement pour les deux assemblées
            if (origine == null) {
                // ajout propriété source senat
                eventProperties.put(QSOURCE_PROPERTY, "SENAT");
                fireEvent(eventProducer, eventProperties);
                // remplace propriété source senat par an
                eventProperties.put(QSOURCE_PROPERTY, "AN");
                fireEvent(eventProducer, eventProperties);
            } else if (ORIGINE_SENAT.equals(origine) || ORIGINE_AN.equals(origine)) {
                eventProperties.put(QSOURCE_PROPERTY, origine);
                fireEvent(eventProducer, eventProperties);
            } else {
                LOGGER.error(
                    session,
                    ReponsesLogEnumImpl.FAIL_PROCESS_OPERATION_EXTR_QUEST_TEC,
                    "Le paramètre facultatif 'origine' doit être égal à 'AN' ou 'SENAT'"
                );
            }
        } finally {
            LOGGER.info(
                session,
                ReponsesLogEnumImpl.END_OPERATION_EXTR_QUEST_TEC,
                "-------------------------------------------------------------------------------"
            );
        }
    }

    private void fireEvent(final EventProducer producer, final Map<String, Serializable> eventProperties) {
        InlineEventContext inlineEventContext = new InlineEventContext(
            session,
            session.getPrincipal(),
            eventProperties
        );
        producer.fireEvent(inlineEventContext.newEvent(ReponsesEventConstant.EXTRACTION_QUESTIONS_BATCH_EVENT));
    }
}
