package fr.dila.reponses.ui.services.organigramme;

import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.CURRENT_GOUVERNEMENT;
import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.NEXT_GOUVERNEMENT;
import static java.util.Optional.ofNullable;

import fr.dila.reponses.api.client.OrganigrammeNodeTimbreDTO;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingLineDetail;
import fr.dila.reponses.api.logging.ReponsesLoggingStatusEnum;
import fr.dila.reponses.api.service.UpdateTimbreService;
import fr.dila.reponses.core.logging.ReponsesLoggingImpl;
import fr.dila.reponses.core.logging.ReponsesLoggingLineImpl;
import fr.dila.reponses.core.organigramme.ReponsesOrderTimbreNodeComparator;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.core.service.organigramme.OrganigrammeNodeTimbreDTOImpl;
import fr.dila.reponses.ui.bean.DetailHistoriqueMAJTimbresDTO;
import fr.dila.reponses.ui.bean.DetailHistoriqueMAJTimbresList;
import fr.dila.reponses.ui.bean.DetailMigrationHistoriqueMAJTimbresDTO;
import fr.dila.reponses.ui.bean.DetailMigrationHistoriqueMAJTimbresList;
import fr.dila.reponses.ui.bean.HistoriqueMAJTimbresDTO;
import fr.dila.reponses.ui.bean.HistoriqueMAJTimbresList;
import fr.dila.reponses.ui.bean.MiseAJourTimbresDetailDTO;
import fr.dila.reponses.ui.bean.MiseAJourTimbresParametrage;
import fr.dila.reponses.ui.bean.MiseAJourTimbresRecapitulatifDTO;
import fr.dila.reponses.ui.bean.MiseAJourTimbresRecapitulatifList;
import fr.dila.reponses.ui.th.bean.MisesAJourTimbresFormDTO;
import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.organigramme.SSMigrationManagerUIServiceImpl;
import fr.dila.ss.ui.services.organigramme.SSOrganigrammeManagerUIServiceImpl;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.bean.SelectValueGroupDTO;
import fr.dila.st.ui.th.bean.EntiteForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

/**
 * ActionBean de gestion des migrations
 */
