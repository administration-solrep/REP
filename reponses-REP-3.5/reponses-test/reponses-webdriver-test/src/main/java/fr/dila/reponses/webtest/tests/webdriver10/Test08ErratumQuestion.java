package fr.dila.reponses.webtest.tests.webdriver10;

import java.io.InputStream;

import junit.framework.Assert;
import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.utils.WsUtils;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.reponses.EnvoyerQuestionsErrataRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsErrataResponse;
import fr.sword.xsd.reponses.ErratumType;

/**
 * Test de l'erratum d'une question
 * @author user
 *
 */
public class Test08ErratumQuestion extends AbstractWebTest{

	@WebTest(description = "1.2.3 Erratum de la question - envoi d'un erratum de la question")
	@TestDocumentation(categories={"WS", "Assemblées"})
	public void erratumQuestion() throws Exception{
		WSQuestion service = WsUtils.getWSQuestionAN();
		InputStream stream = getClass().getResourceAsStream("/webservice/envoyer_question_errata.xml");
		EnvoyerQuestionsErrataRequest request = WsUtils.buildRequestFromFile(stream, EnvoyerQuestionsErrataRequest.class);
		Assert.assertNotNull("Le fichier de la question n'est pas à l'emplacement indiqué", stream);
		request.getErratum().get(0).setType(ErratumType.ERRATUM);
		EnvoyerQuestionsErrataResponse response = service.envoyerQuestionsErrata(request);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getResultatTraitement());
		Assert.assertEquals(1, response.getResultatTraitement().size());
		Assert.assertEquals(WsUtils.STATUS_OK, response.getResultatTraitement().get(0).getStatut().toString());
	}
	
}
