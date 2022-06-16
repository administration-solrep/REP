package fr.dila.reponses.core.notification;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_ANALYSE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RENVOI;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.TA_RUBRIQUE;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;
import static fr.dila.st.api.constant.STVocabularyConstants.XVOCABULARY;

import fr.dila.reponses.api.cases.Question;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.QuestionType;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.runtime.api.Framework;

/**
 * Classe utilitaire concernant les webservices
 * @author bgamard
 *
 */
public class WsUtils {

    /**
     * Retourne un objet QuestionId depuis un objet Question.
     * @param question
     * @return QuestionId
     */
    public static QuestionId getQuestionIdFromQuestion(Question question) {
        // IdQuestion
        // ///////////////////
        QuestionId questionId = new QuestionId();
        Long legislature = question.getLegislatureQuestion();
        questionId.setLegislature((legislature != null) ? legislature.intValue() : 0);

        Long numeroQuestion = question.getNumeroQuestion();
        questionId.setNumeroQuestion((numeroQuestion != null) ? numeroQuestion.intValue() : 0);
        String origineQuestion = question.getOrigineQuestion();
        if ("AN".equals(origineQuestion)) {
            questionId.setSource(QuestionSource.AN);
        } else if ("SENAT".equals(origineQuestion)) {
            questionId.setSource(QuestionSource.SENAT);
        }
        String typeQuestion = question.getTypeQuestion();
        if (("QE").equals(typeQuestion)) {
            questionId.setType(QuestionType.QE);
        } else if ("QOSD".equals(typeQuestion)) {
            questionId.setType(QuestionType.QOSD);
        } else if ("QOAD".equals(typeQuestion)) {
            questionId.setType(QuestionType.QOAD);
        } else if ("QOAE".equals(typeQuestion)) {
            questionId.setType(QuestionType.QOAE);
        } else if ("QG".equals(typeQuestion)) {
            questionId.setType(QuestionType.QG);
        } else if ("QC".equals(typeQuestion)) {
            questionId.setType(QuestionType.QC);
        }
        return questionId;
    }

    /**
     * Ajoute les entrées dans les vocabulaires d'indexation n'existant pas déjà.
     * @param question
     */
    public static void synchronizeVocabulary(Question question) {
        VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
        Framework.doPrivileged(() -> createEntriesIfNotExist(question, vocabularyService));
    }

    private static void createEntriesIfNotExist(Question question, VocabularyService vocabularyService) {
        if (question.getAssNatAnalyses() != null) {
            question
                .getAssNatAnalyses()
                .forEach(id -> vocabularyService.createEntryIfNotExists(VOCABULARY, AN_ANALYSE.getValue(), id));
        }

        if (question.getAssNatRubrique() != null) {
            question
                .getAssNatRubrique()
                .forEach(id -> vocabularyService.createEntryIfNotExists(VOCABULARY, AN_RUBRIQUE.getValue(), id));
        }

        if (question.getAssNatTeteAnalyse() != null) {
            question
                .getAssNatTeteAnalyse()
                .forEach(id -> vocabularyService.createEntryIfNotExists(XVOCABULARY, TA_RUBRIQUE.getValue(), id));
        }

        if (question.getSenatQuestionRenvois() != null) {
            question
                .getSenatQuestionRenvois()
                .forEach(id -> vocabularyService.createEntryIfNotExists(VOCABULARY, SE_RENVOI.getValue(), id));
        }

        if (question.getSenatQuestionRubrique() != null) {
            question
                .getSenatQuestionRubrique()
                .forEach(id -> vocabularyService.createEntryIfNotExists(VOCABULARY, SE_RUBRIQUE.getValue(), id));
        }

        if (question.getSenatQuestionThemes() != null) {
            question
                .getSenatQuestionThemes()
                .forEach(id -> vocabularyService.createEntryIfNotExists(VOCABULARY, SE_THEME.getValue(), id));
        }
    }

    /**
     * Déduplique les entrées d'indexationn.
     * @param in
     * @return
     */
    public static List<String> deduplicateIndexation(List<String> in) {
        if (in == null) {
            return null;
        }

        List<String> out = new ArrayList<>();
        for (String s : in) {
            if (!out.contains(s)) {
                out.add(s);
            }
        }
        return out;
    }
}
