package fr.dila.reponses.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.extraction.Question;

/**
 * Service d'extraction des questions
 *
 */
public interface ExtractionService extends Serializable {
	
	/**
	 * Extrait les questions de la base de données
	 * @param session
	 * @param typeQuestion type de question (QE, QO, QOSD...)
	 * @param origineQuestion origine des questions à récupérer (AN ou SENAT)	 
	 * @return
	 * @throws ClientException
	 */
	List<Question> extractQuestionsOuvertes(final CoreSession session, final String typeQuestion, final String origineQuestion);
	
	/**
	 * Extrait les questions de la base de données
	 * @param session
	 * @param typeQuestion type de question (QE, QO, QOSD...)
	 * @param origineQuestion origine des questions à récupérer (AN ou SENAT)	 
	 * @return
	 * @throws ClientException
	 */
	List<Question> extractQuestionsCloses(final CoreSession session, final String typeQuestion, final String origineQuestion);
	
}
