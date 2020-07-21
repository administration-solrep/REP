package fr.dila.reponses.web.contentview;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.annotations.In;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.platform.query.api.PageSelection;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.client.ReponseDossierListingDTO;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponseDossierListingConstants;
import fr.dila.reponses.api.recherche.IdLabel;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.core.recherche.ReponseDossierListingDTOImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.helper.ReponsesProviderHelper;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.web.contentview.AbstractDTOPageProvider;
import fr.dila.st.web.contentview.HiddenColumnPageProvider;

/**
 * Fourni le DTO des dossiers retournés par la recherche.
 * 
 * Il prend en entrée une requête FNXQL retournant des questions.
 * 
 * Il modifie la requête pour rajouter les champ de tri sur la question dans le select dans le cas de la présence d'un
 * 'distinct' dans la requête, afin d'éviter une erreur sql.
 * 
 * @author spesnel
 * 
 */
public class RechercheResultPageProvider extends AbstractDTOPageProvider implements HiddenColumnPageProvider {

	/**
	 * 
	 */
	private static final long		serialVersionUID			= 8288353350339233296L;
	public static final String		RESOURCES_ACCESSOR_PROPERTY	= "resourcesAccessor";
	private static final String		USER_COLUMN_PROPERTY		= "userColumn";
	@In(required = true, create = true)
	protected transient SSPrincipal	ssPrincipal;

	/**
	 * unrestricted access used to read data from dossier link In case the user have not the read right on it can
	 * happened when the dossier link are get in unrestricted mode
	 * 
	 * @author spesnel
	 * 
	 */
	private static class UpdateDTOWithUnrestrictedDossierLinkData extends UnrestrictedSessionRunner {

		private Map<String, List<DocumentModel>>		mapDTODossierLinks;
		private Map<String, ReponseDossierListingDTO>	mapQuestionIdDTO;

		private ResourcesAccessor						resourcesAccessor;

		public UpdateDTOWithUnrestrictedDossierLinkData(CoreSession session, ResourcesAccessor resourcesAccessor,
				Map<String, List<DocumentModel>> mapDTODossierLinks,
				Map<String, ReponseDossierListingDTO> mapQuestionIdDTO) {
			super(session);
			this.mapDTODossierLinks = mapDTODossierLinks;
			this.resourcesAccessor = resourcesAccessor;
			this.mapQuestionIdDTO = mapQuestionIdDTO;
		}

		@Override
		public void run() throws ClientException {

			for (String idQuestion : mapDTODossierLinks.keySet()) {
				List<DocumentModel> dossierLinks = mapDTODossierLinks.get(idQuestion);

				IdLabel[] idlabels = new IdLabel[dossierLinks.size()];
				String[] routingTasks = new String[dossierLinks.size()];
				int i = 0;
				for (DocumentModel dossierLinkDoc : dossierLinks) {
					DossierLink dl = dossierLinkDoc.getAdapter(DossierLink.class);
					String label = buildDossierLinkLabel(resourcesAccessor, dl.getRoutingTaskLabel(),
							dl.getRoutingTaskMailboxLabel());
					idlabels[i] = new IdLabel(dossierLinkDoc.getId(), label);
					routingTasks[i] = dl.getRoutingTaskLabel();
					i++;
				}

				ReponseDossierListingDTO rrdto = mapQuestionIdDTO.get(idQuestion);
				rrdto.setRoutingTaskType(StringUtil.join(routingTasks, ",", ""));

				rrdto.setCaseLinkIdsLabels(idlabels);
			}
		}

		private String buildDossierLinkLabel(ResourcesAccessor resourcesAccessor, String routingTaskLabel,
				String routingTaskMailboxLabel) {
			String labelFormat = "reponses.dossier.liste.etape.label";
			if (resourcesAccessor == null) {
				return labelFormat + "[" + routingTaskLabel + "][" + routingTaskMailboxLabel + "]";
			} else {
				String routingTaskLabelStr = resourcesAccessor.getMessages().get(routingTaskLabel);
				labelFormat = resourcesAccessor.getMessages().get(labelFormat);
				return MessageFormat.format(labelFormat, routingTaskLabelStr, routingTaskMailboxLabel);
			}
		}
	}

	private ResourcesAccessor retrievePropertyResourcesAccessor() {
		Map<String, Serializable> props = getProperties();
		return (ResourcesAccessor) props.get(RESOURCES_ACCESSOR_PROPERTY);

	}

