package fr.dila.reponses.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import avro.shaded.com.google.common.collect.Lists;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.PlanClassementService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.service.STParametreService;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestPlanClassementService {
    @Inject
    private PlanClassementService planService;

    @Inject
    private CoreSession session;

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private DossierDistributionService dossierDistributionService;

    @Before
    public void setUp() {
        ServiceUtil.getRequiredService(STParametreService.class).clearCache();
        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.newMockSSPrincipal())) {
            DocumentModel questionDocumentModel = ReponseFeature.createDocument(
                session,
                DossierConstants.QUESTION_DOCUMENT_TYPE,
                "newQuestionTest"
            );
            Question question = questionDocumentModel.getAdapter(Question.class);

            question.setAssNatRubrique(Lists.newArrayList("administration"));

            question.setAssNatTeteAnalyse(Lists.newArrayList("activités"));

            // Crée un dossier avec un fond de dossier et les 3 répertoires de niveau 1
            DocumentModel docModel = ReponseFeature.createDocument(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                "newDossierTest"
            );
            Dossier dossier = docModel.getAdapter(Dossier.class);
            dossier =
                dossierDistributionService.createDossier(
                    session,
                    dossier,
                    question,
                    null,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS
                );

            DocumentModel questionDocumentModel2 = ReponseFeature.createDocument(
                session,
                DossierConstants.QUESTION_DOCUMENT_TYPE,
                "newQuestionTest2"
            );
            Question question2 = questionDocumentModel2.getAdapter(Question.class);

            question2.setSenatQuestionThemes(Lists.newArrayList("Affaires étrangères et coopération"));

            question2.setSenatQuestionRubrique(Lists.newArrayList("Cadastre"));

            // Crée un dossier avec un fond de dossier et les 3 répertoires de niveau 1
            DocumentModel docModel2 = ReponseFeature.createDocument(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                "newDossierTest2"
            );
            Dossier dossier2 = docModel2.getAdapter(Dossier.class);
            dossier2 =
                dossierDistributionService.createDossier(
                    session,
                    dossier2,
                    question2,
                    null,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS
                );

            session.save();
        }
    }

    @Test
    public void testGetNiveau1Classement() {
        Map<String, Integer> resultAnNiv1 = planService.getPlanClassementNiveau1(
            session,
            DossierConstants.ORIGINE_QUESTION_AN
        );

        Assert.assertNotNull(resultAnNiv1);
        assertEquals(1, resultAnNiv1.size());
        assertTrue(resultAnNiv1.containsKey("administration"));
        assertEquals(Integer.valueOf(1), resultAnNiv1.get("administration"));

        Map<String, Integer> resultSenatNiv1 = planService.getPlanClassementNiveau1(
            session,
            DossierConstants.ORIGINE_QUESTION_SENAT
        );

        Assert.assertNotNull(resultSenatNiv1);
        assertEquals(2, resultSenatNiv1.size());
        assertTrue(resultSenatNiv1.containsKey("Rubrique"));
        assertEquals(Integer.valueOf(1), resultSenatNiv1.get("Rubrique"));
        assertTrue(resultSenatNiv1.containsKey("Thème"));
        assertEquals(Integer.valueOf(1), resultSenatNiv1.get("Thème"));

        Map<String, Integer> resultDefaultAn = planService.getPlanClassementNiveau1(session, "toto");
        Assert.assertNotNull(resultDefaultAn);
        assertEquals(1, resultDefaultAn.size());
    }

    @Test
    public void testGetNiveau2Classement() {
        Map<String, Integer> resultAnNiv2 = planService.getPlanClassementNiveau2(
            session,
            DossierConstants.ORIGINE_QUESTION_AN,
            "administration"
        );

        Assert.assertNotNull(resultAnNiv2);
        assertEquals(2, resultAnNiv2.size());
        assertTrue(resultAnNiv2.containsKey("activités"));
        assertEquals(Integer.valueOf(1), resultAnNiv2.get("activités"));

        Map<String, Integer> resultSenatNiv2 = planService.getPlanClassementNiveau2(
            session,
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "Rubrique"
        );

        Assert.assertNotNull(resultSenatNiv2);
        assertEquals(1, resultSenatNiv2.size());
        assertTrue(resultSenatNiv2.containsKey("Cadastre"));
        assertEquals(Integer.valueOf(1), resultSenatNiv2.get("Cadastre"));

        resultSenatNiv2 =
            planService.getPlanClassementNiveau2(session, DossierConstants.ORIGINE_QUESTION_SENAT, "Thème");

        Assert.assertNotNull(resultSenatNiv2);
        assertEquals(1, resultSenatNiv2.size());
        assertTrue(resultSenatNiv2.containsKey("Affaires étrangères et coopération"));
        assertEquals(Integer.valueOf(1), resultSenatNiv2.get("Affaires étrangères et coopération"));

        Map<String, Integer> resultDefaultAn = planService.getPlanClassementNiveau2(session, "toto", "administration");
        Assert.assertNotNull(resultDefaultAn);
        assertEquals(2, resultDefaultAn.size());

        Map<String, Integer> noResult = planService.getPlanClassementNiveau2(session, "toto", "toto");
        Assert.assertNotNull(noResult);
        assertEquals(1, noResult.size());
    }

    @Test
    public void testGetDossiersClasses() {
        String lstQuestionsAN = planService.getDossierFromIndexationQuery(
            DossierConstants.ORIGINE_QUESTION_AN,
            "administration",
            "activités"
        );
        Assert.assertNotNull(lstQuestionsAN);
        assertEquals(
            "SELECT ecm:uuid as id FROM Question WHERE (ixa:AN_rubrique = ? OR ixacomp:AN_rubrique = ? ) AND ( ixa:TA_rubrique = ? OR ixacomp:TA_rubrique = ? )",
            lstQuestionsAN
        );

        String lstQuestionsSenat = planService.getDossierFromIndexationQuery(
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "Thème",
            "Affaires étrangères et coopération"
        );
        Assert.assertNotNull(lstQuestionsSenat);
        assertEquals(
            "SELECT ecm:uuid as id FROM Question WHERE (ixa:SE_theme = ? OR ixacomp:SE_theme = ? )",
            lstQuestionsSenat
        );

        String lstNoQuestions = planService.getDossierFromIndexationQuery(
            DossierConstants.ORIGINE_QUESTION_SENAT,
            "Rubrique",
            "Affaires étrangères et coopération"
        );
        Assert.assertNotNull(lstNoQuestions);
        assertEquals(
            "SELECT ecm:uuid as id FROM Question WHERE (ixa:SE_rubrique = ? OR ixacomp:SE_rubrique = ? )",
            lstNoQuestions
        );
    }
}
