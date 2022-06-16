package fr.dila.reponses.rest.management;

import static fr.dila.reponses.api.constant.ReponsesConfigConstant.VALIDATE_REPONSE_AUTHORIZED_TAGS_PARAMETER_NAME;
import static fr.dila.reponses.api.constant.ReponsesConfigConstant.VALIDATE_REPONSE_CONTENT_PARAMETER_NAME;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_REPONSES_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_REPONSES_EVENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_ENVOYER_ERRATA_REPONSES_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_ENVOYER_ERRATA_REPONSES_EVENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_ENVOYER_REPONSES_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_ENVOYER_REPONSES_EVENT;
import static fr.dila.st.api.constant.STWebserviceConstant.CHERCHER_REPONSES;
import static fr.dila.st.api.constant.STWebserviceConstant.ENVOYER_REPONSES;
import static fr.dila.st.api.constant.STWebserviceConstant.ENVOYER_REPONSES_ERRATA;
import static fr.sword.xsd.reponses.TraitementStatut.OK;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.cases.flux.RErratum;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.dila.st.core.util.UnrestrictedQueryRunner;
import fr.sword.xsd.reponses.ChercherReponsesRequest;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.EnvoyerReponseErrataRequest;
import fr.sword.xsd.reponses.EnvoyerReponseErrataResponse;
import fr.sword.xsd.reponses.EnvoyerReponsesRequest;
import fr.sword.xsd.reponses.EnvoyerReponsesResponse;
import fr.sword.xsd.reponses.ErratumReponse;
import fr.sword.xsd.reponses.Ministre;
import fr.sword.xsd.reponses.Reponse;
import fr.sword.xsd.reponses.ReponseQuestion;
import fr.sword.xsd.reponses.ResultatTraitement;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class, MockitoFeature.class })
public class ReponseDelegateTest extends CommonTestDelegate {
    private static final String AUTHORIZED_TAGS = "p,ul,ol,li,table,thead,tbody,tr,th,td,b,i,strong,em";

    private ReponseDelegate delegate;

    @Mock
    private UnrestrictedQueryRunner unrestrictedQueryRunner;

    @Mock
    private UnrestrictedGetDocumentRunner unrestrictedGetDocumentRunner;

    @Mock
    private DocumentModel dossierLinkDoc;

    @Mock
    private DossierLink dossierLink;

    @Mock
    private DocumentModel reponseDoc;

    @Mock
    private fr.dila.reponses.api.cases.Reponse reponseAdapter;

    @Mock
    private DocumentModel fondDeDossierDoc;

    @Mock
    private FondDeDossier fondDeDossier;

    @Mock
    @RuntimeService
    private ConfigService configService;

    @Mock
    @RuntimeService
    private ReponseService reponseService;

    @Mock
    @RuntimeService
    private JetonService jetonService;

    @Mock
    @RuntimeService
    private AllotissementService allotissementService;

    @Mock
    @RuntimeService
    private STMinisteresService ministereService;

    @Captor
    private ArgumentCaptor<List<RErratum>> errataListCaptor;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        delegate = new ReponseDelegate(session);
        delegate = spy(delegate);

        when(dossierLinkDoc.getAdapter(DossierLink.class)).thenReturn(dossierLink);

        when(dossierLink.getDocument()).thenReturn(dossierLinkDoc);

        doReturn(unrestrictedQueryRunner)
            .when(delegate)
            .createUnrestrictedQueryRunner(any(String.class), Mockito.anyVararg());

        doReturn(unrestrictedGetDocumentRunner).when(delegate).createUnrestrictedGetDocumentRunner();

        doReturn(unrestrictedGetDocumentRunner).when(delegate).createUnrestrictedGetDocumentRunner(any(String.class));

