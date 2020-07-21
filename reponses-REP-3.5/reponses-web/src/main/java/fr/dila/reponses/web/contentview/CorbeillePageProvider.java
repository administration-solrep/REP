package fr.dila.reponses.web.contentview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.common.utils.i18n.I18NUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.query.api.PageSelection;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.client.ReponseDossierListingDTO;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.constant.ReponseDossierListingConstants;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.core.recherche.ReponseDossierListingDTOImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.helper.ReponsesProviderHelper;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.contentview.AbstractDTOPageProvider;
import fr.dila.st.web.contentview.HiddenColumnPageProvider;

public class CorbeillePageProvider extends AbstractDTOPageProvider implements HiddenColumnPageProvider {

	private static final String	TOTAL_COUNT_PROPERTY	= "totalCount";
	private static final String	USER_COLUMN_PROPERTY	= "userColumn";
	
	/**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CorbeillePageProvider.class);

	/**
     * 
     */
	private static final long	serialVersionUID		= 1L;

	/**
	 * recupere et converti la propriete totalCount donné a la content view Cette propiété contient le nombre de dossier
	 * total pour eviter de le recalculer
	 * 
	 * @return
	 */
	private long getTotalCountProperty() {
		Map<String, Serializable> props = getProperties();
		String resultCountStr = (String) props.get(TOTAL_COUNT_PROPERTY);
		return Long.parseLong(resultCountStr);
	}

	@Override
	protected void checkQueryCache() {
		super.checkQueryCache();
		if (currentItems != null) {
			// check if totalCount has changed
			if (resultsCount != getTotalCountProperty()) {
				refresh();
			}
		}
	}

