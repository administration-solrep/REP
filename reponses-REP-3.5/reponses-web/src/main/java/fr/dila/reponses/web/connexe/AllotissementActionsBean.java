package fr.dila.reponses.web.connexe;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.Exception.AllotissementException;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Allotissement.TypeAllotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.contentview.ProviderBean;
import fr.dila.reponses.web.context.NavigationContextBean;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.dossier.DossierListingActionsBean;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Une classe qui permet de gérer les actions relatives aux allotissements.
 * 
 * @author asatre
 */
@Name("allotissementsActions")
@Scope(ScopeType.CONVERSATION)
public class AllotissementActionsBean implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long						serialVersionUID	= -3222698155451067690L;

	private static final STLogger					LOGGER				= STLogFactory
																				.getLog(AllotissementActionsBean.class);

	@In(create = true, required = false)
	protected transient CoreSession					documentManager;

	@In(create = true, required = false)
	protected transient NavigationContextBean		navigationContext;

	@In(create = true, required = false)
	protected transient DossierListingActionsBean	dossierListingActions;

	@In(create = true, required = false)
	protected transient ContentViewActions			contentViewActions;

	@In(create = true, required = false)
	protected transient SSPrincipal					ssPrincipal;

	@In(create = true, required = false)
	protected transient DocumentsListsManagerBean	documentsListsManager;

	@In(create = true, required = false)
	protected transient CorbeilleActionsBean		corbeilleActions;

	private Boolean									isChecked;

	private Map<String, Boolean>					mapQuestionChecked;

	private Map<String, Question>					mapQuestion;

	protected String								questionSearch;

	protected String								questionSearchLot;

	@In(create = true, required = false)
	protected transient FacesMessages				facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor			resourcesAccessor;

	private Dossier									currentDoc;

	protected transient DocumentModelList			allotissementsSelected;

	/**
	 * Cette liste est triée selon l'origine et le numéro de la question. (i.e. le dossier directeur n'est pas
	 * forcément en première position).
	 */
	private ArrayList<Object[]>						listQuestions;

	private ArrayList<Object[]>						listQuestionsSearch;

	private ArrayList<Object[]>						listQuestionsErrorSearch;

	private ArrayList<Object[]>						listQuestionsAvecLotSearch;

	public String getQuestionSearch() {
		return questionSearch;
	}

	public void setQuestionSearch(String questionSearch) {
		this.questionSearch = questionSearch;
	}

	public String getQuestionSearchLot() {
		return questionSearchLot;
	}

	public void setQuestionSearchLot(String questionSearchLot) {
		this.questionSearchLot = questionSearchLot;
	}

	public boolean existLot() {
		DocumentModel doc = navigationContext.getCurrentDocument();
		Dossier dossier = doc.getAdapter(Dossier.class);
		return dossier.getDossierLot() != null && !dossier.getDossierLot().isEmpty();
	}

	public String getCurrentQuestion() {
		DocumentModel doc = navigationContext.getCurrentDocument();
		Dossier dossier = doc.getAdapter(Dossier.class);
		Question question = dossier.getQuestion(documentManager);

		return question.getOrigineQuestion() + " " + question.getNumeroQuestion();
	}

	public boolean isDossierDirecteur(String sourceNumeroQuestion) throws ClientException {
		DocumentModel doc = navigationContext.getCurrentDocument();
		Dossier dossier = doc.getAdapter(Dossier.class);
		AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
		Allotissement allotissement = allotissementService.getAllotissement(dossier.getDossierLot(), documentManager);
		if (allotissement != null && !allotissement.getIdDossiers().isEmpty()) {
			List<Question> questionList = allotissementService.getQuestionAllotiesWithOrder(documentManager,
					allotissement);
			if (!questionList.isEmpty()) {
				Question questionDirectrice = questionList.get(0);
				if (questionDirectrice.getSourceNumeroQuestion().equals(sourceNumeroQuestion)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Object[]> getListQuestionsAllotis() throws ClientException {
		DocumentModel doc = navigationContext.getCurrentDocument();
		Dossier dossier = doc.getAdapter(Dossier.class);
		if (currentDoc == null || !currentDoc.getDocument().getId().equals(doc.getId())) {

			currentDoc = dossier;
			AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
			Allotissement allotissement = allotissementService.getAllotissement(dossier.getDossierLot(),
					documentManager);
			listQuestions = new ArrayList<Object[]>();
			mapQuestionChecked = new HashMap<String, Boolean>();
			mapQuestion = new HashMap<String, Question>();

			if (allotissement != null && !allotissement.getIdDossiers().isEmpty()) {
				List<Question> questionList = allotissementService.getQuestionAllotiesWithOrderOrigineNumero(
						documentManager, allotissement);

				for (Question question : questionList) {

					StringBuilder motsClefsSb = new StringBuilder();
					final List<String> motClefs = question.getMotsClef();
					final int lastIndex = motClefs.size() - 1;
					int iindex = 0;
					for (String motClef : motClefs) {
						motsClefsSb.append(motClef);
						if (iindex != lastIndex) {
							motsClefsSb.append(", ");
						}
						iindex++;
					}

					listQuestions.add(new Object[] { question, question.getDocument().getId(),
							question.getSourceNumeroQuestion(), question.getNomCompletAuteur(),
							question.getEtatQuestion(documentManager).getNewState(), motsClefsSb.toString() });

					mapQuestionChecked.put(question.getDocument().getId(), false);
					mapQuestion.put(question.getDocument().getId(), question);

				}
			}
			isChecked = false;
		} else {
			resetField();
		}
		return listQuestions;
	}

	public Boolean isQuestionsAllotis() throws ClientException {
		DocumentModel doc = navigationContext.getCurrentDocument();
		if (currentDoc == null || !currentDoc.getDocument().getId().equals(doc.getId()) || listQuestions == null) {
			getListQuestionsAllotis();
		}
		return !listQuestions.isEmpty();
	}

	public void checkAllBox() {
		isChecked ^= true;
		for (String uuid : mapQuestionChecked.keySet()) {
			mapQuestionChecked.put(uuid, isChecked);
		}
	}

	public void checkBox(String uuid) {
		mapQuestionChecked.put(uuid, !mapQuestionChecked.get(uuid));
	}

	public boolean isCheckAll() {
		return isChecked;
	}

	public boolean isCheck(String uuid) {
		return mapQuestionChecked.get(uuid);
	}

	public boolean oneIsCheck() {
		if (mapQuestionChecked != null) {
			for (String uuid : mapQuestionChecked.keySet()) {
				if (mapQuestionChecked.get(uuid)) {
					return true;
				}
			}
		}
		return false;
	}

	public String removeFromLot() throws ClientException {
		DocumentModel doc = navigationContext.getCurrentDocument();
		if (doc != null) {
			Dossier dossier = doc.getAdapter(Dossier.class);
			Question q = dossier.getQuestion(documentManager);
			AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
			List<Question> listQuestions = new ArrayList<Question>();
			for (Entry<String, Boolean> entry : mapQuestionChecked.entrySet()) {
				if (entry.getValue().equals(Boolean.TRUE)) {
					Question question = mapQuestion.get(entry.getKey());
					if (!documentManager.hasPermission(question.getDocument().getRef(), SecurityConstants.READ)) {
						continue;
					} else {
						listQuestions.add(question);
					}
				}
			}
			if (!listQuestions.isEmpty()) {
				try {
					allotissementService.updateLot(q, listQuestions, documentManager, TypeAllotissement.SUPPR);
					Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);
					doc.refresh();
				} catch (AllotissementException e) {
					facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
				}
			}
		}
		currentDoc = null;
		return corbeilleActions.refreshDossierLinkSimple();

	}

	public void addToLot() throws ClientException {
		AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

		if (getQuestionSearch() == null || getQuestionSearch().trim().length() == 0) {
			String labelFormat = resourcesAccessor.getMessages().get("reponses.allotissement.question.search.vide");
			facesMessages.add(StatusMessage.Severity.WARN, MessageFormat.format(labelFormat, getQuestionSearch()));
		} else {
			RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
			Question question = null;
			try {
				question = rechercheService.searchQuestionBySourceNumero(documentManager, getQuestionSearch().trim());
			} catch (ClientException e) {
				facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
				return;
			}

			if (question == null) {
				String labelFormat = resourcesAccessor.getMessages().get("reponses.allotissement.recherche.vide");
				facesMessages.add(StatusMessage.Severity.WARN, MessageFormat.format(labelFormat, getQuestionSearch()));
			} else {
				if (!documentManager.hasPermission(question.getDocument().getRef(), SecurityConstants.READ)) {
					String labelFormat = resourcesAccessor.getMessages().get("reponses.dossier.connexe.droit");
					facesMessages.add(StatusMessage.Severity.WARN,
							MessageFormat.format(labelFormat, getQuestionSearch()));
					currentDoc = null;
					return;
				} else if (allotissementService.isAllotit(question, documentManager)) {
					String labelFormat = resourcesAccessor.getMessages().get("reponses.dossier.connexe.deja.allotit");
					facesMessages.add(StatusMessage.Severity.WARN,
							MessageFormat.format(labelFormat, getQuestionSearch()));
					currentDoc = null;
					return;
				}

			}

			if (question != null) {
				List<Question> listQuestions = Collections.singletonList(question);
				DocumentModel doc = navigationContext.getCurrentDocument();
				if (doc != null) {
					Dossier dossier = doc.getAdapter(Dossier.class);
					question = dossier.getQuestion(documentManager);
					try {
						allotissementService.updateLot(question, listQuestions, documentManager,
								TypeAllotissement.AJOUT);
						Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);
					} catch (AllotissementException e) {
						facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
					}
				}
			}
		}
		currentDoc = null;

	}

	public String navigate() {
		listQuestionsSearch = new ArrayList<Object[]>();
		listQuestionsErrorSearch = new ArrayList<Object[]>();
		listQuestionsAvecLotSearch = new ArrayList<Object[]>();
		currentDoc = null;
		String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();
		if (selectionListName != null) {
			try {
				allotissementsSelected = new DocumentModelListImpl(
						dossierListingActions.getDossiersFromSelection(selectionListName));
			} catch (ClientException e) {
				LOGGER.error(documentManager, ReponsesLogEnumImpl.FAIL_GET_ALLOT_TEC, e.getMessage());
				LOGGER.debug(documentManager, ReponsesLogEnumImpl.FAIL_GET_ALLOT_TEC, e.getMessage(), e);
				documentsListsManager.resetWorkingList(selectionListName);
				facesMessages.add(StatusMessage.Severity.WARN,
						"Erreur dans le traitement de l'opération : " + e.getMessage());
			}
		}
		return "view_dossier_allotissement_search";
	}

	public List<Object[]> getListQuestionsAllotisSearch() throws ClientException {
		listQuestionsSearch = new ArrayList<Object[]>();
		listQuestionsErrorSearch = new ArrayList<Object[]>();
		listQuestionsAvecLotSearch = new ArrayList<Object[]>();

		// Si la cv est une suivi_alert_content, il faut la changer pour être celle de résultat
		if (allotissementsSelected == null) {
			String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();
			allotissementsSelected = dossierListingActions.getDossiersFromSelection(selectionListName);
		}

		AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

		Boolean admin = false;

		List<PosteNode> listNode = STServiceLocator.getSTPostesService().getPostesNodes(ssPrincipal.getPosteIdSet());

		for (OrganigrammeNode posteNode : listNode) {
			if (posteNode instanceof PosteNode) {
				if (((PosteNode) posteNode).isChargeMissionSGG()) {
					admin = true;
					break;
				} else if (((PosteNode) posteNode).isPosteBdc()) {
					admin = true;
				}
			}
		}

		for (DocumentModel selectedDossierDoc : allotissementsSelected) {
			Dossier selectedDossier = selectedDossierDoc.getAdapter(Dossier.class);
			Question selectedQuestion = selectedDossier.getQuestion(documentManager);
			QuestionStateChange etatCourantQuestion = selectedQuestion.getEtatQuestion(documentManager);

			if (!documentManager.hasPermission(selectedQuestion.getDocument().getRef(), SecurityConstants.READ)) {
				String labelFormat = resourcesAccessor.getMessages().get("reponses.dossier.connexe.droit");
				facesMessages.add(
						StatusMessage.Severity.WARN,
						MessageFormat.format(labelFormat, selectedQuestion.getOrigineQuestion() + " "
								+ selectedQuestion.getNumeroQuestion()));
				continue;
			}
			if (!admin) {
				// si pas alloti on verifie que le dossierLink est dans la corbeille
				final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
				List<DocumentModel> dossierLinkList = corbeilleService.findUpdatableDossierLinkForDossier(
						documentManager, documentManager.getDocument(selectedQuestion.getDossierRef()));
				if (dossierLinkList == null || dossierLinkList.isEmpty()) {
					listQuestionsErrorSearch.add(new Object[] { selectedQuestion,
							selectedQuestion.getDocument().getId(),
							selectedQuestion.getOrigineQuestion() + " " + selectedQuestion.getNumeroQuestion(),
							selectedQuestion.getNomCompletAuteur(), etatCourantQuestion.getNewState(),
							selectedQuestion.getMotsCles(), "reponses.allotissement.liste.error.raison.corbeille" });
					continue;
				}
			}
			if (!etatCourantQuestion.getNewState().equals(VocabularyConstants.ETAT_QUESTION_EN_COURS)
					&& !etatCourantQuestion.getNewState().equals(VocabularyConstants.ETAT_QUESTION_RENOUVELEE)) {
				listQuestionsErrorSearch.add(new Object[] { selectedQuestion, selectedQuestion.getDocument().getId(),
						selectedQuestion.getOrigineQuestion() + " " + selectedQuestion.getNumeroQuestion(),
						selectedQuestion.getNomCompletAuteur(), etatCourantQuestion.getNewState(),
						selectedQuestion.getMotsCles(), "reponses.allotissement.liste.error.etat" });
				continue;
			}
			if (allotissementService.isAllotit(selectedQuestion, documentManager)) {
				listQuestionsAvecLotSearch.add(new Object[] { selectedQuestion, selectedQuestion.getDocument().getId(),
						selectedQuestion.getOrigineQuestion() + " " + selectedQuestion.getNumeroQuestion(),
						selectedQuestion.getNomCompletAuteur(), etatCourantQuestion.getNewState(),
						selectedQuestion.getMotsCles() });
				continue;
			}
			if (!selectedQuestion.isQuestionTypeEcrite()) {
				listQuestionsErrorSearch.add(new Object[] { selectedQuestion, selectedQuestion.getDocument().getId(),
						selectedQuestion.getOrigineQuestion() + " " + selectedQuestion.getNumeroQuestion(),
						selectedQuestion.getNomCompletAuteur(), etatCourantQuestion.getNewState(),
						selectedQuestion.getMotsCles(), "reponses.allotissement.liste.error.raison.qe" });
				continue;
			}

			listQuestionsSearch.add(new Object[] { selectedQuestion, selectedQuestion.getDocument().getId(),
					selectedQuestion.getOrigineQuestion() + " " + selectedQuestion.getNumeroQuestion(),
					selectedQuestion.getNomCompletAuteur(), etatCourantQuestion.getNewState(),
					selectedQuestion.getMotsCles() });
		}

		return listQuestionsSearch;
	}

	public List<Object[]> getListQuestionsErrorAllotisSearch() {
		return listQuestionsErrorSearch;
	}

	public List<Object[]> getListQuestionsAvecLotSearch() {
		return listQuestionsAvecLotSearch;
	}

	public String createLotSearch() throws ClientException {

		RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
		// recherche de la question directrice
		Question questionDirectrice = null;
		try {
			questionDirectrice = rechercheService.searchQuestionBySourceNumero(documentManager, getQuestionSearch()
					.trim());
		} catch (ClientException e) {
			facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
			return null;
		}
		if (questionDirectrice == null) {
			String labelFormat = resourcesAccessor.getMessages().get("reponses.allotissement.recherche.vide");
			facesMessages.add(StatusMessage.Severity.WARN, MessageFormat.format(labelFormat, getQuestionSearch()));
			return null;
		}

		DocumentRef dosierRef = questionDirectrice.getDossierRef();
		DocumentModel dossierModel = documentManager.getDocument(dosierRef);
		Boolean admin = false;

		List<PosteNode> listNode = STServiceLocator.getSTPostesService().getPostesNodes(ssPrincipal.getPosteIdSet());

		for (OrganigrammeNode posteNode : listNode) {
			if (posteNode instanceof PosteNode) {
				if (((PosteNode) posteNode).isChargeMissionSGG()) {
					admin = true;
					break;
				} else if (((PosteNode) posteNode).isPosteBdc()) {
					admin = true;
				}
			}
		}

		if (!documentManager.hasPermission(questionDirectrice.getDocument().getRef(), SecurityConstants.READ)) {
			String labelFormat = resourcesAccessor.getMessages().get("reponses.dossier.connexe.droit");
			facesMessages.add(
					StatusMessage.Severity.WARN,
					MessageFormat.format(labelFormat, questionDirectrice.getOrigineQuestion() + " "
							+ questionDirectrice.getNumeroQuestion()));
			return null;
		}
		if (!admin) {
			// si pas alloti on verifie que le dossierLink est dans la corbeille
			final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
			List<DocumentModel> dossierLinkList = corbeilleService.findUpdatableDossierLinkForDossier(documentManager,
					dossierModel);
			if (dossierLinkList == null || dossierLinkList.isEmpty()) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("reponses.allotissement.liste.error.raison.corbeille"));
				return null;
			}
		}
		QuestionStateChange etatQuestionDir = questionDirectrice.getEtatQuestion(documentManager);

		if (!etatQuestionDir.getNewState().equals(VocabularyConstants.ETAT_QUESTION_EN_COURS)
				&& !etatQuestionDir.getNewState().equals(VocabularyConstants.ETAT_QUESTION_RENOUVELEE)) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("reponses.allotissement.liste.error.etat") + " "
							+ questionDirectrice.getOrigineQuestion() + " " + questionDirectrice.getNumeroQuestion());
			return null;
		}
		if (!questionDirectrice.isQuestionTypeEcrite()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("reponses.allotissement.liste.error.raison.qe") + " "
							+ questionDirectrice.getOrigineQuestion() + " " + questionDirectrice.getNumeroQuestion());
			return null;
		}

		// ajout des questions selectionnés à la liste du lot
		Set<Question> listQuestion = new HashSet<Question>();

		for (Object[] object : listQuestionsSearch) {
			String idQuestion = (String) object[1];
			if (!idQuestion.equals(questionDirectrice.getDocument().getId())) {
				listQuestion.add((Question) object[0]);
			}
		}

		AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

		return makeLotSearch(questionDirectrice, listQuestion,
				allotissementService.isAllotit(questionDirectrice, documentManager));
	}

	private String makeLotSearch(Question questionDirectrice, Set<Question> listQuestion, Boolean modeUpdate)
			throws ClientException {
		AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

		if (listQuestion.isEmpty()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("reponses.allotissement.aucun.dossier"));
		} else {
			boolean hasError = false;
			// vérification des questions du lot
			DocumentModel dossierDoc = documentManager.getDocument(questionDirectrice.getDossierRef());
			for (Question question : listQuestion) {
				if (!documentManager.hasPermission(question.getDocument().getRef(), SecurityConstants.READ)) {
					String labelFormat = resourcesAccessor.getMessages().get("reponses.dossier.connexe.droit");
					facesMessages.add(
							StatusMessage.Severity.WARN,
							MessageFormat.format(labelFormat,
									question.getOrigineQuestion() + " " + question.getNumeroQuestion()));
					hasError = true;
				}
				DocumentModel dossierDoc2 = documentManager.getDocument(question.getDossierRef());
				if (!compareDossiersMinisteres(dossierDoc.getAdapter(Dossier.class),
						dossierDoc2.getAdapter(Dossier.class))) {
					String labelFormat = resourcesAccessor.getMessages().get(
							"reponses.dossier.connexe.ministere.different");
					facesMessages.add(
							StatusMessage.Severity.WARN,
							MessageFormat.format(labelFormat,
									question.getOrigineQuestion() + " " + question.getNumeroQuestion()));
					hasError = true;
				}
			}
			// vérification de la question directrice
			if (!documentManager.hasPermission(questionDirectrice.getDocument().getRef(), SecurityConstants.READ)) {
				String labelFormat = resourcesAccessor.getMessages().get("reponses.dossier.connexe.droit");
				facesMessages.add(
						StatusMessage.Severity.WARN,
						MessageFormat.format(labelFormat, questionDirectrice.getOrigineQuestion() + " "
								+ questionDirectrice.getNumeroQuestion()));
				hasError = true;
			} else if (allotissementService.isAllotit(questionDirectrice, documentManager) && !modeUpdate) {
				String labelFormat = resourcesAccessor.getMessages().get("reponses.dossier.connexe.deja.allotit");
				facesMessages.add(
						StatusMessage.Severity.WARN,
						MessageFormat.format(labelFormat, questionDirectrice.getOrigineQuestion() + " "
								+ questionDirectrice.getNumeroQuestion()));
				hasError = true;
			}

			if (!hasError) {
				// pas d'erreur, on créé le lot
				try {
					if (modeUpdate) {
						allotissementService.updateLot(questionDirectrice, new ArrayList<Question>(listQuestion),
								documentManager, TypeAllotissement.AJOUT);
					} else {
						allotissementService.createLot(questionDirectrice, new ArrayList<Question>(listQuestion),
								documentManager);
					}
					facesMessages.add(StatusMessage.Severity.INFO,
							resourcesAccessor.getMessages().get("reponses.allotissement.ok"));
					navigationContext.resetCurrentDocument();

					resetField();
					String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();
					documentsListsManager.resetWorkingList(selectionListName);

					Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);

					return corbeilleActions.getCurrentView();

				} catch (AllotissementException e) {
					facesMessages.add(StatusMessage.Severity.ERROR, e.getMessage());
				}
			}
		}
		return null;
	}

	private boolean compareDossiersMinisteres(Dossier dossier1, Dossier dossier2) {
		return dossier1.getIdMinistereAttributaireCourant().equals(dossier2.getIdMinistereAttributaireCourant());
	}

	private void resetField() {
		setQuestionSearch("");
		setQuestionSearchLot("");
	}

	public void resetCurrentDoc() {
		this.currentDoc = null;
	}

}
