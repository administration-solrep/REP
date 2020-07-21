package fr.dila.ss.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;

import fr.dila.ss.api.constant.SSInfoUtilisateurConnectionConstants;
import fr.dila.ss.api.documentmodel.SSInfoUtilisateurConnection;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.SSUtilisateurConnectionMonitorService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

public class SSUtilisateurConnectionMonitorServiceImpl implements SSUtilisateurConnectionMonitorService {

	private static final String		ALL_INFO_UTILISATEUR_CONNECTION					= "SELECT d.ecm:uuid as id FROM InfoUtilisateurConnection as d ";

	private static final String		ALL_LOGOUT_INFO_UTILISATEUR_CONNECTION_QUERY	= "SELECT d.ecm:uuid as id FROM InfoUtilisateurConnection as d WHERE d."
																							+ SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA_PREFIX
																							+ ":"
																							+ SSInfoUtilisateurConnectionConstants.IINFO_UTILISATEUR_CONNECTION__IS_LOGOUT
																							+ " = ? ";

	private static final String		INFO_UTILISATEUR_CONNECTION_QUERY				= "SELECT d.ecm:uuid as id FROM InfoUtilisateurConnection as d WHERE d."
																							+ SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA_PREFIX
																							+ ":"
																							+ SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_USER_PROPERTY
																							+ " = ? ";

	private static final Log		LOGGER											= LogFactory
																							.getLog(SSUtilisateurConnectionMonitorServiceImpl.class);
	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOG												= STLogFactory
																							.getLog(SSUtilisateurConnectionMonitorServiceImpl.class);

	@Override
	public void createOrUpdateInfoUtilisateurConnection(final CoreSession session, final STUser user)
			throws ClientException {

		SSInfoUtilisateurConnection infoUtilisateurConnection = getInfoUtilisateurConnection(session,
				user.getUsername());
		STUserService stUserService = STServiceLocator.getSTUserService();
		if (infoUtilisateurConnection == null) {
			final DocumentModel infoUtilisateurConnectionModel = new DocumentModelImpl(
					SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_PATH, user.getUsername(),
					SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DOCUMENT_TYPE);
			infoUtilisateurConnection = infoUtilisateurConnectionModel.getAdapter(SSInfoUtilisateurConnection.class);
			infoUtilisateurConnection.setUserName(user.getUsername());
			infoUtilisateurConnection.setFirstName(user.getFirstName());
			infoUtilisateurConnection.setLastName(user.getLastName());
			infoUtilisateurConnection.setDateConnection(Calendar.getInstance());
			infoUtilisateurConnection.setIsLogout(false);
			infoUtilisateurConnection.setCourriel(user.getEmail());
			infoUtilisateurConnection.setTelephone(user.getTelephoneNumber());
			infoUtilisateurConnection.setPoste(stUserService.getUserProfils(user.getUsername()));
			infoUtilisateurConnection.setMinistereRattachement(stUserService.getUserMinisteres(user.getUsername()));
			infoUtilisateurConnection.setDirection(stUserService.getAllDirectionsRattachement(user.getUsername()));
			infoUtilisateurConnection.setDateCreation(user.getDateDebut());
			infoUtilisateurConnection.setDateDerniereConnexion(Calendar.getInstance());
			session.createDocument(infoUtilisateurConnectionModel);
		} else {
			infoUtilisateurConnection.setDateConnection(Calendar.getInstance());
			infoUtilisateurConnection.setFirstName(user.getFirstName());
			infoUtilisateurConnection.setLastName(user.getLastName());
			infoUtilisateurConnection.setIsLogout(false);
			infoUtilisateurConnection.setCourriel(user.getEmail());
			infoUtilisateurConnection.setTelephone(user.getTelephoneNumber());
			infoUtilisateurConnection.setPoste(stUserService.getUserProfils(user.getUsername()));
			infoUtilisateurConnection.setMinistereRattachement(stUserService.getUserMinisteres(user.getUsername()));
			infoUtilisateurConnection.setDirection(stUserService.getAllDirectionsRattachement(user.getUsername()));
			infoUtilisateurConnection.setDateCreation(user.getDateDebut());
			infoUtilisateurConnection.setDateDerniereConnexion(Calendar.getInstance());
			infoUtilisateurConnection.save(session);
		}

		session.save();
	}

