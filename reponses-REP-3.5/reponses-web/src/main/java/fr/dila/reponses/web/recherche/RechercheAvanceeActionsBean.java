package fr.dila.reponses.web.recherche;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.userworkspace.web.ejb.UserWorkspaceManagerActionsBean;
import org.nuxeo.ecm.webapp.contentbrowser.DocumentActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;

import fr.dila.reponses.api.constant.ReponsesActionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesContentView;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.recherche.RechercheUtils;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.suivi.SuiviActionsBean;
import fr.dila.ss.web.feuilleroute.DocumentRoutingWebActionsBean;
import fr.dila.st.api.constant.STQueryConstant;
import fr.dila.st.api.recherche.Recherche;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Un bean seam pour les traitements spécifiques à la recherche avancée.
 * 
 * @author jgomez
 */
@Name("rechercheAvanceeActions")
@Scope(ScopeType.CONVERSATION)
public class RechercheAvanceeActionsBean implements Serializable {

	private static final long							serialVersionUID	= 1L;

	private static final Log							log					= LogFactory
																					.getLog(RechercheAvanceeActionsBean.class);

	private Requete.INDEX_MODE							currentIndexationMode;

	@In(create = true)
	protected transient NavigationContextBean			navigationContext;

	@In(create = true)
	protected transient ActionManager					actionManager;

	@In(create = true, required = true)
	protected transient CoreSession						documentManager;

	@In(create = true)
	protected ContentViewActions						contentViewActions;

	@In(create = true)
	protected DocumentModel								currentRequete;

	@In(create = true, required = false)
	protected transient FacesMessages					facesMessages;

	@In(create = true, required = false)
	protected transient UserWorkspaceManagerActionsBean	userWorkspaceManagerActions;

	@In(create = true, required = false)
	protected transient RechercheWebActionsBean			rechercheWebActions;

	@In(create = true, required = false)
	protected transient SuiviActionsBean				suiviActions;

	@In(create = true, required = false)
	protected DocumentActions							documentActions;

	@In(create = true, required = false)
	protected transient CorbeilleActionsBean			corbeilleActions;

	@In(create = true, required = false)
	protected transient DocumentRoutingWebActionsBean	routingWebActions;

	@In(create = true)
	protected transient NavigationWebActionsBean		navigationWebActions;

	@In(create = true)
	protected transient RecherchePanneauxActionsBean	recherchePanneauxActions;

	@In(create = true)
	protected transient DocumentsListsManagerBean		documentsListsManager;

	private void initRecherche() throws ClientException {
		// Renseigne le menu du haut
		Action mainMenuAction = actionManager.getAction(ReponsesActionConstant.ESPACE_RECHERCHE);
		navigationWebActions.setCurrentMainMenuAction(mainMenuAction);
		// Déplie le panneau des résultats de recherche
		navigationWebActions.setUpperPanelIsOpened(true);
		navigationWebActions.setLeftPanelIsOpened(false);
		corbeilleActions.setCurrentView(ReponsesViewConstant.EDIT_REQUETE);
		// Renseigne la vue de route des étapes de feuille de route vers les dossiers
		routingWebActions.setFeuilleRouteView(ReponsesViewConstant.EDIT_REQUETE);
		navigationWebActions.setCurrentLeftMenuAction(null);
		navigationContext.resetCurrentDocument();
	}

