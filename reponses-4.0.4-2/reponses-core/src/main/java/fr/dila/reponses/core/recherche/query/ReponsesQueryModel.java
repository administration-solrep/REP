package fr.dila.reponses.core.recherche.query;

import static fr.dila.reponses.api.constant.RequeteConstants.*;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;

/**
 *
 * Classe utilitaire pour donner la clause WHERE d'un model nuxeo.
 * @author jgomez
 *
 */
public final class ReponsesQueryModel {
    private static final Pattern QUERY_PATTERN = Pattern.compile("SELECT \\* FROM Document( WHERE )?");

    private ReponsesQueryModel() {
        // Classe non instanciable
    }

    /**
     * Retourne les clauses de tous les models donnés en argument, liées par un AND (et avec des parenthèses).
     */
    public static String getAndRequeteParts(DocumentModel model, String... modelNames) {
        PageProviderService pageProviderService = ServiceUtil.getRequiredService(PageProviderService.class);

        return Stream
            .of(modelNames)
            .map(modelName -> getRequetePart(model, modelName, pageProviderService))
            .filter(StringUtils::isNotBlank)
            .collect(joining(" AND "));
    }

    private static String getRequetePart(DocumentModel model, String modelName, PageProviderService service) {
        String requetePart;

        // Cas spécial pour le cas du model RequeteIndexTous qui n'existe pas, mais qui est
        // constitué des models requeteIndex et requeteIndexCompl séparés par un OR.
        if (PART_REQUETE_INDEX_TOUS.equals(modelName)) {
            requetePart =
                Stream
                    .of(
                        getNxqlRequetePart(model, PART_REQUETE_INDEX_ORIGINE, service),
                        getNxqlRequetePart(model, PART_REQUETE_INDEX_COMPL, service)
                    )
                    .filter(StringUtils::isNotBlank)
                    .map(ReponsesQueryModel::surroundWithParentheses)
                    .collect(joining(" OR "));
        } else {
            requetePart = getNxqlRequetePart(model, modelName, service);
            if (PART_REQUETE_SIMPLE.equals(modelName) && StringUtils.isNotBlank(requetePart)) {
                requetePart = surroundWithParentheses(requetePart);
            }
        }

        if (StringUtils.isNotBlank(requetePart)) {
            requetePart = surroundWithParentheses(requetePart);
        }

        return requetePart;
    }

    /**
     * Retourne la clause d'un model Nuxeo.
     */
    private static String getNxqlRequetePart(DocumentModel model, String modelName, PageProviderService service) {
        PageProviderDefinition def = service.getPageProviderDefinition(modelName);
        if (def == null || def.getWhereClause() == null) {
            return EMPTY;
        }

        SortInfo[] sortInfos = def.getSortInfos() != null
            ? def.getSortInfos().toArray(new SortInfo[0])
            : new SortInfo[0];

        String query = NXQLQueryBuilder.getQuery(model, def.getWhereClause(), null, sortInfos);
        return QUERY_PATTERN.matcher(query).replaceAll(EMPTY);
    }

    private static String surroundWithParentheses(String value) {
        return "(" + value + ")";
    }
}
