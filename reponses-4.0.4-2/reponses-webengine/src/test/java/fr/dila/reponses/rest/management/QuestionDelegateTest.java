package fr.dila.reponses.rest.management;

import static fr.dila.reponses.api.constant.DossierConstants.QUESTION_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.DossierConstants.REPONSE_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHANGER_ETAT_QUESTION_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHANGER_ETAT_QUESTION_EVENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_CHANGEMENT_ETAT_QUESTION_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_CHANGEMENT_ETAT_QUESTION_EVENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_DOSSIER_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_DOSSIER_EVENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_QUESTION_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_QUESTION_EVENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_EVENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_ENVOYER_QUESTIONS_COMMENT;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.WEBSERVICE_ENVOYER_QUESTIONS_EVENT;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_EN_COURS;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_REPONDU;
import static fr.dila.reponses.api.constant.VocabularyConstants.ETAT_QUESTION_SIGNALEE;
import static fr.dila.reponses.api.constant.VocabularyConstants.GROUPE_POLITIQUE;
import static fr.dila.st.api.constant.STParametreConstant.DELAI_QUESTION_SIGNALEE;
import static fr.dila.st.api.constant.STWebserviceConstant.CHERCHER_CHANGEMENT_ETAT_QUESTION;
import static fr.dila.st.api.constant.STWebserviceConstant.CHERCHER_ERRATA_QUESTIONS;
import static fr.dila.st.api.constant.STWebserviceConstant.CHERCHER_QUESTIONS;
import static fr.dila.st.api.constant.STWebserviceConstant.ENVOYER_CHANGEMENT_ETAT;
import static fr.dila.st.api.constant.STWebserviceConstant.ENVOYER_QUESTIONS;
import static fr.dila.st.api.constant.STWebserviceConstant.PROFIL_AN;
import static fr.dila.st.api.constant.STWebserviceConstant.PROFIL_SENAT;
import static fr.dila.st.api.constant.STWebserviceConstant.RECHERCHER_DOSSIER;
import static fr.sword.xsd.reponses.Civilite.M;
import static fr.sword.xsd.reponses.ErratumType.ERRATUM;
import static fr.sword.xsd.reponses.EtatQuestion.EN_COURS;
import static fr.sword.xsd.reponses.EtatQuestion.SIGNALE;
import static fr.sword.xsd.reponses.QuestionSource.SENAT;
import static fr.sword.xsd.reponses.QuestionType.QG;
import static fr.sword.xsd.reponses.TraitementStatut.KO;
import static fr.sword.xsd.reponses.TraitementStatut.OK;
import static java.time.LocalDateTime.now;
import static java.time.Month.APRIL;
import static java.time.Month.MARCH;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.QErratum;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.service.JetonService;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.core.cases.flux.QErratumImpl;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.util.UnrestrictedQueryRunner;
import fr.sword.xsd.reponses.Auteur;
import fr.sword.xsd.reponses.ChangementEtatQuestion;
import fr.sword.xsd.reponses.ChangerEtatQuestionsRequest;
import fr.sword.xsd.reponses.ChangerEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherChangementDEtatQuestionsRequest;
import fr.sword.xsd.reponses.ChercherChangementDEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherErrataQuestionsRequest;
import fr.sword.xsd.reponses.ChercherErrataQuestionsResponse;
import fr.sword.xsd.reponses.ChercherQuestionsRequest;
import fr.sword.xsd.reponses.ChercherQuestionsResponse;
import fr.sword.xsd.reponses.ChercherQuestionsResponse.Questions;
import fr.sword.xsd.reponses.EnvoyerQuestionsRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsResponse;
import fr.sword.xsd.reponses.ErratumQuestion;
import fr.sword.xsd.reponses.IndexationSenat;
import fr.sword.xsd.reponses.IndexationSenat.Renvois;
import fr.sword.xsd.reponses.Ministre;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionReponse;
import fr.sword.xsd.reponses.RechercherDossierRequest;
import fr.sword.xsd.reponses.RechercherDossierResponse;
import fr.sword.xsd.reponses.ReponsePubliee;
import fr.sword.xsd.reponses.ResultatTraitement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class, MockitoFeature.class })
public class QuestionDelegateTest extends CommonTestDelegate {
    private static final String DELAI_QUESTION_SIGNALEE_VALUE = "7";
    private static final int ID_MINISTRE_DEPOT = 123457;

