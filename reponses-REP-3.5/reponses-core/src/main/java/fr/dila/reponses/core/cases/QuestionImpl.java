package fr.dila.reponses.core.cases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.QErratum;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.recherche.ReponsesComplIndexableDocument;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.core.cases.flux.QErratumImpl;
import fr.dila.reponses.core.cases.flux.RenouvellementImpl;
import fr.dila.reponses.core.cases.flux.SignalementImpl;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.DocUtil;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation d'une Question
 * 
 */
public class QuestionImpl extends STDomainObjectImpl implements Question {
	private static final long				serialVersionUID	= 1L;

	private ReponsesIndexableDocument		indexation;
	private ReponsesComplIndexableDocument	indexationCompl;

	// Stockage temporaire pour pas refaire Nfois des assignations
	private List<QuestionStateChange>		qscList				= null;
	private List<Renouvellement>			renouvellements		= null;
	private List<Signalement>				signalements		= null;

	private static final STLogger			LOGGER				= STLogFactory.getLog(QuestionImpl.class);
	private static final String				QUERY_ETAT_QUESTION	= "SELECT e.ecm:uuid AS id FROM "
																		+ ReponsesSchemaConstant.ETAT_QUESTION_DOCUMENT_TYPE
																		+ " as e WHERE e.ecm:parentId"
																		+ " = ? ORDER BY e."
																		+ ReponsesSchemaConstant.ETAT_QUESTION_DOC_SCHEMA_PREFIX
																		+ ":"
																		+ ReponsesSchemaConstant.ETAT_QUESTION_DATE_PROPERTY;

	public QuestionImpl(DocumentModel document) {
		super(document);
		indexation = getDocument().getAdapter(ReponsesIndexableDocument.class);
		indexationCompl = getDocument().getAdapter(ReponsesComplIndexableDocument.class);
	}

	@Override
	public DocumentModel getQuestionMetadata(Question question) throws ClientException {

		/**
		 * Copie du contenu des schema question, indexation et indexation_complemenataire
		 */
		DocUtil.copySchema(question.getDocument(), getDocument(), DossierConstants.QUESTION_DOCUMENT_SCHEMA);
		DocUtil.copySchema(question.getDocument(), getDocument(), DossierConstants.INDEXATION_DOCUMENT_SCHEMA);
		DocUtil.copySchema(question.getDocument(), getDocument(),
				DossierConstants.INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA);

		setNomCompletAuteur(getNomAuteur(), getPrenomAuteur());
		setSourceNumeroQuestion(getOrigineQuestion(), getNumeroQuestion());

		return getDocument();
	}

	/**
	 * Overrides equality to check documents equality
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QuestionImpl)) {
			return false;
		}
		QuestionImpl otherItem = (QuestionImpl) other;
		return getId().equals(otherItem.getId());
	}

	@Override
	public int hashCode() {
		return getDocument().hashCode();
	}

	protected List<Signalement> getSignalementsProperty(String schema, String value) {
		List<Signalement> signalementList = new ArrayList<Signalement>();
		List<Map<String, Serializable>> signalements = PropertyUtil.getMapStringSerializableListProperty(getDocument(),
				schema, value);
		if (signalements != null) {
			for (Map<String, Serializable> signalement : signalements) {
				signalementList.add(new SignalementImpl(signalement));
			}
		}
		return signalementList;
	}

	protected List<Renouvellement> getRenouvellementsProperty(String schema, String value) {
		List<Renouvellement> renouvellementList = new ArrayList<Renouvellement>();
		List<Map<String, Serializable>> renouvellements = PropertyUtil.getMapStringSerializableListProperty(
				getDocument(), schema, value);
		if (renouvellements != null) {
			for (Map<String, Serializable> renouvellement : renouvellements) {
				renouvellementList.add(new RenouvellementImpl(renouvellement));
			}
		}
		return renouvellementList;
	}

	protected List<QErratum> getErrataProperty(String schema, String value) {
		List<QErratum> erratumList = new ArrayList<QErratum>();
		List<Map<String, Serializable>> errata = PropertyUtil.getMapStringSerializableListProperty(getDocument(),
				schema, value);
		if (errata != null) {
			for (Map<String, Serializable> erratum : errata) {
				erratumList.add(new QErratumImpl(erratum));
			}
		}
		return erratumList;
	}

	protected List<QuestionStateChange> getEtatsQuestionsDoc(CoreSession session) {
		List<QuestionStateChange> questionStateChangeList = new ArrayList<QuestionStateChange>();
		try {
			List<DocumentModel> etatQuestionDocs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
					"EtatQuestion", QUERY_ETAT_QUESTION, new Object[] { this.getId() });
			for (DocumentModel etatDoc : etatQuestionDocs) {
				questionStateChangeList.add(etatDoc.getAdapter(QuestionStateChange.class));
			}
		} catch (ClientException exc) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_HISTORIQUE_ATTR_TEC, exc);
		}
		return questionStateChangeList;
	}

	@Override
	public void setErrata(List<QErratum> errata) {
		ArrayList<Map<String, Serializable>> listeErratum = new ArrayList<Map<String, Serializable>>();
		for (QErratum erratum : errata) {
			Map<String, Serializable> erratumMap = new HashMap<String, Serializable>();
			erratumMap.put(DossierConstants.DOSSIER_ERRATUM_DATE_PUBLICATION_PROPERTY, erratum.getDatePublication());
			erratumMap.put(DossierConstants.DOSSIER_ERRATUM_PAGE_JO_PROPERTY, erratum.getPageJo());
			erratumMap.put(DossierConstants.DOSSIER_ERRATUM_TEXTE_CONSOLIDE_PROPERTY, erratum.getTexteConsolide());
			erratumMap.put(DossierConstants.DOSSIER_ERRATUM_TEXTE_ERRATUM_PROPERTY, erratum.getTexteErratum());
			listeErratum.add(erratumMap);
		}
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ERRATA_PROPERTY, listeErratum);
	}

	/**
	 * Question properties
	 */

