package fr.dila.reponses.api.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.mailbox.Mailbox;

/**
 * Service qui permet de gérer les corbeilles utilisateur / postes.
 * 
 * @author jtremeaux
 */
public interface CorbeilleService extends fr.dila.st.api.service.CorbeilleService {
	/**
	 * Retourne la Mailbox personnelle de l'utilisateur qui possède les dossiers.
	 * 
	 * @param session
	 *            Session
	 * @return Mailbox
	 * @throws ClientException
	 *             ClientException
	 */
	Mailbox getDossierOwnerPersonalMailbox(CoreSession session) throws ClientException;

	/**
	 * Liste les etapes existantes dans une mailbox pour un ministere donné, accompagnées du nombre de dossier présent
	 * dans chaque étape
	 * 
	 * @param session
	 * @param mailboxIds
	 *            ensemble de valeur de l'attribut mailboxId de la mailbox
	 */
	Map<String, Integer> listNotEmptyEtape(CoreSession session, Set<String> mailboxIds, String ministereId)
			throws ClientException;

	/**
	 * Retourne la liste des indexations de niveau 1 pour le mode donnée (AN : Rubrique, Senat : Rubrique+Thème).
	 * 
	 * @param treeMode
	 * @return map des indexations
	 */
	Map<String, Integer> getPlanClassementNiveau1(CoreSession session, String treeMode) throws ClientException;

	/**
	 * Retourne la liste des indexations de niveau 2 pour le mode et l'indexation de niveau 1 donnés.
	 * 
	 * @param treeMode
	 * @param indexation
	 * @return map des indexations
	 */
	Map<String, Integer> getPlanClassementNiveau2(CoreSession session, String treeMode, String indexation)
			throws ClientException;

	/**
	 * Recherche les DossierLink correspondant à la distribution du dossier. L'utilisateur a le droit d'actionner ce
	 * DossierLink, soit parce qu'il est destinataire de la distribution, soit parsqu'il est administrateur.
	 * 
	 * @param session
	 *            Session
	 * @param dossierDoc
	 *            Document dossier
	 * @return Liste de DossierLink
	 * @throws ClientException
	 */
	List<DocumentModel> findUpdatableDossierLinkForDossier(CoreSession session, DocumentModel dossierDoc)
			throws ClientException;

	/**
	 * Retourne tous les dossiers d'un ministère.
	 * 
	 * @param session
	 * @param ministereId
	 * @return
	 * @throws ClientException
	 */
	public List<DocumentModel> findDossierInMinistere(CoreSession session, String ministereId) throws ClientException;

	/**
	 * Retourne tous les dossiers correspondant à un ministère de réattribution
	 * 
	 * @param session
	 * @param ministereReattribution
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findDossierFromMinistereReattribution(CoreSession session, String ministereReattribution)
			throws ClientException;

	/**
	 * Recherche les DossiersLink correspondant à la distribution des dossiers dans un des ministères passés en
	 * paramètre ou ceux présents pour les mailbox id.
	 * 
	 * @param session
	 *            Session
	 * @param dossierId
	 *            Identifiant technique des documents dossiers
	 * @param ministereIdList
	 *            Collection d'identifiant technique de ministère
	 * @param mailboxIdList les mailbox de l'utilisateur
	 * @return Liste de DossierLink
	 */
	List<DocumentModel> findDossierLinkInMinistereOrMailbox(CoreSession session, List<String> dossiersDocsIds,
			Collection<String> ministereIdList, final Collection<String> mailboxIdList) throws ClientException;

	/**
	 * Recherche les DossierLink correspondant à la distribution des dossiers. L'utilisateur a le droit d'actionner ce
	 * DossierLink, soit parce qu'il est destinataire de la distribution, soit parce qu'il est administrateur.
	 * 
	 * @param session
	 *            Session
	 * @param dossierDoc
	 *            Document dossier
	 * @return Liste de DossierLink
	 * @throws ClientException
	 */
	List<DocumentModel> findUpdatableDossierLinkForDossiers(CoreSession session, List<String> dossiersDocsIds)
			throws ClientException;

}
