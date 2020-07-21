package fr.dila.ss.web.admin.organigramme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.platform.ui.web.component.list.UIEditableList;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.web.admin.converter.OrganigrammeMailboxIdToLabelConverter;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.exception.LocalizedClientException;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.administration.organigramme.OrganigrammeTreeBean;
import fr.dila.st.web.converter.OrganigrammeGvtIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeMinIdConverter;
import fr.dila.st.web.converter.OrganigrammeMinIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeMultiIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeNodeIdConverter;
import fr.dila.st.web.converter.OrganigrammePosteIdConverter;
import fr.dila.st.web.converter.OrganigrammePosteIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeUSIdConverter;
import fr.dila.st.web.converter.OrganigrammeUSIdToLabelConverter;
import fr.dila.st.web.converter.OrganigrammeUserIdToLabelConverter;

/**
 * ActionBean de gestion de l'organigramme
 * 
 * @author FEO
 */
@Name("organigrammeManagerActions")
@SerializedConcurrentAccess
@Scope(ScopeType.CONVERSATION)
public abstract class OrganigrammeManagerActionsBean implements Serializable {

	public static final String				ORGANIGRAMME_VIEW				= "view_organigramme";
	public static final String				ORGANIGRAMME_SCHEMA				= "organigramme";
	public static final String				MULTI							= "MULTI";
	public static final String				LIST							= "LIST";
	public static final String				SINGLE							= "SINGLE";
	public static final String				USER_TYPE						= "USER_TYPE";
	public static final String				MIN_TYPE						= "MIN_TYPE";
	public static final String				UST_TYPE						= "UST_TYPE";
	public static final String				GVT_TYPE						= "GVT_TYPE";
	public static final String				DIR_TYPE						= "DIR_TYPE";
	public static final String				USER_MAIL_TYPE					= "USER_MAIL_TYPE";
	public static final String				POSTE_TYPE						= "POSTE_TYPE";
	public static final String				DIR_AND_UST_TYPE				= "DIR_AND_UST_TYPE";
	public static final String				MAILBOX_TYPE					= "MAILBOX_TYPE";
	public static final String				NEW_TIMBRE_EMPTY_VALUE			= "empty_value";
	public static final String				NEW_TIMBRE_UNCHANGED_ENTITY		= "unchanged_entity";
	public static final String				NEW_TIMBRE_DEACTIVATE_ENTITY	= "deactivate_entity";
	public static final String				PREFIXED_ID_TYPE				= "PREFIXED";

	/**
	 * UID
	 */
	private static final long				serialVersionUID				= 5931019726081475086L;

	/**
	 * Logger.
	 */
	private static final STLogger			LOG								= STLogFactory
																					.getLog(OrganigrammeManagerActionsBean.class);

	protected transient OrganigrammeService	organigrammeService;

	@In(create = true, required = false)
	protected FacesMessages					facesMessages;

	@In(create = true)
	protected ResourcesAccessor				resourcesAccessor;

	@In(create = true, required = false)
	protected transient WebActions			webActions;

	@In(create = true, required = false)
	protected CoreSession					documentManager;

	@In(required = true, create = true)
	protected SSPrincipal					ssPrincipal;

	@In(create = true)
	protected OrganigrammeTreeBean			organigrammeTree;

	/**
	 * Id of the editable list component where selection ids are put.
	 * <p>
	 * Component must be an instance of {@link UIEditableList}
	 */
	@RequestParameter
	protected String						organigrammeSelectionListId;

	@RequestParameter
	protected String						organigrammeSelectionType;

	@RequestParameter
	protected String						organigrammeSelectionMultiple;

	@RequestParameter
	protected String						selectedNodeTypeFromWidget;															// abi
																																	// :
																																	// ajouté
																																	// car
																																	// le
																																	// type
																																	// n'est
																																	// pas
																																	// transmis
																																	// pour
																																	// les
																																	// ministères
																																	// via
																																	// organigrammePopupSelectionType

	protected List<Action>					organigrammeActions;

	protected OrganigrammeNode				selectedNodeModel;

	private String							creationType;

	protected String						selectedNodeId;

	protected String						selectedNodeType;

	protected String						organigrammePopupSelectionType;

	protected String						newGroupName;

	protected OrganigrammeNode				currentNode;

	protected boolean						nameAlreadyUsed					= false;

	protected boolean						confirmDialog					= false;

	// Gestion du changement de timbre
	/**
     * 
     */
	protected Map<String, String>			newTimbre;

	protected List<SelectItem>				newTimbreList;

	protected List<SelectItem>				gouvernementList;

	protected String						currentGouvernement;

	protected String						nextGouvernement;

	// Gestion du copier / coller
	protected OrganigrammeNode				selectedNodeForCopy;

	private String							nodeActive;

	/**
	 * Id of the output component where single selection is displayed
	 * <p>
	 * Component must be an instance of {@link ValueHolder}
	 */
	@RequestParameter
	protected String						suggestionSelectionOutputId;

	/**
	 * Id if the hidden component where single selection id is put
	 * <p>
	 * Component must be an instance of {@link EditableValueHolder}
	 */
	@RequestParameter
	protected String						suggestionSelectionHiddenId;

	/**
	 * Id of the delete component displayed next to single selection
	 * <p>
	 * Component must be an instance of {@link UIComponent}
	 */
	@RequestParameter
	protected String						suggestionSelectionDeleteId;
	
	/**
	 * Type de l'élément sélectionné
	 * <p>
	 */
	@RequestParameter
	protected String						idType;

	/**
	 * Default constructor
	 */
	public OrganigrammeManagerActionsBean() {
		// do nothing
	}

