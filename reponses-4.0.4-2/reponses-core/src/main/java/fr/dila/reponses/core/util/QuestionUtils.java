package fr.dila.reponses.core.util;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.core.flux.DelaiCalculateur;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;

public final class QuestionUtils {
    private static final STLogger LOGGER = STLogFactory.getLog(QuestionUtils.class);

    private static final String MOTS_CLEFS_SEPARATOR = ", ";

    private QuestionUtils() {}

    public static String joinMotsClefs(Question question) {
        return Optional
            .ofNullable(question)
            .map(quest -> StringUtils.join(quest.getMotsClef(), MOTS_CLEFS_SEPARATOR))
            .orElse(null);
    }

    public static String getDelaiExpiration(CoreSession session, Question question) {
        String delaiExpirationFdr = "";
        STParametreService paramService = STServiceLocator.getSTParametreService();

        try {
            int dureeTraitement = Integer.parseInt(
                paramService.getParametreValue(session, STParametreConstant.QUESTION_DUREE_TRAITEMENT)
            );

            delaiExpirationFdr =
                DelaiCalculateur.computeDelaiExpirationFdr(question, question.getEtatQuestionSimple(), dureeTraitement);
        } catch (NumberFormatException e) {
            LOGGER.warn(STLogEnumImpl.LOG_EXCEPTION_TEC, e);
        }

        return delaiExpirationFdr;
    }
}
