package fr.dila.reponses.core.service;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.enumeration.IndexModeEnum;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.recherche.Recherche;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.helper.ParameterTestHelper;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/test-recherche-contrib.xml")
public class TestRechercheService {
    private static final Log LOG = LogFactory.getLog(TestRechercheService.class);

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private RechercheService rs;

    @Before
    public void setup() {
        ServiceUtil.getRequiredService(STParametreService.class).clearCache();
    }

    @Test
    public void testGetListDocumentRecherche() {
        List<Recherche> rechercheList = rs.getRecherches();
        Assert.assertEquals(2, rechercheList.size());
        Assert.assertEquals("recherche_simple_mots_clefs_sur_actes", rechercheList.get(0).getRechercheName());
        Assert.assertEquals("simple", rechercheList.get(0).getMode());
        Assert.assertEquals("recherche_complexe_mots_clefs_sur_actes", rechercheList.get(1).getRechercheName());
        Assert.assertEquals("complexe", rechercheList.get(1).getMode());
    }

    @Test
    public void testGetRecherche() {
        Recherche recherche_simple = rs.getRecherche("simple", "mots-clefs", "acte");
        Assert.assertNotNull(recherche_simple);
        Assert.assertEquals("recherche_simple_mots_clefs_sur_actes", recherche_simple.getRechercheName());
        Assert.assertEquals("recherche_simple_contentview", recherche_simple.getContentViewName());
        Recherche recherche_complexe = rs.getRecherche("complexe", "mots-clefs", "acte");
        Assert.assertNotNull(recherche_complexe);
        Assert.assertEquals("recherche_complexe_mots_clefs_sur_actes", recherche_complexe.getRechercheName());
        Assert.assertEquals("recherche_complexe_contentview", recherche_complexe.getContentViewName());
    }

    @Test
    public void testGetRechercheByName() {
        Recherche recherche_simple = rs.getRecherche("recherche_simple_mots_clefs_sur_actes");
        Assert.assertNotNull(recherche_simple);
        Assert.assertEquals("simple", recherche_simple.getMode());
        Recherche recherche_complexe = rs.getRecherche("recherche_complexe_mots_clefs_sur_actes");
        Assert.assertNotNull(recherche_complexe);
        Assert.assertEquals("RequeteComplexe", recherche_complexe.getRequeteName());
    }

