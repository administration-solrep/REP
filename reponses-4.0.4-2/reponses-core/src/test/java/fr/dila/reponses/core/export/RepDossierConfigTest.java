package fr.dila.reponses.core.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(STServiceLocator.class)
@PowerMockIgnore("javax.management.*")
public class RepDossierConfigTest {
    private static final List<String> MOTS_CLES = ImmutableList.of("mots clés 1", "mots clés 2", "mots clés 3");
    private static final String ORIGINE_QUESTION = "origine question";
    private static final Long NUMERO_QUESTION = 2L;
    private static final String TYPE_QUESTION = "type question";
    private static final String NOM_COMPLET_AUTEUR = "nom complet auteur";
    private static final String INTITULE_MINISTERE_ATTRIBUTAIRE = "intitulé ministère attributaire";
    private static final String ETAT_QUESTION_SIMPLE_ID = "repondu";
    private static final String ETAT_QUESTION_SIMPLE_LABEL = "Répondu";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel item;

    @Mock
    private Dossier dossier;

    @Mock
    private Question question;

    @Mock
    private VocabularyService vocabularyService;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(STServiceLocator.class);
        Mockito.when(STServiceLocator.getVocabularyService()).thenReturn(vocabularyService);
        Mockito
            .when(vocabularyService.getAllEntries(VocabularyConstants.ETAT_QUESTION))
            .thenReturn(ImmutableMap.of(ETAT_QUESTION_SIMPLE_ID, ETAT_QUESTION_SIMPLE_LABEL));

        when(question.getMotsClef()).thenReturn(MOTS_CLES);
        when(question.getOrigineQuestion()).thenReturn(ORIGINE_QUESTION);
        when(dossier.getNumeroQuestion()).thenReturn(NUMERO_QUESTION);
        when(question.getTypeQuestion()).thenReturn(TYPE_QUESTION);
        Calendar datePublicationJO = Calendar.getInstance();
        datePublicationJO.set(Calendar.YEAR, 2021);
        datePublicationJO.set(Calendar.MONTH, Calendar.OCTOBER);
        datePublicationJO.set(Calendar.DAY_OF_MONTH, 26);
        when(question.getDatePublicationJO()).thenReturn(datePublicationJO);
        when(question.getNomCompletAuteur()).thenReturn(NOM_COMPLET_AUTEUR);
        when(question.getIntituleMinistereAttributaire()).thenReturn(INTITULE_MINISTERE_ATTRIBUTAIRE);
        when(question.getEtatQuestionSimple()).thenReturn(ETAT_QUESTION_SIMPLE_ID);
    }

    @Test
    public void getDataCellsForDossier() {
        when(item.getType()).thenReturn(DossierConstants.DOSSIER_DOCUMENT_TYPE);
        when(item.getAdapter(Dossier.class)).thenReturn(dossier);
        when(dossier.getQuestion(session)).thenReturn(question);

        RepDossierConfig dossierConfig = new RepDossierConfig(ImmutableList.of(item));

        assertThat(dossierConfig.getDataCells(session, item))
            .hasSize(dossierConfig.getSheetName().getHeadersSize())
            .containsExactly(
                ORIGINE_QUESTION,
                NUMERO_QUESTION.toString(),
                TYPE_QUESTION,
                "26/10/2021",
                NOM_COMPLET_AUTEUR,
                INTITULE_MINISTERE_ATTRIBUTAIRE,
                "mots clés 1, mots clés 2, mots clés 3",
                ETAT_QUESTION_SIMPLE_LABEL
            );
    }

    @Test
    public void getDataCellsForQuestion() {
        when(item.getType()).thenReturn(DossierConstants.QUESTION_DOCUMENT_TYPE);
        when(item.getAdapter(Question.class)).thenReturn(question);
        when(question.getDossier(session)).thenReturn(dossier);

        RepDossierConfig dossierConfig = new RepDossierConfig(ImmutableList.of(item));

        assertThat(dossierConfig.getDataCells(session, item))
            .hasSize(dossierConfig.getSheetName().getHeadersSize())
            .containsExactly(
                ORIGINE_QUESTION,
                NUMERO_QUESTION.toString(),
                TYPE_QUESTION,
                "26/10/2021",
                NOM_COMPLET_AUTEUR,
                INTITULE_MINISTERE_ATTRIBUTAIRE,
                "mots clés 1, mots clés 2, mots clés 3",
                ETAT_QUESTION_SIMPLE_LABEL
            );
    }

    @Test
    public void getDataCellsForQuestionWithoutEtatQuestion() {
        when(item.getType()).thenReturn(DossierConstants.QUESTION_DOCUMENT_TYPE);
        when(item.getAdapter(Question.class)).thenReturn(question);
        when(question.getDossier(session)).thenReturn(dossier);
        String etatQuestion = "état question";
        when(question.getEtatQuestionSimple()).thenReturn(etatQuestion);

        RepDossierConfig dossierConfig = new RepDossierConfig(ImmutableList.of(item));

        assertThat(dossierConfig.getDataCells(session, item))
            .hasSize(dossierConfig.getSheetName().getHeadersSize())
            .containsExactly(
                ORIGINE_QUESTION,
                NUMERO_QUESTION.toString(),
                TYPE_QUESTION,
                "26/10/2021",
                NOM_COMPLET_AUTEUR,
                INTITULE_MINISTERE_ATTRIBUTAIRE,
                "mots clés 1, mots clés 2, mots clés 3",
                etatQuestion
            );
    }

    @Test
    public void getDataCellsWithoutQuestion() {
        when(item.getType()).thenReturn(DossierConstants.DOSSIER_DOCUMENT_TYPE);
        when(item.getAdapter(Dossier.class)).thenReturn(dossier);

        RepDossierConfig dossierConfig = new RepDossierConfig(ImmutableList.of(item));

        assertThat(dossierConfig.getDataCells(session, item))
            .hasSize(dossierConfig.getSheetName().getHeadersSize())
            .containsOnlyNulls();
    }
}
