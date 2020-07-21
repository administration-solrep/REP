package fr.dila.reponses.web.recherche;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.jsf.ContentView;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;

import fr.dila.cm.web.mailbox.CaseManagementMailboxTabsActionsBean;
import fr.dila.reponses.api.client.ReponseDossierListingDTO;
import fr.dila.reponses.api.constant.RechercheExportEventConstants;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.api.recherche.IdLabel;
import fr.dila.reponses.core.util.ExcelUtil;
import fr.dila.reponses.web.contentview.CorbeillePageProvider;
import fr.dila.reponses.web.contentview.RechercheResultPageProvider;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.dossier.DossierListingActionsBean;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SessionUtil;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.contentview.AbstractDTOPageProvider;
import fr.dila.st.web.context.NavigationContextBean;

@Name("rechercheWebActions")
@Scope(ScopeType.CONVERSATION)
public class RechercheWebActionsBean implements Serializable {
	/**
	 * 
	 */
	private static final long						serialVersionUID				= -9177286611995940577L;

	private static final Log						log								= LogFactory
																							.getLog(RechercheWebActionsBean.class);

	public static final String						VIEW_RECHERCHE_BANDEAU_SERVICE	= "view_recherche_bandeau_service";

	@In(create = true, required = true)
	protected ContentViewActions					contentViewActions;

	@In(create = true, required = false)
	protected transient FacesMessages				facesMessages;

	@In(create = true, required = false)
	protected CaseManagementMailboxTabsActionsBean	cmMailboxTabsActionsBean;

	@In(create = true, required = true)
	protected transient ActionManager				actionManager;

	@In(create = true)
	protected transient NavigationWebActionsBean	navigationWebActions;

	@In(create = true, required = false)
	protected transient NavigationContextBean		navigationContext;

	@In(create = true, required = false)
	protected transient DossierListingActionsBean	dossierListingActions;

	@In(create = true, required = true)
	protected transient CorbeilleActionsBean		corbeilleActions;

	private String									contentViewName					= "";

	protected Action								leftMenuAction;

	/**
	 * Change la vue mailbox (en haut), par la vue des résultats de recherche.
	 * 
	 * @throws ClientException
	 */
	public void changeMailboxViewToSearch() throws ClientException {
		cmMailboxTabsActionsBean.setCurrentViewMailboxAction(ReponsesViewConstant.TEMPLATE_REQUETE);
	}

	public void setContentViewName(String contentViewName) {
		this.contentViewName = contentViewName;
	}

	public String getContentViewName() {
		return contentViewName;
	}

	/**
	 * Retourne l'action qui affiche la mini-vue de la recherche simple dans le bandeau du haut.
	 * 
	 * @return L'action de la recherche simple
	 */
	public Action getRechercheBandeauServiceAction() {
		return actionManager.getAction(VIEW_RECHERCHE_BANDEAU_SERVICE);
	}

	/**
	 * Retourne l'action pour le menu de gauche (pour l'instant l'action favoris de dossier)
	 * 
	 * @return Menu de gauche
	 */
	public Action getLeftMenuAction() {
		return actionManager.getAction(ReponsesConstant.LEFT_MENU_FAVORIS_DOSSIER_ACTION);
	}

	public List<Action> getLeftMenuActions() {
		return actionManager.getAllActions(ReponsesConstant.LEFT_MENU_RECHERCHE_CATEGORY);
	}

	/**
	 * Set l'action pour le menu de gauche
	 * 
	 * @param leftMenuAction
	 *            l'action du menu de gauche
	 */
	public void setMenuAction(Action leftMenuAction) {
		this.leftMenuAction = leftMenuAction;
	}

	/**
	 * Initialise le contexte de documents courants après une recherche. Si la recherche retourne un seul dossier, ce
	 * dossier est ouvert; sinon, le dossier courant est fermé.
	 * 
	 * @throws ClientException
	 */
	public void openOrResetSearchContext(String contentViewName) throws ClientException {
		corbeilleActions.setCurrentDossierLink(null);
		navigationContext.resetCurrentDocument();

		ContentView contentView = contentViewActions.getContentViewWithProvider(contentViewName);
		RechercheResultPageProvider pageProvider = (RechercheResultPageProvider) contentView.getCurrentPageProvider();
		List<Map<String, Serializable>> currentPage = null;
		try {
			currentPage = pageProvider.getCurrentPage(); // Exécute la requête
		} catch (ClientRuntimeException e) {
			log.error("Une erreur est survenue", e);
			facesMessages
					.add(StatusMessage.Severity.ERROR,
							"Une erreur est survenue dans la recherche. Veuillez vérifier et affiner vos critères de recherche.");
		} finally {
			if (currentPage != null && currentPage.size() == 1) {
				ReponseDossierListingDTO dossierDto = (ReponseDossierListingDTO) currentPage.get(0);
				IdLabel[] caseLinkIds = dossierDto.getCaseLinkIdsLabels();
				if (caseLinkIds == null || caseLinkIds.length == 0) {
					dossierListingActions.navigateToDossier(dossierDto.getDossierId());
				} else if (caseLinkIds.length == 1) {
					dossierListingActions.navigateToDossierLink(dossierDto.getDossierId(), caseLinkIds[0].getId());
				}
			}
		}
	}

