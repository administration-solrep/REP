package fr.dila.ss.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.core.lifecycle.LifeCycleConstants;
import org.nuxeo.ecm.platform.types.Type;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement.ElementLifeCycleTransistion;
import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants.ExecutionTypeValues;
import fr.dila.ecm.platform.routing.api.RouteFolderElement;
import fr.dila.ecm.platform.routing.api.RouteTable;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteNotLockedException;
import fr.dila.ecm.platform.routing.core.impl.EventFirer;
import fr.dila.ecm.platform.routing.core.runner.CreateNewRouteInstanceUnrestricted;
import fr.dila.ss.api.criteria.RouteStepCriteria;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.schema.RoutingTaskSchemaUtils;
import fr.dila.ss.core.schema.StepFolderSchemaUtils;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.exception.LocalizedClientException;
import fr.dila.st.api.feuilleroute.DocumentRouteTableElement;
import fr.dila.st.api.feuilleroute.DocumentRouteTreeElement;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Implémentation du service de document routing du socle SOLREP, étend et remplace celui de Nuxeo.
 * 
 * @author jtremeaux
 */
public abstract class DocumentRoutingServiceImpl extends
		fr.dila.ecm.platform.routing.core.impl.DocumentRoutingServiceImpl implements DocumentRoutingService {
	/**
	 * Logger.
	 */
	private static final Log		LOGGER					= LogFactory.getLog(DocumentRoutingServiceImpl.class);

	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOG						= STLogFactory.getLog(DocumentRoutingServiceImpl.class);

	private static final String		ORDERED_CHILDREN_QUERY	= "SELECT * FROM Document WHERE ecm:parentId = '%s' AND ecm:isCheckedInVersion  = 0 AND "
																	+ "ecm:mixinType != 'HiddenInNavigation' AND ecm:currentLifeCycleState != 'deleted' and ecm:isProxy = 0 ORDER BY ecm:pos";

	/**
	 * Nom des documents conteneur d'étape série.
	 */
	protected static final String		DOCUMENT_NAME_SERIE		= "serie";

	/**
	 * Nom des documents conteneur d'étape parallèle.
	 */
	protected static final String		DOCUMENT_NAME_PARALLELE	= "parallele";

	@Override
	public boolean canUserModifyRoute(final NuxeoPrincipal currentUser) {
		// Pas assez spécifique dans l'IHM de Nuxeo CM, on utilise la grille de fonctions à la place
		return true;
	}

	@Override
	public boolean canUserCreateRoute(final NuxeoPrincipal currentUser) {
		// Pas assez spécifique dans l'IHM de Nuxeo CM, on utilise la grille de fonctions à la place
		return true;
	}

	@Override
	public boolean canUserValidateRoute(final NuxeoPrincipal currentUser) {
		// Pas assez spécifique dans l'IHM de Nuxeo CM, on utilise la grille de fonctions à la place
		return true;
	}

	@Override
	public void setRouteStepDocName(final DocumentModel routeStepDoc) throws ClientException {
		// Renseigne le titre avec le type d'étape
		final STRouteStep routeStep = routeStepDoc.getAdapter(STRouteStep.class);
		final String routingTaskType = routeStep.getType();
		final String distributionMailboxId = routeStep.getDistributionMailboxId();
		final String title = String.format("etape_%s_%s", routingTaskType, distributionMailboxId);
		routeStepDoc.setPathInfo(routeStepDoc.getPathAsString(), title);
	}

	@Override
	public DocumentModel getDocumentRouteStep(final CoreSession session, final ActionableCaseLink acl)
			throws ClientException {
		final DocumentRouteStep routeStep = acl.getDocumentRouteStep(session);
		if (routeStep == null) {
			throw new ClientException("Impossible de trouver l'étape de feuille de route associée au CaseLink <"
					+ acl.getId() + ">");
		}
		return routeStep.getDocument();
	}

	/*
	 * Surcharge Nuxeo : recherche la dernière instance de feuille à partir du dossier. Retourne une liste de feuilles
	 * de route pour rester compatible avec Nuxeo.
	 */
	@Override
	public List<DocumentRoute> getDocumentRoutesForAttachedDocument(final CoreSession session, final String dossierDocId) {
		final List<DocumentRoute> routeList = new ArrayList<DocumentRoute>();
		if (dossierDocId != null) {
			try {
				// Charge le dossier
				final DocumentModel dossierDoc = session.getDocument(new IdRef(dossierDocId));
				if (dossierDoc.hasFacet(STConstant.ROUTABLE_FACET)) {
					final STDossier dossier = dossierDoc.getAdapter(STDossier.class);
					final String routeInstanceDocId = dossier.getLastDocumentRoute();

					if (routeInstanceDocId != null) {

						// Charge l'instance de feuille de route
						final DocumentModel routeDoc = session.getDocument(new IdRef(routeInstanceDocId));
						final DocumentRoute route = routeDoc.getAdapter(DocumentRoute.class);
						routeList.add(route);
					}
				}
			} catch (final ClientException e) {
				// Gestion d'erreurs "à la Nuxeo" pour respecter la signature de cette méthode
				LOGGER.debug("Impossible de récupérer les documents", e);
			}
		}

		return routeList;
	}

	@Override
	public DocumentRoute requestValidateRouteModel(final DocumentRoute routeModel, final CoreSession session,
			final boolean demandeValidation) throws ClientException {
		if (!isLockedByCurrentUser(routeModel, session)) {
			throw new DocumentRouteNotLockedException();
		}
		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				final STFeuilleRoute route = session.getDocument(routeModel.getDocument().getRef()).getAdapter(
						STFeuilleRoute.class);
				if (demandeValidation) {
					EventFirer.fireEvent(session, route, null, STEventConstant.BEFORE_REQUEST_VALIDATE_FEUILLE_ROUTE);
				} else {
					EventFirer.fireEvent(session, route, null,
							STEventConstant.BEFORE_CANCEL_REQUEST_VALIDATE_FEUILLE_ROUTE);
				}
				route.setDemandeValidation(demandeValidation);
				session.saveDocument(route.getDocument());
				session.save();
				if (demandeValidation) {
					EventFirer.fireEvent(session, route, null, STEventConstant.AFTER_REQUEST_VALIDATE_FEUILLE_ROUTE);
				} else {
					EventFirer.fireEvent(session, route, null,
							STEventConstant.AFTER_CANCEL_REQUEST_VALIDATE_FEUILLE_ROUTE);
				}
			}
		}.runUnrestricted();

		return session.getDocument(routeModel.getDocument().getRef()).getAdapter(DocumentRoute.class);
	}

	@Override
	public void sendValidationMail(final CoreSession session, final DocumentRoute routeModel) throws ClientException {
		// Détermine l'objet et le corps du mail
		final STParametreService parametreService = STServiceLocator.getSTParametreService();
		final String mailObjet = parametreService.getParametreValue(session,
				STParametreConstant.NOTIFICATION_MAIL_VALIDATION_FEUILLE_ROUTE_OBJET);
		final String mailTexte = parametreService.getParametreValue(session,
				STParametreConstant.NOTIFICATION_MAIL_VALIDATION_FEUILLE_ROUTE_TEXT);

		// Détermine les destinataires
		final ProfileService profileService = STServiceLocator.getProfileService();
		final List<STUser> users = profileService
				.getUsersFromBaseFunction(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);

		final List<String> idsFdr = new ArrayList<String>();
		idsFdr.add(routeModel.getDocument().getId());

		// Envoi de l'email
		final STMailService mailService = STServiceLocator.getSTMailService();
		mailService.sendHtmlMailToUserListWithLinkToDossiers(session, users, mailObjet, mailTexte, idsFdr);
	}

	@Override
	public DocumentRoute invalidateRouteModel(final DocumentRoute routeModel, final CoreSession session)
			throws ClientException {
		if (!isLockedByCurrentUser(routeModel, session)) {
			throw new DocumentRouteNotLockedException();
		}
		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				EventFirer.fireEvent(session, routeModel, null, STEventConstant.BEFORE_INVALIDATE_FEUILLE_ROUTE);
				routeModel.followTransition(ElementLifeCycleTransistion.toDraft, session, false);
				EventFirer.fireEvent(session, routeModel, null, STEventConstant.AFTER_INVALIDATE_FEUILLE_ROUTE);
			}
		}.runUnrestricted();

		return routeModel;
	}

	@Override
	public DocumentRoute invalidateRouteModelNotUnrestricted(final DocumentRoute routeModel, final CoreSession session)
			throws ClientException {
		if (!isLockedByCurrentUser(routeModel, session)) {
			throw new DocumentRouteNotLockedException();
		}

		EventFirer.fireEvent(session, routeModel, null, STEventConstant.BEFORE_INVALIDATE_FEUILLE_ROUTE);
		routeModel.followTransition(ElementLifeCycleTransistion.toDraft, session, false);
		EventFirer.fireEvent(session, routeModel, null, STEventConstant.AFTER_INVALIDATE_FEUILLE_ROUTE);

		return routeModel;
	}

	@Override
	public DocumentRoute validateRouteModel(final DocumentRoute routeModel, final CoreSession session)
			throws ClientException {
		if (!isLockedByCurrentUser(routeModel, session)) {
			throw new DocumentRouteNotLockedException();
		}

		checkAndMakeAllStateStepDraft(session, routeModel);

		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				final STFeuilleRoute route = session.getDocument(routeModel.getDocument().getRef()).getAdapter(
						STFeuilleRoute.class);
				EventFirer.fireEvent(session, route, null, DocumentRoutingConstants.Events.beforeRouteValidated.name());

				// Valide la feuille de route
				route.setValidated(session);
				// Annule la demande de validation
				route.setDemandeValidation(false);
				session.saveDocument(route.getDocument());
				session.save();
				EventFirer.fireEvent(session, route, null, DocumentRoutingConstants.Events.afterRouteValidated.name());
			}
		}.runUnrestricted();
		return session.getDocument(routeModel.getDocument().getRef()).getAdapter(DocumentRoute.class);
	}

	@Override
	public void checkAndMakeAllStateStepDraft(final CoreSession session, final DocumentRoute route)
			throws ClientException {
		final DocumentModel routeModel = route.getDocument();
		final DocumentModelList steps = session.getChildren(routeModel.getRef());
		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				makeDraftStepsIfNot(session, steps);
			}
		}.runUnrestricted();
	}

	private void makeDraftStepsIfNot(final CoreSession session, final DocumentModelList steps) throws ClientException {
		final Iterator<DocumentModel> stepIterator = steps.iterator();
		DocumentModel document;
		while (stepIterator.hasNext()) {
			document = stepIterator.next();
			if (document.isFolder()) {
				// Dans le cas d'un dossier, on est dans le cas d'étape parallèles donc on doit aller plus loin
				makeDraftStepsIfNot(session, session.getChildren(document.getRef()));
			} else {
				final STRouteStep step = document.getAdapter(STRouteStep.class);
				if (!step.isDraft()) {
					step.followTransition(ElementLifeCycleTransistion.toDraft, session, false);
				}
			}
		}
	}

	@Override
	public DocumentRoute duplicateRouteModel(final CoreSession session, final DocumentModel routeToCopyDoc,
			final String ministere) throws ClientException {
		// Copie le modèle de feuille de route
		final STFeuilleRoute routeToCopy = routeToCopyDoc.getAdapter(STFeuilleRoute.class);
		final String newTitle = routeToCopy.getName() + " (Copie)";
		String escapedTitle = newTitle.replace('/', '-');
		escapedTitle = escapedTitle.replace('\\', '-');
		final DocumentModel newFeuilleRouteDoc = session.copy(routeToCopyDoc.getRef(), routeToCopyDoc.getParentRef(),
				escapedTitle);

		final STFeuilleRoute newFeuilleRoute = newFeuilleRouteDoc.getAdapter(STFeuilleRoute.class);
		lockDocumentRoute(newFeuilleRoute, session);

		// Si le modèle copié était validé, invalide le nouveau modèle
		if (newFeuilleRoute.isValidated()) {
			invalidateRouteModel(newFeuilleRoute, session);
		}

		// Redonne le bon titre au modèle de feuille de route
		newFeuilleRouteDoc.setPropertyValue("dc:title", newTitle);

		// Réinitialise l'état de la feuille de route
		newFeuilleRoute.setTitle(newTitle);
		newFeuilleRoute.setCreator(session.getPrincipal().getName());
		newFeuilleRoute.setDemandeValidation(false);
		newFeuilleRoute.setFeuilleRouteDefaut(false);
		if (StringUtils.isNotEmpty(ministere) && StringUtils.isEmpty(newFeuilleRoute.getMinistere())) {
			newFeuilleRoute.setMinistere(ministere);
		}

		session.saveDocument(newFeuilleRouteDoc);
		session.save();

		// Initialise les étapes de la nouvelle feuille de route
		initDocumentRouteStep(session, newFeuilleRouteDoc);

		// Si un ministère est fourni et que la feuille de route est non affecté
		unlockDocumentRoute(newFeuilleRoute, session);

		return newFeuilleRoute;
	}

	/**
	 * Initialise les étapes d'une feuille de route après la duplication. Les étapes sont réattachée à la nouvelle
	 * feuille de route (champ dénormalisé).
	 * 
	 * @param session
	 *            Session
	 * @param feuilleRouteDoc
	 *            Document feuille de route
	 * @throws ClientException
	 */
	@Override
	public void initDocumentRouteStep(final CoreSession session, final DocumentModel feuilleRouteDoc)
			throws ClientException {
		final RouteStepCriteria routeStepCriteria = new RouteStepCriteria();
		routeStepCriteria.setDocumentRouteId(feuilleRouteDoc.getId());

		final List<DocumentModel> routeStepDocList = findRouteStepByCriteria(session, routeStepCriteria);

		for (final DocumentModel routeStepDoc : routeStepDocList) {
			final STRouteStep routeStep = routeStepDoc.getAdapter(STRouteStep.class);
			routeStep.setDocumentRouteId(feuilleRouteDoc.getId());
			routeStep.setCreator(session.getPrincipal().getName());
			session.saveDocument(routeStepDoc);
		}
	}

	@Override
	public DocumentModel getDocumentBefore(final CoreSession session, final String parentId, final String elementId)
			throws ClientException {
		final String query = String.format(ORDERED_CHILDREN_QUERY, parentId);
		final DocumentModelList orderedChildren = session.query(query);
		DocumentModel docBefore = null;
		for (final DocumentModel doc : orderedChildren) {
			if (doc.getId().equals(elementId)) {
				return docBefore;
			}
			docBefore = doc;
		}
		return docBefore;
	}

	@Override
	public List<DocumentRouteTableElement> getFeuilleRouteElements(final DocumentRoute route, final CoreSession session) {
		final RouteTable table = new RouteTable(route);
		final List<DocumentRouteTableElement> elements = new ArrayList<DocumentRouteTableElement>();
		getDocumentRouteTableElementInFolder(route.getDocument(), elements, table, session, 0, null);
		int maxDepth = 0;
		for (final DocumentRouteTableElement element : elements) {
			final int d = element.getDepth();
			maxDepth = d > maxDepth ? d : maxDepth;
		}
		table.setMaxDepth(maxDepth);
		for (final DocumentRouteTableElement element : elements) {
			element.computeFirstChildList();
		}
		return elements;
	}

	@Override
	public List<fr.dila.ecm.platform.routing.api.DocumentRouteTableElement> getRouteElements(final DocumentRoute route,
			final CoreSession session) {
		return new ArrayList<fr.dila.ecm.platform.routing.api.DocumentRouteTableElement>(getFeuilleRouteElements(route,
				session));
	}

	/**
	 * Construit un tableau contenant les étapes d'une feuille de route.
	 * 
	 * @param doc
	 *            Document parent (conteneur d'étapes)
	 * @param elements
	 *            Tableau d'éléments modifié par effet de bord
	 * @param table
	 *            Table
	 * @param session
	 *            Session
	 * @param depth
	 *            Niveau
	 * @param folder
	 *            Folder
	 */
	protected void getDocumentRouteTableElementInFolder(final DocumentModel doc,
			final List<DocumentRouteTableElement> elements, final RouteTable table, final CoreSession session,
			final int depth, final RouteFolderElement folder) {
		try {
			final List<DocumentModel> children = getNotDeletedChildren(session, doc.getRef());
			getDocumentRouteTableElementInFolder(doc, elements, table, session, depth, folder, children);
		} catch (final ClientException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * construction recursive
	 * 
	 * @param doc
	 * @param elements
	 * @param table
	 * @param session
	 * @param depth
	 * @param folder
	 */
	protected void getDocumentRouteTableElementInFolder(final DocumentModel doc,
			final List<DocumentRouteTableElement> elements, final RouteTable table, final CoreSession session,
			final int depth, final RouteFolderElement folder, final List<DocumentModel> children)
			throws ClientException {

		boolean first = true;
		for (final DocumentModel child : children) {
			final DocumentRouteElement childElement = child.getAdapter(DocumentRouteElement.class);
			List<DocumentModel> subchildren = null;
			if (child.isFolder()) {
				subchildren = getNotDeletedChildren(session, child.getRef());
			}
			if (subchildren != null && !subchildren.isEmpty()) {
				final RouteFolderElement thisFolder = new RouteFolderElement(childElement, table, first, folder, depth);
				getDocumentRouteTableElementInFolder(child, elements, table, session, depth + 1, thisFolder,
						subchildren);
			} else {
				if (folder != null) {
					folder.increaseTotalChildCount();
				} else {
					table.increaseTotalChildCount();
				}

				// Ajoute une ligne correspondant à l'étape
				elements.add(new DocumentRouteTableElement(childElement, table, depth, folder, first));
			}
			first = false;
		}
	}

	/**
	 * Construit un tableau contenant les étapes d'une feuille de route. Ne prend pas en compte les dossiers vides.
	 * 
	 * @param session
	 *            Session
	 * @param element
	 *            Noeur de l'arbre
	 * @throws ClientException
	 */
	protected void getDocumentRouteTreeElementInFolder(final CoreSession session, final DocumentRouteTreeElement element)
			throws ClientException {
		final DocumentModel folderDoc = element.getDocument();
		final List<DocumentModel> childrenDocs = getNotDeletedChildren(session, folderDoc.getRef());
		getDocumentRouteTreeElementInFolder(session, element, childrenDocs);
	}

	/**
	 * Construit récursivement un tableau contenant les étapes d'une feuille de route. Ne prend pas en compte les
	 * dossiers vides.
	 * 
	 * @param session
	 *            Session
	 * @param element
	 *            Noeur de l'arbre
	 * @throws ClientException
	 */
	private void getDocumentRouteTreeElementInFolder(final CoreSession session, final DocumentRouteTreeElement element,
			final List<DocumentModel> childrenDocs) throws ClientException {

		for (final DocumentModel child : childrenDocs) {
			final DocumentRouteTreeElement childrenElement = new DocumentRouteTreeElement(child, element);
			element.getChildrenList().add(childrenElement);

			if (child.isFolder()) {
				final List<DocumentModel> subChildrenDocs = getNotDeletedChildren(session, child.getRef());
				if (!subChildrenDocs.isEmpty()) {
					getDocumentRouteTreeElementInFolder(session, childrenElement, subChildrenDocs);
				}
			}
		}
	}

	/**
	 * Retourne la liste des enfants d'un document qui ne sont pas supprimés
	 * 
	 * @return
	 * @throws ClientException
	 */
	protected List<DocumentModel> getNotDeletedChildren(final CoreSession session, final DocumentRef docRef)
			throws ClientException {
		final List<DocumentModel> notdeleted = new ArrayList<DocumentModel>();
		final DocumentModelList children = session.getChildren(docRef);
		for (final DocumentModel child : children) {
			if (!STLifeCycleConstant.DELETED_STATE.equals(child.getCurrentLifeCycleState())) {
				notdeleted.add(child);
			}
		}
		return notdeleted;
	}

	@Override
	public List<DocumentModel> pasteRouteStepIntoRoute(final CoreSession session, final DocumentModel documentRouteDoc,
			final DocumentModel parent, DocumentModel relativeDocument, boolean before,
			final List<DocumentModel> documents) throws ClientException {
		// Vérifie les paramètres
		if (documentRouteDoc == null) {
			throw new IllegalArgumentException("Document route document must be defined");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parent destination must be defined");
		}

		if (documents == null) {
			throw new IllegalArgumentException("Documents to paste must be defined");
		}

		// Vérifie si la route destination est verrouillée
		final DocumentRoute parentRoute = getParentDocumentRouteModel(parent.getRef(), session);
		if (!isLockedByCurrentUser(parentRoute, session)) {
			throw new DocumentRouteNotLockedException();
		}

		// Conserve uniquement les documents autorisés dans ce conteneur
		final List<DocumentModel> filteredDocumentList = filterDocuments(parent, documents);
		if (filteredDocumentList.size() <= 0) {
			return filteredDocumentList;
		}

		// Charge l'arborescence complete des documents à copier
		// On suppose que tous les éléments sont du meme arbre (pas de vérification, trop couteux)
		final DocumentRouteTreeElement sourceTree = getDocumentRouteTree(session, filteredDocumentList.get(0));

		// Marque les documents à coller et leurs parents
		final Set<String> documentToPasteIdSet = new HashSet<String>();
		for (final DocumentModel doc : filteredDocumentList) {
			documentToPasteIdSet.add(doc.getId());
		}
		markElementToPaste(sourceTree, documentToPasteIdSet);

		// Construit des nouveaux arbres avec uniquement les éléments à copier
		final List<DocumentRouteTreeElement> destTreeList = pruneDocumentRouteTree(sourceTree, parent);

		// Duplique les documents du nouvel arbre récursivement
		for (final DocumentRouteTreeElement routeTree : destTreeList) {
			LOGGER.info("Collage l'élements relativement au document "
					+ (relativeDocument != null ? relativeDocument.getName() : "-") + " before=" + before);
			relativeDocument = pasteRouteTreeIntoRoute(session, documentRouteDoc, routeTree, parent, relativeDocument,
					before);
			before = false;
		}

		return filteredDocumentList;
	}

	/**
	 * Duplique une arborescence de feuille de route dans une autre arborescence.
	 * 
	 * @param session
	 *            Session
	 * @param documentRouteDoc
	 *            Document feuille de route
	 * @param routeTree
	 *            Arborescence à dupliquer
	 * @param parent
	 *            Document model container cible (identique fdr ou container de la feuille de route)
	 * @param relativeDocument
	 *            Insertion relative à ce document (frère), peut être nul
	 * @param before
	 *            Insertion avant le document (sinon après), non pris en compte si relativeDocument est nul
	 * @return Racine des documents dupliqués
	 * @throws ClientException
	 */
	protected DocumentModel pasteRouteTreeIntoRoute(final CoreSession session, final DocumentModel documentRouteDoc,
			final DocumentRouteTreeElement routeTree, final DocumentModel containerCibleDoc, final DocumentModel relativeDocument,
			final boolean before) throws ClientException {
		final DocumentModel containerDoc = routeTree.getDocument();
		if (routeTree.getDocument().isFolder()) {
			DocumentModel newContainerDoc = getContainerDoc(containerDoc, containerCibleDoc);
			newContainerDoc = addRouteElementToRoute(session, containerCibleDoc.getRef(), relativeDocument, before,
					newContainerDoc);
			for (final DocumentRouteTreeElement children : routeTree.getChildrenList()) {
				pasteRouteTreeIntoRoute(session, documentRouteDoc, children, newContainerDoc, null, false);
			}
			return newContainerDoc;
		} else {
			return copyRouteElementIntoRoute(session, documentRouteDoc, containerCibleDoc, relativeDocument, before, containerDoc);
		}
	}

	/**
	 * Crée un nouveau conteneur pour le coller dans l'arbre. Si le conteneur d'origine est une feuille de route, le
	 * document cible sera un conteneur série.
	 * 
	 * @param sourceContainer
	 *            Conteneur à copier
	 * @param sourceContainer
	 *            Conteneur cible (du conteneur copié)
	 * @return Nouveau conteneur
	 */
	protected DocumentModel getContainerDoc(final DocumentModel sourceContainer, final DocumentModel destContainer)
			throws ClientException {
		final DocumentModel newContainerDoc = new DocumentModelImpl(destContainer.getPathAsString(),
				sourceContainer.getName(), DocumentRoutingConstants.STEP_FOLDER_DOCUMENT_TYPE);
		final String executionType = StepFolderSchemaUtils.getExecution(sourceContainer);
		newContainerDoc.setPropertyValue(DocumentRoutingConstants.EXECUTION_TYPE_PROPERTY_NAME, executionType);

		return newContainerDoc;
	}

	@Override
	public DocumentModel addDocumentRouteElementToRoute(final CoreSession session,
			final DocumentModel documentRouteDoc, final DocumentRef parentDocumentRef, final String sourceName,
			DocumentModel routeElementDoc) throws ClientException {
		final STLockService lockService = STServiceLocator.getSTLockService();
		if (!lockService.isLockByCurrentUser(session, documentRouteDoc)) {
			throw new DocumentRouteNotLockedException();
		}

		// Renseigne l'UUID de la feuille de route dans l'étape (champ dénormalisé)
		String docName = null;
		final PathSegmentService pathSegmentService = STServiceLocator.getPathSegmentService();
		final DocumentModel parentDocument = session.getDocument(parentDocumentRef);
		if (STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeElementDoc.getType())) {
			final STRouteStep routeStep = routeElementDoc.getAdapter(STRouteStep.class);
			routeStep.setDocumentRouteId(documentRouteDoc.getId());
			docName = pathSegmentService.generatePathSegment(routeElementDoc);
		} else if (STConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(routeElementDoc.getType())) {
			final StepFolder stepFolder = routeElementDoc.getAdapter(StepFolder.class);
			if (stepFolder.isParallel()) {
				docName = DOCUMENT_NAME_PARALLELE;
			} else {
				docName = DOCUMENT_NAME_SERIE;
			}
		}
		routeElementDoc.setPathInfo(parentDocument.getPathAsString(), docName);
		final String lifecycleState = parentDocument.getCurrentLifeCycleState().equals(
				DocumentRouteElement.ElementLifeCycleState.draft.name()) ? DocumentRouteElement.ElementLifeCycleState.draft
				.name() : DocumentRouteElement.ElementLifeCycleState.ready.name();
		routeElementDoc.putContextData(LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME, lifecycleState);

		routeElementDoc = session.createDocument(routeElementDoc);
		session.orderBefore(parentDocumentRef, routeElementDoc.getName(), sourceName);
		session.save();

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Création dans le conteneur " + parentDocument.getName() + " de l'élément "
					+ routeElementDoc.getName() + " " + " avant l'élément " + sourceName);
		}

		return routeElementDoc;
	}

	@Override
	public void addRouteElementToRoute(final DocumentRef parentDocumentRef, final int idx,
			final DocumentRouteElement routeElement, final CoreSession session) throws ClientException {
		final DocumentRoute route = getParentRouteModel(parentDocumentRef, session);
		if (!isLockedByCurrentUser(route, session)) {
			throw new DocumentRouteNotLockedException();
		}

		// Renseigne l'UUID de la feuille de route dans l'étape (champ dénormalisé)
		if (STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeElement.getDocument().getType())) {
			final STRouteStep routeStep = routeElement.getDocument().getAdapter(STRouteStep.class);
			routeStep.setDocumentRouteId(route.getDocument().getId());
		}

		final DocumentModelList children = session.query(String.format(ORDERED_CHILDREN_QUERY,
				session.getDocument(parentDocumentRef).getId()));
		DocumentModel sourceDoc;
		try {
			sourceDoc = children.get(idx);
			addRouteElementToRoute(parentDocumentRef, sourceDoc.getName(), routeElement, session);
		} catch (final IndexOutOfBoundsException e) {
			addRouteElementToRoute(parentDocumentRef, null, routeElement, session);
		}
	}

	private DocumentRoute getParentRouteModel(final DocumentRef documentRef, final CoreSession session)
			throws ClientException {
		final DocumentModel parentDoc = session.getDocument(documentRef);
		if (parentDoc.hasFacet(DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_FACET)) {
			return parentDoc.getAdapter(DocumentRoute.class);
		}
		final DocumentRouteElement rElement = parentDoc.getAdapter(DocumentRouteElement.class);
		return rElement.getDocumentRoute(session);

	}

	/**
	 * Ajout d'un élément dans une feuille de route à l'emplacement indiqué.
	 * 
	 * @param session
	 *            Session
	 * @param parentDocumentRef
	 *            Ref. du document parent
	 * @param relativeDocument
	 *            Insertion relative à ce document (frère), peut être nul
	 * @param before
	 *            Insertion avant le document (sinon après), non pris en compte si relativeDocument est nul
	 * @param doc
	 *            Document à insérer
	 * @return Document nouvellement créé
	 * @throws ClientException
	 */
	protected DocumentModel addRouteElementToRoute(final CoreSession session, final DocumentRef parentDocumentRef,
			final DocumentModel relativeDocument, final boolean before, DocumentModel doc) throws ClientException {
		final PathSegmentService pss = STServiceLocator.getPathSegmentService();

		// Réinitialise l'étape nouvellement créée
		final DocumentModel parentDocument = session.getDocument(parentDocumentRef);
		doc.setPathInfo(parentDocument.getPathAsString(), pss.generatePathSegment(doc));
		final String lifecycleState = parentDocument.getCurrentLifeCycleState().equals(
				DocumentRouteElement.ElementLifeCycleState.draft.name()) ? DocumentRouteElement.ElementLifeCycleState.draft
				.name() : DocumentRouteElement.ElementLifeCycleState.ready.name();
		doc.putContextData(LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME, lifecycleState);
		doc = session.createDocument(doc);

		// Ordonne l'étape nouvellement créée
		String relativeDocName = null;
		if (relativeDocument != null) {
			if (before) {
				relativeDocName = relativeDocument.getName();
				session.orderBefore(parentDocument.getRef(), doc.getName(), relativeDocName);
			} else {
				final DocumentModelList orderedChilds = session.getChildren(parentDocumentRef);
				final int relativeDocIndex = orderedChilds.indexOf(relativeDocument);
				if (relativeDocIndex < orderedChilds.size() - 1) {
					relativeDocName = orderedChilds.get(relativeDocIndex + 1).getName();
				}
				session.orderBefore(parentDocument.getRef(), doc.getName(), relativeDocName);
			}
		}
		session.save();

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Création dans le conteneur " + parentDocument.getName() + " de l'élément " + doc.getName()
					+ " " + " avant l'élément " + relativeDocName);
		}

		return doc;
	}

	@Override
	public void removeRouteElement(final DocumentRouteElement routeElement, final CoreSession session)
			throws ClientException {
		final DocumentRoute parentRoute = routeElement.getDocumentRoute(session);
		if (!isLockedByCurrentUser(parentRoute, session)) {
			throw new DocumentRouteNotLockedException();
		}

		final DocumentModel routeElementDoc = routeElement.getDocument();
		if (!isRouteElementObligatoireUpdater(session, routeElementDoc)) {
			throw new LocalizedClientException("st.feuilleRoute.etape.action.delete.obligatoire.error");
		}

		// Supprimé l'élément
		softDeleteStep(session, routeElement.getDocument());
	}

	/**
	 * Détermine si un élément de feuille de route peut être modifié vis-à-vis de son état obligatoire.
	 * 
	 * @param session
	 *            Session
	 * @param routeElementDoc
	 *            Élément de la feuille de route (conteneur ou étape)
	 * @return Vrai si l'utilisateur peut modifier le conteneur ou l'étape
	 * @throws ClientException
	 */
	protected boolean isRouteElementObligatoireUpdater(final CoreSession session, final DocumentModel routeElementDoc)
			throws ClientException {
		// Si l'élément est une étape, calcule son état modifiable
		if (!STConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(routeElementDoc.getType())) {
			return isEtapeObligatoireUpdater(session, routeElementDoc);
		}

		// Si l'élément est un conteneur, parcoure ses fils récursivement
		for (final DocumentModel childrenDoc : session.getChildren(routeElementDoc.getRef())) {
			if (!isRouteElementObligatoireUpdater(session, childrenDoc)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Duplication d'un élément d'une feuille de route à l'emplacement spécifié
	 * 
	 * @param session
	 *            Session
	 * @param documentRouteDoc
	 *            Document feuille de route
	 * @param containerCibleDoc
	 *            Document model cible pour la copie
	 * @param relativeDocument
	 *            Insertion relative à ce document (frère), peut être nul
	 * @param before
	 *            Insertion avant le document (sinon après), non pris en compte si relativeDocument est nul
	 * @param doc
	 *            Document à dupliquer
	 * @return Document nouvellement créé
	 * @throws ClientException
	 */
	public DocumentModel copyRouteElementIntoRoute(final CoreSession session, final DocumentModel documentRouteDoc,
			final DocumentModel containerCibleDoc, final DocumentModel relativeDocument, final boolean before,
			final DocumentModel doc) throws ClientException {
		final NuxeoPrincipal principal = (NuxeoPrincipal) session.getPrincipal();
		
		// création d'une copie
		final DocumentModel docCopy = duplicateRouteStep(session, doc, containerCibleDoc);
		final STRouteStep routeStep = docCopy.getAdapter(STRouteStep.class);
		
		// Reset de obligatoireMinistere en fonction des permissions
		if (!principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)
				&& !principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER)) {
			routeStep.setObligatoireMinistere(false);
		}
		
		// Ordonne l'étape nouvellement créée
		DocumentModel createdDoc;
		{
			DocumentModel computedNextDocument = null;
			if (relativeDocument != null) {
				if (before) {
					computedNextDocument = relativeDocument;
				} else {
					final DocumentModelList orderedChilds = session.getChildren(containerCibleDoc.getRef());
					final int relativeDocIndex = orderedChilds.indexOf(relativeDocument);
					if (relativeDocIndex < orderedChilds.size() - 1) {
						computedNextDocument = orderedChilds.get(relativeDocIndex + 1);
					}
				}
			}
			
			// Ajout de la copie au bon emplacement dans la liste des étapes (avant le computedNextDocument)
			createdDoc = this.addDocumentRouteElementToRoute(session, documentRouteDoc, containerCibleDoc.getRef(), 
					(computedNextDocument != null ? computedNextDocument.getName() : null), docCopy);
		}
		
		return createdDoc;
	}

	/**
	 * Crée un nouveau RouteStep en copiant les valeurs de celui passé en parametre.
	 * Les valeurs copiées sont celles visibles dans l'écran de création d'une étape de feuille de route.
	 * (type, distributionMailboxId, AutomaticValidation, ObligatoireSGG, ObligatoireMinistere, DeadLine)
	 * @param session
	 * @param docToCopy
	 * @param parentDocument
	 * @return
	 * @throws ClientException
	 */
	protected DocumentModel duplicateRouteStep(final CoreSession session, final DocumentModel docToCopy, 
			final DocumentModel parentDocument) throws ClientException {
		
		// SOLON: la copie Nuxeo CoreSession.copy utilisée à l'origine pour les feuilles de route a été remplacée
		// par une copie manuelle. On copie les attributs généraux sans les attributs d'état.
		// On nomme la copie (name) en ajoutant '_copie' mais on ne change pas le dc:title
		// Historiquement, solon-epg a utilisé une version du code qui ajoutait le _copie au dc:title
		DocumentModel newDocument = session.createDocumentModel(
				persister.getOrCreateRootOfDocumentRouteInstanceStructure(session).getPathAsString(), docToCopy.getName() + "_copie",
				STConstant.ROUTE_STEP_DOCUMENT_TYPE);
		newDocument.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, docToCopy.getTitle());
		
		
		STRouteStep routeStepDesired = newDocument.getAdapter(STRouteStep.class);
		STRouteStep routeStepToCopy = docToCopy.getAdapter(STRouteStep.class);
		
		routeStepDesired.setType(routeStepToCopy.getType());
		routeStepDesired.setDistributionMailboxId(routeStepToCopy.getDistributionMailboxId());
		routeStepDesired.setAutomaticValidation(routeStepToCopy.isAutomaticValidation());
		routeStepDesired.setObligatoireSGG(routeStepToCopy.isObligatoireSGG());
		routeStepDesired.setObligatoireMinistere(routeStepToCopy.isObligatoireMinistere());
		routeStepDesired.setDeadLine(routeStepToCopy.getDeadLine());
		routeStepToCopy.getName();
		
		// alreadyDuplicated, automaticValidated ignoré de manière volontaire
		
		return newDocument;
	}
	
	/**
	 * Parcours récursivement l'arborescence de la feuille de route, et marque les éléments à coller dans l'arborescence
	 * cible, ainsi que tous leurs conteneurs.
	 * 
	 * @param sourceTree
	 *            Conteneur de la feuille de route
	 * @param docToPasteIdSet
	 *            Ensemble d'ID des éléments à coller (sans les conteneurs)
	 */
	protected void markElementToPaste(final DocumentRouteTreeElement sourceTree, final Set<String> docToPasteIdSet) {
		for (final DocumentRouteTreeElement children : sourceTree.getChildrenList()) {
			final DocumentModel childrenDoc = children.getDocument();
			if (docToPasteIdSet.contains(childrenDoc.getId())) {
				// Marque l'élément
				children.setToPaste(true);

				// Marque les conteneurs intermédiaires
				markParentContainers(children.getParent());
			} else if (childrenDoc.isFolder()) {
				markElementToPaste(children, docToPasteIdSet);
			}
		}
	}

	/**
	 * Remonte récursivement dans l'arbre de feuille de route et marque les conteneurs intermédiaires afin d'éviter
	 * qu'il y ait des "trous" dans l'arbre des documents collés.
	 * 
	 * @param container
	 *            Conteneur actuel
	 */
	protected void markParentContainers(final DocumentRouteTreeElement container) {
		container.setToPaste(true);
		markParentContainers(container.getParent(), new LinkedList<DocumentRouteTreeElement>());
	}

	/**
	 * Remonte récursivement dans l'arbre de feuille de route et marque les conteneurs intermédiaires afin d'éviter
	 * qu'il y ait des "trous" dans l'arbre des documents collés.
	 * 
	 * @param container
	 *            Conteneur actuel
	 * @param containerToMarkList
	 *            Liste des conteneurs à marquer (construite dynamiquement)
	 */
	protected void markParentContainers(final DocumentRouteTreeElement container,
			final List<DocumentRouteTreeElement> containerToMarkList) {
		// Si on est remonté à la racine : condition d'arrêt, ne marque rien
		if (container == null) {
			return;
		}

		if (container.isToPaste()) {
			// Le conteneur est marqué : condition d'arrêt, marque tous les conteneurs intermédiaires
			for (final DocumentRouteTreeElement containerToMark : containerToMarkList) {
				containerToMark.setToPaste(true);
			}
		} else {
			// Le conteneur n'est pas marqué : ajout à la liste des conteneurs à marquer et remonte un niveau
			containerToMarkList.add(container);
			markParentContainers(container.getParent(), containerToMarkList);
		}
	}

	/**
	 * Charge l'arbre complet de la feuille de route à partir d'un de ses éléments.
	 * 
	 * @param session
	 *            Session
	 * @param routeElementDoc
	 *            N'importe lequel des élément de l'arbre à construire
	 * @return Arbre complet de la feuille de route
	 * @throws ClientException
	 */
	protected DocumentRouteTreeElement getDocumentRouteTree(final CoreSession session,
			final DocumentModel routeElementDoc) throws ClientException {
		// Remonte à la route à partir de l'élément
		final DocumentRouteElement routeElement = routeElementDoc.getAdapter(DocumentRouteElement.class);

		// Si l'elément chargé est un élément de feuille de route, remonte à la route
		if (routeElement == null) {
			throw new ClientException("Document is not a route element");
		}

		final DocumentRoute documentRoute = routeElement.getDocumentRoute(session);
		if (documentRoute == null) {
			throw new ClientException("Cannot get document route from route element");
		}

		final DocumentModel routeDoc = documentRoute.getDocument();
		final DocumentRouteTreeElement tree = new DocumentRouteTreeElement(routeDoc, null);
		getDocumentRouteTreeElementInFolder(session, tree);

		return tree;
	}

	/**
	 * Remonte à la route parente à partir d'un élément ou d'une feuille de route.
	 * 
	 * @param documentRef
	 *            Document source
	 * @param session
	 *            Session
	 * @return Feuille de route
	 * @throws ClientException
	 */
	protected DocumentRoute getParentDocumentRouteModel(final DocumentRef documentRef, final CoreSession session)
			throws ClientException {
		final DocumentModel parentDoc = session.getDocument(documentRef);
		if (parentDoc.hasFacet(DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_FACET)) {
			return parentDoc.getAdapter(DocumentRoute.class);
		}
		final DocumentRouteElement rElement = parentDoc.getAdapter(DocumentRouteElement.class);

		return rElement.getDocumentRoute(session);
	}

	/**
	 * Filtre les documents pour conserver uniquement les documents autorisés dans le conteneur.
	 * 
	 * @param parent
	 *            Conteneur
	 * @param documents
	 *            Documents à filtrer
	 * @return Documents filtrés
	 */
	protected List<DocumentModel> filterDocuments(final DocumentModel parent, final List<DocumentModel> documents) {
		final List<DocumentModel> documentsToPast = new LinkedList<DocumentModel>();

		final TypeManager typeManager = STServiceLocator.getTypeManager();
		final Collection<Type> allowedTypes = typeManager.getAllowedSubTypes(parent.getType());
		final List<String> allowedTypesNames = new LinkedList<String>();
		for (final Type type : allowedTypes) {
			allowedTypesNames.add(type.getId());
		}

		for (final DocumentModel doc : documents) {
			if (allowedTypesNames.contains(doc.getType())) {
				documentsToPast.add(doc);
			}
		}

		return documentsToPast;
	}

	/**
	 * Parcours l'arbre des documents à copier, et construit des nouveaux arbres contenant uniquement les étapes
	 * marquées.
	 * 
	 * @param sourceContainer
	 *            Conteneur source
	 * @param destContainer
	 *            Conteneur cible
	 * @return Racines des sous-arbres
	 * @throws ClientException
	 */
	protected List<DocumentRouteTreeElement> pruneDocumentRouteTree(final DocumentRouteTreeElement sourceContainer,
			final DocumentModel destContainer) throws ClientException {
		// Construit des arbres avec uniquement les étapes marquées
		final List<DocumentRouteTreeElement> rootList = new LinkedList<DocumentRouteTreeElement>();
		pruneDocumentRouteTree(sourceContainer, null, rootList);

		// Retire les étapes racines qui génèreront deux conteneurs séries ou parallèle successifs
		final List<DocumentRouteTreeElement> simplifiedRootList = new LinkedList<DocumentRouteTreeElement>();
		for (final DocumentRouteTreeElement root : rootList) {
			final DocumentModel rootDoc = root.getDocument();
			boolean simplify = false;
			if (rootDoc.isFolder()) {
				final String executionType1 = StepFolderSchemaUtils.getExecution(rootDoc);
				final String executionType2 = StepFolderSchemaUtils.getExecution(destContainer);
				if (executionType1.equals(executionType2)) {
					simplify = true;
				}
			}
			if (simplify) {
				for (final DocumentRouteTreeElement element : root.getChildrenList()) {
					simplifiedRootList.add(element);
				}
			} else {
				simplifiedRootList.add(root);
			}
		}
		return simplifiedRootList;
	}

	/**
	 * Parcours l'arbre des documents à copier, et construit des nouveaux arbres contenant uniquement les étapes
	 * marquées.
	 * 
	 * @param sourceContainer
	 *            Conteneur source
	 * @param destContainer
	 *            Conteneur cible
	 * @param rootList
	 *            Liste des racines (construite par effet de bord)
	 * @return Racines des sous-arbres
	 */
	protected void pruneDocumentRouteTree(final DocumentRouteTreeElement sourceContainer,
			DocumentRouteTreeElement destContainer, final List<DocumentRouteTreeElement> rootList) {
		if (sourceContainer.isToPaste()) {
			if (destContainer == null) {
				// Rencontre un conteneur marqué, qui n'a pas encore de parent destination : c'est une nouvelle racine
				destContainer = new DocumentRouteTreeElement(sourceContainer.getDocument(), null);
				rootList.add(destContainer);
			} else {
				final DocumentRouteTreeElement newContainer = new DocumentRouteTreeElement(
						sourceContainer.getDocument(), null);
				destContainer.getChildrenList().add(newContainer);
				destContainer = newContainer;
			}
		}
		for (final DocumentRouteTreeElement children : sourceContainer.getChildrenList()) {
			final DocumentModel childrenDoc = children.getDocument();
			if (childrenDoc.isFolder()) {
				pruneDocumentRouteTree(children, destContainer, rootList);
			} else {
				if (children.isToPaste()) {
					destContainer.getChildrenList().add(children);
				}
			}
		}
	}

	@Override
	public DocumentModel createNewInstance(final CoreSession session, final DocumentModel routeModelDoc,
			final List<String> docIds) throws ClientException {

		final STFeuilleRoute model = routeModelDoc.getAdapter(STFeuilleRoute.class);
		final CreateNewRouteInstanceUnrestricted runner = new CreateNewRouteInstanceUnrestricted(session, model,
				docIds, false, persister);
		runner.runUnrestricted();
		final DocumentRoute routeInstance = runner.getInstance();
		final DocumentModel routeInstancedoc = routeInstance.getDocument();

		// Initialise les étapes de la nouvelle feuille de route, attache l'identifiant de la feuille de route aux
		// étapes (dénormalisation).
		initDocumentRouteStep(session, routeInstancedoc);
		return routeInstancedoc;
	}

	@Override
	public DocumentModel createNewInstance(final CoreSession session, final String name, final String caseDocId)
			throws ClientException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Création d'une nouvelle instance de feuille de route sur le cas <" + caseDocId);
		}

		DocumentModel route1 = session.createDocumentModel(
				persister.getOrCreateRootOfDocumentRouteInstanceStructure(session).getPathAsString(), name,
				STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE);
		route1.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, name);
		route1 = session.createDocument(route1);
		final List<String> docIds = new ArrayList<String>();
		docIds.add(caseDocId);
		final STFeuilleRoute routeInstance = route1.getAdapter(STFeuilleRoute.class);
		routeInstance.setAttachedDocuments(docIds);

		// Initialise les étapes de la nouvelle feuille de route, attache l'identifiant de la feuille de route aux
		// étapes (dénormalisation).
		initDocumentRouteStep(session, route1);
		return routeInstance.getDocument();
	}

	@Override
	public DocumentRouteStep createNewRouteStep(final CoreSession session, final String mailboxId,
			final String routingTaskType) throws ClientException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Création d'une nouvelle étape de feuille de route");
		}

		final DocumentModel routeStepDoc = session.createDocumentModel(STConstant.ROUTE_STEP_DOCUMENT_TYPE);
		final STRouteStep routeStep = routeStepDoc.getAdapter(STRouteStep.class);
		routeStep.setType(routingTaskType);
		routeStep.setDistributionMailboxId(mailboxId);

		return routeStep;
	}

	@Override
	public void moveRouteStep(final CoreSession session, final String routeStepId, final boolean moveUp)
			throws ClientException {
		final DocumentModel routeElementDocToMove = session.getDocument(new IdRef(routeStepId));
		final DocumentModel parentDoc = session.getDocument(routeElementDocToMove.getParentRef());
		final ExecutionTypeValues executionType = StepFolderSchemaUtils.getExecutionType(parentDoc);
		if (DocumentRoutingConstants.ExecutionTypeValues.parallel.equals(executionType)) {
			throw new LocalizedClientException(
					"feedback.casemanagement.document.route.cant.move.steps.in.parallel.container");
		}
		final DocumentModelList orderedChilds = getOrderedRouteElement(parentDoc.getId(), session);
		final int selectedDocumentIndex = orderedChilds.indexOf(routeElementDocToMove);
		if (moveUp) {
			// Interdit le déplacement si l'étape déplacée est la première étape
			if (selectedDocumentIndex == 0) {
				throw new LocalizedClientException(
						"feedback.casemanagement.document.route.already.first.step.in.container");
			}
			final DocumentModel stepMoveBefore = orderedChilds.get(selectedDocumentIndex - 1);

			// Interdit le déplacement avant une étape en cours ou terminée
			final DocumentRouteElement stepElementMoveBefore = stepMoveBefore.getAdapter(DocumentRouteElement.class);
			if (!stepElementMoveBefore.isDraft() && !stepElementMoveBefore.isReady()) {
				throw new LocalizedClientException(
						"feedback.casemanagement.document.route.cant.move.step.after.no.modifiable.step");
			}

			// Interdit le déplacement avant une étape obligatoire
			if (!isEtapeObligatoireUpdater(session, stepMoveBefore)) {
				throw new LocalizedClientException("st.feuilleRoute.action.moveUp.avantEtapeObligatoire.error");
			}

			// Valide que l'on peut déplacer l'étape vers le haut
			validateMoveRouteStepBefore(stepMoveBefore);

			// Déplace l'étape
			session.orderBefore(parentDoc.getRef(), routeElementDocToMove.getName(), stepMoveBefore.getName());
		} else {
			// Interdit le déplacement si l'étape déplacée est la dernière étape
			if (selectedDocumentIndex == orderedChilds.size() - 1) {
				throw new LocalizedClientException("feedback.casemanagement.document.already.last.step.in.container");
			}

			// Interdit le déplacement après une étape obligatoire
			final DocumentModel stepMoveAfter = orderedChilds.get(selectedDocumentIndex + 1);
			if (!isEtapeObligatoireUpdater(session, stepMoveAfter)) {
				throw new LocalizedClientException("st.feuilleRoute.action.moveUp.apresEtapeObligatoire.error");
			}

			// Déplace l'étape
			session.orderBefore(parentDoc.getRef(), stepMoveAfter.getName(), routeElementDocToMove.getName());
		}
	}

	@Override
	public boolean isEtapeObligatoireUpdater(final CoreSession session, final DocumentModel routeStepDoc)
			throws ClientException {
		// Traite uniquement les étapes de feuille de route et pas les conteneurs
		if (routeStepDoc.hasFacet("Folderish")) {
			return true;
		}
		final STRouteStep routeStep = routeStepDoc.getAdapter(STRouteStep.class);
		final NuxeoPrincipal nuxeoPrincipal = (NuxeoPrincipal) session.getPrincipal();
		final List<String> groups = nuxeoPrincipal.getGroups();

		// Seul l'administrateur fonctionnel a le droit de modifier les étapes obligatoires SGG
		final boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
		if (routeStep.isObligatoireSGG() && !isAdminFonctionnel) {
			return false;
		}

		// Seul l'administrateur ministériel a le droit de modifier les étapes obligatoires ministère
		final boolean isAdminMinisteriel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER);
		if (routeStep.isObligatoireMinistere() && !isAdminMinisteriel) {
			return false;
		}

		return true;
	}

	/**
	 * Recherche d'étapes de feuille de route par critères de recherche.
	 * 
	 * @param criteria
	 *            Critères de recherche
	 * @return Liste d'étapes de feuille de route
	 * @throws ClientException
	 */
	private List<DocumentModel> findRouteStepByCriteria(final CoreSession session, final RouteStepCriteria criteria)
			throws ClientException {
		final StringBuilder sb = new StringBuilder("SELECT s.ecm:uuid as id FROM ").append(
				STConstant.ROUTE_STEP_DOCUMENT_TYPE).append(" AS s ");

		final List<Object> paramList = new ArrayList<Object>();
		if (StringUtils.isNotBlank(criteria.getDocumentRouteId())) {
			sb.append("WHERE isChildOf(s.").append(STSchemaConstant.ECM_UUID_XPATH).append(", SELECT r.")
					.append(STSchemaConstant.ECM_UUID_XPATH).append(" FROM ")
					.append(STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE).append(" AS r WHERE r.")
					.append(STSchemaConstant.ECM_UUID_XPATH).append(" = ?) = 1 ");
			paramList.add(criteria.getDocumentRouteId());
		}

		return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, STConstant.ROUTE_STEP_DOCUMENT_TYPE,
				sb.toString(), paramList.toArray());
	}

	@Override
	public void softDeleteStep(final CoreSession session, final DocumentModel doc) throws ClientException {
		if ("true".equals(Framework.getProperty("socle.solrep.routestep.soft.delete", "true"))) {
			removeDocumentRouteIdData(session, doc);
			session.move(doc.getRef(), session.getDocument(new PathRef("/case-management/trash-root")).getRef(), null);
			if (!STLifeCycleConstant.DELETED_STATE.equals(doc.getCurrentLifeCycleState())) {
				session.followTransition(doc.getRef(), STLifeCycleConstant.TO_DELETE_TRANSITION);
			}
			session.save();
		} else {
			LOG.info(session, SSLogEnumImpl.DEL_STEP_TEC, doc);
			session.removeDocument(doc.getRef());
			session.save();
		}
	}

	private void removeDocumentRouteIdData(final CoreSession session, final DocumentModel doc) throws ClientException {
		if (doc.isFolder()) {
			final List<DocumentModel> children = session.getChildren(doc.getRef());
			for (final DocumentModel child : children) {
				removeDocumentRouteIdData(session, child);
			}
		}
		if (doc.hasSchema(STSchemaConstant.ROUTING_TASK_SCHEMA)) {
			RoutingTaskSchemaUtils.setDocumentRouteId(doc, "");
			session.saveDocument(doc);
		}
	}
}
