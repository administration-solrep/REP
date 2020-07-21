package fr.dila.reponses.web.corbeille;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeExpandedEvent;
import org.richfaces.event.NodeSelectedEvent;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesContentView;
import fr.dila.reponses.api.mailbox.PreComptage;
import fr.dila.reponses.api.mailbox.ReponsesMailbox;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.CorbeilleService;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.organigramme.ProtocolarOrderComparator;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Classe de gestion de l'arbre des corbeilles.
 * 
 * @author bgamard
 */
@Name("corbeilleTree")
@Scope(ScopeType.CONVERSATION)
public class CorbeilleTreeBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long					serialVersionUID	= 1L;

	/**
	 * Logger.
	 */
	private static final Log					log					= LogFactory.getLog(CorbeilleTreeBean.class);

	private List<CorbeilleTreeMinistereNode>	ministereNodes;

	private String								currentIdMinistere;

	private String								currentIdFeuilleRoute;

	@In(create = true, required = true)
	protected transient CoreSession				documentManager;

	@In(required = true, create = true)
	protected transient SSPrincipal				ssPrincipal;

	@In(create = true, required = false)
	protected transient CorbeilleActionsBean	corbeilleActions;

	@In(create = true, required = false)
	protected transient ContentViewActions		contentViewActions;

	@In(create = true, required = false)
	protected transient NavigationContextBean	navigationContext;

	private CorbeilleTreeEtapeNode				currentItem;

	private String								mailboxIds;

	/**
	 * Poste sélectionné depuis l'outil de choix de corbeille.
	 */
	private String								corbeilleSelectionPoste;

	/**
	 * Utilisateur sélectionné depuis l'outil de choix de corbeille.
	 */
	private String								corbeilleSelectionUser;

	private List<CorbeilleTreeMinistereNode>	oldGvntMinisteres;

	/**
	 * Charge l'arbre des corbeilles - dans le cas du SGG, les enfants ne sont pas chargés, et tous les ministères sont
	 * affichés. Dans le cas des utilisateurs normaux, l'arbre est chargé au complet, et les ministères vides sont
	 * cachés.
	 * 
	 * @param keepCorbeille si true le chargement se fait sans mise à jour de la corbeille courante
	 * @return L'arbre chargé
	 * @throws ClientException
	 */
	public List<CorbeilleTreeMinistereNode> getCorbeille(boolean keepCorbeille) throws ClientException {
		if (ministereNodes == null) {
			if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.CORBEILLE_SGG_READER)) {
				// Cas d'un utilisateur SGG
				ministereNodes = loadTreeFromMinisteres(false);
			} else {
				// Cas d'un utilisateur normal
				loadTree(keepCorbeille);
			}
		}
		return ministereNodes;
	}
	
	public List<CorbeilleTreeMinistereNode> getCorbeille() throws ClientException {
		return getCorbeille(true);
	}

	/**
	 * Retourne l'arbre complet avec tous les ministères.
	 * 
	 * @return L'arbre complet
	 * @throws ClientException
	 */
	private List<CorbeilleTreeMinistereNode> loadTreeFromMinisteres(Boolean fromNormalUser) throws ClientException {
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		List<CorbeilleTreeMinistereNode> ministeres = new ArrayList<CorbeilleTreeMinistereNode>();
		oldGvntMinisteres = new ArrayList<CorbeilleTreeMinistereNode>();

		List<EntiteNode> ministereList = ministeresService.getCurrentMinisteres();
		Collections.sort(ministereList, new ProtocolarOrderComparator());

		List<Map<String, List<PreComptage>>> preComptageMap = retrievePrecomptageForSelectedPostes();
		Map<String, Long> precomptageMapList = computePrecomptageForSelectedPostesByMinistere(preComptageMap);

		// Récupération de la liste des ministères
		for (EntiteNode entiteNode : ministereList) {

			Long preComptage = precomptageMapList.get(entiteNode.getId());
			ministeres.add(new CorbeilleTreeMinistereNode(entiteNode.getId().toString(), entiteNode.getEdition(),
					preComptage != null ? preComptage : 0L));
			precomptageMapList.remove(entiteNode.getId());
		}

		for (String ministereId : precomptageMapList.keySet()) {
			for (Map<String, List<PreComptage>> map : preComptageMap) {
				for (Entry<String, List<PreComptage>> entry : map.entrySet()) {
					if (entry.getKey().equals(ministereId)) {
						Long count = 0L;
						for (PreComptage preComptage : entry.getValue()) {
							count += preComptage.getCount();
						}
						OrganigrammeNode node = ministeresService.getEntiteNode(ministereId);
						if (node != null) {
							CorbeilleTreeMinistereNode corbeilleTreeMinistereNode = new CorbeilleTreeMinistereNode(node
									.getId().toString(), ((EntiteNode) node).getEdition(), count != null ? count : 0L);
							corbeilleTreeMinistereNode.setLoaded(Boolean.TRUE);
							ministeres.add(corbeilleTreeMinistereNode);
							if (!fromNormalUser) {
								for (PreComptage p : entry.getValue()) {
									corbeilleTreeMinistereNode.addOrMergedEtape(p.getRoutingTaskType(), p.getCount());
								}
							}
							corbeilleTreeMinistereNode.setLabel(corbeilleTreeMinistereNode.getLabel()
									+ " [Ancien gouvernement] ");
							oldGvntMinisteres.add(corbeilleTreeMinistereNode);
						}

					}
				}
			}

		}

		return ministeres;
	}

	/**
	 * Charge l'arbre des postes sélectionnés.
	 * 
	 * @param keepCorbeille si true, alors on ne charge pas le premier noeud.
	 * 
	 * @throws ClientException
	 */
	private void loadTree(boolean keepCorbeille) throws ClientException {
		ministereNodes = new ArrayList<CorbeilleTreeMinistereNode>();
		List<CorbeilleTreeMinistereNode> ministeres = loadTreeFromMinisteres(true);
		STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		CorbeilleService corbeilleService = STServiceLocator.getCorbeilleService();
		Set<String> tempTreeUser = new HashSet<String>();
		Set<String> tempTreeDossierLink = new HashSet<String>();

		final Set<String> postesId = getSelectedPostes();
		
		// Pour chaque poste, on récupère les ids ministeres
		List<EntiteNode> ministeresUser = ministeresService.getMinistereParentFromPostes(postesId);
		List<EntiteNode> ministeresDossierLinks = new ArrayList<EntiteNode>();
		
		// On récupère les dossiers link liés aux postes du user
		List<DocumentModel> dossierLinkDoc = corbeilleService.findDossierLinkFromPoste(documentManager, new ArrayList<String>(postesId));
		for (DocumentModel doc : dossierLinkDoc) {
			DossierLink dossierLink = doc.getAdapter(DossierLink.class);
			EntiteNode ministereDossierLink = ministeresService.getEntiteNode(dossierLink.getIdMinistereAttributaire());
			ministeresDossierLinks.add(ministereDossierLink);
		}
		
		for (EntiteNode ministereNode : ministeresUser) {
			// initialisation d'un ministère
			tempTreeUser.add(ministereNode.getId());
		}
		for (EntiteNode ministereNode : ministeresDossierLinks) {
			// initialisation d'un ministère
			tempTreeDossierLink.add(ministereNode.getId());
		}

		// Remplissage de l'arbre avec les ministeres accessibles par l'utilisateur
		for (CorbeilleTreeMinistereNode minNode : ministeres) {
			if (tempTreeUser.contains(minNode.getMinistereId())) {
				// ajout du noeud ministère du user
				ministereNodes.add(minNode);
			}
			if (tempTreeDossierLink.contains(minNode.getMinistereId()) && !ministereNodes.contains(minNode)) {
				// ajout du noeud ministère accessible car assigné au poste du user
				minNode.setIsBold(Boolean.FALSE);
				ministereNodes.add(minNode);
			}
		}

		// Ajout des ministères d'anciens gouvernement avec questions non migrées
		for (CorbeilleTreeMinistereNode oldGvntMin : oldGvntMinisteres) {
			if (!ministereNodes.contains(oldGvntMin)) {
				ministereNodes.add(oldGvntMin);
			}
		}

		List<Map<String, List<PreComptage>>> precomptageMapList = retrievePrecomptageForSelectedPostes();

		// Dépliage complet de l'arbre
		for (CorbeilleTreeMinistereNode node : ministereNodes) {
			addChildrenNodes(node, precomptageMapList);
			node.setOpened(Boolean.TRUE);
		}

		if(!keepCorbeille) {
			loadFirstNode();
		}
	}

	public void forceRefresh() throws ClientException {
		forceRefresh(false);
	}
	
	/**
	 * Force le rafraichissement de la corbeille.
	 * 
	 * @throws ClientException
	 */
	public void forceRefresh(boolean keepCorbeille) throws ClientException {
		ministereNodes = null;
		mailboxIds = null;
		
		String prevFeuilleRouteId = null;
		if (currentItem != null) {
			prevFeuilleRouteId = currentItem.getFeuilleRouteId();
		}
		
		getCorbeille(keepCorbeille);

		// Ouverture du dernier ministère courant
		if (currentItem != null) {
			final String ministereId = currentItem.getMinistereId();
			final String feuilleRouteId = currentItem.getFeuilleRouteId();
			
			setContext(null);
			
			for (CorbeilleTreeMinistereNode node : ministereNodes) {
				if (ministereId.equals(node.getMinistereId())) {
					addChildrenNodes(node);
					node.setOpened(Boolean.TRUE);
					CorbeilleTreeEtapeNode etapeNode = node.getEtape(feuilleRouteId);
					setContext(etapeNode);
				} else {
					node.setOpened(Boolean.FALSE);
				}
			}
			
			if (currentItem != null && keepCorbeille && prevFeuilleRouteId != null) {
				currentItem.setFeuilleRouteId(prevFeuilleRouteId);
				
				for (CorbeilleTreeMinistereNode node : ministereNodes) {
					if (ministereId.equals(node.getMinistereId())) {
						CorbeilleTreeEtapeNode etapeNode = node.getEtape(prevFeuilleRouteId);
						setContext(etapeNode);		
					}
				}
			}
		}
	}

	/**
	 * Retourne la condition pour filtrer aux mailbox des postes sélectionnés.
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String getMailboxConditionQuery() throws ClientException {
		if (mailboxIds == null) {
			final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
			final MailboxService mailboxService = STServiceLocator.getMailboxService();

			Set<String> postesId = getSelectedPostes();
			Set<String> userPosteIds = mailboxPosteService.getMailboxPosteIdSetFromPosteIdSet(postesId);
			List<Mailbox> mailBoxes = mailboxService.getMailbox(documentManager, userPosteIds);
			Set<String> mailBoxDocId = new HashSet<String>();

			for (Mailbox mailbox : mailBoxes) {
				mailBoxDocId.add(mailbox.getDocument().getId());
			}

			if (mailBoxDocId.isEmpty()) {
				return "('0')";
			}

			mailboxIds = "(" + StringUtil.join(mailBoxDocId, ",", "'") + ")";
		}

		return mailboxIds;
	}

	/**
	 * Retourne true si on affiche le module de sélection des postes.
	 * 
	 * @return
	 */
	public boolean displayCorbeilleSelection() {
		return ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.ALL_CORBEILLE_READER);
	}

	/**
	 * Retourne les informations de précomptage pour les postes sélectionnés.
	 * 
	 * @return
	 * @throws ClientException
	 */
	private List<Map<String, List<PreComptage>>> retrievePrecomptageForSelectedPostes() throws ClientException {
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		Set<String> postesId = getSelectedPostes();
		List<Mailbox> mailboxes = mailboxPosteService.getMailboxPosteList(documentManager, postesId);

		List<Map<String, List<PreComptage>>> precomptageMapList = new ArrayList<Map<String, List<PreComptage>>>();

		for (Mailbox m : mailboxes) {
			ReponsesMailbox repmailbox = m.getDocument().getAdapter(ReponsesMailbox.class);
			Map<String, List<PreComptage>> precomptageMap = repmailbox.getPreComptagesGroupByMinistereId();
			if (!precomptageMap.isEmpty()) {
				precomptageMapList.add(precomptageMap);
			}
		}
		return precomptageMapList;
	}

	/**
	 * Retourne le nombre de dossiers par ministère à partir des données de précomptage.
	 * 
	 * @param retrievePrecomptage
	 * @return
	 */
	private Map<String, Long> computePrecomptageForSelectedPostesByMinistere(
			List<Map<String, List<PreComptage>>> retrievePrecomptage) {
		Map<String, Long> result = new HashMap<String, Long>();
		for (Map<String, List<PreComptage>> map : retrievePrecomptage) {
			for (String key : map.keySet()) {
				Long comptage = result.get(key) != null ? result.get(key) : 0L;
				for (PreComptage preComptage : map.get(key)) {
					comptage += preComptage.getCount() != null ? preComptage.getCount() : 0L;
				}
				result.put(key, comptage);
			}

		}

		return result;
	}

	/**
	 * Ajout des étapes de feuille de route aux ministères.
	 * 
	 * @param treeNode
	 * @throws ClientException
	 */
	private void addChildrenNodes(CorbeilleTreeMinistereNode ministereNode) throws ClientException {

		if (ministereNode.isLoaded()) {
			return;
		}

		List<Map<String, List<PreComptage>>> precomptageMapList = retrievePrecomptageForSelectedPostes();

		addChildrenNodes(ministereNode, precomptageMapList);
	}

	/**
	 * Charge les noeuds enfants.
	 * 
	 * @param ministereNode
	 * @param precomptageMapList
	 * @throws ClientException
	 */
	private void addChildrenNodes(CorbeilleTreeMinistereNode ministereNode,
			List<Map<String, List<PreComptage>>> precomptageMapList) throws ClientException {
		// Récupère le nombre de dossierLink par étape du ministères, appartenant à l'utilisateur connecté
		final String ministereId = ministereNode.getMinistereId();
		for (Map<String, List<PreComptage>> precomptageMap : precomptageMapList) {
			List<PreComptage> preComptageList = precomptageMap.get(ministereId);
			if (preComptageList != null && !preComptageList.isEmpty()) {
				for (PreComptage p : preComptageList) {
					ministereNode.addOrMergedEtape(p.getRoutingTaskType(), p.getCount());
				}
			}
		}

		ministereNode.setLoaded(true);
	}

	/**
	 * Clic sur le bouton "+" d'un noeud.
	 * 
	 * @param event
	 * @throws ClientException
	 */
	public void changeExpandListener(NodeExpandedEvent event) throws ClientException {
		UIComponent component = event.getComponent();
		if (component instanceof UITree) {
			UITree treeComponent = (UITree) component;
			Object value = treeComponent.getRowData();
			if (value instanceof CorbeilleTreeMinistereNode) {
				CorbeilleTreeMinistereNode minNode = (CorbeilleTreeMinistereNode) value;
				minNode.setOpened(!minNode.isOpened());
			}
		}
	}

	/**
	 * Clic sur un noeud.
	 * 
	 * @param event
	 * @throws IOException
	 * @throws ClientException
	 */
	public void nodeSelectListener(NodeSelectedEvent event) throws IOException, ClientException {
		UIComponent component = event.getComponent();
		if (component instanceof UITree) {
			UITree treeComponent = (UITree) component;
			Object value = treeComponent.getRowData();
			if (log.isDebugEnabled()) {
				log.debug("nodeSelectListener on " + value);
			}
			if (value instanceof CorbeilleTreeMinistereNode) {

				CorbeilleTreeMinistereNode minNode = (CorbeilleTreeMinistereNode) value;
				if (log.isDebugEnabled()) {
					log.debug("nodeSelectListener on node " + minNode.getLabel() + " open = " + minNode.isOpened()
							+ " loaded=" + minNode.isLoaded() + " nbchilds=" + minNode.getNbEtapes());
				}
				addChildrenNodes(minNode);
				minNode.setOpened(!minNode.isOpened());
			}
		}
	}

	/**
	 * Un noeud a été ouvert.
	 * 
	 * @param treeComponent
	 * @return
	 */
	public Boolean adviseNodeOpened(UITree treeComponent) {
		Object value = treeComponent.getRowData();
		if (value instanceof CorbeilleTreeMinistereNode) {
			CorbeilleTreeMinistereNode minNode = (CorbeilleTreeMinistereNode) value;
			return minNode.isOpened();
		}
		return null;
	}

	/**
	 * Getter de currentIdMinistere.
	 * 
	 * @return
	 */
	public String getCurrentIdMinistere() {
		return currentIdMinistere;
	}

	/**
	 * Setter de currentIdMinistere.
	 * 
	 * @param currentIdMinistere
	 */
	public void setCurrentIdMinistere(String currentIdMinistere) {
		this.currentIdMinistere = currentIdMinistere;
	}

	/**
	 * Getter de currentIdFeuilleRoute.
	 * 
	 * @return
	 */
	public String getCurrentIdFeuilleRoute() {
		return currentIdFeuilleRoute;
	}

	/**
	 * Setter de currentIdFeuilleRoute.
	 * 
	 * @param currentIdFeuilleRoute
	 */
	public void setCurrentIdFeuilleRoute(String currentIdFeuilleRoute) {
		this.currentIdFeuilleRoute = currentIdFeuilleRoute;
	}

	/**
	 * Ouvre un noeud de l'arbre redirige vers la corbeille.
	 * 
	 * @param item
	 * @return
	 * @throws ClientException
	 */
	@Observer("corbeilleChanged")
	public String setContext(CorbeilleTreeEtapeNode item) throws ClientException {
		currentItem = item;
		if (item == null) {
			currentIdFeuilleRoute = null;
			currentIdMinistere = null;
		} else {
			currentIdFeuilleRoute = item.getFeuilleRouteId();
			currentIdMinistere = item.getMinistereId();
		}

		// vide le cache du contentView pour ré-executer la requête
		if (contentViewActions != null) {
			contentViewActions.reset(ReponsesContentView.CORBEILLE_CONTENT_VIEW);
		}

		corbeilleActions.setCurrentDossierLink(null);
		navigationContext.resetCurrentDocument();

		return corbeilleActions.getCurrentView();
	}

	public String getContextFolderCount() {
		if (currentItem != null) {
			return Long.toString(currentItem.getCount());
		} else {
			return "0";
		}
	}

	/**
	 * Ouvre le premier noeud de l'arbre.
	 * 
	 * @throws ClientException
	 */
	public void loadFirstNode() throws ClientException {
		boolean found = false;
		if (ministereNodes != null) {
			for (CorbeilleTreeMinistereNode node : ministereNodes) {
				if (node.hasEtape()) {
					setContext(node.getFirst());
					found = true;
					break;
				}
			}
		}
		if (!found) {
			setContext(null);
		}
	}

	/**
	 * Retourne la liste des postes à afficher, ou par défaut les postes de l'utilisateur courant. S'assure que
	 * l'utilisateur courant a les permissions sur ces postes.
	 * 
	 * @return Liste d'identifiants de postes
	 * @throws ClientException
	 */
	public Set<String> getSelectedPostes() throws ClientException {
		Set<String> postesId = new HashSet<String>();

		if (!StringUtils.isBlank(corbeilleSelectionPoste)) {
			postesId.add(corbeilleSelectionPoste);
		}

		if (!StringUtils.isBlank(corbeilleSelectionUser)) {
			UserManager userManager = STServiceLocator.getUserManager();
			DocumentModel userDoc = userManager.getUserModel(corbeilleSelectionUser);
			postesId.addAll(userDoc.getAdapter(STUser.class).getPostes());
		}

		updateVirtualGroups(postesId);

		// L'outil de sélection de poste ou d'utilisateur n'est pas utilisé, fonctionnement normal
		if (postesId.isEmpty()) {
			postesId.addAll(ssPrincipal.getPosteIdSet());
		}

		return postesId;
	}

	/**
	 * Met à jour les groupes virtuels de l'utilisateur connecté pour avoir les droits des postes.
	 * 
	 * @param postesId
	 * @throws ClientException
	 */
	private void updateVirtualGroups(Set<String> postesId) throws ClientException {
		SSPrincipalImpl principal = (SSPrincipalImpl) ssPrincipal;
		List<String> virtualGroups = principal.getVirtualGroups();
		MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		Set<String> mailbox = mailboxPosteService.getMailboxPosteIdSetFromPosteIdSet(postesId);
		mailbox.addAll(virtualGroups);
		principal.setVirtualGroups(new ArrayList<String>(mailbox));
	}

	/**
	 * Getter de corbeilleSelectionPoste.
	 * 
	 * @return the corbeilleSelectionPoste
	 */
	public String getCorbeilleSelectionPoste() {
		return corbeilleSelectionPoste;
	}

	/**
	 * Setter de corbeilleSelectionPoste.
	 * 
	 * @param corbeilleSelectionPoste
	 *            the corbeilleSelectionPoste to set
	 * @throws ClientException
	 */
	public void setCorbeilleSelectionPoste(String corbeilleSelectionPoste) throws ClientException {
		this.corbeilleSelectionPoste = corbeilleSelectionPoste;
	}

	/**
	 * Getter de corbeilleSelectionUser.
	 * 
	 * @return the corbeilleSelectionUser
	 */
	public String getCorbeilleSelectionUser() {
		return corbeilleSelectionUser;
	}

	/**
	 * Setter de corbeilleSelectionUser.
	 * 
	 * @param corbeilleSelectionUser
	 *            the corbeilleSelectionUser to set
	 * @throws ClientException
	 */
	public void setCorbeilleSelectionUser(String corbeilleSelectionUser) throws ClientException {
		this.corbeilleSelectionUser = corbeilleSelectionUser;
	}
}