	public long getTotalResult() throws ClientException {
		ContentView contentView = contentViewActions.getContentViewWithProvider(contentViewName);
		RechercheResultPageProvider pageProvider = (RechercheResultPageProvider) contentView.getCurrentPageProvider();
		long totalResult = pageProvider.getResultsCount();
		log.debug("Nombre d'éléments à exporter " + totalResult);
		return totalResult;
	}

	public ContentView getContentView() throws ClientException {
		return contentViewActions.getContentViewWithProvider(contentViewName);
	}

	public void createExportExcel() throws ClientException {

		ContentView contentView = contentViewActions.getContentViewWithProvider(contentViewName);
		RechercheResultPageProvider pageProvider = (RechercheResultPageProvider) contentView.getCurrentPageProvider();

		CoreSession session = SessionUtil.getCoreSession();
		SSPrincipal user = (SSPrincipal) session.getPrincipal();

		if (StringUtil.isBlank(user.getEmail())) {
			facesMessages.add(StatusMessage.Severity.ERROR,
					"Veuillez renseigner votre mail dans votre profil pour recevoir le mail d'export");
		} else {

			final EventProducer eventProducer = STServiceLocator.getEventProducer();
			final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();

			eventProperties.put(RechercheExportEventConstants.PARAM_MAIL, user.getEmail());
			eventProperties.put(RechercheExportEventConstants.PARAM_QUERY, pageProvider.getCurrentQuery());
			eventProperties.put(RechercheExportEventConstants.PARAM_SESSION, (Serializable) session);

			final InlineEventContext eventContext = new InlineEventContext(session, user, eventProperties);
			eventProducer.fireEvent(eventContext.newEvent(RechercheExportEventConstants.EVENT_NAME));
			facesMessages.add(StatusMessage.Severity.INFO, "La demande d'export a été prise en compte.");
		}

	}

	protected static HttpServletResponse getHttpServletResponse() {
		ServletResponse response = null;
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			response = (ServletResponse) facesContext.getExternalContext().getResponse();
		}
		if (response != null && response instanceof HttpServletResponse) {
			return (HttpServletResponse) response;
		}
		return null;
	}

	/**
	 * Exporte le gouvernement dans un fichier Excel .xls
	 */
	public void exportResultSelection() {
		HttpServletResponse response = getHttpServletResponse();
		if (response == null) {
			return;
		}

		try {

			CoreSession session = SessionUtil.getCoreSession();

			ContentView contentView = contentViewActions.getCurrentContentView();

			AbstractDTOPageProvider pageProvider = (AbstractDTOPageProvider) contentView.getCurrentPageProvider();
			List<Map<String, Serializable>> lstDataSelected = null;

			if (pageProvider instanceof RechercheResultPageProvider) {
				lstDataSelected = ((RechercheResultPageProvider) pageProvider).getAllItemsSelected();
			} else if (pageProvider instanceof CorbeillePageProvider) {
				lstDataSelected = ((CorbeillePageProvider) pageProvider).getAllItemsSelected();
			}

			if (lstDataSelected != null && !lstDataSelected.isEmpty()) {
				DataSource fichier = ExcelUtil.creationExportResultExcel(session, lstDataSelected);

				if (fichier != null) {
					InputStream inputStream = fichier.getInputStream();
					response.reset();
					response.setContentType("application/vnd.ms-excel;charset=utf-8");

					response.addHeader("Content-Disposition", "attachment; filename=\"export_dossier.xls\"");

					OutputStream outputStream = response.getOutputStream();
					BufferedInputStream fif = new BufferedInputStream(inputStream);
					// copie le fichier dans le flux de sortie
					int data;
					while ((data = fif.read()) != -1) {
						outputStream.write(data);
					}
					outputStream.flush();
					outputStream.close();

					FacesContext.getCurrentInstance().responseComplete();
				} else {
					facesMessages.add(StatusMessage.Severity.ERROR,
							"Un souci est survenu lors de la génération du fichier excel");
				}
			} else {
				facesMessages.add(StatusMessage.Severity.ERROR,
						"Un souci est survenu lors de la récupération des données à exporter");
			}
		} catch (Exception e) {
			log.error("Une erreur est survenue lors de la génération du fichier excel", e);
			facesMessages.add(StatusMessage.Severity.ERROR,
					"Un souci est survenu lors de la génération du fichier excel");
		}
	}
}
