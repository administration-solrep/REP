package fr.dila.reponses.web.administration;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingLineDetail;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.core.logging.ReponsesLoggingImpl;
import fr.dila.reponses.core.logging.ReponsesLoggingLineImpl;
import fr.dila.reponses.core.organigramme.ReponsesOrderTimbreNodeComparator;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.administration.organigramme.OrganigrammeManagerActionsBean;
import fr.dila.reponses.web.client.OrganigrammeNodeTimbreDTOImpl;
import fr.dila.reponses.web.client.TimbreDTO;
import fr.dila.ss.web.admin.SSMigrationManagerActionsBean;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * ActionBean de gestion des migrations
 */
@Name("migrationManagerActions")
@SerializedConcurrentAccess
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.APPLICATION + 1)
public class MigrationManagerActionsBean extends SSMigrationManagerActionsBean {

	/**
	 * Serial version UID
	 */
	private static final long						serialVersionUID				= -5568472931074128359L;

	private static final STLogger					LOGGER							= STLogFactory
																							.getLog(MigrationManagerActionsBean.class);
	private static final String						DTO_TOTAL						= "total";
	private static final Long						NOT_CALCULATED_YET				= -999L;
	private static final Long						ERROR_RECUP_COUNT				= -100L;
	private static final String						MIGRATION_STARTED_MESSAGE		= "info.organigrammeManager.migration.started";
	private static final String						RUNNING_MESSAGE					= "label.migration.running";
	private static final String						VIEW_SELECTION_TIMBRES			= "view_selection_timbres";
	private static final String						VIEW_RECAPITULATIF				= "view_recapitulatif_before_migration";
	private static final String						MINISTERE_INCHANGE_LABEL		= "Inchangé";

	@In(create = true, required = false)
	private AdministrationActionsBean				administrationActions;

	private Map<String, OrganigrammeNodeTimbreDTO>	mapOrganigrammeNodeTimbreDTO;
	private Map<String, OrganigrammeNodeTimbreDTO>	mapOldMinIdToNewNodeMinDTO;

	private String									currentReponsesLogging;
	private ReponsesLogging							currentReponsesLoggingDoc;
	private String									currentReponsesLoggingLine;
	private Boolean									updatingTimbre;
	private boolean									resetCountTimbres;
	private boolean									checkAllSignature				= true;
	private boolean									checkAllClosedDossiersMigration	= true;
	private boolean									fullCount						= true;

	private final SelectItemGroup					ancienGouvGroup					= new SelectItemGroup();
	private final SelectItemGroup					newGouvGroup					= new SelectItemGroup();

	/**
	 * Default constructor
	 */
	public MigrationManagerActionsBean() {
		// do nothing
	}

	public String goToViewSelectionTimbres() throws ClientException {

		List<GouvernementNode> gvtList = STServiceLocator.getSTGouvernementService().getActiveGouvernementList();

		GouvernementNode nextGNode = null;
		GouvernementNode currentGNode = null;
		if (gvtList.size() > 1) {
			nextGNode = (GouvernementNode) gvtList.get(gvtList.size() - 1);
			currentGNode = (GouvernementNode) gvtList.get(gvtList.size() - 2);
			organigrammeManagerActions.setCurrentGouvernement(currentGNode.getId());
			organigrammeManagerActions.setNextGouvernement(nextGNode.getId());
		}

		if (currentGNode != null
				&& !STServiceLocator.getSTPostesService().isPosteBdcInEachEntiteFromGouvernement(currentGNode)) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.previous.gvt.no.bdc"));
			return null;
		}

