package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.cases.Question;

/**
 * Service permettant de gérer les jetons de l'application Réponses.
 * 
 * @author jtremeaux
 */
public interface JetonService extends fr.dila.st.api.service.JetonService {
    void createJetonTransmissionsAssemblees(CoreSession session, Question question, String webService) throws ClientException;
}
