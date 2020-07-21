package fr.dila.reponses.webtest.utils;

import fr.dila.reponses.rest.api.WSAttribution;
import fr.sword.xsd.reponses.ChercherAttributionsDateRequest;
import fr.sword.xsd.reponses.ChercherAttributionsDateResponse;
import fr.sword.xsd.reponses.ChercherAttributionsRequest;
import fr.sword.xsd.reponses.ChercherAttributionsResponse;
import fr.sword.xsd.reponses.QuestionId;

/**
 * Permet d'accéder au service d'attribution
 * @author jgomez
 *
 */
public class AttributionUtils {

	/**
	 * Envoi une requête chercherAttribution
	 * @param login
	 * @param password
	 * @param idDossier
	 * @return
	 * @throws Exception 
	 */
	public static ChercherAttributionsResponse chercherAttributions(String login, String password, QuestionId questionId) throws Exception {
		WSAttribution wsAttribution = WsUtils.getWsAttribution(login, password);
		ChercherAttributionsRequest request = new ChercherAttributionsRequest();
		request.getIdQuestions().add(questionId);
		ChercherAttributionsResponse reponses = wsAttribution.chercherAttributions(request);
		return reponses;
	}
	
	/**
	 * Envoi une requête chercherAttributionDate
	 * @param login
	 * @param password
	 * @param idDossier
	 * @return
	 * @throws Exception 
	 */
	public static ChercherAttributionsDateResponse chercherAttributionsDate(String login, String password, QuestionId questionId) throws Exception {
		WSAttribution wsAttribution = WsUtils.getWsAttribution(login, password);
		ChercherAttributionsDateRequest request = new ChercherAttributionsDateRequest();
		request.getIdQuestions().add(questionId);
		//request.setJeton("0");
		ChercherAttributionsDateResponse reponses = wsAttribution.chercherAttributionsDate(request);
		return reponses;
	}

}
