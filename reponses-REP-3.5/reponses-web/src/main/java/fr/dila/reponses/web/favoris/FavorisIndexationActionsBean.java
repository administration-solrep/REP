package fr.dila.reponses.web.favoris;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.userworkspace.web.ejb.UserWorkspaceManagerActionsBean;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.favoris.FavorisIndexation;
import fr.dila.reponses.web.corbeille.PlanClassementTreeBean;
import fr.dila.reponses.web.corbeille.PlanClassementTreeNiveau2Node;

@Name("favorisIndexationActions")
@Scope(ScopeType.CONVERSATION)
public class FavorisIndexationActionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create = true, required = false)
    protected transient PlanClassementTreeBean planClassementTree;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient FavorisIndexationTreeBean favorisIndexationTree;

    @In(create = true, required = false)
    protected transient UserWorkspaceManagerActionsBean userWorkspaceManagerActions;

    public void addFavoris(PlanClassementTreeNiveau2Node item) throws ClientException {
        DocumentModel favorisIndexationRoot = userWorkspaceManagerActions.getCurrentUserPersonalWorkspace();
        DocumentModel favorisDoc = documentManager.createDocumentModel("FavorisIndexation");
        String name = planClassementTree.getTreeMode() + "-" + item.getNiveau1Indexation() + "-" + item.getIndexation();
        favorisDoc.setPathInfo(favorisIndexationRoot.getPathAsString() + "/" + ReponsesSchemaConstant.INDEXATION_ROOT_FOLDER, name);
        FavorisIndexation favoris = favorisDoc.getAdapter(FavorisIndexation.class);
        favoris.setTypeIndexation(planClassementTree.getTreeMode());
        favoris.setNiveau1(item.getNiveau1Indexation());
        favoris.setNiveau2(item.getIndexation());
        documentManager.createDocument(favorisDoc);
        documentManager.saveDocument(favorisDoc);
        favorisIndexationTree.refresh();
    }

    public PlanClassementTreeNiveau2Node getNodeFromFavoris(FavorisIndexation favoris) {
        PlanClassementTreeNiveau2Node node = new PlanClassementTreeNiveau2Node(favoris.getNiveau2(), favoris.getNiveau2(), favoris.getNiveau1(), "0");
        return node;
    }

    public void removeFavoris(FavorisIndexation favoris) throws ClientException {
        documentManager.removeDocument(favoris.getDocument().getRef());
    }

    public void removeFavoris(String idFavoris) throws ClientException {
        documentManager.removeDocument(new IdRef(idFavoris));
        favorisIndexationTree.refresh();
    }
}
