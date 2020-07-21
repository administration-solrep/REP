package fr.dila.reponses.api.cases;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.dila.reponses.api.cases.flux.QErratum;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.st.api.domain.STDomainObject;

/**
 * DocumentDila document.
 * <p>
 * Represents a question item inside a dossier.
 * 
 */
public interface Question extends DossierCommon, STDomainObject {

	/****************************************************************************
	 * 
	 * Question Method.
	 */

	/**
	 * get All the Question properties from another question object
	 * 
	 * @param question
	 *            question
	 * @return DocumentModel question object updated
	 * @throws ClientException
	 */
	DocumentModel getQuestionMetadata(Question question) throws ClientException;

	/****************************************************************************
	 * 
	 * Question Properties Getter/Setter.
	 */

	/**
	 * 
	 * Gets the Question number.
	 */
	Long getNumeroQuestion();

	void setNumeroQuestion(Long numeroQuestion);

	/**
	 * Gets the Question Origin.
	 */
	String getOrigineQuestion();

	void setOrigineQuestion(String origineQuestion);

	/**
	 * Gets the Question Type.
	 */
	String getTypeQuestion();

	void setTypeQuestion(String typeQuestion);

	/**
	 * Gets the Question legislature
	 */
	Long getLegislatureQuestion();

	void setLegislatureQuestion(Long legislature);

	/**
	 * Gets the Question reception date.
	 */
	Calendar getDateReceptionQuestion();

	void setDateReceptionQuestion(Calendar dateReceptionQuestion);

	/**
	 * Gets the Question Author groupe politique.
	 */
	String getGroupePolitique();

	void setGroupePolitique(String groupePolitique);

	/**
	 * Gets the Question Author groupe politique.
	 */
	String getPageJO();

	void setPageJO(String pageJO);

	/**
	 * Gets the Question date publication JO.
	 */
	Calendar getDatePublicationJO();

	void setDatePublicationJO(Calendar datePublicationJO);

	/**
	 * Gets the Question id Ministere Interpelle.
	 */
	String getIdMinistereInterroge();

	void setIdMinistereInterroge(String idMinistereInterroge);

	/**
	 * Gets the Question titre JO Ministere.
	 */
	String getTitreJOMinistere();

	void setTitreJOMinistere(String titreJOMinistere);

	/**
	 * Gets the Question intitule Ministere.
	 */
	String getIntituleMinistere();

	void setIntituleMinistere(String intituleMinistere);

	/**
	 * Gets the Question id Ministere Attributaire.
	 */
	String getIdMinistereAttributaire();

	void setIdMinistereAttributaire(String idMinistereAttributaire);

	/**
	 * Gets the Question intitule Ministere Attributaire.
	 */
	String getIntituleMinistereAttributaire();

	void setIntituleMinistereAttributaire(String intituleMinistereAttributaire);

	/**
	 * Gets the Question nom Auteur.
	 */
	String getNomAuteur();

	void setNomAuteur(String nomAuteur);

	/**
	 * Gets the Question prenom Auteur.
	 */
	String getPrenomAuteur();

	void setPrenomAuteur(String prenomAuteur);

	/**
	 * Gets the Question civilite Auteur.
	 */
	String getCiviliteAuteur();

	void setCiviliteAuteur(String civiliteAuteur);

	/**
	 * Gets the Question nom complet auteur. Mise a jour quand on modife le nom ou le prenom de l'auteur
	 */
	String getNomCompletAuteur();

	/**
	 * Gets the content of the question.
	 */
	String getTexteQuestion();

	void setTexteQuestion(String texteQuestion);

	/**
	 * Gets the Question idMandat.
	 */
	String getIdMandat();

	void setIdMandat(String idMandat);

	/**
	 * Gets the Question Analyse Assemblee Nationale.
	 */
	List<String> getAssNatAnalyses();

	void setAssNatAnalyses(List<String> analyseAssNat);

	/**
	 * Gets the Question Rubrique Assemblee Nationale.
	 */
	List<String> getAssNatRubrique();

	void setAssNatRubrique(List<String> rubriqueAssNat);

	/**
	 * Gets the Question Tete Analyse Assemblee Nationale.
	 */
	List<String> getAssNatTeteAnalyse();

	void setAssNatTeteAnalyse(List<String> teteAnalyseAssNat);

	/**
	 * Gets the Question senat Id.
	 */
	String getSenatQuestionId();

	void setSenatQuestionId(String senatQuestionId);

	/**
	 * Gets the Question senat Titre.
	 */
	String getSenatQuestionTitre();

	void setSenatQuestionTitre(String senatQuestionTitre);

	/**
	 * Gets the Question Senat Themes.
	 */
	List<String> getSenatQuestionThemes();

	void setSenatQuestionThemes(List<String> senatQuestionTheme);

