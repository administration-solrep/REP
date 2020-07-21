package fr.dila.ss.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.st.api.feuilleroute.DocumentRouteTableElement;

/**
 * Service de document routing du socle SOLREP, étend et remplace celui de Nuxeo. Ce service permet de gérer les
 * traitements liés aux feuilles de route et aux modèles de feuilles de route. En revanche, il ne permet pas de gérer le
 * catalogue des modèles, ni la distribution des dossiers.
 * 
 * @author jtremeaux
 */
public interface DocumentRoutingService extends fr.dila.ecm.platform.routing.api.DocumentRoutingService {
	/**
	 * Renseigne le nom du document étape de feuille de route à sa création.
	 * 
	 * @param routeStepDoc
	 *            Document étape de feuille de route
	 * @throws ClientException
	 */
	void setRouteStepDocName(DocumentModel routeStepDoc) throws ClientException;

	/**
	 * Recherche et retourne l'étape de feuille de route liée à un CaseLink.
	 * 
	 * @param session
	 *            Session
	 * @param acl
	 *            ActionableCaseLink
	 * @return Étape de la feuille de route
	 * @throws ClientException
	 */
	DocumentModel getDocumentRouteStep(CoreSession session, ActionableCaseLink acl) throws ClientException;

	/**
	 * Demande la validation d'un modèle de feuille de route à l'état brouillon.
	 * 
	 * @param routeModel
	 *            Modèle de feuille de route
	 * @param session
	 *            Session
	 * @param demandeValidation
	 *            Demande la validation ou annule la demande
	 * @return Modèle de feuille de route
	 * @throws ClientException
	 */
	DocumentRoute requestValidateRouteModel(final DocumentRoute routeModel, CoreSession session,
			boolean demandeValidation) throws ClientException;

	/**
	 * Dévalide un modèle de feuille de route (passe son lifeCycle de "validated" à "draft").
	 * 
	 * @param routeModel
	 *            Modèle de feuille de route
	 * @param session
	 *            Session
	 * @return Feuille de route validée
	 * @throws ClientException
	 */
	DocumentRoute invalidateRouteModel(final DocumentRoute routeModel, CoreSession session) throws ClientException;

	/**
	 * Dévalide un modèle de feuille de route (passe son lifeCycle de "validated" à "draft"). Attention cette methode
	 * n'est pas en unrestricted contrairement a invalidateRouteModel(final DocumentRoute routeModel, CoreSession
	 * session)
	 * 
	 * @param routeModel
	 *            Modèle de feuille de route
	 * @param session
	 *            Session
	 * @return Feuille de route validée
	 * @throws ClientException
	 */
	DocumentRoute invalidateRouteModelNotUnrestricted(final DocumentRoute routeModel, CoreSession session)
			throws ClientException;

	/**
	 * Duplique un modèle de feuille de route.
	 * 
	 * @param session
	 *            Session
	 * @param routeToCopyDoc
	 *            Document modèle de feuille de route à dupliquer
	 * @param ministere
	 *            Si renseigné et que la feuille de route dupliquée est sans ministère, la nouvelle feuille de route
	 *            sera assignée à ce ministère
	 * @return Nouveau modèle de feuille de route
	 * @throws ClientException
	 */
	DocumentRoute duplicateRouteModel(CoreSession session, DocumentModel routeToCopyDoc, String ministere)
			throws ClientException;

	/**
	 * Crée un nouveau document dans un conteneur d'éléments de feuille de route.
	 * 
	 * @param session
	 *            Session
	 * @param documentRouteDoc
	 *            Document feuille de route parent
	 * @param parentDocumentRef
	 *            Document parent
	 * @param sourceName
	 *            Element après lequel ajouter (peut être nul)
	 * @param routeElementDoc
	 *            Document élement à ajouter
	 * @return Element nouvellement créé
	 * @throws ClientException
	 */
	DocumentModel addDocumentRouteElementToRoute(CoreSession session, DocumentModel documentRouteDoc,
			DocumentRef parentDocumentRef, String sourceName, DocumentModel routeElementDoc) throws ClientException;

	/**
	 * Retourne le document précédent un document spécifié dans un document Forderish ordonné. Si le document spécifié
	 * est le premier de la liste, retourne null.
	 * 
	 * @param session
	 *            Session
	 * @param parentId
	 *            Identifiant du document parent
	 * @param elementId
	 *            Identifiant du document spécifié
	 * @return Document précédent le document spécifié
	 * @throws ClientException
	 */
	DocumentModel getDocumentBefore(CoreSession session, String parentId, String elementId) throws ClientException;

	/**
	 * Retourne la liste des étapes liée à la feuille de route.
	 * 
	 * @param routeDocument
	 *            {@link DocumentRoute}.
	 * @param session
	 *            The session used to query the {@link DocumentRoute}.
	 * @param A
	 *            list of {@link DocumentRouteElement}
	 */
	List<DocumentRouteTableElement> getFeuilleRouteElements(DocumentRoute route, CoreSession session);