		// un nouveau gouvernement a été créé et chacune de ses entités contient un poste BDC
		// sinon message à l'utilisateur
		if (nextGNode == null) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.next.gvt.not.found"));
			return null;
		} else {
			if (!STServiceLocator.getSTPostesService().isPosteBdcInEachEntiteFromGouvernement(nextGNode)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("info.organigrammeManager.next.gvt.no.bdc"));
				return null;
			}
		}

		fullCount = true;
		return VIEW_SELECTION_TIMBRES;
	}

	public String goToViewRecapitulatif() throws ClientException {

		if (!checkNewTimbre()) {
			// error msg for user
			return null;
		}

		if (mapOldMinIdToNewNodeMinDTO == null) {
			mapOldMinIdToNewNodeMinDTO = new HashMap<String, OrganigrammeNodeTimbreDTO>();
		} else {
			mapOldMinIdToNewNodeMinDTO.clear();
		}

		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();

		Set<String> entiteNodeIds = new HashSet<String>(newTimbre.values());
		List<EntiteNode> newTimbreNode = ministeresService.getEntiteNodes(entiteNodeIds);
		Map<String, String> mapIdNewTimbreLabelNewTimbre = new HashMap<String, String>();
		for (OrganigrammeNode node : newTimbreNode) {
			if (node != null && node.getId() != null) {
				mapIdNewTimbreLabelNewTimbre.put(node.getId(), node.getLabel());
			}
		}

		StringBuilder labelNextTimbre = new StringBuilder();
		for (Entry<String, String> entryTimbres : newTimbre.entrySet()) {
			String idOldMin = entryTimbres.getKey();
			String idNewMin = entryTimbres.getValue();
			OrganigrammeNodeTimbreDTO minTimbreDTO = mapOrganigrammeNodeTimbreDTO.get(idOldMin);

			String newLabel = mapIdNewTimbreLabelNewTimbre.get(idNewMin);
			if (minTimbreDTO != null && newLabel == null) {
				minTimbreDTO.setLabelNextTimbre(MINISTERE_INCHANGE_LABEL);
			} else if (minTimbreDTO != null) {
				labelNextTimbre.setLength(0);
				if (mapOrganigrammeNodeTimbreDTO.containsKey(idNewMin)) {
					labelNextTimbre.append("[Ancien] ");
				} else {
					labelNextTimbre.append("[Nouveau] ");
				}

				labelNextTimbre.append(newLabel);
				minTimbreDTO.setLabelNextTimbre(labelNextTimbre.toString());
			}
		}
		fullCount = false;
		return VIEW_RECAPITULATIF;
	}

	/**
	 * Mise à jour des timbres
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String updateTimbre() throws ClientException {
		String currentGouvernement = organigrammeManagerActions.getCurrentGouvernement();
		String nextGouvernement = organigrammeManagerActions.getNextGouvernement();

		if (StringUtil.isEmpty(currentGouvernement) || StringUtil.isEmpty(nextGouvernement)
				|| currentGouvernement.equals(nextGouvernement)) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.migration.gvt.different"));
			return null;
		}

		if (!checkNewTimbre()) {
			// error msg for user
			return null;
		}

		// creation du log en bdd qui sera recupéré dans l'event
		UpdateTimbreService updateTimbreService = ReponsesServiceLocator.getUpdateTimbreService();
		OrganigrammeNodeTimbreDTO dto = mapOrganigrammeNodeTimbreDTO.get(DTO_TOTAL);
		String idLogging = updateTimbreService.createLogging(documentManager,
				dto.getCountMigrable() + dto.getCountSigne(), dto.getCountClose(), newTimbre, currentGouvernement,
				nextGouvernement);

		// Post commit event
		EventProducer eventProducer = STServiceLocator.getEventProducer();
		Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
		eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_NEW_TIMBRE_MAP, (Serializable) newTimbre);
		eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_NEXT_GVT, nextGouvernement);
		eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_CURRENT_GVT, currentGouvernement);
		eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_UNCHANGED_ENTITY, NEW_TIMBRE_UNCHANGED_ENTITY);
		eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_DEACTIVATE_ENTITY, NEW_TIMBRE_DEACTIVATE_ENTITY);
		eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_MAP, (Serializable) mapOrganigrammeNodeTimbreDTO);
		eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_CURRENT_LOGGING, idLogging);

		InlineEventContext eventContext = new InlineEventContext(documentManager, ssPrincipal, eventProperties);
		eventProducer.fireEvent(eventContext.newEvent(ReponsesEventConstant.MIGRATION_GVT_EVENT));

		facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(MIGRATION_STARTED_MESSAGE));

		setUpdatingTimbre(Boolean.TRUE);
		// redirection vers la vue de mise a jour en live
		return administrationActions.navigateToViewUpdateTimbre();
	}

	public String updateTimbreDiffere() throws ClientException {
		final String currentGouvernement = organigrammeManagerActions.getCurrentGouvernement();

		if (currentReponsesLoggingDoc != null && currentGouvernement == null) {
			organigrammeManagerActions.setCurrentGouvernement(currentReponsesLoggingDoc.getCurrentGouvernement());
		}

		resetCountTimbres = true;

		// redirection vers la vue de mise a jour en live
		return administrationActions.navigateToViewTimbre();
	}

	public String startUpdateTimbreDiffere() throws ClientException {
		currentReponsesLoggingDoc.setEndDate(null);
		currentReponsesLoggingDoc.setStatus(null);
		currentReponsesLoggingDoc.save(documentManager);
		EventProducer eventProducer = STServiceLocator.getEventProducer();
		Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
		eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_NEW_TIMBRE_MAP,
				(Serializable) currentReponsesLoggingDoc.geTimbresAsMap());
		eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_UNCHANGED_ENTITY, NEW_TIMBRE_UNCHANGED_ENTITY);
		eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_DEACTIVATE_ENTITY, NEW_TIMBRE_DEACTIVATE_ENTITY);
		eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_CURRENT_LOGGING, currentReponsesLoggingDoc.getId());
		InlineEventContext eventContext = new InlineEventContext(documentManager, ssPrincipal, eventProperties);
		eventProducer.fireEvent(eventContext.newEvent(ReponsesEventConstant.MIGRATION_GVT_CLOSE_EVENT));
		facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(MIGRATION_STARTED_MESSAGE));
		return administrationActions.navigateToViewUpdateTimbre();
	}

	/**
	 * Verifie que toutes les nouveaux timbres sont renseignés et
	 * 
	 * @return false si un timbre vaut "empty_value"
	 * @throws ClientException
	 */
	protected boolean checkNewTimbre() throws ClientException {
		if (newTimbre != null) {
			for (Entry<String, String> entry : newTimbre.entrySet()) {
				String value = entry.getValue();
				if (NEW_TIMBRE_EMPTY_VALUE.equals(value)) {
					facesMessages.add(StatusMessage.Severity.WARN,
							resourcesAccessor.getMessages().get("warn.organigrammeManager.migration.ministere.select"));
					return false;
				} else {
					// Si on migre vers un élément existant, on vérifie que lui ne change pas
					String valeurMigrationDestination = newTimbre.get(value);
					if (valeurMigrationDestination != null && issuAncienGouvernement(value)
							&& !valeurMigrationDestination.equalsIgnoreCase(NEW_TIMBRE_UNCHANGED_ENTITY)) {
						facesMessages.add(
								StatusMessage.Severity.WARN,
								resourcesAccessor.getMessages().get(
										"warn.organigrammeManager.migration.ministere.oldvaleur"));
						return false;
					}
				}
			}
		} else {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.organigrammeManager.migration.no.new.min"));
			return false;
		}
		return true;
	}

	private boolean issuAncienGouvernement(String codeMinistere) {
		boolean ancienGouvernement = false;
		int cptBoucle = 0;

		while (cptBoucle < ancienGouvGroup.getSelectItems().length && !ancienGouvernement) {
			SelectItem ancMinItem = ancienGouvGroup.getSelectItems()[cptBoucle];
			LOGGER.debug(STLogEnumImpl.DEFAULT, "On compare '" + codeMinistere + "' avec " + ancMinItem.getValue());
			if (ancMinItem.getValue().equals(codeMinistere)) {
				ancienGouvernement = true;
			}
			++cptBoucle;
		}

		return ancienGouvernement;
	}

	public boolean isUpdateTimbreAvailable() {
		List<GouvernementNode> gvtListNode;

		gvtListNode = STServiceLocator.getSTGouvernementService().getActiveGouvernementList();
		if (gvtListNode.size() <= 1) {
			return false;
		} else {
			if (gvtListNode.size() > 2) {
				gvtListNode = gvtListNode.subList(gvtListNode.size() - 2, gvtListNode.size());
			}
			return gvtListNode.get(0).isActive() && gvtListNode.get(1).isActive();

		}

	}

	/**
	 * lance le calcul des questions closes, à migrer... le calcul est fait si mapOrganigrammeNodeTimbreDTO est null ou
	 * que la variable pour le reset est vrai countInactiveChildren indique s'il faut récupérer les enfants inactifs du
	 * gouv passé en paramètre
	 * 
	 * @param gouvernement
	 * @param countInactiveChildren
	 * @return
	 * @throws ClientException
	 */
	private Set<OrganigrammeNodeTimbreDTO> startCountingTimbres(String gouvernement, boolean countInactiveChildren)
			throws ClientException {
		Set<OrganigrammeNodeTimbreDTO> entiteList = new TreeSet<OrganigrammeNodeTimbreDTO>(
				new ReponsesOrderTimbreNodeComparator());

		if (mapOrganigrammeNodeTimbreDTO == null || resetCountTimbres) {
			mapOrganigrammeNodeTimbreDTO = new HashMap<String, OrganigrammeNodeTimbreDTO>();

			// on réinitialise la liste des timbres
			newTimbre = new HashMap<String, String>();
			GouvernementNode currentGouvernementNode = STServiceLocator.getSTGouvernementService().getGouvernement(
					gouvernement);

			List<OrganigrammeNode> gvtChildren = STServiceLocator.getOrganigrammeService().getChildrenList(
					documentManager, currentGouvernementNode, Boolean.TRUE);

			for (OrganigrammeNode child : gvtChildren) {
				if (child.isActive() || countInactiveChildren) {
					OrganigrammeNodeTimbreDTOImpl dto = new OrganigrammeNodeTimbreDTOImpl(child, NOT_CALCULATED_YET,
							NOT_CALCULATED_YET, NOT_CALCULATED_YET, NOT_CALCULATED_YET);
					mapOrganigrammeNodeTimbreDTO.put(child.getId().toString(), dto);
					newTimbre.put(child.getId().toString(), OrganigrammeManagerActionsBean.NEW_TIMBRE_UNCHANGED_ENTITY);
					entiteList.add(dto);
				}
			}

			OrganigrammeNodeTimbreDTOImpl dto = new OrganigrammeNodeTimbreDTOImpl(NOT_CALCULATED_YET,
					NOT_CALCULATED_YET, NOT_CALCULATED_YET, NOT_CALCULATED_YET);
			dto.setId(DTO_TOTAL);
			dto.setOrder(99999);
			mapOrganigrammeNodeTimbreDTO.put(DTO_TOTAL, dto);
			entiteList.add(dto);
			Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
			eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_CURRENT_GVT,
					(Serializable) mapOrganigrammeNodeTimbreDTO);
			InlineEventContext eventContext = new InlineEventContext(documentManager, ssPrincipal, eventProperties);
			STServiceLocator.getEventProducer().fireEvent(
					eventContext.newEvent(ReponsesEventConstant.MIGRATION_COUNT_INFOS_TIMBRES_EVENT));
			resetCountTimbres = false;
		} else {
			if (fullCount) {
				entiteList.addAll(mapOrganigrammeNodeTimbreDTO.values());
			} else {
				long nbTotalClosed = 0, nbTotalSigned = 0, nbTotalMigrable = 0, nbTotalModFdr = 0;
				for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapOrganigrammeNodeTimbreDTO.entrySet()) {
					if (!MINISTERE_INCHANGE_LABEL.equals(entry.getValue().getLabelNextTimbre())
							&& !DTO_TOTAL.equals(entry.getKey())) {
						entiteList.add(entry.getValue());
						OrganigrammeNodeTimbreDTO timbreDto = entry.getValue();
						nbTotalClosed += timbreDto.getCountClose();
						nbTotalSigned += timbreDto.getCountSigne();
						nbTotalMigrable += timbreDto.getCountMigrable();
						nbTotalModFdr += timbreDto.getCountModelFDR();
					}
				}
				OrganigrammeNodeTimbreDTO total = mapOrganigrammeNodeTimbreDTO.get(DTO_TOTAL);
				total.setCountClose(nbTotalClosed);
				total.setCountSigne(nbTotalSigned);
				total.setCountMigrable(nbTotalMigrable);
				total.setCountModelFDR(nbTotalModFdr);
				entiteList.add(mapOrganigrammeNodeTimbreDTO.get(DTO_TOTAL));
			}
		}
		return entiteList;

	}

	public Set<OrganigrammeNodeTimbreDTO> getGouvernementForUpdateTimbre(String gouvernement) throws ClientException {
		Set<OrganigrammeNodeTimbreDTO> entiteList = new TreeSet<OrganigrammeNodeTimbreDTO>();
		if (gouvernement != null) {
			entiteList = startCountingTimbres(gouvernement, false);
		}
		return entiteList;
	}

	public List<OrganigrammeNodeTimbreDTO> getCurrentGouvernementForUpdateTimbre() throws ClientException {
		String currentGouvernement = organigrammeManagerActions.getCurrentGouvernement();
		return new ArrayList<OrganigrammeNodeTimbreDTO>(getGouvernementForUpdateTimbre(currentGouvernement));
	}

	public String viewCurrentTimbreUpdate() throws ClientException {
		return administrationActions.navigateToViewUpdateTimbre();
	}

	public ReponsesLogging getCurrentTimbreUpdate() throws ClientException {
		return ReponsesServiceLocator.getUpdateTimbreService().getMigrationEnCours(documentManager);
	}

	public Boolean isMigrationEnCours() throws ClientException {
		return ReponsesServiceLocator.getUpdateTimbreService().isMigrationEnCours(documentManager);
	}

	public List<ReponsesLogging> getAllReponsesLogging() throws ClientException {
		return ReponsesServiceLocator.getUpdateTimbreService().getAllReponsesLogging(documentManager);
	}

	public String setCurrentLog(String idReponsesLogging) throws ClientException {
		currentReponsesLoggingLine = null;
		currentReponsesLoggingDoc = null;
		if (!StringUtil.isEmpty(idReponsesLogging)) {
			final DocumentModel doc = documentManager.getDocument(new IdRef(idReponsesLogging));
			currentReponsesLoggingDoc = doc.getAdapter(ReponsesLogging.class);
		}
		currentReponsesLogging = idReponsesLogging;
		return null;
	}

	public ReponsesLogging getCurrentReponsesLoggingDoc() {
		return currentReponsesLoggingDoc;
	}

	public String getReponsesLoggingNextGouvernement() throws ClientException {

		OrganigrammeNode gouv = STServiceLocator.getSTGouvernementService().getGouvernement(
				currentReponsesLoggingDoc.getNextGouvernement());
		if (gouv == null) {
			return null;
		} else {
			return gouv.getLabel();
		}
	}

	public String getReponsesLoggingCurrentGouvernement() throws ClientException {

		OrganigrammeNode gouv = STServiceLocator.getSTGouvernementService().getGouvernement(
				currentReponsesLoggingDoc.getCurrentGouvernement());
		if (gouv == null) {
			return null;
		} else {
			return gouv.getLabel();
		}

	}

	public List<TimbreDTO> getTimbreList() throws ClientException {
		final List<TimbreDTO> list = new ArrayList<TimbreDTO>();
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		Map<String, String> map = currentReponsesLoggingDoc.geTimbresAsMap();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String currentGouvernementLabel = null;
			String nextGouvernementLabel = entry.getValue();
			OrganigrammeNode currentNode = ministeresService.getEntiteNode(entry.getKey());
			if (currentNode != null) {
				currentGouvernementLabel = currentNode.getLabel();
			}
			OrganigrammeNode nextNode = ministeresService.getEntiteNode(entry.getValue());
			if (nextNode != null) {
				nextGouvernementLabel = nextNode.getLabel();
			}
			if (currentNode != null) {
				list.add(new TimbreDTO(currentGouvernementLabel, nextGouvernementLabel, ((EntiteNode) currentNode)
						.getOrdre()));
			}
		}

		Collections.sort(list, new Comparator<TimbreDTO>() {
			@Override
			public int compare(TimbreDTO timbre1, TimbreDTO timbre2) {
				return timbre1.getProtocolarOrder().compareTo(timbre2.getProtocolarOrder());
			}
		});

		return list;
	}

	public List<ReponsesLoggingLine> getCurrentReponsesLoggingLine() throws ClientException {
		return ReponsesServiceLocator.getUpdateTimbreService().getAllReponsesLoggingLine(documentManager,
				currentReponsesLogging);
	}

	public Map<String, String> getCurrentReponsesLoggingTimbre() throws ClientException {
		return ReponsesServiceLocator.getUpdateTimbreService().getReponsesLoggingTimbre(documentManager,
				currentReponsesLogging);
	}

	public String setCurrentLogLine(String idReponsesLoggingLine) {
		currentReponsesLoggingLine = idReponsesLoggingLine;
		return null;
	}

	public List<ReponsesLoggingLineDetail> getCurrentReponsesLoggingDetail() throws ClientException {
		return ReponsesServiceLocator.getUpdateTimbreService().getAllReponsesLoggingLineDetail(documentManager,
				currentReponsesLoggingLine);
	}

	public String getPourcentageAvancement(ReponsesLogging reponsesLogging) throws ReponsesException {
		return getProgressionLogging(reponsesLogging);
	}

	public String getPourcentageAvancementLine(ReponsesLoggingLine reponsesLoggingLine) throws ReponsesException {
		return getProgressionLogging(reponsesLoggingLine);
	}

	private String getProgressionLogging(Serializable repLogging) throws ReponsesException {
		Long previsionalCount = null;
		Long endCount = null;
		if (repLogging instanceof ReponsesLogging) {
			ReponsesLogging reponsesLogging = (ReponsesLoggingImpl) repLogging;
			previsionalCount = reponsesLogging.getPrevisionalCount();
			endCount = reponsesLogging.getEndCount();
		} else if (repLogging instanceof ReponsesLoggingLine) {
			ReponsesLoggingLine reponsesLoggingLine = (ReponsesLoggingLineImpl) repLogging;
			previsionalCount = reponsesLoggingLine.getPrevisionalCount();
			endCount = reponsesLoggingLine.getEndCount();
		} else {
			throw new ReponsesException(
					"Le paramètre de la méthode incorrect. Instances de ReponsesLoggingLine ou ReponsesLogging attendu !");
		}
		if (previsionalCount != null && endCount != null) {
			if (previsionalCount > 0) {
				Double value = endCount.doubleValue() / previsionalCount.doubleValue() * 100;
				DecimalFormat decimalFormat = new DecimalFormat();
				decimalFormat.setMaximumFractionDigits(2);
				return " " + decimalFormat.format(value) + " %";
			} else {
				return " 100 %";
			}

		}
		return resourcesAccessor.getMessages().get(RUNNING_MESSAGE);
	}

	public boolean displayMigrationQuestionClose() {
		if (currentReponsesLoggingDoc != null && currentReponsesLoggingDoc.getClosePrevisionalCount() != null) {
			if (currentReponsesLoggingDoc.getCloseEndCount() == null) {
				return true;
			} else {
				return currentReponsesLoggingDoc.getClosePrevisionalCount().compareTo(
						currentReponsesLoggingDoc.getCloseEndCount()) > 0;
			}
		}
		return false;
	}

	public void setUpdatingTimbre(Boolean updatingTimbre) {
		this.updatingTimbre = updatingTimbre;
	}

	public Boolean isUpdatingTimbre() {
		return updatingTimbre;
	}

	/**
	 * indique si la mise à jour du tableau est activée. Elle est active tant qu'un noeud est toujours en attente d'une
	 * de ses valeurs
	 * 
	 * @return
	 */
	public Boolean isPollCountActivated() {
		if (mapOrganigrammeNodeTimbreDTO == null) {

			try {
				String currentGouvernement = organigrammeManagerActions.getCurrentGouvernement();
				if (currentGouvernement == null) {
					currentGouvernement = STServiceLocator.getSTGouvernementService().getCurrentGouvernement().getId();
					organigrammeManagerActions.setCurrentGouvernement(currentGouvernement);
				}
				startCountingTimbres(currentGouvernement, false);
			} catch (ClientException exc) {
				LOGGER.error(documentManager, ReponsesLogEnumImpl.FAIL_COUNT_TIMBRES_FONC, exc);
				return false;
			}
			return true;
		}

		for (OrganigrammeNodeTimbreDTO node : mapOrganigrammeNodeTimbreDTO.values()) {
			if (!isCountCalculated(node.getCountClose()) || !isCountCalculated(node.getCountMigrable())
					|| !isCountCalculated(node.getCountModelFDR()) || !isCountCalculated(node.getCountSigne())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine si la mise à jour du tableau doit etre activée. Elle est active tant qu'un noeud est toujours en
	 * attente d'une de ses valeurs
	 * 
	 * @return
	 */
	public Boolean checkPollCountActivation() {
		if (isPollCountActivated()) {
			return true;
		}

		if (!resetCountTimbres) {
			resetCountTimbres = true;
			// On garde le poll activated pour recharger une dernière fois la page
			return true;
		}
		return false;
	}

	public Boolean isErrorOccurred() {
		if (mapOrganigrammeNodeTimbreDTO == null) {
			return false;
		}

		for (OrganigrammeNodeTimbreDTO node : mapOrganigrammeNodeTimbreDTO.values()) {
			if (isCountError(node.getCountClose()) || isCountError(node.getCountMigrable())
					|| isCountError(node.getCountModelFDR()) || isCountError(node.getCountSigne())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Normalise l'affichage de la valeur. Si valeur = -100 : affiche Err sinon affiche la valeur
	 * 
	 * @param count
	 * @return
	 */
	public String getStringFromCount(Long count) {
		if (isCountError(count)) {
			return "Err";
		} else {
			return count.toString();
		}
	}

	/**
	 * Vérifie si count != -999
	 * 
	 * @param count
	 * @return
	 */
	private Boolean isCountCalculated(Long count) {
		return NOT_CALCULATED_YET.compareTo(count) != 0;
	}

	/**
	 * Vérifie si count = -100
	 * 
	 * @param count
	 * @return
	 */
	private Boolean isCountError(Long count) {
		return ERROR_RECUP_COUNT.compareTo(count) == 0;
	}

	public List<SelectItem> getNewTimbreList() throws ClientException {
		newTimbreList = new ArrayList<SelectItem>();

		String currentGouvernement = organigrammeManagerActions.getCurrentGouvernement();
		String nextGouvernement = organigrammeManagerActions.getNextGouvernement();

		if (nextGouvernement != null) {
			// Ajoute l'entrée : Sélectionner une valeur
			newTimbreList.add(new SelectItem(NEW_TIMBRE_EMPTY_VALUE, "Sélectionner une valeur"));
			// Ajoute l'entrée : Ministère inchangé
			newTimbreList.add(new SelectItem(NEW_TIMBRE_UNCHANGED_ENTITY, "Ministère inchangé"));
			// Ajoute l'entrée : Ministère désactivé
			newTimbreList.add(new SelectItem(NEW_TIMBRE_DEACTIVATE_ENTITY, "Ministère désactivé"));

			// On regroupe les ministères de l'ancien gouvernement (cf FEV
			// 399 : pour migrer vers un timbre existant)
			GouvernementNode ancienGouvernementNode = STServiceLocator.getSTGouvernementService().getGouvernement(
					currentGouvernement);
			ancienGouvGroup.setLabel(ancienGouvernementNode.getLabel());

			List<OrganigrammeNode> gvtAncChildren = STServiceLocator.getOrganigrammeService().getChildrenList(
					documentManager, ancienGouvernementNode, Boolean.TRUE);
			SelectItem[] lstAncMinisteres = new SelectItem[gvtAncChildren.size()];
			int i = 0;
			for (OrganigrammeNode child : gvtAncChildren) {
				lstAncMinisteres[i] = new SelectItem(child.getId(), child.getLabel());
				++i;
			}
			ancienGouvGroup.setSelectItems(lstAncMinisteres);
			newTimbreList.add(ancienGouvGroup);

			GouvernementNode newGouvernementNode = STServiceLocator.getSTGouvernementService().getGouvernement(
					nextGouvernement);
			List<OrganigrammeNode> gvtChildren = STServiceLocator.getOrganigrammeService().getChildrenList(
					documentManager, newGouvernementNode, Boolean.TRUE);
			// On regroupe les ministères du nouveau gouvernement s'il n'est pas vide
			if (gvtChildren != null && !gvtChildren.isEmpty()) {
				newGouvGroup.setLabel(newGouvernementNode.getLabel());
				SelectItem[] lstNouveauMinisteres = new SelectItem[gvtChildren.size()];
				i = 0;
				for (OrganigrammeNode child : gvtChildren) {
					lstNouveauMinisteres[i] = new SelectItem(child.getId(), child.getLabel());
					++i;
				}
				newGouvGroup.setSelectItems(lstNouveauMinisteres);
				newTimbreList.add(newGouvGroup);
			}
		}
		return newTimbreList;
	}

	public String getNewTimbreLabelForEntite(OrganigrammeNodeTimbreDTO oldEntite) {
		return oldEntite.getLabelNextTimbre();
	}

	public void checkAllBriserSignature() {
		for (OrganigrammeNodeTimbreDTO timbre : mapOrganigrammeNodeTimbreDTO.values()) {
			timbre.setBreakingSeal(!checkAllSignature);
		}
	}

	public boolean isCheckAllSignature() {
		return checkAllSignature;
	}

	public void setCheckAllSignature(boolean checkAllSignature) {
		this.checkAllSignature = checkAllSignature;
	}

	public void checkAllClosedDossiersMigration() {
		for (OrganigrammeNodeTimbreDTO timbre : mapOrganigrammeNodeTimbreDTO.values()) {
			timbre.setMigratingDossiersClos(!checkAllClosedDossiersMigration);
		}
	}

	public boolean isCheckAllClosedDossiersMigration() {
		return checkAllClosedDossiersMigration;
	}

	public void setCheckAllClosedDossiersMigration(boolean checkAllClosedDossiersMigration) {
		this.checkAllClosedDossiersMigration = checkAllClosedDossiersMigration;
	}
}
