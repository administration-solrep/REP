package fr.dila.reponses.rest.validator;

import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contient un ensemble de test pour validité un appel au service web
 * EnvoyerReponses
 *
 * @author spesnel
 *
 */
public class EnvoyerReponseValidator {
    private static final Log log = LogFactory.getLog(EnvoyerReponseValidator.class);

    /**
     * instance
     */
    private static EnvoyerReponseValidator validatorInstance;

    private static String ERROR_UNAUTHORIZED_TAG_MSG_FMT = "Tag non autorisé dans le corps de la réponse [%s]";

    /**
     * Authorized HTML tags
     */
    private Set<String> okTags;

    private EnvoyerReponseValidator() {
        okTags = new HashSet<>();
        final ConfigService configService = STServiceLocator.getConfigService();
        String authorizedTagsStr = configService.getValue(
            ReponsesConfigConstant.VALIDATE_REPONSE_AUTHORIZED_TAGS_PARAMETER_NAME
        );
        if (authorizedTagsStr == null || authorizedTagsStr.isEmpty()) {
            log.warn("No authorized tag for reponse content");
        } else {
            log.warn("Authorized tags for reponse content : " + authorizedTagsStr);
            String[] authorizedTags = authorizedTagsStr.split(",");
            for (String tag : authorizedTags) {
                if (tag != null) {
                    tag = tag.trim();
                    if (!tag.isEmpty()) {
                        okTags.add(tag);
                    }
                }
            }
        }
    }

    public static EnvoyerReponseValidator getInstance() {
        if (validatorInstance == null) {
            validatorInstance = new EnvoyerReponseValidator();
        }
        return validatorInstance;
    }

    /**
     * Verifie la presence de tag html non autorisé dans la reponses
     * @param reponses
     * @return
     */
    public ValidatorResult validateReponseContent(String reponses) {
        TextTagAnalyser analyser = new TextTagAnalyser();
        if (!analyser.checkTags(reponses, okTags)) {
            final String tag = analyser.getNoConformTag();
            final String msg = String.format(ERROR_UNAUTHORIZED_TAG_MSG_FMT, tag);
            return ValidatorResult.error(msg);
        } else {
            return ValidatorResult.RESULT_OK;
        }
    }
}