	@Override
	protected void fillCurrentPageMapList(CoreSession coreSession) throws ClientException {

		currentItems = new ArrayList<Map<String, Serializable>>();

		List<String> questionIds = null;
		resultsCount = QueryUtils.doCountQuery(coreSession, query);
		if (resultsCount > 0) {
			questionIds = QueryUtils.doQueryForIds(coreSession, query, getPageSize(), offset);
		}

		populateFromQuestionIds(coreSession, questionIds);
	}

	protected void populateFromQuestionIds(CoreSession coreSession, List<String> questionIds) throws ClientException {

		if (questionIds != null && !questionIds.isEmpty()) {

			ResourcesAccessor resourcesAccessor = retrievePropertyResourcesAccessor();

			final STParametreService paramService = STServiceLocator.getSTParametreService();
			final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
			// retrieve Document Question
			DocumentModelList dml = QueryUtils.retrieveDocuments(coreSession, DossierConstants.QUESTION_DOCUMENT_TYPE,
					questionIds);

			Map<String, ReponseDossierListingDTO> mapQuestionIdDTO = new HashMap<String, ReponseDossierListingDTO>();

			// traitement question, initialisation DTO
			Integer reponseDureeTraitement = null;
			try {
				reponseDureeTraitement = Integer.parseInt(paramService.getParametreValue(coreSession,
						STParametreConstant.QUESTION_DUREE_TRAITEMENT));
			} catch (ClientException e) {
				throw new ClientRuntimeException(
						"la durée de traitement de question définie dans les paramètres n'a pas pu être récupérée", e);
			}

			// Récupération des errata en une seule fois : on vérifie quelles questions sont parentes d'un erratum#anonymousType dans hierarchy
			final Set<String> questionsIdsWithErratum = ReponsesProviderHelper.getIdsQuestionsWithErratum(coreSession, questionIds);
			final Map<String, String> questionsIdsEtats = ReponsesProviderHelper.getQuestionsStatesFromIds(coreSession, questionIds);
			final Set<String> questionsIdsWithSignalement = ReponsesProviderHelper.getIdsQuestionsSignalees(coreSession, questionIds);
			final Set<String> questionsIdsWithRenouvellement = ReponsesProviderHelper.getIdsQuestionsRenouvelees(coreSession, questionIds);
			final List<String> dossiersDocsIds = new ArrayList<String>();
			
			for (String id : questionIds) {
				DocumentModel dm = retrieveDocument(dml, id);
				if (dm != null) {
					Question question = dm.getAdapter(Question.class);
					dossiersDocsIds.add((String) question.getDossierRef().reference());
					ReponseDossierListingDTO rrdto = new ReponseDossierListingDTOImpl();
					ReponsesProviderHelper.buildDTOFromQuestion(rrdto, question, reponseDureeTraitement, 
						questionsIdsWithSignalement, questionsIdsWithRenouvellement, questionsIdsWithErratum, questionsIdsEtats);
					mapQuestionIdDTO.put(id, rrdto);
					currentItems.add(rrdto);
				}
			}

			// recup dossier en une fois
			dml = QueryUtils.retrieveDocuments(coreSession, DossierConstants.DOSSIER_DOCUMENT_TYPE, dossiersDocsIds);
			final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

			Map<String, List<DocumentModel>> mapDTODossierLinks = new HashMap<String, List<DocumentModel>>();
			Map<String, ReponseDossierListingDTO> mapReponseIdDTO = new HashMap<String, ReponseDossierListingDTO>();
			Map<String, ReponseDossierListingDTO> mapDossierIdDTO = new HashMap<String, ReponseDossierListingDTO>();
			Set<String> dossiersDirecteurs = allotissementService.extractDossiersDirecteurs(dml, coreSession);
			Map<String, String> dossierIdQuestionIdMap = new HashMap<String, String>();

			// lock ?
			final STLockService stLockService = STServiceLocator.getSTLockService();
			Set<String> lockedDossierIds = stLockService.extractLocked(coreSession, mapDossierIdDTO.keySet());			
			for (DocumentModel dossierDoc : dml) {
				String dossierId = dossierDoc.getId();
				Dossier dossier = dossierDoc.getAdapter(Dossier.class);
				dossiersDocsIds.add(dossierId);
				String questionId = dossier.getQuestionId();

				ReponseDossierListingDTO rrdto = mapQuestionIdDTO.get(questionId);
				ReponsesProviderHelper.buildDTOFromDossier(rrdto, dossier, dossiersDirecteurs, lockedDossierIds.contains(dossierId), true, coreSession);
				
				mapReponseIdDTO.put(dossier.getReponseId(), rrdto);

				mapDossierIdDTO.put(dossierId, rrdto);
				dossierIdQuestionIdMap.put(dossierId, questionId);
			}
			
			// Recherche les DossierLink que l'utilisateur peut actionner
			// Récupération des dossiers links en une fois
			List<DocumentModel> dossiersLinks = corbeilleService.findUpdatableDossierLinkForDossiers(coreSession,
					dossiersDocsIds);
			for (DocumentModel dossierLinkDoc : dossiersLinks) {
				DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
				String dossierId = dossierLink.getDossierId();
				String questionId = dossierIdQuestionIdMap.get(dossierId);
				List<DocumentModel> dossiersLinksDocsList = mapDTODossierLinks.get(questionId);
				if (dossiersLinksDocsList == null) {
					dossiersLinksDocsList = new ArrayList<DocumentModel>();
				}
				dossiersLinksDocsList.add(dossierLinkDoc);
				mapDTODossierLinks.put(questionId, dossiersLinksDocsList);
			}
			
			// Récupération des errata en une seule fois : on vérifie quelles reponses sont parentes d'un erratum#anonymousType dans hierarchy
			final Set<String> reponseHasErratumSet = ReponsesProviderHelper.getIdsQuestionsWithErratum(coreSession, mapReponseIdDTO.keySet());
			
			for (Entry<String, ReponseDossierListingDTO> entry : mapReponseIdDTO.entrySet()) {
				ReponseDossierListingDTO rrdto = entry.getValue();
				rrdto.setErrata(reponseHasErratumSet.contains(entry.getKey()) ? "Err" : null);
			}

			// lecture et mise a jour du DTO avec les données des dossierLink accédés en unrestricted
			new UpdateDTOWithUnrestrictedDossierLinkData(coreSession, resourcesAccessor, mapDTODossierLinks,
					mapQuestionIdDTO).runUnrestricted();

		}

	}

