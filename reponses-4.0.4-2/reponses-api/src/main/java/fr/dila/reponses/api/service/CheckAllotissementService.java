package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.exception.CheckAllotissementException;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;

public interface CheckAllotissementService {
    void checkQuestionCanBeAllotie(Question selectedQuestion, CoreSession session) throws CheckAllotissementException;

    void checkQuestionEligibilityInLot(Question question, boolean update, CoreSession session)
        throws CheckAllotissementException;

    void checkListQuestionIsNotEmpty(Set<Question> listQuestions) throws CheckAllotissementException;

    List<String> checkLotCoherence(
        CoreSession session,
        Question questionDirectrice,
        Set<Question> listQuestion,
        boolean updateMode
    );
}
