package fr.dila.reponses.api.cases;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.historique.HistoriqueAttribution;
import fr.dila.st.api.dossier.STDossier;

/**
 * Dossier
 * 
 */
public interface Dossier extends Serializable, STDossier, DossierCommon {

	/**
	 * get the FondDeDossier.
	 */
	FondDeDossier getFondDeDossier(CoreSession session);

	/**
	 * get the Question.
	 * 
	 * @param session
	 *            session
	 */
	Question getQuestion(CoreSession session);

	/**
	 * get the Reponse.
	 * 
	 * @param session
	 *            session
	 */
	Reponse getReponse(CoreSession session);

	/**
	 * Set the Dossier title and name.
	 */
	Dossier setDossierProperties(CoreSession session) throws ClientException;

	/**
	 * Create a FondDeDossier.
	 */
	FondDeDossier createFondDeDossier(CoreSession session) throws ClientException;

	/****************************************************************************
	 * 
	 * Dossier Question Properties.
	 */

	/**
	 * 
	 * Gets the Question number.
	 */
	Long getNumeroQuestion();

	void setNumeroQuestion(Long numeroQuestion);

	/**
	 * Gets the id Ministere Interpelle courant
	 */
	String getIdMinistereAttributaireCourant();

	void setIdMinistereAttributaireCourant(String idMinistereAttributaire);

	/**
	 * Recupère le ministère interpellé précédent (sauvegarde du min. précédent en cas de réattribution)
	 * 
	 * @return ministère interpellé précédent
	 */
	String getIdMinistereAttributairePrecedent();

	/**
	 * Stocke le ministère interpellé (sauvegarde du min. précédent en cas de réattribution)
	 */
	void setIdMinistereAttributairePrecedent(String idMinistereAttributairePrecedent);

	/**
	 * Retourne l'id du ministère à utiliser pour la réattribution
	 * 
	 * @return
	 */
	String getIdMinistereReattribution();

	/**
	 * Stocke l'id du ministère à utiliser pour la réattribution
	 */
	void setIdMinistereReattribution(String idMinistereReattribution);

	Boolean isArchivable(CoreSession session) throws ClientException;

	/**
	 * 
	 * @return nom du lot de l'allotissement
	 */
	String getDossierLot();

	/**
	 * nom du lot de l'allotissement
	 */
	void setDossierLot(String idDossierLot);

	/**
	 * Retourne vrai si le dossier a atteint une étape de rédaction
	 * 
	 * @return vrai si dossier a atteint ou depassé l'étape de rédaction
	 */
	Boolean getEtapeRedactionAtteinte();

	/**
	 * Si le paramêtre est vrai, le dossier a atteint une étape de rédaction
	 * 
	 * @param isEtapeRedactionAtteinte
	 */
	void setEtapeRedactionAtteinte(Boolean isEtapeRedactionAtteinte);

	/**
	 * Retourne vrai si le dossier a atteint une étape de signature
	 * 
	 * @return vrai si dossier a atteint ou depassé l'étape de signature
	 */
	Boolean getEtapeSignatureAtteinte();

	/**
	 * Si le paramêtre est vrai, le dossier a atteint une étape de signature
	 * 
	 * @param isEtapeSignatureAtteinte
	 */
	void setEtapeSignatureAtteinte(Boolean isEtapeSignatureAtteinte);

	/**
	 * Retourne le nombre de fois où le dossier a été réaffecté
	 * 
	 * @return le nombre de réaffections du dossier
	 */
	Long getReaffectionCount();

	/**
	 * Met à jour le compteur des réaffections
	 * 
	 * @param le
	 *            nouveau total des réaffections
	 */
	void setReaffectionCount(Long reaffectionCount);

	/**
	 * Retourne l'id du document question du dossier
	 */
	String getQuestionId();

	/**
	 * Met à jour l'id du document question
	 */
	void setQuestionId(String questionId);

	/**
	 * Retourne l'id du document reponse du dossier
	 */
	String getReponseId();

	/**
	 * Met à jour l'id du document reponse
	 */
	void setReponseId(String reponseId);

	/**
	 * Retourne l'id du document fondDeDossier du dossier
	 * 
	 * @param fddId
	 * @return
	 */
	String getFondDeDossierId();

	/**
	 * Met à jour l'id du document fond de dossier
	 * 
	 * @param fddId
	 */
	void setFondDeDossierId(String fddId);

	/**
	 * Un dossier est actif, si il a une feuille de route en cours. Si il a pas de feuille de route, il n'est pas actif.
	 * 
	 * @param session
	 * @return vrai si le dossier est actif
	 * @throws ClientException
	 */
	Boolean isActive(CoreSession session) throws ClientException;

	/**
	 * Appartenance du dossier à une liste d'élimination
	 * 
	 * @return
	 */
	void setListeElimination(String listeEliminationId);

	/**
	 * @return Retourne la liste d'élimination du dossier
	 */
	String getListeElimination();

	/**
	 * Create the Question document.
	 * 
	 * @param session
	 *            session
	 * @param questionObject
	 *            questionObjetWithMetadata
	 * @param etatQuestion
	 */
	void createQuestion(CoreSession session, Question questionObject, String etatQuestion);

	/**
	 * Create the Reponse document.
	 * 
	 * @param session
	 *            session
	 * @param numeroQuestion
	 *            numeroQuestion used to set Reponse PathName and Title
	 */
	DocumentModel createReponse(CoreSession session, Long numeroQuestion, Reponse reponse);

	List<HistoriqueAttribution> getHistoriqueAttribution(CoreSession session);

	/**
	 * Ajoute un historique d'attribution à la date du jour.
	 * 
	 * @param feuilleRouteTypeCreationInstanciation
	 * @param idMinistere
	 * @throws ClientException
	 */
	void addHistoriqueAttribution(CoreSession session, String feuilleRouteTypeCreationInstanciation, String idMinistere)
			throws ClientException;

	/**
	 * Méthode retournant le label de la prochaine étape.
	 */
	public String getLabelNextStep();

	public void setLabelNextStep(String labelEtapeSuivante);

	/**
	 * Ajoute un historique d'attribution.
	 * 
	 * @param feuilleRouteTypeCreationInstanciation
	 * @param date
	 * @param idMinistere
	 * @throws ClientException
	 */
	void addHistoriqueAttribution(CoreSession session, String feuilleRouteTypeCreationInstanciation, Calendar date,
			String idMinistere) throws ClientException;

	/**
	 * Récupère la métadonnée isArbitrated
	 * 
	 * @return
	 */
	Boolean isArbitrated();

	/**
	 * Met à jour la métadonnée isArbitrated
	 * 
	 * @param isArbitrated
	 */
	void setIsArbitrated(Boolean isArbitrated);

	/**
	 * Récupère la métadonnée hasPJ
	 */
	Boolean hasPJ();

	/**
	 * Met à jour la métadonnée hasPJ
	 */
	void setHasPJ(Boolean hasPJ);
	
	/**
	 * Récupère la métadonnée isRedemarre
	 */
	boolean isRedemarre();
	
	/**
	 * Met à jour la métadonnée isRedemarre
	 */
	void setIsRedemarre(boolean isRedemarrer);
}
