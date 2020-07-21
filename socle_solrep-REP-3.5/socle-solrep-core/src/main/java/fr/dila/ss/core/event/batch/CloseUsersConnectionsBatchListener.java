package fr.dila.ss.core.event.batch;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.documentmodel.SSInfoUtilisateurConnection;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Récupère les utilisateurs toujours indiqués comme connectés et place le flag à "déconnecté"
 * 
 */
public class CloseUsersConnectionsBatchListener extends AbstractBatchEventListener {

	private static final STLogger	LOGGER			= STLogFactory.getLog(CloseUsersConnectionsBatchListener.class);

	private static final String		USERS_GOT		= "%d utilisateurs récupérés";
	private static final String		USERS_UPDATED	= "%d utilisateurs mis à jour";

	public CloseUsersConnectionsBatchListener() {
		super(LOGGER, SSEventConstant.BATCH_EVENT_CLOSE_USERS_CONNECTIONS);
	}

	@Override
	protected void processEvent(CoreSession session, Event event) throws ClientException {
		LOGGER.info(session, SSLogEnumImpl.INIT_B_CLOSE_USERS_CONNEC_TEC);
		final long startTime = Calendar.getInstance().getTimeInMillis();
		List<DocumentModel> infoUsersConnections = SSServiceLocator.getUtilisateurConnectionMonitorService()
				.getAllInfoUtilisateurConnection(session, false);
		int usersUpdated = 0;
		int nbUsers = infoUsersConnections.size();
		LOGGER.info(session, SSLogEnumImpl.GET_IUC_TEC, String.format(USERS_GOT, nbUsers));

		Integer compteurUser = 0;
		try {
			for (DocumentModel infosUserConnectionDoc : infoUsersConnections) {
				SSInfoUtilisateurConnection infoUC = infosUserConnectionDoc
						.getAdapter(SSInfoUtilisateurConnection.class);
				infoUC.setIsLogout(true);
				session.saveDocument(infosUserConnectionDoc);
				LOGGER.info(session, SSLogEnumImpl.UPDATE_IUC_TEC, infosUserConnectionDoc);
				++usersUpdated;
				if (usersUpdated % 50 == 0) {
					LOGGER.info(session, SSLogEnumImpl.UPDATE_IUC_TEC, String.format(USERS_UPDATED, usersUpdated));
				}

				compteurUser = compteurUser + 1;
				if (compteurUser.equals(50)) {
					TransactionHelper.commitOrRollbackTransaction();
					TransactionHelper.startTransaction();
					compteurUser = 0;
				}
			}
			session.save();
		} catch (Exception exc) {
			LOGGER.error(session, SSLogEnumImpl.FAIL_PROCESS_B_CLOSE_USERS_CONNEC_TEC, exc);
			errorCount++;
		}
		final long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			STServiceLocator.getSuiviBatchService().createBatchResultFor(batchLoggerModel, "Utilisateurs mis à jour",
					usersUpdated, endTime - startTime);
		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, exc);
		}
		LOGGER.info(session, SSLogEnumImpl.END_B_CLOSE_USERS_CONNEC_TEC);
	}
}
