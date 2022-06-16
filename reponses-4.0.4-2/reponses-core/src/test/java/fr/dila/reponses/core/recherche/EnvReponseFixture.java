package fr.dila.reponses.core.recherche;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.ReponsesComplIndexableDocument;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * Une fixture basique qui contient un dossier et une réponse
 *
 * @author jgomez
 *
 */
public class EnvReponseFixture {
    protected DocumentRef dossierRef1;

    protected DocumentRef questionRef1;

    protected DocumentRef reponseRef1;

    // //// 2 ou 3 dossiers pour tester les recherches
    public void setUpEnv(CoreSession session) {
        DossierDistributionService ds = ServiceUtil.getRequiredService(DossierDistributionService.class);
        // Dossier 1, avec quasiment rien
        /** Set up env **/
        Reponse reponse1 = createReponse1(session);
        Question question1 = createQuestion1(session);
        ds = ReponsesServiceLocator.getDossierDistributionService();
        Dossier dossier1 = createDossier(session);

        dossier1 = ds.createDossier(session, dossier1, question1, reponse1, VocabularyConstants.ETAT_QUESTION_EN_COURS);

        dossierRef1 = dossier1.getDocument().getRef();
        questionRef1 = dossier1.getQuestion(session).getDocument().getRef();
        reponseRef1 = dossier1.getReponse(session).getDocument().getRef();

        session.save();
    }

    public DocumentRef getDossierRef1() {
        return dossierRef1;
    }

    public DocumentRef getQuestionRef1() {
        return questionRef1;
    }

    public DocumentRef getReponseRef1() {
        return reponseRef1;
    }

    public Dossier getDossier1(CoreSession session) {
        return session.getDocument(dossierRef1).getAdapter(Dossier.class);
    }

    public Question getQuestion1(CoreSession session) {
        return session.getDocument(questionRef1).getAdapter(Question.class);
    }

    public Reponse getReponse1(CoreSession session) {
        return session.getDocument(reponseRef1).getAdapter(Reponse.class);
    }

    private Reponse createReponse1(CoreSession session) {
        DocumentModel modelDesired = session.createDocumentModel(
            "/",
            "reponse",
            DossierConstants.REPONSE_DOCUMENT_TYPE
        );
        DocumentModel modelResult = session.createDocument(modelDesired);

        modelResult.setProperty("reponse", "identifiant", 12356);
        modelResult.setProperty("reponse", "dateValidation", new Date());

        Reponse reponse = modelResult.getAdapter(Reponse.class);
        Calendar dateJO = GregorianCalendar.getInstance();
        dateJO.set(2001, 5, 14);

        reponse.setDateJOreponse(dateJO);
        reponse.setPageJOreponse((long) 3);
        reponse.setTexteReponse("texte réponse");
        return reponse;
    }

    public Question createQuestion1(CoreSession session) {
        DocumentModel questionDocModel = session.createDocumentModel(
            "/",
            "mydoc",
            DossierConstants.QUESTION_DOCUMENT_TYPE
        );
        Question question = questionDocModel.getAdapter(Question.class);
        question.setOrigineQuestion("AN");
        question.setGroupePolitique("UMP");
        question.setNumeroQuestion(4L);
        Calendar dateJO = GregorianCalendar.getInstance();
        dateJO.set(2009, 5, 14);
        question.setDatePublicationJO(dateJO);
        List<String> rubriqueList = new ArrayList<>();
        rubriqueList.add("agroalimentaire");
        rubriqueList.add("ecologie");
        question.setAssNatRubrique(rubriqueList);
        List<String> analyseList = new ArrayList<>();
        analyseList.add("patates");
        analyseList.add("choux");
        question.setAssNatAnalyses(analyseList);
        List<String> teteanalyseList = new ArrayList<>();
        teteanalyseList.add("ta1");
        teteanalyseList.add("ta2");
        question.setAssNatTeteAnalyse(teteanalyseList);
        question.setNomAuteur("Taillerand");
        question.setPrenomAuteur("Jean-Marc");
        List<String> seThemeList = new ArrayList<>();
        seThemeList.add("Bidonville");
        seThemeList.add("Afrique");
        question.setSenatQuestionThemes(seThemeList);
        List<String> seRubriqueList = new ArrayList<>();
        seRubriqueList.add("serub1");
        seRubriqueList.add("serub2");
        question.setSenatQuestionRubrique(seRubriqueList);
        List<String> seRenvoisList = new ArrayList<>();
        seRenvoisList.add("renvois1");
        seRenvoisList.add("renvois2");
        question.setSenatQuestionRenvois(seRenvoisList);
        question.setTexteQuestion(
            "C\\’est un saxophoniste important dans" +
            " l'histoire du jazz, en particulier par la création d’une esthétique originale " +
            "et très personnelle, privilégiant la mélodie et la sensibilité. Garbarek possède" +
            " une identité musicale extrêmement reconnaissable, se démarquant "
        );
        List<String> motsClefsMinistereList = new ArrayList<>();
        motsClefsMinistereList.add("m1");
        motsClefsMinistereList.add("m2");
        ReponsesComplIndexableDocument indexationCompl = question
            .getDocument()
            .getAdapter(ReponsesComplIndexableDocument.class);
        indexationCompl.addVocEntry(SE_THEME.getValue(), "tata");
        indexationCompl.addVocEntry(AN_RUBRIQUE.getValue(), "lutte contre les inondations");
        ReponsesIndexableDocument indexationOrigine = question
            .getDocument()
            .getAdapter(ReponsesIndexableDocument.class);
        indexationOrigine.addVocEntry(SE_THEME.getValue(), "toto");
        question.setMotsClefMinistere(motsClefsMinistereList);
        question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
        return question;
    }

    public Dossier createDossier(CoreSession session) {
        DocumentModel dossierDocModel = session.createDocumentModel("/", "mydoc", "Dossier");
        DocumentModel modelResult = session.createDocument(dossierDocModel);
        Dossier dossier = modelResult.getAdapter(Dossier.class);
        return dossier;
    }
}
