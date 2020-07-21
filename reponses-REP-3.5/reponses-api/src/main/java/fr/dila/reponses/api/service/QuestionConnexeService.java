package fr.dila.reponses.api.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;

public interface QuestionConnexeService {

	public enum HashTarget {
		TITLE, TEXTE, INDEXATION_SE, INDEXATION_AN
	}

	String getHash(Question question, HashTarget hashTarget, CoreSession session)
			throws NoSuchAlgorithmException, ClientException;

	Map<String, List<String>> getMinisteresMap(Question question, CoreSession session) throws ClientException;

	Reponse getReponse(Question question, CoreSession session) throws ClientException;


}
