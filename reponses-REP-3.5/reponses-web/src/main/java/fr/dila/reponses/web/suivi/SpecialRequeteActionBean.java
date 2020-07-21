package fr.dila.reponses.web.suivi;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.model.SelectDataModel;
import org.nuxeo.ecm.platform.ui.web.model.SelectDataModelListener;
import org.nuxeo.ecm.platform.ui.web.model.impl.SelectDataModelRowEvent;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.recherche.ReponsesSelectDataModelImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * WebBean permettant de gérer les requêtes spéciales (c'est à dire trop compliquées pour être gérées comme des requêtes du requeteur).
 * 
 * @author jgomez
 */
@Name("specialRequeteActions")
@Scope(ScopeType.CONVERSATION)
public class SpecialRequeteActionBean implements Serializable, SelectDataModelListener {

    private static final String SMART_FOLDER = "SmartFolder";

    private static final String REQUETE_HISTORIQUE_VIEW = "/requeteExperte/documents_requeteHistorique.xhtml";

    private static final String REQUETE_EXPERTE_VIEW = "/requeteExperte/documents_requeteExperte.xhtml";

    private static final String REQUETE_HISTORIQUE = "historique";

    private static final long serialVersionUID = 1L;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = true)
    protected transient NavigationContextBean navigationContext;

    @In(create = true)
    protected transient NavigationWebActionsBean navigationWebActions;

    @In(create = true, required = true)
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(required = false, create = true)
    protected transient DocumentsListsManager documentsListsManager;

    @In(required = true, create = true)
    protected transient SSPrincipal ssPrincipal;

    private String lastView;

    /**
     * Retourne l'historique des dossiers validés pendant les 7 derniers jours pour le poste courant.
     * 
     * @return La liste des dossiers sont forme de document model
     * @throws ClientException
     */
    @Factory(value = "historiqueValidationsDossier", scope = EVENT)
    public SelectDataModel getHistoryValidatedDossiers() throws ClientException {

        Set<String> postesIdSet = ssPrincipal.getPosteIdSet();
        FeuilleRouteService fdrService = ReponsesServiceLocator.getFeuilleRouteService();
        List<Dossier> dossiers = fdrService.getLastWeekValidatedDossiers(documentManager, postesIdSet);
        List<DocumentModel> questions = new ArrayList<DocumentModel>();
        for (Dossier dossier : dossiers) {
            questions.add(dossier.getQuestion(documentManager).getDocument());
        }
        List<DocumentModel> selectedDocuments = documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
        SelectDataModel model = new ReponsesSelectDataModelImpl("last_validated_dossiers", questions, selectedDocuments);
        model.addSelectModelListener(this);
        return model;
    }

    @Override
    public void processSelectRowEvent(SelectDataModelRowEvent event) {
        Boolean selection = event.getSelected();
        DocumentModel data = (DocumentModel) event.getRowData();
        if (selection) {
            documentsListsManager.addToWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION, data);
        }
    }

    /***
     * Retourne la vue de résultats pour une requête. Pour l'instant, la distinction est seulement nécessaire pour la requête 'historique'
     * 
     * @param requete
     * @return
     */
    public String getResultsView() {
        DocumentModel requete = navigationContext.getCurrentDocument();
        if (requete == null) {
            setLastView(REQUETE_EXPERTE_VIEW);
            return getLastView();
        }
        if (isRequete(requete)) {
            if (!isRequeteHistorique(requete)) {
                setLastView(REQUETE_EXPERTE_VIEW);
            } else {
                setLastView(REQUETE_HISTORIQUE_VIEW);
            }
        }
        return getLastView();
    }

    private boolean isRequeteHistorique(DocumentModel requete) {
        return requete != null && REQUETE_HISTORIQUE.equals(requete.getName());
    }

    private boolean isRequete(DocumentModel requete) {
        return requete != null && SMART_FOLDER.equals(requete.getType());
    }

    public void setLastView(String lastView) {
        this.lastView = lastView;
    }

    public String getLastView() {
        return lastView;
    }
}
