package fr.dila.reponses.web.favoris;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.NameAlreadyBoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.trash.TrashService;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.util.DocumentsListsUtils;
import org.nuxeo.ecm.platform.userworkspace.web.ejb.UserWorkspaceManagerActionsBean;
import org.nuxeo.ecm.webapp.contentbrowser.DocumentActionsBean;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.dossier.DossierListingActionsBean;
import fr.dila.reponses.web.recherche.RechercheAvanceeActionsBean;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Bean pour la gestion des favoris (dossier)
 * 
 * @author jgomez
 * 
 */
@Name("favorisDossierActions")
@Scope(CONVERSATION)
public class FavorisDossierActionsBean implements Serializable {

    private static final String FAVORISDOSSIER_REPERTOIRE_CONTENT_VIEW = "favorisdossier_repertoire_content";

    private static final String DOCUMENT_TYPE_FAVORIS_DOSSIER_REPERTOIRE = "FavorisDossierRepertoire";

    private static final long serialVersionUID = -6601690797613742328L;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient DocumentsListsManagerBean documentsListsManager;

    private static final Log log = LogFactory.getLog(FavorisDossierActionsBean.class);

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected transient DossierListingActionsBean dossierListingActions;

    @In(create = true, required = false)
    protected transient DocumentActionsBean documentActions;

    @In(create = true, required = false)
    protected transient ContentViewActions contentViewActions;

    @In(create = true, required = true)
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(create = true, required = true)
    protected transient UserWorkspaceManagerActionsBean userWorkspaceManagerActions;

    protected transient DocumentModelList favorisSelected;

    @In(create = true, required = true)
    protected transient RechercheAvanceeActionsBean rechercheAvanceeActions;

    protected transient TrashService trashService;

    private String currentVersion1 = "";

    @In(create = true, required = true)
    protected DocumentModel currentRequete;

    private String currentRepertoire;

    private DocumentModel currentFavorisRepertoire;

    public void deleteSelectionListFromQuestion() throws ClientException {
        final FavorisDossierService favorisDossierService = ReponsesServiceLocator.getFavorisDossierService();
        favorisDossierService.delete(documentManager, dossierListingActions.getDossiersFromSelection());
        contentViewActions.refresh(FAVORISDOSSIER_REPERTOIRE_CONTENT_VIEW);
        currentRepertoire = StringUtils.EMPTY;
        return;
    }

    public void deleteSelectionList() throws ClientException {
        List<DocumentModel> selection = documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
        List<DocumentRef> refsToRemove = DocumentsListsUtils.getDocRefs(selection);
        final TrashService trashService = STServiceLocator.getTrashService();
        trashService.purgeDocuments(documentManager, refsToRemove);
        contentViewActions.refresh(FAVORISDOSSIER_REPERTOIRE_CONTENT_VIEW);
        documentsListsManager.resetWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
        return;
    }

