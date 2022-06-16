package fr.dila.reponses.core.cases.flux;

import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

public class QuestionStateChangeImpl implements QuestionStateChange {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private DocumentModel document;

    public QuestionStateChangeImpl(DocumentModel doc) {
        this.document = doc;
    }

    @Override
    public Calendar getChangeDate() {
        return PropertyUtil.getCalendarProperty(
            getDocument(),
            ReponsesSchemaConstant.ETAT_QUESTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.ETAT_QUESTION_DATE_PROPERTY
        );
    }

    @Override
    public void setChangeDate(Calendar changeDate) {
        PropertyUtil.setProperty(
            getDocument(),
            ReponsesSchemaConstant.ETAT_QUESTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.ETAT_QUESTION_DATE_PROPERTY,
            changeDate
        );
    }

    @Override
    public String getNewState() {
        return PropertyUtil.getStringProperty(
            getDocument(),
            ReponsesSchemaConstant.ETAT_QUESTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.ETAT_QUESTION_LABEL_PROPERTY
        );
    }

    @Override
    public void setNewState(String newState) {
        PropertyUtil.setProperty(
            getDocument(),
            ReponsesSchemaConstant.ETAT_QUESTION_DOCUMENT_SCHEMA,
            ReponsesSchemaConstant.ETAT_QUESTION_LABEL_PROPERTY,
            newState
        );
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }
}
