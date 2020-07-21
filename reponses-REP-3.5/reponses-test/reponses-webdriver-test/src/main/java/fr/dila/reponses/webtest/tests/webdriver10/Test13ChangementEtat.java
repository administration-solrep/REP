package fr.dila.reponses.webtest.tests.webdriver10;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;

import junit.framework.Assert;
import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.utils.WsUtils;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.rest.client.WSProxyFactoryException;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.reponses.ChangementEtatQuestion;
import fr.sword.xsd.reponses.ChangerEtatQuestionsRequest;
import fr.sword.xsd.reponses.ChangerEtatQuestionsResponse;
import fr.sword.xsd.reponses.EtatQuestion;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.QuestionType;
import fr.sword.xsd.reponses.ResultatTraitement;

/**
 * Test de changement d'état
 * @author jgomez
 *
 */
public class Test13ChangementEtat extends AbstractWebTest{

	
	@WebTest(description="1.2.9 Changement d'état - caduque", order =10)
	@TestDocumentation(categories={"WS", "Assemblées", "Dossier"})
	public void changementEtatCaduque() throws Exception{
		verifierChangementEtat(EtatQuestion.CADUQUE, ConstantesLotQuestion.DOSSIER_TEST_CHANGEMENT_ETAT_CADUQUE);
	}

	@WebTest(description="1.2.9 Changement d'état - clos autre", order =20)
	@TestDocumentation(categories={"WS", "Assemblées", "Dossier"})
	public void changementEtatClosAutre() throws Exception{
		verifierChangementEtat(EtatQuestion.CLOTURE_AUTRE, ConstantesLotQuestion.DOSSIER_TEST_CHANGEMENT_ETAT_CLOS_AUTRE);
	}
	
	@WebTest(description="1.2.9 Changement d'état - signalé", order =30)
	@TestDocumentation(categories={"WS", "Assemblées", "Dossier"})
	public void changementEtatSignale() throws Exception{
		verifierChangementEtat(EtatQuestion.SIGNALE, ConstantesLotQuestion.DOSSIER_TEST_CHANGEMENT_ETAT_SIGNALE);
	}
	
	@WebTest(description="1.2.9 Changement d'état - retiré", order =40)
	@TestDocumentation(categories={"WS", "Assemblées", "Dossier"})
	public void changementEtat() throws Exception{
		verifierChangementEtat(EtatQuestion.RETIRE, ConstantesLotQuestion.DOSSIER_TEST_CHANGEMENT_ETAT_RETIRE);
	}
	
	private void verifierChangementEtat( EtatQuestion etatQuestion, String nomDossier) throws WSProxyFactoryException,
			Exception {
		WSQuestion service = WsUtils.getWSQuestionAN();
		ChangerEtatQuestionsRequest request = new ChangerEtatQuestionsRequest();
		ChangementEtatQuestion changementEtat = new ChangementEtatQuestion();
		changementEtat.setIdQuestion(getIdQuestion(nomDossier));
		changementEtat.setTypeModif(etatQuestion);
		changementEtat.setDateModif(DatatypeFactory.newInstance().newXMLGregorianCalendar( new GregorianCalendar()));
		request.getNouvelEtat().add(changementEtat);
		ChangerEtatQuestionsResponse response = service.changerEtatQuestions(request);
		Assert.assertNotNull(response.getResultatTraitement());
		Assert.assertEquals(1, response.getResultatTraitement().size());
		for ( ResultatTraitement resultat : response.getResultatTraitement()){
			Assert.assertTrue("Le changement d'état " + etatQuestion + " a échoué", TraitementStatut.OK.toString().equals(resultat.getStatut().toString()));
		}
	}
	
	private QuestionId getIdQuestion(String nomQuestion) {
		String[] tabSplit = nomQuestion.split(" ");
		String source = tabSplit[0];
		String numeroQuestion = tabSplit[1];
		QuestionId questionId = new QuestionId();
		questionId.setLegislature(ConstantesLotQuestion.LEGISLATURE);
		questionId.setNumeroQuestion(Integer.valueOf(numeroQuestion));
		questionId.setSource(QuestionSource.valueOf(source));
		questionId.setType(QuestionType.QE);
		return questionId;
	}
	
	
}
