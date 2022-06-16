package fr.dila.reponses.core.suivi;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.RequeteurService;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Classe de test pour les requêtes préparamétrées définies dans la contribution reponses-content-template.xml
 *
 * @author jgomez
 *
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/test-requetes-preparametrees-contrib.xml")
public class TestRequetePreparametre {
    private static final Log log = LogFactory.getLog(TestRequetePreparametre.class);

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private RequeteurService rs;

    public String BIBLIOPATH = "/case-management/biblio-requetes-root/";

    public enum RequetePreparam {
        R2("r2-questions-etape-redaction-15joursapres-date-publication", 2),
        R3("r3-questions-etape-redaction-30joursapres-date-publication", 1),
        R4("r4-questions-etape-signature-25joursapres-date-publication", 4),
        R5("r5-questions-etape-signature-45joursapres-date-publication", 1),
        R6("r6-questions-deposees-moins-un-moi-plus-de-deux-reaffectations", 1),
        R11("r11-questions-sans-reponse-plus-un-mois", 3),
        R12("r12-questions-signalées-sans-reponse", 1);

        private String name;
        private int expectedResults;

        RequetePreparam(String name, int expectedResults) {
            this.name = name;
            this.expectedResults = expectedResults;
        }

        public String getName() {
            return this.name;
        }

        public int getExpectedResults() {
            return this.expectedResults;
        }
    }

