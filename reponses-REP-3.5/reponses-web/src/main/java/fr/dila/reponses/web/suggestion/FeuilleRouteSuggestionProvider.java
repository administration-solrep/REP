package fr.dila.reponses.web.suggestion;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.st.core.query.QueryUtils;

@Name("feuilleRouteProvider")
@Scope(ScopeType.CONVERSATION)
public class FeuilleRouteSuggestionProvider implements ISuggestionProvider, Serializable {
    private static final long MAX_RESULTS = 10;

    private static final long serialVersionUID = 1L;
    @In(create = true, required = true)
    protected transient CoreSession documentManager;
    
    private static final String NAME = "FEUILLE_DE_ROUTE_PROVIDER";
    @Override
    public String getName() {        
        return NAME;
    }

    @Override
    public List<String> getSuggestions(Object input) throws ClientException, Exception {
        String search = "%"
            + Normalizer.normalize(input.toString(), Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toUpperCase()
            + "%";
                
        StringBuilder request = new StringBuilder("select distinct dublincore.title " +
                "from feuille_route, dublincore where feuille_route.id = dublincore.id and feuille_route.typecreation is null " +
                " and TRANSLATE(UPPER(dublincore.title), 'ÉÈÊÀÁÂÄÇÌÍÎÏÑÓÒÔÖÚÙÛÜ', 'EEEAAAACIIIINOOOOUUUU') LIKE ?");
        
        List<String> out = new ArrayList<String>();
        
        final String colname = "dc:title";
        IterableQueryResult res = null;
        try {
            res = QueryUtils.doSqlQuery(documentManager, new String[]{colname}, 
                    request.toString(), new Object[]{search},
                    MAX_RESULTS, 0);
            Iterator<Map<String, Serializable>> it = res.iterator();
            while(it.hasNext()){
                Map<String, Serializable> row = it.next();
                out.add((String) row.get(colname));
            }
        } finally {
            if (res != null) {
                res.close();
            }
        }
                
        return out;
    }
}