public class ReponsesMigrationManagerUIServiceImpl
    extends SSMigrationManagerUIServiceImpl
    implements ReponsesMigrationManagerUIService {
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesMigrationManagerUIServiceImpl.class);
    private static final String DTO_TOTAL = "total";
    private static final Long NOT_CALCULATED_YET = -999L;
    private static final Long ERROR_RECUP_COUNT = -100L;
    private static final String MIGRATION_STARTED_MESSAGE = "info.organigrammeManager.migration.started";
    private static final String RUNNING_MESSAGE = "label.migration.running";
    private static final String MINISTERE_INCHANGE_LABEL = "Inchangé";

    private Map<String, OrganigrammeNodeTimbreDTO> mapOrganigrammeNodeTimbreDTO;
    private Map<String, OrganigrammeNodeTimbreDTO> mapOldMinIdToNewNodeMinDTO;

    private String currentReponsesLogging;
    private ReponsesLogging currentReponsesLoggingDoc;
    private String currentReponsesLoggingLine;
    private Boolean updatingTimbre;
    private boolean resetCountTimbres;
    private boolean checkAllSignature = true;
    private boolean checkAllClosedDossiersMigration = true;
    private boolean fullCount = true;

    // SelectValueGroupDTOs
    private SelectValueGroupDTO ancienGouvGroup = new SelectValueGroupDTO();
    private SelectValueGroupDTO newGouvGroup = new SelectValueGroupDTO();

    /**
     * Default constructor
     */
    public ReponsesMigrationManagerUIServiceImpl() {
        // do nothing
    }

    @Override
    public boolean initDataForSelectionTimbres(SpecificContext context) {
        List<GouvernementNode> gvtList = STServiceLocator.getSTGouvernementService().getActiveGouvernementList();

        GouvernementNode nextGNode = null;
        GouvernementNode currentGNode = null;
        if (gvtList.size() > 1) {
            nextGNode = gvtList.get(gvtList.size() - 1);
            currentGNode = gvtList.get(gvtList.size() - 2);
            context.putInContextData(CURRENT_GOUVERNEMENT, currentGNode.getId());
            context.putInContextData(NEXT_GOUVERNEMENT, nextGNode.getId());
        }
        List<String> ministereSansBDC = STServiceLocator
            .getSTPostesService()
            .getEntiteWithoutBDCInGouvernement(currentGNode);

        if (currentGNode != null && CollectionUtils.isNotEmpty(ministereSansBDC)) {
            context
                .getMessageQueue()
                .addErrorToQueue(
                    ResourceHelper.getString(
                        "info.organigrammeManager.previous.gvt.no.bdc",
                        StringUtils.join(ministereSansBDC, ", ")
                    )
                );
            return false;
        }

        // un nouveau gouvernement a été créé et chacune de ses entités contient un poste BDC sinon message à l'utilisateur
        if (nextGNode == null) {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("info.organigrammeManager.next.gvt.not.found"));
            return false;
        } else {
            ministereSansBDC = STServiceLocator.getSTPostesService().getEntiteWithoutBDCInGouvernement(nextGNode);
            if (CollectionUtils.isNotEmpty(ministereSansBDC)) {
                context
                    .getMessageQueue()
                    .addErrorToQueue(
                        ResourceHelper.getString(
                            "info.organigrammeManager.previous.gvt.no.bdc",
                            StringUtils.join(ministereSansBDC, ", ")
                        )
                    );
                return false;
            }
        }

        if (!isUpdateTimbreAvailable()) {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("warn.organigrammeManager.migration.no.new.min"));
        }
        fullCount = true;
        return true;
    }

    @Override
    public void checkBeforeRecapitulatif(SpecificContext context) {
        if (!checkNewTimbre(context)) {
            // error msg for user
            return;
        }

        if (mapOldMinIdToNewNodeMinDTO == null) {
            mapOldMinIdToNewNodeMinDTO = new HashMap<>();
        } else {
            mapOldMinIdToNewNodeMinDTO.clear();
        }

        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();

        Set<String> entiteNodeIds = new HashSet<>(newTimbre.values());
        List<EntiteNode> newTimbreNode = ministeresService.getEntiteNodes(entiteNodeIds);
        Map<String, String> mapIdNewTimbreLabelNewTimbre = newTimbreNode
            .stream()
            .filter(node -> node != null && node.getId() != null)
            .collect(Collectors.toMap(OrganigrammeNode::getId, OrganigrammeNode::getLabel));

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
    }

    @Override
    public String updateTimbre(SpecificContext context) {
        String currentGouvernement = null;
        String nextGouvernement = null;
        List<GouvernementNode> gvtList = STServiceLocator.getSTGouvernementService().getActiveGouvernementList();
        if (gvtList.size() > 1) {
            currentGouvernement = gvtList.get(gvtList.size() - 2).getId();
            nextGouvernement = gvtList.get(gvtList.size() - 1).getId();
        }

        if (
            StringUtils.isEmpty(currentGouvernement) ||
            StringUtils.isEmpty(nextGouvernement) ||
            currentGouvernement.equals(nextGouvernement)
        ) {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("warn.organigrammeManager.migration.gvt.different"));
            return null;
        }

        if (!checkNewTimbre(context)) {
            // error msg for user
            return null;
        }

        // creation du log en bdd qui sera recupéré dans l'event
        UpdateTimbreService updateTimbreService = ReponsesServiceLocator.getUpdateTimbreService();
        OrganigrammeNodeTimbreDTO dto = mapOrganigrammeNodeTimbreDTO.get(DTO_TOTAL);
        CoreSession session = context.getSession();
        String idLogging = updateTimbreService.createLogging(
            session,
            dto.getCountMigrable() + dto.getCountSigne(),
            dto.getCountClose(),
            newTimbre,
            currentGouvernement,
            nextGouvernement
        );

        // Post commit event
        EventProducer eventProducer = STServiceLocator.getEventProducer();
        Map<String, Serializable> eventProperties = new HashMap<>();
        eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_NEW_TIMBRE_MAP, (Serializable) newTimbre);
        eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_NEXT_GVT, nextGouvernement);
        eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_CURRENT_GVT, currentGouvernement);
        eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_UNCHANGED_ENTITY, NEW_TIMBRE_UNCHANGED_ENTITY);
        eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_DEACTIVATE_ENTITY, NEW_TIMBRE_DEACTIVATE_ENTITY);
        eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_MAP, (Serializable) mapOrganigrammeNodeTimbreDTO);
        eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_CURRENT_LOGGING, idLogging);

        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        InlineEventContext eventContext = new InlineEventContext(session, ssPrincipal, eventProperties);
        eventProducer.fireEvent(eventContext.newEvent(SSEventConstant.MIGRATION_GVT_EVENT));

        setUpdatingTimbre(Boolean.TRUE);
        // redirection vers la vue de mise a jour en live
        //		return administrationActions.navigateToViewUpdateTimbre();
        return ResourceHelper.getString(MIGRATION_STARTED_MESSAGE);
    }

    @Override
    public String startUpdateTimbreDiffere(CoreSession session, SSPrincipal ssPrincipal) {
        currentReponsesLoggingDoc.setEndDate(null);
        currentReponsesLoggingDoc.setStatus(null);
        currentReponsesLoggingDoc.save(session);
        EventProducer eventProducer = STServiceLocator.getEventProducer();
        Map<String, Serializable> eventProperties = new HashMap<>();
        eventProperties.put(
            ReponsesEventConstant.MIGRATION_GVT_NEW_TIMBRE_MAP,
            (Serializable) currentReponsesLoggingDoc.geTimbresAsMap()
        );
        eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_UNCHANGED_ENTITY, NEW_TIMBRE_UNCHANGED_ENTITY);
        eventProperties.put(ReponsesEventConstant.NEW_TIMBRE_DEACTIVATE_ENTITY, NEW_TIMBRE_DEACTIVATE_ENTITY);
        eventProperties.put(ReponsesEventConstant.MIGRATION_GVT_CURRENT_LOGGING, currentReponsesLoggingDoc.getId());
        InlineEventContext eventContext = new InlineEventContext(session, ssPrincipal, eventProperties);
        eventProducer.fireEvent(eventContext.newEvent(ReponsesEventConstant.MIGRATION_GVT_CLOSE_EVENT));
        return ResourceHelper.getString(MIGRATION_STARTED_MESSAGE);
    }

    @Override
    public boolean checkNewTimbre(SpecificContext context) {
        if (newTimbre != null) {
            for (Entry<String, String> entry : newTimbre.entrySet()) {
                String value = entry.getValue();
                if (NEW_TIMBRE_EMPTY_VALUE.equals(value)) {
                    context
                        .getMessageQueue()
                        .addErrorToQueue(
                            ResourceHelper.getString("warn.organigrammeManager.migration.ministere.select")
                        );
                    return false;
                } else {
                    // Si on migre vers un élément existant, on vérifie que lui ne change pas
                    String valeurMigrationDestination = newTimbre.get(value);
                    if (
                        valeurMigrationDestination != null &&
                        issuAncienGouvernement(value) &&
                        !valeurMigrationDestination.equalsIgnoreCase(NEW_TIMBRE_UNCHANGED_ENTITY)
                    ) {
                        context
                            .getMessageQueue()
                            .addErrorToQueue(
                                ResourceHelper.getString("warn.organigrammeManager.migration.ministere.oldvaleur")
                            );
                        return false;
                    }
                }
            }
        } else {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("warn.organigrammeManager.migration.no.new.min"));
            return false;
        }
        return true;
    }

    private boolean issuAncienGouvernement(String codeMinistere) {
        boolean ancienGouvernement = false;
        int cptBoucle = 0;

        while (cptBoucle < ancienGouvGroup.getSelectValues().size() && !ancienGouvernement) {
            SelectValueDTO ancMinItem = ancienGouvGroup.getSelectValues().get(cptBoucle);
            LOGGER.debug(STLogEnumImpl.DEFAULT, "On compare '" + codeMinistere + "' avec " + ancMinItem.getId());
            if (ancMinItem.getId().equals(codeMinistere)) {
                ancienGouvernement = true;
            }
            ++cptBoucle;
        }

        return ancienGouvernement;
    }

    @Override
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
     * @param context
     * @return
     * @
     */
    private Set<OrganigrammeNodeTimbreDTO> startCountingTimbres(
        String gouvernement,
        boolean countInactiveChildren,
        SpecificContext context
    ) {
        Set<OrganigrammeNodeTimbreDTO> entiteList = new TreeSet<>(new ReponsesOrderTimbreNodeComparator());

        if (mapOrganigrammeNodeTimbreDTO == null || resetCountTimbres) {
            mapOrganigrammeNodeTimbreDTO = new HashMap<>();
            CoreSession session = context.getSession();
            SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();

            // on réinitialise la liste des timbres
            newTimbre = new HashMap<>();
            GouvernementNode currentGouvernementNode = STServiceLocator
                .getSTGouvernementService()
                .getGouvernement(gouvernement);

            List<OrganigrammeNode> gvtChildren = STServiceLocator
                .getOrganigrammeService()
                .getChildrenList(session, currentGouvernementNode, Boolean.TRUE);

            for (OrganigrammeNode child : gvtChildren) {
                if (child.isActive() || countInactiveChildren) {
                    OrganigrammeNodeTimbreDTOImpl dto = new OrganigrammeNodeTimbreDTOImpl(
                        child,
                        NOT_CALCULATED_YET,
                        NOT_CALCULATED_YET,
                        NOT_CALCULATED_YET,
                        NOT_CALCULATED_YET
                    );
                    mapOrganigrammeNodeTimbreDTO.put(child.getId(), dto);
                    newTimbre.put(child.getId(), SSOrganigrammeManagerUIServiceImpl.NEW_TIMBRE_UNCHANGED_ENTITY);
                    entiteList.add(dto);
                }
            }

            OrganigrammeNodeTimbreDTOImpl dto = new OrganigrammeNodeTimbreDTOImpl(
                NOT_CALCULATED_YET,
                NOT_CALCULATED_YET,
                NOT_CALCULATED_YET,
                NOT_CALCULATED_YET
            );
            dto.setId(DTO_TOTAL);
            dto.setOrder(99999);
            dto.setLabel("");
            mapOrganigrammeNodeTimbreDTO.put(DTO_TOTAL, dto);
            entiteList.add(dto);
            Map<String, Serializable> eventProperties = new HashMap<>();
            eventProperties.put(
                ReponsesEventConstant.MIGRATION_GVT_CURRENT_GVT,
                (Serializable) mapOrganigrammeNodeTimbreDTO
            );
            InlineEventContext eventContext = new InlineEventContext(session, ssPrincipal, eventProperties);
            STServiceLocator
                .getEventProducer()
                .fireEvent(eventContext.newEvent(ReponsesEventConstant.MIGRATION_COUNT_INFOS_TIMBRES_EVENT));
            resetCountTimbres = false;
        } else {
            if (fullCount) {
                entiteList.addAll(mapOrganigrammeNodeTimbreDTO.values());
            } else {
                long nbTotalClosed = 0;
                long nbTotalSigned = 0;
                long nbTotalMigrable = 0;
                long nbTotalModFdr = 0;
                for (Entry<String, OrganigrammeNodeTimbreDTO> entry : mapOrganigrammeNodeTimbreDTO.entrySet()) {
                    if (
                        !MINISTERE_INCHANGE_LABEL.equals(entry.getValue().getLabelNextTimbre()) &&
                        !DTO_TOTAL.equals(entry.getKey())
                    ) {
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

    private Set<OrganigrammeNodeTimbreDTO> getGouvernementForUpdateTimbre(
        String gouvernement,
        SpecificContext context
    ) {
        Set<OrganigrammeNodeTimbreDTO> entiteList = new TreeSet<>();
        if (gouvernement != null) {
            entiteList = startCountingTimbres(gouvernement, false, context);
        }
        return entiteList;
    }

    @Override
    public List<OrganigrammeNodeTimbreDTO> getCurrentGouvernementForUpdateTimbre(SpecificContext context) {
        String currentGouvernement = context.getFromContextData(CURRENT_GOUVERNEMENT);
        return new ArrayList<>(getGouvernementForUpdateTimbre(currentGouvernement, context));
    }

    @Override
    public MiseAJourTimbresParametrage getMiseAJourTimbresParametrage(SpecificContext context) {
        MiseAJourTimbresParametrage dto = new MiseAJourTimbresParametrage();
        resetCountTimbres = true;
        dto.setMinisteres(
            getCurrentGouvernementForUpdateTimbre(context)
                .stream()
                .filter(timbre -> !DTO_TOTAL.equals(timbre.getId()))
                .map(this::convertTimbreToEntiteForm)
                .collect(Collectors.toList())
        );
        return dto;
    }

    private EntiteForm convertTimbreToEntiteForm(OrganigrammeNodeTimbreDTO timbre) {
        EntiteForm entite = new EntiteForm();
        entite.setIdentifiant(timbre.getId());
        entite.setAppellation(timbre.getLabel());
        return entite;
    }

    @Override
    public MiseAJourTimbresRecapitulatifList getMiseAJourTimbresRecapitulatifList(SpecificContext context) {
        MiseAJourTimbresRecapitulatifList dto = new MiseAJourTimbresRecapitulatifList(context);
        dto.setListe(
            getCurrentGouvernementForUpdateTimbre(context)
                .stream()
                .map(this::convertTimbreToMiseAJourTimbresRecapitulatifDTO)
                .collect(Collectors.toList())
        );
        return dto;
    }

    private MiseAJourTimbresRecapitulatifDTO convertTimbreToMiseAJourTimbresRecapitulatifDTO(
        OrganigrammeNodeTimbreDTO timbre
    ) {
        boolean isTotal = DTO_TOTAL.equals(timbre.getId());
        MiseAJourTimbresRecapitulatifDTO dto = new MiseAJourTimbresRecapitulatifDTO();
        dto.setAncienMinistere(timbre.getLabel());
        dto.setNouveauMinistere(isTotal ? "Total" : timbre.getLabelNextTimbre());
        dto.setMigrerDossierClos(isTotal ? "" : ResourceHelper.getString(timbre.getMigratingDossiersClosLabel()));
        dto.setBriserSignature(isTotal ? "" : ResourceHelper.getString(timbre.getBreakingSealLabel()));
        dto.setNbQuestionsCloses(timbre.getCountClose());
        dto.setNbQuestionsOuvertes(timbre.getCountSigne());
        dto.setNbQuestionsEnAttente(timbre.getCountMigrable());
        dto.setNbModeleFDR(timbre.getCountModelFDR());
        return dto;
    }

    @Override
    public ReponsesLogging getCurrentTimbreUpdate(CoreSession session) {
        return ReponsesServiceLocator.getUpdateTimbreService().getMigrationEnCours(session);
    }

    @Override
    public Boolean isMigrationEnCours(CoreSession session) {
        return ReponsesServiceLocator.getUpdateTimbreService().isMigrationEnCours(session);
    }

    @Override
    public HistoriqueMAJTimbresList getHistoriqueMAJTimbresList(CoreSession session) {
        HistoriqueMAJTimbresList historiqueMAJTimbresList = new HistoriqueMAJTimbresList();
        historiqueMAJTimbresList.setListe(
            ReponsesServiceLocator
                .getUpdateTimbreService()
                .getAllReponsesLogging(session)
                .stream()
                .map(this::convertToHistoriqueMAJTimbresDTO)
                .collect(Collectors.toList())
        );
        return historiqueMAJTimbresList;
    }

    private HistoriqueMAJTimbresDTO convertToHistoriqueMAJTimbresDTO(ReponsesLogging reponsesLogging) {
        HistoriqueMAJTimbresDTO dto = new HistoriqueMAJTimbresDTO();
        dto.setId(reponsesLogging.getId());
        dto.setMessage(reponsesLogging.getMessage());
        dto.setEtat(getEtatFromStatus(reponsesLogging.getStatus()));
        dto.setDateDebut(reponsesLogging.getStartDate());
        dto.setDateFin(reponsesLogging.getEndDate());
        dto.setQuestionsEnCoursAMigrer(reponsesLogging.getPrevisionalCount());
        dto.setQuestionsEnCoursMigrees(reponsesLogging.getEndCount());
        dto.setQuestionsClosesAMigrer(reponsesLogging.getClosePrevisionalCount());
        dto.setQuestionsClosesMigrees(reponsesLogging.getCloseEndCount());
        return dto;
    }

    private String getEtatFromStatus(ReponsesLoggingStatusEnum status) {
        return status != null ? status.getEtat() : ReponsesConstant.EN_COURS_STATUS;
    }

    @Override
    public String setCurrentLog(String idReponsesLogging, CoreSession session) {
        currentReponsesLoggingLine = null;
        currentReponsesLoggingDoc = null;
        if (StringUtils.isNotEmpty(idReponsesLogging)) {
            final DocumentModel doc = session.getDocument(new IdRef(idReponsesLogging));
            currentReponsesLoggingDoc = doc.getAdapter(ReponsesLogging.class);
        }
        currentReponsesLogging = idReponsesLogging;
        return null;
    }

    @Override
    public ReponsesLogging getCurrentReponsesLoggingDoc() {
        return currentReponsesLoggingDoc;
    }

    @Override
    public String getReponsesLoggingNextGouvernement() {
        OrganigrammeNode gouv = STServiceLocator
            .getSTGouvernementService()
            .getGouvernement(currentReponsesLoggingDoc.getNextGouvernement());
        if (gouv == null) {
            return null;
        } else {
            return gouv.getLabel();
        }
    }

    @Override
    public String getReponsesLoggingCurrentGouvernement() {
        OrganigrammeNode gouv = STServiceLocator
            .getSTGouvernementService()
            .getGouvernement(currentReponsesLoggingDoc.getCurrentGouvernement());
        if (gouv == null) {
            return null;
        } else {
            return gouv.getLabel();
        }
    }

    @Override
    public List<TimbreDTO> getTimbreList() {
        final List<TimbreDTO> list = new ArrayList<>();
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
                list.add(
                    new TimbreDTO(
                        currentGouvernementLabel,
                        nextGouvernementLabel,
                        ((EntiteNode) currentNode).getOrdre()
                    )
                );
            }
        }

        Collections.sort(
            list,
            (timbre1, timbre2) -> {
                return timbre1.getProtocolarOrder().compareTo(timbre2.getProtocolarOrder());
            }
        );

        return list;
    }

    @Override
    public DetailHistoriqueMAJTimbresList getDetailHistoriqueMAJTimbresList(SpecificContext context) {
        DetailHistoriqueMAJTimbresList list = new DetailHistoriqueMAJTimbresList();
        list.setListe(
            ReponsesServiceLocator
                .getUpdateTimbreService()
                .getAllReponsesLoggingLine(context.getSession(), context.getCurrentDocument().getId())
                .stream()
                .map(this::convertToDetailHistoriqueMAJTimbresDTO)
                .collect(Collectors.toList())
        );
        return list;
    }

    private DetailHistoriqueMAJTimbresDTO convertToDetailHistoriqueMAJTimbresDTO(
        ReponsesLoggingLine reponsesLoggingLine
    ) {
        DetailHistoriqueMAJTimbresDTO dto = new DetailHistoriqueMAJTimbresDTO();
        dto.setId(reponsesLoggingLine.getId());
        dto.setMessage(reponsesLoggingLine.getMessage());
        dto.setEtat(getEtatFromStatus(reponsesLoggingLine.getStatus()));
        dto.setDateDebut(reponsesLoggingLine.getStartDate());
        dto.setDateFin(reponsesLoggingLine.getEndDate());
        dto.setQuestionsAMigrer(getNumberQuestions(reponsesLoggingLine.getPrevisionalCount()));
        dto.setQuestionsMigrees(getNumberQuestions(reponsesLoggingLine.getEndCount()));
        dto.setDetails(String.join(", ", reponsesLoggingLine.getFullLog()));
        return dto;
    }

    private String getNumberQuestions(Long value) {
        return ofNullable(value)
            .filter(count -> !Long.valueOf(ReponsesLoggingLine.DASH_COUNT).equals(count))
            .map(Object::toString)
            .orElse("");
    }

    @Override
    public Map<String, String> getCurrentReponsesLoggingTimbre(CoreSession session) {
        return ReponsesServiceLocator
            .getUpdateTimbreService()
            .getReponsesLoggingTimbre(session, currentReponsesLogging);
    }

    @Override
    public String setCurrentLogLine(String idReponsesLoggingLine) {
        currentReponsesLoggingLine = idReponsesLoggingLine;
        return null;
    }

    @Override
    public DetailMigrationHistoriqueMAJTimbresList getDetailMigrationHistoriqueMAJTimbresList(SpecificContext context) {
        DetailMigrationHistoriqueMAJTimbresList list = new DetailMigrationHistoriqueMAJTimbresList();
        list.setListe(
            ReponsesServiceLocator
                .getUpdateTimbreService()
                .getAllReponsesLoggingLineDetail(context.getSession(), context.getCurrentDocument().getId())
                .stream()
                .map(this::convertToDetailMigrationHistoriqueMAJTimbresDTO)
                .collect(Collectors.toList())
        );
        return list;
    }

    private DetailMigrationHistoriqueMAJTimbresDTO convertToDetailMigrationHistoriqueMAJTimbresDTO(
        ReponsesLoggingLineDetail reponsesLoggingLineDetail
    ) {
        DetailMigrationHistoriqueMAJTimbresDTO dto = new DetailMigrationHistoriqueMAJTimbresDTO();
        dto.setId(reponsesLoggingLineDetail.getId());
        dto.setMessage(reponsesLoggingLineDetail.getMessage());
        dto.setEtat(getEtatFromStatus(reponsesLoggingLineDetail.getStatus()));
        dto.setDetails(String.join(", ", reponsesLoggingLineDetail.getFullLog()));
        return dto;
    }

    @Override
    public String getPourcentageAvancement(ReponsesLogging reponsesLogging) throws ReponsesException {
        return getProgressionLogging(reponsesLogging);
    }

    @Override
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
                "Le paramètre de la méthode incorrect. Instances de ReponsesLoggingLine ou ReponsesLogging attendu !"
            );
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
        return ResourceHelper.getString(RUNNING_MESSAGE);
    }

    @Override
    public boolean displayMigrationQuestionClose() {
        if (currentReponsesLoggingDoc != null && currentReponsesLoggingDoc.getClosePrevisionalCount() != null) {
            if (currentReponsesLoggingDoc.getCloseEndCount() == null) {
                return true;
            } else {
                return (
                    currentReponsesLoggingDoc
                        .getClosePrevisionalCount()
                        .compareTo(currentReponsesLoggingDoc.getCloseEndCount()) >
                    0
                );
            }
        }
        return false;
    }

    @Override
    public void setUpdatingTimbre(Boolean updatingTimbre) {
        this.updatingTimbre = updatingTimbre;
    }

    @Override
    public Boolean isUpdatingTimbre() {
        return updatingTimbre;
    }

    @Override
    public Boolean isPollCountActivated(SpecificContext context) {
        if (mapOrganigrammeNodeTimbreDTO == null) {
            String currentGouvernement = context.getFromContextData(CURRENT_GOUVERNEMENT);
            if (currentGouvernement == null) {
                currentGouvernement = STServiceLocator.getSTGouvernementService().getCurrentGouvernement().getId();
                context.putInContextData(CURRENT_GOUVERNEMENT, currentGouvernement);
            }
            startCountingTimbres(currentGouvernement, false, context);

            return true;
        }

        for (OrganigrammeNodeTimbreDTO node : mapOrganigrammeNodeTimbreDTO.values()) {
            if (
                !isCountCalculated(node.getCountClose()) ||
                !isCountCalculated(node.getCountMigrable()) ||
                !isCountCalculated(node.getCountModelFDR()) ||
                !isCountCalculated(node.getCountSigne())
            ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean checkPollCountActivation(SpecificContext context) {
        if (isPollCountActivated(context)) {
            return true;
        }

        if (!resetCountTimbres) {
            resetCountTimbres = true;
            // On garde le poll activated pour recharger une dernière fois la page
            return true;
        }
        return false;
    }

    @Override
    public Boolean isErrorOccurred() {
        if (mapOrganigrammeNodeTimbreDTO == null) {
            return false;
        }

        for (OrganigrammeNodeTimbreDTO node : mapOrganigrammeNodeTimbreDTO.values()) {
            if (
                isCountError(node.getCountClose()) ||
                isCountError(node.getCountMigrable()) ||
                isCountError(node.getCountModelFDR()) ||
                isCountError(node.getCountSigne())
            ) {
                return true;
            }
        }
        return false;
    }

    @Override
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

    @Override
    public List<SelectValueDTO> getNewTimbreList(SpecificContext context) {
        List<SelectValueDTO> newTimbreList = new ArrayList<>();
        String currentGouvernement = context.getFromContextData(CURRENT_GOUVERNEMENT);
        String nextGouvernement = context.getFromContextData(NEXT_GOUVERNEMENT);

        if (nextGouvernement != null) {
            // Ajoute l'entrée : Sélectionner une valeur
            newTimbreList.add(new SelectValueDTO(NEW_TIMBRE_EMPTY_VALUE, "Sélectionner une valeur"));
            // Ajoute l'entrée : Ministère inchangé
            newTimbreList.add(new SelectValueDTO(NEW_TIMBRE_UNCHANGED_ENTITY, "Ministère inchangé"));
            // Ajoute l'entrée : Ministère désactivé
            newTimbreList.add(new SelectValueDTO(NEW_TIMBRE_DEACTIVATE_ENTITY, "Ministère désactivé"));

            // On regroupe les ministères de l'ancien gouvernement (cf FEV
            // 399 : pour migrer vers un timbre existant)
            GouvernementNode ancienGouvernementNode = STServiceLocator
                .getSTGouvernementService()
                .getGouvernement(currentGouvernement);
            ancienGouvGroup = new SelectValueGroupDTO(ancienGouvernementNode.getLabel());

            List<OrganigrammeNode> gvtAncChildren = STServiceLocator
                .getOrganigrammeService()
                .getChildrenList(context.getSession(), ancienGouvernementNode, Boolean.TRUE);
            List<SelectValueDTO> lstAncMinisteres = new ArrayList<>();

            for (OrganigrammeNode child : gvtAncChildren) {
                lstAncMinisteres.add(new SelectValueDTO(child.getId(), child.getLabel()));
            }
            ancienGouvGroup.setSelectValues(lstAncMinisteres);
            newTimbreList.add(ancienGouvGroup);

            GouvernementNode newGouvernementNode = STServiceLocator
                .getSTGouvernementService()
                .getGouvernement(nextGouvernement);
            List<OrganigrammeNode> gvtChildren = STServiceLocator
                .getOrganigrammeService()
                .getChildrenList(context.getSession(), newGouvernementNode, Boolean.TRUE);
            // On regroupe les ministères du nouveau gouvernement s'il n'est pas vide
            if (gvtChildren != null && !gvtChildren.isEmpty()) {
                newGouvGroup = new SelectValueGroupDTO(newGouvernementNode.getLabel());
                List<SelectValueDTO> lstNouveauMinisteres = new ArrayList<>();
                for (OrganigrammeNode child : gvtChildren) {
                    lstNouveauMinisteres.add(new SelectValueDTO(child.getId(), child.getLabel()));
                }
                newGouvGroup.setSelectValues(lstNouveauMinisteres);
                newTimbreList.add(newGouvGroup);
            }
        }
        return newTimbreList;
    }

    @Override
    public String getNewTimbreLabelForEntite(OrganigrammeNodeTimbreDTO oldEntite) {
        return oldEntite.getLabelNextTimbre();
    }

    @Override
    public void saveParametrageTimbres(MisesAJourTimbresFormDTO misesAJourTimbresFormDTO) {
        for (MiseAJourTimbresDetailDTO detailDTO : misesAJourTimbresFormDTO.getDetails()) {
            newTimbre.put(detailDTO.getOldMinistereId(), detailDTO.getNewMinistereId());
            OrganigrammeNodeTimbreDTO timbreDTO = mapOrganigrammeNodeTimbreDTO.get(detailDTO.getOldMinistereId());
            timbreDTO.setBreakingSeal(
                misesAJourTimbresFormDTO.getBriserToutesSignatures() || detailDTO.getBriserSignature()
            );
            timbreDTO.setMigratingDossiersClos(
                misesAJourTimbresFormDTO.getMigrerTousDossiersClos() || detailDTO.getMigrerDossiersClos()
            );
        }
    }

    @Override
    public void checkAllBriserSignature() {
        for (OrganigrammeNodeTimbreDTO timbre : mapOrganigrammeNodeTimbreDTO.values()) {
            timbre.setBreakingSeal(!checkAllSignature);
        }
    }

    @Override
    public boolean isCheckAllSignature() {
        return checkAllSignature;
    }

    @Override
    public void setCheckAllSignature(boolean checkAllSignature) {
        this.checkAllSignature = checkAllSignature;
    }

    @Override
    public void checkAllClosedDossiersMigration() {
        for (OrganigrammeNodeTimbreDTO timbre : mapOrganigrammeNodeTimbreDTO.values()) {
            timbre.setMigratingDossiersClos(!checkAllClosedDossiersMigration);
        }
    }

    @Override
    public boolean isCheckAllClosedDossiersMigration() {
        return checkAllClosedDossiersMigration;
    }

    @Override
    public void setCheckAllClosedDossiersMigration(boolean checkAllClosedDossiersMigration) {
        this.checkAllClosedDossiersMigration = checkAllClosedDossiersMigration;
    }
}
