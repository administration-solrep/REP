package fr.dila.reponses.web.dossier;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.feuilleroute.GetRunningStepsUnrestricted;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.mailbox.ReponsesWebActions;
import fr.dila.ss.web.helper.MailboxHelperBean;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.dossier.STDossier;

/**
 * Une classe qui permet de gérer les actions relatives aux listes de dossiers.
 * 
 * @author jgomez
 */
@Name("dossierListingActions")
@Scope(ScopeType.CONVERSATION)
public class DossierListingActionsBean implements Serializable {

    /**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 6957529610697227813L;

	private static final Log log = LogFactory.getLog(DossierListingActionsBean.class);

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient DocumentsListsManagerBean documentsListsManager;

    @In(create = true, required = false)
    protected MailboxHelperBean mailboxHelper;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(create = true)
    protected transient ReponsesWebActions webActions;

    /**
     * Retourne la ref du dossier auquel la question appartient
     * 
     * @param questionRef : la reference de la question
     * @return la reference du dossier
     * @throws ClientException
     */

    public DocumentRef getDossierRefFromQuestionRef(DocumentRef questionRef) throws ClientException {
        DocumentModel doc = documentManager.getDocument(questionRef);
        return getDossierRefFromQuestionDoc(doc);
    }

    public DocumentRef getDossierRefFromQuestionDoc(DocumentModel questionDoc) {
        Question question = questionDoc.getAdapter(Question.class);
        return question.getDossierRef();
    }

    /**
     * Retourne le dossier auquel la question appartient
     * 
     * @param questionRef : la reference de la question
     * @return le dossier
     * @throws ClientException
     */
    public DocumentModel getDossierFromQuestionRef(DocumentRef questionRef) throws ClientException {
        DocumentRef dossierRef = getDossierRefFromQuestionRef(questionRef);
        return documentManager.getDocument(dossierRef);
    }

    /**
     * 
     * Retourne les running steps de la question
     * 
     * @param questionRef : la reference de la question
     * @return les documents model des running steps
     * @throws ClientException
     * 
     */
    // TODO JGZ : retourner une liste de steps
    public String[] getNameRunningStepsFromQuestionRef(DocumentRef questionRef) throws ClientException {
        DocumentModel dossierDoc = getDossierFromQuestionRef(questionRef);
        STDossier dossier = dossierDoc.getAdapter(STDossier.class);
        final String routeInstanceId = dossier.getLastDocumentRoute();
        FeuilleRouteService service = ReponsesServiceLocator.getFeuilleRouteService();
        GetRunningStepsUnrestricted runningStepsGetter = new GetRunningStepsUnrestricted(documentManager.getRepositoryName(), routeInstanceId,
                service);
        runningStepsGetter.runUnrestricted();
        return runningStepsGetter.getNameRunningSteps();
    }

    // TODO JGZ : retourner une liste de steps
    // TODO JGZ : Bouger la méthode de fabrication du libellé quelque part.
    public String getRunningStepsLabelFromQuestionRef(DocumentRef questionRef) throws ClientException {
        String[] nameRunningStep = getNameRunningStepsFromQuestionRef(questionRef);
        String routingTaskLabel = resourcesAccessor.getMessages().get(nameRunningStep[0]);
        String mailboxName = nameRunningStep[1];
        return routingTaskLabel + " " + mailboxName;
    }

