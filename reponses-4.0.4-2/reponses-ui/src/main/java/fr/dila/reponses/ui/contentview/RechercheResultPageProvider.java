package fr.dila.reponses.ui.contentview;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants.UserColumnEnum;
import fr.dila.reponses.api.constant.ReponseDossierListingConstants;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.helper.ReponsesProviderHelper;
import fr.dila.ss.api.recherche.IdLabel;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.contentview.AbstractDTOPageProvider;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.query.api.PageSelection;

/**
 * Fourni le DTO des dossiers retournés par la recherche.
 *
 * Il prend en entrée une requête FNXQL retournant des questions.
 *
 * Il modifie la requête pour rajouter les champ de tri sur la question dans le select dans le cas de la présence d'un
 * 'distinct' dans la requête, afin d'éviter une erreur sql.
 *
 * @author spesnel
 *
 */
public class RechercheResultPageProvider extends AbstractDTOPageProvider {
    private static final STLogger LOG = STLogFactory.getLog(RechercheResultPageProvider.class);

    /**
     *
     */
    private static final long serialVersionUID = 8288353350339233296L;

    private List<String> lstUserVisibleColumns = new ArrayList<>();

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) {
        currentItems = new ArrayList<>();

        List<String> questionIds = null;
        resultsCount = QueryUtils.doCountQuery(coreSession, query);
        if (resultsCount > 0) {
            questionIds = QueryUtils.doQueryForIds(coreSession, query, getPageSize(), offset);
        }

        populateFromQuestionIds(coreSession, questionIds);
    }

    protected Map<String, RepDossierListingDTO> populateFromQuestionData(
        CoreSession coreSession,
        List<String> questionIds,
        Integer reponseDureeTraitement,
        Map<String, RepDossierListingDTO> mapQuestionIdDTO
    ) {
        // retrieve Document Question
        DocumentModelList dml = QueryUtils.retrieveDocuments(
            coreSession,
            DossierConstants.QUESTION_DOCUMENT_TYPE,
            questionIds,
            true
        );

        List<String> reponsesIds = dml
            .stream()
            .map(doc -> ReponsesProviderHelper.getReponseIdFromQuestion(coreSession, doc.getAdapter(Question.class)))
            .collect(Collectors.toList());
        // Récupération des errata en une seule fois : on vérifie quelles reponses sont parentes d'un
        // erratum#anonymousType dans hierarchy
        final Set<String> reponsesIdsWithErratum = ReponsesProviderHelper.getIdsQuestionsWithErratum(
            coreSession,
            reponsesIds
        );

        Map<String, String> mapLegislatureNames = new HashMap<>();
        if (lstUserVisibleColumns.contains(UserColumnEnum.LEGISLATURE.toString())) {
            mapLegislatureNames = ReponsesProviderHelper.getLegislatureNames();
        }

        for (DocumentModel dm : dml) {
            if (dm != null) {
                Question question = dm.getAdapter(Question.class);
                String reponseId = ReponsesProviderHelper.getReponseIdFromQuestion(coreSession, question);

                RepDossierListingDTO rrdto = new RepDossierListingDTO();
                ReponsesProviderHelper.buildDTOFromQuestion(
                    rrdto,
                    question,
                    reponseDureeTraitement,
                    reponsesIdsWithErratum,
                    reponseId
                );

                if (MapUtils.isNotEmpty(mapLegislatureNames)) {
                    ReponsesProviderHelper.updateLegislatureInfo(rrdto, mapLegislatureNames, question);
                }

                mapQuestionIdDTO.put((String) question.getDossierRef().reference(), rrdto);

                if (currentItems != null) {
                    currentItems.add(rrdto);
                }
            }
        }

        return mapQuestionIdDTO;
    }

    protected void populateFromDossierData(CoreSession coreSession, Map<String, RepDossierListingDTO> dossierIdDTO) {
        // recup dossier en une fois
        DocumentModelList dml = QueryUtils.retrieveDocuments(
            coreSession,
            DossierConstants.DOSSIER_DOCUMENT_TYPE,
            dossierIdDTO.keySet()
        );
        final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

        Set<String> dossiersDirecteurs = allotissementService.extractDossiersDirecteurs(dml, coreSession);

        // lock ?
        final STLockService stLockService = STServiceLocator.getSTLockService();
        Map<String, String> lockedDossierDetails = stLockService.extractLockedInfo(coreSession, dossierIdDTO.keySet());

        for (DocumentModel dossierDoc : dml) {
            String dossierId = dossierDoc.getId();
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);

            RepDossierListingDTO rrdto = dossierIdDTO.get(dossierId);
            rrdto.setLockOwner(lockedDossierDetails.get(dossierId));
            ReponsesProviderHelper.buildDTOFromDossier(
                rrdto,
                dossier,
                dossiersDirecteurs,
                lockedDossierDetails.containsKey(dossierId),
                true,
                coreSession,
                lstUserVisibleColumns
            );
        }
    }

    protected void populateFromFDRData(CoreSession coreSession, Map<String, RepDossierListingDTO> mapDossierIdDTO) {
        final ReponsesCorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
        // Recherche les DossierLink que l'utilisateur peut actionner
        // Récupération des dossiers links en une fois
        List<DocumentModel> dossiersLinks = corbeilleService.findUpdatableDossierLinkForDossiers(
            coreSession,
            new ArrayList<>(mapDossierIdDTO.keySet())
        );

        Map<String, List<IdLabel>> mapDossierCaseLink = new HashMap<>();
        for (DocumentModel dossierLinkDoc : dossiersLinks) {
            DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
            String dossierId = dossierLink.getDossierId();

            RepDossierListingDTO rrdto = mapDossierIdDTO.get(dossierId);
            List<IdLabel> lstCaseIDLabel = mapDossierCaseLink.get(dossierId);

            ReponsesProviderHelper.buildDTOFromDossierLink(rrdto, dossierLink);

            rrdto.setRoutingTaskType(concatenateLabels(rrdto.getRoutingTaskType(), dossierLink.getRoutingTaskLabel()));

            if (lstCaseIDLabel == null) {
                lstCaseIDLabel = new ArrayList<>();
            }

            lstCaseIDLabel.add(new IdLabel(dossierLink.getId(), dossierLink.getRoutingTaskLabel(), dossierId));
            mapDossierCaseLink.put(dossierId, lstCaseIDLabel);
        }

        for (Entry<String, List<IdLabel>> entry : mapDossierCaseLink.entrySet()) {
            RepDossierListingDTO rrdto = mapDossierIdDTO.get(entry.getKey());

            rrdto.setCaseLinkIdsLabels(entry.getValue().toArray(new IdLabel[0]));
        }
    }

    protected void populateFromQuestionIds(CoreSession coreSession, List<String> questionIds) {
        long begining = System.currentTimeMillis();

        if (questionIds != null && !questionIds.isEmpty()) {
            final STParametreService paramService = STServiceLocator.getSTParametreService();

            // traitement question, initialisation DTO
            Integer reponseDureeTraitement = null;

            try {
                reponseDureeTraitement =
                    Integer.parseInt(
                        paramService.getParametreValue(coreSession, STParametreConstant.QUESTION_DUREE_TRAITEMENT)
                    );
            } catch (Exception e) {
                throw new NuxeoException(
                    "La durée de traitement de question définie dans les paramètres n'a pas pu être récupérée",
                    e
                );
            }

            long step1 = System.currentTimeMillis();
            LOG.info(
                STLogEnumImpl.DEFAULT,
                "Temps écoulé après mapping des données des questions " + (step1 - begining) + "ms"
            );
            Map<String, RepDossierListingDTO> mapDossierIdDTO = new HashMap<>();

            populateFromQuestionData(coreSession, questionIds, reponseDureeTraitement, mapDossierIdDTO);

            long step2 = System.currentTimeMillis();

            LOG.info(
                STLogEnumImpl.DEFAULT,
                "Temps écoulé après mapping des données des questions " + (step2 - step1) + "ms"
            );

            populateFromDossierData(coreSession, mapDossierIdDTO);
            long step3 = System.currentTimeMillis();

            LOG.info(
                STLogEnumImpl.DEFAULT,
                "Temps écoulé après mapping des données des dossiers " + (step3 - step2) + "ms"
            );

            populateFromFDRData(coreSession, mapDossierIdDTO);

            long end = System.currentTimeMillis();

            LOG.info(
                STLogEnumImpl.DEFAULT,
                "Temps écoulé après mapping des données des case links " + (end - step3) + "ms"
            );
            LOG.info(STLogEnumImpl.DEFAULT, "Temps exécution total " + (end - begining) + "ms");
        }
    }

    private String concatenateLabels(String oldLabel, String newLabelKey) {
        String label = ResourceHelper.getString(newLabelKey);

        return StringUtils.isBlank(oldLabel) ? label : oldLabel + "," + label;
    }

    @Override
    public void setSearchDocumentModel(DocumentModel searchDocumentModel) {
        if (this.searchDocumentModel != searchDocumentModel) {
            this.searchDocumentModel = searchDocumentModel;
            refresh();
        }
    }

    public List<String> getLstUserVisibleColumns() {
        return lstUserVisibleColumns;
    }

    public void setLstUserVisibleColumns(List<String> lstUserVisibleColumns) {
        this.lstUserVisibleColumns = lstUserVisibleColumns;
    }

    public List<Map<String, Serializable>> getAllItemsSelected() {
        List<Map<String, Serializable>> lstSelectedEntries = new ArrayList<>();
        List<PageSelection<Map<String, Serializable>>> lstDocs = currentSelectPage.getEntries();

        for (PageSelection<Map<String, Serializable>> doc : lstDocs) {
            if (doc.isSelected()) {
                Map<String, Serializable> elem = doc.getData();

                // Conversion de la propriété en label
                String label = elem.get(ReponseDossierListingConstants.ROUTING_TASK_TYPE).toString();
                elem.put(ReponseDossierListingConstants.ROUTING_TASK_TYPE, label);
                String delai = (String) elem.get(ReponseDossierListingConstants.DELAI);

                if (delai.startsWith("label")) {
                    label = ResourceHelper.getString(elem.get(ReponseDossierListingConstants.DELAI).toString());
                    elem.put(ReponseDossierListingConstants.DELAI, label);
                }

                lstSelectedEntries.add(elem);
            }
        }

        return lstSelectedEntries;
    }
}
