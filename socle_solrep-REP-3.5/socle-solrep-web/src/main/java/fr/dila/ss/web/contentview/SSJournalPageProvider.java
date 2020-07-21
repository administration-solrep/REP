package fr.dila.ss.web.contentview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.audit.api.LogEntry;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.contentview.AbstractDTOPageProvider;
import fr.dila.st.web.journal.STJournalActions;

/**
 * page provider du journal affiche dans le dossier
 * 
 * @author BBY, ARN
 * 
 */
public class SSJournalPageProvider extends AbstractDTOPageProvider {

	public static final String CURRENT_DOCUMENT_PROPERTY = "currentDocument";
	
    public static final String JOURNAL_ACTION_PROPERTY = "journalActions";
    
    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(SSJournalPageProvider.class);

    protected JournalService journalService;
    
    protected Date dateStart = null;

    /**
     * Default constructor
     */
    public SSJournalPageProvider(){
    	super();
    }
    
    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) throws ClientException {
        errorMessage = null;
        error = null;
        final Map<String, Serializable> props = getProperties();
        try {
            journalService = STServiceLocator.getJournalService();
            // récupération de la session

            // récupération du dossier et des propriétés associées
            DocumentModel currentDocumentModel = (DocumentModel) props.get(CURRENT_DOCUMENT_PROPERTY);
            List<String> dossierIdList = new ArrayList<String>();
            if (currentDocumentModel != null) {
                // récupération de l'id du dossier
                dossierIdList.add(currentDocumentModel.getId());
            }

            // récupération page courante
            int pageNumber = 1 + (int) (offset / pageSize);

            // ajout des filtres
            final Map<String, Object> mapFilter = new HashMap<String, Object>();
            mapFilter.put(STConstant.FILTER_CATEGORY, getParameters()[0]);
            mapFilter.put(STConstant.FILTER_USER, getParameters()[1]);
            mapFilter.put(STConstant.FILTER_DATE_DEBUT, getParameters()[2]);

            if (props.get(JOURNAL_ACTION_PROPERTY) != null && StringUtils.isEmpty((String) mapFilter.get(STConstant.FILTER_CATEGORY))) {
                STJournalActions bean = (STJournalActions) props.get(JOURNAL_ACTION_PROPERTY);
                // si pas de categoty on filtre sur les categories que l'utilisateur peut voir
                mapFilter.put(STConstant.FILTER_LIST_CATEGORY, bean.getCategoryList());
            }

            final Calendar cal = Calendar.getInstance();
            final Date date = (Date) getParameters()[3];
            if (date != null) {
                cal.setTime(date);
            }
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);

            mapFilter.put(STConstant.FILTER_DATE_FIN, cal.getTime());

            resultsCount = journalService.getEventsCount(dossierIdList, mapFilter, pageNumber, (int) pageSize);
            currentItems = new ArrayList<Map<String, Serializable>>();

            if (resultsCount > 0) {
                final List<LogEntry> logEntries = journalService.queryDocumentAllLogs(dossierIdList, mapFilter, pageNumber, (int) pageSize, sortInfos);

                if (logEntries != null) {
                    Map<String, Serializable> fieldMap = null;
                    final Calendar calendar = Calendar.getInstance();
                    for (LogEntry entry : logEntries) {

                        fieldMap = new HashMap<String, Serializable>();
                        fieldMap.put("principalName", entry.getPrincipalName());
                        fieldMap.put("eventId", entry.getEventId());

                        calendar.setTime(entry.getEventDate());
                        fieldMap.put("eventDate", calendar.getTime());
                        fieldMap.put("docUUID", entry.getDocUUID());
                        fieldMap.put("docType", entry.getDocType());
                        // entrée contenant les informations sur les profils de l'utilisateur
                        fieldMap.put("docPath", entry.getDocPath());

                        fieldMap.put("category", entry.getCategory());
                        fieldMap.put("comment", entry.getComment());
                        fieldMap.put("docLifeCycle", entry.getDocLifeCycle());

                        currentItems.add(fieldMap);

                    }
                }
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            error = e;
            LOG.warn(e.getMessage(), e);
        } 

    }

    @Override
    protected void buildQuery() {
        query = "";
    }

    @Override
    public void setSearchDocumentModel(DocumentModel searchDocumentModel) {
        // remise en place du bug nuxeo pour forcer tout le temps le refresh
        this.searchDocumentModel = searchDocumentModel;
        refresh();
    }

}
