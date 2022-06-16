package fr.dila.reponses.rest.management;

import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DOCUMENT_TYPE;
import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED;
import static fr.sword.xsd.reponses.QuestionSource.SENAT;
import static fr.sword.xsd.reponses.QuestionType.QE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import avro.shaded.com.google.common.collect.ImmutableSet;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.QuestionType;
import org.junit.Before;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.schema.DocumentType;
import org.nuxeo.ecm.core.schema.DocumentTypeImpl;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.runtime.mockito.RuntimeService;

public class CommonTestDelegate {
    protected static final String ID_MINISTERE_ATTRIBUTAIRE = "12345678";
    protected static final int NUMERO_QUESTION = 1;
    protected static final String TITRE_SENAT = "Titre Sénat";
    protected static final String TEXTE_QUESTION = "Texte question";
    protected static final String TEXTE_JOINT = "Texte joint";
    protected static final String PAGE_JO = "3599";
    protected static final String CIVILITE_AUTEUR = "M";
    protected static final String NOM_AUTEUR = "Dupont";
    protected static final String PRENOM_AUTEUR = "Philippe";
    protected static final QuestionSource SOURCE_QUESTION = SENAT;
    protected static final String LABEL_MINISTERE = "Ministère de la justice";
    protected static final String TITRE_JO_MINISTERE = "Titre JO";

    protected static final String docQuestionId = "d3338814-d839-46f0-99fe-a8de03ea63ba";
    protected static final QuestionType TYPE_QUESTION = QE;
    protected static final int LEGISLATURE_QUESTION = 1;

    @Mock
    protected CloseableCoreSession session;

    @Mock
    protected SSPrincipal principal;

    @Mock
    protected DocumentModel questionDoc;

    @Mock
    protected Dossier questionDossier;

    @Mock
    protected Question question;

    @Mock
    private EtatApplication etatApplication;

    @Mock
    private SchemaManager schemaManager;

    @Mock
    @RuntimeService
    protected STMinisteresService ministeresService;

    @Mock
    @RuntimeService
    protected AllotissementService allotissementService;

    @Mock
    @RuntimeService
    protected DossierDistributionService dossierDistributionService;

    @Mock
    @RuntimeService
    protected JournalService journalService;

    @Mock
    @RuntimeService
    protected EtatApplicationService etatApplicationService;

    protected QuestionId questionId;
    protected EntiteNode entiteNode;

    @Before
    public void setUp() {
        when(session.getPrincipal()).thenReturn(principal);
        DocumentRef docRefQuestion = new IdRef(docQuestionId);
        when(session.getDocument(docRefQuestion)).thenReturn(questionDoc);

        when(principal.isMemberOf(ADMIN_ACCESS_UNRESTRICTED)).thenReturn(false);
        when(principal.isAdministrator()).thenReturn(true);
        when(principal.getMinistereIdSet()).thenReturn(ImmutableSet.of(ID_MINISTERE_ATTRIBUTAIRE));

        when(etatApplicationService.getEtatApplicationDocument(session)).thenReturn(etatApplication);
        when(etatApplication.getRestrictionAcces()).thenReturn(false);

        when(
            dossierDistributionService.retrieveDocumentQuestionId(
                session,
                NUMERO_QUESTION,
                TYPE_QUESTION.name(),
                SOURCE_QUESTION.name(),
                LEGISLATURE_QUESTION
            )
        )
            .thenReturn(docQuestionId);

        DocumentType questionDocumentType = new DocumentTypeImpl(QUESTION_DOCUMENT_TYPE);
        when(schemaManager.getDocumentType(QUESTION_DOCUMENT_TYPE)).thenReturn(questionDocumentType);

        when(questionDoc.getAdapter(fr.dila.reponses.api.cases.Question.class)).thenReturn(question);
        when(questionDoc.getId()).thenReturn(docQuestionId);
        when(questionDoc.getRef()).thenReturn(docRefQuestion);

        when(question.getDocument()).thenReturn(questionDoc);
        when(question.getDossier(session)).thenReturn(questionDossier);

        when(question.getNumeroQuestion()).thenReturn((long) NUMERO_QUESTION);
        when(question.getTypeQuestion()).thenReturn(TYPE_QUESTION.name());
        when(question.getLegislatureQuestion()).thenReturn((long) LEGISLATURE_QUESTION);
        when(question.getOrigineQuestion()).thenReturn(SOURCE_QUESTION.value());
        when(question.getSenatQuestionTitre()).thenReturn(TITRE_SENAT);
        when(question.getTexteQuestion()).thenReturn(TEXTE_QUESTION);
        when(question.getTexteJoint()).thenReturn(TEXTE_JOINT);
        when(question.getPageJO()).thenReturn(PAGE_JO);
        when(question.getCiviliteAuteur()).thenReturn(CIVILITE_AUTEUR);
        when(question.getNomAuteur()).thenReturn(NOM_AUTEUR);
        when(question.getPrenomAuteur()).thenReturn(PRENOM_AUTEUR);
        when(question.getIdMinistereAttributaire()).thenReturn(ID_MINISTERE_ATTRIBUTAIRE);

        when(questionDossier.getDocument()).thenReturn(questionDoc);
        when(questionDossier.getQuestion(session)).thenReturn(question);
        when(questionDossier.getIdMinistereAttributaireCourant()).thenReturn(ID_MINISTERE_ATTRIBUTAIRE);

        entiteNode = new EntiteNodeImpl();
        entiteNode.setLabel(LABEL_MINISTERE);
        entiteNode.setEdition(TITRE_JO_MINISTERE);
        when(ministeresService.getEntiteNode(ID_MINISTERE_ATTRIBUTAIRE)).thenReturn(entiteNode);

        questionId = new QuestionId();
        questionId.setNumeroQuestion(NUMERO_QUESTION);
        questionId.setType(TYPE_QUESTION);
        questionId.setLegislature(LEGISLATURE_QUESTION);
        questionId.setSource(SOURCE_QUESTION);
    }

    protected static void assertQuestionId(QuestionId questionId, QuestionId expectedQuestionId) {
        assertThat(questionId.getNumeroQuestion()).isEqualTo(expectedQuestionId.getNumeroQuestion());
        assertThat(questionId.getType()).isEqualTo(expectedQuestionId.getType());
        assertThat(questionId.getLegislature()).isEqualTo(expectedQuestionId.getLegislature());
        assertThat(questionId.getSource()).isEqualTo(expectedQuestionId.getSource());
    }
}
