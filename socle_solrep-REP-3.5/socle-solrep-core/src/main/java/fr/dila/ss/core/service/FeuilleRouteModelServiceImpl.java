package fr.dila.ss.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.ss.api.criteria.FeuilleRouteCriteria;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.StringUtil;

/**
 * Implémentation du service permettant de gérer un catalogue de modèles de feuilles de route.
 * 
 * @author jtremeaux
 */
public abstract class FeuilleRouteModelServiceImpl implements FeuilleRouteModelService {

	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID					= 3838222748672986516L;

	private static final String	FEUILLE_ROUTE_MODEL_FOLDER_QUERY	= "SELECT * FROM "
																			+ STConstant.FEUILLE_ROUTE_MODEL_FOLDER_DOCUMENT_TYPE
																			+ " WHERE ecm:isProxy = 0";

	/**
	 * Conserve l'id du document Racine des modèles de feuilles de route Evite une requete SQL systematique à la base
	 * pour récupérer l'id qui ne change pas
	 */
	private String				feuilleRouteModelFolderDocId;

	@Override
	public DocumentModel getFeuilleRouteModelFolder(final CoreSession session) throws ClientException {
		if (feuilleRouteModelFolderDocId == null) {
			final DocumentModelList list = session.query(FEUILLE_ROUTE_MODEL_FOLDER_QUERY);
			if (list == null || list.isEmpty()) {
				throw new ClientException("Racine des modèles de feuilles de route non trouvée");
			} else if (list.size() > 1) {
				throw new ClientException("Plusieurs racines des modèles de feuilles de route trouvées");
			}

			final DocumentModel doc = list.get(0);
			feuilleRouteModelFolderDocId = doc.getId();
			return doc;
		} else {
			return session.getDocument(new IdRef(feuilleRouteModelFolderDocId));
		}
	}

	@Override
	public String getFeuilleRouteModelFolderId(final CoreSession session) throws ClientException {
		if (feuilleRouteModelFolderDocId == null) {
			// load le document FeuilleRouteModelFolder et
			// renseigne feuilleRouteModelFolderDocId
			getFeuilleRouteModelFolder(session);
		}
		return feuilleRouteModelFolderDocId;
	}

