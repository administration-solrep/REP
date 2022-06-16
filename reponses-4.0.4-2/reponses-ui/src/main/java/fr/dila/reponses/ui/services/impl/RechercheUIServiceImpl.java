package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.api.constant.ReponsesConstant.RECHERCHE_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.RequeteConstants.APPLIQUER_RECHERCHE_EXACTE;
import static fr.dila.reponses.api.constant.RequeteConstants.DANS_TEXTE_QUESTION;
import static fr.dila.reponses.api.constant.RequeteConstants.DANS_TEXTE_REPONSE;
import static fr.dila.reponses.api.constant.RequeteConstants.DANS_TITRE;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_RAPPELE;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_REATTRIBUE;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_RENOUVELE;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_RETIRE;
import static fr.dila.reponses.api.constant.RequeteConstants.ETAT_SIGNALE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_CADUQUE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_RAPPELE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_RENOUVELEE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_RETIREE;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_SIGNALEE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_ANALYSE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.MOTSCLEF_MINISTERE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RENVOI;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;
import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.DEFAULT;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.api.enumeration.IndexModeEnum;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.core.recherche.RequeteImpl;
import fr.dila.reponses.core.recherche.query.SimpleSearchQueryParser;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.InitValueRechercheDTO;
import fr.dila.reponses.ui.bean.RechercheDTO;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.contentview.RechercheResultPageProvider;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.helper.RechercheHelper;
import fr.dila.reponses.ui.helper.RepDossierListHelper;
import fr.dila.reponses.ui.helper.RepDossierListProviderHelper;
import fr.dila.reponses.ui.services.RechercheUIService;
import fr.dila.reponses.ui.services.ReponsesSelectValueUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.recherche.BlocDatesForm;
import fr.dila.reponses.ui.th.bean.recherche.FeuilleRouteForm;
import fr.dila.reponses.ui.th.bean.recherche.MotsClesForm;
import fr.dila.reponses.ui.th.bean.recherche.RechercheGeneraleForm;
import fr.dila.reponses.ui.th.bean.recherche.TexteIntegralForm;
import fr.dila.ss.ui.utils.ExportEventUtils;
import fr.dila.st.api.constant.STRechercheExportEventConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class RechercheUIServiceImpl implements RechercheUIService {
    private static final STLogger LOG = STLogFactory.getLog(RechercheUIServiceImpl.class);
    private static final String RECHERCHE_RESULT_NUMBER_RESULTS = "recherche.result.numberResults";
    private static final String RECHERCHE_RESULT_TITLE = "recherche.result.title";

    @Override
    public RepDossierList getDossiersByOrigineNumero(
        String origineNumero,
        DossierListForm listForm,
        CoreSession session
    ) {
        List<Object> lstParams = Arrays.asList(
            getNumberToSearch(origineNumero),
            getOrigineToSearch(origineNumero),
            ReponsesServiceLocator.getRechercheService().getLegislatureCourante(session)
        );

        RechercheResultPageProvider provider = RepDossierListProviderHelper.initProvider(
            listForm,
            "rechercheSimplePageProvider",
            session,
            lstParams,
            "q."
        );

        List<Map<String, Serializable>> docList2 = provider.getCurrentPage();
        int resultsCount = (int) provider.getResultsCount();

        return RepDossierListHelper.buildDossierList(
            docList2,
            ResourceHelper.getString(RECHERCHE_RESULT_TITLE),
            resultsCount + ResourceHelper.getString(RECHERCHE_RESULT_NUMBER_RESULTS),
            listForm,
            provider.getLstUserVisibleColumns(),
            resultsCount,
            false
        );
    }

    private static String getOrigineToSearch(String simpleSearch) {
        return SimpleSearchQueryParser.getOrigineQuestionToSearch(simpleSearch);
    }

    private static String getNumberToSearch(String simpleSearch) {
        return SimpleSearchQueryParser.getNumberQuestionToSearch(simpleSearch);
    }

    @Override
    public RechercheDTO initRechercheDTO(RechercheDTO dto) {
        if (dto == null) {
            dto = new RechercheDTO();
            dto.setRechercheGeneraleForm(new RechercheGeneraleForm());
            dto.setBlocDatesForm(new BlocDatesForm());
            dto.setMotsClesForm(new MotsClesForm());
            dto.setFeuilleRouteForm(new FeuilleRouteForm());
            dto.setTexteIntegralForm(new TexteIntegralForm());
        }

        dto.setInitValueRechercheDto(getInitValueRechercheDTO());

        return dto;
    }

    private static InitValueRechercheDTO getInitValueRechercheDTO() {
        ReponsesSelectValueUIService selectValueUIService = ReponsesUIServiceLocator.getSelectValueUIService();

        InitValueRechercheDTO initValueDto = new InitValueRechercheDTO();

        initValueDto.setLegislatures(selectValueUIService.getLegislatures());
        initValueDto.setTypesQuestion(selectValueUIService.getQuestionTypes());
        initValueDto.setGroupesPolitiques(selectValueUIService.getGroupesPolitiques());
        initValueDto.setMinisteresAttributaires(selectValueUIService.getCurrentMinisteres());
        initValueDto.setMinisteresInterroges(selectValueUIService.getAllMinisteres());

        // On trie par ordre alphabétique
        List<SelectValueDTO> typesEtapes = selectValueUIService.getRoutingTaskTypes();
        typesEtapes.sort(Comparator.comparing(SelectValueDTO::getLabel));
        initValueDto.setTypesEtape(typesEtapes);

        List<SelectValueDTO> statutEtapes = selectValueUIService.getStatutsEtapeRecherche();
        statutEtapes.sort(Comparator.comparing(SelectValueDTO::getLabel));
        initValueDto.setListStatus(statutEtapes);

        return initValueDto;
    }

    @Override
    public RepDossierList getDossiersForRechercheAvancee(
        CoreSession session,
        RechercheDTO dto,
        DossierListForm listForm
    ) {
        Requete requete = createRequete(dto, session);

        requete.doBeforeQuery();

        String query = ReponsesServiceLocator.getRechercheService().getFullQuery(session, requete);

        LOG.info(DEFAULT, query);

        RechercheResultPageProvider provider = RepDossierListProviderHelper.initProvider(
            listForm,
            "requeteCompositePageProvider",
            session,
            emptyList(),
            "q."
        );

        provider.getDefinition().setPattern(query);

        List<Map<String, Serializable>> searchResults = provider.getCurrentPage();
        int resultsCount = (int) provider.getResultsCount();

        return RepDossierListHelper.buildDossierList(
            searchResults,
            ResourceHelper.getString(RECHERCHE_RESULT_TITLE),
            resultsCount + ResourceHelper.getString(RECHERCHE_RESULT_NUMBER_RESULTS),
            listForm,
            provider.getLstUserVisibleColumns(),
            resultsCount,
            false
        );
    }

    private static Requete createRequete(RechercheDTO dto, CoreSession session) {
        DocumentModel requeteDoc = session.createDocumentModel(RECHERCHE_DOCUMENT_TYPE);

        Requete requete = new RequeteImpl(requeteDoc);

        MapDoc2Bean.beanToDoc(dto.getRechercheGeneraleForm(), requeteDoc);
        mapRechercheGeneraleToDoc(dto.getRechercheGeneraleForm(), requete);
        MapDoc2Bean.beanToDoc(dto.getFeuilleRouteForm(), requeteDoc);
        MapDoc2Bean.beanToDoc(dto.getBlocDatesForm(), requeteDoc);
        MapDoc2Bean.beanToDoc(dto.getTexteIntegralForm(), requeteDoc);
        mapTexteIntegralToDoc(dto.getTexteIntegralForm(), requete);
        mapMotsClesToDoc(dto.getMotsClesForm(), requete);

        return requete;
    }

    private static void mapRechercheGeneraleToDoc(RechercheGeneraleForm general, Requete requete) {
        requete.setTypeQuestion(singletonList(general.getQuestions()));

        List<String> etatsQuestion = general.getEtatQuestion();
        if (CollectionUtils.isNotEmpty(etatsQuestion)) {
            requete.setEtatCaduque(etatsQuestion.contains(ETAT_QUESTION_CADUQUE));
            requete.setEtatClotureAutre(etatsQuestion.contains(ETAT_QUESTION_CLOTURE_AUTRE));
            requete.setEtat(ETAT_RETIRE, etatsQuestion.contains(ETAT_QUESTION_RETIREE));
            requete.setEtat(ETAT_RAPPELE, etatsQuestion.contains(ETAT_QUESTION_RAPPELE));
            requete.setEtat(ETAT_SIGNALE, etatsQuestion.contains(ETAT_QUESTION_SIGNALEE));
            requete.setEtat(ETAT_RENOUVELE, etatsQuestion.contains(ETAT_QUESTION_RENOUVELEE));
            requete.setEtat(ETAT_REATTRIBUE, etatsQuestion.contains(ETAT_QUESTION_REATTRIBUEEE));
        }
    }

    private static void mapTexteIntegralToDoc(TexteIntegralForm texteIntegral, Requete requete) {
        List<String> rechercherDansValues = texteIntegral.getRechercherDans();
        if (CollectionUtils.isNotEmpty(rechercherDansValues)) {
            requete.setDansTexteQuestion(rechercherDansValues.contains(DANS_TEXTE_QUESTION));
            requete.setDansTexteReponse(rechercherDansValues.contains(DANS_TEXTE_REPONSE));
            requete.setDansTitre(rechercherDansValues.contains(DANS_TITRE));
        }

        List<String> rechercherExacteValues = texteIntegral.getRechercheExacte();
        if (CollectionUtils.isNotEmpty(rechercherExacteValues)) {
            requete.setAppliquerRechercheExacte(
                texteIntegral.getRechercheExacte().contains(APPLIQUER_RECHERCHE_EXACTE)
            );
        }
    }

    private static void mapMotsClesToDoc(MotsClesForm motsCles, Requete requete) {
        String rechercheSur = motsCles.getRechercheSur();
        requete.setIndexationMode(IndexModeEnum.fromValue(rechercheSur));

        ReponsesIndexableDocument indexableDoc = requete.getDocument().getAdapter(ReponsesIndexableDocument.class);

        if (CollectionUtils.isNotEmpty(motsCles.getIndexSenat())) {
            indexableDoc.setSenatQuestionRubrique(
                RechercheHelper.getMotsClesByIndexationType(motsCles.getIndexSenat(), SE_RUBRIQUE)
            );
            indexableDoc.setSenatQuestionRenvois(
                RechercheHelper.getMotsClesByIndexationType(motsCles.getIndexSenat(), SE_RENVOI)
            );
            indexableDoc.setSenatQuestionThemes(
                RechercheHelper.getMotsClesByIndexationType(motsCles.getIndexSenat(), SE_THEME)
            );
        }

        if (CollectionUtils.isNotEmpty(motsCles.getIndexAn())) {
            indexableDoc.setAssNatAnalyses(
                RechercheHelper.getMotsClesByIndexationType(motsCles.getIndexAn(), AN_ANALYSE)
            );
            indexableDoc.setAssNatRubrique(
                RechercheHelper.getMotsClesByIndexationType(motsCles.getIndexAn(), AN_RUBRIQUE)
            );
            indexableDoc.setAssNatTeteAnalyse(
                RechercheHelper.getMotsClesByIndexationType(motsCles.getIndexAn(), TA_RUBRIQUE)
            );
        }

        if (CollectionUtils.isNotEmpty(motsCles.getIndexMinistere())) {
            indexableDoc.setMotsClefMinistere(
                RechercheHelper.getMotsClesByIndexationType(motsCles.getIndexMinistere(), MOTSCLEF_MINISTERE)
            );
        }
    }

    @Override
    public void exportAllDossiers(SpecificContext context) {
        CoreSession session = context.getSession();
        Map<String, Object> sessionSearchForms = context.getFromContextData(ReponsesContextDataKey.SEARCH_FORM_KEYS);
        RechercheDTO dto = RechercheHelper.convertTo(sessionSearchForms);

        Requete requete = createRequete(dto, session);
        requete.doBeforeQuery();
        String query = ReponsesServiceLocator.getRechercheService().getFullQuery(session, requete);

        Map<String, Serializable> eventProperties = ImmutableMap.of(STRechercheExportEventConstants.PARAM_QUERY, query);

        ExportEventUtils.fireExportEvent(context, STRechercheExportEventConstants.EVENT_NAME, eventProperties);
    }
}
