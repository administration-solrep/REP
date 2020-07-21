package fr.dila.ss.web.feuilleroute;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.common.collections.ScopedMap;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;
import org.nuxeo.ecm.platform.types.Type;
import org.nuxeo.ecm.platform.ui.web.model.SelectDataModel;
import org.nuxeo.ecm.platform.ui.web.model.SelectDataModelRow;
import org.nuxeo.ecm.webapp.clipboard.ClipboardActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.EventNames;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteNotLockedException;
import fr.dila.ecm.platform.routing.web.DocumentRoutingSelectDataModelImpl;
import fr.dila.ecm.platform.routing.web.DocumentRoutingWebConstants;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.dto.EtapeFeuilleDeRouteDTO;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.dto.activitenormative.EtapeFeuilleDeRouteDTOImpl;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.exception.LocalizedClientException;
import fr.dila.st.api.feuilleroute.DocumentRouteTableElement;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.context.NavigationContextBean;
import fr.dila.st.web.corbeille.CorbeilleActionsBean;
import fr.dila.st.web.lock.STLockActionsBean;

/**
 * WebBean qui surcharge et étend celui de Nuxeo.
 * 
 * @author jtremeaux
 */
@Name("routingActions")
@Scope(CONVERSATION)
@Install(precedence = Install.FRAMEWORK + 1)
public class DocumentRoutingActionsBean extends fr.dila.ecm.platform.routing.web.DocumentRoutingActionsBean implements
		Serializable {
	/**
	 * Serial UID.
	 */
	private static final long							serialVersionUID	= 1L;

	/**
	 * Logger.
	 */
	private static final Log							LOG					= LogFactory
																					.getLog(DocumentRoutingActionsBean.class);

	/**
	 * Document virtuel, correspondant à la création de plusieurs contenueurs parallèle ayant chacun un conteneur série.
	 */
	public static final String							ROUTE_FORK_MASS_TYPE		= "route_fork_mass";
	
	/**
	 * Document virtuel, correspondant à la création de plusieurs conteneurs série.
	 */
	public static final String							ROUTE_ELEMENT_MASS_TYPE		= "route_element_mass";
	
	/**
	 * Ajout d'une étape avant l'étape sélectionnée.
	 */
	public static final String							STEP_ORDER_BEFORE	= "before";

	/**
	 * Ajout d'une étape à l'intérieur du conteneur sélectionné.
	 */
	public static final String							STEP_ORDER_IN		= "in";

	/**
	 * Document virtuel, correspondant à la création d'un conteneur parallèle et de plusieurs conteneurs série.
	 */
	public static final String							ROUTE_FORK_TYPE		= "route_fork";

	@In(create = true)
	protected STLockActionsBean							stLockActions;

	@In(required = false, create = true)
	protected transient DocumentsListsManager			documentsListsManager;

	@In(required = false, create = true)
	protected transient ClipboardActions				clipboardActions;

	@In(required = false, create = true)
	protected transient DocumentRoutingWebActionsBean	routingWebActions;

	@In(create = true, required = false)
	protected transient CorbeilleActionsBean			corbeilleActions;

	/**
	 * Tableau des étapes de feuilles de routes pour la sélection.
	 */
	protected SelectDataModel							selectDataModel;

	/**
	 * Tableau des étapes de l'instance de feuille de route liée.
	 */
	protected DocumentRoutingSelectDataModelImpl		relatedRouteElementsSelectModel;

	/**
	 * Nombre de branches à créer dans l'assistant de création de branches.
	 */
	protected Integer									branchCount;

	
	protected List<EtapeFeuilleDeRouteDTO>				lstEtapes;
	
	protected String 									serial;
	private DocumentModel								routeStepConteneur;
	
	/**
	 * Default constructor
	 */
	public DocumentRoutingActionsBean() {
		super();
	}

	@Override
	public STFeuilleRoute getRelatedRoute() {
		// Retourne le document chargé si c'est une feuille de route
		DocumentModel currentDocument = navigationContext.getCurrentDocument();
		if (currentDocument == null) {
			return null;
		}
		STFeuilleRoute relatedRoute = currentDocument.getAdapter(STFeuilleRoute.class);
		if (relatedRoute != null) {
			docWithAttachedRouteId = null;
			return relatedRoute;
		}

		// Si l'elément chargé est un élément de feuille de route, remonte à la route
		DocumentRouteElement relatedRouteElement = currentDocument.getAdapter(DocumentRouteElement.class);
		if (relatedRouteElement != null) {
			docWithAttachedRouteId = null;
			DocumentRoute documentRoute = relatedRouteElement.getDocumentRoute(documentManager);
			if (documentRoute == null) {
				return null;
			}
			return documentRoute.getDocument().getAdapter(STFeuilleRoute.class);
		}

		// Sinon, on doit être dans un document attaché à la route
		if (relatedRoutes.size() <= 0) {
			return null;
		}
		String relatedRouteModelDocumentId = relatedRoutes.get(0).getId();
		docWithAttachedRouteId = currentDocument.getId();
		try {
			DocumentModel routeDoc = documentManager.getDocument(new IdRef(relatedRouteModelDocumentId));
			return routeDoc.getAdapter(STFeuilleRoute.class);
		} catch (ClientException e) {
			LOG.error("Failed to get STFeuilleRoute object for doc id = " + relatedRouteModelDocumentId, e);
			return null;
		}
	}

	/**
	 * Retourne le Case courant attaché à la feuille de route, ou nul si on le document courant n'est pas un Case.
	 * 
	 * @return Document case
	 */
	public DocumentModel getDossierAttachedToCurrentRoute() {
		DocumentModel currentDocument = navigationContext.getCurrentDocument();
		if (!currentDocument.hasFacet(STConstant.ROUTABLE_FACET)) {
			return null;
		}
		return currentDocument;
	}

	/**
	 * Retourne vrai si l'étape est un conteneur.
	 * 
	 * @param stepDoc
	 *            Etape de feuille de route
	 * @return Vrai si l'étape est un conteneur
	 * @throws ClientException
	 */
	public boolean isRouteFolder(DocumentModel stepDoc) throws ClientException {
		return stepDoc.hasFacet("Folderish");
	}

	@Override
	public boolean isEditableStep(DocumentModel stepDoc) throws ClientException {
		// If fork, is not simple editable step
		if (isRouteFolder(stepDoc)) {
			return false;
		}

		return isEditableRouteElement(stepDoc);
	}

	@Override
	public boolean isEditableRouteElement(DocumentModel stepDoc) throws ClientException {
		// Vérifie si on est la dernière étape d'une instance terminée
		boolean isLastStepFromRouteInstanceDone = isLastStepDoneFromDoneRouteInstance(stepDoc);

		if (stepDoc.hasFacet("Folderish")) {
			DocumentRouteElement stepElement = stepDoc.getAdapter(DocumentRouteElement.class);
			return isLastStepFromRouteInstanceDone || stepElement.isModifiable();
		}
		final STRouteStep routeStep = stepDoc.getAdapter(STRouteStep.class);

		// Si l'étape est en état "running" ou "validated", aucune modification possible
		if (!(isLastStepFromRouteInstanceDone || routeStep.isModifiable())) {
			return false;
		}

		// À l'état brouillon et en demande de validation, seul l'administrateur ministériel a le droit de modifier
		final STFeuilleRoute route = getRelatedRoute();
		if (routeStep.isDraft()) {
			if (route.isDemandeValidation()) {
				return false;
			}
		}

		// Les modèles verrouillés par un autre utilisateur sont non modifiables
		final DocumentModel routeDoc = route.getDocument();
		if (!stLockActions.canUserLockDoc(routeDoc)) {
			return false;
		}

		// Sur les instances en cours, l'utilisateur doit avoir le DossierLink
		if (route.isRunning() && !corbeilleActions.isDossierLoadedInCorbeille()) {
			return false;
		}

		return true;
	}

	/**
	 * Détermine si une étape est sélectionnable (pour copier / coller / supprimer)...
	 * 
	 * @param routeElementDoc
	 * @return Vrai si l'étape est sélectionnable
	 * @throws ClientException
	 */
	public boolean isSelectionnableRouteElement(DocumentModel routeElementDoc) throws ClientException {
		// L'étape est sélectionnable si on a posé le verrou sur le dossier
		return isCurrentRouteLockedByCurrentUser();
	}

	/**
	 * Vérifie si on est la dernière étape terminée d'une instance terminée.
	 * 
	 * @param stepDoc
	 *            Étape de feuille de route
	 * @return Dernière étape
	 * @throws ClientException
	 */
	protected boolean isLastStepDoneFromDoneRouteInstance(DocumentModel stepDoc) throws ClientException {
		if (relatedRouteElementsSelectModel == null) {
			return false;
		}

		// Vérifie si le dossier est à l'état terminé
		DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		if (!dossierDoc.getCurrentLifeCycleState().equals(STDossier.DossierState.done.name())) {
			return false;
		}

		// Détermine la dernière étape à l'état terminé
		List<SelectDataModelRow> rows = relatedRouteElementsSelectModel.getRows();
		DocumentModel elementDoc = null;
		for (int i = rows.size() - 1; i >= 0; --i) {
			SelectDataModelRow row = rows.get(i);
			DocumentRouteTableElement element = (DocumentRouteTableElement) row.getData();
			elementDoc = element.getDocument();
			if (elementDoc.getCurrentLifeCycleState().equals(DocumentRouteElement.ElementLifeCycleState.done.name())) {
				// Remonte au RouteFolder racine, autorise l'ajout uniquement après lui
				if (element.getDepth() > 0) {
					DocumentModel parentDoc = null;
					while (!(parentDoc = documentManager.getDocument(elementDoc.getParentRef())).getType().equals(
							STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE)) {
						elementDoc = parentDoc;
					}
				}
				break;
			}
		}
		if (elementDoc == null || !elementDoc.getId().equals(stepDoc.getId())) {
			return false;
		}
		return true;
	}

	/**
	 * Détermine si l'instance de feuille de route peut être redémarrée.
	 * 
	 * @return Vrai si l'instance de feuille de route peut être redémarrée
	 * @throws ClientException
	 */
	public boolean isFeuilleRouteRestartable() throws ClientException {
		relatedRouteElementsSelectModel = computeSelectDataModelRelatedRouteElements();

		// La dernière étape ne doit pas être à l'état "done"
		List<SelectDataModelRow> rows = relatedRouteElementsSelectModel.getRows();
		if (rows.size() < 1) {
			return false;
		}
		SelectDataModelRow row = rows.get(rows.size() - 1);
		DocumentRouteTableElement element = (DocumentRouteTableElement) row.getData();
		DocumentModel routeStepDoc = element.getDocument();
		if (routeStepDoc.getCurrentLifeCycleState().equals(DocumentRouteElement.ElementLifeCycleState.done.name())) {
			return false;
		}

		// Aucune étape ne doit être à l'état running
		for (SelectDataModelRow r : rows) {
			DocumentRouteTableElement elt = (DocumentRouteTableElement) r.getData();
			DocumentModel rSDoc = elt.getDocument();
			if (rSDoc.getCurrentLifeCycleState().equals(DocumentRouteElement.ElementLifeCycleState.running.name())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Détermine si l'utilisateur a le droit de modifier l'étape vis-à-vis de son état obligatoire.
	 * 
	 * @param routeStepDoc
	 *            Etape de feuille de route
	 * @return Vrai si l'utilisateur a le droit de modifier l'étape
	 * @throws ClientException
	 */
	public boolean isEditableEtapeObligatoire(DocumentModel routeStepDoc) throws ClientException {
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		return documentRoutingService.isEtapeObligatoireUpdater(documentManager, routeStepDoc);
	}

	/**
	 * Détermine si les étapes peuvent être copiées dans la feuille de route.
	 * 
	 * @return Condition
	 * @throws ClientException
	 */
	public boolean isPastableRouteStepIntoFeuilleRoute() throws ClientException {
		STFeuilleRoute feuilleRoute = getRelatedRoute();
		if (feuilleRoute == null) {
			return false;
		}
		DocumentModel feuilleRouteDoc = feuilleRoute.getDocument();
		return clipboardActions.getCanPasteFromClipboardInside(feuilleRouteDoc);
	}

	/**
	 * Affiche une étape de feuille de route en lecture.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String viewStep() throws ClientException {
		if (StringUtils.isEmpty(stepId)) {
			return null;
		}
		DocumentRef stepRef = new IdRef(stepId);
		return navigationContext.navigateToDocument(documentManager.getDocument(stepRef), "view");
	}

	protected DocumentRef getRouteRefFromDocument(DocumentModel currentDocument) {
		DocumentRef routeRef = currentDocument.getRef();
		if (currentDocument.getType().equals(STConstant.DOSSIER_DOCUMENT_TYPE)) {
			STDossier dossier = currentDocument.getAdapter(STDossier.class);
			routeRef = new IdRef(dossier.getLastDocumentRoute());
		}
		return routeRef;
	}

	protected DocumentModel getSourceDocFromRef(DocumentRef sourceDocRef) throws ClientException {
		DocumentModel sourceDoc = documentManager.getDocument(sourceDocRef);
		if (sourceDoc.getType().equals(STConstant.DOSSIER_DOCUMENT_TYPE)) {
			STDossier dossier = sourceDoc.getAdapter(STDossier.class);
			sourceDoc = documentManager.getDocument(new IdRef(dossier.getLastDocumentRoute()));
		}
		return sourceDoc;
	}

	protected String getSourceDocName(DocumentModel sourceDoc, DocumentModel parentDoc) throws ClientException {
		String sourceDocName = null;
		if (STEP_ORDER_BEFORE.equals(hiddenDocOrder)) {
			sourceDocName = sourceDoc.getName();
		} else {
			DocumentModelList orderedChilds = getDocumentRoutingService().getOrderedRouteElement(parentDoc.getId(),
					documentManager);
			int selectedDocumentIndex = orderedChilds.indexOf(sourceDoc);
			int nextIndex = selectedDocumentIndex + 1;
			if (nextIndex >= orderedChilds.size()) {
				sourceDocName = null;
			} else {
				sourceDocName = orderedChilds.get(nextIndex).getName();
			}
		}
		return sourceDocName;
	}

	/**
	 * Redirige vers l'écran de création d'un élément de la feuille de route.
	 * 
	 * @param typeName
	 *            Type de document à créer
	 * @return Vue
	 * @throws ClientException
	 */
	@Override
	public String createRouteElement(String typeName) throws ClientException {
		DocumentModel currentDocument = navigationContext.getCurrentDocument();
		DocumentRef routeRef = getRouteRefFromDocument(currentDocument);
		DocumentRef sourceDocRef = new IdRef(hiddenSourceDocId);

		DocumentModel sourceDoc = getSourceDocFromRef(sourceDocRef);

		String sourceDocName = null;
		String parentPath = null;
		if (STEP_ORDER_IN.equals(hiddenDocOrder)) {
			parentPath = sourceDoc.getPathAsString();
		} else {
			DocumentModel parentDoc = documentManager.getParentDocument(sourceDocRef);
			parentPath = parentDoc.getPathAsString();
			sourceDocName = getSourceDocName(sourceDoc, parentDoc);
		}

		/* Creation en masse */
		if (ROUTE_ELEMENT_MASS_TYPE.equals(typeName)) {
			// Création en masse de branche en série
			this.serial = "true";
			return "create_route_element_mass";
		}
		
		// Pour le type virtuel "route_fork", crée un document StepFolder
		String typeToCreateName = typeName;
		if (ROUTE_FORK_TYPE.equals(typeName) || ROUTE_FORK_MASS_TYPE.equals(typeName)) {
			typeToCreateName = STConstant.STEP_FOLDER_DOCUMENT_TYPE;
		}

		Type docType = typeManager.getType(typeToCreateName);

		// we cannot use typesTool as intermediary since the DataModel callback
		// will alter whatever type we set
		typesTool.setSelectedType(docType);
		DocumentModel changeableDocument = documentManager.createDocumentModel(typeToCreateName);
		ScopedMap context = changeableDocument.getContextData();
		context.put(CoreEventConstants.PARENT_PATH, parentPath);
		context.put(SOURCE_DOC_NAME, sourceDocName);
		context.put(ROUTE_DOCUMENT_REF, routeRef);
		navigationContext.setChangeableDocument(changeableDocument);

		if (ROUTE_FORK_MASS_TYPE.equals(typeName)) {
			// Création en masse de branche parallele
			this.serial = "false";
			this.routeStepConteneur = changeableDocument;
			return "create_route_fork_mass";
		} else if (ROUTE_FORK_TYPE.equals(typeName)) {
			// Création d'un ensemble de branches (assistant)
			branchCount = 2;
			return "create_route_fork";
		} else {
			// Création d'un élément de feuille de route (écran standard)
			return "create_route_element";
		}
	}

	/**
	 * Methode utilisée pour create_route_element.xhtml
	 * 
	 * @return DocumentModel la nouvelle étape à créer
	 * @throws ClientException
	 */

	public DocumentModel newRouteStep() throws ClientException {
		DocumentModel currentDocument = navigationContext.getCurrentDocument();
		DocumentRef routeRef = getRouteRefFromDocument(currentDocument);
		DocumentRef sourceDocRef = new IdRef(hiddenSourceDocId);

		DocumentModel sourceDoc = getSourceDocFromRef(sourceDocRef);

		String sourceDocName = null;
		String parentPath = null;
		if (STEP_ORDER_IN.equals(hiddenDocOrder)) {
			parentPath = sourceDoc.getPathAsString();
		} else {
			DocumentModel parentDoc = documentManager.getParentDocument(sourceDocRef);
			parentPath = parentDoc.getPathAsString();
			sourceDocName = getSourceDocName(sourceDoc, parentDoc);
		}

		// Pour le type virtuel "route_fork", crée un document StepFolder
		String typeToCreateName = STConstant.ROUTE_STEP_DOCUMENT_TYPE;

		Type docType = typeManager.getType(typeToCreateName);

		// we cannot use typesTool as intermediary since the DataModel callback
		// will alter whatever type we set
		typesTool.setSelectedType(docType);
		DocumentModel changeableDocument = documentManager.createDocumentModel(typeToCreateName);
		ScopedMap context = changeableDocument.getContextData();
		context.put(CoreEventConstants.PARENT_PATH, parentPath);
		context.put(SOURCE_DOC_NAME, sourceDocName);
		context.put(ROUTE_DOCUMENT_REF, routeRef);
		navigationContext.setChangeableDocument(changeableDocument);

		return changeableDocument;
	}
	
	protected boolean checkCompatibilityBeforePaste(final List<DocumentModel> stepDocsToPaste) {
		return true;
	}

	/**
	 * Colle les étapes avant une étape.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String pasteBefore() throws ClientException {
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final JournalService journalService = STServiceLocator.getJournalService();
		// Récupère l'étape cible
		if (StringUtils.isEmpty(stepId)) {
			throw new ClientException("L'élément cible pour coller les documents n'est pas spécifié");
		}
		DocumentModel relativeDoc = documentManager.getDocument(new IdRef(stepId));
		if (relativeDoc == null) {
			final String message = resourcesAccessor.getMessages().get(
					"st.feuilleRoute.etape.action.paste.targetUnknown.error");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}
		DocumentModel targetDoc = documentManager.getDocument(relativeDoc.getParentRef());
		if (targetDoc == null) {
			throw new ClientException("Le document cible n'a pas de parent");
		}

		// Récupère les étapes à coller
		List<DocumentModel> docToPasteList = documentsListsManager.getWorkingList(DocumentsListsManager.CLIPBOARD);
		if (docToPasteList == null || docToPasteList.isEmpty()) {
			final String message = resourcesAccessor.getMessages().get(
					"st.feuilleRoute.etape.action.paste.emptySelection.error");
			facesMessages.add(StatusMessage.Severity.WARN, message);

			return null;
		}
		
		// Vérifie la compatibilité
		if (!checkCompatibilityBeforePaste(docToPasteList)) {
			final String message = resourcesAccessor.getMessages().get("st.feuilleRoute.etape.action.paste.compatibility.error");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}

		// Duplique les étapes sélectionnées dans le conteneur cible
		final DocumentModel documentRouteDoc = getRelatedRoute().getDocument();
		// final DocumentModel beforeDoc = documentRoutingService.getDocumentBefore(documentManager, targetDoc.getId(),
		// relativeDoc.getId());
		final DocumentModel beforeDoc = relativeDoc;
		List<DocumentModel> newDocs = documentRoutingService.pasteRouteStepIntoRoute(documentManager, documentRouteDoc,
				targetDoc, beforeDoc, true, docToPasteList);

		Object[] params = { newDocs.size() };
		facesMessages.add(StatusMessage.Severity.INFO, "#0 " + resourcesAccessor.getMessages().get("n_pasted_docs"),
				params);

		if (docWithAttachedRouteId != null) {
			DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
			// Journalise l'action de modification d'une instance de feuille de route
			journalService.journaliserActionFDR(documentManager, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
		} else {
			EventManager.raiseEventsOnDocumentSelected(targetDoc);
			Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, targetDoc);
		}

		// Retourne à la vue du dossier ou du modèle de feuille de route
		return backToModeleFeuilleRouteOrDossier(documentRouteDoc);
	}

	/**
	 * Colle les étapes après une étape.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String pasteAfter() throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		// Récupère l'étape cible
		if (StringUtils.isEmpty(stepId)) {
			throw new ClientException("L'élément cible pour coller les documents n'est pas spécifié");
		}
		DocumentModel relativeDoc = documentManager.getDocument(new IdRef(stepId));
		if (relativeDoc == null) {
			final String message = resourcesAccessor.getMessages().get(
					"st.feuilleRoute.etape.action.paste.targetUnknown.error");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}
		DocumentModel targetDoc = documentManager.getDocument(relativeDoc.getParentRef());
		if (targetDoc == null) {
			throw new ClientException("Le document cible n'a pas de parent");
		}

		// Récupère les étapes à coller
		List<DocumentModel> docToPasteList = documentsListsManager.getWorkingList(DocumentsListsManager.CLIPBOARD);
		if (docToPasteList == null || docToPasteList.isEmpty()) {
			final String message = resourcesAccessor.getMessages().get(
					"st.feuilleRoute.etape.action.paste.emptySelection.error");
			facesMessages.add(StatusMessage.Severity.WARN, message);

			return null;
		}
		
		// Vérifie la compatibilité
		if (!checkCompatibilityBeforePaste(docToPasteList)) {
			final String message = resourcesAccessor.getMessages().get("st.feuilleRoute.etape.action.paste.compatibility.error");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}

		// Duplique les étapes sélectionnées dans le conteneur cible
		final DocumentModel documentRouteDoc = getRelatedRoute().getDocument();
		List<DocumentModel> newDocs = documentRoutingService.pasteRouteStepIntoRoute(documentManager, documentRouteDoc,
				targetDoc, relativeDoc, false, docToPasteList);

		Object[] params = { newDocs.size() };
		facesMessages.add(StatusMessage.Severity.INFO, "#0 " + resourcesAccessor.getMessages().get("n_pasted_docs"),
				params);

		if (docWithAttachedRouteId != null) {
			DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
			// Journalise l'action de modification d'une instance de feuille de route
			journalService.journaliserActionFDR(documentManager, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
		} else {
			EventManager.raiseEventsOnDocumentSelected(targetDoc);
			Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, targetDoc);
		}

		// Retourne à la vue du dossier ou du modèle de feuille de route
		return backToModeleFeuilleRouteOrDossier(documentRouteDoc);
	}

	/**
	 * Colle les étapes dans un conteneur.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String pasteInto() throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		// Récupère l'étape cible
		if (StringUtils.isEmpty(stepId)) {
			throw new ClientException("L'élément cible pour coller les documents n'est pas spécifié");
		}
		DocumentModel targetDoc = documentManager.getDocument(new IdRef(stepId));
		if (targetDoc == null) {
			final String message = resourcesAccessor.getMessages().get(
					"st.feuilleRoute.etape.action.paste.targetUnknown.error");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}

		// Récupère les étapes à coller
		List<DocumentModel> docToPasteList = documentsListsManager.getWorkingList(DocumentsListsManager.CLIPBOARD);
		if (docToPasteList == null || docToPasteList.isEmpty()) {
			final String message = resourcesAccessor.getMessages().get(
					"st.feuilleRoute.etape.action.paste.emptySelection.error");
			facesMessages.add(StatusMessage.Severity.WARN, message);

			return null;
		}

		// Duplique les étapes sélectionnées dans le conteneur cible
		final DocumentModel documentRouteDoc = getRelatedRoute().getDocument();
		List<DocumentModel> newDocs = documentRoutingService.pasteRouteStepIntoRoute(documentManager, documentRouteDoc,
				targetDoc, null, false, docToPasteList);

		Object[] params = { newDocs.size() };
		facesMessages.add(StatusMessage.Severity.INFO, "#0 " + resourcesAccessor.getMessages().get("n_pasted_docs"),
				params);

		if (docWithAttachedRouteId != null) {
			DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
			// Journalise l'action de modification d'une instance de feuille de route
			journalService.journaliserActionFDR(documentManager, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
		} else {
			EventManager.raiseEventsOnDocumentSelected(targetDoc);
			Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, targetDoc);
		}

		// Retourne à la vue du dossier ou du modèle de feuille de route
		return backToModeleFeuilleRouteOrDossier(documentRouteDoc);
	}

	/**
	 * Surcharge de Nuxéo afin de retourner une liste dont on peut faire une sélection multiple.
	 * 
	 * @return Liste d'étapes
	 * @throws ClientException
	 */
	@Override
	@Factory(value = "routeElementsSelectModel", scope = EVENT)
	public SelectDataModel computeSelectDataModelRouteElements() throws ClientException {
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

		STFeuilleRoute currentRoute = getRelatedRoute();
		List<DocumentRouteTableElement> data = documentRoutingService.getFeuilleRouteElements(currentRoute,
				documentManager);

		selectDataModel = new DocumentRoutingSelectDataModelImpl("dm_route_elements", data, null);
		return selectDataModel;
	}

	/**
	 * Copie la sélection vers le presse-papier et vide la sélection.
	 */
	public void putSelectionInClipboardAndEmptySelection() {
		clipboardActions.putSelectionInClipboard();

		if (!documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION)) {
			documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION).clear();
		}
	}

	/**
	 * Sélectionne / désélectionne tous les éléments de la page. Handle complete table selection event after having
	 * ensured that the navigation context stills points to currentDocumentRef to protect against browsers' back button
	 * errors
	 * 
	 * @throws ClientException
	 *             if currentDocRef is not a valid document
	 */
	public void checkCurrentDocAndProcessSelectPage(String contentViewName, String listName, Boolean selection,
			String currentDocRef) throws ClientException {
		DocumentRef currentDocumentRef = new IdRef(currentDocRef);
		if (!currentDocumentRef.equals(navigationContext.getCurrentDocument().getRef())) {
			navigationContext.navigateToRef(currentDocumentRef);
		}
		processSelectPage(contentViewName, listName, selection);
	}

	/**
	 * L'application Réponses surcharge le comportement pour faire un switch contextuel
	 * entre les modèles relatedRouteElementsSelectModel et selectDataModel.
	 */
	protected List<SelectDataModelRow> getSelectableRows() {
		return selectDataModel.getRows();
	}

	public void processSelectPage(String contentViewName, String listName, Boolean selection) {
		List<SelectDataModelRow> rows = getSelectableRows();
		List<DocumentModel> docList = new ArrayList<DocumentModel>();
		if (rows != null) {
			for (SelectDataModelRow row : rows) {
				DocumentRouteTableElement docTableElement = (DocumentRouteTableElement) row.getData();
				DocumentModel doc = docTableElement.getDocument();
				docList.add(doc);
			}
			String lName = listName == null ? DocumentsListsManager.CURRENT_DOCUMENT_SELECTION : listName;
			if (Boolean.TRUE.equals(selection)) {
				documentsListsManager.addToWorkingList(lName, docList);
			} else {
				documentsListsManager.removeFromWorkingList(lName, docList);
			}
		}
	}

	/**
	 * Sélectionne / désélectionne un élément de la liste. Handle row selection event after having ensured that the
	 * navigation context stills points to currentDocumentRef to protect against browsers' back button errors
	 * 
	 * @throws ClientException
	 *             if currentDocRef is not a valid document
	 */
	public void checkCurrentDocAndProcessSelectRow(String docRef, String providerName, String listName,
			Boolean selection, String requestedCurrentDocRef) throws ClientException {
		DocumentRef requestedCurrentDocumentRef = new IdRef(requestedCurrentDocRef);
		DocumentRef currentDocumentRef = null;
		DocumentModel currentDocument = navigationContext.getCurrentDocument();
		if (currentDocument != null) {
			currentDocumentRef = currentDocument.getRef();
		}
		if (!requestedCurrentDocumentRef.equals(currentDocumentRef)) {
			navigationContext.navigateToRef(requestedCurrentDocumentRef);
		}
		processSelectRow(docRef, providerName, listName, selection);
	}

	public void processSelectRow(String docRef, String providerName, String listName, Boolean selection) {
		List<DocumentModel> docList = new ArrayList<DocumentModel>();
		List<SelectDataModelRow> rows = getSelectableRows();
		if (rows != null) {
			for (SelectDataModelRow row : rows) {
				DocumentRouteTableElement docTableElement = (DocumentRouteTableElement) row.getData();
				DocumentModel doc = docTableElement.getDocument();
				docList.add(doc);
			}
		}
		DocumentModel doc = null;
		if (docList != null) {
			for (DocumentModel pagedDoc : docList) {
				if (pagedDoc.getRef().toString().equals(docRef)) {
					doc = pagedDoc;
					break;
				}
			}
		}
		if (doc == null) {
			return;
		}
		String lName = listName == null ? DocumentsListsManager.CURRENT_DOCUMENT_SELECTION : listName;
		if (Boolean.TRUE.equals(selection)) {
			documentsListsManager.addToWorkingList(lName, doc);
		} else {
			documentsListsManager.removeFromWorkingList(lName, doc);
		}
	}

	/**
	 * Retourne l'étape cible dans la liste des étapes de la feuille de route (pour coller, supprimer, etc).
	 * 
	 * @return Étape cible
	 * @throws ClientException
	 */
	protected DocumentModel getTargetStep() throws ClientException {
		if (StringUtils.isEmpty(stepId)) {
			return null;
		}
		DocumentRef docRef = new IdRef(stepId);
		return documentManager.getDocument(docRef);
	}

	/**
	 * Sauvegarde une étape de feuille de route en cours d'édition.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	@Override
	public String saveRouteElement() throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		DocumentModel newDocument = navigationContext.getChangeableDocument();

		// Document has already been created if it has an id.
		// This will avoid creation of many documents if user hit create button
		// too many times.
		if (newDocument.getId() != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Document " + newDocument.getName() + " already created");
			}
			return navigationContext.navigateToDocument(newDocument, "after-create");
		}

		String parentDocumentPath = (String) newDocument.getContextData().get(CoreEventConstants.PARENT_PATH);
		String sourceDocumentName = (String) newDocument.getContextData().get(SOURCE_DOC_NAME);
		DocumentRef routeDocRef = (DocumentRef) newDocument.getContextData().get(ROUTE_DOCUMENT_REF);
		DocumentModel documentRouteDoc = documentManager.getDocument(routeDocRef);
		try {
			validateStepMailbox(newDocument);
			final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
			documentRoutingService.addDocumentRouteElementToRoute(documentManager, documentRouteDoc, new PathRef(
					parentDocumentPath), sourceDocumentName, newDocument);
		} catch (DocumentRouteNotLockedException e) {
			final String message = resourcesAccessor.getMessages().get(
					"feedback.casemanagement.document.route.not.locked");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}

		facesMessages.add(StatusMessage.Severity.INFO, "Etape enregistrée");

		navigationContext.setChangeableDocument(documentRouteDoc);

		// Retourne à la feuille de route
		if (docWithAttachedRouteId == null) {
			Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, documentRouteDoc);
			navigationContext.navigateToDocument(documentRouteDoc);
			return routingWebActions.getFeuilleRouteView();
		}

		DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
		// Journalise l'action de modification d'une instance de feuille de route
		if (STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(newDocument.getType())) {
			STRouteStep routeStep = newDocument.getAdapter(STRouteStep.class);
			journalService.journaliserActionEtapeFDR(documentManager, routeStep, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
		} else {
			journalService.journaliserActionFDR(documentManager, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
		}

		// Retourne au dossier
		return routingWebActions.getFeuilleRouteView();
	}

	/**
	 * Crée un conteneur parallèle et un ensemble de conteneurs séries.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String saveRouteFork() throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		DocumentModel parallelStepFolderDoc = navigationContext.getChangeableDocument();
		String parentDocumentPath = (String) parallelStepFolderDoc.getContextData().get(CoreEventConstants.PARENT_PATH);
		String sourceDocumentName = (String) parallelStepFolderDoc.getContextData().get(SOURCE_DOC_NAME);
		DocumentRef routeDocRef = (DocumentRef) parallelStepFolderDoc.getContextData().get(ROUTE_DOCUMENT_REF);
		DocumentModel documentRouteDoc = documentManager.getDocument(routeDocRef);
		try {
			final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
			// Crée le StepFolder parallèle
			StepFolder parallelStepFolder = parallelStepFolderDoc.getAdapter(StepFolder.class);
			parallelStepFolder.setExecution(STSchemaConstant.STEP_FOLDER_EXECUTION_PARALLEL_VALUE);
			parallelStepFolderDoc = documentRoutingService.addDocumentRouteElementToRoute(documentManager,
					documentRouteDoc, new PathRef(parentDocumentPath), sourceDocumentName, parallelStepFolderDoc);

			// Crée les StepFolder série
			for (int i = 0; i < branchCount; i++) {
				DocumentModel serialStepFolderDoc = documentManager
						.createDocumentModel(STConstant.STEP_FOLDER_DOCUMENT_TYPE);
				StepFolder serialStepFolder = serialStepFolderDoc.getAdapter(StepFolder.class);
				serialStepFolder.setExecution(STSchemaConstant.STEP_FOLDER_EXECUTION_SERIAL_VALUE);
				serialStepFolderDoc = documentRoutingService.addDocumentRouteElementToRoute(documentManager,
						documentRouteDoc, parallelStepFolderDoc.getRef(), null, serialStepFolderDoc);
			}
		} catch (DocumentRouteNotLockedException e) {
			final String message = resourcesAccessor.getMessages().get(
					"feedback.casemanagement.document.route.not.locked");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}
		String message = resourcesAccessor.getMessages().get("st.feuilleRoute.addStepPopup.action.create.success");
		facesMessages.add(StatusMessage.Severity.INFO, message);

		navigationContext.setChangeableDocument(documentRouteDoc);

		// Journalise l'action de modification d'une instance de feuille de route
		if (docWithAttachedRouteId != null) {
			DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
			journalService.journaliserActionFDR(documentManager, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
		}

		// Retourne à la vue du dossier ou du modèle de feuille de route
		return backToModeleFeuilleRouteOrDossier(documentRouteDoc);
	}

	/**
	 * Retourne à la vue du dossier ou du modèle de feuille de route.
	 * 
	 * @param feuilleRouteDoc
	 *            Documetn feuille de route
	 * @return Vue
	 * @throws ClientException
	 */
	protected String backToModeleFeuilleRouteOrDossier(DocumentModel feuilleRouteDoc) throws ClientException {
		// Retourne à la feuille de route
		if (docWithAttachedRouteId == null) {
			Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, feuilleRouteDoc);
			navigationContext.navigateToDocument(feuilleRouteDoc);
			return routingWebActions.getFeuilleRouteView();
		}

		// Retourne au dossier
		return backToCase();
	}

	/**
	 * Crée une nouvelle branche (ie. un conteneur série) dans un conteneur parallèle.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String createNewForkInParallelRouteStep() throws ClientException {
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final JournalService journalService = STServiceLocator.getJournalService();
		DocumentModel parallelStepFolderDoc = getTargetStep();
		if (parallelStepFolderDoc == null) {
			return null;
		}
		DocumentModel documentRouteDoc = getRelatedRoute().getDocument();
		try {
			// Crée le StepFolder série
			DocumentModel serialStepFolderDoc = documentManager
					.createDocumentModel(STConstant.STEP_FOLDER_DOCUMENT_TYPE);
			StepFolder serialStepFolder = serialStepFolderDoc.getAdapter(StepFolder.class);
			serialStepFolder.setExecution(STSchemaConstant.STEP_FOLDER_EXECUTION_SERIAL_VALUE);
			serialStepFolderDoc = documentRoutingService.addDocumentRouteElementToRoute(documentManager,
					documentRouteDoc, parallelStepFolderDoc.getRef(), null, serialStepFolderDoc);
		} catch (DocumentRouteNotLockedException e) {
			final String message = resourcesAccessor.getMessages().get(
					"feedback.casemanagement.document.route.not.locked");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}

		String message = resourcesAccessor.getMessages().get("st.feuilleRoute.etape.action.addBranch.success");
		facesMessages.add(StatusMessage.Severity.INFO, message);

		// Retourne à la feuille de route
		if (docWithAttachedRouteId == null) {
			Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, documentRouteDoc);
			navigationContext.navigateToDocument(documentRouteDoc);
			return routingWebActions.getFeuilleRouteView();
		}

		DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
		// Journalise l'action de modification d'une instance de feuille de route
		journalService.journaliserActionFDR(documentManager, dossierDoc,
				STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
		// Retourne au dossier
		return backToCase();
	}

	/**
	 * Permet de s'assurer que le champ distributionMailboxId contient bien le préfixe "poste-" dans le cas par exemple
	 * ou le poste est renseigné depuis le widget d'organigramme. De plus, la mailbox poste est créé si elle n'existe
	 * pas.
	 * 
	 * @param newDocument
	 *            STRouteStep
	 * @throws ClientException
	 */
	protected void validateStepMailbox(DocumentModel routeStepDoc) throws ClientException {
		if (!STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeStepDoc.getType())) {
			return;
		}

		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		STRouteStep routeStep = routeStepDoc.getAdapter(STRouteStep.class);
		if (routeStep != null) {
			String mailboxId = routeStep.getDistributionMailboxId();
			if (!mailboxId.contains(SSConstant.MAILBOX_POSTE_ID_PREFIX)) {
				mailboxId = mailboxPosteService.getPosteMailboxId(mailboxId);
				routeStep.setDistributionMailboxId(mailboxId);
			}
			String posteId = mailboxPosteService.getPosteIdFromMailboxId(mailboxId);
			mailboxPosteService.getOrCreateMailboxPoste(documentManager, posteId);
		}
	}

	/**
	 * Retourne au case lié à la feuille de route.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	protected String backToCase() throws ClientException {
		setRelatedRouteWhenNavigateBackToCase();
		final DocumentModel caseDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
		((NavigationContextBean) navigationContext).setCurrentTabAndNavigate(caseDoc,
				"TAB_CASE_MANAGEMENT_VIEW_RELATED_ROUTE");
		return routingWebActions.getFeuilleRouteView();
	}

	/**
	 * Surcharge de Nuxeo afin de renseigner correctement le changeableDocument lors du retour de la mise à jour d'un
	 * élément.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	@Override
	public String updateRouteElement() throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		boolean alreadyLockedByCurrentUser = false;
		DocumentModel changeableDocument = navigationContext.getChangeableDocument();
		DocumentRouteElement docRouteElement = changeableDocument.getAdapter(DocumentRouteElement.class);
		DocumentRoute route = docRouteElement.getDocumentRoute(documentManager);
		if (getDocumentRoutingService().isLockedByCurrentUser(route, documentManager)) {
			alreadyLockedByCurrentUser = true;
		} else {
			if (lockRoute(route) == null) {
				return null;
			}
		}
		try {
			validateStepMailbox(changeableDocument);
			getDocumentRoutingService().updateRouteElement(docRouteElement, documentManager);
		} catch (DocumentRouteNotLockedException e) {
			final String message = resourcesAccessor.getMessages().get(
					"feedback.casemanagement.document.route.already.locked");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}
		navigationContext.invalidateCurrentDocument();
		facesMessages.add(StatusMessage.Severity.INFO, "Etape modifiée");
		// Release the lock only when currentUser had locked it before entering this method.
		if (!alreadyLockedByCurrentUser) {
			getDocumentRoutingService().unlockDocumentRoute(route, documentManager);
		}
		if (docWithAttachedRouteId == null) {
			EventManager.raiseEventsOnDocumentChange(changeableDocument);
			final DocumentModel routeDoc = docRouteElement.getDocumentRoute(documentManager).getDocument();
			navigationContext.setChangeableDocument(routeDoc);
			navigationContext.navigateToDocument(routeDoc);
			return routingWebActions.getFeuilleRouteView();
		}

		// Journalise l'action de modification d'une instance de feuille de route
		DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
		if (STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(changeableDocument.getType())) {
			STRouteStep routeStep = changeableDocument.getAdapter(STRouteStep.class);
			journalService.journaliserActionEtapeFDR(documentManager, routeStep, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_UPDATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_UPDATE);
		} else {
			journalService.journaliserActionFDR(documentManager, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_UPDATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_UPDATE);
		}

		// Retourne au Case
		return backToCase();
	}

	@Override
	public String goBackToRoute() throws ClientException {
		DocumentModel changeableDocument = navigationContext.getChangeableDocument();
		DocumentRouteElement docRouteElement = changeableDocument.getAdapter(DocumentRouteElement.class);

		// Retourne à l'instance / au modèle de feuille de route
		if (docWithAttachedRouteId == null) {
			final DocumentModel routeDoc = docRouteElement.getDocumentRoute(documentManager).getDocument();
			navigationContext.navigateToDocument(routeDoc);
			return routingWebActions.getFeuilleRouteView();
		}

		// Retourne au Case
		return backToCase();
	}

	/**
	 * Retourne à la feuille de route lors de l'annulation de la création d'une étape / de branches.
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String goBackToRouteFromCreate() throws ClientException {
		// Retourne à l'instance / au modèle de feuille de route
		if (docWithAttachedRouteId == null) {
			navigationContext.setChangeableDocument(null);
			navigationContext.navigateToDocument(navigationContext.getCurrentDocument());
			return routingWebActions.getFeuilleRouteView();
		}

		// Retourne au Case
		return backToCase();
	}

	protected void setRelatedRouteWhenNavigateBackToCase() throws ClientException {
		// recompute factory
		webActions.resetTabList();
		navigationContext.setCurrentDocument(documentManager.getDocument(new IdRef(docWithAttachedRouteId)));
		relatedRoutes = relatedRouteAction.findRelatedRoute();
	}

	/**
	 * Calcule le tableau des étapes de l'instance de feuille de route liée au document courant.
	 * 
	 * @return Tableau des étapes de l'instance
	 * @throws ClientExce
	 *             ption
	 */
	@Factory(value = "relatedRouteElementsSelectModel", scope = EVENT)
	@Override
	public DocumentRoutingSelectDataModelImpl computeSelectDataModelRelatedRouteElements() throws ClientException {
		relatedRouteElementsSelectModel = new DocumentRoutingSelectDataModelImpl("related_route_elements",
				computeRelatedRouteElements(), null);
		return relatedRouteElementsSelectModel;
	}

	/**
	 * Calcule le tableau des étapes de l'instance de feuille de route liée au dossier courant.
	 * 
	 * @return Tableau des étapes de l'instance
	 * @throws ClientException
	 */
	@Factory(value = "dossierRelatedRouteElementsSelectModel", scope = EVENT)
	public DocumentRoutingSelectDataModelImpl computeDossierSelectDataModelRelatedRouteElements() throws ClientException {
		DocumentModel currentDocument = navigationContext.getCurrentDocument();
		final DossierDistributionService dossierDistributionService = SSServiceLocator.getDossierDistributionService();
		DocumentModel dossierRoute = dossierDistributionService.getLastDocumentRouteForDossier(documentManager,
				currentDocument);
		if (dossierRoute == null) {
			relatedRouteElementsSelectModel = new DocumentRoutingSelectDataModelImpl("related_route_elements",
					new ArrayList<DocumentRouteTableElement>(), null);
		} else {
			DocumentRoute currentRoute = dossierRoute.getAdapter(DocumentRoute.class);
			relatedRouteElementsSelectModel = new DocumentRoutingSelectDataModelImpl("related_route_elements",
					getElements(currentRoute), null);
		}
		return relatedRouteElementsSelectModel;
	}

	/**
	 * retourne les etapes associé a une feuille de route designé par son id
	 * 
	 * @param id
	 *            : id d'une feuille de route
	 * @return
	 * @throws ClientException
	 */
	public DocumentRoutingSelectDataModelImpl computeSelectDataModelRelatedRouteElements(String id) throws ClientException {
		return new DocumentRoutingSelectDataModelImpl("related_route_elements", computeRelatedRouteElements(id), null);
	}

	/**
	 * do the same thing as computeRelatedRouteElements() but takes the id in parameter instead of using the first id in
	 * relatedRoutes
	 * 
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	protected List<fr.dila.ecm.platform.routing.api.DocumentRouteTableElement> computeRelatedRouteElements(String id)
			throws ClientException {
		if (id == null || id.isEmpty()) {
			return new ArrayList<fr.dila.ecm.platform.routing.api.DocumentRouteTableElement>();
		}
		DocumentModel relatedRouteDocumentModel = documentManager.getDocument(new IdRef(id));
		DocumentRoute currentRoute = relatedRouteDocumentModel.getAdapter(DocumentRoute.class);

		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		return documentRoutingService.getRouteElements(currentRoute, documentManager);
	}

	/**
	 * retourne le document associe a l'id dans relatedRouteModelDocumentId
	 */
	public DocumentModel getRelatedRouteModelDocument() throws ClientException {
		return documentManager.getDocument(new IdRef(relatedRouteModelDocumentId));
	}

	/**
	 * Retourne vrai si l'utilisateur peut modifier l'instance de feuille de route.
	 * 
	 * @return Vrai si l'utilisateur peut modifier l'instance de feuille de route.
	 */
	public boolean isFeuilleRouteUpdatable() {
		DocumentRoute route = getRelatedRoute();
		if (route == null) {
			return false;
		}

		// Si la feuille de route est en cours, l'utilisateur doit avoir l'action sur le DossierLink
		if (route.isRunning() && corbeilleActions.isDossierLoadedInCorbeille()) {
			return true;
		}

		// Si la feuille de route est terminée, l'utilisateur doit avoir le droit de la redémarrer
		if (route.isDone() && currentUser.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_INSTANCE_RESTARTER)) {
			return true;
		}

		return false;
	}

	/**
	 * Supprime une étape de feuille de route.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	@Override
	public String removeStep() throws ClientException {
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final JournalService journalService = STServiceLocator.getJournalService();
		// Supprime l'étape de feuille de route
		boolean alreadyLockedByCurrentUser = false;
		DocumentRoute routeModel = getRelatedRoute();
		if (documentRoutingService.isLockedByCurrentUser(routeModel, documentManager)) {
			alreadyLockedByCurrentUser = true;
		} else {
			if (lockRoute(routeModel) == null) {
				return null;
			}
		}
		if (StringUtils.isEmpty(stepId)) {
			return null;
		}
		DocumentRef docRef = new IdRef(stepId);
		DocumentModel stepToDelete = documentManager.getDocument(docRef);
		try {
			documentRoutingService.removeRouteElement(stepToDelete.getAdapter(DocumentRouteElement.class),
					documentManager);
		} catch (LocalizedClientException e) {
			String message = resourcesAccessor.getMessages().get(e.getMessage());
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		} catch (DocumentRouteNotLockedException e) {
			final String message = resourcesAccessor.getMessages().get(
					"feedback.casemanagement.document.route.not.locked");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}
		Contexts.removeFromAllContexts("relatedRoutes");

		if (docWithAttachedRouteId == null) {
			// Mantis #0033527
			Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, routeModel.getDocument());
		}

		// Release the lock only when currentUser had locked it beforeentering this method.
		if (!alreadyLockedByCurrentUser) {
			documentRoutingService.unlockDocumentRoute(routeModel, documentManager);
		}

		// Récupère le dossier attaché si on est sur une instance de feuille de route
		DocumentModel dossierDoc = getDossierAttachedToCurrentRoute();
		if (dossierDoc != null) {
			journalService.journaliserActionFDR(documentManager, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_DELETE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_DELETE);
		}
		return null;
	}

	/**
	 * Déplace d'un cran une étape de feuille de route.
	 * 
	 * @param direction
	 *            Direction du déplacement
	 * @return Vue
	 * @throws ClientException
	 */
	@Override
	public String moveRouteElement(String direction) throws ClientException {
		if (StringUtils.isEmpty(stepId)) {
			return null;
		}

		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final JournalService journalService = STServiceLocator.getJournalService();
		try {
			boolean moveUp = DocumentRoutingWebConstants.MOVE_STEP_UP.equals(direction);
			documentRoutingService.moveRouteStep(documentManager, stepId, moveUp);
		} catch (LocalizedClientException e) {
			final String message = resourcesAccessor.getMessages().get(e.getMessage());
			facesMessages.add(StatusMessage.Severity.WARN, message);
		}

		// Retourne à l'instance / au modèle de feuille de route
		DocumentModel dossierDoc = getDossierAttachedToCurrentRoute();
		if (dossierDoc == null) {
			navigationContext.navigateToDocument(getRelatedRoute().getDocument());
			return routingWebActions.getFeuilleRouteView();
		}

		// Journalise l'action de déplacement de l'étape
		journalService.journaliserActionFDR(documentManager, dossierDoc, STEventConstant.EVENT_FEUILLE_ROUTE_STEP_MOVE,
				STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_MOVE);

		setRelatedRouteWhenNavigateBackToCase();
		webActions.setCurrentTabAndNavigate(documentManager.getDocument(new IdRef(docWithAttachedRouteId)),
				"TAB_CASE_MANAGEMENT_VIEW_RELATED_ROUTE");

		return backToCase();
	}

	public List<EtapeFeuilleDeRouteDTO> getLstEtapes() throws ClientException {
		if (lstEtapes == null) {
			lstEtapes = new ArrayList<EtapeFeuilleDeRouteDTO>();
			DocumentModel etapeDoc = this.newSimpleRouteStep();
			STRouteStep routeStepDesired = etapeDoc.getAdapter(STRouteStep.class);
			getLstEtapes().add(new EtapeFeuilleDeRouteDTOImpl(routeStepDesired));
		}
		return lstEtapes;
	}

	/**
	 * Remet la liste des étapes à créer à une étape non renseignée.
	 * @throws ClientException
	 */
	public void resetLstEtapes() throws ClientException {
		this.lstEtapes.clear();
		this.addEtapeFeuilleDeRoute();
	}
	
	public void setLstEtapes(List<EtapeFeuilleDeRouteDTO> lstEtapes) {
		this.lstEtapes = lstEtapes;
	}
	
	public int getlstEtapesSize() {
		if (this.lstEtapes==null) return 0;
		else  {
			return this.lstEtapes.size();
		}
	}
	
	public String addEtapeFeuilleDeRoute() throws ClientException {
		if (lstEtapes != null) {
			
			DocumentModel etapeDoc = this.newSimpleRouteStep();
			STRouteStep routeStepDesired = etapeDoc.getAdapter(STRouteStep.class);
			getLstEtapes().add(new EtapeFeuilleDeRouteDTOImpl(routeStepDesired));
		} else {
			facesMessages.add(StatusMessage.Severity.ERROR, "Erreur, liste des étapes non initialisée");
		}
		return null;
	}
	
	/**
	 * Creation simple d'une routeStep pour la création en masse
	 * 
	 * @return DocumentModel la nouvelle étape à créer
	 * @throws ClientException
	 */
	public DocumentModel newSimpleRouteStep() throws ClientException {
		DocumentRef sourceDocRef = new IdRef(hiddenSourceDocId);

		DocumentModel sourceDoc = getSourceDocFromRef(sourceDocRef);

		String parentPath = null;
		if (STEP_ORDER_IN.equals(hiddenDocOrder)) {
			parentPath = sourceDoc.getPathAsString();
		} else {
			DocumentModel parentDoc = documentManager.getParentDocument(sourceDocRef);
			parentPath = parentDoc.getPathAsString();
		}
		
		UUID newId = UUID.randomUUID();
		DocumentModel desiredDocument = documentManager.createDocumentModel(STConstant.ROUTE_STEP_DOCUMENT_TYPE);
		desiredDocument.setPathInfo(parentPath, newId.toString());
		
		return desiredDocument;
	}
	
	
	public void removeFromLstEtapeFeuilleDeRoute(EtapeFeuilleDeRouteDTO eFDR) throws ClientException {
			try {
				
				Iterator<EtapeFeuilleDeRouteDTO> itEtapes = this.lstEtapes.iterator();
				
				while (itEtapes.hasNext()) {
					EtapeFeuilleDeRouteDTO etape = itEtapes.next();
					
					if (etape == eFDR || etape.equals(eFDR)) {
						this.lstEtapes.remove(etape);
					}
				}
			} catch (Exception e1) {
				facesMessages.add(StatusMessage.Severity.ERROR, "Erreur");
			}
	}
	
	/**
	 * Retourne à la feuille de route lors de l'annulation de la création d'étapes en masse.
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String goBackToRouteFromCreateMass() throws ClientException {
		// Retourne à l'instance / au modèle de feuille de route
		if (this.lstEtapes != null) {
			this.resetLstEtapes();
			navigationContext.setChangeableDocument(null);
			navigationContext.navigateToDocument(navigationContext.getCurrentDocument());
			return routingWebActions.getFeuilleRouteView();
		}

		// Retourne au Case
		return backToCase();
	}
	
	/**
	 * Crée un DocumentModel de type étape de feuille de route (RouteStep) à partir d'un EtapeFeuilleDeRouteDTO
	 * @return
	 * @throws ClientException 
	 */
	protected DocumentModel createEtapeFromDTO(EtapeFeuilleDeRouteDTO eFDR) throws ClientException {
		
		DocumentModel currentDocument = navigationContext.getCurrentDocument();
		DocumentRef routeRef = getRouteRefFromDocument(currentDocument);
		DocumentRef sourceDocRef = new IdRef(hiddenSourceDocId);

		DocumentModel sourceDoc = getSourceDocFromRef(sourceDocRef);

		String sourceDocName = null;
		String parentPath = null;
		if (STEP_ORDER_IN.equals(hiddenDocOrder)) {
			parentPath = sourceDoc.getPathAsString();
		} else {
			DocumentModel parentDoc = documentManager.getParentDocument(sourceDocRef);
			parentPath = parentDoc.getPathAsString();
			sourceDocName = getSourceDocName(sourceDoc, parentDoc);
		}

		String typeToCreateName = STConstant.ROUTE_STEP_DOCUMENT_TYPE;

		Type docType = typeManager.getType(typeToCreateName);

		// we cannot use typesTool as intermediary since the DataModel callback
		// will alter whatever type we set
		typesTool.setSelectedType(docType);
		DocumentModel changeableDocument = documentManager.createDocumentModel(typeToCreateName);
		ScopedMap context = changeableDocument.getContextData();
		context.put(CoreEventConstants.PARENT_PATH, parentPath);
		context.put(SOURCE_DOC_NAME, sourceDocName);
		context.put(ROUTE_DOCUMENT_REF, routeRef);
		
		STRouteStep routeStepDesired = changeableDocument.getAdapter(STRouteStep.class);
		
		routeStepDesired.setType(eFDR.getTypeEtape());
		routeStepDesired.setDistributionMailboxId(eFDR.getDistributionMailboxId());
		routeStepDesired.setAutomaticValidation(eFDR.getAutomaticValidation()!=null?eFDR.getAutomaticValidation():false);
		routeStepDesired.setObligatoireSGG(eFDR.getObligatoireSGG()!=null?eFDR.getObligatoireSGG():false);
		routeStepDesired.setObligatoireMinistere(eFDR.getObligatoireMinistere()!=null?eFDR.getObligatoireMinistere():false);
		routeStepDesired.setDeadLine(Long.getLong(eFDR.getDeadLine()));
		
		return changeableDocument;
	}
	
	/**
	 * Vérifie les étapes entrées par l'utilisateur lors de la création d'étapes de feuille de route en masse.
	 * Remplit les erreurs facesMessages si besoin.
	 * @return true si les étapes sont ok, false sinon.
	 * @throws ClientException 
	 */
	protected boolean checkInputMass() throws ClientException {
		if (lstEtapes==null || lstEtapes.isEmpty()) {
			// l'utilisateur n'a remplit aucune ligne dans le tableau
			final String message = resourcesAccessor.getMessages().get(
					"label.epg.feuilleRoute.create.error.listEmpty");
			facesMessages.add(StatusMessage.Severity.ERROR, message);
			return false;
		}
		
		if (!this.isMailboxFilled(lstEtapes)) {
			// l'utilisateur n'a pas remplit toutes les mailbox
			final String message = resourcesAccessor.getMessages().get(
					"Le champ destinataire est obligatoire");
			facesMessages.add(StatusMessage.Severity.ERROR, message);
			return false;
		}
		return true;
	}
	
	/**
	 * Lors de la création d'étapes en masse, le destinataire est obligatoire.
	 */
	private boolean isMailboxFilled(List<EtapeFeuilleDeRouteDTO> listeEtapes) throws ClientException {
		for (EtapeFeuilleDeRouteDTO eFDR : listeEtapes) {
			if (eFDR.getDistributionMailboxId()==null || eFDR.getDistributionMailboxId().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Création des étapes de feuilles de routes en série en masse.
	 */
	public String saveRouteElementSerialMass(List<DocumentModel> etapesFDRLst) throws ClientException {
		
		final JournalService journalService = STServiceLocator.getJournalService();
		
		for (DocumentModel changeableDocument : etapesFDRLst) {
			
			DocumentModel newDocument = changeableDocument;
			
			String parentDocumentPath = (String) newDocument.getContextData().get(CoreEventConstants.PARENT_PATH);
			String sourceDocumentName = (String) newDocument.getContextData().get(SOURCE_DOC_NAME);
			DocumentRef routeDocRef = (DocumentRef) newDocument.getContextData().get(ROUTE_DOCUMENT_REF);
			DocumentModel documentRouteDoc = documentManager.getDocument(routeDocRef);
			try {
				validateStepMailbox(newDocument);
				final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
				documentRoutingService.addDocumentRouteElementToRoute(documentManager, documentRouteDoc, new PathRef(
						parentDocumentPath), sourceDocumentName, newDocument);
			} catch (DocumentRouteNotLockedException e) {
				final String message = resourcesAccessor.getMessages().get(
						"feedback.casemanagement.document.route.not.locked");
				facesMessages.add(StatusMessage.Severity.WARN, message);
				return null;
			}
			
			
			// Retourne à la feuille de route si on est dans un modele de feuille de route
			if (docWithAttachedRouteId != null) {
				// journalisation
				DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
				// Journalise l'action de modification d'une instance de feuille de route
				if (STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(newDocument.getType())) {
					STRouteStep routeStep = newDocument.getAdapter(STRouteStep.class);
					journalService.journaliserActionEtapeFDR(documentManager, routeStep, dossierDoc,
							STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, 
							STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
				} else {
					journalService.journaliserActionFDR(documentManager, dossierDoc,
							STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, 
							STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
				}
			}
		}
			
		//Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);
		facesMessages.add(StatusMessage.Severity.INFO, "Etape(s) enregistrée(s)");
		resetLstEtapes();
		return routingWebActions.getFeuilleRouteView();
	}
	
	/**
	 * Sauvegarde les étapes de feuille de route en masse stoquees dans lstEtapes.
	 * Si serial vaut "true" alors les étapes sont créées en série, sinon elles sont créées en parallele.
	 * @return
	 * @throws ClientException
	 */
	public String saveRouteElementMass() throws ClientException {
		
		if (!this.checkInputMass()) {
			return STViewConstant.ERROR_VIEW;
		}
		
		/* Création des DocumentModels des étapes à créer et vérification de la validité */
		List<DocumentModel> etapesFDRLst = new ArrayList<DocumentModel>();
		boolean canCreate = true;
		
		for (EtapeFeuilleDeRouteDTO eFDR : lstEtapes) {
			DocumentModel changeableDocument = createEtapeFromDTO(eFDR);
			etapesFDRLst.add(changeableDocument);
			
			boolean isValid = checkValidityStep(changeableDocument);
			if (!isValid) {
				canCreate = false;
			}
		}
		if (!canCreate) {
			// les étapes ne sont pas valides, on ne crée pas les étapes et l'on demande à ce qu'elles soient modifiée
			// les message d'erreur sont ajoutés à facesMessages par la méthode checkValidityStep
			// Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);
			return STViewConstant.ERROR_VIEW;
		}
		
		
		if ("true".equals(serial)) {
			return this.saveRouteElementSerialMass(etapesFDRLst);
		} else {
			return this.saveRouteElementParallelMass(etapesFDRLst);
		}
	}
	
	protected boolean checkValidityStep(DocumentModel stepDoc) throws ClientException {
		return true;
		
	}
	
	/**
	 * Création d'étapes en paralleles en masse
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	protected String saveRouteElementParallelMass(List<DocumentModel> etapesFDRLst) throws ClientException {
		
		/* Création des étapes en parallele */
		final JournalService journalService = STServiceLocator.getJournalService();
		DocumentModel parallelStepFolderDoc = this.routeStepConteneur;
		String parentDocumentPath = (String) parallelStepFolderDoc.getContextData().get(CoreEventConstants.PARENT_PATH);
		String sourceDocumentName = (String) parallelStepFolderDoc.getContextData().get(SOURCE_DOC_NAME);
		DocumentRef routeDocRef = (DocumentRef) parallelStepFolderDoc.getContextData().get(ROUTE_DOCUMENT_REF);
		DocumentModel documentRouteDoc = documentManager.getDocument(routeDocRef);
		try {
			final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
			// Crée le StepFolder conteneur parallèle
			StepFolder parallelStepFolder = parallelStepFolderDoc.getAdapter(StepFolder.class);
			parallelStepFolder.setExecution(STSchemaConstant.STEP_FOLDER_EXECUTION_PARALLEL_VALUE);
			parallelStepFolderDoc = documentRoutingService.addDocumentRouteElementToRoute(documentManager,
					documentRouteDoc, new PathRef(parentDocumentPath), sourceDocumentName, parallelStepFolderDoc);

			// Crée les StepFolder branches paralleles + ajoute le RouteFolder série dans chaque StepFolder
			for (int i = 0; i < etapesFDRLst.size(); i++) {
				// Création du conteneur
				DocumentModel serialStepFolderDoc = documentManager
						.createDocumentModel(STConstant.STEP_FOLDER_DOCUMENT_TYPE);
				StepFolder serialStepFolder = serialStepFolderDoc.getAdapter(StepFolder.class);
				serialStepFolder.setExecution(STSchemaConstant.STEP_FOLDER_EXECUTION_SERIAL_VALUE);
				serialStepFolderDoc = documentRoutingService.addDocumentRouteElementToRoute(documentManager,
						documentRouteDoc, parallelStepFolderDoc.getRef(), null, serialStepFolderDoc);
				
				DocumentModel serialRouteStepFolderDoc = etapesFDRLst.get(i);
				
				// rajout dans le conteneur
				serialRouteStepFolderDoc = documentRoutingService.addDocumentRouteElementToRoute(documentManager,
						documentRouteDoc, serialStepFolderDoc.getRef(), null, serialRouteStepFolderDoc);
				
			}
		} catch (DocumentRouteNotLockedException e) {
			final String message = resourcesAccessor.getMessages().get(
					"feedback.casemanagement.document.route.not.locked");
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return null;
		}
		String message = resourcesAccessor.getMessages().get("st.feuilleRoute.addStepPopup.action.create.success");
		facesMessages.add(StatusMessage.Severity.INFO, message);

		navigationContext.setChangeableDocument(documentRouteDoc);

		// Journalise l'action de modification d'une instance de feuille de route
		if (docWithAttachedRouteId != null) {
			DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docWithAttachedRouteId));
			journalService.journaliserActionFDR(documentManager, dossierDoc,
					STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
		}

		resetLstEtapes();
		// Retourne à la vue du dossier ou du modèle de feuille de route
		return backToModeleFeuilleRouteOrDossier(documentRouteDoc);
	}
	
	/**
	 * Getter de branchCount.
	 * 
	 * @return branchCount
	 */
	public Integer getBranchCount() {
		return branchCount;
	}

	/**
	 * Setter de branchCount.
	 * 
	 * @param branchCount
	 *            branchCount
	 */
	public void setBranchCount(Integer branchCount) {
		this.branchCount = branchCount;
	}

	/**
	 * @return the selectDataModel
	 */
	public SelectDataModel getSelectDataModel() {
		return selectDataModel;
	}

	/**
	 * @param selectDataModel
	 *            the selectDataModel to set
	 */
	public void setSelectDataModel(SelectDataModel selectDataModel) {
		this.selectDataModel = selectDataModel;
	}

	/**
	 * @return the relatedRouteElementsSelectModel
	 */
	public SelectDataModel getRelatedRouteElementsSelectModel() {
		return relatedRouteElementsSelectModel;
	}

	/**
	 * @param relatedRouteElementsSelectModel
	 *            the relatedRouteElementsSelectModel to set
	 */
	public void setRelatedRouteElementsSelectModel(DocumentRoutingSelectDataModelImpl relatedRouteElementsSelectModel) {
		this.relatedRouteElementsSelectModel = relatedRouteElementsSelectModel;
	}
}