    @Mock
    private QuestionStateChange questionStateChange;

    @Mock
    private DocumentModel newQuestionDoc;

    @Mock
    private Question newQuestion;

    @Mock
    private DocumentModel newQuestionDossierDoc;

    @Mock
    private Dossier newQuestionDossier;

    @Mock
    private DocumentModel reponseDoc;

    @Mock
    private Reponse reponse;

    @Mock
    private DocumentModel newGroupePol;

    @Mock
    private UnrestrictedQueryRunner unrestrictedQueryRunner;

    @Mock
    @RuntimeService
    private JetonService jetonService;

    @Mock
    @RuntimeService
    private STLockService lockService;

    @Mock
    @RuntimeService
    private STParametreService paramService;

    @Mock
    @RuntimeService
    private VocabularyService vocabularyService;

    @Mock
    @RuntimeService
    private ReponsesMailService reponsesMailService;

    @Captor
    private ArgumentCaptor<GregorianCalendar> dateReceptionQuestionCaptor;

    private QuestionDelegate delegate;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        delegate = new QuestionDelegate(session);
        delegate = spy(delegate);

        when(paramService.getParametreValue(session, DELAI_QUESTION_SIGNALEE))
            .thenReturn(DELAI_QUESTION_SIGNALEE_VALUE);
    }

    @Test
    public void chercherQuestions() {
        ChercherQuestionsRequest request = new ChercherQuestionsRequest();
        request.getIdQuestions().add(questionId);

        when(principal.getGroups()).thenReturn(singletonList(CHERCHER_QUESTIONS));

        ChercherQuestionsResponse response = delegate.chercherQuestions(request);

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.isDernierRenvoi()).isTrue();
        assertThat(response.getJetonQuestions()).isEmpty();
        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getQuestions().size()).isEqualTo(1);
        Questions questions = response.getQuestions().get(0);
        assertQuestionId(questions.getIdQuestion(), questionId);
        assertThat(questions.getTitreSenat()).isEqualTo(TITRE_SENAT);
        assertThat(questions.getTexte()).isEqualTo(TEXTE_QUESTION);
        assertThat(questions.getTexteJoint()).isEqualTo(TEXTE_JOINT);
        assertThat(questions.getPageJo()).isEqualTo(Integer.parseInt(PAGE_JO));
        assertThat(questions.getAuteur().getCivilite().name()).isEqualTo(CIVILITE_AUTEUR);
        assertThat(questions.getAuteur().getNom()).isEqualTo(NOM_AUTEUR);
        assertThat(questions.getAuteur().getPrenom()).isEqualTo(PRENOM_AUTEUR);
        assertThat(questions.getMinistreAttributaire().getId()).isEqualTo(Integer.parseInt(ID_MINISTERE_ATTRIBUTAIRE));

        verify(journalService)
            .journaliserActionAdministration(
                session,
                questionDoc,
                WEBSERVICE_CHERCHER_QUESTION_EVENT,
                WEBSERVICE_CHERCHER_QUESTION_COMMENT
            );
    }

    @Test
    public void chercherErrataQuestions() throws DatatypeConfigurationException {
        ChercherErrataQuestionsRequest request = new ChercherErrataQuestionsRequest();
        request.getIdQuestions().add(questionId);

        QErratum erratum = new QErratumImpl();
        erratum.setDatePublication(new GregorianCalendar());
        erratum.setPageJo(1);
        erratum.setTexteConsolide("Texte consolidé");
        erratum.setTexteErratum("Texte erratum");

        when(principal.getGroups()).thenReturn(singletonList(CHERCHER_ERRATA_QUESTIONS));
        when(question.getErrata()).thenReturn(singletonList(erratum));

        ChercherErrataQuestionsResponse response = delegate.chercherErrataQuestions(request);

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.isDernierRenvoi()).isTrue();
        assertThat(response.getJetonErrata()).isEmpty();
        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getErrata().size()).isEqualTo(1);
        ErratumQuestion erratumQuestion = response.getErrata().get(0);
        assertQuestionId(erratumQuestion.getIdQuestion(), questionId);
        assertThat(erratumQuestion.getDatePublicationErratum())
            .isEqualTo(DatatypeFactory.newInstance().newXMLGregorianCalendar(erratum.getDatePublication()));
        assertThat(erratumQuestion.getPageJoErratum()).isEqualTo(erratum.getPageJo());
        assertThat(erratumQuestion.getTexteConsolide()).isEqualTo(erratum.getTexteConsolide());
        assertThat(erratumQuestion.getTexteErratum()).isEqualTo(erratum.getTexteErratum());
        assertThat(erratumQuestion.getType()).isEqualTo(ERRATUM);

        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_ERRATA_QUESTION_EVENT,
                WEBSERVICE_CHERCHER_ERRATA_QUESTION_COMMENT
            );
    }

    @Test
    public void changerEtatQuestions() throws DatatypeConfigurationException {
        ChangerEtatQuestionsRequest request = new ChangerEtatQuestionsRequest();

        ChangementEtatQuestion nouvelEtat = new ChangementEtatQuestion();
        nouvelEtat.setIdQuestion(questionId);
        nouvelEtat.setDateModif(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        nouvelEtat.setTypeModif(SIGNALE);

        request.getNouvelEtat().add(nouvelEtat);

        when(principal.getGroups()).thenReturn(ImmutableList.of(ENVOYER_CHANGEMENT_ETAT, PROFIL_SENAT));

        ChangerEtatQuestionsResponse response = delegate.changerEtatQuestions(request);

        assertThat(response.getResultatTraitement().size()).isEqualTo(1);
        ResultatTraitement resultat = response.getResultatTraitement().get(0);
        assertThat(resultat.getStatut()).isEqualTo(OK);
        assertThat(resultat.getMessageErreur()).isNull();
        assertQuestionId(resultat.getIdQuestion(), questionId);

        verify(lockService, never()).getLockDetails(any(CoreSession.class), any(DocumentModel.class));
        verify(lockService, never()).unlockDocUnrestricted(any(CoreSession.class), any(DocumentModel.class));
        verify(allotissementService, never()).removeDossierFromLotIfNeeded(session, questionDossier);
        verify(paramService).getParametreValue(session, DELAI_QUESTION_SIGNALEE);
        verify(session).saveDocument(questionDoc);
        verify(session).save();
        verify(jetonService)
            .addDocumentInBasket(
                session,
                CHERCHER_CHANGEMENT_ETAT_QUESTION,
                ID_MINISTERE_ATTRIBUTAIRE,
                questionDoc,
                String.valueOf(NUMERO_QUESTION),
                null,
                null
            );
        verify(reponsesMailService).sendMailAfterStateChangedQuestion(session, questionDoc, ETAT_QUESTION_SIGNALEE);
        verify(journalService)
            .journaliserActionAdministration(
                session,
                questionDoc,
                WEBSERVICE_CHANGER_ETAT_QUESTION_EVENT,
                WEBSERVICE_CHANGER_ETAT_QUESTION_COMMENT
            );
    }

    @Test
    public void envoyerQuestions() throws DatatypeConfigurationException {
        EnvoyerQuestionsRequest request = new EnvoyerQuestionsRequest();

        QuestionReponse questionReponse = new QuestionReponse();

        ReponsePubliee reponsePubliee = new ReponsePubliee();
        reponsePubliee.setTexteReponse("Texte réponse");
        reponsePubliee.setDateJo(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        Ministre ministreReponse = new Ministre();
        ministreReponse.setId(123456);
        ministreReponse.setIntituleMinistere("Ministère de l'Intérieur");
        ministreReponse.setTitreJo("Titre JO");
        ministreReponse.setTitreMinistre("Titre ministre");
        reponsePubliee.setMinistreReponse(ministreReponse);
        reponsePubliee.setPageJo(1);

        questionReponse.setReponse(reponsePubliee);

        fr.sword.xsd.reponses.Question questionToSend = new fr.sword.xsd.reponses.Question();

        QuestionId newQuestionId = new QuestionId();
        newQuestionId.setNumeroQuestion(2);
        newQuestionId.setType(QG);
        newQuestionId.setLegislature(2);
        newQuestionId.setSource(SENAT);

        questionToSend.setIdQuestion(newQuestionId);

        Auteur auteur = new Auteur();
        auteur.setCivilite(M);
        auteur.setNom("Martin");
        auteur.setPrenom("Pierre");
        auteur.setCirconscription("Circonscription");
        auteur.setGrpPol("Groupe politique");
        auteur.setIdMandat("Id mandat");
        questionToSend.setAuteur(auteur);

        questionToSend.setTexte("Texte question");
        questionToSend.setTexteJoint("Texte joint");
        questionToSend.setDatePublicationJo(
            DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())
        );
        questionToSend.setEtatQuestion(EN_COURS);
        questionToSend.setPageJo(548);

        Ministre ministreDepot = new Ministre();
        ministreDepot.setId(ID_MINISTRE_DEPOT);
        ministreDepot.setIntituleMinistere("Ministère de l'Economie");
        ministreDepot.setTitreJo("Titre JO");
        ministreDepot.setTitreMinistre("Titre ministre");
        questionToSend.setMinistreDepot(ministreDepot);

        IndexationSenat indexationSenat = new IndexationSenat();
        indexationSenat.getTheme().add("Theme 1");
        indexationSenat.getRubrique().add("Rubrique 1");
        Renvois renvois = new Renvois();
        renvois.getRenvoi().add("Renvoi 1");
        indexationSenat.setRenvois(renvois);
        questionToSend.setIndexationSenat(indexationSenat);
        questionToSend.setTitreSenat("Titre question sénat");
        questionReponse.getQuestion().add(questionToSend);

        request.getQuestionReponse().add(questionReponse);

        when(principal.getGroups()).thenReturn(ImmutableList.of(ENVOYER_QUESTIONS, PROFIL_SENAT));

        when(session.createDocumentModel(QUESTION_DOCUMENT_TYPE)).thenReturn(newQuestionDoc);
        when(session.createDocumentModel(REPONSE_DOCUMENT_TYPE)).thenReturn(reponseDoc);
        when(session.saveDocument(newQuestionDossierDoc)).thenReturn(newQuestionDossierDoc);
        DocumentRef newQuestionDocRef = new IdRef("d3338814-d839-46f0-99fe-a8de03ea63cd");
        when(session.exists(newQuestionDocRef)).thenReturn(false);

        when(newQuestionDoc.getAdapter(Question.class)).thenReturn(newQuestion);
        when(newQuestionDoc.getRef()).thenReturn(newQuestionDocRef);

        when(newQuestion.getDocument()).thenReturn(newQuestionDoc);
        when(newQuestion.getNumeroQuestion()).thenReturn((long) newQuestionId.getNumeroQuestion());

        when(reponseDoc.getAdapter(Reponse.class)).thenReturn(reponse);

        when(newQuestionDossierDoc.getAdapter(Dossier.class)).thenReturn(newQuestionDossier);
        when(newQuestionDossier.getDocument()).thenReturn(newQuestionDossierDoc);
        when(newQuestionDossier.getQuestion(session)).thenReturn(newQuestion);

        when(
            dossierDistributionService.isExistingQuestion(
                session,
                NUMERO_QUESTION,
                newQuestionId.getType().toString(),
                newQuestionId.getSource().toString(),
                newQuestionId.getLegislature()
            )
        )
            .thenReturn(false);
        when(dossierDistributionService.initDossier(session, newQuestion.getNumeroQuestion()))
            .thenReturn(newQuestionDossier);
        when(
            dossierDistributionService.createDossier(
                session,
                newQuestionDossier,
                newQuestion,
                reponse,
                ETAT_QUESTION_EN_COURS
            )
        )
            .thenReturn(newQuestionDossier);

        String groupePol = questionToSend.getAuteur().getGrpPol() + " (" + newQuestionId.getSource() + ')';
        when(vocabularyService.hasDirectoryEntry(GROUPE_POLITIQUE, groupePol)).thenReturn(false);
        when(vocabularyService.getNewEntry(GROUPE_POLITIQUE)).thenReturn(newGroupePol);

        EnvoyerQuestionsResponse response = delegate.envoyerQuestions(request);

        assertThat(response.getResultatTraitement().size()).isEqualTo(1);
        ResultatTraitement resultat = response.getResultatTraitement().get(0);
        assertThat(resultat.getStatut()).isEqualTo(OK);
        assertThat(resultat.getMessageErreur()).isNull();
        assertQuestionId(resultat.getIdQuestion(), newQuestionId);

        verify(newQuestion).setNumeroQuestion((long) newQuestionId.getNumeroQuestion());
        verify(newQuestion).setTypeQuestion(newQuestionId.getType().value());
        verify(newQuestion).setOrigineQuestion(newQuestionId.getSource().toString());
        verify(newQuestion).setLegislatureQuestion((long) newQuestionId.getLegislature());
        verify(newQuestion).setTexteJoint(question.getTexteJoint());
        verify(newQuestion).setDateReceptionQuestion(dateReceptionQuestionCaptor.capture());
        assertThat(dateReceptionQuestionCaptor.getValue().toZonedDateTime().toLocalDateTime())
            .isCloseTo(now(), within(10, SECONDS));
        verify(newQuestion).setPageJO(String.valueOf(questionToSend.getPageJo()));
        verify(newQuestion).setDatePublicationJO(questionToSend.getDatePublicationJo().toGregorianCalendar());
        verify(newQuestion).setIdMinistereInterroge(String.valueOf(questionToSend.getMinistreDepot().getId()));
        verify(newQuestion).setTitreJOMinistere(questionToSend.getMinistreDepot().getTitreJo());
        verify(newQuestion).setIntituleMinistere(questionToSend.getMinistreDepot().getIntituleMinistere());
        verify(newQuestion).setCiviliteAuteur(questionToSend.getAuteur().getCivilite().name());
        verify(newQuestion).setNomAuteur(questionToSend.getAuteur().getNom());
        verify(newQuestion).setPrenomAuteur(questionToSend.getAuteur().getPrenom());
        verify(newQuestion).setIdMandat(questionToSend.getAuteur().getIdMandat());
        verify(newQuestion).setCirconscriptionAuteur(questionToSend.getAuteur().getCirconscription());
        verify(newQuestion).setGroupePolitique(groupePol);
        verify(newQuestion).setSenatQuestionTitre(questionToSend.getTitreSenat());
        verify(newQuestion).setSenatQuestionThemes(questionToSend.getIndexationSenat().getTheme());
        verify(newQuestion).setSenatQuestionRubrique(questionToSend.getIndexationSenat().getRubrique());
        verify(newQuestion).setSenatQuestionRenvois(questionToSend.getIndexationSenat().getRenvois().getRenvoi());
        verify(newQuestion).setTexteQuestion(questionToSend.getTexte());
        verify(newQuestion)
            .setEtatQuestion(
                eq(session),
                eq(ETAT_QUESTION_REPONDU),
                any(GregorianCalendar.class),
                eq(DELAI_QUESTION_SIGNALEE_VALUE)
            );

        verify(vocabularyService).createDirectoryEntry(GROUPE_POLITIQUE, newGroupePol);

        verify(newQuestionDossier).setIdMinistereAttributaireCourant(String.valueOf(ID_MINISTRE_DEPOT));

        verify(dossierDistributionService)
            .createDossier(session, newQuestionDossier, newQuestion, reponse, ETAT_QUESTION_EN_COURS);
        verify(dossierDistributionService).startDefaultRoute(session, newQuestionDossier);

        verify(session).saveDocument(newQuestionDossierDoc);
        verify(session).save();
        verify(session).createDocument(newQuestionDoc);
        verify(session).saveDocument(newQuestionDoc);

        verify(paramService).getParametreValue(session, DELAI_QUESTION_SIGNALEE);

        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_ENVOYER_QUESTIONS_EVENT,
                WEBSERVICE_ENVOYER_QUESTIONS_COMMENT
            );
    }

    @Test
    public void chercherChangementDEtatQuestions() {
        ChercherChangementDEtatQuestionsRequest request = new ChercherChangementDEtatQuestionsRequest();
        request.getIdQuestions().add(questionId);

        when(principal.getGroups()).thenReturn(singletonList(CHERCHER_CHANGEMENT_ETAT_QUESTION));

        when(question.getEtatQuestion(session)).thenReturn(questionStateChange);

        when(questionStateChange.getChangeDate()).thenReturn(new GregorianCalendar());
        when(questionStateChange.getNewState()).thenReturn(ETAT_QUESTION_EN_COURS);

        ChercherChangementDEtatQuestionsResponse response = delegate.chercherChangementDEtatQuestions(request);

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.isDernierRenvoi()).isTrue();
        assertThat(response.getJetonChangementsEtat()).isEmpty();
        assertThat(response.getMessageErreur()).isEmpty();
        assertThat(response.getChangementsEtat().size()).isEqualTo(1);
        ChangementEtatQuestion changementEtatQuestion = response.getChangementsEtat().get(0);
        assertQuestionId(changementEtatQuestion.getIdQuestion(), questionId);
        assertThat(changementEtatQuestion.getDateModif().toGregorianCalendar().toZonedDateTime().toLocalDateTime())
            .isCloseTo(now(), within(1, MINUTES));
        assertThat(changementEtatQuestion.getTypeModif()).isEqualTo(EN_COURS);

        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_CHANGEMENT_ETAT_QUESTION_EVENT,
                WEBSERVICE_CHERCHER_CHANGEMENT_ETAT_QUESTION_COMMENT
            );
    }

    @Test
    public void rechercherDossier() throws DatatypeConfigurationException {
        RechercherDossierRequest request = initDataForRechercherDossier();

        when(unrestrictedQueryRunner.findAll()).thenReturn(new DocumentModelListImpl(singletonList(questionDoc)));

        when(questionDossier.getReponse(session)).thenReturn(reponse);

        String texteReponse = "Texte réponse";
        when(reponse.getTexteReponse()).thenReturn(texteReponse);
        GregorianCalendar dateJOreponse = new GregorianCalendar();
        when(reponse.getDateJOreponse()).thenReturn(dateJOreponse);
        long pageJOreponse = 28L;
        when(reponse.getPageJOreponse()).thenReturn(pageJOreponse);

        RechercherDossierResponse response = delegate.rechercherDossier(request);

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getDossier().size()).isEqualTo(1);
        QuestionReponse resultQuestionReponse = response.getDossier().get(0);
        assertThat(resultQuestionReponse.getQuestion().size()).isEqualTo(1);
        fr.sword.xsd.reponses.Question resultQuestion = resultQuestionReponse.getQuestion().get(0);
        assertQuestionId(resultQuestion.getIdQuestion(), questionId);
        assertThat(resultQuestion.getTitreSenat()).isEqualTo(TITRE_SENAT);
        assertThat(resultQuestionReponse.getReponse().getTexteReponse()).isEqualTo(texteReponse);
        assertThat(resultQuestionReponse.getReponse().getDateJo())
            .isEqualTo(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateJOreponse));
        assertThat(resultQuestionReponse.getReponse().getPageJo()).isEqualTo(Long.valueOf(pageJOreponse).intValue());

        verify(journalService)
            .journaliserActionAdministration(
                session,
                question.getDossier(session).getDocument(),
                WEBSERVICE_CHERCHER_DOSSIER_EVENT,
                WEBSERVICE_CHERCHER_DOSSIER_COMMENT
            );
        verify(journalService, never())
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_DOSSIER_EVENT,
                WEBSERVICE_CHERCHER_DOSSIER_COMMENT
            );
    }

    @Test
    public void rechercherDossierWithoutQuestion() throws DatatypeConfigurationException {
        RechercherDossierRequest request = initDataForRechercherDossier();

        when(unrestrictedQueryRunner.findAll()).thenReturn(new DocumentModelListImpl());

        RechercherDossierResponse response = delegate.rechercherDossier(request);

        assertThat(response.getStatut()).isEqualTo(OK);
        assertThat(response.getMessageErreur()).isNull();
        assertThat(response.getDossier()).isEmpty();

        verify(journalService, never())
            .journaliserActionAdministration(
                session,
                question.getDossier(session).getDocument(),
                WEBSERVICE_CHERCHER_DOSSIER_EVENT,
                WEBSERVICE_CHERCHER_DOSSIER_COMMENT
            );
        verify(journalService)
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_DOSSIER_EVENT,
                WEBSERVICE_CHERCHER_DOSSIER_COMMENT
            );
    }

    @Test
    public void rechercherDossierWithSecurityError() {
        RechercherDossierRequest request = new RechercherDossierRequest();

        request.getSource().add(SENAT);

        when(principal.getGroups()).thenReturn(ImmutableList.of(RECHERCHER_DOSSIER, PROFIL_AN));

        doReturn(unrestrictedQueryRunner).when(delegate).createUnrestrictedQueryRunner(any(String.class));

        when(unrestrictedQueryRunner.findAll()).thenReturn(new DocumentModelListImpl(singletonList(questionDoc)));

        RechercherDossierResponse response = delegate.rechercherDossier(request);

        assertThat(response.getStatut()).isEqualTo(KO);
        assertThat(response.getMessageErreur()).isEqualTo("Vous n'avez pas accès à ces questions");

        verify(journalService, never())
            .journaliserActionAdministration(
                session,
                WEBSERVICE_CHERCHER_DOSSIER_EVENT,
                WEBSERVICE_CHERCHER_DOSSIER_COMMENT
            );
        verify(journalService, never())
            .journaliserActionAdministration(
                session,
                question.getDossier(session).getDocument(),
                WEBSERVICE_CHERCHER_DOSSIER_EVENT,
                WEBSERVICE_CHERCHER_DOSSIER_COMMENT
            );
    }

    private RechercherDossierRequest initDataForRechercherDossier() throws DatatypeConfigurationException {
        RechercherDossierRequest request = new RechercherDossierRequest();

        int legislature1 = 11;
        request.getLegislature().add(legislature1);
        int legislature2 = 21;
        request.getLegislature().add(legislature2);

        LocalDate dateDebut = LocalDate.of(2020, MARCH, 8);
        request.setDateDebut(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateDebut.toString()));
        LocalDate dateFin = LocalDate.of(2020, APRIL, 17);
        request.setDateFin(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateFin.toString()));

        when(principal.getGroups()).thenReturn(ImmutableList.of(RECHERCHER_DOSSIER, PROFIL_SENAT));

        String query =
            "SELECT * FROM Question WHERE " +
            "qu:legislatureQuestion IN (" +
            legislature1 +
            ", " +
            legislature2 +
            ")  AND " +
            "qu:origineQuestion = 'SENAT' AND " +
            "qu:etatQuestion IN ('repondu', 'en cours', 'cloture_autre', 'retiree')  AND " +
            "qu:datePublicationJO >= DATE '" +
            dateDebut.format(DateTimeFormatter.ofPattern("yyyy-M-d")) +
            "' AND " +
            "qu:datePublicationJO <= DATE '" +
            dateFin.format(DateTimeFormatter.ofPattern("yyyy-M-d")) +
            "'";

        doReturn(unrestrictedQueryRunner).when(delegate).createUnrestrictedQueryRunner(Mockito.eq(query), anyVararg());
        return request;
    }
}
