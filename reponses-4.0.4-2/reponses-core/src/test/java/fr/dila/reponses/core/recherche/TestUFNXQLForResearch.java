package fr.dila.reponses.core.recherche;

import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestUFNXQLForResearch {
    private static Log log = LogFactory.getLog(TestUFNXQLForResearch.class);

    @Inject
    private CoreFeature coreFeature;

    private EnvReponseFixture fixture;
    private String questionId1;

    @Before
    public void setUp() {
        STServiceLocator.getSTParametreService().clearCache();
        if (fixture == null) {
            try (CloseableCoreSession session = coreFeature.openCoreSession()) {
                fixture = new EnvReponseFixture();
                fixture.setUpEnv(session);
                questionId1 = fixture.getQuestion1(session).getDocument().getId();
            }
        }
    }

    @Test
    public void testListQuestion() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            {
                String query = "SELECT q.ecm:uuid AS id FROM Question AS q";
                Object[] params = null;
                IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
                try {
                    Iterator<Map<String, Serializable>> it = res.iterator();
                    while (it.hasNext()) {
                        Map<String, Serializable> m = it.next();
                        Assert.assertEquals(1, m.size());
                        Serializable sid = m.get("id");
                        Assert.assertNotNull(sid);
                        String id = (String) sid;
                        Assert.assertNotNull(id);
                        Assert.assertEquals(questionId1, id);
                        //                for(String key : m.keySet()){
                        //                    log.info(key + " --> " + m.get(key));
                        //                }
                    }
                    Assert.assertEquals(1, res.size());
                } finally {
                    res.close();
                }
            }
            {
                // nombre de question par auteur

                String query =
                    "SELECT q.qu:nomCompletAuteur AS auteur, count() FROM Question AS q GROUP BY q.qu:nomCompletAuteur";
                Object[] params = null;
                IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
                try {
                    Iterator<Map<String, Serializable>> it = res.iterator();
                    while (it.hasNext()) {
                        Map<String, Serializable> m = it.next();
                        Assert.assertEquals(2, m.size());

                        String auteur = (String) m.get("auteur");
                        Assert.assertNotNull(auteur);
                        Assert.assertEquals("Taillerand Jean-Marc", auteur);

                        Long count = (Long) m.get("count()");
                        Assert.assertNotNull(count);
                        Assert.assertEquals(new Long(1), count);
                        //                    for(String key : m.keySet()){
                        //                        log.info(key + " --> " + m.get(key));
                        //                    }
                    }
                    Assert.assertEquals(1, res.size());
                } finally {
                    res.close();
                }
            }
        }
    }

    @Test
    public void testQuestionDossier() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String query = null;
            query =
                "SELECT  q.ecm:uuid as id, d.ecm:uuid, d.dc:title " +
                "FROM Dossier AS d, Question AS q WHERE " +
                "q.ecm:uuid = d.dos:idDocumentQuestion AND d.dc:title LIKE ?";
            Object[] params = new Object[] { "Do%4" };

            try (IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params)) {
                Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    Map<String, Serializable> m = it.next();
                    Assert.assertEquals(3, m.size());

                    String id = (String) m.get("id");
                    Assert.assertNotNull(id);
                    Assert.assertEquals(questionId1, id);

                    DocumentModel dossierDoc1 = fixture.getDossier1(session).getDocument();

                    id = (String) m.get("d.ecm:uuid");
                    Assert.assertNotNull(id);
                    Assert.assertEquals(dossierDoc1.getId(), id);

                    String title = (String) m.get("d.dc:title");
                    Assert.assertNotNull(title);
                    Assert.assertEquals(DublincoreSchemaUtils.getTitle(dossierDoc1), title);
                }
                Assert.assertEquals(1, res.size());
            }

            //	        query = "SELECT q.ecm:uuid AS id FROM Question AS q WHERE isChildOf(q.ecm:uuid, SELECT d.ecm:uuid FROM Dossier AS d) = 1";
            //	        try(IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, null);) {
            //	            Assert.fail();
            //	        } catch(NuxeoException e){
            //	            // expect exception because use isChildOf with optimized type and with only one parameter
            //	        }

            query =
                "SELECT q.ecm:uuid AS id FROM Question AS q WHERE isChildOf(q.ecm:uuid, q.ecm:parentId, SELECT d.ecm:uuid FROM Dossier AS d WHERE d.dc:title LIKE 'Do%4') = 1";
            try (IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, null);) {
                Assert.assertEquals(1, res.size());
            }
        }
    }

    @Test
    public void testQuestionIndexation1() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String query = "SELECT  q.ecm:uuid FROM Question AS q WHERE q.ixa:AN_rubrique = ?";
            Object[] params = new Object[] { "ecologie" };
            IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    Map<String, Serializable> m = it.next();
                    for (String key : m.keySet()) {
                        log.info(key + " --> " + m.get(key));
                    }
                    String id = (String) m.get("q.ecm:uuid");
                    Assert.assertEquals(questionId1, id);
                }
                Assert.assertEquals(1, res.size());
            } finally {
                res.close();
            }
        }
    }

    @Test
    public void testQuestionIndexation() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String query = "SELECT  q.ecm:uuid FROM Question AS q WHERE q.ixa:AN_rubrique = ?";
            Object[] params = new Object[] { "ecologie" };
            IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    Map<String, Serializable> m = it.next();
                    for (String key : m.keySet()) {
                        log.info(key + " --> " + m.get(key));
                    }
                    String id = (String) m.get("q.ecm:uuid");
                    Assert.assertEquals(questionId1, id);
                }
                Assert.assertEquals(1, res.size());
            } finally {
                res.close();
            }
        }

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            {
                String query = "SELECT  q.ecm:uuid, q.ixa:AN_rubrique FROM Question AS q";
                Object[] params = null;
                IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
                try {
                    Iterator<Map<String, Serializable>> it = res.iterator();
                    while (it.hasNext()) {
                        Map<String, Serializable> m = it.next();
                        for (String key : m.keySet()) {
                            log.info(key + " --> " + m.get(key));
                        }
                        String id = (String) m.get("q.ecm:uuid");
                        Assert.assertEquals(questionId1, id);
                    }
                    // 2 resultats :
                    // les combinaison de l'id de la question et des deux rubrique d'indexation
                    Assert.assertEquals(2, res.size());
                } finally {
                    res.close();
                }
            }

            // SELECT IXA_AN_RUBRIQUE.ITEM AS LABEL, COUNT(IXA_AN_RUBRIQUE.ITEM) AS COUNT
            {
                String query =
                    "SELECT  q.ixa:AN_rubrique AS label, count() as count FROM Question AS q group by q.ixa:AN_rubrique";
                Object[] params = null;
                IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
                try {
                    Iterator<Map<String, Serializable>> it = res.iterator();
                    while (it.hasNext()) {
                        Map<String, Serializable> m = it.next();
                        for (String key : m.keySet()) {
                            log.info(key + " --> " + m.get(key));
                        }
                        String label = (String) m.get("label");
                        Long count = (Long) m.get("count");
                        Assert.assertEquals(new Long(1L), count);
                        Assert.assertTrue("ecologie".equals(label) || "agroalimentaire".equals(label));
                    }
                    // deux rubrique d'indexation pour
                    Assert.assertEquals(2, res.size());
                } finally {
                    res.close();
                }
            }
        }
    }
}
