package fr.dila.reponses.core.service;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.recherche.EnvReponseFixture;
import fr.dila.reponses.core.recherche.RechercheFeature;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.joda.time.DateTime;
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
@Features({ ReponseFeature.class, SolonMockitoFeature.class })
public class TestRequeteurService {
    public static final String BASE_QUERY = RechercheFeature.BASE_QUERY;

    private static final String NOW = "NOW";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private RequeteurService requeteurService;

    @Inject
    private CoreSession session;

    private RequeteExperte requete1;
    private RequeteExperte requete2;
    private RequeteExperte requete3;
    private RequeteExperte requete4;
    private RequeteExperte requete5;

    @Before
    public void setUp() {
        ServiceUtil.getRequiredService(STParametreService.class).clearCache();

        requete1 = createRequete1(session);
        requete2 = createRequete2(session);
        requete3 = createRequete3(session);
        requete4 = createRequete4(session);
        requete5 = createRequete5(session);

        EnvReponseFixture fixture = new EnvReponseFixture();
        fixture.setUpEnv(session);
    }

    @Test
    public void testInit() {
        Assert.assertNotNull(requeteurService);
        Assert.assertNotNull(requete1);
        Assert.assertEquals(true, requete1.getDocument().hasSchema(STRequeteConstants.SMART_FOLDER_SCHEMA));
    }

    @Test
    public void testGetPattern1() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.newMockSSPrincipal())) {
            String pattern = requeteurService.getPattern(session, requete1);
            Assert.assertEquals(BASE_QUERY + " WHERE ((q.qu:numeroQuestion = '4'))", pattern);
        }
    }

    @Test
    public void testCount1() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.newMockSSPrincipal())) {
            Long count = requeteurService.countResults(session, requete1);
            Assert.assertEquals(1, (long) count);
        }
    }

    @Test
    public void testQuery1() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.newMockSSPrincipal())) {
            List<DocumentModel> docs = requeteurService.query(session, requete1);
            Assert.assertNotNull(docs);
            Assert.assertEquals(1, docs.size());
        }
    }

    @Test
    public void testGetPattern() {
        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.newMockSSPrincipal())) {
            doTestGetPattern2(session);
            doTestGetPattern3(session);
            doTestGetPattern4(session);
            doTestGetPattern5(session);
        }
    }

    private void doTestGetPattern2(CoreSession session) {
        String pattern = requeteurService.getPattern(session, requete2);
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d,Reponse AS r WHERE ((q.qu:numeroQuestion = '4' AND r.note:note LIKE = 'HELLO') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )",
            pattern
        );
    }

    // Pattern avec la fonction date (valeur en jours)
    private void doTestGetPattern3(CoreSession session) {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put(NOW, new DateTime(2011, 7, 5, 10, 36, 0, 0));
        String pattern = requeteurService.getPattern(session, requete3, env);
        Assert.assertEquals(
            BASE_QUERY + " WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication < DATE '2011-06-20'))",
            pattern
        );
    }

    // Pattern avec la fonction date (valeur en mois)
    private void doTestGetPattern4(CoreSession session) {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put(NOW, new DateTime(2011, 7, 5, 10, 36, 0, 0));
        String pattern = requeteurService.getPattern(session, requete4, env);
        Assert.assertEquals(
            BASE_QUERY + " WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication < DATE '2011-06-05'))",
            pattern
        );
    }

    // Pattern avec la fonction ministere
    public void doTestGetPattern5(CoreSession session) {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put(NOW, new DateTime(2011, 7, 5, 10, 36, 0, 0));
        String pattern = requeteurService.getPattern(session, requete5, env);
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d WHERE ((q.qu:numeroQuestion = '4' AND d.dos:" +
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            " IN ('502','503')) AND d.dos:idDocumentQuestion = q.ecm:uuid )",
            pattern
        );
    }

    /**
     * Création d'une requête simple.
     *
     * @return
     * @throws Exception
     */
    private RequeteExperte createRequete1(CoreSession session) {
        DocumentModel doc = session.createDocumentModel("/", "req1", RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc = session.createDocument(doc);
        RequeteExperte requete = doc.getAdapter(RequeteExperte.class);
        requete.setWhereClause("q.qu:numeroQuestion = '4'");
        return requete;
    }

    /**
     * Création d'une requête plus élaborée pour voir si le service de joiture
     * fonctionne correctement.
     *
     * @return
     * @throws Exception
     */
    private RequeteExperte createRequete2(CoreSession session) {
        DocumentModel doc = session.createDocumentModel(RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc = session.createDocument(doc);
        RequeteExperte requete = doc.getAdapter(RequeteExperte.class);
        requete.setWhereClause("q.qu:numeroQuestion = '4' AND r.note:note LIKE = 'HELLO'");
        return requete;
    }

    /**
     * Création d'une requête avec un mot-clé spécial ufnxql_date: pour être
     * résolue directement par le requêteur.
     *
     * @return
     * @throws Exception
     */
    private RequeteExperte createRequete3(CoreSession session) {
        DocumentModel doc = session.createDocumentModel(RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc = session.createDocument(doc);
        RequeteExperte requete = doc.getAdapter(RequeteExperte.class);
        requete.setWhereClause("q.qu:numeroQuestion = '4' AND q.qu:datePublication < ufnxql_date:(NOW-15J)");
        return requete;
    }

    /**
     * Création d'une requête avec un mot-clé spécial ufnxql_date: pour être
     * résolue directement par le requêteur.
     *
     * @return
     * @throws Exception
     */
    private RequeteExperte createRequete4(CoreSession session) {
        DocumentModel doc = session.createDocumentModel(RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc = session.createDocument(doc);
        RequeteExperte requete = doc.getAdapter(RequeteExperte.class);
        requete.setWhereClause("q.qu:numeroQuestion = '4' AND q.qu:datePublication < ufnxql_date:(NOW-1M)");
        return requete;
    }

    /**
     * Création d'une requête avec un mot-clé spécial ufnxql_ministere: pour
     * être résolue directement par le requêteur.
     *
     * @return
     * @throws Exception
     */
    private RequeteExperte createRequete5(CoreSession session) {
        DocumentModel doc = session.createDocumentModel(RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc = session.createDocument(doc);
        RequeteExperte requete = doc.getAdapter(RequeteExperte.class);
        requete.setWhereClause(
            "q.qu:numeroQuestion = '4' AND ufnxql_ministere:(d.dos:" +
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            ")"
        );
        return requete;
    }
}
