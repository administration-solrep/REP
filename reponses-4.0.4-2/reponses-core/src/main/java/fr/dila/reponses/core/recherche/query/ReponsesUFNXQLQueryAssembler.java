package fr.dila.reponses.core.recherche.query;

import fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler;

public class ReponsesUFNXQLQueryAssembler extends UFNXQLQueryAssembler {

    public ReponsesUFNXQLQueryAssembler() {
        super();
    }

    public String getAllResultsQuery() {
        return correspondences.get(0).getQueryPart();
    }
}
