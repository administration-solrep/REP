package fr.dila.reponses.core.recherche;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_ANALYSE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.MOTSCLEF_MINISTERE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RENVOI;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(ReponsesServiceLocator.class)
@PowerMockIgnore("javax.management.*")
public class RequeteImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private Requete requete;

    @Mock
    private DocumentModel documentModel;

    @Mock
    private ReponsesVocabularyService vocabularyService;

    @Test
    public void getListIndexByZone() {
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getVocabularyService()).thenReturn(vocabularyService);

        requete = new RequeteImpl(documentModel);

        String anRubriqueVocabulary = AN_RUBRIQUE.getValue();
        String taRubriqueVocabulary = TA_RUBRIQUE.getValue();
        String anAnalyseVocabulary = AN_ANALYSE.getValue();
        when(vocabularyService.getMapVocabularyToZone())
            .thenReturn(
                ImmutableMap.of(
                    VocabularyConstants.INDEXATION_ZONE_SENAT,
                    ImmutableList.of(SE_THEME.getValue(), SE_RUBRIQUE.getValue(), SE_RENVOI.getValue()),
                    VocabularyConstants.INDEXATION_ZONE_AN,
                    ImmutableList.of(anRubriqueVocabulary, taRubriqueVocabulary, anAnalyseVocabulary),
                    VocabularyConstants.INDEXATION_ZONE_MINISTERE,
                    ImmutableList.of(MOTSCLEF_MINISTERE.getValue())
                )
            );

        String anRubrique1 = "AN Rubrique 1";
        String anRubrique2 = "AN Rubrique 2";
        String anRubrique3 = "AN Rubrique 3";
        when(documentModel.getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA, anRubriqueVocabulary))
            .thenReturn(ImmutableList.of(anRubrique1, anRubrique2, anRubrique3));

        String taRubrique1 = "TA Rubrique 1";
        String taRubrique2 = "TA Rubrique 2";
        String taRubrique3 = "TA Rubrique 3";
        when(documentModel.getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA, taRubriqueVocabulary))
            .thenReturn(ImmutableList.of(taRubrique1, taRubrique2, taRubrique3));

        String anAnalyse1 = "AN Analyse 1";
        String anAnalyse2 = "AN Analyse 2";
        String anAnalyse3 = "AN Analyse 3";
        when(documentModel.getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA, anAnalyseVocabulary))
            .thenReturn(ImmutableList.of(anAnalyse1, anAnalyse2, anAnalyse3));

        List<String[]> results = requete.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN);

        assertThat(results)
            .containsExactly(
                new String[] { anRubriqueVocabulary, anRubrique1, "label.vocabulary." + anRubriqueVocabulary },
                new String[] { anRubriqueVocabulary, anRubrique2, "label.vocabulary." + anRubriqueVocabulary },
                new String[] { anRubriqueVocabulary, anRubrique3, "label.vocabulary." + anRubriqueVocabulary },
                new String[] { taRubriqueVocabulary, taRubrique1, "label.vocabulary." + taRubriqueVocabulary },
                new String[] { taRubriqueVocabulary, taRubrique2, "label.vocabulary." + taRubriqueVocabulary },
                new String[] { taRubriqueVocabulary, taRubrique3, "label.vocabulary." + taRubriqueVocabulary },
                new String[] { anAnalyseVocabulary, anAnalyse1, "label.vocabulary." + anAnalyseVocabulary },
                new String[] { anAnalyseVocabulary, anAnalyse2, "label.vocabulary." + anAnalyseVocabulary },
                new String[] { anAnalyseVocabulary, anAnalyse3, "label.vocabulary." + anAnalyseVocabulary }
            );
    }

    @Test
    public void getListIndexByZoneWithoutVocabularyService() {
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getVocabularyService()).thenReturn(null);

        requete = new RequeteImpl(documentModel);

        assertThat(requete.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN)).isEmpty();
    }
}
