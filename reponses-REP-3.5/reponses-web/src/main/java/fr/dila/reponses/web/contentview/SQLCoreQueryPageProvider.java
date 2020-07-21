package fr.dila.reponses.web.contentview;

import static org.nuxeo.ecm.core.api.security.SecurityConstants.SYSTEM_USERNAME;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.AbstractSession;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.security.SecurityService;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;

import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.StringUtil;

/**
 * Classe permettant d'utiliser directement du SQL dans la définition des contributions de contentview.
 * Voir suivi-contentviews-contrib.xml pour 3 exemples, concernant les Alertes et les Requêtes.
 * 
 * Certains blocs de code ont été repris de {@link AbstractSession} comme la méthode 'isAdministrator' qui était protected, ou l'affichage des logs.
 * 
 *@author eboussaton
 */
@SuppressWarnings("serial")
public class SQLCoreQueryPageProvider extends CoreQueryDocumentPageProvider {

    private static final Log log = LogFactory.getLog(SQLCoreQueryPageProvider.class);

	@Override
    public List<DocumentModel> getCurrentPage() {
        checkQueryCache();
        if (currentPageDocuments == null) {
            error = null;
            errorMessage = null;
        	CoreSession coreSession = getCoreSession();

            if (query == null) {
                buildQuery(coreSession);
            }
            
			currentPageDocuments = new ArrayList<DocumentModel>();
	        long minMaxPageSize = getMinMaxPageSize();

            try {
                if (log.isDebugEnabled()) {
                    log.debug(String.format(
                            "Perform query for provider '%s': '%s' with pageSize=%s, offset=%s",
                            getName(), query, Long.valueOf(minMaxPageSize),
                            Long.valueOf(offset)));
                }

               IterableQueryResult res = QueryUtils.doSqlQuery(coreSession, new String[] { "id" }, query, new Object[] {});
                
                final ArrayList<DocumentRef> documentsRef = new ArrayList<DocumentRef>();
                for (Map<String, Serializable> row : res) {
                    documentsRef.add(new IdRef((String) row.get(FlexibleQueryMaker.COL_ID)));
                }
                final DocumentModelList docs = new DocumentModelListImpl();
                new UnrestrictedSessionRunner(coreSession) {
					
					@Override
					public void run() throws ClientException {
	                    docs.addAll(session.getDocuments(documentsRef.toArray(new DocumentRef[documentsRef.size()])));
					}
				}.runUnrestricted();
                	
                //DocumentModelList docs = coreSession.query(query, null, minMaxPageSize, offset, true);
                resultsCount = docs.totalSize();
                currentPageDocuments = docs;

                if (log.isDebugEnabled()) {
                    log.debug(String.format(
                            "Performed query for provider '%s': got %s hits",
                            getName(), Long.valueOf(resultsCount)));
                }

            } catch (Exception e) {
                error = e;
                errorMessage = e.getMessage();
                log.warn(e.getMessage(), e);
            }
        }
        return currentPageDocuments;
    }
	
    protected void buildQuery(CoreSession coreSession) {
        // Prepare specific subquery for ACL
        Principal principal = coreSession.getPrincipal();
        String[] principals;
        if (isAdministrator(coreSession)) {
            principals = null; // means: no security check needed
        } else {
            principals = SecurityService.getPrincipalsToCheck(principal);
        }
        String subQuery = buildSubQueryForACL(principals);
        
        // Build the query from all parameters described in *contrib.xml
        SortInfo[] sortArray = null;
        if (sortInfos != null) {
            sortArray = sortInfos.toArray(new SortInfo[] {});
        }
        
        PageProviderDefinition def = getDefinition();
		try {
			query = NXQLQueryBuilder.getQuery(def.getPattern() + " and r.acl_id in (" + subQuery + ")",
			            getParameters(), def.getQuotePatternParameters(),
			            def.getEscapePatternParameters(), sortArray);
			
		} catch (ClientException e1) {
			throw new RuntimeException("Impossible de construire la requête suivante: "+def.getPattern());
		}
    }

    /** Construit la partie de la requête qui va restreindre selon les droits de l'utilisateur connecté **/
	private String buildSubQueryForACL(String[] principals) {
    	String query = "SELECT COLUMN_VALUE FROM TABLE(nx_get_read_acls_for(NX_STRING_TABLE(*ACL*)))";
    	String replacement = StringUtil.join(principals, ",", "'");
    	query = query.replace("*ACL*", replacement);
    	return query;
	}
	
    private boolean isAdministrator(CoreSession session) {
        Principal principal = session.getPrincipal();
        if (SYSTEM_USERNAME.equals(principal.getName())) {
            return true;
        }
        if (principal instanceof NuxeoPrincipal) {
            return ((NuxeoPrincipal) principal).isAdministrator();
        }
        return false;
    }
}