	/**
	 * Gets the Question Senat Rubrique.
	 */
	List<String> getSenatQuestionRubrique();

	void setSenatQuestionRubrique(List<String> senatRubrique);

	/**
	 * Gets the Question Senat Renvois.
	 */
	List<String> getSenatQuestionRenvois();

	void setSenatQuestionRenvois(List<String> senatRenvois);

	/**
	 * Gets the Question Date Renouvellement Question.
	 * 
	 * defini via setEtatQuestion
	 */
	Calendar getDateRenouvellementQuestion();

	/**
	 * Gets the Question Date Signalement Question.
	 * 
	 * defini via setEtatQuestion
	 */
	Calendar getDateSignalementQuestion();

	/**
	 * Getter et setter de dateTransmissionAssemblees.
	 */
	Calendar getDateTransmissionAssemblees();

	void setDateTransmissionAssemblees(Calendar dateTransmissionAssemblees);

	/**
	 * Gets the Question Date Retrait Question.
	 * 
	 * Proriété positionné lors de changement d'état
	 */
	Calendar getDateRetraitQuestion();

	/**
	 * Récupère l'état de la question (le dernier de l'historique)
	 * 
	 * @return;
	 */
	QuestionStateChange getEtatQuestion(CoreSession session);

	/**
	 * positionne le dernier etat et le rajoute a l'historique
	 * 
	 * @param delaiQuestionSignalee valeur du paramètre délai de réponse (entier en jours) à une question signalée.
	 * @return
	 */
	void setEtatQuestion(CoreSession session, String etatQuestion, Calendar changeDate, String delaiQuestionSignalee);

	/**
	 * Historique des etat pris par la question Mise a jour par appel a setEtatQuestion qui positionne le dernier etat
	 * et met a jour l'historique
	 * 
	 * @return
	 */
	List<QuestionStateChange> getEtatQuestionHistorique(CoreSession session);

	List<Signalement> getSignalements();

	void setSignalements(List<Signalement> signalements);

	/**
	 * Retourne les renouvellements de la question. Ne peut pas être nul.
	 * 
	 * @return
	 */
	List<Renouvellement> getRenouvellements();

	List<QErratum> getErrata();

	void setErrata(List<QErratum> errata);

	List<String> getMotsClefMinistere();

	void setMotsClefMinistere(List<String> motClefsMinistere);

	List<String> getMotsClef();

	Boolean isPublished();

	/**
	 * Gets the Question caracteristiques.
	 */
	String getCaracteristiquesQuestion();

	void setCaracteristiquesQuestion(String caracteristiquesQuestion);

	// L'identifiant de la question utilisé pour les recherche, composé de la source et du numéro
	// mise a jour quand on definit origineQuestion ou numeroQuestion
	String getSourceNumeroQuestion();

	DocumentRef getDossierRef();

	/**
	 * Met la valeur si la question possède une réponse non videe.
	 * 
	 * @return
	 */
	public void setHasReponseInitiee(Boolean hasReponseInitiee);

	/**
	 * Retourne vraie si la question a une réponse non vide.
	 * 
	 * @return vrai si la question à une réponse
	 */
	public Boolean hasReponseInitiee();

	String getSourceId();

	DocumentModel getDocument();

	// Gestion des hash pour la connexite

	/**
	 * @return hash sur le titre
	 */
	String getHashConnexiteTitre();

	void setHashConnexiteTitre(String hash);

	/**
	 * @return hash sur le texte
	 */
	String getHashConnexiteTexte();

	void setHashConnexiteTexte(String hash);

	/**
	 * @return hash sur l'indexation SE
	 */
	String getHashConnexiteSE();

	void setHashConnexiteSE(String hash);

	/**
	 * @return hash sur l'indexation AN
	 */
	String getHashConnexiteAN();

	void setHashConnexiteAN(String hash);

	public String getTexteJoint();

	public void setTexteJoint(String typeQuestion);

	public String getEtatQuestionSimple();

	/**
	 * Propriete positionné via les changements d'etat
	 * 
	 * @return
	 */
	Boolean getEtatRetire();

	/**
	 * Propriete positionné via les changements d'etat
	 * 
	 * @return
	 */
	Boolean getEtatNonRetire();

	/**
	 * Propriete positionné via les changements d'etat
	 * 
	 * @return
	 */
	Boolean getIsReattribue();

	/**
	 * Propriete positionné via les changements d'etat
	 * 
	 * @return
	 */
	Boolean getEtatRenouvele();

	/**
	 * Propriete positionné via les changements d'etat
	 * 
	 * @return
	 */
	Boolean getEtatSignale();

	Boolean getEtatRappele();

	/**
	 * @return La circonscription de l'auteur.
	 */
	void setCirconscriptionAuteur(String value);

