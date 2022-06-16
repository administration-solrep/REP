package fr.dila.reponses.core.service;

import fr.dila.reponses.api.service.ControleService;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Implementation de {@link ControleService}
 *
 * @author asatre
 *
 */
public class ControleServiceImpl implements ControleService {
    private static final Log LOGGER = LogFactory.getLog(ControleServiceImpl.class);

    private static final Pattern HTML_TAG = Pattern.compile("<(.*?)>");
    private static final Pattern HTML_TEXT = Pattern.compile("&[^&.]+;");
    private static final Pattern LETTER_NUMBER = Pattern.compile("[^a-z0-9]");

    /**
     * Default constructor
     */
    public ControleServiceImpl() {
        // do notinhg
    }

    @Override
    public boolean compareReponses(final String reponse1, final String reponse2) {
        if (StringUtils.isBlank(reponse1) && StringUtils.isNotBlank(reponse2)) {
            return false;
        }

        if (StringUtils.isNotBlank(reponse1) && StringUtils.isBlank(reponse2)) {
            return false;
        }

        final String rep1 = prepareText(reponse1);

        final String rep2 = prepareText(reponse2);

        final Boolean result = rep1.equals(rep2);

        if (!result && LOGGER.isDebugEnabled()) {
            LOGGER.debug("compareReponses fail");
            LOGGER.debug(rep1);
            LOGGER.debug(rep2);
        }

        return result;
    }

    private String prepareText(final String text) {
        // lower case
        String result = text.toLowerCase(Locale.FRENCH);
        // replace html char
        result = StringEscapeUtils.unescapeHtml4(result);
        // replace accent
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        // remove html tag
        result = cleanWithPattern(result, HTML_TAG);
        // remove html char
        result = cleanWithPattern(result, HTML_TEXT);
        // remove all non number and non letter
        result = cleanWithPattern(result, LETTER_NUMBER);
        // trim
        return result.trim();
    }

    private String cleanWithPattern(String value, final Pattern pattern) {
        final Set<String> extracted = new HashSet<>();
        final Matcher htmlMatcher = pattern.matcher(value);
        while (htmlMatcher.find()) {
            extracted.add(htmlMatcher.group());
        }

        for (final String val : extracted) {
            value = value.replace(val, "");
        }

        return value;
    }
}
