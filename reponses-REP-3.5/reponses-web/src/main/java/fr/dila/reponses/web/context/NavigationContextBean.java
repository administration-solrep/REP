package fr.dila.reponses.web.context;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentLocation;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.ui.web.util.DocumentLocator;
import org.nuxeo.ecm.platform.util.RepositoryLocation;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesActionConstant;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.dossier.DossierListingActionsBean;
import fr.dila.reponses.web.recherche.RechercheSimpleActionsBean;
import fr.dila.st.web.action.NavigationWebActionsBean;

/**
 * Surcharge du NavigationContextBean de Socle transverse.
 * 
 * @author asatre
 */
@Name("navigationContext")
@Scope(CONVERSATION)
@Install(precedence = FRAMEWORK + 2)
public class NavigationContextBean extends fr.dila.st.web.context.NavigationContextBean {
	/**
	 * Serial version UID.
	 */
	private static final long						serialVersionUID	= 1356073411805191773L;

	private static final Log						log					= LogFactory
																				.getLog(NavigationContextBean.class);

	@In(create = true, required = false)
	protected transient RechercheSimpleActionsBean	rechercheSimpleActions;

	@In(create = true, required = false)
	protected transient DossierListingActionsBean	dossierListingActions;

	@In(create = true, required = false)
	protected transient CorbeilleActionsBean		corbeilleActions;

	@In(create = true, required = false)
	protected transient NavigationWebActionsBean	navigationWebActions;

	@RequestParameter
	String											docRef;

	private enum MenuAnchor {
		ESPACE_TRAVAIL(ReponsesActionConstant.ESPACE_TRAVAIL, "reponses_espaceDeTravail"), ESPACE_PLAN_CLASSEMENT(
				ReponsesActionConstant.ESPACE_PLAN_CLASSEMENT, "reponses_planDeClassement"), ESPACE_RECHERCHE(
				ReponsesActionConstant.ESPACE_RECHERCHE, "reponses_recherche"), ESPACE_SUIVI(
				ReponsesActionConstant.ESPACE_SUIVI, "reponses_suivi"), ESPACE_STATISTIQUES(
				ReponsesActionConstant.ESPACE_STATISTIQUES, "reponses_statistiques"), ESPACE_ADMINISTRATION(
				ReponsesActionConstant.ESPACE_ADMINISTRATION, "reponses_administration");

		private String	menu;
		private String	anchor;

		MenuAnchor(String menu, String anchor) {
			this.menu = menu;
			this.anchor = anchor;
		}

		public static MenuAnchor getByMenu(String menu) {
			for (MenuAnchor menuAnchor : MenuAnchor.values()) {
				if (menuAnchor.menu.equals(menu)) {
					return menuAnchor;
				}
			}
			return null;
		}

		public String getAnchor() {
			return anchor;
		}

	}

	// start a new conversation if needed, join main if possible
	@Begin(id = "#{conversationIdGenerator.currentOrNewMainConversationId}", join = true)
	public String navigateToDocumentURL() throws ClientException {
		try {
			final DocumentLocation docLoc;

			docLoc = DocumentLocator.parseDocRef(docRef);
			RepositoryLocation repLoc = new RepositoryLocation(docLoc.getServerName());
			// création de la session si elle existe pas
			if (!repLoc.equals(getCurrentServerLocation())) {
				setCurrentServerLocation(repLoc);
			}

			DocumentModel documentModel = documentManager.getDocument(docLoc.getDocRef());
			if (documentModel != null && documentModel.hasSchema(CaseLinkConstants.CASE_LINK_SCHEMA)) {
				DossierLink dossierLink = documentModel.getAdapter(DossierLink.class);
				DocumentModel dossierDoc = documentManager.getDocument(new IdRef(dossierLink.getDossierId()));
				rechercheSimpleActions.setSimpleSearch(dossierDoc.getAdapter(Dossier.class).getNumeroQuestion()
						.toString());
				rechercheSimpleActions.navigateToRechercheSimple();
				dossierListingActions.navigateToDossierLink(dossierDoc, documentModel);
				return corbeilleActions.getCurrentView();
			} else if (documentModel != null && documentModel.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
				Dossier dossier = documentModel.getAdapter(Dossier.class);
				rechercheSimpleActions.setSimpleSearch(dossier.getNumeroQuestion().toString());
				rechercheSimpleActions.navigateToRechercheSimple();
				dossierListingActions.navigateToDossier(dossier.getDocument());
				return corbeilleActions.getCurrentView();
			}
		} catch (Exception e) {
			log.error("docRef error : " + docRef, e);
		}

		return corbeilleActions.navigateToEspaceTravail();
	}

	public String getAnchor() {
		Action currentMainMenuAction = navigationWebActions.getCurrentMainMenuAction();
		if (currentMainMenuAction != null) {
			MenuAnchor menuAnchor = MenuAnchor.getByMenu(currentMainMenuAction.getId());
			if (menuAnchor != null) {
				return "#" + menuAnchor.getAnchor();
			}
		}
		return "";
	}
}
