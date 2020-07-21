package fr.dila.reponses.webtest.utils;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import fr.dila.reponses.rest.api.WSAttribution;
import fr.dila.reponses.rest.api.WSControle;
import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.reponses.rest.api.WSReponse;
import fr.dila.reponses.webtest.helper.UrlHelper;
import fr.dila.st.rest.client.WSProxyFactory;
import fr.dila.st.rest.client.WSProxyFactoryException;
import fr.dila.st.rest.helper.JaxBHelper;

/**
 * Classe qui gère l'accès aux webservices
 * @author user
 *
 */
public class WsUtils {

	public static final String STATUS_OK = "OK";
	
	public static final String ENDPOINT = UrlHelper.DEFAULT_APP_URL;
	
	public static final String BASEPATH = "reponses/site/reponses";
	
	// Identifiants an
	public static final String WS_AN_USERNAME = "ws_an";
	public static final String WS_AN_PASSWORD = "ws_an";
	
	// Identifiants sénat
	public static final String WS_SENAT_USERNAME = "ws_senat";
	public static final String WS_SENAT_PASSWORD = "ws_senat";
	
	// Identifiants dila : Pas d'utilisateur DILA pour l'instant
	public static final String WS_DILA_USERNAME = WS_AN_USERNAME;
	public static final String WS_DILA_PASSWORD = WS_AN_PASSWORD;
	
	// Identifiants ws ministériel
	public static final String WS_MIN_USERNAME = "ws_eco";
	public static final String WS_MIN_PASSWORD = "ws_eco";
	
	
	public static WSReponse getWSReponses() throws WSProxyFactoryException{
		WSProxyFactory proxyFactory = new WSProxyFactory(ENDPOINT, BASEPATH, WS_AN_USERNAME, WS_AN_PASSWORD, null);
		WSReponse service = proxyFactory.getService(WSReponse.class);
		return service;
	}

	public static WSReponse getWSReponsesMinEco() throws WSProxyFactoryException{
		WSProxyFactory proxyFactory = new WSProxyFactory(ENDPOINT, BASEPATH, WS_MIN_USERNAME, WS_MIN_PASSWORD, null);
		WSReponse service = proxyFactory.getService(WSReponse.class);
		return service;
	}
	
	public static WSQuestion getWSQuestionAN() throws WSProxyFactoryException {
		return getWSQuestion(WS_AN_USERNAME, WS_AN_PASSWORD);
	}
	
	public static WSQuestion getWSQuestionSenat() throws WSProxyFactoryException {
		return getWSQuestion(WS_SENAT_USERNAME, WS_SENAT_PASSWORD);
	}
	
	public static WSQuestion getWSQuestion(String username, String password) throws WSProxyFactoryException {
		return getWebservice(username, password, WSQuestion.class);
	}
	
	public static <T> T getWebservice(String username, String password, Class<T> clazz) throws WSProxyFactoryException {
		WSProxyFactory proxyFactory = new WSProxyFactory(ENDPOINT, BASEPATH, username, password, null);
		T service = proxyFactory.getService(clazz);
		return service;
	}
	
	public static <T> T buildRequestFromFile(InputStream inputStream, Class<T> clazz) throws JAXBException {
		T request = JaxBHelper.unmarshall(inputStream, clazz);
		return request;
	}

	public static WSControle getWSControle() throws WSProxyFactoryException {
		WSProxyFactory proxyFactory = new WSProxyFactory(ENDPOINT, BASEPATH, WS_DILA_USERNAME, WS_DILA_PASSWORD, null);
		WSControle service = proxyFactory.getService(WSControle.class);
		return service;
	}
	

	public static WSAttribution getWsAttribution(String username, String password) throws WSProxyFactoryException{
		return getWebservice(username, password, WSAttribution.class);
	}

	
}
