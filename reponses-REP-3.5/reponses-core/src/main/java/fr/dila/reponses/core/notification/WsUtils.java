package fr.dila.reponses.core.notification;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.QuestionType;

/**
 * Classe utilitaire concernant les webservices
 * @author bgamard
 *
 */
public class WsUtils {
    
    /**
     * Retourne un objet QuestionId depuis un objet Question.
     * @param Question
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
     * @throws ClientException 
     */
    public static void synchronizeVocabulary(Question question) throws ClientException {
        final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
        
        if (question.getAssNatAnalyses() != null) {
            for (String id : question.getAssNatAnalyses()) {
                vocabularyService.createEntryIfNotExists(STVocabularyConstants.VOCABULARY, VocabularyConstants.AN_ANALYSE, id);
            }
        }
        
        if (question.getAssNatRubrique() != null) {
            for (String id : question.getAssNatRubrique()) {
                vocabularyService.createEntryIfNotExists(STVocabularyConstants.VOCABULARY, VocabularyConstants.AN_RUBRIQUE, id);
            }
        }

        if (question.getAssNatTeteAnalyse() != null) {
            for (String id : question.getAssNatTeteAnalyse()) {
                vocabularyService.createEntryIfNotExists(STVocabularyConstants.XVOCABULARY, VocabularyConstants.TA_RUBRIQUE, id);
            }
        }
        
        if (question.getSenatQuestionRenvois() != null) {
            for (String id : question.getSenatQuestionRenvois()) {
                vocabularyService.createEntryIfNotExists(STVocabularyConstants.VOCABULARY, VocabularyConstants.SE_RENVOI, id);
            }
        }

        if (question.getSenatQuestionRubrique() != null) {
            for (String id : question.getSenatQuestionRubrique()) {
                vocabularyService.createEntryIfNotExists(STVocabularyConstants.VOCABULARY, VocabularyConstants.SE_RUBRIQUE, id);
            }
        }

        if (question.getSenatQuestionThemes() != null) {
            for (String id : question.getSenatQuestionThemes()) {
                vocabularyService.createEntryIfNotExists(STVocabularyConstants.VOCABULARY, VocabularyConstants.SE_THEME, id);
            }
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
        
        List<String> out = new ArrayList<String>();
        for (String s : in) {
            if (!out.contains(s)) {
                out.add(s);
            }
        }
        return out;
    }
}