	/**
	 * Duplique une liste d'étapes de feuille de route dans un conteneur cible.
	 * 
	 * @param session
	 *            Session
	 * @param documentRouteDoc
	 *            Document feuille de route cible
	 * @param parentDoc
	 *            Document conteneur cible (feuille de route, ou conteneur d'étapes)
	 * @param relativeDocument
	 *            Insertion relative à ce document (frère), peut être nul
	 * @param before
	 *            Insertion avant le document (sinon après), non pris en compte si relativeDocument est nul
	 * @param documents
	 *            Liste d'étapes à dupliquer
	 * @return Liste des nouveaux documents créés
	 * @throws ClientException
	 */
	List<DocumentModel> pasteRouteStepIntoRoute(CoreSession session, DocumentModel documentRouteDoc,
			DocumentModel parentDoc, DocumentModel relativeDocument, boolean before, List<DocumentModel> documents)
			throws ClientException;

	/**
	 * Crée une nouvelle instance de feuille de route à partir d'un modèle. L'instance n'est pas encore démarrée.
	 * 
	 * @param session
	 *            Session
	 * @param routeModelDoc
	 *            Modèle de feuille de route
	 * @param caseDocId
	 *            Cas
	 * @return Nouvelle instance de feuille de route
	 * @throws ClientException
	 */
	DocumentModel createNewInstance(CoreSession session, DocumentModel routeModelDoc, List<String> docIds)
			throws ClientException;

	/**
	 * Crée une nouvelle instance de feuille de route directement, sans cloner un modèle. L'instance n'est pas encore
	 * démarrée.
	 * 
	 * @param session
	 *            Session
	 * @param name
	 *            nom de l'instance de la feuille de route
	 * @param caseDocId
	 *            Cas
	 * @return Nouvelle instance de feuille de route
	 * @throws ClientException
	 */
	DocumentModel createNewInstance(CoreSession session, String name, String caseDocId) throws ClientException;

	/**
	 * Crée une nouvelle étape de feuille de route
	 * 
	 * @param session
	 *            Session
	 * @param mailboxId
	 *            Identifiant technique de la Mailbox
	 * @param routingTaskType
	 *            Type d'étape
	 * @return Nouvelle étape
	 * @throws ClientException
	 */
	DocumentRouteStep createNewRouteStep(CoreSession session, String mailboxId, String routingTaskType)
			throws ClientException;

	/**
	 * Déplace une étape dans son conteneur parent dans la direction indiquée. Si l'étape est dans un conteneur
	 * parallèle, elle ne peut pas être déplacée. La route doit être déjà verrouillée avant d'effectuer cette action.
	 * 
	 * @param session
	 *            Session
	 * @param routeStepId
	 *            Identifiant technique de l'étape à déplacer
	 * @param moveUp
	 *            Déplace l'étape vers le haut (sinon vers le bas)
	 * @throws ClientException
	 */
	void moveRouteStep(CoreSession session, String routeStepId, boolean moveUp) throws ClientException;

	/**
	 * Valide que l'on peut déplacer une étape avant cet étape.
	 * 
	 * @param routeStepDoc
	 *            Document étape à tester
	 * @throws ClientException
	 */
	void validateMoveRouteStepBefore(DocumentModel routeStepDoc) throws ClientException;

	/**
	 * /** Détermine si l'utilisateur a le droit de modifier l'étape vis-à-vis de son état obligatoire.
	 * 
	 * @param session
	 *            Session
	 * @param routeStepDoc
	 *            Document étape de feuille de route
	 * @return Vrai si l'utilisateur a le droit de modifier l'étape
	 * @throws ClientException
	 */
	boolean isEtapeObligatoireUpdater(CoreSession session, DocumentModel routeStepDoc) throws ClientException;

	/**
	 * Envoi d'un mail aux utilisateurs capables de valider le modèle de feuille de route
	 * 
	 * @param session
	 *            CoreSession
	 * @param routeModel
	 *            Modèle de feuille de route
	 * @throws ClientException
	 */
	void sendValidationMail(CoreSession session, DocumentRoute routeModel) throws ClientException;

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
	void initDocumentRouteStep(CoreSession session, DocumentModel feuilleRouteDoc) throws ClientException;

	/**
	 * Pour un DocumentRoute, parcourt les étapes contenues et vérifie qu'elles sont à l'état draft Si ce n'est pas le
	 * cas, les met à draft
	 * 
	 * @param session
	 * @param route
	 * @return
	 * @throws ClientException
	 */
	void checkAndMakeAllStateStepDraft(CoreSession session, DocumentRoute route) throws ClientException;

	void softDeleteStep(CoreSession session, DocumentModel doc) throws ClientException;
}
