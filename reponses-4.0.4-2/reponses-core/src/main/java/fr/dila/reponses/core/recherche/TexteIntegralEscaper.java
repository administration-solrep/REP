package fr.dila.reponses.core.recherche;

import org.nuxeo.ecm.core.search.api.client.querymodel.Escaper;

/**
 *  Un escaper qui ne fait rien pour les -. Ne sera pas nécessaire dans une prochaine version de Nuxeo.
 *  Voir SUPNXP-3187
 * @author jgomez
 *
 */

public class TexteIntegralEscaper implements Escaper {

    /**
     * Default constructor
     */
    public TexteIntegralEscaper() {
        // do nothing
    }

    public String escape(String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'') {
                sb.append("\\\\'");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