	@Override
	public boolean isIntituleUnique(final CoreSession session, final STFeuilleRoute route) throws ClientException {
		final FeuilleRouteCriteria criteria = new FeuilleRouteCriteria();
		if (StringUtils.isNotBlank(route.getMinistere())) {
			criteria.setMinistere(route.getMinistere());
		} else {
			criteria.setMinistereNull(true);
		}
		if (StringUtils.isNotBlank(route.getName())) {
			criteria.setIntitule(route.getName());
		} else {
			criteria.setIntituleNull(true);
		}

		final List<String> feuilleRouteIdList = findFeuilleRouteByCriteria(session, criteria);
		for (final String feuilleRouteId : feuilleRouteIdList) {
			if (!feuilleRouteId.equals(route.getDocument().getId())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getMinistereCriteria(final SSPrincipal ssPrincipal, final String ministereField) {
		// Si l'utilisateur est administrateur fonctionnel, il voit tous les modèles
		final List<String> groups = ssPrincipal.getGroups();
		final boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
		if (isAdminFonctionnel) {
			return "";
		}

		final StringBuilder sb = new StringBuilder(" AND (").append(ministereField).append(" IS NULL ");

		/*
		 * Si l'utilisateur est administrateur ministériel, il voit les modèles de son ministère et les modèles publics.
		 */
		final Set<String> ministereSet = ssPrincipal.getMinistereIdSet();
		final List<String> orCondition = new ArrayList<String>();
		if (ministereSet != null && !ministereSet.isEmpty()) {
			for (final String ministere : ministereSet) {
				orCondition.add("'" + ministere + "'");
			}
			sb.append(" OR ").append(ministereField).append(" IN (").append(StringUtils.join(orCondition, ","))
					.append(")");
		}
		sb.append(")");

		return sb.toString();
	}

	/**
	 * Construit le prédicat pour restreindre les feuilles de routes à celles que l'utilisateur peut modifier.
	 * 
	 * @param ssPrincipal
	 *            Principal
	 * @param ministereField
	 *            Nom du champ (ex. fdr:ministere)
	 * @return Prédicat
	 */
	protected String getMinistereCriteriaForUpdate(final SSPrincipal ssPrincipal, final String ministereField) {
		// Si l'utilisateur est administrateur fonctionnel, il peut modifier tous les modèles
		final List<String> groups = ssPrincipal.getGroups();
		final boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
		if (isAdminFonctionnel) {
			return "";
		}

		final StringBuilder sb = new StringBuilder(" AND (").append(ministereField).append(" IS NOT NULL ");

		// Si l'utilisateur est administrateur ministériel, il peut modifier les modèles de son ministère.
		final Set<String> ministereSet = ssPrincipal.getMinistereIdSet();
		final List<String> andCondition = new ArrayList<String>();
		if (ministereSet != null && !ministereSet.isEmpty()) {
			for (final String ministere : ministereSet) {
				andCondition.add("'" + ministere + "'");
			}
			sb.append(" AND ").append(ministereField).append(" IN (").append(StringUtils.join(andCondition, ","))
					.append(")");
		}
		sb.append(")");

		return sb.toString();
	}

	@Override
	public void substituerPoste(final CoreSession session, final List<DocumentModel> feuilleRouteDocList,
			final String ancienPosteId, final String nouveauPosteId) throws ClientException {
		// Récupère toutes les étapes correspondantes
		final SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
		final String ministereCriteria = getMinistereCriteriaForUpdate(ssPrincipal, "r."
				+ STSchemaConstant.FEUILLE_ROUTE_SCHEMA_PREFIX + ":"
				+ STSchemaConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY);
		final StringBuilder sb = new StringBuilder("select s.ecm:uuid as id from RouteStep AS s ")
				.append("WHERE isChildOf(s.ecm:uuid, select r.ecm:uuid from DocumentRoute AS r ")
				.append("WHERE r.ecm:uuid IN (").append(StringUtil.getQuestionMark(feuilleRouteDocList.size()))
				.append(") AND r.ecm:currentLifeCycleState in ('draft', 'validated') ").append(ministereCriteria)
				.append(") = 1 ").append(" AND s.rtsk:distributionMailboxId = ?");

		final List<Object> params = new ArrayList<Object>();
		for (final DocumentModel doc : feuilleRouteDocList) {
			params.add(doc.getId());
		}
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		final String ancienDistributionMailboxId = mailboxPosteService.getPosteMailboxId(ancienPosteId);
		params.add(ancienDistributionMailboxId);
		final List<DocumentModel> routeStepDocList = QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
				"RouteStep", sb.toString(), params.toArray());

		// Substituel le poste dans chaque étape
		final String newDistributionMailboxId = mailboxPosteService.getPosteMailboxId(nouveauPosteId);
		mailboxPosteService.getOrCreateMailboxPoste(session, nouveauPosteId);
		for (final DocumentModel routeStepDoc : routeStepDocList) {
			final STRouteStep routeStep = routeStepDoc.getAdapter(STRouteStep.class);
			routeStep.setDistributionMailboxId(newDistributionMailboxId);
			session.saveDocument(routeStepDoc);
		}
	}

	/**
	 * Recherche des id de feuilles de route par critères de recherche.
	 * 
	 * @param criteria
	 *            Critères de recherche
	 * @return Liste des ids de feuilles de route
	 * @throws ClientException
	 */
	private List<String> findFeuilleRouteByCriteria(final CoreSession session, final FeuilleRouteCriteria criteria)
			throws ClientException {
		final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
		final DocumentModel feuilleRouteModelFolder = feuilleRouteModelService.getFeuilleRouteModelFolder(session);

		final List<Object> paramList = new ArrayList<Object>();
		final StringBuilder sb = new StringBuilder("SELECT r.ecm:uuid as id FROM ")
				.append(STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE).append(" AS r WHERE r.")
				.append(STSchemaConstant.ECM_PARENT_ID_XPATH).append(" = ? ");
		paramList.add(feuilleRouteModelFolder.getId());

		if (StringUtils.isNotBlank(criteria.getCurrentLifeCycleState())) {
			sb.append(" AND r.").append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH).append(" = ? ");
			paramList.add(criteria.getCurrentLifeCycleState());
		}

		if (criteria.isFeuilleRouteDefaut()) {
			sb.append(" AND r.").append(STSchemaConstant.FEUILLE_ROUTE_DEFAUT_XPATH).append(" = 1 ");
		}

		if (StringUtils.isNotBlank(criteria.getMinistere())) {
			sb.append(" AND r.").append(STSchemaConstant.FEUILLE_ROUTE_MINISTERE_XPATH).append(" = ? ");
			paramList.add(criteria.getMinistere());
		} else if (criteria.getMinistereNull()) {
			sb.append(" AND r.").append(STSchemaConstant.FEUILLE_ROUTE_MINISTERE_XPATH).append(" IS NULL");
		}

		if (StringUtils.isNotBlank(criteria.getIntitule())) {
			sb.append(" AND r.").append(STSchemaConstant.DUBLINCORE_TITLE_XPATH).append(" = ? ");
			paramList.add(criteria.getIntitule());
		} else if (criteria.getIntituleNull()) {
			sb.append(" AND r.").append(STSchemaConstant.DUBLINCORE_TITLE_XPATH).append(" IS NULL ");
		}

		return QueryUtils.doUFNXQLQueryForIdsList(session, sb.toString(), paramList.toArray());
	}
}