    @Before
    public void setUp() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            createDossierTrouveParRequeteR2(session);
            createDossierTrouveParRequeteR3(session);
            createDossierTrouveParRequeteR4(session);
            createDossierTrouveParRequeteR5(session);
            createDossierTrouveParRequeteR6(session);
            createDossierTrouveParRequeteR11(session);
            createDossierTrouveParRequeteR12(session);
        }
    }

    @Test
    public void testInit() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Root
            DocumentModel biblio = getBiblio(session);
            displayRequetes(session, biblio);
            for (RequetePreparam r : RequetePreparam.values()) {
                valideResultatsRequete(session, r);
            }
        }
    }

    private void createDossierTrouveParRequeteR2(CoreSession session) throws Exception {
        Dossier dossier = reponseFeature.createDossier(session);
        Assert.assertFalse(
            "Le flag du dossier relatif à l'étape de redaction doit être à faux",
            dossier.getEtapeRedactionAtteinte()
        );
        // Positionnement d'une date de publication plus de 17 jours avant maintenant
        DateTime datePublication = new DateTime();
        datePublication = datePublication.minusDays(17);
        Question question = dossier.getQuestion(session);
        question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
        session.saveDocument(question.getDocument());
        session.saveDocument(dossier.getDocument());
        session.save();
    }

    private void createDossierTrouveParRequeteR3(CoreSession session) throws Exception {
        Dossier dossier = reponseFeature.createDossier(session);
        Assert.assertFalse(
            "Le flag du dossier relatif à l'étape de redaction doit être à faux",
            dossier.getEtapeRedactionAtteinte()
        );
        // Positionnement d'une date de publication plus de 33 jours avant maintenant
        DateTime datePublication = new DateTime();
        datePublication = datePublication.minusDays(33);
        Question question = dossier.getQuestion(session);
        question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
        session.saveDocument(question.getDocument());
        session.saveDocument(dossier.getDocument());
        session.save();
    }

    private void createDossierTrouveParRequeteR4(CoreSession session) throws Exception {
        Dossier dossier = reponseFeature.createDossier(session);
        dossier.setEtapeRedactionAtteinte(true);
        Assert.assertFalse(
            "Le flag du dossier relatif à l'étape de signature doit être à faux",
            dossier.getEtapeSignatureAtteinte()
        );
        // Positionnement d'une date de publication plus de 28 jours avant maintenant
        DateTime datePublication = new DateTime();
        datePublication = datePublication.minusDays(28);
        Question question = dossier.getQuestion(session);
        question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
        session.saveDocument(question.getDocument());
        session.saveDocument(dossier.getDocument());
        session.save();
    }

    private void createDossierTrouveParRequeteR5(CoreSession session) throws Exception {
        Dossier dossier = reponseFeature.createDossier(session);
        dossier.setEtapeRedactionAtteinte(true);
        Assert.assertFalse(
            "Le flag du dossier relatif à l'étape de signature doit être à faux",
            dossier.getEtapeSignatureAtteinte()
        );
        // Positionnement d'une date de publication plus de 50 jours avant maintenant
        DateTime datePublication = new DateTime();
        datePublication = datePublication.minusDays(50);
        Question question = dossier.getQuestion(session);
        question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
        session.saveDocument(question.getDocument());
        session.saveDocument(dossier.getDocument());
        session.save();
    }

    private void createDossierTrouveParRequeteR6(CoreSession session) throws Exception {
        Dossier dossier = reponseFeature.createDossier(session);
        dossier.setEtapeRedactionAtteinte(true);
        dossier.setReaffectionCount(3L);
        DateTime datePublication = new DateTime();
        // Une date de publication de moins d'un mois
        datePublication = datePublication.minusDays(20);
        Question question = dossier.getQuestion(session);
        question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
        session.saveDocument(question.getDocument());
        session.saveDocument(dossier.getDocument());
        session.save();
    }

    private void createDossierTrouveParRequeteR11(CoreSession session) throws Exception {
        Dossier dossier = reponseFeature.createDossier(session);
        dossier.setEtapeRedactionAtteinte(true);
        DateTime datePublication = new DateTime();
        // Une date de publication de plus d'un mois
        datePublication = datePublication.minusDays(40);
        Question question = dossier.getQuestion(session);
        question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
        session.saveDocument(question.getDocument());
        session.saveDocument(dossier.getDocument());
        session.save();
    }

    private void createDossierTrouveParRequeteR12(CoreSession session) throws Exception {
        Dossier dossier = reponseFeature.createDossier(session);
        dossier.setEtapeRedactionAtteinte(true);
        String delaiQuestionSignalee = "12";
        Calendar dateSignalement = GregorianCalendar.getInstance();

        // Une date de publication de plus d'un mois
        Question question = dossier.getQuestion(session);
        question.setEtatQuestion(
            session,
            VocabularyConstants.ETAT_QUESTION_SIGNALEE,
            dateSignalement,
            delaiQuestionSignalee
        );
        DateTime datePublication = new DateTime();
        datePublication = datePublication.minusDays(5);
        question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
        Assert.assertNotEquals("La question ne doit pas avoir de réponse publiée", "repondu", question.getEtatQuestion(session).getNewState());
        Assert.assertTrue("La question doit être dans un état signalé", question.getEtatSignale());

        List<Signalement> signalements = question.getSignalements();
        Assert.assertFalse(signalements.isEmpty());
        Signalement signalement = signalements.get(0);
        Assert.assertEquals(dateSignalement.getTime(), signalement.getDateEffet());
        dateSignalement.add(Calendar.DAY_OF_YEAR, Integer.parseInt(delaiQuestionSignalee));
        Assert.assertEquals(dateSignalement.getTime(), signalement.getDateAttendue());

        session.saveDocument(question.getDocument());
        session.saveDocument(dossier.getDocument());
        session.save();
    }

    private void valideResultatsRequete(CoreSession session, RequetePreparam requete) {
        DocumentModel requeteR2Doc = session.getDocument(new PathRef(BIBLIOPATH + requete.getName()));
        List<DocumentModel> results = getResults(session, requeteR2Doc);
        Assert.assertEquals(
            "La requête " + requete.getName() + " attend " + requete.getExpectedResults() + " et non " + results.size(),
            requete.getExpectedResults(),
            results.size()
        );
    }

    private List<DocumentModel> getResults(CoreSession session, DocumentModel requeteR2Doc) {
        RequeteExperte requeteR2 = requeteR2Doc.getAdapter(RequeteExperte.class);
        List<DocumentModel> results = rs.query(session, requeteR2);
        log.error(requeteR2Doc.getName());
        log.error(rs.getPattern(session, requeteR2));
        return results;
    }

    private void displayRequetes(CoreSession session, DocumentModel biblio) {
        DocumentModelList requetes = session.getChildren(biblio.getRef());
        for (DocumentModel requeteDoc : requetes) {
            log.error(requeteDoc.getName());
        }
    }

    private DocumentModel getBiblio(CoreSession session) {
        return session.getDocument(new PathRef(BIBLIOPATH));
    }
}
