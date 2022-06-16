package fr.dila.reponses.ui.services.impl;

import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ID_DIRECTION_PILOTE_QUESTION;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ORIGINE_QUESTION;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_SCHEMA_PREFIX;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_TYPE_QUESTION;
import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX;
import static fr.dila.reponses.api.constant.VocabularyConstants.INDEXATION_ZONE_AN;
import static fr.dila.reponses.api.constant.VocabularyConstants.ORIGINE_QUESTION;
import static fr.dila.reponses.api.constant.VocabularyConstants.TYPE_QUESTION;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.service.DossierService;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.ui.bean.BordereauDTO;
import fr.dila.reponses.ui.bean.IndexationItemDTO;
import fr.dila.reponses.ui.services.BordereauUIService;
import fr.dila.reponses.ui.services.actions.IndexActionService;
import fr.dila.reponses.ui.services.actions.ReponsesBordereauActionService;
import fr.dila.reponses.ui.services.actions.ReponsesDocumentRoutingActionService;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class BordereauUIServiceImplTest {
    private static final String DOSSIER_ID = "dossierId";
    private static final String ORIGINE = "origine";
    private static final String ORIGINE_LABEL = "origine label";
    private static final String TYPE = "type";
    private static final String TYPE_LABEL = "type label";
    private static final String ID_MINISTERE_RATTACHEMENT = "001";
    private static final String LIBELLE_MINISTERE_RATTACHEMENT = "Ministère de rattachement";
    private static final String ID_DIRECTION_PILOTE = "002";
    private static final String LIBELLE_DIRECTION_PILOTE = "Direction pilote";
    private static final Calendar DATE_JO = new GregorianCalendar(2020, 7, 15);
    private static final Long PAGE_JO_REPONSE = 15L;
    private static final Calendar DATE_DEBUT_ETAPE = new GregorianCalendar(2020, 7, 16);
    private static final String MAILBOX_ID = "poste-11";
    private static final String POSTE_ID = "11";
    private static final String ID_MINISTERE_ATTRIBUTAIRE = "003";
    private static final String LIBELLE_MINISTERE_ATTRIBUTAIRE = "Ministère attributaire";
    private static final Optional<Calendar> DATE_REPONSE_SIGNALEMENT = Optional.of(Calendar.getInstance());

    private BordereauUIService service;

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel dossierDoc;

    @Mock
    private DocumentModel questionDoc;

    @Mock
    private Dossier dossier;

    @Mock
    private Question question;

    @Mock
    private Reponse reponse;

    @Mock
    private EntiteNode ministereRattachement;

    @Mock
    private EntiteNode ministereAttributaire;

    @Mock
    private SSRouteStep currentStep;

    @Mock
    private UniteStructurelleNode directionPilote;

    @Mock
    private PosteNode poste;

    @Mock
    @RuntimeService
    private IndexActionService indexActionService;

    @Mock
    @RuntimeService
    private ReponsesBordereauActionService bordereauActionService;

    @Mock
    @RuntimeService
    private DossierService dossierService;

    @Mock
    @RuntimeService
    private DossierLockActionService dossierLockActionService;

    @Mock
    @RuntimeService
    private ReponsesVocabularyService vocabularyService;

    @Mock
    @RuntimeService
    private STUsAndDirectionService stuAndDirectionService;

    @Mock
    @RuntimeService
    private STMinisteresService ministereService;

    @Mock
    @RuntimeService
    private ReponsesDocumentRoutingActionService reponsesDocumentRoutingActionService;

    @Mock
    @RuntimeService
    private MailboxPosteService mailboxPosteService;

    @Mock
    @RuntimeService
    private STPostesService postesService;

    @Mock
    @RuntimeService
    private OrganigrammeService organigrammeService;

    @Before
    public void setUp() {
        service = new BordereauUIServiceImpl();
    }

    @Test
    public void testGetBordereau() {
        SpecificContext context = new SpecificContext();
        context.setSession(session);
        context.setCurrentDocument(dossierDoc);

        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(dossierDoc.getPropertyValue(DOSSIER_SCHEMA_PREFIX + ':' + DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT))
            .thenReturn(ID_MINISTERE_ATTRIBUTAIRE);
        when(dossier.getQuestion(session)).thenReturn(question);
        when(dossierDoc.getId()).thenReturn(DOSSIER_ID);

        mockQuestionDoc();

        when(vocabularyService.getEntryLabel(ORIGINE_QUESTION, ORIGINE)).thenReturn(ORIGINE_LABEL);
        when(vocabularyService.getEntryLabel(TYPE_QUESTION, TYPE)).thenReturn(TYPE_LABEL);

        when(ministereService.getEntiteNode(ID_MINISTERE_RATTACHEMENT)).thenReturn(ministereRattachement);
        when(ministereRattachement.getLabel()).thenReturn(LIBELLE_MINISTERE_RATTACHEMENT);

        when(stuAndDirectionService.getLabel(ID_DIRECTION_PILOTE)).thenReturn(LIBELLE_DIRECTION_PILOTE);

        when(question.getDateReponseSignalement()).thenReturn(DATE_REPONSE_SIGNALEMENT);

        when(bordereauActionService.getPartMiseAJour(question)).thenReturn(true);

        when(dossier.getReponse(session)).thenReturn(reponse);

        when(bordereauActionService.getPartReponse(reponse)).thenReturn(true);
        when(bordereauActionService.getPartIndexationAN(question)).thenReturn(true);
        when(bordereauActionService.getPartIndexationSE(question)).thenReturn(true);
        when(bordereauActionService.getPartEditableIndexationComplementaire(context, DOSSIER_ID)).thenReturn(true);
        when(bordereauActionService.getPartIndexationComplementaireAN(question)).thenReturn(true);
        when(bordereauActionService.getPartIndexationComplementaireSE(question)).thenReturn(true);
        when(bordereauActionService.getPartIndexationComplementaireMotCle(question)).thenReturn(true);
        when(bordereauActionService.getPartFeuilleRoute(question, dossier)).thenReturn(true);
        when(bordereauActionService.getPartEditMinistereRattachement(context)).thenReturn(true);
        when(bordereauActionService.getPartEditDirectionPilote(context)).thenReturn(true);
        when(question.isQuestionTypeEcrite()).thenReturn(true);
        when(dossierLockActionService.getCanUnlockCurrentDossier(context)).thenReturn(true);

        when(reponse.getDateJOreponse()).thenReturn(DATE_JO);
        when(reponse.getPageJOreponse()).thenReturn(PAGE_JO_REPONSE);

        when(reponsesDocumentRoutingActionService.getCurrentStep(dossierDoc, session)).thenReturn(currentStep);
        when(currentStep.getDateDebutEtape()).thenReturn(DATE_DEBUT_ETAPE);
        when(currentStep.getDistributionMailboxId()).thenReturn(MAILBOX_ID);
        when(mailboxPosteService.getPosteIdFromMailboxId(MAILBOX_ID)).thenReturn(POSTE_ID);
        when(postesService.getPoste(POSTE_ID)).thenReturn(poste);

        Map<String, IndexationItemDTO> maplibellesRubriqueAN = new HashMap<>();
        IndexationItemDTO libellesRubriqueAN = new IndexationItemDTO();
        libellesRubriqueAN.getAllValues().add("libelle rubrique AN");
        libellesRubriqueAN.getLabels().put("libelle rubrique AN", "label.vocabulary.AN_rubrique");
        maplibellesRubriqueAN.put(AN_RUBRIQUE.getValue(), libellesRubriqueAN);
        when(indexActionService.getListIndexByZoneInMap(questionDoc, INDEXATION_ZONE_AN))
            .thenReturn(maplibellesRubriqueAN);

        when(dossierDoc.getType()).thenReturn("Dossier");

        OrganigrammeNode node = Mockito.mock(OrganigrammeNode.class);

        when(ministereService.getEntiteNode(ID_MINISTERE_ATTRIBUTAIRE)).thenReturn(ministereAttributaire);
        when(ministereAttributaire.getLabel()).thenReturn(LIBELLE_MINISTERE_ATTRIBUTAIRE);
        when(organigrammeService.getOrganigrammeNodeById(Mockito.any(), Mockito.any())).thenReturn(node);

        BordereauDTO dto = service.getBordereau(context);

        assertThat(dto.getOrigineQuestion()).isEqualTo(ORIGINE_LABEL);
        assertThat(dto.getTypeQuestion()).isEqualTo(TYPE_LABEL);
        assertThat(dto.getIntituleMinistereRattachement()).isEqualTo(LIBELLE_MINISTERE_RATTACHEMENT);
        assertThat(dto.getIntituleDirectionPilote()).isEqualTo(LIBELLE_DIRECTION_PILOTE);
        assertThat(dto.getPartMiseAJour()).isTrue();
        assertThat(dto.getPartReponse()).isTrue();
        assertThat(dto.getPartIndexationAN()).isTrue();
        assertThat(dto.getPartIndexationSENAT()).isTrue();
        assertThat(dto.getPartEditableIndexationComplementaire()).isTrue();
        assertThat(dto.getPartIndexationComplementaireAN()).isTrue();
        assertThat(dto.getPartIndexationComplementaireSE()).isTrue();
        assertThat(dto.getPartIndexationComplementaireMotCle()).isTrue();
        assertThat(dto.getPartFeuilleRoute()).isTrue();
        assertThat(dto.getPartEditMinistereRattachement()).isTrue();
        assertThat(dto.getPartEditDirectionPilote()).isTrue();
        assertThat(dto.isQuestionTypeEcrite()).isTrue();
        assertThat(dto.getIsEdit()).isTrue();
        assertThat(dto.getDatePublicationJOReponse()).isEqualTo(DATE_JO);
        assertThat(dto.getPageJOReponse()).isEqualTo(PAGE_JO_REPONSE);
        assertThat(dto.getTacheCoursDate()).isEqualTo(DATE_DEBUT_ETAPE);
        assertThat(dto.getIndexationDTO().getIndexationAN()).containsExactlyEntriesOf(maplibellesRubriqueAN);
        assertThat(dto.getMinistereAttributaireCourant()).isEqualTo(LIBELLE_MINISTERE_ATTRIBUTAIRE);
    }

    private void mockQuestionDoc() {
        when(question.getDocument()).thenReturn(questionDoc);
        when(questionDoc.getType()).thenReturn("Question");
        when(questionDoc.getPropertyValue(getXpathProperty(DOSSIER_ORIGINE_QUESTION))).thenReturn(ORIGINE);
        when(questionDoc.getPropertyValue(getXpathProperty(DOSSIER_TYPE_QUESTION))).thenReturn(TYPE);
        when(questionDoc.getPropertyValue(getXpathProperty(DOSSIER_ID_MINISTERE_RATTACHEMENT_QUESTION)))
            .thenReturn(ID_MINISTERE_RATTACHEMENT);
        when(questionDoc.getPropertyValue(getXpathProperty(DOSSIER_ID_DIRECTION_PILOTE_QUESTION)))
            .thenReturn(ID_DIRECTION_PILOTE);
    }

    private static String getXpathProperty(String propertyName) {
        return QUESTION_DOCUMENT_SCHEMA_PREFIX + ':' + propertyName;
    }
}
