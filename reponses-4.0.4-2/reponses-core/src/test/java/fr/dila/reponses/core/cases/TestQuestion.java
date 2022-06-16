package fr.dila.reponses.core.cases;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.QErratum;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.cases.flux.QErratumImpl;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author arolin
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestQuestion {
    private static final Log log = LogFactory.getLog(TestQuestion.class);

    @Inject
    private CoreFeature coreFeature;

    private DocumentRef questionRef = null;

    @Before
    public void setUp() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            if (questionRef == null) {
                DocumentModel docModel = ReponseFeature.createDocument(
                    session,
                    DossierConstants.QUESTION_DOCUMENT_TYPE,
                    "newQuestionTest"
                );
                questionRef = docModel.getRef();
                session.save();
            }
        }
    }

    @Test
    public void testGetDocument() {
        // on verifie que le document est de type Question, possède
        // possède le schéma "Question"
        log.info("begin : test dossier type ");
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierModel = session.getDocument(questionRef);
            Assert.assertNotNull(dossierModel);
            Assert.assertNotNull(dossierModel.getPath());
            Assert.assertEquals(DossierConstants.QUESTION_DOCUMENT_TYPE, dossierModel.getType());
            Assert.assertTrue(dossierModel.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA));
        }
    }

    @Test
    public void testAllQuestionProperties() throws Exception {
        log.info("begin : test all the question schema properties ");

        // init metadata variable
        String origineQuestion = VocabularyConstants.QUESTION_ORIGINE_AN;
        String pageJO = "12";

        String typeQuestion = VocabularyConstants.QUESTION_TYPE_QE;
        Long numeroQuestion = 4L;

        Calendar dateJO = GregorianCalendar.getInstance();
        dateJO.set(2009, 5, 14);
        Calendar dateReception = GregorianCalendar.getInstance();
        dateReception.set(2010, 5, 14);
        Calendar dateRenouvellement = GregorianCalendar.getInstance();
        dateRenouvellement.set(2011, 5, 14);
        Calendar dateRetrait = GregorianCalendar.getInstance();
        dateRetrait.set(2012, 5, 14);
        Calendar dateCaducite = GregorianCalendar.getInstance();
        dateCaducite.set(2013, 6, 15);
        Calendar dateSignalement = GregorianCalendar.getInstance();
        dateSignalement.set(2013, 8, 15);

        List<String> assNatRubrique = new ArrayList<>();
        assNatRubrique.add("anrub1");
        assNatRubrique.add("anrub2");
        List<String> analyseList = new ArrayList<>();
        analyseList.add("patates");
        analyseList.add("choux");
        List<String> assNatTeteAnalyse = new ArrayList<>();
        assNatTeteAnalyse.add("anta1");
        assNatTeteAnalyse.add("anta2");

        String groupePolitique = "UMP";
        String nomAuteur = "Taillerand";
        String prenomAuteur = "Jean-Marc";

        List<String> seThemeList = new ArrayList<>();
        seThemeList.add("Bidonville");
        seThemeList.add("Afrique");
        List<String> senatQuestionRubrique = new ArrayList<>();
        senatQuestionRubrique.add("serub1");
        senatQuestionRubrique.add("serub2");
        List<String> seRenvoisList = new ArrayList<>();
        seRenvoisList.add("renvois1");
        seRenvoisList.add("renvois2");

        String caracteristiquesQuestion = "sansReponse";
        String texteQuestion =
            "C\\’est un saxophoniste important dans" +
            " l'histoire du jazz, en particulier par la création d’une esthétique originale " +
            "et très personnelle, privilégiant la mélodie et la sensibilité. Garbarek possède" +
            " une identité musicale extrêmement reconnaissable, se démarquant ";

        List<String> motsClefsMinistereList = new ArrayList<>();
        motsClefsMinistereList.add("m1");
        motsClefsMinistereList.add("m2");

        /* indexation complementaire */
        List<String> idxComplMotsClefsMinistereList = new ArrayList<>();
        idxComplMotsClefsMinistereList.add("idxcmpm1");
        idxComplMotsClefsMinistereList.add("idxcmpm2");

        /* indexation complementaire AN */
        List<String> idxComplAssNatRubrique = new ArrayList<>();
        idxComplAssNatRubrique.add("idxcmpanrub1");
        idxComplAssNatRubrique.add("idxcmpanrub2");
        List<String> idxComplAnalyseList = new ArrayList<>();
        idxComplAnalyseList.add("idxcmppatates");
        idxComplAnalyseList.add("idxcmpchoux");
        List<String> idxComplAssNatTeteAnalyse = new ArrayList<>();
        idxComplAssNatTeteAnalyse.add("idxcmpanta1");
        idxComplAssNatTeteAnalyse.add("idxcmpanta2");

        /* indexation complementaire SENAT */
        List<String> idxComplSeThemeList = new ArrayList<>();
        idxComplSeThemeList.add("idxcmpBidonville");
        idxComplSeThemeList.add("idxcmpAfrique");
        List<String> idxComplSenatQuestionRubrique = new ArrayList<>();
        idxComplSenatQuestionRubrique.add("idxcmpserub1");
        idxComplSenatQuestionRubrique.add("idxcmpserub2");
        List<String> idxComplSeRenvoisList = new ArrayList<>();
        idxComplSeRenvoisList.add("idxcmprenvois1");
        idxComplSeRenvoisList.add("idxcmprenvois2");

        QErratum er1 = new QErratumImpl();
        er1.setPageJo(new Integer(12));
        er1.setTexteConsolide("TexteConsolide");
        GregorianCalendar dc = new GregorianCalendar();
        er1.setDatePublication(dc);
        er1.setTexteErratum("TexteErratum");

        QErratum er2 = new QErratumImpl();
        er2.setPageJo(new Integer(12));
        er2.setTexteConsolide("TexteConsolide2");
        er2.setDatePublication(dc);
        er2.setTexteErratum("TexteErratum2");

        List<QErratum> erratumList = new ArrayList<>();
        erratumList.add(er1);
        erratumList.add(er2);

        String state1 = VocabularyConstants.ETAT_QUESTION_EN_COURS;
        String state2 = VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE;
        String state3 = VocabularyConstants.ETAT_QUESTION_RETIREE;

        String delaiQuestionSignale = "10";

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = session.getDocument(questionRef).getAdapter(Question.class);

            // set metadata
            question.setNumeroQuestion(numeroQuestion);
            question.setOrigineQuestion(origineQuestion);
            question.setTypeQuestion(typeQuestion);
            question.setPageJO(pageJO);

            question.setDatePublicationJO(dateJO);
            question.setDateReceptionQuestion(dateReception);

            question.setAssNatRubrique(assNatRubrique);
            question.setAssNatAnalyses(analyseList);
            question.setAssNatTeteAnalyse(assNatTeteAnalyse);

            question.setNomAuteur(nomAuteur);
            question.setPrenomAuteur(prenomAuteur);
            question.setGroupePolitique(groupePolitique);

            question.setSenatQuestionThemes(seThemeList);
            question.setSenatQuestionRubrique(senatQuestionRubrique);
            question.setSenatQuestionRenvois(seRenvoisList);

            question.setTexteQuestion(texteQuestion);

            question.setMotsClefMinistere(motsClefsMinistereList);
            question.setCaracteristiquesQuestion(caracteristiquesQuestion);
            question.setErrata(erratumList);

            question.setIndexationComplAssNatAnalyses(idxComplAnalyseList);
            question.setIndexationComplAssNatRubrique(idxComplAssNatRubrique);
            question.setIndexationComplAssNatTeteAnalyse(idxComplAssNatTeteAnalyse);
            question.setIndexationComplMotsClefMinistere(idxComplMotsClefsMinistereList);
            question.setIndexationComplSenatQuestionRenvois(idxComplSeRenvoisList);
            question.setIndexationComplSenatQuestionRubrique(idxComplSenatQuestionRubrique);
            question.setIndexationComplSenatQuestionThemes(idxComplSeThemeList);

            session.saveDocument(question.getDocument());
            session.save();
        }

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel questionModel = session.getDocument(questionRef);
            Question question = questionModel.getAdapter(Question.class);
            Calendar today = Calendar.getInstance();

            question.setEtatQuestion(session, state1, today, delaiQuestionSignale);
            question.setEtatQuestion(session, state2, today, delaiQuestionSignale);
            question.setEtatQuestion(session, state3, today, delaiQuestionSignale);

            // check properties persistance
            Assert.assertEquals(origineQuestion, question.getOrigineQuestion());
            Assert.assertEquals(numeroQuestion, question.getNumeroQuestion());
            Assert.assertEquals(typeQuestion, question.getTypeQuestion());
            Assert.assertEquals(pageJO, question.getPageJO());

            Assert.assertEquals(dateJO, question.getDatePublicationJO());
            Assert.assertEquals(dateReception, question.getDateReceptionQuestion());

            Assert.assertEquals(question.getAssNatRubrique(), assNatRubrique);
            Assert.assertEquals(question.getAssNatAnalyses(), analyseList);
            Assert.assertEquals(question.getAssNatTeteAnalyse(), assNatTeteAnalyse);

            Assert.assertEquals(nomAuteur, question.getNomAuteur());
            Assert.assertEquals(prenomAuteur, question.getPrenomAuteur());
            Assert.assertEquals(nomAuteur + " " + prenomAuteur, question.getNomCompletAuteur());
            Assert.assertEquals(groupePolitique, question.getGroupePolitique());

            Assert.assertEquals(question.getSenatQuestionThemes(), seThemeList);
            Assert.assertEquals(question.getSenatQuestionRubrique(), senatQuestionRubrique);
            Assert.assertEquals(question.getSenatQuestionRenvois(), seRenvoisList);

            Assert.assertEquals(question.getTexteQuestion(), texteQuestion);
            Assert.assertEquals(question.getCaracteristiquesQuestion(), caracteristiquesQuestion);
            Assert.assertEquals(question.getMotsClefMinistere(), motsClefsMinistereList);

            Assert.assertEquals(2, question.getErrata().size());
            List<QuestionStateChange> lstEtatsQuestion = question.getEtatQuestionHistorique(session);
            Assert.assertEquals(2, lstEtatsQuestion.size());
            Assert.assertEquals(state1, lstEtatsQuestion.get(0).getNewState());
            Assert.assertEquals(today, lstEtatsQuestion.get(0).getChangeDate());
            Assert.assertEquals(state3, lstEtatsQuestion.get(1).getNewState());
            Assert.assertEquals(today, lstEtatsQuestion.get(1).getChangeDate());

            Assert.assertEquals(state3, question.getEtatQuestionSimple());
            Assert.assertEquals(today, question.getDateRetraitQuestion());

            Assert.assertEquals(idxComplAnalyseList, question.getIndexationComplAssNatAnalyses());
            Assert.assertEquals(idxComplAssNatRubrique, question.getIndexationComplAssNatRubrique());
            Assert.assertEquals(idxComplAssNatTeteAnalyse, question.getIndexationComplAssNatTeteAnalyse());
            Assert.assertEquals(idxComplMotsClefsMinistereList, question.getIndexationComplMotsClefMinistere());
            Assert.assertEquals(idxComplSeRenvoisList, question.getIndexationComplSenatQuestionRenvois());
            Assert.assertEquals(idxComplSenatQuestionRubrique, question.getIndexationComplSenatQuestionRubrique());
            Assert.assertEquals(idxComplSeThemeList, question.getIndexationComplSenatQuestionThemes());

            Assert.assertEquals(Boolean.TRUE, question.isQuestionTypeEcrite());
            Assert.assertEquals(Boolean.TRUE, question.hasOrigineAN());
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel questionModel = session.getDocument(questionRef);
            Question question = questionModel.getAdapter(Question.class);

            Assert.assertNull(question.getDateCaducite());
            Assert.assertNull(question.getDateRenouvellementQuestion());

            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_CADUQUE,
                dateCaducite,
                delaiQuestionSignale
            );
            session.saveDocument(question.getDocument());
            session.save();
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel questionModel = session.getDocument(questionRef);
            Question question = questionModel.getAdapter(Question.class);

            Assert.assertEquals(dateCaducite, question.getDateCaducite());

            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_RENOUVELEE,
                dateRenouvellement,
                delaiQuestionSignale
            );

            session.saveDocument(question.getDocument());
            session.save();
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel questionModel = session.getDocument(questionRef);
            Question question = questionModel.getAdapter(Question.class);

            Assert.assertEquals(dateRenouvellement, question.getDateRenouvellementQuestion());

            Assert.assertFalse(question.getEtatRappele());

            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_RAPPELE,
                Calendar.getInstance(),
                delaiQuestionSignale
            );

            session.saveDocument(question.getDocument());
            session.save();
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel questionModel = session.getDocument(questionRef);
            Question question = questionModel.getAdapter(Question.class);

            Assert.assertTrue(question.getEtatRappele());

            Assert.assertFalse(question.getEtatSignale());

            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_SIGNALEE,
                dateSignalement,
                delaiQuestionSignale
            );

            session.saveDocument(question.getDocument());
            session.save();
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel questionModel = session.getDocument(questionRef);
            Question question = questionModel.getAdapter(Question.class);

            Assert.assertTrue(question.getEtatSignale());
            Assert.assertFalse(VocabularyConstants.ETAT_QUESTION_SIGNALEE.equals(question.getEtatQuestionSimple()));

            List<Signalement> signalements = question.getSignalements();
            Assert.assertFalse(signalements.isEmpty());
            Signalement signalement = signalements.get(0);
            Assert.assertEquals(dateSignalement.getTime(), signalement.getDateEffet());
            dateSignalement.add(Calendar.DAY_OF_YEAR, 10);
            Assert.assertEquals(dateSignalement.getTime(), signalement.getDateAttendue());
        }
    }

    /**
     * test le positionnement de la valeur de l'etat courant de la question suite aux appels a setEtatQuestion Celui-ci
     * ne doit etre modifie que pour certaines valeur d'état
     *
     *
     */
    @Test
    public void testEtatQuestionCourant() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String etatModificateurs[] = new String[] {
                VocabularyConstants.ETAT_QUESTION_RETIREE,
                VocabularyConstants.ETAT_QUESTION_CADUQUE,
                VocabularyConstants.ETAT_QUESTION_EN_COURS,
                VocabularyConstants.ETAT_QUESTION_REPONDU,
                VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE
            };

            String etatNonModificateurs[] = new String[] {
                VocabularyConstants.ETAT_QUESTION_SIGNALEE,
                VocabularyConstants.ETAT_QUESTION_RENOUVELEE,
                VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE,
                VocabularyConstants.ETAT_QUESTION_RAPPELE
            };

            for (String etat : etatModificateurs) {
                Assert.assertTrue(QuestionImpl.canBeCurrentStateOfQuestion(etat));
            }

            for (String etat : etatNonModificateurs) {
                Assert.assertFalse(QuestionImpl.canBeCurrentStateOfQuestion(etat));
            }

            GregorianCalendar today = new GregorianCalendar(Locale.FRANCE);
            String delaiQuestionSignale = "10";
            // test la modif de l'etat courant
            Question question = session.getDocument(questionRef).getAdapter(Question.class);
            for (String etat : etatModificateurs) {
                question.setEtatQuestion(session, etat, today, delaiQuestionSignale);
                Assert.assertEquals("Unexpected State", etat, question.getEtatQuestionSimple());
            }

            // test la non modif de l'etat courant
            String lastEtat = question.getEtatQuestionSimple();
            for (String etat : etatNonModificateurs) {
                question.setEtatQuestion(session, etat, today, delaiQuestionSignale);
                Assert.assertEquals("Unexpected State", lastEtat, question.getEtatQuestionSimple());
            }
        }
    }

    /**
     * test le positionnement de l'historique suite aux appels a setEtatQuestion Celui-ci ne doit etre modifie que pour
     * certaines valeur d'état
     *
     *
     */
    @Test
    public void testEtatQuestionHistorique() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Calendar today = GregorianCalendar.getInstance(Locale.FRANCE);

            String etatModificateurs[] = new String[] {
                VocabularyConstants.ETAT_QUESTION_RETIREE,
                VocabularyConstants.ETAT_QUESTION_CADUQUE,
                VocabularyConstants.ETAT_QUESTION_EN_COURS,
                VocabularyConstants.ETAT_QUESTION_REPONDU,
                VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE,
                VocabularyConstants.ETAT_QUESTION_SIGNALEE,
                VocabularyConstants.ETAT_QUESTION_RENOUVELEE
            };

            String etatNonModificateurs[] = new String[] {
                VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE,
                VocabularyConstants.ETAT_QUESTION_RAPPELE
            };

            for (String etat : etatModificateurs) {
                Assert.assertTrue("Expected true for " + etat, QuestionImpl.shouldBeListedInHistoric(etat));
            }

            for (String etat : etatNonModificateurs) {
                Assert.assertFalse("Expected false for " + etat, QuestionImpl.shouldBeListedInHistoric(etat));
            }

            Question question = session.getDocument(questionRef).getAdapter(Question.class);
            String delaiQuestionSignale = "10";
            int prevSize = question.getEtatQuestionHistorique(session).size();
            for (String etat : etatModificateurs) {
                question.setEtatQuestion(session, etat, today, delaiQuestionSignale);
                session.saveDocument(question.getDocument());
                session.save();
                List<String> qscList = listEtatsQuestions(question.getEtatQuestionHistorique(session));
                Assert.assertEquals(prevSize + 1, qscList.size());
                Assert.assertTrue(qscList.contains(etat));
                prevSize++;
            }

            prevSize = question.getEtatQuestionHistorique(session).size();
            String prevEtat = question.getEtatQuestion(session).getNewState();
            for (String etat : etatNonModificateurs) {
                question.setEtatQuestion(session, etat, today, delaiQuestionSignale);
                List<String> qscList = listEtatsQuestions(question.getEtatQuestionHistorique(session));
                Assert.assertEquals(prevSize, qscList.size());
                Assert.assertTrue(qscList.contains(prevEtat));
            }
        }
    }

    private List<String> listEtatsQuestions(List<QuestionStateChange> qscList) {
        ArrayList<String> stringList = new ArrayList<>();
        for (QuestionStateChange qsc : qscList) {
            stringList.add(qsc.getNewState());
        }
        return stringList;
    }
}
