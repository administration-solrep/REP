package fr.dila.reponses.core.requeteur;

import static fr.dila.st.core.service.RequeteurServiceImpl.CONTRIBUTOR;

import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.requeteur.RequeteurFunctionSolver;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Permet d'ajouter une contrainte relative au ministère du principal courant, qui doit correspondre à une certaine valeur (par exemple le ministère attributaire du dossier). Usage : ufnxql_current_ministere(d.dos.ministereAttributaireCourant)
 *
 * @author jgomez
 *
 */
public class KeywordMinistereAttributaireResolver implements RequeteurFunctionSolver {
    // Le mot-clé de la fonction
    private static final String MINISTERE_KEYWORD = "ufnxql_ministere:";

    private static final Log LOGGER = LogFactory.getLog(KeywordMinistereAttributaireResolver.class);

    private static final String ALWAYS_TRUE_CLAUSE = "q." + STSchemaConstant.ECM_UUID_XPATH + " != 'a'";

    protected String expr;

    protected Map<String, Object> env;

    public KeywordMinistereAttributaireResolver() {
        super();
    }

    @Override
    public String getExpr() {
        return expr;
    }

    @Override
    public void setExpr(final String expr) {
        this.expr = expr;
    }

    @Override
    public Map<String, Object> getEnv() {
        return env;
    }

    @Override
    public void setEnv(final Map<String, Object> env) {
        this.env = env;
    }

    /**
     * Retourne la requête avec le ministère courant substitué.
     *
     * @return la clause avec le ministère courant.
     *
     */
    @Override
    public String solve(final CoreSession session) {
        if (session == null || !(session.getPrincipal() instanceof SSPrincipal)) {
            LOGGER.error(
                "Ce solveur a besoin d'avoir une session non nulle et un principal de type SSPrincipal, session = " +
                session
            );
            LOGGER.error("Ne fait rien");
            return ALWAYS_TRUE_CLAUSE;
        }
        final SSPrincipal principal = getPrincipal(session);
        if (principal == null) {
            LOGGER.error("Le principal est nul");
            return ALWAYS_TRUE_CLAUSE;
        }
        // Si l'utilisateur a le droit ssg reader, on renvoie une clause toujours vraie HACK !
        // Le but est que certains utilisateurs puissent voir toutes les questions.
        // Pour l'instant, c'est le même droit que celui donné à l'adminsgg pour qu'ils puissent voir
        // toutes les corbeilles.
        if (
            principal.isMemberOf(ReponsesBaseFunctionConstant.CORBEILLE_SGG_READER) ||
            principal.isMemberOf(ReponsesBaseFunctionConstant.ALL_CORBEILLE_READER)
        ) {
            return ALWAYS_TRUE_CLAUSE;
        }
        final String ministereKey = expr.replaceAll("\\(", "").replaceAll("\\)", "");
        final Set<String> ministereIds = principal.getMinistereIdSet();
        if (ministereIds != null && !ministereIds.isEmpty()) {
            return ministereKey + " IN (" + StringUtil.join(principal.getMinistereIdSet(), ",", "'") + ")";
        } else {
            LOGGER.error("La liste des ministères de l'utilisateur est vide, on renvoie tous les dossiers");
            return ALWAYS_TRUE_CLAUSE;
        }
    }

    protected SSPrincipal getPrincipal(final CoreSession session) {
        final UserManager userManager = STServiceLocator.getUserManager();
        SSPrincipal principal = null;
        if (env != null && env.containsKey(CONTRIBUTOR)) {
            try {
                principal = (SSPrincipal) userManager.getPrincipal((String) env.get(CONTRIBUTOR));
            } catch (final NuxeoException e) {
                LOGGER.error(
                    "Le gestionnaire du mot-clé spécial ministere_attributaire n'a pas pu récupérer le principal de l'utilisateur",
                    e
                );
            }
        } else {
            principal = (SSPrincipal) session.getPrincipal();
        }
        return principal;
    }

    /**
     * Renvoie le pattern qui définit la fonction ministère.
     *
     * @return
     */
    @Override
    public Pattern getPattern() {
        final Pattern pattern = Pattern.compile(MINISTERE_KEYWORD + "\\((.*?)\\)");
        return pattern;
    }

    /**
     * Renvoie une chaîne de caractère à repérer dans la requête.
     *
     * @param les arguments de la fonction
     * @return l'expression a repérer
     */
    @Override
    public String getExpressionToBeMatched(final String groupStr) {
        return MINISTERE_KEYWORD + "(" + groupStr + ")";
    }
}
