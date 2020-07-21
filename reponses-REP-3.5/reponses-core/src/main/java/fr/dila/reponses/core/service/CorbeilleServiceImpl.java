package fr.dila.reponses.core.service;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * Implémentation du service Corbeille de l'application Réponses.
 * 
 * @author jtremeaux
 */
public class CorbeilleServiceImpl extends fr.dila.st.core.service.CorbeilleServiceImpl implements CorbeilleService {

	private static final String	NIVEAU1_THEME			= "Thème";

	private static final String	NIVEAU1_RUBRIQUE		= "Rubrique";

	public static final String	TREEMODE_SENAT			= "Senat";

	private static final String	COL_LABEL				= "label";
	private static final String	COL_COUNT				= "count";

	private static final String	QUERY_AN_RUBRIQUE		= "SELECT q.ixa:AN_rubrique AS "
																+ COL_LABEL
																+ ", count() AS "
																+ COL_COUNT
																+ " FROM Question AS q"
																+ " WHERE q.ixa:AN_rubrique IS NOT NULL GROUP BY q.ixa:AN_rubrique";

	private static final String	QUERY_AN_RUBRIQUE_COMP	= "SELECT q.ixacomp:AN_rubrique AS "
																+ COL_LABEL
																+ ", count() AS "
																+ COL_COUNT
																+ " FROM Question AS q"
																+ " WHERE q.ixacomp:AN_rubrique IS NOT NULL GROUP BY q.ixacomp:AN_rubrique";

	private static final String	QUERY_AN_TA				= "SELECT q.ixa:TA_rubrique AS "
																+ COL_LABEL
																+ ", count() AS "
																+ COL_COUNT
																+ " FROM Question AS q"
																+ " WHERE q.ixa:AN_rubrique = ? AND q.ixa:TA_rubrique IS NOT NULL GROUP BY q.ixa:TA_rubrique";

	private static final String	QUERY_AN_TA_COMP		= "SELECT q.ixacomp:TA_rubrique AS "
																+ COL_LABEL
																+ ", count() AS "
																+ COL_COUNT
																+ " FROM Question AS q"
																+ " WHERE q.ixacomp:AN_rubrique = ? AND q.ixacomp:TA_rubrique IS NOT NULL GROUP BY q.ixacomp:TA_rubrique";

	/**
	 * UID.
	 */
	private static final long	serialVersionUID		= -2392698015083550568L;

	@Override
	public Mailbox getDossierOwnerPersonalMailbox(final CoreSession session) throws ClientException {
		// Détermine le nom du propriétaire des dossiers
		final ConfigService configService = STServiceLocator.getConfigService();
		final String dossierOwner = configService.getValue(ReponsesConfigConstant.REPONSES_DOSSIER_OWNER);

		// Récupère la mailbox personnelle du propriétaire
		final MailboxService mailboxService = ReponsesServiceLocator.getMailboxService();
		final Mailbox mailbox = mailboxService.getUserPersonalMailbox(session, dossierOwner);

		return mailbox;
	}