	/**
	 * Navigue vers l'espace de recherche et édite les critères de recherche avancée.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String navigateToRechercheAvancee() throws ClientException {
		initRecherche();
		Requete requete = RechercheUtils.adaptRequete(currentRequete);
		requete.setQueryType(STQueryConstant.UFNXQL);
		requete.init();
		navigationContext.setChangeableDocument(currentRequete);
		return ReponsesViewConstant.EDIT_REQUETE;
	}

	/**
	 * Navigue vers l'espace de recherche avec les critères déjà entrés par l'utlisateur
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String editRechercheAvancee() throws ClientException {
		initRecherche();
		return ReponsesViewConstant.EDIT_REQUETE;
	}

	public List<Recherche> getRechercheList() throws Exception {
		final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
		return rechercheService.getRecherches();
	}

	public Recherche getRecherche(String rechercheName) throws Exception {
		final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
		Recherche recherche = rechercheService.getRecherche(rechercheName);
		return recherche;
	}

	/**
	 * Pré-traitement de la recherche avancée.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String goToResults() throws ClientException {
		navigationWebActions.setCurrentLeftMenuAction(null);
		contentViewActions.reset(ReponsesContentView.RECHERCHE_AVANCEE_CONTENT_VIEW);
		rechercheWebActions.setContentViewName(ReponsesContentView.RECHERCHE_AVANCEE_CONTENT_VIEW);
		Requete requete = currentRequete.getAdapter(Requete.class);
		requete.setIndexationMode(currentIndexationMode);
		requete.doBeforeQuery();

		final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
		log.info(rechercheService.getFullQuery(documentManager, requete));

		// Prepare la vue des requetes à la place d'une vue Mailbox
		// Renseigne la vue de route des étapes de feuille de route vers les dossiers
		routingWebActions.setFeuilleRouteView(ReponsesViewConstant.VIEW_REQUETE_RESULTS);
		corbeilleActions.setCurrentView(ReponsesViewConstant.VIEW_REQUETE_RESULTS);

		// Si la recherche ne retourne qu'un dossier, on l'ouvre directement
		rechercheWebActions.openOrResetSearchContext(ReponsesContentView.RECHERCHE_AVANCEE_CONTENT_VIEW);

		// Vide la liste de sélection
		documentsListsManager.resetWorkingList(ReponsesConstant.RECHERCHE_SELECTION);

		return corbeilleActions.getCurrentView();
	}

	public String goBack() throws ClientException {
		if (contentViewActions.getCurrentContentView() != null
				&& "suivi_alert_content".equals(contentViewActions.getCurrentContentView().getName())) {
			return suiviActions.navigateToEspaceSuivi();
		} else {
			return corbeilleActions.getCurrentView();
		}
	}

	/**
	 * Retourne la chaine de caractère représentant la requête.
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String getCurrentRequetePattern() throws ClientException {
		final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
		Requete requete = RechercheUtils.adaptRequete(currentRequete);
		String query = rechercheService.getFullQuery(documentManager, requete);
		if (log.isDebugEnabled()) {
			log.debug("Query :" + query);
		}
		return query;
	}

	/**
	 * Réinitialise le document model de la requete courante.
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String reset() throws ClientException {
		currentRequete.reset();
		Requete requete = RechercheUtils.adaptRequete(currentRequete);
		requete.init();
		return null;
	}

	/**
	 * Réinitialise la recherche et revient vers la page de création
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String navigateToNewSearch() {
		try {
			currentRequete.reset();
			recherchePanneauxActions.clearPanneaux();
			String recherche = navigateToRechercheAvancee();
			return recherche;
		} catch (Exception e) {
			facesMessages.add(StatusMessage.Severity.ERROR, "Une erreur est survenue dans la recherche");
			log.warn(e);
			return null;
		}
	}

	@Factory(value = "currentRequete")
	public DocumentModel getCurrentRequete() throws ClientException {
		final RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();

		Requete requete = rechercheService.getRequete(documentManager, "requete");
		if (requete == null) {
			facesMessages.add(StatusMessage.Severity.INFO, "La requete n'a pas pu être initialisée");
		} else {
			currentRequete = requete.getDocument();
		}
		return currentRequete;
	}

	public void setCurrentIndexationMode(Requete.INDEX_MODE mode) {
		this.currentIndexationMode = mode;
	}

	public Requete.INDEX_MODE getCurrentIndexationMode() {
		if (currentIndexationMode == null) {
			currentIndexationMode = Requete.INDEX_MODE.INDEX_ORIG;
		}
		return currentIndexationMode;
	}

}