	@Override
	protected void fillCurrentPageMapList(CoreSession coreSession) throws ClientException {
		currentItems = new ArrayList<Map<String, Serializable>>();

		resultsCount = getTotalCountProperty();

		// recupere la liste des ids triés
		// se baser sur ce tri pour retourner une liste de map triée dans le bon
		// ordre
		List<String> ids = QueryUtils.doQueryForIds(coreSession, query, getPageSize(), offset);

		if (!ids.isEmpty()) {

			List<DocumentModel> dml = QueryUtils.retrieveDocumentsUnrestricted(coreSession,
					STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, ids);

			final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
			final STLockService stLockService = STServiceLocator.getSTLockService();

			Map<String, ReponseDossierListingDTO> mapDossierIdDTO = new HashMap<String, ReponseDossierListingDTO>();
			Map<String, Set<String>> mapMinistereAttributaire = new HashMap<String, Set<String>>();

			// traitement case link, initialisation DTO

			for (String id : ids) {
				DocumentModel dm = retrieveDocument(dml, id);
				if (dm != null) {
					DossierLink dossierLink = dm.getAdapter(DossierLink.class);
					ReponseDossierListingDTO rrdto = new ReponseDossierListingDTOImpl();
					ReponsesProviderHelper.buildDTOFromDossierLink(rrdto, dossierLink);
					mapDossierIdDTO.put(dossierLink.getDossierId(), rrdto);
					currentItems.add(rrdto);
				}

			}

			// recup dossier en une fois
			dml = QueryUtils.retrieveDocumentsUnrestricted(coreSession, DossierConstants.DOSSIER_DOCUMENT_TYPE,
					mapDossierIdDTO.keySet());
			if(dml.isEmpty()) {
				LOGGER.warn(STLogEnumImpl.GET_DOSSIER_TEC, "Aucun dossier n'a pu être récupéré avec l'ID : " + mapDossierIdDTO.keySet().toString());
				return;
			}

			Set<String> lockedDossierIds = stLockService.extractLocked(coreSession, mapDossierIdDTO.keySet());

			Map<String, ReponseDossierListingDTO> mapQuestionIdDTO = new HashMap<String, ReponseDossierListingDTO>();
			Map<String, ReponseDossierListingDTO> mapReponseIdDTO = new HashMap<String, ReponseDossierListingDTO>();
			Set<String> lstDossiersDirecteurs = allotissementService.extractDossiersDirecteurs(dml, coreSession);
			for (DocumentModel dm : dml) {
				String dossierId = dm.getId();
				Dossier dossier = dm.getAdapter(Dossier.class);
				ReponseDossierListingDTO cbdto = mapDossierIdDTO.get(dossierId);
				
				ReponsesProviderHelper.buildDTOFromDossier(cbdto, dossier, lstDossiersDirecteurs, lockedDossierIds.contains(dossierId), false, coreSession);
				
				mapQuestionIdDTO.put(dossier.getQuestionId(), cbdto);
				mapReponseIdDTO.put(dossier.getReponseId(), cbdto);

				if (!isHiddenColumn(ProfilUtilisateurConstants.UserColumnEnum.MINISTERE_ATTRIBUTAIRE.name())) {
					if (mapMinistereAttributaire.get(dossier.getIdMinistereAttributaireCourant()) == null) {
						mapMinistereAttributaire
								.put(dossier.getIdMinistereAttributaireCourant(), new HashSet<String>());
					}

					mapMinistereAttributaire.get(dossier.getIdMinistereAttributaireCourant()).add(
							dossier.getQuestionId());
				}
			}

			// recup question en une fois
			dml = QueryUtils.retrieveDocumentsUnrestricted(coreSession, DossierConstants.QUESTION_DOCUMENT_TYPE,
					mapQuestionIdDTO.keySet());

			Integer reponseDureeTraitement = null;
			STParametreService paramService = STServiceLocator.getSTParametreService();
			try {
				reponseDureeTraitement = Integer.parseInt(paramService.getParametreValue(coreSession,
						STParametreConstant.QUESTION_DUREE_TRAITEMENT));
			} catch (ClientException e) {
				throw new ClientRuntimeException(
						"la durée de traitement de question définie dans les paramètres n'a pas pu être récupérée", e);
			}

			final Map<String, String> questionsIdsEtats = ReponsesProviderHelper.getQuestionsStatesFromIds(coreSession, mapQuestionIdDTO.keySet());
			final Set<String> questionsIdsWithSignalement = ReponsesProviderHelper.getIdsQuestionsSignalees(coreSession, mapQuestionIdDTO.keySet());
			
			for (DocumentModel dm : dml) {
				final String questionId = dm.getId();
				Question question = dm.getAdapter(Question.class);
				ReponseDossierListingDTO cbdto = mapQuestionIdDTO.get(questionId);
				ReponsesProviderHelper.buildDTOFromQuestion(cbdto, question, reponseDureeTraitement, 
						questionsIdsWithSignalement, null, null, questionsIdsEtats);
			}

			// recup reponse en une fois
			// Récupération des errata en une seule fois : on vérifie quelles reponses sont parentes d'un erratum#anonymousType dans hierarchy
			final Set<String> reponseHasErratumSet = ReponsesProviderHelper.getIdsQuestionsWithErratum(coreSession, mapReponseIdDTO.keySet());
			for (Entry<String, ReponseDossierListingDTO> entry : mapReponseIdDTO.entrySet()) {
				ReponseDossierListingDTO rrdto = entry.getValue();
				rrdto.setErrata(reponseHasErratumSet.contains(entry.getKey()) ? "Err" : null);
			}

			if (!mapMinistereAttributaire.keySet().isEmpty()) {
				// des ministeres attributaires sont à récupérer
				List<EntiteNode> listNode = STServiceLocator.getSTMinisteresService().getEntiteNodes(
						mapMinistereAttributaire.keySet());
				for (EntiteNode organigrammeNode : listNode) {
					Set<String> questionIds = mapMinistereAttributaire.get(organigrammeNode.getId());
					for (String questionId : questionIds) {
						ReponseDossierListingDTO cbdto = mapQuestionIdDTO.get(questionId);
						cbdto.setMinistereAttributaire(organigrammeNode.getLabel());
					}
				}
			}

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

	@Override
	public void setSearchDocumentModel(DocumentModel searchDocumentModel) {
		if (this.searchDocumentModel != searchDocumentModel) {
			this.searchDocumentModel = searchDocumentModel;
			refresh();
		}
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

	public List<Map<String, Serializable>> getAllItemsSelected() {
		List<Map<String, Serializable>> lstSelectedEntries = new ArrayList<Map<String, Serializable>>();
		String bundle = "messages";
		Locale locale = Locale.FRENCH;
		List<PageSelection<Map<String, Serializable>>> lstDocs = currentSelectPage.getEntries();

		for (PageSelection<Map<String, Serializable>> doc : lstDocs) {

			if (doc.isSelected()) {
				Map<String, Serializable> elem = doc.getData();

				// Conversion de la propriété en label
				String label = I18NUtils.getMessageString(bundle,
						(String) elem.get(ReponseDossierListingConstants.ROUTING_TASK_TYPE), null, locale);
				elem.put(ReponseDossierListingConstants.ROUTING_TASK_TYPE, label);
				String delai = (String) elem.get(ReponseDossierListingConstants.DELAI);

				if (delai.startsWith("label")) {

					label = I18NUtils.getMessageString(bundle, (String) elem.get(ReponseDossierListingConstants.DELAI),
							null, locale);
					elem.put(ReponseDossierListingConstants.DELAI, label);
				}

				lstSelectedEntries.add(elem);
			}
		}

		return lstSelectedEntries;
	}

}
