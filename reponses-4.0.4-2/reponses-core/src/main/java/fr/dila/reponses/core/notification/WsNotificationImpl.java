package fr.dila.reponses.core.notification;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.api.notification.WsNotification;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.DocumentModel;

public class WsNotificationImpl implements WsNotification {
    private static final long serialVersionUID = 1L;

    protected DocumentModel document;

    public WsNotificationImpl(DocumentModel doc) {
        this.document = doc;
    }

    @Override
    public void setDocument(DocumentModel doc) {
        this.document = doc;
    }

    @Override
    public DocumentModel getDocument() {
        return this.document;
    }

    @Override
    public String getPosteId() {
        return PropertyUtil.getStringProperty(
            document,
            ReponsesSchemaConstant.NOTIFICATION_SCHEMA,
            ReponsesSchemaConstant.NOTIFICATION_POSTE_ID_PROPERTY
        );
    }

    @Override
    public void setPosteId(String posteId) {
        PropertyUtil.setProperty(
            document,
            ReponsesSchemaConstant.NOTIFICATION_SCHEMA,
            ReponsesSchemaConstant.NOTIFICATION_POSTE_ID_PROPERTY,
            posteId
        );
    }

    @Override
    public String getWebservice() {
        return PropertyUtil.getStringProperty(
            document,
            ReponsesSchemaConstant.NOTIFICATION_SCHEMA,
            ReponsesSchemaConstant.NOTIFICATION_WEBSERVICE_PROPERTY
        );
    }

    @Override
    public void setWebservice(String webservice) {
        PropertyUtil.setProperty(
            document,
            ReponsesSchemaConstant.NOTIFICATION_SCHEMA,
            ReponsesSchemaConstant.NOTIFICATION_WEBSERVICE_PROPERTY,
            webservice
        );
    }

    @Override
    public int getNbEssais() {
        return PropertyUtil
            .getLongProperty(
                document,
                ReponsesSchemaConstant.NOTIFICATION_SCHEMA,
                ReponsesSchemaConstant.NOTIFICATION_NB_ESSAIS_PROPERTY
            )
            .intValue();
    }

    @Override
    public void setNbEssais(int nbEssais) {
        PropertyUtil.setProperty(
            document,
            ReponsesSchemaConstant.NOTIFICATION_SCHEMA,
            ReponsesSchemaConstant.NOTIFICATION_NB_ESSAIS_PROPERTY,
            nbEssais
        );
    }

    @Override
    public String getIdQuestion() {
        return PropertyUtil.getStringProperty(
            document,
            ReponsesSchemaConstant.NOTIFICATION_SCHEMA,
            ReponsesSchemaConstant.NOTIFICATION_ID_QUESTION_PROPERTY
        );
    }

    @Override
    public void setIdQuestion(String idQuestion) {
        PropertyUtil.setProperty(
            document,
            ReponsesSchemaConstant.NOTIFICATION_SCHEMA,
            ReponsesSchemaConstant.NOTIFICATION_ID_QUESTION_PROPERTY,
            idQuestion
        );
    }
}