	/**
	 * Liste les etapes existantes dans une mailbox pour un ministere donné, accompagnées du nombre de dossier présent
	 * dans chaque étape
	 * 
	 * @param mailboxIdsForQuery
	 *            contient uneliste d'id separé par des virgule (destine a etre inclu dans un IN)
	 */
	@Override
	public Map<String, Integer> listNotEmptyEtape(final CoreSession session, final Set<String> mailboxIds,
			final String ministereId) throws ClientException {

		if (mailboxIds == null || mailboxIds.isEmpty()) {
			return Collections.emptyMap();
		}

		final StringBuilder query = new StringBuilder("SELECT l.drl:routingTaskType as ").append(COL_LABEL)
				.append(", count() as ").append(COL_COUNT).append(" FROM ")
				.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE).append(" AS l WHERE l.")
				.append(DossierConstants.DOSSIER_REPONSES_LINK_PREFIX).append(":")
				.append(DossierConstants.DOSSIER_REPONSES_LINK_ID_MINISTERE_ATTRIBUTAIRE_PROPERTY)
				.append(" = ? AND l.").append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH)
				.append(" = 'todo' AND l.cmdist:initial_action_internal_participant_mailboxes IN (")
				.append(StringUtil.getQuestionMark(mailboxIds.size())).append(") GROUP BY l.drl:routingTaskType");

		// // Requete en SQL pour des raisons de performances
		// StringBuilder sb = new StringBuilder("SELECT l_D_1.ROUTINGTASKTYPE AS label, count(*) AS count ")
		// .append("FROM DOSSIER_REPONSES_LINK l_D_1 ")
		// .append("WHERE l_D_1.IDMINISTEREATTRIBUTAIRE = ? AND ")
		// .append("(EXISTS (SELECT 1 FROM \"CMDIST_INITIAL_ACTION_4CD43708\" dist WHERE ID = l_D_1.ID AND ")
		// .append("(dist.ITEM IN ( ")
		// .append(StringUtil.getQuestionMark(mailboxIds.size()))
		// .append(" )))) GROUP BY l_D_1.ROUTINGTASKTYPE");

		final Object[] params = new Object[1 + mailboxIds.size()];
		int index = 0;
		params[index++] = ministereId;
		for (final String mailboxId : mailboxIds) {
			params[index++] = mailboxId;
		}

		// Map<String, Integer> etapes = buildResultMapFromSQLQueryResult(session, sb.toString(),
		// new String[]{"drl:routingTaskType", FlexibleQueryMaker.COL_COUNT}, params, null);
		final Map<String, Integer> etapes = buildResultMapFromUFNXQLQueryResult(session, query.toString(), params, null);
		return etapes;
	}

	@Override
	public Map<String, Integer> getPlanClassementNiveau1(final CoreSession session, final String treeMode)
			throws ClientException {
		if (treeMode.equals(TREEMODE_SENAT)) {
			return getPlanClassementSenatNiveau1(session, treeMode);
		} else {
			// indexation initiale
			Map<String, Integer> map = buildResultMapFromUFNXQLQueryResult(session, QUERY_AN_RUBRIQUE, null, null);

			// indexation complémentaire
			map = buildResultMapFromUFNXQLQueryResult(session, QUERY_AN_RUBRIQUE_COMP, null, map);

			return map;
		}
	}

	@Override
	public Map<String, Integer> getPlanClassementNiveau2(final CoreSession session, final String treeMode,
			final String indexation) throws ClientException {
		if (treeMode.equals(TREEMODE_SENAT)) {
			return getSenatRubriqueThemeAndQuestionCount(session, indexation);
		} else {
			// indexation initiale
			Map<String, Integer> map = buildResultMapFromUFNXQLQueryResult(session, QUERY_AN_TA,
					new Object[] { indexation }, null);

			// indexation complémentaire
			map = buildResultMapFromUFNXQLQueryResult(session, QUERY_AN_TA_COMP, new Object[] { indexation }, map);

			return map;
		}
	}

	private Map<String, Integer> getPlanClassementSenatNiveau1(final CoreSession session, final String treeMode)
			throws ClientException {
		final Map<String, Integer> map = new TreeMap<String, Integer>();
		map.put(NIVEAU1_RUBRIQUE, getSenatNbRubriqueTheme(session, NIVEAU1_RUBRIQUE));
		map.put(NIVEAU1_THEME, getSenatNbRubriqueTheme(session, NIVEAU1_THEME));
		return map;
	}

	/**
	 * Retourne les nombres de rubriques ou ou de themes.
	 * 
	 * @param session
	 * @param type
	 *            valeur NIVEAU1_RUBRIQUE pour les rubriques et NIVEAU1_THEME pour les themes
	 * @return
	 */
	private int getSenatNbRubriqueTheme(final CoreSession session, final String type) throws ClientException {
		String column = "SE_rubrique";
		if (!NIVEAU1_RUBRIQUE.equals(type)) {
			column = "SE_theme";
		}
		StringBuilder query = new StringBuilder("SELECT DISTINCT q.ixa:").append(column).append(" AS ")
				.append(COL_LABEL).append(" FROM Question AS q").append(" WHERE q.ixa:").append(column)
				.append(" IS NOT NULL");

		final Set<String> labelSet = new HashSet<String>();
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doUFNXQLQuery(session, query.toString(), null);

			final Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				final Map<String, Serializable> row = iterator.next();
				final String label = (String) row.get(COL_LABEL);
				labelSet.add(label);
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		query = new StringBuilder("SELECT DISTINCT q.ixacomp:").append(column).append(" AS ").append(COL_LABEL)
				.append(" FROM Question AS q").append(" WHERE q.ixacomp:").append(column).append(" IS NOT NULL");

		res = null;
		try {
			res = QueryUtils.doUFNXQLQuery(session, query.toString(), null);

			final Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				final Map<String, Serializable> row = iterator.next();
				final String label = (String) row.get(COL_LABEL);
				labelSet.add(label);
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}
		return labelSet.size();
	}

	/**
	 * Retourne les rubriques ou themes accompagnés du nombre de questions
	 * 
	 * @param session
	 * @param type
	 *            valeur NIVEAU1_RUBRIQUE pour les rubriques et NIVEAU1_THEME pour les themes
	 * @return
	 */
	private Map<String, Integer> getSenatRubriqueThemeAndQuestionCount(final CoreSession session, final String type)
			throws ClientException {
		String column = "SE_rubrique";
		if (!NIVEAU1_RUBRIQUE.equals(type)) {
			column = "SE_theme";
		}
		StringBuilder query = new StringBuilder("SELECT q.ixa:").append(column).append(" AS ").append(COL_LABEL)
				.append(", count() AS ").append(COL_COUNT).append(" FROM Question AS q").append(" WHERE q.ixa:")
				.append(column).append(" IS NOT NULL group by q.ixa:").append(column);

		Map<String, Integer> mapLabelCount = buildResultMapFromUFNXQLQueryResult(session, query.toString(), null, null);

		query = new StringBuilder("SELECT q.ixacomp:").append(column).append(" AS ").append(COL_LABEL)
				.append(", count() AS ").append(COL_COUNT).append(" FROM Question AS q").append(" WHERE q.ixacomp:")
				.append(column).append(" IS NOT NULL group by q.ixacomp:").append(column);

		mapLabelCount = buildResultMapFromUFNXQLQueryResult(session, query.toString(), null, mapLabelCount);

		return mapLabelCount;
	}

	/**
	 * Rempli une map avec les retour de la requete, les champs attendu sont label et count (qui contient le nombre
	 * d'occurence de label) Cree un map ou en complete une. dans ce dernier cas le nombre d'occurence est ajouté au
	 * nombre courant se trouvant dans la map "initMap"
	 * 
	 * @param query
	 *            UFNQL query
	 * @param initMap
	 * @return
	 */
	private Map<String, Integer> buildResultMapFromUFNXQLQueryResult(final CoreSession session, final String query,
			final Object[] params, final Map<String, Integer> initMap) throws ClientException {
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doUFNXQLQuery(session, query, params);

			final Map<String, Integer> map = initMap == null ? new TreeMap<String, Integer>(Collator.getInstance())
					: initMap;
			final Iterator<Map<String, Serializable>> iterator = res.iterator();
			while (iterator.hasNext()) {
				final Map<String, Serializable> row = iterator.next();
				final String label = (String) row.get(COL_LABEL);
				final Long count = (Long) row.get(COL_COUNT);

				final Integer existingCount = map.get(label);
				map.put(label, count.intValue() + (existingCount == null ? 0 : existingCount));
			}
			return map;
		} finally {
			if (res != null) {
				res.close();
			}
		}
	}

	@Override
	public List<DocumentModel> findUpdatableDossierLinkForDossier(final CoreSession session,
			final DocumentModel dossierDoc) throws ClientException {
		return findUpdatableDossierLinkForDossiers(session, Collections.singletonList(dossierDoc.getId()));
	}
	
	@Override
	public List<DocumentModel> findUpdatableDossierLinkForDossiers(final CoreSession session,
			final List<String> dossiersDocsIds) throws ClientException {
		final SSPrincipal principal = (SSPrincipal) session.getPrincipal();
		
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		final Set<String> mailboxIdSet = mailboxPosteService.getMailboxPosteIdSetFromPosteIdSet(principal
				.getPosteIdSet());
		final Set<DocumentModel> dossierLinkList = new HashSet<DocumentModel>();		
		// Si l'utilisateur est un administrateur, il peut actionner le DossierLink à la place de l'utilisateur
		if (principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_UPDATER)) {
			dossierLinkList.addAll(findDossierLinkUnrestricted(session, dossiersDocsIds));
		} else if (principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_MIN_UPDATER)) {
			// Récuépration des dossier link ministère si l'utilisateur à le droit
			dossierLinkList.addAll(findDossierLinkInMinistereOrMailbox(session, dossiersDocsIds, principal.getMinistereIdSet(), mailboxIdSet));
		} else {
			// Recherche des DossierLink distribués directement à l'utilisateur			
			dossierLinkList.addAll(findDossierLinkInMailbox(session, dossiersDocsIds, mailboxIdSet));
		} 
		return new ArrayList<DocumentModel>(dossierLinkList);
	}

	@Override
	public List<DocumentModel> findDossierInMinistere(final CoreSession session, final String ministereId)
			throws ClientException {
		final String query = "SELECT d." + STSchemaConstant.ECM_UUID_XPATH + " AS id FROM Dossier AS d WHERE d.dos:"
				+ DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " = ?";

		final List<String> paramList = new ArrayList<String>();
		paramList.add(ministereId);

		return QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
				DossierConstants.DOSSIER_DOCUMENT_TYPE, query, paramList.toArray(new String[paramList.size()]));
	}

	@Override
	public List<DocumentModel> findDossierFromMinistereReattribution(final CoreSession session,
			final String ministereReattribution) throws ClientException {
		final String query = "SELECT d."
				+ STSchemaConstant.ECM_UUID_XPATH
				+ " AS id FROM Dossier AS d WHERE d.dos:ministereReattribution = ? and d.dos:ministereReattribution != d.dos:ministereAttributaireCourant";

		final List<String> paramList = new ArrayList<String>();
		paramList.add(ministereReattribution);

		return QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
				DossierConstants.DOSSIER_DOCUMENT_TYPE, query, paramList.toArray(new String[paramList.size()]));
	}
	
	@Override
	public List<DocumentModel> findDossierLinkInMinistereOrMailbox(final CoreSession session, final List<String> dossiersDocsIds,
			final Collection<String> ministereIdList, final Collection<String> mailboxIdList) throws ClientException {
		final StringBuilder query = new StringBuilder("SELECT l.").append(STSchemaConstant.ECM_UUID_XPATH)
				.append(" AS id FROM ").append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE).append(" AS l ")
				.append(" WHERE l.cslk:caseDocumentId IN (")
				.append(StringUtil.getQuestionMark(dossiersDocsIds.size()))
				.append(") AND l.")
				.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH)
				.append(" = 'todo' AND (l.drl:idMinistereAttributaire IN (")
				.append(StringUtil.getQuestionMark(ministereIdList.size())).append(") OR l.cmdist:initial_action_internal_participant_mailboxes IN (") 
				.append(StringUtil.getQuestionMark(mailboxIdList.size())).append("))");

		final List<String> paramList = new ArrayList<String>();
		paramList.addAll(dossiersDocsIds);
		paramList.addAll(ministereIdList);
		paramList.addAll(mailboxIdList);

		return QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
				STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, query.toString(),
				paramList.toArray(new String[paramList.size()]));
	}
}
