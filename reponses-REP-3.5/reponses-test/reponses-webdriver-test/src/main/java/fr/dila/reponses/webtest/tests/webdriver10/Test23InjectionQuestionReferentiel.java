package fr.dila.reponses.webtest.tests.webdriver10;

import javax.xml.bind.JAXBException;

import org.junit.Ignore;

import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Injection des questions de la production pour avoir une base de questions diverse
 * @author user
 *
 */
public class Test23InjectionQuestionReferentiel extends AbstractWebTest{
	
	
	private static final String BASEPATH_INJECTION_SENAT = "/injection/senat/";
	
	private static final String SUFFIX_INJECTION_SENAT = "_injection_question_senat.xml";
	
	private static final String BASEPATH_INJECTION_AN = "/injection/an/";
	
	private static final String SUFFIX_INJECTION_AN = "_injection_question_an.xml";

	private static final String BASEPATH_INJECTION_RAPPEL = "/injection/rappel/";
	
	private static final String SUFFIX_INJECTION_RAPPEL = "_injection_question_rappel.xml";
	
	private static final String BASEPATH_INJECTION_ORALE = "/injection/orale/";
	
	private static final String SUFFIX_INJECTION_ORALE = "_injection_question_orale.xml";
	
	@WebTest(description="Injection des questions du sénat pour la partie construction du référentiel", useDriver = false, order =10)
	@TestDocumentation(categories={"WS", "Assemblées"})
	public void testInjectionSenat() throws Exception{
		injectSenat(senatPath("001"));
//		injectSenat(senatPath("002"));
//		injectSenat(senatPath("003"));
	}
	
	@WebTest(description="Injection des questions de l'assemblée national pour la partie construction du référentiel", useDriver = false, order =20)
	@TestDocumentation(categories={"WS", "Assemblées"})
	public void testInjectionAN() throws Exception{
		for (Integer i = 1; i < 19; i++){ 
			String indexWithPadding = String.format("%03d", i);
			injectAN(anPath(indexWithPadding));
		}
	}
	
	@WebTest(description = "Injection des questions de rappel", useDriver = false, order =30)
	@Ignore
	@TestDocumentation(categories={"Test ignoré"})//@TestDocumentation(category={"WS", "Test ignoré"})
	public void testInjectionQuestionRappel() throws JAXBException, Exception{
		// Injection d'une question écrite
		injectSenat(rappelPath("001"));
		injectSenat(rappelPath("002"));
	}
	
	@WebTest(description = "Injection des questions orales", useDriver = false, order =40)
	@TestDocumentation(categories={"WS", "Assemblées"})
	public void testInjectionQuestionOralesSenat() throws JAXBException, Exception{
		// Injection d'une question écrite
		injectSenat(oralePath("001"));
	}
	
	private String senatPath(String numeroFichier) {
		return BASEPATH_INJECTION_SENAT + numeroFichier + SUFFIX_INJECTION_SENAT ;
	} 
	
	private String oralePath(String numeroFichier) {
		return BASEPATH_INJECTION_ORALE + numeroFichier + SUFFIX_INJECTION_ORALE ;
	} 

	private String anPath(String numeroFichier) {
		return BASEPATH_INJECTION_AN + numeroFichier + SUFFIX_INJECTION_AN ;
	} 
	

	private String rappelPath(String numeroFichier) {
		return BASEPATH_INJECTION_RAPPEL + numeroFichier + SUFFIX_INJECTION_RAPPEL ;
	} 
}
