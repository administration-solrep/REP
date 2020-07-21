package fr.dila.reponses.webtest.documentation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import fr.dila.reponses.webtest.tests.webdriver10.*;
import fr.dila.st.annotations.AbstractGenerateDocTests;

public class ReponsesGenerateDocTests extends AbstractGenerateDocTests {
	
	public static void main(String[] args) {		
		categoriesAndTestsMap = new TreeMap<String, List<String>>();
		
		for (Class clazz : getAllTestsClasses()) {
			updateMapWithTestClass(clazz);
		}

		StringBuilder html = generateHtml("REPONSES");
		
		File htmlFile = new File ("DocumentationTestsReponses.html");
		
		try {
			FileUtils.writeStringToFile(htmlFile, html.toString());
		} catch (IOException e) {
			System.out.println(e);
		}
		System.out.println("Génération terminée");
	}

	private static List<Class> getAllTestsClasses() {
		List<Class> classes = new ArrayList<Class>();
		
		classes.add(Test01LoginPage.class);
		classes.add(Test02GestionOrga.class);
		classes.add(Test03ModeleFdr.class);
		classes.add(Test04Question.class);
		classes.add(Test05PlanClassement.class);
		classes.add(Test06RechercheQuestion.class);
		classes.add(Test07TraitementQuestions.class);
		classes.add(Test08ErratumQuestion.class);
		classes.add(Test09ErratumReponse.class);
		classes.add(Test10Reattribution.class);
		classes.add(Test11Reorientation.class);
		classes.add(Test12Arbitrage.class);
		classes.add(Test13ChangementEtat.class);
		classes.add(Test14MiseEnAttente.class);
		classes.add(Test15RenvoiValidationPM.class);
		classes.add(Test16FavorisPlanClassement.class);
		classes.add(Test17Suivi.class);
		classes.add(Test18Modification.class);
		classes.add(Test19Allotissement.class);
		classes.add(Test20Profil.class);
		classes.add(Test21RechercheUtilisateur.class);
		classes.add(Test22DelegationDroits.class);
		classes.add(Test23InjectionQuestionReferentiel.class);
		classes.add(Test24Webservice.class);
		classes.add(Test25AccesRestreint.class);
		
		return classes;
	}
	
}
