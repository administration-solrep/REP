package fr.dila.reponses.api.service;

import fr.dila.reponses.api.extraction.Question;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

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
     *
     */
    List<Question> extractQuestionsOuvertes(
        final CoreSession session,
        final String typeQuestion,
        final String origineQuestion
    );

    /**
     * Extrait les questions de la base de données
     * @param session
     * @param typeQuestion type de question (QE, QO, QOSD...)
     * @param origineQuestion origine des questions à récupérer (AN ou SENAT)
     * @return
     *
     */
    List<Question> extractQuestionsCloses(
        final CoreSession session,
        final String typeQuestion,
        final String origineQuestion
    );
}
