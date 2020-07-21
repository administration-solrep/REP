package fr.dila.reponses.api.service;

import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingLineDetail;

/**
 * Service de calcul pour les mise a jours des timbre.
 * 
 * @author asatre
 */
public interface UpdateTimbreService {

	/**
	 * Requête le nombre de questions closes par ministere
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	Map<String, Long> getCloseCount(CoreSession session) throws ClientException;

	/**
	 * Requête le nombre de questions signées par ministere
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	Map<String, Long> getSigneCount(CoreSession session) throws ClientException;

	/**
	 * Retourne la liste des dossierDocs signés pour l'id de ministère passé en paramètre.
	 * 
	 * @param session
	 *            Doit être une session unrestricted pour récupérer tous les dossiers (ce qui est le cas normalement
	 *            dans la migration)
	 * @param idMinistere
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> getSignedDossiersForMinistere(CoreSession session, String idMinistere) throws ClientException;

	/**
	 * Requête le nombre de question à migrer par ministere
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	Map<String, Long> getMigrableCount(CoreSession session) throws ClientException;

	/**
	 * Retourne la liste des dossierDocs à migrer pour l'id de ministère passé en paramètre.
	 * 
	 * @param session
	 *            Doit être une session unrestricted pour récupérer tous les dossiers (ce qui est le cas normalement
	 *            dans la migration)
	 * @param idMinistere
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> getMigrableDossiersForMinistere(CoreSession session, String idMinistere) throws ClientException;

	/**
	 * Requête le nombre de modèle de feuille de route par ministere
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	Map<String, Long> getModelFDRCount(CoreSession session) throws ClientException;

	/**
	 * Détermine si une migration est en cours
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	Boolean isMigrationEnCours(CoreSession session) throws ClientException;

	/**
	 * Récupère la migration en cours
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	ReponsesLogging getMigrationEnCours(CoreSession session) throws ClientException;

	/**
	 * Creation d'un document {@link ReponsesLogging} a la date du jour
	 * 
	 * @param session
	 * @param previsionnalCount
	 * @return
	 * @throws ClientException
	 */
	String createLogging(final CoreSession session, final Long previsionnalCount, final Long closePrevisionalCount,
			Map<String, String> timbre, String currentGouvernement, String nextGouvernement) throws ClientException;

	List<ReponsesLogging> getAllReponsesLogging(CoreSession session) throws ClientException;

	List<ReponsesLoggingLine> getAllReponsesLoggingLine(CoreSession session, String reponsesLoggingId)
			throws ClientException;

	List<ReponsesLoggingLineDetail> getAllReponsesLoggingLineDetail(CoreSession session, String reponsesLoggingLineId)
			throws ClientException;

	Map<String, String> getReponsesLoggingTimbre(final CoreSession session, final String reponsesLoggingId)
			throws ClientException;

	/**
	 * Calcul les questions closes pour un ministere donné
	 * 
	 * @param coreSession
	 * @param currentMin
	 * @return
	 * @throws ClientException
	 */
	Long getCloseCountForMinistere(CoreSession coreSession, String currentMin) throws ClientException;

	/**
	 * Génère la requête de récupération des ids des questions closes pour un ministère donné
	 * 
	 * @param currentMin
	 * @return
	 */
	String getQueryClosedQuestionsForMinistere(String currentMin);

}
