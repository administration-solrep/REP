import java.util.HashMap;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Script groovy pour mettre a jour les minister id de l'etape a partir du ministere label
 * 
 */

print "Début script groovy de remplissage de ministere Id pour les etapes de la feuille de route";
print "-------------------------------------------------------------------------------";

    String query = "SELECT d.ecm:uuid as id FROM DocumentRouteStep as d WHERE d.rtsk:ministereId is null and d.rtsk:ministereLabel is not null and d.rtsk:dateDebutEtape > DATE '2012-05-23' ";
    
    List<OrganigrammeNode> minList = STServiceLocator.getOrganigrammeService().getMinisteres(Boolean.TRUE);
    HashMap<String, String> minMap = new HashMap<String, String>();
    for (OrganigrammeNode min : minList) {
        minMap.put(min.getLabel(), min.getId());
    }

    Long count = QueryUtils.doCountQuery(Session, QueryUtils.ufnxqlToFnxqlQuery(query), null);
    print count + " étapes trouvees";
    long limit = (long) 100;

    for (Long offset = (long) 0; offset <= count; offset += limit) {
        Long borne = (count<offset+limit?count:offset+limit);
        print "Récupération étapes de " + offset + " à " + borne;
        List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(Session, query, null, limit, 0);
        for (DocumentModel doc : list) {
            STRouteStep step = doc.getAdapter(STRouteStep.class);
            String label = step.getMinistereLabel();
            if (minMap.get(label) != null) {
                step.setMinistereId(minMap.get(label));
                Session.saveDocument(step.getDocument());
            }
        }
        print "Fin traitement étapes de " + offset + " à " + borne;
    }
    
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de remplissage de ministere Id pour les etapes de la feuille de route";
return "Fin du script groovy";
