package fr.dila.reponses.ui.services.actions.impl;

import fr.dila.reponses.ui.services.actions.SuiviActionService;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.runtime.api.Framework;

public class SuiviActionServiceImpl implements SuiviActionService {
    protected List<Action> viewSuiviActionTabs;

    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(SuiviActionServiceImpl.class);

    public DocumentModelList getSavedRequetes(CoreSession session) {
        StringBuilder query = new StringBuilder();
        query
            .append("SELECT * FROM ")
            .append(STRequeteConstants.SMART_FOLDER_DOCUMENT_TYPE)
            .append("WHERE ")
            .append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH)
            .append(" != 'deleted' AND ")
            .append(STSchemaConstant.ECM_ISPROXY_XPATH)
            .append(" = 0 ");
        DocumentModelList docs = session.query(query.toString());
        return docs;
    }

    public boolean isEditingAlert(DocumentModel currentDoc) {
        return (
            currentDoc != null &&
            currentDoc.getDocumentType() != null &&
            STAlertConstant.ALERT_DOCUMENT_TYPE.equals(currentDoc.getDocumentType().getName())
        );
    }

    public void delete(SpecificContext context, CoreSession session, DocumentModel doc) {
        boolean delete = false;
        // Soft delete par défaut
        if ("true".equals(Framework.getProperty("socle.transverse.alert.soft.delete", "true"))) {
            try {
                session.followTransition(doc.getRef(), STLifeCycleConstant.TO_DELETE_TRANSITION);
                session.save();
                delete = true;
            } catch (NuxeoException ce) {
                LOGGER.error(session, STLogEnumImpl.FAIL_UPDATE_REQUETE_TEC, doc, ce);
                context.getMessageQueue().addInfoToQueue("suivi.error.requeteDeletion");
            }
        } else {
            try {
                LOGGER.info(session, STLogEnumImpl.DEL_REQ_EXP_TEC, doc);
                session.removeDocument(doc.getRef());
                session.save();
                delete = true;
            } catch (NuxeoException ce) {
                LOGGER.error(session, STLogEnumImpl.FAIL_DEL_REQ_EXP_TEC, doc, ce);
                context.getMessageQueue().addInfoToQueue("suivi.error.requeteDeletion");
            }
        }
        if (delete) {
            context.getMessageQueue().addToastSuccess(ResourceHelper.getString("suvi.requete.supprimer"));
        }
    }
}