    /**
     * Retourne une liste de dossiers à partir d'une sélection courante constituée de questions, de dossiers ou de dossierLink.
     * 
     * @return dossiers La liste des dossiers, sous forme de DocumentModel
     * @throws ClientException
     */
    public DocumentModelList getDossiersFromSelection() throws ClientException {
        return getDossiersFromSelection(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
    }

    /**
     * Retourne une liste de dossiers à partir d'une sélection courante constituée de questions, de dossiers ou de dossierLink.
     * 
     * @return dossiers La liste des dossiers, sous forme de DocumentModel
     * @throws ClientException
     */
    public DocumentModelList getDossiersFromSelection(String selectionName) throws ClientException {
        DocumentModelList dossiers = new DocumentModelListImpl();
        for (DocumentModel docModel : documentsListsManager.getWorkingList(selectionName)) {
            String docType = docModel.getType();
            if (DossierConstants.QUESTION_DOCUMENT_TYPE.equals(docType)) {
                Question question = docModel.getAdapter(Question.class);
                DocumentModel dossierDoc = documentManager.getDocument(question.getDossierRef());
                dossiers.add(dossierDoc);
            } else if (DossierConstants.DOSSIER_DOCUMENT_TYPE.equals(docType)) {
                dossiers.add(docModel);
            } else if (STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE.equals(docType)) {
                DossierLink dossierLink = docModel.getAdapter(DossierLink.class);
                DocumentModel dossierDoc = dossierLink.getDossier(documentManager).getDocument();
                dossiers.add(dossierDoc);
            } else {
                throw new IllegalArgumentException("Unexpected Document model type [" + docType + "]");
            }
        }
        return dossiers;
    }

    public String getSourceNumeroQuestion(DocumentModel doc) throws ClientException {
        DossierCommon common = doc.getAdapter(DossierCommon.class);
        Dossier dossier = common.getDossier(documentManager);
        return dossier.getQuestion(documentManager).getSourceNumeroQuestion();
    }

    public String navigateToDossier(String docId) throws ClientException {
        return navigateToDossier(docId, null);
    }

    public String navigateToDossier(String docId, String tabId) throws ClientException {
        DocumentModel dossierDoc = documentManager.getDocument(new IdRef(docId));
        return navigateToDossier(dossierDoc, tabId);
    }

    /**
     * Charge un dossier comme document courant à partir d'un espace de recherche, suivi, etc. Si l'utilisateur a la main sur le CaseLink (parce que le dossier lui est destiné, ou qu'il est administrateur), le CaseLink est aussi chargé pour que l'utilisateur puisse effectuer des actions de distribution.
     * 
     * @param dossierDoc Dossier
     * @return Vue
     * @throws ClientException
     */
    public String navigateToDossier(DocumentModel dossierDoc) throws ClientException {
        return navigateToDossier(dossierDoc, null);
    }

    public String navigateToDossier(DocumentModel dossierDoc, String tabId) throws ClientException {
        // Recherche les DossierLink que l'utilisateur peut actionner
        final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        List<DocumentModel> dossierLinkList = corbeilleService.findUpdatableDossierLinkForDossier(documentManager, dossierDoc);

        DocumentModel dossierLinkDoc = null;
        if (dossierLinkList.size() > 0) {
            if (dossierLinkList.size() > 1) {
                log.info("Plus d'un DossierLink trouvé, selection du premier");
            }
            dossierLinkDoc = dossierLinkList.get(0);
        }

        return navigateToDossierLink(dossierDoc, dossierLinkDoc, tabId);
    }

    public String navigateToDossierLink(String dossierDocId, String dossierLinkDocId) throws ClientException {
        return navigateToDossierLink(dossierDocId, dossierLinkDocId, null);
    }

    public String navigateToDossierLink(String dossierDocId, String dossierLinkDocId, String tabId) throws ClientException {
        DocumentModel dossierDoc = documentManager.getDocument(new IdRef(dossierDocId));
        DocumentModel dossierLinkDoc = documentManager.getDocument(new IdRef(dossierLinkDocId));
        return navigateToDossierLink(dossierDoc, dossierLinkDoc, tabId);
    }

    /**
     * Charge un dossier comme document courant à partir d'un espace de recherche, suivi, etc. Charge aussi le DossierLink pour prendre la main sur la distribution.
     * 
     * @param dossierDoc Dossier
     * @param dossierLinkDoc DossierLink
     * @return Vue
     * @throws ClientException
     */
    public String navigateToDossierLink(DocumentModel dossierDoc, DocumentModel dossierLinkDoc) throws ClientException {
        return navigateToDossierLink(dossierDoc, dossierLinkDoc, null);
    }

    public String navigateToDossierLink(DocumentModel dossierDoc, DocumentModel dossierLinkDoc, String tabId) throws ClientException {
        // Charge le dossier comme document courant
        navigationContext.navigateToDocument(dossierDoc);

        // Charge le DossierLink dans la corbeille
        DossierLink dossierLink = null;
        if (dossierLinkDoc != null) {
            dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
        }
        corbeilleActions.setCurrentDossierLink(dossierLink);

        if (tabId != null) {
            webActions.setCurrentTabId(tabId);
        }

        return null;
    }

    /**
     * Cet attribut indique si il faut afficher / masquer les actions disponibles en haut de la liste des dossiers. La liste est affichée uniquement si des dossiers sont sélectionnés
     * 
     * @param workingListId Identifiant technique de la liste de sélection
     * @return Affichage de la barre d'action
     */
    public boolean isShownMassAction(String workingListId) {
        List<DocumentModel> selection = documentsListsManager.getWorkingList(workingListId);
        return selection != null && !selection.isEmpty();
    }

    public String getVewExportCSV() {
        return STViewConstant.CSV_VIEW;
    }
}
