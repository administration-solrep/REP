package fr.dila.reponses.ui.services.impl;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QC;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QE;
import static fr.dila.reponses.api.enumeration.QuestionTypesEnum.QOSD;
import static fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderServiceImpl.QU_DATE_DEBUT_MINISTERE_INTERROGE;
import static fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderServiceImpl.QU_DATE_FIN_MINISTERE_INTERROGE;
import static fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderServiceImpl.QU_ID_MINISTERE_INTERROGE;
import static fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderServiceImpl.QU_INTITULE_MINISTERE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.api.enumeration.QuestionTypesEnum;
import fr.dila.reponses.api.service.vocabulary.EtatQuestionService;
import fr.dila.reponses.api.service.vocabulary.GroupePolitiqueService;
import fr.dila.reponses.api.service.vocabulary.LegislatureService;
import fr.dila.reponses.api.service.vocabulary.QuestionTypeService;
import fr.dila.reponses.api.service.vocabulary.RubriqueANService;
import fr.dila.reponses.api.service.vocabulary.RubriqueSenatService;
import fr.dila.reponses.api.service.vocabulary.StatutEtapeRechercheService;
import fr.dila.reponses.api.service.vocabulary.TeteAnalyseANService;
import fr.dila.reponses.api.service.vocabulary.ThemeSenatService;
import fr.dila.reponses.api.service.vocabulary.ValidationStatutEtapeService;
import fr.dila.reponses.ui.services.ReponsesSelectValueUIService;
import fr.dila.reponses.ui.services.actions.suggestion.ministere.MinistereSuggestionProviderService;
import fr.dila.ss.api.service.vocabulary.RoutingTaskTypeService;
import fr.dila.ss.ui.services.organigramme.SSOrganigrammeManagerUIService;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class RepSelectValueUIServiceImplTest {
    private ReponsesSelectValueUIService service;

    @Mock
    @RuntimeService
    MinistereSuggestionProviderService ministereSuggestionProviderService;

    @Mock
    @RuntimeService
    private SSOrganigrammeManagerUIService ssOrganigrammeManagerUIService;

    @Mock
    @RuntimeService
    private GroupePolitiqueService groupePolitiqueService;

    @Mock
    @RuntimeService
    private LegislatureService legislatureService;

    @Mock
    @RuntimeService
    private QuestionTypeService questionTypeService;

    @Mock
    @RuntimeService
    private RoutingTaskTypeService routingTaskTypeService;

    @Mock
    @RuntimeService
    private StatutEtapeRechercheService statutEtapeRechercheService;

    @Mock
    @RuntimeService
    private ValidationStatutEtapeService validationStatutEtapeService;

    @Mock
    @RuntimeService
    private EtatQuestionService etatQuestionService;

    @Mock
    @RuntimeService
    private RubriqueANService rubriqueANService;

    @Mock
    @RuntimeService
    private RubriqueSenatService rubriqueSenatService;

    @Mock
    @RuntimeService
    private TeteAnalyseANService teteAnalyseANService;

    @Mock
    @RuntimeService
    private ThemeSenatService themeSenatService;

    @Before
    public void setUp() {
        service = new ReponsesSelectValueUIServiceImpl();
    }

    @Test
    public void getAllMinisteres() {
        Map<String, Serializable> ministere1 = ImmutableMap.of(
            QU_ID_MINISTERE_INTERROGE,
            "ministereId1",
            QU_INTITULE_MINISTERE,
            "ministereLabel1",
            QU_DATE_DEBUT_MINISTERE_INTERROGE,
            EMPTY,
            QU_DATE_FIN_MINISTERE_INTERROGE,
            EMPTY
        );
        Map<String, Serializable> ministere2 = ImmutableMap.of(
            QU_ID_MINISTERE_INTERROGE,
            "ministereId3",
            QU_INTITULE_MINISTERE,
            "ministereLabel3",
            QU_DATE_DEBUT_MINISTERE_INTERROGE,
            "(05/08/2018",
            QU_DATE_FIN_MINISTERE_INTERROGE,
            " - 05/08/2020"
        );
        Map<String, Serializable> ministere3 = ImmutableMap.of(
            QU_ID_MINISTERE_INTERROGE,
            EMPTY,
            QU_INTITULE_MINISTERE,
            EMPTY,
            QU_DATE_DEBUT_MINISTERE_INTERROGE,
            EMPTY,
            QU_DATE_FIN_MINISTERE_INTERROGE,
            EMPTY
        );

        when(ministereSuggestionProviderService.getAllMinistereListRecherche())
            .thenReturn(newArrayList(ministere1, ministere2, ministere3));

        List<SelectValueDTO> allMinisteres = service.getAllMinisteres();

        assertThat(allMinisteres)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(ministere1.get(QU_ID_MINISTERE_INTERROGE), ministere1.get(QU_INTITULE_MINISTERE)),
                tuple(
                    ministere2.get(QU_ID_MINISTERE_INTERROGE),
                    ministere2.get(QU_INTITULE_MINISTERE).toString() +
                    ' ' +
                    ministere2.get(QU_DATE_DEBUT_MINISTERE_INTERROGE) +
                    ministere2.get(QU_DATE_FIN_MINISTERE_INTERROGE)
                ),
                tuple(EMPTY, EMPTY)
            );
    }

    @Test
    public void getCurrentMinisteres() {
        EntiteNode entiteNode1 = new EntiteNodeImpl();
        entiteNode1.setId("nodeId1");
        entiteNode1.setLabel("nodeLabel1");

        EntiteNode entiteNode2 = new EntiteNodeImpl();
        entiteNode2.setId("nodeId2");
        entiteNode2.setLabel("nodeLabel2");

        EntiteNode entiteNode3 = new EntiteNodeImpl();
        entiteNode3.setId("nodeId3");
        entiteNode3.setLabel("nodeLabel3");

        when(ssOrganigrammeManagerUIService.getSortedCurrentMinisteres())
            .thenReturn(newArrayList(entiteNode1, entiteNode2, entiteNode3));

        List<SelectValueDTO> currentMinisteres = service.getCurrentMinisteres();

        assertThat(currentMinisteres)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(entiteNode1.getId(), entiteNode1.getLabel()),
                tuple(entiteNode2.getId(), entiteNode2.getLabel()),
                tuple(entiteNode3.getId(), entiteNode3.getLabel())
            );
    }

    @Test
    public void getGroupesPolitiques() {
        ImmutablePair<String, String> groupePolitique1 = ImmutablePair.of("UMP (SENAT)", "UMP (SENAT)");
        ImmutablePair<String, String> groupePolitique2 = ImmutablePair.of("Groupe UC", "Groupe UC");
        ImmutablePair<String, String> groupePolitique3 = ImmutablePair.of("ECOLO (AN)", "ECOLO (AN)");

        when(groupePolitiqueService.getEntries())
            .thenReturn(newArrayList(groupePolitique1, groupePolitique2, groupePolitique3));

        List<SelectValueDTO> groupesPolitiques = service.getGroupesPolitiques();

        assertThat(groupesPolitiques)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(groupePolitique1.getLeft(), groupePolitique1.getRight()),
                tuple(groupePolitique2.getLeft(), groupePolitique2.getRight()),
                tuple(groupePolitique3.getLeft(), groupePolitique3.getRight())
            );
    }

    @Test
    public void getLegislatures() {
        ImmutablePair<Integer, String> legislature13 = ImmutablePair.of(13, "13eme legislature");
        ImmutablePair<Integer, String> legislature14 = ImmutablePair.of(14, "14eme legislature");
        ImmutablePair<Integer, String> legislature15 = ImmutablePair.of(15, "15eme legislature");

        when(legislatureService.getEntries()).thenReturn(newArrayList(legislature13, legislature14, legislature15));

        List<SelectValueDTO> legislatures = service.getLegislatures();

        assertThat(legislatures)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(legislature13.getLeft().toString(), legislature13.getRight()),
                tuple(legislature14.getLeft().toString(), legislature14.getRight()),
                tuple(legislature15.getLeft().toString(), legislature15.getRight())
            );
    }

    @Test
    public void getLegislaturesWithError() {
        NuxeoException error = new NuxeoException("Erreur législatures");
        when(legislatureService.getEntries()).thenThrow(error);

        Throwable throwable = catchThrowable(() -> service.getLegislatures());
        assertThat(throwable).isEqualTo(error);
    }

    @Test
    public void getQuestionTypes() {
        ImmutablePair<QuestionTypesEnum, String> questionTypeQE = ImmutablePair.of(QE, "Questions Ecrites");
        ImmutablePair<QuestionTypesEnum, String> questionTypeQOSD = ImmutablePair.of(
            QOSD,
            "Questions Orales Sans Débat"
        );
        ImmutablePair<QuestionTypesEnum, String> questionTypeQC = ImmutablePair.of(QC, "Questions Cribles");

        when(questionTypeService.getEntries())
            .thenReturn(newArrayList(questionTypeQE, questionTypeQOSD, questionTypeQC));

        List<SelectValueDTO> questionTypes = service.getQuestionTypes();

        assertThat(questionTypes)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(questionTypeQE.getLeft().name(), questionTypeQE.getRight()),
                tuple(questionTypeQOSD.getLeft().name(), questionTypeQOSD.getRight()),
                tuple(questionTypeQC.getLeft().name(), questionTypeQC.getRight())
            );
    }

    @Test
    public void getStatutsEtapeRecherche() {
        ImmutablePair<String, String> statutEtape1 = ImmutablePair.of("running_all", "Étape en cours");
        ImmutablePair<String, String> statutEtape2 = ImmutablePair.of("ready_all", "Étape à venir");
        ImmutablePair<String, String> statutEtape3 = ImmutablePair.of("done_1", "validée manuellement");

        when(statutEtapeRechercheService.getEntries())
            .thenReturn(newArrayList(statutEtape1, statutEtape2, statutEtape3));

        List<SelectValueDTO> statutsEtapeRecherche = service.getStatutsEtapeRecherche();

        assertThat(statutsEtapeRecherche)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(statutEtape1.getLeft(), statutEtape1.getRight()),
                tuple(statutEtape2.getLeft(), statutEtape2.getRight()),
                tuple(statutEtape3.getLeft(), statutEtape3.getRight())
            );
    }

    @Test
    public void getValidationStatutsEtape() {
        ImmutablePair<Integer, String> statut1 = ImmutablePair.of(1, "validée manuellement");
        ImmutablePair<Integer, String> statut2 = ImmutablePair.of(2, "invalidée");
        ImmutablePair<Integer, String> statut3 = ImmutablePair.of(3, "validée automatiquement");

        when(validationStatutEtapeService.getEntries()).thenReturn(newArrayList(statut1, statut2, statut3));

        List<SelectValueDTO> statuts = service.getValidationStatutsEtape();

        assertThat(statuts)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(statut1.getLeft().toString(), statut1.getRight()),
                tuple(statut2.getLeft().toString(), statut2.getRight()),
                tuple(statut3.getLeft().toString(), statut3.getRight())
            );
    }

    @Test
    public void getEtatsQuestions() {
        ImmutablePair<String, String> etatQuestion1 = ImmutablePair.of("retiree", "Retirée");
        ImmutablePair<String, String> etatQuestion2 = ImmutablePair.of("caduque", "Caduque");
        ImmutablePair<String, String> etatQuestion3 = ImmutablePair.of("repondu", "Répondu");

        when(etatQuestionService.getEntries()).thenReturn(newArrayList(etatQuestion1, etatQuestion2, etatQuestion3));

        List<SelectValueDTO> etatsQuestionsRecherche = service.getEtatsQuestions();

        assertThat(etatsQuestionsRecherche)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(etatQuestion1.getLeft(), etatQuestion1.getRight()),
                tuple(etatQuestion2.getLeft(), etatQuestion2.getRight()),
                tuple(etatQuestion3.getLeft(), etatQuestion3.getRight())
            );
    }

    @Test
    public void getRubriquesAN() {
        ImmutablePair<String, String> rubriqueAN1 = ImmutablePair.of("action humanitaire", "action humanitaire");
        ImmutablePair<String, String> rubriqueAN2 = ImmutablePair.of("administration", "administration");
        ImmutablePair<String, String> rubriqueAN3 = ImmutablePair.of("agriculture", "agriculture");

        when(rubriqueANService.getEntries()).thenReturn(newArrayList(rubriqueAN1, rubriqueAN2, rubriqueAN3));

        List<SelectValueDTO> rubriquesANRecherche = service.getRubriquesAN();

        assertThat(rubriquesANRecherche)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(rubriqueAN1.getLeft(), rubriqueAN1.getRight()),
                tuple(rubriqueAN2.getLeft(), rubriqueAN2.getRight()),
                tuple(rubriqueAN3.getLeft(), rubriqueAN3.getRight())
            );
    }

    @Test
    public void getRubriquesSenat() {
        ImmutablePair<String, String> rubriqueSenat1 = ImmutablePair.of("Accords", "Accords");
        ImmutablePair<String, String> rubriqueSenat2 = ImmutablePair.of("Acier", "Acier");
        ImmutablePair<String, String> rubriqueSenat3 = ImmutablePair.of("Acteurs", "Acteurs");

        when(rubriqueSenatService.getEntries())
            .thenReturn(newArrayList(rubriqueSenat1, rubriqueSenat2, rubriqueSenat3));

        List<SelectValueDTO> rubriquesSenatRecherche = service.getRubriquesSenat();

        assertThat(rubriquesSenatRecherche)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(rubriqueSenat1.getLeft(), rubriqueSenat1.getRight()),
                tuple(rubriqueSenat2.getLeft(), rubriqueSenat2.getRight()),
                tuple(rubriqueSenat3.getLeft(), rubriqueSenat3.getRight())
            );
    }

    @Test
    public void getTetesAnalyseAN() {
        ImmutablePair<String, String> teteAnalyseAN1 = ImmutablePair.of("absence", "absence");
        ImmutablePair<String, String> teteAnalyseAN2 = ImmutablePair.of("abus", "abus");
        ImmutablePair<String, String> teteAnalyseAN3 = ImmutablePair.of("adhésion", "adhésion");

        when(teteAnalyseANService.getEntries())
            .thenReturn(newArrayList(teteAnalyseAN1, teteAnalyseAN2, teteAnalyseAN3));

        List<SelectValueDTO> tetesAnalyseANRecherche = service.getTetesAnalyseAN();

        assertThat(tetesAnalyseANRecherche)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(teteAnalyseAN1.getLeft(), teteAnalyseAN1.getRight()),
                tuple(teteAnalyseAN2.getLeft(), teteAnalyseAN2.getRight()),
                tuple(teteAnalyseAN3.getLeft(), teteAnalyseAN3.getRight())
            );
    }

    @Test
    public void getThemesSenat() {
        ImmutablePair<String, String> themeSenat1 = ImmutablePair.of("1", "Collectivités territoriales");
        ImmutablePair<String, String> themeSenat2 = ImmutablePair.of("2", "Agriculture et pêche");
        ImmutablePair<String, String> themeSenat3 = ImmutablePair.of("3", "Société");

        when(themeSenatService.getEntries()).thenReturn(newArrayList(themeSenat1, themeSenat2, themeSenat3));

        List<SelectValueDTO> themesSenatRecherche = service.getThemesSenat();

        assertThat(themesSenatRecherche)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(themeSenat1.getLeft(), themeSenat1.getRight()),
                tuple(themeSenat2.getLeft(), themeSenat2.getRight()),
                tuple(themeSenat3.getLeft(), themeSenat3.getRight())
            );
    }
}
