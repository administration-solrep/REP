package fr.dila.reponses.web.corbeille;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeExpandedEvent;
import org.richfaces.event.NodeSelectedEvent;

import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesContentView;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.core.recherche.ReponsesMinimalEscaper;
import fr.dila.reponses.web.planclassement.PlanClassementActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Classe de gestion de l'arbre du plan de classement.
 * 
 * @author bgamard
 */
@Name("planClassementTree")
@Scope(ScopeType.CONVERSATION)
public class PlanClassementTreeBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -4678375625381932698L;

    /**
     * Mode d'affichage de l'arbre (AN ou Senat)
     */
    private String treeMode = "AN";

    private String niveau1;

    private String niveau2;

    private List<PlanClassementTreeNiveau1Node> niveau1Nodes;

    // private static final Log log = LogFactory.getLog(PlanClassementTreeBean.class);

    @In(create = true, required = false)
    protected transient ContentViewActions contentViewActions;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient PlanClassementActionsBean planClassementActions;

    @In(create = true)
    protected transient DocumentsListsManager documentsListsManager;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    /**
     * charge l'arbre du plan de classement
     * 
     * @throws ClientException
     */
    private void loadTree() throws ClientException {
        niveau1Nodes = new ArrayList<PlanClassementTreeNiveau1Node>();

        // Ajout des ministères à l'arbre
        niveau1Nodes.addAll(planClassementActions.getPlanClassementNiveau1(treeMode));
    }

    public Boolean adviseNodeSelected(UITree treeComponent) {
        return Boolean.FALSE;
    }

    public Boolean adviseNodeOpened(UITree treeComponent) {
        Object value = treeComponent.getRowData();
        if (value instanceof PlanClassementTreeNiveau1Node) {
            PlanClassementTreeNiveau1Node minNode = (PlanClassementTreeNiveau1Node) value;
            return minNode.isOpened();
        }
        return null;
    }

    /**
     * Méthode qui renvoie l'arbre des corbeilles complet
     * 
     * @return l'arbre chargé
     * @throws ClientException
     */
    public List<PlanClassementTreeNiveau1Node> getCorbeille() throws ClientException {
        if (niveau1Nodes == null) {
            loadTree();
        }
        return niveau1Nodes;
    }

    public void forceRefresh() throws ClientException {
        niveau1Nodes = null;
        getCorbeille();
    }

    public void changeExpandListener(NodeExpandedEvent event) throws ClientException {
        UIComponent component = event.getComponent();
        if (component instanceof UITree) {
            UITree treeComponent = (UITree) component;
            Object value = treeComponent.getRowData();
            if (value instanceof PlanClassementTreeNiveau1Node) {
                PlanClassementTreeNiveau1Node minNode = (PlanClassementTreeNiveau1Node) value;
                addChildrenNodes(minNode);
                minNode.setOpened(!minNode.isOpened());
            }
        }
    }

    public void nodeSelectListener(NodeSelectedEvent event) throws ClientException {
        UIComponent component = event.getComponent();
        if (component instanceof UITree) {
            UITree treeComponent = (UITree) component;
            Object value = treeComponent.getRowData();
            if (value instanceof PlanClassementTreeNiveau1Node) {
                PlanClassementTreeNiveau1Node minNode = (PlanClassementTreeNiveau1Node) value;
                addChildrenNodes(minNode);
                minNode.setOpened(!minNode.isOpened());
            }
        }
    }

    /**
     * Ajout des noeuds de niveau 2 à ceux de niveau 1
     * 
     * @param treeNode
     * @throws ClientException
     */
    private void addChildrenNodes(PlanClassementTreeNiveau1Node niveau1Node) throws ClientException {

        if (niveau1Node.isLoaded()) {
            return;
        }

        niveau1Node.clearNiveaux2();

        // récupère la liste des dossierLink du ministères, appartenant à l'utilisateur connecté
        String indexation = niveau1Node.getIndexation();

        Map<String, Integer> niveau2Comptes = planClassementActions.getPlanClassementNiveau2(treeMode, indexation);

        // création des noeuds
        for (Entry<String, Integer> entry : niveau2Comptes.entrySet()) {
            niveau1Node.addNiveau2(entry.getKey(), entry.getValue().toString());
        }

        niveau1Node.setLoaded(true);
    }

    public String getFNXQLPart() {
        if (niveau1 == null || niveau2 == null) {
            // TODO : mieux
            return "q.ecm:uuid=\"0\"";
        }

        ReponsesMinimalEscaper escaper = new ReponsesMinimalEscaper();
        String niveau1escaped = escaper.escape(niveau1);
        String niveau2escaped = escaper.escape(niveau2);

        if (treeMode.equals("AN")) {
            return " (q.ixa:AN_rubrique=\"" + niveau1escaped + "\" OR q.ixacomp:AN_rubrique=\"" + niveau1escaped + "\") AND (q.ixa:TA_rubrique=\""
                    + niveau2escaped + "\" OR q.ixacomp:TA_rubrique=\"" + niveau2escaped + "\")";
        }
        if (treeMode.equals("Senat")) {
            String field = "SE_rubrique";
            if (!niveau1.equals("Rubrique")) {
                field = "SE_theme";
            }
            return " (q.ixa:" + field + "=\"" + niveau2escaped + "\" OR q.ixacomp:" + field + "=\"" + niveau2escaped + "\")";
        }

        return "";
    }

    @Observer("corbeilleChanged")
    public String setContext(PlanClassementTreeNiveau2Node item) throws ClientException {

        niveau1 = item.getNiveau1Indexation();
        niveau2 = item.getIndexation();

        // vide le cache du contentView pour ré-executer la requête
        if (contentViewActions != null) {
            contentViewActions.reset(ReponsesContentView.PLAN_CLASSEMENT_CONTENT_VIEW);
        }

        // Vide la liste de selection
        documentsListsManager.resetWorkingList(ReponsesConstant.CORBEILLE_SELECTION);

        return planClassementActions.navigateToEspacePlanClassement();
    }

    public String setContext(String niveau1, String niveau2) throws ClientException {

        this.niveau1 = niveau1;
        this.niveau2 = niveau2;

        // vide le cache du contentView pour ré-executer la requête
        if (contentViewActions != null && contentViewActions.getCurrentContentView() !=null) {
            contentViewActions.reset(contentViewActions.getCurrentContentView().getName());
        }

        // Décharge le dossier courant
        navigationContext.resetCurrentDocument();

        return ReponsesViewConstant.PLAN_CLASSEMENT_VIEW;
    }

    public String getTreeMode() {
        return treeMode;
    }

    public String getNiveau1() {
        return niveau1;
    }

    public String getNiveau2() {
        return niveau2;
    }

    public void setTreeMode(String treeMode) throws ClientException {
        this.treeMode = treeMode;
        forceRefresh();
    }
}
