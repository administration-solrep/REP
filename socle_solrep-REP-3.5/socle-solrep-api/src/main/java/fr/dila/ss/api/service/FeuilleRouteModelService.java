package fr.dila.ss.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;

/**
 * Service permettant de gérer un catalogue de modèles de feuille de route.
 * 
 * @author jtremeaux
 */
public interface FeuilleRouteModelService extends Serializable {
	/**
	 * Retourne le répertoire racine des modèles de feuilles de route.
	 * 
	 * @return Répertoire des modèles de feuilles de route
	 * @throws ClientException
	 */
	DocumentModel getFeuilleRouteModelFolder(CoreSession session) throws ClientException;

	/**
	 * Retourne l'id du répertoire racine des modèles de feuilles de route.
	 * 
	 * @return Id du répertoire des modèles de feuilles de route
	 * @throws ClientException
	 */
	String getFeuilleRouteModelFolderId(CoreSession session) throws ClientException;

	/**
	 * Substitue un poste dans les modèles de feuilles de route.
	 * 
	 * @param session
	 *            Session
	 * @param feuilleRouteDocList
	 *            Liste des feuilles de route à traiter
	 * @param ancienPosteId
	 *            Identifiant technique de l'ancien poste
	 * @param nouveauPosteId
	 *            Identifiant technique du nouveau poste
	 * @throws ClientException
	 */
	void substituerPoste(CoreSession session, List<DocumentModel> feuilleRouteDocList, String ancienPosteId,
			String nouveauPosteId) throws ClientException;

	/**
	 * Construit le prédicat pour restreindre les feuilles de routes à celles que l'utilisateur peut voir.
	 * 
	 * @param ssPrincipal
	 *            Principal
	 * @param ministereField
	 *            Nom du champ (ex. fdr:ministere)
	 * @return Prédicat
	 */
	String getMinistereCriteria(SSPrincipal ssPrincipal, String ministereField);

	/**
	 * Détermine si l'intitulé d'un modèle de feuille de route est unique.
	 * 
	 * @param session
	 *            Session
	 * @param route
	 *            Modèle de feuille de route
	 * @return Intitulé unique ou non
	 * @throws ClientException
	 */
	boolean isIntituleUnique(CoreSession session, STFeuilleRoute route) throws ClientException;
}
