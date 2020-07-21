package fr.dila.reponses.web.favoris;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.userworkspace.web.ejb.UserWorkspaceManagerActionsBean;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeExpandedEvent;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.favoris.FavorisIndexation;
import fr.dila.reponses.web.favoris.FavorisIndexationTreeNode.TypeIndexation;

/**
 * Classe de gestion de l'arbre des favoris d'indexation.
 * 
 * @author asatre
 */
@Name("favorisIndexationTree")
@Scope(ScopeType.CONVERSATION)
public class FavorisIndexationTreeBean implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4670517998568338934L;

    private List<FavorisIndexationTreeNode> root;

    private String favorisIndexationRootId;

    @In(create = true, required = false)
    protected transient UserWorkspaceManagerActionsBean userWorkspaceManagerActions;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    public Boolean adviseNodeSelected(UITree treeComponent) {
        return Boolean.FALSE;
    }

    public Boolean adviseNodeOpened(UITree treeComponent) {
        Object value = treeComponent.getRowData();
        if (value instanceof FavorisIndexationTreeNode) {
            FavorisIndexationTreeNode minNode = (FavorisIndexationTreeNode) value;
            return minNode.isOpened();
        }
        return null;
    }

    public void changeExpandListener(NodeExpandedEvent event) {
        UIComponent component = event.getComponent();
        if (component instanceof UITree) {
            UITree treeComponent = (UITree) component;
            Object value = treeComponent.getRowData();
            if (value instanceof FavorisIndexationTreeNode) {
                FavorisIndexationTreeNode minNode = (FavorisIndexationTreeNode) value;
                minNode.setOpened(!minNode.isOpened());
            }
        }
    }

    public String findFavorisIndexationRootId() throws ClientException {
        if (favorisIndexationRootId == null) {
            DocumentModel personalWorkspace = userWorkspaceManagerActions.getCurrentUserPersonalWorkspace();
            List<DocumentModel> children = documentManager.getChildren(personalWorkspace.getRef(), ReponsesSchemaConstant.INDEXATION_ROOT_TYPE);
            if (children == null || children.isEmpty()) {
                throw new ClientException("FavorisIndexationRoot introuvable dans l'espace utilisateur");
            } else if (children.size() > 1) {
                throw new ClientException("FavorisIndexationRoot : plus d'un trouvé dans l'espace utilisateur");
            } else {
                favorisIndexationRootId = children.get(0).getId();
            }
        }
        return favorisIndexationRootId;
    }

    public List<FavorisIndexationTreeNode> getFavoris() throws ClientException {
        if (root == null) {
            String favorisIndexationRootId = findFavorisIndexationRootId();

            Map<String, List<FavorisIndexation>> mapAN = new HashMap<String, List<FavorisIndexation>>();
            Map<String, List<FavorisIndexation>> mapSE = new HashMap<String, List<FavorisIndexation>>();

            DocumentModelList favorisDocList = documentManager.getChildren(new IdRef(favorisIndexationRootId),
                    ReponsesSchemaConstant.INDEXATION_DOCUMENT_TYPE);
            for (DocumentModel favorisDoc : favorisDocList) {
                FavorisIndexation favorisIndexation = favorisDoc.getAdapter(FavorisIndexation.class);
                if (FavorisIndexation.TypeIndexation.AN.name().equals(favorisIndexation.getTypeIndexation())) {
                    if (mapAN.get(favorisIndexation.getNiveau1()) == null) {
                        mapAN.put(favorisIndexation.getNiveau1(), new ArrayList<FavorisIndexation>());
                    }
                    mapAN.get(favorisIndexation.getNiveau1()).add(favorisIndexation);
                }
                if (FavorisIndexation.TypeIndexation.Senat.name().equals(favorisIndexation.getTypeIndexation())) {
                    if (mapSE.get(favorisIndexation.getNiveau1()) == null) {
                        mapSE.put(favorisIndexation.getNiveau1(), new ArrayList<FavorisIndexation>());
                    }
                    mapSE.get(favorisIndexation.getNiveau1()).add(favorisIndexation);
                }
            }

            FavorisIndexationTreeNode rootAN = new FavorisIndexationTreeNode(TypeIndexation.ROOT, "Assemblée nationale");
            for (Entry<String, List<FavorisIndexation>> niveau1 : mapAN.entrySet()) {
                rootAN.getChildren().add(new FavorisIndexationTreeNode(TypeIndexation.AN, niveau1.getKey(), niveau1.getValue()));
            }

            FavorisIndexationTreeNode rootSENAT = new FavorisIndexationTreeNode(TypeIndexation.ROOT, "Sénat");
            for (Entry<String, List<FavorisIndexation>> niveau1 : mapSE.entrySet()) {
                rootSENAT.getChildren().add(new FavorisIndexationTreeNode(TypeIndexation.SE, niveau1.getKey(), niveau1.getValue()));
            }

            root = new ArrayList<FavorisIndexationTreeNode>();
            root.add(rootAN);
            root.add(rootSENAT);
        }

        return root;
    }

    public void refresh() throws ClientException {
        root = null;
        getFavoris();
    }

}