	@Override
	public void updateInfoUtilisateurConnection(final CoreSession session, final STUser user, final boolean islogout)
			throws ClientException {

		final SSInfoUtilisateurConnection infoUtilisateurConnection = getInfoUtilisateurConnection(session,
				user.getUsername());
		if (infoUtilisateurConnection != null) {
			infoUtilisateurConnection.setIsLogout(islogout);
			infoUtilisateurConnection.save(session);
			session.save();
		} else {
			LOGGER.error("Pas d'info utilisateur trouvé pour " + user.getUsername());
		}
	}

	@Override
	public void removeInfoUtilisateurConnection(final CoreSession session, final STUser user) throws ClientException {
		final SSInfoUtilisateurConnection infoUtilisateurConnection = getInfoUtilisateurConnection(session,
				user.getUsername());
		if (infoUtilisateurConnection != null) {
			LOG.info(session, SSLogEnumImpl.DEL_INFO_USER_CO_TEC, infoUtilisateurConnection.getDocument());
			session.removeDocument(infoUtilisateurConnection.getDocument().getRef());
			session.save();
		}
	}

	@Override
	public List<DocumentModel> getAllInfoUtilisateurConnection(final CoreSession session) throws ClientException {
		return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DOCUMENT_TYPE,
				ALL_INFO_UTILISATEUR_CONNECTION, null);
	}

	@Override
	public List<DocumentModel> getAllInfoUtilisateurConnection(final CoreSession session, final boolean isLogout)
			throws ClientException {
		return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DOCUMENT_TYPE,
				ALL_LOGOUT_INFO_UTILISATEUR_CONNECTION_QUERY, new Object[] { isLogout });
	}

	@Override
	public SSInfoUtilisateurConnection getInfoUtilisateurConnection(final CoreSession session, final String userName)
			throws ClientException {

		SSInfoUtilisateurConnection infoUtilisateurConnection = null;

		final List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
				SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_DOCUMENT_TYPE,
				INFO_UTILISATEUR_CONNECTION_QUERY, new Object[] { userName }, 1, 0);

		if (list != null && !list.isEmpty()) {
			infoUtilisateurConnection = list.get(0).getAdapter(SSInfoUtilisateurConnection.class);
		}

		return infoUtilisateurConnection;
	}

	@Override
	public List<String> getListInfoUtilisateurConnection(final CoreSession session, final Date dateDeConnexion)
			throws ClientException {

		final List<String> connectedUsers = new ArrayList<String>();
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateDeConnexion);

		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH) + 1;
		final int day = calendar.get(Calendar.DAY_OF_MONTH);

		final StringBuilder strParamDate = new StringBuilder();
		strParamDate.append(year);
		strParamDate.append("/");
		strParamDate.append(month);
		strParamDate.append("/");
		strParamDate.append(day);

		final StringBuilder query = new StringBuilder(
				"SELECT infouc.userName FROM info_utilisateur_connection infouc where TO_DATE(TO_CHAR(infouc.dateConnection,'yyyy/mm/dd') , 'yyyy/mm/dd') >=  TO_DATE('"
						+ strParamDate.toString() + "', 'yyyy/mm/dd') ORDER BY infouc.userName");
		IterableQueryResult res = null;

		try {
			res = QueryUtils.doSqlQuery(session, new String[] { "infouc:userName" }, query.toString(), new Object[] {});

			final Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				final Map<String, Serializable> row = iterator.next();
				final String username = (String) row.get("infouc:userName");
				connectedUsers.add(username);
			}
		} catch (final ClientException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (res != null) {
				res.close();
			}
		}

		return connectedUsers;
	}

	@Override
	public List<String> getListInfoUtilisateurConnectionNotConnectedSince(final CoreSession session,
			final Date dateDeConnexion) throws ClientException {

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateDeConnexion);

		// Ajout de tous les utilisateurs n'ayant pas de date de connexion
		final Map<String, Serializable> filter = new HashMap<String, Serializable>();
		final DocumentModelList userModelList = STServiceLocator.getUserManager().searchUsers(filter, null);
		final List<String> allUsersNeverConnectedList = new ArrayList<String>();

		for (final DocumentModel userDocModel : userModelList) {
			final STUser user = userDocModel.getAdapter(STUser.class);
			if (user.isActive()) {
				final String userName = user.getUsername();
				try {
					final SSInfoUtilisateurConnection infoUC = getInfoUtilisateurConnection(session, userName);
					if (infoUC == null || infoUC.getDateConnection().compareTo(calendar) < 0) {
						allUsersNeverConnectedList.add(userName);
					}
				} catch (final ClientException e) {
					LOGGER.warn("Récupération impossible des informations de connexion de " + userName + " "
							+ e.getMessage());
				}
			}
		}
		;

		return allUsersNeverConnectedList;
	}

}