	@Destroy
	public void destroy() {
		organigrammeService = null;
		LOG.debug(STLogEnumImpl.MIGRATE_MINISTERE_TEC, "Removing user workspace actions bean");
	}

	@Factory(value = "getOrganigrammeActions", scope = ScopeType.EVENT)
	public List<Action> getOrganigrammeActions() {
		if (organigrammeActions == null) {
			organigrammeActions = webActions.getActionsList("ORGANIGRAMME_CONTEXT_MENU");
		}
		return organigrammeActions;
	}

	/**
	 * Suppression d'un noeud dans l'organigramme
	 * 
	 * @return null
	 * @throws ClientException
	 * @author Fabio Esposito
	 */
	public String deleteNode() throws ClientException {
		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}

		if (selectedNodeModel != null) {
			try {
				STServiceLocator.getOrganigrammeService().deleteFromDn(selectedNodeModel, true);
			} catch (LocalizedClientException e) {
				String message = resourcesAccessor.getMessages().get(e.getMessage());
				facesMessages.add(StatusMessage.Severity.WARN, message);
				return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
			}
		}
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * action d'édition d'un élément
	 * 
	 * @return la vue édition
	 * @throws ClientException
	 */
	public String editNode() throws ClientException {
		String view = null;
		if (selectedNodeType != null) {
			confirmDialog = false;
			loadSelectedNodeModel();
			if (OrganigrammeType.MINISTERE.equals(OrganigrammeType.getEnum(selectedNodeType))) {
				view = STViewConstant.ORGANIGRAMME_EDIT_ENTITE;
			} else if (OrganigrammeType.DIRECTION.equals(OrganigrammeType.getEnum(selectedNodeType))
					|| OrganigrammeType.UNITE_STRUCTURELLE.equals(OrganigrammeType.getEnum(selectedNodeType))) {
				view = STViewConstant.ORGANIGRAMME_EDIT_UNITE_STRUCTURELLE;
			} else if (OrganigrammeType.POSTE.equals(OrganigrammeType.getEnum(selectedNodeType))) {
				view = STViewConstant.ORGANIGRAMME_EDIT_POSTE;
			} else if (OrganigrammeType.GOUVERNEMENT.equals(OrganigrammeType.getEnum(selectedNodeType))) {
				view = STViewConstant.ORGANIGRAMME_EDIT_GOUVERNEMENT;
			}
			OrganigrammeNode node = selectedNodeModel;
			if (!StringUtils.isEmpty(node.getLockUserName()) && !isCurrentUserUnlocker(node.getLockUserName())) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
				return null;
			} else {
				lockOrganigrammeNode(node);
			}
		} else {
			view = STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("organigramme.group.notfound"));
		}
		return view;
	}

	public String cancelEditNode() throws ClientException {
		unlockOrganigrammeNode(selectedNodeModel);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * action de désactivation d'un élément
	 * 
	 * @return null, on reste sur la même vue
	 * @throws ClientException
	 */
	public String disableNode() throws ClientException {
		return changeNodeActivation(false);
	}

	/**
	 * action de réactivation d'un élément
	 * 
	 * @return null, on reste sur la même vue
	 * @throws ClientException
	 */
	public String enableNode() throws ClientException {
		return changeNodeActivation(true);
	}

	private String changeNodeActivation(boolean activate) throws ClientException {
		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}
		if (selectedNodeModel != null) {
			try {
				if (activate) {
					STServiceLocator.getOrganigrammeService().enableNodeFromDn(selectedNodeModel.getId(),
							selectedNodeModel.getType());
				} else {
					STServiceLocator.getOrganigrammeService().disableNodeFromDn(selectedNodeModel.getId(),
							selectedNodeModel.getType());
				}
			} catch (LocalizedClientException e) {
				facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get(e.getMessage()));
				return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
			}
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.organigrammeManager.node.disabled"));
		}
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return null;
	}

	/**
	 * Validation du formulaire de création
	 * 
	 * @param context
	 * @param component
	 * @param value
	 */
	public void validateNewGroupName(FacesContext context, UIComponent component, Object value) {
		if (!(value instanceof String) || StringUtils.isEmpty(((String) value).trim())) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"organigramme.creation.missingGroupName"), null);
			// also add global message
			context.addMessage(null, message);
			throw new ValidatorException(message);
		}
	}

	/**
	 * Validation du formulaire de création
	 * 
	 * @param context
	 * @param component
	 * @param value
	 */
	public void validateStartDate(FacesContext context, UIComponent component, Object value) {
		if (!(value instanceof Date)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"La date ne peut être vide"), null);
			// also add global message
			throw new ValidatorException(message);
		}
		Date inputDate = (Date) value;
		if (inputDate.compareTo(new Date()) > 0) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"La date ne peut être supérieure à la date du jour"), null);
			throw new ValidatorException(message);
		}
	}

	/**
	 * Adds selection from selector as a list element
	 * <p>
	 * Must pass request parameter "organigrammeSelectionListId" holding the binding to model. Selection will be
	 * retrieved using the {@link #getSelectedValue()} method.
	 * 
	 * @throws ClientException
	 */
	public void addSelectionToList(ActionEvent event) throws ClientException {
		UIComponent component = event.getComponent();
		if (component == null) {
			return;
		}
		UIComponent base = ComponentUtils.getBase(component);
		UIEditableList list = ComponentUtils.getComponent(base, organigrammeSelectionListId, UIEditableList.class);

		if (list != null) {
			// add selected value to the list
			String selectedValue = getSelectedNodeId();
			String selectedType = organigrammePopupSelectionType;

			if (selectedType != null && selectedType.contains("_TYPE")) {

				if (selectedType.equals("POSTE_TYPE") || selectedType.equals("MAILBOX_TYPE")) {
					selectedType = OrganigrammeType.POSTE.getValue();
				} else if (selectedType.equals("GVT_TYPE")) {
					selectedType = OrganigrammeType.GOUVERNEMENT.getValue();
				} else if (selectedType.equals("MIN_TYPE")) {
					selectedType = OrganigrammeType.MINISTERE.getValue();
				} else if (selectedType.equals("DIR_TYPE") || selectedType.equals("UST_TYPE")
						|| selectedType.equals("DIR_AND_UST_TYPE")) {
					selectedType = OrganigrammeType.UNITE_STRUCTURELLE.getValue();
				} else if (selectedType.equals("USER_TYPE")) {
					selectedType = OrganigrammeType.USER.getValue();
				}
			}

			if (organigrammeService == null) {
				organigrammeService = STServiceLocator.getOrganigrammeService();
			}

			if (idType != null && idType.equals(PREFIXED_ID_TYPE)) {
				// check si l'élément est déjà dans la listes
				@SuppressWarnings("unchecked")
				List<Object> dataList = (List<Object>) list.getEditableModel().getWrappedData();
				if (!dataList.contains(selectedValue)) {
					list.addValue(selectedValue);
				}
			} else {
				String nodeId = "";
				if (selectedType != null && selectedType.equals("USR")) {
					UserNode node = organigrammeService.getUserNode(selectedValue);
					nodeId = node.getId();
	
				} else {
					OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(selectedValue,
							OrganigrammeType.getEnum(selectedType));
					nodeId = node.getId();
				}
	
				// check si l'élément est déjà dans la listes
				@SuppressWarnings("unchecked")
				List<Object> dataList = (List<Object>) list.getEditableModel().getWrappedData();
				if (!dataList.contains(selectedValue)) {
					list.addValue(nodeId);
				}
			}
		}
	}

	/**
	 * Adds selection from selector as single element
	 * <p>
	 * Must pass request parameters "suggestionSelectionOutputId" holding the value to show, and
	 * "suggestionSelectionHiddenId" holding the binding to model. Selection will be retrieved using the
	 * {@link #getSelectedValue()} method.
	 * <p>
	 * Additional optional request parameter "suggestionSelectionDeleteId" can be used to show an area where the "clear"
	 * button is shown.
	 */
	public void addSingleBoundSelection(ActionEvent event) {
		UIComponent component = event.getComponent();
		if (component == null) {
			return;
		}
		UIComponent base = ComponentUtils.getBase(component);
		EditableValueHolder hiddenSelector = ComponentUtils.getComponent(base, suggestionSelectionHiddenId,
				EditableValueHolder.class);
		ValueHolder output = ComponentUtils.getComponent(base, suggestionSelectionOutputId, ValueHolder.class);

		if (hiddenSelector != null && output != null) {
			String selectedValue = getSelectedNodeId();
			output.setValue(selectedValue);
			hiddenSelector.setSubmittedValue(selectedValue);
			// display delete component if needed
			if (suggestionSelectionDeleteId != null) {
				UIComponent deleteComponent = ComponentUtils.getComponent(base, suggestionSelectionDeleteId,
						UIComponent.class);
				if (deleteComponent != null) {
					deleteComponent.setRendered(true);
				}
			}
		}
	}

	/**
	 * Recupere l'id de la mailbox à partir de l'id du poste utilisé dans organigramme_select_node_widget.xhtml
	 * 
	 * @param posteId
	 *            id du poste
	 * @return id de la mailbox
	 */
	public String getMailboxIdFromPosteId(String posteId) {
		if (posteId != null) {
			return SSServiceLocator.getMailboxPosteService().getPosteMailboxId(posteId);
		}
		return null;
	}

	public String getNewGroupName() {
		return newGroupName;
	}

	public void setNewGroupName(String newGroupName) {
		this.newGroupName = newGroupName;
	}

	public String getCreationType() {
		return creationType;
	}

	public void setCreationType(String creationType) {
		this.creationType = creationType;
	}

	public String getSelectedNodeId() {
		return selectedNodeId;
	}

	public void setSelectedNodeId(String selectedNodeId) {
		organigrammeTree.setVisible(Boolean.FALSE); // remove tree from view
		this.selectedNodeId = selectedNodeId;
	}

	public OrganigrammeNode getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(OrganigrammeNode currentNode) {
		this.currentNode = currentNode;
	}

	/**
	 * Retourne le converter pour le type de node organigramme.
	 * 
	 * @param type
	 * @return
	 */
	public Converter getOrganigrammeConverter(String type) {
		if (DIR_TYPE.equals(type) || DIR_AND_UST_TYPE.equals(type) || UST_TYPE.equals(type)) {
			return new OrganigrammeUSIdToLabelConverter();
		} else if (GVT_TYPE.equals(type)) {
			return new OrganigrammeGvtIdToLabelConverter();
		} else if (MIN_TYPE.equals(type)) {
			return new OrganigrammeMinIdToLabelConverter();
		} else if (POSTE_TYPE.equals(type)) {
			return new OrganigrammePosteIdToLabelConverter();
		} else if (USER_TYPE.equals(type)) {
			return new OrganigrammeUserIdToLabelConverter();
		} else if (MAILBOX_TYPE.equals(type)) {
			return new OrganigrammeMailboxIdToLabelConverter();
		} else if (type != null && type.contains(",")) {
			return new OrganigrammeMultiIdToLabelConverter();
		} else {
			return null;
		}
	}

	/**
	 * Retourne un converter permettant de convertir l'ID du noeud de l'organigramme pour identifier le type de noeud
	 * (poste, US, ministère...) traité.
	 * 
	 * @param selectionType
	 *            Type de sélection (un seul type de noeud ou plusieurs types de noeuds)
	 * @param prefix
	 *            Préfixe de l'ID du noeud
	 * @return Convertisseur
	 */
	public Converter getOrganigrammeNodeIdConverter(String selectionType, String prefix) {
		if (MULTI.equals(selectionType)) {
			if (DIR_TYPE.equals(prefix)) {
				return new OrganigrammeUSIdConverter();
			} else if (MIN_TYPE.equals(prefix)) {
				return new OrganigrammeMinIdConverter();
			} else if (POSTE_TYPE.equals(prefix)) {
				return new OrganigrammePosteIdConverter();
			} else if (UST_TYPE.equals(prefix)) {
				return new OrganigrammeUSIdConverter();
			}
		}
		return null;
	}

	public Boolean contains(String selectionType, String type) {
		if (selectionType != null && type != null) {
			return selectionType.contains(type);
		}
		return false;
	}

	/**
	 * Retourne le label d'un node
	 * 
	 * @param selectionType
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	public String getOrganigrammeNodeLabel(String selectionType, String id) throws ClientException {

		String node = "";
		if (MIN_TYPE.equals(selectionType)) {
			EntiteNode minist = STServiceLocator.getSTMinisteresService().getEntiteNode(id);
			if (minist != null) {
				node = minist.getLabel();
			}
		} else if (DIR_TYPE.equals(selectionType) || DIR_AND_UST_TYPE.equals(selectionType)) {
			UniteStructurelleNode unit = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(id);
			if (unit != null) {
				node = unit.getLabel();
			}
		} else if (POSTE_TYPE.equals(selectionType)) {
			PosteNode poste = STServiceLocator.getSTPostesService().getPoste(id);
			if (poste != null) {
				node = poste.getLabel();
			}
		} else if (GVT_TYPE.equals(selectionType)) {
			GouvernementNode gvt = STServiceLocator.getSTGouvernementService().getGouvernement(id);
			if (gvt != null) {
				node = gvt.getLabel();
			}
		} else if (USER_TYPE.equals(selectionType)) {
			UserNode user = STServiceLocator.getOrganigrammeService().getUserNode(id.toString());
			if (user != null) {
				node = user.getLabel();
			}
		} else {
			throw new ClientException("No support for selectionType : [" + selectionType + "]");
		}
		return node;
	}

	/**
	 * @return the selectedNodeType
	 */
	public String getSelectedNodeType() {
		return selectedNodeType;
	}

	/**
	 * @param selectedNodeType
	 *            the selectedNodeType to set
	 */
	public void setSelectedNodeType(String selectedNodeType) {
		this.selectedNodeType = selectedNodeType;
	}

	/**
	 * Retourne true si le node est verrouillé
	 * 
	 * @return
	 * @throws ClientException
	 */
	protected boolean isSelectedNodeLocked() throws ClientException {
		if (selectedNodeModel != null) {
			OrganigrammeNode node = selectedNodeModel;
			if (!StringUtils.isEmpty(node.getLockUserName()) && !isCurrentUserUnlocker(node.getLockUserName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createGouvernementView() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_CREATE_GOUVERNEMENT;
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createEntiteView() throws ClientException {
		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_CREATE_ENTITE;
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createPosteView() throws ClientException {
		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_CREATE_POSTE;
	}

	/**
	 * 
	 * @return Vue de la création des postes webservice
	 * @throws ClientException
	 */
	public String createPosteWsView() throws ClientException {
		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_CREATE_POSTE_WS;
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createUniteStructurelleView() throws ClientException {
		loadSelectedNodeModel();
		if (isSelectedNodeLocked()) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
			return null;
		}
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_CREATE_UNITE_STRUCTURELLE;
	}

	/**
	 * Annuler creation Gouvernement
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String cancelCreateGouvernement() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Annuler creation Entité
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String cancelCreateEntite() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Annuler creation de poste
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String cancelCreatePoste() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Annuler creation de poste webservice
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String cancelCreatePosteWs() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Annuler creation d'une unité structurelle
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String cancelCreateUniteStructurelle() throws ClientException {
		resetNodeModel();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * met a null les documents en cours de création ou d'édition
	 */
	public void resetNodeModel() {
		selectedNodeModel = null;
		confirmDialog = false;
		nameAlreadyUsed = false;
		selectedNodeForCopy = null;
	}

	public void loadSelectedNodeModel() throws ClientException {
		loadNodeModel(getSelectedNodeId(), selectedNodeType);
	}

	private void loadNodeModel(String itemId, String type) throws ClientException {

		if (OrganigrammeType.MINISTERE.equals(OrganigrammeType.getEnum(type))) {
			selectedNodeModel = STServiceLocator.getSTMinisteresService().getEntiteNode(itemId);
		} else if (OrganigrammeType.DIRECTION.equals(OrganigrammeType.getEnum(type))
				|| OrganigrammeType.UNITE_STRUCTURELLE.equals(OrganigrammeType.getEnum(type))) {
			selectedNodeModel = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(itemId);
		} else if (OrganigrammeType.POSTE.equals(OrganigrammeType.getEnum(type))) {
			selectedNodeModel = STServiceLocator.getSTPostesService().getPoste(itemId);
		} else if (OrganigrammeType.GOUVERNEMENT.equals(OrganigrammeType.getEnum(type))) {
			selectedNodeModel = STServiceLocator.getSTGouvernementService().getGouvernement(itemId);
		}

	}

	public OrganigrammeNode getSelectedNodeModel() {
		return selectedNodeModel;
	}

	/**
	 * @return the newGouvernement
	 * @throws ClientException
	 */
	public OrganigrammeNode getNewGouvernement() throws ClientException {

		if (selectedNodeModel == null) {
			selectedNodeModel = STServiceLocator.getSTGouvernementService().getBareGouvernementModel();
		}
		return selectedNodeModel;

	}

	/**
	 * @return the newEntite
	 * @throws ClientException
	 */
	public OrganigrammeNode getNewEntite() throws ClientException {

		if (selectedNodeModel == null) {
			selectedNodeModel = STServiceLocator.getSTMinisteresService().getBareEntiteModel();
			EntiteNode entiteNode = (EntiteNode) selectedNodeModel;
			// ajout du parent
			GouvernementNode gouvernementNode = STServiceLocator.getSTGouvernementService().getGouvernement(
					getSelectedNodeId());
			List<OrganigrammeNode> parentList = new ArrayList<OrganigrammeNode>();
			parentList.add(gouvernementNode);
			entiteNode.setParentList(parentList);
			entiteNode.setParentGouvernement(gouvernementNode.getId());
		}
		return selectedNodeModel;

	}

	/**
	 * @return the newPoste
	 * @throws ClientException
	 */
	public OrganigrammeNode getNewPoste() throws ClientException {
		if (selectedNodeModel == null) {
			selectedNodeModel = STServiceLocator.getSTPostesService().getBarePosteModel();
			PosteNode posteNode = (PosteNode) selectedNodeModel;

			if (OrganigrammeType.MINISTERE.equals(OrganigrammeType.getEnum(selectedNodeType))) {
				List<EntiteNode> parentList = new ArrayList<EntiteNode>();
				EntiteNode organigrammeNode = STServiceLocator.getSTMinisteresService().getEntiteNode(
						getSelectedNodeId());
				parentList.add(organigrammeNode);
				posteNode.setEntiteParentList(parentList);
				posteNode.setParentEntiteId(organigrammeNode.getId());
			} else if (OrganigrammeType.DIRECTION.equals(OrganigrammeType.getEnum(selectedNodeType))
					|| OrganigrammeType.UNITE_STRUCTURELLE.equals(OrganigrammeType.getEnum(selectedNodeType))) {
				List<UniteStructurelleNode> parentList = new ArrayList<UniteStructurelleNode>();
				UniteStructurelleNode organigrammeNode = STServiceLocator.getSTUsAndDirectionService()
						.getUniteStructurelleNode(getSelectedNodeId());
				parentList.add((UniteStructurelleNode) organigrammeNode);
				posteNode.setUniteStructurelleParentList(parentList);
				posteNode.setParentUniteId(organigrammeNode.getId());
			}
		}
		return selectedNodeModel;
	}

	/**
	 * @return the newUniteStructurelle
	 * @throws ClientException
	 */
	public UniteStructurelleNode getNewUniteStructurelle() throws ClientException {
		if (selectedNodeModel == null) {
			selectedNodeModel = STServiceLocator.getSTUsAndDirectionService().getBareUniteStructurelleModel();
			UniteStructurelleNode ustNode = (UniteStructurelleNode) selectedNodeModel;
			OrganigrammeNode organigrammeNode;
			// ajout du parent

			if (OrganigrammeType.MINISTERE.equals(OrganigrammeType.getEnum(selectedNodeType))) {
				List<EntiteNode> parentList = new ArrayList<EntiteNode>();
				organigrammeNode = STServiceLocator.getSTMinisteresService().getEntiteNode(getSelectedNodeId());
				parentList.add((EntiteNode) organigrammeNode);
				ustNode.setEntiteParentList(parentList);
				ustNode.setParentEntiteId(organigrammeNode.getId());
			} else if (OrganigrammeType.DIRECTION.equals(OrganigrammeType.getEnum(selectedNodeType))
					|| OrganigrammeType.UNITE_STRUCTURELLE.equals(OrganigrammeType.getEnum(selectedNodeType))) {
				List<UniteStructurelleNode> parentList = new ArrayList<UniteStructurelleNode>();
				organigrammeNode = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(
						getSelectedNodeId());
				parentList.add((UniteStructurelleNode) organigrammeNode);
				ustNode.setUniteStructurelleParentList(parentList);
				ustNode.setParentUniteId(organigrammeNode.getId());
			}
		}
		return (UniteStructurelleNode) selectedNodeModel;
	}

	/**
	 * Crée un gouvernement
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createGouvernement() throws ClientException {
		STServiceLocator.getSTGouvernementService().createGouvernement((GouvernementNode) selectedNodeModel);
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.gouvernementCreated"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Crée une entité (ministère)
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createEntite() throws ClientException {
		if (!confirmDialog) {
			final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNodeModel)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return null;
			}

			if (!organigrammeService.checkUniqueLabel(selectedNodeModel)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_CREATE_ENTITE;
			}
		} else {
			confirmDialog = false;
		}
		STServiceLocator.getSTMinisteresService().createEntite((EntiteNode) selectedNodeModel);
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.entiteCreated"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		nameAlreadyUsed = false;
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createPoste() throws ClientException {
		if (!confirmDialog) {
			final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNodeModel)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return null;
			}

			if (!organigrammeService.checkUniqueLabel(selectedNodeModel)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_CREATE_POSTE;
			}
		} else {
			confirmDialog = false;
		}
		STServiceLocator.getSTPostesService().createPoste(documentManager, (PosteNode) selectedNodeModel);
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.posteCreated"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		nameAlreadyUsed = false;
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Creation d'un poste webservice
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createPosteWs() throws ClientException {
		if (!confirmDialog) {
			final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNodeModel)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return null;
			}
			if (!organigrammeService.checkUniqueLabel(selectedNodeModel)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_CREATE_POSTE_WS;
			}
		} else {
			confirmDialog = false;
		}
		STServiceLocator.getSTPostesService().createPoste(documentManager, (PosteNode) selectedNodeModel);
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.posteWsCreated"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Creation d'une unité structurelle
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String createUniteStructurelle() throws ClientException {
		UniteStructurelleNode ustNode = (UniteStructurelleNode) selectedNodeModel;

		if (!confirmDialog) {
			List<UniteStructurelleNode> ustParentList = ustNode.getUniteStructurelleParentList();
			List<EntiteNode> entiteParentList = ustNode.getEntiteParentList();
			if (ustParentList == null || ustParentList.isEmpty()) {
				// si pas de parent unite structurelle
				// vérifie les entités parentes
				if (entiteParentList == null || entiteParentList.isEmpty()) {
					// Error
					facesMessages
							.add(StatusMessage.Severity.WARN,
									resourcesAccessor.getMessages().get(
											"warn.organigrammeManager.uniteStructurelleNeedParent"));
					return null;
				}
			}
			final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNodeModel)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return null;
			}
			if (!organigrammeService.checkUniqueLabel(selectedNodeModel)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_CREATE_UNITE_STRUCTURELLE;
			}
		} else {
			confirmDialog = false;
		}
		// enregistrement de l'unite structurelle
		STServiceLocator.getSTUsAndDirectionService()
				.createUniteStructurelle((UniteStructurelleNode) selectedNodeModel);
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.uniteStructurelleCreated"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		nameAlreadyUsed = false;
		return cancelCreateUniteStructurelle();
	}

	/**
	 * Mise à jour de l'entité
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	public String updateEntite() throws ClientException {
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		organigrammeService = STServiceLocator.getOrganigrammeService();
		// vérifie qu'on a toujours le lock
		OrganigrammeNode nodeToUpdate = selectedNodeModel;
		OrganigrammeNode node = ministeresService.getEntiteNode(nodeToUpdate.getId());
		if (node == null || !isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}
		// Contrôle pour vérifier l'unicité de l'entité
		// vérifie que le nom est unique sous les parents
		if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNodeModel)) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
			return null;
		}
		if (!organigrammeService.checkUniqueLabel(selectedNodeModel)) {
			nameAlreadyUsed = true;
			return STViewConstant.ORGANIGRAMME_CREATE_UNITE_STRUCTURELLE;
		}
		try {
			ministeresService.updateEntite((EntiteNode) selectedNodeModel);
		} finally {
			unlockOrganigrammeNode(selectedNodeModel);
		}
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.entiteModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		nameAlreadyUsed = false;
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Mise à jour du gouvernement
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	public String updateGouvernement() throws ClientException {
		final STGouvernementService gouvService = STServiceLocator.getSTGouvernementService();
		// vérifie qu'on a toujours le lock
		OrganigrammeNode nodeToUpdate = selectedNodeModel;
		OrganigrammeNode node = gouvService.getGouvernement(nodeToUpdate.getId());
		if (node == null || !isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}
		try {
			gouvService.updateGouvernement((GouvernementNode) selectedNodeModel);
		} finally {
			unlockOrganigrammeNode(selectedNodeModel);
		}
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.gouvernementModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		nameAlreadyUsed = false;
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Mise à jour du poste
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	public String updatePoste() throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		final STPostesService postesService = STServiceLocator.getSTPostesService();
		// vérifie qu'on a toujours le lock
		OrganigrammeNode nodeToUpdate = selectedNodeModel;
		OrganigrammeNode node = postesService.getPoste(nodeToUpdate.getId());
		if (node == null || !isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}
		if (!confirmDialog) {
			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, selectedNodeModel)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return STViewConstant.ERROR_VIEW;
			}

			if (!organigrammeService.checkUniqueLabel(selectedNodeModel)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_EDIT_POSTE;
			}
		} else {
			confirmDialog = false;
		}
		try {
			postesService.updatePoste(documentManager, (PosteNode) selectedNodeModel);
		} finally {
			unlockOrganigrammeNode(selectedNodeModel);
		}
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.posteModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		nameAlreadyUsed = false;
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Mise à jour du poste webservice
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	public String updatePosteWs() throws ClientException {
		final STPostesService postesService = STServiceLocator.getSTPostesService();
		// vérifie qu'on a toujours le lock
		OrganigrammeNode nodeToUpdate = selectedNodeModel;
		OrganigrammeNode node = postesService.getPoste(nodeToUpdate.getId());
		if (node == null || !isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}

		try {
			postesService.updatePoste(documentManager, (PosteNode) selectedNodeModel);
		} finally {
			unlockOrganigrammeNode(selectedNodeModel);
		}
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.posteWsModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Mise à jour de l'unité structurelle
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	public String updateUniteStructurelle() throws ClientException {
		return updateUniteStructurelle((UniteStructurelleNode) selectedNodeModel);
	}

	protected String updateUniteStructurelle(UniteStructurelleNode node) throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		if (!checkUniteStructurelleLockedByCurrentUser()) {
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}

		if (organigrammeService.checkParentListContainsChildren(node, node, Boolean.TRUE)) {
			facesMessages.add(
					StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get(
							"warn.organigrammeManager.uniteStructurelle.contains.children.in.parentList"));
			return null;
		}
		if (!confirmDialog) {
			// vérifie que le nom est unique sous les parents
			if (!organigrammeService.checkUniqueLabelInParent(documentManager, node)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("warn.organigrammeManager.same.element.name"));
				return null;
			}
			if (!organigrammeService.checkUniqueLabel(node)) {
				nameAlreadyUsed = true;
				return STViewConstant.ORGANIGRAMME_EDIT_UNITE_STRUCTURELLE;
			}
		} else {
			confirmDialog = false;
		}
		try {
			STServiceLocator.getSTUsAndDirectionService().updateUniteStructurelle(node);
		} finally {
			unlockOrganigrammeNode(node);
		}
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.uniteStructurelleModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		nameAlreadyUsed = false;
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * @throws ClientException
	 */
	protected boolean checkUniteStructurelleLockedByCurrentUser() throws ClientException {
		// vérifie qu'on a toujours le lock
		final OrganigrammeNode nodeToUpdate = selectedNodeModel;
		final OrganigrammeNode node = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(
				nodeToUpdate.getId());
		if (node == null || !isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return false;
		}
		return true;
	}

	/**
	 * Test si l'utilisateur peut modifier l'organigramme
	 * 
	 * @param ministereId
	 * @return
	 * @throws ClientException
	 */
	public boolean allowUpdateOrganigramme(String ministereId) throws ClientException {
		if (ssPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_UPDATER)) {
			return true;
		}
		if (ssPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_MINISTERE_UPDATER)) {
			return isUserInMinistere(ssPrincipal, ministereId);
		}
		return false;
	}

	/**
	 * Test si l'utilisateur peut ajouter un poste en fonction de son ministère
	 * 
	 * @param ministereId
	 * @return
	 */
	public boolean allowAddPoste(String ministereId) {
		if (ssPrincipal.isMemberOf(STBaseFunctionConstant.ALLOW_ADD_POSTE_ALL_MINISTERE)) {
			return true;
		}
		return isUserInMinistere(ssPrincipal, ministereId);
	}

	/**
	 * Test si l'utilisateur peut ajouter un élément en fonction de son ministère
	 * 
	 * @param ministereId
	 * @return
	 */
	public boolean allowAddNode(String ministereId) {
		if (ssPrincipal.isMemberOf(STBaseFunctionConstant.PROFIL_SGG)) {
			return true;
		}
		return isUserInMinistere(ssPrincipal, ministereId);
	}

	private boolean isUserInMinistere(final SSPrincipal ssPrincipal, final String ministereId) {
		Set<String> minIdSet = ssPrincipal.getMinistereIdSet();
		if (ministereId != null && minIdSet.contains(ministereId)) {
			return true;
		}
		return false;
	}

	public List<OrganigrammeNode> getCurrentGouvernementEntite() throws ClientException {
		List<OrganigrammeNode> entiteList = new ArrayList<OrganigrammeNode>();
		if (currentGouvernement != null) {

			// on réinitialise la liste des timbres
			newTimbre = null;
			GouvernementNode currentGouvernementNode = STServiceLocator.getSTGouvernementService().getGouvernement(
					currentGouvernement);
			List<OrganigrammeNode> gvtChildren = STServiceLocator.getOrganigrammeService().getChildrenList(
					documentManager, currentGouvernementNode, Boolean.TRUE);
			for (OrganigrammeNode child : gvtChildren) {
				if (child.isActive()) {
					entiteList.add(child);
				}
			}

		}
		return entiteList;
	}

	public List<SelectItem> getGouvernementList() throws ClientException {
		gouvernementList = new ArrayList<SelectItem>();

		List<GouvernementNode> gvtListNode = STServiceLocator.getSTGouvernementService().getGouvernementList();
		List<GouvernementNode> sublist;
		if (gvtListNode.size() > 2) {
			sublist = gvtListNode.subList(gvtListNode.size() - 2, gvtListNode.size());
		} else {
			sublist = gvtListNode;
		}

		for (OrganigrammeNode child : sublist) {
			gouvernementList.add(new SelectItem(child.getId(), child.getLabel()));
		}
		return gouvernementList;

	}

	/**
	 * @return the currentGouvernement
	 */
	public String getCurrentGouvernement() {
		return currentGouvernement;
	}

	/**
	 * @param currentGouvernement
	 *            the currentGouvernement to set
	 */
	public void setCurrentGouvernement(String currentGouvernement) {
		this.currentGouvernement = currentGouvernement;
	}

	/**
	 * @return the nextGouvernement
	 */
	public String getNextGouvernement() {
		return nextGouvernement;
	}

	/**
	 * @param nextGouvernement
	 *            the nextGouvernement to set
	 */
	public void setNextGouvernement(String nextGouvernement) {
		this.nextGouvernement = nextGouvernement;
	}

	/**
	 * Verrouille un élément d'organigramme
	 * 
	 * @param node
	 * @throws ClientException
	 */
	public boolean lockOrganigrammeNode(OrganigrammeNode node) throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		LOG.debug(STLogEnumImpl.MIGRATE_ENTITE_TEC, "Lock organigramme node : " + node.getLabel());
		Boolean result = false;
		if (organigrammeService.lockOrganigrammeNode(documentManager, node)) {
			result = true;
			// Affiche un message d'information
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.organigrammeManager.node.lock"));
		} else {
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.already.locked"));
		}
		return result;
	}

	/**
	 * Verrouille un élément d'organigramme
	 * 
	 * @param node
	 * @throws ClientException
	 */
	public boolean unlockOrganigrammeNode(OrganigrammeNode node) throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		LOG.debug(STLogEnumImpl.MIGRATE_ENTITE_TEC, "Unlock organigramme node : " + node.getLabel());
		Boolean result = false;
		if (organigrammeService.unlockOrganigrammeNode(node)) {
			result = true;
			// Affiche un message d'information
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.organigrammeManager.node.unlock"));
		} else {
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.node.unlock.failed"));
		}
		return result;
	}

	public String getImage(String nodeId, String selectionType, String selectionMultiple) {
		if (nodeId != null && !nodeId.isEmpty()) {
			if (!MULTI.equals(selectionMultiple)) {
				if (POSTE_TYPE.equals(selectionType)) {
					return "poste.png";
				} else if (USER_TYPE.equals(selectionType) || USER_MAIL_TYPE.equals(selectionType)) {
					return "user_16.png";
				} else {
					return "unite_structurelle.png";
				}
			} else {
				if (nodeId.startsWith(OrganigrammeNodeIdConverter.PREFIX_MIN)) {
					return "unite_structurelle.png";
				} else if (nodeId.startsWith(OrganigrammeNodeIdConverter.PREFIX_POSTE)) {
					return "poste.png";
				} else if (nodeId.startsWith(OrganigrammeNodeIdConverter.PREFIX_US)) {
					return "unite_structurelle.png";
				} else {
					return "user_16.png";
				}
			}
		}
		return "unite_structurelle.png";
	}

	/**
	 * Copie un noeud de l'organigramme
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String copyNode() throws ClientException {
		loadSelectedNodeModel();
		selectedNodeForCopy = selectedNodeModel;
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Colle un noeud de l'organigramme sans mettre les utilisateurs dans les postes
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String pasteNodeWithoutUser() throws ClientException {
		loadSelectedNodeModel();
		try {
			STServiceLocator.getOrganigrammeService().copyNodeWithoutUser(documentManager, selectedNodeForCopy,
					selectedNodeModel);
		} catch (LocalizedClientException e) {
			String message = resourcesAccessor.getMessages().get(e.getMessage());
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}
		resetNodeModel();
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Colle un noeud de l'organigramme avec les utilisateurs dans les postes
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String pasteNodeWithUsers() throws ClientException {
		loadSelectedNodeModel();
		try {
			STServiceLocator.getOrganigrammeService().copyNodeWithUsers(documentManager, selectedNodeForCopy,
					selectedNodeModel);
		} catch (LocalizedClientException e) {
			String message = resourcesAccessor.getMessages().get(e.getMessage());
			facesMessages.add(StatusMessage.Severity.WARN, message);
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}
		resetNodeModel();
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * @return the nameAlreadyUsed
	 */
	public boolean isNameAlreadyUsed() {
		return nameAlreadyUsed;
	}

	/**
	 * @param nameAlreadyUsed
	 *            the nameAlreadyUsed to set
	 */
	public void setNameAlreadyUsed(boolean nameAlreadyUsed) {
		this.nameAlreadyUsed = nameAlreadyUsed;
	}

	/**
	 * @return the confirmDialog
	 */
	public boolean isConfirmDialog() {
		return confirmDialog;
	}

	/**
	 * @param confirmDialog
	 *            the confirmDialog to set
	 */
	public void setConfirmDialog(boolean confirmDialog) {
		this.confirmDialog = confirmDialog;
	}

	/**
	 * true si un noeud est chargé pour être copier
	 * 
	 * @return
	 */
	public boolean isNodeSelectedForCopy() {
		if (selectedNodeForCopy != null) {
			return true;
		}
		return false;
	}

	public boolean isCurrentUserUnlocker(String locker) {
		return locker != null && !locker.isEmpty() && locker.equals(ssPrincipal.getName())
				|| ssPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_ADMIN_UNLOCKER);
	}

	public String unlockNode() throws ClientException {
		loadSelectedNodeModel();
		unlockOrganigrammeNode(getSelectedNodeModel());
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		return ORGANIGRAMME_VIEW;
	}

	public String enableOrDisableNode() throws ClientException {
		if (!isNodeActive()) {
			return enableNode();
		} else {
			return disableNode();
		}
	}

	private boolean isNodeActive() {
		return Boolean.valueOf(getNodeActive());
	}

	public String getNodeActive() {
		return nodeActive;
	}

	public void setNodeActive(String nodeActive) {
		this.nodeActive = nodeActive;
	}

	/**
	 * Clears single selection.
	 * <p>
	 * Must pass request parameters "suggestionSelectionOutputId" holding the value to show, and
	 * "suggestionSelectionHiddenId" holding the binding to model.
	 * <p>
	 * Additional optional request parameter "suggestionSelectionDeleteId" can be used to hide an area where the "clear"
	 * button is shown.
	 */
	public void clearSingleSelection(ActionEvent event) {
		UIComponent component = event.getComponent();
		if (component == null) {
			return;
		}
		UIComponent base = component;
		EditableValueHolder hiddenSelector = ComponentUtils.getComponent(base, suggestionSelectionHiddenId,
				EditableValueHolder.class);
		ValueHolder output = ComponentUtils.getComponent(base, suggestionSelectionOutputId, ValueHolder.class);

		if (hiddenSelector != null && output != null) {
			output.setValue("");
			hiddenSelector.setSubmittedValue("");
			// hide delete component if needed
			if (suggestionSelectionDeleteId != null) {
				UIComponent deleteComponent = ComponentUtils.getComponent(base, suggestionSelectionDeleteId,
						UIComponent.class);
				if (deleteComponent != null) {
					deleteComponent.setRendered(false);
				}
			}
		}
	}

	public Converter getGouvernementConverter() {
		return new Converter() {
			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof List) {
					String gvtId = (String) ((List) value).get(0);
					Converter gvtConverter = new OrganigrammeGvtIdToLabelConverter();
					return gvtConverter.getAsString(null, null, gvtId);
				}
				return null;
			}

			@Override
			public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
				return null;
			}
		};
	}

	public String getOrganigrammePopupSelectionType() {
		return organigrammePopupSelectionType;
	}

	public void setOrganigrammePopupSelectionType(String organigrammePopupSelectionType) {
		this.organigrammePopupSelectionType = organigrammePopupSelectionType;
	}

}
