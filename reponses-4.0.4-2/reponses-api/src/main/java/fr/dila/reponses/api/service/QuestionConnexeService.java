package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

public interface QuestionConnexeService {
    public enum HashTarget {
        TITLE,
        TEXTE,
        INDEXATION_SE,
        INDEXATION_AN
    }

    String getHash(Question question, HashTarget hashTarget, CoreSession session) throws NoSuchAlgorithmException;

    Map<String, List<String>> getMinisteresMap(Question question, CoreSession session);

    Reponse getReponse(Question question, CoreSession session);

    List<String> getMinisteresDossiersConnexe(Question question, String ministereID, CoreSession session);
}