        when(unrestrictedQueryRunner.findAll()).thenReturn(new DocumentModelListImpl(singletonList(dossierLinkDoc)));
    }

    @Test
    public void chercherReponses() {
        ChercherReponsesRequest request = new ChercherReponsesRequest();
        EtatApplication etatApplication = mock(EtatApplication.class);
        DocumentRef docReference = mock(DocumentRef.class);
        DocumentModel dossierDoc = mock(DocumentModel.class);
        Dossier dossier = mock(Dossier.class);
        Dossier dossierReponse = mock(Dossier.class);
        fr.dila.reponses.api.cases.Reponse reponse = mock(fr.dila.reponses.api.cases.Reponse.class);

        List<String> groups = new ArrayList<>();
        groups.add(CHERCHER_REPONSES);
        groups.add(STWebserviceConstant.PROFIL_SENAT);
        request.getIdQuestions().add(questionId);

        when(principal.getGroups()).thenReturn(groups);
        when(etatApplicationService.getEtatApplicationDocument(session)).thenReturn(etatApplication);
        when(etatApplication.getRestrictionAcces()).thenReturn(false);
        when(principal.isMemberOf(STBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)).thenReturn(true);
        when(question.getLegislatureQuestion()).thenReturn(Long.valueOf(LEGISLATURE_QUESTION));
        when(question.getNumeroQuestion()).thenReturn(Long.valueOf(NUMERO_QUESTION));
        when(question.getOrigineQuestion()).thenReturn(SOURCE_QUESTION.toString());
        when(question.getTypeQuestion()).thenReturn(TYPE_QUESTION.toString());
        when(question.getDossierRef()).thenReturn(docReference);
        when(question.isRepondue()).thenReturn(true);
        when(unrestrictedGetDocumentRunner.getByRef(docReference)).thenReturn(dossierDoc);
        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(allotissementService.isAllotit(question, session)).thenReturn(false);
        when(delegate.getDossierFromQuestionId(questionId)).thenReturn(dossierReponse);
        when(dossierReponse.getIdMinistereAttributaireCourant()).thenReturn(ID_MINISTERE_ATTRIBUTAIRE);
        when(ministereService.getEntiteNode(Mockito.anyString())).thenReturn(entiteNode);
        when(dossierReponse.getReponse(session)).thenReturn(reponse);
        when(reponse.getTexteReponse()).thenReturn("Text de la réponse");
        when(dossierReponse.getFondDeDossier(session)).thenReturn(null);

        ChercherReponsesResponse response = delegate.chercherReponses(request);

        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.isDernierRenvoi()).isTrue();
        assertThat(response.getJetonReponses()).isEmpty();
        assertThat(response.getReponses().size()).isEqualTo(1);
        ReponseQuestion reponses = response.getReponses().get(0);
        assertQuestionId(reponses.getIdQuestions().get(0), questionId);
        assertThat(reponses.getReponse().getMinistreReponse().getId())
            .isEqualTo(Integer.valueOf(ID_MINISTERE_ATTRIBUTAIRE));
        assertThat(reponses.getReponse().getMinistreReponse().getTitreJo()).isEqualTo(TITRE_JO_MINISTERE);
        assertThat(reponses.getReponse().getMinistreReponse().getIntituleMinistere()).isEqualTo(LABEL_MINISTERE);
        assertThat(reponses.getReponse().getTexteReponse()).isNotEmpty();

        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_REPONSES_EVENT,
                WEBSERVICE_CHERCHER_REPONSES_COMMENT
            );
    }

    @Test
    public void envoyerReponses() {
        EnvoyerReponsesRequest request = new EnvoyerReponsesRequest();

        ReponseQuestion reponseQuestion = new ReponseQuestion();
        reponseQuestion.getIdQuestions().add(questionId);
        Reponse reponse = new Reponse();
        reponse.setTexteReponse("Texte réponse");
        Ministre ministreReponse = new Ministre();
        ministreReponse.setId(123456);
        ministreReponse.setIntituleMinistere("Ministère de l'Intérieur");
        ministreReponse.setTitreJo("Titre JO");
        ministreReponse.setTitreMinistre("Titre ministre");
        reponse.setMinistreReponse(ministreReponse);
        reponseQuestion.setReponse(reponse);

        request.getReponseQuestion().add(reponseQuestion);

        when(principal.getGroups()).thenReturn(singletonList(ENVOYER_REPONSES));
        when(configService.getBooleanValue(VALIDATE_REPONSE_CONTENT_PARAMETER_NAME)).thenReturn(true);

        when(questionDossier.createReponse(session, Long.valueOf(NUMERO_QUESTION), null)).thenReturn(reponseDoc);
        when(questionDossier.getFondDeDossier(session)).thenReturn(fondDeDossier);

        when(fondDeDossier.getDocument()).thenReturn(fondDeDossierDoc);

        when(reponseService.getReponseFromDossier(session, questionDoc)).thenReturn(null);
        when(reponseService.saveReponseFromMinistere(session, reponseDoc)).thenReturn(reponseDoc);

        when(configService.getValue(VALIDATE_REPONSE_AUTHORIZED_TAGS_PARAMETER_NAME)).thenReturn(AUTHORIZED_TAGS);

        when(reponseDoc.getAdapter(fr.dila.reponses.api.cases.Reponse.class)).thenReturn(reponseAdapter);

        EnvoyerReponsesResponse response = delegate.envoyerReponses(request);

        assertThat(response.getResultatTraitement().size()).isEqualTo(1);
        ResultatTraitement resultat = response.getResultatTraitement().get(0);
        assertThat(resultat.getStatut()).isEqualTo(OK);
        assertThat(resultat.getMessageErreur()).isNull();

        verify(reponseAdapter).setTexteReponse(reponse.getTexteReponse());
        verify(reponseAdapter).setIdAuteurReponse(Integer.valueOf(ministreReponse.getId()).toString());
        verify(question).setHasReponseInitiee(true);
        verify(session).saveDocument(questionDoc);
        verify(session).save();
        verify(reponseService).saveReponseFromMinistere(session, reponseDoc);
        verify(dossierDistributionService).validerEtape(session, questionDoc, dossierLinkDoc);
        verify(journalService)
            .journaliserActionAdministration(
                session,
                questionDoc,
                WEBSERVICE_ENVOYER_REPONSES_EVENT,
                WEBSERVICE_ENVOYER_REPONSES_COMMENT
            );
    }

    @Test
    public void envoyerReponseErrata() {
        EnvoyerReponseErrataRequest request = new EnvoyerReponseErrataRequest();

        ErratumReponse erratum = new ErratumReponse();
        erratum.setTexteConsolide("Texte consolidé");
        erratum.setTexteErratum("Texte erratum");
        erratum.getIdQuestion().add(questionId);

        request.getErratum().add(erratum);

        when(principal.getGroups()).thenReturn(singletonList(ENVOYER_REPONSES_ERRATA));

        when(questionDossier.getReponse(session)).thenReturn(reponseAdapter);

        when(reponseAdapter.isPublished()).thenReturn(true);
        when(reponseAdapter.getDocument()).thenReturn(reponseDoc);

        when(allotissementService.getAllotissement(null, session)).thenReturn(null);

        EnvoyerReponseErrataResponse response = delegate.envoyerReponseErrata(request);

        assertThat(response.getResultatTraitement().size()).isEqualTo(1);
        ResultatTraitement resultat = response.getResultatTraitement().get(0);
        assertThat(resultat.getStatut()).isEqualTo(OK);
        assertThat(resultat.getMessageErreur()).isNull();

        verify(reponseAdapter).setErrata(errataListCaptor.capture());
        RErratum erratumReponse = errataListCaptor.getValue().get(0);
        assertThat(erratumReponse.getDatePublication().toZonedDateTime().toLocalDateTime())
            .isCloseTo(now(), within(10, SECONDS));
        assertThat(erratumReponse.getPageJo()).isEqualTo(0);
        assertThat(erratumReponse.getTexteErratum()).isEqualTo(erratum.getTexteErratum());
        assertThat(erratumReponse.getTexteConsolide()).isEqualTo(erratum.getTexteConsolide());

        verify(reponseAdapter).setTexteReponse(erratum.getTexteConsolide());
        verify(session).saveDocument(reponseDoc);
        verify(session).save();
        verify(dossierDistributionService).validerEtape(session, questionDoc, dossierLinkDoc);
        verify(journalService)
            .journaliserActionAdministration(
                session,
                questionDoc,
                WEBSERVICE_ENVOYER_ERRATA_REPONSES_EVENT,
                WEBSERVICE_ENVOYER_ERRATA_REPONSES_COMMENT
            );
    }
}