	@Override
	public Boolean isHiddenColumn(String isHidden) throws ClientException {
		if (!StringUtils.isEmpty(isHidden)) {
			Map<String, Serializable> props = getProperties();
			@SuppressWarnings("unchecked")
			Set<String> list = new HashSet<String>((List<String>) props.get(USER_COLUMN_PROPERTY));
			if (list != null) {
				return !list.contains(isHidden);
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Retourne le document model associé a un id donné
	 */
	private DocumentModel retrieveDocument(List<DocumentModel> docList, String id) {
		for (DocumentModel doc : docList) {
			if (id.equals(doc.getId())) {
				return doc;
			}
		}
		return null;
	}

	@Override
	public void setSearchDocumentModel(DocumentModel searchDocumentModel) {
		if (this.searchDocumentModel != searchDocumentModel) {
			this.searchDocumentModel = searchDocumentModel;
			refresh();
		}
	}

	public List<Map<String, Serializable>> getAllItemsSelected() {
		List<Map<String, Serializable>> lstSelectedEntries = new ArrayList<Map<String, Serializable>>();
		List<PageSelection<Map<String, Serializable>>> lstDocs = currentSelectPage.getEntries();
		ResourcesAccessor resourcesAccessor = retrievePropertyResourcesAccessor();

		for (PageSelection<Map<String, Serializable>> doc : lstDocs) {

			if (doc.isSelected()) {
				Map<String, Serializable> elem = doc.getData();

				// Conversion de la propriété en label
				String label = resourcesAccessor.getMessages().get(
						elem.get(ReponseDossierListingConstants.ROUTING_TASK_TYPE));
				elem.put(ReponseDossierListingConstants.ROUTING_TASK_TYPE, label);
				String delai = (String) elem.get(ReponseDossierListingConstants.DELAI);

				if (delai.startsWith("label")) {

					label = resourcesAccessor.getMessages().get(elem.get(ReponseDossierListingConstants.DELAI));
					elem.put(ReponseDossierListingConstants.DELAI, label);
				}

				lstSelectedEntries.add(elem);
			}
		}

		return lstSelectedEntries;
	}

}
