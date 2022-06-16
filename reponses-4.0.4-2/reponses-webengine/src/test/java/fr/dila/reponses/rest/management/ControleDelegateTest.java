package fr.dila.reponses.rest.management;

import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_EVENT;
import static fr.dila.st.api.constant.STParametreConstant.OBJET_ALERTE_CONTROLE_PUBLICATION;
import static fr.dila.st.api.constant.STParametreConstant.TEXTE_ALERTE_CONTROLE_PUBLICATION;
import static fr.dila.st.api.constant.STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION;
import static fr.dila.st.api.constant.STWebserviceConstant.CONTROLE_PUBLICATION;
import static fr.dila.st.api.constant.STWebserviceConstant.PROFIL_SENAT;
import static fr.sword.xsd.reponses.TraitementStatut.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.service.JetonService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;
import fr.sword.xsd.reponses.ControleQuestion;
import fr.sword.xsd.reponses.ControleQuestionReponses;
import fr.sword.xsd.reponses.ControleReponses;
import fr.sword.xsd.reponses.MinistrePublication;
import fr.sword.xsd.reponses.ReferencePublication;
import fr.sword.xsd.reponses.ResultatControlePublication;
import fr.sword.xsd.reponses.ResultatControlePublicationQR;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class, MockitoFeature.class })
public class ControleDelegateTest extends CommonTestDelegate {
    private static final int PAGE_JO = 56;
    private static final int NO_PUBLICATION = 58;

    private ControleDelegate delegate;

    @Mock
    private DocumentModel reponseDoc;

    @Mock
    private Reponse reponse;

    @Mock
    @RuntimeService
    private JetonService jetonService;

    @Mock
    @RuntimeService
    private STParametreService paramService;

    @Mock
    @RuntimeService
    private STMailService mailService;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        delegate = new ControleDelegate(session);
    }

    @Test
    public void controlePublication() throws DatatypeConfigurationException {
        ControlePublicationRequest request = new ControlePublicationRequest();

        ControleQuestion controleQuestion = new ControleQuestion();
        controleQuestion.setIdQuestion(questionId);
        controleQuestion.setTexteQuestion("Texte question");
        ReferencePublication reference = new ReferencePublication();
        reference.setDatePublication(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        reference.setPageJo(PAGE_JO);
        reference.setNoPublication(NO_PUBLICATION);
        controleQuestion.setReferencePublication(reference);
        request.getQuestion().add(controleQuestion);

        ControleQuestionReponses controleQuestionReponses = new ControleQuestionReponses();
        controleQuestionReponses.getQuestion().add(controleQuestion);
        ControleReponses controleReponses = new ControleReponses();
        controleReponses.setReferencePublication(reference);
        MinistrePublication ministrePublication = new MinistrePublication();
        ministrePublication.setId(Integer.valueOf(ID_MINISTERE_ATTRIBUTAIRE));
        ministrePublication.setIntituleMin("Intitulé min");
        ministrePublication.setTitreJo("Titre JO");
        controleReponses.setMinistreJo(ministrePublication);
        controleReponses.setTexteReponse("Texte réponse");
        controleQuestionReponses.setReponses(controleReponses);
        request.getQuestionReponse().add(controleQuestionReponses);

        when(principal.getGroups()).thenReturn(ImmutableList.of(CONTROLE_PUBLICATION, PROFIL_SENAT));

        when(questionDossier.hasFeuilleRoute()).thenReturn(true);
        when(questionDossier.getReponse(session)).thenReturn(reponse);
        when(questionDossier.getNumeroQuestion()).thenReturn(Long.valueOf(NUMERO_QUESTION));

        when(question.getDateTransmissionAssemblees()).thenReturn(Calendar.getInstance());

        when(reponse.getDocument()).thenReturn(reponseDoc);
        when(reponse.getNumeroJOreponse()).thenReturn(null);
        when(reponse.getPageJOreponse()).thenReturn(null);

        ControlePublicationResponse response = delegate.controlePublication(request);

        assertThat(response.getResultatControleQuestion().size()).isEqualTo(1);
        ResultatControlePublication resultatControleQuestion = response.getResultatControleQuestion().get(0);
        assertThat(resultatControleQuestion.getStatut()).isEqualTo(OK);
        assertThat(resultatControleQuestion.getMessageErreur()).isNull();
        assertQuestionId(resultatControleQuestion.getIdQuestion(), questionId);

        verify(question).setDatePublicationJO(reference.getDatePublication().toGregorianCalendar());
        verify(question).setPageJO(String.valueOf(PAGE_JO));

        verify(session, times(3)).saveDocument(questionDoc);
        verify(session).saveDocument(reponseDoc);
        verify(session, times(2)).save();

        assertThat(response.getResultatControleQuestionReponse().size()).isEqualTo(1);
        ResultatControlePublicationQR resultatControlePublicationQR = response
            .getResultatControleQuestionReponse()
            .get(0);
        assertThat(resultatControlePublicationQR.getStatut()).isEqualTo(OK);
        assertThat(resultatControlePublicationQR.getMessageErreur()).isNull();
        assertThat(resultatControlePublicationQR.getIdQuestion().size()).isEqualTo(1);
        assertQuestionId(resultatControlePublicationQR.getIdQuestion().get(0), questionId);

        verify(reponse).setDateJOreponse(reference.getDatePublication().toGregorianCalendar());
        verify(reponse).setNumeroJOreponse(Long.valueOf(NO_PUBLICATION));
        verify(reponse).setPageJOreponse(Long.valueOf(PAGE_JO));

        verify(jetonService)
            .addDocumentInBasket(
                session,
                CHERCHER_RETOUR_PUBLICATION,
                SOURCE_QUESTION.toString(),
                questionDoc,
                String.valueOf(NUMERO_QUESTION),
                null,
                null
            );

        verify(paramService, never()).getParametreValue(session, OBJET_ALERTE_CONTROLE_PUBLICATION);
        verify(paramService, never()).getParametreValue(session, TEXTE_ALERTE_CONTROLE_PUBLICATION);
        verify(mailService, never())
            .sendTemplateMail(
                any(String.class),
                any(String.class),
                any(String.class),
                anyMapOf(String.class, Object.class)
            );

        verify(journalService, times(2))
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CONTROLE_PUBLICATION_EVENT,
                WEBSERVICE_CONTROLE_PUBLICATION_COMMENT
            );
    }
}
