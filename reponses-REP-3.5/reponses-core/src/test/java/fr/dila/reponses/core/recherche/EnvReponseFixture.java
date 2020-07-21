package fr.dila.reponses.core.recherche;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.model.PropertyException;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.ReponsesComplIndexableDocument;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

/**
 * Une fixture basique qui contient un dossier et une réponse
 * 
 * @author jgomez
 * 
 */
public class EnvReponseFixture {

	protected Dossier						dossier1;

	protected Question						question1;

	protected Reponse						reponse1;

	protected DossierDistributionService	ds;

	// //// 2 ou 3 dossiers pour tester les recherches
	public void setUpEnv(CoreSession session) throws Exception {
		// Dossier 1, avec quasiment rien
		/** Set up env **/
		reponse1 = createReponse1(session);
		question1 = createQuestion1(session);
		ds = ReponsesServiceLocator.getDossierDistributionService();
		dossier1 = createDossier(session);

		ds.createDossier(session, dossier1, question1, reponse1, VocabularyConstants.ETAT_QUESTION_EN_COURS);

		session.save();
	}

	public Dossier getDossier1() {
		return dossier1;
	}

	public Reponse getReponse1() {
		return reponse1;
	}

	private Reponse createReponse1(CoreSession session) throws ClientException {
		DocumentModel modelDesired = new DocumentModelImpl("/", "reponse", DossierConstants.REPONSE_DOCUMENT_TYPE);
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

	public Question createQuestion1(CoreSession session) throws PropertyException, ClientException {
		DocumentModel questionDocModel = new DocumentModelImpl("/", "mydoc", DossierConstants.QUESTION_DOCUMENT_TYPE);
		Question question = questionDocModel.getAdapter(Question.class);
		question.setOrigineQuestion("AN");
		question.setGroupePolitique("UMP");
		question.setNumeroQuestion(4L);
		Calendar dateJO = GregorianCalendar.getInstance();
		dateJO.set(2009, 5, 14);
		question.setDatePublicationJO(dateJO);
		List<String> rubriqueList = new ArrayList<String>();
		rubriqueList.add("agroalimentaire");
		rubriqueList.add("ecologie");
		question.setAssNatRubrique(rubriqueList);
		List<String> analyseList = new ArrayList<String>();
		analyseList.add("patates");
		analyseList.add("choux");
		question.setAssNatAnalyses(analyseList);
		List<String> teteanalyseList = new ArrayList<String>();
		teteanalyseList.add("ta1");
		teteanalyseList.add("ta2");
		question.setAssNatTeteAnalyse(teteanalyseList);
		question.setNomAuteur("Taillerand");
		question.setPrenomAuteur("Jean-Marc");
		List<String> seThemeList = new ArrayList<String>();
		seThemeList.add("Bidonville");
		seThemeList.add("Afrique");
		question.setSenatQuestionThemes(seThemeList);
		List<String> seRubriqueList = new ArrayList<String>();
		seRubriqueList.add("serub1");
		seRubriqueList.add("serub2");
		question.setSenatQuestionRubrique(seRubriqueList);
		List<String> seRenvoisList = new ArrayList<String>();
		seRenvoisList.add("renvois1");
		seRenvoisList.add("renvois2");
		question.setSenatQuestionRenvois(seRenvoisList);
		question.setTexteQuestion("C\\’est un saxophoniste important dans"
				+ " l'histoire du jazz, en particulier par la création d’une esthétique originale "
				+ "et très personnelle, privilégiant la mélodie et la sensibilité. Garbarek possède"
				+ " une identité musicale extrêmement reconnaissable, se démarquant ");
		List<String> motsClefsMinistereList = new ArrayList<String>();
		motsClefsMinistereList.add("m1");
		motsClefsMinistereList.add("m2");
		ReponsesComplIndexableDocument indexationCompl = question.getDocument().getAdapter(
				ReponsesComplIndexableDocument.class);
		indexationCompl.addVocEntry(VocabularyConstants.SE_THEME, "tata");
		indexationCompl.addVocEntry(VocabularyConstants.AN_RUBRIQUE, "lutte contre l'exclusion");
		ReponsesIndexableDocument indexationOrigine = question.getDocument()
				.getAdapter(ReponsesIndexableDocument.class);
		indexationOrigine.addVocEntry(VocabularyConstants.SE_THEME, "toto");
		question.setMotsClefMinistere(motsClefsMinistereList);
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		return question;
	}

	public Dossier createDossier(CoreSession session) throws ClientException {
		DocumentModel dossierDocModel = new DocumentModelImpl("/", "mydoc", "Dossier");
		DocumentModel modelResult = session.createDocument(dossierDocModel);
		Dossier dossier = modelResult.getAdapter(Dossier.class);
		return dossier;
	}
}