	@Override
	public Long getNumeroQuestion() {
		return getLongProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_NUMERO_QUESTION);
	}

	@Override
	public void setNumeroQuestion(Long numeroQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_NUMERO_QUESTION, numeroQuestion);
		setSourceNumeroQuestion(getOrigineQuestion(), numeroQuestion);
	}

	@Override
	public String getOrigineQuestion() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ORIGINE_QUESTION);
	}

	@Override
	public void setOrigineQuestion(String origineQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ORIGINE_QUESTION,
				origineQuestion);
		setSourceNumeroQuestion(origineQuestion, getNumeroQuestion());
	}

	@Override
	public String getTypeQuestion() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_TYPE_QUESTION);
	}

	@Override
	public void setTypeQuestion(String typeQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_TYPE_QUESTION, typeQuestion);
	}

	@Override
	public Long getLegislatureQuestion() {
		return getLongProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_LEGISLATURE_QUESTION);
	}

	@Override
	public void setLegislatureQuestion(Long legislature) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_LEGISLATURE_QUESTION,
				legislature);
	}

	@Override
	public Calendar getDateReceptionQuestion() {
		return getDateProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_DATE_RECEPTION_QUESTION);
	}

	@Override
	public void setDateReceptionQuestion(Calendar dateReceptionQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_RECEPTION_QUESTION,
				dateReceptionQuestion);
	}

	@Override
	public String getGroupePolitique() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_GROUPE_POLITIQUE_QUESTION);
	}

	@Override
	public void setGroupePolitique(String groupePolitique) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_GROUPE_POLITIQUE_QUESTION,
				groupePolitique);
	}

	@Override
	public String getTexteQuestion() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_TEXTE_QUESTION);
	}

	@Override
	public void setTexteQuestion(String texteQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_TEXTE_QUESTION, texteQuestion);
	}

	@Override
	public QuestionStateChange getEtatQuestion(CoreSession session) {
		if (qscList == null) {
			qscList = getEtatQuestionHistorique(session);
		}
		if (qscList != null && !qscList.isEmpty()) {
			return qscList.get(qscList.size() - 1);
		}
		return null;
	}

	@Override
	public List<QuestionStateChange> getEtatQuestionHistorique(CoreSession session) {
		return getEtatsQuestionsDoc(session);
	}

	private void addHistoriqueEtatQuestion(CoreSession session, Calendar date, String label) {

		String titreDoc = "EtatQuestion-" + DateUtil.formatYYYYMMdd(date.getTime()) + "-" + label;

		try {
			DocumentModel etatDoc = session.createDocumentModel(getDocument().getPath().toString(), titreDoc,
					ReponsesSchemaConstant.ETAT_QUESTION_DOCUMENT_TYPE);
			QuestionStateChange etat = etatDoc.getAdapter(QuestionStateChange.class);
			if (etat != null) {
				etat.setChangeDate(date);
				etat.setNewState(label);
				session.createDocument(etat.getDocument());
				session.save();
			}
		} catch (ClientException exc) {
			LOGGER.error(ReponsesLogEnumImpl.FAIL_SAVE_QUESTION_TEC, exc);

		}
	}

	/**
	 * Retourne si une valeur d'état question peut-etre un etat courant pris par la question
	 * 
	 * @return vrai pour ETAT_QUESTION_RETIREE, ETAT_QUESTION_CADUQUE, ETAT_QUESTION_EN_COURS, ETAT_QUESTION_REPONDU et
	 *         ETAT_QUESTION_CLOTURE_AUTRE
	 */
	protected static boolean canBeCurrentStateOfQuestion(String etatQuestion) {
		return VocabularyConstants.ETAT_QUESTION_RETIREE.equals(etatQuestion)
				|| VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion)
				|| VocabularyConstants.ETAT_QUESTION_EN_COURS.equals(etatQuestion)
				|| VocabularyConstants.ETAT_QUESTION_REPONDU.equals(etatQuestion)
				|| VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion);
	}

	/**
	 * Retourne si une valeur d'état question peut-etre doit etre ajouté a l'historique
	 * 
	 * @param etatQuestion
	 * @return vrai si l'etat verifie canBeCurrentStateOfQuestion OU ETAT_QUESTION_SIGNALEE, ETAT_QUESTION_RENOUVELEE
	 * @see canBeCurrentStateOfQuestion
	 */
	protected static boolean shouldBeListedInHistoric(String etatQuestion) {
		return canBeCurrentStateOfQuestion(etatQuestion)
				|| VocabularyConstants.ETAT_QUESTION_SIGNALEE.equals(etatQuestion)
				|| VocabularyConstants.ETAT_QUESTION_RENOUVELEE.equals(etatQuestion);
	}

	@Override
	public void setEtatQuestion(CoreSession session, String etatQuestion, Calendar date, String delaiQuestionSignalee) {

		// Pour les etats ETAT_QUESTION_REATTRIBUEEE, ETAT_QUESTION_RAPPELE
		// ont ne change pas l'etat courant, ni l'historique

		if (canBeCurrentStateOfQuestion(etatQuestion)) {
			// Met à jour la métadonnée simple état question pour la recherche
			// qui correspond a l'état courant de la question
			// les états courants étant ceux decrit ci-dessus
			setEtatQuestionSimple(etatQuestion);
		}

		if (shouldBeListedInHistoric(etatQuestion)) {
			addHistoriqueEtatQuestion(session, date, etatQuestion);
		}

		// Met à jour les métadonnées simples d'état.
		if (VocabularyConstants.ETAT_QUESTION_SIGNALEE.equals(etatQuestion)) {
			setEtatSignale(true);
			setDateSignalementQuestion(date);

			List<Signalement> signalementList = getSignalements();
			Signalement nouveauSignalement = new SignalementImpl();
			nouveauSignalement.setDateEffet(date.getTime());
			
			int delaiReponseSignaleInt = 0;
			try {
				delaiReponseSignaleInt = Integer.parseInt(delaiQuestionSignalee);
			} catch (NumberFormatException nfe) {
				LOGGER.warn(ReponsesLogEnumImpl.FAIL_SAVE_QUESTION_TEC, nfe);
			}
			Calendar dateAttendue = (Calendar) date.clone(); // On ne modifie pas la date renseignée en paramètre
			dateAttendue.add(Calendar.DAY_OF_YEAR, delaiReponseSignaleInt);
			nouveauSignalement.setDateAttendue(dateAttendue.getTime());
			
			signalementList.add(nouveauSignalement);
			setSignalements(signalementList);

		} else if (VocabularyConstants.ETAT_QUESTION_RENOUVELEE.equals(etatQuestion)) {
			setEtatRenouvele(true);
			setDateRenouvellementQuestion(date);

			List<Renouvellement> renouvellementList = getRenouvellements();
			Renouvellement nouveauRenouvellement = new RenouvellementImpl();
			nouveauRenouvellement.setDateEffet(date.getTime());
			renouvellementList.add(nouveauRenouvellement);
			setRenouvellements(renouvellementList);

		} else if (VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE.equals(etatQuestion)) {
			setIsReattribue(true);

		} else if (VocabularyConstants.ETAT_QUESTION_RETIREE.equals(etatQuestion)) {
			setEtatRetire(true);
			setEtatNonRetire(false);
			setDateRetraitQuestion(date);
		} else if (VocabularyConstants.ETAT_QUESTION_RAPPELE.equals(etatQuestion)) {
			setEtatRappele(true);
			setDateRappelQuestion(date);
		} else if (VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion)) {
			setDateCaducite(date);
		} else if (VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion)) {
			setDateClotureQuestion(date);
		}

	}

	private void setEtatQuestionSimple(String etatQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_QUESTION_ITEM_PROPERTY,
				etatQuestion);
	}

	@Override
	public String getEtatQuestionSimple() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_ETAT_QUESTION_ITEM_PROPERTY);
	}

	@Override
	public String getPageJO() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_PAGE_JO_QUESTION);
	}

	@Override
	public void setPageJO(String pageJO) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_PAGE_JO_QUESTION, pageJO);
	}

	@Override
	public Calendar getDatePublicationJO() {
		return getDateProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_DATE_PUBLICATION_JO_QUESTION);
	}

	@Override
	public void setDatePublicationJO(Calendar datePublicationJO) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_PUBLICATION_JO_QUESTION,
				datePublicationJO);
	}

	@Override
	public String getIdMinistereInterroge() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_ID_MINISTERE_INTERROGE_QUESTION);
	}

	@Override
	public void setIdMinistereInterroge(String idMinistereInterroge) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_ID_MINISTERE_INTERROGE_QUESTION, idMinistereInterroge);
	}

	@Override
	public String getTitreJOMinistere() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_TITRE_JO_MINISTERE_QUESTION);
	}

	@Override
	public void setTitreJOMinistere(String titreJOMinistere) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_TITRE_JO_MINISTERE_QUESTION,
				titreJOMinistere);
	}

	@Override
	public String getIntituleMinistere() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_INTITULE_MINISTERE_QUESTION);
	}

	@Override
	public void setIntituleMinistere(String intituleMinistere) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_INTITULE_MINISTERE_QUESTION,
				intituleMinistere);
	}

	@Override
	public String getIdMinistereAttributaire() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_QUESTION);
	}

	@Override
	public void setIdMinistereAttributaire(String idMinistereAttributaire) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_QUESTION, idMinistereAttributaire);
	}

	@Override
	public String getIntituleMinistereAttributaire() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_INTITULE_MINISTERE_ATTRIBUTAIRE_QUESTION);
	}

	@Override
	public void setIntituleMinistereAttributaire(String intituleMinistereAttributaire) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_INTITULE_MINISTERE_ATTRIBUTAIRE_QUESTION, intituleMinistereAttributaire);
	}

	@Override
	public String getNomCompletAuteur() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_NOM_COMPLET_AUTEUR_QUESTION);
	}

	private void setNomCompletAuteur(String nomCompletAuteur) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_NOM_COMPLET_AUTEUR_QUESTION,
				nomCompletAuteur);

	}

	private void setNomCompletAuteur(String nomAuteur, String prenomAuteur) {
		String nomCompletAuteur;
		if (nomAuteur == null) {
			// nomAuteur null
			nomCompletAuteur = prenomAuteur;
		} else {
			if (prenomAuteur == null) {
				// prenomAuteur null
				nomCompletAuteur = nomAuteur;
			} else {
				// nomAuteur et prenomAuteur non null
				nomCompletAuteur = nomAuteur + " " + prenomAuteur;
			}
		}
		setNomCompletAuteur(nomCompletAuteur);
	}

	@Override
	public String getNomAuteur() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_NOM_AUTEUR_QUESTION);
	}

	@Override
	public void setNomAuteur(String nomAuteur) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_NOM_AUTEUR_QUESTION, nomAuteur);
		setNomCompletAuteur(nomAuteur, getPrenomAuteur());
	}

	@Override
	public String getPrenomAuteur() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_PRENOM_AUTEUR_QUESTION);
	}

	@Override
	public void setPrenomAuteur(String prenomAuteur) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_PRENOM_AUTEUR_QUESTION,
				prenomAuteur);
		setNomCompletAuteur(getNomAuteur(), prenomAuteur);
	}

	@Override
	public String getCiviliteAuteur() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_CIVILITE_AUTEUR_QUESTION);
	}

	@Override
	public void setCiviliteAuteur(String civiliteAuteur) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_CIVILITE_AUTEUR_QUESTION,
				civiliteAuteur);
	}

	@Override
	public String getIdMandat() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ID_MANDAT_QUESTION);
	}

	@Override
	public void setIdMandat(String idMandat) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ID_MANDAT_QUESTION, idMandat);
	}

	@Override
	public List<String> getAssNatAnalyses() {

		return indexation.getAssNatAnalyses();
	}

	@Override
	public void setAssNatAnalyses(List<String> analyseAssNat) {
		indexation.setAssNatAnalyses(analyseAssNat);
	}

	@Override
	public List<String> getAssNatRubrique() {
		return indexation.getAssNatRubrique();
	}

	@Override
	public void setAssNatRubrique(List<String> rubriqueAssNat) {
		indexation.setAssNatRubrique(rubriqueAssNat);
	}

	@Override
	public List<String> getAssNatTeteAnalyse() {
		return indexation.getAssNatTeteAnalyse();
	}

	@Override
	public void setAssNatTeteAnalyse(List<String> teteAnalyseAssNat) {
		indexation.setAssNatTeteAnalyse(teteAnalyseAssNat);
	}

	@Override
	public String getSenatQuestionId() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_SENAT_ID_QUESTION);
	}

	@Override
	public void setSenatQuestionId(String senatQuestionId) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_SENAT_ID_QUESTION,
				senatQuestionId);
	}

	@Override
	public String getSenatQuestionTitre() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_SENAT_TITRE_QUESTION);
	}

	@Override
	public void setSenatQuestionTitre(String senatQuestionTitre) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_SENAT_TITRE_QUESTION,
				senatQuestionTitre);
	}

	@Override
	public List<String> getSenatQuestionThemes() {
		return indexation.getSenatQuestionThemes();
	}

	@Override
	public void setSenatQuestionThemes(List<String> senatQuestionTheme) {
		indexation.setSenatQuestionThemes(senatQuestionTheme);
	}

	@Override
	public List<String> getSenatQuestionRubrique() {
		return indexation.getSenatQuestionRubrique();
	}

	@Override
	public void setSenatQuestionRubrique(List<String> senatRubrique) {
		indexation.setSenatQuestionRubrique(senatRubrique);
	}

	@Override
	public Calendar getDateRenouvellementQuestion() {
		return getDateProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_DATE_RENOUVELLEMENT_QUESTION);
	}

	@Override
	public Calendar getDateSignalementQuestion() {
		return getDateProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_DATE_SIGNALEMENT_QUESTION);
	}

	private void setDateRenouvellementQuestion(Calendar dateRenouvellementQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_RENOUVELLEMENT_QUESTION,
				dateRenouvellementQuestion);
	}

	@Override
	public void setDateSignalementQuestion(Calendar dateSignalementQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_SIGNALEMENT_QUESTION,
				dateSignalementQuestion);
	}

	@Override
	public Calendar getDateRetraitQuestion() {
		return getDateProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_DATE_RETRAIT_QUESTION);
	}

	private void setDateRetraitQuestion(Calendar dateRetraitQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_RETRAIT_QUESTION,
				dateRetraitQuestion);
	}

	@Override
	public void setDateClotureQuestion(Calendar dateClotureQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_CLOTURE_QUESTION,
				dateClotureQuestion);
	}

	@Override
	public Calendar getDateRappelQuestion() {
		return getDateProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_RAPPEL_QUESTION);
	}

	@Override
	public void setDateRappelQuestion(Calendar dateRappelQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_RAPPEL_QUESTION,
				dateRappelQuestion);
	}

	@Override
	public List<Signalement> getSignalements() {
		return getSignalementsProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_SIGNALEMENTS);
	}

	@Override
	public void setSignalements(List<Signalement> signalements) {
		ArrayList<Map<String, Serializable>> listeSignalement = new ArrayList<Map<String, Serializable>>();
		this.signalements = signalements;

		for (Signalement signalement : signalements) {
			listeSignalement.add(signalement.getSignalementMap());
		}
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, "signalementsQuestion", listeSignalement);
	}

	@Override
	public List<Renouvellement> getRenouvellements() {
		return getRenouvellementsProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_RENOUVELLEMENTS);
	}

	@Override
	public List<QErratum> getErrata() {
		return getErrataProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ERRATA_PROPERTY);
	}

	private void setRenouvellements(List<Renouvellement> renouvellements) {
		List<Map<String, Serializable>> listeRenouvellement = new ArrayList<Map<String, Serializable>>();
		this.renouvellements = renouvellements;

		for (Renouvellement renouvellement : renouvellements) {
			listeRenouvellement.add(renouvellement.getRenouvellementMap());
		}
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_RENOUVELLEMENTS,
				listeRenouvellement);
	}

	@Override
	public List<String> getSenatQuestionRenvois() {
		return indexation.getSenatQuestionRenvois();
	}

	@Override
	public void setSenatQuestionRenvois(List<String> senatRenvois) {
		indexation.setSenatQuestionRenvois(senatRenvois);
	}

	@Override
	public List<String> getMotsClefMinistere() {
		return indexation.getMotsClefMinistere();
	}

	@Override
	public void setMotsClefMinistere(List<String> motClefsMinistere) {
		indexation.setMotsClefMinistere(motClefsMinistere);
	}

	// TODO Mettre dans indexation service en prenant le caseLinkId en argument
	@Override
	public List<String> getMotsClef() {
		List<String> resultList = new ArrayList<String>();
		resultList.addAll(getAssNatRubrique());
		resultList.addAll(getAssNatTeteAnalyse());
		resultList.addAll(getAssNatAnalyses());
		resultList.addAll(getSenatQuestionRenvois());
		resultList.addAll(getSenatQuestionThemes());
		resultList.addAll(getSenatQuestionRubrique());
		resultList.addAll(getMotsClefMinistere());
		return resultList;
	}

	@Override
	public Boolean isPublished() {
		String pageJo = getPageJO();
		return (pageJo != null && !pageJo.isEmpty());
	}

	@Override
	public String getCaracteristiquesQuestion() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_CARACTERISTIQUE_QUESTION);
	}

	@Override
	public void setCaracteristiquesQuestion(String caracteristiquesQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_CARACTERISTIQUE_QUESTION,
				caracteristiquesQuestion);
	}

	private void setSourceNumeroQuestion(String origineQuestion, Long numeroQuestion) {
		String idQuestion = origineQuestion + " " + numeroQuestion;
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_REPONSES_LINK_SOURCE_NUMERO_QUESTION_PROPERTY, idQuestion);
	}

	@Override
	public DocumentRef getDossierRef() {
		return getDocument().getParentRef();
	}

	@Override
	public void setHasReponseInitiee(Boolean hasReponseInitiee) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_HAS_REPONSE_INITIEE,
				hasReponseInitiee);
	}

	@Override
	public Boolean hasReponseInitiee() {
		return getBooleanProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_HAS_REPONSE_INITIEE);
	}

	@Override
	public String getSourceId() {
		return this.getDocument().getSessionId();
	}

	@Override
	public String getHashConnexiteTitre() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_HASH_TITRE);
	}

	@Override
	public void setHashConnexiteTitre(String hash) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_HASH_TITRE, hash);

	}

	@Override
	public String getHashConnexiteTexte() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_HASH_TEXTE);
	}

	@Override
	public void setHashConnexiteTexte(String hash) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_HASH_TEXTE, hash);

	}

	@Override
	public String getHashConnexiteSE() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_HASH_SENAT);
	}

	@Override
	public void setHashConnexiteSE(String hash) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_HASH_SENAT, hash);

	}

	@Override
	public String getHashConnexiteAN() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_HASH_AN);
	}

	@Override
	public void setHashConnexiteAN(String hash) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_HASH_AN, hash);
	}

	@Override
	public String getTexteJoint() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_TEXTE_JOINT);
	}

	@Override
	public void setTexteJoint(String typeQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_TEXTE_JOINT, typeQuestion);
	}

	private void setEtatRetire(Boolean etat) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_RETIRE, etat);
	}

	@Override
	public Boolean getEtatRetire() {
		return getBooleanProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_RETIRE);
	}

	private void setEtatNonRetire(Boolean etat) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_NON_RETIRE, etat);
	}

	@Override
	public Boolean getEtatNonRetire() {
		return getBooleanProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_NON_RETIRE);
	}

	@Override
	public void setIsReattribue(Boolean etat) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_REATTRIBUE, etat);
	}

	@Override
	public Boolean getIsReattribue() {
		return getBooleanProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_REATTRIBUE);
	}

	private void setEtatRenouvele(Boolean etat) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_RENOUVELE, etat);
	}

	@Override
	public Boolean getEtatRenouvele() {
		return getBooleanProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_RENOUVELE);
	}

	private void setEtatSignale(Boolean etat) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_SIGNALE, etat);
	}

	@Override
	public Boolean getEtatSignale() {
		return getBooleanProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_SIGNALE);
	}

	private void setEtatRappele(Boolean etat) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_RAPPELE, etat);
	}

	@Override
	public Boolean getEtatRappele() {
		return getBooleanProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ETAT_RAPPELE);
	}

	@Override
	public void setCirconscriptionAuteur(String value) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_CIRCONSCRIPTION_AUTEUR_QUESTION, value);
	}

	@Override
	public String getCirconscriptionAuteur() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_CIRCONSCRIPTION_AUTEUR_QUESTION);
	}

	@Override
	public String getSourceNumeroQuestion() {
		return getOrigineQuestion() + " " + getNumeroQuestion();
	}

	@Override
	public String getMotsCles() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_REPONSES_MOTS_CLES_PROPERTY);
	}

	@Override
	public void setMotsCles(String motsCles) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_REPONSES_MOTS_CLES_PROPERTY,
				motsCles);
	}

	@Override
	public String getEtatsQuestion() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_REPONSES_ETATS_QUESTION_PROPERTY);
	}

	@Override
	public void setEtatsQuestion(String etatsQuestion) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_REPONSES_ETATS_QUESTION_PROPERTY, etatsQuestion);
	}

	@Override
	public Calendar getDateCaducite() {
		return getDateProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_CADUCITE);
	}

	private void setDateCaducite(Calendar dateCaducite) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_CADUCITE, dateCaducite);
	}

	@Override
	public Calendar getDateTransmissionAssemblees() {
		return getDateProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_DATE_TRANSMISSION_ASSEMBLEES);
	}

	@Override
	public void setDateTransmissionAssemblees(Calendar dateTransmissionAssemblees) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_DATE_TRANSMISSION_ASSEMBLEES,
				dateTransmissionAssemblees);
	}

	@Override
	public List<String> getIndexationComplAssNatAnalyses() {
		return indexationCompl.getAssNatAnalyses();
	}

	@Override
	public void setIndexationComplAssNatAnalyses(List<String> analyseAssNat) {
		indexationCompl.setAssNatAnalyses(analyseAssNat);
	}

	@Override
	public List<String> getIndexationComplAssNatRubrique() {
		return indexationCompl.getAssNatRubrique();
	}

	@Override
	public void setIndexationComplAssNatRubrique(List<String> rubriqueAssNat) {
		indexationCompl.setAssNatRubrique(rubriqueAssNat);
	}

	@Override
	public List<String> getIndexationComplAssNatTeteAnalyse() {
		return indexationCompl.getAssNatTeteAnalyse();
	}

	@Override
	public void setIndexationComplAssNatTeteAnalyse(List<String> teteAnalyseAssNat) {
		indexationCompl.setAssNatTeteAnalyse(teteAnalyseAssNat);
	}

	@Override
	public List<String> getIndexationComplSenatQuestionThemes() {
		return indexationCompl.getSenatQuestionThemes();
	}

	@Override
	public void setIndexationComplSenatQuestionThemes(List<String> senatQuestionTheme) {
		indexationCompl.setSenatQuestionThemes(senatQuestionTheme);
	}

	@Override
	public List<String> getIndexationComplSenatQuestionRubrique() {
		return indexationCompl.getSenatQuestionRubrique();
	}

	@Override
	public void setIndexationComplSenatQuestionRubrique(List<String> senatRubrique) {
		indexationCompl.setSenatQuestionRubrique(senatRubrique);
	}

	@Override
	public List<String> getIndexationComplSenatQuestionRenvois() {
		return indexationCompl.getSenatQuestionRenvois();
	}

	@Override
	public void setIndexationComplSenatQuestionRenvois(List<String> senatRenvois) {
		indexationCompl.setSenatQuestionRenvois(senatRenvois);
	}

	@Override
	public List<String> getIndexationComplMotsClefMinistere() {
		return indexationCompl.getMotsClefMinistere();
	}

	@Override
	public void setIndexationComplMotsClefMinistere(List<String> motClefsMinistere) {
		indexationCompl.setMotsClefMinistere(motClefsMinistere);
	}

	@Override
	public Boolean isRepondue() {
		return VocabularyConstants.ETAT_QUESTION_REPONDU.equals(getEtatQuestionSimple());
	}

	@Override
	public Boolean isRetiree() {
		return getDateRetraitQuestion() != null || getEtatRetire();
	}

	@Override
	public Boolean isRenouvelle() {
		if (renouvellements == null) {
			renouvellements = getRenouvellements();
		}
		return renouvellements != null && !renouvellements.isEmpty();
	}

	@Override
	public Boolean isSignale() {
		if (signalements == null) {
			signalements = getSignalements();
		}
		return signalements != null && !signalements.isEmpty();
	}

	@Override
	public Boolean isQuestionTypeEcrite() {
		String type = getTypeQuestion();
		return VocabularyConstants.QUESTION_TYPE_QE.equals(type);
	}

	@Override
	public Boolean hasOrigineAN() {
		return VocabularyConstants.QUESTION_ORIGINE_AN.equals(getOrigineQuestion());
	}

	@Override
	public Boolean hasIndexationAn() {

		return !getAssNatAnalyses().isEmpty() || !getAssNatRubrique().isEmpty() || !getAssNatTeteAnalyse().isEmpty();
	}

	@Override
	public Boolean hasIndexationSenat() {
		return !getSenatQuestionRenvois().isEmpty() || !getSenatQuestionThemes().isEmpty()
				|| !getSenatQuestionRubrique().isEmpty();
	}

	@Override
	public Boolean hasIndexationComplementaireAn() {
		return !getIndexationComplAssNatAnalyses().isEmpty() || !getIndexationComplAssNatRubrique().isEmpty()
				|| !getIndexationComplAssNatTeteAnalyse().isEmpty();
	}

	/**
	 * Retourne vrai si la question a des données d'indexation complementaire SENAT
	 */
	@Override
	public Boolean hasIndexationComplementaireSenat() {
		return !getIndexationComplSenatQuestionRubrique().isEmpty()
				|| !getIndexationComplSenatQuestionThemes().isEmpty()
				|| !getIndexationComplSenatQuestionRenvois().isEmpty();
	}

	@Override
	public Boolean hasIndexationComplementaireMotCleMinistere() {
		return !getIndexationComplMotsClefMinistere().isEmpty();
	}

	@Override
	public Dossier getDossier(CoreSession session) throws ClientException {
		return session.getDocument(getDossierRef()).getAdapter(Dossier.class);
	}

	@Override
	public String getIdMinistereRattachement() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION);
	}

	@Override
	public void setIdMinistereRattachement(String idMinistereRattachement) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION, idMinistereRattachement);
	}

	@Override
	public String getIntituleMinistereRattachement() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_INTITULE_DIRECTION_PILOTE_QUESTION);
	}

	@Override
	public void setIntituleMinistereRattachement(String intituleMinistereRattachement) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_INTITULE_MINISTERE_RATTACHEMENT_QUESTION, intituleMinistereRattachement);
	}

	@Override
	public String getIdDirectionPilote() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_ID_DIRECTION_PILOTE_QUESTION);
	}

	@Override
	public void setIdDirectionPilote(String idDirectionPilote) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ID_DIRECTION_PILOTE_QUESTION,
				idDirectionPilote);

	}

	@Override
	public String getIntituleDirectionPilote() {
		return getStringProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_INTITULE_DIRECTION_PILOTE_QUESTION);
	}

	@Override
	public void setIntituleDirectionPilote(String intituleDirectionPilote) {
		setProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				DossierConstants.DOSSIER_INTITULE_DIRECTION_PILOTE_QUESTION, intituleDirectionPilote);
	}

	@Override
	public boolean hasConnexite() {

		Long value = getLongProperty(DossierConstants.QUESTION_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_CONNEXITE);

		return value == null ? false : value > 1;
	}
}