    @Test
    public void testFdr() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Requete req = rs.createRequete(session, "essai");
            req.getDocument().setProperty("requeteFeuilleRoute", "typeStep", "1");
            req.getDocument().setProperty("requeteFeuilleRoute", "statusStep", "validated_2");
            req.doBeforeQuery();
            String query = rs.getWhereClause(req, "requeteFdr");
            Assert.assertEquals(
                "(e2.rtsk:type = \'1\' AND e2.rtsk:validationStatus = '2' AND e2.ecm:currentLifeCycleState = 'validated')",
                query
            );
        }
    }

    @Test
    public void testCumulPanneaux() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Requete req = rs.createRequete(session, "essai");
            req.getDocument().setProperty("requeteFeuilleRoute", "typeStep", "1");
            req.getDocument().setProperty("requeteFeuilleRoute", "statusStep", "validated_2");
            List<String> index = new ArrayList<>();
            index.add("cailloux");
            index.add("chou");
            req.getDocument().setProperty("indexation", SE_THEME.getValue(), index);
            req.getDocument().setProperty("indexation", SE_RUBRIQUE.getValue(), index);
            req.doBeforeQuery();
            String query = rs.getWhereClause(req, "requeteFdr", "requeteIndex");
            Assert.assertEquals(
                "(e2.rtsk:type = '1' AND e2.rtsk:validationStatus = '2' AND e2.ecm:currentLifeCycleState = 'validated') AND (q.ixa:SE_rubrique IN ('cailloux', 'chou') AND q.ixa:SE_theme IN ('cailloux', 'chou'))",
                query
            );
        }
    }

    @Test
    public void testGetRequeteParts() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Requete req = rs.createRequete(session, "essai");
            req.setIndexationMode(IndexModeEnum.INDEX_ORIG);
            String[] requeteModelNames = ((RechercheServiceImpl) rs).getRequeteParts(req);
            Assert.assertEquals(6, requeteModelNames.length);
            Assert.assertEquals(false, arrayContains(requeteModelNames, "requeteIndexCompl"));
            Assert.assertEquals(true, arrayContains(requeteModelNames, "requeteIndex"));
        }
    }

    @Test
    public void testCumulTousPanneaux() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Requete req = rs.createRequete(session, "essai");
            req.getDocument().setProperty("requeteFeuilleRoute", "typeStep", "1");
            req.getDocument().setProperty("requeteFeuilleRoute", "statusStep", "validated_2");
            List<String> index = new ArrayList<>();
            index.add("cailloux");
            index.add("chou");
            req.getDocument().setProperty("indexation", SE_THEME.getValue(), index);
            req.getDocument().setProperty("indexation", SE_RUBRIQUE.getValue(), index);
            req.setIndexationMode(IndexModeEnum.INDEX_ORIG);
            req.doBeforeQuery();
            String[] queryModels = ((RechercheServiceImpl) rs).getRequeteParts(req);
            String query = rs.getWhereClause(req, queryModels);
            Assert.assertEquals(
                "(q.ixa:SE_rubrique IN ('cailloux', 'chou') AND q.ixa:SE_theme IN ('cailloux', 'chou')) AND (e2.rtsk:type = '1' AND e2.rtsk:validationStatus = '2' AND e2.ecm:currentLifeCycleState = 'validated')",
                query
            );
        }
    }

    @Test
    public void testCumulPanneauxNuls() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Requete req = rs.createRequete(session, "essai");
            req.getDocument().setProperty("requeteFeuilleRoute", "typeStep", "1");
            req.getDocument().setProperty("requeteFeuilleRoute", "statusStep", "validated_2");
            List<String> index = new ArrayList<>();
            index.add("cailloux");
            index.add("chou");
            req.doBeforeQuery();
            String query = rs.getWhereClause(req, "requeteFdr", "requeteIndex", "requeteComplexe");
            Assert.assertEquals(
                "(e2.rtsk:type = \'1\' AND e2.rtsk:validationStatus = '2' AND e2.ecm:currentLifeCycleState = 'validated')",
                query
            );
        }
    }

    @Test
    public void testTousPanneauxNuls() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Requete req = rs.createRequete(session, "essai");
            String query = rs.getWhereClause(req, "requeteFdr", "requeteIndex", "requeteComplexe");
            Assert.assertEquals("", query);
        }
    }

    @Test
    public void testWrongPanneau() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Requete req = rs.createRequete(session, "essai");
            String query = rs.getWhereClause(
                req,
                "requeteFdr",
                "indexation",
                "requeteComplexe",
                "requeteTexteIntegral"
            );
            Assert.assertEquals("", query);
        }
    }

    @Test
    public void testSearchQuestionBySourceNumero() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // recherche une question sans que le parametre sur la legislation courante
            // soit defini
            ParameterTestHelper.changeOrCreateParammeter(session, ReponsesParametreConstant.LEGISLATURE_COURANTE, null);
            try {
                rs.searchQuestionBySourceNumero(session, "555");
                Assert.fail("legislation parameter incorrect : should raise exception");
            } catch (NuxeoException e) {
                // expected exception
                LOG.info(e.getMessage());
                Assert.assertTrue(e.getMessage().contains("législature courante n'est pas paramétrée correctement"));
            }

            // create parameter for legislation
            final Long legislature = 12L;
            ParameterTestHelper.changeOrCreateParammeter(
                session,
                ReponsesParametreConstant.LEGISLATURE_COURANTE,
                Long.toString(legislature)
            );
            session.save();

            // recherche une question avec que le parametre sur la legislation courante
            // defini
            try {
                rs.searchQuestionBySourceNumero(session, "555");
                Assert.fail("ne devrait pas trouvé de question : should raise exception");
            } catch (NuxeoException e) {
                // expected exception
                LOG.info(e.getMessage());
                Assert.assertTrue(e.getMessage().startsWith("Aucune question trouvée"));
            }

            // create question for test
            DocumentModel doc;
            Question question;
            doc = reponseFeature.createDocument(session, "q-555-1", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
            question = doc.getAdapter(Question.class);
            question.setOrigineQuestion("AN");
            question.setNumeroQuestion(555L);
            question.setTypeQuestion("QE");
            question.setLegislatureQuestion(legislature);
            doc = session.saveDocument(doc);
            final String question555Id = doc.getId();

            doc = reponseFeature.createDocument(session, "q-555-2", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
            question = doc.getAdapter(Question.class);
            question.setOrigineQuestion("AN");
            question.setNumeroQuestion(555L);
            question.setTypeQuestion("QE");
            question.setLegislatureQuestion(2L);
            doc = session.saveDocument(doc);

            doc = reponseFeature.createDocument(session, "q-666", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
            question = doc.getAdapter(Question.class);
            question.setOrigineQuestion("AN");
            question.setNumeroQuestion(666L);
            question.setTypeQuestion("QO");
            question.setLegislatureQuestion(legislature);
            doc = session.saveDocument(doc);

            doc = reponseFeature.createDocument(session, "q-777-1", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
            question = doc.getAdapter(Question.class);
            question.setOrigineQuestion("AN");
            question.setNumeroQuestion(777L);
            question.setTypeQuestion("QE");
            question.setLegislatureQuestion(legislature);
            doc = session.saveDocument(doc);

            doc = reponseFeature.createDocument(session, "q-777-2", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
            question = doc.getAdapter(Question.class);
            question.setOrigineQuestion("AN");
            question.setNumeroQuestion(777L);
            question.setTypeQuestion("QE");
            question.setLegislatureQuestion(legislature);
            doc = session.saveDocument(doc);

            session.save();

            Question q = rs.searchQuestionBySourceNumero(session, "AN 555");
            Assert.assertEquals(question555Id, q.getDocument().getId());

            // recherche d'une question : le numéro existe mais n'est pas une QE
            try {
                rs.searchQuestionBySourceNumero(session, "AN 666");
                Assert.fail("ne devrait pas trouvé de question : should raise exception");
            } catch (NuxeoException e) {
                // expected exception
                LOG.info(e.getMessage());
                Assert.assertTrue(e.getMessage().startsWith("Aucune question trouvée"));
            }

            // recherche d'une question : plusieurs correspondance
            try {
                rs.searchQuestionBySourceNumero(session, "AN 777");
                Assert.fail("devrait trouvé plus d'une question : should raise exception");
            } catch (NuxeoException e) {
                // expected exception
                LOG.info(e.getMessage());
                Assert.assertTrue(e.getMessage().startsWith("Plusieurs questions trouvées"));
            }
        }
    }

    private <T> boolean arrayContains(T[] array, T value) {
        if (array != null) {
            for (T item : array) {
                if (item != null && item.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
}
