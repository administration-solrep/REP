package fr.dila.reponses.api.service;

import java.util.Collection;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.st.api.feuilleroute.STRouteStep;

/**
 * Service permettant d'effectuer des actions spécifiques sur les instances de feuille de route dans Réponses.
 * 
 * @author jtremeaux
 */
public interface FeuilleRouteService extends SSFeuilleRouteService {

	/**
	 * Retourne l'étape "Validation premier ministre" correspondant au dossier.
	 * 
	 * @param session
	 *            Session
	 * @param feuilleRouteInstanceId
	 *            Identifiant technique de l'instance de feuille de route
	 * @return Etape validation PM
	 * @throws ClientException
	 */
	DocumentModel getValidationPMStep(CoreSession session, String feuilleRouteInstanceId) throws ClientException;

	/**
	 * Ajout d'une étape Pour attente à la suite de l'étape en cours
	 * 
	 * @param session
	 *            session
	 * @param routingTaskId
	 *            id de l'étape en cours
	 * @throws ClientException
	 */
	void addStepAttente(CoreSession session, DocumentModel etapeDoc) throws ClientException;

	/**
	 * Ajout des étapes 'pour retour', 'Pour signature' et 'Pour validation retour Premier ministre' à la suite de
	 * l'étape en cours
	 * 
	 * @param session
	 *            session
	 * @param etapeDoc
	 *            doc de l'étape en cours
	 * @throws ClientException
	 */
	void addStepValidationRetourPM(final CoreSession session, final DocumentModel etapeDoc,
			final DocumentModel dossierDoc) throws ClientException;

	/**
	 * Vérifie l'étape de feuille de route suivante, valide l'étape si c'est réattribution ou réorientation, dans le cas
	 * de la réattribution on test si le ministère du poste associé à l'étape réattribution est différent du ministère
	 * du poste de l'étape en cours.
	 * 
	 * @param session
	 *            session
	 * @param routingTaskId
	 *            id de l'étape en cours
	 * @return true si réattribution ou réorientation
	 * @throws ClientException
	 */
	boolean isNextStepReorientationOrReattributionOrArbitrage(CoreSession session, String routingTaskId)
			throws ClientException;

	/**
	 * Ajout d'une étape réorientation suite à un non concerné sur l'étape en cours
	 * 
	 * @param session
	 * @param routingTaskId
	 * @return
	 * @throws ClientException
	 */
	void addStepAfterReorientation(CoreSession session, DocumentModel etapeDoc) throws ClientException;

	/**
	 * Ajout d'une étape réorientation suite à un non concerné sur l'étape en cours
	 * 
	 * @param session
	 * @param etapeDoc
	 * @param mailboxId
	 *            id de la mailbox du poste à ajouter
	 * @throws ClientException
	 */
	void addStepAfterReorientation(CoreSession session, DocumentModel etapeDoc, String mailboxId)
			throws ClientException;

	/**
	 * Ajout d'une étape réattribution suite à un non concerné sur l'étape en cours
	 * 
	 * @param session
	 * @param routingTaskId
	 * @throws ClientException
	 */
	void addStepAfterReattribution(CoreSession documentManager, DocumentModel etapeDoc, String mailboxId)
			throws ClientException;

	/**
	 * Ajout d'une étape attribution suite à un rejet de réattribution sur l'étape en cours
	 * 
	 * @param session
	 * @param routingTaskId
	 * @throws ClientException
	 */
	void addStepAfterRejectReattribution(CoreSession session, String routingTaskId) throws ClientException;

	/**
	 * 
	 * Retourne la liste des dossiers ayant été validé depuis moins de 7 jours sur le poste de l'utilisateur.
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return
	 * @throws ClientException
	 */
	List<Dossier> getLastWeekValidatedDossiers(CoreSession session, Collection<String> posteIds) throws ClientException;

	/**
	 * Ajoute des étapes pour signature et pour transmission aux assemblées après l'étape donnée
	 * 
	 * @param session
	 * @param routingTaskId
	 * @throws ClientException
	 */
	void addStepsSignatureAndTransmissionAssemblees(CoreSession session, DocumentModel dossierDoc, STRouteStep routeStep)
			throws ClientException;
	
	/**
	 * Ajout de nouvelles étapes suite au rejet de l'étape en cours.
	 * 
	 * @param session
	 *            Session
	 * @param routingTaskId
	 *            id de l'étape en cours
	 * @param dossier
	 *            dossier
	 * @throws ClientException
	 */
	boolean addStepAfterReject(CoreSession session, String routingTaskId, Dossier dossier) throws ClientException;

	/**
	 * Retourne true si la prochaine étape est de type Pour tranmission aux assemblées
	 * 
	 * @param session
	 * @param routingTaskId
	 * @return
	 * @throws ClientException
	 */
	boolean isNextStepTransmissionAssemblees(CoreSession session, String feuilleRouteDocId, DocumentModel stepDoc)
			throws ClientException;

	/**
	 * Retourne true si l'étape passée en paramètre est à la racine de sa feuille de route
	 * 
	 * @param session
	 *            CoreSession
	 * @param routingTaskId
	 *            id de l'étape de feuille de route
	 * @return boolean
	 * @throws ClientException
	 */
	boolean isRootStep(CoreSession session, String routingTaskId) throws ClientException;

	/**
	 * Envoi du mail après la distribution d'un dossier
	 * 
	 * @param session
	 * @param routeStep
	 * @throws ClientException
	 */
	void sendMailAfterDistribution(CoreSession session, STRouteStep routeStep) throws ClientException;

	/**
	 * Retourne la liste des étapes validées durant les dernières 24h
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<STRouteStep> getLastDayValidatedSteps(CoreSession session) throws ClientException;

	/**
	 * Envoi des mails aux utilisateurs dont des dossiers ont transités par leur corbeille durant les dernières 24h
	 * 
	 * @param session
	 * @throws ClientException
	 */
	void sendDailyDistributionMail(CoreSession session) throws ClientException;

	void initDirectionFdr(CoreSession session) throws ClientException;

	/**
	 * Indique si l'étape suivante celle en cours est "pour arbitrage"
	 * 
	 * @param session
	 * @param routingTaskId
	 * @return
	 * @throws ClientException
	 */
	boolean isNextStepArbitrage(CoreSession session, String routingTaskId) throws ClientException;
}
