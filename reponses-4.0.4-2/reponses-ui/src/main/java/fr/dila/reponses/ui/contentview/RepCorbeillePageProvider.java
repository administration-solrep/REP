package fr.dila.reponses.ui.contentview;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.constant.ReponseDossierListingConstants;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.helper.ReponsesProviderHelper;
import fr.dila.ss.api.recherche.IdLabel;
import fr.dila.ss.ui.contentview.AbstractSSCorbeillePageProvider;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.nuxeo.common.utils.i18n.I18NUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.query.api.PageSelection;

public class RepCorbeillePageProvider extends AbstractSSCorbeillePageProvider<RepDossierListingDTO, DossierLink> {
    private static final long serialVersionUID = 1L;

    private static final STLogger LOGGER = STLogFactory.getLog(RepCorbeillePageProvider.class);

    @Override
    protected void populateFromDossierLinkIds(CoreSession session, List<String> ids) {
        super.populateFromDossierLinkIds(session, ids);

        // recup dossier en une fois
        dml = QueryUtils.retrieveDocuments(session, STConstant.DOSSIER_DOCUMENT_TYPE, mapDossierIdDTO.keySet());
        if (dml.isEmpty()) {
            LOGGER.warn(
                STLogEnumImpl.GET_DOSSIER_TEC,
                "Aucun dossier n'a pu être récupéré avec l'ID : " + mapDossierIdDTO.keySet()
            );
            return;
        }

        final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        final STLockService stLockService = STServiceLocator.getSTLockService();
        Map<String, String> lockedDossierDetails = stLockService.extractLockedInfo(session, mapDossierIdDTO.keySet());

        Map<String, RepDossierListingDTO> mapQuestionIdDTO = new HashMap<>();
        Set<String> lstDossiersDirecteurs = allotissementService.extractDossiersDirecteurs(dml, session);
        for (DocumentModel dm : dml) {
            String dossierId = dm.getId();
            Dossier dossier = dm.getAdapter(Dossier.class);
            RepDossierListingDTO cbdto = mapDossierIdDTO.get(dossierId);
            cbdto.setLockOwner(lockedDossierDetails.get(dossierId));

            ReponsesProviderHelper.buildDTOFromDossier(
                cbdto,
                dossier,
                lstDossiersDirecteurs,
                lockedDossierDetails.containsKey(dossierId),
                false,
                session,
                getLstUserVisibleColumns()
            );

            mapQuestionIdDTO.put(dossier.getQuestionId(), cbdto);
        }

        // recup question en une fois
        dml = QueryUtils.retrieveDocuments(session, DossierConstants.QUESTION_DOCUMENT_TYPE, mapQuestionIdDTO.keySet());

        STParametreService paramService = STServiceLocator.getSTParametreService();

        int reponseDureeTraitement = Integer.parseInt(
            paramService.getParametreValue(session, STParametreConstant.QUESTION_DUREE_TRAITEMENT)
        );

        List<String> reponsesIds = dml
            .stream()
            .map(doc -> ReponsesProviderHelper.getReponseIdFromQuestion(session, doc.getAdapter(Question.class)))
            .collect(Collectors.toList());

        final Set<String> reponseHasErratumSet = ReponsesProviderHelper.getIdsQuestionsWithErratum(
            session,
            reponsesIds
        );

        Map<String, String> mapLegislatureNames = new HashMap<>();
        if (lstUserVisibleColumns.contains(ProfilUtilisateurConstants.UserColumnEnum.LEGISLATURE.toString())) {
            mapLegislatureNames = ReponsesProviderHelper.getLegislatureNames();
        }

        for (DocumentModel dm : dml) {
            final String questionId = dm.getId();
            Question question = dm.getAdapter(Question.class);
            RepDossierListingDTO cbdto = mapQuestionIdDTO.get(questionId);
            ReponsesProviderHelper.buildDTOFromQuestion(
                cbdto,
                question,
                reponseDureeTraitement,
                reponseHasErratumSet,
                ReponsesProviderHelper.getReponseIdFromQuestion(session, question)
            );

            if (MapUtils.isNotEmpty(mapLegislatureNames)) {
                ReponsesProviderHelper.updateLegislatureInfo(cbdto, mapLegislatureNames, question);
            }
        }
    }

    public List<Map<String, Serializable>> getAllItemsSelected() {
        List<Map<String, Serializable>> lstSelectedEntries = new ArrayList<>();
        String bundle = "messages";
        Locale locale = Locale.FRENCH;
        List<PageSelection<Map<String, Serializable>>> lstDocs = currentSelectPage.getEntries();

        for (PageSelection<Map<String, Serializable>> doc : lstDocs) {
            if (doc.isSelected()) {
                Map<String, Serializable> elem = doc.getData();

                // Conversion de la propriété en label
                String label = I18NUtils.getMessageString(
                    bundle,
                    (String) elem.get(ReponseDossierListingConstants.ROUTING_TASK_TYPE),
                    null,
                    locale
                );
                elem.put(ReponseDossierListingConstants.ROUTING_TASK_TYPE, label);
                String delai = (String) elem.get(ReponseDossierListingConstants.DELAI);

                if (delai.startsWith("label")) {
                    label =
                        I18NUtils.getMessageString(
                            bundle,
                            (String) elem.get(ReponseDossierListingConstants.DELAI),
                            null,
                            locale
                        );
                    elem.put(ReponseDossierListingConstants.DELAI, label);
                }

                lstSelectedEntries.add(elem);
            }
        }

        return lstSelectedEntries;
    }

    @Override
    protected void buildDTOFromDossierLinks(RepDossierListingDTO dto, DossierLink dossierLink, CoreSession session) {
        ReponsesProviderHelper.buildDTOFromDossierLink(dto, dossierLink);
    }

    @Override
    protected void populateDossierListingDTO(
        RepDossierListingDTO dto,
        DossierLink dossierLink,
        IdLabel[] currentDossierLink
    ) {
        dto.setCaseLinkIdsLabels(currentDossierLink);
        dto.setRoutingTaskType(ResourceHelper.getString(dossierLink.getRoutingTaskLabel()));
    }

    @Override
    protected RepDossierListingDTO createDossierListingDTO() {
        return new RepDossierListingDTO();
    }
}
