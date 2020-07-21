package fr.dila.reponses.web.connexe;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.Exception.AllotissementException;
import fr.dila.reponses.api.cases.Allotissement.TypeAllotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.api.service.QuestionConnexeService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.client.DossierConnexeDTO;
import fr.dila.reponses.web.client.DossierConnexeDTOImpl;
import fr.dila.reponses.web.contentview.ProviderBean;
import fr.dila.reponses.web.dossier.ReponseActionsBean;
import fr.dila.reponses.web.mailbox.ReponsesWebActions;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.dossier.DossierLockActionsBean;

/**
 * Une classe qui permet de gérer les actions relatives aux questions connexes.
 * 
 * @author asatre
 */
@Name("dossierConnexeActions")
@Scope(ScopeType.CONVERSATION)
public class DossierConnexeActionsBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long						serialVersionUID	= 1L;

	@In(create = true, required = true)
	protected transient CoreSession					documentManager;

	@In(create = true, required = false)
	protected transient NavigationContext			navigationContext;

	@In(create = true, required = false)
	protected transient FacesMessages				facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor			resourcesAccessor;

	@In(required = true, create = true)
	protected transient SSPrincipal					ssPrincipal;

	@In(create = true, required = false)
	protected transient DossierLockActionsBean		dossierLockActions;

	@In(create = true, required = false)
	protected transient DocumentsListsManagerBean	documentsListsManager;

	@In(create = true)
	protected transient ReponsesWebActions			webActions;

	@In(create = true, required = false)
	protected transient AllotissementActionsBean	allotissementsActions;

	@In(create = true)
	protected ContentViewActions					contentViewActions;

	private Map<String, List<String>>				map;

	private Dossier									currentDossier;

	private List<Object[]>							list;

	private String									currentIndex;

	private ArrayList<DossierConnexeDTO>			listResult;

	private String									currentQuestionId;

	private Reponse									currentReponse;

	private Boolean									oneDisabled;

	public void getMap() throws ClientException {
		DocumentModel doc = navigationContext.getCurrentDocument();
		map = new HashMap<String, List<String>>();
		if (doc != null) {
			Dossier dossier = doc.getAdapter(Dossier.class);
			currentDossier = dossier;
			Question question = dossier.getQuestion(documentManager);
			QuestionConnexeService questionConnexeService = ReponsesServiceLocator.getQuestionConnexeService();
			map = questionConnexeService.getMinisteresMap(question, documentManager);
		}
	}

	public List<Object[]> getMinisteres() throws ClientException {
		if (currentDossier == null
				|| !currentDossier.getDocument().getId().equals(navigationContext.getCurrentDocument().getId())) {
			getMap();
			list = new ArrayList<Object[]>();
			for (String ministere : map.keySet()) {
				list.add(new Object[] { ministere, String.valueOf(map.get(ministere).size()) });
			}

			Collections.sort(list, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] obj1, Object[] obj2) {
					return Integer.valueOf((String) obj2[1]).compareTo(Integer.valueOf((String) obj1[1]));
				}
			});

			currentIndex = null;
			currentQuestionId = null;
		}
		documentsListsManager.resetWorkingList("DOSSIER_CONNEXE_SELECTION");
		return list;
	}

	public void setQuestions(String index) throws ClientException {
		oneDisabled = false;
		if (index != null && !index.isEmpty()) {
			AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

			currentIndex = index;
			List<String> idsQuestion = map.get(index);

			listResult = new ArrayList<DossierConnexeDTO>();

			Boolean admin = false;

			List<PosteNode> listNode = STServiceLocator.getSTPostesService()
					.getPostesNodes(ssPrincipal.getPosteIdSet());

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

			for (String idQuestion : idsQuestion) {

				DocumentModel documentModel = documentManager.getDocument(new IdRef(idQuestion));
				if (documentModel != null) {

					Question question = documentModel.getAdapter(Question.class);
					Boolean disabledCheck = allotissementService.isAllotit(question, documentManager);
					if (!disabledCheck && !admin) {
						// si pas alloti on verifie que le dossierLink est dans la corbeille
						final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
						List<DocumentModel> dossierLinkList = corbeilleService.findUpdatableDossierLinkForDossier(
								documentManager, documentManager.getDocument(question.getDossierRef()));
						disabledCheck = dossierLinkList == null || dossierLinkList.isEmpty();
					}

					DossierConnexeDTO dossierConnexeDTO = new DossierConnexeDTOImpl();
					dossierConnexeDTO.mapFields(question, disabledCheck, documentManager);
					listResult.add(dossierConnexeDTO);
					if (disabledCheck) {
						oneDisabled = true;
					}
				}
			}

			contentViewActions.getContentView("dossier_connexe_content").getPageProvider().refresh();

		}
	}

	public List<DossierConnexeDTO> getListResult() {
		return listResult;
	}

	public Boolean isOneDisabled() {
		return oneDisabled;
	}

	public String getCurrentIndex() {
		return currentIndex;
	}

	public Boolean isCurrentIndex() {
		return currentDossier != null
				&& currentDossier.getDocument().getId().equals(navigationContext.getCurrentDocument().getId())
				&& currentIndex != null;
	}

	public void setQuestion(String uuid) {
		currentQuestionId = uuid;
	}

	public Object[] getQuestion() throws ClientException {
		if (currentQuestionId != null) {
			for (DossierConnexeDTO dossierConnexeDTO : listResult) {
				if (dossierConnexeDTO.getQuestionId().equals(currentQuestionId)) {
					QuestionConnexeService questionConnexeService = ReponsesServiceLocator.getQuestionConnexeService();
					Question question = documentManager.getDocument(new IdRef(currentQuestionId)).getAdapter(
							Question.class);
					currentReponse = questionConnexeService.getReponse(question, documentManager);
					return new Object[] { question.getOrigineQuestion() + " " + question.getNumeroQuestion(),
							question.getTexteQuestion(), currentReponse != null ? currentReponse.getTexteReponse() : "" };
				}
			}
		}
		return null;
	}

	public Boolean isCurrentQuestion() {
		return currentDossier != null
				&& currentDossier.getDocument().getId().equals(navigationContext.getCurrentDocument().getId())
				&& currentIndex != null && currentQuestionId != null;
	}

	public void retourListeQuestion() {
		currentQuestionId = null;
	}

	public void retourListeMinistere() {
		currentQuestionId = null;
		currentIndex = null;
	}

	public void setReponseQuestion() throws ClientException {
		if (currentReponse == null) {
			return;
		}

		DocumentModel doc = navigationContext.getCurrentDocument();
		Dossier dossier = doc.getAdapter(Dossier.class);

		Boolean lockable = dossierLockActions.getCanLockCurrentDossier();
		if (!lockable) {
			facesMessages.add(StatusMessage.Severity.WARN, "Impossible de vérouiller le dossier.");
			return;
		}

		try {
			dossierLockActions.lockCurrentDossier();
		} catch (ClientException e) {
			facesMessages.clear();
			facesMessages.add(StatusMessage.Severity.WARN, "Impossible de vérouiller le dossier.");
			return;
		}

		Reponse reponse = dossier.getReponse(documentManager);
		reponse.setTexteReponse(currentReponse.getTexteReponse());
		final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		reponseService.saveReponse(documentManager, reponse.getDocument(), doc);
		Events.instance().raiseEvent(ReponseActionsBean.REPONSE_UPDATED);
		currentQuestionId = null;
		currentIndex = null;
		webActions.setCurrentTabId(ReponsesWebActions.TAB_DOSSIER_PARAPHEUR);

		dossierLockActions.unlockCurrentDossier();

		// on enleve le message de lock/unlock du dossier
		facesMessages.clear();
		facesMessages.add(StatusMessage.Severity.INFO, "Réponse copiée.");

	}

	public void createLot() throws ClientException {
		DocumentModel currentDoc = navigationContext.getCurrentDocument();

		if (currentDoc != null) {
			AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

			currentDossier = currentDoc.getAdapter(Dossier.class);
			Question currentQuestion = currentDossier.getQuestion(documentManager);
			List<Question> listQuestions = new ArrayList<Question>();
			boolean hasError = false;

			List<DocumentModel> selectedQuestionsDoc = documentsListsManager
					.getWorkingList("DOSSIER_CONNEXE_SELECTION");

			for (DocumentModel selectedQuestionDoc : selectedQuestionsDoc) {
				Question selectedQuestion = selectedQuestionDoc.getAdapter(Question.class);
				Dossier selectedDossier = documentManager.getDocument(selectedQuestion.getDossierRef()).getAdapter(
						Dossier.class);

				if (!documentManager.hasPermission(currentQuestion.getDocument().getRef(), SecurityConstants.READ)) {
					continue;
				} else {
					if (allotissementService.isAllotit(selectedQuestion, documentManager)) {
						String labelFormat = resourcesAccessor.getMessages().get(
								"reponses.dossier.connexe.deja.allotit");
						facesMessages.add(
								StatusMessage.Severity.WARN,
								MessageFormat.format(labelFormat, currentQuestion.getOrigineQuestion() + " "
										+ currentQuestion.getNumeroQuestion()));
						hasError = true;
					} else if (!compareDossiersMinisteres(currentDossier, selectedDossier)) {
						String labelFormat = resourcesAccessor.getMessages().get(
								"reponses.dossier.connexe.ministere.different");
						facesMessages.add(
								StatusMessage.Severity.WARN,
								MessageFormat.format(labelFormat, currentQuestion.getOrigineQuestion() + " "
										+ currentQuestion.getNumeroQuestion()));
						hasError = true;
					} else {
						listQuestions.add(selectedQuestion);
					}
				}
			}

			if (!listQuestions.isEmpty() && !hasError) {
				try {
					allotissementService.createLot(currentQuestion, listQuestions, documentManager);
					documentsListsManager.resetWorkingList("DOSSIER_CONNEXE_SELECTION");
					allotissementsActions.resetCurrentDoc();
					Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);
				} catch (AllotissementException e) {
					facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
				} finally {
					currentQuestionId = null;
					currentIndex = null;
				}
			}
		}

	}

	public void updateLot() throws ClientException {
		DocumentModel doc = navigationContext.getCurrentDocument();
		if (doc != null) {
			AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

			currentDossier = doc.getAdapter(Dossier.class);
			Question currentQuestion = currentDossier.getQuestion(documentManager);
			boolean hasError = false;
			List<Question> listQuestions = new ArrayList<Question>();

			List<DocumentModel> selectedQuestionsDoc = documentsListsManager
					.getWorkingList("DOSSIER_CONNEXE_SELECTION");

			for (DocumentModel selectedQuestionDoc : selectedQuestionsDoc) {
				Question selectedQuestion = selectedQuestionDoc.getAdapter(Question.class);
				Dossier selectedDossier = documentManager.getDocument(selectedQuestion.getDossierRef()).getAdapter(
						Dossier.class);
				if (!documentManager.hasPermission(currentQuestion.getDocument().getRef(), SecurityConstants.READ)) {
					continue;
				} else {
					if (allotissementService.isAllotit(selectedQuestion, documentManager)) {
						String labelFormat = resourcesAccessor.getMessages().get(
								"reponses.dossier.connexe.deja.allotit");
						facesMessages.add(
								StatusMessage.Severity.WARN,
								MessageFormat.format(labelFormat, currentQuestion.getOrigineQuestion() + " "
										+ currentQuestion.getNumeroQuestion()));
						hasError = true;
					} else if (!compareDossiersMinisteres(currentDossier, selectedDossier)) {
						String labelFormat = resourcesAccessor.getMessages().get(
								"reponses.dossier.connexe.ministere.different");
						facesMessages.add(
								StatusMessage.Severity.WARN,
								MessageFormat.format(labelFormat, currentQuestion.getOrigineQuestion() + " "
										+ currentQuestion.getNumeroQuestion()));
						hasError = true;
					} else {
						listQuestions.add(selectedQuestion);
					}
				}
			}
			if (!listQuestions.isEmpty() && !hasError) {
				try {
					allotissementService.updateLot(currentQuestion, listQuestions, documentManager,
							TypeAllotissement.AJOUT);
					documentsListsManager.resetWorkingList("DOSSIER_CONNEXE_SELECTION");
					Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);
				} catch (AllotissementException e) {
					facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
				} finally {
					currentQuestionId = null;
					currentIndex = null;
				}
			}
		}
	}

	private boolean compareDossiersMinisteres(Dossier dossier1, Dossier dossier2) {
		return dossier1.getIdMinistereAttributaireCourant().equals(dossier2.getIdMinistereAttributaireCourant());
	}
}