    /**
     * ajoute la selection list aux favoris
     * 
     */
    public void addSelectionList() {
        final FavorisDossierService favorisDossierService = ReponsesServiceLocator.getFavorisDossierService();
        if (favorisSelected == null) {
            return;
        }
        try {
            favorisDossierService.add(documentManager, favorisSelected, currentRepertoire);
            contentViewActions.refresh(FAVORISDOSSIER_REPERTOIRE_CONTENT_VIEW);
            currentRepertoire = StringUtils.EMPTY;
        } catch (ClientException e) {
            log.warn(e);
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("favoris.erreur.add"));
        }
        return;
    }

    /**
     * cree le repertoire de favoris et ajoute la selection de favoris à ce repertoire
     * 
     */
    public String createRepertoireAndAddFavoris() {
        final FavorisDossierService favorisDossierService = ReponsesServiceLocator.getFavorisDossierService();
        String view = "";
        try {
            currentRepertoire = DublincoreSchemaUtils.getTitle(navigationContext.getChangeableDocument());
            Calendar dateValidite = DublincoreSchemaUtils.getValidDate(navigationContext.getChangeableDocument());
            DocumentModel repertoire = favorisDossierService.createFavorisRepertoire(documentManager, currentRepertoire, dateValidite);
            addSelectionList();
            view = navigationContext.navigateToDocumentWithView(repertoire, null);
        } catch (NameAlreadyBoundException e) {
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("favoris.erreur.name"));
        } catch (ClientException e) {
            log.warn(e);
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("favoris.erreur.add"));
        }
        return view;
    }

    /**
     * Prépare la création d'un repertoire et navigue vers le document prêt à être créé.
     * 
     * @throws ClientException
     */
    public String newRepertoire() throws ClientException {
        String selectionListName = contentViewActions.getCurrentContentView().getSelectionListName();

        favorisSelected = new DocumentModelListImpl(dossierListingActions.getDossiersFromSelection(selectionListName));
        final FavorisDossierService favorisDossierService = ReponsesServiceLocator.getFavorisDossierService();
        DocumentModel root = favorisDossierService.getRootFavorisDossier(documentManager);
        if (root == null) {
            log.error("La racine des favoris est nulle");
            return "prepare_add_favoris";
        }
        DocumentModel rep = new DocumentModelImpl(root.getPathAsString(), "favorisdossier", DOCUMENT_TYPE_FAVORIS_DOSSIER_REPERTOIRE);
        navigationContext.setChangeableDocument(rep);
        return "prepare_add_favoris";
    }

    /**
     * Retourne la liste des répertoires de favoris de l'utilisateur
     * 
     * @return
     */
    public DocumentModelList getRepertoires() {
        final FavorisDossierService favorisDossierService = ReponsesServiceLocator.getFavorisDossierService();
        DocumentModelList resultList = new DocumentModelListImpl();
        if (favorisDossierService == null) {
            return resultList;
        }
        try {
            resultList = (DocumentModelList) favorisDossierService.getFavorisRepertoires(documentManager);

        } catch (ClientException e) {
            log.warn(e);
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("favoris.erreur.listeRepertoire"));
        }
        return resultList;
    }

    /**
     * permet l'affichage des répertoires favoris existants pour permettre d'ajouter de nouveaux elements
     * 
     * @return
     * @throws ClientException
     */
    public List<SelectItem> getVersionList() throws ClientException {
        List<SelectItem> versionList = new ArrayList<SelectItem>();
        DocumentModelList reponseList = getRepertoires();
        if (!reponseList.isEmpty()) {
            for (DocumentModel element : reponseList) {
                versionList.add(new SelectItem(element.getName(), element.getTitle()));
            }
        } else {
            versionList.add(new SelectItem("1", "aucun favori"));
        }
        return versionList;
    }

    /**
     * ajoute les elements au repertoire selectionne dans la liste deroulante
     * 
     * @return
     */
    public String addFavoris() {
        navigationContext.getChangeableDocument().reset();
        final FavorisDossierService favorisDossierService = ReponsesServiceLocator.getFavorisDossierService();
        String view = "";
        try {
            if (currentVersion1 != null && !"".equals(currentVersion1)) {
                currentRepertoire = currentVersion1;
                DocumentModel repertoire = favorisDossierService.getCurrentRepertoireDocument(documentManager, currentRepertoire);
                addSelectionList();
                view = navigationContext.navigateToDocumentWithView(repertoire, null);
            }
        } catch (ClientException e) {
            log.warn(e);
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("favoris.erreur.add"));
        }
        return view;
    }

    public String getCurrentVersion1() {
        return currentVersion1;
    }

    public void setCurrentVersion1(String currentVersion1) {
        this.currentVersion1 = currentVersion1;
    }

    /**
     * Prépare la création d'un repertoire et navigue vers le document prêt à être crée.
     * 
     * @throws ClientException
     */

    public String goToRepertoire(DocumentModel doc) throws ClientException {
        this.setCurrentFavorisRepertoire(doc);
        return navigationContext.navigateToDocumentWithView(doc, "view_favoris_repertoire");
    }

    public DocumentModel getCurrentFavorisRepertoire() {
        return currentFavorisRepertoire;
    }

    public void setCurrentFavorisRepertoire(DocumentModel doc) {
        currentFavorisRepertoire = doc;
    }
}
