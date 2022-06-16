package fr.dila.reponses.core.service;

import fr.dila.cm.cases.CaseDistribConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.ss.api.constant.SSConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Teste la selection du modele de feuille de route pour un dossier
 *
 * @author spesnel
 *
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestFeuilleRouteService {
    private static final Log log = LogFactory.getLog(TestFeuilleRouteService.class);

    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";
    private static final String DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO = "DefaultRouteModelForEco";

    private static final String ID_MINISTERE_ECO = "50000507";
    private static final String SENAT_QUESTION_TITRE = "un titre pour le senat et pas l'assemblée";

    private static final String SENAT_RUB_1 = "senat rub'1";
    private static final String SENAT_RUB_2 = "senat rub'2";
    private static final String SENAT_THEME_1 = "senat theme'1";
    private static final String SENAT_THEME_2 = "senat theme'2";
    private static final String SENAT_THEME_3 = "senat theme'3";
    private static final String AN_RUB_1 = "an'rubrique";
    private static final String AN_RUB_2 = "an'rubrique 2";
    private static final String AN_RUB_3 = "an'rubrique 3";
    private static final String AN_TA_1 = "tête d'analyse";
    private static final String AN_TA_2 = "tête d'analyse 2";
    private static final String AN_ana_1 = "analyse 1";
    private static final String AN_ana_2 = "analyse 2";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private FeuilleRouteModelService feuilleRouteModelService;

    private DocumentRef defaultRouteRef;
    private DocumentRef defaultRouteForEcoRef;
    private DocumentRef routeSenatTitreRef;
    private DocumentRef routeSenatThemeRef;
    private DocumentRef routeSenatRubriqueRef;
    private DocumentRef routeANRubriqueRef;
    private DocumentRef routeANRubriqueTeteRef;
    private DocumentRef routeANRubriqueTeteAnalyseRef;

    private int nbModelCreated;

    private String delaiQuestionSignalee = "10";

    @Before
    public void setUp() {
        reponseFeature.setFeuilleRootModelFolderId(null);
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            reponseFeature.createOrGetFeuilleRouteModelFolder(session);

            FeuilleRoute route = createDefaultRoute(
                session,
                DEFAULT_ROUTE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );
            defaultRouteRef = route.getDocument().getRef();
            route =
                createDefaultRoute(
                    session,
                    DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO,
                    ID_MINISTERE_ECO,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                );
            defaultRouteForEcoRef = route.getDocument().getRef();

            route =
                createDefaultRoute(
                    session,
                    DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO,
                    ID_MINISTERE_ECO,
                    null,
                    null,
                    null,
                    null,
                    null,
                    SENAT_QUESTION_TITRE
                );
            routeSenatTitreRef = route.getDocument().getRef();

            List<String> senatThemes = new ArrayList<>();
            senatThemes.add(SENAT_THEME_1);
            senatThemes.add(SENAT_THEME_2);
            route =
                createDefaultRoute(
                    session,
                    DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO,
                    ID_MINISTERE_ECO,
                    null,
                    null,
                    null,
                    null,
                    senatThemes,
                    null
                );
            routeSenatThemeRef = route.getDocument().getRef();

            List<String> senatThemesbis = new ArrayList<>();
            senatThemes.add(SENAT_THEME_3);
            route =
                createDefaultRoute(
                    session,
                    DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO,
                    ID_MINISTERE_ECO,
                    null,
                    null,
                    null,
                    null,
                    senatThemesbis,
                    null
                );
            /* routeSenatTheme2Ref = */route.getDocument().getRef();

            route =
                createDefaultRoute(
                    session,
                    DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO,
                    ID_MINISTERE_ECO,
                    null,
                    null,
                    null,
                    null,
                    senatThemesbis,
                    null
                );
            /* routeSenatTheme2CopieRef = */route.getDocument().getRef();

            List<String> senatRubriques = new ArrayList<>();
            senatRubriques.add(SENAT_RUB_1);
            senatRubriques.add(SENAT_RUB_2);
            route =
                createDefaultRoute(
                    session,
                    DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO,
                    ID_MINISTERE_ECO,
                    null,
                    null,
                    null,
                    senatRubriques,
                    null,
                    null
                );
            routeSenatRubriqueRef = route.getDocument().getRef();

            List<String> anRubriques = new ArrayList<>();
            anRubriques.add(AN_RUB_1);

            List<String> anTete = new ArrayList<>();
            anTete.add(AN_TA_1);

            List<String> analyses = new ArrayList<>();
            analyses.add(AN_ana_1);
            analyses.add(AN_ana_2);
            route =
                createDefaultRoute(
                    session,
                    DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO,
                    ID_MINISTERE_ECO,
                    anRubriques,
                    analyses,
                    anTete,
                    null,
                    null,
                    null
                );
            routeANRubriqueTeteAnalyseRef = route.getDocument().getRef();

            anRubriques.clear();
            anRubriques.add(AN_RUB_2);
            anTete.clear();
            anTete.add(AN_TA_2);
            route =
                createDefaultRoute(
                    session,
                    DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO,
                    ID_MINISTERE_ECO,
                    anRubriques,
                    null,
                    anTete,
                    null,
                    null,
                    null
                );
            routeANRubriqueTeteRef = route.getDocument().getRef();

            anRubriques.clear();
            anRubriques.add(AN_RUB_3);
            route =
                createDefaultRoute(
                    session,
                    DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO,
                    ID_MINISTERE_ECO,
                    anRubriques,
                    null,
                    null,
                    null,
                    null,
                    null
                );
            routeANRubriqueRef = route.getDocument().getRef();

            nbModelCreated = 10;

            session.save();
        }
    }

    @Test
    public void testGetService() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String id = feuilleRouteModelService.getFeuilleRouteModelFolderId(session);
            Assert.assertEquals(reponseFeature.getFeuilleRootModelFolderId(), id);

            DocumentModel dm = feuilleRouteModelService.getFeuilleRouteModelFolder(session);
            Assert.assertNotNull(dm);
            Assert.assertEquals(id, dm.getId());

            String query = "SELECT * FROM FeuilleRoute WHERE ecm:parentId = '" + id + "'";
            DocumentModelList dml = session.query(query);
            Assert.assertEquals(nbModelCreated, dml.size());
        }
    }

    @Test
    public void testSelectForNotQE() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = createQuestion(session);
            Dossier dossier = createDossier(session, question);

            Assert.assertNull(feuilleRouteModelService.selectRouteForDossier(session, dossier));
        }
    }

    @Test
    public void testSelectForWithoutState() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = createQuestion(session);
            question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
            question.setOrigineQuestion("AN");

            session.saveDocument(question.getDocument());
            Dossier dossier = createDossier(session, question);

            Assert.assertNull(feuilleRouteModelService.selectRouteForDossier(session, dossier));
        }
    }

    @Test
    public void testSelectForANWithoutIdMinistere() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = createQuestion(session);
            question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_EN_COURS,
                new GregorianCalendar(),
                delaiQuestionSignalee
            );
            question.setOrigineQuestion("AN");

            session.saveDocument(question.getDocument());
            Dossier dossier = createDossier(session, question);

            FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
            Assert.assertNotNull(route);
            log.debug(route);
            Assert.assertEquals(defaultRouteRef, route.getDocument().getRef());
        }
    }

    @Test
    public void testSelectForAN() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = createQuestion(session);
            question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_EN_COURS,
                new GregorianCalendar(),
                delaiQuestionSignalee
            );
            question.setOrigineQuestion("AN");
            question.setIdMinistereInterroge(ID_MINISTERE_ECO);

            session.saveDocument(question.getDocument());
            Dossier dossier = createDossier(session, question);

            FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
            Assert.assertNotNull(route);
            Assert.assertEquals(defaultRouteForEcoRef, route.getDocument().getRef());
        }
    }

    @Test
    public void testSelectForANWithRubrique() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = createQuestion(session);

            question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_EN_COURS,
                new GregorianCalendar(),
                delaiQuestionSignalee
            );
            question.setOrigineQuestion("AN");
            question.setIdMinistereInterroge(ID_MINISTERE_ECO);
            List<String> rubriqueAssNat = new ArrayList<>();
            rubriqueAssNat.add(AN_RUB_3);
            List<String> taAssNat = new ArrayList<>();
            taAssNat.add("une ta");
            question.setAssNatRubrique(rubriqueAssNat);
            question.setAssNatTeteAnalyse(taAssNat);

            session.saveDocument(question.getDocument());
            Dossier dossier = createDossier(session, question);

            FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
            Assert.assertNotNull(route);
            Assert.assertEquals(routeANRubriqueRef, route.getDocument().getRef());
        }
    }

    @Test
    public void testSelectForANWithRubriqueTA() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            {
                Question question = createQuestion(session);
                question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
                question.setEtatQuestion(
                    session,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS,
                    new GregorianCalendar(),
                    delaiQuestionSignalee
                );
                question.setOrigineQuestion("AN");
                question.setIdMinistereInterroge(ID_MINISTERE_ECO);
                List<String> rubriqueAssNat = new ArrayList<>();
                rubriqueAssNat.add(AN_RUB_2);
                List<String> taAssNat = new ArrayList<>();
                taAssNat.add(AN_TA_2);
                question.setAssNatRubrique(rubriqueAssNat);
                question.setAssNatTeteAnalyse(taAssNat);

                session.saveDocument(question.getDocument());
                Dossier dossier = createDossier(session, question);

                FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
                Assert.assertNotNull(route);
                Assert.assertEquals(routeANRubriqueTeteRef, route.getDocument().getRef());
            }

            {
                Question question = createQuestion(session);
                question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
                question.setEtatQuestion(
                    session,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS,
                    new GregorianCalendar(),
                    delaiQuestionSignalee
                );
                question.setOrigineQuestion("AN");
                question.setIdMinistereInterroge(ID_MINISTERE_ECO);
                List<String> rubriqueAssNat = new ArrayList<>();
                rubriqueAssNat.add(AN_RUB_2);
                List<String> taAssNat = new ArrayList<>();
                taAssNat.add(AN_TA_2);
                List<String> anaAssNat = new ArrayList<>();
                anaAssNat.add("une analyse qcq");
                question.setAssNatRubrique(rubriqueAssNat);
                question.setAssNatTeteAnalyse(taAssNat);
                question.setAssNatAnalyses(anaAssNat);

                session.saveDocument(question.getDocument());
                Dossier dossier = createDossier(session, question);

                FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
                Assert.assertNotNull(route);
                Assert.assertEquals(routeANRubriqueTeteRef, route.getDocument().getRef());
            }
        }
    }

    @Test
    public void testSelectForANWithRubriqueTAAnalyse() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = createQuestion(session);
            question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_EN_COURS,
                new GregorianCalendar(),
                delaiQuestionSignalee
            );
            question.setOrigineQuestion("AN");
            question.setIdMinistereInterroge(ID_MINISTERE_ECO);
            List<String> rubriqueAssNat = new ArrayList<>();
            rubriqueAssNat.add(AN_RUB_1);
            List<String> taAssNat = new ArrayList<>();
            taAssNat.add(AN_TA_1);
            List<String> anaAssNat = new ArrayList<>();
            anaAssNat.add(AN_ana_1);
            anaAssNat.add(AN_ana_2);
            question.setAssNatRubrique(rubriqueAssNat);
            question.setAssNatTeteAnalyse(taAssNat);
            question.setAssNatAnalyses(anaAssNat);

            session.saveDocument(question.getDocument());
            Dossier dossier = createDossier(session, question);

            FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
            Assert.assertNotNull(route);
            Assert.assertEquals(routeANRubriqueTeteAnalyseRef, route.getDocument().getRef());
        }
    }

    @Test
    public void testSelectForSenatWithoutIdMinistere() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = createQuestion(session);
            question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_EN_COURS,
                new GregorianCalendar(),
                delaiQuestionSignalee
            );
            question.setOrigineQuestion("SENAT");

            session.saveDocument(question.getDocument());
            Dossier dossier = createDossier(session, question);

            FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
            Assert.assertNotNull(route);
            Assert.assertEquals(defaultRouteRef, route.getDocument().getRef());
        }
    }

    @Test
    public void testSelectForSenat() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = createQuestion(session);
            question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_EN_COURS,
                new GregorianCalendar(),
                delaiQuestionSignalee
            );
            question.setOrigineQuestion("SENAT");
            question.setIdMinistereInterroge(ID_MINISTERE_ECO);

            session.saveDocument(question.getDocument());
            Dossier dossier = createDossier(session, question);

            FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
            Assert.assertNotNull(route);
            Assert.assertEquals(defaultRouteForEcoRef, route.getDocument().getRef());
        }
    }

    @Test
    public void testSelectForSenatWithTitre() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Question question = createQuestion(session);
            question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
            question.setEtatQuestion(
                session,
                VocabularyConstants.ETAT_QUESTION_EN_COURS,
                new GregorianCalendar(),
                delaiQuestionSignalee
            );
            question.setOrigineQuestion("SENAT");
            question.setIdMinistereInterroge(ID_MINISTERE_ECO);
            question.setSenatQuestionTitre(SENAT_QUESTION_TITRE);

            session.saveDocument(question.getDocument());
            Dossier dossier = createDossier(session, question);

            FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
            Assert.assertNotNull(route);
            Assert.assertEquals(routeSenatTitreRef, route.getDocument().getRef());
        }
    }

    @Test
    public void testSelectForSenatWithTheme() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            {
                Question question = createQuestion(session);
                question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
                question.setEtatQuestion(
                    session,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS,
                    new GregorianCalendar(),
                    delaiQuestionSignalee
                );
                question.setOrigineQuestion("SENAT");
                question.setIdMinistereInterroge(ID_MINISTERE_ECO);
                List<String> senatThemes = new ArrayList<>();
                senatThemes.add(SENAT_THEME_2);
                senatThemes.add(SENAT_THEME_1);
                question.setSenatQuestionThemes(senatThemes);
                List<String> senatRubriques = new ArrayList<>();
                senatRubriques.add(SENAT_RUB_1);
                senatRubriques.add(SENAT_RUB_2);
                question.setSenatQuestionRubrique(senatRubriques);

                session.saveDocument(question.getDocument());
                Dossier dossier = createDossier(session, question);

                FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
                Assert.assertNotNull(route);
                Assert.assertEquals(routeSenatThemeRef, route.getDocument().getRef());
            }
        }
    }

    @Test
    public void testSelectForSenatWithRubrique() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            {
                Question question = createQuestion(session);
                question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
                question.setEtatQuestion(
                    session,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS,
                    new GregorianCalendar(),
                    delaiQuestionSignalee
                );
                question.setOrigineQuestion("SENAT");
                question.setIdMinistereInterroge(ID_MINISTERE_ECO);
                List<String> senatThemes = new ArrayList<>();
                senatThemes.add(SENAT_THEME_1);
                senatThemes.add(SENAT_THEME_2);
                senatThemes.add("un theme en plus non connu");
                question.setSenatQuestionThemes(senatThemes);
                List<String> senatRubriques = new ArrayList<>();
                senatRubriques.add(SENAT_RUB_1);
                senatRubriques.add(SENAT_RUB_2);
                question.setSenatQuestionRubrique(senatRubriques);

                session.saveDocument(question.getDocument());
                Dossier dossier = createDossier(session, question);

                FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
                Assert.assertNotNull(route);
                Assert.assertEquals(routeSenatRubriqueRef, route.getDocument().getRef());
            }

            {
                Question question = createQuestion(session);
                question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
                question.setEtatQuestion(
                    session,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS,
                    new GregorianCalendar(),
                    delaiQuestionSignalee
                );
                question.setOrigineQuestion("SENAT");
                question.setIdMinistereInterroge(ID_MINISTERE_ECO);
                List<String> senatThemes = new ArrayList<>();
                senatThemes.add(SENAT_THEME_3); // plsuieurs feuille pour ce theme donc doit se rabattre sur la rubrique
                question.setSenatQuestionThemes(senatThemes);
                List<String> senatRubriques = new ArrayList<>();
                senatRubriques.add(SENAT_RUB_1);
                senatRubriques.add(SENAT_RUB_2);
                question.setSenatQuestionRubrique(senatRubriques);

                session.saveDocument(question.getDocument());
                Dossier dossier = createDossier(session, question);

                FeuilleRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
                Assert.assertNotNull(route);
                Assert.assertEquals(routeSenatRubriqueRef, route.getDocument().getRef());
            }
        }
    }

    /**
     * Retourne la route par défaut (crée ses étapes par effet de bord).
     *
     * @param session
     *            Session
     * @return Route par défaut
     * @throws Exception
     */
    private FeuilleRoute createDefaultRoute(
        CoreSession session,
        String name,
        String idMinistere,
        List<String> rubriqueAssNat,
        List<String> analyseAssNat,
        List<String> teteAnalyseAssNat,
        List<String> rubriqueSenat,
        List<String> themeSenat,
        String titreSenat
    ) {
        DocumentModel root = reponseFeature.createOrGetFeuilleRouteModelFolder(session);
        String rootpath = root.getPathAsString();

        DocumentModel route = reponseFeature.createDocument(
            session,
            name,
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE,
            rootpath
        );
        ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
        Assert.assertNotNull(feuilleRoute);

        if (idMinistere != null) {
            feuilleRoute.setMinistere(idMinistere);
        }

        if (rubriqueAssNat != null) {
            feuilleRoute
                .getDocument()
                .setProperty(
                    DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
                    DossierConstants.DOSSIER_ASSEMBLE_NATIONALE_RUBRIQUE_QUESTION,
                    rubriqueAssNat
                );
        }
        if (analyseAssNat != null) {
            feuilleRoute
                .getDocument()
                .setProperty(
                    DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
                    DossierConstants.DOSSIER_ASSEMBLE_NATIONALE_ANALYSES_QUESTION,
                    analyseAssNat
                );
        }
        if (teteAnalyseAssNat != null) {
            feuilleRoute
                .getDocument()
                .setProperty(
                    DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
                    DossierConstants.DOSSIER_ASSEMBLE_NATIONALE_TETE_ANALYSE_QUESTION,
                    teteAnalyseAssNat
                );
        }

        if (rubriqueSenat != null) {
            feuilleRoute
                .getDocument()
                .setProperty(
                    DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
                    DossierConstants.DOSSIER_SENAT_RUBRIQUE_QUESTION,
                    rubriqueSenat
                );
        }
        if (themeSenat != null) {
            feuilleRoute
                .getDocument()
                .setProperty(
                    DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
                    DossierConstants.DOSSIER_SENAT_THEMES_QUESTION,
                    themeSenat
                );
        }

        if (titreSenat != null) {
            feuilleRoute.setTitreQuestion(titreSenat);
        }

        feuilleRoute.setFeuilleRouteDefaut(true);
        session.saveDocument(feuilleRoute.getDocument());
        feuilleRoute.validate(session);
        session.save();

        List<String> stepNameList = new ArrayList<>();

        // Etape 1
        DocumentModel step1 = reponseFeature.createDocument(
            session,
            "Tâche de distribution user1",
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
            route.getPathAsString()
        );
        Mailbox user1Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
        step1.setPropertyValue(CaseDistribConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user1Mailbox.getId());
        session.saveDocument(step1);
        stepNameList.add(step1.getName());

        // Etape 2
        DocumentModel step2 = reponseFeature.createDocument(
            session,
            "Tâche de validation user2",
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
            route.getPathAsString()
        );
        Mailbox user2Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user2);
        step2.setPropertyValue(CaseDistribConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
        session.saveDocument(step2);
        stepNameList.add(step2.getName());

        session.save();

        return route.getAdapter(FeuilleRoute.class);
    }

    private Dossier createDossier(CoreSession session, Question q) throws Exception {
        DocumentModel docModel = ReponseFeature.createDocument(
            session,
            DossierConstants.DOSSIER_DOCUMENT_TYPE,
            "newDossierTest"
        );
        Dossier dossier = docModel.getAdapter(Dossier.class);
        Assert.assertNotNull(dossier);

        dossier.setQuestionId(q.getDocument().getId());

        dossier.setIdMinistereAttributaireCourant(q.getIdMinistereInterroge());

        dossier.save(session);
        session.save();
        return dossier;
    }

    private Question createQuestion(CoreSession session) throws Exception {
        DocumentModel questionDM = ReponseFeature.createDocument(
            session,
            DossierConstants.QUESTION_DOCUMENT_TYPE,
            "question1"
        );
        return questionDM.getAdapter(Question.class);
    }
}
