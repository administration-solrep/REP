package fr.dila.reponses.core.event.batch;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

public class UpdateQuestionConnexeBatchListener extends AbstractBatchEventListener {
	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOGGER	= STLogFactory.getLog(UpdateQuestionConnexeBatchListener.class);

	public UpdateQuestionConnexeBatchListener() {
		super(LOGGER, ReponsesEventConstant.BATCH_EVENT_UPDATE_QUESTION_CONNEXE);
	}

	@Override
	protected void processEvent(final CoreSession session, final Event event) throws ClientException {
		if (checkMigrationRunning(session)) {
			// Une migration est en cours, on ne lance pas le traitement du lanceur général !
			LOGGER.info(session, STLogEnumImpl.END_B_LANCEUR_GENERAL_TEC,
					"Pas de calcul de connexité : une migration est en cours !");
			return;
		}
		LOGGER.info(session, ReponsesLogEnumImpl.INIT_B_UPDATE_Q_CONNEXE);
		long startTime = Calendar.getInstance().getTimeInMillis();
		try {
			final String function_call = "UPDATE_CONNEXITE()";
			QueryUtils.execSqlFunction(session, function_call, null);
			LOGGER.info(session, ReponsesLogEnumImpl.PROCESS_B_UPDATE_Q_CONNEXITE_TEC,
					"Calcul des questions connexes effectué");
		} catch (Exception exc) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PROCESS_B_UPDATE_Q_CONNEXE, exc);
			errorCount++;
		}
		long endTime = Calendar.getInstance().getTimeInMillis();
		SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
		try {
			suiviBatchService.createBatchResultFor(batchLoggerModel, "Calcul des questions connexes effectué", endTime
					- startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOGGER.info(session, ReponsesLogEnumImpl.END_B_UPDATE_Q_CONNEXE);
	}

	private boolean checkMigrationRunning(final CoreSession session) throws ClientException {
		return ReponsesServiceLocator.getUpdateTimbreService().isMigrationEnCours(session);
	}
}