	String getCirconscriptionAuteur();

	String getEtatsQuestion();

	void setEtatsQuestion(String etatsQuestion);

	String getMotsCles();

	void setMotsCles(String motsCles);

	/**
	 * get propriete dateCaduciteQuestion
	 * 
	 * Propriete defini par un appel a setEtatQuestion
	 * 
	 * @return
	 */
	Calendar getDateCaducite();

	/**
	 * Indexation Complementaire Gets the Question Analyse Assemblee Nationale.
	 */
	List<String> getIndexationComplAssNatAnalyses();

	void setIndexationComplAssNatAnalyses(List<String> analyseAssNat);

	/**
	 * Indexation Complementaire Gets the Question Rubrique Assemblee Nationale.
	 */
	List<String> getIndexationComplAssNatRubrique();

	void setIndexationComplAssNatRubrique(List<String> rubriqueAssNat);

	/**
	 * Indexation Complementaire Gets the Question Tete Analyse Assemblee Nationale.
	 */
	List<String> getIndexationComplAssNatTeteAnalyse();

	void setIndexationComplAssNatTeteAnalyse(List<String> teteAnalyseAssNat);

	/**
	 * Indexation Complementaire Gets the Question Senat Themes.
	 */
	List<String> getIndexationComplSenatQuestionThemes();

	void setIndexationComplSenatQuestionThemes(List<String> senatQuestionTheme);

	/**
	 * Indexation Complementaire Gets the Question Senat Rubrique.
	 */
	List<String> getIndexationComplSenatQuestionRubrique();

	void setIndexationComplSenatQuestionRubrique(List<String> senatRubrique);

	/**
	 * Indexation Complementaire Gets the Question Senat Renvois.
	 */
	List<String> getIndexationComplSenatQuestionRenvois();

	void setIndexationComplSenatQuestionRenvois(List<String> senatRenvois);

	/**
	 * Indexation Complementaire mot cle ministere
	 */
	List<String> getIndexationComplMotsClefMinistere();

	void setIndexationComplMotsClefMinistere(List<String> motClefsMinistere);

	/* --- Ensemble de tests sur les propriétés ci-dessus --- */

	/** teste si la question est repondue */
	Boolean isRepondue();

	/** teste si la question est retiree */
	Boolean isRetiree();

	/**
	 * Retourne vraie si la question est signalée.
	 * 
	 * @return Vraie si signalée
	 */
	Boolean isSignale();

	/**
	 * 
	 * Retourne vrai si la question est renouvellée.
	 * 
	 * @return Vrai si renouvellée
	 */
	Boolean isRenouvelle();

	/**
	 * Retourne vrai si la question est une question ecrite
	 */
	Boolean isQuestionTypeEcrite();

	/**
	 * Retourne si la question a pour orgine l'AN
	 */
	Boolean hasOrigineAN();

	/**
	 * Retourne vrai si la question a des données d'indexation AN
	 */
	Boolean hasIndexationAn();

	/**
	 * Retourne vrai si la question a des données d'indexation Senat
	 */
	Boolean hasIndexationSenat();

	/**
	 * Retourne vrai si la question a des données d'indexation complementaire AN
	 */
	Boolean hasIndexationComplementaireAn();

	/**
	 * Retourne vrai si la question a des données d'indexation complementaire SENAT
	 */
	Boolean hasIndexationComplementaireSenat();

	/**
	 * Retourne vrai si la question a des données d'indexation complementaire mot clé
	 * 
	 * @return
	 */
	Boolean hasIndexationComplementaireMotCleMinistere();

	/**
	 * Met la valeur à vrai si le nombre de réaffectation est supérieur à zéro.
	 * 
	 * @param etat
	 */
	public void setIsReattribue(Boolean isReattribue);

	void setDateSignalementQuestion(Calendar dateSignalementQuestion);

	void setDateClotureQuestion(Calendar dateClotureQuestion);

	Calendar getDateRappelQuestion();

	void setDateRappelQuestion(Calendar dateRappelQuestion);

	/**
	 * Gets the Question id Ministere rattachement.
	 */
	String getIdMinistereRattachement();

	void setIdMinistereRattachement(String idMinistereRattachement);

	/**
	 * Gets the Question intitule Ministere rattachement.
	 */
	String getIntituleMinistereRattachement();

	void setIntituleMinistereRattachement(String intituleMinistereRattachement);

	/**
	 * Gets the Question id Ministere rattachement.
	 */
	String getIdDirectionPilote();

	void setIdDirectionPilote(String idDirectionPilote);

	/**
	 * Gets the Question intitule pilote.
	 */
	String getIntituleDirectionPilote();

	void setIntituleDirectionPilote(String intituleDirectionPilote);

	boolean hasConnexite();

}
