package fr.dila.reponses.rest.helper;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.wrap;
import static org.nuxeo.ecm.core.query.sql.model.Operator.EQ;

import java.util.List;

public class QueryHelper {

    /**
     * Init nxql query : "SELECT * FROM 'documentType' WHERE "
     * @param documentType
     * @return
     */
    public static StringBuilder initNxqlQuery(final String documentType) {
        return new StringBuilder("SELECT * FROM ").append(documentType).append(" WHERE ");
    }

    /**
     * Update query with list params with 'IN' or '=' operator.
     * @param query
     * @param params
     * @param schema
     * @param property
     * @param beforeAnd : If true,  AND operator added before query part
     * @param quoted : If true, elements are with quotes (ex: 'element')
     */
    public static void appendListParams(
        StringBuilder query,
        List<? extends Object> params,
        String schema,
        String property,
        boolean beforeAnd,
        boolean quoted
    ) {
        if (params != null && !params.isEmpty()) {
            int listSize = params.size();
            if (beforeAnd) {
                query.append(" AND ");
            }
            query.append(schema).append(":").append(property);
            if (listSize == 1) {
                query.append(" = ");
                appendParam(query, params.get(0), quoted);
            } else {
                query.append(" IN (");
                int position = 0;
                for (Object param : params) {
                    appendParam(query, param, quoted);
                    position++;
                    if (position != listSize) {
                        query.append(", ");
                    }
                }
                query.append(") ");
            }
        }
    }

    /**
     * Update query with param with '=' operator
     * @param query
     * @param param
     * @param schema
     * @param property
     * @param beforeAnd : If true,  AND operator added before query part
     * @param quoted : If true, elements are with quotes (ex: 'element')
     */
    public static void appendParam(
        StringBuilder query,
        Object param,
        String schema,
        String property,
        boolean beforeAnd,
        boolean quoted
    ) {
        appendParam(query, param, schema, property, beforeAnd, EQ.toString(), quoted);
    }

    public static void appendParam(
        StringBuilder query,
        Object param,
        String schema,
        String property,
        boolean beforeAnd,
        String operator,
        boolean quoted
    ) {
        if (param != null) {
            if (beforeAnd) {
                query.append(" AND ");
            }
            query.append(schema).append(":").append(property).append(wrap(operator, SPACE));
            appendParam(query, param, quoted);
        }
    }

    private static void appendParam(StringBuilder query, Object param, boolean quoted) {
        if (quoted) {
            query.append("'").append(param).append("'");
        } else {
            query.append(param);
        }
    }
}
