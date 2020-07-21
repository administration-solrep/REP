package fr.dila.reponses.web.fonddossier;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.richfaces.component.UITree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.api.fonddossier.FondDeDossierFile;
import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.reponses.api.service.FondDeDossierService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.tree.SSTreeNode;

/**
 * Classe de gestion de l'arbre contenant l'arborescence du fond de dossier.
 * 
 * @author ARN
 */
@Name("fondDeDossierTree")
@Scope(ScopeType.SESSION)
public class FondDeDossierTreeBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    private TreeNode<SSTreeNode> rootNode = null;

    private String dossierIdentifier;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    @In(required = true, create = true)
    protected transient SSPrincipal ssPrincipal;

    private static final Log log = LogFactory.getLog(FondDeDossierTreeBean.class);

    /**
     * charge l'arbre contenant l'arborescence du fond de dossier.
     * 
     * @throws ClientException
     */
    private void loadTree(CoreSession session, DocumentModel fddDocument) throws ClientException {

        // get nuxeo baseUrl : used to send WSS adress to all the child
        // String baseUrl = BaseURL.getBaseURL();

        if (fddDocument == null || fddDocument.getId() == null) {
            throw new ClientException("fond de dossier not found");
        }

        rootNode = new TreeNodeImpl<SSTreeNode>();

        // on récupère les répertoire qui composent la racine du fond de dossier
        final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
        List<FondDeDossierFolder> repertoiresRacine = fondDeDossierService.getVisibleChildrenFolder(session, fddDocument, ssPrincipal);

        Set<String> ministereIds = new HashSet<String>();
        if (ssPrincipal != null) {
            // on récupère le ministère d'appartenance de l'utilisateur
            ministereIds = ssPrincipal.getMinistereIdSet();
        }

        int counter = 1;
        // on parcourt les éléments du fond de dossier en partant des répertoires
        for (SSTreeNode elementFddNoeud : repertoiresRacine) {

            TreeNodeImpl<SSTreeNode> nodeImpl = new TreeNodeImpl<SSTreeNode>();
            nodeImpl.setData(elementFddNoeud);

            addFondDossierElement(elementFddNoeud.getId(), nodeImpl, session, ministereIds);

            rootNode.addChild(counter, nodeImpl);
            counter++;
        }

    }

    /**
     * Méthode récursive qui ajoute les fils d'un noeud
     * 
     * @param documentId nom du document contenant les documents fils
     * @param node noeud dans lequel les sous-groupes sont ajoutés
     * @throws ClientException
     */
    @SuppressWarnings("unchecked")
	private void addFondDossierElement(String documentId, TreeNodeImpl<SSTreeNode> node, CoreSession session, Set<String> ministereIds)
            throws ClientException {
        
        final FondDeDossierService fondDeDossierService = ReponsesServiceLocator.getFondDeDossierService();
        List<FondDeDossierFile> childs = (List<FondDeDossierFile>) fondDeDossierService.getChildrenFile(session, session.getDocument(new IdRef(documentId)));

        if (childs != null && !childs.isEmpty()) {
            int counter = 1;
            for (FondDeDossierFile childGroup : childs) {
                boolean isVisible = true;
                if (childGroup.getNiveauVisibilite().equals(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_MINISTERE_LABEL)) {
                    String ministereId = childGroup.getMinistereAjoute();
                    isVisible = ministereIds.contains(ministereId);
                }
                if (childGroup.isDeleted()) {
                    isVisible = false;
                }
                
                if (isVisible) {
                    TreeNodeImpl<SSTreeNode> nodeImpl = new TreeNodeImpl<SSTreeNode>();
                    nodeImpl.setData(childGroup);
                    addFondDossierElement(childGroup.getId(), nodeImpl, session, ministereIds);
                    node.addChild(counter, nodeImpl);
                    counter++;
                }

            }
        }
    }

    /**
     * Méthode qui renvoie l'arborescence complète du fond de dossier.
     * 
     * @return l'arborescence complète du fond de dossier chargé
     * @throws ClientException
     */
    public TreeNode<SSTreeNode> getFondDeDossier() throws ClientException {
        String currentDossierId = getCurrentDossierId();
        if (rootNode == null || !currentDossierId.equals(dossierIdentifier)) {
            if (log.isDebugEnabled()) {
                log.debug("tree reloaded ");
            }
            dossierIdentifier = currentDossierId;
            loadTree(documentManager, getFondDeDossierDocument());
        }
        return rootNode;
    }

    /**
     * Get the current Dossier from context.
     * 
     */
    public String getCurrentDossierId() {
        String idDossier = null;
        DocumentModel doc = navigationContext.getCurrentDocument();
        if (doc != null && doc.getId() != null) {
            idDossier = doc.getId();
        }
        return idDossier;
    }

    public String getDossierIdentifier() {
        return dossierIdentifier;
    }

    public void setDossierIdentifier(String dossierIdentifier) {
        this.dossierIdentifier = dossierIdentifier;
    }

    @Observer(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_TREE_CHANGED_EVENT)
    public void reloadFondDeDossierTree() throws ClientException {
        if (log.isDebugEnabled()) {
            log.debug("reload fond de dossier tree");
        }
        loadTree(documentManager, getFondDeDossierDocument());
    }

    /**
     * Get the current FondDeDossierDocument from Dossier.
     * 
     */
    @Factory(value = "fondDeDossierDocument", scope = EVENT)
    public DocumentModel getFondDeDossierDocument() {
        // the Dossier document is the current Document
        DocumentModel doc = navigationContext.getCurrentDocument();
        Dossier dossier = doc.getAdapter(Dossier.class);
        FondDeDossier fondDeDossier = dossier.getFondDeDossier(documentManager);
        if (fondDeDossier != null) {
	        DocumentModel fondDeDossierDocument = fondDeDossier.getDocument();
	        if (log.isDebugEnabled()) {
	            log.debug("get FondDeDossierDocument doc : id = " + fondDeDossierDocument.getId());
	        }
	        return fondDeDossierDocument;
        }
        return null;
    }

    /**
     * method used to auto expend the fond de dossier tree.
     * 
     */
    public Boolean adviseNodeOpened(UITree tree) {
        return Boolean.TRUE;
    }
}
